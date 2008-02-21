package org.kuali.module.cams.bo;

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
    
    /* location - on/off campus (AssetGlobalDetail / CM_MULT_AST_DTL_T) */
    private String leoAssetsToCreate;
    private List<AssetGlobalDetail> assetGlobalDetails;

    /* land information */
    private String landCountyName;
    private String landAcreageSize;
    private String landParcelNumber;

    /* lookup */
    private Chart organizationOwnerChartOfAccountsCodeObject;
    private Account organizationOwnerAccountNumberObject;
    //org. owner
    private Asset agencyNumberObject;
    private Asset acquisitionTypeCodeObject;
    private Asset inventoryStatusCodeObject;
    private Asset conditionCodeObject;
    private Asset capitalAssetTypeCodeObject;
    //asset rep.
    private Asset lastInventoryDateObject;
    private Asset createDateObject;
    private Asset financialDocumentPostingYearObject;
    private Asset financialDocumentPostingPeriodCodeObject;
    private Asset capitalAssetInServiceDateObject;
    //deprec. date
    
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
     * Gets the acquisitionTypeCodeObject attribute. 
     * @return Returns the acquisitionTypeCodeObject.
     */
    public Asset getAcquisitionTypeCodeObject() {
        return acquisitionTypeCodeObject;
    }

    /**
     * Sets the acquisitionTypeCodeObject attribute value.
     * @param acquisitionTypeCodeObject The acquisitionTypeCodeObject to set.
     */
    public void setAcquisitionTypeCodeObject(Asset acquisitionTypeCodeObject) {
        this.acquisitionTypeCodeObject = acquisitionTypeCodeObject;
    }

    /**
     * Gets the agencyNumberObject attribute. 
     * @return Returns the agencyNumberObject.
     */
    public Asset getAgencyNumberObject() {
        return agencyNumberObject;
    }

    /**
     * Sets the agencyNumberObject attribute value.
     * @param agencyNumberObject The agencyNumberObject to set.
     */
    public void setAgencyNumberObject(Asset agencyNumberObject) {
        this.agencyNumberObject = agencyNumberObject;
    }

    /**
     * Gets the organizationOwnerAccountNumberObject attribute. 
     * @return Returns the organizationOwnerAccountNumberObject.
     */
    public Account getOrganizationOwnerAccountNumberObject() {
        return organizationOwnerAccountNumberObject;
    }

    /**
     * Sets the organizationOwnerAccountNumberObject attribute value.
     * @param organizationOwnerAccountNumberObject The organizationOwnerAccountNumberObject to set.
     */
    public void setOrganizationOwnerAccountNumberObject(Account organizationOwnerAccountNumberObject) {
        this.organizationOwnerAccountNumberObject = organizationOwnerAccountNumberObject;
    }

    /**
     * Gets the organizationOwnerChartOfAccountsCodeObject attribute. 
     * @return Returns the organizationOwnerChartOfAccountsCodeObject.
     */
    public Chart getOrganizationOwnerChartOfAccountsCodeObject() {
        return organizationOwnerChartOfAccountsCodeObject;
    }

    /**
     * Sets the organizationOwnerChartOfAccountsCodeObject attribute value.
     * @param organizationOwnerChartOfAccountsCodeObject The organizationOwnerChartOfAccountsCodeObject to set.
     */
    public void setOrganizationOwnerChartOfAccountsCodeObject(Chart organizationOwnerChartOfAccountsCodeObject) {
        this.organizationOwnerChartOfAccountsCodeObject = organizationOwnerChartOfAccountsCodeObject;
    }

    /**
     * Gets the inventoryStatusCodeObject attribute. 
     * @return Returns the inventoryStatusCodeObject.
     */
    public Asset getInventoryStatusCodeObject() {
        return inventoryStatusCodeObject;
    }

    /**
     * Sets the inventoryStatusCodeObject attribute value.
     * @param inventoryStatusCodeObject The inventoryStatusCodeObject to set.
     */
    public void setInventoryStatusCodeObject(Asset inventoryStatusCodeObject) {
        this.inventoryStatusCodeObject = inventoryStatusCodeObject;
    }

    /**
     * Gets the conditionCodeObject attribute. 
     * @return Returns the conditionCodeObject.
     */
    public Asset getConditionCodeObject() {
        return conditionCodeObject;
    }

    /**
     * Sets the conditionCodeObject attribute value.
     * @param conditionCodeObject The conditionCodeObject to set.
     */
    public void setConditionCodeObject(Asset conditionCodeObject) {
        this.conditionCodeObject = conditionCodeObject;
    }
    
    /**
     * Gets the capitalAssetTypeCodeObject attribute. 
     * @return Returns the capitalAssetTypeCodeObject.
     */
    public Asset getCapitalAssetTypeCodeObject() {
        return capitalAssetTypeCodeObject;
    }

    /**
     * Sets the capitalAssetTypeCodeObject attribute value.
     * @param capitalAssetTypeCodeObject The capitalAssetTypeCodeObject to set.
     */
    public void setCapitalAssetTypeCodeObject(Asset capitalAssetTypeCodeObject) {
        this.capitalAssetTypeCodeObject = capitalAssetTypeCodeObject;
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
     * Gets the capitalAssetInServiceDateObject attribute. 
     * @return Returns the capitalAssetInServiceDateObject.
     */
    public Asset getCapitalAssetInServiceDateObject() {
        return capitalAssetInServiceDateObject;
    }

    /**
     * Sets the capitalAssetInServiceDateObject attribute value.
     * @param capitalAssetInServiceDateObject The capitalAssetInServiceDateObject to set.
     */
    public void setCapitalAssetInServiceDateObject(Asset capitalAssetInServiceDateObject) {
        this.capitalAssetInServiceDateObject = capitalAssetInServiceDateObject;
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
     * Gets the createDateObject attribute. 
     * @return Returns the createDateObject.
     */
    public Asset getCreateDateObject() {
        return createDateObject;
    }

    /**
     * Sets the createDateObject attribute value.
     * @param createDateObject The createDateObject to set.
     */
    public void setCreateDateObject(Asset createDateObject) {
        this.createDateObject = createDateObject;
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
     * Gets the financialDocumentPostingPeriodCodeObject attribute. 
     * @return Returns the financialDocumentPostingPeriodCodeObject.
     */
    public Asset getFinancialDocumentPostingPeriodCodeObject() {
        return financialDocumentPostingPeriodCodeObject;
    }

    /**
     * Sets the financialDocumentPostingPeriodCodeObject attribute value.
     * @param financialDocumentPostingPeriodCodeObject The financialDocumentPostingPeriodCodeObject to set.
     */
    public void setFinancialDocumentPostingPeriodCodeObject(Asset financialDocumentPostingPeriodCodeObject) {
        this.financialDocumentPostingPeriodCodeObject = financialDocumentPostingPeriodCodeObject;
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
     * Gets the financialDocumentPostingYearObject attribute. 
     * @return Returns the financialDocumentPostingYearObject.
     */
    public Asset getFinancialDocumentPostingYearObject() {
        return financialDocumentPostingYearObject;
    }

    /**
     * Sets the financialDocumentPostingYearObject attribute value.
     * @param financialDocumentPostingYearObject The financialDocumentPostingYearObject to set.
     */
    public void setFinancialDocumentPostingYearObject(Asset financialDocumentPostingYearObject) {
        this.financialDocumentPostingYearObject = financialDocumentPostingYearObject;
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
     * Gets the lastInventoryDateObject attribute. 
     * @return Returns the lastInventoryDateObject.
     */
    public Asset getLastInventoryDateObject() {
        return lastInventoryDateObject;
    }

    /**
     * Sets the lastInventoryDateObject attribute value.
     * @param lastInventoryDateObject The lastInventoryDateObject to set.
     */
    public void setLastInventoryDateObject(Asset lastInventoryDateObject) {
        this.lastInventoryDateObject = lastInventoryDateObject;
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
}
