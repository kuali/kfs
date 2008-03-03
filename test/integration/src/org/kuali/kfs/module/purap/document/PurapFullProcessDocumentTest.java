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
package org.kuali.module.purap.document;

import static org.kuali.test.fixtures.UserNameFixture.GHATTEN;
import static org.kuali.test.fixtures.UserNameFixture.APPLETON;
import static org.kuali.test.fixtures.UserNameFixture.STROUD;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.PARKE;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;

import org.kuali.core.service.DocumentService;
import org.kuali.core.service.impl.DocumentServiceImpl;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.fixtures.AssignContractManagerDocumentFixture;
import org.kuali.module.purap.fixtures.PaymentRequestDocumentFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.suite.RelatesTo;
import org.kuali.test.suite.RelatesTo.JiraIssue;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * Used to create and test populated Purchase Order Documents of various kinds. 
 */
@ConfigureContext(session = KHUNTLEY)
public class PurapFullProcessDocumentTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentServiceImpl.class);

    private static final String SUB_ACCOUNT_REVIEW = "Sub Account Review";
    private static final String ACCOUNT_REVIEW = "Account Review";
    private static final String ORG_REVIEW = "Org Review";

    protected static DocumentService documentService = null;

    protected void setUp() throws Exception {
        documentService = SpringContext.getBean(DocumentService.class);
    }
    
    /*
     * Requisition
    * PO 
    * Amend PO
    * PREQ
    * CM
    * Close PO
     */
    @RelatesTo(JiraIssue.KULPURAP2348)
    @ConfigureContext(session = PARKE, shouldCommitTransactions = true)
    public final void testFullProcess() throws Exception {
        // 1. use the ACM document to create the REQ and PO
        AssignContractManagerDocumentTest acmDocTest = new AssignContractManagerDocumentTest();
        String reqNumber = acmDocTest.testRouteDocument2();
        RequisitionDocument reqDoc = (RequisitionDocument) documentService.getByDocumentHeaderId(reqNumber);
        String poNumber = reqDoc.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poNumber);
        // approve the PO
        poDoc.setPurchaseOrderVendorChoiceCode("LPRC");
        // submit then approve the PO
        documentService.routeDocument(poDoc, "Test routing as PARKE", null); 

        poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poNumber);
        
        // 3 use the PO number to create a Payment Request and have it go final
        PaymentRequestDocument preqDoc = routePREQDocumentToFinal(poDoc);
        
        // 4. use the PO number to create a Credit Memo and have it go final
        changeCurrentUser(APPLETON);
        CreditMemoDocumentTest cmDocTest = new CreditMemoDocumentTest();
        CreditMemoDocument cmDoc = cmDocTest.routeDocument(preqDoc);
        
        // 2. based on the PO document number, create the Amend PO doc and let it go final (with philips?)
        changeCurrentUser(PARKE);
        PurchaseOrderAmendmentDocument amendDoc = (PurchaseOrderAmendmentDocument) SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(poDoc.getPurchaseOrderRestrictedMaterials(), poDoc.getPurchaseOrderRestrictionStatusHistories(), poNumber, PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PurchaseOrderStatuses.AMENDMENT);
        documentService.routeDocument(amendDoc, "Test routing as PARKE", null);
        WorkflowTestUtils.waitForStatusChange(amendDoc.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        // 5. use the PO number to create a Close PO and have it go final
        changeCurrentUser(PARKE);
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
    
    

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final PaymentRequestDocument routePREQDocumentToFinal(PurchaseOrderDocument POdoc) throws Exception {
//        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        PaymentRequestDocumentTest preqDocTest = new PaymentRequestDocumentTest();
        PaymentRequestDocument paymentRequestDocument = preqDocTest.createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                POdoc, true, new KualiDecimal[] {new KualiDecimal(100)});
        
        final String docId = paymentRequestDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), SUB_ACCOUNT_REVIEW);

        // the document should now be routed to VPUTMAN as Fiscal Officer
        changeCurrentUser(STROUD);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, SUB_ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("STROUD should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as STROUD", null); 
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
        changeCurrentUser(RORENFRO);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument,     
                ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("RORENFRO should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as RORENFRO", null); 
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ORG_REVIEW);
        changeCurrentUser(GHATTEN);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        documentService.approveDocument(paymentRequestDocument, "Test approving as GHATTEN", null); 

        WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);
//
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());    
        return paymentRequestDocument;
    }
 
    private UserNameFixture getInitialUserName() {
        return KHUNTLEY;
    }

    protected UserNameFixture getTestUserName() {
        return KHUNTLEY;
    }
}
