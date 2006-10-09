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
package org.kuali.module.financial.document;

import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.test.fixtures.AccountingLineFixture.GEC_LINE1;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.module.financial.bo.GECSourceAccountingLine;
import org.kuali.module.financial.bo.GECTargetAccountingLine;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;

/**
 * This class is used to test GeneralErrorCorrectionDocument.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class GeneralErrorCorrectionDocumentTest extends TransactionalDocumentTestBase {

    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createTransactionalDocument(getDocumentService(), GeneralErrorCorrectionDocument.class, 2007, "07");
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    @Override
    public List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(GEC_LINE1);
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    @Override
    public List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(GEC_LINE1);
        return list;
    }
    /**
     * need to use GEC accounting line types
     * @see org.kuali.core.document.TransactionalDocumentTestBase#buildDocument()
     */
    @Override
    protected Document buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        TransactionalDocument document = (TransactionalDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            
            document.addSourceAccountingLine(sourceFixture.createAccountingLine(GECSourceAccountingLine.class, document.getFinancialDocumentNumber(), document.getPostingYear(), document.getNextSourceLineNumber()));
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            document.addTargetAccountingLine(targetFixture.createAccountingLine(GECTargetAccountingLine.class, document.getFinancialDocumentNumber(), document.getPostingYear(), document.getNextTargetLineNumber()));
        }

        return document;
    }
    /*
     * TODO: OLD CODE FOR FUTURE CONSIDERATION WHEN WE GET TO THIS DOCUMENT This should probably go in the rule test class
     * 
     * public void testFailBusinessRules() throws WorkflowException, IllegalObjectStateException, Exception {
     * GeneralErrorCorrectionDocument documentOne = getBasicDocument(); SourceAccountingLine sourceAccountingLineOne =
     * (SourceAccountingLine) DocumentServiceTestUtil.createLine(SourceAccountingLine.class, "UA", "1912610", "BEER", "1175", null,
     * "BOB", new Integer(2005), new KualiDecimal("2.50"), null, "", "", "", "");
     * sourceAccountingLineOne.getObjectCode().setFinancialObjectType(new ObjectType("IC"));
     * 
     * assertFalse(SpringServiceLocator.getKualiRuleService().applyRules( new AddAccountingLineEvent(documentOne,
     * sourceAccountingLineOne)));
     * 
     * GeneralErrorCorrectionDocument documentTwo = getBasicDocument(); SourceAccountingLine sourceAccountingLineTwo =
     * (SourceAccountingLine) DocumentServiceTestUtil.createLine(SourceAccountingLine.class, "UA", "1912610", "BEER", "5120", null,
     * "BOB", new Integer(2005), new KualiDecimal("2.50"), null, "", "", "", ""); sourceAccountingLineTwo.setObjectType(new
     * ObjectType("ES")); sourceAccountingLineTwo.getObjectCode().setFinancialObjectSubType(new ObjSubTyp("DR"));
     * assertFalse(SpringServiceLocator.getKualiRuleService().applyRules( new AddAccountingLineEvent(documentTwo,
     * sourceAccountingLineTwo)));
     * 
     * GeneralErrorCorrectionDocument documentThree = getBasicDocument(); SourceAccountingLine sourceAccountingLineThree =
     * (SourceAccountingLine) DocumentServiceTestUtil.createLine(SourceAccountingLine.class, "UA", "1912610", "BEER", "5102", null,
     * "BOB", new Integer(2005), new KualiDecimal("2.50"), null, "", "", "", ""); sourceAccountingLineThree.setObjectType(new
     * ObjectType("ES")); sourceAccountingLineThree.getObjectCode().setFinancialObjectSubType(new ObjSubTyp("VA"));
     * 
     * assertFalse(SpringServiceLocator.getKualiRuleService().applyRules( new AddAccountingLineEvent(documentThree,
     * sourceAccountingLineThree)));
     * 
     * GeneralErrorCorrectionDocument documentFour = getBasicDocument(); SourceAccountingLine sourceAccountingLineFour =
     * (SourceAccountingLine) DocumentServiceTestUtil.createLine(SourceAccountingLine.class, "UA", "1912610", "BEER", "3050", null,
     * "BOB", new Integer(2005), new KualiDecimal("2.50"), null, "", "", "", ""); sourceAccountingLineFour.setObjectType(new
     * ObjectType("EX")); sourceAccountingLineFour.getObjectCode().setFinancialObjectSubType(new ObjSubTyp("HW"));
     * assertFalse(SpringServiceLocator.getKualiRuleService().applyRules( new AddAccountingLineEvent(documentFour,
     * sourceAccountingLineFour)));
     * 
     * GeneralErrorCorrectionDocument documentFive = getBasicDocument(); SourceAccountingLine sourceAccountingLineFive =
     * (SourceAccountingLine) DocumentServiceTestUtil.createLine(SourceAccountingLine.class, "UA", "1912610", "BEER", "8000", null,
     * "BOB", new Integer(2005), new KualiDecimal("2.50"), null, "", "", "", ""); sourceAccountingLineFive.setObjectType(new
     * ObjectType("AS")); sourceAccountingLineFive.getObjectCode().setFinancialObjectSubType(new ObjSubTyp("CA"));
     * assertFalse(SpringServiceLocator.getKualiRuleService().applyRules( new AddAccountingLineEvent(documentFive,
     * sourceAccountingLineFive)));
     * 
     * GeneralErrorCorrectionDocument documentSix = getBasicDocument(); SourceAccountingLine sourceAccountingLineSix =
     * (SourceAccountingLine) DocumentServiceTestUtil.createLine(SourceAccountingLine.class, "UA", "1912610", "BEER", "5166", null,
     * "BOB", new Integer(2005), new KualiDecimal("2.50"), null, "", "", "", ""); sourceAccountingLineSix.setObjectType(new
     * ObjectType("ES")); sourceAccountingLineSix.getObjectCode().setFinancialObjectSubType(new ObjSubTyp("NA"));
     * assertFalse(SpringServiceLocator.getKualiRuleService().applyRules( new AddAccountingLineEvent(documentSix,
     * sourceAccountingLineSix))); }
     * 
     * public void testPassBusinessRules() throws WorkflowException, IllegalObjectStateException, Exception {
     * GeneralErrorCorrectionDocument documentOne = getBasicDocument(); SourceAccountingLine sourceAccountingLineOne =
     * (SourceAccountingLine) DocumentServiceTestUtil.createLine(SourceAccountingLine.class, "UA", "1912610", "BEER", "2000", null,
     * "BOB", new Integer(2005), new KualiDecimal("2.50"), null, "", "", "", "");
     * sourceAccountingLineOne.getObjectCode().setFinancialObjectType(new ObjectType("ES"));
     * sourceAccountingLineOne.getObjectCode().setFinancialObjectSubType(new ObjSubTyp("SA"));
     * 
     * assertFalse(SpringServiceLocator.getKualiRuleService().applyRules( new AddAccountingLineEvent(documentOne,
     * sourceAccountingLineOne))); }
     */
}
