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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class represents the business rules for the maintenance of {@link SecurityReportingGroup} business objects
 */
public class SecurityReportingGroupRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityReportingGroupRule.class);

    private SecurityReportingGroup newReportingGroup;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        newReportingGroup = (SecurityReportingGroup) super.getNewBo();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();

        if (isValid) {

            isValid = validateReportingGroupOrder(newReportingGroup);
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * Checks if there is another active security reporting group in the database with the same order number
     * 
     * @param reportingGroup the reporting group to validate
     * @return true if there is no othersecurity reporting group in the database with the same order number, false otherwise
     */
    public boolean validateReportingGroupOrder(SecurityReportingGroup reportingGroup) {
        boolean success = true;
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // get all the active security reporting groups in the database
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(EndowPropertyConstants.SECURITY_REPORTING_GROUP_ACTIVE_INFICATOR, Boolean.TRUE.toString());
        fieldValues.put(EndowPropertyConstants.SECURITY_REPORTING_GROUP_ORDER, String.valueOf(reportingGroup.getSecurityReportingGrpOrder()));

        List<SecurityReportingGroup> dataToValidateList = new ArrayList<SecurityReportingGroup>(businessObjectService.findMatching(SecurityReportingGroup.class, fieldValues));

        // iterate throught the retrieved list of reporting groups and check if there is another reporting group with the same order
        // number
        for (SecurityReportingGroup record : dataToValidateList) {
            if (reportingGroup.getCode() != null && !reportingGroup.getCode().equals(record.getCode())) {
                putFieldError(EndowPropertyConstants.SECURITY_REPORTING_GROUP_ORDER, EndowKeyConstants.SecurityReportingGroupConstants.ERROR_SECURITY_REPORTING_GROUP_ORDER_DUPLICATE_VALUE);
                success = false;
                break;
            }

        }
        return success;
    }

}
