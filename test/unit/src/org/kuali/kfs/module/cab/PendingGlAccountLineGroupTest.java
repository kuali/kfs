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
package org.kuali.kfs.module.cab;

import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.PendingGlAccountLineGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class PendingGlAccountLineGroupTest extends KualiTestBase {

    @Override
    protected void setUp() throws Exception {
    }

    public void testCombineEntry() throws Exception {
        PendingGlAccountLineGroup first = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D", new KualiDecimal(100));
        GeneralLedgerPendingEntry second = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D", new KualiDecimal(200));
        assertTrue(first.equals(new PendingGlAccountLineGroup(second)));
        first.combineEntry(second);
        assertEquals("D", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(300), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(300), first.getAmount());
        GeneralLedgerPendingEntry third = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "C", new KualiDecimal(200));
        assertTrue(first.equals(new PendingGlAccountLineGroup(third)));
        first.combineEntry(third);
        assertEquals("D", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(100), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(100), first.getAmount());

        GeneralLedgerPendingEntry fourth = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "C", new KualiDecimal(200));
        assertTrue(first.equals(new PendingGlAccountLineGroup(fourth)));
        first.combineEntry(fourth);
        assertEquals("C", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(100), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(-100), first.getAmount());

        GeneralLedgerPendingEntry fifth = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D", new KualiDecimal(200));
        assertTrue(first.equals(new PendingGlAccountLineGroup(fifth)));
        first.combineEntry(fifth);
        assertEquals("D", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(100), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(100), first.getAmount());

        List<GeneralLedgerPendingEntry> sourceEntries = first.getSourceEntries();
        assertEquals(5, sourceEntries.size());
        KualiDecimal srcAmount = KualiDecimal.ZERO;
        for (GeneralLedgerPendingEntry entry : sourceEntries) {
            if (KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
                srcAmount = srcAmount.add(entry.getTransactionLedgerEntryAmount().negated());
            }
            else {
                srcAmount = srcAmount.add(entry.getTransactionLedgerEntryAmount());
            }
        }
        assertEquals(new KualiDecimal(100), srcAmount);
        assertTrue(first.getAmount().equals(srcAmount));
    }

    private PendingGlAccountLineGroup createAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode, KualiDecimal amount) {
        GeneralLedgerPendingEntry entry = createEntry(i, chartCode, acctNum, subAcctNum, objCd, subObjCd, fiscalPrd, docNum, refDocNum, dbtCrdtCode, amount);
        PendingGlAccountLineGroup first = new PendingGlAccountLineGroup(entry);
        return first;
    }

    private GeneralLedgerPendingEntry createEntry(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode, KualiDecimal amount) {
        GeneralLedgerPendingEntry entry = new GeneralLedgerPendingEntry();
        entry.setUniversityFiscalYear(i);
        entry.setChartOfAccountsCode(chartCode);
        entry.setAccountNumber(acctNum);
        entry.setSubAccountNumber(subAcctNum);
        entry.setFinancialObjectCode(objCd);
        entry.setFinancialSubObjectCode(subObjCd);
        entry.setUniversityFiscalPeriodCode(fiscalPrd);
        entry.setDocumentNumber(docNum);
        entry.setReferenceFinancialDocumentNumber(refDocNum);
        entry.setTransactionDebitCreditCode(dbtCrdtCode);
        entry.setTransactionLedgerEntryAmount(amount);
        return entry;
    }


}
