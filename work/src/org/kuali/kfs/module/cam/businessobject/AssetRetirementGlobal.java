package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobal extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Long mergedTargetCapitalAssetNumber;
    private String inventoryStatusCode;
    private String retirementReasonCode;
    private Date retirementDate;
    private Asset mergedTargetCapitalAsset;
    private AssetRetirementReason retirementReason;
    private AssetStatus inventoryStatus;
    private DocumentHeader documentHeader;
    private List<AssetRetirementGlobalDetail> retiredAssetDetails;

    /**
     * Default constructor.
     */
    public AssetRetirementGlobal() {
        this.retiredAssetDetails = new TypedArrayList(AssetRetirementGlobalDetail.class);

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @return Returns the mergedTargetCapitalAssetNumber
     * 
     */
    public Long getMergedTargetCapitalAssetNumber() {
        return mergedTargetCapitalAssetNumber;
    }

    /**
     * Sets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @param mergedTargetCapitalAssetNumber The mergedTargetCapitalAssetNumber to set.
     * 
     */
    public void setMergedTargetCapitalAssetNumber(Long mergedTargetCapitalAssetNumber) {
        this.mergedTargetCapitalAssetNumber = mergedTargetCapitalAssetNumber;
    }


    /**
     * Gets the inventoryStatusCode attribute.
     * 
     * @return Returns the inventoryStatusCode
     * 
     */
    public String getInventoryStatusCode() {
        return inventoryStatusCode;
    }

    /**
     * Sets the inventoryStatusCode attribute.
     * 
     * @param inventoryStatusCode The inventoryStatusCode to set.
     * 
     */
    public void setInventoryStatusCode(String inventoryStatusCode) {
        this.inventoryStatusCode = inventoryStatusCode;
    }


    /**
     * Gets the retirementReasonCode attribute.
     * 
     * @return Returns the retirementReasonCode
     * 
     */
    public String getRetirementReasonCode() {
        return retirementReasonCode;
    }

    /**
     * Sets the retirementReasonCode attribute.
     * 
     * @param retirementReasonCode The retirementReasonCode to set.
     * 
     */
    public void setRetirementReasonCode(String retirementReasonCode) {
        this.retirementReasonCode = retirementReasonCode;
    }


    /**
     * Gets the retirementDate attribute.
     * 
     * @return Returns the retirementDate
     * 
     */
    public Date getRetirementDate() {
        return retirementDate;
    }

    /**
     * Sets the retirementDate attribute.
     * 
     * @param retirementDate The retirementDate to set.
     * 
     */
    public void setRetirementDate(Date retirementDate) {
        this.retirementDate = retirementDate;
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

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public List<AssetRetirementGlobalDetail> getRetiredAssetDetails() {
        return retiredAssetDetails;
    }

    public void setRetiredAssetDetails(List<AssetRetirementGlobalDetail> retiredAssetDetails) {
        this.retiredAssetDetails = retiredAssetDetails;
    }

}
