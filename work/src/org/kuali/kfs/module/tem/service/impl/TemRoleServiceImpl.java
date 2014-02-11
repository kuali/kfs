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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.module.tem.identity.TemKimAttributes;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class TemRoleServiceImpl implements TemRoleService{

    public static Logger LOG = Logger.getLogger(TemRoleServiceImpl.class);

    RoleService roleService;
    BusinessObjectService businessObjectService;
    TravelArrangerDocumentService arrangerDocumentService;

    /**
     * @see org.kuali.kfs.module.tem.service.TemRoleService#canAccessTravelDocument(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean canAccessTravelDocument(TravelDocument travelDocument, Person currentUser){

        boolean canAccess = false;
        String initiatorId = travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();

        if (initiatorId.equals(currentUser.getPrincipalId())) {
            canAccess = true;
        }
        else if (ObjectUtils.isNotNull(travelDocument.getTemProfileId())) {

            //Get the profile from the document
            TemProfile profile = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(TemProfile.class, travelDocument.getTemProfileId());

            if (ObjectUtils.isNotNull(profile)){

                //user is the traveler
                if (currentUser.getPrincipalId().equals(profile.getPrincipalId())) {
                    canAccess = true;
                }
                else {

                    String profileId = travelDocument.getTemProfileId().toString();
                    String documentType = travelDocument.getDocumentTypeName();

                    //profile exists and user does not have access as the arranger
                    if(isTravelArranger(currentUser, profile.getHomeDepartment(), profileId, documentType)) {
                        canAccess = true;
                    }
                }
            }
        }

        return canAccess;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemRoleService#isArrangerForProfile(java.lang.String, int)
     */
    @Override
    public boolean isArrangerForProfile(String principalId, int profileId) {
        boolean isArranger = false;
        if(ObjectUtils.isNotNull(profileId) && ObjectUtils.isNotNull(principalId)) {
            isArranger = ObjectUtils.isNotNull(arrangerDocumentService.findTemProfileArranger(principalId, profileId));
        }

        return isArranger;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#isTravelDocumentArrangerForProfile(java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean isTravelDocumentArrangerForProfile(String documentType, String principalId, Integer profileId) {
        boolean isTravelArranger = false;

        TemProfileArranger arranger = arrangerDocumentService.findTemProfileArranger(principalId, profileId);
        if (arranger != null){
            if (TravelDocTypes.getAuthorizationDocTypes().contains(documentType)){
                isTravelArranger = arranger.getTaInd();
            }else if (TravelDocTypes.getReimbursementDocTypes().contains(documentType)){
                isTravelArranger = arranger.getTrInd();
            }
        }
        return isTravelArranger;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemRoleService#isProfileArranger(java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isProfileArranger(String arrangerId) {
        boolean isProfileArranger = false;
        if(StringUtils.isNotBlank(arrangerId)) {
            Map fieldValues = new HashMap();
            fieldValues.put(TemProfileProperties.PRINCIPAL_ID, arrangerId);
            List<TemProfileArranger> profileArrangers = new ArrayList<TemProfileArranger>( businessObjectService.findMatching(TemProfileArranger.class, fieldValues));
            isProfileArranger = ObjectUtils.isNotNull(profileArrangers) && !profileArrangers.isEmpty();
        }
        return isProfileArranger;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemRoleService#isProfileAdmin(org.kuali.rice.kim.bo.Person, java.lang.String)
     */
    @Override
    public boolean isProfileAdmin(Person currentUser, String homeDepartment) {
        boolean hasAdminRole = checkUserRole(currentUser, TemConstants.TemRoleNames.TEM_PROFILE_ADMINISTRATOR, TemConstants.PARAM_NAMESPACE, null);
        boolean hasOrgRole = checkOrganizationRole(currentUser, TemConstants.TemRoleNames.TEM_PROFILE_ADMINISTRATOR, TemConstants.PARAM_NAMESPACE, homeDepartment);

        return hasAdminRole && hasOrgRole;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#isTravelArranger(org.kuali.rice.kim.bo.Person, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean isTravelArranger(final Person user, final String primaryDepartmentCode, String profileId, String documentType) {

        //add the role qualification
        Map<String,String> qualification = new HashMap<String,String>();
        if (isProfileAlsoUser(user, profileId)) {
            qualification.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, user.getPrincipalId());
        } else {
            qualification.put(TemProfileProperties.PROFILE_ID, profileId);
        }
        qualification.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME, documentType);

        boolean checkProfileAssignedRole = checkUserRole(user, TemConstants.TEM_ASSIGNED_PROFILE_ARRANGER, TemConstants.PARAM_NAMESPACE, qualification);
        boolean checkOrgRole = checkOrganizationRole(user, TemConstants.TEM_ORGANIZATION_PROFILE_ARRANGER, TemConstants.PARAM_NAMESPACE, primaryDepartmentCode);

        //user is an arranger if either check is successful
        return checkProfileAssignedRole || checkOrgRole;
    }

    /**
     * Determines if the given profile id represents the profile of the given user
     * @param user a user
     * @param profileId a profile id
     * @return true if the profile and the user represent the same entity; false otherwise
     */
    protected boolean isProfileAlsoUser(Person user, String profileId) {
        final TemProfile profile = getProfile(profileId);
        return (profile != null && StringUtils.equals(profile.getPrincipalId(), user.getPrincipalId()));
    }

    /**
     * Looks up the active profile for the given profile id; null if nothing is found or the profile is not active
     * @param profileId the profile id to look up
     * @return
     */
    protected TemProfile getProfile(String profileId) {
        final TemProfile profile = businessObjectService.findBySinglePrimaryKey(TemProfile.class, profileId);
        if (profile != null && profile.isActive()) {
            return profile;
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#isTravelArranger(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean isTravelArranger(final Person user) {
        Map fieldValues = new HashMap();
        fieldValues.put(TemPropertyConstants.TemProfileProperties.PRINCIPAL_ID, user.getPrincipalId());
        fieldValues.put(KFSPropertyConstants.ACTIVE, "Y");
        int count =  businessObjectService.countMatching(TemProfileArranger.class, fieldValues);
        boolean isArranger = count > 0 ? true : false;

        boolean checkOrgRole = checkOrganizationRole(user, TemConstants.TEM_ORGANIZATION_PROFILE_ARRANGER, TemConstants.PARAM_NAMESPACE, null);
        //user is an arranger if either check is successful
        return isArranger || checkOrgRole;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#isTravelManager(org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean isTravelManager(final Person user) {
        return checkUserRole(user, TemConstants.TRAVEL_MANAGER, KFSConstants.CoreModuleNamespaces.KFS, null);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemRoleService#checkUserTemRole(org.kuali.rice.kim.bo.Person, java.lang.String)
     */
    @Override
    public boolean checkUserTEMRole(final Person user, String role){
        return checkUserRole(user, role, TemConstants.PARAM_NAMESPACE, null);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemRoleService#checkUserRole(org.kuali.rice.kim.bo.Person, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.Map<String,String>)
     */
    @Override
    public boolean checkUserRole(final Person user, String role, String parameterNamespace, Map<String,String> qualification){
        try{
            List<String> roleIds = new ArrayList<String>();
            roleIds.add(roleService.getRoleIdByNamespaceCodeAndName(parameterNamespace, role));
            return roleService.principalHasRole(user.getPrincipalId(), roleIds, qualification);
        }
        catch (NullPointerException e) {
            LOG.error(e);
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemRoleService#checkOrganizationRole(org.kuali.rice.kim.bo.Person, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkOrganizationRole(final Person user, String role, String parameterNamespace, String primaryDepartmentCode){
        try{
            List<String> roleIds = new ArrayList<String>();
            roleIds.add(roleService.getRoleIdByNamespaceCodeAndName(parameterNamespace, role));

            Map<String,String> qualification = null;
            String chartOfAccounts, organizationCode = null;
            if (StringUtils.isNotEmpty(primaryDepartmentCode)) {
                String[] split = primaryDepartmentCode.split("-");
                if(split != null){
                    chartOfAccounts = split[0];
                    qualification = new HashMap<String,String>();
                    qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccounts);
                    qualification.put("performQualifierMatch", "True");

                    if(split.length == 2){
                        organizationCode = split[1];

                        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
                    }
                }
            }

            if (roleService.principalHasRole(user.getPrincipalId(), roleIds, qualification)) {
                return true;
            }
        }
        catch (NullPointerException e) {
            LOG.error(e);
        }
        return false;
    }

    /**
     * This method gets the contract manager user identifier.
     * @return contractManagerId
     */
    @Override
    public Collection<RoleMembership> getTravelArrangers(String chartCode, String orgCode) {
       // String contractManagerId = null;
        Map<String,String> qualification = new HashMap<String,String>();
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartCode);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, orgCode);
        qualification.put(KfsKimAttributes.DESCEND_HIERARCHY, "Y");

        RoleService roleService = KimApiServiceLocator.getRoleService();
        String roleId = roleService.getRoleIdByNamespaceCodeAndName(TemConstants.PARAM_NAMESPACE, TemConstants.TemRoleNames.TEM_ORGANIZATION_PROFILE_ARRANGER);

        Collection<RoleMembership> roleMemberships = roleService.getRoleMembers(Collections.singletonList(roleId), qualification);

        return roleMemberships;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setArrangerDocumentService(TravelArrangerDocumentService travelArrangerDocumentService) {
        this.arrangerDocumentService = travelArrangerDocumentService;
    }


}
