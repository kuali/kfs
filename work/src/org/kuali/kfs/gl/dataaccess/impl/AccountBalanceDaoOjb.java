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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.OptionsService;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.AccountBalanceDao;
import org.kuali.module.gl.service.AccountBalanceService;
import org.kuali.module.gl.util.BusinessObjectHandler;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * TODO Oracle Specific Code in this class
 * 
 */
public class AccountBalanceDaoOjb extends PersistenceBrokerDaoSupport implements AccountBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceDaoOjb.class);

    private DateTimeService dateTimeService;
    private OptionsService optionsService;

    public AccountBalanceDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#getByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public AccountBalance getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", t.getUniversityFiscalYear());
        crit.addEqualTo("chartOfAccountsCode", t.getChartOfAccountsCode());
        crit.addEqualTo("accountNumber", t.getAccountNumber());
        crit.addEqualTo("subAccountNumber", t.getSubAccountNumber());
        crit.addEqualTo("objectCode", t.getFinancialObjectCode());
        crit.addEqualTo("subObjectCode", t.getFinancialSubObjectCode());

        QueryByCriteria qbc = QueryFactory.newQuery(AccountBalance.class, crit);
        return (AccountBalance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#save(org.kuali.module.gl.bo.AccountBalance)
     */
    public void save(AccountBalance ab) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(ab);
    }

    /**
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAvailableAccountBalance(java.util.Map, boolean)
     */
    public Iterator findAvailableAccountBalance(Map fieldValues, boolean isConsolidated) {

        Criteria criteria = BusinessObjectHandler.buildCriteriaFromMap(fieldValues, new AccountBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountBalance.class, criteria);

        List attributeList = buildAttributeList(false);
        List groupByList = buildGroupList(false);

        // consolidate the selected entries
        if (isConsolidated) {
            attributeList.remove("subAccountNumber");
            groupByList.remove("subAccountNumber");

            // set the selection attributes
            String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
            query.setAttributes(attributes);

            // add the group criteria into the selection statement
            String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
            query.addGroupBy(groupBy);
        }

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByConsolidation(java.lang.Integer, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByConsolidation(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated,int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByConsolidation() started");

        Options options = optionsService.getOptions(universityFiscalYear);

        // Delete any data for this session if it exists already
        sqlCommand("DELETE fp_bal_by_cons_t WHERE person_sys_id = USERENV('SESSIONID')");

        // Add in all the source data
        sqlCommand("INSERT INTO fp_interim1_cons_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
                "ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_IND, FIN_OBJ_TYP_CD, SESID, OBJ_ID, VER_NBR) SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR, " + 
                "A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD, A.CURR_BDLN_BAL_AMT, A.ACLN_ACTLS_BAL_AMT, A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP, SUBSTR(fin_report_sort_cd, 1, 1), " + 
                "t.fin_obj_typ_cd, USERENV('SESSIONID'), sys_guid(), 1 FROM gl_acct_balances_t a, ca_object_code_t o, ca_obj_type_t t WHERE a.univ_fiscal_yr = " + 
                universityFiscalYear + " AND a.fin_coa_cd = '" + chartOfAccountsCode + "' AND a.account_nbr = '" + accountNumber + 
                "' AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd " + 
                " AND a.fin_object_cd = o.fin_object_cd AND o.fin_obj_typ_cd = t.fin_obj_typ_cd AND o.univ_fiscal_yr = " + universityFiscalYear + " AND o.fin_coa_cd = '" + 
                chartOfAccountsCode + "' " + " AND o.fin_obj_typ_cd IN " + allowedObjectTypes(options));

        // Summarize pending entries into fp_interim1_cons_mt if necessary
        if ( (pendingEntriesCode == AccountBalanceService.PENDING_ALL) || (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED) ) {
            if ( getMatchingPendingEntriesByConsolidation(options,universityFiscalYear, chartOfAccountsCode, accountNumber, isExcludeCostShare,pendingEntriesCode) ) {
                summarizeConsolidation(options);
            }
        }

        // Add some reference data
        sqlCommand("INSERT INTO fp_interim2_cons_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
                "ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_IND,FIN_OBJ_TYP_CD, SESID, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, OBJ_ID, VER_NBR) " + 
                "SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR,A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD, A.CURR_BDLN_BAL_AMT, A.ACLN_ACTLS_BAL_AMT, " + 
                "A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP, A.FIN_REPORT_SORT_IND, A.FIN_OBJ_TYP_CD, A.SESID,c.fin_report_sort_cd,c.fin_cons_obj_cd,sys_guid(), 1 " + 
                "FROM fp_interim1_cons_mt a,ca_object_code_t o,ca_obj_level_t l,ca_obj_consoldtn_t c WHERE a.univ_fiscal_yr = o.univ_fiscal_yr " + 
                "AND a.fin_coa_cd = o.fin_coa_cd AND a.fin_object_cd = o.fin_object_cd AND o.fin_coa_cd = l.fin_coa_cd AND o.fin_obj_level_cd = l.fin_obj_level_cd " + 
                "AND c.fin_coa_cd = l.fin_coa_cd AND c.fin_cons_obj_cd = l.fin_cons_obj_cd AND o.univ_fiscal_yr = " +
                universityFiscalYear + " AND o.fin_coa_cd = '" + chartOfAccountsCode + "' " + "AND l.fin_coa_cd = '" + chartOfAccountsCode + "' AND a.SESID = USERENV('SESSIONID')");

        // Get rid of stuff we don't need
        if (isExcludeCostShare) {
            sqlCommand("DELETE fp_interim2_cons_mt WHERE ROWID IN (SELECT i.ROWID FROM fp_interim2_cons_mt i,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = i.fin_coa_cd " + 
                    "AND a.account_nbr = i.account_nbr AND a.sub_acct_nbr = i.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND fp_interim2_cons_mt.SESID = USERENV('SESSIONID'))");
        }

        // Summarize
        if (isConsolidated) {
            sqlCommand("INSERT INTO fp_bal_by_cons_t (SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
                    "ACLN_ENCUM_BAL_AMT, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT '*ALL*',fin_report_sort_ind,cons_fin_report_sort_cd,fin_cons_obj_cd,SUM(curr_bdln_bal_amt), " + 
                    "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim2_cons_mt WHERE fp_interim2_cons_mt.SESID = USERENV('SESSIONID') " + 
                    "GROUP BY cons_fin_report_sort_cd, fin_report_sort_ind, fin_cons_obj_cd");
        }
        else {
            sqlCommand("INSERT INTO fp_bal_by_cons_t (SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
                    "ACLN_ENCUM_BAL_AMT, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT sub_acct_nbr, fin_report_sort_ind, cons_fin_report_sort_cd, fin_cons_obj_cd, SUM(curr_bdln_bal_amt), " + 
                    "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim2_cons_mt WHERE fp_interim2_cons_mt.SESID = USERENV('SESSIONID') " + 
                    "GROUP BY sub_acct_nbr, cons_fin_report_sort_cd, fin_report_sort_ind, fin_cons_obj_cd");
        }

        // Here's the data
        List data = sqlSelect("select SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT " + "from fp_bal_by_cons_t where PERSON_SYS_ID = USERENV('SESSIONID') order by fin_report_sort_cd,cons_fin_report_sort_cd");

        // Clean up everything
        sqlCommand("DELETE fp_interim1_cons_mt WHERE fp_interim1_cons_mt.SESID = USERENV('SESSIONID')");
        sqlCommand("DELETE fp_interim2_cons_mt WHERE fp_interim2_cons_mt.SESID = USERENV('SESSIONID')");
        sqlCommand("DELETE from fp_bal_by_cons_t where person_sys_id = USERENV('SESSIONID')");
        sqlCommand("delete from gl_pending_entry_mt where PERSON_UNVL_ID = USERENV('SESSIONID')");

        return data;
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByLevel(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated,int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByLevel() started");

        // Not sure what this is for
        String financialReportingSortCode = "A";

        Options options = optionsService.getOptions(universityFiscalYear);

        // Delete any data for this session if it exists already
        sqlCommand("DELETE fp_bal_by_level_t WHERE person_sys_id = USERENV('SESSIONID')");
        sqlCommand("DELETE  fp_interim1_level_mt WHERE fp_interim1_level_mt.SESID = USERENV('SESSIONID')");

        // Add in all the data we need
        sqlCommand("INSERT INTO fp_interim1_level_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, " + 
                "ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_CD, FIN_OBJ_LEVEL_CD, SESID, OBJ_ID, VER_NBR) SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, " + 
                "A.ACCOUNT_NBR, A.SUB_ACCT_NBR,A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD,A.CURR_BDLN_BAL_AMT, A.ACLN_ACTLS_BAL_AMT, A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP, " + 
                "fin_report_sort_cd, l.fin_obj_level_cd, USERENV('SESSIONID'), sys_guid(), 1 FROM gl_acct_balances_t a, ca_object_code_t o, ca_obj_level_t l " + 
                "WHERE a.univ_fiscal_yr = " + universityFiscalYear + " AND a.fin_coa_cd = '" + chartOfAccountsCode + "' AND a.account_nbr = '" + accountNumber + "' " + 
                "AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd AND a.fin_object_cd = o.fin_object_cd AND o.fin_coa_cd = l.fin_coa_cd " + 
                "AND o.fin_obj_level_cd = l.fin_obj_level_cd AND l.fin_cons_obj_cd = '" + financialConsolidationObjectCode + 
                "' AND o.univ_fiscal_yr = " + universityFiscalYear + " " + "AND o.fin_coa_cd = '" + chartOfAccountsCode + "'");

        // Summarize pending entries into fp_interim1_level_mt if necessary
        if ( (pendingEntriesCode == AccountBalanceService.PENDING_ALL) || (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED) ) {
            if ( getMatchingPendingEntriesByLevel(options,universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, isCostShareExcluded,pendingEntriesCode) ) {
                summarizeLevel(options);
            }
        }

        // Delete what we don't need
        if (isCostShareExcluded) {
            sqlCommand("DELETE fp_interim1_level_mt WHERE ROWID IN (SELECT i.ROWID FROM fp_interim1_level_mt i,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = i.fin_coa_cd " + "AND a.account_nbr = i.account_nbr AND a.sub_acct_nbr = i.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND fp_interim1_level_mt.SESID = USERENV('SESSIONID'))");
        }

        // Summarize
        if (isConsolidated) {
            sqlCommand("INSERT INTO fp_bal_by_level_t (SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, " + "TYP_FIN_REPORT_SORT_CD, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT '*ALL*', fin_obj_level_cd,fin_report_sort_cd, SUM(curr_bdln_bal_amt), " + "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt),'" + financialReportingSortCode + "', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_level_mt " + "WHERE fp_interim1_level_mt.SESID = USERENV('SESSIONID') GROUP BY fin_report_sort_cd, fin_obj_level_cd");
        }
        else {
            sqlCommand("INSERT INTO fp_bal_by_level_t (SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, " + "TYP_FIN_REPORT_SORT_CD, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT  sub_acct_nbr, fin_obj_level_cd, fin_report_sort_cd, SUM(curr_bdln_bal_amt), " + "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), '" + financialReportingSortCode + "', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_level_mt " + "WHERE fp_interim1_level_mt.SESID = USERENV('SESSIONID') GROUP BY sub_acct_nbr, fin_report_sort_cd, fin_obj_level_cd");
        }

        // Here's the data
        List data = sqlSelect("select SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TYP_FIN_REPORT_SORT_CD " + "from FP_BAL_BY_LEVEL_T where PERSON_SYS_ID = USERENV('SESSIONID')");

        // Clean up everything
        sqlCommand("DELETE fp_bal_by_level_t WHERE person_sys_id = USERENV('SESSIONID')");
        sqlCommand("DELETE  fp_interim1_level_mt WHERE fp_interim1_level_mt.SESID = USERENV('SESSIONID')");
        sqlCommand("delete from gl_pending_entry_mt where PERSON_UNVL_ID = USERENV('SESSIONID')");

        return data;
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByObject(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated,int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByObject() started");

        Options options = optionsService.getOptions(universityFiscalYear);

        // Delete any data for this session if it exists already
        sqlCommand("DELETE fp_bal_by_obj_t WHERE person_sys_id = USERENV('SESSIONID')");
        sqlCommand("DELETE  fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID = USERENV('SESSIONID')");

        // Add in all the data we need
        sqlCommand("INSERT INTO fp_interim1_obj_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR,FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT," + "ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP,SESID, OBJ_ID, VER_NBR) SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR," + "A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD, A.CURR_BDLN_BAL_AMT,A.ACLN_ACTLS_BAL_AMT, A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP,USERENV('SESSIONID'), sys_guid(), 1 " + "FROM gl_acct_balances_t a, ca_object_code_t o WHERE a.univ_fiscal_yr = " + universityFiscalYear + " AND a.fin_coa_cd = '" + chartOfAccountsCode + "' AND a.account_nbr = '" + accountNumber + "' AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd AND a.fin_object_cd = o.fin_object_cd " + "AND o.fin_obj_level_cd = '" + financialObjectLevelCode + "'");

        // Summarize pending entries into fp_interim1_level_mt if necessary
        if ( (pendingEntriesCode == AccountBalanceService.PENDING_ALL) || (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED) ) {
            if ( getMatchingPendingEntriesByObject(options,universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode, isCostShareExcluded,pendingEntriesCode) ) {
                summarizeObject(options);
            }
        }

        // Delete what we don't need
        if (isCostShareExcluded) {
            sqlCommand("DELETE fp_interim1_obj_mt WHERE ROWID IN (SELECT i.ROWID FROM fp_interim1_obj_mt i,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = i.fin_coa_cd " + "AND a.account_nbr = i.account_nbr AND a.sub_acct_nbr = i.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND fp_interim1_obj_mt.SESID = USERENV('SESSIONID'))");
        }

        // Summarize
        if (isConsolidated) {
            sqlCommand("INSERT INTO fp_bal_by_obj_t (SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD, " + "OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT  '*ALL*',fin_object_cd, SUM(curr_bdln_bal_amt),SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt)," + "'B', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID  = USERENV('SESSIONID') " + "GROUP BY fin_object_cd");
        }
        else {
            sqlCommand("INSERT INTO fp_bal_by_obj_t (SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD, " + "OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT  sub_acct_nbr, fin_object_cd, SUM(curr_bdln_bal_amt), SUM(acln_actls_bal_amt),SUM(acln_encum_bal_amt), " + "'B', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID  = USERENV('SESSIONID') " + "GROUP BY sub_acct_nbr, fin_object_cd");
        }

        // Here's the data
        List data = sqlSelect("select SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD from fp_bal_by_obj_t " + "where PERSON_SYS_ID = USERENV('SESSIONID') order by fin_object_cd");

        // Clean up everything
        sqlCommand("DELETE fp_bal_by_obj_t WHERE person_sys_id = USERENV('SESSIONID')");
        sqlCommand("DELETE  fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID = USERENV('SESSIONID')");
        sqlCommand("delete from gl_pending_entry_mt where PERSON_UNVL_ID = USERENV('SESSIONID')");

        return data;
    }

    private void summarizeObject(Options options) {
        LOG.debug("summarizeObject() started");

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();

            PreparedStatement getBalanceStatement = c.prepareStatement("SELECT CURR_BDLN_BAL_AMT,ACLN_ACTLS_BAL_AMT,ACLN_ENCUM_BAL_AMT FROM fp_interim1_obj_mt WHERE univ_fiscal_yr = ? AND " + 
                    "fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ?");
            PreparedStatement updateBalanceStatement = c.prepareStatement("UPDATE fp_interim1_obj_mt SET curr_bdln_bal_amt = ?,acln_actls_bal_amt = ?,acln_encum_bal_amt = ? WHERE " +
                    "univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ?");
            PreparedStatement insertBalanceStatement = c.prepareStatement("INSERT INTO fp_interim1_obj_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " +
                    "FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, SESID, OBJ_ID, VER_NBR) VALUES (?,?,?,?,?," +
                    "?,?,?,?,sysdate,USERENV('SESSIONID'),sys_guid(),1)");

            ResultSet rs = stmt.executeQuery("SELECT b.FIN_OFFST_GNRTN_CD,t.FIN_OBJTYP_DBCR_CD,e.* FROM gl_pending_entry_mt e,CA_OBJ_TYPE_T t,CA_BALANCE_TYPE_T b " +
                    "WHERE e.PERSON_UNVL_ID = USERENV('SESSIONID') AND e.FIN_OBJ_TYP_CD = t.FIN_OBJ_TYP_CD AND e.fin_balance_typ_cd = b.fin_balance_typ_cd " +
                    "ORDER BY e.univ_fiscal_yr,e.account_nbr,e.sub_acct_nbr,e.fin_object_cd,e.fin_sub_obj_cd,e.fin_obj_typ_cd");

            int updateCount = 0;
            int insertCount = 0;
            while (rs.next()) {
                getBalanceStatement.clearParameters();
                getBalanceStatement.setInt(1, rs.getInt("UNIV_FISCAL_YR"));
                getBalanceStatement.setString(2, rs.getString("FIN_COA_CD"));
                getBalanceStatement.setString(3, rs.getString("ACCOUNT_NBR"));
                getBalanceStatement.setString(4, rs.getString("SUB_ACCT_NBR"));
                getBalanceStatement.setString(5, rs.getString("FIN_OBJECT_CD"));
                getBalanceStatement.setString(6, rs.getString("FIN_SUB_OBJ_CD"));

                String balanceType = rs.getString("FIN_BALANCE_TYP_CD");
                String debitCreditCode = rs.getString("TRN_DEBIT_CRDT_CD");
                String objectTypeDebitCreditCode = rs.getString("FIN_OBJTYP_DBCR_CD");
                String offsetGenerationCode = rs.getString("FIN_OFFST_GNRTN_CD");

                ResultSet balance = getBalanceStatement.executeQuery();

                if ( balance.next() ) {
                    updateCount++;

                    BigDecimal budget = balance.getBigDecimal("CURR_BDLN_BAL_AMT");
                    BigDecimal actual = balance.getBigDecimal("ACLN_ACTLS_BAL_AMT");
                    BigDecimal encumb = balance.getBigDecimal("ACLN_ENCUM_BAL_AMT");

                    if ( balanceType.equals(options.getBudgetCheckingBalanceTypeCd()) ) {
                        budget = budget.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                    } else if ( balanceType.equals(options.getActualFinancialBalanceTypeCd()) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            actual = actual.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        } else {
                            actual = actual.subtract(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        }
                    } else if ( balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            encumb = encumb.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        } else {
                            encumb = encumb.subtract(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        }
                    }

                    // A balance exists, so we need to update it
                    updateBalanceStatement.clearParameters();
                    updateBalanceStatement.setBigDecimal(1, budget);
                    updateBalanceStatement.setBigDecimal(2, actual);
                    updateBalanceStatement.setBigDecimal(3, encumb);
                    updateBalanceStatement.setInt(4, rs.getInt("UNIV_FISCAL_YR"));
                    updateBalanceStatement.setString(5, rs.getString("FIN_COA_CD"));
                    updateBalanceStatement.setString(6, rs.getString("ACCOUNT_NBR"));
                    updateBalanceStatement.setString(7, rs.getString("SUB_ACCT_NBR"));
                    updateBalanceStatement.setString(8, rs.getString("FIN_OBJECT_CD"));
                    updateBalanceStatement.setString(9, rs.getString("FIN_SUB_OBJ_CD"));
                    updateBalanceStatement.execute();
                } else {
                    insertCount++;

                    BigDecimal budget = new BigDecimal("0");
                    BigDecimal actual = new BigDecimal("0");
                    BigDecimal encumb = new BigDecimal("0");

                    if ( balanceType.equals(options.getBudgetCheckingBalanceTypeCd()) ) {
                        budget = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                    } else if ( balanceType.equals(options.getActualFinancialBalanceTypeCd()) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            actual = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                        } else {
                            actual = rs.getBigDecimal("TRN_LDGR_ENTR_AMT").negate();
                        }
                    } else if ( balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            encumb = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                        } else {
                            encumb = rs.getBigDecimal("TRN_LDGR_ENTR_AMT").negate();
                        }
                    }

                    // No balance exists, so we need to insert one
                    insertBalanceStatement.clearParameters();
                    insertBalanceStatement.setInt(1, rs.getInt("UNIV_FISCAL_YR"));
                    insertBalanceStatement.setString(2, rs.getString("FIN_COA_CD"));
                    insertBalanceStatement.setString(3, rs.getString("ACCOUNT_NBR"));
                    insertBalanceStatement.setString(4, rs.getString("SUB_ACCT_NBR"));
                    insertBalanceStatement.setString(5, rs.getString("FIN_OBJECT_CD"));
                    insertBalanceStatement.setString(6, rs.getString("FIN_SUB_OBJ_CD"));
                    insertBalanceStatement.setBigDecimal(7, budget); // balance.getBigDecimal("CURR_BDLN_BAL_AMT"));
                    insertBalanceStatement.setBigDecimal(8, actual); // balance.getBigDecimal("ACLN_ACTLS_BAL_AMT"));
                    insertBalanceStatement.setBigDecimal(9, encumb); // balance.getBigDecimal("ACLN_ENCUM_BAL_AMT"));
                    insertBalanceStatement.execute();
                }
            }
            LOG.info("summarizeObject() INSERTS: " + insertCount);
            LOG.info("summarizeObject() UPDATES: " + updateCount);
        }
        catch (Exception e) {
            LOG.error("summarizeObject() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    private void summarizeLevel(Options options) {
        LOG.debug("summarizeLevel() started");

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();

            PreparedStatement getBalanceStatement = c.prepareStatement("SELECT CURR_BDLN_BAL_AMT,ACLN_ACTLS_BAL_AMT,ACLN_ENCUM_BAL_AMT FROM fp_interim1_level_mt WHERE univ_fiscal_yr = ? AND " + 
                    "fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ?");
            PreparedStatement updateBalanceStatement = c.prepareStatement("UPDATE fp_interim1_level_mt SET curr_bdln_bal_amt = ?,acln_actls_bal_amt = ?,acln_encum_bal_amt = ? WHERE " +
                    "univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ?");
            PreparedStatement insertBalanceStatement = c.prepareStatement("INSERT INTO fp_interim1_level_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " +
                    "FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_CD, FIN_OBJ_LEVEL_CD, SESID, OBJ_ID, VER_NBR) VALUES (?,?,?,?,?," +
                    "?,?,?,?,sysdate,?,?,USERENV('SESSIONID'),sys_guid(),1)");

            ResultSet rs = stmt.executeQuery("SELECT o.FIN_OBJ_LEVEL_CD,b.FIN_OFFST_GNRTN_CD,t.FIN_OBJTYP_DBCR_CD,l.fin_report_sort_cd,e.* " +
                    "FROM gl_pending_entry_mt e,CA_OBJ_TYPE_T t,CA_BALANCE_TYPE_T b,CA_OBJECT_CODE_T o,CA_OBJ_LEVEL_T l " +
                    "WHERE e.PERSON_UNVL_ID = USERENV('SESSIONID') AND e.FIN_OBJ_TYP_CD = t.FIN_OBJ_TYP_CD AND e.fin_balance_typ_cd = b.fin_balance_typ_cd AND e.univ_fiscal_yr = o.univ_fiscal_yr AND " +
                    "e.fin_coa_cd = o.fin_coa_cd AND e.fin_object_cd = o.fin_object_cd AND o.fin_coa_cd = l.fin_coa_cd AND o.fin_obj_level_cd = l.fin_obj_level_cd " +
                    "ORDER BY e.univ_fiscal_yr,e.account_nbr,e.sub_acct_nbr,e.fin_object_cd,e.fin_sub_obj_cd,e.fin_obj_typ_cd");

            int updateCount = 0;
            int insertCount = 0;
            while (rs.next()) {
                String sortCode = rs.getString("FIN_REPORT_SORT_CD");
                if ( sortCode.length() > 1 ) {
                    sortCode = sortCode.substring(0,1);
                }

                getBalanceStatement.clearParameters();
                getBalanceStatement.setInt(1, rs.getInt("UNIV_FISCAL_YR"));
                getBalanceStatement.setString(2, rs.getString("FIN_COA_CD"));
                getBalanceStatement.setString(3, rs.getString("ACCOUNT_NBR"));
                getBalanceStatement.setString(4, rs.getString("SUB_ACCT_NBR"));
                getBalanceStatement.setString(5, rs.getString("FIN_OBJECT_CD"));
                getBalanceStatement.setString(6, rs.getString("FIN_SUB_OBJ_CD"));

                String balanceType = rs.getString("FIN_BALANCE_TYP_CD");
                String objectType = rs.getString("FIN_OBJ_TYP_CD");
                String debitCreditCode = rs.getString("TRN_DEBIT_CRDT_CD");
                String objectTypeDebitCreditCode = rs.getString("FIN_OBJTYP_DBCR_CD");
                String offsetGenerationCode = rs.getString("FIN_OFFST_GNRTN_CD");

                ResultSet balance = getBalanceStatement.executeQuery();

                if ( balance.next() ) {
                    updateCount++;

                    BigDecimal budget = balance.getBigDecimal("CURR_BDLN_BAL_AMT");
                    BigDecimal actual = balance.getBigDecimal("ACLN_ACTLS_BAL_AMT");
                    BigDecimal encumb = balance.getBigDecimal("ACLN_ENCUM_BAL_AMT");

                    if ( balanceType.equals(options.getBudgetCheckingBalanceTypeCd()) ) {
                        budget = budget.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                    } else if ( balanceType.equals(options.getActualFinancialBalanceTypeCd()) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            actual = actual.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        } else {
                            actual = actual.subtract(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        }
                    } else if ( balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            encumb = encumb.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        } else {
                            encumb = encumb.subtract(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        }
                    }

                    // A balance exists, so we need to update it
                    updateBalanceStatement.clearParameters();
                    updateBalanceStatement.setBigDecimal(1, budget);
                    updateBalanceStatement.setBigDecimal(2, actual);
                    updateBalanceStatement.setBigDecimal(3, encumb);
                    updateBalanceStatement.setInt(4, rs.getInt("UNIV_FISCAL_YR"));
                    updateBalanceStatement.setString(5, rs.getString("FIN_COA_CD"));
                    updateBalanceStatement.setString(6, rs.getString("ACCOUNT_NBR"));
                    updateBalanceStatement.setString(7, rs.getString("SUB_ACCT_NBR"));
                    updateBalanceStatement.setString(8, rs.getString("FIN_OBJECT_CD"));
                    updateBalanceStatement.setString(9, rs.getString("FIN_SUB_OBJ_CD"));
                    updateBalanceStatement.execute();
                } else {
                    insertCount++;

                    BigDecimal budget = new BigDecimal("0");
                    BigDecimal actual = new BigDecimal("0");
                    BigDecimal encumb = new BigDecimal("0");

                    if ( balanceType.equals(options.getBudgetCheckingBalanceTypeCd()) ) {
                        budget = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                    } else if ( balanceType.equals(options.getActualFinancialBalanceTypeCd()) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            actual = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                        } else {
                            actual = rs.getBigDecimal("TRN_LDGR_ENTR_AMT").negate();
                        }
                    } else if ( balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            encumb = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                        } else {
                            encumb = rs.getBigDecimal("TRN_LDGR_ENTR_AMT").negate();
                        }
                    }

                    // No balance exists, so we need to insert one
                    insertBalanceStatement.clearParameters();
                    insertBalanceStatement.setInt(1, rs.getInt("UNIV_FISCAL_YR"));
                    insertBalanceStatement.setString(2, rs.getString("FIN_COA_CD"));
                    insertBalanceStatement.setString(3, rs.getString("ACCOUNT_NBR"));
                    insertBalanceStatement.setString(4, rs.getString("SUB_ACCT_NBR"));
                    insertBalanceStatement.setString(5, rs.getString("FIN_OBJECT_CD"));
                    insertBalanceStatement.setString(6, rs.getString("FIN_SUB_OBJ_CD"));
                    insertBalanceStatement.setBigDecimal(7, budget); // balance.getBigDecimal("CURR_BDLN_BAL_AMT"));
                    insertBalanceStatement.setBigDecimal(8, actual); // balance.getBigDecimal("ACLN_ACTLS_BAL_AMT"));
                    insertBalanceStatement.setBigDecimal(9, encumb); // balance.getBigDecimal("ACLN_ENCUM_BAL_AMT"));
                    insertBalanceStatement.setString(10, rs.getString("FIN_REPORT_SORT_CD"));
                    insertBalanceStatement.setString(11,rs.getString("FIN_OBJ_LEVEL_CD"));
                    insertBalanceStatement.execute();
                }
            }
            LOG.info("summarizeLevel() INSERTS: " + insertCount);
            LOG.info("summarizeLevel() UPDATES: " + updateCount);
        }
        catch (Exception e) {
            LOG.error("summarizeLevel() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    private void summarizeConsolidation(Options options) {
        LOG.debug("summarizeConsolidation() started");

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();

            PreparedStatement getBalanceStatement = c.prepareStatement("SELECT CURR_BDLN_BAL_AMT,ACLN_ACTLS_BAL_AMT,ACLN_ENCUM_BAL_AMT FROM fp_interim1_cons_mt WHERE univ_fiscal_yr = ? AND " + 
                    "fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ? AND fin_obj_typ_cd = ?");
            PreparedStatement updateBalanceStatement = c.prepareStatement("UPDATE fp_interim1_cons_mt SET curr_bdln_bal_amt = ?,acln_actls_bal_amt = ?,acln_encum_bal_amt = ? WHERE " +
                    "univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ? AND fin_obj_typ_cd = ?");
            PreparedStatement insertBalanceStatement = c.prepareStatement("INSERT INTO fp_interim1_cons_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " +
                    "FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_IND, FIN_OBJ_TYP_CD, SESID, OBJ_ID, VER_NBR) VALUES (?,?,?,?,?," +
                    "?,?,?,?,sysdate,?,?,USERENV('SESSIONID'),sys_guid(),1)");

            ResultSet rs = stmt.executeQuery("SELECT b.FIN_OFFST_GNRTN_CD,t.FIN_OBJTYP_DBCR_CD,t.fin_report_sort_cd,e.* FROM gl_pending_entry_mt e,CA_OBJ_TYPE_T t,CA_BALANCE_TYPE_T b " +
                    "WHERE e.PERSON_UNVL_ID = USERENV('SESSIONID') AND e.FIN_OBJ_TYP_CD = t.FIN_OBJ_TYP_CD AND e.fin_balance_typ_cd = b.fin_balance_typ_cd " +
                    "ORDER BY e.univ_fiscal_yr,e.account_nbr,e.sub_acct_nbr,e.fin_object_cd,e.fin_sub_obj_cd,e.fin_obj_typ_cd");

            int updateCount = 0;
            int insertCount = 0;
            while (rs.next()) {
                LOG.info("summarizeConsolidation() FOUND ONE");

                String sortCode = rs.getString("FIN_REPORT_SORT_CD");
                if ( sortCode.length() > 1 ) {
                    sortCode = sortCode.substring(0,1);
                }

                getBalanceStatement.clearParameters();
                getBalanceStatement.setInt(1, rs.getInt("UNIV_FISCAL_YR"));
                getBalanceStatement.setString(2, rs.getString("FIN_COA_CD"));
                getBalanceStatement.setString(3, rs.getString("ACCOUNT_NBR"));
                getBalanceStatement.setString(4, rs.getString("SUB_ACCT_NBR"));
                getBalanceStatement.setString(5, rs.getString("FIN_OBJECT_CD"));
                getBalanceStatement.setString(6, rs.getString("FIN_SUB_OBJ_CD"));
                getBalanceStatement.setString(7, rs.getString("FIN_OBJ_TYP_CD"));

                ResultSet balance = getBalanceStatement.executeQuery();

                String balanceType = rs.getString("FIN_BALANCE_TYP_CD");
                String objectType = rs.getString("FIN_OBJ_TYP_CD");
                String debitCreditCode = rs.getString("TRN_DEBIT_CRDT_CD");
                String objectTypeDebitCreditCode = rs.getString("FIN_OBJTYP_DBCR_CD");
                String offsetGenerationCode = rs.getString("FIN_OFFST_GNRTN_CD");

                if ( balance.next() ) {
                    updateCount++;

                    BigDecimal budget = balance.getBigDecimal("CURR_BDLN_BAL_AMT");
                    BigDecimal actual = balance.getBigDecimal("ACLN_ACTLS_BAL_AMT");
                    BigDecimal encumb = balance.getBigDecimal("ACLN_ENCUM_BAL_AMT");

                    if ( balanceType.equals(options.getBudgetCheckingBalanceTypeCd()) ) {
                        budget = budget.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                    } else if ( balanceType.equals(options.getActualFinancialBalanceTypeCd()) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            actual = actual.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        } else {
                            actual = actual.subtract(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        }
                    } else if ( balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            encumb = encumb.add(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        } else {
                            encumb = encumb.subtract(rs.getBigDecimal("TRN_LDGR_ENTR_AMT"));
                        }
                    }

                    // A balance exists, so we need to update it
                    updateBalanceStatement.clearParameters();
                    updateBalanceStatement.setBigDecimal(1, budget);
                    updateBalanceStatement.setBigDecimal(2, actual);
                    updateBalanceStatement.setBigDecimal(3, encumb);
                    updateBalanceStatement.setInt(4, rs.getInt("UNIV_FISCAL_YR"));
                    updateBalanceStatement.setString(5, rs.getString("FIN_COA_CD"));
                    updateBalanceStatement.setString(6, rs.getString("ACCOUNT_NBR"));
                    updateBalanceStatement.setString(7, rs.getString("SUB_ACCT_NBR"));
                    updateBalanceStatement.setString(8, rs.getString("FIN_OBJECT_CD"));
                    updateBalanceStatement.setString(9, rs.getString("FIN_SUB_OBJ_CD"));
                    updateBalanceStatement.setString(10, rs.getString("FIN_OBJ_TYP_CD"));
                    updateBalanceStatement.execute();
                } else {
                    insertCount++;

                    BigDecimal budget = new BigDecimal("0");
                    BigDecimal actual = new BigDecimal("0");
                    BigDecimal encumb = new BigDecimal("0");

                    if ( balanceType.equals(options.getBudgetCheckingBalanceTypeCd()) ) {
                        budget = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                    } else if ( balanceType.equals(options.getActualFinancialBalanceTypeCd()) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            actual = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                        } else {
                            actual = rs.getBigDecimal("TRN_LDGR_ENTR_AMT").negate();
                        }
                    } else if ( balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType) ) {
                        if ( debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && Constants.GL_BUDGET_CODE.equals(debitCreditCode))) ) {
                            encumb = rs.getBigDecimal("TRN_LDGR_ENTR_AMT");
                        } else {
                            encumb = rs.getBigDecimal("TRN_LDGR_ENTR_AMT").negate();
                        }
                    }

                    // No balance exists, so we need to insert one
                    insertBalanceStatement.clearParameters();
                    insertBalanceStatement.setInt(1, rs.getInt("UNIV_FISCAL_YR"));
                    insertBalanceStatement.setString(2, rs.getString("FIN_COA_CD"));
                    insertBalanceStatement.setString(3, rs.getString("ACCOUNT_NBR"));
                    insertBalanceStatement.setString(4, rs.getString("SUB_ACCT_NBR"));
                    insertBalanceStatement.setString(5, rs.getString("FIN_OBJECT_CD"));
                    insertBalanceStatement.setString(6, rs.getString("FIN_SUB_OBJ_CD"));
                    insertBalanceStatement.setBigDecimal(7, budget); // balance.getBigDecimal("CURR_BDLN_BAL_AMT"));
                    insertBalanceStatement.setBigDecimal(8, actual); // balance.getBigDecimal("ACLN_ACTLS_BAL_AMT"));
                    insertBalanceStatement.setBigDecimal(9, encumb); // balance.getBigDecimal("ACLN_ENCUM_BAL_AMT"));
                    insertBalanceStatement.setString(10, sortCode);
                    insertBalanceStatement.setString(11, rs.getString("FIN_OBJ_TYP_CD"));
                    insertBalanceStatement.execute();
                }
            }
            LOG.info("summarizeConsolidation() INSERTS: " + insertCount);
            LOG.info("summarizeConsolidation() UPDATES: " + updateCount);
        }
        catch (Exception e) {
            LOG.error("summarizeConsolidation() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    private boolean getMatchingPendingEntriesByConsolidation(Options options,Integer universityFiscalYear,String chartOfAccountsCode,String accountNumber,boolean isCostShareExcluded,int pendingEntriesCode) {
        LOG.debug("getMatchingPendingEntriesByConsolidation() started");

        // If they have specified this year, we will get all the pending entries where the year is equal or the year is null (because most eDocs don't fill in the year field).
        // If they have specified a previous year, we will get all the pending entries where the year is equal to their selection without the nulls (because we will post eDocs
        // with blank years tonight most probably.

        UniversityDate today = dateTimeService.getCurrentUniversityDate();

        sqlCommand("delete from gl_pending_entry_mt where PERSON_UNVL_ID = USERENV('SESSIONID')");

        String sql = "insert into GL_PENDING_ENTRY_MT (PERSON_UNVL_ID, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR,OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " + 
            "FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD,TRANSACTION_DT, " +
            "FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD,FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, " +
            "ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD,TRNENTR_PROCESS_TM, BDGT_YR) SELECT USERENV('SESSIONID'), p.FS_ORIGIN_CD, p.FDOC_NBR, p.TRN_ENTR_SEQ_NBR,sys_guid(), 1, " +
            "p.FIN_COA_CD, p.ACCOUNT_NBR, p.SUB_ACCT_NBR, p.FIN_OBJECT_CD, p.FIN_SUB_OBJ_CD, p.FIN_BALANCE_TYP_CD,p.FIN_OBJ_TYP_CD, p.UNIV_FISCAL_YR, p.UNIV_FISCAL_PRD_CD, " +
            "p.TRN_LDGR_ENTR_DESC, p.TRN_LDGR_ENTR_AMT, p.TRN_DEBIT_CRDT_CD, p.TRANSACTION_DT, p.FDOC_TYP_CD, p.ORG_DOC_NBR, PROJECT_CD, p.ORG_REFERENCE_ID, " +
            "p.FDOC_REF_TYP_CD, p.FS_REF_ORIGIN_CD,p.FDOC_REF_NBR, p.FDOC_REVERSAL_DT, p.TRN_ENCUM_UPDT_CD, p.FDOC_APPROVED_CD, p.ACCT_SF_FINOBJ_CD, " +
            "p.TRN_ENTR_OFST_CD,p.TRNENTR_PROCESS_TM, p.BDGT_YR FROM gl_pending_entry_t p ";

        if ( pendingEntriesCode == AccountBalanceService.PENDING_APPROVED ) {
            sql = sql + ", fp_doc_header_t d WHERE p.fdoc_nbr = d.fdoc_nbr AND d.fdoc_status_cd = 'A' AND ";
        } else {
            sql = sql + " WHERE ";
        }

        sql = sql + " p.FIN_COA_CD = '" + chartOfAccountsCode + "' AND p.account_nbr = '" + accountNumber + "' AND p.fin_obj_typ_cd IN " + allowedObjectTypes(options);

        if ( today.getUniversityFiscalYear().equals(universityFiscalYear) ) {
            sql = sql + "and (p.univ_fiscal_yr is null or p.univ_fiscal_yr = " + universityFiscalYear + ")";
        } else {
            sql = sql + "and p.univ_fiscal_yr = " + universityFiscalYear;
        }
        sqlCommand(sql);

        if ( isCostShareExcluded ) {
            deleteCostSharePendingEntries();
        }

        if ( countPendingEntries() == 0 ) {
            return false;
        }

        cleanUpPendingEntries(options.getUniversityFiscalYear());

        return true;
    }

// This is used for debugging.  It isn't needed in production but handy to have around.
//    private void printEm(String when,List data) {
//        for (Iterator iter = data.iterator(); iter.hasNext();) {
//            Map element = (Map) iter.next();
//            StringBuffer sb = new StringBuffer(when + ":");
//            for (Iterator iterator = element.keySet().iterator(); iterator.hasNext();) {
//                String key = (String) iterator.next();
//                sb.append(key);
//                sb.append("=");
//                sb.append(element.get(key));
//                sb.append(",");
//            }
//            LOG.info("printEm() " + sb);
//        }
//    }
    
    private boolean getMatchingPendingEntriesByLevel(Options options,Integer universityFiscalYear,String chartOfAccountsCode,String accountNumber, String financialConsolidationObjectCode,boolean isCostShareExcluded, int pendingEntriesCode) {
        LOG.debug("getMatchingPendingEntriesByLevel() started");

        // If they have specified this year, we will get all the pending entries where the year is equal or the year is null (because most eDocs don't fill in the year field).
        // If they have specified a previous year, we will get all the pending entries where the year is equal to their selection without the nulls (because we will post eDocs
        // with blank years tonight most probably.

        UniversityDate today = dateTimeService.getCurrentUniversityDate();

        sqlCommand("delete from gl_pending_entry_mt where PERSON_UNVL_ID = USERENV('SESSIONID')");

        String sql = "insert into GL_PENDING_ENTRY_MT (PERSON_UNVL_ID, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR,OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD," +  
                "TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD,FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD,TRNENTR_PROCESS_TM, BDGT_YR) " + 
                "SELECT USERENV('SESSIONID'), p.FS_ORIGIN_CD, p.FDOC_NBR, p.TRN_ENTR_SEQ_NBR,sys_guid(), 1, p.FIN_COA_CD, p.ACCOUNT_NBR, p.SUB_ACCT_NBR, p.FIN_OBJECT_CD, p.FIN_SUB_OBJ_CD, p.FIN_BALANCE_TYP_CD,p.FIN_OBJ_TYP_CD, p.UNIV_FISCAL_YR, p.UNIV_FISCAL_PRD_CD, p.TRN_LDGR_ENTR_DESC, p.TRN_LDGR_ENTR_AMT, p.TRN_DEBIT_CRDT_CD," + 
                "p.TRANSACTION_DT, p.FDOC_TYP_CD, p.ORG_DOC_NBR, PROJECT_CD, p.ORG_REFERENCE_ID, p.FDOC_REF_TYP_CD, p.FS_REF_ORIGIN_CD,p.FDOC_REF_NBR, p.FDOC_REVERSAL_DT, p.TRN_ENCUM_UPDT_CD, p.FDOC_APPROVED_CD, p.ACCT_SF_FINOBJ_CD, p.TRN_ENTR_OFST_CD,p.TRNENTR_PROCESS_TM, p.BDGT_YR from gl_pending_entry_t p,ca_object_code_t o,ca_obj_level_t l " + 
                "where o.FIN_COA_CD = p.FIN_COA_CD and o.FIN_OBJECT_CD = p.FIN_OBJECT_CD and o.UNIV_FISCAL_YR = " + universityFiscalYear + " and l.FIN_CONS_OBJ_CD = '" + financialConsolidationObjectCode + "' " +
                "and l.FIN_COA_CD = o.FIN_COA_CD and l.FIN_OBJ_LEVEL_CD = o.FIN_OBJ_LEVEL_CD and " +
                "p.FIN_COA_CD = '" + chartOfAccountsCode + "' and p.account_nbr = '" + accountNumber + "' ";

        if ( today.getUniversityFiscalYear().equals(universityFiscalYear) ) {
            sql = sql + "and (p.univ_fiscal_yr is null or p.univ_fiscal_yr = " + universityFiscalYear + ")";
        } else {
            sql = sql + "and p.univ_fiscal_yr = " + universityFiscalYear;
        }
        sqlCommand(sql);

        if ( isCostShareExcluded ) {
            deleteCostSharePendingEntries();
        }

        if ( countPendingEntries() == 0 ) {
            return false;
        }

        cleanUpPendingEntries(options.getUniversityFiscalYear());

        return true;
    }

    /**
     * Get any matching pending entries.  Return true if there were some, false if not.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialObjectLevelCode
     * @param pendingEntriesCode
     * @return
     */
    private boolean getMatchingPendingEntriesByObject(Options options,Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode,boolean isCostShareExcluded,int pendingEntriesCode) {
        LOG.debug("getMatchingPendingEntriesByObject() started");

        // If they have specified this year, we will get all the pending entries where the year is equal or the year is null (because most eDocs don't fill in the year field).
        // If they have specified a previous year, we will get all the pending entries where the year is equal to their selection without the nulls (because we will post eDocs
        // with blank years tonight most probably.

        UniversityDate today = dateTimeService.getCurrentUniversityDate();

        sqlCommand("delete from gl_pending_entry_mt where PERSON_UNVL_ID = USERENV('SESSIONID')");

        String sql = "insert into GL_PENDING_ENTRY_MT (PERSON_UNVL_ID, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR,OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD," +  
                "TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD,FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD,TRNENTR_PROCESS_TM, BDGT_YR) " + 
                "SELECT USERENV('SESSIONID'), p.FS_ORIGIN_CD, p.FDOC_NBR, p.TRN_ENTR_SEQ_NBR,sys_guid(), 1, p.FIN_COA_CD, p.ACCOUNT_NBR, p.SUB_ACCT_NBR, p.FIN_OBJECT_CD, p.FIN_SUB_OBJ_CD, p.FIN_BALANCE_TYP_CD,p.FIN_OBJ_TYP_CD, p.UNIV_FISCAL_YR, p.UNIV_FISCAL_PRD_CD, p.TRN_LDGR_ENTR_DESC, p.TRN_LDGR_ENTR_AMT, p.TRN_DEBIT_CRDT_CD," + 
                "p.TRANSACTION_DT, p.FDOC_TYP_CD, p.ORG_DOC_NBR, PROJECT_CD, p.ORG_REFERENCE_ID, p.FDOC_REF_TYP_CD, p.FS_REF_ORIGIN_CD,p.FDOC_REF_NBR, p.FDOC_REVERSAL_DT, p.TRN_ENCUM_UPDT_CD, p.FDOC_APPROVED_CD, p.ACCT_SF_FINOBJ_CD, p.TRN_ENTR_OFST_CD,p.TRNENTR_PROCESS_TM, p.BDGT_YR from gl_pending_entry_t p,ca_object_code_t o " + 
                "where o.FIN_COA_CD = p.FIN_COA_CD and o.FIN_OBJECT_CD = p.FIN_OBJECT_CD and o.FIN_OBJ_LEVEL_CD = '" + financialObjectLevelCode + "' and " +
                "p.FIN_COA_CD = '" + chartOfAccountsCode + "' and p.account_nbr = '" + accountNumber + "' and o.univ_fiscal_yr = " + universityFiscalYear;

        if ( today.getUniversityFiscalYear().equals(universityFiscalYear) ) {
            sql = sql + " and (p.univ_fiscal_yr is null or p.univ_fiscal_yr = " + universityFiscalYear + ")";
        } else {
            sql = sql + " and p.univ_fiscal_yr = " + universityFiscalYear;
        }
        sqlCommand(sql);

        if ( isCostShareExcluded ) {
            deleteCostSharePendingEntries();
        }

        if ( countPendingEntries() == 0 ) {
            return false;
        }

        cleanUpPendingEntries(options.getUniversityFiscalYear());

        return true;
    }

    /**
     * Get a list of allowed object types in a string for use as an IN clause in sql
     * 
     * @param option
     * @return
     */
    private String allowedObjectTypes(Options options) {
        return "('" + options.getFinObjTypeExpendNotExpCode() + "','" + options.getFinObjTypeExpenditureexpCd() + 
            "','" + options.getFinObjTypeExpNotExpendCode() + "','" + options.getFinObjectTypeIncomecashCode() + 
            "','" + options.getFinObjTypeIncomeNotCashCd() + "','" + options.getFinObjTypeCshNotIncomeCd() + "')";
    }

    private void deleteCostSharePendingEntries() {
        sqlCommand("DELETE gl_pending_entry_mt WHERE ROWID IN (SELECT g.ROWID FROM gl_pending_entry_mt g,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = g.fin_coa_cd " +
                "AND a.account_nbr = g.account_nbr AND a.sub_acct_nbr = g.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND g.person_unvl_id = USERENV('SESSIONID'))");        
    }

    private int countPendingEntries() {
        List results = sqlSelect("select count(*) as COUNT from gl_pending_entry_mt WHERE person_unvl_id = USERENV('SESSIONID')");
        Map row0 = (Map)results.get(0);
        BigDecimal count = (BigDecimal)row0.get("COUNT");
        return count.intValue();
    }

    private void cleanUpPendingEntries(Integer universityFiscalYear) {
        // Clean up the data
        sqlCommand("update GL_PENDING_ENTRY_MT set univ_fiscal_yr = " + universityFiscalYear + " where PERSON_UNVL_ID = USERENV('SESSIONID')");
        sqlCommand("update gl_pending_entry_mt set SUB_ACCT_NBR = '-----' where (SUB_ACCT_NBR is null or SUB_ACCT_NBR = '     ')");
        sqlCommand("update gl_pending_entry_mt set FIN_SUB_OBJ_CD = '---' where (FIN_SUB_OBJ_CD is null or FIN_SUB_OBJ_CD = '   ')");
        sqlCommand("update gl_pending_entry_mt set PROJECT_CD = '----------' where (PROJECT_CD is null or PROJECT_CD = '          ')");        
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @param isExtended determine whether the extended attributes will be used
     * @param type the type of selection. Its value may be BY_CONSOLIDATION, BY_LEVEL and BY_OBJECT
     * 
     * @return List an attribute list
     */
    private List buildAttributeList(boolean isExtended) {
        List attributeList = this.buildGroupList(isExtended);

        attributeList.add("sum(currentBudgetLineBalanceAmount)");
        attributeList.add("sum(accountLineActualsBalanceAmount)");
        attributeList.add("sum(accountLineEncumbranceBalanceAmount)");

        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     * 
     * @param isExtended determine whether the extended attributes will be used
     * @param type the type of selection. Its value may be BY_CONSOLIDATION, BY_LEVEL and BY_OBJECT
     * 
     * @return List an group by attribute list
     */
    private List buildGroupList(boolean isExtended) {
        List attributeList = new ArrayList();

        attributeList.add("universityFiscalYear");
        attributeList.add("chartOfAccountsCode");
        attributeList.add("accountNumber");
        attributeList.add("subAccountNumber");

        // use the extended option and type
        if (!isExtended) {
            attributeList.add("objectCode");
            attributeList.add("financialObject.financialObjectTypeCode");
        }
        return attributeList;
    }

    private int sqlCommand(String sql) {
        LOG.info("sqlCommand() started: " + sql);

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();
            return stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    private List sqlSelect(String sql) {
        LOG.debug("sqlSelect() started");

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            List result = new ArrayList();
            while (rs.next()) {
                Map row = new HashMap();
                int numColumns = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    row.put(rs.getMetaData().getColumnName(i).toUpperCase(), rs.getObject(i));
                }
                result.add(row);
            }
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    /**
     * Purge an entire fiscal year for a single chart.
     * 
     * @param chartOfAccountscode
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addLessThan("universityFiscalYear", new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(AccountBalance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setOptionsService(OptionsService os) {
        optionsService = os;
    }
}