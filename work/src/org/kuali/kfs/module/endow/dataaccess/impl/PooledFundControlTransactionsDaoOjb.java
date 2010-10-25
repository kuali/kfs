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

import java.sql.Date;
import java.util.ArrayList;
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

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundControlTransactionsDao#getAllPooledFundControlTransaction()
     */
    public List<PooledFundControl> getAllPooledFundControlTransaction() {
        Criteria crit = new Criteria();
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(TransactionArchiveSecurity.class, crit);
        return (List<PooledFundControl>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PooledFundControl.class, crit));        
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundControlTransactionsDao#getTransactionArchiveSecurityWithSecurityId(java.lang.String, java.util.List)
     */
    public List<TransactionArchiveSecurity> getTransactionArchiveSecurityWithSecurityId(String securityId, List<String> documentTypeNames, Date currentDate) {
        // get the list of TransactionArchiveSecurity that has securityId
        Criteria crit = new Criteria();
        crit.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_SECURITY_ID, securityId);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = (List<TransactionArchiveSecurity>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(TransactionArchiveSecurity.class, crit));
        
        // filter transactionArchiveSecurityRecords 
        List<TransactionArchiveSecurity> fnalTransactionArchiveSecurityRecords = new ArrayList<TransactionArchiveSecurity>();
        for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
            if (existsTransactionArchiveSecurityWithDocNames(documentTypeNames, transactionArchiveSecurity, currentDate)) {
                fnalTransactionArchiveSecurityRecords.add(transactionArchiveSecurity);
            }
        }
        
        return fnalTransactionArchiveSecurityRecords;
    }    

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.PooledFundControlTransactionsDao#getTransactionArchiveWithSecurityAndDocNames(java.lang.String, java.util.List)
     */
    public List<TransactionArchive> getTransactionArchiveWithSecurityAndDocNames(String securityId, List<String> documentTypeNames, Date currentDate) {
        // get the list of TransactionArchiveSecurity that has securityId
        Criteria crit1 = new Criteria();
        crit1.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_SECURITY_ID, securityId);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = (List<TransactionArchiveSecurity>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(TransactionArchiveSecurity.class, crit1));

        // get the list of TransactionArchive matching TransactionArchiveSecurity
        Criteria crit2 = new Criteria();
        crit2.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE,currentDate);
        if (documentTypeNames != null && !documentTypeNames.isEmpty()) {
            crit2.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, documentTypeNames);
        }
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
     * Checks to see if the TransactionArchiveSecurity exists in TransactionArchive with the given condition 
     * @param documentTypeNames
     * @param transactionArchiveSecurity
     * @return
     */
    protected boolean existsTransactionArchiveSecurityWithDocNames(List<String> documentTypeNames, TransactionArchiveSecurity transactionArchiveSecurity, Date currentDate) {
        Criteria crit = new Criteria();
        //crit.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, kemService.getCurrentDate());
        crit.addLessOrEqualThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, currentDate);
        if (documentTypeNames != null && !documentTypeNames.isEmpty()) {
            crit.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, documentTypeNames);
        }
        crit.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, transactionArchiveSecurity.getDocumentNumber());
        crit.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, transactionArchiveSecurity.getLineNumber());
        crit.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, transactionArchiveSecurity.getLineTypeCode());
        
        return getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(TransactionArchive.class, crit)) > 0 ? true : false;
    }
    
}
