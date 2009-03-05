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
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PaymentRequestItemFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class is used to create and test populated Payment Request Documents of various kinds.
 */
@ConfigureContext(session = appleton)
public class PaymentRequestDocumentTest extends KualiTestBase {
    public static final Class<PaymentRequestDocument> DOCUMENT_CLASS = PaymentRequestDocument.class;
    private static final String ACCOUNT_REVIEW = "Account";
    
    protected static DocumentService documentService = null;
    protected static PurchaseOrderService purchaseOrderService = null;
    private PaymentRequestDocument paymentRequestDocument = null;
    protected static PurchaseOrderDocument purchaseOrderDocument = null;

    protected void setUp() throws Exception {
        documentService = SpringContext.getBean(DocumentService.class);
        purchaseOrderService = SpringContext.getBean(PurchaseOrderService.class);
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
                (AccountsPayableDocument) DocumentTestUtils.createDocument(documentService, DOCUMENT_CLASS), 
                items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, documentService);
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testConvertIntoErrorCorrection() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, false);
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(paymentRequestDocument, getExpectedPrePeCount(), 
                documentService, SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testSaveDocument() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, false);
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testRouteDocument() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});
        AccountingDocumentTestUtils.testRouteDocument(paymentRequestDocument, documentService);
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testRouteDocumentToFinal() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});
        
        final String docId = paymentRequestDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to vputman as Fiscal Officer
        changeCurrentUser(rorenfro);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("rorenfro should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as rorenfro", null); 

        WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);

        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());        
    }
    
    //Commented due to Jira issue preventing documents created by PREQ from going to final
    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testClosePo() throws Exception {
        //route a preq and mark the close po indicator
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.CLOSE_PO_WITH_PREQ, true);        
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.CLOSE_PO_WITH_PREQ, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});

        final String docId = paymentRequestDocument.getDocumentNumber();
        final String poDocId = paymentRequestDocument.getPurchaseOrderDocument().getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);                       

        // now route
        changeCurrentUser(rorenfro);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("rorenfro should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as rorenfro", null); 

        WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);

        // check if the purchase order document is closed
        PurchaseOrderDocument purchaseOrderDocument =(PurchaseOrderDocument) purchaseOrderService.getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderDocument().getPurapDocumentIdentifier()); //documentService.getByDocumentHeaderId(poDocId); 
        assertTrue( "Purchase order should be closed.", PurchaseOrderStatuses.CLOSED.equals( purchaseOrderDocument.getStatusCode() ) );        
    }

    //Commented due to Jira issue preventing documents created by PREQ from going to final
    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testReopenPo() throws Exception {
        //route a preq and mark the close po indicator
        /*purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.REOPEN_PO_WITH_PREQ, true);        
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.REOPEN_PO_WITH_PREQ, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});

        final String docId = paymentRequestDocument.getDocumentNumber();
        final String poDocId = paymentRequestDocument.getPurchaseOrderDocument().getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);                       

        //close po
        changeCurrentUser(parke);
        purchaseOrderDocument.setStatusCode(PurchaseOrderStatuses.CLOSED);
        AccountingDocumentTestUtils.testSaveDocument(purchaseOrderDocument, documentService);
        purchaseOrderDocument =(PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocId);
        
        // now route
        changeCurrentUser(rorenfro);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("rorenfro should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as rorenfro", null); 

        WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);

        // check if the purchase order document is open
        PurchaseOrderDocument purchaseOrderDocument =(PurchaseOrderDocument)purchaseOrderService.getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderDocument().getPurapDocumentIdentifier()); //documentService.getByDocumentHeaderId(poDocId); 
        assertTrue( "Purchase order should be opened.", PurchaseOrderStatuses.OPEN.equals( purchaseOrderDocument.getStatusCode() ) );  */      
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testRequestCancel() throws Exception{
        //route a preq and mark the preq as requested cancel
        /*purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, false);        
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});

        final String docId = paymentRequestDocument.getDocumentNumber();
        final String poDocId = paymentRequestDocument.getPurchaseOrderDocument().getDocumentNumber();
        AccountingDocumentTestUtils.testRouteDocument(paymentRequestDocument, documentService);              
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
        
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);        
        PaymentRequestDocumentActionAuthorizer preqAA = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument);                
        assertTrue( "Payment Request should allow remove request cancel.", preqAA.canRemoveRequestCancel() );*/
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testRequestHold() throws Exception{
        //route a preq and mark the preq as requested hold
        /*purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.REQUEST_HOLD_PREQ, false);        
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.REQUEST_HOLD_PREQ, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});

        final String docId = paymentRequestDocument.getDocumentNumber();
        final String poDocId = paymentRequestDocument.getPurchaseOrderDocument().getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);                       

        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);        
        PaymentRequestDocumentActionAuthorizer preqAA = new PaymentRequestDocumentActionAuthorizer(paymentRequestDocument);                
        assertTrue( "Payment Request should allow remove hold.", preqAA.canRemoveHold());*/
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public final void testCalculate() throws Exception{        
    }

    
    /**
     * Creates a purchase order document with a provided purchase order document.
     * At a minimum saves the document, but can additionally route the document (stipulation: coded to work only for budget review).
     * 
     * @param poFixture - purchase order document fixture to source test data
     * @param routePO - An option to route the purchase order if set to true
     * @return
     * @throws Exception
     * @ConfigureContext(shouldCommitTransactions=false)
     */
    public final PurchaseOrderDocument createPurchaseOrderDocument (
            PurchaseOrderDocumentFixture poFixture, boolean routePO)throws Exception{
        
        //check if test has been setup
        if (documentService == null){
            documentService = SpringContext.getBean(DocumentService.class);
        }
        
        //setup purchase order and save
        changeCurrentUser(parke);               
        PurchaseOrderDocument po = poFixture.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        po.refreshNonUpdateableReferences();               
        AccountingDocumentTestUtils.testSaveDocument(po, documentService);        
        
        //retrieve saved purchase order
        final String poDocId = po.getDocumentNumber();        
        po = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocId);
        
        //route if requested
        if(routePO){                        
            AccountingDocumentTestUtils.testRouteDocument(po, documentService);
            WorkflowTestUtils.waitForStatusChange(po.getDocumentHeader().getWorkflowDocument(), "F");
            /*WorkflowTestUtils.waitForNodeChange(po.getDocumentHeader().getWorkflowDocument(), BUDGET_REVIEW);            
        
            changeCurrentUser(butt);
            po =(PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocId);
            AccountingDocumentTestUtils.approveDocument(po, documentService);
            WorkflowTestUtils.waitForStatusChange(po.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
            */
            po =(PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocId);
        }
        
        return po;
    }
            
    /**
     * Creates a new payment request document and additionally saves it.
     * 
     * @param preqFixture - The payment request document fixture to source test data from
     * @param po - The purchase order to copy select values from and to link to the payment request document
     * @param copyPoItems - An option to copy the items from the purchase order if set to true
     * @param itemQuantityList - A list of quantity values for each item in your payment request document
     * @return
     * @throws Exception
     * @ConfigureContext(shouldCommitTransactions=false)
     */
    public final PaymentRequestDocument createPaymentRequestDocument (
            PaymentRequestDocumentFixture preqFixture, PurchaseOrderDocument po, boolean copyPoItems, KualiDecimal[] itemQuantityList)throws Exception{

        //check if test has been setup
        if (documentService == null){
            documentService = SpringContext.getBean(DocumentService.class);
        }

        //setup purchase order and save
        changeCurrentUser(appleton);
        PaymentRequestDocument preq = preqFixture.createPaymentRequestDocument();
        preq.initiateDocument();
        
        //set payment request from po, and optionally copy the items
        if(copyPoItems){
            preq.populatePaymentRequestFromPurchaseOrder(po, new HashMap());
        }else{
            populatePaymentRequestFromPurchaseOrderWithoutItems(preq, po);
        }
        
        //set quantity and calculate price                      
        updateQuantityAndPrice(preq, itemQuantityList);
        
        AccountingDocumentTestUtils.testSaveDocument(preq, documentService);

        //retrieve saved payment request
        final String preqDocId = preq.getDocumentNumber();
        preq = (PaymentRequestDocument) documentService.getByDocumentHeaderId(preqDocId);
        
        return preq;        
    }
    
    /**
     * Populates fields on a payment request with data from a purchase order, but
     * does not copy the purchase order items.
     * 
     * @param preq
     * @param po
     */
    private void populatePaymentRequestFromPurchaseOrderWithoutItems(PaymentRequestDocument preq, PurchaseOrderDocument po){
        
        preq.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        preq.getDocumentHeader().setOrganizationDocumentNumber(po.getDocumentHeader().getOrganizationDocumentNumber());
        preq.setPostingYear(po.getPostingYear());
        preq.setVendorCustomerNumber(po.getVendorCustomerNumber());
        
        if (po.getPurchaseOrderCostSource() != null) {
            preq.setPaymentRequestCostSource(po.getPurchaseOrderCostSource());
            preq.setPaymentRequestCostSourceCode(po.getPurchaseOrderCostSourceCode());
        }
        
        if (po.getVendorShippingPaymentTerms() != null) {
            preq.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
            preq.setVendorShippingPaymentTermsCode(po.getVendorShippingPaymentTermsCode());
        }

        if (po.getRecurringPaymentType() != null) {
            preq.setRecurringPaymentType(po.getRecurringPaymentType());
            preq.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        }

        preq.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        preq.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        preq.setVendorCustomerNumber(po.getVendorCustomerNumber());
        preq.setVendorName(po.getVendorName());

        // populate preq vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
        VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(po.getVendorHeaderGeneratedIdentifier(), po.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            preq.templateVendorAddress(vendorAddress);
            preq.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
        }else {
            // set address from PO
            preq.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
            preq.setVendorLine1Address(po.getVendorLine1Address());
            preq.setVendorLine2Address(po.getVendorLine2Address());
            preq.setVendorCityName(po.getVendorCityName());
            preq.setVendorStateCode(po.getVendorStateCode());
            preq.setVendorPostalCode(po.getVendorPostalCode());
            preq.setVendorCountryCode(po.getVendorCountryCode());
        }

        if ((po.getVendorPaymentTerms() == null) || ("".equals(po.getVendorPaymentTerms().getVendorPaymentTermsCode()))) {
            preq.setVendorPaymentTerms(new PaymentTermType());
            preq.setVendorPaymentTermsCode("");
        }
        else {
            preq.setVendorPaymentTermsCode(po.getVendorPaymentTermsCode());
            preq.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }
        
        preq.setPaymentRequestPayDate(SpringContext.getBean(PaymentRequestService.class).calculatePayDate(preq.getInvoiceDate(), preq.getVendorPaymentTerms()));

        // add missing below the line
        SpringContext.getBean(PurapService.class).addBelowLineItems(preq);
        preq.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        preq.refreshNonUpdateableReferences();
    }

    /**
     * Updates the quanities for each payment request item in order.
     * Also updates the extended price for above and below the line items,
     * and finally updates the amounts for each accounting line.
     * 
     * @param preq
     * @param quantityList
     */
    public void updateQuantityAndPrice(PaymentRequestDocument preq, KualiDecimal[] quantityList){
        
        int quantityIndex = 0;
        KualiDecimal quantity = null;
        
        for (PaymentRequestItem pri : (List<PaymentRequestItem>) preq.getItems()) {
            if( pri.getItemType().isLineItemIndicator() ){
                if(pri.getItemType().isQuantityBasedGeneralLedgerIndicator()){
                    
                    //if within range of passed in quantities
                    if( quantityIndex <= (quantityList.length - 1) ){
                        quantity = quantityList[quantityIndex];
                    }else{
                        quantity = new KualiDecimal(0);
                    }
                    
                    pri.setItemQuantity( quantity );                
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

    }

}

