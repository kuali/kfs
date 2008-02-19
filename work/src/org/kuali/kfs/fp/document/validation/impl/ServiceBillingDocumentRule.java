/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.document.AccountingDocument;

/**
 * Business rule(s) applicable to Service Billing documents. They differ from {@link InternalBillingDocumentRule} by not routing for
 * fiscal officer approval. Instead, they route straight to final, by a formal pre-agreement between the service provider and the
 * department being billed, based on the service provider's ability to provide documentation for all transactions. These agreements
 * are configured in the Service Billing Control table by workgroup and income account number. This class enforces those agreements.
 */
public class ServiceBillingDocumentRule extends InternalBillingDocumentRule {

    /**
     * This method determines if an account is still accessible.  This is performed by one of two methods:
     * <ul>
     * <li> If the document is in 'initiated' or 'saved' status, then if the accounting line is a target line and the
     * account associated with that line is accessible based on ServiceBillingDocumentRuleUtils.serviceBillingIncomeAccountIsAccessible()</li>
     * <li> If the document is not in 'initiated' or 'saved' status, then call the super method to determine accessibility.</li>
     * </ul>
     * 
     * @param financialDocument The document used to retrieve the route status from.  The route status will impact how
     *                          accessibility of the account is determined.
     * @param accountingLine The accounting line the account is retrieved from.
     * @return True if the account is accessible, false otherwise.
     * 
     * @see AccountingDocumentRuleBase#accountIsAccessible(FinancialDocument, AccountingLine)
     * @see ServiceBillingDocumentRuleUtil#serviceBillingIncomeAccountIsAccessible(AccountingLine, org.kuali.kfs.rules.AccountingDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean accountIsAccessible(AccountingDocument financialDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            // The use from hasAccessibleAccountingLines() is not important for SB, which routes straight to final.
            return accountingLine.isTargetAccountingLine() || ServiceBillingDocumentRuleUtil.serviceBillingIncomeAccountIsAccessible(accountingLine, null);
        }
        return super.accountIsAccessible(financialDocument, accountingLine);
    }

    /**
     * This method determines if an account is still accessible.  This is performed by one of two methods:
     * <ul>
     * <li> If the document is in 'initiated' or 'saved' status, then if the accounting line is a target line and the
     * account associated with that line is accessible based on ServiceBillingDocumentRuleUtils.serviceBillingIncomeAccountIsAccessible()</li>
     * <li> If the document is not in 'initiated' or 'saved' status, then call the super method to determine accessibility.</li>
     * </ul>
     * 
     * @param financialDocument The document used to retrieve the route status from.  The route status will impact how
     *                          accessibility of the account is determined.
     * @param accountingLine The accounting line the account is retrieved from.
     * @param action The constant used to identify which error key to use when reporting errors.
     * @return True if the account is accessible, false otherwise.
     * 
     * @see FinancialDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.financial.rules.FinancialDocumentRuleBase.AccountingLineAction)
     * @see AccountingDocumentRuleBase.AccountingLineAction
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        // Duplicate code from accountIsAccessible() to avoid unnecessary calls to SB control and Workgroup services.
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            return accountingLine.isTargetAccountingLine() || ServiceBillingDocumentRuleUtil.serviceBillingIncomeAccountIsAccessible(accountingLine, action);
        }
        if (!super.accountIsAccessible(financialDocument, accountingLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, action.accessibilityErrorKey, accountingLine.getAccountNumber(), GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier());
            return false;
        }
        return true;
    }
}