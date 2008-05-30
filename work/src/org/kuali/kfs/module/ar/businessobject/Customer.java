package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Customer extends PersistableBusinessObjectBase {

    private String customerNumber;
    private String customerName;
    private String customerParentCompanyNumber;
    private String customerTypeCode;
    private Date customerAddressChangeDate;
    private Date customerRecordAddDate;
    private Date customerLastActivityDate;
    private String customerSocialSecurityNumberIdentifier;
    private String customerFederalIdentifierNumber;
    private boolean customerActiveIndicator;
    private String customerPhoneNumber;
    private String customer800PhoneNumber;
    private String customerContactName;
    private String customerContactPhoneNumber;
    private String customerFaxNumber;
    private Date customerBirthDate;
    private boolean customerTaxExemptIndicator;
    private KualiDecimal customerCreditLimitAmount;
    private String customerCreditApprovedByName;
    private String customerEmailAddress;

    private Customer customerParentCompany;
    private CustomerType customerType;

    private List<CustomerAddress> customerAddresses;

    /**
     * Default constructor.
     */
    public Customer() {
        customerAddresses = new TypedArrayList(CustomerAddress.class);
    }

    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    /**
     * Gets the customerName attribute.
     * 
     * @return Returns the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute.
     * 
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    /**
     * Gets the customerParentCompanyNumber attribute.
     * 
     * @return Returns the customerParentCompanyNumber
     */
    public String getCustomerParentCompanyNumber() {
        return customerParentCompanyNumber;
    }

    /**
     * Sets the customerParentCompanyNumber attribute.
     * 
     * @param customerParentCompanyNumber The customerParentCompanyNumber to set.
     */
    public void setCustomerParentCompanyNumber(String customerParentCompanyNumber) {
        this.customerParentCompanyNumber = customerParentCompanyNumber;
    }


    /**
     * Gets the customerTypeCode attribute.
     * 
     * @return Returns the customerTypeCode
     */
    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    /**
     * Sets the customerTypeCode attribute.
     * 
     * @param customerTypeCode The customerTypeCode to set.
     */
    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }


    /**
     * Gets the customerAddressChangeDate attribute.
     * 
     * @return Returns the customerAddressChangeDate
     */
    public Date getCustomerAddressChangeDate() {
        return customerAddressChangeDate;
    }

    /**
     * Sets the customerAddressChangeDate attribute.
     * 
     * @param customerAddressChangeDate The customerAddressChangeDate to set.
     */
    public void setCustomerAddressChangeDate(Date customerAddressChangeDate) {
        this.customerAddressChangeDate = customerAddressChangeDate;
    }


    /**
     * Gets the customerRecordAddDate attribute.
     * 
     * @return Returns the customerRecordAddDate
     */
    public Date getCustomerRecordAddDate() {
        return customerRecordAddDate;
    }

    /**
     * Sets the customerRecordAddDate attribute.
     * 
     * @param customerRecordAddDate The customerRecordAddDate to set.
     */
    public void setCustomerRecordAddDate(Date customerRecordAddDate) {
        this.customerRecordAddDate = customerRecordAddDate;
    }


    /**
     * Gets the customerLastActivityDate attribute.
     * 
     * @return Returns the customerLastActivityDate
     */
    public Date getCustomerLastActivityDate() {
        return customerLastActivityDate;
    }

    /**
     * Sets the customerLastActivityDate attribute.
     * 
     * @param customerLastActivityDate The customerLastActivityDate to set.
     */
    public void setCustomerLastActivityDate(Date customerLastActivityDate) {
        this.customerLastActivityDate = customerLastActivityDate;
    }


    /**
     * Gets the customerSocialSecurityNumberIdentifier attribute.
     * 
     * @return Returns the customerSocialSecurityNumberIdentifier
     */
    public String getCustomerSocialSecurityNumberIdentifier() {
        return customerSocialSecurityNumberIdentifier;
    }

    /**
     * Sets the customerSocialSecurityNumberIdentifier attribute.
     * 
     * @param customerSocialSecurityNumberIdentifier The customerSocialSecurityNumberIdentifier to set.
     */
    public void setCustomerSocialSecurityNumberIdentifier(String customerSocialSecurityNumberIdentifier) {
        this.customerSocialSecurityNumberIdentifier = customerSocialSecurityNumberIdentifier;
    }


    /**
     * Gets the customerFederalIdentifierNumber attribute.
     * 
     * @return Returns the customerFederalIdentifierNumber
     */
    public String getCustomerFederalIdentifierNumber() {
        return customerFederalIdentifierNumber;
    }

    /**
     * Sets the customerFederalIdentifierNumber attribute.
     * 
     * @param customerFederalIdentifierNumber The customerFederalIdentifierNumber to set.
     */
    public void setCustomerFederalIdentifierNumber(String customerFederalIdentifierNumber) {
        this.customerFederalIdentifierNumber = customerFederalIdentifierNumber;
    }


    /**
     * Gets the customerActiveIndicator attribute.
     * 
     * @return Returns the customerActiveIndicator
     */
    public boolean isCustomerActiveIndicator() {
        return customerActiveIndicator;
    }

    /**
     * Sets the customerActiveIndicator attribute.
     * 
     * @param customerActiveIndicator The customerActiveIndicator to set.
     */
    public void setCustomerActiveIndicator(boolean customerActiveIndicator) {
        this.customerActiveIndicator = customerActiveIndicator;
    }


    /**
     * Gets the customerPhoneNumber attribute.
     * 
     * @return Returns the customerPhoneNumber
     */
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    /**
     * Sets the customerPhoneNumber attribute.
     * 
     * @param customerPhoneNumber The customerPhoneNumber to set.
     */
    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }


    /**
     * Gets the customer800PhoneNumber attribute.
     * 
     * @return Returns the customer800PhoneNumber
     */
    public String getCustomer800PhoneNumber() {
        return customer800PhoneNumber;
    }

    /**
     * Sets the customer800PhoneNumber attribute.
     * 
     * @param customer800PhoneNumber The customer800PhoneNumber to set.
     */
    public void setCustomer800PhoneNumber(String customer800PhoneNumber) {
        this.customer800PhoneNumber = customer800PhoneNumber;
    }


    /**
     * Gets the customerContactName attribute.
     * 
     * @return Returns the customerContactName
     */
    public String getCustomerContactName() {
        return customerContactName;
    }

    /**
     * Sets the customerContactName attribute.
     * 
     * @param customerContactName The customerContactName to set.
     */
    public void setCustomerContactName(String customerContactName) {
        this.customerContactName = customerContactName;
    }


    /**
     * Gets the customerContactPhoneNumber attribute.
     * 
     * @return Returns the customerContactPhoneNumber
     */
    public String getCustomerContactPhoneNumber() {
        return customerContactPhoneNumber;
    }

    /**
     * Sets the customerContactPhoneNumber attribute.
     * 
     * @param customerContactPhoneNumber The customerContactPhoneNumber to set.
     */
    public void setCustomerContactPhoneNumber(String customerContactPhoneNumber) {
        this.customerContactPhoneNumber = customerContactPhoneNumber;
    }


    /**
     * Gets the customerFaxNumber attribute.
     * 
     * @return Returns the customerFaxNumber
     */
    public String getCustomerFaxNumber() {
        return customerFaxNumber;
    }

    /**
     * Sets the customerFaxNumber attribute.
     * 
     * @param customerFaxNumber The customerFaxNumber to set.
     */
    public void setCustomerFaxNumber(String customerFaxNumber) {
        this.customerFaxNumber = customerFaxNumber;
    }


    /**
     * Gets the customerBirthDate attribute.
     * 
     * @return Returns the customerBirthDate
     */
    public Date getCustomerBirthDate() {
        return customerBirthDate;
    }

    /**
     * Sets the customerBirthDate attribute.
     * 
     * @param customerBirthDate The customerBirthDate to set.
     */
    public void setCustomerBirthDate(Date customerBirthDate) {
        this.customerBirthDate = customerBirthDate;
    }


    /**
     * Gets the customerCreditLimitAmount attribute.
     * 
     * @return Returns the customerCreditLimitAmount
     */
    public KualiDecimal getCustomerCreditLimitAmount() {
        return customerCreditLimitAmount;
    }

    /**
     * Sets the customerCreditLimitAmount attribute.
     * 
     * @param customerCreditLimitAmount The customerCreditLimitAmount to set.
     */
    public void setCustomerCreditLimitAmount(KualiDecimal customerCreditLimitAmount) {
        this.customerCreditLimitAmount = customerCreditLimitAmount;
    }


    /**
     * Gets the customerCreditApprovedByName attribute.
     * 
     * @return Returns the customerCreditApprovedByName
     */
    public String getCustomerCreditApprovedByName() {
        return customerCreditApprovedByName;
    }

    /**
     * Sets the customerCreditApprovedByName attribute.
     * 
     * @param customerCreditApprovedByName The customerCreditApprovedByName to set.
     */
    public void setCustomerCreditApprovedByName(String customerCreditApprovedByName) {
        this.customerCreditApprovedByName = customerCreditApprovedByName;
    }


    /**
     * Gets the customerEmailAddress attribute.
     * 
     * @return Returns the customerEmailAddress
     */
    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    /**
     * Sets the customerEmailAddress attribute.
     * 
     * @param customerEmailAddress The customerEmailAddress to set.
     */
    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
    }

    /**
     * Gets the customerParentCompany attribute.
     * 
     * @return Returns the customerParentCompany
     */
    public Customer getCustomerParentCompany() {
        return customerParentCompany;
    }

    /**
     * Sets the customerParentCompany attribute.
     * 
     * @param customerParentCompany The customerParentCompany to set.
     * @deprecated
     */
    public void setCustomerParentCompany(Customer customerParentCompany) {
        this.customerParentCompany = customerParentCompany;
    }

    /**
     * Gets the customerType attribute.
     * 
     * @return Returns the customerType.
     */
    public CustomerType getCustomerType() {
        return customerType;
    }

    /**
     * Sets the customerType attribute value.
     * 
     * @param customerType The customerType to set.
     * @deprecated
     */
    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("customerNumber", this.customerNumber);
        return m;
    }

    public boolean isCustomerTaxExemptIndicator() {
        return customerTaxExemptIndicator;
    }

    public void setCustomerTaxExemptIndicator(boolean customerTaxExemptIndicator) {
        this.customerTaxExemptIndicator = customerTaxExemptIndicator;
    }

    /**
     * This method gets customerAddresses
     * 
     * @return customerAddresses
     */
    public List<CustomerAddress> getCustomerAddresses() {
        return customerAddresses;
    }

    /**
     * This method sets customerAddresses
     * 
     * @param customerAddresses
     */
    public void setCustomerAddresses(List<CustomerAddress> customerAddresses) {
        this.customerAddresses = customerAddresses;
    }

}
