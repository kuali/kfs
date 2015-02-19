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
package org.kuali.kfs.sys.businessobject;

import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext
public class AccountingLineBaseTest extends KualiTestBase {

    public final void testIsLike_sourceVsTarget() {
        TargetAccountingLine t1 = new TargetAccountingLine();
        SourceAccountingLine s1 = new SourceAccountingLine();

        assertFalse(t1.isLike(s1));
    }


    public final void testIsLike_emptySource() {
        SourceAccountingLine s1 = new SourceAccountingLine();
        SourceAccountingLine s2 = new SourceAccountingLine();

        assertTrue(s1.isLike(s2));
    }

    public final void testIsLike_emptyTarget() {
        TargetAccountingLine t1 = new TargetAccountingLine();
        TargetAccountingLine t2 = new TargetAccountingLine();

        assertTrue(t1.isLike(t2));
    }


    public final void testIsLike_nullSource() {
        SourceAccountingLine s1 = new SourceAccountingLine();
        SourceAccountingLine s2 = null;

        assertFalse(s1.isLike(s2));
    }

    public final void testIsLike_nullTarget() {
        TargetAccountingLine t1 = new TargetAccountingLine();
        TargetAccountingLine t2 = null;

        assertFalse(t1.isLike(t2));
    }

    @SuppressWarnings("deprecation")
    public final void testIsLike_differentSource_irrelevantDifferences() throws Exception {
        // create identical source lines
        SourceAccountingLine s1 = buildSourceLine();
        SourceAccountingLine s2 = buildSourceLine();

        // add different irrelevant data
        Account a1 = new Account();
        a1.setAccountNumber("a1");
        s1.setAccount(a1);

        Account a2 = new Account();
        a2.setAccountNumber("a2");
        s2.setAccount(a2);

        // compare relevant fields
        assertTrue(s1.isLike(s2));
    }

    @SuppressWarnings("deprecation")
    public final void testIsLike_differentTarget_irrelevantDifferences() throws Exception {
        TargetAccountingLine t1 = buildTargetLine();
        TargetAccountingLine t2 = buildTargetLine();

        // add different irrelevant data
        Chart c1 = new Chart();
        c1.setChartOfAccountsCode("c1");
        t1.setChart(c1);

        Chart c2 = new Chart();
        c2.setChartOfAccountsCode("c2");
        t2.setChart(c2);

        // compare relevant fields
        assertTrue(t1.isLike(t2));
    }


    public final void testIsLike_differentSource_relevantDifferences() throws Exception {
        SourceAccountingLine s1 = buildSourceLine();
        SourceAccountingLine s2 = buildSourceLine();

        // add relevant differences
        s1.setAccountNumber("a1");
        s2.setAccountNumber("a2");

        // compare relevant fields
        assertFalse(s1.isLike(s2));
    }

    public final void testIsLike_differentTarget_relevantDifferences() throws Exception {
        TargetAccountingLine t1 = buildTargetLine();
        TargetAccountingLine t2 = buildTargetLine();

        // add relevant differences
        t1.setDocumentNumber("f1");
        t2.setDocumentNumber("f2");

        // compare relevant fields
        assertFalse(t1.isLike(t2));
    }


    public final void testIsLike_identicalSource() throws Exception {
        SourceAccountingLine s1 = buildSourceLine();
        SourceAccountingLine s2 = buildSourceLine();

        assertTrue(s1.isLike(s2));
    }

    public final void testIsLike_identicalTarget() throws Exception {
        TargetAccountingLine t1 = buildTargetLine();
        TargetAccountingLine t2 = buildTargetLine();

        assertTrue(t1.isLike(t2));
    }


    public final void testCopyFrom_null() throws Exception {
        boolean failedAsExpected = false;

        SourceAccountingLine s1 = buildSourceLine();

        try {
            s1.copyFrom(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testCopyFrom_differentAccountingLine() throws Exception {
        SourceAccountingLine s1 = buildSourceLine();
        s1.setOrganizationReferenceId("A");
        SourceAccountingLine s2 = (SourceAccountingLine) ObjectUtils.deepCopy(s1);
        s2.setOrganizationReferenceId("B");

        assertEquals("A", s1.getOrganizationReferenceId());
        s1.copyFrom(s2);
        assertEquals("B", s1.getOrganizationReferenceId());
        assertTrue(s1.isLike(s2));
    }


    private SourceAccountingLine buildSourceLine() throws InstantiationException, IllegalAccessException {
        return LINE.createSourceAccountingLine();
    }

    private TargetAccountingLine buildTargetLine() throws InstantiationException, IllegalAccessException {
        return LINE.createTargetAccountingLine();
    }
}
