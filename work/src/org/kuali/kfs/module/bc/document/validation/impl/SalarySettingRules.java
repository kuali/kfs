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
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService;
import org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;

public class SalarySettingRules implements SalarySettingRule {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRules.class);

    private BudgetConstructionRuleHelperService budgetConstructionRuleHelperService = SpringContext.getBean(BudgetConstructionRuleHelperService.class);
    private SalarySettingRuleHelperService salarySettingRuleHelperService = SpringContext.getBean(SalarySettingRuleHelperService.class);
    private ErrorMap errorMap = GlobalVariables.getErrorMap();

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAddAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAddAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("processAddAppointmentFunding() start");

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        // if the formats of the fields are correct, check if there exist the references of a set of specified fields
        boolean hasValidReference = budgetConstructionRuleHelperService.hasValidChart(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasDetailPositionRequiredObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidPosition(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidIncumbent(appointmentFunding, errorMap);

        if (!hasValidReference) {
            return hasValidReference;
        }

        boolean hasValidAmount = salarySettingRuleHelperService.hasValidRequestedAmount(appointmentFunding, errorMap);
           
        return hasValidAmount;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return this.processAddAppointmentFunding(appointmentFunding);
    }
}
