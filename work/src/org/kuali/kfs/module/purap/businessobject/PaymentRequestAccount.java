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

package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.util.ObjectPopulationUtils;
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
        ObjectPopulationUtils.populateFromBaseClass(AccountingLineBase.class, poa, this, PurapConstants.KNOWN_UNCOPYABLE_FIELDS);
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
