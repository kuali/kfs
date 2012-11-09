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

import org.kuali.kfs.fp.document.NonCheckDisbursementDocument;
import org.kuali.kfs.fp.service.AccountingDocumentPreRuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

/**
 * Performs warning checks and prompts for NonCheckDisbursement.
 */
public class NonCheckDisbursementDocumentPreRules extends PromptBeforeValidationBase {

    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;

        NonCheckDisbursementDocument nonCheckDocument = (NonCheckDisbursementDocument) document;

        preRulesOK &= checkBankCodeActive(nonCheckDocument);

        preRulesOK &= SpringContext.getBean(AccountingDocumentPreRuleService.class).expiredAccountOverrideQuestion((AccountingDocumentBase) document, this, this.event);
        
        return preRulesOK;
    }

    /**
     * If bank specification is enabled, prompts user to use the continuation bank code when the
     * given bank code is inactive
     *
     * @param nonCheckDocument document containing bank code
     * @return true
     */
    protected boolean checkBankCodeActive(NonCheckDisbursementDocument nonCheckDocument) {
        BankService bankService = SpringContext.getBean(BankService.class);

        // if bank specification is not enabled, no need to validate bank
        if ( bankService.isBankSpecificationEnabled() ) {
            Bank bank = bankService.getByPrimaryId(nonCheckDocument.getFinancialDocumentBankCode());

            // if bank is inactive and continuation is active, prompt user to use continuation bank
            if ( bank != null  && !bank.isActive() ) {
                Bank continuationBank = bankService.getByPrimaryId(bank.getContinuationBankCode());
                if ( continuationBank != null && continuationBank.isActive() ) {
                    String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_BANK_INACTIVE);
                    questionText = MessageFormat.format(questionText, bank.getBankCode(), continuationBank.getBankCode());

                    boolean useContinuation = askOrAnalyzeYesNoQuestion(KFSConstants.USE_CONTINUATION_BANK_QUESTION, questionText);
                    if (useContinuation) {
                        nonCheckDocument.setFinancialDocumentBankCode(continuationBank.getBankCode());
                    }
                }
            }
        }

        return true;
    }

}
