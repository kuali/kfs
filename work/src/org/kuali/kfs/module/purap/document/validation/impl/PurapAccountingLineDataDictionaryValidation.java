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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingLineRuleHelperService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * A validation which uses the AccountingLineRuleHelperService to validate the values on an accounting line
 */
public class PurapAccountingLineDataDictionaryValidation extends GenericValidation {
    private PurapAccountingLineRuleHelperService ruleHelperService; 
    private AccountingLine accountingLineForValidation;

    /**
     * Okay, okay, so yeah, I could have handled this through a bunch of DD validations.  But it's late Thursday afternoon
     * and someone has already written this code for me...
     * <strong>Expects an accounting line as the first parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        ruleHelperService.setDocument((PurchasingAccountsPayableDocument)event.getDocument());
        return ruleHelperService.validateAccountingLine(accountingLineForValidation);
    }

    /**
     * Sets the ruleHelperService attribute value.
     * @param ruleHelperService The ruleHelperService to set.
     */
    public void setAccountingLineRuleHelperService(PurapAccountingLineRuleHelperService ruleHelperService) {
        this.ruleHelperService = ruleHelperService;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
