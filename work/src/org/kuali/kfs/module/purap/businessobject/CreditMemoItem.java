/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CreditMemoItem extends BusinessObjectBase {

	private Integer creditMemoItemIdentifier;
	private Integer creditMemoIdentifier;
	private Integer itemLineNumber;
	private String itemTypeCode;
	private KualiDecimal itemCreditQuantity;
	private BigDecimal itemUnitPrice;
	private KualiDecimal itemExtendedPrice;
	private boolean itemAssignedToTradeInIndicator;

    private CreditMemo creditMemo;

	/**
	 * Default constructor.
	 */
	public CreditMemoItem() {

	}

	/**
	 * Gets the creditMemoItemIdentifier attribute.
	 * 
	 * @return - Returns the creditMemoItemIdentifier
	 * 
	 */
	public Integer getCreditMemoItemIdentifier() { 
		return creditMemoItemIdentifier;
	}

	/**
	 * Sets the creditMemoItemIdentifier attribute.
	 * 
	 * @param - creditMemoItemIdentifier The creditMemoItemIdentifier to set.
	 * 
	 */
	public void setCreditMemoItemIdentifier(Integer creditMemoItemIdentifier) {
		this.creditMemoItemIdentifier = creditMemoItemIdentifier;
	}


	/**
	 * Gets the creditMemoIdentifier attribute.
	 * 
	 * @return - Returns the creditMemoIdentifier
	 * 
	 */
	public Integer getCreditMemoIdentifier() { 
		return creditMemoIdentifier;
	}

	/**
	 * Sets the creditMemoIdentifier attribute.
	 * 
	 * @param - creditMemoIdentifier The creditMemoIdentifier to set.
	 * 
	 */
	public void setCreditMemoIdentifier(Integer creditMemoIdentifier) {
		this.creditMemoIdentifier = creditMemoIdentifier;
	}


	/**
	 * Gets the itemLineNumber attribute.
	 * 
	 * @return - Returns the itemLineNumber
	 * 
	 */
	public Integer getItemLineNumber() { 
		return itemLineNumber;
	}

	/**
	 * Sets the itemLineNumber attribute.
	 * 
	 * @param - itemLineNumber The itemLineNumber to set.
	 * 
	 */
	public void setItemLineNumber(Integer itemLineNumber) {
		this.itemLineNumber = itemLineNumber;
	}


	/**
	 * Gets the itemTypeCode attribute.
	 * 
	 * @return - Returns the itemTypeCode
	 * 
	 */
	public String getItemTypeCode() { 
		return itemTypeCode;
	}

	/**
	 * Sets the itemTypeCode attribute.
	 * 
	 * @param - itemTypeCode The itemTypeCode to set.
	 * 
	 */
	public void setItemTypeCode(String itemTypeCode) {
		this.itemTypeCode = itemTypeCode;
	}


	/**
	 * Gets the itemCreditQuantity attribute.
	 * 
	 * @return - Returns the itemCreditQuantity
	 * 
	 */
	public KualiDecimal getItemCreditQuantity() { 
		return itemCreditQuantity;
	}

	/**
	 * Sets the itemCreditQuantity attribute.
	 * 
	 * @param - itemCreditQuantity The itemCreditQuantity to set.
	 * 
	 */
	public void setItemCreditQuantity(KualiDecimal itemCreditQuantity) {
		this.itemCreditQuantity = itemCreditQuantity;
	}


	/**
	 * Gets the itemUnitPrice attribute.
	 * 
	 * @return - Returns the itemUnitPrice
	 * 
	 */
	public BigDecimal getItemUnitPrice() { 
		return itemUnitPrice;
	}

	/**
	 * Sets the itemUnitPrice attribute.
	 * 
	 * @param - itemUnitPrice The itemUnitPrice to set.
	 * 
	 */
	public void setItemUnitPrice(BigDecimal itemUnitPrice) {
		this.itemUnitPrice = itemUnitPrice;
	}


	/**
	 * Gets the itemExtendedPrice attribute.
	 * 
	 * @return - Returns the itemExtendedPrice
	 * 
	 */
	public KualiDecimal getItemExtendedPrice() { 
		return itemExtendedPrice;
	}

	/**
	 * Sets the itemExtendedPrice attribute.
	 * 
	 * @param - itemExtendedPrice The itemExtendedPrice to set.
	 * 
	 */
	public void setItemExtendedPrice(KualiDecimal itemExtendedPrice) {
		this.itemExtendedPrice = itemExtendedPrice;
	}


	/**
	 * Gets the itemAssignedToTradeInIndicator attribute.
	 * 
	 * @return - Returns the itemAssignedToTradeInIndicator
	 * 
	 */
	public boolean getItemAssignedToTradeInIndicator() { 
		return itemAssignedToTradeInIndicator;
	}

	/**
	 * Sets the itemAssignedToTradeInIndicator attribute.
	 * 
	 * @param - itemAssignedToTradeInIndicator The itemAssignedToTradeInIndicator to set.
	 * 
	 */
	public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
		this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
	}


	/**
	 * Gets the creditMemo attribute.
	 * 
	 * @return - Returns the creditMemo
	 * 
	 */
	public CreditMemo getCreditMemo() { 
		return creditMemo;
	}

	/**
	 * Sets the creditMemo attribute.
	 * 
	 * @param - creditMemo The creditMemo to set.
	 * @deprecated
	 */
	public void setCreditMemo(CreditMemo creditMemo) {
		this.creditMemo = creditMemo;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.creditMemoItemIdentifier != null) {
            m.put("creditMemoItemIdentifier", this.creditMemoItemIdentifier.toString());
        }
	    return m;
    }
}
