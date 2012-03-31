/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import static org.kuali.kfs.sys.fixture.UserNameFixture.ferland;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.impl.DocumentServiceImpl;

/**
 * Used to create and test populated Purchase Order Documents of various kinds.
 */
@ConfigureContext(session = khuntley)
public class PurapFullProcessDocumentTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentServiceImpl.class);

    private static final String SUB_ACCOUNT_REVIEW = "SubAccount";
    private static final String ACCOUNT_REVIEW = "Account";
    private static final String ORG_REVIEW = "AccountingOrganizationHierarchy";

    protected static DocumentService documentService = null;

    @Override
    protected void setUp() throws Exception {
        documentService = SpringContext.getBean(DocumentService.class);
    }

    /**
     * TODO: Remove once other tests are fixed
     */
    public void testNothing() {

    }

    /*
     * Requisition
    * PO
    * Amend PO
    * PREQ
    * CM
    * Close PO
     */
    @ConfigureContext(session = parke, shouldCommitTransactions = true)
    public final void PATCHFIX_testFullProcess() throws Exception {
        // 1. use the ACM document to create the REQ and PO
        ContractManagerAssignmentDocumentTest acmDocTest = new ContractManagerAssignmentDocumentTest();
        String reqNumber = acmDocTest.testRouteDocument2();
        RequisitionDocument reqDoc = (RequisitionDocument) documentService.getByDocumentHeaderId(reqNumber);
        String poNumber = reqDoc.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poNumber);
        poDoc.setReceivingDocumentRequiredIndicator(false);
        // approve the PO
        poDoc.setPurchaseOrderVendorChoiceCode("LPRC");
        // submit then approve the PO
        documentService.routeDocument(poDoc, "Test routing as parke", null);

        poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poNumber);

        // 3 use the PO number to create a Payment Request and have it go final
        PaymentRequestDocument preqDoc = routePREQDocumentToFinal(poDoc);

        // 4. use the PO number to create a Credit Memo and have it go final
        changeCurrentUser(appleton);
        CreditMemoDocumentTest cmDocTest = new CreditMemoDocumentTest();
        VendorCreditMemoDocument cmDoc = cmDocTest.routeDocument(preqDoc);

        // 2. based on the PO document number, create the Amend PO doc and let it go final (with philips?)
        changeCurrentUser(parke);
        PurchaseOrderAmendmentDocument amendDoc = (PurchaseOrderAmendmentDocument) SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(poNumber, PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PurchaseOrderStatuses.APPDOC_AMENDMENT);
        documentService.routeDocument(amendDoc, "Test routing as parke", null);
        WorkflowTestUtils.waitForDocumentApproval(amendDoc.getDocumentNumber());

        // 5. use the PO number to create a Close PO and have it go final
        changeCurrentUser(parke);
        PurchaseOrderCloseDocument closeDoc = (PurchaseOrderCloseDocument) SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(poNumber, PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, PurchaseOrderStatuses.APPDOC_PENDING_CLOSE);
        documentService.routeDocument(closeDoc, "Test routing as parke", null);
        WorkflowTestUtils.waitForDocumentApproval(closeDoc.getDocumentNumber());

        LOG.info("Requisition document: " + reqDoc.getDocumentNumber());
        LOG.info("PO document: " + poDoc.getDocumentNumber());
        LOG.info("PREQ document: " + preqDoc.getDocumentNumber());
        LOG.info("CM document: " + cmDoc.getDocumentNumber());
        LOG.info("Amend PO document: " + amendDoc.getDocumentNumber());
        LOG.info("Close PO document: " + closeDoc.getDocumentNumber());
    }



    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public final PaymentRequestDocument routePREQDocumentToFinal(PurchaseOrderDocument POdoc) throws Exception {
//        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        PaymentRequestDocumentTest preqDocTest = new PaymentRequestDocumentTest();
        PaymentRequestDocument paymentRequestDocument = preqDocTest.createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED,
                POdoc, true, new KualiDecimal[] {new KualiDecimal(100)});

        final String docId = paymentRequestDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        /*WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), SUB_ACCOUNT_REVIEW);

        // the document should now be routed to vputman as Fiscal Officer
        changeCurrentUser(stroud);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, SUB_ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("stroud should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as stroud", null); */
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
        changeCurrentUser(ferland);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument,
                ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("ferland should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as ferland", null);

        WorkflowTestUtils.waitForDocumentApproval(paymentRequestDocument.getDocumentNumber());

        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isFinal());
        return paymentRequestDocument;
    }

    private UserNameFixture getInitialUserName() {
        return khuntley;
    }

    protected UserNameFixture getTestUserName() {
        return khuntley;
    }
}

