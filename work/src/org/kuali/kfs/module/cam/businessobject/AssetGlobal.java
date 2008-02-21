package org.kuali.module.cams.bo;

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
    private String needMoreInfo; // testing only
    
    /* document overview (AssetGlobal / CM_MULT_AST_DOC_T) */
    private String capitalAssetDescription;
    private String capitalAssetTypeCode;
    private String conditionCode;
    
    /* asset detail infomation (Asset / CM_CPTLAST_T) */
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String agencyNumber;
    private String acquisitionTypeCode;
    private String inventoryStatusCode;
  
    
    /* location - on/off campus */
    /* collection (AssetGlobalDetail / CM_MULT_AST_DTL_T) */
    private List<AssetGlobalDetail> assetGlobalDetails;
    /* on campus
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;   
    // off campus
    private String offCampusName;
    private String offCampusAddress;
    private String offCampusCityName;
    private String offCampusStateCode;
    private String offCampusZipCode;
    private String offCampusCountryCode; */

    /* land information */
    private String landCountyName;
    private String landAcreageSize;
    private String landParcelNumber;

    /* lookup */
    private Chart organizationOwnerChartOfAccountsCodeObject;
    private Account organizationOwnerAccountNumberObject;
    private Asset asset;
    private Asset agencyNumberObject;
    private Asset acquisitionTypeCodeObject;
    
    /**
     * Default constructor.
     */
    public AssetGlobal() {
        assetGlobalDetails = new TypedArrayList(AssetGlobalDetail.class);
    }   
    
    /**
     * Gets the needMoreInfo attribute. 
     * @return Returns the needMoreInfo.
     */
    public String getNeedMoreInfo() {
        return needMoreInfo;
    }

    /**
     * Sets the needMoreInfo attribute value.
     * @param needMoreInfo The needMoreInfo to set.
     */
    public void setNeedMoreInfo(String needMoreInfo) {
        this.needMoreInfo = needMoreInfo;
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
            asset.setCapitalAssetDescription(capitalAssetDescription);
            asset.setCapitalAssetTypeCode(capitalAssetTypeCode);
            asset.setConditionCode(conditionCode);

            asset.setCapitalAssetNumber(detail.getCapitalAssetNumber());
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

}
