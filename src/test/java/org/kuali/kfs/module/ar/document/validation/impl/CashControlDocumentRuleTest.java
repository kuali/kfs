/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests the rules in CashControlDocumentRule
 */
@ConfigureContext(session = khuntley)
public class CashControlDocumentRuleTest extends KualiTestBase {

    protected CashControlDocumentRule rule;
    protected CashControlDocument document;
    protected DocumentService documentService;
    protected CashControlDocumentService cashControlDocumentService;
    protected AccountsReceivableDocumentHeaderService arDocHeaderService;
    protected PaymentApplicationDocument appDoc;

    protected static final KualiDecimal ZERO_AMOUNT = KualiDecimal.ZERO;
    protected static final KualiDecimal NEGATIVE_AMOUNT = new KualiDecimal(-1);
    protected static final KualiDecimal POSITIVE_AMOUNT = new KualiDecimal(2);

    // any value that does not represent a valid payment medium code
    protected static final String CUSTOMER_PAYMENT_MEDIUM_NOT_VALID_CODE = "MEH";
    protected static final String REFERENCE_DOCUMENT_NUMBER = "123456";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new CashControlDocumentRule();
        documentService = SpringContext.getBean(DocumentService.class);
        cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        arDocHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        document = createCashControlDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        documentService = null;
        cashControlDocumentService = null;
        super.tearDown();
    }

    protected CashControlDocument createCashControlDocument() throws WorkflowException {

        CashControlDocument doc = (CashControlDocument) documentService.getNewDocument(CashControlDocument.class);
        doc.getDocumentHeader().setDocumentDescription("This is a test document.");
        doc.setCustomerPaymentMediumCode("CK");

        AccountsReceivableDocumentHeader arDocHeader = arDocHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        arDocHeader.setDocumentNumber(doc.getDocumentNumber());
        doc.setAccountsReceivableDocumentHeader(arDocHeader);

        try {
            documentService.saveDocument(doc);
        } catch ( ValidationException ex ) {
            fail( "Unable to save document - failed validation: \n" + dumpMessageMapErrors() + "\nDocument: " + doc);
        }

        return doc;
    }

    /**
     * This method tests if validateCashControlDetails rule returns true when passed a valid line amount
     */
    public void testValidateCashControlDetails_True() {

        CashControlDetail detail1 = new CashControlDetail();
        detail1.setFinancialDocumentLineAmount(POSITIVE_AMOUNT);
        document.addCashControlDetail(detail1);

        assertTrue("Document should have passed validation. " + GlobalVariables.getMessageMap(), rule.validateCashControlDetails(document));

    }

    /**
     * This method tests if validateCashControlDetails rule returns false when passed a negative line amount
     */
    public void testValidateCashControlDetails_Negative_False() {

        CashControlDetail detail1 = new CashControlDetail();
        detail1.setFinancialDocumentLineAmount(NEGATIVE_AMOUNT);
        document.addCashControlDetail(detail1);

        assertFalse("Document should have failed validation", rule.validateCashControlDetails(document));
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

//    /**
//     * This method tests that checkAllAppDocsApproved returns true when all application document are approved
//     */
//    public void testCheckAllAppDocsApproved_True() throws WorkflowException {
//
//        CashControlDetail detail1 = new CashControlDetail();
//        detail1.setFinancialDocumentLineAmount(POSITIVE_AMOUNT);
//
//        cashControlDocumentService.addNewCashControlDetail("desc", document, detail1);
//        // get the first application document from the details as it is the only one we have added
//        PaymentApplicationDocument applicationDocument = document.getCashControlDetail(0).getReferenceFinancialDocument();
//        // mock a fully approved payment application document
//        applicationDocument.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
//
//    }

//    /**
//     * This method tests that checkAllAppDocsApproved rule returns false when at least one application document is in other state
//     * than approved
//     */
//    public void testCheckAllAppDocsApproved_False() throws WorkflowException {
//
//        CashControlDetail detail1 = new CashControlDetail();
//        detail1.setFinancialDocumentLineAmount(POSITIVE_AMOUNT);
//
//        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
//        cashControlDocumentService.addNewCashControlDetail("desc", document, detail1);
//
//    }

    /**
     * This method tests that checkGLPEsCreated rule returns true when glpes are not null
     */
    public void testCheckGLPEsCreated_True() throws WorkflowException {

        Document tempDocument = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        GeneralLedgerPendingEntry tempEntry =  new GeneralLedgerPendingEntry();
        documentService.saveDocument(tempDocument);
        document.getGeneralLedgerPendingEntries().add(tempEntry);

        assertTrue("GLPE's should have been created: " + dumpMessageMapErrors(),rule.checkGLPEsCreated(document));
    }

    /**
     * This method tests that checkGLPEsCreated rule returns false when the glpes list is null or empty
     */
    public void testCheckGLPEsCreated_False() {

        document.setGeneralLedgerPendingEntries(null);

        assertFalse("GLPE's should not have been created", rule.checkGLPEsCreated(document));
    }

    /**
     * This method tests that checkPaymentMedium rule returns false if payment medium is not null and has a valid value
     */
    public void testCheckPaymentMedium_True() {

        document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.CASH);

        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(), rule.checkPaymentMedium(document));
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

        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(),rule.checkRefDocNumber(document));
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

        assertTrue("Business Rules should not have failed: " + dumpMessageMapErrors(), rule.checkGLPEsNotGenerated(document));

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

