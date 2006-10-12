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
public class PurchaseOrderItem extends PurchasingItemBase {

	private Integer purchaseOrderIdentifier;
	private KualiDecimal itemOrderedQuantity;
	private KualiDecimal itemInvoicedTotalQuantity;
	private KualiDecimal itemInvoicedTotalAmount;
	private KualiDecimal itemReceivedTotalQuantity;
	private KualiDecimal itemReturnedTotalQuantity;
	private KualiDecimal itemOutstandingEncumberedQuantity;
	private KualiDecimal itemOutstandingEncumbranceAmount;
	private boolean itemActiveIndicator;
	private String purchaseOrderCommodityCd;

    private PurchaseOrder purchaseOrder;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderItem() {

	}

    /**
     * Gets the itemActiveIndicator attribute. 
     * @return Returns the itemActiveIndicator.
     */
    public boolean isItemActiveIndicator() {
        return itemActiveIndicator;
    }

    /**
     * Sets the itemActiveIndicator attribute value.
     * @param itemActiveIndicator The itemActiveIndicator to set.
     */
    public void setItemActiveIndicator(boolean itemActiveIndicator) {
        this.itemActiveIndicator = itemActiveIndicator;
    }

    /**
     * Gets the itemInvoicedTotalAmount attribute. 
     * @return Returns the itemInvoicedTotalAmount.
     */
    public KualiDecimal getItemInvoicedTotalAmount() {
        return itemInvoicedTotalAmount;
    }

    /**
     * Sets the itemInvoicedTotalAmount attribute value.
     * @param itemInvoicedTotalAmount The itemInvoicedTotalAmount to set.
     */
    public void setItemInvoicedTotalAmount(KualiDecimal itemInvoicedTotalAmount) {
        this.itemInvoicedTotalAmount = itemInvoicedTotalAmount;
    }

    /**
     * Gets the itemInvoicedTotalQuantity attribute. 
     * @return Returns the itemInvoicedTotalQuantity.
     */
    public KualiDecimal getItemInvoicedTotalQuantity() {
        return itemInvoicedTotalQuantity;
    }

    /**
     * Sets the itemInvoicedTotalQuantity attribute value.
     * @param itemInvoicedTotalQuantity The itemInvoicedTotalQuantity to set.
     */
    public void setItemInvoicedTotalQuantity(KualiDecimal itemInvoicedTotalQuantity) {
        this.itemInvoicedTotalQuantity = itemInvoicedTotalQuantity;
    }

    /**
     * Gets the itemOrderedQuantity attribute. 
     * @return Returns the itemOrderedQuantity.
     */
    public KualiDecimal getItemOrderedQuantity() {
        return itemOrderedQuantity;
    }

    /**
     * Sets the itemOrderedQuantity attribute value.
     * @param itemOrderedQuantity The itemOrderedQuantity to set.
     */
    public void setItemOrderedQuantity(KualiDecimal itemOrderedQuantity) {
        this.itemOrderedQuantity = itemOrderedQuantity;
    }

    /**
     * Gets the itemOutstandingEncumberedQuantity attribute. 
     * @return Returns the itemOutstandingEncumberedQuantity.
     */
    public KualiDecimal getItemOutstandingEncumberedQuantity() {
        return itemOutstandingEncumberedQuantity;
    }

    /**
     * Sets the itemOutstandingEncumberedQuantity attribute value.
     * @param itemOutstandingEncumberedQuantity The itemOutstandingEncumberedQuantity to set.
     */
    public void setItemOutstandingEncumberedQuantity(KualiDecimal itemOutstandingEncumberedQuantity) {
        this.itemOutstandingEncumberedQuantity = itemOutstandingEncumberedQuantity;
    }

    /**
     * Gets the itemOutstandingEncumbranceAmount attribute. 
     * @return Returns the itemOutstandingEncumbranceAmount.
     */
    public KualiDecimal getItemOutstandingEncumbranceAmount() {
        return itemOutstandingEncumbranceAmount;
    }

    /**
     * Sets the itemOutstandingEncumbranceAmount attribute value.
     * @param itemOutstandingEncumbranceAmount The itemOutstandingEncumbranceAmount to set.
     */
    public void setItemOutstandingEncumbranceAmount(KualiDecimal itemOutstandingEncumbranceAmount) {
        this.itemOutstandingEncumbranceAmount = itemOutstandingEncumbranceAmount;
    }

    /**
     * Gets the itemReceivedTotalQuantity attribute. 
     * @return Returns the itemReceivedTotalQuantity.
     */
    public KualiDecimal getItemReceivedTotalQuantity() {
        return itemReceivedTotalQuantity;
    }

    /**
     * Sets the itemReceivedTotalQuantity attribute value.
     * @param itemReceivedTotalQuantity The itemReceivedTotalQuantity to set.
     */
    public void setItemReceivedTotalQuantity(KualiDecimal itemReceivedTotalQuantity) {
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
    }

    /**
     * Gets the itemReturnedTotalQuantity attribute. 
     * @return Returns the itemReturnedTotalQuantity.
     */
    public KualiDecimal getItemReturnedTotalQuantity() {
        return itemReturnedTotalQuantity;
    }

    /**
     * Sets the itemReturnedTotalQuantity attribute value.
     * @param itemReturnedTotalQuantity The itemReturnedTotalQuantity to set.
     */
    public void setItemReturnedTotalQuantity(KualiDecimal itemReturnedTotalQuantity) {
        this.itemReturnedTotalQuantity = itemReturnedTotalQuantity;
    }

    /**
     * Gets the purchaseOrder attribute. 
     * @return Returns the purchaseOrder.
     */
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the purchaseOrder attribute value.
     * @param purchaseOrder The purchaseOrder to set.
     */
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    /**
     * Gets the purchaseOrderCommodityCd attribute. 
     * @return Returns the purchaseOrderCommodityCd.
     */
    public String getPurchaseOrderCommodityCd() {
        return purchaseOrderCommodityCd;
    }

    /**
     * Sets the purchaseOrderCommodityCd attribute value.
     * @param purchaseOrderCommodityCd The purchaseOrderCommodityCd to set.
     */
    public void setPurchaseOrderCommodityCd(String purchaseOrderCommodityCd) {
        this.purchaseOrderCommodityCd = purchaseOrderCommodityCd;
    }

    /**
     * Gets the purchaseOrderIdentifier attribute. 
     * @return Returns the purchaseOrderIdentifier.
     */
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    /**
     * Sets the purchaseOrderIdentifier attribute value.
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }
    
}
