/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;

/**
 * This class holds document specific business rules for the Distribution of Income and Expense. It overrides methods in the base
 * rule class to apply specific checks.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DistributionOfIncomeAndExpenseDocumentRule extends TransactionalDocumentRuleBase implements
        DistributionOfIncomeAndExpenseDocumentRuleConstants {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDebit(org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {
        if (accountingLine.getAmount().isNegative()) {
            throw new IllegalStateException("Negative amounts are not allowed.");
        }
        return isDebitConsideringSection(accountingLine);
    }

    /**
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        TransactionalDocument transactionalDocument = (TransactionalDocument) document;

        boolean success = isDocumentBalanceValid(transactionalDocument);

        if (success) {
            success &= isAccountingLinesRequiredNumberForRoutingMet(transactionalDocument);
        }

        return success;
    }

    /**
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectSubTypeAllowed(accountingLine);
        if (valid) {
            KualiParameterRule rule = getParameterRule(DISTRIBUTION_OF_INCOME_AND_EXPENSE_DOCUMENT_SECURITY_GROUPING,
                    RESTRICTED_SUB_TYPE_GROUP_CODES);
            String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            String objectSubType = objectCode.getFinancialObjectSubTypeCode();

            valid = rule.failsRule(objectSubTypeCode);

            if (!valid) {
                reportError(PropertyConstants.FINANCIAL_OBJECT_CODE,
                        KeyConstants.DistributionOfIncomeAndExpense.ERROR_DOCUMENT_DI_INVALID_OBJ_SUB_TYPE, new String[] {
                                objectCode.getFinancialObjectCode(), objectSubTypeCode });
            }
        }
        return valid;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        boolean isValid = super.isDocumentBalanceValid(transactionalDocument);

        if (isValid) {
            List accountingLines = new ArrayList();
            accountingLines.addAll(transactionalDocument.getSourceAccountingLines());
            accountingLines.addAll(transactionalDocument.getTargetAccountingLines());

            KualiDecimal expense = KualiDecimal.ZERO;
            KualiDecimal revenue = KualiDecimal.ZERO;

            for (Iterator i = accountingLines.iterator(); i.hasNext();) {
                AccountingLine accountingLine = (AccountingLine) i.next();
                KualiDecimal amount = accountingLine.getAmount();

                boolean isExpense = isExpenseOrAsset(accountingLine);
                boolean isSourceLine = isSourceAccountingLine(accountingLine);

                if (isExpense) {
                    if (isSourceLine) {
                        expense = expense.add(amount);
                    }
                    else {
                        expense = expense.subtract(amount);
                    }
                }
                else {
                    if (isSourceLine) {
                        revenue.add(amount);
                    }
                    else {
                        revenue.subtract(amount);
                    }
                }
            }
            if (!expense.equals(revenue)) {
                isValid = false;
                reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BALANCE, new String[] {
                        transactionalDocument.getSourceAccountingLinesSectionTitle(),
                        transactionalDocument.getTargetAccountingLinesSectionTitle() });
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.core.rule.AccountingLineRule#isObjectConsolidationAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectConsolidationAllowed(accountingLine);

        if (valid) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    DISTRIBUTION_OF_INCOME_AND_EXPENSE_DOCUMENT_SECURITY_GROUPING, RESTRICTED_CONSOLIDATED_OBJECT_CODES_GROUP);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            ObjLevel objectLevel = objectCode.getFinancialObjectLevel();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            String consolidatedObjectCode = objectLevel.getConsolidatedObjectCode();

            if (rule.failsRule(consolidatedObjectCode)) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().put(
                        PropertyConstants.FINANCIAL_OBJECT_CODE,
                        KeyConstants.DistributionOfIncomeAndExpense.ERROR_DOCUMENT_DI_INVALID_CONSOLIDATION_OBJECT_CODE,
                        new String[] { objectCode.getFinancialObjectCode(), objectLevel.getFinancialObjectLevelCode(),
                                consolidatedObjectCode });
            }
        }

        return valid;
    }

    /**
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectTypeAllowed(accountingLine);

        if (valid) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    DISTRIBUTION_OF_INCOME_AND_EXPENSE_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            ObjectType objectType = objectCode.getFinancialObjectType();
            if (ObjectUtils.isNull(objectType)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_TYPE_CODE);
            }

            String objectTypeCode = objectCode.getFinancialObjectTypeCode();

            if (rule.failsRule(objectTypeCode)) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().put(PropertyConstants.FINANCIAL_OBJECT_CODE,
                        KeyConstants.DistributionOfIncomeAndExpense.ERROR_DOCUMENT_DI_INVALID_OBJECT_TYPE_CODE,
                        new String[] { objectCode.getFinancialObjectCode(), objectTypeCode });
            }
        }

        return valid;
    }
}
