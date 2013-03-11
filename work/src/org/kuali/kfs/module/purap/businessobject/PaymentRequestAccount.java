/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Payment Request Account Business Object.
 */
public class PaymentRequestAccount extends PurApAccountingLineBase {

    private KualiDecimal disencumberedAmount = KualiDecimal.ZERO;

    /**
     * Default constructor.
     */
    public PaymentRequestAccount() {
        this.setAmount(null);
        this.setAccountLinePercent(null);
        this.setSequenceNumber(0);
    }

    /**
     * Constructor.
     *
     * @param item - payment request item
     * @param poa - purchase order account
     */
    public PaymentRequestAccount(PaymentRequestItem item, PurchaseOrderAccount poa) {
        // copy base attributes
        PurApObjectUtils.populateFromBaseClass(AccountingLineBase.class, poa, this);
        // copy percent
        this.setSequenceNumber(poa.getSequenceNumber());
        this.setAccountLinePercent(poa.getAccountLinePercent());
        setItemIdentifier(item.getItemIdentifier());
        setPaymentRequestItem(item);
    }

    public KualiDecimal getDisencumberedAmount() {
        return disencumberedAmount;
    }

    public void setDisencumberedAmount(KualiDecimal disencumberedAmount) {
        this.disencumberedAmount = disencumberedAmount;
    }

    public PaymentRequestItem getPaymentRequestItem() {
        return super.getPurapItem();
    }

    public void setPaymentRequestItem(PaymentRequestItem paymentRequestItem) {
        super.setPurapItem(paymentRequestItem);
    }

    /**
     * Caller of this method should take care of creating PaymentRequestItems
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#copyFrom(org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    public void copyFrom(AccountingLine other) {
        super.copyFrom(other);
        if (other instanceof PaymentRequestAccount) {
            PaymentRequestAccount preqOther = (PaymentRequestAccount)other;
            setDisencumberedAmount(preqOther.getDisencumberedAmount());
        }
    }

}
