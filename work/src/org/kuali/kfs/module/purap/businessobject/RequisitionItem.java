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
public class RequisitionItem extends BusinessObjectBase {

	private Integer requisitionItemIdentifier;
	private Integer requisitionIdentifier;
	private Integer itemLineNumber;
	private String capitalAssetTransactionTypeCode;
	private String itemUnitOfMeasureCode;
	private KualiDecimal itemQuantity;
	private String itemCatalogNumber;
	private String itemDescription;
	private String itemCapitalAssetNoteText;
	private BigDecimal itemUnitPrice;
	private boolean itemRestrictedIndicator;
	private String itemTypeCode;
	private String requisitionLineIdentifier;
	private String itemAuxiliaryPartIdentifier;
	private String externalOrganizationB2bProductReferenceNumber;
	private String externalOrganizationB2bProductTypeName;
	private boolean itemAssignedToTradeInIndicator;

    private Requisition requisition;
	private CapitalAssetTransactionType capitalAssetTransactionType;
	private ItemType itemType;

	/**
	 * Default constructor.
	 */
	public RequisitionItem() {

	}

	/**
	 * Gets the requisitionItemIdentifier attribute.
	 * 
	 * @return - Returns the requisitionItemIdentifier
	 * 
	 */
	public Integer getRequisitionItemIdentifier() { 
		return requisitionItemIdentifier;
	}

	/**
	 * Sets the requisitionItemIdentifier attribute.
	 * 
	 * @param - requisitionItemIdentifier The requisitionItemIdentifier to set.
	 * 
	 */
	public void setRequisitionItemIdentifier(Integer requisitionItemIdentifier) {
		this.requisitionItemIdentifier = requisitionItemIdentifier;
	}


	/**
	 * Gets the requisitionIdentifier attribute.
	 * 
	 * @return - Returns the requisitionIdentifier
	 * 
	 */
	public Integer getRequisitionIdentifier() { 
		return requisitionIdentifier;
	}

	/**
	 * Sets the requisitionIdentifier attribute.
	 * 
	 * @param - requisitionIdentifier The requisitionIdentifier to set.
	 * 
	 */
	public void setRequisitionIdentifier(Integer requisitionIdentifier) {
		this.requisitionIdentifier = requisitionIdentifier;
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
	 * Gets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @return - Returns the capitalAssetTransactionTypeCode
	 * 
	 */
	public String getCapitalAssetTransactionTypeCode() { 
		return capitalAssetTransactionTypeCode;
	}

	/**
	 * Sets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @param - capitalAssetTransactionTypeCode The capitalAssetTransactionTypeCode to set.
	 * 
	 */
	public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
		this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
	}


	/**
	 * Gets the itemUnitOfMeasureCode attribute.
	 * 
	 * @return - Returns the itemUnitOfMeasureCode
	 * 
	 */
	public String getItemUnitOfMeasureCode() { 
		return itemUnitOfMeasureCode;
	}

