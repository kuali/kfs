/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.arizona.kfs.module.ld.document.web.struts;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.document.web.struts.CorrectionAction;
import org.kuali.kfs.gl.document.web.struts.CorrectionForm;
import org.kuali.kfs.module.ld.document.LaborCorrectionDocument;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.module.ld.document.web.struts.LaborCorrectionForm;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.List;

/**
 * Struts Action Class for the Labor Ledger Correction Process.
 */
public class LaborCorrectionAction extends org.kuali.kfs.module.ld.document.web.struts.LaborCorrectionAction {

    /**
     * Prepare labor correction document for routing
     *
     * @see CorrectionAction#prepareForRoute(CorrectionForm)
     */
    @Override
    protected boolean prepareForRoute(CorrectionForm correctionForm) throws Exception {

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) correctionForm;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        // Is there a description?
        if (StringUtils.isEmpty(document.getDocumentHeader().getDocumentDescription())) {
            GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            return false;
        }

        if (laborCorrectionForm.isPersistedOriginEntriesMissing()) {
            GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
            return false;
        }

        // Did they pick the edit method and system?
        if (!checkMainDropdown(laborCorrectionForm)) {
            return false;
        }

        if (laborCorrectionForm.getDataLoadedFlag() || laborCorrectionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputFileName(correctionForm.getInputGroupId());
        } else {
            document.setCorrectionInputFileName(null);
        }

        if (!checkOriginEntryGroupSelectionBeforeRouting(document)) {
            return false;
        }

        // were the system and edit methods inappropriately changed?
        if (GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_SYSTEM_OR_EDIT_METHOD_CHANGE)) {
            return false;
        }

        // was the input group inappropriately changed?
        if (GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_INPUT_GROUP_CHANGE)) {
            return false;
        }

        if (!validGroupsItemsForDocumentSave(laborCorrectionForm)) {
            return false;
        }

        // If it is criteria, are all the criteria valid?
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(laborCorrectionForm.getEditMethod())) {
            if (!validChangeGroups(laborCorrectionForm)) {
                return false;
            }
        }

        if (!checkRestrictedFunctionalityModeForManualEdit(laborCorrectionForm)) {
            return false;
        }

        if (!checkInputGroupPersistedForDocumentSave(laborCorrectionForm)) {
            return false;
        }

        if (!checkGlobalTransactionValidation(laborCorrectionForm)) {
            return false;
        }

        // Get the output group if necessary
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(laborCorrectionForm.getEditMethod())) {
            if (!laborCorrectionForm.isRestrictedFunctionalityMode() && laborCorrectionForm.getDataLoadedFlag() && !laborCorrectionForm.getShowOutputFlag()) {
                // we're going to force the user to view the output group upon routing, so apply the criteria
                // if the user wasn't in show output mode.
                updateEntriesFromCriteria(laborCorrectionForm, false);
            }
            laborCorrectionForm.setShowOutputFlag(true);
        } else {
            // If it is manual edit, we don't need to save any correction groups
            document.getCorrectionChangeGroup().clear();
        }

        // Populate document
        document.setCorrectionTypeCode(laborCorrectionForm.getEditMethod());
        document.setCorrectionSelection(laborCorrectionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!laborCorrectionForm.getProcessInBatch());
        document.setCorrectionInputFileName(laborCorrectionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null); // this field is never used

        // we'll populate the output group id when the doc has a route level change
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(LaborCorrectionDocumentService.class).persistOriginEntryGroupsForDocumentSave(document, laborCorrectionForm);

        return true;
    }

    protected boolean checkGlobalTransactionValidation(LaborCorrectionForm laborCorrectionForm) {
        boolean result = true;
        int lineNum = 1;
        List<OriginEntryFull> allEntries = laborCorrectionForm.getAllEntries();
        GlobalTransactionEditService globalTransactionEditService = SpringContext.getBean(GlobalTransactionEditService.class);

        for (OriginEntryFull oe : allEntries) {
            oe.refreshReferenceObject("account");
            oe.refreshReferenceObject("financialObject");
            Account account = oe.getAccount();
            account.refreshReferenceObject("subFundGroup");

            if (ObjectUtils.isNull(account)) {
                throw new IllegalArgumentException("This account specified " + oe.getChartOfAccountsCode() + "-" + oe.getAccountNumber() + " does not exist. For sequence " + oe.getTransactionLedgerEntrySequenceNumber());
            }
            if (ObjectUtils.isNull(oe.getFinancialObject())) {
                throw new IllegalArgumentException("This account specified " + oe.getFinancialObjectCode() + "-" + oe.getAccountNumber() + " does not exist. For sequence " + oe.getTransactionLedgerEntrySequenceNumber());
            }
            if (ObjectUtils.isNull(account.getSubFundGroup())) {
                throw new IllegalArgumentException("This account specified " + oe.getChartOfAccountsCode() + "-" + oe.getAccountNumber() + " does not exist. For sequence " + oe.getTransactionLedgerEntrySequenceNumber());
            }
            Message message = globalTransactionEditService.isAccountingLineAllowable(oe.getFinancialSystemOriginationCode(),
                    account.getSubFundGroup().getFundGroupCode(),
                    account.getSubFundGroupCode(),
                    oe.getFinancialDocumentTypeCode(),
                    oe.getFinancialObject().getFinancialObjectTypeCode(),
                    oe.getFinancialObject().getFinancialObjectSubTypeCode());
            if (message != null) {
                GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[]{message.getMessage(), "on line " + lineNum});
                result = false;
            }
            lineNum++;
        }
        return result;
    }

}

