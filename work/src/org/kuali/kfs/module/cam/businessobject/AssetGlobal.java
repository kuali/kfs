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

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private String documentNumber;
    private String capitalAssetDescription;
    private String capitalAssetTypeCode;
    private String conditionCode;

    private AssetType capitalAssetType;
    private AssetCondition condition;
    
    private List<AssetGlobalDetail> assetGlobalDetails;
    
    public List<AssetGlobalDetail> getAssetGlobalDetails() {
        return assetGlobalDetails;
    }

    public void setAssetGlobalDetails(List<AssetGlobalDetail> assetGlobalDetails) {
        this.assetGlobalDetails = assetGlobalDetails;
    }
    
    /**
     * Default constructor.
     */
    public AssetGlobal() {
        assetGlobalDetails = new TypedArrayList(AssetGlobalDetail.class);
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
     * Gets the capitalAssetType attribute.
     * 
     * @return Returns the capitalAssetType
     * 
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
    
    /**
     * Gets the condition attribute. 
     * @return Returns the condition.
     */
    public AssetCondition getCondition() {
        return condition;
    }

    /**
     * Sets the condition attribute value.
     * @param condition The condition to set.
     * @deprecated
     */
    public void setCondition(AssetCondition condition) {
        this.condition = condition;
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
}
