package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CollectionStatus extends PersistableBusinessObjectBase {

	private String customerCollectionStatusCode;
	private String customerCollectionStatusName;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CollectionStatus() {

	}

	/**
	 * Gets the customerCollectionStatusCode attribute.
	 * 
	 * @return Returns the customerCollectionStatusCode
	 * 
	 */
	public String getCustomerCollectionStatusCode() { 
		return customerCollectionStatusCode;
	}

	/**
	 * Sets the customerCollectionStatusCode attribute.
	 * 
	 * @param customerCollectionStatusCode The customerCollectionStatusCode to set.
	 * 
	 */
	public void setCustomerCollectionStatusCode(String customerCollectionStatusCode) {
		this.customerCollectionStatusCode = customerCollectionStatusCode;
	}


	/**
	 * Gets the customerCollectionStatusName attribute.
	 * 
	 * @return Returns the customerCollectionStatusName
	 * 
	 */
	public String getCustomerCollectionStatusName() { 
		return customerCollectionStatusName;
	}

	/**
	 * Sets the customerCollectionStatusName attribute.
	 * 
	 * @param customerCollectionStatusName The customerCollectionStatusName to set.
	 * 
	 */
	public void setCustomerCollectionStatusName(String customerCollectionStatusName) {
		this.customerCollectionStatusName = customerCollectionStatusName;
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
        m.put("customerCollectionStatusCode", this.customerCollectionStatusCode);
	    return m;
    }
}
