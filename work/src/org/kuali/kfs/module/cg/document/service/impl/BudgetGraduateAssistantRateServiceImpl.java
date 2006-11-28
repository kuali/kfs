/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

package org.kuali.module.kra.budget.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetGraduateAssistantRate;
import org.kuali.module.kra.budget.bo.GraduateAssistantRate;
import org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService;

/**
 * 
 * 
 */
public class BudgetGraduateAssistantRateServiceImpl implements BudgetGraduateAssistantRateService {

    private BusinessObjectService businessObjectService;

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService#getAllGraduateAssistantRates()
     */
    public List getAllGraduateAssistantRates() {
        return new ArrayList(businessObjectService.findAll(GraduateAssistantRate.class));
    }

    public boolean isValidGraduateAssistantRate(KualiDecimal fringeRate) {
        return fringeRate == null ? false : fringeRate.isLessEqual(Constants.GRADUATE_ASSISTANT_RATE_MAX);
    }

    /**
     * This method...
     * 
     * @param budgetForm
     */
    public void setupDefaultGradAssistantRates(Budget budget) {
        for (Iterator iter = getAllGraduateAssistantRates().iterator(); iter.hasNext();) {
            GraduateAssistantRate graduateAssistantRate = (GraduateAssistantRate) iter.next();
            BudgetGraduateAssistantRate budgetGraduateAssistantRate = new BudgetGraduateAssistantRate(budget.getDocumentNumber(), graduateAssistantRate.getCampusCode(), graduateAssistantRate.getCampusMaximumPeriod1Rate(), graduateAssistantRate.getCampusMaximumPeriod2Rate(), graduateAssistantRate.getCampusMaximumPeriod3Rate(), graduateAssistantRate.getCampusMaximumPeriod4Rate(), graduateAssistantRate.getCampusMaximumPeriod5Rate(), graduateAssistantRate.getCampusMaximumPeriod6Rate(), graduateAssistantRate);
            budget.getGraduateAssistantRates().add(budgetGraduateAssistantRate);
        }
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}