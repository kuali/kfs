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

import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

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
    protected boolean checkBankCodeActive(CashManagementDocument cmDocument) {
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
                String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_BANK_INACTIVE);
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
