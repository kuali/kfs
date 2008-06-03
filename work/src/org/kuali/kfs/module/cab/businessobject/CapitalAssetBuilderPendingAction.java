package org.kuali.module.capitalAssetBuilder.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CapitalAssetBuilderPendingAction extends PersistableBusinessObjectBase {

	private String actionCode;
	private String actionDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CapitalAssetBuilderPendingAction() {

	}

	/**
	 * Gets the actionCode attribute.
	 * 
	 * @return Returns the actionCode
	 * 
	 */
	public String getActionCode() { 
		return actionCode;
	}

	/**
	 * Sets the actionCode attribute.
	 * 
	 * @param actionCode The actionCode to set.
	 * 
	 */
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}


	/**
	 * Gets the actionDescription attribute.
	 * 
	 * @return Returns the actionDescription
	 * 
	 */
	public String getActionDescription() { 
		return actionDescription;
	}

	/**
	 * Sets the actionDescription attribute.
	 * 
	 * @param actionDescription The actionDescription to set.
	 * 
	 */
	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
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
        m.put("actionCode", this.actionCode);
	    return m;
    }
}
