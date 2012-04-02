/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.AutoPopulatingList;

public class CustomerRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(CustomerRule.class);
    protected Customer oldCustomer;
    protected Customer newCustomer;
    protected DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

    /**
     * This method initializes the old and new customer
     * 
     * @param document
     */
    protected void initializeAttributes(MaintenanceDocument document) {
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
        MessageMap errorMap = GlobalVariables.getMessageMap();
        //negate the return value from hasErrors() becase when there are no errors
        //the method returns false so we need to negate the resuls otherwise
        //out validations will fail.
        isValid &= !errorMap.hasErrors();
        if (isValid) {
            initializeAttributes(document);
            isValid &= checkCustomerHasAddress(newCustomer);

            if (isValid) {
                isValid &= validateAddresses(newCustomer);
            }

            if (isValid) {
                isValid &= checkAddresses(newCustomer);
            }

            if (isValid) {
                isValid &= checkTaxNumber(newCustomer);
            }

            if (isValid) {
                isValid &= checkNameIsValidLength(newCustomer.getCustomerName());
            }

            // TODO This should probably be done in a BO 'before insert' hook, rather than in the business rule validation,
            // unless there's some reason not clear why it needs to happen here.
            if (isValid && document.isNew() && StringUtils.isBlank(newCustomer.getCustomerNumber())) {
                isValid &= setCustomerNumber();
            }
        }

        return isValid;
    }

    /**
     * This method sets the new customer number
     * 
     * @return Returns true if the customer number is set successfully, false otherwise.
     */
    protected boolean setCustomerNumber() {
        // TODO This should probably be done in a BO 'before insert' hook, rather than in the business rule validation,
        // unless there's some reason not clear why it needs to happen here.
        boolean success = true;
        try {
            String customerNumber = SpringContext.getBean(CustomerService.class).getNextCustomerNumber(newCustomer);
            newCustomer.setCustomerNumber(customerNumber);
            if (oldCustomer != null) {
                oldCustomer.setCustomerNumber(customerNumber);
            }
        }
        catch (StringIndexOutOfBoundsException sibe) {
            // It is expected that if a StringIndexOutOfBoundsException occurs, it is due to the customer name being less than three
            // characters
            GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_NAME_LESS_THAN_THREE_CHARACTERS);
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
            GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArKeyConstants.CustomerConstants.ERROR_AT_LEAST_ONE_ADDRESS);
        }
        return success;

    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);

        if (collectionName.equals(ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES)) {
            CustomerAddress customerAddress = (CustomerAddress) line;

            if (isValid) {
                isValid &= checkAddressIsValid(customerAddress);
                isValid &= validateEndDateForNewAddressLine(customerAddress.getCustomerAddressEndDate());
            }

            if (isValid) {
                Customer customer = (Customer) document.getNewMaintainableObject().getBusinessObject();
                if (customerAddress.getCustomerAddressTypeCode().equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {

                    for (int i = 0; i < customer.getCustomerAddresses().size(); i++) {
                        if (customer.getCustomerAddresses().get(i).getCustomerAddressTypeCode().equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {
                            customer.getCustomerAddresses().get(i).setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
                            // OK
                            // break;
                        }
                    }
                }
                // if new address is not Primary, check if there is an active primary address for this customer. If not, make a new
                // address primary
                else {
                    boolean isActivePrimaryAddress = false;
                    Date endDate;

                    for (int i = 0; i < customer.getCustomerAddresses().size(); i++) {
                        if (customer.getCustomerAddresses().get(i).getCustomerAddressTypeCode().equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY)) {
                            endDate = customer.getCustomerAddresses().get(i).getCustomerAddressEndDate();
                            // check if endDate qualifies this customer address as inactive (if endDate is a passed date or present
                            // date)
                            if (!checkEndDateIsValid(endDate, false))
                                customer.getCustomerAddresses().get(i).setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
                            else
                                isActivePrimaryAddress = true;
                        }
                    }
                    if (!isActivePrimaryAddress)
                        customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY);
                }
            }
        }

        return isValid;

    }

    /**
     * This method checks if customer end date is valid: 1. if a new address is being added, customer end date must be a future date
     * 2. if inactivating an address, customer end date must be current or future date
     * 
     * @param endDate
     * @param canBeTodaysDateFlag
     * @return True if endDate is valid.
     */
    public boolean checkEndDateIsValid(Date endDate, boolean canBeTodaysDateFlag) {
        boolean isValid = true;

        if (ObjectUtils.isNull(endDate))
            return isValid;

        Timestamp today = dateTimeService.getCurrentTimestamp();
        today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime());

        // end date must be todays date or future date
        if (canBeTodaysDateFlag) {
            if (endDate.before(today)) {
                isValid = false;
            }
        } // end date must be a future date
        else {
            if (!endDate.after(today)) {
                isValid = false;
            }
        }

        return isValid;
    }

    public boolean validateEndDateForNewAddressLine(Date endDate) {
        boolean isValid = checkEndDateIsValid(endDate, false);
        if (!isValid) {
            String propertyName = ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES_ADD_NEW_ADDRESS + "." + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_END_DATE;
            putFieldError(propertyName, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_END_DATE_MUST_BE_FUTURE_DATE);
        }

        return isValid;
    }

    public boolean validateEndDateForExistingCustomerAddress(Date newEndDate, int ind) {
        boolean isValid = checkEndDateIsValid(newEndDate, true);

        // valid end date for an existing customer address;
        // 1. blank <=> no date entered
        // 2. todays date -> makes address inactive
        // 3. future date
        // 4. if end date is a passed date AND it hasn't been updated <=> oldEndDate = newEndDate
        // 
        // invalid end date for an existing customer address
        // 1. if end date is a passed date AND it has been updated <=> oldEndDate != newEndDate
        if (!isValid) {
            Date oldEndDate = oldCustomer.getCustomerAddresses().get(ind).getCustomerAddressEndDate();
            // passed end date has been entered
            if (ObjectUtils.isNull(oldEndDate) || ObjectUtils.isNotNull(oldEndDate) && !oldEndDate.toString().equals(newEndDate.toString())) {
                String propertyName = ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES + "[" + ind + "]." + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_END_DATE;
                putFieldError(propertyName, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_END_DATE_MUST_BE_CURRENT_OR_FUTURE_DATE);
            }
            else {
                isValid = true;
            }
        }
        return isValid;
    }

    /**
     * This method checks if the customer name entered is greater than or equal to three (3) characters long. This rule was
     * implemented to ensure that there are three characters available from the name to be used as a the customer code.
     * 
     * @param customerName The name of the customer.
     * @return True if the name is greater than or equal to 3 characters long.
     */
    public boolean checkNameIsValidLength(String customerName) {
        boolean success = true;
        if (customerName.length() < 3) {
            success = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_NAME_LESS_THAN_THREE_CHARACTERS);
        }

        if (customerName.indexOf(' ') > -1 && customerName.indexOf(' ') < 3) {
            success = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_NAME_NO_SPACES_IN_FIRST_THREE_CHARACTERS);
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
                // GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE,
                // ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
                putFieldError(propertyName + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
            }
            if (customerAddress.getCustomerAddressInternationalProvinceName() == null || "".equalsIgnoreCase(customerAddress.getCustomerAddressInternationalProvinceName())) {
                isValid = false;
                // GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME,
                // ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
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
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_ZIP_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
            if (customerAddress.getCustomerStateCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerStateCode())) {
                isValid = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_STATE_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US);
            }
        }
        else {
            if (customerAddress.getCustomerInternationalMailCode() == null || "".equalsIgnoreCase(customerAddress.getCustomerInternationalMailCode())) {
                isValid = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
            }
            if (customerAddress.getCustomerAddressInternationalProvinceName() == null || "".equalsIgnoreCase(customerAddress.getCustomerAddressInternationalProvinceName())) {
                isValid = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
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
                    GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArKeyConstants.CustomerConstants.ERROR_ONLY_ONE_PRIMARY_ADDRESS);
                }
                else {
                    hasPrimaryAddress = true;
                }
            }
        }

        // customer must have at least one primary address
        if (!hasPrimaryAddress) {
            isValid = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES, ArKeyConstants.CustomerConstants.ERROR_ONLY_ONE_PRIMARY_ADDRESS);
        }
        return isValid;
    }

    public boolean validateAddresses(Customer customer) {
        boolean isValid = true;
        int i = 0;
        // this block is to validate email addresses
        for (CustomerAddress customerAddress : customer.getCustomerAddresses()) {
            if (ObjectUtils.isNotNull(customerAddress.getCustomerEmailAddress())) {
                String errorPath = KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES + "[" + i + "]";
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);

                this.getDictionaryValidationService().validateBusinessObject(customerAddress);
                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            }
            i++;
        }
        i = 0;
        for (CustomerAddress customerAddress : customer.getCustomerAddresses()) {
            isValid &= checkAddressIsValid(customerAddress, i);
            isValid &= validateEndDateForExistingCustomerAddress(customerAddress.getCustomerAddressEndDate(), i);
            i++;
        }
        if (GlobalVariables.getMessageMap().getErrorCount() > 0)
            isValid = false;

        if (isValid) {
            i = 0;
            for (CustomerAddress customerAddress : customer.getCustomerAddresses()) {
                if (customerAddress.getCustomerAddressTypeCode().equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY) && ObjectUtils.isNotNull(customerAddress.getCustomerAddressEndDate()))
                    isValid &= checkIfPrimaryAddressActive(customerAddress.getCustomerAddressEndDate(), i);
                i++;
            }
        }

        return isValid;
    }

    public boolean checkIfPrimaryAddressActive(Date newEndDate, int ind) {
        // if here -> this is a Primary Address, customer end date is not null
        boolean isActiveAddressFlag = checkEndDateIsValid(newEndDate, false);

        if (!isActiveAddressFlag) {
            String propertyName = ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES + "[" + ind + "]." + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_END_DATE;
            putFieldError(propertyName, ArKeyConstants.CustomerConstants.ERROR_CUSTOMER_PRIMARY_ADDRESS_MUST_HAVE_FUTURE_END_DATE);
        }

        return isActiveAddressFlag;
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
                GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.CustomerFields.CUSTOMER_SOCIAL_SECURITY_NUMBER, ArKeyConstants.CustomerConstants.ERROR_TAX_NUMBER_IS_REQUIRED);
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
            return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(Customer.class, KFSConstants.CustomerParameter.TAX_NUMBER_REQUIRED_IND);
        }
        else
            return false;
    }

}
