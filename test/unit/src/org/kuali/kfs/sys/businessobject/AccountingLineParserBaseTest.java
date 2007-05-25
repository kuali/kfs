/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.bo;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import org.kuali.KeyConstants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.exceptions.AccountingLineParserException;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.document.InternalBillingDocument;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.CrossSectionSuite;

/**
 * Test class for testing <code>{@link AccountingLineParserBase}</code>
 */
@AnnotationTestSuite(CrossSectionSuite.class)
@WithTestSpringContext(session = KHUNTLEY)
public class AccountingLineParserBaseTest extends KualiTestBase {

    private AccountingDocument accountingDocument;
    private AccountingLineParser parser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        accountingDocument = (AccountingDocument) SpringServiceLocator.getDocumentService().getNewDocument(InternalBillingDocument.class);
        parser = accountingDocument.getAccountingLineParser();
    }

    /**
     */
    public void testParseSourceAccountingLine_failure_notANumber() {
        try {
            parser.parseSourceAccountingLine(accountingDocument, "BL,4631672, , ,KUL, , , a");
            fail("didn't throw AccountingLineParserException");
        }
        catch (AccountingLineParserException e) {
            // good
            assertInvalidPropertyValue(e, " a");
        }
    }

    private static void assertInvalidPropertyValue(AccountingLineParserException e, String expectedErrorParam) {
        assertEquals(KeyConstants.AccountingLineParser.ERROR_INVALID_PROPERTY_VALUE, e.getErrorKey());
        List<String> errorParams = Arrays.asList(e.getErrorParameters());
        assertTrue(errorParams + " contains '" + expectedErrorParam + "'", errorParams.contains(expectedErrorParam));
    }

    /**
     */
    public void testParseTargetAccountingLine_failure_notANumber() {
        try {
            parser.parseTargetAccountingLine(accountingDocument, "BL,4631672, , ,KUL, , , b");
            fail("didn't throw AccountingLineParserException");
        }
        catch (AccountingLineParserException e) {
            // good
            assertInvalidPropertyValue(e, " b");
        }
    }

    /**
     */
    public void testParseSourceAccountingLine_pass_upperCase() {
        String chartOfAccountsCode = "BL";
        String accountNumber = "463172";
        String projectCode = "KUL";
        String amount = "12.31";

        StringBuffer sb = new StringBuffer();
        sb.append(chartOfAccountsCode);
        sb.append(",");
        sb.append(accountNumber);
        sb.append(",,,,");
        sb.append(projectCode);
        sb.append(",,");
        sb.append(amount);
        SourceAccountingLine result = parser.parseSourceAccountingLine(accountingDocument, sb.toString());

        assertEquals(accountingDocument.getPostingYear(), result.getPostingYear());
        assertEquals(accountingDocument.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(accountingDocument.getSourceAccountingLines().size() + 1, result.getSequenceNumber().intValue());
        assertEquals(chartOfAccountsCode, result.getChartOfAccountsCode());
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(projectCode, result.getProjectCode());
        assertEquals(new KualiDecimal(amount), result.getAmount());
    }

    /**
     */
    public void testParseSourceAccountingLine_pass_lowerCase() {
        String chartOfAccountsCode = "bl";
        String accountNumber = "463172";
        String projectCode = "kul";
        String amount = "12.31";

        StringBuffer sb = new StringBuffer();
        sb.append(chartOfAccountsCode);
        sb.append(",");
        sb.append(accountNumber);
        sb.append(",,,,");
        sb.append(projectCode);
        sb.append(",,");
        sb.append(amount);
        SourceAccountingLine result = parser.parseSourceAccountingLine(accountingDocument, sb.toString());

        assertEquals(accountingDocument.getPostingYear(), result.getPostingYear());
        assertEquals(accountingDocument.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(accountingDocument.getSourceAccountingLines().size() + 1, result.getSequenceNumber().intValue());
        assertEquals(chartOfAccountsCode.toUpperCase(), result.getChartOfAccountsCode());
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(projectCode.toUpperCase(), result.getProjectCode());
        assertEquals(new KualiDecimal(amount), result.getAmount());
    }


    /**
     */
    public void testParseTargetAccountingLine_pass_upperCase() {
        String chartOfAccountsCode = "BL";
        String accountNumber = "463172";
        String projectCode = "KUL";
        String amount = "12.31";

        StringBuffer sb = new StringBuffer();
        sb.append(chartOfAccountsCode);
        sb.append(",");
        sb.append(accountNumber);
        sb.append(",,,,");
        sb.append(projectCode);
        sb.append(",,");
        sb.append(amount);
        TargetAccountingLine result = parser.parseTargetAccountingLine(accountingDocument, sb.toString());

        assertEquals(accountingDocument.getPostingYear(), result.getPostingYear());
        assertEquals(accountingDocument.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(accountingDocument.getTargetAccountingLines().size() + 1, result.getSequenceNumber().intValue());
        assertEquals(chartOfAccountsCode, result.getChartOfAccountsCode());
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(projectCode, result.getProjectCode());
        assertEquals(new KualiDecimal(amount), result.getAmount());
    }

    /**
     */
    public void testParseTargetAccountingLine_pass_lowerCase() {
        String chartOfAccountsCode = "bl";
        String accountNumber = "463172";
        String projectCode = "kul";
        String amount = "12.31";

        StringBuffer sb = new StringBuffer();
        sb.append(chartOfAccountsCode);
        sb.append(",");
        sb.append(accountNumber);
        sb.append(",,,,");
        sb.append(projectCode);
        sb.append(",,");
        sb.append(amount);
        TargetAccountingLine result = parser.parseTargetAccountingLine(accountingDocument, sb.toString());

        assertEquals(accountingDocument.getPostingYear(), result.getPostingYear());
        assertEquals(accountingDocument.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(accountingDocument.getTargetAccountingLines().size() + 1, result.getSequenceNumber().intValue());
        assertEquals(chartOfAccountsCode.toUpperCase(), result.getChartOfAccountsCode());
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(projectCode.toUpperCase(), result.getProjectCode());
        assertEquals(new KualiDecimal(amount), result.getAmount());
    }

    /**
     */
    public void testImportSourceAccountingLine_pass() {
        String chartOfAccountsCode = "bl";
        String accountNumber = "463172";
        String projectCode = "kul";
        String amount = "12.31";

        StringBuffer sb = new StringBuffer();
        sb.append(chartOfAccountsCode);
        sb.append(",");
        sb.append(accountNumber);
        sb.append(",,,,");
        sb.append(projectCode);
        sb.append(",,");
        sb.append(amount);
        List<SourceAccountingLine> lineList = parser.importSourceAccountingLines(null, new ByteArrayInputStream(sb.toString().getBytes()), accountingDocument);
        for (SourceAccountingLine result : lineList) {
            assertEquals(accountingDocument.getPostingYear(), result.getPostingYear());
            assertEquals(accountingDocument.getDocumentNumber(), result.getDocumentNumber());
            assertEquals(accountingDocument.getSourceAccountingLines().size() + 1, result.getSequenceNumber().intValue());
            assertEquals(chartOfAccountsCode.toUpperCase(), result.getChartOfAccountsCode());
            assertEquals(accountNumber, result.getAccountNumber());
            assertEquals(projectCode.toUpperCase(), result.getProjectCode());
            assertEquals(new KualiDecimal(amount), result.getAmount());
        }
    }
    //
    // public void testImportSourceAccountingLines() throws Exception {
    // List result = null;
    // List expected = null;
    // TransactionalDocument document = getDocument();
    //
    // expected = getImportSourceAccountingLinesExpected(document);
    // result = getAccountingLineParser().importSourceAccountingLines(getImportSourceAccountingLinesFixture(), document);
    // assertTrue((result != null));
    // assertEquals(expected.toString(), result.toString());
    // }
    //
    // public void testImportTargetAccountingLines() throws Exception {
    // List result = null;
    // List expected = null;
    // TransactionalDocument document = getDocument();
    //
    // expected = getImportTargetAccountingLinesExpected(document);
    // result = getAccountingLineParser().importTargetAccountingLines(getImportTargetAccountingLinesFixture(), document);
    // assertTrue((result != null));
    // assertEquals(expected.toString(), result.toString());
    // }
    //
    // /**
    // * KULEDOCS-1317
    // *
    // * @throws Exception
    // */
    // public void testImportSourceAccountingLines_lowerCase() throws Exception {
    // StringBuffer buffer = new
    // StringBuffer().append(StringUtils.lowerCase(getSerializedTargetAccountingLine())).append("\n").append(StringUtils.lowerCase(getSerializedTargetAccountingLine()));
    // InputStream input = new ByteArrayInputStream(buffer.toString().getBytes());
    // List result = null;
    // List expected = null;
    // TransactionalDocument document = getDocument();
    // expected = getImportSourceAccountingLinesExpected(document);
    // result = getAccountingLineParser().importSourceAccountingLines(input, document);
    // assertTrue((result != null));
    // assertEquals(expected.toString(), result.toString());
    // }
}
