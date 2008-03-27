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
package org.kuali.module.effort.rules;

import java.util.List;
import java.util.Properties;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.Message;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.testdata.EffortTestDataPropertyConstants;
import org.kuali.module.integration.bo.LaborLedgerBalance;
import org.kuali.module.integration.service.LaborModuleService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

@ConfigureContext
public class LedgerBalanceFieldValidatorTest extends KualiTestBase {

    private Properties properties, message;
    private String balanceFieldNames;
    private String deliminator;
    Integer postingYear;

    private BusinessObjectService businessObjectService;
    private PersistenceService persistenceService;
    private LaborModuleService laborModuleService;

    private Class<? extends LaborLedgerBalance> ledgerBalanceClass;

    /**
     * Constructs a LedgerBalanceFieldValidatorTest.java.
     */
    public LedgerBalanceFieldValidatorTest() {
        super();
        String messageFileName = "org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "org/kuali/module/effort/testdata/ledgerBalanceFieldValidator.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);
        balanceFieldNames = properties.getProperty(EffortTestDataPropertyConstants.BALANCE_FIELD_NAMES);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        ledgerBalanceClass = laborModuleService.getLaborLedgerBalanceClass();

        TestDataPreparator.doCleanUpWithoutReference(ledgerBalanceClass, properties, EffortTestDataPropertyConstants.DATA_CLEANUP, balanceFieldNames, deliminator);
    }

    public void testHasValidAccount_valid() throws Exception {
        String testTarget = "hasValidAccount.valid.";
        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.hasValidAccount(ledgerBalance);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasValidAccount.valid");
        assertTrue(errorMessage, validationMessage == null);
    }

    public void testHasValidAccount_invalid() throws Exception {
        String testTarget = "hasValidAccount.invalid.";

        // the test is disable because the account in the test data volates an integrity constraint foreign and the test data cannot
        // be stored into database
        // LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        // Message validationMessage = LedgerBalanceFieldValidator.hasValidAccount(ledgerBalance);
        // String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasValidAccount.invalid");
        // assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsInFundGroups_Contain() throws Exception {
        String testTarget = "isInFundGroups.contain.";
        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInFundGroups.contain");
        assertTrue(errorMessage, validationMessage == null);
    }

    public void testIsInFundGroups_NotContain() throws Exception {
        String testTarget = "isInFundGroups.notContain.";
        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInFundGroups.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsInSubFundGroups_Contain() throws Exception {
        String testTarget = "isInSubFundGroups.contain.";
        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.SUB_FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInSubFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInSubFundGroups.contain");
        assertTrue(errorMessage, validationMessage == null);
    }

    public void testIsInSubFundGroups_NotContain() throws Exception {
        String testTarget = "isInSubFundGroups.notContain.";
        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.SUB_FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInSubFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInSubFundGroups.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsNonZeroAmountBalanceWithinReportPeriod_IsNonZeroAmount() throws Exception {
        String testTarget = "isNonZeroAmountBalanceWithinReportPeriod.isNonZeroAmount.";
        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod(ledgerBalance, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod.isNonZeroAmount");
        assertTrue(errorMessage, validationMessage == null);
    }

    public void testIsNonZeroAmountBalanceWithinReportPeriod_IsZeroAmount() throws Exception {
        String testTarget = "isNonZeroAmountBalanceWithinReportPeriod.isZeroAmount.";
        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod(ledgerBalance, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod.isZeroAmount");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsTotalAmountPositive_IsPositive() throws Exception {
        String testTarget = "isTotalAmountPositive.isPositive.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isTotalAmountPositive.isPositive");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testIsTotalAmountPositive_IsZero() throws Exception {
        String testTarget = "isTotalAmountPositive.isZero.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isTotalAmountPositive.isZero");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsTotalAmountPositive_IsNegative() throws Exception {
        String testTarget = "isTotalAmountPositive.isNegative.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isTotalAmountPositive.isNegative");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testHasGrantAccount_ByFundGroup_Contain() throws Exception {
        String testTarget = "hasGrantAccount.byFundGroup.contain.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.byFundGroup.contain");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasGrantAccount_ByFundGroup_NotContain() throws Exception {
        String testTarget = "hasGrantAccount.byFundGroup.notContain.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.byFundGroup.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testHasGrantAccount_BySubFundGroup_Contain() throws Exception {
        String testTarget = "hasGrantAccount.bySubFundGroup.contain.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.SUB_FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.bySubFundGroup.contain");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasGrantAccount_BySubFundGroup_NotContain() throws Exception {
        String testTarget = "hasGrantAccount.bySubFundGroup.notContain.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.SUB_FUND_GROUPS), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.bySubFundGroup.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsFromSingleOrganization_Single() throws Exception {
        String testTarget = "isFromSingleOrganization.single.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isFromSingleOrganization(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isFromSingleOrganization.single");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testIsFromSingleOrganization_Multiple() throws Exception {
        String testTarget = "isFromSingleOrganization.multiple.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isFromSingleOrganization(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isFromSingleOrganization.multiple");
        assertTrue(errorMessage, validationMessage != null);
    }

    public void testHasFederalFunds_FederalFunds() throws Exception {
        String testTarget = "hasFederalFunds.federalFunds.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> federalAgencyTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FEDERAL_AGENCY_TYPE_CODES), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasFederalFunds(ledgerBalances, federalAgencyTypeCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasFederalFunds.federalFunds");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasFederalFunds_PassThrough() throws Exception {
        String testTarget = "hasFederalFunds.passThrough.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> federalAgencyTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FEDERAL_AGENCY_TYPE_CODES), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasFederalFunds(ledgerBalances, federalAgencyTypeCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasFederalFunds.passThrough");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasFederalFunds_NoFederalFunds() throws Exception {
        String testTarget = "hasFederalFunds.noFederalFunds.";
        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> federalAgencyTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FEDERAL_AGENCY_TYPE_CODES), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasFederalFunds(ledgerBalances, federalAgencyTypeCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasFederalFunds.noFederalFunds");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    /**
     * construct a ledger balance and persist it
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a ledger balance
     */
    private LaborLedgerBalance buildLedgerBalance(String testTarget) {
        LaborLedgerBalance ledgerBalance = TestDataPreparator.buildTestDataObject(ledgerBalanceClass, properties, testTarget + EffortTestDataPropertyConstants.INPUT_BALANCE, balanceFieldNames, deliminator);
        businessObjectService.save(ledgerBalance);
        persistenceService.retrieveNonKeyFields(ledgerBalance);

        return ledgerBalance;
    }

    /**
     * construct a list of ledger balances and persist them
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a list of ledger balances
     */
    private List<LaborLedgerBalance> buildLedgerBalances(String testTarget) {
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_DATA));

        List<LaborLedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(ledgerBalanceClass, properties, testTarget + EffortTestDataPropertyConstants.INPUT_BALANCE, balanceFieldNames, deliminator, numberOfTestData);
        businessObjectService.save(ledgerBalances);
        for (LaborLedgerBalance balance : ledgerBalances) {
            persistenceService.retrieveNonKeyFields(balance);
        }

        return ledgerBalances;
    }

    /**
     * build a report defintion object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a report defintion object
     */
    private EffortCertificationReportDefinition buildReportDefinition(String testTarget) {
        EffortCertificationReportDefinition reportDefinition = new EffortCertificationReportDefinition();
        String reprtDefinitionFieldNames = properties.getProperty(EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_NAMES);
        ObjectUtil.populateBusinessObject(reportDefinition, properties, testTarget + EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_VALUES, reprtDefinitionFieldNames, deliminator);

        return reportDefinition;
    }
}
