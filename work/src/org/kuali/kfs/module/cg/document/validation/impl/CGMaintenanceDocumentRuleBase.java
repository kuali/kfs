/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cg.document.validation.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.CGProjectDirector;
import org.kuali.kfs.module.cg.businessobject.Primaryable;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the Proposal/Award maintenance document.
 */
public class CGMaintenanceDocumentRuleBase extends MaintenanceDocumentRuleBase {

    protected static final String PROJECT_DIRECTOR_DECEASED = "D";
    protected static final String[] PROJECT_DIRECTOR_INVALID_STATUSES = { PROJECT_DIRECTOR_DECEASED };

    protected static final String AGENCY_TYPE_CODE_FEDERAL = "F";

    /**
     * Checks to see if the end date is after the begin date
     * 
     * @param begin
     * @param end
     * @param propertyName
     * @return true if end is after begin, false otherwise
     */
    protected boolean checkEndAfterBegin(Date begin, Date end, String propertyName) {
        boolean success = true;
        if (ObjectUtils.isNotNull(begin) && ObjectUtils.isNotNull(end) && !end.after(begin)) {
            putFieldError(propertyName, KFSKeyConstants.ERROR_ENDING_DATE_NOT_AFTER_BEGIN);
            success = false;
        }
        return success;
    }

    /**
     * @param <E>
     * @param primaryables
     * @param elementClass
     * @param collectionName
     * @param boClass
     * @return
     */
    protected <E extends Primaryable> boolean checkPrimary(Collection<E> primaryables, Class<E> elementClass, String collectionName, Class<? extends BusinessObject> boClass) {
        boolean success = true;
        int count = 0;
        for (Primaryable p : primaryables) {
            if (p.isPrimary()) {
                count++;
            }
        }
        if (count != 1) {
            success = false;
            String elementLabel = SpringContext.getBean(DataDictionaryService.class).getCollectionElementLabel(boClass.getName(), collectionName, elementClass);
            switch (count) {
                case 0:
                    putFieldError(collectionName, KFSKeyConstants.ERROR_NO_PRIMARY, elementLabel);
                    break;
                default:
                    putFieldError(collectionName, KFSKeyConstants.ERROR_MULTIPLE_PRIMARY, elementLabel);
            }

        }
        return success;
    }

