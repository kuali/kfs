/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.List;
import java.util.UUID;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.util.UnitTestSqlDao;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;

/**
 * A test that allows manual testing of report generation for nightly out (ie, creates results and then people can look at those results and make certain they were expected)
 */
@ConfigureContext
@RawSQL
public class ReportServiceTest extends KualiTestBase {

    private NightlyOutService nightlyOutService;
    private ReportService reportService;
    private UnitTestSqlDao unitTestSqlDao;

    /**
     * Sets up the services needed by this test
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        nightlyOutService = SpringContext.getBean(NightlyOutService.class);
        reportService = SpringContext.getBean(ReportService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }

    /**
     * This method isn't as much a test as an easy way to fire off the report generation process so that the reports can be checked.
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPendingEntryReport() throws Exception {

        // Empty out the origin entry group & origin entry tables
        unitTestSqlDao.sqlCommand("delete from gl_origin_entry_t");
        unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");

        // Empty out the pending entry table & doc header table
        unitTestSqlDao.sqlCommand("delete from fp_doc_header_t where fdoc_nbr in ('1','2','3','4','5','6')");
        unitTestSqlDao.sqlCommand("delete from gl_pending_entry_t");

        // Add a few documents
        unitTestSqlDao.sqlCommand("insert into fp_doc_header_t (fdoc_nbr,obj_id,ver_nbr,fdoc_status_cd,fdoc_desc,fdoc_total_amt,org_doc_nbr," + "fdoc_in_err_nbr,fdoc_tmpl_nbr) values ('1','" + UUID.randomUUID().toString() + "',1,'A','a',100,'OA',null,null)");
        unitTestSqlDao.sqlCommand("insert into fp_doc_header_t (fdoc_nbr,obj_id,ver_nbr,fdoc_status_cd,fdoc_desc,fdoc_total_amt,org_doc_nbr," + "fdoc_in_err_nbr,fdoc_tmpl_nbr) values ('2','" + UUID.randomUUID().toString() + "',1,'A','b',100,'OB',null,null)");
        unitTestSqlDao.sqlCommand("insert into fp_doc_header_t (fdoc_nbr,obj_id,ver_nbr,fdoc_status_cd,fdoc_desc,fdoc_total_amt,org_doc_nbr," + "fdoc_in_err_nbr,fdoc_tmpl_nbr) values ('3','" + UUID.randomUUID().toString() + "',1,'A','c',100,'OC',null,null)");

        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','1',1,'" + UUID.randomUUID().toString() + "',1,'BA','123456'," + "null,'4161',null,'AC','EX',2004,'01','Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',11,null,null,null,null,null,null,' ','A','4166',null,null)");

        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','1',2,'" + UUID.randomUUID().toString() + "',1,'BA','123456'," + "null,'4162',null,'AC','EX',2004,'01','Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',12,null,null,null,null,null,null,' ','A','4166',null,null)");

        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','2',3,'" + UUID.randomUUID().toString() + "',1,'BA','223456'," + "null,'4163',null,'AC','EX',2004,'01','Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',13,null,null,null,null,null,null,' ','A','4166',null,null)");

        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','2',4,'" + UUID.randomUUID().toString() + "',1,'BA','223456'," + "null,'4164',null,'AC','EX',2004,'01','Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',14,null,null,null,null,null,null,' ','A','4166',null,null)");

        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','3',5,'" + UUID.randomUUID().toString() + "',1,'BA','323456'," + "null,'4165',null,'AC','EX',2004,'01','Description',100,''," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',15,null,null,null,null,null,null,' ','A','4166',null,null)");

        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','3',6,'" + UUID.randomUUID().toString() + "',1,'BA','323456'," + "null,'4166',null,'AC','EX',2004,'01','Description',100,''," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',16,null,null,null,null,null,null,' ','A','4166',null,null)");

        nightlyOutService.copyApprovedPendingLedgerEntries();

        List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t");
        assertEquals("Should have 1 group", 1, groups.size());

        List entries = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_t");
        assertEquals("Should have 6 entries", 6, entries.size());
        // TODO: fix this
        // reportService.generatePendingEntryReport();

    }

}
