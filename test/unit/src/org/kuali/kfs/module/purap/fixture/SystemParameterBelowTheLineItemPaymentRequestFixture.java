/*
 * Copyright 2007 The Kuali Foundation
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
