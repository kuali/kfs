/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.ojb;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.ExpenditureTransaction;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ExpenditureTransactionDao;

/**
 * The OJB implmentation of ExpenditureTransactionDao
 */
public class ExpenditureTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements ExpenditureTransactionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenditureTransactionDaoOjb.class);

    private final static String UNIVERISITY_FISCAL_YEAR = "universityFiscalYear";
    private final static String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private final static String ACCOUNT_NUMBER = "accountNumber";
    private final static String SUB_ACCOUNT_NUMBER = "subAccountNumber";
    private final static String OBJECT_CODE = "objectCode";
    private final static String BALANCE_TYPE_CODE = "balanceTypeCode";
    private final static String OBJECT_TYPE_CODE = "objectTypeCode";
    private final static String UNIVERSITY_FISCAL_ACCOUNTING_PERIOD = "universityFiscalAccountingPeriod";
    private final static String SUB_OBJECT_CODE = "subObjectCode";
    private final static String PROJECT_CODE = "projectCode";
    private final static String ORGANIZATION_REFERENCE_ID = "organizationReferenceId";

    /**
     * Constructs a ExpenditureTransactionDaoOjb instance
     */
    public ExpenditureTransactionDaoOjb() {
        super();
    }

    /**
     * Queries the database to find the expenditure transaction in the database that would be affected if the given transaction is posted
     * 
     * @param t a transaction to find a related expenditure transaction for
     * @return the expenditure transaction if found, null otherwise
     * @see org.kuali.module.gl.dao.ExpenditureTransactionDao#getByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public ExpenditureTransaction getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(UNIVERISITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
        crit.addEqualTo(UNIVERSITY_FISCAL_ACCOUNTING_PERIOD, t.getUniversityFiscalPeriodCode());
        crit.addEqualTo(PROJECT_CODE, t.getProjectCode());

        if (t.getOrganizationReferenceId() == null) {
            crit.addEqualTo(ORGANIZATION_REFERENCE_ID, GLConstants.getDashOrganizationReferenceId());
        }
        else {
            crit.addEqualTo("organizationReferenceId", t.getOrganizationReferenceId());
        }

        QueryByCriteria qbc = QueryFactory.newQuery(ExpenditureTransaction.class, crit);
        return (ExpenditureTransaction) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Fetches all expenditure transactions currently in the database
     * 
     * @return an Iterator with all expenditure transactions from the database
     * @see org.kuali.module.gl.dao.ExpenditureTransactionDao#getAllExpenditureTransactions()
     */
    public Iterator getAllExpenditureTransactions() {
        LOG.debug("getAllExpenditureTransactions() started");

        Criteria crit = new Criteria();
        // We want them all so no criteria is added

        QueryByCriteria qbc = QueryFactory.newQuery(ExpenditureTransaction.class, crit);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * Deletes the given expenditure transaction
     * 
     * @param et the expenditure transaction that will be removed, as such, from the database
     * @see org.kuali.module.gl.dao.ExpenditureTransactionDao#delete(org.kuali.module.gl.bo.ExpenditureTransaction)
     */
    public void delete(ExpenditureTransaction et) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(et);
    }

    /**
     * Saves an expenditure transaction
     * @param et the expenditure transaction to save
     * @see org.kuali.module.gl.dao.ExpenditureTransactionDao#save(org.kuali.module.gl.bo.ExpenditureTransaction)
     */
    public void save(ExpenditureTransaction et) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(et);
    }

    /**
     * Since expenditure transactions are temporary, just like flies that live for a mere day, this method removes all of the currently existing
     * expenditure transactions from the database, all expenditure transactions having run through the poster and fulfilled their lifecycle
     * @see org.kuali.module.gl.dao.ExpenditureTransactionDao#deleteAllExpenditureTransactions()
     */
    public void deleteAllExpenditureTransactions() {
        LOG.debug("deleteAllExpenditureTransactions() started");
        Iterator<ExpenditureTransaction> i = getAllExpenditureTransactions();
        while (i.hasNext()) {
            ExpenditureTransaction et = i.next();
            if (LOG.isInfoEnabled()) {
                LOG.info("The following ExpenditureTransaction was deleted: " + et.toString());
            }
            delete(et);
        }
    }
}
