/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Check for Arranger Derived Role base on the TEM Profile ID and the Principal
 */
@SuppressWarnings("deprecation")
public class ArrangerDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    protected TravelArrangerDocumentService arrangerDocumentService;

    /**
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#hasDerivedRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String,String> qualification) {
        //first we need to grab the profileId if it exists
        if(qualification!=null && !qualification.isEmpty()){
            final String profileId = qualification.get(TemPropertyConstants.TemProfileProperties.PROFILE_ID);
            final String documentType = qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            if(!StringUtils.isBlank(profileId)) {

                final Integer profIdAsInt = new Integer(profileId);
                final TemProfileArranger arranger = getArrangerDocumentService().findTemProfileArranger(principalId, profIdAsInt);
                if (arranger != null){
                    if (!StringUtils.isBlank(documentType)) {
                        if (TravelDocTypes.getAuthorizationDocTypes().contains(documentType)){
                            return arranger.getTaInd();
                        } else if (TravelDocTypes.getReimbursementDocTypes().contains(documentType)){
                            return arranger.getTrInd();
                        } else if (documentType.equals(TemConstants.TravelDocTypes.TRAVEL_PROFILE_DOCUMENT)) {
                            if (arranger.getPrincipalId().equals(principalId)) {
                                return true;
                            }
                        }
                    } else {
                        // we are coming from the TEM Profile lookup and are checking if we are an arranger and therefore can edit this profile.
                        if (arranger.getPrincipalId().equals(principalId)) {
                            return true;
                        }
                    }
                }
            } else {
                // do we have a profile principal id?
                final String profilePrincipalId = qualification.get(KfsKimAttributes.PROFILE_PRINCIPAL_ID);
                if (!StringUtils.isBlank(profilePrincipalId)) {
                    // in this case, we're just wondering if the user is an assigned arranger in general
                    return getArrangerDocumentService().hasArrangees(profilePrincipalId);
                }
            }
        }

        //Because workflow (route/save/copy) would not pick up the qualifer from Document Authorizor, but ONLY base on the permission template, we will
        //simply check whether the person is an arranger (not particularly tied to a profile)
        if(StringUtils.isNotBlank(principalId) && (qualification == null || !qualification.containsKey(TemKimAttributes.PROFILE_ID) || qualification.get(TemKimAttributes.PROFILE_ID) == null)) {
            Map fieldValues = new HashMap();
            fieldValues.put(TemPropertyConstants.TemProfileProperties.PRINCIPAL_ID, principalId);
            fieldValues.put(KFSPropertyConstants.ACTIVE, "Y");
            Collection<TemProfileArranger> arrangers = getBusinessObjectService().findMatching(TemProfileArranger.class, fieldValues);
            //List<TemProfileArranger> profileArrangers = new ArrayList<TemProfileArranger>( getBusinessObjectService().findMatching(TemProfileArranger.class, fieldValues));
            return ObjectUtils.isNotNull(arrangers) && !arrangers.isEmpty();
        }
        return false;
    }

    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        final List<RoleMembership> members = new ArrayList<RoleMembership>(1);
        return members;
    }

    /**
     * @return the injected implementation of TravelArrangerDocumentService
     */
    public TravelArrangerDocumentService getArrangerDocumentService() {
        return arrangerDocumentService;
    }

    /**
     * Injects an implementation of TravelArrangerDocumentService
     * @param arrangerDocumentService the implementation of TravelArrangerDocumentService to utilize
     */
    public void setArrangerDocumentService(TravelArrangerDocumentService arrangerDocumentService) {
        this.arrangerDocumentService = arrangerDocumentService;
    }
}
