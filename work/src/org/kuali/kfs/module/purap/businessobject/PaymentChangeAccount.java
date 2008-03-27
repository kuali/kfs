/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * Payment Change Account Business Object.
 */
public class PaymentChangeAccount extends PersistableBusinessObjectBase {

    private Integer paymentChangeIdentifier;
    private Integer itemLineNumber;
    private Integer generalLedgerPendingEntryIdentifier;
    private KualiDecimal itemAccountTotalAmount;

    /**
     * Default constructor.
     */
    public PaymentChangeAccount() {

    }

    public Integer getPaymentChangeIdentifier() {
        return paymentChangeIdentifier;
    }

    public void setPaymentChangeIdentifier(Integer paymentChangeIdentifier) {
        this.paymentChangeIdentifier = paymentChangeIdentifier;
    }

    public Integer getGeneralLedgerPendingEntryIdentifier() {
        return generalLedgerPendingEntryIdentifier;
    }

    public void setGeneralLedgerPendingEntryIdentifier(Integer generalLedgerPendingEntryIdentifier) {
        this.generalLedgerPendingEntryIdentifier = generalLedgerPendingEntryIdentifier;
    }

    public KualiDecimal getItemAccountTotalAmount() {
        return itemAccountTotalAmount;
    }

    public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
        this.itemAccountTotalAmount = itemAccountTotalAmount;
    }

    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.paymentChangeIdentifier != null) {
            m.put("paymentChangeIdentifier", this.paymentChangeIdentifier.toString());
        }
        if (this.itemLineNumber != null) {
            m.put("itemLineNumber", this.itemLineNumber.toString());
        }
        if (this.generalLedgerPendingEntryIdentifier != null) {
            m.put("generalLedgerPendingEntryIdentifier", this.generalLedgerPendingEntryIdentifier.toString());
        }
        return m;
    }
}
