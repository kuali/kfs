package org.kuali.module.cams.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.GlobalBusinessObjectDetail;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private String documentNumber;
    private boolean active;
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
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
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

            Map pk = new HashMap();
            pk.put("CPTLAST_NBR", detail.getCapitalAssetNumber());
            
            /** @TODO check into a better way to do the below other then getting / setting a dozen fields -- deepCopy? */
            
            Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, pk);
            if (ObjectUtils.isNull(asset)) {
                // Asset doesn't exist yet, create and set our values
                asset = new Asset();
                asset.setCapitalAssetDescription(capitalAssetDescription);
                asset.setCapitalAssetTypeCode(capitalAssetTypeCode);
                asset.setConditionCode(conditionCode);

                asset.setCapitalAssetNumber(detail.getCapitalAssetNumber());
                asset.setCampusCode(detail.getCampusCode());
                asset.setBuildingCode(detail.getBuildingCode());
                asset.setBuildingRoomNumber(detail.getBuildingRoomNumber());
                asset.setBuildingSubRoomNumber(detail.getBuildingSubRoomNumber());
                
                asset.setActive(true);
            } else {
                // Asset exists, overwrite our values only if they are provided
                asset.setCapitalAssetDescription(update(capitalAssetDescription, asset.getCapitalAssetDescription()));
                asset.setCapitalAssetTypeCode(update(capitalAssetTypeCode, asset.getCapitalAssetTypeCode()));
                asset.setConditionCode(update(conditionCode, asset.getConditionCode()));

                asset.setCapitalAssetNumber(update(asset.getCapitalAssetNumber(), detail.getCapitalAssetNumber()));
                asset.setCampusCode(update(asset.getCampusCode(), detail.getCampusCode()));
                asset.setBuildingCode(update(asset.getBuildingCode(), detail.getBuildingCode()));
                asset.setBuildingRoomNumber(update(asset.getBuildingRoomNumber(), detail.getBuildingRoomNumber()));
                asset.setBuildingSubRoomNumber(update(asset.getBuildingSubRoomNumber(), detail.getBuildingSubRoomNumber()));
                
                asset.setActive(active);
            }

            persistables.add(asset);
        }

        return persistables;
    }
    
    /** @TODO this is taken from ObjectCodeGlobal -- should it be centralized? ObjectUtils? */
    private String update(String newValue, String oldValue) {
        if (newValue == null || newValue.length() == 0) {
            return oldValue;
        }
        return newValue;
    }
    
    /** @TODO See above comment. Same thing but this one doesn't exist in ObjectUtils. */
    private Long update(Long newValue, Long oldValue) {
        if (newValue == null) {
            return oldValue;
        }
        return newValue;
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
