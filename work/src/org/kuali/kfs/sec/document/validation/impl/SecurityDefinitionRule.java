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
package org.kuali.kfs.sec.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;


/**
 * Implements business rules checks on the SecurityDefinition maintenance document
 */
public class SecurityDefinitionRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityDefinitionRule.class);

    protected SecurityDefinition oldSecurityDefinition;
    protected SecurityDefinition newSecurityDefinition;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(document);

        if (!isValid) {
            return isValid;
        }

        boolean isMaintenanceEdit = document.isEdit();

        isValid &= validateSecurityDefinition(isMaintenanceEdit);

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (!isValid) {
            return isValid;
        }

        boolean isMaintenanceEdit = document.isEdit();

        isValid &= validateSecurityDefinition(isMaintenanceEdit);

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        oldSecurityDefinition = (SecurityDefinition) super.getOldBo();
        newSecurityDefinition = (SecurityDefinition) super.getNewBo();
    }

    /**
     * Validates business rules against a security definition record
     *
     * @param isMaintenanceEdit boolean indicating whether the maintenance action is an edit (true), or a new/copy (false)
     * @return boolean true if all rules pass, false if at least one fails
     */
    protected boolean validateSecurityDefinition(boolean isMaintenanceEdit) {
        boolean isValid = true;

        if (!isMaintenanceEdit) {
            boolean validDefinitionName = verifyDefinitionNameIsUnique(newSecurityDefinition, KRADConstants.MAINTENANCE_NEW_MAINTAINABLE);
            if (!validDefinitionName) {
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * For new or copy action verifies the name given for the definition is not being used by another definition
     *
     * @param securityDefinition SecurityDefinition with name to check
     * @param errorKeyPrefix String errorPrefix to use if any errors are found
     * @return boolean true if name exists, false if not
     */
    protected boolean verifyDefinitionNameIsUnique(SecurityDefinition securityDefinition, String errorKeyPrefix) {
        boolean isValid = true;

        Map<String, String> searchValues = new HashMap<String, String>();
        searchValues.put(KFSPropertyConstants.NAME, securityDefinition.getName());

        int matchCount = getBoService().countMatching(SecurityDefinition.class, searchValues);
        if (matchCount > 0) {
            GlobalVariables.getMessageMap().putError(errorKeyPrefix + KFSPropertyConstants.NAME, SecKeyConstants.ERROR_DEFINITION_NAME_NON_UNIQUE, securityDefinition.getName());
            isValid = false;
        }
        // TODO: check if KIM role exists - fail if present

        return isValid;
    }


}
