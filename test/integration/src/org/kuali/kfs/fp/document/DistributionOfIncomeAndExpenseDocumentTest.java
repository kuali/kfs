package org.kuali.module.financial.document;

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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class is used to test DistributionOfIncomeAndExpenseDocument.
 * 
 * @author Kuali Transaction Processing Team (kualidev@oncourse.iu.edu)
 */
public class DistributionOfIncomeAndExpenseDocumentTest extends TransactionalDocumentTestBase {
    public static final String COLLECTION_NAME = "DistributionOfIncomeAndExpenseDocumentTest.collection1";
    public static final String USER_NAME = "user1";
    public static final String DOCUMENT_PARAMETER = "distributionOfIncomeAndExpenseDocumentParameter1";
    public static final String SOURCE_LINE1 = "sourceLine2";
    public static final String TARGET_LINE1 = "targetLine2";
    public static final String SERIALIZED_LINE_PARAMTER = "serializedLine1";

    /**
     * Get names of fixture collections test class is using.
     *
     * @return String[]
     */
    public String[] getFixtureCollectionNames() {
        return new String[] { COLLECTION_NAME };
    }

    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public DocumentParameter getDocumentParameterFixture() {
        return (TransactionalDocumentParameter) getFixtureEntryFromCollection(COLLECTION_NAME, DOCUMENT_PARAMETER).createObject();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    public List getTargetAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, TARGET_LINE1).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    public List getSourceAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, SOURCE_LINE1).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getUserName()
     */
    public String getUserName() {
        return (String) getFixtureEntryFromCollection(COLLECTION_NAME, USER_NAME).createObject();
    }
    
    // START TEST METHODS
    /**
     * Overrides the parent to do nothing since the DofI&E doesn't set the posting period in 
     * the record it stores.  This test doesn't apply to this type of document.
     */
    public final void testConvertIntoCopy_invalidYear() throws Exception {
        //do nothing to pass
    }
    
    /**
     * Overrides the parent to do nothing since the DofI&E doesn't set the posting period in 
     * the record it stores.  This test doesn't apply to this type of document.
     */
    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        //do nothing to pass
    }

    /*
     * THIS IS OLD TEST CODE THAT SHOULD BE CONSIDERED AND POSSIBLY IMPLEMENTED WHEN WE GET TO THIS DOCUMENT 
     * AS PART OF THE WORK CYCLE - IT WILL MOSTLY LIKE GO IN THE RULE TEST CLASS
     * 
     * public final void testGetNewDocument_knownUser_knownDocumentType() throws Exception {
     Document document = SpringServiceLocator.getDocumentService().getNewDocument(KNOWN_DOCUMENT_TYPENAME);
     
     assertNotNull( document );
     assertNotNull( document.getDocumentHeader() );
     assertNotNull( document.getDocumentHeader().getFinancialDocumentNumber() );
     }

     public final void testApplyAddAccountingLineBusinessRulesInvalidSubObjectCode() throws Exception {
     DistributionOfIncomeAndExpenseDocument didoc = createInvalidRulesDIDocumentInvalidSubObjectCode();

     // Check business rules
     List src = didoc.getSourceAccountingLines();
     List dst = didoc.getTargetAccountingLines();

     for (Iterator i = src.iterator(); i.hasNext();) {
     SourceAccountingLine sourceAccountingLine = (SourceAccountingLine) i.next();
     sourceAccountingLine.refresh();

     assertFalse(SpringServiceLocator.getKualiRuleService().applyRules(
     new AddAccountingLineEvent(didoc, sourceAccountingLine)));
     }

     for (Iterator i = dst.iterator(); i.hasNext();) {
     TargetAccountingLine targetAccountingLine = (TargetAccountingLine) i.next();
     targetAccountingLine.refresh();

     assertFalse(SpringServiceLocator.getKualiRuleService().applyRules(
     new AddAccountingLineEvent(didoc, targetAccountingLine)));
     }
     }

     public final void testApplyAddAccountingLineBusinessRulesValidSubObjectCode() throws Exception {
     DistributionOfIncomeAndExpenseDocument didoc = createValidRulesDIDocument();

     // Check business rules
     List src = didoc.getSourceAccountingLines();
     List dst = didoc.getTargetAccountingLines();

     for (Iterator i = src.iterator(); i.hasNext();) {
     SourceAccountingLine sourceAccountingLine = (SourceAccountingLine) i.next();
     sourceAccountingLine.refresh();

     assertTrue(SpringServiceLocator.getKualiRuleService().applyRules(
     new AddAccountingLineEvent(didoc, sourceAccountingLine)));
     }

     for (Iterator i = dst.iterator(); i.hasNext();) {
     TargetAccountingLine targetAccountingLine = (TargetAccountingLine) i.next();
     targetAccountingLine.refresh();

     assertTrue(SpringServiceLocator.getKualiRuleService().applyRules(
     new AddAccountingLineEvent(didoc, targetAccountingLine)));
     }
     }

     public final void testApplyRouteDocumentBusinessRules_invalidDocument() throws Exception {
     // TODO: Create an appropriate document.
     TransactionalDocument document = createInvalidRulesDIDocumentNotInBalance();
     assertFalse(SpringServiceLocator.getKualiRuleService().applyRules(new RouteDocumentEvent(document)));

     document = createInvalidRulesDIDocumentInvalidSubObjectCode();
     assertFalse(SpringServiceLocator.getKualiRuleService().applyRules(new RouteDocumentEvent(document)));
     }

     public final void testApplyRouteDocumentBusinessRules_validDocument() throws Exception {
     // TODO: Create an appropriate document.
     TransactionalDocument document = createValidRulesDIDocument();
     assertTrue(SpringServiceLocator.getKualiRuleService().applyRules(new RouteDocumentEvent(document)));
     }


     private static final String CHART = "BL";
     private static final String ACCOUNT = "1031400";
     private static final String SUB_ACCT = "ADV";

     //   MT :: 5197, 5198, 1696, 1699, 1669, 9959, 9977, 9903
     //   TN :: 9918, 9900, 9924, 9951, 9912, 5163, 1663, 5216, 9915, 9920, 9923, 9925, 9930

     private static final String MT_OBJ_CODE = "5197";
     private static final String TN_OBJ_CODE = "9918";
     private static final String UNKNOWN_SUB_OBJ_CODE = "YYZZY";
     private static final String SUB_OBJ_CODE = "SSS";
     private static final String PROJECT = "KUL3";
     private static BalanceTyp BAL_TYP = new BalanceTyp();
     private static final int YEAR = 2004;
     private static final KualiDecimal AMT1 = new KualiDecimal("2.50");
     private static final KualiDecimal AMT2 = new KualiDecimal("5.20");

     final private DistributionOfIncomeAndExpenseDocument createValidRulesDIDocument() throws Exception {
     // Get a new object, unpopulated
     DistributionOfIncomeAndExpenseDocument didoc = (DistributionOfIncomeAndExpenseDocument) SpringServiceLocator
     .getDocumentService().getNewDocument(KNOWN_DOCUMENT_TYPENAME);

     // Set attributes on the document itself
     didoc.setPostingYear(new Integer(YEAR));
     didoc.setExplanation("This is a test document, testing valid accounting line business rules.");
     didoc.getDocumentHeader().setFinancialDocumentDescription(
     "testing DistributionOfIncomeAndExpenseDocumentServiceTest.createValidRuleDIDocument");
     String documentHeaderId = didoc.getFinancialDocumentNumber();

     // Setup income lines
     SourceAccountingLine incomeLine = DocumentTestUtils.createSourceLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT,
     TN_OBJ_CODE, SUB_OBJ_CODE, PROJECT, YEAR, AMT1, 1, "", "", "", "", "", "", "");
     didoc.addSourceAccountingLine(incomeLine);
     incomeLine = DocumentTestUtils.createSourceLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT, MT_OBJ_CODE, SUB_OBJ_CODE,
     PROJECT, YEAR, AMT2, 2, "", "", "", "", "", "", "");
     didoc.addSourceAccountingLine(incomeLine);

     // Setup expense lines
     TargetAccountingLine expenseLine = DocumentTestUtils.createTargetLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT,
     TN_OBJ_CODE, SUB_OBJ_CODE, PROJECT, YEAR, AMT1, 1, "", "", "", "", "", "", "");
     didoc.addTargetAccountingLine(expenseLine);
     expenseLine = DocumentTestUtils.createTargetLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT, MT_OBJ_CODE, SUB_OBJ_CODE,
     PROJECT, YEAR, AMT2, 2, "", "", "", "", "", "", "");
     didoc.addTargetAccountingLine(expenseLine);

     return didoc;
     }

     final private DistributionOfIncomeAndExpenseDocument createInvalidRulesDIDocumentNotInBalance() throws Exception {
     // Get a new object, unpopulated
     DistributionOfIncomeAndExpenseDocument didoc = (DistributionOfIncomeAndExpenseDocument) SpringServiceLocator
     .getDocumentService().getNewDocument(KNOWN_DOCUMENT_TYPENAME);

     // Set attributes on the document itself
     didoc.setPostingYear(new Integer(YEAR));
     didoc.setExplanation("This is a test document, testing valid accounting line business rules.");
     didoc.getDocumentHeader().setFinancialDocumentDescription(
     "testing DistributionOfIncomeAndExpenseDocumentServiceTest.createValidRuleDIDocument");
     String documentHeaderId = didoc.getFinancialDocumentNumber();

     // Setup income lines
     SourceAccountingLine incomeLine = DocumentTestUtils.createSourceLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT,
     TN_OBJ_CODE, SUB_OBJ_CODE, PROJECT, YEAR, AMT1, 1, "", "", "", "", "", "", "");
     didoc.addSourceAccountingLine(incomeLine);
     incomeLine = DocumentTestUtils.createSourceLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT, MT_OBJ_CODE, SUB_OBJ_CODE,
     PROJECT, YEAR, AMT1, 2, "", "", "", "", "", "", "");
     didoc.addSourceAccountingLine(incomeLine);

     // Setup expense lines
     TargetAccountingLine expenseLine = DocumentTestUtils.createTargetLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT,
     TN_OBJ_CODE, SUB_OBJ_CODE, PROJECT, YEAR, AMT2, 1, "", "", "", "", "", "", "");
     didoc.addTargetAccountingLine(expenseLine);
     expenseLine = DocumentTestUtils.createTargetLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT, MT_OBJ_CODE, SUB_OBJ_CODE,
     PROJECT, YEAR, AMT2, 2, "", "", "", "", "", "", "");
     didoc.addTargetAccountingLine(expenseLine);

     return didoc;
     }

     final private DistributionOfIncomeAndExpenseDocument createInvalidRulesDIDocumentInvalidSubObjectCode() throws Exception {
     // Get a new object, unpopulated
     DistributionOfIncomeAndExpenseDocument didoc = (DistributionOfIncomeAndExpenseDocument) SpringServiceLocator
     .getDocumentService().getNewDocument(KNOWN_DOCUMENT_TYPENAME);

     // Set attributes on the document itself
     didoc.setPostingYear(new Integer(YEAR));
     didoc.setExplanation("This is a test document, testing valid accounting line business rules.");
     didoc.getDocumentHeader().setFinancialDocumentDescription(
     "testing DistributionOfIncomeAndExpenseDocumentServiceTest.createValidRuleDIDocument");
     String documentHeaderId = didoc.getFinancialDocumentNumber();

     // Setup income lines
     SourceAccountingLine incomeLine = DocumentTestUtils.createSourceLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT,
     TN_OBJ_CODE, UNKNOWN_SUB_OBJ_CODE, PROJECT, YEAR, AMT1, 1, "", "", "", "", "", "", "");
     didoc.addSourceAccountingLine(incomeLine);
     incomeLine = DocumentTestUtils.createSourceLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT, MT_OBJ_CODE, SUB_OBJ_CODE,
     PROJECT, YEAR, AMT2, 2, "", "", "", "", "", "", "");
     didoc.addSourceAccountingLine(incomeLine);

     // Setup expense lines
     TargetAccountingLine expenseLine = DocumentTestUtils.createTargetLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT,
     TN_OBJ_CODE, SUB_OBJ_CODE, PROJECT, YEAR, AMT1, 1, "", "", "", "", "", "", "");
     didoc.addTargetAccountingLine(expenseLine);
     expenseLine = DocumentTestUtils.createTargetLine(documentHeaderId, CHART, ACCOUNT, SUB_ACCT, MT_OBJ_CODE, SUB_OBJ_CODE,
     PROJECT, YEAR, AMT2, 2, "", "", "", "", "", "", "");
     didoc.addTargetAccountingLine(expenseLine);

     return didoc;
     }
     */
}
