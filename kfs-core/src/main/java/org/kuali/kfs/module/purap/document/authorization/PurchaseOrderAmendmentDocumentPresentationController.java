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

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PurchaseOrderEditMode;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

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
}
