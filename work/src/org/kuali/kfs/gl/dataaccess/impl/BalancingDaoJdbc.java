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
package org.kuali.kfs.gl.dataaccess.impl;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.AccountBalanceHistory;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.EncumbranceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.dataaccess.BalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

/**
 * JDBC implementation of BalancingDao and LedgerBalancingDao. This essentially is a copy of one table to another with
 * group by in some cases. Hence the idea is that JDBC is much faster in this case then creating
 * BO objects that are essentially not necessary.
 */
public class BalancingDaoJdbc extends PlatformAwareDaoBaseJdbc implements BalancingDao, LedgerBalancingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingDaoJdbc.class);
    
    protected static final String VER_NBR = "VER_NBR";
    protected static final String ROW_COUNT = "ROW_CNT";

    protected static final String ENTRY_KEY_FIELDS = GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + ", " + GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE + ", " + GeneralLedgerConstants.ColumnNames.OBJECT_CODE + ", " + GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE + ", " + GeneralLedgerConstants.ColumnNames.FISCAL_PERIOD_CODE+ ", " + GeneralLedgerConstants.ColumnNames.DEBIT_CREDIT_CODE;

    protected static final String BALANCE_KEY_FIELDS = GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + ", " + GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE + ", " + GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER + ", " + GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER + ", " + GeneralLedgerConstants.ColumnNames.OBJECT_CODE + ", " + GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE + ", " + GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE + ", " + GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE;
    protected static final String BALANCE_AMOUNT_FIELDS = GeneralLedgerConstants.ColumnNames.ANNUAL_BALANCE + ", " + GeneralLedgerConstants.ColumnNames.BEGINNING_BALANCE + ", " + GeneralLedgerConstants.ColumnNames.CONTRACT_AND_GRANTS_BEGINNING_BALANCE;
    protected static final String BALANCE_MONTH_AMOUNT_FIELDS = "MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT";
    
    protected static final String ACCOUNT_BALANCE_KEY_FIELDS = GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + ", " + GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE + ", " + GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER + ", " + GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER + ", " + GeneralLedgerConstants.ColumnNames.OBJECT_CODE + ", " + GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE;
    protected static final String ACCOUNT_BALANCE_AMOUNT_FIELDS = GeneralLedgerConstants.ColumnNames.CURRENT_BUDGET_LINE_BALANCE_AMOUNT + ", " + GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ACTUALS_BALANCE_AMOUNT + ", " + GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_BALANCE_AMOUNT;
    
    protected static final String ENCUMBRANCE_KEY_FIELDS = GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + ", " + GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE + ", " + GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER + ", " + GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER + ", " + GeneralLedgerConstants.ColumnNames.OBJECT_CODE + ", " + GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE + ", " + GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE + ", " + GeneralLedgerConstants.ColumnNames.FINANCIAL_DOCUMENT_TYPE_CODE + ", " + GeneralLedgerConstants.ColumnNames.ORIGINATION_CODE + ", " + GeneralLedgerConstants.ColumnNames.DOCUMENT_NUMBER;
    protected static final String ENCUMBRANCE_AMOUNT_FIELDS = GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_AMOUNT + ", " + GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_CLOSED_AMOUNT;
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerBalancingDao#populateLedgerEntryHistory(java.lang.Integer)
     */
    public int populateLedgerEntryHistory(Integer universityFiscalYear) {
        String entryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Entry.class).getFullTableName();
        String entryHistoryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(EntryHistory.class).getFullTableName();
        
        String sql = "INSERT INTO " + entryHistoryTableName + " (" + ENTRY_KEY_FIELDS + ", " + VER_NBR + ", " + GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT + ", " + ROW_COUNT + ")"
        + " SELECT " + ENTRY_KEY_FIELDS + ", 1, sum(" +  GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT + "), count(*)"
        + " FROM " + entryTableName
        + " WHERE " + GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + " >= " + universityFiscalYear
        + " GROUP BY " + ENTRY_KEY_FIELDS;
        
        LOG.debug(sql);
        
        return getSimpleJdbcTemplate().update(sql);
    }
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerBalancingDao#populateLedgerBalanceHistory(java.lang.Integer)
     */
    public int populateLedgerBalanceHistory(Integer universityFiscalYear) {
        String balanceTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Balance.class).getFullTableName();
        String balanceHistoryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(BalanceHistory.class).getFullTableName();
        
        String sql = "INSERT INTO " + balanceHistoryTableName + " (" + BALANCE_KEY_FIELDS + ", " + BALANCE_AMOUNT_FIELDS + ", " + BALANCE_MONTH_AMOUNT_FIELDS + ")"
        + " SELECT " + BALANCE_KEY_FIELDS + ", " + BALANCE_AMOUNT_FIELDS + ", " + BALANCE_MONTH_AMOUNT_FIELDS
        + " FROM " + balanceTableName
        + " WHERE " + GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + " >= " + universityFiscalYear;
        
        LOG.debug(sql);
        
        return getSimpleJdbcTemplate().update(sql);
    }
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.BalancingDao#populateAccountBalancesHistory(java.lang.Integer)
     */
    public int populateAccountBalancesHistory(Integer universityFiscalYear) {
        String accountBalanceTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(AccountBalance.class).getFullTableName();
        String accountBalanceHistoryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(AccountBalanceHistory.class).getFullTableName();
        
        String sql = "INSERT INTO " + accountBalanceHistoryTableName + " (" + ACCOUNT_BALANCE_KEY_FIELDS + ", " + ACCOUNT_BALANCE_AMOUNT_FIELDS + ")"
        + " SELECT " + ACCOUNT_BALANCE_KEY_FIELDS + ", " + ACCOUNT_BALANCE_AMOUNT_FIELDS
        + " FROM " + accountBalanceTableName
        + " WHERE " + GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + " >= " + universityFiscalYear;
        
        LOG.debug(sql);
        
        return getSimpleJdbcTemplate().update(sql);
    }
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.BalancingDao#populateEncumbranceHistory(java.lang.Integer)
     */
    public int populateEncumbranceHistory(Integer universityFiscalYear) {
        String encumbranceTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Encumbrance.class).getFullTableName();
        String encumbranceHistoryTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(EncumbranceHistory.class).getFullTableName();
        
        String sql = "INSERT INTO " + encumbranceHistoryTableName + " (" + ENCUMBRANCE_KEY_FIELDS + ", " + ENCUMBRANCE_AMOUNT_FIELDS + ")"
        + " SELECT " + ENCUMBRANCE_KEY_FIELDS + ", " + ENCUMBRANCE_AMOUNT_FIELDS
        + " FROM " + encumbranceTableName
        + " WHERE " + GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR + " >= " + universityFiscalYear;
        
        LOG.debug(sql);
        
        return getSimpleJdbcTemplate().update(sql);
    }
}
