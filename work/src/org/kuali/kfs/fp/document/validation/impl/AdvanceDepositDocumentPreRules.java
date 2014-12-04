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
