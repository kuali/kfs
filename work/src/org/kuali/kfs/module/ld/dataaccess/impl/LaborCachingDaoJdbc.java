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
package org.kuali.kfs.module.ld.dataaccess.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.dataaccess.impl.CachingDaoJdbc;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborCachingDao;
import org.kuali.rice.kns.util.Guid;

public class LaborCachingDaoJdbc extends CachingDaoJdbc implements LaborCachingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCachingDaoJdbc.class);

    private HashMap<String, Object> dataCache = new HashMap<String, Object>();

    private PreparedStatement ledgerEntryInsert;
    private PreparedStatement laborObjectPreparedSelect;
    private PreparedStatement ledgerEntryPreparedSelect;

    private Connection connection;

    public void insertLedgerEntry(LedgerEntry ledgerEntry) {
        try {
            ledgerEntryInsert.setInt(1, ledgerEntry.getUniversityFiscalYear());
            ledgerEntryInsert.setString(2, ledgerEntry.getChartOfAccountsCode());
            ledgerEntryInsert.setString(3, ledgerEntry.getAccountNumber());
            ledgerEntryInsert.setString(4, ledgerEntry.getSubAccountNumber());
            ledgerEntryInsert.setString(5, ledgerEntry.getFinancialObjectCode());
            ledgerEntryInsert.setString(6, ledgerEntry.getFinancialSubObjectCode());
            ledgerEntryInsert.setString(7, ledgerEntry.getFinancialBalanceTypeCode());
            ledgerEntryInsert.setString(8, ledgerEntry.getFinancialObjectTypeCode());
            ledgerEntryInsert.setString(9, ledgerEntry.getUniversityFiscalPeriodCode());
            ledgerEntryInsert.setString(10, ledgerEntry.getFinancialDocumentTypeCode());
            ledgerEntryInsert.setString(11, ledgerEntry.getFinancialSystemOriginationCode());
            ledgerEntryInsert.setString(12, ledgerEntry.getDocumentNumber());
            ledgerEntryInsert.setInt(13, ledgerEntry.getTransactionLedgerEntrySequenceNumber());
            if (ledgerEntry.getObjectId() == null) {
                ledgerEntryInsert.setString(14, new Guid().toString());
            }
            else
            {
                ledgerEntryInsert.setString(14, ledgerEntry.getObjectId());
            }
            if (ledgerEntry.getVersionNumber() == null) {
                ledgerEntryInsert.setLong(15, 1);
            }
            else
            {
                ledgerEntryInsert.setLong(15, ledgerEntry.getVersionNumber()); 
            }
            ledgerEntryInsert.setString(16, ledgerEntry.getPositionNumber());
            ledgerEntryInsert.setString(17, ledgerEntry.getProjectCode());
            ledgerEntryInsert.setString(18, ledgerEntry.getTransactionLedgerEntryDescription());
            ledgerEntryInsert.setBigDecimal(19, ledgerEntry.getTransactionLedgerEntryAmount().bigDecimalValue());
            ledgerEntryInsert.setString(20, ledgerEntry.getTransactionDebitCreditCode());
            ledgerEntryInsert.setDate(21, ledgerEntry.getTransactionDate());
            ledgerEntryInsert.setString(22, ledgerEntry.getOrganizationDocumentNumber());
            ledgerEntryInsert.setString(23, ledgerEntry.getOrganizationReferenceId());
            ledgerEntryInsert.setString(24, ledgerEntry.getReferenceFinancialDocumentTypeCode());
            ledgerEntryInsert.setString(25, ledgerEntry.getReferenceFinancialSystemOriginationCode());
            ledgerEntryInsert.setString(26, ledgerEntry.getReferenceFinancialDocumentNumber());
            ledgerEntryInsert.setDate(27, ledgerEntry.getFinancialDocumentReversalDate());
            ledgerEntryInsert.setString(28, ledgerEntry.getTransactionEncumbranceUpdateCode());
            ledgerEntryInsert.setDate(29, ledgerEntry.getTransactionPostingDate());
            ledgerEntryInsert.setDate(30, ledgerEntry.getPayPeriodEndDate());
            ledgerEntryInsert.setBigDecimal(31, ledgerEntry.getTransactionTotalHours());
            if (ledgerEntry.getPayrollEndDateFiscalYear() == null) {
                ledgerEntryInsert.setNull(32, java.sql.Types.INTEGER);
            }
            else {
                ledgerEntryInsert.setInt(32, ledgerEntry.getPayrollEndDateFiscalYear());
            }
            ledgerEntryInsert.setString(33, ledgerEntry.getPayrollEndDateFiscalPeriodCode());
            ledgerEntryInsert.setString(34, ledgerEntry.getEmplid());
            if (ledgerEntry.getEmployeeRecord() == null) {
                ledgerEntryInsert.setNull(35, java.sql.Types.INTEGER);
            }
            else {
                ledgerEntryInsert.setInt(35, ledgerEntry.getEmployeeRecord());
            }
            ledgerEntryInsert.setString(36, ledgerEntry.getEarnCode());
            ledgerEntryInsert.setString(37, ledgerEntry.getPayGroup());
            ledgerEntryInsert.setString(38, ledgerEntry.getSalaryAdministrationPlan());
            ledgerEntryInsert.setString(39, ledgerEntry.getGrade());
            ledgerEntryInsert.setString(40, ledgerEntry.getRunIdentifier());
            ledgerEntryInsert.setString(41, ledgerEntry.getLaborLedgerOriginalChartOfAccountsCode());
            ledgerEntryInsert.setString(42, ledgerEntry.getLaborLedgerOriginalAccountNumber());
            ledgerEntryInsert.setString(43, ledgerEntry.getLaborLedgerOriginalSubAccountNumber());
            ledgerEntryInsert.setString(44, ledgerEntry.getLaborLedgerOriginalFinancialObjectCode());
            ledgerEntryInsert.setString(45, ledgerEntry.getLaborLedgerOriginalFinancialSubObjectCode());
            ledgerEntryInsert.setString(46, ledgerEntry.getHrmsCompany());
            ledgerEntryInsert.setString(47, ledgerEntry.getSetid());
            ledgerEntryInsert.setTimestamp(48, ledgerEntry.getTransactionDateTimeStamp());

            ledgerEntryInsert.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LaborObject getLaborObject(OriginEntry originEntry) {
        LaborObject laborObject = null;
        String key = "LD_LABOR_OBJ_T:" + originEntry.getUniversityFiscalYear().toString() + "/" + originEntry.getChartOfAccountsCode() + "/" + originEntry.getFinancialObjectCode();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                laborObject = (LaborObject) value;
            }
        }
        else {
            try {
                laborObjectPreparedSelect.setInt(1, originEntry.getUniversityFiscalYear());
                laborObjectPreparedSelect.setString(2, originEntry.getChartOfAccountsCode());
                laborObjectPreparedSelect.setString(3, originEntry.getFinancialObjectCode());
                ResultSet rs = laborObjectPreparedSelect.executeQuery();
                if (rs.next()) {
                    laborObject = new LaborObject();
                    laborObject.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
                    laborObject.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
                    laborObject.setFinancialObjectCode(originEntry.getFinancialObjectCode());
                    dataCache.put(key, laborObject);
                }
                else {
                    LOG.debug("LaborObject not found: " + key);
                    dataCache.put(key, " ");
                }
                if (rs.next()) {
                    throw new RuntimeException("More than one row returned from select by primary key.");
                }
                rs.close();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return laborObject;
    }


    public int getMaxLaborSequenceNumber(LedgerEntry t) {
        // TODO: This method actually does NOT cache, so probably should be in a different DaoJdbc, or at least called from
        // LaborLedgerEntryDaoJdbc
        // TODO: if sequence number is never incremented anywhere else, could change this to cache after all, and just add 1 if it's
        // already in the cache, which would speed things up
        int transactionLedgerEntrySequenceNumber = 0;
        try {
            ledgerEntryPreparedSelect.setInt(1, t.getUniversityFiscalYear());
            ledgerEntryPreparedSelect.setString(2, t.getChartOfAccountsCode());
            ledgerEntryPreparedSelect.setString(3, t.getAccountNumber());
            ledgerEntryPreparedSelect.setString(4, t.getSubAccountNumber());
            ledgerEntryPreparedSelect.setString(5, t.getFinancialObjectCode());
            ledgerEntryPreparedSelect.setString(6, t.getFinancialSubObjectCode());
            ledgerEntryPreparedSelect.setString(7, t.getFinancialBalanceTypeCode());
            ledgerEntryPreparedSelect.setString(8, t.getFinancialObjectTypeCode());
            ledgerEntryPreparedSelect.setString(9, t.getUniversityFiscalPeriodCode());
            ledgerEntryPreparedSelect.setString(10, t.getFinancialDocumentTypeCode());
            ledgerEntryPreparedSelect.setString(11, t.getFinancialSystemOriginationCode());
            ledgerEntryPreparedSelect.setString(12, t.getDocumentNumber());
            ResultSet rs = ledgerEntryPreparedSelect.executeQuery();
            if (rs.next()) { // TODO: should there be a check for multiple records being returned?
                transactionLedgerEntrySequenceNumber = rs.getInt(1);
            }
            if (rs.next()) {
                throw new RuntimeException("More than one row returned from select by primary key.");
            }
            rs.close();
        }
        catch (SQLException e) {
            // TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
        return transactionLedgerEntrySequenceNumber;
    }


    public void init() {
        if (connection == null) {
            try {
                connection = getDataSource().getConnection();
                laborObjectPreparedSelect = connection.prepareStatement("select finobj_frngslry_cd from LD_LABOR_OBJ_T where univ_fiscal_yr = ? and fin_coa_cd = ? and fin_object_cd = ?");
                ledgerEntryInsert = connection.prepareStatement("INSERT INTO LD_LDGR_ENTR_T VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ledgerEntryPreparedSelect = connection.prepareStatement("select max(trn_entr_seq_nbr) from ld_ldgr_entr_t where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fin_obj_typ_cd = ? and univ_fiscal_prd_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
            }
            catch (SQLException e) {
                LOG.info(e.getErrorCode() + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }
}
