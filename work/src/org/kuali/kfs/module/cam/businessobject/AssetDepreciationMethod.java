package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetDepreciationMethod extends PersistableBusinessObjectBase {

	private String depreciationMethodCode;
	private String depreciationMethodName;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public AssetDepreciationMethod() {

	}

	/**
	 * Gets the depreciationMethodCode attribute.
	 * 
	 * @return Returns the depreciationMethodCode
	 * 
	 */
	public String getDepreciationMethodCode() { 
		return depreciationMethodCode;
	}

	/**
	 * Sets the depreciationMethodCode attribute.
	 * 
	 * @param depreciationMethodCode The depreciationMethodCode to set.
	 * 
	 */
	public void setDepreciationMethodCode(String depreciationMethodCode) {
		this.depreciationMethodCode = depreciationMethodCode;
	}


	/**
	 * Gets the depreciationMethodName attribute.
	 * 
	 * @return Returns the depreciationMethodName
	 * 
	 */
	public String getDepreciationMethodName() { 
		return depreciationMethodName;
	}

	/**
	 * Sets the depreciationMethodName attribute.
	 * 
	 * @param depreciationMethodName The depreciationMethodName to set.
	 * 
	 */
	public void setDepreciationMethodName(String depreciationMethodName) {
		this.depreciationMethodName = depreciationMethodName;
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
        m.put("depreciationMethodCode", this.depreciationMethodCode);
	    return m;
    }
}
