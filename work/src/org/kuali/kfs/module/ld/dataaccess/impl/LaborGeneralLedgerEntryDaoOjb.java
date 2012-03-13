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
package org.kuali.kfs.module.ld.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborGeneralLedgerEntryDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This is the data access object for labor general ledger entry
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry
 */
public class LaborGeneralLedgerEntryDaoOjb extends PlatformAwareDaoBaseOjb implements LaborGeneralLedgerEntryDao {

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborGeneralLedgerEntryDao#getMaxSequenceNumber(org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry)
     */
    public Integer getMaxSequenceNumber(LaborGeneralLedgerEntry laborGeneralLedgerEntry) {
        Criteria criteria = new Criteria();

        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, laborGeneralLedgerEntry.getUniversityFiscalYear());
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, laborGeneralLedgerEntry.getChartOfAccountsCode());
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, laborGeneralLedgerEntry.getAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, laborGeneralLedgerEntry.getSubAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, laborGeneralLedgerEntry.getFinancialObjectCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, laborGeneralLedgerEntry.getFinancialSubObjectCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, laborGeneralLedgerEntry.getFinancialBalanceTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, laborGeneralLedgerEntry.getFinancialObjectTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, laborGeneralLedgerEntry.getUniversityFiscalPeriodCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, laborGeneralLedgerEntry.getFinancialDocumentTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, laborGeneralLedgerEntry.getFinancialSystemOriginationCode());
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, laborGeneralLedgerEntry.getDocumentNumber());

        ReportQueryByCriteria query = QueryFactory.newReportQuery(LaborGeneralLedgerEntry.class, criteria);
        query.setAttributes(new String[] { "max(" + KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER + ")" });

        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        Integer maxSequenceNumber = Integer.valueOf(0);

        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            if (data[0] != null) {
                maxSequenceNumber = ((BigDecimal) data[0]).intValue();
            }
        }
        return maxSequenceNumber;
    }
}
