/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.dao.ojb;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.springframework.dao.DataAccessException;

import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.financial.bo.CashieringItemInProcess;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.bo.CashieringTransaction;
import org.kuali.module.financial.dao.CashManagementDao;

public class CashManagementDaoOjb extends PlatformAwareDaoBaseOjb implements CashManagementDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashManagementDaoOjb.class);
    
    public CashManagementDaoOjb() {
        super();
    }
    
    /**
     * @see org.kuali.module.financial.dao.CashManagementDao#findOpenItemsInProcessByWorkgroupName(java.lang.String)
     */
    public List<CashieringItemInProcess> findOpenItemsInProcessByWorkgroupName(String wrkgrpName) throws DataAccessException {
        List<CashieringItemInProcess> openItems = new ArrayList<CashieringItemInProcess>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("workgroupName", wrkgrpName);
        criteria.addColumnIsNull("ITM_CLOSED_DT");
        
        QueryByCriteria openItemsQuery = QueryFactory.newQuery(CashieringItemInProcess.class, criteria);
        Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(openItemsQuery);
        while (iter.hasNext()) {
            openItems.add((CashieringItemInProcess)iter.next());
        }
        return openItems;
    }

    /**
     * @see org.kuali.module.financial.dao.CashManagementDao#findCoinDetailByCashieringRecordSource(java.lang.String, java.lang.String, java.lang.String)
     */
    public CoinDetail findCoinDetailByCashieringRecordSource(String documentNumber, String documentTypeCode, String cashieringRecordSource) {
        return (CoinDetail)retrieveCashDetail(documentNumber, documentTypeCode, cashieringRecordSource, CoinDetail.class);
    }

    /**
     * @see org.kuali.module.financial.dao.CashManagementDao#findCurrencyDetailByCashieringRecordSource(java.lang.String, java.lang.String, java.lang.String)
     */
    public CurrencyDetail findCurrencyDetailByCashieringRecordSource(String documentNumber, String documentTypeCode, String cashieringRecordSource) {
        return (CurrencyDetail)retrieveCashDetail(documentNumber, documentTypeCode, cashieringRecordSource, CurrencyDetail.class);
    }

    /**
     * 
     * This takes the primary keys for a cash or currency detail record and returns an OJB criteria for it
     * @param documentNumber document number to retrieve
     * @param documentTypeCode type code of the document
     * @param cashieringRecordSource the cashiering record source
     * @return a criteria, based on all of the given information
     */
    private Criteria getCashDetailCriteria(String documentNumber, String documentTypeCode, String cashieringRecordSource) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        criteria.addEqualTo("financialDocumentTypeCode", documentTypeCode);
        criteria.addEqualTo("cashieringRecordSource", cashieringRecordSource);
        return criteria;
    }
    
    /**
     * This method retrieves a cash detail from the database
     * @param documentNumber the document number to retrieve from
     * @param documentTypeCode the document type of the document the cash detail to look up is associated with
     * @param cashieringRecordSource the cashiering record source to look up from
     * @param detailType the class of the cash detail type we want
     * @return the cash detail type record
     */
    private Object retrieveCashDetail(String documentNumber, String documentTypeCode, String cashieringRecordSource, Class detailType) {
        QueryByCriteria cashDetailQuery = QueryFactory.newQuery(detailType, getCashDetailCriteria(documentNumber, documentTypeCode, cashieringRecordSource));
        Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(cashDetailQuery);
        return (iter.hasNext() ? iter.next() : null);
    }

    /**
     * @see org.kuali.module.financial.dao.CashManagementDao#selectCashieringChecksForDeposit(java.lang.String, java.lang.Integer)
     */
    public List<Check> selectCashieringChecksForDeposit(String documentNumber, Integer depositLineNumber) {
        QueryByCriteria depositedChecksQuery = QueryFactory.newQuery(CheckBase.class, createDepositedCashieringCheckCriteria(documentNumber, depositLineNumber));
        return putResultsIntoCheckList(getPersistenceBrokerTemplate().getIteratorByQuery(depositedChecksQuery));
    }
    
    /**
     * 
     * This method creates a criteria to find the cashiering checks associated with a given deposit
     * @param documentNumber the document number the deposit is associated with
     * @param depositLineNumber the line number of the deposit
     * @return a criteria to find those checks
     */
    private Criteria createDepositedCashieringCheckCriteria(String documentNumber, Integer depositLineNumber) {
        Criteria criteria = getCashDetailCriteria(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CheckSources.CASH_MANAGEMENT);
        criteria.addEqualTo("financialDocumentDepositLineNumber", depositLineNumber);
        return criteria;
    }
    
    /**
     * 
     * This method puts the check elements of an iterator into a list
     * @param iter an iterator with checks results in it
     * @return a list of checks
     */
    private List<Check> putResultsIntoCheckList(Iterator iter) {
        List<Check> result = new ArrayList<Check>();
        while (iter.hasNext()) {
            result.add((Check)iter.next());
        }
        return result;
    }

    /**
     * @see org.kuali.module.financial.dao.CashManagementDao#selectUndepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectUndepositedCashieringChecks(String documentNumber) {
        QueryByCriteria undepositedChecksQuery = QueryFactory.newQuery(CheckBase.class, createUndepositedCashieringCheckCriteria(documentNumber));
        return putResultsIntoCheckList(getPersistenceBrokerTemplate().getIteratorByQuery(undepositedChecksQuery));
    }
    
    /**
     * 
     * This method creates the criteria to find undeposited cashiering checks
     * @param documentNumber the document number undeposited checks are associated with
     * @return a criteria to find them
     */
    private Criteria createUndepositedCashieringCheckCriteria(String documentNumber) {
        Criteria criteria = getCashDetailCriteria(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CheckSources.CASH_MANAGEMENT);
        criteria.addColumnIsNull("FDOC_DPST_LN_NBR");
        return criteria;
    }
    
    /**
     * @see org.kuali.module.financial.dao.CashManagementDao#selectDepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectDepositedCashieringChecks(String documentNumber) {
        QueryByCriteria depositedChecksQuery = QueryFactory.newQuery(CheckBase.class, createDepositedCashieringCheckCriteria(documentNumber));
        return putResultsIntoCheckList(getPersistenceBrokerTemplate().getIteratorByQuery(depositedChecksQuery));
    }

    /**
     * This method creates the criteria to find deposited checks
     * @param documentNumber the CM document the checks are associated with
     * @return a criteria to find deposited checks
     */
    private Criteria createDepositedCashieringCheckCriteria(String documentNumber) {
        Criteria criteria = getCashDetailCriteria(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CheckSources.CASH_MANAGEMENT);
        criteria.addColumnNotNull("FDOC_DPST_LN_NBR");
        return criteria;
    }
    
    /**
     * This method retrieves all currency details associated with a cash management document
     * @param documentNumber the document number of the cash management document to get currency details for
     * @return a list of currency details
     */
    public List<CurrencyDetail> getAllCurrencyDetails(String documentNumber) {
        QueryByCriteria allCurrencyDetailsQuery = QueryFactory.newQuery(CurrencyDetail.class, getAllCashDetailCriteria(documentNumber));
        List<CurrencyDetail> result = new ArrayList<CurrencyDetail>();
        for (Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(allCurrencyDetailsQuery); iter.hasNext();) {
            result.add((CurrencyDetail)iter.next());
        }
        return result;
    }
    
    /**
     * This method gets all coin details for a particular document number, irregardless of cashiering record source
     * @param documentNumber the document number to find cash details for
     * @return hopefully, a bunch of coin details
     */
    public List<CoinDetail> getAllCoinDetails(String documentNumber) {
        QueryByCriteria allCoinDetailsQuery = QueryFactory.newQuery(CoinDetail.class, getAllCashDetailCriteria(documentNumber));
        List<CoinDetail> result = new ArrayList<CoinDetail>();
        for (Iterator iter = getPersistenceBrokerTemplate().getIteratorByQuery(allCoinDetailsQuery); iter.hasNext();) {
            result.add((CoinDetail)iter.next());
        }
        return result;
    }
    
    /**
     * This method creates details for getting all of a certain cash detail, irregardless of cashiering record source
     * @param documentNumber the document number to get cash details for
     * @return the criteria that will allow the retrieval of the right cash details
     */
    private Criteria getAllCashDetailCriteria(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        criteria.addEqualTo("financialDocumentTypeCode", CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        return criteria;
    }
}
