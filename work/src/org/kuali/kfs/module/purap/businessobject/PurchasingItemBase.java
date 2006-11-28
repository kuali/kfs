/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * 
 */
public class PurchasingItemBase extends BusinessObjectBase implements PurchasingItem {

	private Integer itemIdentifier;
	private Integer itemLineNumber;
	private String capitalAssetTransactionTypeCode;
	private String itemUnitOfMeasureCode;
	private String itemCatalogNumber;
	private String itemDescription;
	private String itemCapitalAssetNoteText;
	private BigDecimal itemUnitPrice;
	private String itemTypeCode;
	private String requisitionLineIdentifier;
	private String itemAuxiliaryPartIdentifier;
	private String externalOrganizationB2bProductReferenceNumber;
	private String externalOrganizationB2bProductTypeName;
	private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal extendedPrice; //not currently in DB

	private CapitalAssetTransactionType capitalAssetTransactionType;
	private ItemType itemType;

	/**
	 * Default constructor.
	 */
	public PurchasingItemBase() {
	    //TODO: Chris - default itemType (should probably get this from spring or Constants file)
        itemTypeCode = "ITEM";
	}

	/**
	 * Gets the ItemIdentifier attribute.
	 * 
	 * @return Returns the ItemIdentifier
	 * 
	 */
	public Integer getItemIdentifier() { 
		return itemIdentifier;
	}

	/**
	 * Sets the ItemIdentifier attribute.
	 * 
	 * @param ItemIdentifier The ItemIdentifier to set.
	 * 
	 */
	public void setItemIdentifier(Integer ItemIdentifier) {
		this.itemIdentifier = ItemIdentifier;
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
	 * Gets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @return Returns the capitalAssetTransactionTypeCode
	 * 
	 */
	public String getCapitalAssetTransactionTypeCode() { 
		return capitalAssetTransactionTypeCode;
	}

	/**
	 * Sets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @param capitalAssetTransactionTypeCode The capitalAssetTransactionTypeCode to set.
	 * 
	 */
	public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
		this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
	}


	/**
	 * Gets the itemUnitOfMeasureCode attribute.
	 * 
	 * @return Returns the itemUnitOfMeasureCode
	 * 
	 */
	public String getItemUnitOfMeasureCode() { 
		return itemUnitOfMeasureCode;
	}

