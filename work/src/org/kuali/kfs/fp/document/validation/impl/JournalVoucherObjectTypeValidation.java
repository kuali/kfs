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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Validation which checks at the object type code on a Journal Voucher's accounting line is valid
 */
public class JournalVoucherObjectTypeValidation extends GenericValidation {
    private VoucherSourceAccountingLine accountingLineForValidation;
    private AccountingLineRuleHelperService accountingLineRuleHelperService;
    private DataDictionaryService dataDictionaryService;
    
    /**
     * Checks that the object type code on the Journal Voucher accounting line is valid and active
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        if (StringUtils.isNotBlank(accountingLineForValidation.getObjectTypeCode())) {
            accountingLineForValidation.refreshReferenceObject("objectType");
            ObjectType objectTypeCode = accountingLineForValidation.getObjectType();
            valid &= getAccountingLineRuleHelperService().isValidObjectTypeCode("", objectTypeCode, getDataDictionaryService().getDataDictionary());
        }
        return valid;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public VoucherSourceAccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(VoucherSourceAccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the accountingLineRuleHelperService attribute. 
     * @return Returns the accountingLineRuleHelperService.
     */
    public AccountingLineRuleHelperService getAccountingLineRuleHelperService() {
        return accountingLineRuleHelperService;
    }

    /**
     * Sets the accountingLineRuleHelperService attribute value.
     * @param accountingLineRuleHelperService The accountingLineRuleHelperService to set.
     */
    public void setAccountingLineRuleHelperService(AccountingLineRuleHelperService accountingLineRuleHelperService) {
        this.accountingLineRuleHelperService = accountingLineRuleHelperService;
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
