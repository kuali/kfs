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
package org.kuali.module.budget.service.impl;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.budget.dao.BudgetConstructionPositionFundingDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionPositionFundingDetailReportService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionPositionFundingDetailReportServiceImpl implements BudgetConstructionPositionFundingDetailReportService {

    BudgetConstructionPositionFundingDetailReportDao budgetConstructionPositionFundingDetailReportDao;
    
    public void updatePositionFundingDetailReport(String personUserIdentifier, boolean applyAThreshold, boolean selectOnlyGreaterThanOrEqualToThreshold, KualiDecimal thresholdPercent) {
        budgetConstructionPositionFundingDetailReportDao.updateReportsPositionFundingDetailTable(personUserIdentifier, applyAThreshold, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
    }
    
    
}
