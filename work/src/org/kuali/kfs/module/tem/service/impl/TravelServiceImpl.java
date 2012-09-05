/*
 * Copyright 2012 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.datadictionary.validation.charlevel.RegexValidationPattern;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Travel Service Implementation
 */
public class TravelServiceImpl implements TravelService {
    
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    private RoleService roleService;
    private TravelArrangerDocumentService arrangerDocumentService;
    private TravelDocumentDao travelDocumentDao;

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
        if (StringUtils.isBlank(countryCode) || (!countryCode.equals("US"))){
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
    
    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#checkUserRole(org.kuali.rice.kim.bo.Person, java.lang.String, java.lang.String, javax.print.attribute.AttributeSet)
     */
    public boolean checkUserTEMRole(final Person user, String role) {
        return checkUserRole(user, role, TemConstants.PARAM_NAMESPACE, null);
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#checkUserRole(org.kuali.rice.kim.bo.Person, java.lang.String, java.lang.String, javax.print.attribute.AttributeSet)
     */
    public boolean checkUserRole(final Person user, String role, String namespace, AttributeSet qualifications) {
        final String arrangerRoleId = roleService.getRoleIdByName(namespace, role);
        boolean hasRole = false;
        if (arrangerRoleId != null){
            List<String> roleIds = new ArrayList<String>();
            roleIds.add(arrangerRoleId);
             hasRole = roleService.principalHasRole(user.getPrincipalId(), roleIds, qualifications);
        }
        return hasRole;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#isUserInitiatorOrArranger(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.rice.kim.bo.Person)
     */
    public boolean isUserInitiatorOrArranger(TravelDocument document, Person user){
        boolean isUser = false;
        
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        String initiator = workflowDocument.getRouteHeader().getInitiatorPrincipalId();
        String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        
        if(initiator.equals(user.getPrincipalId()) || arrangerDocumentService.isTravelDocumentArrangerForProfile(docType, user.getPrincipalId(), document.getProfileId()) ) {
            isUser = true;
        }
        return isUser;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#findAllDistinctPrimaryDestinations(java.lang.String)
     */
    @Override
    public List<PrimaryDestination> findAllDistinctPrimaryDestinations(String tripType){
        return getTravelDocumentDao().findAllDistinctPrimaryDestinations(tripType);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#findDefaultPrimaryDestinations(java.lang.Class, java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List findDefaultPrimaryDestinations(Class clazz, String countryCode) {
        return getTravelDocumentDao().findDefaultPrimaryDestinations(clazz, countryCode);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(final ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        return accountsReceivableModuleService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }    
    
    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }
    
    protected TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    public RoleService getRoleService() {
        return roleService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public TravelArrangerDocumentService getArrangerDocumentService() {
        return arrangerDocumentService;
    }

    public void setArrangerDocumentService(TravelArrangerDocumentService arrangerDocumentService) {
        this.arrangerDocumentService = arrangerDocumentService;
    }
}
