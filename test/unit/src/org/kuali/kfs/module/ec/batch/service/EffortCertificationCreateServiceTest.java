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
package org.kuali.kfs.module.ec.batch.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class EffortCertificationCreateServiceTest extends KualiTestBase {

    private final Properties properties, message;
    private final String detailFieldNames, documentFieldNames, reportDefinitionFieldNames, documentHeaderFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private EffortCertificationCreateService effortCertificationCreateService;

    /**
     * Constructs a EffortCertificationCreateServiceTest.java.
     */
    public EffortCertificationCreateServiceTest() {
        super();
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationCreateService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);

        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        documentFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_FIELD_NAMES);
        reportDefinitionFieldNames = properties.getProperty(EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_NAMES);
        documentHeaderFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_HEADER_FIELD_NAMES);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        effortCertificationCreateService = SpringContext.getBean(EffortCertificationCreateService.class);
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_ValidParameters() throws Exception {
        String testTarget = "inputParameters.validParameters.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(message.getProperty("error.validParameters") + " - Reported Errors: " + GlobalVariables.getMessageMap() );
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_EmptyFiscalYear() throws Exception {
        String testTarget = "inputParameters.emptyFiscalYear.";
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        try {
            effortCertificationCreateService.create(null, reportNumber);
            fail(message.getProperty("error.emptyFiscalYear"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_EmptyReportNumber() throws Exception {
        String testTarget = "inputParameters.emptyReportNumber.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.emptyReportNumber"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_UndefinedReportDefinition() throws Exception {
        String testTarget = "inputParameters.undefinedReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.undefinedReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_InactiveReportDefinition() throws Exception {
        String testTarget = "inputParameters.inactiveReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.inactiveReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_NotOpenedReportDefinition() throws Exception {
        String testTarget = "inputParameters.notOpenedReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.notOpenedReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_ClosedReportDefinition() throws Exception {
        String testTarget = "inputParameters.closedReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.closedReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_DocumentBuildNotExist() throws Exception {
        String testTarget = "inputParameters.documentBuildNotExist.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.documentBuildNotExist"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_DocumentExist() throws Exception {
        String testTarget = "inputParameters.documentExist.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        FinancialSystemDocumentHeader documentHeader = TestDataPreparator.buildTestDataObject(FinancialSystemDocumentHeader.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_HEADER, documentHeaderFieldNames, deliminator);
        documentHeader = TestDataPreparator.persistDataObject(documentHeader);

        EffortCertificationDocument document = this.buildDocument(testTarget);
        document.setDocumentHeader(documentHeader);
        document = TestDataPreparator.persistDataObject(document);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.documentExist"));
        }
        catch (Exception e) {
        }
    }

    /**
     * If everything is set up correctly, a set of documents can be created.
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testCreate() throws Exception {
        String testTarget = "create.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
        } catch ( ValidationException e ) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DOCUMENTS));
        List<EffortCertificationDocument> expectedDocuments = TestDataPreparator.buildExpectedValueList(EffortCertificationDocument.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DOCUMENT, documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(expectedDocuments, documentList, documentKeyFields));
    }

    /**
     * after each document is created, it should be routed for approval.
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testRoute() throws Exception {
        String testTarget = "route.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.FISCAL_YEAR)));
        String reportNumber = properties.getProperty(testTarget + EffortTestDataPropertyConstants.REPORT_NUMBER);

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
        } catch ( ValidationException e ) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        for (EffortCertificationDocument document : documentList) {
            assertEquals(document.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode(), KFSConstants.DocumentStatusCodes.ENROUTE);
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

    /**
     * build an Effort Certification Document object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return an Effort Certification Document object
     */
    private EffortCertificationDocument buildDocument(String testTarget) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationDocument.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT, documentFieldNames, deliminator);
    }

    private EffortCertificationDocumentBuild buildDocumentBuild(String testTarget) {
        EffortCertificationDocumentBuild documentBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDocumentBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_BUILD, documentFieldNames, deliminator);
        List<EffortCertificationDetailBuild> detailBuild = this.buildDetailLineBuild(testTarget);
        documentBuild.setEffortCertificationDetailLinesBuild(detailBuild);
        return documentBuild;
    }

    private List<EffortCertificationDetailBuild> buildDetailLineBuild(String testTarget) {
        int numberOfDetailBuild = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_DETAIL_BUILDS)));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetailBuild.class, properties, testTarget + EffortTestDataPropertyConstants.DETAIL_BUILD, detailFieldNames, deliminator, numberOfDetailBuild);
    }
}

