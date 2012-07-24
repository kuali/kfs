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
package org.kuali.kfs.module.bc.document.service.impl;

import org.kuali.kfs.module.bc.document.dataaccess.BudgetOrganizationPushPullDao;
import org.kuali.kfs.module.bc.document.service.BudgetPushPullService;
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
    
    protected BudgetOrganizationPushPullDao budgetOrganizationPushPullDao;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetPushPullService#pullupSelectedOrganizationDocuments(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    public void pullupSelectedOrganizationDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        budgetOrganizationPushPullDao.pullupSelectedOrganizationDocuments(principalId, FiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetPushPullService#pushdownSelectedOrganizationDocuments(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    public void pushdownSelectedOrganizationDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        budgetOrganizationPushPullDao.pushdownSelectedOrganizationDocuments(principalId, FiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetPushPullService#buildPullUpBudgetedDocuments(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    public int buildPullUpBudgetedDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        return budgetOrganizationPushPullDao.buildPullUpBudgetedDocuments(principalId, FiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetPushPullService#buildPushDownBudgetedDocuments(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
     */
    public int buildPushDownBudgetedDocuments(String principalId, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        return budgetOrganizationPushPullDao.buildPushDownBudgetedDocuments(principalId, FiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
    }

    /**
     * Sets the budgetOrganizationPushPullDao attribute value.
     * @param budgetOrganizationPushPullDao The budgetOrganizationPushPullDao to set.
     */
    public void setBudgetOrganizationPushPullDao(BudgetOrganizationPushPullDao budgetOrganizationPushPullDao) {
        this.budgetOrganizationPushPullDao = budgetOrganizationPushPullDao;
    }
    
    

}

