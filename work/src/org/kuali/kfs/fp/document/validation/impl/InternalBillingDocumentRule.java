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

import static org.kuali.module.financial.rules.InternalBillingDocumentRuleConstants.CAPITAL_OBJECT_SUB_TYPE_CODES;

import org.kuali.core.document.Document;
import org.kuali.core.util.ExceptionUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.document.InternalBillingDocument;

/**
 * Business rule(s) applicable to InternalBilling document.
 */
public class InternalBillingDocumentRule extends AccountingDocumentRuleBase {

    /**
     * This method determines if an accounting line is a debit accounting line by calling IsDebitUtils.isDebitConsideringSection().
     * 
     * @param transactionalDocument The document containing the accounting line being analyzed.
     * @param accountingLine The accounting line being reviewed to determine if it is a debit line or not.
     * @return True if the accounting line is a debit accounting line, false otherwise.
     * 
     * @see IsDebitUtils#isDebitConsideringSection(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        return IsDebitUtils.isDebitConsideringSection(this, transactionalDocument, accountingLine);
    }

    /**
     * Overrides to only disallow zero, allowing negative amounts.  
     * 
     * @param document The document which contains the accounting line being validated.
     * @param accountingLine The accounting line containing the amount being validated.
     * @return True if the amount is not zero, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAmountValid(FinancialDocument, AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        if (accountingLine.getAmount().equals(KFSConstants.ZERO)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
            return false;
        }
        return true;
    }

    /**
     * @param document 
     * @param accountingLine 
     * @return 
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(FinancialDocument,
     *      AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        return processCommonCustomAccountingLineRules(accountingLine);
    }

    /**
     * @param document 
     * @param accountingLine 
     * @return 
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(FinancialDocument,
     *      AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        return processCommonCustomAccountingLineRules(accountingLine);
    }

    /**
     * @param document 
     * @param accountingLine 
     * @return 
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(FinancialDocument,
     *      AccountingLine, AccountingLine)
     */
    @Override
    public boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument document, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCommonCustomAccountingLineRules(updatedAccountingLine);
    }

    /**
     * Processes rules common to the three custom accounting line rule methods.
     * 
     * @param accountingLine The accounting line the business rules will be applied to.
     * @return True if the custom business rule succeed, false otherwise.
     */
    private boolean processCommonCustomAccountingLineRules(AccountingLine accountingLine) {
        return validateCapitalObjectCodes(accountingLine);
    }

    /**
     * Evaluates the object sub type code of the accounting line's object code to determine whether the object code is a capital
     * object code. If so, and this accounting line is in the income section, then it is not valid. <p/> 
     * 
     * Note: this is an IU specific business rule.
     * 
     * @param accountingLine The accounting line the object code will be retrieved from.
     * @return True if the given line is valid with respect to capital object codes.
     */
    private boolean validateCapitalObjectCodes(AccountingLine accountingLine) {
        if (accountingLine.isSourceAccountingLine() && isCapitalObject(accountingLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_IB_CAPITAL_OBJECT_IN_INCOME_SECTION);
            LOG.debug("APC rule failure " + ExceptionUtils.describeStackLevel(0));
            return false;
        }
        else {
            return true;
        }
        // TODO phase II
        // int pendPurchaseCount = 0; 
        // TODO need to do something with this but I have no idea what
        // if (!SUB_FUND_GROUP_CODE.CODE_EXTAGY.equals(subFundGroupCode) && restrictedCapitalObjectCodes.contains(objectSubTypeCode)
        // && (pendPurchaseCount <= 0))
    }

    /**
     * Checks whether the given AccountingLine's ObjectCode is a capital one.
     * 
     * @param accountingLine The accounting line the object code will be retrieved from.
     * @return True if the given accounting line's object code is a capital code, false otherwise.
     */
    private boolean isCapitalObject(AccountingLine accountingLine) {
        ParameterEvaluator evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(InternalBillingDocument.class, CAPITAL_OBJECT_SUB_TYPE_CODES, accountingLine.getObjectCode().getFinancialObjectSubTypeCode());
        return evaluator.evaluationSucceeds();
    }

    /**
     * This method overrides the processCustomRouteDocumentBusinessRules() method in AccountingDocumentRuleBase to 
     * allow for additional rules to be run prior to routing.  In addition to calling the parent method to perform the 
     * general business rule checks, this method also performs a validation check on all InternalBillingItems associated 
     * with the given document.  
     * 
     * @param document The document being routed.
     * @return True if the parent method finds no business rule problems and all associated InternalBillingItems are valid.
     * 
     * @see FinancialDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        // This super method actually does something.
        boolean success = true;
        success &= super.processCustomRouteDocumentBusinessRules(document);
        if (success) {
            success &= validateItems((InternalBillingDocument) document);
        }
        // TODO: for phase II, when capital object codes are allowed on expense accounting lines, check that there are any if and
        // only if the Capital Assets tab contains information about the associated capital asset.
        // TODO: for phase II, check that this bills for no more than one capital asset.
        return success;
    }

    /**
     * Validates all the InternalBillingItems in the given Document, adding global errors for invalid items. It just uses the
     * DataDictionary validation.
     * 
     * @param internalBillingDocument The document the InternalBillingItems will be retrieved from to validate.
     * @return Whether or not any associated items within the given document are invalid.
     */
    private boolean validateItems(InternalBillingDocument internalBillingDocument) {
        boolean retval = true;
        for (int i = 0; i < internalBillingDocument.getItems().size(); i++) {
            String propertyName = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.ITEM + "[" + i + "]";
            retval &= getDictionaryValidationService().isBusinessObjectValid(internalBillingDocument.getItem(i), propertyName);
        }
        return retval;
    }
}