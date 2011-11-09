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
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.batch.ExtractStep;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

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
    private Timestamp beforeRun = null;

    private DateTimeService dateTimeService;
    private BusinessObjectService boService;
    private ExtractStep extractStep;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        extractStep = (ExtractStep) ProxyUtils.getTargetIfProxied( SpringContext.getBean(Step.class,"cabExtractStep") );
        beforeRun = dateTimeService.getCurrentTimestamp();
        extractStep.setBatchExtractReportService(new MockBatchExtractReportService());
        boService = SpringContext.getBean(BusinessObjectService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        extractStep.setBatchExtractReportService(SpringContext.getBean(BatchExtractReportService.class));
    }
//NO RUN SETUP
    public void testNothing() {
    }
   
    public void NORUN_testExecute() throws Exception {
//        java.sql.Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Date currentSqlDate = dateTimeService.getCurrentSqlDate();
        extractStep.execute("CabBatchExtractJob", dateTimeService.getCurrentDate());
        // Count of GL lines
        
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("transactionDate", dateTimeService.getCurrentSqlDate());
        Collection<GeneralLedgerEntry> gls = boService.findMatching(GeneralLedgerEntry.class,m);
        assertEquals(13, gls.size());

        // Count of purap docs
        Map<String,String> m2 = new HashMap<String,String>();
        m2.put("activityStatusCode",  CabConstants.ActivityStatusCode.NEW);        
        Collection<PurchasingAccountsPayableDocument> allCabDocs = boService.findMatching(PurchasingAccountsPayableDocument.class,m2);
        assertEquals(7, allCabDocs.size());

        // Count of purap items ---- drill through doc header to get itemAssets and test against qty = 1....
       //Map<String, Object> keys = new HashMap<String, Object>();
       //keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.DOCUMENT_NUMBER, cabPurapDoc.getDocumentNumber());
       //keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER, apItem.getItemIdentifier());
       //Collection<PurchasingAccountsPayableItemAsset> matchingItems = businessObjectService.findMatching(PurchasingAccountsPayableItemAsset.class, keys);

        Map<String,Object> m3 = new HashMap<String,Object>();
        m3.put("activityStatusCode", CabConstants.ActivityStatusCode.NEW);        
        Collection<PurchasingAccountsPayableItemAsset> allCabItems = boService.findMatching(PurchasingAccountsPayableItemAsset.class, m3);
        for(PurchasingAccountsPayableItemAsset aci:allCabItems){
            
            System.out.println(aci.isActive()+" - "+aci.getActivityStatusCode());
        }
       //assertEquals(14, allCabItems.size());

        // Count of purap account lines
        Collection<PurchasingAccountsPayableLineAssetAccount> allCabAccts = boService.findAll(PurchasingAccountsPayableLineAssetAccount.class);
       //for(PurchasingAccountsPayableLineAssetAccount aca:allCabAccts){
            
       //     System.out.println(aca.isActive()+" - "+aca.getGeneralLedgerEntry().getTransactionDate());
       // }
       //assertEquals(17, allCabAccts.size());

        // assert the extract date value
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals(fmt.format(currentSqlDate), findCabExtractTimeParam().getValue().substring(0, 10));
    }
 // END NO RUN SETUP     
}
