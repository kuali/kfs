/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.validation.impl;

import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * Business rules for BudgetConstructionAppointmentFundingReasonCode maintenance document. 
 */
public class BudgetConstructionAppointmentFundingReasonCodeRule extends MaintenanceDocumentRuleBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAppointmentFundingReasonCodeRule.class);

    protected BudgetConstructionAppointmentFundingReasonCode oldBudgetConstructionAppointmentFundingReasonCode;
    protected BudgetConstructionAppointmentFundingReasonCode newBudgetConstructionAppointmentFundingReasonCode;

    protected SalarySettingService salarySettingService;

    public BudgetConstructionAppointmentFundingReasonCodeRule() {
        
        this.setSalarySettingService(SpringContext.getBean(SalarySettingService.class));
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");

        boolean success = true;
        
        // checks when editing existing code
        if (document.isEdit()) {
            success &= checkInactivateReason(document);
        }

        return success;
    }

    protected boolean checkInactivateReason(MaintenanceDocument document){
        LOG.info("checkInactivateReason called");

        boolean success = true;
        
        // just return when not being inactivated 
        if (!(oldBudgetConstructionAppointmentFundingReasonCode.isActive() && !newBudgetConstructionAppointmentFundingReasonCode.isActive())) {
            return true;
        }

        // disallow inactivation if appointment funding reasons exist using this editable code
        if (salarySettingService.hasExistingFundingReason(newBudgetConstructionAppointmentFundingReasonCode)){
            putGlobalError(BCKeyConstants.ERROR_BUDGET_REASONMAINT_INACTIVATE_REASONEXIST);
            success &= false;
        }

        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        
        oldBudgetConstructionAppointmentFundingReasonCode = (BudgetConstructionAppointmentFundingReasonCode) super.getOldBo();
        newBudgetConstructionAppointmentFundingReasonCode = (BudgetConstructionAppointmentFundingReasonCode) super.getNewBo();
    }

    /**
     * Sets the salarySettingService attribute value.
     * @param salarySettingService The salarySettingService to set.
     */
    public void setSalarySettingService(SalarySettingService salarySettingService) {
        this.salarySettingService = salarySettingService;
    } 
}
