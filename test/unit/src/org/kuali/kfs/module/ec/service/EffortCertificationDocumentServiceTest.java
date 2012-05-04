/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerEntry;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = kfs)
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
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationDocumentService.properties";

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

        KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        ledgerBalanceClass = LedgerBalance.class;
        ledgerEntryClass = kualiModuleService.getResponsibleModuleService(LaborLedgerEntry.class).createNewObjectFromExternalizableClass(LaborLedgerEntry.class).getClass();
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

        try {
            boolean isCreated = effortCertificationDocumentService.createAndRouteEffortCertificationDocument(documentBuild);
            assertTrue(isCreated);
        } catch ( ValidationException ex ) {            
            fail( "Business Rule Failure: " + GlobalVariables.getMessageMap() );
        }

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocument> expectedDocuments = TestDataPreparator.buildExpectedValueList(EffortCertificationDocument.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(expectedDocuments, documentList, documentKeyFields));

        for (EffortCertificationDocument document : documentList) {
            assertEquals(KFSConstants.DocumentStatusCodes.ENROUTE, document.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
        }
    }

    /**
     * check if the service can appropriately create and route SET document
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

        try {
            boolean isCreated = effortCertificationDocumentService.createAndRouteEffortCertificationDocument(documentBuild);
            assertTrue(isCreated);
        } catch ( ValidationException ex ) {            
            fail( "Business Rule Failure: " + GlobalVariables.getMessageMap() );
        }

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

