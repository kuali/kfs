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

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;

/**
 * This class holds document specific business rules for the Distribution of Income and Expense. It overrides methods in the base
 * rule class to apply specific checks.
 */
public class DistributionOfIncomeAndExpenseDocumentRule extends AccountingDocumentRuleBase implements DistributionOfIncomeAndExpenseDocumentRuleConstants {

    /**
     * The DI allows one sided documents for correcting - so if one side is empty, the other side must have at least two lines in
     * it. The balancing rules take care of validation of amounts.
     * 
     * @param financialDocument submitted accounting document
     * @return true number of accounting lines required for routing is met
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        return isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(financialDocument);
    }
}
