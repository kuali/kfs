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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthEmergencyContactRequiredValidation extends GenericValidation {
    MessageMap mm = GlobalVariables.getMessageMap();

    //@Override
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        ParameterService paramService = SpringContext.getBean(ParameterService.class);

        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)event.getDocument();
        taDocument.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = taDocument.getTripType();

        if (paramService.getParameterValueAsBoolean(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.ENABLE_CONTACT_INFORMATION_IND) && ObjectUtils.isNotNull(tripType)) {
            if (tripType.isContactInfoRequired()  && (taDocument.getDocumentHeader().getWorkflowDocument().isInitiated() || taDocument.getDocumentHeader().getWorkflowDocument().isSaved())) {
                rulePassed = validEmergencyContact(taDocument);
            }

            if (paramService.getParameterValuesAsString(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TravelParameters.INTERNATIONAL_TRIP_TYPE_CODES).contains(tripType.getCode())) {
                if (StringUtils.isBlank(taDocument.getCellPhoneNumber())) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.CELL_PHONE_NUMBER, KFSKeyConstants.ERROR_REQUIRED, "Traveler's Cell or Other Contact Number During Trip");
                    GlobalVariables.getMessageMap().removeFromErrorPath(KRADPropertyConstants.DOCUMENT);
                }

                // make sure at least one mode of transportation is filled in
                if(taDocument.getTransportationModes() == null || taDocument.getTransportationModes().size() == 0) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.EM_CONTACT);
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.MODE_OF_TRANSPORT, TemKeyConstants.ERROR_TA_AUTH_MODE_OF_TRANSPORT_REQUIRED);
                    GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.EM_CONTACT);
                }

                // we have an international trip, make sure fields are filled in
                if (StringUtils.isBlank(taDocument.getRegionFamiliarity())) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.REGION_FAMILIARITY, KFSKeyConstants.ERROR_REQUIRED, "Region Familiarity");
                    GlobalVariables.getMessageMap().removeFromErrorPath(KRADPropertyConstants.DOCUMENT);
                }
            }
        }

        return rulePassed;
    }


    private boolean validEmergencyContact(TravelAuthorizationDocument taDocument){
        // check to see if there are emergency contacts and that at least one of them has real data
        boolean validEmergencyContact = false;
        for (TravelerDetailEmergencyContact emergencyContact : taDocument.getTraveler().getEmergencyContacts()) {
            if (emergencyContact != null && !StringUtils.isBlank(emergencyContact.getContactName())) {
                validEmergencyContact = true;
                return validEmergencyContact;
            }
        }
        if (!validEmergencyContact) {
            //remove the previous error because it could already be in the message map in the wrong order
            GlobalVariables.getMessageMap().removeAllErrorMessagesForProperty(TemPropertyConstants.EM_CONTACT+"."+TemPropertyConstants.TRVL_AUTH_EM_CONTACT_CONTACT_NAME);
            GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.EM_CONTACT);
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_EM_CONTACT_CONTACT_NAME, TemKeyConstants.ERROR_TA_AUTH_EMERGENCY_CONTACT_REQUIRED);
            GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.EM_CONTACT);
        }

        return validEmergencyContact;
    }

}
