/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/dataaccess/impl/OriginEntrySourceDaoOjb.java,v $
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

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntrySourceDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

public class OriginEntrySourceDaoOjb extends PersistenceBrokerDaoSupport implements OriginEntrySourceDao {

    private static final String FINANCIAL_DOCUMENT_REVERSAL_DATE = "financialDocumentReversalDate";
    private static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    private static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
    private static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
    private static final String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
    private static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
    private static final String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
    private static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
    private static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
    private static final String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";

    public OriginEntrySourceDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.OriginEntrySourceDao#findAll()
     */
    public Collection findAll() {
        QueryByCriteria query = QueryFactory.newQuery(OriginEntrySource.class, (Criteria) null);// "SELECT * FROM
        // GL_ORIGIN_ENTRY_SRC_T");
        Collection thawed = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        // Collection frozen = Collections.unmodifiableCollection(thawed);
        return thawed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.OriginEntrySourceDao#findBySourceCode(java.lang.String)
     */
    public OriginEntrySource findBySourceCode(String code) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("code", code);
        QueryByCriteria query = QueryFactory.newQuery(OriginEntrySource.class, criteria);
        return (OriginEntrySource) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

}
