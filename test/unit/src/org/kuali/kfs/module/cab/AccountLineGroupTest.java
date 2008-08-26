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

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PendingGlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PurApAccountLineGroup;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class AccountLineGroupTest extends KualiTestBase {

    @Override
    protected void setUp() throws Exception {
    }

    public void testEquals() throws Exception {
        // test reflexive
        GlAccountLineGroup first = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D");
        PendingGlAccountLineGroup second = createPendingAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "C");
        GlAccountLineGroup third = createAccountLineGroup(2008, "BL", new String("BL002323"), "--", "7000", null, "01", "1001", null, "D");
        assertTrue(first.equals(first));
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
        assertTrue(second.equals(third));
        assertTrue(third.equals(second));
        assertTrue(first.equals(third));
        assertTrue(third.equals(first));

        first = createAccountLineGroup(2008, "BA", "BL002323", "--", "7000", null, "01", "1001", null, "C");
        second = createPendingAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D");
        third = createAccountLineGroup(2008, "BA", "BL002323", "--", "7000", null, "01", "1001", null, "C");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
        assertFalse(second.equals(third));
        assertFalse(third.equals(second));
        assertTrue(first.equals(third));
        assertTrue(third.equals(first));

        first = createAccountLineGroup(2008, "BA", "BL002323", "X", "7000", null, "01", "1001", null, "C");
        second = createPendingAccountLineGroup(2008, "BA", "BL002323", "XXX", "7000", null, "01", "1001", null, "D");
        third = createAccountLineGroup(2008, "BA", "BL002323", "XXXX", "7000", null, "01", "1001", null, "C");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
        assertFalse(second.equals(third));
        assertFalse(third.equals(second));
        assertFalse(first.equals(third));
        assertFalse(third.equals(first));
        assertFalse(first.equals(null));
        assertFalse(second.equals(null));
        assertFalse(third.equals(null));
    }

    public void testHashcode() throws Exception {
        GlAccountLineGroup first = createAccountLineGroup(new Integer(2008), new String("BL"), "BL002323", "--", "7000", "12121", "01", "1001", "A", "C");
        GlAccountLineGroup second = createAccountLineGroup(2008, "BL", "BL002323", "--", new String("7000"), "12121", "01", "1001", "A", "C");
        PendingGlAccountLineGroup third = createPendingAccountLineGroup(2008, "BL", new String("BL002323"), "", "7000", "12121", "01", "1001", "A", "D");
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(second.hashCode(), third.hashCode());
        assertEquals(first.hashCode(), third.hashCode());
    }

    private GlAccountLineGroup createAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode) {
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
        entry.setTransactionLedgerEntryAmount(KualiDecimal.ZERO);
        GlAccountLineGroup first = new GlAccountLineGroup(entry);
        return first;
    }


    private PendingGlAccountLineGroup createPendingAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode) {
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
        entry.setTransactionLedgerEntryAmount(KualiDecimal.ZERO);
        PendingGlAccountLineGroup first = new PendingGlAccountLineGroup(entry);
        return first;
    }
}
