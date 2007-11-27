package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerProcessingType extends PersistableBusinessObjectBase {

	private String customerSpecialProcessingCode;
	private String customerSpecialProcessingDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CustomerProcessingType() {

	}

	/**
	 * Gets the customerSpecialProcessingCode attribute.
	 * 
	 * @return Returns the customerSpecialProcessingCode
	 * 
	 */
	public String getCustomerSpecialProcessingCode() { 
		return customerSpecialProcessingCode;
	}

	/**
	 * Sets the customerSpecialProcessingCode attribute.
	 * 
	 * @param customerSpecialProcessingCode The customerSpecialProcessingCode to set.
	 * 
	 */
	public void setCustomerSpecialProcessingCode(String customerSpecialProcessingCode) {
		this.customerSpecialProcessingCode = customerSpecialProcessingCode;
	}


	/**
	 * Gets the customerSpecialProcessingDescription attribute.
	 * 
	 * @return Returns the customerSpecialProcessingDescription
	 * 
	 */
	public String getCustomerSpecialProcessingDescription() { 
		return customerSpecialProcessingDescription;
	}

	/**
	 * Sets the customerSpecialProcessingDescription attribute.
	 * 
	 * @param customerSpecialProcessingDescription The customerSpecialProcessingDescription to set.
	 * 
	 */
	public void setCustomerSpecialProcessingDescription(String customerSpecialProcessingDescription) {
		this.customerSpecialProcessingDescription = customerSpecialProcessingDescription;
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
        m.put("customerSpecialProcessingCode", this.customerSpecialProcessingCode);
	    return m;
    }
}
