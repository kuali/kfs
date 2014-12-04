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
