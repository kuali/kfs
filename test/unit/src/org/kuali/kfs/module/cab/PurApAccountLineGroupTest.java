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

import org.kuali.kfs.module.cab.businessobject.PurApAccountLineGroup;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PurApAccountLineGroupTest extends KualiTestBase {
    public static class PurApAccountLineGroupTestable extends PurApAccountLineGroup {
        public PurApAccountLineGroupTestable(PurApAccountingLineBase entry, String docNum, String refDocNum) {
            setDocumentNumber(docNum);
            setReferenceFinancialDocumentNumber(refDocNum);
            setUniversityFiscalYear(entry.getPostingYear());
            setUniversityFiscalPeriodCode(entry.getPostingPeriodCode());
            setChartOfAccountsCode(entry.getChartOfAccountsCode());
            setAccountNumber(entry.getAccountNumber());
            setSubAccountNumber(entry.getSubAccountNumber());
            setFinancialObjectCode(entry.getFinancialObjectCode());
            setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
            getSourceEntries().add(entry);
            if (CreditMemoAccountRevision.class.isAssignableFrom(entry.getClass())) {
                setAmount(entry.getAmount().negated());
            }
            else {
                setAmount(entry.getAmount());
            }
        }
    }

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCombineEntry_PREQ() throws Exception {
        PurApAccountLineGroup group = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(100), PaymentRequestAccountRevision.class);
        PurApAccountingLineBase first = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(200), PaymentRequestAccountRevision.class);
        assertTrue(group.equals(new PurApAccountLineGroupTestable(first, "1001", null)));
        group.combineEntry(first);
        assertEquals(new KualiDecimal(300), group.getAmount());
        PurApAccountingLineBase second = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(-100), PaymentRequestAccountRevision.class);
        assertTrue(group.equals(new PurApAccountLineGroupTestable(second, "1001", null)));
        group.combineEntry(second);
        assertEquals(new KualiDecimal(200), group.getAmount());

        PurApAccountingLineBase third = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(-200), PaymentRequestAccountRevision.class);
        assertTrue(group.equals(new PurApAccountLineGroupTestable(third, "1001", null)));
        group.combineEntry(third);
        assertEquals(new KualiDecimal(0), group.getAmount());

        List<PurApAccountingLineBase> sourceEntries = group.getSourceEntries();
        assertEquals(4, sourceEntries.size());
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (PurApAccountingLineBase entry : sourceEntries) {
            totalAmount = totalAmount.add(entry.getAmount());
        }
        assertEquals(group.getAmount(), totalAmount);
    }

    public void testCombineEntry_CM() throws Exception {
        PurApAccountLineGroup group = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(100), CreditMemoAccountRevision.class);
        PurApAccountingLineBase first = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(200), CreditMemoAccountRevision.class);
        assertTrue(group.equals(new PurApAccountLineGroupTestable(first, "1001", null)));
        group.combineEntry(first);
        assertEquals(new KualiDecimal(-300), group.getAmount());
        PurApAccountingLineBase second = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(-100), CreditMemoAccountRevision.class);
        assertTrue(group.equals(new PurApAccountLineGroupTestable(second, "1001", null)));
        group.combineEntry(second);
        assertEquals(new KualiDecimal(-200), group.getAmount());

        PurApAccountingLineBase third = createEntry(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null, new KualiDecimal(-200), CreditMemoAccountRevision.class);
        assertTrue(group.equals(new PurApAccountLineGroupTestable(third, "1001", null)));
        group.combineEntry(third);
        assertEquals(new KualiDecimal(0), group.getAmount());

        List<PurApAccountingLineBase> sourceEntries = group.getSourceEntries();
        assertEquals(4, sourceEntries.size());
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (PurApAccountingLineBase entry : sourceEntries) {
            totalAmount = totalAmount.add(entry.getAmount());
        }
        assertEquals(group.getAmount(), totalAmount);
    }

    private PurApAccountLineGroup createAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, KualiDecimal amount, Class<? extends PurApAccountingLineBase> clazz) {
        PurApAccountingLineBase entry = createEntry(i, chartCode, acctNum, subAcctNum, objCd, subObjCd, fiscalPrd, docNum, refDocNum, amount, clazz);
        PurApAccountLineGroup first = new PurApAccountLineGroupTestable(entry, docNum, refDocNum);
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
