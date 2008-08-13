/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.web;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.AttributeDefinition;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.validation.ValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.DateValidationPattern;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.Field;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.kfs.sys.document.web.renderers.CheckboxRenderer;
import org.kuali.kfs.sys.document.web.renderers.CurrencyRenderer;
import org.kuali.kfs.sys.document.web.renderers.DateRenderer;
import org.kuali.kfs.sys.document.web.renderers.DropDownRenderer;
import org.kuali.kfs.sys.document.web.renderers.DynamicNameLabelRenderer;
import org.kuali.kfs.sys.document.web.renderers.FieldRenderer;
import org.kuali.kfs.sys.document.web.renderers.HiddenRenderer;
import org.kuali.kfs.sys.document.web.renderers.RadioButtonGroupRenderer;
import org.kuali.kfs.sys.document.web.renderers.ReadOnlyRenderer;
import org.kuali.kfs.sys.document.web.renderers.TextAreaRenderer;
import org.kuali.kfs.sys.document.web.renderers.TextRenderer;

/**
 * Represents a field (plus, optionally, a dynamic name field) to be rendered as part of an accounting line.
 */
public class AccountingLineViewField extends FieldTableJoiningWithHeader implements HeaderLabelPopulating, ReadOnlyable {
    private Field field;
    private AccountingLineViewFieldDefinition definition;
    private int tabIndex;
    private int arbitrarilyHighIndex;
    
    /**
     * Gets the definition attribute. 
     * @return Returns the definition.
     */
    public AccountingLineViewFieldDefinition getDefinition() {
        return definition;
    }
    /**
     * Sets the definition attribute value.
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewFieldDefinition definition) {
        this.definition = definition;
    }
    
    /**
     * Determines if this field should use the short label or not
     * @return true if the short label should be used, false otherwise
     */
    private boolean shouldUseShortLabel() {
        return definition.shouldUseShortLabel();
    }
    
    /**
     * Gets the field attribute. 
     * @return Returns the field.
     */
    public Field getField() {
        return field;
    }
    /**
     * Sets the field attribute value.
     * @param field The field to set.
     */
    public void setField(Field field) {
        this.field = field;
    }
    /**
     * Checks the field to see if the field itself is hidden
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElementField#isHidden()
     */
    public boolean isHidden() {
        return (field.getFieldType().equals(Field.HIDDEN) || definition.isHidden());
    }
    
