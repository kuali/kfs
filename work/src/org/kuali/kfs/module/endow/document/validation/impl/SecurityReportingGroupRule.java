/*
 * Copyright 2009 The Kuali Foundation.
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
