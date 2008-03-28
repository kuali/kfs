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
import org.kuali.core.document.DocumentBase;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class tests the rules in CashControlDocumentRule
 */
@ConfigureContext(session = KHUNTLEY)
public class CashControlDocumentRuleTest extends KualiTestBase {

    CashControlDocumentRule rule;
    CashControlDocument document;
    DocumentService documentService;
    PaymentApplicationDocument appDoc;

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
        // TODO add implementation
    }

    /**
     * This method tests that checkAllAppDocsApproved rule returns false when at least one application document is in other state
     * than approved
     */
    public void testCheckAllAppDocsApproved_False() {
        // TODO add implementation
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
     * This method tests that checkReferenceDocument rule returns true when reference document number is not null
     */
    public void testCheckReferenceDocument_True() throws WorkflowException {

        Document tempDocument = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        documentService.saveDocument(tempDocument);
        document.setReferenceFinancialDocumentNumber(tempDocument.getDocumentNumber());

        assertTrue(rule.checkReferenceDocument(document));
    }

    /**
     * This method tests that checkReferenceDocument rule returns false when reference document number is null
     */
    public void testCheckReferenceDocument_False() {

        document.setReferenceFinancialDocumentNumber(null);

        assertFalse(rule.checkReferenceDocument(document));
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
     * This method that checkOrgDocNumber rule returns true if organization document number is set and valid when payment mewdium is
     * cash
     */
    public void testCheckOrgDocNumber_True() throws WorkflowException {

        GeneralErrorCorrectionDocument tempDoc = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        documentService.saveDocument(tempDoc);
        document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.CASH);
        document.getDocumentHeader().setOrganizationDocumentNumber(tempDoc.getDocumentNumber());

        assertTrue(rule.checkOrgDocNumber(document));
    }

    /**
     * This method that checkOrgDocNumber rule returns false if organization document number is null when payment mewdium is cash
     */
    public void testCheckOrgDocNumber_False() {

        document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.CASH);
        document.getDocumentHeader().setOrganizationDocumentNumber(null);

        assertFalse(rule.checkOrgDocNumber(document));

    }

    /**
     * This method that checkReferenceDocumentNumberNotGenerated rule returns true if reference document number is not generated
     */
    public void testCheckReferenceDocumentNumberNotGenerated_True() {

        document.setReferenceFinancialDocumentNumber(null);

        assertTrue(rule.checkReferenceDocumentNumberNotGenerated(document));

    }

    /**
     * This method that checkReferenceDocumentNumberNotGenerated rule returns false if reference document number is generated
     */
    public void testCheckReferenceDocumentNumberNotGenerated_False() {

        document.setReferenceFinancialDocumentNumber(REFERENCE_DOCUMENT_NUMBER);

        assertFalse(rule.checkReferenceDocumentNumberNotGenerated(document));

    }

}
