package org.kuali.kfs.module.cab.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.module.cam.businessobject.AssetType;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchasingAccountsPayableItemAsset extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer accountsPayableLineItemIdentifier;
    private Integer capitalAssetBuilderLineNumber;
    private String accountsPayableLineItemDescription;
    private KualiDecimal accountsPayableItemQuantity;
    private String capitalAssetDescription;
    private String capitalAssetTypeCode;
    private String vendorName;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private String capitalAssetManagementDocumentNumber;
    private boolean active;
    
    private PurchasingAccountsPayableDocument purchasingAccountsPayableDocument;
    private AssetType capitalAssetType;
    private List<PurchasingAccountsPayableAssetDetail> purchasingAccountsPayableAssetDetails;
    private List<PurchasingAccountsPayableLineAssetAccount> purchasingAccountsPayableLineAssetAccounts;

    // non persistent fields
    private Integer itemLineNumber;
    private String capitalAssetTransactionTypeCode;
    private boolean additionalChargeNonTradeInIndicator;
    private boolean tradeInIndicator;
    private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal unitCost;
    private String firstFincialObjectCode;


    public boolean isTradeInIndicator() {
        return tradeInIndicator;
    }

    public void setTradeInIndicator(boolean tradeInIndicator) {
        this.tradeInIndicator = tradeInIndicator;
    }

    public boolean isAdditionalChargeNonTradeInIndicator() {
        return additionalChargeNonTradeInIndicator;
    }

    public void setAdditionalChargeNonTradeInIndicator(boolean itemTypeBelowTheLineIndicator) {
        this.additionalChargeNonTradeInIndicator = itemTypeBelowTheLineIndicator;
    }

    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public PurchasingAccountsPayableItemAsset() {
        this.purchasingAccountsPayableAssetDetails = new TypedArrayList(PurchasingAccountsPayableAssetDetail.class);
        this.purchasingAccountsPayableLineAssetAccounts = new TypedArrayList(PurchasingAccountsPayableLineAssetAccount.class);
    }

    public List<PurchasingAccountsPayableLineAssetAccount> getPurchasingAccountsPayableLineAssetAccounts() {
        return purchasingAccountsPayableLineAssetAccounts;
    }

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
     * Gets the purchasingAccountsPayableAssetDetail attribute.
     * 
     * @return Returns the purchasingAccountsPayableAssetDetail.
     */
    public List<PurchasingAccountsPayableAssetDetail> getPurchasingAccountsPayableAssetDetails() {
        return purchasingAccountsPayableAssetDetails;
    }

    /**
     * Sets the purchasingAccountsPayableAssetDetail attribute value.
     * 
     * @param purchasingAccountsPayableAssetDetail The purchasingAccountsPayableAssetDetail to set.
     */
    public void setPurchasingAccountsPayableAssetDetails(List<PurchasingAccountsPayableAssetDetail> purchasingAccountsPayableAssetDetails) {
        this.purchasingAccountsPayableAssetDetails = purchasingAccountsPayableAssetDetails;
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
     * Gets the capitalAssetBuilderQuantity attribute.
     * 
     * @return Returns the capitalAssetBuilderQuantity
     */
    public KualiDecimal getCapitalAssetBuilderQuantity() {
        return accountsPayableItemQuantity;
    }

    /**
     * Sets the capitalAssetBuilderQuantity attribute.
     * 
     * @param capitalAssetBuilderQuantity The capitalAssetBuilderQuantity to set.
     */
    public void setCapitalAssetBuilderQuantity(KualiDecimal capitalAssetBuilderQuantity) {
        this.accountsPayableItemQuantity = capitalAssetBuilderQuantity;
    }

    /**
     * Gets the capitalAssetDescription attribute.
     * 
     * @return Returns the capitalAssetDescription
     */
    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    /**
     * Sets the capitalAssetDescription attribute.
     * 
     * @param capitalAssetDescription The capitalAssetDescription to set.
     */
    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }

    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     */
    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    /**
     * Sets the capitalAssetTypeCode attribute.
     * 
     * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
     */
    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }

    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Gets the manufacturerName attribute.
     * 
     * @return Returns the manufacturerName
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the manufacturerName attribute.
     * 
     * @param manufacturerName The manufacturerName to set.
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * Gets the manufacturerModelNumber attribute.
     * 
     * @return Returns the manufacturerModelNumber
     */
    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    /**
     * Sets the manufacturerModelNumber attribute.
     * 
     * @param manufacturerModelNumber The manufacturerModelNumber to set.
     */
    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
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

    /**
     * Gets the capitalAssetType attribute.
     * 
     * @return Returns the capitalAssetType
     */
    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    /**
     * Sets the capitalAssetType attribute.
     * 
     * @param capitalAssetType The capitalAssetType to set.
     * @deprecated
     */
    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }

    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
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


    public PurchasingAccountsPayableDocument getPurchasingAccountsPayableDocument() {
        return purchasingAccountsPayableDocument;
    }

    public void setPurchasingAccountsPayableDocument(PurchasingAccountsPayableDocument purchasingAccountsPayableDocument) {
        this.purchasingAccountsPayableDocument = purchasingAccountsPayableDocument;
    }

    public void setUnitCost(KualiDecimal unitCost) {
        this.unitCost = unitCost;
    }
    
    public KualiDecimal getUnitCost(){
        return this.unitCost;
    }

    public void setFirstFincialObjectCode(String firstFincialObjectCode) {
        this.firstFincialObjectCode = firstFincialObjectCode;
    }
    
    public String getFirstFincialObjectCode() {
        return this.firstFincialObjectCode;
    }

    
    public boolean isItemAssignedToTradeInIndicator() {
        return itemAssignedToTradeInIndicator;
    }

    public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
        this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
    }
}