    /**
     * Asks the wrapped field if it is read only (dynamic fields are, of course, always read only and therefore don't count in this determination)
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElementField#isReadOnly()
     */
    public boolean isReadOnly() {
        return field.isReadOnly();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return field.getPropertyName();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyize()
     */
    public void readOnlyize() {
        if (!isHidden()) {
            this.field.setReadOnly(true);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#getHeaderLabelProperty()
     */
    public String getHeaderLabelProperty() {
        return this.field.getPropertyName();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        renderField(pageContext, parentTag, renderingContext.getAccountingLine(), renderingContext.getAccountingLinePropertyPath(), renderingContext.getFieldNamesForAccountingLine());
        if (shouldRenderDynamicFeldLabel() && renderingContext.fieldsCanRenderDynamicLabels()) {
            renderDynamicNameLabel(pageContext, parentTag, renderingContext);
        }
    }
    
    /**
     * Renders the field portion of this tag
     * @param pageContext the page context to render to
     * @param parentTag the tag requesting rendering
     * @param accountingLineProperty the property from the form to the accounting line
     * @param fieldNames the names of all fields on this accounting line
     * @throws JspException thrown if something goes wrong
     */
    protected void renderField(PageContext pageContext, Tag parentTag, AccountingLine accountingLine, String accountingLineProperty, List<String> fieldNames) throws JspException {
        FieldRenderer renderer = getFieldRendererForField(accountingLine);
        if (renderer != null) {
            getField().setPropertyPrefix(accountingLineProperty);
            populateFieldForLookupAndInquiry(accountingLine, fieldNames);
            if (!StringUtils.isBlank(definition.getDynamicLabelProperty())) {
                renderer.setDynamicNameLabel(accountingLineProperty+"."+definition.getDynamicLabelProperty());
            }
            if (!isHidden()) {
                renderer.openNoWrapSpan(pageContext, parentTag);
            }
            prepareFieldRenderer(renderer, accountingLine);
            renderer.render(pageContext, parentTag);
            if (!isHidden()) {
                renderer.closeNoWrapSpan(pageContext, parentTag);
            }
            renderer.clear();
        }
    }
    
    /**
     * Updates the field so that it can have a quickfinder and inquiry link if need be
     * @param accountingLine the accounting line that is being rendered
     * @param fieldNames the list of all fields being displayed on this accounting line
     */
    protected void populateFieldForLookupAndInquiry(AccountingLine accountingLine, List<String> fieldNames) {
        LookupUtils.setFieldQuickfinder(accountingLine, getField().getPropertyName(), getField(), fieldNames);
        LookupUtils.setFieldDirectInquiry(getField());
    }
    
    /**
     * Based on the control type of the field, returns a proper field renderer
     * @return the field renderer which will properly display this field
     */
    protected FieldRenderer getFieldRendererForField(AccountingLine accountingLineToRender) {
        FieldRenderer renderer = null;
        if (field.isReadOnly() || field.getFieldType().equals(Field.READONLY)) {
            renderer = new ReadOnlyRenderer();
        } else if (field.getFieldType().equals(Field.TEXT)) {
            if (field.isDatePicker() || usesDateValidation(getField().getPropertyName(), accountingLineToRender)) { // are we a date?
                renderer = new DateRenderer();
            } else {
                renderer = new TextRenderer();
            }
        } else if (field.getFieldType().equals(Field.TEXT_AREA)) {
            renderer = new TextAreaRenderer();
        } else if (field.getFieldType().equals(Field.HIDDEN)) {
            renderer = new HiddenRenderer();
        } else if (field.getFieldType().equals(Field.CURRENCY)) {
            renderer = new CurrencyRenderer();
        } else if (field.getFieldType().equals(Field.DROPDOWN)) {
            renderer = new DropDownRenderer();
        } else if (field.getFieldType().equals(Field.RADIO)) {
            renderer = new RadioButtonGroupRenderer();
        } else if (field.getFieldType().equals(Field.CHECKBOX)) {
            renderer = new CheckboxRenderer();
        }
        final int passNumber = getTabIndexPass();
        if (passNumber > -1) {
            //renderer.setTabIndex(tabIndex);
            renderer.setTabIndex(0);
            renderer.setArbitrarilyHighTabIndex(arbitrarilyHighIndex);
        }
        
        return renderer;
    }
    
    /**
     * Determines if this method uses a date validation pattern, in which case, a date picker should be rendered
     * @param propertyName the property of the field being checked from the command line
     * @param accountingLineToRender the accounting line which is being rendered
     * @return true if the property does use date validation, false otherwise
     */
    protected boolean usesDateValidation(String propertyName, Object businessObject) {
        final BusinessObjectEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
        AttributeDefinition attributeDefinition = entry.getAttributeDefinition(propertyName);
        
        if (attributeDefinition == null) {
            if (!propertyName.contains(".")) return false;
            final int firstNestingPoint = propertyName.indexOf(".");
            final String toNestingPoint = propertyName.substring(0, firstNestingPoint);
            final String fromNestingPoint = propertyName.substring(firstNestingPoint+1);
            Object childObject = null;
            try {
                final Class childClass = PropertyUtils.getPropertyType(businessObject, toNestingPoint);
                childObject = childClass.newInstance();
            }
            catch (IllegalAccessException iae) {
                new UnsupportedOperationException(iae);
            }
            catch (InvocationTargetException ite) {
                new UnsupportedOperationException(ite);
            }
            catch (NoSuchMethodException nsme) {
                new UnsupportedOperationException(nsme);
            }
            catch (InstantiationException ie) {
                throw new UnsupportedOperationException(ie);
            }
            return usesDateValidation(fromNestingPoint, childObject);
        }
        
        final ValidationPattern validationPattern = attributeDefinition.getValidationPattern();
        if (validationPattern == null) return false; // no validation for sure means we ain't using date validation
        return validationPattern instanceof DateValidationPattern;
    }
    
    /**
     * Does some initial set up on the field renderer - sets the field and the business object being rendered
     * @param fieldRenderer the field renderer to prepare
     * @param accountingLine the accounting line being rendered
     */
    protected void prepareFieldRenderer(FieldRenderer fieldRenderer, AccountingLine accountingLine) {
        
        fieldRenderer.setField(field);
    }
    
    /**
     * Determines if a dynamic field label should be rendered for the given field
     * @return true if a dynamic field label should be rendered, false otherwise
     */
    protected boolean shouldRenderDynamicFeldLabel() {
        return (!StringUtils.isBlank(getField().getWebOnBlurHandler()) && !StringUtils.isBlank(definition.getDynamicLabelProperty()) && !getField().getFieldType().equals(Field.HIDDEN));
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformation(org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    @Override
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map editModes, Map unconvertedValues) {
        for (AccountingLineFieldRenderingTransformation fieldTransformation : fieldTransformations) {
            fieldTransformation.transformField(accountingLine, this, editModes, unconvertedValues);
        }
    }
    
    /**
     * Renders a dynamic field label
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting this rendering
     * @param context the rendering context
     */
    protected void renderDynamicNameLabel(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        DynamicNameLabelRenderer renderer = new DynamicNameLabelRenderer();
        if (!StringUtils.isBlank(getField().getPropertyValue())) {
            if (getField().isSecure()) {
                renderer.setFieldValue(getField().getDisplayMask().maskValue(getField().getPropertyValue()));
            } else {
                renderer.setFieldValue(getDynamicNameLabelDisplayedValue(renderingContext.getAccountingLine()));
            }
        }
        renderer.setFieldName(renderingContext.getAccountingLinePropertyPath()+"."+definition.getDynamicLabelProperty());
        renderer.render(pageContext, parentTag);
        renderer.clear();
    }
    
    /**
     * Gets the value from the accounting line to display as the field value
     * @param accountingLine the accounting line to get the value from
     * @return the value to display for the dynamic name label
     */
    protected String getDynamicNameLabelDisplayedValue(AccountingLine accountingLine) {
        final Object value = ObjectUtils.getPropertyValue(accountingLine, definition.getDynamicLabelProperty());
        if (value != null) return value.toString();
        return null;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {        
        return new FieldHeaderLabel(this);
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.HeaderLabelPopulating#populateHeaderLabel(org.kuali.kfs.sys.document.web.HeaderLabel, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void populateHeaderLabel(HeaderLabel headerLabel, AccountingLineRenderingContext renderingContext) {
        FieldHeaderLabel label = (FieldHeaderLabel)headerLabel;
        label.setLabel(getField().getFieldLabel());
        label.setLabeledFieldEmptyOrHidden(isEmpty() || isHidden());
        label.setReadOnly(getField().isReadOnly());
        label.setRequired(getField().isFieldRequired());
        if (renderingContext.fieldsShouldRenderHelp()) {
            label.setFullClassNameForHelp(renderingContext.getAccountingLine().getClass().getName());
            label.setAttributeEntryForHelp(getField().getPropertyName());
        }
    }
    
    /**
     * Adds the wrapped field to the list
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     */
    public void appendFields(List<Field> fields) {
        fields.add(getField());
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TabIndexRequestor#setTabIndex(int, int)
     */
    public void setTabIndex(int currentTabIndex, int arbitrarilyHighIndex) {
        this.tabIndex = currentTabIndex;
        this.arbitrarilyHighIndex = arbitrarilyHighIndex;
    }
    
    /**
     * If hidden or read only, gets passed over; otherwise asks for the first pass
     * @see org.kuali.kfs.sys.document.web.TabIndexRequestor#getTabIndexPass()
     */
    public int getTabIndexPass() {
        if (isReadOnly() || isHidden()) return -1;
        return 0;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int[] passIndexes, int reallyHighIndex) {
        if (getTabIndexPass() > -1) {
            setTabIndex(passIndexes[getTabIndexPass()], reallyHighIndex);
        }
    }
    
}
