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

import java.util.List;

import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.dao.BudgetReportsControlListDao;
import org.kuali.module.budget.service.BudgetReportsControlListService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetReportsControlListService.
 */
@Transactional
public class BudgetReportsControlListServiceImpl implements BudgetReportsControlListService {

    BudgetReportsControlListDao budgetReportsControlListDao;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReportsControlList(java.lang.String,
     *      java.lang.String, java.lang.Integer, java.util.List)
     */
    public void updateReportsControlList(String idForSession, String personUserIdentifier, Integer universityFiscalYear, List<BudgetConstructionPullup> budgetConstructionPullup) {
        budgetReportsControlListDao.cleanReportsControlList(personUserIdentifier);
        budgetReportsControlListDao.cleanReportsControlListPart1(idForSession);
        budgetReportsControlListDao.cleanReportsControlListPart2(idForSession);

        budgetReportsControlListDao.updateReportsControlListpart1(idForSession, personUserIdentifier, universityFiscalYear);
        for (BudgetConstructionPullup bcp : budgetConstructionPullup) {
            budgetReportsControlListDao.updateReportsControlListpart2(idForSession, personUserIdentifier, bcp.getChartOfAccountsCode(), bcp.getOrganizationCode());
        }

        budgetReportsControlListDao.updateReportsControlListDisp1(idForSession);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReportsSubFundGroupSelectList(java.lang.String)
     */
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier) {
        budgetReportsControlListDao.cleanReportsSubFundGroupSelectList(personUserIdentifier);
        budgetReportsControlListDao.updateReportsSubFundGroupSelectList(personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#changeFlagOrganizationAndChartOfAccountCodeSelection(java.lang.String,
     *      java.util.List)
     */
    public void changeFlagOrganizationAndChartOfAccountCodeSelection(String personUserIdentifier, List<BudgetConstructionPullup> budgetConstructionPullup) {
        for (BudgetConstructionPullup bcp : budgetConstructionPullup) {
            budgetReportsControlListDao.changeFlagOrganizationAndChartOfAccountCodeSelection(personUserIdentifier, bcp.getChartOfAccountsCode(), bcp.getOrganizationCode());
        }
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReportsSelectedSubFundGroupFlags(java.lang.String,
     *      java.util.List)
     */
    public void updateReportsSelectedSubFundGroupFlags(String personUserIdentifier, List<String> selectedSubfundGroupCodeList) {

        for (String subfundCode : selectedSubfundGroupCodeList) {
            budgetReportsControlListDao.updateReportsSelectedSubFundGroupFlags(personUserIdentifier, subfundCode);
        }

    }

    /**
     * Sets the budgetReportsControlListDao
     * 
     * @param budgetReportsControlListDao The budgetReportsControlListDao to set.
     */
    public void setBudgetReportsControlListDao(BudgetReportsControlListDao budgetReportsControlListDao) {
        this.budgetReportsControlListDao = budgetReportsControlListDao;
    }
}
