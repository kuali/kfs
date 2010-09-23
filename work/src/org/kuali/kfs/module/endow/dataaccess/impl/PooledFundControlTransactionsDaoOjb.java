/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.PooledFundControlTransactionsDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class PooledFundControlTransactionsDaoOjb extends PlatformAwareDaoBaseOjb implements PooledFundControlTransactionsDao {

    protected KEMService kemService;
    
    /*
     * Select all records in END_TRAN_ARCHV_T where
     *  1. TRAN_TYP_CD is EAI, EAD, ECI, or ECD and
     *  2. TRAN_PSTD_DT is equal to the current date and
     *  3. END_TRAN_ARCHV_SEC_T:TRAN_SEC_ID is in END_POOL_FND_CTRL_T:POOL_SEC_ID  
     */

    public List<PooledFundControl> getPooledFundControlTransactions(List<String> transactionTypeCodes) {
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = getTransactionArchiveSecurity(transactionTypeCodes);
        Criteria crit = new Criteria();
        for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
            Criteria c = new Criteria();
            c.addEqualTo(EndowPropertyConstants.POOL_SECURITY_ID, transactionArchiveSecurity.getSecurityId());
            crit.addOrCriteria(c);            
        }
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(TransactionArchiveSecurity.class, crit);
        return (List<PooledFundControl>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PooledFundControl.class, crit));        
    }

    public List<TransactionArchiveSecurity> getTransactionArchiveSecurity(List<String> transactionTypeCodes) {

        // get the list of TransactionArchive with the specified transaction type code
        Criteria crit1 = new Criteria();
        crit1.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, kemService.getCurrentDate());
        if (transactionTypeCodes != null && !transactionTypeCodes.isEmpty()) {
            crit1.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, transactionTypeCodes);
        }
        List<TransactionArchive> transactionArchiveRecords = (List<TransactionArchive>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(TransactionArchive.class, crit1));

        // get the list of TransactionArchiveSecurity joined with TransactionArchive and PooledFundControl
        Criteria subCrit = new Criteria();
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(PooledFundControl.class, subCrit);
        subQuery.setAttributes(new String[] {EndowPropertyConstants.POOL_SECURITY_ID});
        Criteria crit2 = new Criteria();
        crit2.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_SECURITY_ID, subQuery);
        Criteria crit3 = new Criteria();
        for (TransactionArchive transactionArchive : transactionArchiveRecords) {
            Criteria c = new Criteria();
            c.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, transactionArchive.getDocumentNumber());
            c.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, transactionArchive.getLineNumber());
            c.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, transactionArchive.getLineTypeCode());
            crit3.addOrCriteria(c);
        }
        crit2.addAndCriteria(crit3);
        crit2.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_SECURITY_ID);

//        ReportQueryByCriteria subQuery2 = QueryFactory.newReportQuery(TransactionArchiveSecurity.class, crit2);
//        subQuery2.setAttributes(new String[] {EndowPropertyConstants.TRANSACTION_ARCHIVE_SECURITY_ID, "sum(holdingCost)"});
//        subQuery2.addGroupBy(EndowPropertyConstants.TRANSACTION_ARCHIVE_SECURITY_ID);
       
        return (List<TransactionArchiveSecurity>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(TransactionArchiveSecurity.class, crit2));
    }
    
    public List<TransactionArchive> getTransactionArchive(List<String> trnsactionTypeCodes) {
        
        Criteria subCri = new Criteria();
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(PooledFundControl.class, subCri);
        subQuery.setAttributes(new String[] {EndowPropertyConstants.POOL_SECURITY_ID});
        Criteria crit1 = new Criteria();
        crit1.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_SECURITY_ID, subQuery);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = (List<TransactionArchiveSecurity>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(TransactionArchiveSecurity.class, crit1));
                
        Criteria crit2 = new Criteria();
        crit2.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, kemService.getCurrentDate());
        crit2.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, trnsactionTypeCodes);
        Criteria crit3 = new Criteria();
        for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
            Criteria c = new Criteria();
            c.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, transactionArchiveSecurity.getDocumentNumber());
            c.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, transactionArchiveSecurity.getLineNumber());
            c.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, transactionArchiveSecurity.getLineTypeCode());
            crit3.addOrCriteria(c);
        }
        crit2.addAndCriteria(crit3);
               
        return (List<TransactionArchive>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(TransactionArchive.class, crit2));
    }    
    

    
    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

}
