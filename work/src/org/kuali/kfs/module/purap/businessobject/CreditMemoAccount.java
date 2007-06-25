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

/**
 * Accounting line Business Object for a credit memo item line.
 */
public class CreditMemoAccount extends PurApAccountingLineBase {
	private Integer creditMemoAccountIdentifier;
	private Integer creditMemoItemIdentifier;
	private KualiDecimal itemAccountTotalAmount;

    private CreditMemoItem creditMemoItem;
    
	/**
	 * Default constructor.
	 */
	public CreditMemoAccount() {

	}

	/**
	 * Gets the creditMemoAccountIdentifier attribute.
	 * 
	 * @return Returns the creditMemoAccountIdentifier
	 * 
	 */
	public Integer getCreditMemoAccountIdentifier() { 
		return creditMemoAccountIdentifier;
	}

	/**
	 * Sets the creditMemoAccountIdentifier attribute.
	 * 
	 * @param creditMemoAccountIdentifier The creditMemoAccountIdentifier to set.
	 * 
	 */
	public void setCreditMemoAccountIdentifier(Integer creditMemoAccountIdentifier) {
		this.creditMemoAccountIdentifier = creditMemoAccountIdentifier;
	}


	/**
	 * Gets the creditMemoItemIdentifier attribute.
	 * 
	 * @return Returns the creditMemoItemIdentifier
	 * 
	 */
	public Integer getCreditMemoItemIdentifier() { 
		return creditMemoItemIdentifier;
	}

	/**
	 * Sets the creditMemoItemIdentifier attribute.
	 * 
	 * @param creditMemoItemIdentifier The creditMemoItemIdentifier to set.
	 * 
	 */
	public void setCreditMemoItemIdentifier(Integer creditMemoItemIdentifier) {
		this.creditMemoItemIdentifier = creditMemoItemIdentifier;
	}



	/**
	 * Gets the itemAccountTotalAmount attribute.
	 * 
	 * @return Returns the itemAccountTotalAmount
	 * 
	 */
	public KualiDecimal getItemAccountTotalAmount() { 
		return itemAccountTotalAmount;
	}

	/**
	 * Sets the itemAccountTotalAmount attribute.
	 * 
	 * @param itemAccountTotalAmount The itemAccountTotalAmount to set.
	 * 
	 */
	public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
		this.itemAccountTotalAmount = itemAccountTotalAmount;
	}


	/**
	 * Gets the creditMemoItem attribute.
	 * 
	 * @return Returns the creditMemoItem
	 * 
	 */
	public CreditMemoItem getCreditMemoItem() { 
		return creditMemoItem;
	}

	/**
	 * Sets the creditMemoItem attribute.
	 * 
	 * @param creditMemoItem The creditMemoItem to set.
	 * @deprecated
	 */
	public void setCreditMemoItem(CreditMemoItem creditMemoItem) {
		this.creditMemoItem = creditMemoItem;
	}

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.creditMemoAccountIdentifier != null) {
            m.put("creditMemoAccountIdentifier", this.creditMemoAccountIdentifier.toString());
        }
        return m;
    }
}
