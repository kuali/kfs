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

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.web.renderers.DynamicNameLabelRenderer;
import org.kuali.kfs.sys.document.web.renderers.FieldRenderer;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Field;

/**
 * Represents a field (plus, optionally, a dynamic name field) to be rendered as part of an accounting line.
 */
public class AccountingLineViewField extends FieldTableJoiningWithHeader implements HeaderLabelPopulating, ReadOnlyable {
    private Field field;
    private AccountingLineViewFieldDefinition definition;
    private int tabIndex;
    private int arbitrarilyHighIndex;
    private List<AccountingLineViewOverrideField> overrideFields;
    
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
     * Gets the overrideFields attribute. 
     * @return Returns the overrideFields.
     */
    public List<AccountingLineViewOverrideField> getOverrideFields() {
        return overrideFields;
    }
    /**
     * Sets the overrideFields attribute value.
     * @param overrideFields The overrideFields to set.
     */
    public void setOverrideFields(List<AccountingLineViewOverrideField> overrideFields) {
        this.overrideFields = overrideFields;
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
        return field.isReadOnly() || isHidden();
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
        if (getOverrideFields() != null && getOverrideFields().size() > 0) {
            renderOverrideFields(pageContext, parentTag, renderingContext);
        }
        if (shouldRenderDynamicFeldLabel() && renderingContext.fieldsCanRenderDynamicLabels()) {
            renderDynamicNameLabel(pageContext, parentTag, renderingContext.getAccountingLine(), renderingContext.getAccountingLinePropertyPath());
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
        FieldRenderer renderer = SpringContext.getBean(AccountingLineRenderingService.class).getFieldRendererForField(getField(), accountingLine);
        if (renderer != null) {
            prepareFieldRenderer(renderer, getField(), accountingLine, accountingLineProperty, fieldNames);
            if (!isHidden()) {
                renderer.openNoWrapSpan(pageContext, parentTag);
            }
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
     * Does some initial set up on the field renderer - sets the field and the business object being rendered
     * @param fieldRenderer the field renderer to prepare
     * @param accountingLine the accounting line being rendered
     */
    protected void prepareFieldRenderer(FieldRenderer fieldRenderer, Field field, AccountingLine accountingLine, String accountingLineProperty, List<String> fieldNames) {
        fieldRenderer.setField(field);
        
        getField().setPropertyPrefix(accountingLineProperty);
        populateFieldForLookupAndInquiry(accountingLine, fieldNames);
        if (!StringUtils.isBlank(definition.getDynamicLabelProperty())) {
            fieldRenderer.setDynamicNameLabel(accountingLineProperty+"."+definition.getDynamicLabelProperty());
        }
        
        final int passNumber = getTabIndexPass();
        if (passNumber > -1) {
            //renderer.setTabIndex(tabIndex);
            fieldRenderer.setTabIndex(0);
            fieldRenderer.setArbitrarilyHighTabIndex(arbitrarilyHighIndex);
        }
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
            fieldTransformation.transformField(accountingLine, getField(), getDefinition(), editModes, unconvertedValues);
            if (getOverrideFields() != null && getOverrideFields().size() > 0) {
                transformOverrideFields(fieldTransformation, accountingLine, editModes, unconvertedValues);
            }
        }
    }
    
    /**
     * Runs a field transformation against all the overrides encapsulated within this field
     * @param fieldTransformation the field transformation which will utterly change our fields
     * @param accountingLine the accounting line being rendered
     * @param editModes the current document edit modes
     * @param unconvertedValues a Map of unconvertedValues
     */
    protected void transformOverrideFields(AccountingLineFieldRenderingTransformation fieldTransformation, AccountingLine accountingLine, Map editModes, Map unconvertedValues) {
        for (AccountingLineViewOverrideField overrideField : getOverrideFields()) {
            overrideField.transformField(fieldTransformation, accountingLine, editModes, unconvertedValues);
        }
    }
    
    /**
     * Renders the override fields for the line
     * @param pageContext the page context to render to
     * @param parentTag the tag requesting all this rendering
     * @param accountingLine the accounting line we're rendering
     * @param accountingLinePropertyPath the path to get to that accounting
     * @throws JspException thrown if rendering fails
     */
    public void renderOverrideFields(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        for (AccountingLineViewOverrideField overrideField : getOverrideFields()) {
            overrideField.setAccountingLineProperty(renderingContext.getAccountingLinePropertyPath());
            overrideField.renderElement(pageContext, parentTag, renderingContext);
        }
    }
    
    /**
     * Renders a dynamic field label
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting this rendering
     * @param context the rendering context
     */
    protected void renderDynamicNameLabel(PageContext pageContext, Tag parentTag, AccountingLine accountingLine, String accountingLinePropertyPath) throws JspException {
        DynamicNameLabelRenderer renderer = new DynamicNameLabelRenderer();
        if (!StringUtils.isBlank(getField().getPropertyValue())) {
            if (getField().isSecure()) {
                renderer.setFieldValue(getField().getDisplayMask().maskValue(getField().getPropertyValue()));
            } else {
                renderer.setFieldValue(getDynamicNameLabelDisplayedValue(accountingLine));
            }
        }
        renderer.setFieldName(accountingLinePropertyPath+"."+definition.getDynamicLabelProperty());
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
     * Adds the wrapped field to the list; adds any override fields this field encapsulates as well
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     */
    public void appendFields(List<Field> fields) {
        fields.add(getField());
        if (getOverrideFields() != null && getOverrideFields().size() > 0) {
            for (AccountingLineViewOverrideField field : getOverrideFields()) {
                field.appendFields(fields);
            }
        }
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
