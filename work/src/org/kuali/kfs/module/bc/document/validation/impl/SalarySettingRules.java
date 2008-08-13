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

/**
 * the rule implementation for the actions of salary setting component
 */
public class SalarySettingRules implements SalarySettingRule {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRules.class);

    private BudgetConstructionRuleHelperService budgetConstructionRuleHelperService = SpringContext.getBean(BudgetConstructionRuleHelperService.class);
    private SalarySettingRuleHelperService salarySettingRuleHelperService = SpringContext.getBean(SalarySettingRuleHelperService.class);
    private ErrorMap errorMap = GlobalVariables.getErrorMap();

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("processSaveAppointmentFunding() start");
        
        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        boolean hasValidReferences = this.hasValidRefences(appointmentFunding, errorMap);
        if (!hasValidReferences) {
            return hasValidReferences;
        }
        
        boolean isObjectCodeMatching = salarySettingRuleHelperService.hasObjectCodeMatchingDefaultOfPosition(appointmentFunding, errorMap);
        if(!isObjectCodeMatching) {
            return isObjectCodeMatching;
        }
        
        boolean isAssociatedWithBudgetableDocument = budgetConstructionRuleHelperService.isAssociatedWithValidDocument(appointmentFunding, errorMap, KFSConstants.EMPTY_STRING);
        if(!isAssociatedWithBudgetableDocument) {
            return isAssociatedWithBudgetableDocument;
        }

        boolean hasValidAmounts = this.hasValidAmounts(appointmentFunding, errorMap);
        if(!hasValidAmounts) {
            return hasValidAmounts;
        }
        
        return true;
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAddAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAddAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("processAddAppointmentFunding() start");

        boolean hasExistingFunding = salarySettingRuleHelperService.hasSameExistingLine(existingAppointmentFundings, appointmentFunding, errorMap);
        if (hasExistingFunding) {
            return false;
        }

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        boolean hasValidReferences = this.hasValidRefences(appointmentFunding, errorMap);
        if (!hasValidReferences) {
            return hasValidReferences;
        }
        
        boolean isObjectCodeMatching = salarySettingRuleHelperService.hasObjectCodeMatchingDefaultOfPosition(appointmentFunding, errorMap);
        if(!isObjectCodeMatching) {
            return isObjectCodeMatching;
        }
        
        boolean isAssociatedWithBudgetableDocument = budgetConstructionRuleHelperService.isAssociatedWithValidDocument(appointmentFunding, errorMap, KFSConstants.EMPTY_STRING);
        if(!isAssociatedWithBudgetableDocument) {
            return isAssociatedWithBudgetableDocument;
        }
        
        boolean hasValidAmounts = this.hasValidAmounts(appointmentFunding, errorMap);
        if(!hasValidAmounts) {
            return hasValidAmounts;
        }
        
        return true;
    }

    // test if all references of the given appointment funding are valid
    private boolean hasValidRefences(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        boolean hasValidReference = budgetConstructionRuleHelperService.hasValidChart(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasDetailPositionRequiredObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidPosition(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidIncumbent(appointmentFunding, errorMap);

        return hasValidReference;
    }

    // test if all amounts are legal
    private boolean hasValidAmounts(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        boolean hasValidAmounts = salarySettingRuleHelperService.hasValidRequestedAmount(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedFteQuantity(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedFundingMonth(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedTimePercent(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasRequestedAmountZeroWhenFullYearLeave(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasRequestedFteQuantityZeroWhenFullYearLeave(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedCsfAmount(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedCsfTimePercent(appointmentFunding, errorMap);
        
        return hasValidAmounts;
    }
}
