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
