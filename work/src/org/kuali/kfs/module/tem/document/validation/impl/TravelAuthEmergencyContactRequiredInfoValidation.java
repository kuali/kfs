/*
 * Copyright 2010 The Kuali Foundation.
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

import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_AUTH_EM_CONTACT_CONTACT_NAME;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_AUTH_EM_CONTACT_PHONE_NUM;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_AUTH_EM_CONTACT_REL_TYPE;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.validation.event.AddEmergencyContactLineEvent;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthEmergencyContactRequiredInfoValidation extends GenericValidation {
    protected TravelService travelService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        TravelDocumentBase taDocument = (TravelDocumentBase) event.getDocument();
        boolean valid = true;
        TravelerDetailEmergencyContact emergencyContact = ((AddEmergencyContactLineEvent) event).getEmergencyContact();
        // Ensure all fields are filled in before attempting to add a new emergency contact line
        if (StringUtils.isBlank(emergencyContact.getContactName())) {
            GlobalVariables.getMessageMap().putError(TRVL_AUTH_EM_CONTACT_CONTACT_NAME, KFSKeyConstants.ERROR_REQUIRED, "Contact Name");
            valid &= false;
        }
        if (StringUtils.isBlank(emergencyContact.getPhoneNumber())) {
            GlobalVariables.getMessageMap().putError(TRVL_AUTH_EM_CONTACT_PHONE_NUM, KFSKeyConstants.ERROR_REQUIRED, "Contact Phone Number");
            valid &= false;
        }
        else {
            String errorMessage = getTravelService().validatePhoneNumber(emergencyContact.getPhoneNumber(), TemKeyConstants.ERROR_EMERGENCY_PHONE_NUMBER);
            if (!StringUtils.isBlank(errorMessage)) {
                GlobalVariables.getMessageMap().putError(TRVL_AUTH_EM_CONTACT_PHONE_NUM, errorMessage, new String[] { "Contact Phone Number"});
                valid &= false;
            }
        }
        if (ObjectUtils.isNull(emergencyContact.getContactRelationTypeCode())) {
            GlobalVariables.getMessageMap().putError(TRVL_AUTH_EM_CONTACT_REL_TYPE, KFSKeyConstants.ERROR_REQUIRED, "Relationship");
            valid &= false;
        }

        return valid;
    }

    /**
     * Gets the travelService attribute.
     * @return Returns the travelService.
     */
    public TravelService getTravelService() {
        return travelService;
    }

    /**
     * Sets the travelService attribute value.
     * @param travelService The travelService to set.
     */
    public void setTravelService(TravelService travelService) {
        this.travelService = travelService;
    }

}