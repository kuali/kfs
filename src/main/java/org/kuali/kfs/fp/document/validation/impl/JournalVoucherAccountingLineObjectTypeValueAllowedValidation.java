/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
