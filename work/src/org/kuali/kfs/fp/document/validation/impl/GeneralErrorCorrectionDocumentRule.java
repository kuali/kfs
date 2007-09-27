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

import static org.kuali.kfs.KFSKeyConstants.GeneralErrorCorrection.ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_SUB_TYPE_CODE;
import static org.kuali.kfs.KFSKeyConstants.GeneralErrorCorrection.ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE;
import static org.kuali.kfs.KFSKeyConstants.GeneralErrorCorrection.ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_WITH_SUB_TYPE_CODE;
import static org.kuali.kfs.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.COMBINED_RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.COMBINED_RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.GENERAL_ERROR_CORRECTION_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRuleConstants.TRANSACTION_LEDGER_ENTRY_DESCRIPTION_DELIMITER;
import static org.kuali.module.financial.rules.NonCheckDisbursementDocumentRuleConstants.NON_CHECK_DISBURSEMENT_SECURITY_GROUPING;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * Business rule(s) applicable to <code>{@link org.kuali.module.financial.document.GeneralErrorCorrectionDocument}</code>
 * instances.
 */
public class GeneralErrorCorrectionDocumentRule extends AccountingDocumentRuleBase {

    /**
     * Convenience method for accessing the most-likely requested security grouping
     * 
     * @return String
     */
    protected String getDefaultParameterNamespace() {
        return KFSConstants.FINANCIAL_NAMESPACE;
    }
    
    /**
     * Convenience method for accessing the most-likely requested detail type code
     * 
     * @return String
     */
    protected String getDefaultParameterDetailTypeCode() {
        return KFSConstants.Components.GENERAL_ERROR_CORRECTION_DOC;
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
    private boolean processGenericAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        ObjectCode objectCode = accountingLine.getObjectCode();

        retval = isObjectTypeAndObjectSubTypeAllowed(objectCode);

        if (retval) {
            retval = isRequiredReferenceFieldsValid(accountingLine);
        }

        return retval;
    }

    /**
     * @see IsDebitUtils#isDebitConsideringSectionAndTypePositiveOnly(FinancialDocumentRuleBase, FinancialDocument,
     *      AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        return IsDebitUtils.isDebitConsideringSectionAndTypePositiveOnly(this, transactionalDocument, accountingLine);
    }

    /**
     * The GEC allows one sided documents for correcting - so if one side is empty, the other side must have at least two lines in
     * it. The balancing rules take care of validation of amounts.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(AccountingDocument transactionalDocument) {
        return isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(transactionalDocument);
    }

    /**
     * Overrides to call super and then GEC specific accounting line rules.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        retval = super.processCustomReviewAccountingLineBusinessRules(document, accountingLine);
        if (retval) {
            retval = processGenericAccountingLineBusinessRules(document, accountingLine);
        }

        return retval;
    }

    /**
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
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
    private String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(AccountingDocument transactionalDocument, AccountingLine line) {
        String description = "";
        description = line.getReferenceOriginCode() + "-" + line.getReferenceNumber();

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
     * Used to determine of object code sub types are valid with the object type code.
     * 
     * @param code
     * @return boolean
     */
    protected boolean isObjectTypeAndObjectSubTypeAllowed(ObjectCode code) {
        boolean retval = true;

        if (!getKualiConfigurationService().evaluateConstrainedParameter(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.GENERAL_ERROR_CORRECTION_DOC, COMBINED_RESTRICTED_OBJECT_TYPE_CODES, code.getFinancialObjectTypeCode(), code.getFinancialObjectSubTypeCode())) {
            // add message
            GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_WITH_SUB_TYPE_CODE, new String[] { code.getFinancialObjectCode(), code.getFinancialObjectTypeCode(), code.getFinancialObjectSubTypeCode() });
            retval = false;
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

            if (failsRule( RESTRICTED_OBJECT_TYPE_CODES, objectCode.getFinancialObjectTypeCode())) {
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

        boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(alclass.getName());
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

            if (failsRule( RESTRICTED_OBJECT_SUB_TYPE_CODES, objectCode.getFinancialObjectSubTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
            }
        }

        return valid;
    }
}
