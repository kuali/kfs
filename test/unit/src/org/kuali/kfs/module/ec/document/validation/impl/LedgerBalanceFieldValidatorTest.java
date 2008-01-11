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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class LedgerBalanceFieldValidatorTest extends KualiTestBase {

    private Properties properties, message;
    private String balanceFieldNames;
    private String deliminator;
    Integer postingYear;

    private BusinessObjectService businessObjectService;
    private PersistenceService persistenceService;

    /**
     * Constructs a LedgerBalanceFieldValidatorTest.java.
     */
    public LedgerBalanceFieldValidatorTest() {
        super();
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/ledgerBalanceFieldValidator.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();

        deliminator = properties.getProperty("deliminator");

        balanceFieldNames = properties.getProperty("balanceFieldNames");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        this.doCleanUp();
    }

    public void testHasValidAccount_valid() throws Exception {
        String testTarget = "hasValidAccount.valid.";
        LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);

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
        LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "fundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInFundGroups.contain");
        assertTrue(errorMessage, validationMessage == null);
    }

    public void testIsInFundGroups_NotContain() throws Exception {
        String testTarget = "isInFundGroups.notContain.";
        LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "fundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInFundGroups.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsInSubFundGroups_Contain() throws Exception {
        String testTarget = "isInSubFundGroups.contain.";
        LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "subFundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInSubFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInSubFundGroups.contain");
        assertTrue(errorMessage, validationMessage == null);
    }

    public void testIsInSubFundGroups_NotContain() throws Exception {
        String testTarget = "isInSubFundGroups.notContain.";
        LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "subFundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.isInSubFundGroups(ledgerBalance, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isInSubFundGroups.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsNonZeroAmountBalanceWithinReportPeriod_IsNonZeroAmount() throws Exception {
        String testTarget = "isNonZeroAmountBalanceWithinReportPeriod.isNonZeroAmount.";
        LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod(ledgerBalance, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod.isNonZeroAmount");
        assertTrue(errorMessage, validationMessage == null);
    }

    public void testIsNonZeroAmountBalanceWithinReportPeriod_IsZeroAmount() throws Exception {
        String testTarget = "isNonZeroAmountBalanceWithinReportPeriod.isZeroAmount.";
        LedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod(ledgerBalance, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod.isZeroAmount");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsTotalAmountPositive_IsPositive() throws Exception {
        String testTarget = "isTotalAmountPositive.isPositive.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isTotalAmountPositive.isPositive");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testIsTotalAmountPositive_IsZero() throws Exception {
        String testTarget = "isTotalAmountPositive.isZero.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isTotalAmountPositive.isZero");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsTotalAmountPositive_IsNegative() throws Exception {
        String testTarget = "isTotalAmountPositive.isNegative.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportDefinition.getReportPeriods());
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isTotalAmountPositive.isNegative");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testHasGrantAccount_ByFundGroup_Contain() throws Exception {
        String testTarget = "hasGrantAccount.byFundGroup.contain.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "fundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances, true, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.byFundGroup.contain");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasGrantAccount_ByFundGroup_NotContain() throws Exception {
        String testTarget = "hasGrantAccount.byFundGroup.notContain.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "fundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances, true, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.byFundGroup.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testHasGrantAccount_BySubFundGroup_Contain() throws Exception {
        String testTarget = "hasGrantAccount.bySubFundGroup.contain.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "subFundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances, false, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.bySubFundGroup.contain");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasGrantAccount_BySubFundGroup_NotContain() throws Exception {
        String testTarget = "hasGrantAccount.bySubFundGroup.notContain.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> fundGroupCodes = ObjectUtil.split(properties.getProperty(testTarget + "subFundGroups"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances, false, fundGroupCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasGrantAccount.bySubFundGroup.notContain");
        assertTrue(errorMessage.toString(), validationMessage != null);
    }

    public void testIsFromSingleOrganization_Single() throws Exception {
        String testTarget = "isFromSingleOrganization.single.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isFromSingleOrganization(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isFromSingleOrganization.single");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testIsFromSingleOrganization_Multiple() throws Exception {
        String testTarget = "isFromSingleOrganization.multiple.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        Message validationMessage = LedgerBalanceFieldValidator.isFromSingleOrganization(ledgerBalances);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.isFromSingleOrganization.multiple");
        assertTrue(errorMessage, validationMessage != null);
    }

    public void testHasFederalFunds_FederalFunds() throws Exception {
        String testTarget = "hasFederalFunds.federalFunds.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> federalAgencyTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + "federalAgencyTypeCodes"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasFederalFunds(ledgerBalances, federalAgencyTypeCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasFederalFunds.federalFunds");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasFederalFunds_PassThrough() throws Exception {
        String testTarget = "hasFederalFunds.passThrough.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> federalAgencyTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + "federalAgencyTypeCodes"), deliminator);

        Message validationMessage = LedgerBalanceFieldValidator.hasFederalFunds(ledgerBalances, federalAgencyTypeCodes);
        String errorMessage = message.getProperty("error.ledgerBalanceFieldValidator.hasFederalFunds.passThrough");
        assertTrue(errorMessage.toString(), validationMessage == null);
    }

    public void testHasFederalFunds_NoFederalFunds() throws Exception {
        String testTarget = "hasFederalFunds.noFederalFunds.";
        List<LedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);
        List<String> federalAgencyTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + "federalAgencyTypeCodes"), deliminator);

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
    private LedgerBalance buildLedgerBalance(String testTarget) {
        LedgerBalance ledgerBalance = TestDataPreparator.buildTestDataObject(LedgerBalance.class, properties, testTarget + "inputBalance", balanceFieldNames, deliminator);
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
    private List<LedgerBalance> buildLedgerBalances(String testTarget) {
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        List<LedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "inputBalance", balanceFieldNames, deliminator, numberOfTestData);
        businessObjectService.save(ledgerBalances);
        for (LedgerBalance balance : ledgerBalances) {
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
        String reprtDefinitionFieldNames = properties.getProperty("reportDefinitionFieldNames");
        ObjectUtil.populateBusinessObject(reportDefinition, properties, testTarget + "reportDefinitionFieldValues", reprtDefinitionFieldNames, deliminator);

        return reportDefinition;
    }

    /**
     * remove the existing data from the database so that they cannot affact the test results
     */
    private void doCleanUp() throws Exception {
        LedgerBalance cleanup = new LedgerBalance();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", balanceFieldNames, deliminator);
        Map<String, Object> fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(balanceFieldNames, deliminator)));
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }
}
