/*
 * Copyright 2008-2009 The Kuali Foundation
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
