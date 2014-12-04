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
