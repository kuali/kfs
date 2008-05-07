package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class CustomerType extends PersistableBusinessObjectBase implements Inactivateable {

	private String customerTypeCode;
	private String customerTypeDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CustomerType() {

	}

	/**
	 * Gets the customerTypeCode attribute.
	 * 
	 * @return Returns the customerTypeCode
	 * 
	 */
	public String getCustomerTypeCode() { 
		return customerTypeCode;
	}

	/**
	 * Sets the customerTypeCode attribute.
	 * 
	 * @param customerTypeCode The customerTypeCode to set.
	 * 
	 */
	public void setCustomerTypeCode(String customerTypeCode) {
		this.customerTypeCode = customerTypeCode;
	}


	/**
	 * Gets the customerTypeDescription attribute.
	 * 
	 * @return Returns the customerTypeDescription
	 * 
	 */
	public String getCustomerTypeDescription() { 
		return customerTypeDescription;
	}

	/**
	 * Sets the customerTypeDescription attribute.
	 * 
	 * @param customerTypeDescription The customerTypeDescription to set.
	 * 
	 */
	public void setCustomerTypeDescription(String customerTypeDescription) {
		this.customerTypeDescription = customerTypeDescription;
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
        m.put("customerTypeCode", this.customerTypeCode);
	    return m;
    }
}
