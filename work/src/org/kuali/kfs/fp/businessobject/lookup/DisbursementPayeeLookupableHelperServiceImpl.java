/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.fp.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;

public class DisbursementPayeeLookupableHelperServiceImpl extends AbstractPayeeLookupableHelperServiceImpl  {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementPayeeLookupableHelperServiceImpl.class);

    private static final int NAME_REQUIRED_FILLED_WITH_WILDCARD = 4;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm,
     *      java.util.Collection, boolean)
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map<String, String> fieldValues = lookupForm.getFieldsForLookup();
        String paymentReasonCode = fieldValues.get(KFSPropertyConstants.PAYMENT_REASON_CODE);

        List<DisbursementPayee> displayList = (List<DisbursementPayee>) super.performLookup(lookupForm, resultTable, bounded);

        this.filterReturnUrl((List<ResultRow>) resultTable, displayList, paymentReasonCode);

        MessageList messageList = KNSGlobalVariables.getMessageList();
        disbursementVoucherPaymentReasonService.postPaymentReasonCodeUsage(paymentReasonCode, messageList);

        return displayList;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<DisbursementPayee> searchResults = new ArrayList<DisbursementPayee>();

        if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER)) || StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.VENDOR_NAME))) {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
        }
        else if (StringUtils.isNotBlank(fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID))) {
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        }
        else {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        }

        CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
        
        if (checkMinimumFieldsFilled(fieldValues)) {
            validateVendorNameUse(fieldValues);
            validateTaxNumberCriteria(fieldValues);
            validateEmployeeNameUse(fieldValues);
        }
        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }
    
    /**
     * This method checks if the minimum required fields are filled
     */
    public boolean checkMinimumFieldsFilled(Map fieldValues) {
        if(StringUtils.isBlank((String) fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER)) && StringUtils.isBlank((String) fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID)) && StringUtils.isBlank((String)fieldValues.get(KIMPropertyConstants.Person.FIRST_NAME)) && 
                StringUtils.isBlank((String)fieldValues.get(KIMPropertyConstants.Person.LAST_NAME)) && StringUtils.isBlank((String)fieldValues.get(KFSPropertyConstants.VENDOR_NAME))) {
            final String vendorNumberLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NUMBER);
            final String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            final String firstNameLabel = this.getAttributeLabel(KIMPropertyConstants.Person.FIRST_NAME);
            final String lastNameLabel = this.getAttributeLabel(KIMPropertyConstants.Person.LAST_NAME);
            final String employeeIdLabel = this.getAttributeLabel(KIMPropertyConstants.Person.EMPLOYEE_ID);
            
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.VENDOR_NUMBER, KFSKeyConstants.ERROR_DV_LOOKUP_NEEDS_SOME_FIELD, new String[] {vendorNumberLabel, employeeIdLabel, vendorNameLabel, firstNameLabel, lastNameLabel});
            return false;
        }
        return true;
    }
    
    /**
     * This method validates the vendor name usage in lookup
     */
    public void validateVendorNameUse(Map fieldValues) {
        final String vendorName = (String) fieldValues.get(KFSPropertyConstants.VENDOR_NAME);
        final String vendorNumber = (String) fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER);
        final String employeeId = (String) fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID);
        
        final boolean isVendorInfoEntered = StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(vendorNumber);
        if (isVendorInfoEntered && StringUtils.isNotBlank(employeeId)) {
            // only can use the vendor name and vendor number fields or the employee id field, but not both.
            String messageKey = KFSKeyConstants.ERROR_DV_VENDOR_EMPLOYEE_CONFUSION;
            String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            String vendorNumberLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NUMBER);
            GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.EMPLOYEE_ID, messageKey, this.getAttributeLabel(KIMPropertyConstants.Person.EMPLOYEE_ID), vendorNameLabel, vendorNumberLabel);
        }
        
        if (StringUtils.isBlank(vendorNumber) && !StringUtils.isBlank(vendorName) && !filledEnough(vendorName)) {
            final String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.VENDOR_NAME, KFSKeyConstants.ERROR_DV_NAME_NOT_FILLED_ENOUGH, new String[] {vendorNameLabel, Integer.toString(getNameLengthWithWildcardRequirement())});
        }
    }
    
    /**
     * This method validates the tax criteria
     */
    public void validateTaxNumberCriteria(Map fieldValues) {
        final String employeeId = (String) fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID);
        final String firstName = (String)fieldValues.get(KIMPropertyConstants.Person.FIRST_NAME);
        final String lastName = (String)fieldValues.get(KIMPropertyConstants.Person.LAST_NAME);
        final String vendorTaxNumber = (String)fieldValues.get(KFSPropertyConstants.TAX_NUMBER);
        
        // we do not use to use Tax Number field for the lookup on the person...
        if(StringUtils.isNotBlank(vendorTaxNumber) && StringUtils.isNotBlank(firstName)) {
            fieldValues.remove(KFSPropertyConstants.TAX_NUMBER);
            String messageKey = KFSKeyConstants.ERROR_DV_LOOKUP_TAX_NUMBER_EMPLOYEE_DETAILS_CONFUSION;
            GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.FIRST_NAME, messageKey, this.getAttributeLabel(KFSPropertyConstants.TAX_NUMBER), this.getAttributeLabel(KIMPropertyConstants.Person.FIRST_NAME));                
        }
        // if tax number and employee last name entered then send an error message...
        if(StringUtils.isNotBlank(vendorTaxNumber) && StringUtils.isNotBlank(lastName)) {
            fieldValues.remove(KFSPropertyConstants.TAX_NUMBER);
            String messageKey = KFSKeyConstants.ERROR_DV_LOOKUP_TAX_NUMBER_EMPLOYEE_DETAILS_CONFUSION;
            GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.LAST_NAME, messageKey, this.getAttributeLabel(KFSPropertyConstants.TAX_NUMBER), this.getAttributeLabel(KIMPropertyConstants.Person.LAST_NAME));                
        }
        // if tax number and employee id entered then send an error message...
        if(StringUtils.isNotBlank(vendorTaxNumber) && StringUtils.isNotBlank(employeeId)) {
            fieldValues.remove(KFSPropertyConstants.TAX_NUMBER);
            String messageKey = KFSKeyConstants.ERROR_DV_LOOKUP_TAX_NUMBER_EMPLOYEE_DETAILS_CONFUSION;
            GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.EMPLOYEE_ID, messageKey, this.getAttributeLabel(KIMPropertyConstants.Person.EMPLOYEE_ID), this.getAttributeLabel(KFSPropertyConstants.TAX_NUMBER));                
        }
    }
    
    /**
     * This method validates the employee name usage in lookup
     */
    public void validateEmployeeNameUse(Map fieldValues) {
        final String firstName = (String)fieldValues.get(KIMPropertyConstants.Person.FIRST_NAME);
        final String lastName = (String)fieldValues.get(KIMPropertyConstants.Person.LAST_NAME);
        final String vendorName = (String) fieldValues.get(KFSPropertyConstants.VENDOR_NAME);
        final String employeeId = (String) fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID);
        final boolean isPersonNameEntered = StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(lastName);
        
        if (isPersonNameEntered && StringUtils.isNotBlank(vendorName)) {
            // only can use the person first and last name fields or the vendor name field, but not both.
            String messageKey = KFSKeyConstants.ERROR_DV_VENDOR_NAME_PERSON_NAME_CONFUSION;

            String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            String firstNameLabel = this.getAttributeLabel(KIMPropertyConstants.Person.FIRST_NAME);
            String lastNameLabel = this.getAttributeLabel(KIMPropertyConstants.Person.LAST_NAME);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.VENDOR_NAME, messageKey, vendorNameLabel, firstNameLabel, lastNameLabel);
        }
        if (StringUtils.isBlank(employeeId)) {
            if (StringUtils.isBlank(firstName) && !StringUtils.isBlank(lastName) && !filledEnough(lastName)) {
                final String label = getAttributeLabel(KIMPropertyConstants.Person.LAST_NAME);
                GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.LAST_NAME, KFSKeyConstants.ERROR_DV_NAME_NOT_FILLED_ENOUGH, new String[] { label, Integer.toString(getNameLengthWithWildcardRequirement() ) } );
            } else if (StringUtils.isBlank(lastName) && !StringUtils.isBlank(firstName) && !filledEnough(firstName)) {
                final String label = getAttributeLabel(KIMPropertyConstants.Person.FIRST_NAME);
                GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.FIRST_NAME, KFSKeyConstants.ERROR_DV_NAME_NOT_FILLED_ENOUGH, new String[] { label, Integer.toString(getNameLengthWithWildcardRequirement() ) } );
            }
        }
    }
    
    /**
     * Determines if a String is "filled enough" - ie, is not null, has a length greater than zero and if a wildcard is present, has a length greater than 4 (3 characters, plus a wildcard)
     * @param s the String to test
     * @return true if the given String is "filled" by the definition above, false otherwise
     */
    protected boolean filledEnough(String s) {
        final boolean containsWildcard = containsLookupWildcard(s);
        return s != null && s.length() > 0 && ((containsWildcard && s.length() >= getNameLengthWithWildcardRequirement()) || !containsWildcard);
    }
    
    /**
     * @return the number of characters a name field must be filled in for the search to be valid
     */
    protected int getNameLengthWithWildcardRequirement() {
        return DisbursementPayeeLookupableHelperServiceImpl.NAME_REQUIRED_FILLED_WITH_WILDCARD;
    }
    
    /**
     * Determines if the given String contains a lookup wildcard
     * @param s the String to test
     * @return true if a lookup wildcard is in the String, false otherwise
     */
    protected boolean containsLookupWildcard(String s) {
        return s != null && (s.indexOf('*') > -1 || s.indexOf('%') > -1); 
    }

   
    
    

}
