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
