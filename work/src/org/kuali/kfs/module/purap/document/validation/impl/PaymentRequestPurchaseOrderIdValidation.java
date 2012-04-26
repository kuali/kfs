/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestPurchaseOrderIdValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        Integer POID = document.getPurchaseOrderIdentifier();

        PurchaseOrderDocument purchaseOrderDocument = document.getPurchaseOrderDocument();
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
        }
        else if (purchaseOrderDocument.isPendingActionIndicator()) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_PENDING_ACTION);
            valid &= false;
        }
        else if (!StringUtils.equals(purchaseOrderDocument.getApplicationDocumentStatus(), PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN)) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
            valid &= false;
            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it
        }
        else {
            // Verify that there exists at least 1 item left to be invoiced
            //valid &= encumberedItemExistsForInvoicing(purchaseOrderDocument);
        }
        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

    /**
     * Determines if there are items with encumbrances to be invoiced on passed in
     * purchase order document.
     * 
     * @param document - purchase order document
     * @return
     */
    protected boolean encumberedItemExistsForInvoicing(PurchaseOrderDocument document) {
        boolean zeroDollar = true;
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) document.getItems()) {
            // Quantity-based items
            if (poi.getItemType().isLineItemIndicator() && poi.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                KualiDecimal encumberedQuantity = poi.getItemOutstandingEncumberedQuantity() == null ? KualiDecimal.ZERO : poi.getItemOutstandingEncumberedQuantity();
                if (encumberedQuantity.compareTo(KualiDecimal.ZERO) == 1) {
                    zeroDollar = false;
                    break;
                }
            }
            // Service Items or Below-the-line Items
            else if (poi.getItemType().isAmountBasedGeneralLedgerIndicator() || poi.getItemType().isAdditionalChargeIndicator()) {
                KualiDecimal encumberedAmount = poi.getItemOutstandingEncumberedAmount() == null ? KualiDecimal.ZERO : poi.getItemOutstandingEncumberedAmount();
                if (encumberedAmount.compareTo(KualiDecimal.ZERO) == 1) {
                    zeroDollar = false;
                    break;
                }
            }
        }
        if (zeroDollar) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_NO_ITEMS_TO_INVOICE);
        }
        GlobalVariables.getMessageMap().clearErrorPath();
        return !zeroDollar;
    }

}
