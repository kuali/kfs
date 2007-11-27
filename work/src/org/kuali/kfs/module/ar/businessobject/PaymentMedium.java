package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PaymentMedium extends PersistableBusinessObjectBase {

	private String customerPaymentMediumCode;
	private String customerPaymentMediumDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public PaymentMedium() {

	}

	/**
	 * Gets the customerPaymentMediumCode attribute.
	 * 
	 * @return Returns the customerPaymentMediumCode
	 * 
	 */
	public String getCustomerPaymentMediumCode() { 
		return customerPaymentMediumCode;
	}

	/**
	 * Sets the customerPaymentMediumCode attribute.
	 * 
	 * @param customerPaymentMediumCode The customerPaymentMediumCode to set.
	 * 
	 */
	public void setCustomerPaymentMediumCode(String customerPaymentMediumCode) {
		this.customerPaymentMediumCode = customerPaymentMediumCode;
	}


	/**
	 * Gets the customerPaymentMediumDescription attribute.
	 * 
	 * @return Returns the customerPaymentMediumDescription
	 * 
	 */
	public String getCustomerPaymentMediumDescription() { 
		return customerPaymentMediumDescription;
	}

	/**
	 * Sets the customerPaymentMediumDescription attribute.
	 * 
	 * @param customerPaymentMediumDescription The customerPaymentMediumDescription to set.
	 * 
	 */
	public void setCustomerPaymentMediumDescription(String customerPaymentMediumDescription) {
		this.customerPaymentMediumDescription = customerPaymentMediumDescription;
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
        m.put("customerPaymentMediumCode", this.customerPaymentMediumCode);
	    return m;
    }
}
