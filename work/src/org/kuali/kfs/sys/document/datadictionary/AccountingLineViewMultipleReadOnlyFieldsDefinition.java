/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewMultipleReadOnlyFields;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * The definition for an accounting line component which displays multiple fields from the command line, but
 * all of them as read only, with their headers displayed first
 */
public class AccountingLineViewMultipleReadOnlyFieldsDefinition extends DataDictionaryDefinitionBase implements AccountingLineViewRenderableElementDefinition {
    public List<String> fieldNames;

    /**
     * Makes sure that the number of fields set is greater than 0
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (fieldNames.isEmpty()) {
            throw new AttributeValidationException("Please specify one or more field names when defining AccountingLineViewMultipleReadOnlyFields "+getId());
        }
    }

    /**
     * Creates a new AccountingLineViewMultipleReadOnlyField
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        List<Field> fields = new ArrayList<Field>();
        for (String fieldName: fieldNames) {
            fields.add(getKNSFieldForDefinition(accountingLineClass, fieldName));
        }
        return new AccountingLineViewMultipleReadOnlyFields(this, fields);
    }
    
    /**
     * Creates a KNS Field for an AccountingLineViewField definition
     * @param accountingLineClass the class of the accounting line used by this definition
     * @param fieldName the name of the field to initialize
     * @return a properly initialized KNS field
     */
    public Field getKNSFieldForDefinition(Class<? extends AccountingLine> accountingLineClass, String fieldName) {
        Field realField = FieldUtils.getPropertyField(accountingLineClass, fieldName, false);
        return realField;
    }

    /**
     * @return the field names of fields to display in the cell
     */
    public List<String> getFieldNames() {
        return fieldNames;
    }

    /**
     * Sets the field names to display in the field, in top-down order
     * @param fieldNames the field names to display
     */
    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }
    
}
