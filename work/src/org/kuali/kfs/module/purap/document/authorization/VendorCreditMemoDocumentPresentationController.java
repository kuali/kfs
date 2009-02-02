/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.CreditMemoEditMode;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class VendorCreditMemoDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

//FIXME hjs KIM cleanup    
//                //Set can edit bank to true if the document has not been extracted, for now without Kim (more changes when Kim is available).
//                if (!vendorCreditMemoDocument.isExtracted()) {
//                    flags.setCanEditBank(true);
//                }

    @Override
    protected boolean canSave(Document document) {
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) document;
        if (!StringUtils.equals(vendorCreditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.IN_PROCESS)) {
            return false;
        }
        return super.canSave(document);
    }

    @Override
    protected boolean canCancel(Document document) {
        //controlling the cancel button through getExtraButtons in PaymentRequestForm
        return false;
    }

    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) document;

        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((VendorCreditMemoDocument) document)) {
            return false;
        }

        if (workflowDocument.isAdHocRequested()) {
            return false;
        }

        return super.canEdit(document);
    }

    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument)document;
        boolean isFullDocumentEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(vendorCreditMemoDocument);

        if (canCancel(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL);
        }
        
        if (canHold(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.HOLD);
        }

        if (canRemoveHold(vendorCreditMemoDocument)) {
            editModes.add(CreditMemoEditMode.REMOVE_HOLD);
        }

        if (isFullDocumentEntryCompleted) {
            editModes.add(CreditMemoEditMode.FULL_DOCUMENT_ENTRY_COMPLETED);
        }

        if (StringUtils.equals(vendorCreditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE)) {
            editModes.add(CreditMemoEditMode.DISPLAY_INIT_TAB);
        }
        
        if (!vendorCreditMemoDocument.isExtracted() && 
                !workflowDocument.isAdHocRequested() &&
                !PurapConstants.CreditMemoStatuses.CANCELLED_STATUSES.contains(vendorCreditMemoDocument.getStatusCode())) {
            editModes.add(CreditMemoEditMode.EDIT_PRE_EXTRACT);
        }

        if (!vendorCreditMemoDocument.isSourceDocumentPaymentRequest()) {
            editModes.add(CreditMemoEditMode.LOCK_VENDOR_ENTRY);
        }
        
        // only allow tax editing and display the "clear all taxes" button if doc is not using use tax
        if (vendorCreditMemoDocument.isUseTaxIndicator()) {
            editModes.add(CreditMemoEditMode.CLEAR_ALL_TAXES);
            editModes.add(CreditMemoEditMode.LOCK_TAX_AMOUNT_ENTRY);
        }

        // See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);
        }

        //TODO hjs-is this right?  check to see if the checkbox is showing up for non-AP folks
        if (!vendorCreditMemoDocument.isSourceVendor() &&
                !isFullDocumentEntryCompleted && 
                PurapConstants.PurchaseOrderStatuses.CLOSED.equals(vendorCreditMemoDocument.getPurchaseOrderDocument().getStatusCode())) {
            editModes.add(CreditMemoEditMode.ALLOW_REOPEN_PURCHASE_ORDER);
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
    private boolean canHold(VendorCreditMemoDocument cmDocument) {
        return !cmDocument.isHoldIndicator() && !cmDocument.isExtracted() && !PurapConstants.CreditMemoStatuses.STATUSES_DISALLOWING_HOLD.contains(cmDocument.getStatusCode());
    }

    /**
     * Determines if the document can be taken off hold.  Credit memo must be on hold.
     * 
     * @param cmDocument - credit memo document that is on hold.
     * @return boolean - true if document can be taken off hold, false if it cannot.
     */
    private boolean canRemoveHold(VendorCreditMemoDocument cmDocument) {
        return cmDocument.isHoldIndicator();
    }

    /**
     * Determines if the document can be canceled. Document can be canceled if not in canceled status already, extracted date is
     * null, and hold indicator is false.
     * 
     * @param cmDocument - credit memo document to cancel.
     * @return boolean - true if document can be canceled, false if it cannot be.
     */
    private boolean canCancel(VendorCreditMemoDocument cmDocument) {
        return !CreditMemoStatuses.CANCELLED_STATUSES.contains(cmDocument.getStatusCode()) && !cmDocument.isExtracted() && !cmDocument.isHoldIndicator();
    }

}
