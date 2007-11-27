package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerAddress extends PersistableBusinessObjectBase {

	private String customerNumber;
	private Integer customerAddressIdentifier;
	private String customerAddressName;
	private String customerLine1StreetAddress;
	private String customerLine2StreetAddress;
	private String customerCityName;
	private String customerStateCode;
	private String customerZipCode;
	private String customerInternationalMailCode;
	private String customerEmailAddress;
	private String customerAddressTypeCode;
	private Date customerAddressEndDate;

	private CustomerAddressType customerAddressType;
    private Customer customer;
    
	/**
	 * Default constructor.
	 */
	public CustomerAddress() {

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
	 * Gets the customerAddressIdentifier attribute.
	 * 
	 * @return Returns the customerAddressIdentifier
	 * 
	 */
	public Integer getCustomerAddressIdentifier() { 
		return customerAddressIdentifier;
	}

	/**
	 * Sets the customerAddressIdentifier attribute.
	 * 
	 * @param customerAddressIdentifier The customerAddressIdentifier to set.
	 * 
	 */
	public void setCustomerAddressIdentifier(Integer customerAddressIdentifier) {
		this.customerAddressIdentifier = customerAddressIdentifier;
	}


	/**
	 * Gets the customerAddressName attribute.
	 * 
	 * @return Returns the customerAddressName
	 * 
	 */
	public String getCustomerAddressName() { 
		return customerAddressName;
	}

	/**
	 * Sets the customerAddressName attribute.
	 * 
	 * @param customerAddressName The customerAddressName to set.
	 * 
	 */
	public void setCustomerAddressName(String customerAddressName) {
		this.customerAddressName = customerAddressName;
	}


	/**
	 * Gets the customerLine1StreetAddress attribute.
	 * 
	 * @return Returns the customerLine1StreetAddress
	 * 
	 */
	public String getCustomerLine1StreetAddress() { 
		return customerLine1StreetAddress;
	}

	/**
	 * Sets the customerLine1StreetAddress attribute.
	 * 
	 * @param customerLine1StreetAddress The customerLine1StreetAddress to set.
	 * 
	 */
	public void setCustomerLine1StreetAddress(String customerLine1StreetAddress) {
		this.customerLine1StreetAddress = customerLine1StreetAddress;
	}


	/**
	 * Gets the customerLine2StreetAddress attribute.
	 * 
	 * @return Returns the customerLine2StreetAddress
	 * 
	 */
	public String getCustomerLine2StreetAddress() { 
		return customerLine2StreetAddress;
	}

	/**
	 * Sets the customerLine2StreetAddress attribute.
	 * 
	 * @param customerLine2StreetAddress The customerLine2StreetAddress to set.
	 * 
	 */
	public void setCustomerLine2StreetAddress(String customerLine2StreetAddress) {
		this.customerLine2StreetAddress = customerLine2StreetAddress;
	}


	/**
	 * Gets the customerCityName attribute.
	 * 
	 * @return Returns the customerCityName
	 * 
	 */
	public String getCustomerCityName() { 
		return customerCityName;
	}

	/**
	 * Sets the customerCityName attribute.
	 * 
	 * @param customerCityName The customerCityName to set.
	 * 
	 */
	public void setCustomerCityName(String customerCityName) {
		this.customerCityName = customerCityName;
	}


	/**
	 * Gets the customerStateCode attribute.
	 * 
	 * @return Returns the customerStateCode
	 * 
	 */
	public String getCustomerStateCode() { 
		return customerStateCode;
	}

	/**
	 * Sets the customerStateCode attribute.
	 * 
	 * @param customerStateCode The customerStateCode to set.
	 * 
	 */
	public void setCustomerStateCode(String customerStateCode) {
		this.customerStateCode = customerStateCode;
	}


	/**
	 * Gets the customerZipCode attribute.
	 * 
	 * @return Returns the customerZipCode
	 * 
	 */
	public String getCustomerZipCode() { 
		return customerZipCode;
	}

	/**
	 * Sets the customerZipCode attribute.
	 * 
	 * @param customerZipCode The customerZipCode to set.
	 * 
	 */
	public void setCustomerZipCode(String customerZipCode) {
		this.customerZipCode = customerZipCode;
	}


	/**
	 * Gets the customerInternationalMailCode attribute.
	 * 
	 * @return Returns the customerInternationalMailCode
	 * 
	 */
	public String getCustomerInternationalMailCode() { 
		return customerInternationalMailCode;
	}

	/**
	 * Sets the customerInternationalMailCode attribute.
	 * 
	 * @param customerInternationalMailCode The customerInternationalMailCode to set.
	 * 
	 */
	public void setCustomerInternationalMailCode(String customerInternationalMailCode) {
		this.customerInternationalMailCode = customerInternationalMailCode;
	}


	/**
	 * Gets the customerEmailAddress attribute.
	 * 
	 * @return Returns the customerEmailAddress
	 * 
	 */
	public String getCustomerEmailAddress() { 
		return customerEmailAddress;
	}

	/**
	 * Sets the customerEmailAddress attribute.
	 * 
	 * @param customerEmailAddress The customerEmailAddress to set.
	 * 
	 */
	public void setCustomerEmailAddress(String customerEmailAddress) {
		this.customerEmailAddress = customerEmailAddress;
	}


	/**
	 * Gets the customerAddressTypeCode attribute.
	 * 
	 * @return Returns the customerAddressTypeCode
	 * 
	 */
	public String getCustomerAddressTypeCode() { 
		return customerAddressTypeCode;
	}

	/**
	 * Sets the customerAddressTypeCode attribute.
	 * 
	 * @param customerAddressTypeCode The customerAddressTypeCode to set.
	 * 
	 */
	public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
		this.customerAddressTypeCode = customerAddressTypeCode;
	}


	/**
	 * Gets the customerAddressEndDate attribute.
	 * 
	 * @return Returns the customerAddressEndDate
	 * 
	 */
	public Date getCustomerAddressEndDate() { 
		return customerAddressEndDate;
	}

	/**
	 * Sets the customerAddressEndDate attribute.
	 * 
	 * @param customerAddressEndDate The customerAddressEndDate to set.
	 * 
	 */
	public void setCustomerAddressEndDate(Date customerAddressEndDate) {
		this.customerAddressEndDate = customerAddressEndDate;
	}

	/**
	 * Gets the customerAddressType attribute.
	 * 
	 * @return Returns the customerAddressType
	 * 
	 */
	public CustomerAddressType getCustomerAddressType() { 
		return customerAddressType;
	}

	/**
	 * Sets the customerAddressType attribute.
	 * 
	 * @param customerAddressType The customerAddressType to set.
	 * @deprecated
	 */
	public void setCustomerAddressType(CustomerAddressType customerAddressType) {
		this.customerAddressType = customerAddressType;
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
        m.put("customerNumber", this.customerNumber);
        if (this.customerAddressIdentifier != null) {
            m.put("customerAddressIdentifier", this.customerAddressIdentifier.toString());
        }
	    return m;
    }

}
