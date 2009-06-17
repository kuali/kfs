package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetLocationType extends PersistableBusinessObjectBase implements Inactivateable {

    private String assetLocationTypeCode;
    private String assetLocationTypeName;
    private boolean active;

    /**
     * Default constructor.
     */
    public AssetLocationType() {

    }

    /**
     * Gets the assetLocationTypeCode attribute.
     * 
     * @return Returns the assetLocationTypeCode
     */
    public String getAssetLocationTypeCode() {
        return assetLocationTypeCode;
    }

    /**
     * Sets the assetLocationTypeCode attribute.
     * 
     * @param assetLocationTypeCode The assetLocationTypeCode to set.
     */
    public void setAssetLocationTypeCode(String assetLocationTypeCode) {
        this.assetLocationTypeCode = assetLocationTypeCode;
    }


    /**
     * Gets the assetLocationTypeName attribute.
     * 
     * @return Returns the assetLocationTypeName
     */
    public String getAssetLocationTypeName() {
        return assetLocationTypeName;
    }

    /**
     * Sets the assetLocationTypeName attribute.
     * 
     * @param assetLocationTypeName The assetLocationTypeName to set.
     */
    public void setAssetLocationTypeName(String assetLocationTypeName) {
        this.assetLocationTypeName = assetLocationTypeName;
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("assetLocationTypeCode", this.assetLocationTypeCode);
        return m;
    }

}
