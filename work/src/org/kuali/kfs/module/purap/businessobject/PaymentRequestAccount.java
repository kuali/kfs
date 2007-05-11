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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * 
 */
public class PaymentRequestAccount extends PurApAccountingLineBase {

	private KualiDecimal itemAccountTotalAmount;
	private KualiDecimal disencumberedAmount;

    private PaymentRequestItem paymentRequestItem;
    
	/**
	 * Default constructor.
	 */
	public PaymentRequestAccount() {

	}
    
    /**
     * Gets the disencumberedAmount attribute. 
     * @return Returns the disencumberedAmount.
     */
    public KualiDecimal getDisencumberedAmount() {
        return disencumberedAmount;
    }

    /**
     * Sets the disencumberedAmount attribute value.
     * @param disencumberedAmount The disencumberedAmount to set.
     */
    public void setDisencumberedAmount(KualiDecimal disencumberedAmount) {
        this.disencumberedAmount = disencumberedAmount;
    }

    /**
     * Gets the itemAccountTotalAmount attribute. 
     * @return Returns the itemAccountTotalAmount.
     */
    public KualiDecimal getItemAccountTotalAmount() {
        return itemAccountTotalAmount;
    }

    /**
     * Sets the itemAccountTotalAmount attribute value.
     * @param itemAccountTotalAmount The itemAccountTotalAmount to set.
     */
    public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
        this.itemAccountTotalAmount = itemAccountTotalAmount;
    }

    /**
     * Gets the paymentRequestItem attribute. 
     * @return Returns the paymentRequestItem.
     */
    public PaymentRequestItem getPaymentRequestItem() {
        return paymentRequestItem;
    }

    /**
     * Sets the paymentRequestItem attribute value.
     * @param paymentRequestItem The paymentRequestItem to set.
     */
    public void setPaymentRequestItem(PaymentRequestItem paymentRequestItem) {
        this.paymentRequestItem = paymentRequestItem;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
    protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.getAccountIdentifier() != null) {
            m.put("paymentRequestAccountIdentifier", this.getAccountIdentifier().toString());
        }
	    return m;
    }
}
