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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ExceptionUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;

/**
 * Business rule(s) applicable to InternalBilling document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class InternalBillingDocumentRule extends TransactionalDocumentRuleBase {
    private static final Logger LOG = Logger.getLogger(TransactionalDocumentRuleBase.class);


    // Set container for restricted capital object codes
    protected static Set restrictedCapitalObjectCodes;

    // Set container for restricted object sub type codes
    protected static Set restrictedObjectSubTypeCodes;

    // initialize some static data structures that will be used for business
    // rule checks
    static {
        initializeRestrictedCapitalObjectCodes();
        initializeRestrictedObjectSubTypeCodes();
    }

    /**
     * This method sets up the restricted capital object code set that will be checked against by business rules.
     */
    private static void initializeRestrictedCapitalObjectCodes() {
        restrictedCapitalObjectCodes = new TreeSet();
        restrictedCapitalObjectCodes.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
        restrictedCapitalObjectCodes.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
        restrictedCapitalObjectCodes.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
        restrictedCapitalObjectCodes.add(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
        restrictedCapitalObjectCodes.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
        restrictedCapitalObjectCodes.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
        restrictedCapitalObjectCodes.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
    }

    /**
     * This method sets up the restricted object sub type codes that will be checked against by business rules.
     */
    private static void initializeRestrictedObjectSubTypeCodes() {
        restrictedObjectSubTypeCodes = new TreeSet();
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.RESERVES);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.STATE_APP);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.SALARIES);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.PLANT);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.INVEST);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.FRINGE_BEN);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.NON_MANDATORY_TRANSFER);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
        restrictedObjectSubTypeCodes.add(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
    }

    /**
     * @see org.kuali.core.rule.AddAccountingLineRule#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules( document, accountingLine ) && validateAccountingLine(document, accountingLine);
    }

    /**
     * @see org.kuali.core.rule.ReviewAccountingLineRule#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules( document, accountingLine ) && validateAccountingLine(document, accountingLine);
    }

    /**
     * Note: This method implements an IU specific business rule.
     * 
     * This method evaluates the accounting line's object sub type code for its object code in addition to the accounting line's sub
     * fund group for the account. This didn't fit any of the interface methods, so this rule was programmed in the "custom rule"
     * method.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean validateAccountingLine(TransactionalDocument document, AccountingLine accountingLine) {
        String objectCode = accountingLine.getObjectCode().getFinancialObjectCode();
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubType().getCode();
        String subFundGroupCode = accountingLine.getAccount().getSubFundGroup().getSubFundGroupCode();

        // TODO - This is an Indiana only requirement based on the specification doc
        if (OBJECT_SUB_TYPE_CODE.STUDENT_FEES.equals(objectSubTypeCode)
                && !SUB_FUND_GROUP_CODE.CONTINUE_EDUC.equals(subFundGroupCode)) {
            // objects
            String errorObjects[] = { objectCode, objectSubTypeCode };
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE,
                    errorObjects);
            return false;
        }

        return true;
    }

    public boolean processCustomRouteDocumentBusinessRules(TransactionalDocument document) {
        // commented out super call, since the super method is the only way this method gets called
        // super.processRouteDocument(doc);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        // TODO: Fill this in when Tony finishes his Utility to apply all core business rules.

        List sLines = document.getSourceAccountingLines();
        List tLines = document.getTargetAccountingLines();

        // First, validate the accounting lines
        // then. check for expense/revenue balance
        // finally, do object code restrictions checking
        KualiDecimal sExpense = new KualiDecimal(0);
        KualiDecimal sRevenue = new KualiDecimal(0);
        KualiDecimal tExpense = new KualiDecimal(0);
        KualiDecimal tRevenue = new KualiDecimal(0);

        for (Iterator i = sLines.iterator(); i.hasNext();) {
            SourceAccountingLine sal = (SourceAccountingLine) i.next();
            if (isExpenseOrAsset(sal)) {
                sExpense = sExpense.add(sal.getAmount());
            }
            else {
                sRevenue = sRevenue.add(sal.getAmount());
            }
        }

        for (Iterator i = tLines.iterator(); i.hasNext();) {
            TargetAccountingLine tal = (TargetAccountingLine) i.next();
            if (isExpenseOrAsset(tal)) {
                tExpense = tExpense.add(tal.getAmount());
            }
            else {
                tRevenue = tRevenue.add(tal.getAmount());
            }
        }

        KualiDecimal sDiff = sRevenue.subtract(sExpense);
        KualiDecimal tDiff = tRevenue.subtract(tExpense);
        if (!sDiff.equals(tDiff)) {
            reportError(Constants.DOCUMENT_ERRORS,
                    "Document does not balance. (sourceRevenue minus sourceExpense) should be equal to (targetRevenue minus targetExpense)!", "Amount");
            return false;
        }

        return errorMap.isEmpty();
    }

    /**
     * Note: this is an IU specific business rule.
     * 
     * This implementation evaluates the object sub type code of the accounting line's object code to determine whether the object
     * code is capital object code. If it is a capital object code, then this accounting line is not valid.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectCodeAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectCodeAllowed(AccountingLine accountingLine) {
        int pendPurchaseCount = 0; // TODO need to do something with this but I have no idea what
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubType().getCode();
        String subFundGroupCode = accountingLine.getAccount().getSubFundGroup().getSubFundGroupCode();

        // TODO purchase count still needs to be entered in
        if (!SUB_FUND_GROUP_CODE.CODE_EXTAGY.equals(subFundGroupCode) && restrictedCapitalObjectCodes.contains(objectSubTypeCode)
                && (pendPurchaseCount <= 0)) {
            String errorObjects[] = { objectSubTypeCode, subFundGroupCode };
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_LEVEL,
                    errorObjects);
            return false;
        }

        return true;
    }

    /**
     * This method overrides the parent's to call the parent's isObjectTypeAllowed() method first, and then makes sure that it's not
     * an Income Not Cash and a Expense Not Expenditure object type.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean isValid = true;

        isValid &= super.isObjectTypeAllowed(accountingLine);

        // if this is being set behind the scenes automatically, as most documents do,
        // this will not be set before the line is added
        if (accountingLine.getObjectTypeCode() != null) {
            // now check to make sure that the object type code is not an income not cash or expense not expenditure type code
            String objectTypeCode = accountingLine.getObjectTypeCode();
            if (OBJECT_TYPE_CODE.INCOME_NOT_CASH.equals(objectTypeCode)
                    || OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE.equals(objectTypeCode)) {
                reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_TYPE,
                        new String[] { objectTypeCode });
                isValid &= false;
            }
        }

        return isValid;
    }

    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code isn't restricted according to the above
     * initialized restriction list.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubType().getCode();

        if (restrictedObjectSubTypeCodes.contains(objectSubTypeCode)) {
            String errorObjects[] = { objectSubTypeCode };
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_SUB_TYPE, errorObjects);
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent's implementation to check to make sure that the provided object code's level isn't a contract and grants
     * level.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isObjectLevelAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectLevelAllowed(AccountingLine accountingLine) {
        String objectLevel = accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode();

        if (OBJECT_LEVEL_CODE.CONTRACT_GRANTS.equals(objectLevel)) {
            String errorObjects[] = { accountingLine.getObjectCode().getFinancialObjectCode(), objectLevel };
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_LEVEL,
                    errorObjects);
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent's implementation to check to make sure that the provided object code's consolidation isn't an asset of
     * liability consolidation.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isObjectConsolidationAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine) {
        String consolidatedObjectCode = accountingLine.getObjectCode().getFinancialObjectLevel().getConsolidatedObjectCode();

        if (CONSOLIDATED_OBJECT_CODE.ASSETS.equals(consolidatedObjectCode)
                || CONSOLIDATED_OBJECT_CODE.LIABILITIES.equals(consolidatedObjectCode)) {
            String errorObjects[] = { accountingLine.getObjectCode().getFinancialObjectCode(), consolidatedObjectCode };
            reportError(Constants.ACCOUNTING_LINE_ERRORS,
                    KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_CONSOLIDATED_OBJ_CODE, errorObjects);
            return false;
        }

        return true;
    }

    /**
     * This implementation overrides the parent to check and make sure that the fund group is not a Loan fund group.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isFundGroupAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isFundGroupAllowed(AccountingLine accountingLine) {
        String fundGroupCode = accountingLine.getAccount().getSubFundGroup().getFundGroup().getCode();

        if (FUND_GROUP_CODE.LOAN_FUND.equals(fundGroupCode)) {
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CUSTOM, "Invalid Fund Group for this eDoc");
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent's implementation to check and make sure that the sub fund group is not the Retire Indebt or the
     * Investment Plant sub fund group.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSubFundGroupAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isSubFundGroupAllowed(AccountingLine accountingLine) {
        String subFundGroupCode = accountingLine.getAccount().getSubFundGroup().getSubFundGroupCode();

        if (SUB_FUND_GROUP_CODE.CODE_RETIRE_INDEBT.equals(subFundGroupCode)
                || SUB_FUND_GROUP_CODE.CODE_INVESTMENT_PLANT.equals(subFundGroupCode)) {
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CUSTOM, "Invalid Sub Fund Group for this eDoc");
            return false;
        }

        return true;
    }

    /**
     * Wrapper around global errorMap.put call, to allow better logging
     * 
     * @param propertyName
     * @param errorKey
     * @param errorParam
     */
    private void reportError(String propertyName, String errorKey, String errorParam) {
        GlobalVariables.getErrorMap().put(propertyName, errorKey, errorParam);
        LOG.debug("InternalBillingDocument rule failure at " + ExceptionUtils.describeStackLevel(1));
    }

    /**
     * Wrapper around global errorMap.put call, to allow better logging
     * 
     * @param propertyName
     * @param errorKey
     * @param errorParams
     */
    private void reportError(String propertyName, String errorKey, String[] errorParams) {
        GlobalVariables.getErrorMap().put(propertyName, errorKey, errorParams);
        LOG.debug("InternalBillingDocument rule failure at " + ExceptionUtils.describeStackLevel(1));
    }
}