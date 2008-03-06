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

import static org.kuali.kfs.bo.AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
import static org.kuali.kfs.bo.AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED;
import static org.kuali.kfs.bo.AccountingLineOverride.CODE.NONE;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.testdata.EffortTestDataPropertyConstants;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

@ConfigureContext
public class EffortCertificationDocumentRuleUtilTest extends KualiTestBase {

    private final Properties properties, message;
    private final String detailFieldNames, documentFieldNames, documentHeaderFieldNames, reportDefinitionFieldNames;
    private final String deliminator;

    /**
     * Constructs a EffortCertificationDocumentRuleUtilTest.java.
     */
    public EffortCertificationDocumentRuleUtilTest() {
        super();
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/effortCertificationDocumentRuleUtil.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);

        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        documentFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_FIELD_NAMES);
        reportDefinitionFieldNames = properties.getProperty(EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_NAMES);
        documentHeaderFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_HEADER_FIELD_NAMES);
    }

    public void testApplyDefaultvalues() throws Exception {
        String testTarget = "applyDefaultValues.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            EffortCertificationDocumentRuleUtil.applyDefaultValues(detailLine);
        }

        int numberOfExpectedDetails = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DETAILS)));
        List<EffortCertificationDetail> expectedDetails = TestDataPreparator.buildTestDataList(EffortCertificationDetail.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator, numberOfExpectedDetails);

        assertEquals(numberOfExpectedDetails, details.size());

        List<String> detailKeyFields = ObjectUtil.split(detailFieldNames, deliminator);
        detailKeyFields.remove(EffortPropertyConstants.FINANCIAL_DOCUMENT_POSTING_YEAR);
        assertTrue(TestDataPreparator.hasSameElements(expectedDetails, details, detailKeyFields));
    }

    public void testCanExpiredAccountBeUsed() throws Exception {
        String testTarget = "canExpiredAccountBeUsed.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            detailLine.setOverrideCode(EXPIRED_ACCOUNT);
            assertTrue(EffortCertificationDocumentRuleUtil.canExpiredAccountBeUsed(detailLine));
        }

        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            detailLine.setOverrideCode(EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED);
            assertTrue(EffortCertificationDocumentRuleUtil.canExpiredAccountBeUsed(detailLine));
        }

        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            detailLine.setOverrideCode(NONE);
            assertFalse(EffortCertificationDocumentRuleUtil.canExpiredAccountBeUsed(detailLine));
        }
    }

    public void testHasA21SubAccount_Yes() throws Exception {
        String testTarget = "hasA21SubAccount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);            
            assertTrue(EffortCertificationDocumentRuleUtil.hasA21SubAccount(detailLine));
        }
    }
    
    public void testHasA21SubAccount_No() throws Exception {
        String testTarget = "hasA21SubAccount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);           
            assertFalse(EffortCertificationDocumentRuleUtil.hasA21SubAccount(detailLine));
        }
    }

    public void testHasClosedAccount_Yes() throws Exception {
        String testTarget = "hasClosedAccount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertTrue(EffortCertificationDocumentRuleUtil.hasClosedAccount(detailLine));
        }
    }
    
    public void testHasClosedAccount_No() throws Exception {
        String testTarget = "hasClosedAccount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertFalse(EffortCertificationDocumentRuleUtil.hasClosedAccount(detailLine));
        }
    }

    public void testHasContractGrantAccount_Yes() throws Exception {
        String testTarget = "hasContractGrantAccount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertTrue(EffortCertificationDocumentRuleUtil.hasContractGrantAccount(detailLine));
        }
    }
    
    public void testHasContractGrantAccount_No() throws Exception {
        String testTarget = "hasContractGrantAccount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertFalse(EffortCertificationDocumentRuleUtil.hasContractGrantAccount(detailLine));
        }
    }

    public void testHasCostShareSubAccount_Yes() throws Exception {
        String testTarget = "hasCostShareSubAccount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<String> designatedCostShareSubAccountTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.COST_SHARE_SUB_ACCOUNT_TYPE_CODES), deliminator);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
            assertTrue(EffortCertificationDocumentRuleUtil.hasCostShareSubAccount(detailLine, designatedCostShareSubAccountTypeCodes));
        }
    }
    
    public void testHasCostShareSubAccount_No() throws Exception {
        String testTarget = "hasCostShareSubAccount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<String> designatedCostShareSubAccountTypeCodes = ObjectUtil.split(properties.getProperty(testTarget + EffortTestDataPropertyConstants.COST_SHARE_SUB_ACCOUNT_TYPE_CODES), deliminator);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
            assertFalse(EffortCertificationDocumentRuleUtil.hasCostShareSubAccount(detailLine, designatedCostShareSubAccountTypeCodes));
        }
    }

    public void testHasNonnegativePayrollAmount_Yes() throws Exception {
        String testTarget = "hasNonnegativePayrollAmount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertTrue(EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine));
        }
    }
    
    public void testHasNonnegativePayrollAmount_No() throws Exception {
        String testTarget = "hasNonnegativePayrollAmount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertFalse(EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine));
        }
    }

    public void testHasSameExistingLine_Yes() throws Exception {
        String testTarget = "hasSameExistingLine.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        EffortCertificationDetail newDetail = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.NEW_DETAIL);
        List<String> comparableFields = Arrays.asList(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        assertTrue(EffortCertificationDocumentRuleUtil.hasSameExistingLine(document, newDetail, comparableFields));
    }
    
    public void testHasSameExistingLine_No() throws Exception {
        String testTarget = "hasSameExistingLine.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        EffortCertificationDetail newDetail = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.NEW_DETAIL);
        List<String> comparableFields = Arrays.asList(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        assertFalse(EffortCertificationDocumentRuleUtil.hasSameExistingLine(document, newDetail, comparableFields));
    }

    public void testHasValidEffortPercent_Yes() throws Exception {
        String testTarget = "hasValidEffortPercent.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertTrue(EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine));
        }
    }
    
    public void testHasValidEffortPercent_No() throws Exception {
        String testTarget = "hasValidEffortPercent.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertFalse(EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine));
        }
    }

    public void testIsPayrollAmountChanged_Line_Yes() throws Exception {
        String testTarget = "isPayrollAmountChanged.line.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertTrue(EffortCertificationDocumentRuleUtil.isPayrollAmountChanged(detailLine));
        }
    }
    
    public void testIsPayrollAmountChanged_Line_No() throws Exception {
        String testTarget = "isPayrollAmountChanged.line.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertFalse(EffortCertificationDocumentRuleUtil.isPayrollAmountChanged(detailLine));
        }
    }
    
    public void testIsPayrollAmountChanged_Document_Yes() throws Exception {
        String testTarget = "isPayrollAmountChanged.document.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        assertTrue(EffortCertificationDocumentRuleUtil.isPayrollAmountChanged(document));
    }
    
    public void testIsPayrollAmountChanged_Document_No() throws Exception {
        String testTarget = "isPayrollAmountChanged.document.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        assertFalse(EffortCertificationDocumentRuleUtil.isPayrollAmountChanged(document));
    }
    
    public void testIsPayrollAmountNonnegative_Yes() throws Exception {
        this.testHasNonnegativePayrollAmount_Yes();
    }

    public void testIsPayrollAmountNonnegative_No() throws Exception {
        this.testHasNonnegativePayrollAmount_No();
    }

    public void testIsPayrollAmountOverChanged_Line_Yes() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.line.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        KualiDecimal originalTotalAmount = document.getTotalOriginalPayrollAmount();
        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertTrue(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(detailLine, originalTotalAmount, limitOfLinePayrollAmountChange));
        }
    }
    
    public void testIsPayrollAmountOverChanged_Line_No() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.line.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        KualiDecimal originalTotalAmount = document.getTotalOriginalPayrollAmount();
        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertFalse(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(detailLine, originalTotalAmount, limitOfLinePayrollAmountChange));
        }
    }
    
    public void testIsPayrollAmountOverChanged_Document_Yes() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.document.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));        
        assertTrue(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(document, limitOfLinePayrollAmountChange));
    }
    
    public void testIsPayrollAmountOverChanged_Document_No() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.document.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));        
        assertFalse(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(document, limitOfLinePayrollAmountChange));
    }

    public void testIsTotalEffortPercentageAs100_Yes() throws Exception {
        String testTarget = "isTotalEffortPercentageAs100.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        assertTrue(EffortCertificationDocumentRuleUtil.isTotalEffortPercentageAs100(document));
    }
    
    public void testIsTotalEffortPercentageAs100_No() throws Exception {
        String testTarget = "isTotalEffortPercentageAs100.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        assertFalse(EffortCertificationDocumentRuleUtil.isTotalEffortPercentageAs100(document));
    }

    public void testIsTotalPayrollAmountOverChanged_Yes() throws Exception {
        String testTarget = "isTotalPayrollAmountOverChanged.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        double limitOfTotalPayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_TOTAL_PAYROLL_AMOUNT_CHANGE)));        
        assertTrue(EffortCertificationDocumentRuleUtil.isTotalPayrollAmountOverChanged(document, limitOfTotalPayrollAmountChange));
    }
    
    public void testIsTotalPayrollAmountOverChanged_No() throws Exception {
        String testTarget = "isTotalPayrollAmountOverChanged.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        double limitOfTotalPayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_TOTAL_PAYROLL_AMOUNT_CHANGE)));        
        assertFalse(EffortCertificationDocumentRuleUtil.isTotalPayrollAmountOverChanged(document, limitOfTotalPayrollAmountChange));
    }

    public void testIsValidPercent_Yes() throws Exception {
        this.testHasValidEffortPercent_Yes();
    }
    
    public void testIsValidPercent_No() throws Exception {
        this.testHasValidEffortPercent_No();
    }

    public void testUpdateSourceAccountInformation() throws Exception {
        String testTarget = "updateSourceAccountInformation.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);
        
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
            EffortCertificationDocumentRuleUtil.updateSourceAccountInformation(detailLine);
        }
        
        List<EffortCertificationDetail> expectedDetails = this.buildExpectedDetailLines(testTarget);
        
        List<String> detailKeyFields = ObjectUtil.split(detailFieldNames, deliminator);
        assertTrue(TestDataPreparator.hasSameElements(expectedDetails, details, detailKeyFields));
    }

    /**
     * load test data into database before a test case starts
     * 
     * @param testTarget the target test case
     */
    private EffortCertificationDocument loadEffortCertificationDocument(String testTarget) throws Exception {
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, EffortTestDataPropertyConstants.DOCUMENT_CLEANUP, documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        DocumentHeader documentHeader = TestDataPreparator.buildTestDataObject(DocumentHeader.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_HEADER, documentHeaderFieldNames, deliminator);
        documentHeader = TestDataPreparator.persistDataObject(documentHeader);

        EffortCertificationDocument document = this.buildDocument(testTarget);
        document.setDocumentHeader(documentHeader);

        List<EffortCertificationDetail> detailLines = this.buildDetailLines(testTarget);
        document.setEffortCertificationDetailLines(detailLines);

        document = TestDataPreparator.persistDataObject(document);

        return document;
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

    private List<EffortCertificationDetail> buildDetailLines(String testTarget) {
        int numberOfDetails = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_DETAILS)));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetail.class, properties, testTarget + EffortTestDataPropertyConstants.DETAIL, detailFieldNames, deliminator, numberOfDetails);
    }
    
    private List<EffortCertificationDetail> buildExpectedDetailLines(String testTarget) {
        int numberOfDetails = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DETAILS)));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetail.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator, numberOfDetails);
    }
    
    private EffortCertificationDetail buildDetailLine(String testTarget, String propertyName) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationDetail.class, properties, testTarget + propertyName, detailFieldNames, deliminator);
    }
}
