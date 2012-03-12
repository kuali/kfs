/*
 * Copyright 2006 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyType;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Rules for processing Agency instances.
 */
public class AgencyRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(AgencyRule.class);

    protected Agency newAgency;
    protected Agency oldAgency;

    BusinessObjectService businessObjectService;

    /**
     * Default constructor.
     */
    public AgencyRule() {
        super();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering AgencyRule.processCustomApproveDocumentBusinessRules");
        boolean success = super.processCustomApproveDocumentBusinessRules(document);

        success &= checkAgencyReportsTo(document);

        LOG.info("Leaving AgencyRule.processCustomApproveDocumentBusinessRules");
        return success;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering AgencyRule.processCustomRouteDocumentBusinessRules");
        boolean success = super.processCustomRouteDocumentBusinessRules(document);

        success &= checkAgencyReportsTo(document);

        LOG.info("Leaving AgencyRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering AgencyRule.processCustomSaveDocumentBusinessRules");
        boolean success = super.processCustomSaveDocumentBusinessRules(document);

        success &= checkAgencyReportsTo(document);
        success &= validateAgencyType(document);

        LOG.info("Leaving AgencyRule.processCustomSaveDocumentBusinessRules");
        return success;
    }

    protected boolean validateAgencyType(MaintenanceDocument document) {
        String agencyType = newAgency.getAgencyTypeCode();
        Map params = new HashMap();
        params.put("code", agencyType);
        Object o = businessObjectService.findByPrimaryKey(AgencyType.class, params);
        if (null == o) {
            putFieldError("agencyTypeCode", KFSKeyConstants.ERROR_AGENCY_TYPE_NOT_FOUND, agencyType);
            return false;
        }
        return false;
    }

    protected boolean checkAgencyReportsTo(MaintenanceDocument document) {
        boolean success = true;

        if (newAgency.getReportsToAgencyNumber() != null) {
            if (newAgency.getReportsToAgency() == null) { // Agency must exist

                putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_NOT_FOUND, newAgency.getReportsToAgencyNumber());
                success = false;

            }
            else if (!newAgency.getReportsToAgency().isActive()) { // Agency must be active. See KULCG-263

                putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_INACTIVE, newAgency.getReportsToAgencyNumber());
                success = false;

            }
            else if (newAgency.getAgencyNumber().equals(newAgency.getReportsToAgencyNumber())) {

                putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_REPORTS_TO_SELF, newAgency.getAgencyNumber());
                success = false;

            }
            else { // No circular references

                List agencies = new ArrayList();

                Agency agency = newAgency;

                while (agency.getReportsToAgency() != null && success) {
                    if (!agencies.contains(agency.getAgencyNumber())) {
                        agencies.add(agency.getAgencyNumber());
                    }
                    else {

                        putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_CIRCULAR_REPORTING, agency.getAgencyNumber());
                        success = false;
                    }

                    agency = agency.getReportsToAgency();
                }
            }
        }
        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        newAgency = (Agency) super.getNewBo();
        oldAgency = (Agency) super.getOldBo();
    }

}
