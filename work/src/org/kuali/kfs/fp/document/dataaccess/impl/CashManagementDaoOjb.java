/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.fp.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.fp.businessobject.CashieringItemInProcess;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.document.dataaccess.CashManagementDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;

public class CashManagementDaoOjb extends PlatformAwareDaoBaseOjb implements CashManagementDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashManagementDaoOjb.class);

    public CashManagementDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#findOpenItemsInProcessByCampusCode(java.lang.String)
     */
    public List<CashieringItemInProcess> findOpenItemsInProcessByCampusCode(String campusCode) throws DataAccessException {
        List<CashieringItemInProcess> openItems = new ArrayList<CashieringItemInProcess>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("campusCode", campusCode);
        criteria.addColumnIsNull("ITM_CLOSED_DT");

        QueryByCriteria openItemsQuery = QueryFactory.newQuery(CashieringItemInProcess.class, criteria);
        return new ArrayList<CashieringItemInProcess>( getPersistenceBrokerTemplate().getCollectionByQuery(openItemsQuery) );
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#findRecentlyClosedItemsInProcess(java.lang.String)
     */
    public List<CashieringItemInProcess> findRecentlyClosedItemsInProcess(String campusCode) {
        List<CashieringItemInProcess> closedItems = new ArrayList<CashieringItemInProcess>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("campusCode", campusCode);
        criteria.addColumnNotNull("ITM_CLOSED_DT");
        Calendar thirtyDaysAgo = new GregorianCalendar();
        thirtyDaysAgo.add(Calendar.DAY_OF_YEAR, -30);
        criteria.addGreaterThan("itemClosedDate", new java.sql.Date(thirtyDaysAgo.getTimeInMillis()));

        QueryByCriteria closedItemsQuery = QueryFactory.newQuery(CashieringItemInProcess.class, criteria);
        Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(closedItemsQuery);
        while (iter.hasNext()) {
            closedItems.add((CashieringItemInProcess) iter.next());
        }
        return closedItems;
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#findCoinDetailByCashieringStatus(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public CoinDetail findCoinDetailByCashieringStatus(String documentNumber, String documentTypeCode, String cashieringStatus) {
        return (CoinDetail) retrieveCashDetail(documentNumber, documentTypeCode, cashieringStatus, CoinDetail.class);
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#findCurrencyDetailByCashieringStatus(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public CurrencyDetail findCurrencyDetailByCashieringStatus(String documentNumber, String documentTypeCode, String cashieringStatus) {
        return (CurrencyDetail) retrieveCashDetail(documentNumber, documentTypeCode, cashieringStatus, CurrencyDetail.class);
    }

    /**
     * This takes the primary keys for a cash or currency detail record and returns an OJB criteria for it
     * 
     * @param documentNumber document number to retrieve
     * @param documentTypeCode type code of the document
     * @param cashieringStatus the cashiering status
     * @return a criteria, based on all of the given information
     */
    protected Criteria getCashDetailCriteria(String documentNumber, String documentTypeCode, String cashieringStatus) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        criteria.addEqualTo("financialDocumentTypeCode", documentTypeCode);
        criteria.addEqualTo("cashieringStatus", cashieringStatus);
        return criteria;
    }

    /**
     * This method retrieves a cash detail from the database
     * 
     * @param documentNumber the document number to retrieve from
     * @param documentTypeCode the document type of the document the cash detail to look up is associated with
     * @param cashieringStatus the cashiering status to look up from
     * @param detailType the class of the cash detail type we want
     * @return the cash detail type record
     */
    protected Object retrieveCashDetail(String documentNumber, String documentTypeCode, String cashieringStatus, Class detailType) {
        QueryByCriteria cashDetailQuery = QueryFactory.newQuery(detailType, getCashDetailCriteria(documentNumber, documentTypeCode, cashieringStatus));
        Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(cashDetailQuery);
        return (iter.hasNext() ? iter.next() : null);
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#selectCashieringChecksForDeposit(java.lang.String, java.lang.Integer)
     */
    public List<Check> selectCashieringChecksForDeposit(String documentNumber, Integer depositLineNumber) {
        QueryByCriteria depositedChecksQuery = QueryFactory.newQuery(CheckBase.class, createDepositedCashieringCheckCriteria(documentNumber, depositLineNumber));
        return putResultsIntoCheckList(getPersistenceBrokerTemplate().getIteratorByQuery(depositedChecksQuery));
    }

    /**
     * This method creates a criteria to find the cashiering checks associated with a given deposit
     * 
     * @param documentNumber the document number the deposit is associated with
     * @param depositLineNumber the line number of the deposit
     * @return a criteria to find those checks
     */
    protected Criteria createDepositedCashieringCheckCriteria(String documentNumber, Integer depositLineNumber) {
        Criteria criteria = getCashDetailCriteria(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CheckSources.CASH_MANAGEMENT);
        criteria.addEqualTo("financialDocumentDepositLineNumber", depositLineNumber);
        return criteria;
    }

    /**
     * This method puts the check elements of an iterator into a list
     * 
     * @param iter an iterator with checks results in it
     * @return a list of checks
     */
    protected List<Check> putResultsIntoCheckList(Iterator iter) {
        List<Check> result = new ArrayList<Check>();
        while (iter.hasNext()) {
            result.add((Check) iter.next());
        }
        return result;
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#selectUndepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectUndepositedCashieringChecks(String documentNumber) {
        QueryByCriteria undepositedChecksQuery = QueryFactory.newQuery(CheckBase.class, createUndepositedCashieringCheckCriteria(documentNumber));
        return putResultsIntoCheckList(getPersistenceBrokerTemplate().getIteratorByQuery(undepositedChecksQuery));
    }

    /**
     * This method creates the criteria to find undeposited cashiering checks
     * 
     * @param documentNumber the document number undeposited checks are associated with
     * @return a criteria to find them
     */
    protected Criteria createUndepositedCashieringCheckCriteria(String documentNumber) {
        Criteria criteria = getCashDetailCriteria(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CheckSources.CASH_MANAGEMENT);
        criteria.addColumnIsNull("FDOC_DPST_LN_NBR");
        return criteria;
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#selectDepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectDepositedCashieringChecks(String documentNumber) {
        QueryByCriteria depositedChecksQuery = QueryFactory.newQuery(CheckBase.class, createDepositedCashieringCheckCriteria(documentNumber));
        return putResultsIntoCheckList(getPersistenceBrokerTemplate().getIteratorByQuery(depositedChecksQuery));
    }

    /**
     * This method creates the criteria to find deposited checks
     * 
     * @param documentNumber the CM document the checks are associated with
     * @return a criteria to find deposited checks
     */
    protected Criteria createDepositedCashieringCheckCriteria(String documentNumber) {
        Criteria criteria = getCashDetailCriteria(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CheckSources.CASH_MANAGEMENT);
        criteria.addColumnNotNull("FDOC_DPST_LN_NBR");
        return criteria;
    }

    /**
     * This method retrieves all currency details associated with a cash management document
     * 
     * @param documentNumber the document number of the cash management document to get currency details for
     * @return a list of currency details
     */
    public List<CurrencyDetail> getAllCurrencyDetails(String documentNumber) {
        QueryByCriteria allCurrencyDetailsQuery = QueryFactory.newQuery(CurrencyDetail.class, getAllCashDetailCriteria(documentNumber));
        List<CurrencyDetail> result = new ArrayList<CurrencyDetail>();
        for (Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(allCurrencyDetailsQuery); iter.hasNext();) {
            result.add((CurrencyDetail) iter.next());
        }
        return result;
    }

    /**
     * This method gets all coin details for a particular document number, irregardless of cashiering record source
     * 
     * @param documentNumber the document number to find cash details for
     * @return hopefully, a bunch of coin details
     */
    public List<CoinDetail> getAllCoinDetails(String documentNumber) {
        QueryByCriteria allCoinDetailsQuery = QueryFactory.newQuery(CoinDetail.class, getAllCashDetailCriteria(documentNumber));
        List<CoinDetail> result = new ArrayList<CoinDetail>();
        for (Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(allCoinDetailsQuery); iter.hasNext();) {
            result.add((CoinDetail) iter.next());
        }
        return result;
    }

    /**
     * This method creates details for getting all of a certain cash detail, irregardless of cashiering record source
     * 
     * @param documentNumber the document number to get cash details for
     * @return the criteria that will allow the retrieval of the right cash details
     */
    protected Criteria getAllCashDetailCriteria(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        criteria.addEqualTo("financialDocumentTypeCode", CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        return criteria;
    }

    /**
     * @see org.kuali.kfs.fp.document.dataaccess.CashManagementDao#selectNextAvailableCheckLineNumber(java.lang.String)
     */
    public Integer selectNextAvailableCheckLineNumber(String documentNumber) {
        if (documentNumber != null) {
            // select all cashiering checks associated with document
            Criteria criteria = new Criteria();
            criteria.addEqualTo("documentNumber", documentNumber);
            criteria.addEqualTo("cashieringStatus", KFSConstants.CheckSources.CASH_MANAGEMENT);
            criteria.addEqualTo("financialDocumentTypeCode", CashieringTransaction.DETAIL_DOCUMENT_TYPE);

            QueryByCriteria cmChecksQuery = QueryFactory.newQuery(CheckBase.class, criteria);
            cmChecksQuery.addOrderByDescending("sequenceId");
            Iterator allChecksIter = getPersistenceBrokerTemplate().getIteratorByQuery(cmChecksQuery);
            if (allChecksIter.hasNext()) {
                return new Integer((((Check) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(allChecksIter)).getSequenceId()).intValue() + 1);
            }
            else {
                return new Integer(1);
            }
        }
        else {
            return null;
        }
    }

}
