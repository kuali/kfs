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
package org.kuali.kfs.fp.document.validation.impl;

import java.text.MessageFormat;

import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.NonCheckDisbursementDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.service.KualiConfigurationService;

/**
 * Performs warning checks and prompts for CashManagement.
 */
public class CashManagementDocumentPreRules extends PromptBeforeValidationBase {

    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;

        CashManagementDocument cmDocument = (CashManagementDocument) document;

        preRulesOK &= checkBankCodeActive(cmDocument);

        return preRulesOK;
    }

    /**
     * If bank specification is enabled, prompts user to use the continuation bank code when the given bank code is inactive
     * 
     * @param cmDocument document containing bank code
     * @return true
     */
    private boolean checkBankCodeActive(CashManagementDocument cmDocument) {
        boolean continueRules = true;

        // if bank specification is not enabled, no need to validate bank
        if (!SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            return continueRules;
        }

        int questionIndex = 0;
        for (Deposit deposit : cmDocument.getDeposits()) {
            questionIndex++;

            // refresh bank reference so continuation bank can be checked for active status
            deposit.refreshReferenceObject(KFSPropertyConstants.BANK);
            Bank bank = deposit.getBank();

            // if bank is inactive and continuation is active, prompt user to use continuation bank
            if (bank != null && !bank.isActive() && bank.getContinuationBank().isActive()) {
                String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.QUESTION_BANK_INACTIVE);
                questionText = MessageFormat.format(questionText, deposit.getDepositBankCode(), bank.getContinuationBankCode());

                boolean useContinuation = super.askOrAnalyzeYesNoQuestion(KFSConstants.USE_CONTINUATION_BANK_QUESTION + questionIndex, questionText);
                if (useContinuation) {
                    deposit.setDepositBankCode(bank.getContinuationBankCode());
                }
            }
        }

        return continueRules;
    }

}
