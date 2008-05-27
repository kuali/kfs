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
package org.kuali.module.purap.document;

import static org.kuali.test.fixtures.UserNameFixture.APPLETON;
import static org.kuali.test.fixtures.UserNameFixture.PARKE;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.authorization.PurchaseOrderDocumentActionAuthorizer;
import org.kuali.module.purap.fixtures.PaymentRequestDocumentFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.test.ConfigureContext;
import org.kuali.workflow.WorkflowTestUtils;

/**
 * This class is used to test the authorization of the
 * buttons in Purchase Order Documents. It will invoke the canXXX
 * methods in PurchaseOrderDocumentActionAuthorizer to 
 * test whether certain buttons could be displayed. 
 */
@ConfigureContext(session = PARKE)
public class PurchaseOrderDocumentActionAuthorizerTest extends KualiTestBase {

    private PurchaseOrderDocument purchaseOrderDocument = null;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        purchaseOrderDocument = null;
        super.tearDown();
    }

    /**
     * Tests that the retransmit button is displayed.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testValidForRetransmit() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        PurchaseOrderDocument po = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_RETRANSMIT.createPurchaseOrderDocument();
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(po, editMode);
        assertTrue(auth.canRetransmit());
    }
    
    /**
     * Tests that the print retransmit button is displayed when the purchase order 
     * is not an APO. It should allow purchasing users (in this case we use PARKE)
     * to see the button if the purchase order is not an APO.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testValidForPrintingRetransmitNonAPO() throws Exception {
        Map editMode = new HashMap();
        editMode.put(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB, true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_RETRANSMIT.createPurchaseOrderDocument();
        poDocument.prepareForSave();       
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), "F");      
        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());

        PurchaseOrderService purchaseOrderService = SpringContext.getBean(PurchaseOrderService.class);
        PurchaseOrderDocument poRetransmitDocument = purchaseOrderService.createAndRoutePotentialChangeDocument(poDocument.getDocumentNumber(), PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, null, null, "RTPE");
        poRetransmitDocument.setStatusCode("CGIN");
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poRetransmitDocument, editMode);
        assertTrue(auth.canPrintRetransmit());
    }
    
    /**
     * Tests that the print retransmit button is displayed when the purchase order is an
     * APO and the user can be anyone (here it is set as RORENFRO prior to checking for the authorizer).
     * It should allow anyone to see the print retransmit button if it's an APO.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testValidForPrintingRetransmitAPO() throws Exception {
        Map editMode = new HashMap();
        editMode.put(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB, true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_RETRANSMIT.createPurchaseOrderDocument();
        poDocument.prepareForSave();       
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), "F");      
        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());

        PurchaseOrderService purchaseOrderService = SpringContext.getBean(PurchaseOrderService.class);
        PurchaseOrderDocument poRetransmitDocument = purchaseOrderService.createAndRoutePotentialChangeDocument(poDocument.getDocumentNumber(), PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, null, null, "RTPE");
        poRetransmitDocument.setStatusCode("CGIN");
        poRetransmitDocument.setPurchaseOrderAutomaticIndicator(true);
        changeCurrentUser(RORENFRO);
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poRetransmitDocument, editMode);
        assertTrue(auth.canPrintRetransmit());
    }
    
    /**
     * Tests that the print button for first transmit is displayed.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testFirstTransmitPrintPO() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_FIRST_TRANSMIT_PRINT.createPurchaseOrderDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        poDocument.prepareForSave();       
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), "F");        
        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
        
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode);
        assertTrue(auth.canFirstTransmitPrintPo());
    }
    
    /**
     * Tests that the open order button is displayed.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testReopen() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_REOPEN.createPurchaseOrderDocument();
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode);
        assertTrue(auth.canReopen());
    }
    
    /**
     * Tests that the close order button is displayed.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testClose() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        //We create and save this req to obtain a number from the AP document link identifier sequencer in the database
        RequisitionDocument dummyReqDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        documentService.saveDocument(dummyReqDocument);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_CLOSE.createPurchaseOrderDocument();
        poDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(dummyReqDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());
        poDocument.prepareForSave();       
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), "F");    
        changeCurrentUser(APPLETON);
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_FOR_PO_CLOSE_DOC.createPaymentRequestDocument();
        preq.setPurchaseOrderIdentifier(poDocument.getPurapDocumentIdentifier());
        preq.setProcessingCampusCode("BL");
        preq.setAccountsPayablePurchasingDocumentLinkIdentifier(poDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());
        preq.prepareForSave();       
        AccountingDocumentTestUtils.saveDocument(preq, documentService);
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode);
        assertTrue(auth.canClose());
    }
    
    /**
     * Tests that the payment hold buttons are displayed.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testPaymentHold() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_CLOSE.createPurchaseOrderDocument();
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode);
        assertTrue(auth.canHoldPayment());
    }
    
    /**
     * Tests that the void button is displayed when the purchase order
     * is in Pending Print status.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testVoidPendingPrint() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_VOID_PENDING_PRINT.createPurchaseOrderDocument();
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode);
        assertTrue(auth.canVoid());
    }
    
    /**
     * Tests that the void button is displayed when the purchase order
     * is in OPEN status and there is no payment request associated
     * with the purchase order.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testVoidOpenNoPreq() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_VOID_OPEN_NO_PREQ.createPurchaseOrderDocument();
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode);
        assertTrue(auth.canVoid());
    }
    
    /**
     * Tests that the remove hold button is displayed.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testRemoveHold() throws Exception {
        Map editMode = new HashMap();
        editMode.put("fullEntry", true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_REMOVE_HOLD.createPurchaseOrderDocument();
        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode);
        assertTrue(auth.canRemoveHold());
    }
}
