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

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.financial.rule.AddCheckRule;
import org.kuali.module.financial.rule.DeleteCheckRule;
import org.kuali.module.financial.rule.UpdateCheckRule;


/**
 * Business rule(s) applicable to CashReceipt documents.
 */
public class CashReceiptDocumentRule extends CashReceiptFamilyRule implements AddCheckRule, DeleteCheckRule, UpdateCheckRule {
    /**
     * Implements Cash Receipt specific rule checks for the cash reconciliation section, to make sure that the cash, check, and coin
     * totals are not negative.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        if (isValid) {
            isValid &= validateAccountingLineTotal((CashReceiptFamilyBase) document);
            isValid &= !CashReceiptDocumentRuleUtil.areCashTotalsInvalid((CashReceiptDocument) document);
        }

        return isValid;
    }

    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @see org.kuali.core.rule.AddCheckRule#processAddCheckBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processAddCheckBusinessRules(AccountingDocument FinancialDocument, Check check) {
        boolean isValid = validateCheck(check);

        return isValid;
    }

    /**
     * Default implementation does nothing now.
     * 
     * @see org.kuali.core.rule.DeleteCheckRule#processDeleteCheckBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processDeleteCheckBusinessRules(AccountingDocument FinancialDocument, Check check) {
        boolean processed = true;

        return processed;
    }

    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @see org.kuali.core.rule.UpdateCheckRule#processUpdateCheckRule(org.kuali.core.document.FinancialDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processUpdateCheckRule(AccountingDocument FinancialDocument, Check check) {
        boolean isValid = validateCheck(check);

        return isValid;
    }

    /**
     * This method validates checks for a CR document.
     * 
     * @param check
     * @return boolean
     */
    private boolean validateCheck(Check check) {
        // validate the specific check coming in
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(check);

        boolean isValid = GlobalVariables.getErrorMap().isEmpty();

        // check to make sure the amount is also valid
        if (check.getAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.CHECK_AMOUNT, KeyConstants.CashReceipt.ERROR_ZERO_CHECK_AMOUNT, PropertyConstants.CHECKS);
            isValid = false;
        }
        else if (check.getAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.CHECK_AMOUNT, KeyConstants.CashReceipt.ERROR_NEGATIVE_CHECK_AMOUNT, PropertyConstants.CHECKS);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Method used by <code>{@link org.kuali.module.financial.service.CashReceiptCoverSheetService}</code> to determine of the
     * <code>{@link CashReceiptDocument}</code> validates business rules for generating a cover page. <br/> <br/> Rule is the
     * <code>{@link Document}</code> must be ENROUTE.
     * 
     * @param document
     * @return boolean
     */
    public boolean isCoverSheetPrintable(CashReceiptFamilyBase document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());
    }
}
