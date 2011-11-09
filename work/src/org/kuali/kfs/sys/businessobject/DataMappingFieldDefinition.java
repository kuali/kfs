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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.dataaccess.FieldMetaData;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

public class DataMappingFieldDefinition extends TransientBusinessObjectBase {
    private String namespaceCode;
    private String componentClass;
    private String propertyName;
    private String tableName;
    private String fieldName;
    private FunctionalFieldDescription functionalFieldDescription;

    private BusinessObjectEntry businessObjectEntry;
    private AttributeDefinition attributeDefinition;
    private FieldMetaData fieldMetaData;
    private String propertyType;
    private String referenceComponentLabel;

    public DataMappingFieldDefinition() {
    }

    public DataMappingFieldDefinition(FunctionalFieldDescription functionalFieldDescription, BusinessObjectEntry businessObjectEntry, AttributeDefinition attributeDefinition, FieldMetaData fieldMetaData, String propertyType, String referenceComponentLabel) {
        setNamespaceCode(functionalFieldDescription.getNamespaceCode());
        setComponentClass(functionalFieldDescription.getComponentClass());
        setPropertyName(functionalFieldDescription.getPropertyName());
        setTableName(fieldMetaData.getTableName());
        setFieldName(fieldMetaData.getColumnName());
        setFunctionalFieldDescription(functionalFieldDescription);
        this.businessObjectEntry = businessObjectEntry;
        this.attributeDefinition = attributeDefinition;
        this.fieldMetaData = fieldMetaData;
        this.propertyType = propertyType;
        this.referenceComponentLabel = referenceComponentLabel;
    }

    public String getDatabaseDataType() {
        return fieldMetaData.getDataType();
    }

    public String getApplicationDataType() {
        return propertyType;
    }

    public int getDatabaseDefinedLength() {
        return fieldMetaData.getLength();
    }

    public int getApplicationDefinedLength() {
        return attributeDefinition.getMaxLength();
    }

    public int getDecimalPlaces() {
        return fieldMetaData.getDecimalPlaces();
    }

    public String getReferenceComponent() {
        return referenceComponentLabel;
    }

    public boolean isRequired() {
        return attributeDefinition.isRequired();
    }

    public String getValidationPattern() {
        return new StringBuffer(attributeDefinition.getValidationPattern().getClass().getSimpleName()).append(" (").append(attributeDefinition.getValidationPattern().getRegexPattern().toString()).append(")").toString();
    }
    
    public boolean isEncrypted() {
        return fieldMetaData.isEncrypted();
    }
    
    public String getMaskPattern() {
        // TODO: see how to handle the multiple mask formatters that may appear on an AttributeSecurity object
        /*if (attributeDefinition.getDisplayMask().getMaskFormatter() instanceof MaskFormatterLiteral) {
            return ((MaskFormatterLiteral)attributeDefinition.getDisplayMask().getMaskFormatter()).getLiteral();
        }
        else if (attributeDefinition.getDisplayMask().getMaskFormatter() instanceof MaskFormatterSubString) {
            return new StringBuffer(((MaskFormatterSubString)attributeDefinition.getDisplayMask().getMaskFormatter()).getMaskLength()).append(" ").append(((MaskFormatterSubString)attributeDefinition.getDisplayMask().getMaskFormatter()).getMaskCharacter()).append(" characters").toString();
        }
        else {*/
            return "Unknown MaskFormatter";
        //} 
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    public String getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FunctionalFieldDescription getFunctionalFieldDescription() {
        return functionalFieldDescription;
    }

    public void setFunctionalFieldDescription(FunctionalFieldDescription functionalFieldDescription) {
        this.functionalFieldDescription = functionalFieldDescription;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return new LinkedHashMap();
    }
}
