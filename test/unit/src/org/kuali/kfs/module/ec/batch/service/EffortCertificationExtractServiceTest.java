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

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class EffortCertificationExtractServiceTest extends KualiTestBase {

    private final Properties properties, message;
    private final String balanceFieldNames, detailFieldNames, documentFieldNames, documentHeaderFieldNames, reportDefinitionFieldNames;
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

        this.doCleanUp("");
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
        reportDefinition = this.persistDataObject(reportDefinition);
        
        this.doCleanUp(testTarget);
        
        try {
            effortCertificationExtractService.extract(fiscalYear, reportNumber);           
        }
        catch (Exception e) {
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
        reportDefinition = this.persistDataObject(reportDefinition);
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
        reportDefinition = (EffortCertificationReportDefinition) businessObjectService.retrieve(reportDefinition);
        reportDefinition.setActive(true);

        DocumentHeader header = TestDataPreparator.buildTestDataObject(DocumentHeader.class, properties, testTarget + "documentHeader", documentHeaderFieldNames, deliminator);
        //header = this.persistDataObject(header);

        EffortCertificationDocument document = this.buildDocument(testTarget);
        document.setDocumentHeader(header);
        document = this.persistDataObject(document);

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
    public void testEmployeeSelection() throws Exception {

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
        LedgerBalance ledgerBalance = TestDataPreparator.buildTestDataObject(LedgerBalance.class, properties, testTarget + "inputBalance", balanceFieldNames, deliminator);
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
        return TestDataPreparator.buildTestDataObject(EffortCertificationReportDefinition.class, properties, testTarget + "reportDefinitionFieldValues", reportDefinitionFieldNames, deliminator);
    }

    /**
     * build an Effort Certification Document object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return an Effort Certification Document object
     */
    private EffortCertificationDocument buildDocument(String testTarget) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationDocument.class, properties, testTarget + "document", documentFieldNames, deliminator);
    }

    /**
     * persist the given data object if it is not in the persistent store
     * 
     * @param dataObject the given data object
     * @return return the data object persisted into the data store
     */
    private <T extends PersistableBusinessObject> T persistDataObject(T dataObject) {
        T existingDataObject = (T) businessObjectService.retrieve(dataObject);
        if (existingDataObject == null) {
            businessObjectService.save(dataObject);
            return dataObject;
        }

        return existingDataObject;
    }

    /**
     * remove the existing data from the database so that they cannot affact the test results
     */
    private void doCleanUp(String testTarget) throws Exception {
        LedgerBalance cleanup = new LedgerBalance();
        ObjectUtil.populateBusinessObject(cleanup, properties, testTarget + "dataCleanup", balanceFieldNames, deliminator);
        Map<String, Object> fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(balanceFieldNames, deliminator)));
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }
}
