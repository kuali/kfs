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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Payment Change Business Object.
 */
public class PaymentChange extends PersistableBusinessObjectBase {

    private Integer paymentChangeIdentifier;
    private Integer paymentRequestIdentifier;
    private Integer creditMemoIdentifier;
    private String lastUpdateUserId;
    private Timestamp lastUpdate;
    
    /**
     * Default constructor.
     */
    public PaymentChange() {

    }

    public Integer getPaymentChangeIdentifier() {
        return paymentChangeIdentifier;
    }

    public void setPaymentChangeIdentifier(Integer paymentChangeIdentifier) {
        this.paymentChangeIdentifier = paymentChangeIdentifier;
    }

    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }

    public Integer getCreditMemoIdentifier() {
        return creditMemoIdentifier;
    }

    public void setCreditMemoIdentifier(Integer creditMemoIdentifier) {
        this.creditMemoIdentifier = creditMemoIdentifier;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(String lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.paymentChangeIdentifier != null) {
            m.put("paymentChangeIdentifier", this.paymentChangeIdentifier.toString());
        }
        return m;
    }
}
