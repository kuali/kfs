/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapSize;

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.SubObjCdGlobal;
import org.kuali.module.chart.bo.SubObjCdGlobalDetail;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class SubObjCdGlobalRuleTest extends ChartRuleTestBase {
    private class SOCDocument {
        private class ChartCode {
            private static final String GOOD1 = "BL";
            private static final String BAD1 = "ZZ";
        }

        private class FiscalYear {
            private static final int GOOD1 = 2006;
            private static final int BAD1 = 0;

        }
    }

    public void testDefaultExistenceChecks_Chart_KnownGood() {

        // create new SubObjCdGlobal
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();

        socChangeDocument.setChartOfAccountsCode(SOCDocument.ChartCode.GOOD1);
        testDefaultExistenceCheck(socChangeDocument, "chartOfAccountsCode", false);
        assertGlobalErrorMapEmpty();
    }

    public void testDefaultExistenceChecks_Chart_KnownBad() {

        // create new SubObjCdGlobal
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();

        socChangeDocument.setChartOfAccountsCode(SOCDocument.ChartCode.BAD1);

        // run the test
        testDefaultExistenceCheck(socChangeDocument, "chartOfAccountsCode", true);
        assertGlobalErrorMapSize(1);
    }

    public void testDefaultExistenceChecks_FiscalYear_KnownGood() {

        // create new SubObjCdGlobal
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();

        socChangeDocument.setUniversityFiscalYear(SOCDocument.FiscalYear.GOOD1);
        testDefaultExistenceCheck(socChangeDocument, "universityFiscalYear", false);
        assertGlobalErrorMapEmpty();
    }

    public void testDefaultExistenceChecks_FiscalYear_KnownBad() {

        // create new SubObjCdGlobal
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();

        socChangeDocument.setUniversityFiscalYear(SOCDocument.FiscalYear.BAD1);

        // run the test
        testDefaultExistenceCheck(socChangeDocument, "universityFiscalYear", true);
        assertGlobalErrorMapSize(1);
    }

    public void testSubObjCdGlobalDetail_InDocument() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkForSubObjCdGlobalDetails(socChangeDocument.getSubObjCdGlobalDetails());

        assertTrue("Rule should pass", result);
    }

    public void testSubObjCdGlobalDetail_NotInDocument() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkForSubObjCdGlobalDetails(socChangeDocument.getSubObjCdGlobalDetails());

        assertFalse("Rule should not pass", result);
    }

    public void testAccountGlobalDetail_InDocument() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

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
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

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
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkFiscalYear(socChangeDocument, socChangeDetail, 0, false);
        assertTrue("Rules should pass", result);

    }

    public void testFiscalYear_NotSameOnDocAndDetail() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2005);
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);
        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkFiscalYear(socChangeDocument, socChangeDetail, 0, false);
        assertFalse(result);
    }

    public void testFiscalYear_SameOnDocAndDetailMultipleLines() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();
        SubObjCdGlobalDetail socChangeDetail2 = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2006);
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkFiscalYearAllLines(socChangeDocument);
        assertTrue("Rules should pass", result);

    }

    public void testFiscalYear_NotSameOnDocAndDetailMultipleLines() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();
        SubObjCdGlobalDetail socChangeDetail2 = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2005);
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
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
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartOnSubObjCodeDetails(socChangeDocument, socChangeDetail, 0, false);
        assertTrue("Rules should pass", result);
    }

    public void testChart_SameOnDocAndAcctDetail() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("BL");
        acctDetail.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
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
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("UA");
        socChangeDetail.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
        socDetails.add(socChangeDetail);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartOnSubObjCodeDetails(socChangeDocument, socChangeDetail, 0, false);
        assertFalse("Rules should not pass", result);

    }

    public void testChart_NotSameOnDocAndAcctDetail() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        AccountGlobalDetail acctDetail = new AccountGlobalDetail();
        acctDetail.setChartOfAccountsCode("UA");
        acctDetail.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
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
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();
        SubObjCdGlobalDetail socChangeDetail2 = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2006);
        socChangeDetail2.setChartOfAccountsCode("BL");
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
        socDetails.add(socChangeDetail);
        socDetails.add(socChangeDetail2);

        socChangeDocument.setSubObjCdGlobalDetails(socDetails);

        SubObjCdGlobalRule rule = new SubObjCdGlobalRule();
        result = rule.checkChartAllLines(socChangeDocument);
        assertTrue("Rules should pass", result);

    }

    public void testChart_NotSameOnDocAndDetailMultipleLines() {
        boolean result;
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();
        SubObjCdGlobalDetail socChangeDetail2 = new SubObjCdGlobalDetail();

        socChangeDocument.setUniversityFiscalYear(2006);
        socChangeDocument.setChartOfAccountsCode("BL");
        socChangeDocument.refreshNonUpdateableReferences();

        socChangeDetail.setUniversityFiscalYear(2006);
        socChangeDetail.setChartOfAccountsCode("BL");
        socChangeDetail.refreshNonUpdateableReferences();

        socChangeDetail2.setUniversityFiscalYear(2006);
        socChangeDetail2.setChartOfAccountsCode("UA");
        socChangeDetail2.refreshNonUpdateableReferences();

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
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
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();
        SubObjCdGlobalDetail socChangeDetail2 = new SubObjCdGlobalDetail();

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

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
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
        SubObjCdGlobal socChangeDocument = new SubObjCdGlobal();
        SubObjCdGlobalDetail socChangeDetail = new SubObjCdGlobalDetail();
        SubObjCdGlobalDetail socChangeDetail2 = new SubObjCdGlobalDetail();

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

        List<SubObjCdGlobalDetail> socDetails = new ArrayList<SubObjCdGlobalDetail>();
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
