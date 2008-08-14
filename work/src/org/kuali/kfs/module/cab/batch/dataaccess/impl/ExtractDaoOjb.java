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
        Collection<Entry> glEntryList = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return glEntryList;
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
        Collection<GeneralLedgerPendingEntry> glPendingList = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return glPendingList;
    }
}
