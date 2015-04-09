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

import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;

public enum PaymentRequestItemFixture {

    PREQ_QTY_UNRESTRICTED_ITEM_1(
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_QTY_UNRESTRICTED_ITEM_2(
            PurApItemFixture.BASIC_QTY_ITEM_2, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_QTY_APO_ITEM_1(
            PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.PREQ_APO_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    PREQ_ITEM_FOR_PO_CLOSE_DOC(
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.PREQ_ACCOUNT_FOR_PO_CLOSE_DOC } // paymentRequestAccountMultiFixtures
    ),
    PREQ_VALID_FREIGHT_ITEM(
            PurApItemFixture.VALID_FREIGHT_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_VALID_SHIPPING_AND_HANDLING_ITEM(
            PurApItemFixture.VALID_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_VALID_MIN_ORDER_ITEM(
            PurApItemFixture.VALID_MIN_ORDER_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_VALID_MISC_ITEM(
            PurApItemFixture.VALID_MISC_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_VALID_FED_GROSS_CODE_ITEM(
            PurApItemFixture.VALID_FED_GROSS_CODE_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_VALID_STATE_GROSS_CODE_ITEM(
            PurApItemFixture.VALID_STATE_GROSS_CODE_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),    
    PREQ_WITH_NEGATIVE_FREIGHT_ITEM(
            PurApItemFixture.NEGATIVE_FREIGHT_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_SHIPPING_AND_HANDLING_ITEM(
            PurApItemFixture.NEGATIVE_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_MIN_ORDER_ITEM(
            PurApItemFixture.NEGATIVE_MIN_ORDER_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_FED_GROSS_CODE_ITEM(
            PurApItemFixture.NEGATIVE_FED_GROSS_CODE_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_STATE_GROSS_CODE_ITEM(
            PurApItemFixture.NEGATIVE_STATE_GROSS_CODE_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_MISC_ITEM(
            PurApItemFixture.NEGATIVE_MISC_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_DISC_ITEM(
            PurApItemFixture.NEGATIVE_DISC_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_RSTO_ITEM(
            PurApItemFixture.NEGATIVE_RSTO_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_MSCR_ITEM(
            PurApItemFixture.NEGATIVE_MSCR_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_TRDI_ITEM(
            PurApItemFixture.NEGATIVE_TRDI_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_ORDS_ITEM(
            PurApItemFixture.NEGATIVE_ORDS_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_FDTX_ITEM(
            PurApItemFixture.NEGATIVE_FDTX_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_NEGATIVE_STTX_ITEM(
            PurApItemFixture.NEGATIVE_STTX_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_POSITIVE_DISC_ITEM(
            PurApItemFixture.POSITIVE_DISC_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_POSITIVE_RSTO_ITEM(
            PurApItemFixture.POSITIVE_RSTO_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_POSITIVE_MSCR_ITEM(
            PurApItemFixture.POSITIVE_MSCR_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_POSITIVE_TRDI_ITEM(
            PurApItemFixture.POSITIVE_TRDI_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_POSITIVE_ORDS_ITEM(
            PurApItemFixture.POSITIVE_ORDS_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_POSITIVE_FDTX_ITEM(
            PurApItemFixture.POSITIVE_FDTX_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_POSITIVE_STTX_ITEM(
            PurApItemFixture.POSITIVE_STTX_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_ZERO_MIN_ORDER_ITEM(
            PurApItemFixture.ZERO_MIN_ORDER_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_ZERO_MISC_ITEM(
            PurApItemFixture.ZERO_MISC_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_ZERO_DISC_ITEM(
            PurApItemFixture.ZERO_DISC_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_ZERO_FREIGHT_ITEM(
            PurApItemFixture.ZERO_FREIGHT_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_ZERO_SHIPPING_AND_HANDLING_ITEM(
            PurApItemFixture.ZERO_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_ZERO_RSTO_ITEM(
            PurApItemFixture.ZERO_RSTO_ITEM, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_FREIGHT_ITEM_NO_DESC(
            PurApItemFixture.FREIGHT_ITEM_NO_DESC, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),
    PREQ_WITH_MISC_ITEM_NO_DESC(
            PurApItemFixture.MISC_ITEM_NO_DESC, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    ),;
    
    private PurApItemFixture purApItemFixture;
    private PaymentRequestAccountingLineFixture[] paymentRequestAccountingLineFixtures;
    
    private PaymentRequestItemFixture(PurApItemFixture purApItemFixture, PaymentRequestAccountingLineFixture[] paymentRequestAccountingLineFixtures) {        
        this.purApItemFixture = purApItemFixture;
        this.paymentRequestAccountingLineFixtures = paymentRequestAccountingLineFixtures;
    }

    public void addTo(PaymentRequestDocument paymentRequestDocument) {
        PaymentRequestItem item = null;
        item = (PaymentRequestItem) this.createPaymentRequestItem();
        paymentRequestDocument.addItem(item);
        // iterate over the accounts
        for (PaymentRequestAccountingLineFixture paymentRequestAccountingLineFixture : paymentRequestAccountingLineFixtures) {
            paymentRequestAccountingLineFixture.addTo(item);
        }
    }

    public AccountsPayableItem createPaymentRequestItem() {
        PaymentRequestItem item = (PaymentRequestItem) purApItemFixture.createPurApItem(PaymentRequestItem.class);        
        return item;
    }

}
