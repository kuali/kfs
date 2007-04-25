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
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
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
        if (ObjectUtils.isNotNull(begin) && ObjectUtils.isNotNull(begin) && !end.after(begin)) {
            putFieldError(propertyName, KeyConstants.ERROR_ENDING_DATE_NOT_AFTER_BEGIN);
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
                    putFieldError(collectionName, KeyConstants.ERROR_NO_PRIMARY, elementLabel);
                    break;
                default:
                    putFieldError(collectionName, KeyConstants.ERROR_MULTIPLE_PRIMARY, elementLabel);
            }

        }
        return success;
    }

    protected <T extends CGProjectDirector> boolean checkProjectDirectorsExist(List<T> projectDirectors, Class<T> elementClass, String collectionName) {
        boolean success = true;
        final String personUserPropertyName = PropertyConstants.PROJECT_DIRECTOR + "." + PropertyConstants.PERSON_USER_IDENTIFIER;
        String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(elementClass, personUserPropertyName);
        int i = 0;
        for (T pd : projectDirectors) {
            String propertyName = collectionName + "[" + (i++) + "]." + personUserPropertyName;
            String id = pd.getPersonUniversalIdentifier();
            if (StringUtils.isBlank(id) || !SpringServiceLocator.getProjectDirectorService().primaryIdExists(id)) {
                putFieldError(propertyName, KeyConstants.ERROR_EXISTENCE, label);
                success = false;
            }
        }
        return success;
    }

}
