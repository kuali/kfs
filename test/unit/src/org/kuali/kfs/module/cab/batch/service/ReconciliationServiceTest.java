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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

/**
 * This class tests Cab Reconciliation service
 */
public class ReconciliationServiceTest extends BatchTestBase {

    private BatchExtractService batchExtractService;

    @ConfigureContext(session = UserNameFixture.KHUNTLEY, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        batchExtractService = SpringContext.getBean(BatchExtractService.class);
    }

    /**
     * Test to ensure that reconciliation service is not configured as singleton
     * 
     * @throws Exception
     */
    public void testServiceNotSingleton() throws Exception {
        ReconciliationService cabReconciliationService1 = SpringContext.getBean(ReconciliationService.class);
        ReconciliationService cabReconciliationService2 = SpringContext.getBean(ReconciliationService.class);
        assertFalse(cabReconciliationService1.equals(cabReconciliationService2));
    }

    public void testReconcile() throws Exception {
        ReconciliationService service = SpringContext.getBean(ReconciliationService.class);
        Collection<Entry> glEntries = batchExtractService.findElgibleGLEntries(new ExtractProcessLog());
        List<Entry> purapLines = new ArrayList<Entry>();
        batchExtractService.separatePOLines(new ArrayList<Entry>(), purapLines, glEntries);
        Collection<PurApAccountingLineBase> purapAcctEntries = batchExtractService.findPurapAccountHistory();
        Collection<GeneralLedgerPendingEntry> pendingGlEntries = batchExtractService.findPurapPendingGLEntries();
        service.reconcile(purapLines, pendingGlEntries, purapAcctEntries);
        List<Entry> ignoredEntries = service.getDuplicateEntries();
        Collection<GlAccountLineGroup> mismatchedEntries = service.getMisMatchedGroups();
        Collection<GlAccountLineGroup> validEntries = service.getMatchedGroups();

        assertEquals(0, ignoredEntries.size());
        assertEquals(10, validEntries.size());
        assertEquals(0, mismatchedEntries.size());

    }

}
