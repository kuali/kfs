/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapEmpty;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapSize;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.businessobject.AccountGlobalDetail;
import org.kuali.kfs.coa.businessobject.SubObjectCodeGlobal;
import org.kuali.kfs.coa.businessobject.SubObjectCodeGlobalDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.TestUtils;

@ConfigureContext
public class SubObjCdGlobalRuleTest extends ChartRuleTestBase {
    private static class SOCDocument {
        private class ChartCode {
            private static final String GOOD1 = "BL";
            private static final String BAD1 = "ZZ";
        }

        private static class FiscalYear {
            public static int getFiscalYear_GOOD1() {
                return TestUtils.getFiscalYearForTesting().intValue();
            }
            
            public static int getFiscalYear_BAD1() {
                return 0;
            }
        }
    }

    public void testDefaultExistenceChecks_Chart_KnownGood() {

        // create new SubObjCdGlobal
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();

        socChangeDocument.setChartOfAccountsCode(SOCDocument.ChartCode.GOOD1);
        testDefaultExistenceCheck(socChangeDocument, "chartOfAccountsCode", false);
        assertGlobalMessageMapEmpty();
    }

    public void testDefaultExistenceChecks_Chart_KnownBad() {

        // create new SubObjCdGlobal
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();

        socChangeDocument.setChartOfAccountsCode(SOCDocument.ChartCode.BAD1);

        // run the test
        testDefaultExistenceCheck(socChangeDocument, "chartOfAccountsCode", true);
        assertGlobalMessageMapSize(1);
    }

    public void testDefaultExistenceChecks_FiscalYear_KnownGood() {

        // create new SubObjCdGlobal
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();

        socChangeDocument.setUniversityFiscalYear(SOCDocument.FiscalYear.getFiscalYear_GOOD1());
        testDefaultExistenceCheck(socChangeDocument, "universityFiscalYear", false);
        assertGlobalMessageMapEmpty();
    }

    public void testDefaultExistenceChecks_FiscalYear_KnownBad() {

        // create new SubObjCdGlobal
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();

        socChangeDocument.setUniversityFiscalYear(SOCDocument.FiscalYear.getFiscalYear_BAD1());

        // run the test
        testDefaultExistenceCheck(socChangeDocument, "universityFiscalYear", true);
        assertGlobalMessageMapSize(1);
    }

    public void testSubObjCdGlobalDetail_InDocument() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkForSubObjCdGlobalDetails(socChangeDocument.getSubObjCdGlobalDetails());

