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
package org.kuali.kfs.sys.document.datadictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DynamicNameLabelGenerator;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.document.web.AccountingLineViewOverrideField;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.FieldBridge;

/**
 * Data dictionary definition of a field to be rendered as part of an accounting line view.
 */
public class AccountingLineViewFieldDefinition extends MaintainableFieldDefinition implements AccountingLineViewRenderableElementDefinition {
    private String dynamicLabelProperty;
    private boolean useShortLabel = false;
    private boolean hidden = false;
    private List<AccountingLineViewOverrideFieldDefinition> overrideFields;
    private String dynamicNameLabelGeneratorBeanName;
    private DynamicNameLabelGenerator dynamicNameLabelGenerator;

    /**
     * Gets the dynamicLabelProperty attribute. 
     * @return Returns the dynamicLabelProperty.
     */
    public String getDynamicLabelProperty() {
        return dynamicLabelProperty;
    }

    /**
     * Sets the dynamicLabelProperty attribute value.
     * @param dynamicLabelProperty The dynamicLabelProperty to set.
     */
    public void setDynamicLabelProperty(String dynamicLabelProperty) {
        this.dynamicLabelProperty = dynamicLabelProperty;
    }
    
    /**
     * Gets the useShortLabel attribute. 
     * @return Returns the useShortLabel.
     */
    public boolean shouldUseShortLabel() {
        return useShortLabel;
    }

    /**
     * Sets the useShortLabel attribute value.
     * @param useShortLabel The useShortLabel to set.
     */
    public void setUseShortLabel(boolean useShortLabel) {
        this.useShortLabel = useShortLabel;
    }

    /**
     * Gets the hidden attribute. 
     * @return Returns the hidden.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the hidden attribute value.
     * @param hidden The hidden to set.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Gets the overrideFields attribute. 
     * @return Returns the overrideFields.
     */
    public List<AccountingLineViewOverrideFieldDefinition> getOverrideFields() {
        return overrideFields;
    }

    /**
     * Sets the overrideFields attribute value.
     * @param overrideFields The overrideFields to set.
     */
    public void setOverrideFields(List<AccountingLineViewOverrideFieldDefinition> overrideFields) {
        this.overrideFields = overrideFields;
    }

    /**
     * Gets the dynamicNameLabelGeneratorBeanName attribute. 
     * @return Returns the dynamicNameLabelGeneratorBeanName.
     */
    public String getDynamicNameLabelGeneratorBeanName() {
        return dynamicNameLabelGeneratorBeanName;
    }

    /**
     * Sets the dynamicNameLabelGeneratorBeanName attribute value.
     * @param dynamicNameLabelGeneratorBeanName The dynamicNameLabelGeneratorBeanName to set.
     */
    public void setDynamicNameLabelGeneratorBeanName(String dynamicNameLabelGeneratorBeanName) {
        this.dynamicNameLabelGeneratorBeanName = dynamicNameLabelGeneratorBeanName;
    }
    
    /**
     * Returns the dynamicNameLabelGenerator for this field definition, if it has one
     * @return an implementation of DynamicNameLabelGenerator to use for this field
     */
    public DynamicNameLabelGenerator getDynamicNameLabelGenerator() {
        if (!StringUtils.isBlank(dynamicNameLabelGeneratorBeanName) && dynamicNameLabelGenerator == null) {
            Map<String, DynamicNameLabelGenerator> generators = SpringContext.getBeansOfType(DynamicNameLabelGenerator.class);
            dynamicNameLabelGenerator = generators.get(dynamicNameLabelGeneratorBeanName);
        }
        return dynamicNameLabelGenerator;
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement()
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        AccountingLineViewField layoutElement = new AccountingLineViewField();
        layoutElement.setDefinition(this);
        layoutElement.setField(getKNSFieldForDefinition(accountingLineClass));
        layoutElement.setOverrideFields(getFieldsForOverrideFields(layoutElement, accountingLineClass));
        return layoutElement;
    }
    
    /**
     * Creates a KNS Field for an AccountingLineViewField definition
     * @param accountingLineClass the class of the accounting line used by this definition
     * @return a properly initialized KNS field
     */
    public Field getKNSFieldForDefinition(Class<? extends AccountingLine> accountingLineClass) {
        Field realField = FieldUtils.getPropertyField(accountingLineClass, getName(), false);
        FieldBridge.setupField(realField, this);
        if (isHidden()) {
            realField.setFieldType(Field.HIDDEN);
        }
        /*if (!StringUtils.isBlank(getWebUILeaveFieldFunction())) {
            realField.setWebOnBlurHandler(getWebUILeaveFieldFunction());
        }
        if (!StringUtils.isBlank(getWebUILeaveFieldCallbackFunction())) {
            realField.setWebOnBlurHandlerCallback(getWebUILeaveFieldCallbackFunction());
        }*/
        return realField;
    }
    
    /**
     * For each defined override field within this definition, creates a Field and puts them together as a List
     * @param parentField the AccountingLineViewField which will own all of the override fields
     * @param accountingLineClass the class of accounting lines which will be rendered
     * @return a List of override fields, or if no override fields were defined, an empty List
     */
    protected List<AccountingLineViewOverrideField> getFieldsForOverrideFields(AccountingLineViewField parentField, Class<? extends AccountingLine> accountingLineClass) {
        List<AccountingLineViewOverrideField> fields = new ArrayList<AccountingLineViewOverrideField>();
        if (getOverrideFields() != null && getOverrideFields().size() > 0) {
            for (AccountingLineViewOverrideFieldDefinition overrideFieldDefinition : getOverrideFields()) {
                fields.add(overrideFieldDefinition.getOverrideFieldForDefinition(parentField, accountingLineClass));
            }
        }
        return fields;
    }
}
