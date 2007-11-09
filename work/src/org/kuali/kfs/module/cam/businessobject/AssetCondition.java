package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetCondition extends PersistableBusinessObjectBase {

	private String assetConditionCode;
	private String assetConditionName;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public AssetCondition() {

	}

	/**
	 * Gets the assetConditionCode attribute.
	 * 
	 * @return Returns the assetConditionCode
	 * 
	 */
	public String getAssetConditionCode() { 
		return assetConditionCode;
	}

	/**
	 * Sets the assetConditionCode attribute.
	 * 
	 * @param assetConditionCode The assetConditionCode to set.
	 * 
	 */
	public void setAssetConditionCode(String assetConditionCode) {
		this.assetConditionCode = assetConditionCode;
	}


	/**
	 * Gets the assetConditionName attribute.
	 * 
	 * @return Returns the assetConditionName
	 * 
	 */
	public String getAssetConditionName() { 
		return assetConditionName;
	}

	/**
	 * Sets the assetConditionName attribute.
	 * 
	 * @param assetConditionName The assetConditionName to set.
	 * 
	 */
	public void setAssetConditionName(String assetConditionName) {
		this.assetConditionName = assetConditionName;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("assetConditionCode", this.assetConditionCode);
	    return m;
    }
}
