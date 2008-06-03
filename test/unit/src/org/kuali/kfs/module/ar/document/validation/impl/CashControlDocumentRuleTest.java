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
package org.kuali.module.ar.rules;

import static org.kuali.test.fixtures.UserNameFixture.BUTT;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.service.CashControlDocumentService;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class tests the rules in CashControlDocumentRule
 */
@ConfigureContext(session = KHUNTLEY)
public class CashControlDocumentRuleTest extends KualiTestBase {

    private CashControlDocumentRule rule;
    private CashControlDocument document;
    private DocumentService documentService;
    private PaymentApplicationDocument appDoc;

    private static final KualiDecimal ZERO_AMOUNT = new KualiDecimal(0);
    private static final KualiDecimal NEGATIVE_AMOUNT = new KualiDecimal(-1);
    private static final KualiDecimal POSITIVE_AMOUNT = new KualiDecimal(2);

    // any value that does not represent a valid payment medium code
    private static final String CUSTOMER_PAYMENT_MEDIUM_NOT_VALID_CODE = "MEH";
    private static final String REFERENCE_DOCUMENT_NUMBER = "123456";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new CashControlDocumentRule();
        document = new CashControlDocument();
        documentService = SpringContext.getBean(DocumentService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        documentService = null;
        super.tearDown();
    }

    /**
     * This method tests if validateCashControlDetails rule returns true when passed a valid line amount
     */
    public void testValidateCashControlDetails_True() {

        CashControlDetail detail1 = new CashControlDetail();
        detail1.setFinancialDocumentLineAmount(POSITIVE_AMOUNT);
        document.addCashControlDetail(detail1);

        assertTrue(rule.validateCashControlDetails(document));

    }

    /**
     * This method tests if validateCashControlDetails rule returns false when passed a negative line amount
     */
    public void testValidateCashControlDetails_Negative_False() {

        CashControlDetail detail1 = new CashControlDetail();
        detail1.setFinancialDocumentLineAmount(NEGATIVE_AMOUNT);
        document.addCashControlDetail(detail1);

        assertFalse(rule.validateCashControlDetails(document));
    }

    /**
     * This method tests if validateCashControlDetails rule returns false when passed a zero line amount
     */
    public void testValidateCashControlDetails_Zero_Amount_False() {

        CashControlDetail detail1 = new CashControlDetail();
        detail1.setFinancialDocumentLineAmount(ZERO_AMOUNT);
        document.addCashControlDetail(detail1);

        assertFalse(rule.validateCashControlDetails(document));
    }

    /**
     * This method tests that checkAllAppDocsApproved returns true when all application document are approved
     */
    public void testCheckAllAppDocsApproved_True() throws WorkflowException {

        CashControlDetail detail1 = new CashControlDetail();
        detail1.setFinancialDocumentLineAmount(POSITIVE_AMOUNT);

        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        cashControlDocumentService.addNewCashControlDetail("desc", document, detail1);
        // get the first application document from the details as it is the only one we have added
        PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) document.getCashControlDetail(0).getReferenceFinancialDocument();
        // mock a fully approved payment application document
        applicationDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().setDocRouteStatus(KFSConstants.DocumentStatusCodes.APPROVED);

    }

    /**
     * This method tests that checkAllAppDocsApproved rule returns false when at least one application document is in other state
     * than approved
     */
    public void testCheckAllAppDocsApproved_False() throws WorkflowException {

        CashControlDetail detail1 = new CashControlDetail();
        detail1.setFinancialDocumentLineAmount(POSITIVE_AMOUNT);

        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        cashControlDocumentService.addNewCashControlDetail("desc", document, detail1);

    }

    /**
     * This method tests that checkUserOrgOptions rule returns true when the user organization options are set up
     */
    public void testCheckUserOrgOptions_True() {
        // make sure current user has organization options set up
        assertTrue(rule.checkUserOrgOptions(document));
    }

    /**
     * This method tests that checkUserOrgOptions rule returns false when the user organization options are not set up
     */
    public void testCheckUserOrgOptions_False() throws Exception {
        // change to a user that does not have user organization options set up
        changeCurrentUser(BUTT);
        assertFalse(rule.checkUserOrgOptions(document));
    }

    /**
     * This method tests that checkGLPEsCreated rule returns true when glpes are not null
     */
    public void testCheckGLPEsCreated_True() throws WorkflowException {

        Document tempDocument = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        GeneralLedgerPendingEntry tempEntry =  new GeneralLedgerPendingEntry();
        documentService.saveDocument(tempDocument);
        document.getGeneralLedgerPendingEntries().add(tempEntry);

        assertTrue(rule.checkGLPEsCreated(document));
    }

    /**
     * This method tests that checkGLPEsCreated rule returns false when the glpes list is null or empty
     */
    public void testCheckGLPEsCreated_False() {

        document.setGeneralLedgerPendingEntries(null);

        assertFalse(rule.checkGLPEsCreated(document));
    }

    /**
     * This method tests that checkPaymentMedium rule returns false if payment medium is not null and has a valid value
     */
    public void testCheckPaymentMedium_True() {

        document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.CASH);

        assertTrue(rule.checkPaymentMedium(document));
    }

    /**
     * This method tests that checkPaymentMedium rule returns false if payment medium is not valid
     */
    public void testCheckPaymentMedium_NotValid_False() {

        document.setCustomerPaymentMediumCode(CUSTOMER_PAYMENT_MEDIUM_NOT_VALID_CODE);

        assertFalse(rule.checkPaymentMedium(document));
    }

    /**
     * This method tests that checkPaymentMedium rule returns false if payment medium is null
     */
    public void testCheckPaymentMedium_Null_False() {

        document.setCustomerPaymentMediumCode(null);

        assertFalse(rule.checkPaymentMedium(document));
    }


    /**
     * This method that checkRefDocNumber rule returns true if the reference document number is set and valid when payment mewdium is
     * cash
     */
    public void testCheckRefDocNumber_True() throws WorkflowException {

        GeneralErrorCorrectionDocument tempDoc = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        documentService.saveDocument(tempDoc);
        document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.CASH);
        document.setReferenceFinancialDocumentNumber(tempDoc.getDocumentNumber());

        assertTrue(rule.checkRefDocNumber(document));
    }

    /**
     * This method that checkRefDocNumber rule returns false when reference document number is null when payment mewdium is cash
     */
    public void testCheckRefDocNumber_False() {

        document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.CASH);
        document.setReferenceFinancialDocumentNumber(null);

        assertFalse(rule.checkRefDocNumber(document));

    }

    /**
     * This method that checkGLPEsNotGenerated rule returns true if the glpes are not generated
     */
    public void testCheckGLPEsNotGenerated_True() {

        document.setGeneralLedgerPendingEntries(null);

        assertTrue(rule.checkGLPEsNotGenerated(document));

    }

    /**
     * This method that checkGLPEsNotGenerated rule returns false if the glpes are generated
     */
    public void testCheckGLPEsNotGenerated_False() {

        GeneralLedgerPendingEntry tempEntry = new GeneralLedgerPendingEntry();
        document.getGeneralLedgerPendingEntries().add(tempEntry);

        assertFalse(rule.checkGLPEsNotGenerated(document));

    }

}
