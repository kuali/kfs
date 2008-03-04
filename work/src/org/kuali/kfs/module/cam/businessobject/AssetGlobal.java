package org.kuali.module.cams.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.GlobalBusinessObjectDetail;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    /* misc. */
    private String documentNumber;
    
    /* asset detail infomation (Asset / CM_CPTLAST_T) */
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String leoOrganizationOwner; //org. owner
    private String agencyNumber;
    private String acquisitionTypeCode;
    private String inventoryStatusCode;
    private String conditionCode; // (AssetGlobal / CM_MULT_AST_DOC_T)
    private String capitalAssetDescription; // (AssetGlobal / CM_MULT_AST_DOC_T)
    private String capitalAssetTypeCode; // (AssetGlobal / CM_MULT_AST_DOC_T)
    private String vendorName;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private String organizationText; // (AssetOrganization / CM_AST_ORG_T)
    private String leoAssetRepresentitive; //asset rep.
    private Timestamp lastInventoryDate;
    private Date createDate;
    private int financialDocumentPostingYear;
    private String financialDocumentPostingPeriodCode;
    private Date capitalAssetInServiceDate;
    private Date leoDeprecationDate; //deprec. date
    
    /* unique asset information (Asset / CM_CPTLAST_T) */
    private String leoAssetsToCreate; //nbr of assets to create
    private long capitalAssetNumber; // PK
    private String campusTagNumber;
    private String serialNumber;
    private String leoGovernmentTag; // government tag
    private String leoNationalStockNbr; //national stock number
    private String organizationInventoryName;
    private String organizationAssetTypeIdentifier; // (AssetOrganization / CM_AST_ORG_T)

    /* location - on/off campus (AssetGlobalDetail / CM_MULT_AST_DTL_T) */
    private List<AssetGlobalDetail> assetGlobalDetails; 

    /* land information */
    private String landCountyName;
    private String landAcreageSize;
    private String landParcelNumber;
    
    /* Add Payments (AssetPayment / CM_AST_PAYMENT_T) */
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String projectCode;
    private String organizationReferenceId;
    private String leoDocumentNumber; // dup/exists?
    private String financialDocumentTypeCode;
    private String purchaseOrderNumber;
    private String requisitionNumber;
    private Date financialDocumentPostingDate;
    private int leoFinancialDocumentPostingYear ;  // dup/exists?
    private String leoFinancialDocumentPostingPeriodCode; // dup/exists?
    private BigDecimal accountChargeAmount;
    private BigDecimal leoAddPaymentsTotalCost;

    /* lookup */
    private Chart organizationOwnerChartOfAccounts;
    private Account organizationOwnerAccount;
    //org. owner
    // Commented below out... can't type fields to "Asset" that arn't of object type Asset.
//    private Asset agency;
//    private Asset acquisitionType;
//    private Asset inventoryStatus;
    private AssetCondition condition;
    private AssetType capitalAssetType;
