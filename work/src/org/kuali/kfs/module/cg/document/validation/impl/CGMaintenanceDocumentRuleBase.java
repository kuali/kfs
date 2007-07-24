/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.rules;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.cg.bo.CGProjectDirector;
import org.kuali.module.cg.bo.Primaryable;

/**
 * Rules for the Proposal/Award maintenance document.
 */
public class CGMaintenanceDocumentRuleBase extends MaintenanceDocumentRuleBase {

    private static final String PROJECT_DIRECTOR_DECEASED = "D";
    private static final String[] PROJECT_DIRECTOR_INVALID_STATUSES = {PROJECT_DIRECTOR_DECEASED};
    
    private static final String AGENCY_TYPE_CODE_FEDERAL = "F";
    
    /**
     * checks to see if the end date is after the begine date
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
            String elementLabel = SpringServiceLocator.getDataDictionaryService().getCollectionElementLabel(boClass.getName(), collectionName, elementClass);
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

    protected <T extends CGProjectDirector> boolean checkProjectDirectorsExist(List<T> projectDirectors, Class<T> elementClass, String collectionName) {
        boolean success = true;
        final String personUserPropertyName = KFSPropertyConstants.PROJECT_DIRECTOR + "." + KFSPropertyConstants.PERSON_USER_IDENTIFIER;
        String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(elementClass, personUserPropertyName);
        int i = 0;
        for (T pd : projectDirectors) {
            String propertyName = collectionName + "[" + (i++) + "]." + personUserPropertyName;
            String id = pd.getPersonUniversalIdentifier();
            if (StringUtils.isBlank(id) || !SpringServiceLocator.getProjectDirectorService().primaryIdExists(id)) {
                putFieldError(propertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
                success = false;
            }
        }
        return success;
    }
    
    /**
     * 
     * This method takes in a collection of Project Directors and reviews them to see if any have invalid states for being added to
     * a proposal.  An example would be a status code of "D" which means "Deceased".  Project Directors with a status of "D" cannot
     * be added to a proposal or award.
     * 
     * @param <T>
     * @param projectDirectors Collection of project directors to be reviewed.
     * @param elementClass Type of object that the collection belongs to.
     * @param propertyName Name of field that error will be attached to.
     * @return True if all the project directors have valid statuses, false otherwise.
     */
    protected <T extends CGProjectDirector> boolean checkProjectDirectorsStatuses(List<T> projectDirectors, Class<T> elementClass, String propertyName) {
        boolean success = true;
        final String personUserPropertyName = KFSPropertyConstants.PROJECT_DIRECTOR + "." + KFSPropertyConstants.PERSON_USER_IDENTIFIER;
        String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(elementClass, personUserPropertyName);
        for(T pd : projectDirectors) {
            String pdEmplStatusCode = pd.getProjectDirector().getUniversalUser().getEmployeeStatusCode();
            String pdEmplStatusName = pd.getProjectDirector().getUniversalUser().getEmployeeStatus().getName();
            if(StringUtils.isBlank(pdEmplStatusCode) || Arrays.asList(PROJECT_DIRECTOR_INVALID_STATUSES).contains(pdEmplStatusCode)) {
                String[] errors = {pd.getProjectDirector().getPersonName(), pdEmplStatusCode + " - " + pdEmplStatusName};
                putFieldError(propertyName, KFSKeyConstants.ERROR_INVALID_PROJECT_DIRECTOR_STATUS, errors);
                success = false;
            }
        }
        return success;
    }
    
    /**
     * 
     * This method checks to see if the two agency values passed in are the same agency.  The agency for a C&G document cannot
     * be the same as the Federal Pass Through Agency for that same document.
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
     * checks if the required federal pass through fields are filled in if the federal pass through indicator is yes
     *
     * @return True if all the necessary rules regarding the federal pass through agency input fields are met, false otherwise.
     */
    protected boolean checkFederalPassThrough(boolean federalPassThroughIndicator, Agency primaryAgency, String federalPassThroughAgencyNumber, Class propertyClass, String federalPassThroughIndicatorFieldName) {
        boolean success = true;

        // check if primary agency is federal
        boolean primaryAgencyIsFederal = AGENCY_TYPE_CODE_FEDERAL.equalsIgnoreCase(primaryAgency.getAgencyTypeCode());
        
        String indicatorLabel = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(propertyClass, federalPassThroughIndicatorFieldName);
        String agencyLabel = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(propertyClass, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);

        if(primaryAgencyIsFederal) {
            if(federalPassThroughIndicator) {
                // fpt indicator should not be checked if primary agency is federal
                putFieldError(federalPassThroughIndicatorFieldName, KFSKeyConstants.ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_INDICATOR_IS_CHECKED, new String[] { primaryAgency.getAgencyNumber(), AGENCY_TYPE_CODE_FEDERAL});
                success = false;
            }
            if(!StringUtils.isBlank(federalPassThroughAgencyNumber)) {
                // fpt agency number should be blank if primary agency is federal
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_AGENCY_IS_NOT_BLANK, new String[] { primaryAgency.getAgencyNumber(), AGENCY_TYPE_CODE_FEDERAL});
                success = false;
            }
        }
        else {
            if(federalPassThroughIndicator && StringUtils.isBlank(federalPassThroughAgencyNumber)) {
                // fpt agency number is required if fpt indicator is checked
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_FPT_AGENCY_NUMBER_REQUIRED);
                success = false;
            }
            else if(!federalPassThroughIndicator && !StringUtils.isBlank(federalPassThroughAgencyNumber)) {
                // fpt agency number should be blank if fpt indicator is not checked
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_FPT_AGENCY_NUMBER_NOT_BLANK);
                success = false;
            }
        }
        
        return success;
    }

}
