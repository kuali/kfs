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

import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.test.fixtures.UserNameFixture.APPLETON;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.AccountsPayableItem;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.fixtures.PaymentRequestItemFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * This class is used to create and test populated Payment Request Documents of various kinds.
 */
@ConfigureContext(session = APPLETON)
public class PaymentRequestDocumentTest extends KualiTestBase {
    public static final Class<PaymentRequestDocument> DOCUMENT_CLASS = PaymentRequestDocument.class;
    private static final String ACCOUNT_REVIEW = "Account Review";
    
    private PaymentRequestDocument paymentRequestDocument = null;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        paymentRequestDocument = null;
        super.tearDown();      
    }

    private int getExpectedPrePeCount() {
        return 4;
    }

    public final void testAddItem() throws Exception {
        List<AccountsPayableItem> items = new ArrayList<AccountsPayableItem>();
        items.add(PaymentRequestItemFixture.PREQ_QTY_UNRESTRICTED_ITEM_1.createPaymentRequestItem());
                
        int expectedItemTotal = items.size();
        AccountsPayableDocumentTestUtils.testAddItem(
                (AccountsPayableDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), 
                items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        paymentRequestDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(paymentRequestDocument, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {
        paymentRequestDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testSaveDocument(paymentRequestDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testRouteDocument() throws Exception {
        paymentRequestDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testRouteDocument(paymentRequestDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testRouteDocumentToFinal() throws Exception {
        paymentRequestDocument = buildSimpleDocument();
        final String docId = paymentRequestDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to VPUTMAN as Fiscal Officer
        changeCurrentUser(RORENFRO);
        paymentRequestDocument = (PaymentRequestDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("RORENFRO should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(paymentRequestDocument, "Test approving as RORENFRO", null); 

        WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(KHUNTLEY);
        paymentRequestDocument = (PaymentRequestDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());        
    }
    
    // test util methods
    public PaymentRequestDocument buildSimpleDocument() throws Exception {
        
        PaymentRequestDocument preq = createBasicDocument1();
                                
        return preq;        
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    private PaymentRequestDocument createBasicDocument1() throws Exception{
        
        //REQUISITION DOCUMENT
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ALTERNATE_APO.createRequisitionDocument();
        final String docId = requisitionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to VPUTMAN as Fiscal Officer
        changeCurrentUser(RORENFRO);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(requisitionDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("RORENFRO should have an approve request.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as RORENFRO", null);

        //WorkflowTestUtils.waitForStatusChange(800, requisitionDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(KHUNTLEY);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        //assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());

        // get related POs, if count = 1 then all is well
        Integer linkIdentifier = requisitionDocument.getAccountsPayablePurchasingDocumentLinkIdentifier();
        List<PurchaseOrderView> relatedPOs = SpringContext.getBean(PurapService.class).getRelatedViews(PurchaseOrderView.class, linkIdentifier);
        assertNotNull(relatedPOs);
        assertTrue(relatedPOs.size() > 0);
        
        //get PO
        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(relatedPOs.get(0).getDocumentNumber());
        
        changeCurrentUser(APPLETON);
        //create preq
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) SpringContext.getBean(DocumentService.class).getNewDocument(PaymentRequestDocument.class);
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        paymentRequestDocument.initiateDocument();

        paymentRequestDocument.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        paymentRequestDocument.setInvoiceNumber("12345");
        paymentRequestDocument.setVendorInvoiceAmount(new KualiDecimal(100));        
        paymentRequestDocument.setInvoiceDate(today);        
        paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW);// IN_PROCESS);
        paymentRequestDocument.setPaymentRequestCostSourceCode(PurapConstants.POCostSources.ESTIMATE);
        
        header(paymentRequestDocument);
        AccountingDocumentTestUtils.saveDocument(paymentRequestDocument, SpringContext.getBean(DocumentService.class));
        paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(po);
        paymentRequestDocument.refreshNonUpdateableReferences();

        for (PaymentRequestItem pri : (List<PaymentRequestItem>) paymentRequestDocument.getItems()) {
            if( pri.getItemType().isItemTypeAboveTheLineIndicator() ){
                if(pri.getItemType().isQuantityBasedGeneralLedgerIndicator()){
                    pri.setItemQuantity( new KualiDecimal(100) );                
                    pri.setExtendedPrice( pri.calculateExtendedPrice() );                    
                }else{
                    pri.setExtendedPrice( pri.calculateExtendedPrice() );                    
                }
            }else{
                pri.setExtendedPrice( pri.calculateExtendedPrice() );                
            }
            
            for (PurApAccountingLine accountingLine : (List<PurApAccountingLine>) pri.getSourceAccountingLines() ) {                    
                accountingLine.setAmount(pri.getExtendedPrice());
            }                                    
        }

        return paymentRequestDocument;
    }

    private void header(Document document) {
        document.getDocumentHeader().setFinancialDocumentDescription("test");
    }

}
