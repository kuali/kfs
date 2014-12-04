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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Accounting line authorizer for Requisition document which allows adding accounting lines at specified nodes
 */
public class PurchaseOrderAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    private static final String NEW_UNORDERED_ITEMS_NODE = "NewUnorderedItems";

    /**
     * Allow new lines to be rendered at NewUnorderedItems node
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        WorkflowDocument workflowDocument = ((PurchasingAccountsPayableDocument) accountingDocument).getFinancialSystemDocumentHeader().getWorkflowDocument();

        Set <String> currentRouteNodeName = workflowDocument.getCurrentNodeNames();

        //  if its in the NEW_UNORDERED_ITEMS node, then allow the new line to be drawn
        if (CollectionUtils.isNotEmpty(currentRouteNodeName) && PurchaseOrderAccountingLineAuthorizer.NEW_UNORDERED_ITEMS_NODE.equals(currentRouteNodeName.toString())) {
            return true;
        }

        if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT.equals(workflowDocument.getDocumentTypeName()) && StringUtils.isNotBlank(accountingGroupProperty) && accountingGroupProperty.contains(PurapPropertyConstants.ITEM)) {
            //KFSMI-8961: The accounting line should be addable in the new items as well as
            //existing items in POA..
            //    int itemNumber = determineItemNumberFromGroupProperty(accountingGroupProperty);
            //    PurchaseOrderAmendmentDocument poaDoc = (PurchaseOrderAmendmentDocument) accountingDocument;
            //    List <PurchaseOrderItem> items = poaDoc.getItems();
            //     PurchaseOrderItem item = items.get(itemNumber);
            //    return item.isNewItemForAmendment() || item.getSourceAccountingLines().size() == 0;
            return true;
        }

        return super.renderNewLine(accountingDocument, accountingGroupProperty);
    }

    private int determineItemNumberFromGroupProperty(String accountingGroupProperty) {
        int openBracketPos = accountingGroupProperty.indexOf("[");
        int closeBracketPos = accountingGroupProperty.indexOf("]");
        String itemNumberString = accountingGroupProperty.substring(openBracketPos + 1, closeBracketPos);
        int itemNumber = new Integer(itemNumberString).intValue();
        return itemNumber;
    }

    @Override
    protected boolean allowAccountingLinesAreEditable(AccountingDocument accountingDocument,
            AccountingLine accountingLine){
        PurApAccountingLine purapAccount = (PurApAccountingLine)accountingLine;
        PurchaseOrderItem poItem = (PurchaseOrderItem)purapAccount.getPurapItem();
        PurchaseOrderDocument po = (PurchaseOrderDocument)accountingDocument;


        if (poItem != null && !poItem.getItemType().isAdditionalChargeIndicator()) {
            if (!poItem.isItemActiveIndicator()) {
                return false;
            }

            // if total amount has a value and is non-zero
            if (poItem.getItemInvoicedTotalAmount() != null && poItem.getItemInvoicedTotalAmount().compareTo(new KualiDecimal(0)) != 0) {
                return false;
            }

            if (po.getContainsUnpaidPaymentRequestsOrCreditMemos() && !poItem.isNewItemForAmendment()) {
                return false;
            }

        }
        return super.allowAccountingLinesAreEditable(accountingDocument, accountingLine);
    }

    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
     // the fields in a new line should be always editable
        if (accountingLine.getSequenceNumber() == null) {
            return true;
        }

        // check the initiation permission on the document if it is in the state of preroute, but only if
        // the PO status is not In Process.
        WorkflowDocument workflowDocument = ((PurchasingAccountsPayableDocument) accountingDocument).getFinancialSystemDocumentHeader().getWorkflowDocument();

        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)accountingDocument;
        if (!poDocument.getApplicationDocumentStatus().equals(PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS) && (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested())) {
            if (PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT.equals(workflowDocument.getDocumentTypeName())) {
                PurApAccountingLine purapAccount = (PurApAccountingLine)accountingLine;
                purapAccount.refreshReferenceObject("purapItem");

                PurchaseOrderItem item = (PurchaseOrderItem)purapAccount.getPurapItem();
                return item.isNewItemForAmendment() || item.getSourceAccountingLines().size() == 0;
            }
            else {
                return currentUserIsDocumentInitiator;
            }
        }
        else {
            return true;
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set<String> unviewableBlocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);
        unviewableBlocks.remove(KFSPropertyConstants.PERCENT);
        unviewableBlocks.remove(KFSPropertyConstants.AMOUNT);

        return unviewableBlocks;
    }
}
