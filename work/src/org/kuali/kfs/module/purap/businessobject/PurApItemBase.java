/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.util.ObjectPopulationUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purap Item Base Business Object.
 */
public abstract class PurApItemBase extends PersistableBusinessObjectBase implements PurApItem {

    private Integer itemIdentifier;
    private Integer itemLineNumber;
    private String itemUnitOfMeasureCode;
    private String itemCatalogNumber;
    private String itemDescription;
    private BigDecimal itemUnitPrice;
    private String itemTypeCode;
    private String itemAuxiliaryPartIdentifier;
    private String externalOrganizationB2bProductReferenceNumber;
    private String externalOrganizationB2bProductTypeName;
    private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal extendedPrice; // not currently in DB
    private KualiDecimal itemSalesTaxAmount;

    private List<PurApItemUseTax> useTaxItems;
    private List<PurApAccountingLine> sourceAccountingLines;
    private List<PurApAccountingLine> baselineSourceAccountingLines;
    private PurApAccountingLine newSourceLine;

    private ItemType itemType;
    private Integer purapDocumentIdentifier;
    private KualiDecimal itemQuantity;

    private PurchasingAccountsPayableDocument purapDocument;

    /**
     * Default constructor.
     */
    public PurApItemBase() {
        itemTypeCode = PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE;
        sourceAccountingLines = new ArrayList();
        baselineSourceAccountingLines = new ArrayList();
        useTaxItems = new ArrayList();
        resetAccount();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItem#getItemIdentifierString()
     */
    @Override
    public String getItemIdentifierString() {
        String itemLineNumberString = (getItemLineNumber() != null ? getItemLineNumber().toString() : "");
        String identifierString = (getItemType().isLineItemIndicator() ? "Item " + itemLineNumberString : getItemType().getItemTypeDescription());
        return identifierString;
    }

    @Override
    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    @Override
    public void setItemIdentifier(Integer ItemIdentifier) {
        this.itemIdentifier = ItemIdentifier;
    }

    @Override
    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    @Override
    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    @Override
    public String getItemUnitOfMeasureCode() {
        return itemUnitOfMeasureCode;
    }

    @Override
    public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
        this.itemUnitOfMeasureCode = (StringUtils.isNotBlank(itemUnitOfMeasureCode) ? itemUnitOfMeasureCode.toUpperCase() : itemUnitOfMeasureCode);
    }

    @Override
    public String getItemCatalogNumber() {
        return itemCatalogNumber;
    }

    @Override
    public void setItemCatalogNumber(String itemCatalogNumber) {
        this.itemCatalogNumber = itemCatalogNumber;
    }

    @Override
    public String getItemDescription() {
        return itemDescription;
    }

    @Override
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    @Override
    public BigDecimal getItemUnitPrice() {
        // Setting scale on retrieval of unit price
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

    @Override
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

    @Override
    public String getItemTypeCode() {
        return itemTypeCode;
    }

    @Override
    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    @Override
    public String getItemAuxiliaryPartIdentifier() {
        return itemAuxiliaryPartIdentifier;
    }

    @Override
    public void setItemAuxiliaryPartIdentifier(String itemAuxiliaryPartIdentifier) {
        this.itemAuxiliaryPartIdentifier = itemAuxiliaryPartIdentifier;
    }

    @Override
    public String getExternalOrganizationB2bProductReferenceNumber() {
        return externalOrganizationB2bProductReferenceNumber;
    }

    @Override
    public void setExternalOrganizationB2bProductReferenceNumber(String externalOrganizationB2bProductReferenceNumber) {
        this.externalOrganizationB2bProductReferenceNumber = externalOrganizationB2bProductReferenceNumber;
    }

    @Override
    public String getExternalOrganizationB2bProductTypeName() {
        return externalOrganizationB2bProductTypeName;
    }

    @Override
    public void setExternalOrganizationB2bProductTypeName(String externalOrganizationB2bProductTypeName) {
        this.externalOrganizationB2bProductTypeName = externalOrganizationB2bProductTypeName;
    }

    @Override
    public boolean getItemAssignedToTradeInIndicator() {
        return itemAssignedToTradeInIndicator;
    }

    @Override
    public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
        this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
    }

