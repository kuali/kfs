package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
            
            if (isValid) {
                validateAddresses(newCustomer);
            }

            if (isValid) {
                isValid &= checkTaxNumber(newCustomer);
            }
            
            if (isValid) {
                isValid &= checkNameIsValidLength(newCustomer.getCustomerName());
            }

            //TODO This should probably be done in a BO 'before insert' hook, rather than in the business rule validation, 
            //     unless there's some reason not clear why it needs to happen here.
            if (isValid && document.isNew() && StringUtils.isBlank(newCustomer.getCustomerNumber())) {
                isValid &= setCustomerNumber();
            }
        }

        return isValid;
    }

    /**
     * This method sets the new customer number
     * @return Returns true if the customer number is set successfully, false otherwise.
     */
    private boolean setCustomerNumber() {
        //TODO This should probably be done in a BO 'before insert' hook, rather than in the business rule validation, 
        //     unless there's some reason not clear why it needs to happen here.
        boolean success = true;
        try {
            String customerNumber = SpringContext.getBean(CustomerService.class).getNextCustomerNumber(newCustomer);
            newCustomer.setCustomerNumber(customerNumber);
            if (oldCustomer != null) {
                oldCustomer.setCustomerNumber(customerNumber);
            }
        } catch(StringIndexOutOfBoundsException sibe) {
            // It is expected that if a StringIndexOutOfBoundsException occurs, it is due to the customer name being less than three characters
            GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_NAME_LESS_THAN_THREE_CHARACTERS);
            success = false;
        }
        return success;
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
            success = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArKeyConstants.CustomerConstants.ERROR_AT_LEAST_ONE_ADDRESS);
        }
        return success;

    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        isValid &= errorMap.isEmpty();

        if (collectionName.equals(ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES)) {
            CustomerAddress customerAddress = (CustomerAddress) line;
            
            if (isValid) {
                isValid &= checkAddressIsValid(customerAddress);
            }
            
            if (isValid) {
                Customer customer = (Customer) document.getNewMaintainableObject().getBusinessObject();
                if (customerAddress.getCustomerAddressTypeCode().equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {

                    for (int i = 0; i < customer.getCustomerAddresses().size(); i++) {
                        if (customer.getCustomerAddresses().get(i).getCustomerAddressTypeCode().equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {
                            customer.getCustomerAddresses().get(i).setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
                            break;
                        }
                    }
                }
            }
        }

        return isValid;

    }
    
    /**
     * 
     * This method checks if the customer name entered is greater than or equal to three (3) characters long.
     * This rule was implemented to ensure that there are three characters available from the name to be used as a the customer code.
     * 
     * @param customerName The name of the customer.
     * @return True if the name is greater than or equal to 3 characters long.
     */
    public boolean checkNameIsValidLength(String customerName) {
        boolean success = true;
        if (customerName.length() < 3) {
            success = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_NAME_LESS_THAN_THREE_CHARACTERS);
        }
        
        if(customerName.indexOf(' ')>-1 && customerName.indexOf(' ')<3) {
            success = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_NAME_NO_SPACES_IN_FIRST_THREE_CHARACTERS);
        }
        return success;
    }
    
    /**
     * This method checks if the address is valid
     * 
     * @param customerAddress
     * @return true if valid, false otherwise
     */
    public boolean checkAddressIsValid(CustomerAddress customerAddress, int ind) {
        boolean isValid = true;
        String propertyName = ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES + "[" + ind + "].";

        if (ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_US.equalsIgnoreCase(customerAddress.getCustomerCountryCode())) {

            if (customerAddress.getCustomerZipCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerZipCode())) {
                isValid = false;
                putFieldError(propertyName + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_ZIP_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
            if (customerAddress.getCustomerStateCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerStateCode())) {
                isValid = false;
                putFieldError(propertyName + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_STATE_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
        }
        else {
            if (customerAddress.getCustomerInternationalMailCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerInternationalMailCode())) {
                isValid = false;
                //GlobalVariables.getErrorMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
                putFieldError(propertyName + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
            }
            if (customerAddress.getCustomerAddressInternationalProvinceName() == null || "".equalsIgnoreCase(customerAddress.getCustomerAddressInternationalProvinceName())) {
                isValid = false;
                //GlobalVariables.getErrorMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
                putFieldError(propertyName + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
            }
        }
        return isValid;
    }
    
    public boolean checkAddressIsValid(CustomerAddress customerAddress) {
        boolean isValid = true;

        if (ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_US.equalsIgnoreCase(customerAddress.getCustomerCountryCode())) {

            if (customerAddress.getCustomerZipCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerZipCode())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_ZIP_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
            if (customerAddress.getCustomerStateCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerStateCode())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_STATE_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
        }
        else {
            if (customerAddress.getCustomerInternationalMailCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerInternationalMailCode())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
            }
            if (customerAddress.getCustomerAddressInternationalProvinceName() == null || "".equalsIgnoreCase(customerAddress.getCustomerAddressInternationalProvinceName())) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
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
            if (customerAddress.getCustomerAddressTypeCode().equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {
                if (hasPrimaryAddress) {
                    isValid = false;
                    GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArKeyConstants.CustomerConstants.ERROR_ONLY_ONE_PRIMARY_ADDRESS);
                }
                else {
                    hasPrimaryAddress = true;
                }
            }
        }

        // customer must have at least one primary address
        if (!hasPrimaryAddress) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArKeyConstants.CustomerConstants.ERROR_ONLY_ONE_PRIMARY_ADDRESS);
        }
        return isValid;
    }
    
    public boolean validateAddresses(Customer customer) {
        boolean isValid = true;
        int i = 0;
        for (CustomerAddress customerAddress : customer.getCustomerAddresses()) {
            isValid &= checkAddressIsValid(customerAddress,i);
            i++;
            
        }
        
        return isValid;
    }
    
    /**
     * This method checks if tax number is entered when tax number is required
     * 
     * @param customer
     * @return true if tax number is required and tax number is entered or if tax number is not required, false if tax number
     *         required and tax number not entered
     */
    public boolean checkTaxNumber(Customer customer) {
        boolean isValid = true;
        if (isTaxNumberRequired()) {
            boolean noTaxNumber = (customer.getCustomerTaxNbr() == null || customer.getCustomerTaxNbr().equalsIgnoreCase(""));
            if (noTaxNumber) {
                isValid = false;
                GlobalVariables.getErrorMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_SOCIAL_SECURITY_NUMBER, ArKeyConstants.CustomerConstants.ERROR_TAX_NUMBER_IS_REQUIRED);
            }
        }
        return isValid;
    }

    /**
     * This method checks if tax number is required
     * 
     * @return true if tax number is required, false otherwise
     */
    public boolean isTaxNumberRequired() {
        boolean paramExists = SpringContext.getBean(ParameterService.class).parameterExists(Customer.class, KFSConstants.CustomerParameter.TAX_NUMBER_REQUIRED_IND);
        if (paramExists) {
            return SpringContext.getBean(ParameterService.class).getIndicatorParameter(Customer.class, KFSConstants.CustomerParameter.TAX_NUMBER_REQUIRED_IND);
        }
        else
            return false;
    }

}
