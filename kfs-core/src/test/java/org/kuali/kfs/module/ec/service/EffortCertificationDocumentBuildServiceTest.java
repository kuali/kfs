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
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
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
public class EffortCertificationDocumentBuildServiceTest extends KualiTestBase {

    private Properties properties, message;
    private String balanceFieldNames, detailFieldNames, documentFieldNames;
    private String deliminator;
    Integer postingYear;

    private EffortCertificationReportDefinition reportDefinition;

    private BusinessObjectService businessObjectService;
    private PersistenceService persistenceService;
    private EffortCertificationDocumentBuildService effortCertificationDocumentBuildService;
    private LaborModuleService laborModuleService;

    private Class<? extends LaborLedgerBalance> ledgerBalanceClass;

    /**
     * Constructs a EffortCertificationDetailBuildServiceTest.java.
     */
    public EffortCertificationDocumentBuildServiceTest() {
        super();
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationDocumentBuildService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);

        balanceFieldNames = properties.getProperty(EffortTestDataPropertyConstants.BALANCE_FIELD_NAMES);
        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        documentFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_FIELD_NAMES);

        postingYear = Integer.valueOf(properties.getProperty(EffortTestDataPropertyConstants.POSTING_YEAR));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        effortCertificationDocumentBuildService = SpringContext.getBean(EffortCertificationDocumentBuildService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        ledgerBalanceClass = LedgerBalance.class;
        
        TestDataPreparator.doCleanUpWithoutReference(ledgerBalanceClass, properties, EffortTestDataPropertyConstants.DATA_CLEANUP, balanceFieldNames, deliminator);
    }

    /**
     * test if a build document as well as its detail lines can be generated approperiately
     */
    public void testGenerateDocumentBuild() throws Exception {
        String testTarget = "generateDocumentBuild.";
        reportDefinition = this.buildReportDefinition("");
        this.assertDocumentEquals(testTarget, false);
    }

    /**
     * test if the input balances can be grouped and converted to documents correctly
     */
    public void testGenerateDocumentBuildList() throws Exception {
        String testTarget = "generateDocumentBuildList.";
        reportDefinition = this.buildReportDefinition("");
        this.assertDocumentListEquals(testTarget);
    }

    /**
     * test if a build document as well as its detail lines can be generated and stored into database approperiately
     */
    public void testGenerateDocumentBuild_SaveIntoDatabase() throws Exception {
        String testTarget = "generateDocumentBuild.saveIntoDatabase.";
        reportDefinition = this.buildReportDefinition("");

        EffortCertificationReportDefinition existingReportDefinition = (EffortCertificationReportDefinition) businessObjectService.retrieve(reportDefinition);
        if (existingReportDefinition == null) {
            businessObjectService.save(reportDefinition);
        }

        this.assertDocumentEquals(testTarget, true);
    }

    /**
     * test if the percentages of detail lines can be calculated correctly
     */
    public void testGenerateDocumentBuild_PercentageCalculation() throws Exception {
        String testTarget = "generateDocumentBuild.percentageCalculation.";
        reportDefinition = this.buildReportDefinition("");
        this.assertDocumentEquals(testTarget, false);
    }

    /**
     * test if the detail lines can be consolidated when their sub account type codes are not 'CS' (cost shared)
     */
    public void testGenerateDocumentBuild_NonCostShareSubAccountConsolidation() throws Exception {
        String testTarget = "generateDocumentBuild.nonCostShareSubAccountConsolidation.";
        reportDefinition = this.buildReportDefinition("");
        this.assertDocumentEquals(testTarget, false);
    }

    /**
     * compare the resulting detail line with the expected
     * 
     * @param testTarget the given test target that specifies the test data being used
     */
    private void assertDocumentEquals(String testTarget, boolean savedIntoDatabase) {
        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        List<String> detailKeyFields = ObjectUtil.split(detailFieldNames, deliminator);

        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        EffortCertificationDocumentBuild documentBuild = effortCertificationDocumentBuildService.generateDocumentBuild(postingYear, reportDefinition, ledgerBalances);
        if (savedIntoDatabase) {
            businessObjectService.save(documentBuild);
            persistenceService.retrieveNonKeyFields(documentBuild);
        }
        List<EffortCertificationDetailBuild> detailBuild = documentBuild.getEffortCertificationDetailLinesBuild();

        EffortCertificationDocumentBuild expectedDocumentBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator);
        int numberOfExpectedDetails = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DETAILS));
        List<EffortCertificationDetailBuild> expectedDetailsBuild = TestDataPreparator.buildTestDataList(EffortCertificationDetailBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator, numberOfExpectedDetails);

        String errorMessage = message.getProperty("error.documentBuildService.unexpectedDocumentGenerated");
        assertTrue(errorMessage, ObjectUtil.equals(documentBuild, expectedDocumentBuild, documentKeyFields));

        assertEquals(errorMessage, expectedDetailsBuild.size(), detailBuild.size());

        for (int i = 0; i < numberOfExpectedDetails; i++) {
            EffortCertificationDetailBuild expected = expectedDetailsBuild.get(i);
            EffortCertificationDetailBuild actual = detailBuild.get(i);

            assertTrue(errorMessage, ObjectUtil.equals(actual, expected, detailKeyFields));
        }
    }

    /**
     * compare the resulting detail line with the expected
     * 
     * @param testTarget the given test target that specifies the test data being used
     */
    private void assertDocumentListEquals(String testTarget) {
        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);

        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        List<EffortCertificationDocumentBuild> documentBuildList = effortCertificationDocumentBuildService.generateDocumentBuildList(postingYear, reportDefinition, ledgerBalances);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocumentBuild> expectedDocumentBuildList = TestDataPreparator.buildExpectedValueList(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        String errorMessage = message.getProperty("error.documentBuildService.unexpectedDocumentGenerated");
        assertEquals(errorMessage, expectedDocumentBuildList.size(), documentBuildList.size());

        for (int j = 0; j < numberOfExpectedDocuments; j++) {
            EffortCertificationDocumentBuild expected = expectedDocumentBuildList.get(j);
            boolean contain = false;

            for (int i = 0; i < numberOfExpectedDocuments - j; i++) {
                EffortCertificationDocumentBuild actual = documentBuildList.get(i);

                contain = ObjectUtil.equals(actual, expected, documentKeyFields);
                if (contain) {
                    documentBuildList.remove(i);
                    break;
                }
            }

            if (!contain) {
                fail(errorMessage);
            }
        }
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
