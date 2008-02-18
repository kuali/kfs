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
package org.kuali.module.effort.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.bo.LaborLedgerBalance;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.LaborModuleService;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.EffortConstants.SystemParameters;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

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
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/effortCertificationDocumentBuildService.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();

        deliminator = properties.getProperty("deliminator");

        balanceFieldNames = properties.getProperty("balanceFieldNames");
        detailFieldNames = properties.getProperty("detailFieldNames");
        documentFieldNames = properties.getProperty("documentFieldNames");

        postingYear = Integer.valueOf(properties.getProperty("postingYear"));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        effortCertificationDocumentBuildService = SpringContext.getBean(EffortCertificationDocumentBuildService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        ledgerBalanceClass = laborModuleService.getLaborLedgerBalanceClass();

        TestDataPreparator.doCleanUpWithoutReference(ledgerBalanceClass, properties, "dataCleanup", balanceFieldNames, deliminator);
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
        Map<String, List<String>> systemParameters = this.buildSystemParameterMap("");

        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        EffortCertificationDocumentBuild documentBuild = effortCertificationDocumentBuildService.generateDocumentBuild(postingYear, reportDefinition, ledgerBalances, systemParameters);
        if (savedIntoDatabase) {
            businessObjectService.save(documentBuild);
            persistenceService.retrieveNonKeyFields(documentBuild);
        }
        List<EffortCertificationDetailBuild> detailBuild = documentBuild.getEffortCertificationDetailLinesBuild();

        EffortCertificationDocumentBuild expectedDocumentBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDocumentBuild.class, properties, testTarget + "expectedDocument", documentFieldNames, deliminator);
        int numberOfExpectedDetails = Integer.valueOf(properties.getProperty(testTarget + "numOfExpectedDetails"));
        List<EffortCertificationDetailBuild> expectedDetailsBuild = TestDataPreparator.buildTestDataList(EffortCertificationDetailBuild.class, properties, testTarget + "expectedDetail", detailFieldNames, deliminator, numberOfExpectedDetails);

        String errorMessage = message.getProperty("error.documentBuildService.unexpectedDocumentGenerated");
        assertTrue(errorMessage, ObjectUtil.compareObject(documentBuild, expectedDocumentBuild, documentKeyFields));

        assertEquals(errorMessage, expectedDetailsBuild.size(), detailBuild.size());

        for (int i = 0; i < numberOfExpectedDetails; i++) {
            EffortCertificationDetailBuild expected = expectedDetailsBuild.get(i);
            EffortCertificationDetailBuild actual = detailBuild.get(i);

            assertTrue(errorMessage, ObjectUtil.compareObject(actual, expected, detailKeyFields));
        }
    }

    /**
     * compare the resulting detail line with the expected
     * 
     * @param testTarget the given test target that specifies the test data being used
     */
    private void assertDocumentListEquals(String testTarget) {
        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        Map<String, List<String>> systemParameters = this.buildSystemParameterMap("");

        List<LaborLedgerBalance> ledgerBalances = this.buildLedgerBalances(testTarget);

        List<EffortCertificationDocumentBuild> documentBuildList = effortCertificationDocumentBuildService.generateDocumentBuildList(postingYear, reportDefinition, ledgerBalances, systemParameters);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + "numOfExpectedDocuments"));
        List<EffortCertificationDocumentBuild> expectedDocumentBuildList = TestDataPreparator.buildExpectedValueList(EffortCertificationDocumentBuild.class, properties, testTarget + "expectedDocument", documentFieldNames, deliminator, numberOfExpectedDocuments);

        String errorMessage = message.getProperty("error.documentBuildService.unexpectedDocumentGenerated");
        assertEquals(errorMessage, expectedDocumentBuildList.size(), documentBuildList.size());

        for (int j = 0; j < numberOfExpectedDocuments; j++) {
            EffortCertificationDocumentBuild expected = expectedDocumentBuildList.get(j);
            boolean contain = false;

            for (int i = 0; i < numberOfExpectedDocuments - j; i++) {
                EffortCertificationDocumentBuild actual = documentBuildList.get(i);

                contain = ObjectUtil.compareObject(actual, expected, documentKeyFields);
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
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        List<LaborLedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(ledgerBalanceClass, properties, testTarget + "inputBalance", balanceFieldNames, deliminator, numberOfTestData);
        businessObjectService.save(ledgerBalances);
        for (LaborLedgerBalance balance : ledgerBalances) {
            persistenceService.retrieveNonKeyFields(balance);
        }

        return ledgerBalances;
    }

    /**
     * construct a system parameter map
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a system parameter map
     */
    private Map<String, List<String>> buildSystemParameterMap(String testTarget) {
        List<String> expSubAccountType = ObjectUtil.split(properties.getProperty(testTarget + "systemParameter.EXPENSE_SUB_ACCOUNT_TYPE_CODE"), deliminator);
        List<String> csSubAccountType = ObjectUtil.split(properties.getProperty(testTarget + "systemParameter.COST_SHARE_SUB_ACCOUNT_TYPE_CODE"), deliminator);

        Map<String, List<String>> systemParameters = new HashMap<String, List<String>>();
        systemParameters.put(SystemParameters.EXPENSE_SUB_ACCOUNT_TYPE_CODE, expSubAccountType);
        systemParameters.put(SystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE, csSubAccountType);

        return systemParameters;
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
}
