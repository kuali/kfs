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
package org.kuali.kfs.fp.document.validation.impl;

import java.text.MessageFormat;

import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.service.AccountingDocumentPreRuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

/**
 * Performs warning checks and prompts for AdvanceDeposit.
 */
public class AdvanceDepositDocumentPreRules extends PromptBeforeValidationBase {

    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;

        AdvanceDepositDocument adDocument = (AdvanceDepositDocument) document;

        preRulesOK &= checkBankCodeActive(adDocument);
        preRulesOK &= SpringContext.getBean(AccountingDocumentPreRuleService.class).expiredAccountOverrideQuestion((AccountingDocumentBase) document, this, this.event);

        return preRulesOK;
    }

    /**
     * If bank specification is enabled, prompts user to use the continuation bank code when the given bank code is inactive
     *
     * @param adDocument document containing bank code
     * @return true
     */
    protected boolean checkBankCodeActive(AdvanceDepositDocument adDocument) {
        boolean continueRules = true;

        // if bank specification is not enabled, no need to validate bank
        if (!SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            return continueRules;
        }

        int questionIndex = 0;
        for (AdvanceDepositDetail advanceDeposit : adDocument.getAdvanceDeposits()) {
            questionIndex++;

            // refresh bank reference so continuation bank can be checked for active status
            advanceDeposit.refreshReferenceObject(KFSPropertyConstants.BANK);
            Bank bank = advanceDeposit.getBank();

            // if bank is inactive and continuation is active, prompt user to use continuation bank
            if (bank != null && !bank.isActive() && bank.getContinuationBank().isActive()) {
                String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_BANK_INACTIVE);
                questionText = MessageFormat.format(questionText, advanceDeposit.getFinancialDocumentBankCode(), bank.getContinuationBankCode());

                boolean useContinuation = super.askOrAnalyzeYesNoQuestion(KFSConstants.USE_CONTINUATION_BANK_QUESTION + questionIndex, questionText);
                if (useContinuation) {
                    advanceDeposit.setFinancialDocumentBankCode(bank.getContinuationBankCode());
                }
            }
        }

        return continueRules;
    }

}
