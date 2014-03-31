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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.document.TravelDocument;
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
    protected PersonService personService;
    protected static volatile AccountsReceivableModuleService arModuleService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        GroupTraveler groupTraveler = ((AddGroupTravelLineEvent) event).getGroupTraveler();

        if (ObjectUtils.isNull(groupTraveler.getGroupTravelerTypeCode())) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.GROUP_TRVL_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED, "Traveler Type Code");
            valid = false;
        }
        else {
            if (StringUtils.isBlank(groupTraveler.getGroupTravelerEmpId()) && (groupTraveler.getGroupTravelerTypeCode().equals(TemConstants.GroupTravelerType.EMPLOYEE.getCode()) || groupTraveler.getGroupTravelerTypeCode().equals(TemConstants.GroupTravelerType.STUDENT.getCode()) || groupTraveler.getGroupTravelerTypeCode().equals(TemConstants.GroupTravelerType.CUSTOMER.getCode()))) {
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.GROUP_TRAVELER_EMP_ID, KFSKeyConstants.ERROR_REQUIRED, "Group Traveler Emp Id");
                valid = false;
            } else {
                if (groupTraveler.getGroupTravelerTypeCode().equals(TemConstants.GroupTravelerType.EMPLOYEE.getCode()) || groupTraveler.getGroupTravelerTypeCode().equals(TemConstants.GroupTravelerType.STUDENT.getCode())) {
                    Person person = getPersonService().getPerson(groupTraveler.getGroupTravelerEmpId());
                    if (person == null) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.GROUP_TRAVELER_EMP_ID, TemKeyConstants.ERROR_TRVL_GROUP_TRVL_EMP_NOT_FOUND, groupTraveler.getGroupTravelerEmpId());
                        valid = false;
                    }
                } else if (groupTraveler.getGroupTravelerTypeCode().equals(TemConstants.GroupTravelerType.CUSTOMER.getCode())) {
                    final AccountsReceivableCustomer customer = getAccountsReceivableModuleService().findCustomer(groupTraveler.getGroupTravelerEmpId());
                    if (customer == null) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.GROUP_TRAVELER_EMP_ID, TemKeyConstants.ERROR_TRVL_GROUP_TRVL_EMP_NOT_FOUND, groupTraveler.getGroupTravelerEmpId());
                        valid = false;
                    }
                }
                // we don't validate "Other"
            }
        }

        if (ObjectUtils.isNull(groupTraveler.getName())) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.GROUP_TRAVELER_NAME, KFSKeyConstants.ERROR_REQUIRED, "Name");
            valid = false;
        } else {
            TravelDocument document = (TravelDocument)event.getDocument();
            List<GroupTraveler> groupTravelers = document.getGroupTravelers();
            for (GroupTraveler gt : groupTravelers) {
                if (StringUtils.equalsIgnoreCase(gt.getName(), groupTraveler.getName()) || StringUtils.equalsIgnoreCase(gt.getGroupTravelerEmpId(), groupTraveler.getGroupTravelerEmpId())) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.GROUP_TRAVELER_NAME, TemKeyConstants.ERROR_TRVL_GROUP_TRVL_DUPLICATE, "Name");
                    valid = false;
                }
            }

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

    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (arModuleService == null) {
            arModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        return arModuleService;
    }
}
