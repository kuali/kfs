/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.batch;

import org.kuali.core.UserSession;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.impl.DocumentServiceImpl;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.document.AssignContractManagerDocumentTest;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.CreditMemoDocumentTest;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PaymentRequestDocumentTest;
import org.kuali.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.module.purap.document.PurchaseOrderCloseDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.fixtures.PaymentRequestDocumentFixture;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * Step used to auto approve payment requests that meet a certain criteria
 */
public class CreateFullDocumentSuiteStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentServiceImpl.class);

    private static final String SUB_ACCOUNT_REVIEW = "Sub Account Review";
    private static final String ACCOUNT_REVIEW = "Account Review";
    private static final String ORG_REVIEW = "Org Review";

    private DocumentService documentService = null;

    public CreateFullDocumentSuiteStep() {
        super();
    }

    /**
     * Calls service method to approve payment requests
     * 
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) throws InterruptedException {
        try {
            testFullProcess();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            throw new InterruptedException(e.getMessage());
        }
        return true;
    }

    public boolean execute() throws InterruptedException {
        return execute(null);
    }


    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void testFullProcess() throws Exception {
        changeCurrentUser("PARKE");
        // 1. use the ACM document to create the REQ and PO
        AssignContractManagerDocumentTest acmDocTest = new AssignContractManagerDocumentTest();
        String reqNumber = acmDocTest.testRouteDocument2();
        RequisitionDocument reqDoc = (RequisitionDocument) documentService.getByDocumentHeaderId(reqNumber);
        String poNumber = reqDoc.getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poNumber);
        // approve the PO
        poDoc.setPurchaseOrderVendorChoiceCode("LPRC");
        // submit then approve the PO
        documentService.routeDocument(poDoc, "Test routing as PARKE", null); 

        poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poNumber);
        
        // 3 use the PO number to create a Payment Request and have it go final
        PaymentRequestDocument preqDoc = routePREQDocumentToFinal(poDoc);
        
        // 4. use the PO number to create a Credit Memo and have it go final
        changeCurrentUser("APPLETON");
        CreditMemoDocumentTest cmDocTest = new CreditMemoDocumentTest();
        CreditMemoDocument cmDoc = cmDocTest.routeDocument(preqDoc);
        
        // 2. based on the PO document number, create the Amend PO doc and let it go final (with philips?)
        changeCurrentUser("PARKE");
        PurchaseOrderAmendmentDocument amendDoc = (PurchaseOrderAmendmentDocument) SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(poDoc.getPurchaseOrderRestrictedMaterials(), poDoc.getPurchaseOrderRestrictionStatusHistories(), poNumber, PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PurchaseOrderStatuses.AMENDMENT);
        documentService.routeDocument(amendDoc, "Test routing as PARKE", null);
        WorkflowTestUtils.waitForStatusChange(amendDoc.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        // 5. use the PO number to create a Close PO and have it go final
        changeCurrentUser("PARKE");
        PurchaseOrderCloseDocument closeDoc = (PurchaseOrderCloseDocument) SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(poDoc.getPurchaseOrderRestrictedMaterials(), poDoc.getPurchaseOrderRestrictionStatusHistories(), poNumber, PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, PurchaseOrderStatuses.PENDING_CLOSE);
        documentService.routeDocument(closeDoc, "Test routing as PARKE", null);
        WorkflowTestUtils.waitForStatusChange(closeDoc.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        LOG.info("Requisition document: " + reqDoc.getDocumentNumber());
        LOG.info("PO document: " + poDoc.getDocumentNumber());
        LOG.info("PREQ document: " + preqDoc.getDocumentNumber());
        LOG.info("CM document: " + cmDoc.getDocumentNumber());
        LOG.info("Amend PO document: " + amendDoc.getDocumentNumber());
        LOG.info("Close PO document: " + closeDoc.getDocumentNumber());
    }
        
    public final PaymentRequestDocument routePREQDocumentToFinal(PurchaseOrderDocument POdoc) throws Exception {
        changeCurrentUser("APPLETON");
        PaymentRequestDocumentTest preqDocTest = new PaymentRequestDocumentTest();
        PaymentRequestDocument paymentRequestDocument = preqDocTest.createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                POdoc, true, new KualiDecimal[] {new KualiDecimal(100)});
        
        final String docId = paymentRequestDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), SUB_ACCOUNT_REVIEW);

        // the document should now be routed to VPUTMAN as Fiscal Officer
        changeCurrentUser("STROUD");
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        documentService.approveDocument(paymentRequestDocument, "Test approving as STROUD", null); 
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
        changeCurrentUser("RORENFRO");
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        documentService.approveDocument(paymentRequestDocument, "Test approving as RORENFRO", null); 
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ORG_REVIEW);
        changeCurrentUser("GHATTEN");
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        documentService.approveDocument(paymentRequestDocument, "Test approving as GHATTEN", null); 

        WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        return paymentRequestDocument;
    }
 
    protected void changeCurrentUser(String sessionUser) throws Exception {
        GlobalVariables.setUserSession(new UserSession(sessionUser));
    }

}
