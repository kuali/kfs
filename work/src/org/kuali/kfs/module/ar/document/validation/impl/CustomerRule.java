package org.kuali.module.ar.rules;

import org.apache.log4j.Logger;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.bo.CustomerAddress;
import org.kuali.module.ar.service.CustomerService;

public class CustomerRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(CustomerRule.class);
    private Customer oldCustomer;
    private Customer newCustomer;

    /**
     * This method initializes the old and new customer
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newCustomer == null) {
            newCustomer = (Customer) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldCustomer == null) {
            oldCustomer = (Customer) document.getOldMaintainableObject().getBusinessObject();
        }
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        isValid &= errorMap.isEmpty();
        if (isValid) {
            initializeAttributes(document);
            isValid &= checkCustomerHasAddress(newCustomer);
            if (isValid) {
                isValid &= checkAddresses(newCustomer);
            }

            if (isValid && document.isNew() && newCustomer.getCustomerNumber() == null) {
                setCustomerNumber();
            }
        }

        return isValid;
    }

    /**
     * This method sets the new customer number
     */
    private void setCustomerNumber() {
        String customerNumber = SpringContext.getBean(CustomerService.class).getNextCustomerNumber(newCustomer);
        newCustomer.setCustomerNumber(customerNumber);
        oldCustomer.setCustomerNumber(newCustomer.getCustomerNumber());
    }

    /**
     * This method checks if the new customer has at least one address
     * 
     * @param newCustomer the new customer
     * @return true is the new customer has at least one address, false otherwise
     */
    public boolean checkCustomerHasAddress(Customer newCustomer) {
        boolean success = true;
        if (newCustomer.getCustomerAddresses().isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArConstants.CustomerConstants.ERROR_AT_LEAST_ONE_ADDRESS);
        }
        return success;

    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument, java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        isValid &= errorMap.isEmpty();

        if (collectionName.equals(ArConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES)) {
            CustomerAddress customerAddress = (CustomerAddress) line;
            
            if (isValid) {
                isValid &= checkAddressIsValid(customerAddress);
            }
            
            if (isValid) {
                Customer customer = (Customer) document.getNewMaintainableObject().getBusinessObject();
                if (customerAddress.getCustomerAddressTypeCode().equalsIgnoreCase(ArConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {

                    for (int i = 0; i < customer.getCustomerAddresses().size(); i++) {
                        if (customer.getCustomerAddresses().get(i).getCustomerAddressTypeCode().equalsIgnoreCase(ArConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {
                            customer.getCustomerAddresses().get(i).setCustomerAddressTypeCode(ArConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
                            break;
                        }
                    }
                }
            }
        }

        return isValid;

    }

    /**
     * This method checks if the address is valid
     * 
     * @param customerAddress
     * @return true if valid, false otherwise
     */
    public boolean checkAddressIsValid(CustomerAddress customerAddress) {
        boolean isValid = true;
        
        if (ArConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_US.equalsIgnoreCase(customerAddress.getCustomerCountryCode())) {

            if (customerAddress.getCustomerZipCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerZipCode())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerFields.CUSTOMER_ADDRESS_ZIP_CODE, ArConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
            if (customerAddress.getCustomerStateCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerStateCode())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerFields.CUSTOMER_ADDRESS_STATE_CODE, ArConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
        }
        else {
            if (customerAddress.getCustomerInternationalMailCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerInternationalMailCode())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE, ArConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
            }
            if (customerAddress.getCustomerAddressInternationalProvinceName() == null || "".equalsIgnoreCase(customerAddress.getCustomerAddressInternationalProvinceName())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME, ArConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
            }
        }
        return isValid;
    }
    
    /**
     * This method checks if the customer addresses are valid: has one and only one primary address
     * 
     * @param customer
     * @return true if valid, false otherwise
     */
    public boolean checkAddresses(Customer customer) {
        boolean isValid = true;
        boolean hasPrimaryAddress = false;
        for (CustomerAddress customerAddress : customer.getCustomerAddresses()) {
            if (customerAddress.getCustomerAddressTypeCode().equalsIgnoreCase(ArConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {
                if (hasPrimaryAddress) {
                    isValid = false;
                    GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArConstants.CustomerConstants.ERROR_ONLY_ONE_PRIMARY_ADDRESS);
                }
                else {
                    hasPrimaryAddress = true;
                }
            }
        }
        return isValid;
    }

}
