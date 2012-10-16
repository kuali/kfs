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
package org.kuali.kfs.module.tem.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfileArranger;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;

@SuppressWarnings("rawtypes")
public class TravelArrangerDocumentServiceImpl implements TravelArrangerDocumentService {

    private BusinessObjectService boService;
    
    @Override
    public void createTravelProfileArranger(TravelArrangerDocument arrangerDoc) {
        Integer profileId = arrangerDoc.getProfileId();
        String arrangerId = arrangerDoc.getArrangerId();
        
        TEMProfileArranger profileArranger = findTemProfileArranger(arrangerId, profileId);
        if(ObjectUtils.isNull(profileArranger)) {
           profileArranger = createNewTravelProfileArranger(arrangerDoc);
        } else {
            //update the existing profile
            profileArranger.setActive(true);
            profileArranger.setPrimary(arrangerDoc.getPrimaryInd());
            profileArranger.setTaInd(arrangerDoc.getTaInd());
            profileArranger.setTrInd(arrangerDoc.getTrInd());
        }

        getBoService().save(profileArranger);
        
    }

    @Override
    public void removeTravelProfileArranger(TravelArrangerDocument arrangerDoc) {
        Integer profileId = arrangerDoc.getProfileId();
        String arrangerId = arrangerDoc.getArrangerId();
        TEMProfileArranger profileArranger = findTemProfileArranger(arrangerId, profileId);
        if(ObjectUtils.isNotNull(profileArranger)) {
            getBoService().delete(profileArranger);
        }
        
    }
    
    @Override
    public TEMProfileArranger findPrimaryTravelProfileArranger(String arrangerId, Integer profileId) {
        Map fieldValues = new HashMap();
        fieldValues.put("profileId", profileId);
        
        List<TEMProfileArranger> profileArrangers = new ArrayList<TEMProfileArranger>( getBoService().findMatching(TEMProfileArranger.class, fieldValues));
        
        for(TEMProfileArranger profileArranger: profileArrangers) {
            if(profileArranger.getPrimary() && !profileArranger.getPrincipalId().equals(arrangerId)) {
                return profileArranger;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#isArrangerForProfile(java.lang.String, int)
     */
    @Override
    public boolean isArrangerForProfile(String principalId, int profileId) {
        boolean isArranger = false;
        if(ObjectUtils.isNotNull(profileId) && ObjectUtils.isNotNull(principalId)) {
            isArranger = ObjectUtils.isNotNull(findTemProfileArranger(principalId, profileId));
        }
        
        return isArranger;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#isTravelDocumentArrangerForProfile(java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean isTravelDocumentArrangerForProfile(String documentType, String principalId, int profileId) {
        boolean isTravelArranger = false;

        TEMProfileArranger arranger = findTemProfileArranger(principalId, profileId);
        if (arranger != null){
            List<String> authorizationDocTypes = new ArrayList<String>();
            authorizationDocTypes.add(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
            authorizationDocTypes.add(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
            authorizationDocTypes.add(TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
            
            if (authorizationDocTypes.contains(documentType)){
                isTravelArranger = arranger.getTaInd();
            }else if (TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(documentType)){
                isTravelArranger = arranger.getTrInd();
            }
        }
        return isTravelArranger;
    }

    @Override
    public boolean isArranger(String arrangerId) {
        if(ObjectUtils.isNotNull(arrangerId)) {
            Map fieldValues = new HashMap();
            fieldValues.put("principalId", arrangerId);
            List<TEMProfileArranger> profileArrangers = new ArrayList<TEMProfileArranger>( getBoService().findMatching(TEMProfileArranger.class, fieldValues));
            
            if(ObjectUtils.isNotNull(profileArrangers) && profileArrangers.size() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    private TEMProfileArranger createNewTravelProfileArranger(TravelArrangerDocument arrangerDoc) {
        TEMProfileArranger profileArranger = new TEMProfileArranger();
        profileArranger.setActive(true);
        profileArranger.setProfileId(arrangerDoc.getProfileId());
        profileArranger.setPrincipalId(arrangerDoc.getArrangerId());
        profileArranger.setPrimary(arrangerDoc.getPrimaryInd());
        profileArranger.setTaInd(arrangerDoc.getTaInd());
        profileArranger.setTrInd(arrangerDoc.getTrInd());
        
        return profileArranger;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#findTemProfileArranger(java.lang.Integer, java.lang.String)
     */
    @Override
    public TEMProfileArranger findTemProfileArranger(String principalId, Integer profileId) {
        Map fieldValues = new HashMap();
        fieldValues.put(TEMProfileProperties.PRINCIPAL_ID, principalId);
        fieldValues.put(TEMProfileProperties.PROFILE_ID, profileId);
        
        //find active profile arrangers only
        fieldValues.put(KNSPropertyConstants.ACTIVE, "Y");
        
        List<TEMProfileArranger> profileArrangers = new ArrayList<TEMProfileArranger>( getBoService().findMatching(TEMProfileArranger.class, fieldValues));
        if(profileArrangers.size() == 1) {
            return profileArrangers.get(0);
        } else if (profileArrangers.size() == 0) {
            return null;
        }
        return null;
    }

    /**
     * Gets the boService attribute. 
     * @return Returns the boService.
     */
    public BusinessObjectService getBoService() {
        return boService;
    }

    /**
     * Sets the boService attribute value.
     * @param boService The boService to set.
     */
    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }    

}
