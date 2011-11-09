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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class tests Cab Reconciliation service
 */
public class ReconciliationServiceTest extends BatchTestBase {

    private BatchExtractService batchExtractService;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
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
        Collection<PurApAccountingLineBase> purapAcctEntries = batchExtractService.findPurapAccountRevisions();
        service.reconcile(purapLines, purapAcctEntries);
        List<Entry> ignoredEntries = service.getDuplicateEntries();
        Collection<GlAccountLineGroup> mismatchedEntries = service.getMisMatchedGroups();
        Collection<GlAccountLineGroup> validEntries = service.getMatchedGroups();

        assertEquals(0, ignoredEntries.size());
        assertEquals(11, validEntries.size());
        assertEquals(0, mismatchedEntries.size());

        // assert if result amounts are reconciled correctly
        for (GlAccountLineGroup glAccountLineGroup : validEntries) {
            KualiDecimal purapAmount = KualiDecimal.ZERO;
            List<PurApAccountingLineBase> matchedPurApAcctLines = glAccountLineGroup.getMatchedPurApAcctLines();
            for (PurApAccountingLineBase purApAccountingLineBase : matchedPurApAcctLines) {
                purapAmount = purapAmount.add(purApAccountingLineBase.getAmount());
            }
            Entry targetEntry = glAccountLineGroup.getTargetEntry();
            KualiDecimal targetAmount = targetEntry.getTransactionLedgerEntryAmount();
            if (CabConstants.CM.equals(targetEntry.getFinancialDocumentTypeCode())) {
                assertTrue(glAccountLineGroup.getAmount().equals(purapAmount.negated()));
            }
            else {
                assertTrue(glAccountLineGroup.getAmount().equals(purapAmount));
            }

            if (KFSConstants.GL_CREDIT_CODE.equals(targetEntry.getTransactionDebitCreditCode())) {
                targetAmount = targetAmount.negated();
            }
            if (CabConstants.CM.equals(targetEntry.getFinancialDocumentTypeCode())) {
                assertTrue(targetAmount.equals(purapAmount.negated()));
            }
            else {
                assertTrue(targetAmount.equals(purapAmount));
            }
            purapAmount = KualiDecimal.ZERO;
        }

    }

}
