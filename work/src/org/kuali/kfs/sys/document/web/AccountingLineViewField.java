/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.web;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.web.renderers.DynamicNameLabelRenderer;
import org.kuali.kfs.sys.document.web.renderers.FieldRenderer;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Represents a field (plus, optionally, a dynamic name field) to be rendered as part of an accounting line.
 */
public class AccountingLineViewField extends FieldTableJoiningWithHeader implements HeaderLabelPopulating, ReadOnlyable {
    public static final String ACCOUNTING_LINE_NAME_PREFIX_PLACE_HOLDER = "${accountingLineName}";

    private Field field;
    private AccountingLineViewFieldDefinition definition;
    private int arbitrarilyHighIndex;
    private List<AccountingLineViewOverrideField> overrideFields;
    private PersistenceStructureService persistenceStructureService;

    /**
     * Gets the definition attribute.
     *
     * @return Returns the definition.
     */
    public AccountingLineViewFieldDefinition getDefinition() {
        return definition;
    }

    /**
     * Sets the definition attribute value.
     *
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewFieldDefinition definition) {
        this.definition = definition;
    }

    /**
     * Determines if this field should use the short label or not
     *
     * @return true if the short label should be used, false otherwise
     */
    private boolean shouldUseShortLabel() {
        return definition.shouldUseShortLabel();
    }

    /**
     * Gets the field attribute.
     *
     * @return Returns the field.
     *
     * KRAD Conversion: Gets the fields - No use of data dictionary
     */
    public Field getField() {
        return field;
    }

    /**
     * Sets the field attribute value.
     *
     * @param field The field to set.
     *
     * KRAD Conversion: sets the fields - No use of data dictionary
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Gets the overrideFields attribute.
     *
     * @return Returns the overrideFields.
     */
    public List<AccountingLineViewOverrideField> getOverrideFields() {
        return overrideFields;
    }

    /**
     * Sets the overrideFields attribute value.
     *
     * @param overrideFields The overrideFields to set.
     */
    public void setOverrideFields(List<AccountingLineViewOverrideField> overrideFields) {
        this.overrideFields = overrideFields;
    }

    /**
     * Checks the field to see if the field itself is hidden
     *
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElementField#isHidden()
     */
    @Override
    public boolean isHidden() {
        return (field.getFieldType().equals(Field.HIDDEN) || definition.isHidden());
    }

