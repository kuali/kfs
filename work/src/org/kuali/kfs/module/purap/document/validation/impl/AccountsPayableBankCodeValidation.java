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

import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.BankCodeValidation;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;

public class AccountsPayableBankCodeValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableBankCodeValidation.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        
        AccountsPayableDocumentBase apDocument = (AccountsPayableDocumentBase) accountingDocumentForValidation;

        boolean isValid = true;
        if (isDocumentTypeUsingBankCode(apDocument)) {
            isValid = BankCodeValidation.validate(apDocument, apDocument.getBankCode(), PurapPropertyConstants.BANK_CODE, false, true);
        }

        return isValid;
    }

    /**
     * Verify that this document is using bank codes before validating.
     * 
     * @param apDocument
     * @return true if {@link KFSParameterKeyConstants.BANK_CODE_DOCUMENT_TYPES} contains this document type
     */
    private boolean isDocumentTypeUsingBankCode(AccountsPayableDocumentBase apDocument) {
        String documentTypeName = apDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Bank.class, KFSParameterKeyConstants.BANK_CODE_DOCUMENT_TYPES, documentTypeName);
        return evaluator.evaluationSucceeds();
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
}
