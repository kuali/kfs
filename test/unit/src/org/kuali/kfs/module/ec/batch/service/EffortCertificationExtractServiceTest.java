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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Parameter;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.batch.EffortCertificationExtractStep;
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

@ConfigureContext
public class EffortCertificationExtractServiceTest extends KualiTestBase {

    private final Properties properties, message;
    private final String balanceFieldNames, entryFieldNames;
    private final String detailFieldNames, documentFieldNames, documentHeaderFieldNames, reportDefinitionFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private PersistenceService persistenceService;
    private ParameterService parameterService;

    private EffortCertificationDetailBuildService effortCertificationDetailBuildService;
    private EffortCertificationExtractService effortCertificationExtractService;
    private LaborModuleService laborModuleService;

    private Class<? extends LaborLedgerBalance> ledgerBalanceClass;
    private Class<? extends LaborLedgerEntry> ledgerEntryClass;

    /**
     * Constructs a EffortCertificationDetailBuildServiceTest.java.
     */
    public EffortCertificationExtractServiceTest() {
        super();
        String messageFileName = "org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "org/kuali/module/effort/testdata/effortCertificationExtractService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);

        balanceFieldNames = properties.getProperty(EffortTestDataPropertyConstants.BALANCE_FIELD_NAMES);
        entryFieldNames = properties.getProperty(EffortTestDataPropertyConstants.ENTRY_FIELD_NAMES);

        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        documentFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_FIELD_NAMES);
        reportDefinitionFieldNames = properties.getProperty(EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_NAMES);
        documentHeaderFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_HEADER_FIELD_NAMES);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        parameterService = SpringContext.getBean(ParameterService.class);

        effortCertificationDetailBuildService = SpringContext.getBean(EffortCertificationDetailBuildService.class);
        effortCertificationExtractService = SpringContext.getBean(EffortCertificationExtractService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        ledgerBalanceClass = laborModuleService.getLaborLedgerBalanceClass();
        ledgerEntryClass = laborModuleService.getLaborLedgerEntryClass();
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_ValidParameters() throws Exception {
        String testTarget = "inputParameters.validParameters.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        TestDataPreparator.doCleanUpWithoutReference(ledgerEntryClass, properties, testTarget + EffortTestDataPropertyConstants.DATA_CLEANUP, entryFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(message.getProperty("error.validParameters"));
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_EmptyFiscalYear() throws Exception {
        String testTarget = "inputParameters.emptyFiscalYear.";
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        try {
            effortCertificationExtractService.extract(null, reportNumber);
            fail(message.getProperty("error.emptyFiscalYear"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_EmptyReportNumber() throws Exception {
        String testTarget = "inputParameters.emptyReportNumber.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.emptyReportNumber"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_UndefinedReportDefinition() throws Exception {
        String testTarget = "inputParameters.undefinedReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.undefinedReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_InactiveReportDefinition() throws Exception {
        String testTarget = "inputParameters.inactiveReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);
        reportDefinition.setActive(false);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.inactiveReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_DocumentExist() throws Exception {
        String testTarget = "inputParameters.documentExist.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        DocumentHeader documentHeader = TestDataPreparator.buildTestDataObject(DocumentHeader.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_HEADER, documentHeaderFieldNames, deliminator);
        documentHeader = TestDataPreparator.persistDataObject(documentHeader);

        EffortCertificationDocument document = this.buildDocument(testTarget);
        document.setDocumentHeader(documentHeader);
        document = TestDataPreparator.persistDataObject(document);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.documentExist"));
        }
        catch (Exception e) {
        }
    }

    /**
     * employees that meet certain criteria can be selected
     */
    public void testEmployeeSelection_Selected() throws Exception {
        String testTarget = "employeeSelection.selected.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocumentBuild> expectedDocumentBuildList = TestDataPreparator.buildExpectedValueList(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * employees that meet certain criteria can be selected
     */
    public void testEmployeeSelection_NotSelected() throws Exception {
        String testTarget = "employeeSelection.notSelected.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * check if the qualified balances for effort reporting can be selected
     */
    public void testBalanceSelection_Selected() throws Exception {
        String testTarget = "balanceSelection.selected.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocumentBuild> expectedDocumentBuildList = TestDataPreparator.buildExpectedValueList(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(documentBuildList, expectedDocumentBuildList, documentKeyFields));

        List<EffortCertificationDetailBuild> detailLinesBuild = new ArrayList<EffortCertificationDetailBuild>();
        for (EffortCertificationDocumentBuild document : documentBuildList) {
            detailLinesBuild.addAll(document.getEffortCertificationDetailLinesBuild());
        }

        int numberOfExpectedDetailLines = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DETAILS));
        List<EffortCertificationDetailBuild> expectedDetailLines = TestDataPreparator.buildExpectedValueList(EffortCertificationDetailBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator, numberOfExpectedDetailLines);

        assertEquals(expectedDetailLines.size(), detailLinesBuild.size());

        List<String> detailLineKeyFields = ObjectUtil.split(detailFieldNames, deliminator);
        detailLineKeyFields.remove(EffortPropertyConstants.FINANCIAL_DOCUMENT_POSTING_YEAR);
        assertTrue(TestDataPreparator.hasSameElements(detailLinesBuild, expectedDetailLines, detailLineKeyFields));
    }

    /**
     * check if the balances without qualified balance type codes cannot be selected
     */
    public void testBalanceSelection_UnqualifiedBalanceType() throws Exception {
        String testTarget = "balanceSelection.unqualifiedBalanceType.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * check if the balances without the qualified object type codes cannot be selected
     */
    public void testBalanceSelection_UnqualifiedObjectType() throws Exception {
        String testTarget = "balanceSelection.unqualifiedObjectType.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * check if the non-salary balances cannot be selected
     */
    public void testBalanceSelection_NotSalary() throws Exception {
        String testTarget = "balanceSelection.notSalary.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * check if the employees not paid by grants cannot be selected
     */
    public void testBalanceSelection_NotGrantAccount() throws Exception {
        String testTarget = "balanceSelection.notGrantAccount.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * check if the balances with Zero total amount cannot be selected
     */
    public void testBalanceSelection_ZeroAmountBalance() throws Exception {
        String testTarget = "balanceSelection.zeroAmountBalance.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * check if the employees without positive payments cannot be selected
     */
    public void testBalanceSelection_NonpositiveTotalAmount() throws Exception {
        String testTarget = "balanceSelection.nonpositiveTotalAmount.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters("");

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * check if the documents are generated correctly
     */
    public void testDocumentGeneration() throws Exception {
        String testTarget = "documentGeneration.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters(testTarget);

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocumentBuild> expectedDocumentBuildList = TestDataPreparator.buildExpectedValueList(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(documentBuildList, expectedDocumentBuildList, documentKeyFields));

        List<EffortCertificationDetailBuild> detailLinesBuild = new ArrayList<EffortCertificationDetailBuild>();
        for (EffortCertificationDocumentBuild document : documentBuildList) {
            detailLinesBuild.addAll(document.getEffortCertificationDetailLinesBuild());
        }

        int numberOfExpectedDetailLines = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DETAILS));
        List<EffortCertificationDetailBuild> expectedDetailLines = TestDataPreparator.buildExpectedValueList(EffortCertificationDetailBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator, numberOfExpectedDetailLines);

        List<String> detailLineKeyFields = ObjectUtil.split(detailFieldNames, deliminator);
        detailLineKeyFields.remove(EffortPropertyConstants.FINANCIAL_DOCUMENT_POSTING_YEAR);
        assertTrue(TestDataPreparator.hasSameElements(detailLinesBuild, expectedDetailLines, detailLineKeyFields));
    }

    /**
     * check if the employees paid by federal fundings can be selected when federal fund only indicator is enabled
     */
    public void testFederalGrantOnly_HasFederalGrant() throws Exception {
        String testTarget = "federalGrantOnly.hasFederalGrant.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters(testTarget);

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocumentBuild> expectedDocumentBuildList = TestDataPreparator.buildExpectedValueList(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(documentBuildList, expectedDocumentBuildList, documentKeyFields));

        List<EffortCertificationDetailBuild> detailLinesBuild = new ArrayList<EffortCertificationDetailBuild>();
        for (EffortCertificationDocumentBuild document : documentBuildList) {
            detailLinesBuild.addAll(document.getEffortCertificationDetailLinesBuild());
        }

        int numberOfExpectedDetailLines = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DETAILS));
        List<EffortCertificationDetailBuild> expectedDetailLines = TestDataPreparator.buildExpectedValueList(EffortCertificationDetailBuild.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator, numberOfExpectedDetailLines);

        assertEquals(expectedDetailLines.size(), detailLinesBuild.size());

        List<String> detailLineKeyFields = ObjectUtil.split(detailFieldNames, deliminator);
        detailLineKeyFields.remove(EffortPropertyConstants.FINANCIAL_DOCUMENT_POSTING_YEAR);
        assertTrue(TestDataPreparator.hasSameElements(detailLinesBuild, expectedDetailLines, detailLineKeyFields));
    }

    /**
     * check if the employees not paid by federal fundings cannot be selected when federal fund only indicator is enabled
     */
    public void testFederalGrantOnly_NoFederalGrant() throws Exception {
        String testTarget = "federalGrantOnly.noFederalGrant.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);
        this.loadTestData(testTarget);
        this.updateSystemParameters(testTarget);

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * load test data into database before a test case starts
     * 
     * @param testTarget the target test case
     */
    private void loadTestData(String testTarget) throws Exception {
        TestDataPreparator.doCleanUpWithoutReference(ledgerEntryClass, properties, testTarget + EffortTestDataPropertyConstants.DATA_CLEANUP, entryFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithoutReference(ledgerBalanceClass, properties, testTarget + EffortTestDataPropertyConstants.DATA_CLEANUP, balanceFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfEntries = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_ENTRIES));
        List<LaborLedgerEntry> ledgerEntries = TestDataPreparator.buildTestDataList(ledgerEntryClass, properties, testTarget + EffortTestDataPropertyConstants.INPUT_ENTRY, entryFieldNames, deliminator, numberOfEntries);
        TestDataPreparator.persistDataObject(ledgerEntries);

        int numberOfBalances = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_BALANCES));
        List<LaborLedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(ledgerBalanceClass, properties, testTarget + EffortTestDataPropertyConstants.INPUT_BALANCE, balanceFieldNames, deliminator, numberOfBalances);
        TestDataPreparator.persistDataObject(ledgerBalances);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);
    }

    /**
     * construct a ledger balance and persist it
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a ledger balance
     */
    private LaborLedgerBalance buildLedgerBalance(String testTarget) {
        return TestDataPreparator.buildTestDataObject(ledgerBalanceClass, properties, testTarget + EffortTestDataPropertyConstants.INPUT_BALANCE, balanceFieldNames, deliminator);
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

    /**
     * build an Effort Certification Document object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return an Effort Certification Document object
     */
    private EffortCertificationDocument buildDocument(String testTarget) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationDocument.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT, documentFieldNames, deliminator);
    }

    /**
     * update the system parameters
     * 
     * @param testTarget the given test target that specifies the test data being used
     */
    private void updateSystemParameters(String testTarget) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("parameterNamespaceCode", "KFS-ER");
        fieldValues.put("parameterDetailTypeCode", EffortCertificationExtractStep.class.getSimpleName());

        List<Parameter> parameters = (List<Parameter>) businessObjectService.findMatching(Parameter.class, fieldValues);
        for (Parameter param : parameters) {
            String name = param.getParameterName();

            String propertyKey = testTarget + "systemParameter." + name;
            String propertyValue = StringUtils.trim(properties.getProperty(propertyKey));

            if (propertyValue != null) {
                // NOTE: parameter servcie is caching the searching results that may cause the tests unstable.
                parameterService.setParameterForTesting(EffortCertificationExtractStep.class, name, propertyValue);
            }
        }
    }
}