    /**
     * Asks the wrapped field if it is read only (dynamic fields are, of course, always read only and therefore don't count in this
     * determination)
     *
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElementField#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return field.isReadOnly() || isHidden();
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    @Override
    public String getName() {
        return field.getPropertyName();
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyize()
     */
    @Override
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
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext,
     *      javax.servlet.jsp.tagext.Tag)
     */
    @Override
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        renderField(pageContext, parentTag, renderingContext);

        if (getOverrideFields() != null && getOverrideFields().size() > 0) {
            renderOverrideFields(pageContext, parentTag, renderingContext);
        }
        if (shouldRenderDynamicFeldLabel() && renderingContext.fieldsCanRenderDynamicLabels()) {
            renderDynamicNameLabel(pageContext, parentTag, renderingContext);
        }
    }

    /**
     * Renders the field portion of this tag
     *
     * @param pageContext the page context to render to
     * @param parentTag the tag requesting rendering
     * @param renderingContext the rendering context of the accounting line
     * @throws JspException thrown if something goes wrong
     */
    protected void renderField(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        AccountingLine accountingLine = renderingContext.getAccountingLine();
        String accountingLineProperty = renderingContext.getAccountingLinePropertyPath();
        List<String> fieldNames = renderingContext.getFieldNamesForAccountingLine();
        List errors = renderingContext.getErrors();

        this.getField().setPropertyPrefix(accountingLineProperty);
        boolean chartSetByAccount = getName().equals(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME) && !SpringContext.getBean(AccountService.class).accountsCanCrossCharts();
        //set chartOfAccountsCode readOnly if account can't cross charts
        if (!renderingContext.isFieldModifyable(this.getName()) || chartSetByAccount) {
            this.getField().setReadOnly(true);
        }

        FieldRenderer renderer = SpringContext.getBean(AccountingLineRenderingService.class).getFieldRendererForField(getField(), accountingLine);
        if (renderer != null) {
            prepareFieldRenderer(renderer, getField(), renderingContext.getAccountingDocument(), accountingLine, accountingLineProperty, fieldNames);
            if (fieldInError(errors)) {
                renderer.setShowError(true);
            }

            if (!isHidden()) {
                renderer.openNoWrapSpan(pageContext, parentTag);
            }

            // dynamically set the accessible title to the current field
            if (!this.isReadOnly()) {
                String accessibleTitle = getField().getFieldLabel();

                if (renderingContext.isNewLine()) {
                    String format = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.LABEL_NEW_ACCOUNTING_LINE_FIELD);
                    accessibleTitle = MessageFormat.format(format, accessibleTitle, renderingContext.getGroupLabel());
                }
                else {
                    Integer lineNumber = renderingContext.getCurrentLineCount() + 1;
                    String format = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.LABEL_ACCOUNTING_LINE_FIELD);
                    accessibleTitle = MessageFormat.format(format, accessibleTitle, renderingContext.getGroupLabel(), lineNumber);
                }

                renderer.setAccessibleTitle(accessibleTitle);
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
     *
     * @param accountingDocument the accounting document the accounting line the field will render part of is on or will at some
     *        point be on
     * @param accountingLine the accounting line that is being rendered
     * @param fieldNames the list of all fields being displayed on this accounting line
     * @param accountingLinePrefix the prefix of all field names in the accounting line
     */
    protected void populateFieldForLookupAndInquiry(AccountingDocument accountingDocument, AccountingLine accountingLine, List<String> fieldNames, String accountingLinePrefix) {
        if (!isHidden()) {
            LookupUtils.setFieldQuickfinder(accountingLine, getField().getPropertyName(), getField(), fieldNames);

            // apply the customized lookup parameters if any
            String overrideLookupParameters = definition.getOverrideLookupParameters();
            if (StringUtils.isNotBlank(overrideLookupParameters)) {
                String lookupParameters = getField().getLookupParameters();

                Map<String, String> lookupParametersMap = this.getActualParametersMap(lookupParameters, overrideLookupParameters, accountingLinePrefix);

                getField().setLookupParameters(lookupParametersMap);

                // if there are any any lookup parameters present, make sure the other lookup fields are populated.
                // this can be necessary if there wouldnt natually be a lookup, via DD or OJB relationships, but one
                // is forced.
                if (!lookupParametersMap.isEmpty()) {
                    if (getDefinition().getOverrideLookupClass() != null) {
                        getField().setQuickFinderClassNameImpl(getDefinition().getOverrideLookupClass().getName());
                    }
                }
            }

            // apply the customized field conversions if any
            String overrideFieldConversions = definition.getOverrideFieldConversions();
            if (StringUtils.isNotBlank(overrideFieldConversions)) {
                String fieldConversions = getField().getFieldConversions();

                Map<String, String> fieldConversionsMap = this.getActualParametersMap(fieldConversions, overrideFieldConversions, accountingLinePrefix);

                getField().setFieldConversions(fieldConversionsMap);
            }

            if (isRenderingInquiry(accountingDocument, accountingLine)) {
                FieldUtils.setInquiryURL(getField(), accountingLine, getField().getPropertyName());
            }
        }
    }

    /**
     * Lazily retrieves the persistence structure service
     *
     * @return an implementation of PersistenceStructureService
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        if (persistenceStructureService == null) {
            persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);
        }
        return persistenceStructureService;
    }

    /**
     * Does some initial set up on the field renderer - sets the field and the business object being rendered
     *
     * @param fieldRenderer the field renderer to prepare
     * @param accountingLine the accounting line being rendered
     * @param accountingLineProperty the property to get the accounting line from the form
     * @param fieldNames the names of all the fields that will be rendered as part of this accounting line
     *
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    protected void prepareFieldRenderer(FieldRenderer fieldRenderer, Field field, AccountingDocument document, AccountingLine accountingLine, String accountingLineProperty, List<String> fieldNames) {
        fieldRenderer.setField(field);

        getField().setPropertyPrefix(accountingLineProperty);
        populateFieldForLookupAndInquiry(document, accountingLine, fieldNames, getField().getPropertyPrefix());

        if (definition.getDynamicNameLabelGenerator() != null) {
            fieldRenderer.overrideOnBlur(definition.getDynamicNameLabelGenerator().getDynamicNameLabelOnBlur(accountingLine, accountingLineProperty));
        }
        else if (!StringUtils.isBlank(definition.getDynamicLabelProperty())) {
            fieldRenderer.setDynamicNameLabel(accountingLineProperty + "." + definition.getDynamicLabelProperty());
        }

        fieldRenderer.setArbitrarilyHighTabIndex(arbitrarilyHighIndex);
    }

    /**
     * Determines if a dynamic field label should be rendered for the given field
     *
     * @return true if a dynamic field label should be rendered, false otherwise
     */
    protected boolean shouldRenderDynamicFeldLabel() {
        return (!getField().getFieldType().equals(Field.HIDDEN) && ((!StringUtils.isBlank(getField().getWebOnBlurHandler()) && !StringUtils.isBlank(definition.getDynamicLabelProperty())) || definition.getDynamicNameLabelGenerator() != null));
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformation(org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    @Override
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        for (AccountingLineFieldRenderingTransformation fieldTransformation : fieldTransformations) {
            fieldTransformation.transformField(accountingLine, getField(), getDefinition(), unconvertedValues);
            if (getOverrideFields() != null && getOverrideFields().size() > 0) {
                transformOverrideFields(fieldTransformation, accountingLine, unconvertedValues);
            }
        }
    }

    /**
     * Runs a field transformation against all the overrides encapsulated within this field
     *
     * @param fieldTransformation the field transformation which will utterly change our fields
     * @param accountingLine the accounting line being rendered
     * @param editModes the current document edit modes
     * @param unconvertedValues a Map of unconvertedValues
     */
    protected void transformOverrideFields(AccountingLineFieldRenderingTransformation fieldTransformation, AccountingLine accountingLine, Map unconvertedValues) {
        for (AccountingLineViewOverrideField overrideField : getOverrideFields()) {
            overrideField.transformField(fieldTransformation, accountingLine, unconvertedValues);
        }
    }

    /**
     * Renders the override fields for the line
     *
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
     *
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting this rendering
     * @param accountingLine the line which owns the field being rendered
     * @param accountingLinePropertyPath the path from the form to the accounting line
     */
    protected void renderDynamicNameLabel(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        AccountingLine accountingLine = renderingContext.getAccountingLine();
        String accountingLinePropertyPath = renderingContext.getAccountingLinePropertyPath();

        DynamicNameLabelRenderer renderer = new DynamicNameLabelRenderer();
        if (definition.getDynamicNameLabelGenerator() != null) {
            renderer.setFieldName(definition.getDynamicNameLabelGenerator().getDynamicNameLabelFieldName(accountingLine, accountingLinePropertyPath));
            renderer.setFieldValue(definition.getDynamicNameLabelGenerator().getDynamicNameLabelValue(accountingLine, accountingLinePropertyPath));
        }
        else {
            if (!StringUtils.isBlank(getField().getPropertyValue())) {
                if (getField().isSecure()) {
                    renderer.setFieldValue(getField().getDisplayMask().maskValue(getField().getPropertyValue()));
                }
                else {
                    renderer.setFieldValue(getDynamicNameLabelDisplayedValue(accountingLine));
                }
            }
            renderer.setFieldName(accountingLinePropertyPath + "." + definition.getDynamicLabelProperty());
        }
        renderer.render(pageContext, parentTag);
        renderer.clear();
    }

    /**
     * Gets the value from the accounting line to display as the field value
     *
     * @param accountingLine the accounting line to get the value from
     * @return the value to display for the dynamic name label
     */
    protected String getDynamicNameLabelDisplayedValue(AccountingLine accountingLine) {
        String dynamicLabelProperty = definition.getDynamicLabelProperty();
        Object value = accountingLine;
        while (!ObjectUtils.isNull(value) && dynamicLabelProperty.indexOf('.') > -1) {
            String currentProperty = StringUtils.substringBefore(dynamicLabelProperty, ".");
            dynamicLabelProperty = StringUtils.substringAfter(dynamicLabelProperty, ".");
            if (value instanceof PersistableBusinessObject) {
                ((PersistableBusinessObject) value).refreshReferenceObject(currentProperty);
            }
            value = ObjectUtils.getPropertyValue(value, currentProperty);
        }
        if (!ObjectUtils.isNull(value)) {
            value = ObjectUtils.getPropertyValue(value, dynamicLabelProperty);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    @Override
    public HeaderLabel createHeaderLabel() {
        return new FieldHeaderLabel(this);
    }

    /**
     * If the field definition had an override col span greater than 1 and it doesn't seem as if the given cell had its colspan
     * lengthened already, this method will increase the colspan of the table cell to whatever is listed
     *
     * @param cell the cell to possibly lengthen
     */
    protected void updateTableCellWithColSpanOverride(AccountingLineTableCell cell) {
        if (definition.getOverrideColSpan() > 1 && cell.getColSpan() == 1) {
            cell.setColSpan(definition.getOverrideColSpan());
        }
    }

    /**
     * Overridden to allow for colspan override
     *
     * @see org.kuali.kfs.sys.document.web.FieldTableJoiningWithHeader#createHeaderLabelTableCell()
     */
    @Override
    protected AccountingLineTableCell createHeaderLabelTableCell() {
        AccountingLineTableCell cell = super.createHeaderLabelTableCell();
        updateTableCellWithColSpanOverride(cell);
        return cell;
    }

    /**
     * Overridden to allow for colspan override
     *
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#createTableCell()
     */
    @Override
    protected AccountingLineTableCell createTableCell() {
        AccountingLineTableCell cell = super.createTableCell();
        updateTableCellWithColSpanOverride(cell);
        return cell;
    }

    /**
     * @return the colspan override of this field
     */
    public int getColSpanOverride() {
        return definition.getOverrideColSpan();
    }

    /**
     * @see org.kuali.kfs.sys.document.web.HeaderLabelPopulating#populateHeaderLabel(org.kuali.kfs.sys.document.web.HeaderLabel,
     *      org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    @Override
    public void populateHeaderLabel(HeaderLabel headerLabel, AccountingLineRenderingContext renderingContext) {
        FieldHeaderLabel label = (FieldHeaderLabel) headerLabel;
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
     *
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     *
     * KRAD Conversion: Customization of adding the fields - No use of data dictionary
     */
    @Override
    public void appendFields(List<Field> fields) {
        fields.add(getField());
        if (getOverrideFields() != null && getOverrideFields().size() > 0) {
            for (AccountingLineViewOverrideField field : getOverrideFields()) {
                field.appendFields(fields);
            }
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    @Override
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {
        this.arbitrarilyHighIndex = reallyHighIndex;
    }

    /**
     * Determines if this field is among the fields that are in error
     *
     * @param errors the errors on the form
     * @return true if this field is in error, false otherwise
     *
     * KRAD Conversion: Checks if fields have errors - No use of data dictionary
     */
    protected boolean fieldInError(List errors) {
        boolean fieldInError = false;

        if (errors != null) {
            String fieldName = getField().getPropertyName();
            if (!StringUtils.isBlank(getField().getPropertyPrefix())) {
                fieldName = getField().getPropertyPrefix() + "." + fieldName;
            }
            fieldInError = matchesError(errors, fieldName);

            // if there was no match, it may be because the field is part of a collection, and there
            // is a mismatch between the fieldName and the errorKeys in the errors List. Need to check
            // again accommodating that by changing the fieldName to be plural
            if (fieldName.matches(".*[^s]\\[\\d+\\].*") && !fieldInError) {
                fieldInError = matchesError(errors, fieldName.replaceAll("\\[", "s\\["));
            }
        }

        return fieldInError;
    }

    /**
     * Check the errorKeys for a match to the fieldName
     *
     * @param errors the errors on the form
     * @param fieldName true if the fieldName matches an errorKey, false otherwise
     * @return
     */
    private boolean matchesError(List errors, String fieldName) {
        boolean matchesError = false;

        for (Object errorKeyAsObject : errors) {
            final String errorKey = (String) errorKeyAsObject;
            if (fieldName.equals(errorKey)) {
                matchesError = true;
                break;
            }
        }

        return matchesError;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#setEditable()
     */
    @Override
    public void setEditable() {
        if (!isHidden()) {
            this.field.setReadOnly(false);
        }
    }

    /**
     * Determines whether to render the inquiry for this field
     *
     * @param document the document which the accounting line is part of or hopefully sometime will be part of
     * @param line the accounting line being rendered
     * @return true if inquiry links should be rendered, false otherwise
     */
    protected boolean isRenderingInquiry(AccountingDocument document, AccountingLine line) {
        return isReadOnly();
    }

    /**
     * build the lookup parameter map through applying the override parameters onto the defaults
     *
     * @param lookupParameters the default lookup parameter string
     * @param overrideLookupParameters the override lookup parameter string
     * @param accountingLinePrefix the actual accounting line prefix
     * @return the actual lookup parameter map
     */
    private Map<String, String> getActualParametersMap(String parameters, String overrideParameters, String accountingLinePrefix) {
        BidiMap parametersMap = this.buildBidirecionalMapFromParameters(parameters, accountingLinePrefix);
        BidiMap overrideParametersMap = this.buildBidirecionalMapFromParameters(overrideParameters, accountingLinePrefix);
        parametersMap.putAll(overrideParametersMap);

        return parametersMap;
    }

    /**
     * parse the given lookup parameter string into a bidirectinal map
     *
     * @param lookupParameters the lookup parameter string
     * @param accountingLinePrefix the actual accounting line prefix
     * @return a bidirectinal map that holds all the given lookup parameters
     */
    private BidiMap buildBidirecionalMapFromParameters(String parameters, String accountingLinePrefix) {
        BidiMap parameterMap = new DualHashBidiMap();

        //  if we didnt get any incoming parameters, then just return an empty parameterMap
        if (StringUtils.isBlank(parameters)) {
            return parameterMap;
        }

        String[] parameterArray = StringUtils.split(parameters, KFSConstants.FIELD_CONVERSIONS_SEPERATOR);

        for (String parameter : parameterArray) {
            String[] entrySet = StringUtils.split(parameter, KFSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

            if (entrySet != null) {
                String parameterKey = escapeAccountingLineName(entrySet[0], accountingLinePrefix);
                String parameterValue = escapeAccountingLineName(entrySet[1], accountingLinePrefix);

                parameterMap.put(parameterKey, parameterValue);
            }
        }

        return parameterMap;
    }

    /**
     * Escapes the String ${accountingLineName} within a field and replaces it with the actual prefix of an accounting line
     *
     * @param propertyName the name of the property to escape the special string ${accountingLineName} out of
     * @param accountingLinePrefix the actual accounting line prefix
     * @return the property name with the correct accounting line prefix
     */
    protected String escapeAccountingLineName(String propertyName, String accountingLinePrefix) {
        return StringUtils.replace(propertyName, ACCOUNTING_LINE_NAME_PREFIX_PLACE_HOLDER, accountingLinePrefix + ".");
    }
}
