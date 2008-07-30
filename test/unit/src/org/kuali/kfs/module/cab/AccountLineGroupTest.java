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

import org.kuali.kfs.module.cab.businessobject.AccountLineGroup;
import org.kuali.kfs.sys.context.KualiTestBase;

public class AccountLineGroupTest extends KualiTestBase {

    @Override
    protected void setUp() throws Exception {
    }

    public void testEquals() throws Exception {
        // test reflexive
        assertEquals(new AccountLineGroup(), new AccountLineGroup());
        AccountLineGroup first = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null);
        AccountLineGroup second = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null);
        AccountLineGroup third = createAccountLineGroup(2008, "BL", new String("BL002323"), "--", "7000", null, "01", "1001", null);
        assertTrue(first.equals(first));
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
        assertTrue(second.equals(third));
        assertTrue(third.equals(second));
        assertTrue(first.equals(third));
        assertTrue(third.equals(first));

        first = createAccountLineGroup(2008, "BA", "BL002323", "--", "7000", null, "01", "1001", null);
        second = createAccountLineGroup(2008, "BL", "BL002323", "--", "7000", null, "01", "1001", null);
        third = createAccountLineGroup(2008, "BA", "BL002323", "--", "7000", null, "01", "1001", null);

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
        assertFalse(second.equals(third));
        assertFalse(third.equals(second));
        assertTrue(first.equals(third));
        assertTrue(third.equals(first));

        first = createAccountLineGroup(2008, "BA", "BL002323", "X", "7000", null, "01", "1001", null);
        second = createAccountLineGroup(2008, "BA", "BL002323", "XXX", "7000", null, "01", "1001", null);
        third = createAccountLineGroup(2008, "BA", "BL002323", "XXXX", "7000", null, "01", "1001", null);

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
        AccountLineGroup first = createAccountLineGroup(new Integer(2008), new String("BL"), "BL002323", "--", "7000", "12121", "01", "1001", "A");
        AccountLineGroup second = createAccountLineGroup(2008, "BL", "BL002323", "--", new String("7000"), "12121", "01", "1001", "A");
        AccountLineGroup third = createAccountLineGroup(2008, "BL", new String("BL002323"), "--", "7000", "12121", "01", "1001", "A");
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(second.hashCode(), third.hashCode());
        assertEquals(first.hashCode(), third.hashCode());
    }

    private AccountLineGroup createAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum) {
        AccountLineGroup first = new AccountLineGroup();
        first.setUniversityFiscalYear(i);
        first.setChartOfAccountsCode(chartCode);
        first.setAccountNumber(acctNum);
        first.setSubAccountNumber(subAcctNum);
        first.setFinancialObjectCode(objCd);
        first.setFinancialSubObjectCode(subObjCd);
        first.setUniversityFiscalPeriodCode(fiscalPrd);
        first.setDocumentNumber(docNum);
        first.setReferenceFinancialDocumentNumber(refDocNum);
        return first;
    }
}
