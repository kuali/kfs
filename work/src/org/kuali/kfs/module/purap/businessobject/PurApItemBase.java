/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.util.PurApObjectUtils;

/**
 * 
 */
public abstract class PurApItemBase extends PersistableBusinessObjectBase implements PurApItem {

	private Integer itemIdentifier;
	private Integer itemLineNumber;
	private String capitalAssetTransactionTypeCode;
	private String itemUnitOfMeasureCode;
	private String itemCatalogNumber;
	private String itemDescription;
	private String itemCapitalAssetNoteText;
	private BigDecimal itemUnitPrice;
	private String itemTypeCode;
	private String itemAuxiliaryPartIdentifier;
	private String externalOrganizationB2bProductReferenceNumber;
	private String externalOrganizationB2bProductTypeName;
	private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal extendedPrice; //not currently in DB

    
    private List<PurApAccountingLine> sourceAccountingLines;
    private transient List<PurApAccountingLine> baselineSourceAccountingLines;
    private transient PurApAccountingLine newSourceLine;
    
    
	private CapitalAssetTransactionType capitalAssetTransactionType;
	private ItemType itemType;
    private Integer purapDocumentIdentifier;
    private KualiDecimal itemQuantity;

	/**
	 * Default constructor.
	 */
	public PurApItemBase() {
        itemTypeCode = PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE;
        sourceAccountingLines = new TypedArrayList(getAccountingLineClass());
        baselineSourceAccountingLines = new TypedArrayList(getAccountingLineClass());
        resetAccount();
	}
	
    public String getItemIdentifierString() {
        String itemLineNumberString = (getItemLineNumber() != null ? getItemLineNumber().toString() : "");
        String identifierString = (getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + itemLineNumberString : getItemType().getItemTypeDescription()); 
        return identifierString;
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
        //KULPURAP-1096 Setting scale on retrieval of unit price
        if(itemUnitPrice!=null) {
            if(itemUnitPrice.scale()<PurapConstants.DOLLAR_AMOUNT_MIN_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.DOLLAR_AMOUNT_MIN_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            } else if(itemUnitPrice.scale()>PurapConstants.DOLLAR_AMOUNT_MIN_SCALE && itemUnitPrice.scale() < PurapConstants.UNIT_PRICE_MAX_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.UNIT_PRICE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            }
        }

        return itemUnitPrice;
	}

