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
package org.kuali.kfs.module.tem.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

@SuppressWarnings("rawtypes")
public class TravelArrangerDocumentServiceImpl implements TravelArrangerDocumentService {

    protected BusinessObjectService businessObjectService;

    @Override
    public void createTravelProfileArranger(TravelArrangerDocument arrangerDoc) {
        Integer profileId = arrangerDoc.getProfileId();
        String arrangerId = arrangerDoc.getArrangerId();

        TemProfileArranger profileArranger = findProfileArranger(arrangerId, profileId);
        if(ObjectUtils.isNull(profileArranger)) {
           profileArranger = createNewTravelProfileArranger(arrangerDoc);
        } else {
            //update the existing profile
            profileArranger.setActive(true);
            profileArranger.setPrimary(arrangerDoc.getPrimaryInd());
            profileArranger.setTaInd(arrangerDoc.getTaInd());
            profileArranger.setTrInd(arrangerDoc.getTrInd());
        }

        businessObjectService.save(profileArranger);
    }

    @Override
    public void inactivateTravelProfileArranger(TravelArrangerDocument arrangerDoc) {
        Integer profileId = arrangerDoc.getProfileId();
        String arrangerId = arrangerDoc.getArrangerId();
        TemProfileArranger profileArranger = findTemProfileArranger(arrangerId, profileId);
        if(ObjectUtils.isNotNull(profileArranger)) {
            profileArranger.setActive(Boolean.FALSE);
            businessObjectService.save(profileArranger);
        }

    }

    @Override
    public TemProfileArranger findPrimaryTravelProfileArranger(String arrangerId, Integer profileId) {
        Map fieldValues = new HashMap();
        fieldValues.put("profileId", profileId);

        List<TemProfileArranger> profileArrangers = new ArrayList<TemProfileArranger>( businessObjectService.findMatching(TemProfileArranger.class, fieldValues));

        for(TemProfileArranger profileArranger: profileArrangers) {
            if(profileArranger.getPrimary() && !profileArranger.getPrincipalId().equals(arrangerId)) {
                return profileArranger;
            }
        }
        return null;
    }

    /**
     *
     * @param arrangerDoc
     * @return
     */
    protected TemProfileArranger createNewTravelProfileArranger(TravelArrangerDocument arrangerDoc) {
        TemProfileArranger profileArranger = new TemProfileArranger();
        profileArranger.setActive(true);
        profileArranger.setProfileId(arrangerDoc.getProfileId());
        profileArranger.setPrincipalId(arrangerDoc.getArrangerId());
        profileArranger.setPrimary(arrangerDoc.getPrimaryInd());
        profileArranger.setTaInd(arrangerDoc.getTaInd());
        profileArranger.setTrInd(arrangerDoc.getTrInd());

        return profileArranger;
    }

    protected TemProfileArranger findProfileArranger(String principalId, Integer profileId) {
        Map fieldValues = new HashMap();
        fieldValues.put(TemProfileProperties.PRINCIPAL_ID, principalId);
        fieldValues.put(TemProfileProperties.PROFILE_ID, profileId);

        List<TemProfileArranger> profileArrangers = new ArrayList<TemProfileArranger>( businessObjectService.findMatching(TemProfileArranger.class, fieldValues));
        if(profileArrangers.size() == 1) {
            return profileArrangers.get(0);
        } else if (profileArrangers.size() == 0) {
            return null;
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#findTemProfileArranger(java.lang.Integer, java.lang.String)
     */
    @Override
    public TemProfileArranger findTemProfileArranger(String principalId, Integer profileId) {
        Map fieldValues = new HashMap();
        fieldValues.put(TemProfileProperties.PRINCIPAL_ID, principalId);
        fieldValues.put(TemProfileProperties.PROFILE_ID, profileId);
        //find active profile arrangers only
        fieldValues.put(KRADPropertyConstants.ACTIVE, "Y");

        List<TemProfileArranger> profileArrangers = new ArrayList<TemProfileArranger>( businessObjectService.findMatching(TemProfileArranger.class, fieldValues));
        if(profileArrangers.size() == 1) {
            return profileArrangers.get(0);
        } else if (profileArrangers.size() == 0) {
            return null;
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService#hasArrangees(java.lang.String)
     */
    @Override
    public boolean hasArrangees(String principalId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(TemProfileProperties.PRINCIPAL_ID, principalId);
        fieldValues.put(KRADPropertyConstants.ACTIVE, "Y");

        final int arrangeeCount = businessObjectService.countMatching(TemProfileArranger.class, fieldValues);
        return arrangeeCount > 0;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
