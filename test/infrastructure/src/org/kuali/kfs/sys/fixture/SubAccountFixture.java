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
package org.kuali.test.fixtures;

import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.SubAccount;

public enum SubAccountFixture {
    ACTIVE_SUB_ACCOUNT(null, null, null, null, true, null, null, null), INACTIVE_SUB_ACCOUNT(null, null, null, null, false, null, null, null), VALID_SUB_ACCOUNT("BA", "6044900", "ARREC"), INVALID_SUB_ACCOUNT("ZZ", "0000000", "A"), SUB_ACCOUNT_WITH_REPORTS_TO_ORGANIZATION("EA", "2367527", "SOCHR", null, true, "EA", "SACT", null), // another
                                                                                                                                                                                                                                                                                                                                            // sub
                                                                                                                                                                                                                                                                                                                                            // acccount
                                                                                                                                                                                                                                                                                                                                            // value
                                                                                                                                                                                                                                                                                                                                            // is
                                                                                                                                                                                                                                                                                                                                            // SOCON
    SUB_ACCOUNT_WITHOUT_REPORTS_TO_ORGANIZATION("BL", "1031400", "BLDG"), SUB_ACCOUNT_WITH_BAD_CG_FUND_GROUP("BL", "2220090", "12345", "A New SubAccount", true, null, null, null), A21_SUB_ACCOUNT_WITH_BAD_CG_ACCOUNT_TYPE("BL", "4831497", "12345", "A New SubAccount", true, null, null, null, "ZZ", null, null, null, null, false, null, null, null), ;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String subAccountName;
    private boolean subAccountActiveIndicator;
    private String financialReportChartCode;
    private String finReportOrganizationCode;
    private String financialReportingCode;

    private A21SubAccount a21;

    /**
     * Constructs a SubAccountFixture.java.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     */
    private SubAccountFixture(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Constructs a SubAccountFixture.java in order to create a new Sub Account that includes an A21 Sub Account
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param subAccountActiveIndicator
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     */
    private SubAccountFixture(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountName, boolean subAccountActiveIndicator, String finReportChartCode, String finReportOrgCode, String finReportingCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
        this.subAccountName = subAccountName;
        this.subAccountActiveIndicator = subAccountActiveIndicator;
        this.financialReportChartCode = finReportChartCode;
        this.finReportOrganizationCode = finReportOrgCode;
        this.financialReportingCode = finReportingCode;
    }

    /**
     * Constructs a SubAccountFixture.java in order to create a new Sub Account that includes an A21 Sub Account
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param subAccountActiveIndicator
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     * @param subAccountTypeCode
     * @param icrTypeCode
     * @param finSeriesId
     * @param icrChartCode
     * @param icrAccountNumber
     * @param offCampusCode
     * @param costShareChartCode
     * @param costShareAccountNumber
     * @param costShareSubAccountNumber
     */
    private SubAccountFixture(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountName, boolean subAccountActiveIndicator, String finReportChartCode, String finReportOrgCode, String finReportingCode, String subAccountTypeCode, String icrTypeCode, String finSeriesId, String icrChartCode, String icrAccountNumber, boolean offCampusCode, String costShareChartCode, String costShareAccountNumber, String costShareSubAccountNumber) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
        this.subAccountName = subAccountName;
        this.subAccountActiveIndicator = subAccountActiveIndicator;
        this.financialReportChartCode = finReportChartCode;
        this.finReportOrganizationCode = finReportOrgCode;
        this.financialReportingCode = finReportingCode;

        a21 = new A21SubAccount();
        a21.setChartOfAccountsCode(chartOfAccountsCode);
        a21.setAccountNumber(accountNumber);
        a21.setSubAccountTypeCode(subAccountTypeCode);
        a21.setIndirectCostRecoveryTypeCode(icrTypeCode);
        a21.setFinancialIcrSeriesIdentifier(finSeriesId);
        a21.setIndirectCostRecoveryChartOfAccountsCode(icrChartCode);
        a21.setIndirectCostRecoveryAccountNumber(icrAccountNumber);
        a21.setOffCampusCode(offCampusCode);
        a21.setCostShareChartOfAccountCode(costShareChartCode);
        a21.setCostShareSourceAccountNumber(costShareAccountNumber);
        a21.setCostShareSourceSubAccountNumber(costShareSubAccountNumber);

    }

    public SubAccount createSubAccount() {
        SubAccount subAccount = new SubAccount();
        subAccount.setChartOfAccountsCode(chartOfAccountsCode);
        subAccount.setAccountNumber(accountNumber);
        subAccount.setSubAccountNumber(subAccountNumber);
        subAccount.setSubAccountName(subAccountName);
        subAccount.setSubAccountActiveIndicator(subAccountActiveIndicator);
        subAccount.setFinancialReportChartCode(financialReportChartCode);
        subAccount.setFinReportOrganizationCode(finReportOrganizationCode);
        subAccount.setFinancialReportingCode(financialReportingCode);
        subAccount.setA21SubAccount(a21);
        return subAccount;
    }

}
