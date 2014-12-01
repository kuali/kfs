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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;

public enum SystemParameterBelowTheLineItemPaymentRequestFixture {
    PREQ_VALID_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_VALID_FREIGHT_ITEM, 
                                             PaymentRequestItemFixture.PREQ_VALID_SHIPPING_AND_HANDLING_ITEM,
                                             PaymentRequestItemFixture.PREQ_VALID_MIN_ORDER_ITEM,
                                             PaymentRequestItemFixture.PREQ_VALID_MISC_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_DISC_ITEM} // requisitionItemMultiFixtures
    ),
    PREQ_INVALID_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_RSTO_ITEM} // requisitionItemMultiFixtures
    ), 
    PREQ_WITH_VALID_NEGATIVE_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_MISC_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_DISC_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_TRDI_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_ORDS_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_FDTX_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_STTX_ITEM} // requisitionItemMultiFixtures
    ),
    PREQ_WITH_INVALID_NEGATIVE_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_FED_GROSS_CODE_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_FREIGHT_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_MIN_ORDER_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_SHIPPING_AND_HANDLING_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_NEGATIVE_STATE_GROSS_CODE_ITEM} // requisitionItemMultiFixtures
    ),    
    PREQ_WITH_VALID_POSITIVE_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_VALID_FED_GROSS_CODE_ITEM,
                                             PaymentRequestItemFixture.PREQ_VALID_FREIGHT_ITEM,
                                             PaymentRequestItemFixture.PREQ_VALID_MIN_ORDER_ITEM,
                                             PaymentRequestItemFixture.PREQ_VALID_SHIPPING_AND_HANDLING_ITEM,
                                             PaymentRequestItemFixture.PREQ_VALID_STATE_GROSS_CODE_ITEM} // requisitionItemMultiFixtures
    ),       
    PREQ_WITH_INVALID_POSITIVE_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] { PaymentRequestItemFixture.PREQ_WITH_POSITIVE_DISC_ITEM,
                                              PaymentRequestItemFixture.PREQ_WITH_POSITIVE_RSTO_ITEM,
                                              PaymentRequestItemFixture.PREQ_WITH_POSITIVE_MSCR_ITEM,
                                              PaymentRequestItemFixture.PREQ_WITH_POSITIVE_FDTX_ITEM,
                                              PaymentRequestItemFixture.PREQ_WITH_POSITIVE_STTX_ITEM} // requisitionItemMultiFixtures
    ),        
    PREQ_WITH_VALID_ZERO_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_WITH_ZERO_MISC_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_ZERO_MIN_ORDER_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_ZERO_DISC_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_ZERO_SHIPPING_AND_HANDLING_ITEM,
                                             PaymentRequestItemFixture.PREQ_WITH_ZERO_FREIGHT_ITEM} // requisitionItemMultiFixtures
    ),  
    PREQ_WITH_INVALID_ZERO_BELOW_LINE_ITEMS(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_WITH_ZERO_RSTO_ITEM} // requisitionItemMultiFixtures
    ),
    PREQ_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_WITH_FREIGHT_ITEM_NO_DESC} // requisitionItemMultiFixtures
    ),
    PREQ_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION(
            new PaymentRequestItemFixture[] {PaymentRequestItemFixture.PREQ_VALID_MISC_ITEM} // requisitionItemMultiFixtures
    ),    
    ;
    private PaymentRequestItemFixture[] paymentRequestItemFixtures;
    
    
    private SystemParameterBelowTheLineItemPaymentRequestFixture(
            PaymentRequestItemFixture[] paymentRequestItemFixtures) {
        this.paymentRequestItemFixtures = paymentRequestItemFixtures;
    }
    
    public PaymentRequestDocument createPaymentRequestDocument() {
        PaymentRequestDocument doc = PaymentRequestDocumentFixture.PREQ_ONLY_REQUIRED_FIELDS.createPaymentRequestDocument();
        //Removes all the existing item from doc, we'll add the appropriate items later.
        doc.getItems().clear();         
        for (PaymentRequestItemFixture paymentRequestItemFixture : paymentRequestItemFixtures) { 
            paymentRequestItemFixture.addTo(doc);
        }
        
        return doc;
    }
}
