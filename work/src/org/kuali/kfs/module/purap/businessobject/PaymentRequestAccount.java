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

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.module.purap.util.PurApObjectUtils;

/**
 * 
 */
public class PaymentRequestAccount extends PurApAccountingLineBase {
    //FIXME: remove this field, it is superseeded by amount on parent
	private KualiDecimal itemAccountTotalAmount = KualiDecimal.ZERO;
	private KualiDecimal disencumberedAmount = KualiDecimal.ZERO;

    private PaymentRequestItem paymentRequestItem;
    
	/**
	 * Default constructor.
	 */
	public PaymentRequestAccount() {

	}

    /**
     * Default constructor.
     */
    public PaymentRequestAccount(PaymentRequestItem item, PurchaseOrderAccount poa) {
//      copy base attributes
        PurApObjectUtils.populateFromBaseClass(AccountingLineBase.class, poa, this);
        //copy percent
        this.setAccountLinePercent(poa.getAccountLinePercent());
        setPaymentRequestItem(item);
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
     * @deprecated
     */
    public KualiDecimal getItemAccountTotalAmount() {
        return itemAccountTotalAmount;
    }

    /**
     * Sets the itemAccountTotalAmount attribute value.
     * @param itemAccountTotalAmount The itemAccountTotalAmount to set.
     * @deprecated
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

//    /**
//	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
//	 */
//	@Override
//    protected LinkedHashMap toStringMapper() {
//	    LinkedHashMap m = new LinkedHashMap();	    
//        if (this.getAccountIdentifier() != null) {
//            m.put("paymentRequestAccountIdentifier", this.getAccountIdentifier().toString());
//        }
//	    return m;
//    }
}
