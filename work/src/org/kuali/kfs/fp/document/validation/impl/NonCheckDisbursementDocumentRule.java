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

import static org.kuali.kfs.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;

/**
 * Business rule(s) applicable to NonCheckDisbursement documents.
 */
public class NonCheckDisbursementDocumentRule extends AccountingDocumentRuleBase {

    /**
     * Overrides to consider the object types.<br/>
     * <p>
     * Note: This <code>{@link org.kuali.core.document.Document}</code> is always balanced because it only has From: lines.
     *
     * @param financialDocument The document whose balance is being validated.
     * @return Always returns true, because this type of document is always balanced.
     *
     * @see FinancialDocumentRuleBase#isDocumentBalanceValid(FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument financialDocument) {
        return true;
    }

    /**
     * This method performs business rule checks on the accounting line being added to the document to ensure the accounting line
     * is valid and appropriate for the document.  Currently, this method calls isRequiredReferenceFieldsValid() 
     * associated with the new accounting line.  
     * 
     * @param financialDocument The document the new line is being added to.
     * @param accountingLine The new accounting line being added.
     * @return True if the business rules all pass, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;
        retval = super.processCustomAddAccountingLineBusinessRules(document, accountingLine);
        if (retval) {
            retval = isRequiredReferenceFieldsValid(accountingLine);
        }
        return retval;
    }

    /**
     * Overrides the parent to display correct error message for a single sided document.
     * 
     * @param financialDocument The document to be routed.
     * @return True if the document contains source accounting lines, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        if (0 == financialDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides the parent to return true, because NonCheckDisbursement documents only use the SourceAccountingLines data
     * structures. The list that holds TargetAccountingLines should be empty. This will be checked when the document is 
     * "routed" or submitted to post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @param financialDocument The document to be routed.
     * @return This method always returns true.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        return true;
    }

    /**
     * This method checks that values exist in the reference fields that are required.  The reference field that 
     * is required is the 'reference number' field.
     * 
     * @param accountingLine The accounting line being validated.
     * @return True if all of the required reference fields are valid, false otherwise.
     */
    private boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        BusinessObjectEntry boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(SourceAccountingLine.class.getName());
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            putRequiredPropertyError(boe, REFERENCE_NUMBER);
            valid = false;
        }
        return valid;
    }
}