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
package org.kuali.kfs.module.ec.document.validation.impl;

import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED;
import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.NONE;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * unit tests for the methods in EffortCertificationDocumentRuleUtil
 * 
 * @see org.kuali.kfs.module.ec.document.validation.impl.EffortCertificationDocumentRuleUtil
 */

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
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationDocumentRuleUtil.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);

        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        documentFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_FIELD_NAMES);
        reportDefinitionFieldNames = properties.getProperty(EffortTestDataPropertyConstants.REPORT_DEFINITION_FIELD_NAMES);
        documentHeaderFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DOCUMENT_HEADER_FIELD_NAMES);
    }

    /**
     * the default values will be applied into the specific fields when they are blank
     */
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
        detailKeyFields.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        assertTrue(TestDataPreparator.hasSameElements(expectedDetails, details, detailKeyFields));
    }

    /**
     * test if an expired account can be used through surveying the override code on the detail line
     */
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

    /**
     * an A21 sub account is associated with the detail line
     */
    public void testHasA21SubAccount_Yes() throws Exception {
        String testTarget = "hasA21SubAccount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
            assertTrue(EffortCertificationDocumentRuleUtil.hasA21SubAccount(detailLine));
        }
    }

    /**
     * no A21 sub account is associated with the detail line
     */
    public void testHasA21SubAccount_No() throws Exception {
        String testTarget = "hasA21SubAccount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
            assertFalse(EffortCertificationDocumentRuleUtil.hasA21SubAccount(detailLine));
        }
    }

    /**
     * the account associated with the detail line is closed
     */
    public void testHasClosedAccount_Yes() throws Exception {
        String testTarget = "hasClosedAccount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertTrue(EffortCertificationDocumentRuleUtil.hasClosedAccount(detailLine));
        }
    }

    /**
     * the account associated with the detail line is not closed (Still active)
     */
    public void testHasClosedAccount_No() throws Exception {
        String testTarget = "hasClosedAccount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertFalse(EffortCertificationDocumentRuleUtil.hasClosedAccount(detailLine));
        }
    }

    /**
     * the account associated with the detail line is a contract & grant account
     */
    public void testHasContractGrantAccount_Yes() throws Exception {
        String testTarget = "hasContractGrantAccount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertTrue(EffortCertificationDocumentRuleUtil.hasContractGrantAccount(detailLine));
        }
    }

    /**
     * the account associated with the detail line is not a contract & grant account
     */
    public void testHasContractGrantAccount_No() throws Exception {
        String testTarget = "hasContractGrantAccount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            assertFalse(EffortCertificationDocumentRuleUtil.hasContractGrantAccount(detailLine));
        }
    }

    /**
     * the sub account associated with the detail line is cost shared
     */
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

    /**
     * the sub account associated with the detail line is not cost shared
     */
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

    /**
     * the payroll amount of the detail line is zero or positive number
     */
    public void testHasNonnegativePayrollAmount_Yes() throws Exception {
        String testTarget = "hasNonnegativePayrollAmount.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertTrue(EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine));
        }
    }

    /**
     * the payroll amount of the detail line is a negatitive number
     */
    public void testHasNonnegativePayrollAmount_No() throws Exception {
        String testTarget = "hasNonnegativePayrollAmount.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertFalse(EffortCertificationDocumentRuleUtil.hasNonnegativePayrollAmount(detailLine));
        }
    }

    /**
     * there is an existing detail line in the document with the same chart, account and sub account as the new detail line
     */
    public void testHasSameExistingLine_Yes() throws Exception {
        String testTarget = "hasSameExistingLine.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        EffortCertificationDetail newDetail = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.NEW_DETAIL);
        List<String> comparableFields = Arrays.asList(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        assertTrue(EffortCertificationDocumentRuleUtil.hasSameExistingLine(document, newDetail, comparableFields));
    }

    /**
     * there is no existing detail line in the document with the same chart, account and sub account as the new detail line
     */
    public void testHasSameExistingLine_No() throws Exception {
        String testTarget = "hasSameExistingLine.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        EffortCertificationDetail newDetail = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.NEW_DETAIL);
        List<String> comparableFields = Arrays.asList(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        assertFalse(EffortCertificationDocumentRuleUtil.hasSameExistingLine(document, newDetail, comparableFields));
    }

    /**
     * the detail line has valid effort percent
     */
    public void testHasValidEffortPercent_Yes() throws Exception {
        String testTarget = "hasValidEffortPercent.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertTrue(EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine));
        }
    }

    /**
     * the detail line has no valid effort percent
     */
    public void testHasValidEffortPercent_No() throws Exception {
        String testTarget = "hasValidEffortPercent.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertFalse(EffortCertificationDocumentRuleUtil.hasValidEffortPercent(detailLine));
        }
    }

    /**
     * the payroll amounts of the detail lines have been changed
     */
    public void testIsPayrollAmountChanged_Line_Yes() throws Exception {
        String testTarget = "isPayrollAmountChanged.line.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertTrue(EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromOriginal(detailLine));
        }
    }

    /**
     * the payroll amounts of the detail lines have not been changed
     */
    public void testIsPayrollAmountChanged_Line_No() throws Exception {
        String testTarget = "isPayrollAmountChanged.line.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : details) {
            assertFalse(EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromOriginal(detailLine));
        }
    }

    /**
     * the payroll amount of the document has been changed
     */
    public void testIsPayrollAmountChanged_Document_Yes() throws Exception {
        String testTarget = "isPayrollAmountChanged.document.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        assertTrue(EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromOriginal(document));
    }

    /**
     * the payroll amount of the document has not been changed
     */
    public void testIsPayrollAmountChanged_Document_No() throws Exception {
        String testTarget = "isPayrollAmountChanged.document.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        assertFalse(EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromOriginal(document));
    }

    /**
     * the payroll amount of the detail line is zero or positive number
     */
    public void testIsPayrollAmountNonnegative_Yes() throws Exception {
        this.testHasNonnegativePayrollAmount_Yes();
    }

    /**
     * the payroll amount of the detail line is a negatitive number
     */
    public void testIsPayrollAmountNonnegative_No() throws Exception {
        this.testHasNonnegativePayrollAmount_No();
    }

    /**
     * the payroll amount of the detail line is overchanged
     */
    public void testIsPayrollAmountOverChanged_Line_Yes() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.line.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        KualiDecimal originalTotalAmount = document.getTotalOriginalPayrollAmount();
        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        
        int countOfOverChangedLine = 0;
        for (EffortCertificationDetail detailLine : details) {
            if(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(detailLine, originalTotalAmount, limitOfLinePayrollAmountChange)) {
                countOfOverChangedLine++;
            }
        }
        
        assertEquals("All line amounts are overchanged.", details.size(), countOfOverChangedLine);
    }

    /**
     * the payroll amount of the detail line is not overchanged
     */
    public void testIsPayrollAmountOverChanged_Line_No() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.line.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        KualiDecimal originalTotalAmount = document.getTotalOriginalPayrollAmount();
        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));
        List<EffortCertificationDetail> details = document.getEffortCertificationDetailLines();
        
        int countOfOverChangedLine = 0;
        for (EffortCertificationDetail detailLine : details) {
            if(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(detailLine, originalTotalAmount, limitOfLinePayrollAmountChange)) {
                countOfOverChangedLine++;
            }
        }
        
        assertEquals("There is no line whose amount is overchanged.", 0, countOfOverChangedLine);
    }

    /**
     * the payroll amount of at least one the detail line in the document is not overchanged
     */
    public void testIsPayrollAmountOverChanged_Document_Yes() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.document.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));
        assertTrue(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(document, limitOfLinePayrollAmountChange));
    }

    /**
     * the payroll amounts of the detail lines in the document are not overchanged
     */
    public void testIsPayrollAmountOverChanged_Document_No() throws Exception {
        String testTarget = "isPayrollAmountOverChanged.document.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        double limitOfLinePayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_LINE_PAYROLL_AMOUNT_CHANGE)));
        assertFalse(EffortCertificationDocumentRuleUtil.isPayrollAmountOverChanged(document, limitOfLinePayrollAmountChange));
    }

    /**
     * the total effort of the document is 100
     */
    public void testIsTotalEffortPercentageAs100_Yes() throws Exception {
        String testTarget = "isTotalEffortPercentageAs100.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        assertTrue(EffortCertificationDocumentRuleUtil.isTotalEffortPercentageAs100(document));
    }

    /**
     * the total effort of the document is not 100
     */
    public void testIsTotalEffortPercentageAs100_No() throws Exception {
        String testTarget = "isTotalEffortPercentageAs100.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        assertFalse(EffortCertificationDocumentRuleUtil.isTotalEffortPercentageAs100(document));
    }

    /**
     * the total payroll amount of the document is overchanged
     */
    public void testIsTotalPayrollAmountOverChanged_Yes() throws Exception {
        String testTarget = "isTotalPayrollAmountOverChanged.yes.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        double limitOfTotalPayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_TOTAL_PAYROLL_AMOUNT_CHANGE)));
        assertTrue(EffortCertificationDocumentRuleUtil.isTotalPayrollAmountOverChanged(document, limitOfTotalPayrollAmountChange));
    }

    /**
     * the total payroll amount of the document is not overchanged
     */
    public void testIsTotalPayrollAmountOverChanged_No() throws Exception {
        String testTarget = "isTotalPayrollAmountOverChanged.no.";
        EffortCertificationDocument document = this.loadEffortCertificationDocument(testTarget);

        double limitOfTotalPayrollAmountChange = Double.parseDouble(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.LIMIT_OF_TOTAL_PAYROLL_AMOUNT_CHANGE)));
        assertFalse(EffortCertificationDocumentRuleUtil.isTotalPayrollAmountOverChanged(document, limitOfTotalPayrollAmountChange));
    }

    /**
     * the detail line has valid effort percent
     */
    public void testIsValidPercent_Yes() throws Exception {
        this.testHasValidEffortPercent_Yes();
    }

    /**
     * the detail line has invalid effort percent
     */
    public void testIsValidPercent_No() throws Exception {
        this.testHasValidEffortPercent_No();
    }

    /**
     * test if the source account information is updated approperitely
     */
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

        FinancialSystemDocumentHeader documentHeader = TestDataPreparator.buildTestDataObject(FinancialSystemDocumentHeader.class, properties, testTarget + EffortTestDataPropertyConstants.DOCUMENT_HEADER, documentHeaderFieldNames, deliminator);
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

    /**
     * build a list of detail lines for the specified test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a list of detail lines for the specified test target
     */
    private List<EffortCertificationDetail> buildDetailLines(String testTarget) {
        int numberOfDetails = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_DETAILS)));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetail.class, properties, testTarget + EffortTestDataPropertyConstants.DETAIL, detailFieldNames, deliminator, numberOfDetails);
    }

    /**
     * build a list of expected detail lines for the specified test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a list of expected detail lines for the specified test target
     */
    private List<EffortCertificationDetail> buildExpectedDetailLines(String testTarget) {
        int numberOfDetails = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_DETAILS)));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetail.class, properties, testTarget + EffortTestDataPropertyConstants.EXPECTED_DETAIL, detailFieldNames, deliminator, numberOfDetails);
    }

    /**
     * build a detail lines for the specified test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @param propertyName the name the specified property that contains the data being used to build a detail line
     * @return a detail lines for the specified test target with the given property
     */
    private EffortCertificationDetail buildDetailLine(String testTarget, String propertyName) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationDetail.class, properties, testTarget + propertyName, detailFieldNames, deliminator);
    }
}
