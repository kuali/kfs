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
package org.kuali.module.gl.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class...
 * @author Bin Gao from Michigan State University
 */
public class NightlyOutServiceTest extends KualiTestBaseWithSpring {

    private NightlyOutService nightlyOutService;
    private UnitTestSqlDao unitTestSqlDao;

    protected void setUp() throws Exception {
        super.setUp();

        nightlyOutService = SpringServiceLocator.getNightlyOutService();
        unitTestSqlDao = (UnitTestSqlDao)SpringServiceLocator.getBeanFactory().getBean("glUnitTestSqlDao");
    }

    /**
     * This test validates that the correct data is copied into origin_entry
     * 
     * @throws Exception
     */
    public void testCopyPendingLedgerEntry() throws Exception {
      // Empty out the origin entry group & origin entry tables
      unitTestSqlDao.sqlCommand("delete from gl_origin_entry_t");
      unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");

      // Empty out the pending entry table & doc header table
      unitTestSqlDao.sqlCommand("delete from fp_doc_header_t where fdoc_nbr in ('1','2','3')");
      unitTestSqlDao.sqlCommand("delete from gl_pending_entry_t");

      // Add a few documents
      unitTestSqlDao.sqlCommand("insert into fp_doc_header_t (fdoc_nbr,obj_id,ver_nbr,fdoc_status_cd,fdoc_desc,fdoc_total_amt,org_doc_nbr," +
        "fdoc_in_err_nbr,fdoc_tmpl_nbr) values ('1',sys_guid(),1,'A','a',100,'OA',null,null)");
      unitTestSqlDao.sqlCommand("insert into fp_doc_header_t (fdoc_nbr,obj_id,ver_nbr,fdoc_status_cd,fdoc_desc,fdoc_total_amt,org_doc_nbr," +
        "fdoc_in_err_nbr,fdoc_tmpl_nbr) values ('2',sys_guid(),1,'D','b',100,'OB',null,null)");
      unitTestSqlDao.sqlCommand("insert into fp_doc_header_t (fdoc_nbr,obj_id,ver_nbr,fdoc_status_cd,fdoc_desc,fdoc_total_amt,org_doc_nbr," +
        "fdoc_in_err_nbr,fdoc_tmpl_nbr) values ('3',sys_guid(),1,'A','c',100,'OC',null,null)");

      unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," +
        "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," +
        "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," +
        "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," +
        "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm,bdgt_yr) values ('01','1',1,sys_guid(),1,'BA','123456'," +
        "null,'4166',null,'AC','EX',2004,'01'," +
        "'Description',100,'D',sysdate,'JV',null,null," +
        "null,null,null,null,null,' ','A'," +
        "'4166',null,null,null)");
      unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," +
          "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," +
          "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," +
          "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," +
          "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm,bdgt_yr) values ('01','1',2,sys_guid(),1,'BA','123456'," +
          "null,'4166',null,'AC','EX',2004,'01'," +
          "'Description',100,'C',sysdate,'JV',null,null," +
          "null,null,null,null,null,' ','A'," +
          "'4166',null,null,null)");
      unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," +
          "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," +
          "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," +
          "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," +
          "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm,bdgt_yr) values ('01','2',3,sys_guid(),1,'BA','123456'," +
          "null,'4166',null,'AC','EX',2004,'01'," +
          "'Description',100,'D',sysdate,'JV',null,null," +
          "null,null,null,null,null,' ','A'," +
          "'4166',null,null,null)");
      unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," +
          "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," +
          "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," +
          "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," +
          "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm,bdgt_yr) values ('01','2',4,sys_guid(),1,'BA','123456'," +
          "null,'4166',null,'AC','EX',2004,'01'," +
          "'Description',100,'C',sysdate,'JV',null,null," +
          "null,null,null,null,null,' ','A'," +
          "'4166',null,null,null)");
      unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," +
          "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," +
          "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," +
          "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," +
          "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm,bdgt_yr) values ('01','3',5,sys_guid(),1,'BA','123456'," +
          "null,'4166',null,'AC','EX',2004,'01'," +
          "'Description',100,'D',sysdate,'JV',null,null," +
          "null,null,null,null,null,' ','X'," +
          "'4166',null,null,null)");
      unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," +
          "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," +
          "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," +
          "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," +
          "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm,bdgt_yr) values ('01','3',6,sys_guid(),1,'BA','123456'," +
          "null,'4166',null,'AC','EX',2004,'01'," +
          "'Description',100,'C',sysdate,'JV',null,null," +
          "null,null,null,null,null,' ','X'," +
          "'4166',null,null,null)");

      nightlyOutService.copyApprovedPendingLedgerEntries();

      List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t");
      assertEquals("Should have 1 group",1,groups.size());

      List entries = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_t");
      assertEquals("Should have 2 entries",2,entries.size());
      for (Iterator iter = entries.iterator(); iter.hasNext();) {
        Map entry = (Map)iter.next();
        assertEquals("fdoc_nbr should be 1","1",(String)entry.get("FDOC_NBR"));
      }

      // 2 transactions were set to X to start with, 2 were marked as X when copyApprovedPendingLedgerEntries was run
      List pendingEntries = unitTestSqlDao.sqlSelect("select * from gl_pending_entry_t where fdoc_approved_cd = 'X'");
      assertEquals("Should have 4 copied entries",4,pendingEntries.size());

      nightlyOutService.deleteCopiedPendingLedgerEntries();
      List remainderEntries = unitTestSqlDao.sqlSelect("select * from gl_pending_entry_t");
      assertEquals("Should have 2 remaining entries",2,remainderEntries.size());
    }
}
