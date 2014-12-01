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
package org.kuali.kfs.gl.dataaccess.impl;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * The OJB implmentation of ExpenditureTransactionDao
 */
public class ExpenditureTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements ExpenditureTransactionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenditureTransactionDaoOjb.class);

    /**
     * Constructs a ExpenditureTransactionDaoOjb instance
     */
    public ExpenditureTransactionDaoOjb() {
        super();
    }

    /**
     * Queries the database to find the expenditure transaction in the database that would be affected if the given transaction is
     * posted
     * 
     * @param t a transaction to find a related expenditure transaction for
     * @return the expenditure transaction if found, null otherwise
     * @see org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao#getByTransaction(org.kuali.kfs.gl.businessobject.Transaction)
     */
    public ExpenditureTransaction getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(KFSPropertyConstants.SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(KFSPropertyConstants.OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD, t.getUniversityFiscalPeriodCode());
        crit.addEqualTo(KFSPropertyConstants.PROJECT_CODE, t.getProjectCode());

        if (StringUtils.isBlank(t.getOrganizationReferenceId())) {
            crit.addEqualTo(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, GeneralLedgerConstants.getDashOrganizationReferenceId());
        }
        else {
            crit.addEqualTo(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, t.getOrganizationReferenceId());
        }

        QueryByCriteria qbc = QueryFactory.newQuery(ExpenditureTransaction.class, crit);
        return (ExpenditureTransaction) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Fetches all expenditure transactions currently in the database
     * 
     * @return an Iterator with all expenditure transactions from the database
     * @see org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao#getAllExpenditureTransactions()
     */
    public Iterator getAllExpenditureTransactions() {
        LOG.debug("getAllExpenditureTransactions() started");
        try {
            Criteria crit = new Criteria();
            // We want them all so no criteria is added

            QueryByCriteria qbc = QueryFactory.newQuery(ExpenditureTransaction.class, crit);
            return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes the given expenditure transaction
     * 
     * @param et the expenditure transaction that will be removed, as such, from the database
     * @see org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao#delete(org.kuali.kfs.gl.businessobject.ExpenditureTransaction)
     */
    public void delete(ExpenditureTransaction et) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(et);
    }

    /**
     * Since expenditure transactions are temporary, just like flies that live for a mere day, this method removes all of the
     * currently existing expenditure transactions from the database, all expenditure transactions having run through the poster and
     * fulfilled their lifecycle
     * 
     * @see org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao#deleteAllExpenditureTransactions()
     */
    public void deleteAllExpenditureTransactions() {
        LOG.debug("deleteAllExpenditureTransactions() started");
        try{
            Iterator<ExpenditureTransaction> i = getAllExpenditureTransactions();
            while (i.hasNext()) {
                ExpenditureTransaction et = i.next();
                if (LOG.isInfoEnabled()) {
                    LOG.info("The following ExpenditureTransaction was deleted: " + et.toString());
                }
                delete(et);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }   
    }
}
