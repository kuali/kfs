/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service.impl;

import static org.apache.commons.lang.StringUtils.isBlank;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.validation.charlevel.RegexValidationPattern;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Travel Service Implementation
 */
public class TravelServiceImpl implements TravelService {
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    protected TravelDocumentDao travelDocumentDao;
    /**
     * Sets the travelDocumentDao attribute value.
     * @param travelDocumentDao The travelDocumentDao to set.
     */
    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    /**
     * Validate a phone number
     *
     * @param phoneNumber to validate
     */
    @Override
    public String validatePhoneNumber(final String phoneNumber, String error){
        return validatePhoneNumber("", phoneNumber, error);
    }

    /**
     * Validate a phone number
     * 
     * @param countryCode to consider for validation
     * @param phoneNumber to validate
     */
    @Override
    public String validatePhoneNumber(final String countryCode, final String phoneNumber, String error){
        
        //Determine if the US phone format should be used or a very lax international format.
        if (isBlank(countryCode) || (!countryCode.equals("US"))){
            RegexValidationPattern pattern = new RegexValidationPattern();
            pattern.setPattern(TemConstants.INT_PHONE_PATTERN);
            if (phoneNumber != null && pattern.matches(phoneNumber)){
                return "";
            }
            else{
                return error;
            }
        }
        else{
            RegexValidationPattern pattern = new RegexValidationPattern();
            pattern.setPattern(TemConstants.US_PHONE_PATTERN);
            if (pattern.matches(phoneNumber)){
                return "";
            }
            else{
                return error;
            }
        }
    }
    
    @Override
    public TEMProfile findTemProfileByPrincipalId(String principalId) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put("principalId", principalId);
        Collection<TEMProfile> profiles = getBusinessObjectService().findMatching(TEMProfile.class, criteria);
        if(ObjectUtils.isNotNull(profiles) && profiles.size() > 0) {
            return profiles.iterator().next();
        }
        return null;
    }

    @Override
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(final ParameterService parameterService) {
        this.parameterService = parameterService;
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
    
    @Override
    public List<PrimaryDestination> findAllDistinctPrimaryDestinations(String tripType){
        return getTravelDocumentDao().findAllDistinctPrimaryDestinations(tripType);
    }
    
    protected TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    @Override
    public List findDefaultPrimaryDestinations(Class clazz, String countryCode) {
        // TODO Auto-generated method stub
        return getTravelDocumentDao().findDefaultPrimaryDestinations(clazz, countryCode);
    }
}
