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
 * Purap Item Base Business Object.
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
    private KualiDecimal extendedPrice; // not currently in DB

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

    /**
     * @see org.kuali.module.purap.bo.PurApItem#getItemIdentifierString()
     */
    public String getItemIdentifierString() {
        String itemLineNumberString = (getItemLineNumber() != null ? getItemLineNumber().toString() : "");
        String identifierString = (getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + itemLineNumberString : getItemType().getItemTypeDescription());
        return identifierString;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer ItemIdentifier) {
        this.itemIdentifier = ItemIdentifier;
    }

    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public String getItemUnitOfMeasureCode() {
        return itemUnitOfMeasureCode;
    }

    public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
        this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
    }

    public String getItemCatalogNumber() {
        return itemCatalogNumber;
    }

    public void setItemCatalogNumber(String itemCatalogNumber) {
        this.itemCatalogNumber = itemCatalogNumber;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemCapitalAssetNoteText() {
        return itemCapitalAssetNoteText;
    }

    public void setItemCapitalAssetNoteText(String itemCapitalAssetNoteText) {
        this.itemCapitalAssetNoteText = itemCapitalAssetNoteText;
    }

    public BigDecimal getItemUnitPrice() {
        // KULPURAP-1096 Setting scale on retrieval of unit price
        if (itemUnitPrice != null) {
            if (itemUnitPrice.scale() < PurapConstants.DOLLAR_AMOUNT_MIN_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.DOLLAR_AMOUNT_MIN_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            }
            else if (itemUnitPrice.scale() > PurapConstants.UNIT_PRICE_MAX_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.UNIT_PRICE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            }
        }

        return itemUnitPrice;
    }

    public void setItemUnitPrice(BigDecimal itemUnitPrice) {
        if (itemUnitPrice != null) {
            if (itemUnitPrice.scale() < PurapConstants.DOLLAR_AMOUNT_MIN_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.DOLLAR_AMOUNT_MIN_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            }
            else if (itemUnitPrice.scale() > PurapConstants.UNIT_PRICE_MAX_SCALE) {
                itemUnitPrice = itemUnitPrice.setScale(PurapConstants.UNIT_PRICE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
            }
        }
        this.itemUnitPrice = itemUnitPrice;
    }

    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    public String getItemAuxiliaryPartIdentifier() {
        return itemAuxiliaryPartIdentifier;
    }

    public void setItemAuxiliaryPartIdentifier(String itemAuxiliaryPartIdentifier) {
        this.itemAuxiliaryPartIdentifier = itemAuxiliaryPartIdentifier;
    }

    public String getExternalOrganizationB2bProductReferenceNumber() {
        return externalOrganizationB2bProductReferenceNumber;
    }

    public void setExternalOrganizationB2bProductReferenceNumber(String externalOrganizationB2bProductReferenceNumber) {
        this.externalOrganizationB2bProductReferenceNumber = externalOrganizationB2bProductReferenceNumber;
    }

    public String getExternalOrganizationB2bProductTypeName() {
        return externalOrganizationB2bProductTypeName;
    }

    public void setExternalOrganizationB2bProductTypeName(String externalOrganizationB2bProductTypeName) {
        this.externalOrganizationB2bProductTypeName = externalOrganizationB2bProductTypeName;
    }

    public boolean getItemAssignedToTradeInIndicator() {
        return itemAssignedToTradeInIndicator;
    }

    public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
        this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
    }

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

    public KualiDecimal getExtendedPrice() {
        return calculateExtendedPrice();
    }

    public KualiDecimal calculateExtendedPrice() {
        KualiDecimal extendedPrice = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(itemUnitPrice)) {
            if (!this.itemType.isQuantityBasedGeneralLedgerIndicator()) {
                // SERVICE ITEM: return unit price as extended price
                extendedPrice = new KualiDecimal(this.itemUnitPrice.toString());
            }
            else if (ObjectUtils.isNotNull(this.getItemQuantity())) {
                BigDecimal calcExtendedPrice = this.itemUnitPrice.multiply(this.itemQuantity.bigDecimalValue());
                // ITEM TYPE (qty driven): return (unitPrice x qty)
                extendedPrice = new KualiDecimal(calcExtendedPrice);
            }
        }
        return extendedPrice;
    }

    public void setExtendedPrice(KualiDecimal extendedPrice) {
        this.extendedPrice = extendedPrice;
    }

    public List<PurApAccountingLine> getSourceAccountingLines() {
        return sourceAccountingLines;
    }

    public void setSourceAccountingLines(List<PurApAccountingLine> accountingLines) {
        this.sourceAccountingLines = accountingLines;
    }

    public List<PurApAccountingLine> getBaselineSourceAccountingLines() {
        return baselineSourceAccountingLines;
    }

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
        return (PurApAccountingLine) getSourceAccountingLines().get(index);
    }

    public PurApAccountingLine getBaselineSourceAccountingLine(int index) {
        return (PurApAccountingLine) getBaselineSourceAccountingLines().get(index);
    }

    private PurApAccountingLine getNewAccount() throws RuntimeException {

        PurApAccountingLine newAccount = null;
        try {
            newAccount = (PurApAccountingLine) getAccountingLineClass().newInstance();
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

    public void resetAccount() {
        // add a blank accounting line
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

    public PurApAccountingLine getNewSourceLine() {
        return newSourceLine;
    }

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
        summaryItem.getItemType().setItemTypeDescription(this.itemType.getItemTypeDescription());
        return summaryItem;
    }

}
