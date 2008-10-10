/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.batch.dataaccess.impl;

import java.util.Arrays;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao;
import org.kuali.kfs.module.cab.businessobject.BatchParameters;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountHistory;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountHistory;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class ExtractDaoOjb extends PlatformAwareDaoBaseOjb implements ExtractDao {
    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findMatchingGLEntries(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<Entry> findMatchingGLEntries(BatchParameters batchParameters) {
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.Entry.TRANSACTION_DATE_TIME_STAMP, batchParameters.getLastRunTime());
        criteria.addNotIn(CabPropertyConstants.Entry.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());
        criteria.addNotIn(CabPropertyConstants.Entry.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());
        criteria.addIn(CabPropertyConstants.Entry.FINANCIAL_BALANCE_TYPE_CODE, batchParameters.getIncludedFinancialBalanceTypeCodes());
        criteria.addIn(CabPropertyConstants.Entry.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());
        criteria.addNotIn(CabPropertyConstants.Entry.UNIVERSITY_FISCAL_PERIOD_CODE, batchParameters.getExcludedFiscalPeriods());
        criteria.addNotIn(CabPropertyConstants.Entry.FINANCIAL_DOCUMENT_TYPE_CODE, batchParameters.getExcludedDocTypeCodes());
        QueryByCriteria query = new QueryByCriteria(Entry.class, criteria);
        query.addOrderByAscending(CabPropertyConstants.Entry.DOCUMENT_NUMBER);
        query.addOrderByAscending(CabPropertyConstants.Entry.TRANSACTION_DATE_TIME_STAMP);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findPreTaggablePOAccounts(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<PurchaseOrderAccount> findPreTaggablePOAccounts(BatchParameters batchParameters) {
        Criteria statusCodeCond1 = new Criteria();
        statusCodeCond1.addEqualTo(CabPropertyConstants.PreTagExtract.PURAP_CAPITAL_ASSET_SYSTEM_STATE_CODE, CabConstants.CAPITAL_ASSET_SYSTEM_STATE_CODE_NEW);
        Criteria statusCodeOrCond = new Criteria();
        statusCodeOrCond.addIsNull(CabPropertyConstants.PreTagExtract.PURAP_CAPITAL_ASSET_SYSTEM_STATE_CODE);
        statusCodeOrCond.addOrCriteria(statusCodeCond1);
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.PreTagExtract.PO_INITIAL_OPEN_DATE, batchParameters.getLastRunDate());
        criteria.addEqualTo(CabPropertyConstants.PreTagExtract.PO_STATUS_CODE, CabConstants.PO_STATUS_CODE_OPEN);
        criteria.addAndCriteria(statusCodeOrCond);
        criteria.addGreaterOrEqualThan(CabPropertyConstants.PreTagExtract.PURAP_ITEM_UNIT_PRICE, batchParameters.getCapitalizationLimitAmount());
        criteria.addNotIn(CabPropertyConstants.PreTagExtract.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());
        criteria.addNotIn(CabPropertyConstants.PreTagExtract.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());
        criteria.addIn(CabPropertyConstants.PreTagExtract.FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());
        QueryByCriteria query = new QueryByCriteria(PurchaseOrderAccount.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findPurapPendingGLEntries(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<GeneralLedgerPendingEntry> findPurapPendingGLEntries(BatchParameters batchParameters) {
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.GeneralLedgerPendingEntry.TRANSACTION_ENTRY_PROCESSED_TS, batchParameters.getLastRunTime());
        criteria.addNotIn(CabPropertyConstants.GeneralLedgerPendingEntry.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());
        criteria.addNotIn(CabPropertyConstants.GeneralLedgerPendingEntry.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());
        criteria.addIn(CabPropertyConstants.GeneralLedgerPendingEntry.FINANCIAL_BALANCE_TYPE_CODE, batchParameters.getIncludedFinancialBalanceTypeCodes());
        criteria.addIn(CabPropertyConstants.GeneralLedgerPendingEntry.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());
        criteria.addNotIn(CabPropertyConstants.GeneralLedgerPendingEntry.UNIVERSITY_FISCAL_PERIOD_CODE, batchParameters.getExcludedFiscalPeriods());
        criteria.addIn(CabPropertyConstants.GeneralLedgerPendingEntry.FINANCIAL_DOCUMENT_TYPE_CODE, Arrays.asList(CabConstants.PREQ, CabConstants.CM));
        QueryByCriteria query = new QueryByCriteria(GeneralLedgerPendingEntry.class, criteria);
        query.addOrderByAscending(CabPropertyConstants.GeneralLedgerPendingEntry.DOCUMENT_NUMBER);
        Collection<GeneralLedgerPendingEntry> glPendingList = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return glPendingList;
    }


    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findCreditMemoAccountHistory(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<CreditMemoAccountHistory> findCreditMemoAccountHistory(BatchParameters batchParameters) {
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.CreditMemoAccountHistory.ACCOUNT_HISTORY_TIMESTAMP, batchParameters.getLastRunTime());
        criteria.addNotIn(CabPropertyConstants.CreditMemoAccountHistory.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());
        criteria.addNotIn(CabPropertyConstants.CreditMemoAccountHistory.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());
        criteria.addIn(CabPropertyConstants.CreditMemoAccountHistory.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());
        QueryByCriteria query = new QueryByCriteria(CreditMemoAccountHistory.class, criteria);
        query.addOrderByAscending(CabPropertyConstants.CreditMemoAccountHistory.ACCOUNT_HISTORY_TIMESTAMP);
        Collection<CreditMemoAccountHistory> historyRecs = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return historyRecs;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao#findPaymentRequestAccountHistory(org.kuali.kfs.module.cab.businessobject.BatchParameters)
     */
    public Collection<PaymentRequestAccountHistory> findPaymentRequestAccountHistory(BatchParameters batchParameters) {
        Criteria criteria = new Criteria();
        criteria.addGreaterThan(CabPropertyConstants.PaymentRequestAccountHistory.ACCOUNT_HISTORY_TIMESTAMP, batchParameters.getLastRunTime());
        criteria.addNotIn(CabPropertyConstants.PaymentRequestAccountHistory.CHART_OF_ACCOUNTS_CODE, batchParameters.getExcludedChartCodes());
        criteria.addNotIn(CabPropertyConstants.PaymentRequestAccountHistory.ACCOUNT_SUB_FUND_GROUP_CODE, batchParameters.getExcludedSubFundCodes());
        criteria.addIn(CabPropertyConstants.PaymentRequestAccountHistory.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE, batchParameters.getIncludedFinancialObjectSubTypeCodes());
        QueryByCriteria query = new QueryByCriteria(PaymentRequestAccountHistory.class, criteria);
        query.addOrderByAscending(CabPropertyConstants.PaymentRequestAccountHistory.ACCOUNT_HISTORY_TIMESTAMP);
        Collection<PaymentRequestAccountHistory> historyRecs = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return historyRecs;
    }
}
