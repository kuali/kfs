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
package org.kuali.kfs.module.cab;

import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class GlAccountLineGroupTest extends KualiTestBase {

    @Override
    protected void setUp() throws Exception {
    }

    public void testCombineEntry() throws Exception {
        GlAccountLineGroup first = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D", new KualiDecimal(100));
        Entry second = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D", new KualiDecimal(200));
        assertTrue(first.equals(new GlAccountLineGroup(second)));
        first.combineEntry(second);
        assertEquals("D", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(300), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(300), first.getAmount());
        Entry third = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "C", new KualiDecimal(200));
        assertTrue(first.equals(new GlAccountLineGroup(third)));
        first.combineEntry(third);
        assertEquals("D", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(100), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(100), first.getAmount());

        Entry fourth = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "C", new KualiDecimal(200));
        assertTrue(first.equals(new GlAccountLineGroup(fourth)));
        first.combineEntry(fourth);
        assertEquals("C", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(100), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(-100), first.getAmount());

        Entry fifth = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D", new KualiDecimal(200));
        assertTrue(first.equals(new GlAccountLineGroup(fifth)));
        first.combineEntry(fifth);
        assertEquals("D", first.getTargetEntry().getTransactionDebitCreditCode());
        assertEquals(new KualiDecimal(100), first.getTargetEntry().getTransactionLedgerEntryAmount());
        assertEquals(new KualiDecimal(100), first.getAmount());

        List<Entry> sourceEntries = first.getSourceEntries();
        assertEquals(5, sourceEntries.size());
        KualiDecimal srcAmount = KualiDecimal.ZERO;
        for (Entry entry : sourceEntries) {
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

    private GlAccountLineGroup createAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode, KualiDecimal amount) {
        Entry entry = createEntry(i, chartCode, acctNum, subAcctNum, objCd, subObjCd, fiscalPrd, docNum, refDocNum, dbtCrdtCode, amount);
        GlAccountLineGroup first = new GlAccountLineGroup(entry);
        return first;
    }

    private Entry createEntry(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode, KualiDecimal amount) {
        Entry entry = new Entry();
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
