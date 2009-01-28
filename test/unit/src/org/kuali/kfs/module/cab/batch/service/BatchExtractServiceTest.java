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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.suite.RelatesTo;
import org.kuali.kfs.sys.suite.RelatesTo.JiraIssue;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.DateUtils;

/**
 * Default implementation
 */
public class BatchExtractServiceTest extends BatchTestBase {

    private BatchExtractService batchExtractService;
    private BusinessObjectService boService;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        batchExtractService = SpringContext.getBean(BatchExtractService.class);
        boService = SpringContext.getBean(BusinessObjectService.class);
    }

    public void testBatchProcess() throws Exception {
        ExtractProcessLog processLog = new ExtractProcessLog();
        Collection<Entry> glEntries = batchExtractService.findElgibleGLEntries(processLog);
        assertNotNull(glEntries);
        assertEquals(13, glEntries.size());
        List<Entry> fpLines = new ArrayList<Entry>();
        List<Entry> purapLines = new ArrayList<Entry>();
        // Test separation of lines
        batchExtractService.separatePOLines(fpLines, purapLines, glEntries);
        assertEquals(11, purapLines.size());
        assertEquals(2, fpLines.size());
        // Test saving of FP lines
        batchExtractService.saveFPLines(fpLines, processLog);
        Collection<GeneralLedgerEntry> fpGls = boService.findAll(GeneralLedgerEntry.class);
        assertEquals(2, fpGls.size());
        Timestamp ts = new Timestamp(DateUtils.parseDate("01/01/1970 23:59:59", new String[] { CabConstants.DATE_FORMAT_TS }).getTime());
        // Test updating the last extract time stamp
        batchExtractService.updateLastExtractTime(ts);
        Map<String, String> primaryKeys = new LinkedHashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_BATCH);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAME, CabConstants.Parameters.LAST_EXTRACT_TIME);
        Parameter parameter = (Parameter) boService.findByPrimaryKey(Parameter.class, primaryKeys);
        assertEquals("01/01/1970 23:59:59", parameter.getParameterValue());

        // Test saving PO lines
        batchExtractService.savePOLines(purapLines, processLog);

        // After saving PO lines asset count of records
        Collection<GeneralLedgerEntry> gls = boService.findAll(GeneralLedgerEntry.class);
        assertEquals(13, gls.size());
        Collection<PurchasingAccountsPayableDocument> allCabDocs = boService.findAll(PurchasingAccountsPayableDocument.class);
        assertEquals(7, allCabDocs.size());

        Collection<PurchasingAccountsPayableItemAsset> allCabItems = boService.findAll(PurchasingAccountsPayableItemAsset.class);
        assertEquals(14, allCabItems.size());

        Collection<PurchasingAccountsPayableLineAssetAccount> allCabAccts = boService.findAll(PurchasingAccountsPayableLineAssetAccount.class);
        assertEquals(17, allCabAccts.size());
    }


    public void testFindPreTaggablePOAccounts() throws Exception {
        Collection<PurchaseOrderAccount> preTaggablePOAccounts = batchExtractService.findPreTaggablePOAccounts();
        assertEquals(6, preTaggablePOAccounts.size());
        batchExtractService.savePreTagLines(preTaggablePOAccounts);
    }
}
