package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetStatus extends PersistableBusinessObjectBase {

	private String inventoryStatusCode;
	private String inventoryStatusName;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public AssetStatus() {

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
	 * Gets the inventoryStatusName attribute.
	 * 
	 * @return Returns the inventoryStatusName
	 * 
	 */
	public String getInventoryStatusName() { 
		return inventoryStatusName;
	}

	/**
	 * Sets the inventoryStatusName attribute.
	 * 
	 * @param inventoryStatusName The inventoryStatusName to set.
	 * 
	 */
	public void setInventoryStatusName(String inventoryStatusName) {
		this.inventoryStatusName = inventoryStatusName;
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
        m.put("inventoryStatusCode", this.inventoryStatusCode);
	    return m;
    }
}
