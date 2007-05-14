/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.util.List;

import org.kuali.KeyConstants;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionDetail;

/**
 * This class implements the business rules specific to the Organization Reversion Maintenance Document.
 */
public class OrganizationReversionRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionRule.class);

    OrganizationReversion oldOrgReversion;
    OrganizationReversion newOrgReversion;

    /**
     * 
     * No-Args Constructor for an OrganizationReversionRule.
     * 
     */
    public OrganizationReversionRule() {

    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        // make sure its a valid organization reversion MaintenanceDocument
        if (!isCorrectMaintenanceClass(document, OrganizationReversion.class)) {
            throw new IllegalArgumentException("Maintenance Document passed in was of the incorrect type.  Expected " + "'" + OrganizationReversion.class.toString() + "', received " + "'" + document.getOldMaintainableObject().getBoClass().toString() + "'.");
        }

        // get the real business object
        newOrgReversion = (OrganizationReversion) document.getNewMaintainableObject().getBusinessObject();

        // add check to validate document recursively to get to the collection attributes
        success &= validateDetailBusinessObjects(newOrgReversion);

        return success;
    }

    /**
     * 
     * Tests each option attached to the main business object and validates its properties.
     * 
     * @param orgReversion
     * @return
     */
    private boolean validateDetailBusinessObjects(OrganizationReversion orgReversion) {
        GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");
        List<OrganizationReversionDetail> details = orgReversion.getOrganizationReversionDetail();
        int index = 0;
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        for (OrganizationReversionDetail dtl : details) {
            String errorPath = "organizationReversionDetail[" + index + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
            validateOrganizationReversionDetail(dtl);
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            index++;
        }
        GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");
        return GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;
    }

    protected boolean validateOrganizationReversionDetail(OrganizationReversionDetail detail) {
        boolean result = true; // let's assume this detail will pass the rule
        // 1. makes sure the financial object code exists
        detail.refreshReferenceObject("organizationReversionObject");
        LOG.debug("organization reversion finanical object = "+detail.getOrganizationReversionObject());
        if (ObjectUtils.isNull(detail.getOrganizationReversionObject())) {
            result = false;
            GlobalVariables.getErrorMap().putError("organizationReversionObjectCode", KeyConstants.ERROR_EXISTENCE, new String[] { "Financial Object Code: "+detail.getOrganizationReversionObjectCode() });
        }
        return result;
    }

}
