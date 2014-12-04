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

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PurApAccountLineGroup;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class AccountLineGroupTest extends KualiTestBase {

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testEquals() throws Exception {
        // test reflexive
        GlAccountLineGroup first = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", "--------", "01", "1001", null, "D");
        GlAccountLineGroup second = createAccountLineGroup(2008, "BL", "BL002323", "------", "7000", null, "01", "1001", null, "C");
        PurApAccountLineGroup third = createPurApAccountLineGroup(2008, "BL", "BL002323", "---", "7000", null, "01", "1001", "----", new KualiDecimal(100), PaymentRequestAccountRevision.class);
        // equals all
        assertTrue(first.equals(first));
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
        assertTrue(second.equals(third));
        assertTrue(third.equals(second));
        assertTrue(first.equals(third));
        assertTrue(third.equals(first));

        // first and third equals
        first = createAccountLineGroup(2008, "BA", "BL002323", "--", "7000", null, "01", "1001", null, "C");
        second = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, "D");
        third = createPurApAccountLineGroup(2008, "BA", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(100), PaymentRequestAccountRevision.class);

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
        assertFalse(second.equals(third));
        assertFalse(third.equals(second));
        assertTrue(first.equals(third));
        assertTrue(third.equals(first));

        first = createAccountLineGroup(2008, "BA", "BL002323", "X", "7000", null, "01", "1001", null, "C");
        second = createAccountLineGroup(2008, "BA", "BL002323", "XXX", "7000", null, "01", "1001", null, "D");
        third = createPurApAccountLineGroup(2008, "BL", "BL002323", "X", "7001", null, "01", "1001", null, new KualiDecimal(100), PaymentRequestAccountRevision.class);

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
        GlAccountLineGroup third = createAccountLineGroup(2008, "BL", new String("BL002323"), "", "7000", "12121", "01", "1001", "A", "D");
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


    private PurApAccountLineGroup createPurApAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, KualiDecimal amount, Class<? extends PurApAccountingLineBase> clazz) {
        PurApAccountingLineBase entry = createEntry(i, chartCode, acctNum, subAcctNum, objCd, subObjCd, fiscalPrd, docNum, refDocNum, amount, clazz);
        PurApAccountLineGroup first = new PurApAccountLineGroupTest.PurApAccountLineGroupTestable(entry, docNum, refDocNum);
        return first;
    }

    private PurApAccountingLineBase createEntry(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, KualiDecimal amount, Class<? extends PurApAccountingLineBase> clazz) {
        PurApAccountingLineBase entry = null;
        try {
            entry = (PurApAccountingLineBase) clazz.newInstance();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        entry.setPostingYear(i);
        entry.setChartOfAccountsCode(chartCode);
        entry.setAccountNumber(acctNum);
        entry.setSubAccountNumber(subAcctNum);
        entry.setFinancialObjectCode(objCd);
        entry.setFinancialSubObjectCode(subObjCd);
        entry.setPostingPeriodCode(fiscalPrd);
        entry.setDocumentNumber(docNum);
        entry.setAmount(amount);
        return entry;
    }
}
