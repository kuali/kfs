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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.CreditMemoEditMode;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;


public class VendorCreditMemoDocumentPresentationController extends PurchasingAccountsPayableDocumentPresentationController {

    @Override
    public boolean canSave(Document document) {
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) document;

        if (StringUtils.equals(vendorCreditMemoDocument.getApplicationDocumentStatus(), PurapConstants.CreditMemoStatuses.APPDOC_INITIATE)) {
            return false;
        }

        if (canEditPreExtraction(vendorCreditMemoDocument)) {
            return true;
        }

        return super.canSave(document);
    }

    @Override
    public boolean canReload(Document document) {
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) document;

        if (StringUtils.equals(vendorCreditMemoDocument.getApplicationDocumentStatus(), PurapConstants.CreditMemoStatuses.APPDOC_INITIATE)) {
            return false;
        }

        if (canEditPreExtraction(vendorCreditMemoDocument)) {
            return true;
        }

        return super.canReload(document);
    }

    @Override
    public boolean canCancel(Document document) {
        //controlling the cancel button through getExtraButtons in CreditMemoForm
        return false;
    }

    @Override
    public boolean canDisapprove(Document document) {
        //disapprove is never allowed for Credit Memo
        return false;
    }

    /**
     *
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((VendorCreditMemoDocument) document)) {
            return false;
        }

        return super.canEdit(document);
    }

    /**
     *
     * @see org.kuali.rice.krad.document.authorization.TransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument)document;
        WorkflowDocument workflowDocument = vendorCreditMemoDocument.getFinancialSystemDocumentHeader().getWorkflowDocument();
        
        if (canCancel(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL);
        }

        if (canHold(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.HOLD);
        }

        if (canRemoveHold(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.REMOVE_HOLD);
        }

        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.FULL_DOCUMENT_ENTRY_COMPLETED);
        }
        else {
            if (ObjectUtils.isNotNull(vendorCreditMemoDocument.getPurchaseOrderDocument()) && 
                    !vendorCreditMemoDocument.isSourceVendor() && 
                    PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(vendorCreditMemoDocument.getPurchaseOrderDocument().getApplicationDocumentStatus())) {
                // TODO hjs-is this right? check to see if the checkbox is showing up for non-AP folks
                editModes.add(CreditMemoEditMode.ALLOW_REOPEN_PURCHASE_ORDER);
            }
        }

        if (StringUtils.equals(vendorCreditMemoDocument.getApplicationDocumentStatus(), PurapConstants.CreditMemoStatuses.APPDOC_INITIATE)) {
            editModes.add(CreditMemoEditMode.DISPLAY_INIT_TAB);
        }

        if (canEditPreExtraction(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.EDIT_PRE_EXTRACT);
        }

        if (!vendorCreditMemoDocument.isSourceDocumentPaymentRequest()) {
            editModes.add(CreditMemoEditMode.LOCK_VENDOR_ENTRY);
        }

        // See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);

            if (vendorCreditMemoDocument.isUseTaxIndicator()) {
                // only allow tax editing if doc is not using use tax
                editModes.add(CreditMemoEditMode.LOCK_TAX_AMOUNT_ENTRY);
            }
            else {
                // display the "clear all taxes" button if doc is not using use tax
                editModes.add(CreditMemoEditMode.CLEAR_ALL_TAXES);
            }
        }

        // Remove editBank edit mode if the document has been extracted
        if (vendorCreditMemoDocument.isExtracted()) {
            editModes.remove(KFSConstants.BANK_ENTRY_EDITABLE_EDITING_MODE);
        }

        return editModes;
    }

    /**
     * Determines if the document can be put on hold. Credit memo not already on hold, extracted date is null, and credit memo
     * status approved or complete.
     *
     * @param cmDocument - credit memo document to hold.
     * @return boolean - true if hold can occur, false if not allowed.
     */
    protected boolean canHold(VendorCreditMemoDocument cmDocument) {
        return !cmDocument.isHoldIndicator() && !cmDocument.isExtracted() && !PurapConstants.CreditMemoStatuses.STATUSES_DISALLOWING_HOLD.contains(cmDocument.getApplicationDocumentStatus());
    }

    /**
     * Determines if the document can be taken off hold.  Credit memo must be on hold.
     *
     * @param cmDocument - credit memo document that is on hold.
     * @return boolean - true if document can be taken off hold, false if it cannot.
     */
    protected boolean canRemoveHold(VendorCreditMemoDocument cmDocument) {
        return cmDocument.isHoldIndicator();
    }

    /**
     * Determines if the document can be canceled. Document can be canceled if not in canceled status already, extracted date is
     * null, and hold indicator is false.
     *
     * @param cmDocument - credit memo document to cancel.
     * @return boolean - true if document can be canceled, false if it cannot be.
     */
    protected boolean canCancel(VendorCreditMemoDocument cmDocument) {
        return !CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getApplicationDocumentStatus()) && !cmDocument.isExtracted() && !cmDocument.isHoldIndicator();
    }

    protected boolean canEditPreExtraction(VendorCreditMemoDocument vendorCreditMemoDocument) {
        return (!vendorCreditMemoDocument.isExtracted() && 
                !SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(vendorCreditMemoDocument.getFinancialSystemDocumentHeader().getWorkflowDocument(), GlobalVariables.getUserSession().getPrincipalId()) &&
                !PurapConstants.CreditMemoStatuses.CANCELLED_STATUSES.contains(vendorCreditMemoDocument.getApplicationDocumentStatus()));
    }

}