        assertTrue("Rule should pass", result);
    }

    public void testSubObjCdGlobalDetail_NotInDocument() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkForSubObjCdGlobalDetails(socChangeDocument.getSubObjCdGlobalDetails());

        assertFalse("Rule should not pass", result);
    }

    public void testAccountGlobalDetail_InDocument() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("BL");
        acctDetail.refreshNonUpdateableReferences();

        List<AccountGlobalDetail> acctDetails = new ArrayList<AccountGlobalDetail>();

        acctDetails.add(acctDetail);

        socChangeDocument.setAccountGlobalDetails(acctDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkForAccountGlobalDetails(socChangeDocument.getAccountGlobalDetails());

        assertTrue("Rule should pass", result);
    }

    public void testAccountGlobalDetail_NotInDocument() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("BL");
        acctDetail.refreshNonUpdateableReferences();

        List<AccountGlobalDetail> acctDetails = new ArrayList<AccountGlobalDetail>();

        socChangeDocument.setAccountGlobalDetails(acctDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkForAccountGlobalDetails(socChangeDocument.getAccountGlobalDetails());

        assertFalse("Rule should not pass", result);
    }

    public void testFiscalYear_SameOnDocAndDetail() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkFiscalYear(socChangeDocument, socChangeDetail, 0, false);
        assertTrue("Rules should pass", result);

    }

    public void testFiscalYear_NotSameOnDocAndDetail() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting()-1);
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkFiscalYear(socChangeDocument, socChangeDetail, 0, false);
        assertFalse(result);
    }

    public void testFiscalYear_SameOnDocAndDetailMultipleLines() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();
        SubObjectCodeGlobalDetail socChangeDetail2 = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkFiscalYearAllLines(socChangeDocument);
        assertTrue("Rules should pass", result);

    }

    public void testFiscalYear_NotSameOnDocAndDetailMultipleLines() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();
        SubObjectCodeGlobalDetail socChangeDetail2 = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting()-1);
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkFiscalYearAllLines(socChangeDocument);
        assertFalse(result);
    }

    public void testChart_SameOnDocAndDetail() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartOnSubObjCodeDetails(socChangeDocument, socChangeDetail, 0, false);
        assertTrue("Rules should pass", result);
    }

    public void testChart_SameOnDocAndAcctDetail() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("BL");
        acctDetail.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        List<AccountGlobalDetail> acctDetails = new ArrayList<AccountGlobalDetail>();

        socDetails.add(socChangeDetail);
        acctDetails.add(acctDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        socChangeDocument.setAccountGlobalDetails(acctDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartOnAccountDetails(socChangeDocument, acctDetail, 0, false);
        assertTrue("Rules should pass", result);
    }

    public void testChart_NotSameOnDocAndDetail() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("UA");
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartOnSubObjCodeDetails(socChangeDocument, socChangeDetail, 0, false);
        assertFalse("Rules should not pass", result);

    }

    public void testChart_NotSameOnDocAndAcctDetail() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("UA");
        acctDetail.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        List<AccountGlobalDetail> acctDetails = new ArrayList<AccountGlobalDetail>();

        socDetails.add(socChangeDetail);
        acctDetails.add(acctDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        socChangeDocument.setAccountGlobalDetails(acctDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartOnAccountDetails(socChangeDocument, acctDetail, 0, false);
        assertFalse("Rules should not pass", result);
    }

    public void testChart_SameOnDocAndDetailMultipleLines() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();
        SubObjectCodeGlobalDetail socChangeDetail2 = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2006);
        socChangeDetail2.setChartOfAccountsCode("BL");
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartAllLines(socChangeDocument);
        assertTrue("Rules should pass", result);

    }

    public void testChart_NotSameOnDocAndDetailMultipleLines() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();
        SubObjectCodeGlobalDetail socChangeDetail2 = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2006);
        socChangeDetail2.setChartOfAccountsCode("UA");
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartAllLines(socChangeDocument);
        assertFalse(result);
    }

    public void testChart_SameOnDocAndAcctDetailMultipleLines() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();
        SubObjectCodeGlobalDetail socChangeDetail2 = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2006);
        socChangeDetail2.setChartOfAccountsCode("BL");
        socChangeDetail2.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("BL");
        acctDetail.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail2 = new AccountGlobalDetail();
        acctDetail2.setChartOfAccountsCode("BL");
        acctDetail2.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        List<AccountGlobalDetail> acctDetails = new ArrayList<AccountGlobalDetail>();

        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        acctDetails.add(acctDetail);
        acctDetails.add(acctDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        socChangeDocument.setAccountGlobalDetails(acctDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartAllLines(socChangeDocument);
        assertTrue("Rules should pass", result);

    }

    public void testChart_NotSameOnDocAndAcctDetailMultipleLines() {
        boolean result;
        SubObjectCodeGlobal socChangeDocument = new SubObjectCodeGlobal();
        SubObjectCodeGlobalDetail socChangeDetail = new SubObjectCodeGlobalDetail();
        SubObjectCodeGlobalDetail socChangeDetail2 = new SubObjectCodeGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2006);
        socChangeDetail2.setChartOfAccountsCode("BL");
        socChangeDetail2.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("UA");
        acctDetail.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail2 = new AccountGlobalDetail();
        acctDetail2.setChartOfAccountsCode("BL");
        acctDetail2.refreshNonUpdateableReferences();

        List<SubObjectCodeGlobalDetail> socDetails = new ArrayList<SubObjectCodeGlobalDetail>();
        List<AccountGlobalDetail> acctDetails = new ArrayList<AccountGlobalDetail>();

        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        acctDetails.add(acctDetail);
        acctDetails.add(acctDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        socChangeDocument.setAccountGlobalDetails(acctDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartAllLines(socChangeDocument);
        assertFalse(result);
    }

}
