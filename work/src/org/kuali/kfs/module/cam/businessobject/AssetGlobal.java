package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.GlobalBusinessObjectDetail;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private String documentNumber;
    private String acquisitionTypeCode;
    private String capitalAssetDescription;
    private String inventoryStatusCode;
    private String conditionCode;
    private String primaryDepreciationMethodCode;
    private KualiDecimal primaryDepreciationBaseAmount;
    private String acquisitionSourceName;
    private String capitalAssetTypeCode;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private Date capitalizationDate;
    private KualiDecimal totalCostAmount;
    private String landCountyName;
    private Integer landAcreageSize;
    private String landParcelNumber;
    private String vendorName;
    private String organizationText;
    private Date createDate;
    private Date capitalAssetInServiceDate;
    private Date capitalAssetDepreciationDate;
    private String representativeUniversalIdentifier;
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String agencyNumber;
    private Integer financialDocumentNextLineNumber;    
    
    // Not Presisted
    private Date lastInventoryDate;
    private AssetHeader assetHeader;
    private AssetType capitalAssetType;
    private AssetCondition assetCondition;
    private AssetStatus inventoryStatus;
    private List<AssetGlobalDetail> assetGlobalDetails;
    private List<AssetGlobalDetail> assetSharedDetails;
    private List<AssetPaymentDetail> assetPaymentDetails;
    private AssetAcquisitionType acquisitionType;
    private Chart organizationOwnerChartOfAccounts;
    private Account organizationOwnerAccount;

    /**
     * Default constructor.
     */
    public AssetGlobal() {
        assetGlobalDetails = new TypedArrayList(AssetGlobalDetail.class);
        assetSharedDetails = new TypedArrayList(AssetGlobalDetail.class);
        assetPaymentDetails = new TypedArrayList(AssetPaymentDetail.class);
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
     * Gets the acquisitionTypeCode attribute.
     * 
     * @return Returns the acquisitionTypeCode
     */
    public String getAcquisitionTypeCode() {
        return acquisitionTypeCode;
    }

    /**
     * Sets the acquisitionTypeCode attribute.
     * 
     * @param acquisitionTypeCode The acquisitionTypeCode to set.
     */
    public void setAcquisitionTypeCode(String acquisitionTypeCode) {
        this.acquisitionTypeCode = acquisitionTypeCode;
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
     * Gets the inventoryStatusCode attribute.
     * 
     * @return Returns the inventoryStatusCode
     */
    public String getInventoryStatusCode() {
        return inventoryStatusCode;
    }

    /**
     * Sets the inventoryStatusCode attribute.
     * 
     * @param inventoryStatusCode The inventoryStatusCode to set.
     */
    public void setInventoryStatusCode(String inventoryStatusCode) {
        this.inventoryStatusCode = inventoryStatusCode;
    }


    /**
     * Gets the conditionCode attribute.
     * 
     * @return Returns the conditionCode
     */
    public String getConditionCode() {
        return conditionCode;
    }

    /**
     * Sets the conditionCode attribute.
     * 
     * @param conditionCode The conditionCode to set.
     */
    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }


    /**
     * Gets the primaryDepreciationMethodCode attribute.
     * 
     * @return Returns the primaryDepreciationMethodCode
     */
    public String getPrimaryDepreciationMethodCode() {
        return primaryDepreciationMethodCode;
    }

    /**
     * Sets the primaryDepreciationMethodCode attribute.
     * 
     * @param primaryDepreciationMethodCode The primaryDepreciationMethodCode to set.
     */
    public void setPrimaryDepreciationMethodCode(String primaryDepreciationMethodCode) {
        this.primaryDepreciationMethodCode = primaryDepreciationMethodCode;
    }


    /**
     * Gets the primaryDepreciationBaseAmount attribute.
     * 
     * @return Returns the primaryDepreciationBaseAmount
     */
    public KualiDecimal getPrimaryDepreciationBaseAmount() {
        return primaryDepreciationBaseAmount;
    }

    /**
     * Sets the primaryDepreciationBaseAmount attribute.
     * 
     * @param primaryDepreciationBaseAmount The primaryDepreciationBaseAmount to set.
     */
    public void setPrimaryDepreciationBaseAmount(KualiDecimal primaryDepreciationBaseAmount) {
        this.primaryDepreciationBaseAmount = primaryDepreciationBaseAmount;
    }


    /**
     * Gets the acquisitionSourceName attribute.
     * 
     * @return Returns the acquisitionSourceName
     */
    public String getAcquisitionSourceName() {
        return acquisitionSourceName;
    }

    /**
     * Sets the acquisitionSourceName attribute.
     * 
     * @param acquisitionSourceName The acquisitionSourceName to set.
     */
    public void setAcquisitionSourceName(String acquisitionSourceName) {
        this.acquisitionSourceName = acquisitionSourceName;
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
     * Gets the capitalizationDate attribute.
     * 
     * @return Returns the capitalizationDate
     */
    public Date getCapitalizationDate() {
        return capitalizationDate;
    }

    /**
     * Sets the capitalizationDate attribute.
     * 
     * @param capitalizationDate The capitalizationDate to set.
     */
    public void setCapitalizationDate(Date capitalizationDate) {
        this.capitalizationDate = capitalizationDate;
    }


    /**
     * Gets the totalCostAmount attribute.
     * 
     * @return Returns the totalCostAmount
     */
    public KualiDecimal getTotalCostAmount() {
        return totalCostAmount;
    }

    /**
     * Sets the totalCostAmount attribute.
     * 
     * @param totalCostAmount The totalCostAmount to set.
     */
    public void setTotalCostAmount(KualiDecimal totalCostAmount) {
        this.totalCostAmount = totalCostAmount;
    }


    /**
     * Gets the landCountyName attribute.
     * 
     * @return Returns the landCountyName
     */
    public String getLandCountyName() {
        return landCountyName;
    }

    /**
     * Sets the landCountyName attribute.
     * 
     * @param landCountyName The landCountyName to set.
     */
    public void setLandCountyName(String landCountyName) {
        this.landCountyName = landCountyName;
    }


    /**
     * Gets the landAcreageSize attribute.
     * 
     * @return Returns the landAcreageSize
     */
    public Integer getLandAcreageSize() {
        return landAcreageSize;
    }

    /**
     * Sets the landAcreageSize attribute.
     * 
     * @param landAcreageSize The landAcreageSize to set.
     */
    public void setLandAcreageSize(Integer landAcreageSize) {
        this.landAcreageSize = landAcreageSize;
    }


    /**
     * Gets the landParcelNumber attribute.
     * 
     * @return Returns the landParcelNumber
     */
    public String getLandParcelNumber() {
        return landParcelNumber;
    }

    /**
     * Sets the landParcelNumber attribute.
     * 
     * @param landParcelNumber The landParcelNumber to set.
     */
    public void setLandParcelNumber(String landParcelNumber) {
        this.landParcelNumber = landParcelNumber;
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
     * Gets the organizationText attribute.
     * 
     * @return Returns the organizationText
     */
    public String getOrganizationText() {
        return organizationText;
    }

    /**
     * Sets the organizationText attribute.
     * 
     * @param organizationText The organizationText to set.
     */
    public void setOrganizationText(String organizationText) {
        this.organizationText = organizationText;
    }


    /**
     * Gets the createDate attribute.
     * 
     * @return Returns the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the createDate attribute.
     * 
     * @param createDate The createDate to set.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    /**
     * Gets the capitalAssetInServiceDate attribute.
     * 
     * @return Returns the capitalAssetInServiceDate
     */
    public Date getCapitalAssetInServiceDate() {
        return capitalAssetInServiceDate;
    }

    /**
     * Sets the capitalAssetInServiceDate attribute.
     * 
     * @param capitalAssetInServiceDate The capitalAssetInServiceDate to set.
     */
    public void setCapitalAssetInServiceDate(Date capitalAssetInServiceDate) {
        this.capitalAssetInServiceDate = capitalAssetInServiceDate;
    }


    /**
     * Gets the capitalAssetDepreciationDate attribute.
     * 
     * @return Returns the capitalAssetDepreciationDate
     */
    public Date getCapitalAssetDepreciationDate() {
        return capitalAssetDepreciationDate;
    }

    /**
     * Sets the capitalAssetDepreciationDate attribute.
     * 
     * @param capitalAssetDepreciationDate The capitalAssetDepreciationDate to set.
     */
    public void setCapitalAssetDepreciationDate(Date capitalAssetDepreciationDate) {
        this.capitalAssetDepreciationDate = capitalAssetDepreciationDate;
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
     * Gets the representativeUniversalIdentifier attribute. 
     * @return Returns the representativeUniversalIdentifier.
     */
    public String getRepresentativeUniversalIdentifier() {
        return representativeUniversalIdentifier;
    }

    /**
     * Sets the representativeUniversalIdentifier attribute value.
     * @param representativeUniversalIdentifier The representativeUniversalIdentifier to set.
     */
    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
    }

    /**
     * Gets the assetCondition attribute.
     * 
     * @return Returns the assetCondition.
     */
    public AssetCondition getAssetCondition() {
        return assetCondition;
    }

    /**
     * Sets the assetCondition attribute value.
     * 
     * @param assetCondition The assetCondition to set.
     * @deprecated
     */
    public void setAssetCondition(AssetCondition assetCondition) {
        this.assetCondition = assetCondition;
    }

    /**
     * Gets the capitalAssetType attribute.
     * 
     * @return Returns the capitalAssetType.
     */
    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    /**
     * Sets the capitalAssetType attribute value.
     * 
     * @param capitalAssetType The capitalAssetType to set.
     * @deprecated
     */
    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }

    /**
     * Gets the inventoryStatus attribute.
     * 
     * @return Returns the inventoryStatus.
     */
    public AssetStatus getInventoryStatus() {
        return inventoryStatus;
    }

    /**
     * Sets the inventoryStatus attribute value.
     * 
     * @param inventoryStatus The inventoryStatus to set.
     * @deprecated
     */
    public void setInventoryStatus(AssetStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
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
     * @deprecated
     */
    public void setOrganizationOwnerAccount(Account organizationOwnerAccount) {
        this.organizationOwnerAccount = organizationOwnerAccount;
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
     * @deprecated
     */
    public void setOrganizationOwnerChartOfAccounts(Chart organizationOwnerChartOfAccounts) {
        this.organizationOwnerChartOfAccounts = organizationOwnerChartOfAccounts;
    }

    /**
     * Gets the assetHeader attribute.
     * 
     * @return Returns the assetHeader.
     */
    public AssetHeader getAssetHeader() {
        return assetHeader;
    }

    /**
     * Sets the assetHeader attribute value.
     * 
     * @param assetHeader The assetHeader to set.
     */
    public void setAssetHeader(AssetHeader assetHeader) {
        this.assetHeader = assetHeader;
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
     * Gets the assetPaymentDetails attribute.
     * 
     * @return Returns the assetPaymentDetails.
     */
    public List<AssetPaymentDetail> getAssetPaymentDetails() {
        return assetPaymentDetails;
    }

    /**
     * Sets the assetPaymentDetails attribute value.
     * 
     * @param assetPaymentDetails The assetPaymentDetails to set.
     */
    public void setAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentDetails) {
        this.assetPaymentDetails = assetPaymentDetails;
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
     * @see org.kuali.core.bo.GlobalBusinessObject#generateGlobalChangesToPersist() becomes an asset
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        for (AssetGlobalDetail detail : assetGlobalDetails) {

            /** @TODO check into a better way to do the below other then getting / setting a dozen fields -- deepCopy? */

            // Asset never exists since per location we don't look up Asset numbers.
            Asset asset = new Asset();
            // AssetPayment assetPayment = new AssetPayment();
            // newAssetGlobalDetail = (AssetGlobalDetail) ObjectUtils.deepCopy(locationDetail);
            asset.setCapitalAssetNumber(detail.getCapitalAssetNumber());
            asset.setCapitalAssetDescription(capitalAssetDescription);
            asset.setCapitalAssetTypeCode(capitalAssetTypeCode);
            asset.setConditionCode(conditionCode);
            asset.setCampusCode(detail.getCampusCode());
            asset.setBuildingCode(detail.getBuildingCode());
            asset.setBuildingRoomNumber(detail.getBuildingRoomNumber());
            asset.setBuildingSubRoomNumber(detail.getBuildingSubRoomNumber());
            asset.setActive(true);
            // asset.setVersionNumber(1L);
            persistables.add(asset);
        }

        for (AssetPaymentDetail payment : assetPaymentDetails) {
            for (AssetGlobalDetail location : assetGlobalDetails) {
                // Distribute Asset Payments from AssetPaymentDetails to AssetPayment
                // Divide each payment to records in Asset AssetGlobalDetails
                AssetPayment assetPayment = new AssetPayment();
                assetPayment.setCapitalAssetNumber(location.getCapitalAssetNumber());
                assetPayment.setPaymentSequenceNumber(payment.getFinancialDocumentLineNumber());
                assetPayment.setChartOfAccountsCode(payment.getChartOfAccountsCode());
                assetPayment.setAccountNumber(payment.getAccountNumber());
                assetPayment.setSubAccountNumber(payment.getSubAccountNumber());
                assetPayment.setFinancialObjectCode(payment.getFinancialObjectCode());
                assetPayment.setFinancialSubObjectCode(payment.getFinancialSubObjectCode());
                assetPayment.setProjectCode(payment.getProjectCode());
                assetPayment.setOrganizationReferenceId(payment.getOrganizationReferenceId());
                assetPayment.setDocumentNumber(payment.getDocumentNumber()); // ???
                // ??? assetPayment.setFinancialDocumentTypeCode(detail.getFinancialDocumentTypeCode());
                assetPayment.setPurchaseOrderNumber(payment.getPurchaseOrderNumber());
                assetPayment.setRequisitionNumber(payment.getRequisitionNumber());
                assetPayment.setFinancialDocumentPostingYear(payment.getFinancialDocumentPostingYear());
                assetPayment.setFinancialDocumentPostingPeriodCode(payment.getFinancialDocumentPostingPeriodCode());
                // assetPayment.setAccountChargeAmount(payment.getAccountChargeAmount().divide(new
                // KualiDecimal(assetGlobalDetails.size())));
                assetPayment.setAccountChargeAmount(payment.getAmount().divide(new KualiDecimal(assetGlobalDetails.size())));
                // assetPayment.setVersionNumber(1L);
                persistables.add(assetPayment);
            }
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
     * Gets the lastInventoryDate attribute.
     * 
     * @return Returns the lastInventoryDate.
     */
    public Date getLastInventoryDate() {
        return lastInventoryDate;
    }

    /**
     * Sets the lastInventoryDate attribute value.
     * 
     * @param lastInventoryDate The lastInventoryDate to set.
     */
    public void setLastInventoryDate(Date lastInventoryDate) {
        this.lastInventoryDate = lastInventoryDate;
    }


    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        assetHeader.setDocumentNumber(documentNumber);
        super.beforeUpdate(persistenceBroker);
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        assetHeader.setDocumentNumber(documentNumber);
        super.beforeInsert(persistenceBroker);
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    /**
     * Gets the financialDocumentNextLineNumber attribute.
     * 
     * @return Returns the financialDocumentNextLineNumber.
     */
    public Integer getFinancialDocumentNextLineNumber() {
        return financialDocumentNextLineNumber;
    }

    /**
     * Sets the financialDocumentNextLineNumber attribute value.
     * 
     * @param financialDocumentNextLineNumber The financialDocumentNextLineNumber to set.
     */
    public void setFinancialDocumentNextLineNumber(Integer financialDocumentNextLineNumber) {
        this.financialDocumentNextLineNumber = financialDocumentNextLineNumber;
    }

    public Integer incrementFinancialDocumentLineNumber() {
        if (this.financialDocumentNextLineNumber == null) {
            this.financialDocumentNextLineNumber = 0;
        }
        this.financialDocumentNextLineNumber += 1;
        return financialDocumentNextLineNumber;
    }

    public List<AssetGlobalDetail> getAssetSharedDetails() {
        return assetSharedDetails;
    }

    public void setAssetSharedDetails(List<AssetGlobalDetail> assetSharedDetails) {
        this.assetSharedDetails = assetSharedDetails;
    }

    public AssetAcquisitionType getAcquisitionType() {
        return acquisitionType;
    }

    public void setAcquisitionType(AssetAcquisitionType acquisitionType) {
        this.acquisitionType = acquisitionType;
    }


}
