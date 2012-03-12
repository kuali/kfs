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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.GENERIC_CODE_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.OBJECT_TYPE_CODE_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_REQUIRED;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineValueAllowedValidation;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * The Journal Voucher's version of the accounting line Object Type validation.
 */
public class JournalVoucherAccountingLineObjectTypeValueAllowedValidation extends AccountingLineValueAllowedValidation {
    private DataDictionaryService dataDictionaryService;

    /**
     * The JV allows any object type b/c it is up to the user to enter it into the interface, but it is required. The existence
     * check is done for us automatically by the data dictionary validation if a value exists; beforehand so we can assume that any
     * value in that field is valid.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        String objectTypeCode = getAccountingLineForValidation().getObjectTypeCode();
        if (StringUtils.isNotBlank(objectTypeCode)) {
            return true;
        }
        else {
            String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(ObjectType.class.getName()).getAttributeDefinition(GENERIC_CODE_PROPERTY_NAME).getLabel();
            GlobalVariables.getMessageMap().putError(OBJECT_TYPE_CODE_PROPERTY_NAME, ERROR_REQUIRED, label);
            return false;
        }
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
