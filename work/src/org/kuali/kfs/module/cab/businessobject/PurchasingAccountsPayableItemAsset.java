package org.kuali.kfs.module.cab.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchasingAccountsPayableItemAsset extends PersistableBusinessObjectBase implements Comparable<PurchasingAccountsPayableItemAsset> {

    private String documentNumber;
    private Integer accountsPayableLineItemIdentifier;
    private Integer capitalAssetBuilderLineNumber;
    private String accountsPayableLineItemDescription;
    private KualiDecimal accountsPayableItemQuantity;
    private String capitalAssetManagementDocumentNumber;
    private boolean active;

    private PurchasingAccountsPayableDocument purchasingAccountsPayableDocument;
    private List<PurchasingAccountsPayableLineAssetAccount> purchasingAccountsPayableLineAssetAccounts;

    // non persistent fields
    private Integer itemLineNumber;
    private String capitalAssetTransactionTypeCode;
    private boolean additionalChargeNonTradeInIndicator;
    private boolean tradeInAllowance;
    private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal unitCost;
    private KualiDecimal totalCost;
    private String firstFincialObjectCode;
    private KualiDecimal splitQty;
    private boolean selectedValue;
    private String itemTypeCode;
    // used to control "create asset" and "apply payment" button display
    private boolean createAssetIndicator;
    private boolean applyPaymentIndicator;

    public PurchasingAccountsPayableItemAsset() {
        this.purchasingAccountsPayableLineAssetAccounts = new TypedArrayList(PurchasingAccountsPayableLineAssetAccount.class);
        this.selectedValue = false;
        this.createAssetIndicator = false;
        this.applyPaymentIndicator = false;
    }

    public PurchasingAccountsPayableItemAsset(PurchasingAccountsPayableItemAsset initialItemAsset) {
        this.documentNumber = initialItemAsset.documentNumber;
        this.accountsPayableLineItemIdentifier = initialItemAsset.getAccountsPayableLineItemIdentifier();
        this.accountsPayableLineItemDescription = initialItemAsset.getAccountsPayableLineItemDescription();
        this.itemLineNumber = initialItemAsset.getItemLineNumber();
        this.firstFincialObjectCode = initialItemAsset.getFirstFincialObjectCode();
        this.active = true;
        this.tradeInAllowance = initialItemAsset.isTradeInAllowance();
        this.itemAssignedToTradeInIndicator = initialItemAsset.isItemAssignedToTradeInIndicator();
        this.purchasingAccountsPayableLineAssetAccounts = new TypedArrayList(PurchasingAccountsPayableLineAssetAccount.class);
        this.selectedValue = false;
        this.createAssetIndicator = initialItemAsset.isCreateAssetIndicator();
        this.applyPaymentIndicator = initialItemAsset.isApplyPaymentIndicator();
    }

    

    /**
     * Gets the createAssetIndicator attribute. 
     * @return Returns the createAssetIndicator.
     */
    public boolean isCreateAssetIndicator() {
        return createAssetIndicator;
    }

    /**
     * Sets the createAssetIndicator attribute value.
     * @param createAssetIndicator The createAssetIndicator to set.
     */
    public void setCreateAssetIndicator(boolean createAssetIndicator) {
        this.createAssetIndicator = createAssetIndicator;
    }

    /**
     * Gets the applyPaymentIndicator attribute. 
     * @return Returns the applyPaymentIndicator.
     */
    public boolean isApplyPaymentIndicator() {
        return applyPaymentIndicator;
    }

    /**
     * Sets the applyPaymentIndicator attribute value.
     * @param applyPaymentIndicator The applyPaymentIndicator to set.
     */
    public void setApplyPaymentIndicator(boolean applyPaymentIndicator) {
        this.applyPaymentIndicator = applyPaymentIndicator;
    }

    /**
     * Gets the selectedValue attribute.
     * 
     * @return Returns the selectedValue.
     */
    public boolean isSelectedValue() {
        return selectedValue;
    }

    /**
     * Sets the selectedValue attribute value.
     * 
     * @param selectedValue The selectedValue to set.
     */
    public void setSelectedValue(boolean selectedValue) {
        this.selectedValue = selectedValue;
    }

    /**
     * Gets the itemTypeCode attribute.
     * 
     * @return Returns the itemTypeCode.
     */
    public String getItemTypeCode() {
        return itemTypeCode;
    }

    /**
     * Sets the itemTypeCode attribute value.
     * 
     * @param itemTypeCode The itemTypeCode to set.
     */
    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    /**
     * Gets the capitalAssetTransactionTypeCode attribute.
     * 
     * @return Returns the capitalAssetTransactionTypeCode.
     */
    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    /**
     * Sets the capitalAssetTransactionTypeCode attribute value.
     * 
     * @param capitalAssetTransactionTypeCode The capitalAssetTransactionTypeCode to set.
     */
    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    /**
     * Gets the additionalChargeNonTradeInIndicator attribute.
     * 
     * @return Returns the additionalChargeNonTradeInIndicator.
     */
    public boolean isAdditionalChargeNonTradeInIndicator() {
        return additionalChargeNonTradeInIndicator;
    }

    /**
     * Sets the additionalChargeNonTradeInIndicator attribute value.
     * 
     * @param additionalChargeNonTradeInIndicator The additionalChargeNonTradeInIndicator to set.
     */
    public void setAdditionalChargeNonTradeInIndicator(boolean additionalChargeNonTradeInIndicator) {
        this.additionalChargeNonTradeInIndicator = additionalChargeNonTradeInIndicator;
    }

    /**
     * Gets the tradeInAllowance attribute.
     * 
     * @return Returns the tradeInAllowance.
     */
    public boolean isTradeInAllowance() {
        return tradeInAllowance;
    }

    /**
     * Sets the tradeInAllowance attribute value.
     * 
     * @param tradeInAllowance The tradeInAllowance to set.
     */
    public void setTradeInAllowance(boolean tradeInAllowance) {
        this.tradeInAllowance = tradeInAllowance;
    }

    /**
     * Gets the splitQty attribute.
     * 
     * @return Returns the splitQty.
     */
    public KualiDecimal getSplitQty() {
        return splitQty;
    }

    /**
     * Sets the splitQty attribute value.
     * 
     * @param splitQty The splitQty to set.
     */
    public void setSplitQty(KualiDecimal splitQty) {
        this.splitQty = splitQty;
    }


    /**
     * Gets the purchasingAccountsPayableLineAssetAccounts attribute.
     * 
     * @return Returns the purchasingAccountsPayableLineAssetAccounts.
     */
    public List<PurchasingAccountsPayableLineAssetAccount> getPurchasingAccountsPayableLineAssetAccounts() {
        return purchasingAccountsPayableLineAssetAccounts;
    }

    /**
     * Sets the purchasingAccountsPayableLineAssetAccounts attribute value.
     * 
     * @param purchasingAccountsPayableLineAssetAccounts The purchasingAccountsPayableLineAssetAccounts to set.
     */
    public void setPurchasingAccountsPayableLineAssetAccounts(List<PurchasingAccountsPayableLineAssetAccount> purchasingAccountsPayableLineAssetAccounts) {
        this.purchasingAccountsPayableLineAssetAccounts = purchasingAccountsPayableLineAssetAccounts;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the accountsPayableLineItemIdentifier attribute.
     * 
     * @return Returns the accountsPayableLineItemIdentifier.
     */
    public Integer getAccountsPayableLineItemIdentifier() {
        return accountsPayableLineItemIdentifier;
    }

    /**
     * Sets the accountsPayableLineItemIdentifier attribute value.
     * 
     * @param accountsPayableLineItemIdentifier The accountsPayableLineItemIdentifier to set.
     */
    public void setAccountsPayableLineItemIdentifier(Integer accountsPayableLineItemIdentifier) {
        this.accountsPayableLineItemIdentifier = accountsPayableLineItemIdentifier;
    }

    /**
     * Gets the accountsPayableLineItemDescription attribute.
     * 
     * @return Returns the accountsPayableLineItemDescription.
     */
    public String getAccountsPayableLineItemDescription() {
        return accountsPayableLineItemDescription;
    }

    /**
     * Sets the accountsPayableLineItemDescription attribute value.
     * 
     * @param accountsPayableLineItemDescription The accountsPayableLineItemDescription to set.
     */
    public void setAccountsPayableLineItemDescription(String accountsPayableLineItemDescription) {
        this.accountsPayableLineItemDescription = accountsPayableLineItemDescription;
    }

    /**
     * Gets the accountsPayableItemQuantity attribute.
     * 
     * @return Returns the accountsPayableItemQuantity.
     */
    public KualiDecimal getAccountsPayableItemQuantity() {
        return accountsPayableItemQuantity;
    }

    /**
     * Sets the accountsPayableItemQuantity attribute value.
     * 
     * @param accountsPayableItemQuantity The accountsPayableItemQuantity to set.
     */
    public void setAccountsPayableItemQuantity(KualiDecimal accountsPayableItemQuantity) {
        this.accountsPayableItemQuantity = accountsPayableItemQuantity;
    }

    /**
     * Gets the capitalAssetBuilderLineNumber attribute.
     * 
     * @return Returns the capitalAssetBuilderLineNumber
     */
    public Integer getCapitalAssetBuilderLineNumber() {
        return capitalAssetBuilderLineNumber;
    }

    /**
     * Sets the capitalAssetBuilderLineNumber attribute.
     * 
     * @param capitalAssetBuilderLineNumber The capitalAssetBuilderLineNumber to set.
     */
    public void setCapitalAssetBuilderLineNumber(Integer capitalAssetBuilderLineNumber) {
        this.capitalAssetBuilderLineNumber = capitalAssetBuilderLineNumber;
    }


    /**
     * Gets the capitalAssetManagementDocumentNumber attribute.
     * 
     * @return Returns the capitalAssetManagementDocumentNumber
     */
    public String getCapitalAssetManagementDocumentNumber() {
        return capitalAssetManagementDocumentNumber;
    }

    /**
     * Sets the capitalAssetManagementDocumentNumber attribute.
     * 
     * @param capitalAssetManagementDocumentNumber The capitalAssetManagementDocumentNumber to set.
     */
    public void setCapitalAssetManagementDocumentNumber(String capitalAssetManagementDocumentNumber) {
        this.capitalAssetManagementDocumentNumber = capitalAssetManagementDocumentNumber;
    }

    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("accountsPayableLineItemIdentifier", this.accountsPayableLineItemIdentifier);
        m.put("capitalAssetBuilderLineNumber", this.capitalAssetBuilderLineNumber);
        return m;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the purchasingAccountsPayableDocument attribute.
     * 
     * @return Returns the purchasingAccountsPayableDocument.
     */
    public PurchasingAccountsPayableDocument getPurchasingAccountsPayableDocument() {
        return purchasingAccountsPayableDocument;
    }

    /**
     * Sets the purchasingAccountsPayableDocument attribute value.
     * 
     * @param purchasingAccountsPayableDocument The purchasingAccountsPayableDocument to set.
     */
    public void setPurchasingAccountsPayableDocument(PurchasingAccountsPayableDocument purchasingAccountsPayableDocument) {
        this.purchasingAccountsPayableDocument = purchasingAccountsPayableDocument;
    }

    /**
     * Gets the itemAssignedToTradeInIndicator attribute.
     * 
     * @return Returns the itemAssignedToTradeInIndicator.
     */
    public boolean isItemAssignedToTradeInIndicator() {
        return itemAssignedToTradeInIndicator;
    }

    /**
     * Sets the itemAssignedToTradeInIndicator attribute value.
     * 
     * @param itemAssignedToTradeInIndicator The itemAssignedToTradeInIndicator to set.
     */
    public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
        this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
    }

    /**
     * Gets the unitCost attribute.
     * 
     * @return Returns the unitCost.
     */
    public KualiDecimal getUnitCost() {
        return unitCost;
    }

    /**
     * Sets the unitCost attribute value.
     * 
     * @param unitCost The unitCost to set.
     */
    public void setUnitCost(KualiDecimal unitCost) {
        this.unitCost = unitCost;
    }

    /**
     * Gets the totalCost attribute.
     * 
     * @return Returns the totalCost.
     */
    public KualiDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the totalCost attribute value.
     * 
     * @param totalCost The totalCost to set.
     */
    public void setTotalCost(KualiDecimal totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Gets the firstFincialObjectCode attribute.
     * 
     * @return Returns the firstFincialObjectCode.
     */
    public String getFirstFincialObjectCode() {
        return firstFincialObjectCode;
    }

    /**
     * Sets the firstFincialObjectCode attribute value.
     * 
     * @param firstFincialObjectCode The firstFincialObjectCode to set.
     */
    public void setFirstFincialObjectCode(String firstFincialObjectCode) {
        this.firstFincialObjectCode = firstFincialObjectCode;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(PurchasingAccountsPayableItemAsset o) {
        boolean o1ItemTypeBelowTheLine = this.isAdditionalChargeNonTradeInIndicator() | this.isTradeInAllowance();
        boolean o2ItemTypeBelowTheLine = o.isAdditionalChargeNonTradeInIndicator() | o.isTradeInAllowance();
        if (o1ItemTypeBelowTheLine && !o2ItemTypeBelowTheLine) {
            return 1;
        }
        else if (o2ItemTypeBelowTheLine && !o1ItemTypeBelowTheLine) {
            return -1;
        }
        return 0;
    }

}
