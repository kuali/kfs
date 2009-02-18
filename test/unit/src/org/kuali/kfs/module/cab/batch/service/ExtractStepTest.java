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
package org.kuali.kfs.module.cab.batch.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.batch.ExtractStep;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * This class tests the extract step involved in CAB Batch job
 */
public class ExtractStepTest extends BatchTestBase {

    /**
     * This class prevents creating PDF files for every test run. Also it helps in asserting the extract log content
     */
    private static class MockBatchExtractReportService implements BatchExtractReportService {
        public File generateStatusReportPDF(ExtractProcessLog extractProcessLog) {
            assertNotNull(extractProcessLog);
            assertTrue(extractProcessLog.isSuccess());
            assertEquals(Integer.valueOf(13), extractProcessLog.getTotalGlCount());
            assertEquals(Integer.valueOf(2), extractProcessLog.getNonPurApGlCount());
            assertEquals(Integer.valueOf(11), extractProcessLog.getPurApGlCount());
            assertNotNull(extractProcessLog.getStartTime());
            assertNotNull(extractProcessLog.getFinishTime());
            assertNotNull(extractProcessLog.getLastExtractTime());
            assertTrue(extractProcessLog.getIgnoredGLEntries() == null || extractProcessLog.getIgnoredGLEntries().isEmpty());
            assertTrue(extractProcessLog.getMismatchedGLEntries() == null || extractProcessLog.getMismatchedGLEntries().isEmpty());
            assertTrue(extractProcessLog.getDuplicateGLEntries() == null || extractProcessLog.getDuplicateGLEntries().isEmpty());
            return null;
        }

        public File generateMismatchReportPDF(ExtractProcessLog extractProcessLog) {
            return null;
        }
    }

    private DateTimeService dateTimeService;
    private BusinessObjectService boService;
    private ExtractStep extractStep;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        extractStep = SpringContext.getBean(ExtractStep.class);
        extractStep.setBatchExtractReportService(new MockBatchExtractReportService());
        boService = SpringContext.getBean(BusinessObjectService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        extractStep.setBatchExtractReportService(SpringContext.getBean(BatchExtractReportService.class));
    }

    public void testExecute() throws Exception {
        java.sql.Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();

        extractStep.execute("CabBatchExtractJob", dateTimeService.getCurrentDate());

        // Count of GL lines
        Collection<GeneralLedgerEntry> gls = boService.findAll(GeneralLedgerEntry.class);
        assertEquals(13, gls.size());

        // Count of purap docs
        Collection<PurchasingAccountsPayableDocument> allCabDocs = boService.findAll(PurchasingAccountsPayableDocument.class);
        assertEquals(7, allCabDocs.size());

        // Count of purap items
        Collection<PurchasingAccountsPayableItemAsset> allCabItems = boService.findAll(PurchasingAccountsPayableItemAsset.class);
        assertEquals(14, allCabItems.size());

        // Count of purap account lines
        Collection<PurchasingAccountsPayableLineAssetAccount> allCabAccts = boService.findAll(PurchasingAccountsPayableLineAssetAccount.class);
        assertEquals(17, allCabAccts.size());

        // assert the extract date value
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals(fmt.format(currentSqlDate), findCabExtractTimeParam().getParameterValue().substring(0, 10));
    }
}
