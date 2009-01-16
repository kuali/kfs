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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
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
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) document;
        if (StringUtils.equals(vendorCreditMemoDocument.getStatusCode(), CreditMemoStatuses.INITIATE)) {
            return false;
        }
        return super.canCancel(document);
    }

    @Override
    protected boolean canClose(Document document) {
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) document;
        if (StringUtils.equals(vendorCreditMemoDocument.getStatusCode(), CreditMemoStatuses.INITIATE)) {
            return false;
        }
        return super.canClose(document);
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
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument)document;
        Set<String> editModes = new HashSet<String>();

        if (StringUtils.equals(vendorCreditMemoDocument.getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE)) {
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB);
        }
        
        if (!vendorCreditMemoDocument.isExtracted() && 
                !workflowDocument.isAdHocRequested() &&
                !PurapConstants.CreditMemoStatuses.CANCELLED_STATUSES.contains(vendorCreditMemoDocument.getStatusCode())) {
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.EDIT_PRE_EXTRACT);
        }

        if (!vendorCreditMemoDocument.isSourceDocumentPaymentRequest()) {
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.LOCK_VENDOR_ENTRY);
        }
        
        // only allow tax editing and display the "clear all taxes" button if doc is not using use tax
        if (vendorCreditMemoDocument.isUseTaxIndicator()) {
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.CLEAR_ALL_TAXES);
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.LOCK_TAX_AMOUNT_ENTRY);
        }

        // See if purap tax is enabled
        boolean salesTaxInd = SpringContext.getBean(KualiConfigurationService.class).getIndicatorParameter(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurapAuthorizationConstants.PURAP_TAX_ENABLED);
        }

        //TODO hjs-is this right?  check to see if the checkbox is showing up for non-AP folks
        if (!vendorCreditMemoDocument.isSourceVendor() &&
                !SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(vendorCreditMemoDocument) && 
                PurapConstants.PurchaseOrderStatuses.CLOSED.equals(vendorCreditMemoDocument.getPurchaseOrderDocument().getStatusCode())) {
            editModes.add(PurapAuthorizationConstants.CreditMemoEditMode.ALLOW_REOPEN_PURCHASE_ORDER);
        }
        
        return editModes;
    }

}