    @Override
    public ItemType getItemType() {
        if (ObjectUtils.isNull(itemType) || !itemType.getItemTypeCode().equals(itemTypeCode)) {
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
    @Override
    @Deprecated
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public KualiDecimal getItemTaxAmount() {
        KualiDecimal taxAmount = KualiDecimal.ZERO;

        if (ObjectUtils.isNull(purapDocument)) {
            this.refreshReferenceObject("purapDocument");
        }

        if (purapDocument.isUseTaxIndicator() == false) {
            taxAmount = this.itemSalesTaxAmount;
        }
        else {
            // sum use tax item tax amounts
            for (PurApItemUseTax useTaxItem : this.getUseTaxItems()) {
                taxAmount = taxAmount.add(useTaxItem.getTaxAmount());
            }
        }

        return taxAmount;
    }

    @Override
    public void setItemTaxAmount(KualiDecimal itemTaxAmount) {

        if (purapDocument == null) {
            this.refreshReferenceObject("purapDocument");
        }

        if (purapDocument.isUseTaxIndicator() == false) {
            this.itemSalesTaxAmount = itemTaxAmount;
        }

    }

    public final KualiDecimal getItemSalesTaxAmount() {
        return itemSalesTaxAmount;
    }

    public final void setItemSalesTaxAmount(KualiDecimal itemSalesTaxAmount) {
        this.itemSalesTaxAmount = itemSalesTaxAmount;
    }

    @Override
    public KualiDecimal getExtendedPrice() {
        return calculateExtendedPrice();
    }

    @Override
    public KualiDecimal getTotalAmount() {
        KualiDecimal totalAmount = getExtendedPrice();
        if (ObjectUtils.isNull(totalAmount)) {
            totalAmount = KualiDecimal.ZERO;
        }

        KualiDecimal taxAmount = getItemTaxAmount();
        if (ObjectUtils.isNull(taxAmount)) {
            taxAmount = KualiDecimal.ZERO;
        }

        totalAmount = totalAmount.add(taxAmount);

        return totalAmount;
    }

    @Override
    public void setTotalAmount(KualiDecimal totalAmount) {
        // do nothing, setter required by interface
    }

    @Override
    public KualiDecimal calculateExtendedPrice() {
        KualiDecimal extendedPrice = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(itemUnitPrice)) {
            if (this.itemType.isAmountBasedGeneralLedgerIndicator()) {
                // SERVICE ITEM: return unit price as extended price
                extendedPrice = new KualiDecimal(this.itemUnitPrice.toString());
            }
            else if (ObjectUtils.isNotNull(this.getItemQuantity())) {
                BigDecimal calcExtendedPrice = this.itemUnitPrice.multiply(this.itemQuantity.bigDecimalValue());
                // ITEM TYPE (qty driven): return (unitPrice x qty)
                extendedPrice = new KualiDecimal(calcExtendedPrice.setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR));
            }
        }
        return extendedPrice;
    }

    @Override
    public void setExtendedPrice(KualiDecimal extendedPrice) {
        this.extendedPrice = extendedPrice;
    }

    @Override
    public List<PurApAccountingLine> getSourceAccountingLines() {
        return sourceAccountingLines;
    }

    @Override
    public void setSourceAccountingLines(List<PurApAccountingLine> accountingLines) {
        this.sourceAccountingLines = accountingLines;
    }

    @Override
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
     * @see org.kuali.rice.krad.document.FinancialDocument#getTargetAccountingLine(int)
     */
    public PurApAccountingLine getSourceAccountingLine(int index) {
        while (getSourceAccountingLines().size() <= index) {
            PurApAccountingLine newAccount = getNewAccount();
            getSourceAccountingLines().add(newAccount);
        }
        return getSourceAccountingLines().get(index);
    }

