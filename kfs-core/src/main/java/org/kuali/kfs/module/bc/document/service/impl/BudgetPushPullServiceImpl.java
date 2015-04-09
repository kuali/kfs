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

