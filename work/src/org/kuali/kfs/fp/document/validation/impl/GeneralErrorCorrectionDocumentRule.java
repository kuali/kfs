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

import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import static org.kuali.KeyConstants.GeneralErrorCorrection.ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_SUB_TYPE_CODE;
import static org.kuali.KeyConstants.GeneralErrorCorrection.ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE;
import static org.kuali.KeyConstants.GeneralErrorCorrection.ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_WITH_SUB_TYPE_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.REFERENCE_NUMBER;
import static org.kuali.PropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.COMBINED_RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.COMBINED_RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.GENERAL_ERROR_CORRECTION_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.TRANSACTION_LEDGER_ENTRY_DESCRIPTION_DELIMITER;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;

/**
 * Business rule(s) applicable to <code>{@link org.kuali.module.financial.document.GeneralErrorCorrectionDocument}</code>
 * instances.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class GeneralErrorCorrectionDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * Convenience method for accessing the most-likely requested security grouping
     * 
     * @return String
     */
    @Override
    protected String getDefaultSecurityGrouping() {
        return GENERAL_ERROR_CORRECTION_SECURITY_GROUPING;
    }

    /**
     * Convenience method for accessing delimiter for the <code>TransactionLedgerEntryDescription</code> of a
     * <code>{@link GeneralLedgerPendingEntry}</code>
     * 
     * @return String
     */
    protected String getEntryDescriptionDelimiter() {
        return TRANSACTION_LEDGER_ENTRY_DESCRIPTION_DELIMITER;
    }

    /**
     * Helper method for business rules concerning <code>{@link AccountingLine}</code> instances.
     * 
     * @param document
     * @param accountingLine
     * @return boolean pass or fail
     */
    private boolean processGenericAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        ObjectCode objectCode = accountingLine.getObjectCode();

        retval = isObjectTypeAndObjectSubTypeAllowed(objectCode);

        if (retval) {
            retval = isRequiredReferenceFieldsValid(accountingLine);
        }

        return retval;
    }

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
     * The GEC allows one sided documents for correcting - so if one side is empty, the other side must have at least two lines in
     * it. The balancing rules take care of validation of amounts.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(transactionalDocument);
    }

    /**
     * Overrides to call super and then GEC specific accounting line rules.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;
        retval = super.processCustomAddAccountingLineBusinessRules(document, accountingLine);
        if (retval) {
            retval = processGenericAccountingLineBusinessRules(document, accountingLine);
        }
        return retval;
    }


    /**
     * Overrides to call super and then GEC specific accounting line rules.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        retval = super.processCustomReviewAccountingLineBusinessRules(document, accountingLine);
        if (retval) {
            retval = processGenericAccountingLineBusinessRules(document, accountingLine);
        }

        return retval;
    }
    
    /**
     * @see TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setTransactionLedgerEntryDescription(buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(transactionalDocument, accountingLine));

        // Clearing fields that are already handled by the parent algorithm - we don't actually want
        // these to copy over from the accounting lines b/c they don't belond in the GLPEs
        // if the aren't nulled, then GECs fail to post
        explicitEntry.setReferenceFinancialDocumentNumber(null);
        explicitEntry.setReferenceFinancialSystemOriginationCode(null);
        explicitEntry.setReferenceFinancialDocumentTypeCode(null);
    }
    
    /**
     * Builds an appropriately formatted string to be used for the <code>transactionLedgerEntryDescription</code>. It is built
     * using information from the <code>{@link AccountingLine}</code>.  Format is "01-12345: blah blah blah".
     * 
     * @param line
     * @param transactionalDocument
     * @return String
     */
    private String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(TransactionalDocument transactionalDocument, AccountingLine line) {
        String description = "";
        if(StringUtils.isBlank(line.getReferenceOriginCode()) || StringUtils.isBlank(line.getReferenceNumber())) {
            throw new IllegalStateException("Reference Origin Code and Reference Document Number are required and should be validated before this point.");
        }
        
        description = line.getReferenceOriginCode() + "-" + line.getReferenceNumber();
        
        if(StringUtils.isNotBlank(line.getFinancialDocumentLineDescription())) {
            description += ": " + line.getFinancialDocumentLineDescription();
        } else {
            description += ": " + transactionalDocument.getDocumentHeader().getFinancialDocumentDescription();
        }
        
        if(description.length() > GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH) {
            description = description.substring(0, GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH - 3)+ "...";
        }
            
        return description;
    }

    /**
     * Used to determine of object code sub types are valid with the object type code.
     * 
     * @param code
     * @return boolean
     */
    protected boolean isObjectTypeAndObjectSubTypeAllowed(ObjectCode code) {
        boolean retval = true;

        retval = !(failsRule(COMBINED_RESTRICTED_OBJECT_TYPE_CODES, code.getFinancialObjectTypeCode()) && failsRule(COMBINED_RESTRICTED_OBJECT_SUB_TYPE_CODES, code.getFinancialObjectSubTypeCode()));

        if (!retval) {
            // add message
            GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_WITH_SUB_TYPE_CODE, new String[] { code.getFinancialObjectCode(), code.getFinancialObjectTypeCode(), code.getFinancialObjectSubTypeCode() });
        }

        return retval;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to General Error Correction specific rules. This
     * method leverages the APC for checking restricted object type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectTypeAllowed(accountingLine);

        if (valid) {
            ObjectCode objectCode = accountingLine.getObjectCode();

            if (failsRule(RESTRICTED_OBJECT_TYPE_CODES, objectCode.getFinancialObjectTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectTypeCode() });
            }
        }

        return valid;
    }

    /**
     * This method checks that values exist in the two reference fields ENCUMBRANCE.
     * 
     * @param accountingLine
     * @return True if all of the required external encumbrance reference fields are valid, false otherwise.
     */
    private boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;
        Class alclass = null;
        BusinessObjectEntry boe;

        if (accountingLine instanceof SourceAccountingLine) {
            alclass = SourceAccountingLine.class;
        }
        else if (accountingLine instanceof TargetAccountingLine) {
            alclass = TargetAccountingLine.class;
        }

        boe = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(alclass);
        if (StringUtils.isEmpty(accountingLine.getReferenceOriginCode())) {
            putRequiredPropertyError(boe, REFERENCE_ORIGIN_CODE);
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            putRequiredPropertyError(boe, REFERENCE_NUMBER);
            valid = false;
        }
        return valid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to General Error Correction specific rules. This
     * method leverages the APC for checking restricted object sub type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectSubTypeAllowed(accountingLine);

        if (valid) {
            ObjectCode objectCode = accountingLine.getObjectCode();

            if (failsRule(RESTRICTED_OBJECT_SUB_TYPE_CODES, objectCode.getFinancialObjectSubTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
            }
        }

        return valid;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.SourceAccountingLine)
     */
    @Override
    protected SufficientFundsItem processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument, SourceAccountingLine sourceAccountingLine) {
        return processAccountingLineSufficientFundsCheckingPreparation(transactionalDocument, sourceAccountingLine);
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.TargetAccountingLine)
     */
    @Override
    protected SufficientFundsItem processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument, TargetAccountingLine targetAccountingLine) {
        return processAccountingLineSufficientFundsCheckingPreparation(transactionalDocument, targetAccountingLine);
    }

    /**
     * Helper method to build a <code>{@link SufficientFundsItem}</code> from a <code>{@link TransactionalDocument}</code> and a
     * <code>{@link AccountingLine}</code>
     * 
     * @param document
     * @param accountingLine
     * @return SufficientFundsItem
     */
    private SufficientFundsItem processAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument document, AccountingLine accountingLine) {
        SufficientFundsItem item = null;
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        String accountSufficientFundsCode = accountingLine.getAccount().getAccountSufficientFundsCode();
        String financialObjectCode = accountingLine.getObjectCode().getFinancialObjectCode();
        String financialObjectLevelCode = accountingLine.getObjectCode().getFinancialObjectLevelCode();
        KualiDecimal lineAmount = getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine);
        Integer fiscalYear = accountingLine.getPostingYear();
        String financialObjectTypeCode = accountingLine.getObjectTypeCode();

        // always credit
        String debitCreditCode = null;

        if (isDebit(document, accountingLine)) {
            debitCreditCode = GL_CREDIT_CODE;
        }
        else {
            debitCreditCode = GL_DEBIT_CODE;
        }
        String sufficientFundsObjectCode = getSufficientFundsObjectCode(chartOfAccountsCode, financialObjectCode, accountSufficientFundsCode, financialObjectLevelCode);
        item = buildSufficentFundsItem(accountNumber, accountSufficientFundsCode, lineAmount, chartOfAccountsCode, sufficientFundsObjectCode, debitCreditCode, financialObjectCode, financialObjectLevelCode, fiscalYear, financialObjectTypeCode);

        return item;
    }

    /**
     * Helper method to get the sufficient funds object code.
     * 
     * @param chartOfAccountsCode
     * @param financialObjectCode
     * @param accountSufficientFundsCode
     * @param financialObjectLevelCode
     * @return String
     */
    private String getSufficientFundsObjectCode(String chartOfAccountsCode, String financialObjectCode, String accountSufficientFundsCode, String financialObjectLevelCode) {
        return SpringServiceLocator.getSufficientFundsService().getSufficientFundsObjectCode(chartOfAccountsCode, financialObjectCode, accountSufficientFundsCode, financialObjectLevelCode);
    }
}
