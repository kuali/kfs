/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.GroupTravelerForLookup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Lookup which does searches in KIM Person records or AR Customer records to find people who might be traveling as part of a group
 */
public class GroupTravelerForLookupLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected AccountsReceivableModuleService accountsReceivableModuleService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        checkMinimumFieldsFilled(fieldValues);

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    /**
     * Note: This method is not called by validateSearchParameters anymore.
     * This method checks if the minimum required fields are filled
     */
    public boolean checkMinimumFieldsFilled(Map fieldValues) {
        if(StringUtils.isBlank((String) fieldValues.get(KFSPropertyConstants.CUSTOMER_NUMBER)) && StringUtils.isBlank((String) fieldValues.get(KFSPropertyConstants.CUSTOMER_NAME)) && StringUtils.isBlank((String) fieldValues.get(KFSPropertyConstants.EMPLOYEE_ID)) && StringUtils.isBlank((String)fieldValues.get(KFSPropertyConstants.PERSON_FIRST_NAME)) &&
                StringUtils.isBlank((String)fieldValues.get(KFSPropertyConstants.PERSON_LAST_NAME)) && StringUtils.isBlank((String)fieldValues.get(KFSPropertyConstants.PERSON+"."+KFSPropertyConstants.PERSON_USER_IDENTIFIER))) {
            final String customerNumberLabel = getAttributeLabel(KFSPropertyConstants.CUSTOMER_NUMBER);
            final String customerNameLabel = getAttributeLabel(KFSPropertyConstants.CUSTOMER_NAME);
            final String principalNameLabel = getAttributeLabel(KFSPropertyConstants.PERSON+"."+KFSPropertyConstants.PERSON_USER_IDENTIFIER);
            final String firstNameLabel = getAttributeLabel(KIMPropertyConstants.Person.FIRST_NAME);
            final String lastNameLabel = getAttributeLabel(KIMPropertyConstants.Person.LAST_NAME);
            final String employeeIdLabel = getAttributeLabel(KIMPropertyConstants.Person.EMPLOYEE_ID);

            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PERSON+"."+KFSPropertyConstants.PERSON_USER_IDENTIFIER, TemKeyConstants.ERROR_GROUP_TRAVELER_LOOKUP_NEEDS_SOME_FIELD, new String[] {customerNumberLabel, customerNameLabel, firstNameLabel, lastNameLabel, principalNameLabel, employeeIdLabel});
            return false;
        }
        return true;
    }

    /**
     * get the label for the given attribute of the current business object
     *
     * @param attributeName
     * @return
     */
    protected String getAttributeLabel(String attributeName) {
        return this.getDataDictionaryService().getAttributeLabel(GroupTravelerForLookup.class, attributeName);
    }

    /**
     * Perform the search
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<GroupTravelerForLookup> searchResults = new ArrayList<GroupTravelerForLookup>();

        if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.CUSTOMER_NUMBER)) || StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.CUSTOMER_NAME))) {
            searchResults.addAll(getCustomersAsGroupTravelers(fieldValues));
        }
        else if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.EMPLOYEE_ID)) || !StringUtils.isBlank(fieldValues.get(KFSPropertyConstants.PERSON+"."+KFSPropertyConstants.PERSON_USER_IDENTIFIER))) {
            searchResults.addAll(this.getPersonsAsGroupTravelers(fieldValues));
        }
        else {
            searchResults.addAll(getCustomersAsGroupTravelers(fieldValues));
            searchResults.addAll(getPersonsAsGroupTravelers(fieldValues));
        }

        return sortSearchResults(searchResults);
    }

    /**
     * get the search criteria valid for person lookup
     *
     * @param fieldValues
     * @return
     */
    protected Map<String, String> getPersonFieldValues(Map<String, String> fieldValues) {
        Map<String, String> personFieldValues = new HashMap<String, String>();
        personFieldValues.put(KFSPropertyConstants.PERSON_USER_IDENTIFIER, fieldValues.get(KFSPropertyConstants.PERSON+"."+KFSPropertyConstants.PERSON_USER_IDENTIFIER));
        personFieldValues.put(KFSPropertyConstants.PERSON_FIRST_NAME, fieldValues.get(KFSPropertyConstants.PERSON_FIRST_NAME));
        personFieldValues.put(KFSPropertyConstants.PERSON_LAST_NAME, fieldValues.get(KFSPropertyConstants.PERSON_LAST_NAME));
        personFieldValues.put(KFSPropertyConstants.EMPLOYEE_ID, fieldValues.get(KFSPropertyConstants.EMPLOYEE_ID));
        personFieldValues.put(KFSPropertyConstants.ACTIVE, fieldValues.get(KFSPropertyConstants.ACTIVE));
        personFieldValues.put(KIMPropertyConstants.Person.EMPLOYEE_STATUS_CODE, KFSConstants.EMPLOYEE_ACTIVE_STATUS);

        return personFieldValues;
    }

    /**
     * Converts a Person record to a GroupTravelerForLookup record
     *
     * @param personDetail the person detail to convert
     * @param fieldValues the search fields
     * @return a converted GroupTravelerForLookup
     */
    protected GroupTravelerForLookup getGroupTravelerFromPerson(Person personDetail, Map<String, String> fieldValues) {
        GroupTravelerForLookup traveler = new GroupTravelerForLookup();
        traveler.setPrincipalId(personDetail.getPrincipalId());
        traveler.setFirstName(personDetail.getFirstNameUnmasked());
        traveler.setLastName(personDetail.getLastNameUnmasked());
        traveler.setEmployeeId(personDetail.getEmployeeId());
        traveler.setName(personDetail.getNameUnmasked());
        if (personDetail.hasAffiliationOfType(KimConstants.PersonAffiliationTypes.FACULTY_AFFILIATION_TYPE) || personDetail.hasAffiliationOfType(KimConstants.PersonAffiliationTypes.STAFF_AFFILIATION_TYPE)) {
            traveler.setGroupTravelerTypeCode(TemConstants.GroupTravelerType.EMPLOYEE);
        } else if (personDetail.hasAffiliationOfType(KimConstants.PersonAffiliationTypes.STUDENT_AFFILIATION_TYPE)) {
            traveler.setGroupTravelerTypeCode(TemConstants.GroupTravelerType.STUDENT);
        } else {
            traveler.setGroupTravelerTypeCode(TemConstants.GroupTravelerType.OTHER);
        }
        traveler.setActive(personDetail.isActive());
        return traveler;
    }

    /**
     * perform person search
     *
     * @param fieldValues
     * @return
     */
    protected List<GroupTravelerForLookup> getPersonsAsGroupTravelers(Map<String, String> fieldValues) {
        List<GroupTravelerForLookup> groupTravelerList = new ArrayList<GroupTravelerForLookup>();

        Map<String, String> fieldsForLookup = this.getPersonFieldValues(fieldValues);
        List<Person> persons = SpringContext.getBean(PersonService.class).findPeople(fieldsForLookup);

        for (Person personDetail : persons) {
            GroupTravelerForLookup groupTraveler = getGroupTravelerFromPerson(personDetail, fieldValues);
            groupTravelerList.add(groupTraveler);
        }

        return groupTravelerList;
    }

    /**
     * get the search criteria valid for customer lookup
     *
     * @param fieldValues
     * @return
     */
    protected Map<String, String> getCustomerFieldValues(Map<String, String> fieldValues) {
        Map<String, String> customerFieldValues = new HashMap<String, String>();

        customerFieldValues.put(KFSPropertyConstants.CUSTOMER_NUMBER, fieldValues.get(KFSPropertyConstants.CUSTOMER_NUMBER));
        if (!StringUtils.isBlank(fieldValues.get(KFSPropertyConstants.CUSTOMER_NAME))) {
            customerFieldValues.put(KFSPropertyConstants.CUSTOMER_NAME, fieldValues.get(KFSPropertyConstants.CUSTOMER_NAME));
        } else if (!StringUtils.isBlank(fieldValues.get(KFSPropertyConstants.PERSON_FIRST_NAME)) && !StringUtils.isBlank(fieldValues.get(KFSPropertyConstants.PERSON_LAST_NAME))) {
            final String name = fieldValues.get(KFSPropertyConstants.PERSON_FIRST_NAME)+" "+fieldValues.get(KFSPropertyConstants.PERSON_LAST_NAME);
            customerFieldValues.put(KFSPropertyConstants.CUSTOMER_NAME, name);
        } else if (!StringUtils.isBlank(fieldValues.get(KFSPropertyConstants.PERSON_FIRST_NAME))) {
            final String name = fieldValues.get(KFSPropertyConstants.PERSON_FIRST_NAME)+"*";
            customerFieldValues.put(KFSPropertyConstants.CUSTOMER_NAME, name);
        } else if (!StringUtils.isBlank(fieldValues.get(KFSPropertyConstants.PERSON_LAST_NAME))) {
            final String name = "*"+fieldValues.get(KFSPropertyConstants.PERSON_LAST_NAME);
            customerFieldValues.put(KFSPropertyConstants.CUSTOMER_NAME, name);
        }
        customerFieldValues.put(KFSPropertyConstants.ACTIVE, fieldValues.get(KFSPropertyConstants.ACTIVE));

        return customerFieldValues;
    }

    /**
    *
    * @param customer
    * @param fieldValues
    * @return
    */
   protected GroupTravelerForLookup getGroupTravelerFromCustomer(AccountsReceivableCustomer customer, Map<String, String> fieldValues) {
       GroupTravelerForLookup traveler = new GroupTravelerForLookup();
       traveler.setCustomerName(customer.getCustomerName());
       traveler.setCustomerNumber(customer.getCustomerNumber());
       traveler.setName(customer.getCustomerName());
       traveler.setActive(customer.isActive());
       traveler.setGroupTravelerTypeCode(TemConstants.GroupTravelerType.VENDOR);
       return traveler;
   }

   /**
    * perform customer search
    *
    * @param fieldValues
    * @return
    */
   protected List<GroupTravelerForLookup> getCustomersAsGroupTravelers(Map<String, String> fieldValues) {
       List<GroupTravelerForLookup> groupTravelerList = new ArrayList<GroupTravelerForLookup>();

       Map<String, String> fieldsForLookup = this.getCustomerFieldValues(fieldValues);

       List<AccountsReceivableCustomer> customerList = (List<AccountsReceivableCustomer>) getAccountsReceivableModuleService().searchForCustomers(fieldsForLookup);
       for (AccountsReceivableCustomer customer : customerList) {
           GroupTravelerForLookup groupTraveler = getGroupTravelerFromCustomer(customer, fieldValues);
           groupTravelerList.add(groupTraveler);
       }

       return groupTravelerList;
   }

   /**
    * Sorts search results.
    */
   protected List<? extends BusinessObject> sortSearchResults(List<GroupTravelerForLookup> searchResults) {
       CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

       // sort list if default sort column given
       List<String> defaultSortColumns = getDefaultSortColumns();
       if (defaultSortColumns.size() > 0) {
           Collections.sort(results, new BeanPropertyComparator(getDefaultSortColumns(), true));
       }

       return results;
   }

    public AccountsReceivableModuleService getAccountsReceivableModuleService() {
        return accountsReceivableModuleService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }
}