	/**
	 * Sets the itemUnitOfMeasureCode attribute.
	 * 
	 * @param - itemUnitOfMeasureCode The itemUnitOfMeasureCode to set.
	 * 
	 */
	public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
		this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
	}


	/**
	 * Gets the itemQuantity attribute.
	 * 
	 * @return - Returns the itemQuantity
	 * 
	 */
	public KualiDecimal getItemQuantity() { 
		return itemQuantity;
	}

	/**
	 * Sets the itemQuantity attribute.
	 * 
	 * @param - itemQuantity The itemQuantity to set.
	 * 
	 */
	public void setItemQuantity(KualiDecimal itemQuantity) {
		this.itemQuantity = itemQuantity;
	}


	/**
	 * Gets the itemCatalogNumber attribute.
	 * 
	 * @return - Returns the itemCatalogNumber
	 * 
	 */
	public String getItemCatalogNumber() { 
		return itemCatalogNumber;
	}

	/**
	 * Sets the itemCatalogNumber attribute.
	 * 
	 * @param - itemCatalogNumber The itemCatalogNumber to set.
	 * 
	 */
	public void setItemCatalogNumber(String itemCatalogNumber) {
		this.itemCatalogNumber = itemCatalogNumber;
	}


	/**
	 * Gets the itemDescription attribute.
	 * 
	 * @return - Returns the itemDescription
	 * 
	 */
	public String getItemDescription() { 
		return itemDescription;
	}

	/**
	 * Sets the itemDescription attribute.
	 * 
	 * @param - itemDescription The itemDescription to set.
	 * 
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}


	/**
	 * Gets the itemCapitalAssetNoteText attribute.
	 * 
	 * @return - Returns the itemCapitalAssetNoteText
	 * 
	 */
	public String getItemCapitalAssetNoteText() { 
		return itemCapitalAssetNoteText;
	}

	/**
	 * Sets the itemCapitalAssetNoteText attribute.
	 * 
	 * @param - itemCapitalAssetNoteText The itemCapitalAssetNoteText to set.
	 * 
	 */
	public void setItemCapitalAssetNoteText(String itemCapitalAssetNoteText) {
		this.itemCapitalAssetNoteText = itemCapitalAssetNoteText;
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
	 * Gets the itemRestrictedIndicator attribute.
	 * 
	 * @return - Returns the itemRestrictedIndicator
	 * 
	 */
	public boolean getItemRestrictedIndicator() { 
		return itemRestrictedIndicator;
	}

	/**
	 * Sets the itemRestrictedIndicator attribute.
	 * 
	 * @param - itemRestrictedIndicator The itemRestrictedIndicator to set.
	 * 
	 */
	public void setItemRestrictedIndicator(boolean itemRestrictedIndicator) {
		this.itemRestrictedIndicator = itemRestrictedIndicator;
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
	 * Gets the requisitionLineIdentifier attribute.
	 * 
	 * @return - Returns the requisitionLineIdentifier
	 * 
	 */
	public String getRequisitionLineIdentifier() { 
		return requisitionLineIdentifier;
	}

	/**
	 * Sets the requisitionLineIdentifier attribute.
	 * 
	 * @param - requisitionLineIdentifier The requisitionLineIdentifier to set.
	 * 
	 */
	public void setRequisitionLineIdentifier(String requisitionLineIdentifier) {
		this.requisitionLineIdentifier = requisitionLineIdentifier;
	}


	/**
	 * Gets the itemAuxiliaryPartIdentifier attribute.
	 * 
	 * @return - Returns the itemAuxiliaryPartIdentifier
	 * 
	 */
	public String getItemAuxiliaryPartIdentifier() { 
		return itemAuxiliaryPartIdentifier;
	}

	/**
	 * Sets the itemAuxiliaryPartIdentifier attribute.
	 * 
	 * @param - itemAuxiliaryPartIdentifier The itemAuxiliaryPartIdentifier to set.
	 * 
	 */
	public void setItemAuxiliaryPartIdentifier(String itemAuxiliaryPartIdentifier) {
		this.itemAuxiliaryPartIdentifier = itemAuxiliaryPartIdentifier;
	}


	/**
	 * Gets the externalOrganizationB2bProductReferenceNumber attribute.
	 * 
	 * @return - Returns the externalOrganizationB2bProductReferenceNumber
	 * 
	 */
	public String getExternalOrganizationB2bProductReferenceNumber() { 
		return externalOrganizationB2bProductReferenceNumber;
	}

	/**
	 * Sets the externalOrganizationB2bProductReferenceNumber attribute.
	 * 
	 * @param - externalOrganizationB2bProductReferenceNumber The externalOrganizationB2bProductReferenceNumber to set.
	 * 
	 */
	public void setExternalOrganizationB2bProductReferenceNumber(String externalOrganizationB2bProductReferenceNumber) {
		this.externalOrganizationB2bProductReferenceNumber = externalOrganizationB2bProductReferenceNumber;
	}


	/**
	 * Gets the externalOrganizationB2bProductTypeName attribute.
	 * 
	 * @return - Returns the externalOrganizationB2bProductTypeName
	 * 
	 */
	public String getExternalOrganizationB2bProductTypeName() { 
		return externalOrganizationB2bProductTypeName;
	}

	/**
	 * Sets the externalOrganizationB2bProductTypeName attribute.
	 * 
	 * @param - externalOrganizationB2bProductTypeName The externalOrganizationB2bProductTypeName to set.
	 * 
	 */
	public void setExternalOrganizationB2bProductTypeName(String externalOrganizationB2bProductTypeName) {
		this.externalOrganizationB2bProductTypeName = externalOrganizationB2bProductTypeName;
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
	 * Gets the requisition attribute.
	 * 
	 * @return - Returns the requisition
	 * 
	 */
	public Requisition getRequisition() { 
		return requisition;
	}

	/**
	 * Sets the requisition attribute.
	 * 
	 * @param - requisition The requisition to set.
	 * @deprecated
	 */
	public void setRequisition(Requisition requisition) {
		this.requisition = requisition;
	}

	/**
	 * Gets the capitalAssetTransactionType attribute.
	 * 
	 * @return - Returns the capitalAssetTransactionType
	 * 
	 */
	public CapitalAssetTransactionType getCapitalAssetTransactionType() { 
		return capitalAssetTransactionType;
	}

	/**
	 * Sets the capitalAssetTransactionType attribute.
	 * 
	 * @param - capitalAssetTransactionType The capitalAssetTransactionType to set.
	 * @deprecated
	 */
	public void setCapitalAssetTransactionType(CapitalAssetTransactionType capitalAssetTransactionType) {
		this.capitalAssetTransactionType = capitalAssetTransactionType;
	}

	/**
	 * Gets the itemType attribute.
	 * 
	 * @return - Returns the itemType
	 * 
	 */
	public ItemType getItemType() { 
		return itemType;
	}

	/**
	 * Sets the itemType attribute.
	 * 
	 * @param - itemType The itemType to set.
	 * @deprecated
	 */
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.requisitionItemIdentifier != null) {
            m.put("requisitionItemIdentifier", this.requisitionItemIdentifier.toString());
        }
	    return m;
    }
}
