/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
    private static final String FINANCIAL_DOCUMENT_NUMBER = "financialDocumentNumber";
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
