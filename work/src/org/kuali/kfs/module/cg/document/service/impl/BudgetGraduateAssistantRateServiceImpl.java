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

package org.kuali.module.kra.budget.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetGraduateAssistantRate;
import org.kuali.module.kra.budget.bo.GraduateAssistantRate;
import org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService;
import org.springframework.transaction.annotation.Transactional;

public class BudgetGraduateAssistantRateServiceImpl implements BudgetGraduateAssistantRateService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService#getAllGraduateAssistantRates()
     */
    public List getAllGraduateAssistantRates() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        return new ArrayList(businessObjectService.findMatching(GraduateAssistantRate.class, fieldValues));
    }

    public boolean isValidGraduateAssistantRate(KualiDecimal fringeRate) {
        return fringeRate == null ? false : fringeRate.isLessEqual(KFSConstants.GRADUATE_ASSISTANT_RATE_MAX);
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

    public BudgetGraduateAssistantRate getBudgetGraduateAssistantRate(String documentNumber, String campusCode) {

        BudgetGraduateAssistantRate budgetGradAsstRate = (BudgetGraduateAssistantRate) businessObjectService.retrieve(new BudgetGraduateAssistantRate(documentNumber, campusCode));

        if (budgetGradAsstRate == null) {
            GraduateAssistantRate gradAsstRate = (GraduateAssistantRate) businessObjectService.retrieve(new GraduateAssistantRate(campusCode));
            budgetGradAsstRate = new BudgetGraduateAssistantRate(documentNumber, gradAsstRate);
        }

        return budgetGradAsstRate;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}