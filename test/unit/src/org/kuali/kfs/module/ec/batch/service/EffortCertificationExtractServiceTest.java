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

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class EffortCertificationExtractServiceTest extends KualiTestBase {

    private final Properties properties, message;
    private final String balanceFieldNames, entryFieldNames;
    private final String detailFieldNames, documentFieldNames, documentHeaderFieldNames, reportDefinitionFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private PersistenceService persistenceService;
    private EffortCertificationDetailBuildService effortCertificationDetailBuildService;
    private EffortCertificationExtractService effortCertificationExtractService;

    /**
     * Constructs a EffortCertificationDetailBuildServiceTest.java.
     */
    public EffortCertificationExtractServiceTest() {
        super();
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/effortCertificationExtractService.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();

        deliminator = properties.getProperty("deliminator");

        balanceFieldNames = properties.getProperty("balanceFieldNames");
        entryFieldNames = properties.getProperty("entryFieldNames");

        detailFieldNames = properties.getProperty("detailFieldNames");
        documentFieldNames = properties.getProperty("documentFieldNames");
        reportDefinitionFieldNames = properties.getProperty("reportDefinitionFieldNames");
        documentHeaderFieldNames = properties.getProperty("documentHeaderFieldNames");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        effortCertificationDetailBuildService = SpringContext.getBean(EffortCertificationDetailBuildService.class);
        effortCertificationExtractService = SpringContext.getBean(EffortCertificationExtractService.class);

        TestDataPreparator.doCleanUpWithoutReference(LedgerBalance.class, properties, "dataCleanup", balanceFieldNames, deliminator);
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_ValidParameters() throws Exception {
        String testTarget = "inputParameters.validParameters.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        TestDataPreparator.doCleanUpWithoutReference(LedgerEntry.class, properties, testTarget + "dataCleanup", entryFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, testTarget + "documentCleanup", documentFieldNames, deliminator);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(message.getProperty("error.effortCertificationExtractService.inputParameters.validParameters"));
        }

    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters_EmptyFiscalYear() throws Exception {
        String testTarget = "inputParameters.emptyFiscalYear.";
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        try {
            effortCertificationExtractService.extract(null, reportNumber);
            fail(message.getProperty("error.effortCertificationExtractService.inputParameters.emptyFiscalYear"));
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
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.effortCertificationExtractService.inputParameters.emptyReportNumber"));
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
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.effortCertificationExtractService.inputParameters.undefinedReportDefinition"));
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
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);
        reportDefinition.setActive(false);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.effortCertificationExtractService.inputParameters.inactiveReportDefinition"));
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
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        DocumentHeader documentHeader = TestDataPreparator.buildTestDataObject(DocumentHeader.class, properties, testTarget + "documentHeader", documentHeaderFieldNames, deliminator);
        documentHeader = TestDataPreparator.persistDataObject(documentHeader);

        EffortCertificationDocument document = this.buildDocument(testTarget);
        document.setDocumentHeader(documentHeader);
        document = TestDataPreparator.persistDataObject(document);

        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);
            fail(message.getProperty("error.effortCertificationExtractService.inputParameters.documentExist"));
        }
        catch (Exception e) {
        }
    }

    /**
     * employees that meet certain criteria can be selected
     */
    public void testEmployeeSelection_Selected() throws Exception {
        String testTarget = "employeeSelection.selected.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithoutReference(LedgerEntry.class, properties, testTarget + "dataCleanup", entryFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithoutReference(LedgerBalance.class, properties, testTarget + "dataCleanup", balanceFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, testTarget + "documentCleanup", documentFieldNames, deliminator);

        int numberOfBalances = Integer.valueOf(properties.getProperty(testTarget + "numOfBalances"));
        List<LedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "inputBalance", balanceFieldNames, deliminator, numberOfBalances);
        TestDataPreparator.persistDataObject(ledgerBalances);

        int numberOfEntries = Integer.valueOf(properties.getProperty(testTarget + "numOfEntries"));
        List<LedgerEntry> ledgerEntries = TestDataPreparator.buildTestDataList(LedgerEntry.class, properties, testTarget + "inputEntry", entryFieldNames, deliminator, numberOfEntries);
        TestDataPreparator.persistDataObject(ledgerEntries);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        effortCertificationExtractService.extract(fiscalYear, reportNumber);

        List<EffortCertificationDocumentBuild> documentBuildList = TestDataPreparator.findMatching(EffortCertificationDocumentBuild.class, properties, testTarget + "documentCleanup", documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + "numOfExpectedDocuments"));
        List<EffortCertificationDocumentBuild> expectedDocumentBuildList = TestDataPreparator.buildExpectedValueList(EffortCertificationDocumentBuild.class, properties, testTarget + "expectedDocument", documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentBuildList.size());
    }

    /**
     * employees that meet certain criteria can be selected
     */
    public void testEmployeeSelection_NotSelected() throws Exception {

    }

    /**
     * balances that meet certain criteria can be selected
     */
    public void testBalanceSelection() throws Exception {

    }

    /**
     * the selected employee must be paid by at lease on grant account or cost shared
     */
    public void testGrantAccountValidation() throws Exception {

    }

    /**
     * the account used to pay the selected employee must have valid account number and higher education function code
     */
    public void testAccountValidation() throws Exception {

    }

    /**
     * the account used to pay the empolyee must be funded by a federal agency only if the employee who was paid by fedeal grants is
     * required to have effort reports.
     */
    public void testFederalFundsAccountValidation() throws Exception {

    }

    /**
     * examine the generated build documents and their detail lines
     */
    public void testBuildDocumentGeneration() throws Exception {

    }

    /**
     * construct a ledger balance and persist it
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a ledger balance
     */
    private LedgerBalance buildLedgerBalance(String testTarget) {
        return this.buildDataObject(LedgerBalance.class, properties, testTarget + "inputBalance", balanceFieldNames, deliminator);
    }

    /**
     * build a report defintion object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a report defintion object
     */
    private EffortCertificationReportDefinition buildReportDefinition(String testTarget) {
        return this.buildDataObject(EffortCertificationReportDefinition.class, properties, testTarget + "reportDefinitionFieldValues", reportDefinitionFieldNames, deliminator);
    }

    /**
     * build an Effort Certification Document object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return an Effort Certification Document object
     */
    private EffortCertificationDocument buildDocument(String testTarget) {
        return this.buildDataObject(EffortCertificationDocument.class, properties, testTarget + "document", documentFieldNames, deliminator);
    }
    
    /**
     * build an Effort Certification Document object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return an Effort Certification Document object
     */
    private <T> T buildDataObject(Class<T> clazz, Properties properties, String propertykey, String fieldNames, String deliminator) {
        return TestDataPreparator.buildTestDataObject(clazz, properties, propertykey, fieldNames, deliminator);
    }
}
