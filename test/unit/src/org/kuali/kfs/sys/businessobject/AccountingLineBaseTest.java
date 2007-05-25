/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.bo;

import static org.kuali.test.fixtures.AccountingLineFixture.LINE;

import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
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
