/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * This class holds document specific business rules for the Distribution of Income and Expense. It overrides methods in the base
 * rule class to apply specific checks.
 * 
 * 
 */
public class DistributionOfIncomeAndExpenseDocumentRule extends TransactionalDocumentRuleBase implements DistributionOfIncomeAndExpenseDocumentRuleConstants {

    /**
     * @see IsDebitUtils#isDebitConsideringSectionAndTypePositiveOnly(TransactionalDocumentRuleBase, TransactionalDocument,
     *      AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        return IsDebitUtils.isDebitConsideringSectionAndTypePositiveOnly(this, transactionalDocument, accountingLine);
    }

    /**
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectSubTypeAllowed(accountingLine);
        if (valid) {
            KualiParameterRule rule = getParameterRule(DISTRIBUTION_OF_INCOME_AND_EXPENSE_DOCUMENT_SECURITY_GROUPING, RESTRICTED_SUB_TYPE_GROUP_CODES);
            String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            valid = !rule.failsRule(objectSubTypeCode);

            if (!valid) {
                reportError(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.DistributionOfIncomeAndExpense.ERROR_DOCUMENT_DI_INVALID_OBJ_SUB_TYPE, objectCode.getFinancialObjectCode(), objectSubTypeCode);
            }
        }
        return valid;
    }

    /**
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectTypeAllowed(accountingLine);

        if (valid) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(DISTRIBUTION_OF_INCOME_AND_EXPENSE_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();

            String objectTypeCode = objectCode.getFinancialObjectTypeCode();

            valid = !rule.failsRule(objectTypeCode);
            if (!valid) {
                // add message
                GlobalVariables.getErrorMap().putError(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.DistributionOfIncomeAndExpense.ERROR_DOCUMENT_DI_INVALID_OBJECT_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectTypeCode });
            }
        }

        return valid;
    }

    /**
     * The DI allows one sided documents for correcting - so if one side is empty, the other side must have at least two lines in
     * it. The balancing rules take care of validation of amounts.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(transactionalDocument);
    }
}
