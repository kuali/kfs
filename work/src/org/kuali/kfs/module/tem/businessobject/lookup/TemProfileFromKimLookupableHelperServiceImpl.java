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
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromKimPerson;
import org.kuali.kfs.module.tem.service.TEMRoleService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.identity.PersonImpl;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;

@SuppressWarnings("rawtypes")
public class TemProfileFromKimLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    public static Logger LOG = Logger.getLogger(TemProfileFromKimLookupableHelperServiceImpl.class);

    private TravelerService travelerService;
    private PersonService personService;
    private TemProfileService temProfileService;

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
        List<TemProfileFromKimPerson> searchResults = new ArrayList<TemProfileFromKimPerson>();

        //final Map<String, String> kimFieldsForLookup = fieldValues;
        final Map<String, String> kimFieldsForLookup = getPersonFieldValues(fieldValues);

        LOG.debug("Looking up people with criteria " + kimFieldsForLookup);
        final List<? extends Person> persons = personService.findPeople(kimFieldsForLookup);

        for (Person personDetail : persons) {
            searchResults.add(travelerService.convertToTemProfileFromKim(personDetail));
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
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getBusinessObjectClass()
     */
    @Override
    public Class getBusinessObjectClass() {
        return TemProfileFromKimPerson.class;
    }

    /**
     * Generates a {@link Map} of field values where object properties are mapped to values for
     * search purposes.
     *
     * @param fieldValues original field values passed to the lookup
     * @return Map of values
     */
    protected Map<String, String> getPersonFieldValues(final Map<String, String> fieldValues) {
        BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(PersonImpl.class.getName());
        Collection<FieldDefinition> lookupFields = businessObjectEntry.getLookupDefinition().getLookupFields();

        Map<String, String> personFieldValues = new HashMap<String, String>();
        for (final FieldDefinition lookupField : lookupFields) {
            final String attrName = lookupField.getAttributeName();
            if (containsAttribute(PersonImpl.class, attrName)) {
                personFieldValues.put(attrName, fieldValues.get(attrName));
            }
        }

        return personFieldValues;
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionary#getBusinessObjectEntry(String)
     */
    protected boolean containsAttribute(final Class boClass, final String attributeName) {
        return getDataDictionaryService().isAttributeDefined(boClass, attributeName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return super.getSearchResultsUnbounded(fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = super.getCustomActionUrls(businessObject, pkNames);


        TEMRoleService temRoleService = SpringContext.getBean(TEMRoleService.class);
        boolean profileAdmin = temRoleService.isProfileAdmin(GlobalVariables.getUserSession().getPerson(), ((TemProfileFromKimPerson)businessObject).getPrimaryDepartmentCode());

        if (!profileAdmin) {
            return htmlDataList;
        }

        String principalId = ((TemProfileFromKimPerson) businessObject).getPrincipalId();

        Properties parameters = new Properties();
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TEMProfile.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, "principalId");
        parameters.put(KFSConstants.REFRESH_CALLER, "principalId" + "::" + principalId);
        parameters.put("principalId", principalId);

        Map<String,String> criteria = new HashMap<String,String>(2);
        criteria.put("principalId", principalId);
        criteria.put("active", "true");

        // If an active TEM Profile doesn't exist, display a create link
        if (temProfileService.findTemProfile(criteria) == null) {
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);

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
     * Sets the personService attribute value.
     *
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     *
     * Sets the travelerService attribute
     * @param travelerService
     */
    public void setTravelerService(final TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    /**
     * Sets the temProfileService attribute value.
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

}
