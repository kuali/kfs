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
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;

public class DisbursementPayeeLookupableHelperServiceImpl extends AbstractPayeeLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementPayeeLookupableHelperServiceImpl.class);
    private static final int NAME_REQUIRED_FILLED_WITH_WILDCARD = 4;

    protected DisbursementVoucherPaymentReasonService disbursementVoucherPaymentReasonService;  

    /**
     * Note: This method is not called by validateSearchParameters anymore.
     * This method checks if the minimum required fields are filled
     */
    @Deprecated
    protected boolean checkMinimumFieldsFilled(Map fieldValues) {
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
     * Note: This method is not called by validateSearchParameters anymore.
     * This method validates the vendor name usage in lookup
     */
    @Deprecated
    protected void validateVendorNameUse(Map fieldValues) {
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
     * Note: This method is not called by validateSearchParameters anymore.
     * This method validates the tax criteria
     */
    @Deprecated
    protected void validateTaxNumberCriteria(Map fieldValues) {
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
     * Note: This method is not called by validateSearchParameters anymore.
     * This method validates the employee name usage in lookup
     */
    @Deprecated
    protected void validateEmployeeNameUse(Map fieldValues) {
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
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        String vendorNumberLabel = getAttributeLabel(KFSPropertyConstants.VENDOR_NUMBER);
        String taxNumberLabel = getAttributeLabel(KFSPropertyConstants.TAX_NUMBER);
        String employeeIdLabel = getAttributeLabel(KFSPropertyConstants.EMPLOYEE_ID);
        String payeeNameLabel = getAttributeLabel(KFSPropertyConstants.PAYEE_NAME);
        String lastNameLabel = getAttributeLabel(KFSPropertyConstants.PERSON_LAST_NAME);
        String firstNameLabel = getAttributeLabel(KFSPropertyConstants.PERSON_FIRST_NAME);
        
        String vendorNumber = (String)fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER);
        String taxNumber = (String)fieldValues.get(KFSPropertyConstants.TAX_NUMBER);
        String employeeId = (String)fieldValues.get(KFSPropertyConstants.EMPLOYEE_ID);
        String payeeName = (String)fieldValues.get(KFSPropertyConstants.PAYEE_NAME);
        
        boolean vendorNumberBlank = StringUtils.isBlank(vendorNumber);
        boolean taxNumberBlank = StringUtils.isBlank(taxNumber);
        boolean employeeIdBlank = StringUtils.isBlank(employeeId);
        boolean payeeNameBlank = StringUtils.isBlank(payeeName);
        boolean vendorInfoBlank = vendorNumberBlank && taxNumberBlank;
        
        // must at least enter one of the following searching criteria: vendorNumber, taxNumber, employeeId, payeeName 
        if (vendorNumberBlank && taxNumberBlank && employeeIdBlank && payeeNameBlank) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_NAME, KFSKeyConstants.ERROR_DV_LOOKUP_NEEDS_SOME_FIELD, new String[]{vendorNumberLabel, taxNumberLabel, employeeIdLabel, payeeNameLabel});
        }
        // can't enter both vendor info (vendorNumber/taxNumber) and employee info (employeeId)
        else if (!(vendorNumberBlank && taxNumberBlank) && !employeeIdBlank) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.EMPLOYEE_ID, KFSKeyConstants.ERROR_DV_VENDOR_EMPLOYEE_CONFUSION, new String[]{employeeIdLabel, vendorNumberLabel, taxNumberLabel});            
        }
        // if vendorNumber is entered, then taxNumber and/or payeeName will be ignored in searching for vendor payee
        else if (!vendorNumberBlank && !(taxNumberBlank && payeeNameBlank)) {
            GlobalVariables.getMessageMap().putWarning(KFSPropertyConstants.VENDOR_NUMBER, KFSKeyConstants.WARNING_DV_PAYEE_ID_UNIQUE, new String[]{vendorNumberLabel, "Vendor"});            
        }
        // if employeeId is entered, then payeeName will be ignored in searching for person payee
        else if (!employeeIdBlank && !payeeNameBlank) {
            GlobalVariables.getMessageMap().putWarning(KFSPropertyConstants.EMPLOYEE_ID, KFSKeyConstants.WARNING_DV_PAYEE_ID_UNIQUE, new String[]{employeeIdLabel, "Employee"});            
        }
        // if payeeName is entered, check its filled level 
        else if (!payeeNameBlank) {
            String minlen = Integer.toString(getNameLengthWithWildcardRequirement());
            
            if (!canSplit(payeeName)) {
                // check whether non-splitable payeeName is filled enough if wildcard is used
                if (!filledEnough(payeeName)) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_NAME, KFSKeyConstants.ERROR_DV_NAME_NOT_FILLED_ENOUGH, new String[]{payeeNameLabel, minlen});
                }
            }
            else {
                String lastName = splitLastName(payeeName);
                String firstName = splitFirstName(payeeName);
                
                // check at least one side of "lastName, firstName" is filled
                if (StringUtils.isBlank(lastName) && StringUtils.isBlank(firstName)) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_NAME, KFSKeyConstants.ERROR_DV_LAST_FIRST_NAME_NOT_FILLED, new String[]{payeeNameLabel, minlen});                    
                }

                // check whether lastName is filled enough if wildcard is used
                if (!filledEnough(lastName)) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_NAME, KFSKeyConstants.ERROR_DV_NAME_NOT_FILLED_ENOUGH, new String[]{lastNameLabel, minlen});
                }
                
                // check whether firstName is filled enough if wildcard is used
                if (!filledEnough(firstName)) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_NAME, KFSKeyConstants.ERROR_DV_NAME_NOT_FILLED_ENOUGH, new String[]{firstNameLabel, minlen});
                }   
            }
        }
        
        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }
    
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

        if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER)) || StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.TAX_NUMBER))) {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
        }
        else if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.EMPLOYEE_ID))) {
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        }
        else {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        }

        return sortSearchResults(searchResults);
    }

    /**
     * Sorts search results.
     */
    protected List<? extends BusinessObject> sortSearchResults(List<DisbursementPayee> searchResults) {
        CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return results;
    }
    
    /**
     * Decides if the specified payeeName can be split into lastName and firstName, i.e. whether it contains a comma.
     */
    protected boolean canSplit(String payeeName) {
        return StringUtils.contains(payeeName, KFSConstants.COMMA);
    }
    
    /**
     * Splits the last name from the specified payeeName, which is in the format of "lastName,firstName".
     * If comma doesn't exist in the name, then the whole string is returned;
     */
    protected String splitLastName(String payeeName) {
        return StringUtils.strip(StringUtils.substringBefore(payeeName, KFSConstants.COMMA));
    }
    
    /**
     * Splits the first name from the specified payeeName, which is in the format of "lastName,firstName".
     * If comma doesn't exist in the name, then an empty string is returned.
     */
    protected String splitFirstName(String payeeName) {
        return StringUtils.strip(StringUtils.substringAfter(payeeName, KFSConstants.COMMA));
    }

    /**
     * Determines if a String is "filled enough", i.e. if a wildcard is present, has a length greater than the defined minimum length (3 characters, plus a wildcard).
     * @param s the String to test
     * @return true if the given String is "filled" by the definition above, false otherwise
     */
    protected boolean filledEnough(String s) {        
        return !containsLookupWildcard(s) || StringUtils.length(s) >= getNameLengthWithWildcardRequirement();
    }
    
    /**
     * @return the number of characters a name field must be filled in for the search to be valid
     */
    protected int getNameLengthWithWildcardRequirement() {
        return NAME_REQUIRED_FILLED_WITH_WILDCARD;
    }
    
    /**
     * Determines if the given String contains a lookup wildcard
     * @param s the String to test
     * @return true if a lookup wildcard is in the String, false otherwise
     */
    protected boolean containsLookupWildcard(String s) {
        return StringUtils.contains(s, "*") || StringUtils.contains(s, "%");
    }
    
    /**
     * Gets the search criteria valid for vendor lookup, from the specified field value map.
     */
    @Override
    protected Map<String, String> getVendorFieldValues(Map<String, String> fieldValues) {
        String vendorNumber = (String)fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER);
        String taxNumber = (String)fieldValues.get(KFSPropertyConstants.TAX_NUMBER);
        String payeeName = (String)fieldValues.get(KFSPropertyConstants.PAYEE_NAME);        
        boolean vendorNumberBlank = StringUtils.isBlank(vendorNumber);
        boolean taxNumberBlank = StringUtils.isBlank(taxNumber);
        boolean payeeNameBlank = StringUtils.isBlank(payeeName);
        
        Map<String, String> vendorFieldValues = new HashMap<String, String>();        
        vendorFieldValues.put(KFSPropertyConstants.ACTIVE, fieldValues.get(KFSPropertyConstants.ACTIVE));
        
        if (!vendorNumberBlank) {
            // if vendorNumber is entered, then taxNumber and/or payeeName will be ignored in searching for vendor payee
            vendorFieldValues.put(KFSPropertyConstants.VENDOR_NUMBER, vendorNumber);                      
        }
        else {
            if (!taxNumberBlank) {
                vendorFieldValues.put(KFSPropertyConstants.TAX_NUMBER, taxNumber);       
            }
            if (!payeeNameBlank) {
                vendorFieldValues.put(KFSPropertyConstants.PAYEE_NAME, getVendorName(payeeName));
            }
        }
             
        Map<String, String> fieldConversionMap = disbursementVoucherPayeeService.getFieldConversionBetweenPayeeAndVendor();
        replaceFieldKeys(vendorFieldValues, fieldConversionMap);
        return vendorFieldValues;
    }
    
    /**
     * Gets the proper vendor name based on the given payee name.
     */
    protected String getVendorName(String payeeName) {
        String vendorName = "";

        // if payee name can't be split into "lastName,firstName", then treat it as a whole vendor name
        if (!canSplit(payeeName)) {
            vendorName = payeeName;
        }
        // otherwise at least one side of "lastName,firstName" shall not be empty
        else {
            // replace empty last name with wildcard
            String lastName = splitLastName(payeeName);
            if (StringUtils.isBlank(lastName)) {
                lastName = "*";
            }
            // replace empty first name with wildcard
            String firstName = splitFirstName(payeeName);   
            if (StringUtils.isBlank(firstName)) {
                firstName = "*";
            }
            // concat into "lastName, firstName"
            vendorName = lastName + VendorConstants.NAME_DELIM + firstName;
        }
        
        return vendorName;            
    }    
        
    /**
     * Gets the search criteria valid for vendor lookup, from the specified field value map.
     */
    @Override
    protected Map<String, String> getPersonFieldValues(Map<String, String> fieldValues) {
        String employeeId = (String)fieldValues.get(KFSPropertyConstants.EMPLOYEE_ID);
        String payeeName = (String)fieldValues.get(KFSPropertyConstants.PAYEE_NAME);
        boolean employeeIdBlank = StringUtils.isBlank(employeeId);
        boolean payeeNameBlank = StringUtils.isBlank(payeeName);
        
        Map<String, String> personFieldValues = new HashMap<String, String>();        
        personFieldValues.put(KFSPropertyConstants.ACTIVE, fieldValues.get(KFSPropertyConstants.ACTIVE));
        
        // if employeeId is entered, then payeeName will be ignored in searching for employee payee
        if (!employeeIdBlank) {
            personFieldValues.put(KFSPropertyConstants.EMPLOYEE_ID, employeeId);                      
        }
        // otherwise at least one side of "lastName,firstName" shall not be empty; if "'" doesn't exist in the name string, treat the whole string as last name
        else if (!payeeNameBlank) {
            // add lastName as search criteria if not empty
            String lastName = splitLastName(payeeName);
            if (StringUtils.isNotBlank(lastName)) {
                personFieldValues.put(KFSPropertyConstants.PERSON_LAST_NAME, getVendorName(lastName));
            }
            // add firstName as search criteria if not empty
            String firstName = splitFirstName(payeeName);
            if (StringUtils.isNotBlank(firstName)) {
                personFieldValues.put(KFSPropertyConstants.PERSON_FIRST_NAME, getVendorName(firstName));
            }       
        }
                
        Map<String, String> fieldConversionMap = disbursementVoucherPayeeService.getFieldConversionBetweenPayeeAndPerson();
        this.replaceFieldKeys(personFieldValues, fieldConversionMap);
        return personFieldValues;
    }  

    /**
     * Removes its return URLs if a row is not qualified for returning.
     * 
     * @param resultRowList
     * @param payeeList
     * @param paymentReasonCode
     */
    protected void filterReturnUrl(List<ResultRow> resultRowList, List<DisbursementPayee> payeeList, String paymentReasonCode) {
        Collection<String> payeeTypeCodes = disbursementVoucherPaymentReasonService.getPayeeTypesByPaymentReason(paymentReasonCode);
        if (payeeTypeCodes == null || payeeTypeCodes.isEmpty()) {
            return;
        }

        for (int index = 0; index < payeeList.size(); index++) {
            DisbursementPayee payee = payeeList.get(index);

            boolean isQualified = disbursementVoucherPaymentReasonService.isPayeeQualifiedForPayment(payee, paymentReasonCode, payeeTypeCodes);
            if (!isQualified) {
                resultRowList.get(index).setReturnUrl(StringUtils.EMPTY);
            }
        }
    }

    /**
     * Sets the disbursementVoucherPaymentReasonService attribute value.
     * 
     * @param disbursementVoucherPaymentReasonService The disbursementVoucherPaymentReasonService to set.
     */
    public void setDisbursementVoucherPaymentReasonService(DisbursementVoucherPaymentReasonService disbursementVoucherPaymentReasonService) {
        this.disbursementVoucherPaymentReasonService = disbursementVoucherPaymentReasonService;
    }

}
