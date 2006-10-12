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
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RequisitionItem extends PurchasingItemBase {

    //Superclass contains
    //private Integer itemIdentifier;
    //private Integer itemLineNumber;
    //private String capitalAssetTransactionTypeCode;
    //private String itemUnitOfMeasureCode;
    //private String itemCatalogNumber;
    //private String itemDescription;
    //private String itemCapitalAssetNoteText;
    //private BigDecimal itemUnitPrice;
    //private String itemTypeCode;
    //private String requisitionLineIdentifier;
    //private String itemAuxiliaryPartIdentifier;
    //private String externalOrganizationB2bProductReferenceNumber;
    //private String externalOrganizationB2bProductTypeName;
    //private boolean itemAssignedToTradeInIndicator;

	private Integer requisitionIdentifier;
	private KualiDecimal itemQuantity;
	private boolean itemRestrictedIndicator;

    private RequisitionDocument requisition;
	
	/**
	 * Default constructor.
	 */
	public RequisitionItem() {

	}

    /**
     * Gets the itemQuantity attribute. 
     * @return Returns the itemQuantity.
     */
    public KualiDecimal getItemQuantity() {
        return itemQuantity;
    }

    /**
     * Sets the itemQuantity attribute value.
     * @param itemQuantity The itemQuantity to set.
     */
    public void setItemQuantity(KualiDecimal itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    /**
     * Gets the itemRestrictedIndicator attribute. 
     * @return Returns the itemRestrictedIndicator.
     */
    public boolean isItemRestrictedIndicator() {
        return itemRestrictedIndicator;
    }

    /**
     * Sets the itemRestrictedIndicator attribute value.
     * @param itemRestrictedIndicator The itemRestrictedIndicator to set.
     */
    public void setItemRestrictedIndicator(boolean itemRestrictedIndicator) {
        this.itemRestrictedIndicator = itemRestrictedIndicator;
    }

    /**
     * Gets the requisition attribute. 
     * @return Returns the requisition.
     */
    public RequisitionDocument getRequisition() {
        return requisition;
    }

    /**
     * Sets the requisition attribute value.
     * @param requisition The requisition to set.
     */
    public void setRequisition(RequisitionDocument requisition) {
        this.requisition = requisition;
    }

    /**
     * Gets the requisitionIdentifier attribute. 
     * @return Returns the requisitionIdentifier.
     */
    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    /**
     * Sets the requisitionIdentifier attribute value.
     * @param requisitionIdentifier The requisitionIdentifier to set.
     */
    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }


}
