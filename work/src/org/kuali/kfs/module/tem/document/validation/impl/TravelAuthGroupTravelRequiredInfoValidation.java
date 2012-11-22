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

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.document.validation.event.AddGroupTravelLineEvent;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthGroupTravelRequiredInfoValidation extends GenericValidation {
    private PersonService personService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        GroupTraveler groupTraveler = ((AddGroupTravelLineEvent) event).getGroupTraveler();

        if (ObjectUtils.isNull(groupTraveler.getTravelerTypeCode())) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.GROUP_TRVL_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED, "Traveler Type Code");
            valid = false;
        }
        else {
            if (groupTraveler.getTravelerTypeCode().equals(TemConstants.EMP_TRAVELER_TYP_CD)) {
                if (groupTraveler.getGroupTravelerEmpId() == null) {
                    GlobalVariables.getMessageMap().putError("groupTravelerEmpId", KFSKeyConstants.ERROR_REQUIRED, "Group Traveler Emp Id");
                    valid = false;
                }
                else {
                    Person person = getPersonService().getPerson(groupTraveler.getGroupTravelerEmpId());
                    if (person == null) {
                        GlobalVariables.getMessageMap().putError("groupTravelerEmpId", TemKeyConstants.ERROR_TRVL_GROUP_TRVL_EMP_NOT_FOUND, groupTraveler.getGroupTravelerEmpId());
                        valid = false;
                    }
                }
            }
        }

        if (ObjectUtils.isNull(groupTraveler.getName())) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.GROUP_TRAVELER_NAME, KFSKeyConstants.ERROR_REQUIRED, "Name");
            valid = false;
        }

        return valid;
    }

    public PersonService getPersonService() {
        if (personService == null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

}
