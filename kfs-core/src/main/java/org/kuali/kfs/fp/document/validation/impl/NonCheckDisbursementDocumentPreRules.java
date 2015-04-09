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
