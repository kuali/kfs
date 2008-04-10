package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.GlobalBusinessObjectDetail;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private String documentNumber;
    private Long mergedTargetCapitalAssetNumber;
    private String inventoryStatusCode;
    private String retirementReasonCode;
    private Date retirementDate;
    private Asset mergedTargetCapitalAsset;
    private AssetRetirementReason retirementReason;
    private AssetStatus inventoryStatus;
    private DocumentHeader documentHeader;
    private List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails;
    // non-persistent relation
    private AssetRetirementGlobalDetail sharedRetirementInfo;

    private Date defaultRetirementDate;


    public AssetRetirementGlobalDetail getSharedRetirementInfo() {
        return sharedRetirementInfo;
    }


    public void setSharedRetirementInfo(AssetRetirementGlobalDetail sharedRetirementInfo) {
        this.sharedRetirementInfo = sharedRetirementInfo;
    }


    /**
     * Default constructor.
     */
    public AssetRetirementGlobal() {
        this.assetRetirementGlobalDetails = new TypedArrayList(AssetRetirementGlobalDetail.class);
    }


    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * @see org.kuali.core.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        List<PersistableBusinessObject> persistables = new ArrayList();

        for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
            // load the object by key
            Map pkMap = new HashMap();
            pkMap.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, detail.getCapitalAssetNumber());

            Asset asset = (Asset) boService.findByPrimaryKey(Asset.class, pkMap);

            asset.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED);
            asset.setRetirementReasonCode(retirementReasonCode);

            if (CamsConstants.AssetRetirementReasonCode.THEFT.equalsIgnoreCase(retirementReasonCode) && StringUtils.isNotBlank(sharedRetirementInfo.getPaidCaseNumber())) {
                asset.setCampusPoliceDepartmentCaseNumber(sharedRetirementInfo.getPaidCaseNumber());
            }

            persistables.add(asset);
        }

        return persistables;
    }

    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetRetirementGlobalDetails();
    }

    public boolean isPersistable() {
        // TODO Auto-generated method stub
        return true;
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
     * Gets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @return Returns the mergedTargetCapitalAssetNumber
     */
    public Long getMergedTargetCapitalAssetNumber() {
        return mergedTargetCapitalAssetNumber;
    }

    /**
     * Sets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @param mergedTargetCapitalAssetNumber The mergedTargetCapitalAssetNumber to set.
     */
    public void setMergedTargetCapitalAssetNumber(Long mergedTargetCapitalAssetNumber) {
        this.mergedTargetCapitalAssetNumber = mergedTargetCapitalAssetNumber;
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
     * Gets the retirementReasonCode attribute.
     * 
     * @return Returns the retirementReasonCode
     */
    public String getRetirementReasonCode() {
        return retirementReasonCode;
    }

    /**
     * Sets the retirementReasonCode attribute.
     * 
     * @param retirementReasonCode The retirementReasonCode to set.
     */
    public void setRetirementReasonCode(String retirementReasonCode) {
        this.retirementReasonCode = retirementReasonCode;
    }


    /**
     * Gets the retirementDate attribute.
     * 
     * @return Returns the retirementDate
     */
    public Date getRetirementDate() {
        return retirementDate;
    }

    /**
     * Sets the retirementDate attribute.
     * 
     * @param retirementDate The retirementDate to set.
     */
    public void setRetirementDate(Date remeretirementDatentDate) {
        this.retirementDate = remeretirementDatentDate;
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
     * Gets the mergedTargetCapitalAsset attribute.
     * 
     * @return Returns the mergedTargetCapitalAsset.
     */
    public Asset getMergedTargetCapitalAsset() {
        return mergedTargetCapitalAsset;
    }

    /**
     * Sets the mergedTargetCapitalAsset attribute value.
     * 
     * @param mergedTargetCapitalAsset The mergedTargetCapitalAsset to set.
     * @deprecated
     */
    public void setMergedTargetCapitalAsset(Asset mergedTargetCapitalAsset) {
        this.mergedTargetCapitalAsset = mergedTargetCapitalAsset;
    }

    /**
     * Gets the retirementReason attribute.
     * 
     * @return Returns the retirementReason.
     */
    public AssetRetirementReason getRetirementReason() {
        return retirementReason;
    }

    /**
     * Sets the retirementReason attribute value.
     * 
     * @param retirementReason The retirementReason to set.
     * @deprecated
     */
    public void setRetirementReason(AssetRetirementReason retirementReason) {
        this.retirementReason = retirementReason;
    }


    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public List<AssetRetirementGlobalDetail> getAssetRetirementGlobalDetails() {
        return assetRetirementGlobalDetails;
    }

    public void setAssetRetirementGlobalDetails(List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails) {
        this.assetRetirementGlobalDetails = assetRetirementGlobalDetails;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }


    public Date getDefaultRetirementDate() {
        if (this.retirementDate == null) {
            this.retirementDate = DateUtils.convertToSqlDate(new java.util.Date());
            this.defaultRetirementDate = this.retirementDate;
        }
        return retirementDate;
    }


    public void setDefaultRetirementDate(Date formattedRetirementDate) {
        this.retirementDate = formattedRetirementDate;
        this.defaultRetirementDate = formattedRetirementDate;
    }


}
