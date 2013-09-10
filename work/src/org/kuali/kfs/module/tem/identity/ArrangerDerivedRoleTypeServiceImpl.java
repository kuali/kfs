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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfileArranger;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
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
            final String profileId = qualification.get(TEMProfileProperties.PROFILE_ID);
            final String documentType = qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            if(!StringUtils.isBlank(profileId)) {

                final Integer profIdAsInt = new Integer(profileId);
                final TEMProfileArranger arranger = getArrangerDocumentService().findTemProfileArranger(principalId, profIdAsInt);
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
            }
        }

        //Because workflow (route/save/copy) would not pick up the qualifer from Document Authorizor, but ONLY base on the permission template, we will
        //simply check whether the person is an arranger (not particularly tied to a profile)
        if(StringUtils.isNotBlank(principalId) && (qualification == null || !qualification.containsKey(TemKimAttributes.PROFILE_ID) || qualification.get(TemKimAttributes.PROFILE_ID) == null)) {
            Map fieldValues = new HashMap();
            fieldValues.put(TEMProfileProperties.PRINCIPAL_ID, principalId);
            fieldValues.put(KFSPropertyConstants.ACTIVE, "Y");
            Collection<TEMProfileArranger> arrangers = getBusinessObjectService().findMatching(TEMProfileArranger.class, fieldValues);
            //List<TEMProfileArranger> profileArrangers = new ArrayList<TEMProfileArranger>( getBusinessObjectService().findMatching(TEMProfileArranger.class, fieldValues));
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