    /**
     * This implementation is coupled tightly with some underlying issues that the Struts PojoProcessor plugin has with how objects
     * get instantiated within lists. The first three lines are required otherwise when the PojoProcessor tries to automatically
     * inject values into the list, it will get an index out of bounds error if the instance at an index is being called and prior
     * instances at indices before that one are not being instantiated. So changing the code below will cause adding lines to break
     * if you add more than one item to the list.
     *
     * @see org.kuali.rice.krad.document.FinancialDocument#getTargetAccountingLine(int)
     */
    public PurApAccountingLine getBaselineSourceAccountingLine(int index) {
        while (getBaselineSourceAccountingLines().size() <= index) {
            PurApAccountingLine newAccount = getNewAccount();
            getBaselineSourceAccountingLines().add(newAccount);
        }
        return getBaselineSourceAccountingLines().get(index);
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

    @Override
    public abstract Class getAccountingLineClass();

    @Override
    public abstract Class getUseTaxClass();

    @Override
    public void resetAccount() {
        // add a blank accounting line
        PurApAccountingLine purApAccountingLine = getNewAccount();

        purApAccountingLine.setItemIdentifier(this.itemIdentifier);
        purApAccountingLine.setPurapItem(this);
        purApAccountingLine.setSequenceNumber(0);
        setNewSourceLine(purApAccountingLine);
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#buildListOfDeletionAwareLists()
     */

    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = new ArrayList();

        managedLists.add(getSourceAccountingLines());

        return managedLists;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.itemIdentifier != null) {
            m.put("requisitionItemIdentifier", this.itemIdentifier.toString());
        }
        return m;
    }

    @Override
    public PurApAccountingLine getNewSourceLine() {
        return newSourceLine;
    }

    @Override
    public void setNewSourceLine(PurApAccountingLine newAccountingLine) {
        this.newSourceLine = newAccountingLine;
    }

    @Override
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    @Override
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    @Override
    public List<PurApItemUseTax> getUseTaxItems() {
        return useTaxItems;
    }

    @Override
    public void setUseTaxItems(List<PurApItemUseTax> useTaxItems) {
        this.useTaxItems = useTaxItems;
    }

    @Override
    public KualiDecimal getItemQuantity() {
        return itemQuantity;
    }

    @Override
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

    @Override
    public PurApSummaryItem getSummaryItem() {
        PurApSummaryItem summaryItem = new PurApSummaryItem();
        ObjectPopulationUtils.populateFromBaseClass(PurApItemBase.class, this, summaryItem, new HashMap());
        summaryItem.getItemType().setItemTypeDescription(this.itemType.getItemTypeDescription());
        return summaryItem;
    }

    @Override
    public final <T extends PurchasingAccountsPayableDocument> T getPurapDocument() {
        return (T) purapDocument;
    }

    @Override
    public final void setPurapDocument(PurchasingAccountsPayableDocument purapDoc) {
        this.purapDocument = purapDoc;
    }

    /**
     * fixes item references on accounts
     *
     * @see org.kuali.kfs.module.purap.businessobject.PurApItem#fixAccountReferences()
     */
    @Override
    public void fixAccountReferences() {
        if (ObjectUtils.isNull(this.getItemIdentifier())) {
            for (PurApAccountingLine account : this.getSourceAccountingLines()) {
                account.setSequenceNumber(0);
                account.setPurapItem(this);
            }
        }
    }

    @Override
    public void refreshNonUpdateableReferences() {
        PurchasingAccountsPayableDocument document = null;
        PurchasingAccountsPayableDocument tempDocument = getPurapDocument();
        if (tempDocument != null) {
            Integer tempDocumentIdentifier = tempDocument.getPurapDocumentIdentifier();
            if (tempDocumentIdentifier != null) {
                document = this.getPurapDocument();
            }
        }
        super.refreshNonUpdateableReferences();
        if (ObjectUtils.isNotNull(document)) {
            this.setPurapDocument(document);
        }
    }

    @Override
    public KualiDecimal getTotalRemitAmount() {
        if (!purapDocument.isUseTaxIndicator()) {
            return this.getTotalAmount();
        }
        return this.getExtendedPrice();
    }

    @Override
    public String toString() {
        return "Line "+(itemLineNumber==null?"(null)":itemLineNumber.toString())+": ["+itemTypeCode+"] " +
                "Unit:"+(itemUnitPrice==null?"(null)":itemUnitPrice.toString())+" " +
                "Tax:"+(itemSalesTaxAmount==null?"(null)":itemSalesTaxAmount.toString())+" " +
                "*"+itemDescription+"*";
    }

}
