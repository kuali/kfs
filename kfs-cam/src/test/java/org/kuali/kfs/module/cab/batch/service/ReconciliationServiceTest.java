/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