//    //asset rep.
//    private Asset lastInventory;
//    private Asset create;
//    private Asset financialDocumentPostingYr;
//    private Asset financialDocumentPostingPeriod;
//    private Asset capitalAssetInService;
    //deprec. date
    
    private MultipleAssetHeader assetGlobalHeader;
    private List<AssetPaymentDetail> assetPaymentDetails; 
    
    /**
     * Default constructor.
     */
    public AssetGlobal() {
        assetGlobalDetails = new TypedArrayList(AssetGlobalDetail.class);
    }     
    
    /**
     * Gets the leoAssetRepresentitive attribute. 
     * @return Returns the leoAssetRepresentitive.
     */
    public String getLeoAssetRepresentitive() {
        return leoAssetRepresentitive;
    }

    /**
     * Sets the leoAssetRepresentitive attribute value.
     * @param leoAssetRepresentitive The leoAssetRepresentitive to set.
     */
    public void setLeoAssetRepresentitive(String leoAssetRepresentitive) {
        this.leoAssetRepresentitive = leoAssetRepresentitive;
    }

    /**
     * Gets the leoDeprecationDate attribute. 
     * @return Returns the leoDeprecationDate.
     */
    public Date getLeoDeprecationDate() {
        return leoDeprecationDate;
    }

    /**
     * Sets the leoDeprecationDate attribute value.
     * @param leoDeprecationDate The leoDeprecationDate to set.
     */
    public void setLeoDeprecationDate(Date leoDeprecationDate) {
        this.leoDeprecationDate = leoDeprecationDate;
    }

    /**
     * Gets the leoOrganizationOwner attribute. 
     * @return Returns the leoOrganizationOwner.
     */
    public String getLeoOrganizationOwner() {
        return leoOrganizationOwner;
    }

    /**
     * Sets the leoOrganizationOwner attribute value.
     * @param leoOrganizationOwner The leoOrganizationOwner to set.
     */
    public void setLeoOrganizationOwner(String leoOrganizationOwner) {
        this.leoOrganizationOwner = leoOrganizationOwner;
    }

    /**
     * Gets the assetGlobalDetails attribute.
     * 
     * @return Returns the assetGlobalDetails.
     */
    public List<AssetGlobalDetail> getAssetGlobalDetails() {
        return assetGlobalDetails;
    }

    /**
     * Sets the assetGlobalDetails attribute value.
     *
     * @param assetGlobalDetails The assetGlobalDetails to set.
     */
    public void setAssetGlobalDetails(List<AssetGlobalDetail> assetGlobalDetails) {
        this.assetGlobalDetails = assetGlobalDetails;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
  
    /**
     * Gets the capitalAssetDescription attribute.
     * 
     * @return Returns the capitalAssetDescription
     * 
     */
    public String getCapitalAssetDescription() { 
        return capitalAssetDescription;
    }

    /**
     * Sets the capitalAssetDescription attribute.
     * 
     * @param capitalAssetDescription The capitalAssetDescription to set.
     * 
     */
    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }


    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     * 
     */
    public String getCapitalAssetTypeCode() { 
        return capitalAssetTypeCode;
    }

    /**
     * Sets the capitalAssetTypeCode attribute.
     * 
     * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
     * 
     */
    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }


    /**
     * Gets the conditionCode attribute.
     * 
     * @return Returns the conditionCode
     * 
     */
    public String getConditionCode() { 
        return conditionCode;
    }

    /**
     * Sets the conditionCode attribute.
     * 
     * @param conditionCode The conditionCode to set.
     * 
     */
    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    /**
     * @see org.kuali.core.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * This returns a list of Object Codes to Update and/or Add
     * 
     * @see org.kuali.core.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        List<PersistableBusinessObject> persistables = new ArrayList();

        for (AssetGlobalDetail detail : assetGlobalDetails) {

            /** @TODO check into a better way to do the below other then getting / setting a dozen fields -- deepCopy? */

            // Asset never exists since per location we don't look up Asset numbers.
            Asset asset = new Asset();
            asset.setCapitalAssetNumber(detail.getCapitalAssetNumber());
            
            asset.setCapitalAssetDescription(capitalAssetDescription);
            asset.setCapitalAssetTypeCode(capitalAssetTypeCode);
            asset.setConditionCode(conditionCode);

            asset.setCampusCode(detail.getCampusCode());
            asset.setBuildingCode(detail.getBuildingCode());
            asset.setBuildingRoomNumber(detail.getBuildingRoomNumber());
            asset.setBuildingSubRoomNumber(detail.getBuildingSubRoomNumber());
            
            asset.setActive(true);

            persistables.add(asset);
        }

        return persistables;
    }
    
    public boolean isPersistable() {
        return true;
    }
    
    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetGlobalDetails();
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * Gets the acquisitionTypeCode attribute. 
     * @return Returns the acquisitionTypeCode.
     */
    public String getAcquisitionTypeCode() {
        return acquisitionTypeCode;
    }

    /**
     * Sets the acquisitionTypeCode attribute value.
     * @param acquisitionTypeCode The acquisitionTypeCode to set.
     */
    public void setAcquisitionTypeCode(String acquisitionTypeCode) {
        this.acquisitionTypeCode = acquisitionTypeCode;
    }

    /**
     * Gets the agencyNumber attribute. 
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute value.
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the inventoryStatusCode attribute. 
     * @return Returns the inventoryStatusCode.
     */
    public String getInventoryStatusCode() {
        return inventoryStatusCode;
    }

    /**
     * Sets the inventoryStatusCode attribute value.
     * @param inventoryStatusCode The inventoryStatusCode to set.
     */
    public void setInventoryStatusCode(String inventoryStatusCode) {
        this.inventoryStatusCode = inventoryStatusCode;
    }

    /**
     * Gets the landAcreageSize attribute. 
     * @return Returns the landAcreageSize.
     */
    public String getLandAcreageSize() {
        return landAcreageSize;
    }

    /**
     * Sets the landAcreageSize attribute value.
     * @param landAcreageSize The landAcreageSize to set.
     */
    public void setLandAcreageSize(String landAcreageSize) {
        this.landAcreageSize = landAcreageSize;
    }

    /**
     * Gets the landCountyName attribute. 
     * @return Returns the landCountyName.
     */
    public String getLandCountyName() {
        return landCountyName;
    }

    /**
     * Sets the landCountyName attribute value.
     * @param landCountyName The landCountyName to set.
     */
    public void setLandCountyName(String landCountyName) {
        this.landCountyName = landCountyName;
    }

    /**
     * Gets the landParcelNumber attribute. 
     * @return Returns the landParcelNumber.
     */
    public String getLandParcelNumber() {
        return landParcelNumber;
    }

    /**
     * Sets the landParcelNumber attribute value.
     * @param landParcelNumber The landParcelNumber to set.
     */
    public void setLandParcelNumber(String landParcelNumber) {
        this.landParcelNumber = landParcelNumber;
    }

    /**
     * Gets the organizationOwnerAccountNumber attribute. 
     * @return Returns the organizationOwnerAccountNumber.
     */
    public String getOrganizationOwnerAccountNumber() {
        return organizationOwnerAccountNumber;
    }

    /**
     * Sets the organizationOwnerAccountNumber attribute value.
     * @param organizationOwnerAccountNumber The organizationOwnerAccountNumber to set.
     */
    public void setOrganizationOwnerAccountNumber(String organizationOwnerAccountNumber) {
        this.organizationOwnerAccountNumber = organizationOwnerAccountNumber;
    }

    /**
     * Gets the organizationOwnerChartOfAccountsCode attribute. 
     * @return Returns the organizationOwnerChartOfAccountsCode.
     */
    public String getOrganizationOwnerChartOfAccountsCode() {
        return organizationOwnerChartOfAccountsCode;
    }

    /**
     * Sets the organizationOwnerChartOfAccountsCode attribute value.
     * @param organizationOwnerChartOfAccountsCode The organizationOwnerChartOfAccountsCode to set.
     */
    public void setOrganizationOwnerChartOfAccountsCode(String organizationOwnerChartOfAccountsCode) {
        this.organizationOwnerChartOfAccountsCode = organizationOwnerChartOfAccountsCode;
    }

    /**
     * Gets the organizationOwnerAccount attribute. 
     * @return Returns the organizationOwnerAccount.
     */
    public Account getOrganizationOwnerAccount() {
        return organizationOwnerAccount;
    }

    /**
     * Sets the organizationOwnerAccount attribute value.
     * @param organizationOwnerAccount The organizationOwnerAccount to set.
     */
    public void setOrganizationOwnerAccount(Account organizationOwnerAccountNumberObject) {
        this.organizationOwnerAccount = organizationOwnerAccountNumberObject;
    }

    /**
     * Gets the organizationOwnerChartOfAccounts attribute. 
     * @return Returns the organizationOwnerChartOfAccounts.
     */
    public Chart getOrganizationOwnerChartOfAccounts() {
        return organizationOwnerChartOfAccounts;
    }

    /**
     * Sets the organizationOwnerChartOfAccounts attribute value.
     * @param organizationOwnerChartOfAccounts The organizationOwnerChartOfAccounts to set.
     */
    public void setOrganizationOwnerChartOfAccounts(Chart organizationOwnerChartOfAccountsCodeObject) {
        this.organizationOwnerChartOfAccounts = organizationOwnerChartOfAccountsCodeObject;
    }

    /**
     * Gets the manufacturerModelNumber attribute. 
     * @return Returns the manufacturerModelNumber.
     */
    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    /**
     * Sets the manufacturerModelNumber attribute value.
     * @param manufacturerModelNumber The manufacturerModelNumber to set.
     */
    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }

    /**
     * Gets the manufacturerName attribute. 
     * @return Returns the manufacturerName.
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the manufacturerName attribute value.
     * @param manufacturerName The manufacturerName to set.
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * Gets the organizationText attribute. 
     * @return Returns the organizationText.
     */
    public String getOrganizationText() {
        return organizationText;
    }

    /**
     * Sets the organizationText attribute value.
     * @param organizationText The organizationText to set.
     */
    public void setOrganizationText(String organizationText) {
        this.organizationText = organizationText;
    }

    /**
     * Gets the vendorName attribute. 
     * @return Returns the vendorName.
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute value.
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Gets the capitalAssetInServiceDate attribute. 
     * @return Returns the capitalAssetInServiceDate.
     */
    public Date getCapitalAssetInServiceDate() {
        return capitalAssetInServiceDate;
    }

    /**
     * Sets the capitalAssetInServiceDate attribute value.
     * @param capitalAssetInServiceDate The capitalAssetInServiceDate to set.
     */
    public void setCapitalAssetInServiceDate(Date capitalAssetInServiceDate) {
        this.capitalAssetInServiceDate = capitalAssetInServiceDate;
    }

    /**
     * Gets the createDate attribute. 
     * @return Returns the createDate.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the createDate attribute value.
     * @param createDate The createDate to set.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the financialDocumentPostingPeriodCode attribute. 
     * @return Returns the financialDocumentPostingPeriodCode.
     */
    public String getFinancialDocumentPostingPeriodCode() {
        return financialDocumentPostingPeriodCode;
    }

    /**
     * Sets the financialDocumentPostingPeriodCode attribute value.
     * @param financialDocumentPostingPeriodCode The financialDocumentPostingPeriodCode to set.
     */
    public void setFinancialDocumentPostingPeriodCode(String financialDocumentPostingPeriodCode) {
        this.financialDocumentPostingPeriodCode = financialDocumentPostingPeriodCode;
    }

    /**
     * Gets the financialDocumentPostingYear attribute. 
     * @return Returns the financialDocumentPostingYear.
     */
    public int getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }

    /**
     * Sets the financialDocumentPostingYear attribute value.
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(int financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }

    /**
     * Gets the lastInventoryDate attribute. 
     * @return Returns the lastInventoryDate.
     */
    public Timestamp getLastInventoryDate() {
        return lastInventoryDate;
    }

    /**
     * Sets the lastInventoryDate attribute value.
     * @param lastInventoryDate The lastInventoryDate to set.
     */
    public void setLastInventoryDate(Timestamp lastInventoryDate) {
        this.lastInventoryDate = lastInventoryDate;
    }

    /**
     * Gets the leoAssetsToCreate attribute. 
     * @return Returns the leoAssetsToCreate.
     */
    public String getLeoAssetsToCreate() {
        return leoAssetsToCreate;
    }

    /**
     * Sets the leoAssetsToCreate attribute value.
     * @param leoAssetsToCreate The leoAssetsToCreate to set.
     */
    public void setLeoAssetsToCreate(String leoAssetsToCreate) {
        this.leoAssetsToCreate = leoAssetsToCreate;
    }

    /**
     * Gets the campusTagNumber attribute. 
     * @return Returns the campusTagNumber.
     */
    public String getCampusTagNumber() {
        return campusTagNumber;
    }

    /**
     * Sets the campusTagNumber attribute value.
     * @param campusTagNumber The campusTagNumber to set.
     */
    public void setCampusTagNumber(String campusTagNumber) {
        this.campusTagNumber = campusTagNumber;
    }

    /**
     * Gets the capitalAssetNumber attribute. 
     * @return Returns the capitalAssetNumber.
     */
    public long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute value.
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Gets the leoGovernmentTag attribute. 
     * @return Returns the leoGovernmentTag.
     */
    public String getLeoGovernmentTag() {
        return leoGovernmentTag;
    }

    /**
     * Sets the leoGovernmentTag attribute value.
     * @param leoGovernmentTag The leoGovernmentTag to set.
     */
    public void setLeoGovernmentTag(String leoGovernmentTag) {
        this.leoGovernmentTag = leoGovernmentTag;
    }

    /**
     * Gets the leoNationalStockNbr attribute. 
     * @return Returns the leoNationalStockNbr.
     */
    public String getLeoNationalStockNbr() {
        return leoNationalStockNbr;
    }

    /**
     * Sets the leoNationalStockNbr attribute value.
     * @param leoNationalStockNbr The leoNationalStockNbr to set.
     */
    public void setLeoNationalStockNbr(String leoNationalStockNbr) {
        this.leoNationalStockNbr = leoNationalStockNbr;
    }

    /**
     * Gets the organizationAssetTypeIdentifier attribute. 
     * @return Returns the organizationAssetTypeIdentifier.
     */
    public String getOrganizationAssetTypeIdentifier() {
        return organizationAssetTypeIdentifier;
    }

    /**
     * Sets the organizationAssetTypeIdentifier attribute value.
     * @param organizationAssetTypeIdentifier The organizationAssetTypeIdentifier to set.
     */
    public void setOrganizationAssetTypeIdentifier(String organizationAssetTypeIdentifier) {
        this.organizationAssetTypeIdentifier = organizationAssetTypeIdentifier;
    }

    /**
     * Gets the organizationInventoryName attribute. 
     * @return Returns the organizationInventoryName.
     */
    public String getOrganizationInventoryName() {
        return organizationInventoryName;
    }

    /**
     * Sets the organizationInventoryName attribute value.
     * @param organizationInventoryName The organizationInventoryName to set.
     */
    public void setOrganizationInventoryName(String organizationInventoryName) {
        this.organizationInventoryName = organizationInventoryName;
    }

    /**
     * Gets the serialNumber attribute. 
     * @return Returns the serialNumber.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serialNumber attribute value.
     * @param serialNumber The serialNumber to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountChargeAmount attribute. 
     * @return Returns the accountChargeAmount.
     */
    public BigDecimal getAccountChargeAmount() {
        return accountChargeAmount;
    }

    /**
     * Sets the accountChargeAmount attribute value.
     * @param accountChargeAmount The accountChargeAmount to set.
     */
    public void setAccountChargeAmount(BigDecimal accountChargeAmount) {
        this.accountChargeAmount = accountChargeAmount;
    }

    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the financialDocumentPostingDate attribute. 
     * @return Returns the financialDocumentPostingDate.
     */
    public Date getFinancialDocumentPostingDate() {
        return financialDocumentPostingDate;
    }

    /**
     * Sets the financialDocumentPostingDate attribute value.
     * @param financialDocumentPostingDate The financialDocumentPostingDate to set.
     */
    public void setFinancialDocumentPostingDate(Date financialDocumentPostingDate) {
        this.financialDocumentPostingDate = financialDocumentPostingDate;
    }

    /**
     * Gets the financialDocumentTypeCode attribute. 
     * @return Returns the financialDocumentTypeCode.
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute value.
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the financialObjectCode attribute. 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute. 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the leoAddPaymentsTotalCost attribute. 
     * @return Returns the leoAddPaymentsTotalCost.
     */
    public BigDecimal getLeoAddPaymentsTotalCost() {
        return leoAddPaymentsTotalCost;
    }

    /**
     * Sets the leoAddPaymentsTotalCost attribute value.
     * @param leoAddPaymentsTotalCost The leoAddPaymentsTotalCost to set.
     */
    public void setLeoAddPaymentsTotalCost(BigDecimal leoAddPaymentsTotalCost) {
        this.leoAddPaymentsTotalCost = leoAddPaymentsTotalCost;
    }

    /**
     * Gets the leoDocumentNumber attribute. 
     * @return Returns the leoDocumentNumber.
     */
    public String getLeoDocumentNumber() {
        return leoDocumentNumber;
    }

    /**
     * Sets the leoDocumentNumber attribute value.
     * @param leoDocumentNumber The leoDocumentNumber to set.
     */
    public void setLeoDocumentNumber(String leoDocumentNumber) {
        this.leoDocumentNumber = leoDocumentNumber;
    }

    /**
     * Gets the leoFinancialDocumentPostingPeriodCode attribute. 
     * @return Returns the leoFinancialDocumentPostingPeriodCode.
     */
    public String getLeoFinancialDocumentPostingPeriodCode() {
        return leoFinancialDocumentPostingPeriodCode;
    }

    /**
     * Sets the leoFinancialDocumentPostingPeriodCode attribute value.
     * @param leoFinancialDocumentPostingPeriodCode The leoFinancialDocumentPostingPeriodCode to set.
     */
    public void setLeoFinancialDocumentPostingPeriodCode(String leoFinancialDocumentPostingPeriodCode) {
        this.leoFinancialDocumentPostingPeriodCode = leoFinancialDocumentPostingPeriodCode;
    }

    /**
     * Gets the leoFinancialDocumentPostingYear attribute. 
     * @return Returns the leoFinancialDocumentPostingYear.
     */
    public int getLeoFinancialDocumentPostingYear() {
        return leoFinancialDocumentPostingYear;
    }

    /**
     * Sets the leoFinancialDocumentPostingYear attribute value.
     * @param leoFinancialDocumentPostingYear The leoFinancialDocumentPostingYear to set.
     */
    public void setLeoFinancialDocumentPostingYear(int leoFinancialDocumentPostingYear) {
        this.leoFinancialDocumentPostingYear = leoFinancialDocumentPostingYear;
    }

    /**
     * Gets the organizationReferenceId attribute. 
     * @return Returns the organizationReferenceId.
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * Sets the organizationReferenceId attribute value.
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * Gets the projectCode attribute. 
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute value.
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the purchaseOrderNumber attribute. 
     * @return Returns the purchaseOrderNumber.
     */
    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    /**
     * Sets the purchaseOrderNumber attribute value.
     * @param purchaseOrderNumber The purchaseOrderNumber to set.
     */
    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    /**
     * Gets the requisitionNumber attribute. 
     * @return Returns the requisitionNumber.
     */
    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    /**
     * Sets the requisitionNumber attribute value.
     * @param requisitionNumber The requisitionNumber to set.
     */
    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    /**
     * Gets the subAccountNumber attribute. 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the assetGlobalHeader attribute. 
     * @return Returns the assetGlobalHeader.
     */
    public MultipleAssetHeader getAssetGlobalHeader() {
        return assetGlobalHeader;
    }

    /**
     * Sets the assetGlobalHeader attribute value.
     * @param assetGlobalHeader The assetGlobalHeader to set.
     */
    public void setAssetGlobalHeader(MultipleAssetHeader assetGlobalHeader) {
        this.assetGlobalHeader = assetGlobalHeader;
    }

    /**
     * Gets the assetPaymentDetails attribute. 
     * @return Returns the assetPaymentDetails.
     */
    public List<AssetPaymentDetail> getAssetPaymentDetails() {
        return assetPaymentDetails;
    }

    /**
     * Sets the assetPaymentDetails attribute value.
     * @param assetPaymentDetails The assetPaymentDetails to set.
     */
    public void setAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentDetails) {
        this.assetPaymentDetails = assetPaymentDetails;
    }
}