    /**
     * @param <T>
     * @param projectDirectors
     * @param elementClass
     * @param collectionName
     * @return
     */
    protected <T extends CGProjectDirector> boolean checkProjectDirectorsExist(List<T> projectDirectors, Class<T> elementClass, String collectionName) {
        boolean success = true;
        final String personUserPropertyName = KFSPropertyConstants.PROJECT_DIRECTOR + "." + KFSPropertyConstants.PERSON_USER_IDENTIFIER;
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(elementClass, personUserPropertyName);
        int i = 0;
        for (T pd : projectDirectors) {
            String propertyName = collectionName + "[" + (i++) + "]." + personUserPropertyName;
            String id = pd.getPrincipalId();
            if (StringUtils.isBlank(id) || (SpringContext.getBean(PersonService.class).getPerson(id) == null)) {
                putFieldError(propertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
                success = false;
            }
        }
        return success;
    }
    
    /**
     * @param <T>
     * @param projectDirectors
     * @param elementClass
     * @param collectionName
     * @return
     */
    protected <T extends CGProjectDirector> boolean checkProjectDirectorsAreDirectors(List<T> projectDirectors, Class<T> elementClass, String collectionName) {
        boolean success = true;
        final String personUserPropertyName = KFSPropertyConstants.PROJECT_DIRECTOR + "." + KFSPropertyConstants.PERSON_USER_IDENTIFIER;
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(elementClass, personUserPropertyName);
        RoleService roleService = SpringContext.getBean(RoleService.class);
        
        List<String> roleId = new ArrayList<String>();
        roleId.add(roleService.getRoleIdByNamespaceCodeAndName(KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimApiConstants.CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR));
        
        int i = 0;
        for (T pd : projectDirectors) {
            String propertyName = collectionName + "[" + (i++) + "]." + personUserPropertyName;
            String id = pd.getProjectDirector().getPrincipalId();
            if (!roleService.principalHasRole(id, roleId, null)) {
                putFieldError(propertyName, KFSKeyConstants.ERROR_NOT_A_PROJECT_DIRECTOR, id);
                success = false;
            }
        }
        return success;
    }
    

    /**
     * This method takes in a collection of {@link ProjectDirector}s and reviews them to see if any have invalid states for being
     * added to a {@link Proposal}. An example would be a status code of "D" which means "Deceased". Project Directors with a
     * status of "D" cannot be added to a {@link Proposal} or {@link Award}.
     * 
     * @param projectDirectors Collection of project directors to be reviewed.
     * @param elementClass Type of object that the collection belongs to.
     * @param propertyName Name of field that error will be attached to.
     * @return True if all the project directors have valid statuses, false otherwise.
     */
    protected <T extends CGProjectDirector> boolean checkProjectDirectorsStatuses(List<T> projectDirectors, Class<T> elementClass, String propertyName) {
        boolean success = true;
        for (T pd : projectDirectors) {
            String pdEmplStatusCode = pd.getProjectDirector().getEmployeeStatusCode();
            if (StringUtils.isBlank(pdEmplStatusCode) || Arrays.asList(PROJECT_DIRECTOR_INVALID_STATUSES).contains(pdEmplStatusCode)) {
                String pdEmplStatusName = "INVALID STATUS CODE " + pdEmplStatusCode;
                if ( StringUtils.isNotBlank(pdEmplStatusCode) ) {
                    CodedAttribute empStatus = KimApiServiceLocator.getIdentityService().getEmploymentStatus(pdEmplStatusCode);
                    if ( empStatus != null ) {
                        pdEmplStatusName = empStatus.getName();
                    }
                }
                String[] errors = { pd.getProjectDirector().getName(), pdEmplStatusCode + " - " + pdEmplStatusName };
                putFieldError(propertyName, KFSKeyConstants.ERROR_INVALID_PROJECT_DIRECTOR_STATUS, errors);
                success = false;
            }
        }
        return success;
    }

    /**
     * This method checks to see if the two agency values passed in are the same {@link Agency}. The agency for a C&G document
     * cannot be the same as the Federal Pass Through Agency for that same document.
     * 
     * @param agency
     * @param federalPassThroughAgency
     * @param agencyPropertyName
     * @return True if the agencies are not the same, false otherwise.
     */
    protected boolean checkAgencyNotEqualToFederalPassThroughAgency(Agency agency, Agency federalPassThroughAgency, String agencyPropertyName, String fedPassThroughAgencyPropertyName) {
        boolean success = true;
        if (ObjectUtils.isNotNull(agency) && ObjectUtils.isNotNull(federalPassThroughAgency) && agency.equals(federalPassThroughAgency)) {
            putFieldError(agencyPropertyName, KFSKeyConstants.ERROR_AGENCY_EQUALS_FEDERAL_PASS_THROUGH_AGENCY);
            putFieldError(fedPassThroughAgencyPropertyName, KFSKeyConstants.ERROR_FEDERAL_PASS_THROUGH_AGENCY_EQUALS_AGENCY);
            success = false;
        }
        return success;
    }

    /**
     * Checks if the required federal pass through fields are filled in if the federal pass through indicator is yes.
     * 
     * @return True if all the necessary rules regarding the federal pass through agency input fields are met, false otherwise.
     */
    protected boolean checkFederalPassThrough(boolean federalPassThroughIndicator, Agency primaryAgency, String federalPassThroughAgencyNumber, Class propertyClass, String federalPassThroughIndicatorFieldName) {
        boolean success = true;

        // check if primary agency is federal
        boolean primaryAgencyIsFederal = false;

        if (ObjectUtils.isNotNull(primaryAgency)) {
            primaryAgencyIsFederal = AGENCY_TYPE_CODE_FEDERAL.equalsIgnoreCase(primaryAgency.getAgencyTypeCode());
        }

        String indicatorLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(propertyClass, federalPassThroughIndicatorFieldName);
        String agencyLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(propertyClass, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);

        if (primaryAgencyIsFederal) {
            if (federalPassThroughIndicator) {
                // fpt indicator should not be checked if primary agency is federal
                putFieldError(federalPassThroughIndicatorFieldName, KFSKeyConstants.ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_INDICATOR_IS_CHECKED, new String[] { primaryAgency.getAgencyNumber(), AGENCY_TYPE_CODE_FEDERAL });
                success = false;
            }
            if (!StringUtils.isBlank(federalPassThroughAgencyNumber)) {
                // fpt agency number should be blank if primary agency is federal
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_AGENCY_IS_NOT_BLANK, new String[] { primaryAgency.getAgencyNumber(), AGENCY_TYPE_CODE_FEDERAL });
                success = false;
            }
        }
        else {
            if (federalPassThroughIndicator && StringUtils.isBlank(federalPassThroughAgencyNumber)) {
                // fpt agency number is required if fpt indicator is checked
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_FPT_AGENCY_NUMBER_REQUIRED);
                success = false;
            }
            else if (!federalPassThroughIndicator && !StringUtils.isBlank(federalPassThroughAgencyNumber)) {
                // fpt agency number should be blank if fpt indicator is not checked
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_FPT_AGENCY_NUMBER_NOT_BLANK);
                success = false;
            }
        }

        return success;
    }

}

