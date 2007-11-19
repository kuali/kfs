/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import java.util.List;

import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.AccountsPayableItem;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.document.PaymentRequestDocument;

public enum PaymentRequestItemFixture {

    PREQ_QTY_UNRESTRICTED_ITEM_1(
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new PaymentRequestAccountingLineFixture[] { PaymentRequestAccountingLineFixture.BASIC_PREQ_ACCOUNT_1 } // paymentRequestAccountMultiFixtures
    );
    
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
