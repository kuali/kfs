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

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.CreditMemoDocument;

/**
 * 
 */
public class CreditMemoItem extends AccountsPayableItemBase {



	private Integer creditMemoIdentifier;
	private Integer itemLineNumber;
	private String itemTypeCode;
	private KualiDecimal itemCreditQuantity;
	private BigDecimal itemUnitPrice;
	private KualiDecimal itemExtendedPrice;
	private boolean itemAssignedToTradeInIndicator;

    private CreditMemoDocument creditMemo;

	/**
	 * Default constructor.
	 */
	public CreditMemoItem() {

	}



	/**
	 * Gets the creditMemoIdentifier attribute.
	 * 
	 * @return Returns the creditMemoIdentifier
	 * 
	 */
	public Integer getCreditMemoIdentifier() { 
		return creditMemoIdentifier;
	}

	/**
	 * Sets the creditMemoIdentifier attribute.
	 * 
	 * @param creditMemoIdentifier The creditMemoIdentifier to set.
	 * 
	 */
	public void setCreditMemoIdentifier(Integer creditMemoIdentifier) {
		this.creditMemoIdentifier = creditMemoIdentifier;
	}


	/**
	 * Gets the itemLineNumber attribute.
	 * 
	 * @return Returns the itemLineNumber
	 * 
	 */
	public Integer getItemLineNumber() { 
		return itemLineNumber;
	}

	/**
	 * Sets the itemLineNumber attribute.
	 * 
	 * @param itemLineNumber The itemLineNumber to set.
	 * 
	 */
	public void setItemLineNumber(Integer itemLineNumber) {
		this.itemLineNumber = itemLineNumber;
	}


	/**
	 * Gets the itemTypeCode attribute.
	 * 
	 * @return Returns the itemTypeCode
	 * 
	 */
	public String getItemTypeCode() { 
		return itemTypeCode;
	}

	/**
	 * Sets the itemTypeCode attribute.
	 * 
	 * @param itemTypeCode The itemTypeCode to set.
	 * 
	 */
	public void setItemTypeCode(String itemTypeCode) {
		this.itemTypeCode = itemTypeCode;
	}


	/**
	 * Gets the itemCreditQuantity attribute.
	 * 
	 * @return Returns the itemCreditQuantity
	 * 
	 */
	public KualiDecimal getItemCreditQuantity() { 
		return itemCreditQuantity;
	}

	/**
	 * Sets the itemCreditQuantity attribute.
	 * 
	 * @param itemCreditQuantity The itemCreditQuantity to set.
	 * 
	 */
	public void setItemCreditQuantity(KualiDecimal itemCreditQuantity) {
		this.itemCreditQuantity = itemCreditQuantity;
	}


	/**
	 * Gets the itemUnitPrice attribute.
	 * 
	 * @return Returns the itemUnitPrice
	 * 
	 */
	public BigDecimal getItemUnitPrice() { 
		return itemUnitPrice;
	}

	/**
	 * Sets the itemUnitPrice attribute.
	 * 
	 * @param itemUnitPrice The itemUnitPrice to set.
	 * 
	 */
	public void setItemUnitPrice(BigDecimal itemUnitPrice) {
		this.itemUnitPrice = itemUnitPrice;
	}


	/**
	 * Gets the itemExtendedPrice attribute.
	 * 
	 * @return Returns the itemExtendedPrice
	 * 
	 */
	public KualiDecimal getItemExtendedPrice() { 
		return itemExtendedPrice;
	}

	/**
	 * Sets the itemExtendedPrice attribute.
	 * 
	 * @param itemExtendedPrice The itemExtendedPrice to set.
	 * 
	 */
	public void setItemExtendedPrice(KualiDecimal itemExtendedPrice) {
		this.itemExtendedPrice = itemExtendedPrice;
	}


	/**
	 * Gets the itemAssignedToTradeInIndicator attribute.
	 * 
	 * @return Returns the itemAssignedToTradeInIndicator
	 * 
	 */
	public boolean getItemAssignedToTradeInIndicator() { 
		return itemAssignedToTradeInIndicator;
	}

	/**
	 * Sets the itemAssignedToTradeInIndicator attribute.
	 * 
	 * @param itemAssignedToTradeInIndicator The itemAssignedToTradeInIndicator to set.
	 * 
	 */
	public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
		this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
	}

    
    /**
     * @see org.kuali.module.purap.bo.PurApItemBase#getAccountingLineClass()
     */
    @Override
    public Class getAccountingLineClass() {
        return CreditMemoAccount.class;
    }

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.getItemIdentifier() != null) {
            m.put("creditMemoItemIdentifier", this.getItemIdentifier().toString());
        }
	    return m;
    }

    public CreditMemoDocument getCreditMemo() {
        return creditMemo;
    }
}
