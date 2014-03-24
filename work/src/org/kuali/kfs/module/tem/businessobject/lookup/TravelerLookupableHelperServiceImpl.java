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
package org.kuali.kfs.module.tem.businessobject.lookup;

import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.EMPLOYEE_TRAVELER_TYPE_CODES;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.NON_EMPLOYEE_TRAVELER_TYPE_CODES;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TRAVELER_TYPES_NOT_CONFIGURED;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TRAVELER_TYPES_NOT_SELECTED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_DOC_TRAVELER_TYP_CD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.datadictionary.TravelDetailLookupMappedFieldProxy;
import org.kuali.kfs.module.tem.dataaccess.TravelerDao;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.identity.PersonImpl;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Custom helper service used to find {@link TravelerDetail} instances of employees or non-employees or both.
 *
 */
@SuppressWarnings("deprecation")
public class TravelerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    public static Logger LOG = Logger.getLogger(TravelerLookupableHelperServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private PersonService personService;
    private TravelerService travelerService;
    private TravelerDao travelerDao;
    private AccountsReceivableModuleService accountsReceivableModuleService;

    private static final int NAME_REQUIRED_FILLED_WITH_WILDCARD = 4;

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<TravelerDetail> searchResults = new ArrayList<TravelerDetail>();

        if (isEmployeeSearch(fieldValues)) {
            searchResults.addAll(getEmployeesAsTravelers(fieldValues));
        }
        else {
            LOG.debug("Doing search for customers");
            searchResults.addAll(getNonEmployeesAsTravelers(fieldValues));
        }

        CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        if (fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD) == null || fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD).equals("")) {
            GlobalVariables.getMessageMap().putError(TRVL_DOC_TRAVELER_TYP_CD, ERROR_TRAVELER_TYPES_NOT_SELECTED, new String[] { (String) fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD) });
        }

        if (!isEmployeeSearch(fieldValues) && !isNonEmployeeSearch(fieldValues)) {
            GlobalVariables.getMessageMap().putError(TRVL_DOC_TRAVELER_TYP_CD,
                                                     ERROR_TRAVELER_TYPES_NOT_CONFIGURED,
                                                     new String[] { (String) fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD) });
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    protected Map<String, String> convertFieldValues(final Class boClass, final Map<String, String> fieldValues) {
        Map<String, String> retval = new HashMap<String, String>();

        LOG.debug("Converting field values " + fieldValues);

        for (final FieldDefinition lookupField : getLookupFieldsFor(TravelerDetail.class.getName())) {
            final String attrName = lookupField.getAttributeName();

            if (lookupField instanceof TravelDetailLookupMappedFieldProxy) {
                final TravelDetailLookupMappedFieldProxy mappedField = (TravelDetailLookupMappedFieldProxy) lookupField;
                final String key = mappedField.getAttributeMap().get(boClass.getSimpleName());
                String value = fieldValues.get(attrName);

                if (retval.containsKey(key)) {
                    value = retval.get(key) + value;
                }
                if (key != null) {
                    retval.put(key, value);
                }
                else {
                    LOG.warn("Got a null key for attribute name " + attrName);
                }
            }
            else if (containsAttribute(boClass, attrName)) {
                retval.put(attrName, fieldValues.get(attrName));
            }
        }

        return retval;
    }

    /**
     * Generates a {@link Map} of field values where object properties are mapped to values for
     * search purposes.
     *
     * @param fieldValues original field values passed to the lookup
     * @return Map of values
     */
    protected Map<String, String> getPersonFieldValues(final Map<String, String> fieldValues) {
        return convertFieldValues(PersonImpl.class, fieldValues);
    }

    /**
     * Generates a {@link Map} of field values where object properties are mapped to values for
     * search purposes.
     *
     * @param fieldValues original field values passed to the lookup
     * @return Map of values
     */
    protected Map<String, String> getCustomerFieldValues(final Map<String, String> fieldValues) {
        return convertFieldValues(getAccountsReceivableModuleService().createCustomer().getClass(), fieldValues);
    }

    /**
     * Determine using fieldValues map from the lookup form whether the user is doing an employee or non-employee
     * search
     *
     * @param fieldValues is a value map of lookup form parameters
     * @return true if an employee search, false otherwise
     */
    protected boolean isEmployeeSearch(final Map<String, String> fieldValues) {
        LOG.debug("Checking traveler type code " + fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD));
        final String employeeTypes = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, EMPLOYEE_TRAVELER_TYPE_CODES);
        return employeeTypes.indexOf(fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD)) != -1;
    }

    /**
     * Determine using fieldValues map from the lookup form whether the user is doing an employee or non-employee
     * search
     *
     * @param fieldValues is a value map of lookup form parameters
     * @return true if an employee search, false otherwise
     */
    protected boolean isNonEmployeeSearch(final Map<String, String> fieldValues) {
        LOG.debug("Checking traveler type code " + fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD));
        final String nonEmployeeTypes = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, NON_EMPLOYEE_TRAVELER_TYPE_CODES);
        return nonEmployeeTypes.indexOf(fieldValues.get(TRVL_DOC_TRAVELER_TYP_CD)) != -1;
    }

    protected boolean isKimOnlySearch(final Map<String, String> fieldValues) {
        return (fieldValues.get("principalName") != null || fieldValues.get("principalId") != null);
    }

    /**
     * Performs a person search and converts the results to {@link TravelerDetail} instances
     *
     * @param fieldValues
     * @return List of {@link TravelerDetail} instances
     */
    protected List<TravelerDetail> getEmployeesAsTravelers(Map<String, String> fieldValues) {
        final List<TravelerDetail> travelers = new ArrayList<TravelerDetail>();

        final Map<String, String> kimFieldsForLookup = this.getPersonFieldValues(fieldValues);

        LOG.debug("Looking up people with criteria " + kimFieldsForLookup);
        final List<? extends Person> persons = getPersonService().findPeople(kimFieldsForLookup);

        for (Person personDetail : persons) {
            travelers.add(getTravelerService().convertToTraveler(personDetail));
        }

        if (!isKimOnlySearch(fieldValues)) {
            final Map<String, String> arFieldsForLookup  = this.getCustomerFieldValues(fieldValues);
            final Collection<AccountsReceivableCustomer> customers = getTravelerDao().findCustomersBy(arFieldsForLookup);
            for (AccountsReceivableCustomer customer : customers) {
                travelers.add(getTravelerService().convertToTraveler(customer));
            }
        }

        return travelers;
    }

    /**
     * Performs a customer search and converts the results to {@link TravelerDetail} instances
     *
     * @param fieldValues
     * @return List of {@link TravelerDetail} instances
     */
    protected List<TravelerDetail> getNonEmployeesAsTravelers(final Map<String, String> fieldValues) {
        final List<TravelerDetail> travelers = new ArrayList<TravelerDetail>();

        final Map<String, String> fieldsForLookup = this.getCustomerFieldValues(fieldValues);

        final List<AccountsReceivableCustomerType> customerTypeList = getAccountsReceivableModuleService().findByCustomerTypeDescription(TemConstants.CUSTOMER_TRAVLER_TYPE_CODE);
        LOG.debug("Got customer types " + customerTypeList);
        LOG.debug("Got customer types " + customerTypeList.size());
        if(customerTypeList != null && customerTypeList.size() > 0) {
            LOG.debug("Adding " + TemPropertyConstants.CUSTOMER_TYPE_CODE + " to fields to lookup");
            fieldsForLookup.put(TemPropertyConstants.CUSTOMER_TYPE_CODE, customerTypeList.get(0).getCustomerTypeCode());
        }
        LOG.debug("Using fieldsForLookup " + fieldsForLookup);
        final Collection<AccountsReceivableCustomer> customers = getTravelerDao().findCustomersBy(fieldsForLookup);

        for (final AccountsReceivableCustomer customer : customers) {
            travelers.add(getTravelerService().convertToTraveler(customer));
        }

        return travelers;
    }

    // replace the keys in fieldValues with the corresponding values defined in fieldConversionMap
    protected void replaceFieldKeys(final Map<String, String> fieldValues, final Map<String, String> helperMap) {
        for (String key : helperMap.keySet()) {
            if (fieldValues.containsKey(key)) {
                String value = fieldValues.get(key);
                String newKey = helperMap.get(key);

                fieldValues.remove(key);
                fieldValues.put(newKey, value);
            }
        }
    }

    /**
     * Sets the personService attribute value.
     *
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public PersonService getPersonService() {
        return this.personService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    @Override
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Override
    public ParameterService getParameterService() {
        return this.parameterService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    @Override
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    @Override
    public BusinessObjectService getBusinessObjectService() {
        return this.businessObjectService;
    }

    protected Collection<FieldDefinition> getLookupFieldsFor(final String className) {

        BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(className);
        return businessObjectEntry.getLookupDefinition().getLookupFields();
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionary#getBusinessObjectEntry(String)
     */
    @SuppressWarnings("rawtypes")
    protected boolean containsAttribute(final Class boClass, final String attributeName) {
        return getDataDictionaryService().isAttributeDefined(boClass, attributeName);
    }

    /**
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    @Override
    public void setDataDictionaryService(final DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    @Override
    public DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    protected TravelerDao getTravelerDao() {
        return travelerDao;
    }

    public void setTravelerDao(final TravelerDao travelerDao) {
        this.travelerDao = travelerDao;
    }

    public void setTravelerService(final TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    public TravelerService getTravelerService() {
        return this.travelerService;
    }

    /**
     * Gets the accountsReceivableModuleService attribute.
     *
     * @return Returns the accountsReceivableModuleService.
     */
    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }

        return accountsReceivableModuleService;
    }

    /**
     * Sets the accountsReceivableModuleService attribute value.
     *
     * @param accountsReceivableModuleService The accountsReceivableModuleService to set.
     */
    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }
}
