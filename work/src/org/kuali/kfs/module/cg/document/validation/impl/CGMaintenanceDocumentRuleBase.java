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
     * This method checks to see if the two agency values passed in are the same agency.  The agency for a C&G document cannot
     * be the same as the Federal Pass Through Agency for that same document.
     * @param agency
     * @param federalPassThroughAgency
     * @param propertyName
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

}
