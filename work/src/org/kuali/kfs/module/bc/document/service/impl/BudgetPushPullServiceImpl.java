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

import org.kuali.core.util.spring.Logged;
import org.kuali.module.budget.dao.BudgetOrganizationPushPullDao;
import org.kuali.module.budget.service.BudgetPushPullService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements BudgetPushPullService by populating temporary tables with the potential set of documents to push down or pull up.
 * The temporary tables are then used to drive the entire push down or pull up process. First, an attempt is made to place
 * budget locks on each document. Successfully locked documents are then pushed down or pulled up by setting the associated
 * BudgetConstructionHeader (ld_bcnstr_hdr_t) row with the appropriate level attribute values and releasing the locks.
 */
@Transactional
public class BudgetPushPullServiceImpl implements BudgetPushPullService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetPushPullServiceImpl.class);
    
    private BudgetOrganizationPushPullDao budgetOrganizationPushPullDao;

    /**
     * @see org.kuali.module.budget.service.BudgetPushPullService#pullupSelectedOrganizationDocuments(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Logged
    public void pullupSelectedOrganizationDocuments(String personUniversalIdentifier, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {

        budgetOrganizationPushPullDao.pullupSelectedOrganizationDocuments(personUniversalIdentifier, FiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);

    }

    /**
     * @see org.kuali.module.budget.service.BudgetPushPullService#pushdownSelectedOrganizationDocuments(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Logged
    public void pushdownSelectedOrganizationDocuments(String personUniversalIdentifier, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        // TODO Auto-generated method stub

    }

    /**
     * Gets the budgetOrganizationPushPullDao attribute. 
     * @return Returns the budgetOrganizationPushPullDao.
     */
    public BudgetOrganizationPushPullDao getBudgetOrganizationPushPullDao() {
        return budgetOrganizationPushPullDao;
    }

    /**
     * Sets the budgetOrganizationPushPullDao attribute value.
     * @param budgetOrganizationPushPullDao The budgetOrganizationPushPullDao to set.
     */
    public void setBudgetOrganizationPushPullDao(BudgetOrganizationPushPullDao budgetOrganizationPushPullDao) {
        this.budgetOrganizationPushPullDao = budgetOrganizationPushPullDao;
    }

}
