/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.util.List;

import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;

public class SalarySettingRules implements SalarySettingRule {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRules.class);
    
    private AccountingLineRuleHelperService accountingLineRuleHelperService = SpringContext.getBean(AccountingLineRuleHelperService.class);
    private DataDictionary dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAddAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAddAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("processAddAppointmentFunding() start");
        boolean isValid = true;

        boolean hasValidFormat = SalarySettingRuleUtil.isFieldFormatValid(appointmentFunding);
        
        // if the formats of the fields are correct, check if there exist the references of a set of specified fields
        boolean hasValidReference = true;
        if (hasValidFormat) {
            hasValidReference &= accountingLineRuleHelperService.isValidAccount(appointmentFunding.getAccount(), dataDictionary, EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS);
            hasValidReference &= accountingLineRuleHelperService.isValidChart(appointmentFunding.getChartOfAccounts(), dataDictionary, EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS);
            hasValidReference &= accountingLineRuleHelperService.isValidChart(appointmentFunding.getChartOfAccounts(), dataDictionary, EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS);

            if (!KFSConstants.getDashSubAccountNumber().equals(appointmentFunding.getSubAccountNumber())) {
                hasValidReference &= accountingLineRuleHelperService.isValidSubAccount(appointmentFunding.getSubAccount(), dataDictionary, EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS);
            }
        }
        
        if (!SalarySettingRuleUtil.isValidRequestedAmount(appointmentFunding)) {
            GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_NONNEGATIVE_REQUIRED);
            isValid = false;
        }
        

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return this.processAddAppointmentFunding(appointmentFunding);
    }
}
