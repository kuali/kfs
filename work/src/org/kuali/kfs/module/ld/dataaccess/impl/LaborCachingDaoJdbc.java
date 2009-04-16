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
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.service.LaborAccountingCycleCachingService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.Guid;
import org.kuali.rice.kns.util.KualiDecimal;

public class LaborCachingDaoJdbc extends CachingDaoJdbc implements LaborAccountingCycleCachingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCachingDaoJdbc.class);

    private PreparedStatement ledgerEntryInsert;
    private PreparedStatement laborObjectPreparedSelect;
    private PreparedStatement ledgerEntryPreparedSelect;
    private PreparedStatement ledgerBalancePreparedSelect;
    private PreparedStatement ledgerBalanceInsert;
    private PreparedStatement ledgerBalanceUpdate;

    private String previousLedgerBalanceKey = "";
    private LedgerBalance previousLedgerBalance = new LedgerBalance();
    
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

    
    
    
    public LedgerBalance getLedgerBalance(LedgerBalance ledgerBalance) {
        //NOTE: caches one value only!
        String key = "LD_LDGR_BAL_T:" + ledgerBalance.getUniversityFiscalYear().toString() + "/" + ledgerBalance.getChartOfAccountsCode() + "/" + ledgerBalance.getAccountNumber() + "/" + ledgerBalance.getSubAccountNumber() + "/" + ledgerBalance.getFinancialObjectCode() + "/" + ledgerBalance.getFinancialSubObjectCode() + "/" + ledgerBalance.getFinancialBalanceTypeCode() + "/" + ledgerBalance.getFinancialObjectTypeCode() + "/" + ledgerBalance.getPositionNumber() + "/" + ledgerBalance.getEmplid();
        if (!key.equals(previousLedgerBalanceKey)) {
            try {
                ledgerBalancePreparedSelect.setInt(1, ledgerBalance.getUniversityFiscalYear());
                ledgerBalancePreparedSelect.setString(2, ledgerBalance.getChartOfAccountsCode());
                ledgerBalancePreparedSelect.setString(3, ledgerBalance.getAccountNumber());
                ledgerBalancePreparedSelect.setString(4, ledgerBalance.getSubAccountNumber());
                ledgerBalancePreparedSelect.setString(5, ledgerBalance.getFinancialObjectCode());
                ledgerBalancePreparedSelect.setString(6, ledgerBalance.getFinancialSubObjectCode());
                ledgerBalancePreparedSelect.setString(7, ledgerBalance.getFinancialBalanceTypeCode());
                ledgerBalancePreparedSelect.setString(8, ledgerBalance.getFinancialObjectTypeCode());
                ledgerBalancePreparedSelect.setString(9, ledgerBalance.getPositionNumber());
                ledgerBalancePreparedSelect.setString(10, ledgerBalance.getEmplid());
                ResultSet rs = ledgerBalancePreparedSelect.executeQuery();
                if (rs.next()) {
                    previousLedgerBalance.setUniversityFiscalYear(ledgerBalance.getUniversityFiscalYear());
                    previousLedgerBalance.setChartOfAccountsCode(ledgerBalance.getChartOfAccountsCode());
                    previousLedgerBalance.setAccountNumber(ledgerBalance.getAccountNumber());
                    previousLedgerBalance.setSubAccountNumber(ledgerBalance.getSubAccountNumber());
                    previousLedgerBalance.setFinancialObjectCode(ledgerBalance.getFinancialObjectCode());
                    previousLedgerBalance.setFinancialSubObjectCode(ledgerBalance.getFinancialSubObjectCode());
                    previousLedgerBalance.setFinancialBalanceTypeCode(ledgerBalance.getFinancialBalanceTypeCode());
                    previousLedgerBalance.setFinancialObjectTypeCode(ledgerBalance.getFinancialObjectTypeCode());
                    previousLedgerBalance.setPositionNumber(ledgerBalance.getPositionNumber());
                    previousLedgerBalance.setEmplid(ledgerBalance.getEmplid());
                    previousLedgerBalance.setAccountLineAnnualBalanceAmount(new KualiDecimal(rs.getBigDecimal(1)));
                    previousLedgerBalance.setBeginningBalanceLineAmount(new KualiDecimal(rs.getBigDecimal(2)));
                    previousLedgerBalance.setContractsGrantsBeginningBalanceAmount(new KualiDecimal(rs.getBigDecimal(3)));
                    previousLedgerBalance.setMonth1Amount(new KualiDecimal(rs.getBigDecimal(4)));
                    previousLedgerBalance.setMonth2Amount(new KualiDecimal(rs.getBigDecimal(5)));
                    previousLedgerBalance.setMonth3Amount(new KualiDecimal(rs.getBigDecimal(6)));
                    previousLedgerBalance.setMonth4Amount(new KualiDecimal(rs.getBigDecimal(7)));
                    previousLedgerBalance.setMonth5Amount(new KualiDecimal(rs.getBigDecimal(8)));
                    previousLedgerBalance.setMonth6Amount(new KualiDecimal(rs.getBigDecimal(9)));
                    previousLedgerBalance.setMonth7Amount(new KualiDecimal(rs.getBigDecimal(10)));
                    previousLedgerBalance.setMonth8Amount(new KualiDecimal(rs.getBigDecimal(11)));
                    previousLedgerBalance.setMonth9Amount(new KualiDecimal(rs.getBigDecimal(12)));
                    previousLedgerBalance.setMonth10Amount(new KualiDecimal(rs.getBigDecimal(13)));
                    previousLedgerBalance.setMonth11Amount(new KualiDecimal(rs.getBigDecimal(14)));
                    previousLedgerBalance.setMonth12Amount(new KualiDecimal(rs.getBigDecimal(15)));
                    previousLedgerBalance.setMonth13Amount(new KualiDecimal(rs.getBigDecimal(16)));
                    previousLedgerBalanceKey = key;
               } else { LOG.debug("Ledger Balance not found: " + key); return null; }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
    //          TODO: should do something else here I'm sure
                throw new RuntimeException(e);
            }
        }
        return previousLedgerBalance;
    }
    
    public void insertLedgerBalance(LedgerBalance ledgerBalance) {
        try {
            ledgerBalanceInsert.setInt(1, ledgerBalance.getUniversityFiscalYear());
            ledgerBalanceInsert.setString(2, ledgerBalance.getChartOfAccountsCode());
            ledgerBalanceInsert.setString(3, ledgerBalance.getAccountNumber());
            ledgerBalanceInsert.setString(4, ledgerBalance.getSubAccountNumber());
            ledgerBalanceInsert.setString(5, ledgerBalance.getFinancialObjectCode());
            ledgerBalanceInsert.setString(6, ledgerBalance.getFinancialSubObjectCode());
            ledgerBalanceInsert.setString(7, ledgerBalance.getFinancialBalanceTypeCode());
            ledgerBalanceInsert.setString(8, ledgerBalance.getFinancialObjectTypeCode());
            ledgerBalanceInsert.setString(9, ledgerBalance.getPositionNumber());
            ledgerBalanceInsert.setString(10, ledgerBalance.getEmplid());
            if (ledgerBalance.getObjectId() == null) {
                ledgerBalanceInsert.setString(11, new Guid().toString());
            }
            else
            {
                ledgerBalanceInsert.setString(11, ledgerBalance.getObjectId());
            }
            if (ledgerBalance.getVersionNumber() == null) {
                ledgerBalanceInsert.setLong(12, 1);
            }
            else
            {
                ledgerBalanceInsert.setLong(12, ledgerBalance.getVersionNumber()); 
            }
            ledgerBalanceInsert.setBigDecimal(13, ledgerBalance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(14, ledgerBalance.getBeginningBalanceLineAmount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(15, ledgerBalance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(16, ledgerBalance.getMonth1Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(17, ledgerBalance.getMonth2Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(18, ledgerBalance.getMonth3Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(19, ledgerBalance.getMonth4Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(20, ledgerBalance.getMonth5Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(21, ledgerBalance.getMonth6Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(22, ledgerBalance.getMonth7Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(23, ledgerBalance.getMonth8Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(24, ledgerBalance.getMonth9Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(25, ledgerBalance.getMonth10Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(26, ledgerBalance.getMonth11Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(27, ledgerBalance.getMonth12Amount().bigDecimalValue());
            ledgerBalanceInsert.setBigDecimal(28, ledgerBalance.getMonth13Amount().bigDecimalValue());
            ledgerBalanceInsert.setTimestamp(29, dateTimeService.getCurrentTimestamp());
            
            ledgerBalanceInsert.executeUpdate();
            previousLedgerBalanceKey = "LD_LDGR_BAL_T:" + ledgerBalance.getUniversityFiscalYear().toString() + "/" + ledgerBalance.getChartOfAccountsCode() + "/" + ledgerBalance.getAccountNumber() + "/" + ledgerBalance.getSubAccountNumber() + "/" + ledgerBalance.getFinancialObjectCode() + "/" + ledgerBalance.getFinancialSubObjectCode() + "/" + ledgerBalance.getFinancialBalanceTypeCode() + "/" + ledgerBalance.getFinancialObjectTypeCode() + "/" + ledgerBalance.getPositionNumber() + "/" + ledgerBalance.getEmplid();
            previousLedgerBalance = ledgerBalance;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }
    
    public void updateLedgerBalance(LedgerBalance ledgerBalance) {
        try {
            ledgerBalanceUpdate.setBigDecimal(1, ledgerBalance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(2, ledgerBalance.getBeginningBalanceLineAmount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(3, ledgerBalance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(4, ledgerBalance.getMonth1Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(5, ledgerBalance.getMonth2Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(6, ledgerBalance.getMonth3Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(7, ledgerBalance.getMonth4Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(8, ledgerBalance.getMonth5Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(9, ledgerBalance.getMonth6Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(10, ledgerBalance.getMonth7Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(11, ledgerBalance.getMonth8Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(12, ledgerBalance.getMonth9Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(13, ledgerBalance.getMonth10Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(14, ledgerBalance.getMonth11Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(15, ledgerBalance.getMonth12Amount().bigDecimalValue());
            ledgerBalanceUpdate.setBigDecimal(16, ledgerBalance.getMonth13Amount().bigDecimalValue());
            ledgerBalanceUpdate.setTimestamp(17, dateTimeService.getCurrentTimestamp());
            ledgerBalanceUpdate.setInt(18, ledgerBalance.getUniversityFiscalYear());
            ledgerBalanceUpdate.setString(19, ledgerBalance.getChartOfAccountsCode());
            ledgerBalanceUpdate.setString(20, ledgerBalance.getAccountNumber());
            ledgerBalanceUpdate.setString(21, ledgerBalance.getSubAccountNumber());
            ledgerBalanceUpdate.setString(22, ledgerBalance.getFinancialObjectCode());
            ledgerBalanceUpdate.setString(23, ledgerBalance.getFinancialSubObjectCode());
            ledgerBalanceUpdate.setString(24, ledgerBalance.getFinancialBalanceTypeCode());
            ledgerBalanceUpdate.setString(25, ledgerBalance.getFinancialObjectTypeCode());
            ledgerBalanceUpdate.setString(26, ledgerBalance.getPositionNumber());
            ledgerBalanceUpdate.setString(27, ledgerBalance.getEmplid());
 
            ledgerBalanceUpdate.executeUpdate();
            previousLedgerBalance = ledgerBalance;  //should we also update the key for safety?  it should be the same though
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }

    
    
    
    
    
    
    
    
    

    public void init() {
        if (connection == null) {
            try {
                super.init();
                
                laborObjectPreparedSelect = connection.prepareStatement("select finobj_frngslry_cd from LD_LABOR_OBJ_T where univ_fiscal_yr = ? and fin_coa_cd = ? and fin_object_cd = ?");
                ledgerEntryInsert = connection.prepareStatement("INSERT INTO LD_LDGR_ENTR_T VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ledgerEntryPreparedSelect = connection.prepareStatement("select max(trn_entr_seq_nbr) from ld_ldgr_entr_t where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fin_obj_typ_cd = ? and univ_fiscal_prd_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
                
                ledgerBalancePreparedSelect = connection.prepareStatement("select ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT from LD_LDGR_BAL_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and POSITION_NBR = ? and EMPLID = ?");
                ledgerBalanceInsert = connection.prepareStatement("insert into LD_LDGR_BAL_T values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ledgerBalanceUpdate = connection.prepareStatement("update LD_LDGR_BAL_T set ACLN_ANNL_BAL_AMT = ?, FIN_BEG_BAL_LN_AMT = ?, CONTR_GR_BB_AC_AMT = ?, MO1_ACCT_LN_AMT = ?, MO2_ACCT_LN_AMT = ?, MO3_ACCT_LN_AMT = ?, MO4_ACCT_LN_AMT = ?, MO5_ACCT_LN_AMT = ?, MO6_ACCT_LN_AMT = ?, MO7_ACCT_LN_AMT = ?, MO8_ACCT_LN_AMT = ?, MO9_ACCT_LN_AMT = ?, MO10_ACCT_LN_AMT = ?, MO11_ACCT_LN_AMT = ?, MO12_ACCT_LN_AMT = ?, MO13_ACCT_LN_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and POSITION_NBR = ? and EMPLID = ?");
                
                
                
            }
            catch (SQLException e) {
                LOG.info(e.getErrorCode() + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
