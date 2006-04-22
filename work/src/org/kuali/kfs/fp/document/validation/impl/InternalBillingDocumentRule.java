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

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ExceptionUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.financial.document.InternalBillingDocument;

/**
 * Business rule(s) applicable to InternalBilling document.
 *
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class InternalBillingDocumentRule extends TransactionalDocumentRuleBase implements InternalBillingDocumentRuleConstants {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDebit(org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {
        return isDebitConsideringSection(accountingLine);
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(TransactionalDocument, AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean success = true;
        success &= validIndianaStudentFeesNotContinueEduc(accountingLine);
        if (success) {
            success &= validateCapitalObjectCodes(accountingLine);
        }
        return success;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(TransactionalDocument, AccountingLine)
     */
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean success = true;
        success &= validIndianaStudentFeesNotContinueEduc(accountingLine);
        if (success) {
            success &= validateCapitalObjectCodes(accountingLine);
        }
        return success;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(TransactionalDocument, AccountingLine, AccountingLine)
     */
    public boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument document,
                                                                  AccountingLine originalAccountingLine,
                                                                  AccountingLine updatedAccountingLine)
    {
        boolean success = true;
        success &= validIndianaStudentFeesNotContinueEduc(updatedAccountingLine);
        if (success) {
            success &= validateCapitalObjectCodes(updatedAccountingLine);
        }
        return success;
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
                new String[]{accountingLine.getFinancialObjectCode(),
                    objectSubTypeCode,
                    requiredSubFundGroupCode,
                    actualSubFundGroupCode});
            return false;
        }
        return true;
    }

    /**
     * @see TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        // This super method actually does something.
        boolean success = true;
        success &= super.processCustomRouteDocumentBusinessRules(document);
        if (success) {
            success &= validateItems((InternalBillingDocument) document);
        }
        // todo: for phase II, when capital object codes are allowed on expense accounting lines, check that there are any if and
        // only if the Capital Assets tab contains information about the associated capital asset.
        // todo: for phase II, check that this bills for no more than one capital asset.
        return success;
    }

    private static boolean validateItems(InternalBillingDocument internalBillingDocument) {
        final ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.addToErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        int originalErrorCount = errorMap.getErrorCount();
        for (int i = 0; i < internalBillingDocument.getItems().size(); i++) {
            // todo: expose and use a method for this List handling in DictionaryValidationService
            String propertyName = "item[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(internalBillingDocument.getItem(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }
        // todo: return a boolean from DictionaryValidationService instead of checking errorMap.  KULNRVSYS-1093
        int currentErrorCount = errorMap.getErrorCount();
        errorMap.removeFromErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        return currentErrorCount == originalErrorCount;
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
     * Evaluates the object sub type code of the accounting line's object code to determine whether the object code is a capital
     * object code.  If so, and this accounting line is in the income section, then it is not valid.
     * <p/>
     * Note: this is an IU specific business rule.
     *
     * @param accountingLine
     * @return whether the given line is valid with respect to capital object codes
     */
    private boolean validateCapitalObjectCodes(AccountingLine accountingLine) {
        if (isSourceAccountingLine(accountingLine) && isCapitalObject(accountingLine)) {
            GlobalVariables.getErrorMap().put(PropertyConstants.FINANCIAL_OBJECT_CODE,
                KeyConstants.ERROR_DOCUMENT_IB_CAPITAL_OBJECT_IN_INCOME_SECTION);
            LOG.debug("APC rule failure " + ExceptionUtils.describeStackLevel(0));
            return false;
        }
        else {
            return true;
        }
        // todo: phase II
        // int pendPurchaseCount = 0; // TODO need to do something with this but I have no idea what
        // if (!SUB_FUND_GROUP_CODE.CODE_EXTAGY.equals(subFundGroupCode) && restrictedCapitalObjectCodes.contains(objectSubTypeCode)
        //     && (pendPurchaseCount <= 0))
    }

    private boolean isCapitalObject(AccountingLine accountingLine) {
        return getParameterRule(INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING, CAPITAL_OBJECT_SUB_TYPE_CODES).succeedsRule(
            accountingLine.getObjectCode().getFinancialObjectSubTypeCode());
    }

    /**
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectTypeAllowed(AccountingLine)
     */
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        return indirectRuleSucceeds(getParameterRule(INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES),
            new AttributeReference(SourceAccountingLine.class, PropertyConstants.FINANCIAL_OBJECT_CODE,
                accountingLine.getFinancialObjectCode()),
            new AttributeReference(ObjectCode.class, PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE,
                accountingLine.getObjectCode().getFinancialObjectTypeCode()));
    }

    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code isn't restricted according to the APC.
     *
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectSubTypeAllowed(AccountingLine)
     */
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        return indirectRuleSucceeds(getParameterRule(INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_SUB_TYPE_CODES),
            new AttributeReference(SourceAccountingLine.class, PropertyConstants.FINANCIAL_OBJECT_CODE,
                accountingLine.getFinancialObjectCode()),
            new AttributeReference(ObjectCode.class, PropertyConstants.FINANCIAL_OBJECT_SUB_TYPE_CODE,
                accountingLine.getObjectCode().getFinancialObjectSubTypeCode()));
    }

    /**
     * Overrides the parent's implementation to check to make sure that the provided object code's level isn't a contract and grants
     * level.
     *
     * @see TransactionalDocumentRuleBase#isObjectLevelAllowed(AccountingLine)
     */
    public boolean isObjectLevelAllowed(AccountingLine accountingLine) {
        return indirectRuleSucceeds(getParameterRule(INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_LEVEL_CODES),
            new AttributeReference(SourceAccountingLine.class, PropertyConstants.FINANCIAL_OBJECT_CODE,
                accountingLine.getFinancialObjectCode()),
            new AttributeReference(ObjectCode.class, PropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE,
                accountingLine.getObjectCode().getFinancialObjectLevelCode()));
    }

    /**
     * This implementation overrides the parent to check and make sure that the fund group is not a Loan fund group.
     *
     * @see TransactionalDocumentRuleBase#isFundGroupAllowed(AccountingLine)
     */
    public boolean isFundGroupAllowed(AccountingLine accountingLine) {
        return indirectRuleSucceeds(getParameterRule(INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING, RESTRICTED_FUND_GROUP_CODES),
            new AttributeReference(SourceAccountingLine.class, PropertyConstants.ACCOUNT_NUMBER, accountingLine.getAccountNumber()),
            new AttributeReference(SubFundGroup.class, PropertyConstants.FUND_GROUP_CODE,
                accountingLine.getAccount().getSubFundGroup().getFundGroupCode()));
        // This calls for double indirection, but I'm not sure if such an error message would be more user friendly.
    }

    /**
     * Overrides the parent's implementation to check and make sure that the sub fund group is not the Retire Indebt or the
     * Investment Plant sub fund group.
     *
     * @see TransactionalDocumentRuleBase#isSubFundGroupAllowed(AccountingLine)
     */
    public boolean isSubFundGroupAllowed(AccountingLine accountingLine) {
        return indirectRuleSucceeds(getParameterRule(INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING, RESTRICTED_SUB_FUND_GROUP_CODES),
            new AttributeReference(SourceAccountingLine.class, PropertyConstants.ACCOUNT_NUMBER, accountingLine.getAccountNumber()),
            new AttributeReference(Account.class, PropertyConstants.SUB_FUND_GROUP_CODE,
                accountingLine.getAccount().getSubFundGroupCode()));
    }
}