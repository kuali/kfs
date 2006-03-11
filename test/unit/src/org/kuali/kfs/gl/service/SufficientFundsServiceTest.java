/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.bo.user.Options;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.test.KualiTestBaseWithSpring;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * 
 * tests the Sufficient Funds service.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class SufficientFundsServiceTest extends KualiTestBaseWithSpring {
    private Options globalOptions;
    private Options originalOptions;
    private SufficientFundsService sufficientFundsService;
    private BusinessObjectService businessObjectService;

    private static final String ARBITRARY_VALUE = "XX";
    private static final String COLLECTION_NAME = "SufficientFundsServiceTest.collection1";
    private static final String VALID_OBJECT_LEVEL = "validObjectLevel1";
    private static final String TRASACTIONAL_DOCUMENT = "documentParameter5";
    private static final String YEAR_END_DOCUMENT = "yearEndTransferOfFundsDocumentParameter1";
    private static final String[] SOURCE_LINES = { "sufficientFundsServiceTestSourceLine1",
            "sufficientFundsServiceTestSourceLine2", "sourceLine3" };
    private static final String[] TARGET_LINES = { "targetLine1", "targetLine2", "targetLine3" };

    /**
     * @see org.kuali.test.KualiTestBaseWithSpring#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        sufficientFundsService = SpringServiceLocator.getSufficientFundsService();
        businessObjectService = SpringServiceLocator.getBusinessObjectService();
    }

    public void testGetSufficientFundsObjectCode_level_checking_object() {
        String rv = sufficientFundsService.getSufficientFundsObjectCode(null, ARBITRARY_VALUE, Constants.SF_TYPE_OBJECT, null);
        assertEquals(ARBITRARY_VALUE, rv);
    }

    public void testGetSufficientFundsObjectCode_level_checking_level() {
        String rv = sufficientFundsService.getSufficientFundsObjectCode(null, null, Constants.SF_TYPE_LEVEL, ARBITRARY_VALUE);
        assertEquals(ARBITRARY_VALUE, rv);
    }

    public void testGetSufficientFundsObjectCode_default_return_value() {
        String rv = sufficientFundsService.getSufficientFundsObjectCode(null, null, ARBITRARY_VALUE, null);
        assertEquals(Constants.NOT_AVAILABLE_STRING, rv);
    }

    public void testGetSufficientFundsObjectCode_level_checking_consolidation_invalidObjectLevel_nullKey() {
        boolean faildAsExpected = false;
        try {
            sufficientFundsService.getSufficientFundsObjectCode(null, null, Constants.SF_TYPE_CONSOLIDATION, null);
        }
        catch (IllegalArgumentException e) {
            faildAsExpected = true;
        }

        assertTrue(faildAsExpected);
    }

    public void testGetSufficientFundsObjectCode_level_checking_consolidation_invalidObjectLevel_InvalidKey() {
        boolean faildAsExpected = false;
        try {
            sufficientFundsService.getSufficientFundsObjectCode("QU", null, Constants.SF_TYPE_CONSOLIDATION, "BADVALUE");
        }
        catch (IllegalArgumentException e) {
            faildAsExpected = true;
        }

        assertTrue(faildAsExpected);
    }

    public void testGetSufficientFundsObjectCode_level_checking_consolidation() {
        ObjLevel objectLevel = getObjectLevelFixture();
        String rvObjectCode = sufficientFundsService.getSufficientFundsObjectCode(objectLevel.getChartOfAccountsCode(), null,
                Constants.SF_TYPE_CONSOLIDATION, objectLevel.getFinancialObjectLevelCode());

        assertEquals(objectLevel.getFinancialConsolidationObjectCode(), rvObjectCode);
    }

    /**
     * <code>
     public void testCheckSufficientfunds_global_budget_checking_disabled() throws Exception {
     TransactionalDocument document = getTransactionalDocumentFromFixture(TRASACTIONAL_DOCUMENT, Constants.GL_CREDIT_CODE);
     setOptionsDisableBudgetChecking(document.getPostingYear());

     // override dao to ensure that budget checking is disabled
     boolean isSufficentFunds = sufficientFundsService.checkSufficientFunds(document);
     assertTrue(isSufficentFunds);

     }

     public void testCheckSufficientfunds_global_budget_checking_enabled() throws Exception {
     TransactionalDocument document = getTransactionalDocumentFromFixture(TRASACTIONAL_DOCUMENT, Constants.GL_CREDIT_CODE);
     setOptionsEnableBudgetChecking(document.getPostingYear());

     // override dao to ensure that budget checking is disabled
     boolean isSufficentFunds = sufficientFundsService.checkSufficientFunds(document);
     assertTrue(isSufficentFunds);

     }

     public void testCheckSufficientFunds_debit_code() throws Exception {

     TransactionalDocument document = getTransactionalDocumentFromFixture(TRASACTIONAL_DOCUMENT, Constants.GL_DEBIT_CODE);
     setOptionsEnableBudgetChecking(document.getPostingYear());

     // override dao to ensure that budget checking is disabled
     boolean isSufficentFunds = sufficientFundsService.checkSufficientFunds(document);
     assertTrue(isSufficentFunds);
     }

     public void testCheckSufficientFunds_invalid_fiscalYear_null() throws Exception {
     boolean failedAsExpected = false;

     TransactionalDocument document = getTransactionalDocumentFromFixture(TRASACTIONAL_DOCUMENT, Constants.GL_CREDIT_CODE);

     setOptionsEnableBudgetChecking(document.getPostingYear());
     List lines = document.getSourceAccountingLines();
     SourceAccountingLine line = (SourceAccountingLine) lines.get(0);
     line.setPostingYear(null);
     line.setDebitCreditCode(Constants.GL_CREDIT_CODE);

     document.getTargetAccountingLines().clear();
     document.getSourceAccountingLines().clear();

     lines.add(line);
     document.setSourceAccountingLines(lines);
     try {
     sufficientFundsService.checkSufficientFunds(document);
     }
     catch (IllegalArgumentException e) {
     failedAsExpected = StringUtils.equals("Invalid (null) universityFiscalYear", e.getMessage());
     }
     assertTrue(failedAsExpected);
     }

     public void testCheckSufficientFunds_invalid_Account_null() throws Exception {
     boolean failedAsExpected = false;

     TransactionalDocument document = getTransactionalDocumentFromFixture(TRASACTIONAL_DOCUMENT, Constants.GL_CREDIT_CODE);

     setOptionsEnableBudgetChecking(document.getPostingYear());
     List lines = document.getSourceAccountingLines();
     SourceAccountingLine line = (SourceAccountingLine) lines.get(0);
     line.setChartOfAccountsCode(null);
     line.setDebitCreditCode(Constants.GL_CREDIT_CODE);

     document.getTargetAccountingLines().clear();
     document.getSourceAccountingLines().clear();

     lines.add(line);
     document.setSourceAccountingLines(lines);
     try {
     sufficientFundsService.checkSufficientFunds(document);
     }
     catch (IllegalArgumentException e) {
     failedAsExpected = StringUtils.contains(e.getMessage(), "Invalid (null) account for");
     }
     assertTrue(failedAsExpected);
     }

     public void testCheckSufficientFunds_YearEndDocument() throws Exception {
     TransactionalDocument document = getTransactionalDocumentFromFixture(YEAR_END_DOCUMENT, Constants.GL_CREDIT_CODE);
     setOptionsEnableBudgetChecking(document.getPostingYear());

     // override dao to ensure that budget checking is disabled
     boolean isSufficentFunds = sufficientFundsService.checkSufficientFunds(document);
     assertFalse(isSufficentFunds);
     }

     public void testCheckSufficientFunds_retrieveOptions_invalid_fiscalYear_null() throws Exception {
     boolean failedAsExpected = false;

     TransactionalDocument document = getTransactionalDocumentFromFixture(TRASACTIONAL_DOCUMENT, Constants.GL_CREDIT_CODE);
     setOptionsEnableBudgetChecking(document.getPostingYear());
     document.setPostingYear(new Integer(0));
     try {
     sufficientFundsService.checkSufficientFunds(document);
     }
     catch (IllegalArgumentException e) {
     failedAsExpected = StringUtils.contains(e.getMessage(), "Invalid (null) Options for universityFiscalYear");
     }
     assertTrue(failedAsExpected);
     }

     public void testCheckSufficientFunds_multipleLines_for_same_account() throws Exception {
     TransactionalDocument document = getTransactionalDocumentFromFixture(TRASACTIONAL_DOCUMENT, Constants.GL_CREDIT_CODE);

     setOptionsEnableBudgetChecking(document.getPostingYear());
     List lines = document.getSourceAccountingLines();
     SourceAccountingLine line = (SourceAccountingLine) lines.get(0);
     line.setDebitCreditCode(Constants.GL_CREDIT_CODE);

     lines.add(line);
     SourceAccountingLine line2 = (SourceAccountingLine) ObjectUtils.deepCopy(line);
     line2.setDebitCreditCode(null);
     lines.add(line2);
     document.setSourceAccountingLines(lines);
     boolean isSufficientFunds = sufficientFundsService.checkSufficientFunds(document);
     assertFalse(isSufficientFunds);
     }
     </code>
     */
    /**
     * @see org.kuali.test.KualiTestBaseWithSpring#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        //removePreparedOptions();
    }

    // fixture accessor methods
    private ObjLevel getObjectLevelFixture() {
        return (ObjLevel) getFixtureEntryFromCollection(COLLECTION_NAME, VALID_OBJECT_LEVEL).createObject();
    }

    private TransactionalDocument getTransactionalDocumentFromFixture(String documentFixture, String debitCreditCode)
            throws Exception {
        TransactionalDocumentParameter parameter = (TransactionalDocumentParameter) getFixtureEntryFromCollection(COLLECTION_NAME,
                documentFixture).createObject();
        TransactionalDocument document = (TransactionalDocument) parameter.createDocument(getDocumentService());
        AccountingLine line = null;
        for (Iterator i = getAccountingLineFromFixtures(SOURCE_LINES).iterator(); i.hasNext();) {
            line = ((AccountingLineParameter) i.next()).createLine(document.getFinancialDocumentNumber());
            line.setDebitCreditCode(debitCreditCode);
            document.addSourceAccountingLine((SourceAccountingLine) line);
        }
        for (Iterator i = getAccountingLineFromFixtures(TARGET_LINES).iterator(); i.hasNext();) {
            line = ((AccountingLineParameter) i.next()).createLine(document.getFinancialDocumentNumber());
            line.setDebitCreditCode(debitCreditCode);
            document.addTargetAccountingLine((TargetAccountingLine) line);
        }

        return document;
    }

    private List getAccountingLineFromFixtures(String[] lines) {
        List list = new ArrayList();
        for (int i = 0; i < lines.length; i++) {
            list.add(getFixtureEntryFromCollection(COLLECTION_NAME, lines[i]).createObject());
        }
        return list;
    }


    // test util methods
    private void setOptionsDisableBudgetChecking(Integer fiscalYear) {
        globalOptions = new Options();
        globalOptions.setBudgetCheckingOptionsCode("N");

        prepareOptions(fiscalYear);
    }

    private void setOptionsEnableBudgetChecking(Integer fiscalYear) {
        globalOptions = new Options();
        globalOptions.setBudgetCheckingOptionsCode(Constants.BUDGET_CHECKING_OPTIONS_CD_ACTIVE);

        prepareOptions(fiscalYear);
    }


    private void prepareOptions(Integer fiscalYear) {
        Map keyMap = new HashMap();
        keyMap.put("universityFiscalYear", fiscalYear);
        originalOptions = (Options) businessObjectService.findByPrimaryKey(Options.class, keyMap);
        if (originalOptions != null) {
            Options tmpOptions = (Options) ObjectUtils.deepCopy(originalOptions);

            tmpOptions.setBudgetCheckingOptionsCode(globalOptions.getBudgetCheckingOptionsCode());
            tmpOptions.setVersionNumber(null);
            tmpOptions.setObjectId(null);
            businessObjectService.delete(originalOptions);

            businessObjectService.save(tmpOptions);
            // verify existance
            globalOptions = (Options) businessObjectService.retrieve(tmpOptions);
            assertNotNull(globalOptions);
        }
    }

    private void removePreparedOptions() {
        if (originalOptions != null) {
            // remove modified options
            businessObjectService.delete(originalOptions);
            // restore original options
            originalOptions.setVersionNumber(null);
            originalOptions.setObjectId(null);
            businessObjectService.save(originalOptions);
            globalOptions = (Options) businessObjectService.retrieve(globalOptions);

            globalOptions.setVersionNumber(null);
            originalOptions.setVersionNumber(null);
            globalOptions.setObjectId(null);
            originalOptions.setObjectId(null);
            assertNotNull(globalOptions);

            assertEquals(originalOptions.getUniversityFiscalYear(), globalOptions.getUniversityFiscalYear());
            assertEquals(originalOptions.getBudgetCheckingOptionsCode(), globalOptions.getBudgetCheckingOptionsCode());

            globalOptions = null;
            originalOptions = null;
        }
    }
}
