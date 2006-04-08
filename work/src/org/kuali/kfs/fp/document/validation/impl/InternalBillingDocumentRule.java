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

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;

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
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(TransactionalDocument, AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return validIndianaStudentFeesNotContinueEduc(accountingLine);
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(TransactionalDocument, AccountingLine)
     */
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return validIndianaStudentFeesNotContinueEduc(accountingLine);
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(TransactionalDocument, AccountingLine, AccountingLine)
     */
    public boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument document,
                                                                  AccountingLine originalAccountingLine,
                                                                  AccountingLine updatedAccountingLine)
    {
        return validIndianaStudentFeesNotContinueEduc(updatedAccountingLine);
    }

    /**
     * Note: This method implements an IU specific business rule.
     * <p/>
     * This method evaluates the accounting line's object sub type code for its object code in addition to the accounting line's sub
     * fund group for the account. This didn't fit any of the interface methods, so this rule was programmed in the "custom rule"
     * method.
     *
     * @return whether this rule passes
     */
    private boolean validIndianaStudentFeesNotContinueEduc(AccountingLine accountingLine) {
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();
        String actualSubFundGroupCode = accountingLine.getAccount().getSubFundGroupCode();
        String requiredSubFundGroupCode = SUB_FUND_GROUP_CODE.CONTINUE_EDUC;

        if (OBJECT_SUB_TYPE_CODE.STUDENT_FEES.equals(objectSubTypeCode)
            && !requiredSubFundGroupCode.equals(actualSubFundGroupCode))
        {
            // The user could fix this via either ObjectCode or Account, but we arbitrarily choose the ObjectCode to highlight.
            reportError(PropertyConstants.OBJECT_CODE, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_FUND_GROUP,
                new String[]{accountingLine.getFinancialObjectCode(), objectSubTypeCode, requiredSubFundGroupCode, actualSubFundGroupCode});
            return false;
        }
        return true;
    }

    /**
     * @see TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        // todo: for phase II, when capital object codes are allowed on expense accounting lines, check that there are any if and
        // only if the Capital Assets tab contains information about the associated capital asset.
        // todo: for phase II, check that this bills for no more than one capital asset.
        return super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * Overrides to consider the object types.
     *
     * @see TransactionalDocumentRuleBase#isDocumentBalanceValid(TransactionalDocument)
     */
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return isDocumentBalancedConsideringObjectTypes(transactionalDocument);
    }

    /**
     * Note: this is an IU specific business rule.
     * <p/>
     * This implementation evaluates the object sub type code of the accounting line's object code to determine whether the object
     * code is capital object code. If it is a capital object code, then this accounting line is not valid.
     *
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectCodeAllowed(AccountingLine)
     */
    public boolean isObjectCodeAllowed(AccountingLine accountingLine) {
        int pendPurchaseCount = 0; // TODO need to do something with this but I have no idea what
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubType().getCode();
        String subFundGroupCode = accountingLine.getAccount().getSubFundGroup().getSubFundGroupCode();

        // TODO purchase count still needs to be entered in
        if (!SUB_FUND_GROUP_CODE.CODE_EXTAGY.equals(subFundGroupCode) && restrictedCapitalObjectCodes.contains(objectSubTypeCode)
            && (pendPurchaseCount <= 0))
        {
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_LEVEL,
                new String[]{objectSubTypeCode, subFundGroupCode});
            return false;
        }

        return true;
    }

    /**
     * This method overrides the parent's to call the parent's isObjectTypeAllowed() method first, and then makes sure that it's not
     * an Income Not Cash and a Expense Not Expenditure object type.
     *
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectTypeAllowed(AccountingLine)
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
                || OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE.equals(objectTypeCode))
            {
                reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_TYPE, objectTypeCode);
                isValid &= false;
            }
        }

        return isValid;
    }

    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code isn't restricted according to the above
     * initialized restriction list.
     *
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectSubTypeAllowed(AccountingLine)
     */
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubType().getCode();

        if (restrictedObjectSubTypeCodes.contains(objectSubTypeCode)) {
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_SUB_TYPE, objectSubTypeCode);
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent's implementation to check to make sure that the provided object code's level isn't a contract and grants
     * level.
     *
     * @see TransactionalDocumentRuleBase#isObjectLevelAllowed(AccountingLine)
     */
    public boolean isObjectLevelAllowed(AccountingLine accountingLine) {
        String objectLevel = accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode();

        if (OBJECT_LEVEL_CODE.CONTRACT_GRANTS.equals(objectLevel)) {
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_LEVEL,
                new String[]{accountingLine.getObjectCode().getFinancialObjectCode(), objectLevel});
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent's implementation to check to make sure that the provided object code's consolidation isn't an asset of
     * liability consolidation.
     *
     * @see TransactionalDocumentRuleBase#isObjectConsolidationAllowed(AccountingLine)
     */
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine) {
        String consolidatedObjectCode = accountingLine.getObjectCode().getFinancialObjectLevel().getConsolidatedObjectCode();

        if (CONSOLIDATED_OBJECT_CODE.ASSETS.equals(consolidatedObjectCode)
            || CONSOLIDATED_OBJECT_CODE.LIABILITIES.equals(consolidatedObjectCode))
        {
            reportError(Constants.ACCOUNTING_LINE_ERRORS,
                KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_CONSOLIDATED_OBJ_CODE,
                new String[]{accountingLine.getObjectCode().getFinancialObjectCode(), consolidatedObjectCode});
            return false;
        }

        return true;
    }

    /**
     * This implementation overrides the parent to check and make sure that the fund group is not a Loan fund group.
     *
     * @see TransactionalDocumentRuleBase#isFundGroupAllowed(AccountingLine)
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
     * @see TransactionalDocumentRuleBase#isSubFundGroupAllowed(AccountingLine)
     */
    public boolean isSubFundGroupAllowed(AccountingLine accountingLine) {
        String subFundGroupCode = accountingLine.getAccount().getSubFundGroup().getSubFundGroupCode();

        if (SUB_FUND_GROUP_CODE.CODE_RETIRE_INDEBT.equals(subFundGroupCode)
            || SUB_FUND_GROUP_CODE.CODE_INVESTMENT_PLANT.equals(subFundGroupCode))
        {
            reportError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CUSTOM, "Invalid Sub Fund Group for this eDoc");
            return false;
        }

        return true;
    }
}