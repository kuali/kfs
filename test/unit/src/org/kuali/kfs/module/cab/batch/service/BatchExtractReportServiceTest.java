/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.batch.service;

import java.io.File;
import java.sql.Timestamp;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

/**
 * This test will test the PDF report generation of batch status report
 */
public class BatchExtractReportServiceTest extends KualiTestBase {

    private BatchExtractReportService batchExtractReportService;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        batchExtractReportService = SpringContext.getBean(BatchExtractReportService.class);
    }

    public void testGenerateStatusReport() throws Exception {
        ExtractProcessLog processLog = new ExtractProcessLog();
        processLog.setStartTime(new Timestamp(System.currentTimeMillis()));
        processLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
        processLog.setLastExtractTime(new Timestamp(System.currentTimeMillis()));
        processLog.setTotalGlCount(15);
        processLog.setPurApGlCount(10);
        processLog.setNonPurApGlCount(5);
        processLog.setSuccess(false);
        processLog.setErrorMessage("Unexpected error occured");

        Entry entry1 = new Entry();
        entry1.setDocumentNumber("1234");
        entry1.setReferenceFinancialDocumentNumber("PO232");
        entry1.setAccountNumber("AC2323");
        entry1.setTransactionLedgerEntryDescription("Mismatched");

        Entry entry2 = new Entry();
        entry2.setDocumentNumber("1235");
        entry2.setReferenceFinancialDocumentNumber("PO235");
        entry2.setAccountNumber("AC2325");
        entry2.setTransactionLedgerEntryDescription("Duplicate");

        Entry entry3 = new Entry();
        entry3.setDocumentNumber("1236");
        entry3.setReferenceFinancialDocumentNumber("PO236");
        entry3.setAccountNumber("AC2326");
        entry3.setTransactionLedgerEntryDescription("Ignored");

        processLog.addMismatchedGLEntry(entry1);
        processLog.addMismatchedGLEntry(entry1);
        processLog.addMismatchedGLEntry(entry1);

        processLog.addDuplicateGLEntry(entry2);
        processLog.addDuplicateGLEntry(entry2);
        processLog.addDuplicateGLEntry(entry2);

        processLog.addIgnoredGLEntry(entry3);
        processLog.addIgnoredGLEntry(entry3);
        processLog.addIgnoredGLEntry(entry3);

        try {
            File report = batchExtractReportService.generateStatusReportPDF(processLog);
            if (report.isFile()) {
                // check file is created and deleted
                assertTrue(report.delete());
            }
            else {
                fail("Report not generated");
            }
        }
        catch (Exception e) {
            fail("fail to generate PDF file." + e);
        }
    }

    public void testGenerateMismatchReport() throws Exception {
        ExtractProcessLog processLog = new ExtractProcessLog();
        processLog.setStartTime(new Timestamp(System.currentTimeMillis()));
        processLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
        processLog.setLastExtractTime(new Timestamp(System.currentTimeMillis()));
        processLog.setTotalGlCount(15);
        processLog.setPurApGlCount(10);
        processLog.setNonPurApGlCount(5);
        processLog.setSuccess(false);
        processLog.setErrorMessage("Unexpected error occured");

        Entry entry1 = new Entry();
        entry1.setDocumentNumber("1234");
        entry1.setReferenceFinancialDocumentNumber("PO232");
        entry1.setAccountNumber("AC2323");
        entry1.setTransactionLedgerEntryDescription("Mismatched");

        Entry entry2 = new Entry();
        entry2.setDocumentNumber("1235");
        entry2.setReferenceFinancialDocumentNumber("PO235");
        entry2.setAccountNumber("AC2325");
        entry2.setTransactionLedgerEntryDescription("Duplicate");

        Entry entry3 = new Entry();
        entry3.setDocumentNumber("1236");
        entry3.setReferenceFinancialDocumentNumber("PO236");
        entry3.setAccountNumber("AC2326");
        entry3.setTransactionLedgerEntryDescription("Ignored");

        processLog.addMismatchedGLEntry(entry1);
        processLog.addMismatchedGLEntry(entry1);
        processLog.addMismatchedGLEntry(entry1);

        try {
            File report = batchExtractReportService.generateMismatchReportPDF(processLog);
            if (report.isFile()) {
                // check file is created and deleted
                assertTrue(report.delete());
            }
            else {
                fail("Report not generated");
            }
        }
        catch (Exception e) {
            fail("fail to generate PDF file." + e);
        }
    }

}

