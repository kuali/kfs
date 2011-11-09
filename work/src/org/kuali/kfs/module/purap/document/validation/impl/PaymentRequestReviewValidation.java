/*
 * Copyright 2009 The Kuali Foundation
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

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PaymentRequestReviewValidation extends GenericValidation {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestReviewValidation.class);

    private PaymentRequestItem itemForValidation;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument paymentRequest = (PaymentRequestDocument)event.getDocument();        
        

        boolean containsAccounts = false;
        int accountLineNbr = 0;

        String identifier = itemForValidation.getItemIdentifierString();
        BigDecimal total = BigDecimal.ZERO;
        if (LOG.isDebugEnabled()) {
            LOG.debug("validatePaymentRequestReview() The " + identifier + " is getting the total percent field set to " + BigDecimal.ZERO);
        }

        if ((itemForValidation.getTotalAmount() != null && itemForValidation.getTotalAmount().isNonZero() && itemForValidation.getItemType().isLineItemIndicator() && ((itemForValidation.getItemType().isAmountBasedGeneralLedgerIndicator() && (itemForValidation.getPoOutstandingAmount() == null || itemForValidation.getPoOutstandingAmount().isZero())) || (itemForValidation.getItemType().isQuantityBasedGeneralLedgerIndicator() && (itemForValidation.getPoOutstandingQuantity() == null || itemForValidation.getPoOutstandingQuantity().isZero()))))) {
            // ERROR because we have total amount and no open encumberance on the PO item
            // this error should have been caught at an earlier level
            if (itemForValidation.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has total amount '" + itemForValidation.getTotalAmount() + "' but outstanding encumbered amount " + itemForValidation.getPoOutstandingAmount();
                LOG.error("validatePaymentRequestReview() " + error);
            }
            else {
                String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has quantity '" + itemForValidation.getItemQuantity() + "' but outstanding encumbered quantity " + itemForValidation.getPoOutstandingQuantity();
                LOG.error("validatePaymentRequestReview() " + error);
            }
        }
        else {
            // not validating but ok
            String error = "Payment Request " + paymentRequest.getPurapDocumentIdentifier() + ", " + identifier + " has total amount '" + itemForValidation.getTotalAmount() + "'";
            if (itemForValidation.getItemType().isLineItemIndicator()) {
                if (itemForValidation.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                    error = error + " with outstanding encumbered amount " + itemForValidation.getPoOutstandingAmount();
                }
                else {
                    error = error + " with outstanding encumbered quantity " + itemForValidation.getPoOutstandingQuantity();
                }
            }
            LOG.info("validatePaymentRequestReview() " + error);
        }

        return valid;
    }


    public PaymentRequestItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PaymentRequestItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }


}
