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

import java.sql.Connection;
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
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.AccountBalanceDao;
import org.kuali.module.gl.util.BusinessObjectHandler;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 *  
 */
public class AccountBalanceDaoOjb extends PersistenceBrokerDaoSupport implements AccountBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceDaoOjb.class);
    
    private final String BY_CONSOLIDATION = "consolidation";
    private final String BY_LEVEL = "level";
    private final String BY_OBJECT = "object";

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

        List attributeList = buildAttributeList(false, "");
        List groupByList = buildGroupList(false, "");

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
     * This is based on the code in the FIS.  Unfortunately, there is business logic in here.  This should be cleaned up at some point.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param isExcludeCostShare
     * @param isConsolidated
     * @return
     */
    public List findAccountBalanceByConsolidation(Integer universityFiscalYear,String chartOfAccountsCode,String accountNumber,
        boolean isExcludeCostShare,boolean isConsolidated) {
      LOG.debug("findAccountBalanceByConsolidation() started");

      // Delete any data for this session if it exists already
      sqlCommand("DELETE fp_bal_by_cons_t WHERE person_sys_id = USERENV('SESSIONID')");

      // Add in all the source data
      sqlCommand("INSERT INTO fp_interim1_cons_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
        "ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_IND, FIN_OBJ_TYP_CD, SESID, OBJ_ID, VER_NBR) SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR, " + 
        "A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD, A.CURR_BDLN_BAL_AMT, A.ACLN_ACTLS_BAL_AMT, A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP, SUBSTR(fin_report_sort_cd, 1, 1), " + 
        "t.fin_obj_typ_cd, USERENV('SESSIONID'), sys_guid(), 1 FROM gl_acct_balances_t a, ca_object_code_t o, ca_obj_type_t t WHERE a.univ_fiscal_yr = " + universityFiscalYear +  
        " AND a.fin_coa_cd = '" + chartOfAccountsCode + "' AND a.account_nbr = '" + accountNumber + "' AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd " + 
        " AND a.fin_object_cd = o.fin_object_cd AND o.fin_obj_typ_cd = t.fin_obj_typ_cd AND o.univ_fiscal_yr = " + universityFiscalYear + " AND o.fin_coa_cd = '" + chartOfAccountsCode + "' " +
        " AND o.fin_obj_typ_cd IN ('EE', 'EX', 'ES', 'IN', 'IC', 'CH')");

      sqlCommand("INSERT INTO fp_interim2_cons_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
        "ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_IND,FIN_OBJ_TYP_CD, SESID, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, OBJ_ID, VER_NBR) " +
        "SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR,A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD, A.CURR_BDLN_BAL_AMT, A.ACLN_ACTLS_BAL_AMT, " + 
        "A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP, A.FIN_REPORT_SORT_IND, A.FIN_OBJ_TYP_CD, A.SESID,c.fin_report_sort_cd,c.fin_cons_obj_cd,sys_guid(), 1 " +
        "FROM fp_interim1_cons_mt a,ca_object_code_t o,ca_obj_level_t l,ca_obj_consoldtn_t c WHERE a.univ_fiscal_yr = o.univ_fiscal_yr " +
        "AND a.fin_coa_cd = o.fin_coa_cd AND a.fin_object_cd = o.fin_object_cd AND o.fin_coa_cd = l.fin_coa_cd AND o.fin_obj_level_cd = l.fin_obj_level_cd " + 
        "AND c.fin_coa_cd = l.fin_coa_cd AND c.fin_cons_obj_cd = l.fin_cons_obj_cd AND o.univ_fiscal_yr = " + universityFiscalYear + " AND o.fin_coa_cd = '" + chartOfAccountsCode + "' " + 
        "AND l.fin_coa_cd = '" + chartOfAccountsCode + "' AND a.SESID = USERENV('SESSIONID')");

      // Get rid of stuff we don't need
      if ( isExcludeCostShare ) {
        sqlCommand("DELETE fp_interim2_cons_mt WHERE ROWID IN (SELECT i.ROWID FROM fp_interim2_cons_mt i,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = i.fin_coa_cd " + 
          "AND a.account_nbr = i.account_nbr AND a.sub_acct_nbr = i.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND fp_interim2_cons_mt.SESID = USERENV('SESSIONID'))");
      }

      // Summarize
      if ( isConsolidated ) {
        sqlCommand("INSERT INTO fp_bal_by_cons_t (SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
          "ACLN_ENCUM_BAL_AMT, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT '-----',fin_report_sort_ind,cons_fin_report_sort_cd,fin_cons_obj_cd,SUM(curr_bdln_bal_amt), " + 
          "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim2_cons_mt WHERE fp_interim2_cons_mt.SESID = USERENV('SESSIONID') " + 
          "GROUP BY cons_fin_report_sort_cd, fin_report_sort_ind, fin_cons_obj_cd");
      } else {
        sqlCommand("INSERT INTO fp_bal_by_cons_t (SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + 
          "ACLN_ENCUM_BAL_AMT, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT sub_acct_nbr, fin_report_sort_ind, cons_fin_report_sort_cd, fin_cons_obj_cd, SUM(curr_bdln_bal_amt), " + 
          "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim2_cons_mt WHERE fp_interim2_cons_mt.SESID = USERENV('SESSIONID') " + 
          "GROUP BY sub_acct_nbr, cons_fin_report_sort_cd, fin_report_sort_ind, fin_cons_obj_cd");
      }

      // Here's the data
      List data = sqlSelect("select SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT " +
         "from fp_bal_by_cons_t where PERSON_SYS_ID = USERENV('SESSIONID') order by fin_report_sort_cd,cons_fin_report_sort_cd");

      // Clean up everything
      sqlCommand("DELETE fp_interim1_cons_mt WHERE fp_interim1_cons_mt.SESID = USERENV('SESSIONID')");
      sqlCommand("DELETE fp_interim2_cons_mt WHERE fp_interim2_cons_mt.SESID = USERENV('SESSIONID')");
      sqlCommand("DELETE from fp_bal_by_cons_t where person_sys_id = USERENV('SESSIONID')");

      return data;
    }

    /**
     * Find balances by level
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear,String chartOfAccountsCode,String accountNumber,
        String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated) {

      // Not sure what this is for
      String financialReportingSortCode = "A";

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
          "AND o.fin_obj_level_cd = l.fin_obj_level_cd AND l.fin_cons_obj_cd = '" + financialConsolidationObjectCode + "' AND o.univ_fiscal_yr = " + universityFiscalYear + " " + 
          "AND o.fin_coa_cd = '" + chartOfAccountsCode + "'");

      // Delete what we don't need
      if ( isCostShareExcluded ) {
        sqlCommand("DELETE fp_interim1_level_mt WHERE ROWID IN (SELECT i.ROWID FROM fp_interim1_level_mt i,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = i.fin_coa_cd " + 
           "AND a.account_nbr = i.account_nbr AND a.sub_acct_nbr = i.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND fp_interim1_level_mt.SESID = USERENV('SESSIONID'))");
      }

      // Summarize
      if ( isConsolidated ) {
        sqlCommand("INSERT INTO fp_bal_by_level_t (SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, " +
            "TYP_FIN_REPORT_SORT_CD, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT '-----', fin_obj_level_cd,fin_report_sort_cd, SUM(curr_bdln_bal_amt), " + 
            "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt),'" + financialReportingSortCode + "', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_level_mt " + 
            "WHERE fp_interim1_level_mt.SESID = USERENV('SESSIONID') GROUP BY fin_report_sort_cd, fin_obj_level_cd");
      } else {
        sqlCommand("INSERT INTO fp_bal_by_level_t (SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, " +
            "TYP_FIN_REPORT_SORT_CD, OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT  sub_acct_nbr, fin_obj_level_cd, fin_report_sort_cd, SUM(curr_bdln_bal_amt), " + 
            "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), '" + financialReportingSortCode + "', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_level_mt " + 
            "WHERE fp_interim1_level_mt.SESID = USERENV('SESSIONID') GROUP BY sub_acct_nbr, fin_report_sort_cd, fin_obj_level_cd");
      }

      // Here's the data
      List data = sqlSelect("select SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TYP_FIN_REPORT_SORT_CD " +
          "from FP_BAL_BY_LEVEL_T where PERSON_SYS_ID = USERENV('SESSIONID')");

      // Clean up everything
      sqlCommand("DELETE fp_bal_by_level_t WHERE person_sys_id = USERENV('SESSIONID')");
      sqlCommand("DELETE  fp_interim1_level_mt WHERE fp_interim1_level_mt.SESID = USERENV('SESSIONID')");

      return data;
    }

    public List findAccountBalanceByObject(Integer universityFiscalYear,String chartOfAccountsCode,String accountNumber,String financialObjectLevelCode,
        String financialReportingSortCode,boolean isCostShareExcluded,boolean isConsolidated) {

      // Delete any data for this session if it exists already
      sqlCommand("DELETE fp_bal_by_obj_t WHERE person_sys_id = USERENV('SESSIONID')");
      sqlCommand("DELETE  fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID = USERENV('SESSIONID')");

      // Add in all the data we need
      sqlCommand("INSERT INTO fp_interim1_obj_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR,FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT," + 
          "ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP,SESID, OBJ_ID, VER_NBR) SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR," + 
          "A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD, A.CURR_BDLN_BAL_AMT,A.ACLN_ACTLS_BAL_AMT, A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP,USERENV('SESSIONID'), sys_guid(), 1 " +
          "FROM gl_acct_balances_t a, ca_object_code_t o WHERE a.univ_fiscal_yr = " + universityFiscalYear + " AND a.fin_coa_cd = '" + chartOfAccountsCode + 
          "' AND a.account_nbr = '" + accountNumber + "' AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd AND a.fin_object_cd = o.fin_object_cd " + 
          "AND o.fin_obj_level_cd = '" + financialObjectLevelCode + "'");

      // Delete what we don't need
      if ( isCostShareExcluded ) {
        sqlCommand("DELETE fp_interim1_obj_mt WHERE ROWID IN (SELECT i.ROWID FROM fp_interim1_obj_mt i,ca_a21_sub_acct_t a WHERE (a.fin_coa_cd = i.fin_coa_cd " + 
            "AND a.account_nbr = i.account_nbr AND a.sub_acct_nbr = i.sub_acct_nbr AND a.sub_acct_typ_cd = 'CS') AND fp_interim1_obj_mt.SESID = USERENV('SESSIONID'))");
      }

      // Summarize
      if ( isConsolidated ) {
        sqlCommand("INSERT INTO fp_bal_by_obj_t (SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD, " +
            "OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT  '-----',fin_object_cd, SUM(curr_bdln_bal_amt),SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt)," + 
            "'B', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID  = USERENV('SESSIONID') " +
            "GROUP BY fin_object_cd");
      } else {
        sqlCommand("INSERT INTO fp_bal_by_obj_t (SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD, " +
            "OBJ_ID, VER_NBR, PERSON_SYS_ID) SELECT  sub_acct_nbr, fin_object_cd, SUM(curr_bdln_bal_amt), SUM(acln_actls_bal_amt),SUM(acln_encum_bal_amt), " +
            "'B', sys_guid(), 1, USERENV('SESSIONID') FROM fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID  = USERENV('SESSIONID') " + 
            "GROUP BY sub_acct_nbr, fin_object_cd");
      }

      // Here's the data
      List data = sqlSelect("select SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD from fp_bal_by_obj_t " +
         "where PERSON_SYS_ID = USERENV('SESSIONID') order by fin_object_cd");

      // Clean up everything
      sqlCommand("DELETE fp_bal_by_obj_t WHERE person_sys_id = USERENV('SESSIONID')");
      sqlCommand("DELETE  fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID = USERENV('SESSIONID')");

      return data;
    }

    /**
     * This method builds the atrribute list used by balance searching
     * @param isExtended determine whether the extended attributes will be used 
     * @param type the type of selection. Its value may be BY_CONSOLIDATION, BY_LEVEL and BY_OBJECT 
     * 
     * @return List an attribute list
     */
    private List buildAttributeList(boolean isExtended, String type) {
        List attributeList = this.buildGroupList(isExtended, type);

        attributeList.add("sum(currentBudgetLineBalanceAmount)");
        attributeList.add("sum(accountLineActualsBalanceAmount)");
        attributeList.add("sum(accountLineEncumbranceBalanceAmount)");

        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     * @param isExtended determine whether the extended attributes will be used 
     * @param type the type of selection. Its value may be BY_CONSOLIDATION, BY_LEVEL and BY_OBJECT
     * 
     * @return List an group by attribute list
     */
    private List buildGroupList(boolean isExtended, String type) {
        List attributeList = new ArrayList();

        attributeList.add("universityFiscalYear");
        attributeList.add("chartOfAccountsCode");
        attributeList.add("accountNumber");
        attributeList.add("subAccountNumber");

        // use the extended option and type
		if(!isExtended){
		    attributeList.add("objectCode");
		    attributeList.add("financialObject.financialObjectTypeCode");
		}
        else {
            if(type.equals(this.BY_CONSOLIDATION)){
                attributeList.add("financialObject.financialObjectType.financialReportingSortCode");
	            attributeList.add("financialObject.financialObjectLevel.financialConsolidationObject.financialReportingSortCode");	            
	            attributeList.add("financialObject.financialObjectLevel.financialConsolidationObjectCode");
            }
            else if(type.equals(this.BY_LEVEL)){
	            attributeList.add("financialObject.financialObjectLevel.financialReportingSortCode");
	            attributeList.add("financialObject.financialObjectLevel.financialObjectLevelCode"); 
	            attributeList.add("financialObject.financialObjectLevel.financialConsolidationObjectCode");
            }
            else if(type.equals(this.BY_OBJECT)){
                attributeList.add("objectCode");
                attributeList.add("financialObject.financialObjectLevel.financialReportingSortCode");
                attributeList.add("financialObject.financialObjectLevelCode");
            }
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
        } catch (Exception e) {
          throw new RuntimeException("Unable to execute: " + e.getMessage());
        } finally {
          try {
            if ( stmt != null ) {
              stmt.close();
            }
          } catch (Exception e) {
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
          for ( int i = 1 ; i <= numColumns ; i++ ) {
            row.put(rs.getMetaData().getColumnName(i).toUpperCase(),rs.getObject(i));
          }
          result.add(row);
        }
        return result;
      } catch (Exception e) {
        throw new RuntimeException("Unable to execute: " + e.getMessage());
      } finally {
        try {
          if ( stmt != null ) {
            stmt.close();
          }
        } catch (Exception e) {
          throw new RuntimeException("Unable to close connection: " + e.getMessage());
        }
      }
    }
}