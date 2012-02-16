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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.ArrangerFields;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelerOnlyOnePrimaryArranger extends GenericValidation {
    private TravelArrangerDocumentService arrangerDocumentService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        TravelArrangerDocument document = (TravelArrangerDocument)event.getDocument();
        Integer profileId = document.getProfileId();
        String arrangerId = document.getArrangerId();
        Person arranger = null;
        
        if(!document.getPrimaryInd()) {
            return success;
        }
        if(ObjectUtils.isNotNull(profileId)) {
            document.refreshReferenceObject("profile");
        }
        
        if(ObjectUtils.isNotNull(arrangerId)) {
            arranger = SpringContext.getBean(PersonService.class).getPerson(arrangerId);
        }
        
        if(ObjectUtils.isNotNull(arrangerDocumentService.findPrimaryTravelProfileArranger(arrangerId, profileId))) {
            GlobalVariables.getMessageMap().putError(ArrangerFields.PRIMARY_ARRANGER, TemKeyConstants.ERROR_TTA_ARRGR_ONE_PRIMARY);
            return false;
        }
        
        return success;
    }

    /**
     * Gets the arrangerDocumentService attribute. 
     * @return Returns the arrangerDocumentService.
     */
    public TravelArrangerDocumentService getArrangerDocumentService() {
        return arrangerDocumentService;
    }

    /**
     * Sets the arrangerDocumentService attribute value.
     * @param arrangerDocumentService The arrangerDocumentService to set.
     */
    public void setArrangerDocumentService(TravelArrangerDocumentService arrangerDocumentService) {
        this.arrangerDocumentService = arrangerDocumentService;
    }
    
    

}
