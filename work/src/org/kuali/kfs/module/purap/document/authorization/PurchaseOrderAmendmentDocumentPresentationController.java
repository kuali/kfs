/*
 * Copyright 2008-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.authorization;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PurchaseOrderEditMode;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderTabIdentifierService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

public class PurchaseOrderAmendmentDocumentPresentationController extends PurchaseOrderDocumentPresentationController {

    @Override
    public boolean canEdit(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        // po amend docs in CGIP status are only editable when in Initiated or Saved status
        if (PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS.equals(poDocument.getApplicationDocumentStatus())) {
            WorkflowDocument workflowDocument = poDocument.getFinancialSystemDocumentHeader().getWorkflowDocument();

            if (!workflowDocument.isInitiated() && !workflowDocument.isSaved()  && !workflowDocument.isCompletionRequested()) {
                return false;
            }
        }
        return super.canEdit(document);
    }

    protected boolean canDisapprovePOA(Document document) {
        String statusCode = ((PurchaseOrderAmendmentDocument)document).getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();

        boolean isEnroute = ((PurchaseOrderAmendmentDocument)document).getDocumentHeader().getWorkflowDocument().isEnroute();
        //If the status is not already disapproved, then check whether this document can be disapproved.
        if (!PurchaseOrderStatuses.APPDOC_DISAPPROVED_CHANGE.equals(statusCode) && isEnroute) {
            boolean isApprovalRequested = ((PurchaseOrderAmendmentDocument)document).getDocumentHeader().getWorkflowDocument().isApprovalRequested();
            return isApprovalRequested && getDocumentActions(document).contains(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
        }
        else {
            return false;
        }

    }

    @Override
    public Set<String> getEditModes(Document document) {


        Set<String> editModes = super.getEditModes(document);
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS.equals(poDocument.getApplicationDocumentStatus())) {
            WorkflowDocument workflowDocument = poDocument.getFinancialSystemDocumentHeader().getWorkflowDocument();
            //  amendment doc needs to lock its field for initiator while enroute
            if (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested()) {
                editModes.add(PurchaseOrderEditMode.AMENDMENT_ENTRY);
            }
        }
        if (PurchaseOrderStatuses.APPDOC_AWAIT_NEW_UNORDERED_ITEM_REVIEW.equals(poDocument.getApplicationDocumentStatus())) {
            editModes.add(PurchaseOrderEditMode.AMENDMENT_ENTRY);
        }

        if (SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument) document, "New Unordered Items")) {
            editModes.add(PurchaseOrderEditMode.UNORDERED_ITEM_ACCOUNT_ENTRY);
        }

        boolean showDisableRemoveAccounts = true;
        PurchaseOrderAmendmentDocument purchaseOrderAmendmentDocument = (PurchaseOrderAmendmentDocument)document;
        List<PurApItem> aboveTheLinePOItems = PurApItemUtils.getAboveTheLineOnly(purchaseOrderAmendmentDocument.getItems());
        PurchaseOrderDocument po = (PurchaseOrderDocument)document;
        boolean containsUnpaidPaymentRequestsOrCreditMemos = po.getContainsUnpaidPaymentRequestsOrCreditMemos();
        ItemLoop:
        for (PurApItem poItem : aboveTheLinePOItems) {
            boolean acctLinesEditable = allowAccountingLinesAreEditable((PurchaseOrderItem) poItem, containsUnpaidPaymentRequestsOrCreditMemos);
            for(PurApAccountingLine poAccoutingLine : poItem.getSourceAccountingLines()){
                if(!acctLinesEditable){
                    showDisableRemoveAccounts = false;
                    break ItemLoop;
                }
            }
        }

        if(!showDisableRemoveAccounts){
            editModes.add(PurchaseOrderEditMode.DISABLE_REMOVE_ACCTS);
        }

        List<String> result = getPurchaseOrderTabIdentifierService().getModifiedTabs(purchaseOrderAmendmentDocument);

        if(result != null && result.size() > 0){

            if(result.contains(PurapConstants.EditedTabs.PURAP_ADDITIONAL_TAB)) {
                editModes.add(PurchaseOrderEditMode.ADDITIONAL_TAB_EDITED);
            }

            if(result.contains(PurapConstants.EditedTabs.PURAP_VENDOR_TAB)) {
                editModes.add(PurchaseOrderEditMode.VENDOR_TAB_EDITED);
            }

            if(result.contains(PurapConstants.EditedTabs.PURAP_STIPULATION_TAB)) {
                editModes.add(PurchaseOrderEditMode.STIPULATION_TAB_EDITED);
            }

            if(result.contains(PurapConstants.EditedTabs.PURAP_DELIVERY_TAB)) {
                editModes.add(PurchaseOrderEditMode.DELIVERY_TAB_EDITED);
            }

            if(result.contains(PurapConstants.EditedTabs.PURAP_PAYMENT_TAB)) {
                editModes.add(PurchaseOrderEditMode.PAYMENT_TAB_EDITED);
            }

            if(result.contains(PurapConstants.EditedTabs.PURAP_QUOTES_TAB)) {
                editModes.add(PurchaseOrderEditMode.QUOTE_TAB_EDITED);
            }

            if(result.contains(PurapConstants.EditedTabs.PURAP_CAMS_TAB)) {
                editModes.add(PurchaseOrderEditMode.CAMS_TAB_EDITED);
            }

            if(result.contains(PurapConstants.EditedTabs.PURAP_ITEMS_TAB)) {
                editModes.add(PurchaseOrderEditMode.ITEMS_TAB_EDITED);
                if (canDisapprovePOA(document)) {
                    editModes.add(PurchaseOrderEditMode.DISAPPROVE_POA);
                }
            }
        }



        return editModes;
    }



    protected boolean allowAccountingLinesAreEditable(PurchaseOrderItem poItem, boolean containsUnpaidPaymentRequestsOrCreditMemos) {

        if (poItem != null && !poItem.getItemType().isAdditionalChargeIndicator()) {
            if (!poItem.isItemActiveIndicator()) {
                return false;
            }

            // if total amount has a value and is non-zero
            if (poItem.getItemInvoicedTotalAmount() != null && poItem.getItemInvoicedTotalAmount().compareTo(new KualiDecimal(0)) != 0) {
                return false;
            }

            if (containsUnpaidPaymentRequestsOrCreditMemos && !poItem.isNewItemForAmendment()) {
                return false;
            }

        }
        return true;
    }

    @Override
    public boolean canReload(Document document) {
        //  show the reload button if the doc is anything but processed or final
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        WorkflowDocument workflowDocument = poDocument.getFinancialSystemDocumentHeader().getWorkflowDocument();
        return (workflowDocument.isSaved() || workflowDocument.isEnroute()) ;
    }

    private PurchaseOrderTabIdentifierService getPurchaseOrderTabIdentifierService(){
        return SpringContext.getBean(PurchaseOrderTabIdentifierService.class);
    }
}
