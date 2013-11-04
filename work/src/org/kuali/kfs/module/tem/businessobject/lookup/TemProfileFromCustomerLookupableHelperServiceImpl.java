/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromCustomer;
import org.kuali.kfs.module.tem.dataaccess.TravelerDao;
import org.kuali.kfs.module.tem.datadictionary.MappedDefinition;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.UrlFactory;

public class TemProfileFromCustomerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    public static Logger LOG = Logger.getLogger(TemProfileFromCustomerLookupableHelperServiceImpl.class);

    private TravelerService travelerService;
    private TravelerDao travelerDao;
    private TemProfileService temProfileService;
    private AccountsReceivableModuleService accountsReceivableModuleService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        lookupForm.setSuppressActions(false);
        lookupForm.setSupplementalActionsEnabled(true);
        lookupForm.setHideReturnLink(false);
        lookupForm.setShowMaintenanceLinks(true);
        return super.performLookup(lookupForm, resultTable, bounded);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        List<TemProfileFromCustomer> searchResults = new ArrayList<TemProfileFromCustomer>();
        searchResults.addAll(getCustomers(fieldValues));

        CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }

        return results;
    }

    /**
     * Generates a {@link Map} of field values where object properties are mapped to values for
     * search purposes.
     *
     * @param fieldValues original field values passed to the lookup
     * @return Map of values
     */
    protected Map<String, String> getCustomerFieldValues(final Map<String, String> fieldValues) {
        Map<String, String> retval = new HashMap<String, String>();
        retval.putAll(convertFieldValues(getAccountsReceivableModuleService().createCustomer().getClass(), fieldValues, "", TemProfileFromCustomer.class.getName()));
        retval.putAll(convertFieldValues(getAccountsReceivableModuleService().createCustomerAddress().getClass(), fieldValues, "customerAddresses.", TemProfileFromCustomer.class.getName()));
        // Only looking for primary addresses currently. Will need to modify to search all addresses with KUALITEM-467
        retval.put(TemPropertyConstants.CUSTOMER_ADDRESS_ADDRESS_TYPE_CODE, TemConstants.CUSTOMER_PRIMARY_ADDRESS_TYPE_CODE);

        //Restrict the search to only return customers of type "Traveler"
        List<AccountsReceivableCustomerType> customerTypes = getAccountsReceivableModuleService().findByCustomerTypeDescription(TemConstants.CUSTOMER_TRAVLER_TYPE_CODE);
        //should only have 1 with matching type description
        retval.put(TemPropertyConstants.CUSTOMER_TYPE_CODE, customerTypes.get(0).getCustomerTypeCode());

        //Removing the customerEmailAddress so the search only happens with the email address in the customer address like the AR Customer search
        retval.remove(TemPropertyConstants.CUSTOMER_EMAIL_ADDRESS);

        return retval;
    }

    private Map<String, String> convertFieldValues(Class<? extends BusinessObject> boClass, Map<String, String> fieldValues, String prefix, String lookupClassName) {
        Map<String, String> retval = new HashMap<String, String>();

        LOG.debug("Converting field values " + fieldValues);

        for (final FieldDefinition lookupField : getLookupFieldsFor(lookupClassName)) {
            String attrName = lookupField.getAttributeName();

            if (lookupField instanceof MappedDefinition) {
                final MappedDefinition mappedField = (MappedDefinition) lookupField;
                final String key = mappedField.getAttributeMap().get(boClass.getSimpleName());
                String value = fieldValues.get(attrName);

                if (retval.containsKey(key)) {
                    value = retval.get(key) + value;
                }
                if (key != null) {
                    retval.put(prefix+key, value);
                }
                else {
                    LOG.warn("Got a null key for attribute name " + attrName);
                }
            }
            else if (containsAttribute(boClass, attrName)) {
                retval.put(prefix+attrName, fieldValues.get(attrName));
            }
        }

        return retval;
    }

    /**
     * Performs a customer search and converts the results to {@link TemProfileFromCustomer} instances
     *
     * @param fieldValues
     * @return List of {@link TemProfileFromCustomer} instances
     */
    protected List<TemProfileFromCustomer> getCustomers(final Map<String, String> fieldValues) {

        final List<TemProfileFromCustomer> profiles = new ArrayList<TemProfileFromCustomer>();

        final Map<String, String> fieldsForLookup = this.getCustomerFieldValues(fieldValues);

        final Map<String, String> lookupFieldValues = new HashMap<String, String>();
        LOG.debug("Using fieldsForLookup " + fieldsForLookup);

        final Collection<AccountsReceivableCustomer> customers = getTravelerDao().findCustomersBy(fieldsForLookup);

        for (final AccountsReceivableCustomer customer : customers) {
            profiles.add(getTravelerService().convertToTemProfileFromCustomer(customer));
        }

        return profiles;
    }

    protected boolean containsAttribute(final Class boClass, final String attributeName) {
        return getDataDictionaryService().isAttributeDefined(boClass, attributeName);
    }

    private Collection<FieldDefinition>  getLookupFieldsFor(String className) {
        BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(className);
        return businessObjectEntry.getLookupDefinition().getLookupFields();
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = super.getCustomActionUrls(businessObject, pkNames);
        String customerNumber = ((TemProfileFromCustomer) businessObject).getCustomerNumber();

        Properties parameters = new Properties();
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TemProfile.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, "customerNumber");
        parameters.put(KFSConstants.REFRESH_CALLER, "customerNumber" + "::" + customerNumber);
        parameters.put("customerNumber", customerNumber);


        Map<String,String> criteria = new HashMap<String,String>(2);
        criteria.put("customerNumber", customerNumber);
        criteria.put("active", "true");

        // If an active TEM Profile doesn't exist, display a create link
        if (getTemProfileService().findTemProfile(criteria) == null) {
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL);

            String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
            AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, "start", "create new profile");
            htmlDataList.add(anchorHtmlData);
        }
        else {
            // An active TEM Profile exists, display an edit link
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);

            String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
            AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, "start", "edit profile");
            htmlDataList.add(anchorHtmlData);
        }

        return htmlDataList;
    }

    /**
     * Gets the travelerService attribute.
     * @return Returns the travelerService.
     */
    public TravelerService getTravelerService() {
        return travelerService;
    }

    /**
     * Sets the travelerService attribute value.
     * @param travelerService The travelerService to set.
     */
    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    /**
     * Gets the travelerDao attribute.
     * @return Returns the travelerDao.
     */
    public TravelerDao getTravelerDao() {
        return travelerDao;
    }

    /**
     * Sets the travelerDao attribute value.
     * @param travelerDao The travelerDao to set.
     */
    public void setTravelerDao(TravelerDao travelerDao) {
        this.travelerDao = travelerDao;
    }

    /**
     * Gets the temProfileService attribute.
     * @return Returns the temProfileService.
     */
    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    /**
     * Sets the temProfileService attribute value.
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }

        return accountsReceivableModuleService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }
}
