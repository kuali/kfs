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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class CashControlDocumentServiceTest extends KualiTestBase {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentServiceTest.class);;

    protected static final String PROCESSING_CHART_CODE = "UA";
    protected static final String PROCESSING_ORG_CODE = "AR";
    protected static final String CUSTOMER_NUMBER = "ABB2";
    protected static final KualiDecimal DETAIL_AMOUNT = new KualiDecimal("100.00");
    protected static final String PAYMENT_MEDIUM_CODE = "CK";

    CashControlDocumentService service;
    DocumentService documentService;
    AccountsReceivableDocumentHeaderService arDocHeaderService;
    DataDictionaryService dataDictionaryService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        arDocHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        service = SpringContext.getBean(CashControlDocumentService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        service = null;
        documentService = null;
        super.tearDown();
    }

    /**
     * This method test if createAndSavePaymentApplicationDocument creates and saves a payment application document
     *
     * @throws WorkflowException
     */
    public void testCreateAndSavePaymentApplicationDocument() throws WorkflowException {

        CashControlDocument cashControlDocument = createCashControlDocumentWithOneDetail();
        PaymentApplicationDocument applicationDocument = service.createAndSavePaymentApplicationDocument(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC, cashControlDocument, cashControlDocument.getCashControlDetail(0));

        assertNotNull(applicationDocument);

        PaymentApplicationDocument applicationDocument2 = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(applicationDocument.getDocumentNumber());

        assertNotNull(applicationDocument2);
        assertEquals("Document status is incorrect", DocumentStatus.SAVED, applicationDocument2.getDocumentHeader().getWorkflowDocument().getStatus());
    }

    protected CashControlDocument createCashControlDocumentWithOneDetail() {
        CashControlDocument cashControlDocument;
        try {
            cashControlDocument = (CashControlDocument)documentService.getNewDocument(dataDictionaryService.getDocumentTypeNameByClass(CashControlDocument.class));
        }
        catch (Exception e) {
            LOG.error("A Exception was thrown while trying to initiate a new CashControl document.", e);
            throw new RuntimeException("A Exception was thrown while trying to initiate a new CashControl document.", e);
        }
        cashControlDocument.setCustomerPaymentMediumCode(PAYMENT_MEDIUM_CODE);
        cashControlDocument.getDocumentHeader().setDocumentDescription("CashControlDocument created for testing.");

        //  setup the AR header for this CashControl doc
        AccountsReceivableDocumentHeader arDocHeader;
        try {
            arDocHeader = arDocHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        }
        catch (Exception e) {
            LOG.error("An Exception was thrown while trying to create a new AccountsReceivableDocumentHeader.", e);
            throw new RuntimeException("An Exception was thrown while trying to create a new AccountsReceivableDocumentHeader.", e);
        }
        arDocHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
        arDocHeader.setCustomerNumber(CUSTOMER_NUMBER);
        cashControlDocument.setAccountsReceivableDocumentHeader(arDocHeader);

        //  create a new cashcontrol detail
        CashControlDetail detail = new CashControlDetail();
        detail.setCustomerNumber(CUSTOMER_NUMBER);
        detail.setFinancialDocumentLineAmount(DETAIL_AMOUNT);
        detail.setCustomerPaymentDescription("Test Detail");

        //  add it to the document
        try {
            service.addNewCashControlDetail("Test Document", cashControlDocument, detail);
        }
        catch (Exception e) {
            LOG.error("A Exception was thrown while trying to create a new CashControl detail.", e);
            throw new RuntimeException("A Exception was thrown while trying to create a new CashControl detail.", e);
        }

        return cashControlDocument;
    }

}

