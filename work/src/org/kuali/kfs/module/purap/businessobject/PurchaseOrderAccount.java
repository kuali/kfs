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
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * 
 */
public class PurchaseOrderAccount extends PurApAccountingLineBase {

    private String documentNumber;
	private KualiDecimal itemAccountOutstandingEncumbranceAmount;

    private PurchaseOrderItem purchaseOrderItem;

    
	/**
	 * Default constructor.
	 */
	public PurchaseOrderAccount() {

	}

	/**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

	/**
	 * Gets the itemAccountOutstandingEncumbranceAmount attribute.
	 * 
	 * @return Returns the itemAccountOutstandingEncumbranceAmount
	 * 
	 */
	public KualiDecimal getItemAccountOutstandingEncumbranceAmount() { 
		return itemAccountOutstandingEncumbranceAmount;
	}

	/**
	 * Sets the itemAccountOutstandingEncumbranceAmount attribute.
	 * 
	 * @param itemAccountOutstandingEncumbranceAmount The itemAccountOutstandingEncumbranceAmount to set.
	 * 
	 */
	public void setItemAccountOutstandingEncumbranceAmount(KualiDecimal itemAccountOutstandingEncumbranceAmount) {
		this.itemAccountOutstandingEncumbranceAmount = itemAccountOutstandingEncumbranceAmount;
	}

    /**
     * Gets the purchaseOrderItem attribute. 
     * @return Returns the purchaseOrderItem.
     */
    public PurchaseOrderItem getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    /**
     * Sets the purchaseOrderItem attribute value.
     * @param purchaseOrderItem The purchaseOrderItem to set.
     * @deprecated
     */
    public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        this.purchaseOrderItem = purchaseOrderItem;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        if (this.getAccountIdentifier() != null) {
            m.put("accountIdentifier", this.getAccountIdentifier().toString());
        }
        return m;
    }    

}
