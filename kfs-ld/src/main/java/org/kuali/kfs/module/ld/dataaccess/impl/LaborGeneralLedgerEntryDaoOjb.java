/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
