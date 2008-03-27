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

import static org.kuali.test.fixtures.UserNameFixture.KULUSER;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.testdata.EffortTestDataPropertyConstants;
import org.kuali.module.integration.bo.LaborLedgerBalance;
import org.kuali.module.integration.bo.LaborLedgerEntry;
import org.kuali.module.integration.service.LaborModuleService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

@ConfigureContext(session = KULUSER)
public class EffortCertificationDocumentServiceTest extends KualiTestBase {
    private final Properties properties, message;
    private final String balanceFieldNames, entryFieldNames;
    private final String detailFieldNames, documentFieldNames, reportDefinitionFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private EffortCertificationDocumentService effortCertificationDocumentService;

    private LaborModuleService laborModuleService;

    private Class<? extends LaborLedgerBalance> ledgerBalanceClass;
    private Class<? extends LaborLedgerEntry> ledgerEntryClass;

    /**
     * Constructs a EffortCertificationCreateServiceTest.java.
     */
    public EffortCertificationDocumentServiceTest() {
        super();
        String messageFileName = "org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "org/kuali/module/effort/testdata/effortCertificationDocumentService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);

        balanceFieldNames = properties.getProperty(EffortTestDataPropertyConstants.BALANCE_FIELD_NAMES);
        entryFieldNames = properties.getProperty(EffortTestDataPropertyConstants.ENTRY_FIELD_NAMES);

        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        documentFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_FIELD_NAMES);
        reportDefinitionFieldNames = properties.getProperty(EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_NAMES);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        effortCertificationDocumentService = SpringContext.getBean(EffortCertificationDocumentService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        ledgerBalanceClass = laborModuleService.getLaborLedgerBalanceClass();
        ledgerEntryClass = laborModuleService.getLaborLedgerEntryClass();
    }

    /**
     * check if the service can approperiately create and route effort certification document
     * 
     * @see effortCertificationDocumentService.createEffortCertificationDocument(EffortCertificationDocumentBuild)
     */
    public void testCreateEffortCertificationDocument() throws Exception {
        String testTarget = "createEffortCertificationDocument.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        boolean isCreated = effortCertificationDocumentService.createAndRouteEffortCertificationDocument(documentBuild);
        assertTrue(isCreated);

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocument> expectedDocuments = TestDataPreparator.buildExpectedValueList(EffortCertificationDocument.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(expectedDocuments, documentList, documentKeyFields));

        for (EffortCertificationDocument document : documentList) {
            assertEquals(document.getDocumentHeader().getFinancialDocumentStatusCode(), KFSConstants.DocumentStatusCodes.ENROUTE);
        }
    }

    /**
     * check if the service can approperiately create and route SET document
     * 
     * @see effortCertificationDocumentService.generateSalaryExpenseTransferDocument(EffortCertificationDocument)
     */
    public void testGenerateSalaryExpenseTransferDocument() throws Exception {
        String testTarget = "generateSalaryExpenseTransferDocument.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        this.loadLaborTestData(testTarget);

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        boolean isCreated = effortCertificationDocumentService.createAndRouteEffortCertificationDocument(documentBuild);
        assertTrue(isCreated);

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        for (EffortCertificationDocument document : documentList) {
            document.refreshReferenceObject(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_DEFINITION);

            boolean isGenerated = effortCertificationDocumentService.generateSalaryExpenseTransferDocument(document);
            assertTrue("The document should be generated and approved.", isGenerated);
        }
    }

    /**
     * build a report defintion object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a report defintion object
     */
    private EffortCertificationReportDefinition buildReportDefinition(String testTarget) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationReportDefinition.class, properties, testTarget + EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_VALUES, reportDefinitionFieldNames, deliminator);
    }

    private EffortCertificationDocumentBuild buildDocumentBuild(String testTarget) {
        EffortCertificationDocumentBuild documentBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_BUILD, documentFieldNames, deliminator);
        List<EffortCertificationDetailBuild> detailBuild = this.buildDetailLineBuild(testTarget);
        documentBuild.setEffortCertificationDetailLinesBuild(detailBuild);
        return documentBuild;
    }

    private List<EffortCertificationDetailBuild> buildDetailLineBuild(String testTarget) {
        int numberOfDetailBuild = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_DETAIL_BUILDS));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetailBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DETAIL_BUILD, detailFieldNames, deliminator, numberOfDetailBuild);
    }

    /**
     * load test data into database before a test case starts
     * 
     * @param testTarget the target test case
     */
    private void loadLaborTestData(String testTarget) throws Exception {
        TestDataPreparator.doCleanUpWithoutReference(ledgerEntryClass, properties, testTarget + EffortTestDataPropertyConstants.DATA_CLEANUP, entryFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithoutReference(ledgerBalanceClass, properties, testTarget + EffortTestDataPropertyConstants.DATA_CLEANUP, balanceFieldNames, deliminator);

        int numberOfEntries = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_ENTRIES));
        List<LaborLedgerEntry> ledgerEntries = TestDataPreparator.buildTestDataList(ledgerEntryClass, properties, testTarget + EffortTestDataPropertyConstants.INPUT_ENTRY, entryFieldNames, deliminator, numberOfEntries);
        TestDataPreparator.persistDataObject(ledgerEntries);

        int numberOfBalances = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_BALANCES));
        List<LaborLedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(ledgerBalanceClass, properties, testTarget + EffortTestDataPropertyConstants.INPUT_BALANCE, balanceFieldNames, deliminator, numberOfBalances);
        TestDataPreparator.persistDataObject(ledgerBalances);
    }
}
