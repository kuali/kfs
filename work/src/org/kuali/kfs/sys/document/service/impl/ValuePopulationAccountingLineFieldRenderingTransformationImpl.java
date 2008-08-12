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
package org.kuali.kfs.sys.document.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.FieldUtils;
import org.kuali.core.web.ui.Field;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * A field transformation which populates a field with its matching business object value
 */
public class ValuePopulationAccountingLineFieldRenderingTransformationImpl implements AccountingLineFieldRenderingTransformation {

    /**
     * Using FieldUtils.populateFieldsFromBusinessObject(), populates the fields
     * @see org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation#transformField(org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.web.AccountingLineViewField)
     */
    public void transformField(AccountingLine accountingLine, AccountingLineViewField field, Map editModes, Map unconvertedValues) {
        List<Field> fieldList = new ArrayList<Field>();
        fieldList.add(field.getField());
        FieldUtils.populateFieldsFromBusinessObject(fieldList, accountingLine);
        
        setFieldUnformattedValueIfNecessary(field, unconvertedValues);
        encryptFieldIfNecessary(field, editModes);
    }

    /**
     * If an unconverted value for this field exists on this form, we'll use that instead
     * @param field the field to find and perhaps replace the value with, with the unconverted value
     * @param form the form holding the unconverted values
     */
    protected void setFieldUnformattedValueIfNecessary(AccountingLineViewField field, Map unconvertedValues) {
        if (unconvertedValues.containsKey(field.getField().getPropertyName())) {
            // we're up for finding an unconverted value!
            field.getField().setPropertyValue(unconvertedValues.get(field.getField().getPropertyName()));
        }
    }
    
    /**
     * If this field is secure, the value will be encrypted
     * @param field the field to potentially encrypt the value of
     * @param editModes the current edit modes for the field
     */
    protected void encryptFieldIfNecessary(AccountingLineViewField field, final Map editModes) {
        if (field.getField().isSecure() && editModes.get(field.getField().getDisplayEditMode()) == null) {
            field.getField().setPropertyValue(field.getField().getEncryptedValue());
        }
    }
}
