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
package org.kuali.kfs.module.ec.service;

import java.util.List;
import java.util.Properties;

import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.PersistenceService;

@ConfigureContext
public class EffortCertificationDetailBuildServiceTest extends KualiTestBase {

    private Properties properties, message;
    private String balanceFieldNames, detailFieldNames;
    private String deliminator;
    Integer postingYear;

    private EffortCertificationReportDefinition reportDefinition;

    private BusinessObjectService businessObjectService;
    private PersistenceService persistenceService;
    private EffortCertificationDetailBuildService effortCertificationDetailBuildService;
    private LaborModuleService laborModuleService;

    private Class<? extends LaborLedgerBalance> ledgerBalanceClass;

    /**
     * Constructs a EffortCertificationDetailBuildServiceTest.java.
     */
    public EffortCertificationDetailBuildServiceTest() {
        super();
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationDetailBuildService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);

        balanceFieldNames = properties.getProperty(EffortTestDataPropertyConstants.BALANCE_FIELD_NAMES);
        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        postingYear = Integer.valueOf(properties.getProperty(EffortTestDataPropertyConstants.POSTING_YEAR));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        effortCertificationDetailBuildService = SpringContext.getBean(EffortCertificationDetailBuildService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        ledgerBalanceClass = LedgerBalance.class;

        TestDataPreparator.doCleanUpWithoutReference(ledgerBalanceClass, properties, EffortTestDataPropertyConstants.DATA_CLEANUP, balanceFieldNames, deliminator);
    }

    /**
     * test if a build detail line can be generated from the specified ledger balance whose sub account is not in sub account table
     */
    public void testGenerateDetailBuild_NullSubAccount() throws Exception {
        String testTarget = "generateDetailBuild.nullSubAccount.";
        reportDefinition = this.buildReportDefinition("");
        this.assertDetailEquals(testTarget);
    }

    /**
     * test if a build detail line can be generated from the specified ledger balance whose sub account is of expense type.
     */
    public void testGenerateDetailBuild_ExpenseSubAccount() throws Exception {
        String testTarget = "generateDetailBuild.expenseSubAccount.";
        reportDefinition = this.buildReportDefinition("");
        this.assertDetailEquals(testTarget);
    }

    /**
     * test if a build detail line can be generated from the specified ledger balance whose sub account is of cost share type.
     */
    public void testGenerateDetailBuild_CostShareSubAccount() throws Exception {
        String testTarget = "generateDetailBuild.costShareSubAccount.";
        reportDefinition = this.buildReportDefinition("");
        this.assertDetailEquals(testTarget);
    }

    /**
     * test if a build detail line can be generated from the specified ledger balance approperitely for the customized long period
     */
    public void testGenerateDetailBuild_LongReportPeriod() throws Exception {
        String testTarget = "generateDetailBuild.longReportPeriod.";
        reportDefinition = this.buildReportDefinition(testTarget);
        this.assertDetailEquals(testTarget);
    }

    /**
     * compare the resulting detail line with the expected
     * 
     * @param testTarget the given test target that specifies the test data being used
     */
    private void assertDetailEquals(String testTarget) {
        List<String> keyFields = ObjectUtil.split(detailFieldNames, deliminator);

        LaborLedgerBalance ledgerBalance = this.buildLedgerBalance(testTarget);

        EffortCertificationDetailBuild detailBuild = effortCertificationDetailBuildService.generateDetailBuild(postingYear, ledgerBalance, reportDefinition);
        System.out.printf("FringeBenefitAmount = %s; PayrollAmount = %s.\n", detailBuild.getFringeBenefitAmount(), detailBuild.getEffortCertificationPayrollAmount());

        EffortCertificationDetailBuild expectedDetailBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDetailBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator);

        String errorMessage = message.getProperty("error.detailBuildService.unexpectedDetailLineGenerated");
        assertTrue(errorMessage, ObjectUtil.equals(detailBuild, expectedDetailBuild, keyFields));
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
