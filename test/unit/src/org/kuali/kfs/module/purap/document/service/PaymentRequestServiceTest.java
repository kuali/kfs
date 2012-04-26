/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = UserNameFixture.appleton)
public class PaymentRequestServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceTest.class);

    private DocumentService documentService;
    // private PaymentRequestDocument document;
    private KualiDecimal defaultMinimumLimit;
    private ParameterService parameterService;
    private NegativePaymentRequestApprovalLimitService npras;
    private PaymentRequestService paymentRequestService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        if (null == documentService) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        if (null == parameterService) {
            parameterService = SpringContext.getBean(ParameterService.class);
            String samt = parameterService.getParameterValueAsString(PaymentRequestDocument.class, PurapParameterConstants.PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT);
            defaultMinimumLimit = new KualiDecimal(samt);
        }
        if (null == npras) {
            npras = SpringContext.getBean(NegativePaymentRequestApprovalLimitService.class);
        }
        if (null == paymentRequestService) {
            paymentRequestService = SpringContext.getBean(PaymentRequestService.class);
        }
    }

    private void cancelDocument(Document document) throws WorkflowException {
        documentService.cancelDocument(document, "testing complete");
    }

    private void header(Document document) {
        document.getDocumentHeader().setDocumentDescription("test");
    }

    private PaymentRequestDocument createBasicDocument() throws WorkflowException {

        RequisitionDocument requisitionDocument = (RequisitionDocument) documentService.getNewDocument(RequisitionDocument.class);
        requisitionDocument.initiateDocument();
        header(requisitionDocument);
        documentService.saveDocument(requisitionDocument);
        requisitionDocument.refreshNonUpdateableReferences();

        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument) documentService.getNewDocument(PurchaseOrderDocument.class);
        purchaseOrderDocument.populatePurchaseOrderFromRequisition(requisitionDocument);
        header(purchaseOrderDocument);
        documentService.saveDocument(purchaseOrderDocument);
        purchaseOrderDocument.refreshNonUpdateableReferences();

        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) documentService.getNewDocument(PaymentRequestDocument.class);
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        // paymentRequestDocument.initiateDocument();
        paymentRequestDocument.setInvoiceDate(today);
        paymentRequestDocument.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_AWAITING_ACCOUNTS_PAYABLE_REVIEW);// IN_PROCESS);
        paymentRequestDocument.setPaymentRequestCostSourceCode(PurapConstants.POCostSources.ESTIMATE);
        purchaseOrderDocument.setPurchaseOrderCreateTimestamp(new Timestamp(today.getTime()));
        // purchaseOrderDocument.setDefaultValuesForAPO();
        // purchaseOrderDocument.setP

        paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(purchaseOrderDocument);
        header(paymentRequestDocument);
        documentService.saveDocument(paymentRequestDocument);
        paymentRequestDocument.refreshNonUpdateableReferences();

        // paymentRequestDocument.setPurchaseOrderIdentifier(1);
        // paymentRequestDocument.setInvoiceDate(today);
        // Person currentUser = (Person)GlobalVariables.getUserSession().getKfsUser();
        // paymentRequestDocument.setAccountsPayableProcessorIdentifier(currentUser.getPrincipalId());
        // paymentRequestDocument.getDocumentHeader().setDocumentDescription("test description");

        // PurchaseOrderDocument purchaseOrderDocument =
        // SpringContext.getBean(DocumentService.class).getNewDocument(PurchaseOrderDocument.class);
        // purchaseOrderDocument.getDocumentHeader().setDocumentDescription("test");
        // documentService.saveDocument(purchaseOrderDocument);
        //        
        // paymentRequestDocument.setPurchaseOrderDocument(purchaseOrderDocument);
        // Integer poid = purchaseOrderDocument.getPurapDocumentIdentifier();
        // paymentRequestDocument.setPurchaseOrderIdentifier(poid);
        // documentService.saveDocument(paymentRequestDocument);
        return paymentRequestDocument;
    }

    @ConfigureContext(session = UserNameFixture.appleton)
    public void testFoo() throws Exception {
        // PaymentRequestDocument document = createBasicDocument();
        // boolean isApprovalRequested = document.getDocumentHeader().getWorkflowDocument().isApprovalRequested();
        // documentService.routeDocument(document, "", new ArrayList());
        // document.setChartOfAccountsCode("BA");
        // //changeCurrentUser(UserNameFixture.khuntley);
        // boolean approved = SpringContext.getBean(PaymentRequestService.class).autoApprovePaymentRequest(document,
        // defaultMinimumLimit);
        // Map map = GlobalVariables.getMessageMap();
        // boolean breakonme = approved;
    }

    /**
     * Payment requests with a negative payment request approval limit higher than the default limit should be auto-approved.
     * 
     * @throws Exception
     */
    public void testAutoApprovePaymentRequests_defaultLimit() throws Exception {

        // (laran) This is just sample code that doesn't really do anything.
        // I was trying to come up with a good testing strategy. This code
        // probably shouldn't be used for real tests. But it may be helpful to
        // look at.
        /*
         * Collection<NegativePaymentRequestApprovalLimit> notOverriddenByChart = new HashSet<NegativePaymentRequestApprovalLimit>();
         * Collection<NegativePaymentRequestApprovalLimit> limits = npras.findAboveLimit(defaultMinimumLimit);
         * for(NegativePaymentRequestApprovalLimit limit : limits) { LOG.info("Creating PayReq for limit."); PaymentRequestDocument
         * document = createBasicDocument(); PurchasingApItem item = new PaymentRequestItem(); item.setItemQuantity(new
         * KualiDecimal(1)); // Set the total for this document below the default minimum. // item.setItemUnitPrice(new
         * BigDecimal(defaultMinimumLimit.intValue() - 1)); document.addItem(item); String preSaveStatusCode =
         * document.getStatusCode(); paymentRequestService.autoApprovePaymentRequests(); // Status should have changed if document
         * was approved. LOG.info("Pre-save status code is " + preSaveStatusCode + ". Post-save status code is " +
         * document.getStatusCode()); assertNotSame(preSaveStatusCode, document.getStatusCode()); cancelDocument(document); }
         */
    }

    /**
     * When the chart negative payment request approval limit is lower than the default limit, and lower than the limit according to
     * (chart + account) and the limit according to (chart and org) the payreq should be auto-approved.
     * 
     * @throws Exception
     */
    public void testAutoApprovePaymentRequests_chartLimit() throws Exception {
    }

    /**
     * When the (chart+account) negative payment request approval limit is lower than the default limit, and lower than the limit
     * according to chart and the limit according to (chart and org) the payreq should be auto-approved.
     * 
     * @throws Exception
     */
    public void testAutoApprovePaymentRequests_chartAndAccountLimit() throws Exception {
    }

    public void testAutoApprovePaymentRequests_chartAndOrganizationLimit() throws Exception {
    }

    public void testAutoApprovePaymentRequests_held() throws Exception {
    }

    public void testAutoApprovePaymentRequests_cancelled() throws Exception {
    }

    public void testAutoApprovePaymentRequests_futurePayDate() throws Exception {
    }

}