	/**
	 * Sets the itemUnitOfMeasureCode attribute.
	 * 
	 * @param itemUnitOfMeasureCode The itemUnitOfMeasureCode to set.
	 * 
	 */
	public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
		this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
	}

	/**
	 * Gets the itemCatalogNumber attribute.
	 * 
	 * @return Returns the itemCatalogNumber
	 * 
	 */
	public String getItemCatalogNumber() { 
		return itemCatalogNumber;
	}

	/**
	 * Sets the itemCatalogNumber attribute.
	 * 
	 * @param itemCatalogNumber The itemCatalogNumber to set.
	 * 
	 */
	public void setItemCatalogNumber(String itemCatalogNumber) {
		this.itemCatalogNumber = itemCatalogNumber;
	}


	/**
	 * Gets the itemDescription attribute.
	 * 
	 * @return Returns the itemDescription
	 * 
	 */
	public String getItemDescription() { 
		return itemDescription;
	}

	/**
	 * Sets the itemDescription attribute.
	 * 
	 * @param itemDescription The itemDescription to set.
	 * 
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}


	/**
	 * Gets the itemCapitalAssetNoteText attribute.
	 * 
	 * @return Returns the itemCapitalAssetNoteText
	 * 
	 */
	public String getItemCapitalAssetNoteText() { 
		return itemCapitalAssetNoteText;
	}

	/**
	 * Sets the itemCapitalAssetNoteText attribute.
	 * 
	 * @param itemCapitalAssetNoteText The itemCapitalAssetNoteText to set.
	 * 
	 */
	public void setItemCapitalAssetNoteText(String itemCapitalAssetNoteText) {
		this.itemCapitalAssetNoteText = itemCapitalAssetNoteText;
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
	 * Gets the requisitionLineIdentifier attribute.
	 * 
	 * @return Returns the requisitionLineIdentifier
	 * 
	 */
	public String getRequisitionLineIdentifier() { 
		return requisitionLineIdentifier;
	}

	/**
	 * Sets the LineIdentifier attribute.
	 * 
	 * @param LineIdentifier The LineIdentifier to set.
	 * 
	 */
	public void setRequisitionLineIdentifier(String requisitionLineIdentifier) {
		this.requisitionLineIdentifier = requisitionLineIdentifier;
	}


	/**
	 * Gets the itemAuxiliaryPartIdentifier attribute.
	 * 
	 * @return Returns the itemAuxiliaryPartIdentifier
	 * 
	 */
	public String getItemAuxiliaryPartIdentifier() { 
		return itemAuxiliaryPartIdentifier;
	}

	/**
	 * Sets the itemAuxiliaryPartIdentifier attribute.
	 * 
	 * @param itemAuxiliaryPartIdentifier The itemAuxiliaryPartIdentifier to set.
	 * 
	 */
	public void setItemAuxiliaryPartIdentifier(String itemAuxiliaryPartIdentifier) {
		this.itemAuxiliaryPartIdentifier = itemAuxiliaryPartIdentifier;
	}


	/**
	 * Gets the externalOrganizationB2bProductReferenceNumber attribute.
	 * 
	 * @return Returns the externalOrganizationB2bProductReferenceNumber
	 * 
	 */
	public String getExternalOrganizationB2bProductReferenceNumber() { 
		return externalOrganizationB2bProductReferenceNumber;
	}

	/**
	 * Sets the externalOrganizationB2bProductReferenceNumber attribute.
	 * 
	 * @param externalOrganizationB2bProductReferenceNumber The externalOrganizationB2bProductReferenceNumber to set.
	 * 
	 */
	public void setExternalOrganizationB2bProductReferenceNumber(String externalOrganizationB2bProductReferenceNumber) {
		this.externalOrganizationB2bProductReferenceNumber = externalOrganizationB2bProductReferenceNumber;
	}


	/**
	 * Gets the externalOrganizationB2bProductTypeName attribute.
	 * 
	 * @return Returns the externalOrganizationB2bProductTypeName
	 * 
	 */
	public String getExternalOrganizationB2bProductTypeName() { 
		return externalOrganizationB2bProductTypeName;
	}

	/**
	 * Sets the externalOrganizationB2bProductTypeName attribute.
	 * 
	 * @param externalOrganizationB2bProductTypeName The externalOrganizationB2bProductTypeName to set.
	 * 
	 */
	public void setExternalOrganizationB2bProductTypeName(String externalOrganizationB2bProductTypeName) {
		this.externalOrganizationB2bProductTypeName = externalOrganizationB2bProductTypeName;
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
	 * Gets the capitalAssetTransactionType attribute.
	 * 
	 * @return Returns the capitalAssetTransactionType
	 * 
	 */
	public CapitalAssetTransactionType getCapitalAssetTransactionType() { 
		return capitalAssetTransactionType;
	}

	/**
	 * Sets the capitalAssetTransactionType attribute.
	 * 
	 * @param capitalAssetTransactionType The capitalAssetTransactionType to set.
	 * @deprecated
	 */
	public void setCapitalAssetTransactionType(CapitalAssetTransactionType capitalAssetTransactionType) {
		this.capitalAssetTransactionType = capitalAssetTransactionType;
	}

	/**
	 * Gets the itemType attribute.
	 * 
	 * @return Returns the itemType
	 * 
	 */
	public ItemType getItemType() { 
		return itemType;
	}

	/**
	 * Sets the itemType attribute.
	 * 
	 * @param itemType The itemType to set.
	 * @deprecated
	 */
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	/**
     * Gets the extendedPrice attribute. 
     * @return Returns the extendedPrice.
     */
    public KualiDecimal getExtendedPrice() {
        return extendedPrice;
    }

    /**
     * Sets the extendedPrice attribute value.
     * @param extendedPrice The extendedPrice to set.
     */
    public void setExtendedPrice(KualiDecimal extendedPrice) {
        this.extendedPrice = extendedPrice;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.itemIdentifier != null) {
            m.put("requisitionItemIdentifier", this.itemIdentifier.toString());
        }
	    return m;
    }
}
