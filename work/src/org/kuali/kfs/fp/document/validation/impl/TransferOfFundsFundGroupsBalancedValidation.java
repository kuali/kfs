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
package org.kuali.module.financial.document.validation.impl;

import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_FUND_GROUP_SET_DOES_NOT_BALANCE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.document.TransferOfFundsDocument;

/**
 * Validation for Transfer of Funds document that tests if the fund groups represented by a given document are in balance.
 */
public class TransferOfFundsFundGroupsBalancedValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;
    private ParameterService parameterService;

    /**
     * This is a helper method that wraps the fund group balancing check. This check can be configured by updating the 
     * application parameter table that is associated with this check. See the document's specification for details.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        return isFundGroupSetBalanceValid(accountingDocumentForValidation, TransferOfFundsDocument.class, AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.FUND_GROUP_BALANCING_SET);
    }
    
    /**
     * This method will make sure that totals for a specified set of fund groups is valid across the two different accounting line
     * sections.
     * 
     * @param tranDoc
     * @param fundGroupCodes An array of the fund group codes that will be considered for balancing.
     * @return True if they balance; false otherwise.
     */
    protected boolean isFundGroupSetBalanceValid(AccountingDocument tranDoc, Class componentClass, String parameterName) {
        // don't need to do any of this if there's no parameter
        if (!getParameterService().parameterExists(componentClass, parameterName)) {
            return true;
        }

        List lines = new ArrayList();

        lines.addAll(tranDoc.getSourceAccountingLines());
        lines.addAll(tranDoc.getTargetAccountingLines());

        KualiDecimal sourceLinesTotal = KualiDecimal.ZERO;
        KualiDecimal targetLinesTotal = KualiDecimal.ZERO;

        // iterate over each accounting line and if it has an account with a
        // fund group that should be balanced, then add that lines amount to the bucket
        for (Iterator i = lines.iterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            String fundGroupCode = line.getAccount().getSubFundGroup().getFundGroupCode();

            ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(componentClass, parameterName, fundGroupCode);
            if (evaluator.evaluationSucceeds()) {
                KualiDecimal glpeLineAmount = tranDoc.getGeneralLedgerPendingEntryAmountForDetail(line);
                if (line.isSourceAccountingLine()) {
                    sourceLinesTotal = sourceLinesTotal.add(glpeLineAmount);
                }
                else {
                    targetLinesTotal = targetLinesTotal.add(glpeLineAmount);
                }
            }
        }

        // check that the amounts balance across sections
        boolean isValid = true;

        if (sourceLinesTotal.compareTo(targetLinesTotal) != 0) {
            isValid = false;

            // creating an evaluator to just format the fund codes into a nice string
            ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(componentClass, parameterName, "");
            GlobalVariables.getErrorMap().putError("document.sourceAccountingLines", ERROR_DOCUMENT_FUND_GROUP_SET_DOES_NOT_BALANCE, new String[] { tranDoc.getSourceAccountingLinesSectionTitle(), tranDoc.getTargetAccountingLinesSectionTitle(), evaluator.getParameterValuesForMessage() });
        }

        return isValid;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
