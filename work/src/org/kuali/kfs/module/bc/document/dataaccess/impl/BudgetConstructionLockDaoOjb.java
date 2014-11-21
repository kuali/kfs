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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao
 */
public class BudgetConstructionLockDaoOjb extends PlatformAwareDaoBaseOjb implements BudgetConstructionLockDao {
    private BudgetConstructionDao budgetConstructionDao;

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao#getAllAccountLocks(java.lang.String)
     */
    public List<BudgetConstructionHeader> getAllAccountLocks(String lockUnivId) {
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(lockUnivId)) {
            criteria.addEqualTo(BCPropertyConstants.BUDGET_LOCK_USER_IDENTIFIER, lockUnivId);
        }
        else {
            criteria.addNotNull(BCPropertyConstants.BUDGET_LOCK_USER_IDENTIFIER);
        }
        
        ReportQueryByCriteria query = QueryFactory.newReportQuery(BudgetConstructionHeader.class, criteria);
        query.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        
        return (List<BudgetConstructionHeader>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao#getAllPositionFundingLocks(java.lang.String)
     */
    public List<PendingBudgetConstructionAppointmentFunding> getAllPositionFundingLocks(String lockUnivId) {
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(lockUnivId)) {
            criteria.addEqualTo(BCPropertyConstants.APPOINTMENT_FUNDING_LOCK_USER_ID, lockUnivId);
        }
        else {
            criteria.addNotNull(BCPropertyConstants.APPOINTMENT_FUNDING_LOCK_USER_ID);
        }

        ReportQueryByCriteria query = QueryFactory.newReportQuery(BudgetConstructionFundingLock.class, criteria);
        query.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        List<BudgetConstructionFundingLock> allFundingLocks = (List<BudgetConstructionFundingLock>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        List<PendingBudgetConstructionAppointmentFunding> positionFundingLocks = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        for (BudgetConstructionFundingLock fundingLock : allFundingLocks) {
            Criteria criteria2 = new Criteria();
            criteria2.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fundingLock.getUniversityFiscalYear());
            criteria2.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, fundingLock.getChartOfAccountsCode());
            criteria2.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, fundingLock.getAccountNumber());
            criteria2.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, fundingLock.getSubAccountNumber());
            criteria2.addEqualTo(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION + "." + BCPropertyConstants.POSITION_LOCK_USER_IDENTIFIER, fundingLock.getAppointmentFundingLockUserId());

            positionFundingLocks.addAll((List<PendingBudgetConstructionAppointmentFunding>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PendingBudgetConstructionAppointmentFunding.class, criteria2)));
        }

        return positionFundingLocks;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao#getOrphanedPositionLocks(java.lang.String)
     */
    public List<BudgetConstructionPosition> getOrphanedPositionLocks(String lockUnivId) {
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(lockUnivId)) {
            criteria.addEqualTo(BCPropertyConstants.POSITION_LOCK_USER_IDENTIFIER, lockUnivId);
        }
        else {
            criteria.addNotNull(BCPropertyConstants.POSITION_LOCK_USER_IDENTIFIER);
        }
        
        ReportQueryByCriteria query = QueryFactory.newReportQuery(BudgetConstructionPosition.class, criteria);
        query.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        query.addOrderByAscending(BCPropertyConstants.POSITION_NUMBER);
        
        List<BudgetConstructionPosition> allPositionLocks = (List<BudgetConstructionPosition>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        List<BudgetConstructionPosition> orphanedPositionLocks = new ArrayList<BudgetConstructionPosition>();
        for (BudgetConstructionPosition position : allPositionLocks) {
            Criteria criteria2 = new Criteria();
            criteria2.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, position.getUniversityFiscalYear());
            criteria2.addEqualTo(BCPropertyConstants.POSITION_NUMBER, position.getPositionNumber());
            
            Criteria subCrit = new Criteria();
            subCrit.addEqualToField(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, Criteria.PARENT_QUERY_PREFIX + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            subCrit.addEqualToField(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, Criteria.PARENT_QUERY_PREFIX + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            subCrit.addEqualToField(KFSPropertyConstants.ACCOUNT_NUMBER, Criteria.PARENT_QUERY_PREFIX + KFSPropertyConstants.ACCOUNT_NUMBER);
            subCrit.addEqualToField(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, Criteria.PARENT_QUERY_PREFIX + KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            subCrit.addEqualTo(BCPropertyConstants.APPOINTMENT_FUNDING_LOCK_USER_ID, position.getPositionLockUserIdentifier());
            
            ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(BudgetConstructionFundingLock.class, subCrit);   
            subQuery.setAttributes(new String[] { "1" });
            criteria2.addExists(subQuery);
            
            List<PendingBudgetConstructionAppointmentFunding> appointmentFundingLocks = (List<PendingBudgetConstructionAppointmentFunding>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PendingBudgetConstructionAppointmentFunding.class, criteria2));
            if (appointmentFundingLocks == null || appointmentFundingLocks.isEmpty()) {
                orphanedPositionLocks.add(position);
            }
        }

        return orphanedPositionLocks;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao#getAllTransactionLocks(java.lang.String)
     */
    public List<BudgetConstructionHeader> getAllTransactionLocks(String lockUnivId) {
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(lockUnivId)) {
            criteria.addEqualTo(BCPropertyConstants.BUDGET_TRANSACTION_LOCK_USER_IDENTIFIER, lockUnivId);
        }
        else {
            criteria.addNotNull(BCPropertyConstants.BUDGET_TRANSACTION_LOCK_USER_IDENTIFIER);
        }
        
        ReportQueryByCriteria query = QueryFactory.newReportQuery(BudgetConstructionHeader.class, criteria);
        query.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        return (List<BudgetConstructionHeader>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao#getOrphanedFundingLocks(java.lang.String)
     */
    public List<BudgetConstructionFundingLock> getOrphanedFundingLocks(String lockUnivId) {
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(lockUnivId)) {
            criteria.addEqualTo(BCPropertyConstants.APPOINTMENT_FUNDING_LOCK_USER_ID, lockUnivId);
        }
        else {
            criteria.addNotNull(BCPropertyConstants.APPOINTMENT_FUNDING_LOCK_USER_ID);
        }
        
        ReportQueryByCriteria query = QueryFactory.newReportQuery(BudgetConstructionFundingLock.class, criteria);
        query.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        List<BudgetConstructionFundingLock> allFundingLocks = (List<BudgetConstructionFundingLock>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        List<BudgetConstructionFundingLock> orphanedFundingLocks = new ArrayList<BudgetConstructionFundingLock>();
        for (BudgetConstructionFundingLock fundingLock : allFundingLocks) {
            String positionNumber = budgetConstructionDao.getPositionAssociatedWithFundingLock(fundingLock);
            if (BCConstants.POSITION_NUMBER_NOT_FOUND.equals(positionNumber)) {
                orphanedFundingLocks.add(fundingLock);
            }
        }

        return orphanedFundingLocks;
    }

    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao) {
        this.budgetConstructionDao = budgetConstructionDao;
    }

}
