/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.impl.BalancingDaoJdbc;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory;
import org.kuali.kfs.module.ld.businessobject.LaborEntryHistory;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;

/**
 * JDBC implementation of LedgerBalancingDao. This essentially is a copy of one table to another with
 * group by in some cases. Hence the idea is that JDBC is much faster in this case then creating
 * BO objects that are essentially not necessary.
 */
public class LaborBalancingDaoJdbc extends BalancingDaoJdbc implements LedgerBalancingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalancingDaoJdbc.class);
    
    protected static final String BALANCE_LABOR_KEY_FIELDS = LaborConstants.ColumnNames.POSITION_NUMBER + ", " + LaborConstants.ColumnNames.EMPLOYEE_IDENTIFIER;
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerBalancingDao#populateLedgerEntryHistory(java.lang.Integer)
     */
    public int populateLedgerEntryHistory(Integer universityFiscalYear) {
        String laborEntryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(LedgerEntry.class).getFullTableName();
        String laborEntryHistoryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(LaborEntryHistory.class).getFullTableName();
        
        String sql = "INSERT INTO " + laborEntryHistoryTableName + " (" + ENTRY_KEY_FIELDS + ", " + VER_NBR + ", " + LaborConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT + ", " + ROW_COUNT + ")"
        + " SELECT " + ENTRY_KEY_FIELDS + ", 1, sum(" +  LaborConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT + "), count(*)"
        + " FROM " + laborEntryTableName
        + " WHERE " + LaborConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + " >= " + universityFiscalYear
        + " GROUP BY " + ENTRY_KEY_FIELDS;
        
        LOG.debug(sql);
        
        return getSimpleJdbcTemplate().update(sql);
    }
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerBalancingDao#populateLedgerBalanceHistory(java.lang.Integer)
     */
    public int populateLedgerBalanceHistory(Integer universityFiscalYear) {
        String laborBalanceTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(LedgerBalance.class).getFullTableName();
        String laborBalanceHistoryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(LaborBalanceHistory.class).getFullTableName();
        
        String sql = "INSERT INTO " + laborBalanceHistoryTableName + " (" + BALANCE_KEY_FIELDS + ", " + BALANCE_LABOR_KEY_FIELDS + ", " + VER_NBR + ", " + BALANCE_AMOUNT_FIELDS + ", " + BALANCE_MONTH_AMOUNT_FIELDS + ")"
        + " SELECT " + BALANCE_KEY_FIELDS + ", " + BALANCE_LABOR_KEY_FIELDS + ", 1, " + BALANCE_AMOUNT_FIELDS + ", " + BALANCE_MONTH_AMOUNT_FIELDS
        + " FROM " + laborBalanceTableName
        + " WHERE " + LaborConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + " >= " + universityFiscalYear;
        
        LOG.debug(sql);
        
        return getSimpleJdbcTemplate().update(sql);
    }
}
