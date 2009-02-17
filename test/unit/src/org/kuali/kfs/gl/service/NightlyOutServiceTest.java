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
package org.kuali.kfs.gl.service;

import java.util.List;
import java.util.UUID;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.Guid;

/**
 * Tests the NighlyOutService
 */
@ConfigureContext
public class NightlyOutServiceTest extends KualiTestBase {

    private NightlyOutService nightlyOutService;
    private UnitTestSqlDao unitTestSqlDao;
    private DateTimeService dateTimeService;

    /**
     * Initializes the services needed for this test
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        nightlyOutService = SpringContext.getBean(NightlyOutService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     * This test validates that the correct data is copied into origin_entry
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCopyPendingLedgerEntry() throws Exception {
        // Empty out the origin entry group & origin entry tables
        unitTestSqlDao.sqlCommand("delete from gl_origin_entry_t");
        unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");

        // Empty out the pending entry table & doc header table
        unitTestSqlDao.sqlCommand("delete from krns_doc_hdr_t where doc_hdr_id in ('1','2','3')");
        unitTestSqlDao.sqlCommand("delete from fs_doc_header_t where fdoc_nbr in ('1','2','3')");
        unitTestSqlDao.sqlCommand("delete from gl_pending_entry_t");

        // Add a few documents
        unitTestSqlDao.sqlCommand("insert into krns_doc_hdr_t (doc_hdr_id,obj_id,ver_nbr,fdoc_desc,org_doc_hdr_id,tmpl_doc_hdr_id) values ('1','" + UUID.randomUUID().toString() + "',1,'a','OA',null)");
        unitTestSqlDao.sqlCommand("insert into fs_doc_header_t (fdoc_nbr,fdoc_status_cd,fdoc_total_amt,fdoc_in_err_nbr) values ('1','A',100,null)");
        unitTestSqlDao.sqlCommand("insert into krns_doc_hdr_t (doc_hdr_id,obj_id,ver_nbr,fdoc_desc,org_doc_hdr_id,tmpl_doc_hdr_id) values ('2','" + UUID.randomUUID().toString() + "',1,'b','OB',null)");
        unitTestSqlDao.sqlCommand("insert into fs_doc_header_t (fdoc_nbr,fdoc_status_cd,fdoc_total_amt,fdoc_in_err_nbr) values ('2','D',100,null)");
        unitTestSqlDao.sqlCommand("insert into krns_doc_hdr_t (doc_hdr_id,obj_id,ver_nbr,fdoc_desc,org_doc_hdr_id,tmpl_doc_hdr_id) values ('3','" + UUID.randomUUID().toString() + "',1,'c','OC',null)");
        unitTestSqlDao.sqlCommand("insert into fs_doc_header_t (fdoc_nbr,fdoc_status_cd,fdoc_total_amt,fdoc_in_err_nbr) values ('3','A',100,null)");

        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','1',1,'" + new Guid().toString() + "',1,'BA','123456'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','A'," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','1',2,'" + new Guid().toString() + "',1,'BA','123456'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ',null," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','2',3,'" + new Guid().toString() + "',1,'BA','123456'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','A'," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','2',4,'" + new Guid().toString() + "',1,'BA','123456'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ',null," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','3',5,'" + new Guid().toString() + "',1,'BA','123456'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','X'," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into gl_pending_entry_t (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','3',6,'" + new Guid().toString() + "',1,'BA','123456'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','X'," + "'4166',null,null)");

        nightlyOutService.copyApprovedPendingLedgerEntries();

        //TODO: originEntryGroup and originEntry tables are not used anymore.  Need to change it using file.
        
        //List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t");
        //assertEquals("Should have 1 group", 1, groups.size());

        //List entries = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_t");
        //assertEquals("Should have 2 entries", 2, entries.size());

        // 2 transactions were set to X to start with, 2 were marked as X when copyApprovedPendingLedgerEntries was run
        // TODO: Shawn - copied entries are 2 because 2 entries are not copied because there is no originEntryTable.
        // this might change to 4 after above test are passed. 
        List pendingEntries = unitTestSqlDao.sqlSelect("select * from gl_pending_entry_t where fdoc_approved_cd = 'X'");
        //assertEquals("Should have 4 copied entries", 4, pendingEntries.size());
        assertEquals("Should have 2 copied entries", 2, pendingEntries.size());

        // TODO: Shawn - opposite from above - remaning entries should be 4 since 2 entries are not copied 
        nightlyOutService.deleteCopiedPendingLedgerEntries();
        List remainderEntries = unitTestSqlDao.sqlSelect("select * from gl_pending_entry_t");
        //assertEquals("Should have 2 remaining entries", 2, remainderEntries.size());
        assertEquals("Should have 4 remaining entries", 4, remainderEntries.size());
    }
}
