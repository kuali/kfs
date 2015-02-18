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
