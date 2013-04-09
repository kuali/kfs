/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Tests the NighlyOutService
 */
@ConfigureContext
public class NightlyOutServiceTest extends KualiTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NightlyOutServiceTest.class);

    private NightlyOutService nightlyOutService;
    private UnitTestSqlDao unitTestSqlDao;
    private DateTimeService dateTimeService;

    protected Date date;
    protected String batchDirectory;
    protected String nightlyOutFileName;

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

        date = dateTimeService.getCurrentDate();
        batchDirectory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory")+"/gl/test_directory/originEntry";
        File batchDirectoryFile = new File(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory")+"/gl/test_directory");
        batchDirectoryFile.mkdir();
        batchDirectoryFile = new File(batchDirectory);
        batchDirectoryFile.mkdir();
        nightlyOutFileName = batchDirectory + File.separator + GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    }

    /**
     * Removes the directory for the batch files to go in
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        File batchDirectoryFile = new File(batchDirectory);
        for (File f : batchDirectoryFile.listFiles()) {
            f.delete();
        }
        batchDirectoryFile.delete();
        batchDirectoryFile = new File(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory")+"/gl/test_directory");
        batchDirectoryFile.delete();
    }

    /**
     * An inner class to filter only the files for this batch run
     */

    final class BatchFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        @Override
        public boolean accept(File dir, String name) {
            return name.equals(GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE);
        }
    }

    /**
     * This test validates that the correct data is copied into origin_entry
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    @ConfigureContext(shouldCommitTransactions=true)
    public void testCopyPendingLedgerEntry() throws Exception {
        // Empty out the origin entry group & origin entry tables
        assertTrue("batch directory does not exist", new File(batchDirectory).isDirectory());
        for (File file : new File(batchDirectory).listFiles()) {
            file.delete();
        }

        // Empty out the pending entry table & doc header table
        LOG.info( "deleting from KRNS_DOC_HDR_T" );
        unitTestSqlDao.sqlCommand("delete from KRNS_DOC_HDR_T where doc_hdr_id in ('1','2','3')");
        LOG.info( "deleting from FS_DOC_HEADER_T" );
        unitTestSqlDao.sqlCommand("delete from FS_DOC_HEADER_T where fdoc_nbr in ('1','2','3')");
        LOG.info( "deleting from GL_PENDING_ENTRY" );
        unitTestSqlDao.sqlCommand("delete from GL_PENDING_ENTRY_T");

        // Add a few documents
        LOG.info( "Inserting new documents" );
        unitTestSqlDao.sqlCommand("insert into KRNS_DOC_HDR_T (doc_hdr_id,obj_id,ver_nbr,fdoc_desc,org_doc_hdr_id,tmpl_doc_hdr_id) values ('1','" + UUID.randomUUID().toString() + "',1,'a','OA',null)");
        unitTestSqlDao.sqlCommand("insert into FS_DOC_HEADER_T (fdoc_nbr,fdoc_status_cd,fdoc_total_amt,fdoc_in_err_nbr) values ('1','A',100,null)");
        unitTestSqlDao.sqlCommand("insert into KRNS_DOC_HDR_T (doc_hdr_id,obj_id,ver_nbr,fdoc_desc,org_doc_hdr_id,tmpl_doc_hdr_id) values ('2','" + UUID.randomUUID().toString() + "',1,'b','OB',null)");
        unitTestSqlDao.sqlCommand("insert into FS_DOC_HEADER_T (fdoc_nbr,fdoc_status_cd,fdoc_total_amt,fdoc_in_err_nbr) values ('2','D',100,null)");
        unitTestSqlDao.sqlCommand("insert into KRNS_DOC_HDR_T (doc_hdr_id,obj_id,ver_nbr,fdoc_desc,org_doc_hdr_id,tmpl_doc_hdr_id) values ('3','" + UUID.randomUUID().toString() + "',1,'c','OC',null)");
        unitTestSqlDao.sqlCommand("insert into FS_DOC_HEADER_T (fdoc_nbr,fdoc_status_cd,fdoc_total_amt,fdoc_in_err_nbr) values ('3','A',100,null)");

        LOG.info( "Inserting new GL Pending entries" );
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','1',1,'" + UUID.randomUUID().toString() + "',1,'BA','1234567'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','A'," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','1',2,'" + UUID.randomUUID().toString() + "',1,'BA','1234567'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ',null," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','2',3,'" + UUID.randomUUID().toString() + "',1,'BA','1234567'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','A'," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','2',4,'" + UUID.randomUUID().toString() + "',1,'BA','1234567'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ',null," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','3',5,'" + UUID.randomUUID().toString() + "',1,'BA','1234567'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','X'," + "'4166',null,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (fs_origin_cd,fdoc_nbr,trn_entr_seq_nbr,obj_id,ver_nbr,fin_coa_cd,account_nbr," + "sub_acct_nbr,fin_object_cd,fin_sub_obj_cd,fin_balance_typ_cd,fin_obj_typ_cd,univ_fiscal_yr,univ_fiscal_prd_cd," + "trn_ldgr_entr_desc,trn_ldgr_entr_amt,trn_debit_crdt_cd,transaction_dt,fdoc_typ_cd,org_doc_nbr,project_cd," + "org_reference_id,fdoc_ref_typ_cd,fs_ref_origin_cd,fdoc_ref_nbr,fdoc_reversal_dt,trn_encum_updt_cd,fdoc_approved_cd," + "acct_sf_finobj_cd,trn_entr_ofst_cd,trnentr_process_tm) values ('01','3',6,'" + UUID.randomUUID().toString() + "',1,'BA','1234567'," + "null,'4166',null,'AC','EX',2004,'01'," + "'Description',100,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'JV',null,null," + "null,null,null,null,null,' ','X'," + "'4166',null,null)");

        LOG.info( "Calling NightlyOutService to copy the pending entries" );
        nightlyOutService.copyApprovedPendingLedgerEntries();
        LOG.info( "Completed Call to NightlyOutService" );

        assertEquals("Should have 2 group and one done file", 2, new File(batchDirectory).list().length);
        assertEquals("Should have 2 entries", 2, countOriginEntriesInFile());

        LOG.info( "Checking for processed GL Pending Entries" );
        // 2 transactions were set to X to start with, 2 were marked as X when copyApprovedPendingLedgerEntries was run
        List pendingEntries = unitTestSqlDao.sqlSelect("select * from GL_PENDING_ENTRY_T where fdoc_approved_cd = 'X'");
        assertEquals("Should have 4 copied entries", 4, pendingEntries.size());

        LOG.info( "Deleting processed GL Pending Entries" );
        nightlyOutService.deleteCopiedPendingLedgerEntries();
        LOG.info( "Confirming that GL Pending Entry is again empty" );
        List remainderEntries = unitTestSqlDao.sqlSelect("select * from GL_PENDING_ENTRY_T");
        assertEquals("Should have 2 remaining entries", 2, remainderEntries.size());
    }

    /**
     * @return the number of entries in the nightly out origin entry file
     */
    protected int countOriginEntriesInFile() {
        int count = 0;
        try {
            File nightlyOutFile = new File(this.nightlyOutFileName);
            BufferedReader nightlyOutFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(nightlyOutFile)));
            while (nightlyOutFileIn.readLine() != null) {
                count += 1;
            }
        }
        catch (FileNotFoundException fnfe) {
            //let's not sweat this one - if the file didn't exist, we'd hit errors before this
            throw new RuntimeException(fnfe);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return count;
    }
}
