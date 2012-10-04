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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromCustomer;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromKimPerson;
import org.kuali.kfs.module.tem.datadictionary.MappedDefinition;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;

public class TEMProfileAccountLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    public static Logger LOG = Logger.getLogger(TEMProfileAccountLookupableHelperServiceImpl.class);

    private TravelerService travelerService;
    private TravelService travelService;
    private PersonService<Person> personService;
    private IdentityManagementService identityManagementService;
    private TemProfileService temProfileService;

    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        TEMProfileAccount account = (TEMProfileAccount) businessObject;
        List primaryKeys = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(account.getProfile().getClass());
        htmlDataList.add(getUrlData(account.getProfile(), KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, primaryKeys));
        
        return htmlDataList;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceEditAction(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
        // TODO Auto-generated method stub
        return super.allowsMaintenanceEditAction(businessObject);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // TODO Auto-generated method stub
        String principalId = fieldValues.get("profile.principal.principalId");
        fieldValues.remove("profile.principal.principalId");
        fieldValues.put("profile.principalId", principalId);
        return super.getSearchResults(fieldValues);
    }

    
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSupplementalMenuBar()
     */
    @Override
    public String getSupplementalMenuBar() {
        // TODO Auto-generated method stub
        return super.getSupplementalMenuBar();
    }

    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Gets the travelerService attribute.
     * 
     * @return Returns the travelerService.
     */
    public TravelerService getTravelerService() {
        return travelerService;
    }

    /**
     * Sets the travelerService attribute value.
     * 
     * @param travelerService The travelerService to set.
     */
    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }


    /**
     * Sets the personService attribute value.
     * 
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService<Person> personService) {
        this.personService = personService;
    }

    /**
     * Gets the personService attribute.
     * 
     * @return Returns the personService.
     */
    public PersonService<Person> getPersonService() {
        return personService;
    }

    /**
     * Gets the travelService attribute.
     * 
     * @return Returns the travelService.
     */
    public TravelService getTravelService() {
        return travelService;
    }

    /**
     * Sets the travelService attribute value.
     * 
     * @param travelService The travelService to set.
     */
    public void setTravelService(TravelService travelService) {
        this.travelService = travelService;
    }


    /**
     * Gets the identityManagementService attribute.
     * 
     * @return Returns the identityManagementService.
     */
    public IdentityManagementService getIdentityManagementService() {
        return identityManagementService;
    }


    /**
     * Sets the identityManagementService attribute value.
     * 
     * @param identityManagementService The identityManagementService to set.
     */
    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }

    /**
     * Gets the temProfileService attribute.
     * 
     * @return Returns the temProfileService.
     */
    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    /**
     * Sets the temProfileService attribute value.
     * 
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

}
