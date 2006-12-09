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

import static org.kuali.KeyConstants.NonCheckDisbursement.ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_CONSOLIDATION_CODE;
import static org.kuali.KeyConstants.NonCheckDisbursement.ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_SUB_TYPE_CODE;
import static org.kuali.KeyConstants.NonCheckDisbursement.ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE;
import static org.kuali.KeyConstants.NonCheckDisbursement.ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_SUB_FUND_GROUP;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.REFERENCE_NUMBER;
import static org.kuali.module.financial.rules.NonCheckDisbursementDocumentRuleConstants.NON_CHECK_DISBURSEMENT_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.NonCheckDisbursementDocumentRuleConstants.RESTRICTED_CONSOLIDATION_CODES;
import static org.kuali.module.financial.rules.NonCheckDisbursementDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.NonCheckDisbursementDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.module.financial.rules.NonCheckDisbursementDocumentRuleConstants.RESTRICTED_SUB_FUND_GROUP_TYPE_CODES;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * Business rule(s) applicable to NonCheckDisbursement documents.
 * 
 * 
 */
public class NonCheckDisbursementDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * Convenience method for accessing the most-likely requested security grouping
     * 
     * @return String
     */
    @Override
    protected String getDefaultSecurityGrouping() {
        return NON_CHECK_DISBURSEMENT_SECURITY_GROUPING;
    }

    /**
     * Overrides to consider the object types.<br/>
     * 
     * <p>
     * Note: This <code>{@link org.kuali.core.document.Document} is always balanced because it only
     * has From: lines.
     *
     * @see TransactionalDocumentRuleBase#isDocumentBalanceValid(TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Overrides to call super and then NCD specific accounting line rules.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;
        retval = super.processCustomAddAccountingLineBusinessRules(document, accountingLine);
        if (retval) {
            retval = isRequiredReferenceFieldsValid(accountingLine);
        }
        return retval;
    }

    /**
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(TransactionalDocumentRuleBase, TransactionalDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws IllegalStateException {
        return IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, transactionalDocument, accountingLine);
    }
    
    /**
     * overrides the parent to display correct error message for a single sided document
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        if (0 == transactionalDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + PropertyConstants.SOURCE_ACCOUNTING_LINES, KeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * Overrides the parent to return true, because NonCheckDisbursement documents only use the SourceAccountingLines data
     * structures. The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or
     * submitted to post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
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
     * using information from the <code>{@link AccountingLine}</code>. Format is "01-12345: blah blah blah".
     * 
     * @param line
     * @param transactionalDocument
     * @return String
     */
    private String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(TransactionalDocument transactionalDocument, AccountingLine line) {
        String description = "";
        if (StringUtils.isBlank(line.getReferenceNumber())) {
            throw new IllegalStateException("Reference Document Number is required and should be validated before this point.");
        }

        description = Constants.ORIGIN_CODE_KUALI + "-" + line.getReferenceNumber();

        if (StringUtils.isNotBlank(line.getFinancialDocumentLineDescription())) {
            description += ": " + line.getFinancialDocumentLineDescription();
        }
        else {
            description += ": " + transactionalDocument.getDocumentHeader().getFinancialDocumentDescription();
        }

        if (description.length() > GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH) {
            description = description.substring(0, GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH - 3) + "...";
        }

        return description;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to Non-Check Disbursement specific rules. This method
     * leverages the APC for checking restricted object type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectTypeAllowed(accountingLine);

        ObjectCode objectCode = accountingLine.getObjectCode();

        if (valid) {
            valid = succeedsRule(RESTRICTED_OBJECT_TYPE_CODES, objectCode.getFinancialObjectTypeCode());
        }

        if (!valid) {
            // add message
            GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectTypeCode() });
        }

        return valid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to Non-Check Disbursement specific rules. This method
     * leverages the APC for checking restricted object sub type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectSubTypeAllowed(accountingLine);

        ObjectCode objectCode = accountingLine.getObjectCode();

        if (valid) {
            valid = succeedsRule(RESTRICTED_OBJECT_SUB_TYPE_CODES, objectCode.getFinancialObjectSubTypeCode());
        }

        if (!valid) {
            // add message
            GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
        }

        return valid;
    }

    /**
     * @see TransactionalDocumentRuleBase#isSubFundGroupAllowed(AccountingLine accountingLine)
     */
    @Override
    public boolean isSubFundGroupAllowed(AccountingLine accountingLine) {
        boolean valid = super.isSubFundGroupAllowed(accountingLine);

        String subFundGroupTypeCode = accountingLine.getAccount().getSubFundGroup().getSubFundGroupTypeCode();
        ObjectCode objectCode = accountingLine.getObjectCode();

        if (valid) {
            valid = succeedsRule(RESTRICTED_SUB_FUND_GROUP_TYPE_CODES, subFundGroupTypeCode);
        }

        if (!valid) {
            // add message
            GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_SUB_FUND_GROUP, new String[] { objectCode.getFinancialObjectCode(), subFundGroupTypeCode });
        }

        return valid;
    }

    /**
     * @see TransactionalDocumentRuleBase#isObjectConsolidationAllowed(AccountingLine accountingLine)
     */
    @Override
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        ObjectCode objectCode = accountingLine.getObjectCode();
        String consolidationCode = objectCode.getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        valid &= succeedsRule(RESTRICTED_CONSOLIDATION_CODES, consolidationCode);

        if (!valid) {
            // add message
            GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_CONSOLIDATION_CODE, new String[] { objectCode.getFinancialObjectCode(), consolidationCode });
        }

        return valid;
    }

    /**
     * This method checks that values exist in the three reference fields that are required
     * 
     * @param accountingLine
     * @return True if all of the required reference fields are valid, false otherwise.
     */
    private boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        BusinessObjectEntry boe = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(SourceAccountingLine.class);
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            putRequiredPropertyError(boe, REFERENCE_NUMBER);
            valid = false;
        }
        return valid;
    }
}