	/**
	 * Sets the itemUnitPrice attribute.
	 * 
	 * @param itemUnitPrice The itemUnitPrice to set.
	 * 
	 */
	public void setItemUnitPrice(BigDecimal itemUnitPrice) {
		if(itemUnitPrice!=null) {
            if(itemUnitPrice.scale()<PurapConstants.DOLLAR_AMOUNT_MIN_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.DOLLAR_AMOUNT_MIN_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            } else if(itemUnitPrice.scale()>PurapConstants.DOLLAR_AMOUNT_MIN_SCALE && itemUnitPrice.scale() < PurapConstants.UNIT_PRICE_MAX_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.UNIT_PRICE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            }
        }
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
        if (ObjectUtils.isNull(itemType)) {
            refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
        }
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

    
// from epic
//    public BigDecimal getExtendedCost() {
//        if (this.unitPrice == null) {
//          return null;
//        } else if (this.getIsServiceItem()) {
//          return getUnitPrice().setScale(2,BigDecimal.ROUND_HALF_UP);
//        } else {
//          return this.orderQuantity.multiply(getUnitPrice()).setScale(2,BigDecimal.ROUND_HALF_UP);
//        }
//      }

	/**
     * Gets the extendedPrice attribute. 
     * @return Returns the extendedPrice.
     */
    public KualiDecimal getExtendedPrice() {
        return calculateExtendedPrice();
    }

    /**
     * This method calculates the extended price based on item type
     * 
     * @return KualiDecimal - calculate extended price
     */
    public KualiDecimal calculateExtendedPrice() {
        KualiDecimal extendedPrice = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(itemUnitPrice)) {
            if (!this.itemType.isQuantityBasedGeneralLedgerIndicator()) {
                //SERVICE ITEM: return unit price as extended price
                extendedPrice = new KualiDecimal(this.itemUnitPrice.toString());
            } else if (ObjectUtils.isNotNull(this.getItemQuantity())) {
                BigDecimal calcExtendedPrice = this.itemUnitPrice.multiply(this.itemQuantity.bigDecimalValue());
                //ITEM TYPE (qty driven): return (unitPrice x qty)
                extendedPrice = new KualiDecimal(calcExtendedPrice);
            }
        }
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
     * Gets the accountingLines attribute. 
     * @return Returns the accountingLines.
     */
    public List<PurApAccountingLine> getSourceAccountingLines() {
        return sourceAccountingLines;
    }

    /**
     * Sets the accountingLines attribute value.
     * @param accountingLines The accountingLines to set.
     */
    public void setSourceAccountingLines(List<PurApAccountingLine> accountingLines) {
        this.sourceAccountingLines = accountingLines;
    }

    /**
     * Gets the baselineSourceLines attribute. 
     * @return Returns the baselineSourceLines.
     */
    public List<PurApAccountingLine> getBaselineSourceAccountingLines() {
        return baselineSourceAccountingLines;
    }

    /**
     * Sets the baselineSourceLines attribute value.
     * @param baselineSourceLines The baselineSourceLines to set.
     */
    public void setBaselineSourceAccountingLines(List<PurApAccountingLine> baselineSourceLines) {
        this.baselineSourceAccountingLines = baselineSourceLines;
    }

    /**
     * This implementation is coupled tightly with some underlying issues that the Struts PojoProcessor plugin has with how objects
     * get instantiated within lists. The first three lines are required otherwise when the PojoProcessor tries to automatically
     * inject values into the list, it will get an index out of bounds error if the instance at an index is being called and prior
     * instances at indices before that one are not being instantiated. So changing the code below will cause adding lines to break
     * if you add more than one item to the list.
     * 
     * @see org.kuali.core.document.FinancialDocument#getTargetAccountingLine(int)
     */
    public PurApAccountingLine getSourceAccountingLine(int index) {
        //TODO: we probably don't need this because of the TypedArrayList
//        while (getAccountingLines().size() <= index) {
//            PurApAccountingLine newAccount = getNewAccount();
//            getAccountingLines().add(newAccount);
//        }
        
        return (PurApAccountingLine) getSourceAccountingLines().get(index);
    }

    public PurApAccountingLine getBaselineSourceAccountingLine(int index) {
        return (PurApAccountingLine) getBaselineSourceAccountingLines().get(index);
    }
    
    /**
     * This method...
     * @param newAccount
     * @return
     * @throws RuntimeException
     */
    private PurApAccountingLine getNewAccount() throws RuntimeException {
        
        PurApAccountingLine newAccount=null;
        try {
            newAccount = (PurApAccountingLine)getAccountingLineClass().newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to get class");
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get class");
        }
        catch (NullPointerException e) {
            throw new RuntimeException("Can't instantiate Purchasing Account from base");
        }
        return newAccount;
    }

    public abstract Class getAccountingLineClass();
    
    /**
     * 
     * @see org.kuali.module.purap.bo.PurchasingApItem#resetAccount()
     */
    public void resetAccount() {
        //add a blank accounting line
        PurApAccountingLine purApAccountingLine = getNewAccount();
        setNewSourceLine(purApAccountingLine);
    }
    
    /**
     * @see org.kuali.core.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = new ArrayList();

        managedLists.add(getSourceAccountingLines());

        return managedLists;
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

    /**
     * Gets the newSourceLine attribute. 
     * @return Returns the newSourceLine.
     */
    public PurApAccountingLine getNewSourceLine() {
        return newSourceLine;
    }

    /**
     * Sets the newSourceLine attribute value.
     * @param newSourceLine The newSourceLine to set.
     */
    public void setNewSourceLine(PurApAccountingLine newAccountingLine) {
        this.newSourceLine = newAccountingLine;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    public KualiDecimal getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(KualiDecimal itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
    
    public boolean isAccountListEmpty() {
        List<PurApAccountingLine> accounts = getSourceAccountingLines();
        if (ObjectUtils.isNotNull(accounts)) {
            for (PurApAccountingLine element : accounts) {
                if (!element.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public PurApSummaryItem getSummaryItem() {
        PurApSummaryItem summaryItem = new PurApSummaryItem();
        PurApObjectUtils.populateFromBaseClass(PurApItemBase.class, this, summaryItem, new HashMap());
        return summaryItem;
    }
       
}
