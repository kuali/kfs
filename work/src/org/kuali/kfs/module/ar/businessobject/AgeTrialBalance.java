package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AgeTrialBalance extends PersistableBusinessObjectBase {

	private String personUniversalIdentifier;
	private String customerNumber;
	private String customerName;
	private KualiDecimal currentInvoiceAmount;
	private KualiDecimal over30InvoiceAmount;
	private KualiDecimal over60InvoiceAmount;
	private KualiDecimal over90InvoiceAmount;

    private Customer customer;
    
	/**
	 * Default constructor.
	 */
	public AgeTrialBalance() {

	}

	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return Returns the personUniversalIdentifier
	 * 
	 */
	public String getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
	}


	/**
	 * Gets the customerNumber attribute.
	 * 
	 * @return Returns the customerNumber
	 * 
	 */
	public String getCustomerNumber() { 
		return customerNumber;
	}

	/**
	 * Sets the customerNumber attribute.
	 * 
	 * @param customerNumber The customerNumber to set.
	 * 
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}


	/**
	 * Gets the customerName attribute.
	 * 
	 * @return Returns the customerName
	 * 
	 */
	public String getCustomerName() { 
		return customerName;
	}

	/**
	 * Sets the customerName attribute.
	 * 
	 * @param customerName The customerName to set.
	 * 
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	/**
	 * Gets the currentInvoiceAmount attribute.
	 * 
	 * @return Returns the currentInvoiceAmount
	 * 
	 */
	public KualiDecimal getCurrentInvoiceAmount() { 
		return currentInvoiceAmount;
	}

	/**
	 * Sets the currentInvoiceAmount attribute.
	 * 
	 * @param currentInvoiceAmount The currentInvoiceAmount to set.
	 * 
	 */
	public void setCurrentInvoiceAmount(KualiDecimal currentInvoiceAmount) {
		this.currentInvoiceAmount = currentInvoiceAmount;
	}


	/**
	 * Gets the over30InvoiceAmount attribute.
	 * 
	 * @return Returns the over30InvoiceAmount
	 * 
	 */
	public KualiDecimal getOver30InvoiceAmount() { 
		return over30InvoiceAmount;
	}

	/**
	 * Sets the over30InvoiceAmount attribute.
	 * 
	 * @param over30InvoiceAmount The over30InvoiceAmount to set.
	 * 
	 */
	public void setOver30InvoiceAmount(KualiDecimal over30InvoiceAmount) {
		this.over30InvoiceAmount = over30InvoiceAmount;
	}


	/**
	 * Gets the over60InvoiceAmount attribute.
	 * 
	 * @return Returns the over60InvoiceAmount
	 * 
	 */
	public KualiDecimal getOver60InvoiceAmount() { 
		return over60InvoiceAmount;
	}

	/**
	 * Sets the over60InvoiceAmount attribute.
	 * 
	 * @param over60InvoiceAmount The over60InvoiceAmount to set.
	 * 
	 */
	public void setOver60InvoiceAmount(KualiDecimal over60InvoiceAmount) {
		this.over60InvoiceAmount = over60InvoiceAmount;
	}


	/**
	 * Gets the over90InvoiceAmount attribute.
	 * 
	 * @return Returns the over90InvoiceAmount
	 * 
	 */
	public KualiDecimal getOver90InvoiceAmount() { 
		return over90InvoiceAmount;
	}

	/**
	 * Sets the over90InvoiceAmount attribute.
	 * 
	 * @param over90InvoiceAmount The over90InvoiceAmount to set.
	 * 
	 */
	public void setOver90InvoiceAmount(KualiDecimal over90InvoiceAmount) {
		this.over90InvoiceAmount = over90InvoiceAmount;
	}

    /**
     * Gets the customer attribute. 
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * @param customer The customer to set.
     * @deprecated
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put("customerNumber", this.customerNumber);
        return m;
    }

}
