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
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItemUseTax;
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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class is used to create and test populated Payment Request Documents of various kinds.
 */
@ConfigureContext(session = appleton)
public class PaymentRequestDocumentTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestItem.class);

    public static final Class<PaymentRequestDocument> DOCUMENT_CLASS = PaymentRequestDocument.class;
    private static final String ACCOUNT_REVIEW = "Account";

    protected static DocumentService documentService = null;
    protected static PurchaseOrderService purchaseOrderService = null;
    private PaymentRequestDocument paymentRequestDocument = null;
    protected static PurchaseOrderDocument purchaseOrderDocument = null;

    @Override
    protected void setUp() throws Exception {
        documentService = SpringContext.getBean(DocumentService.class);
        purchaseOrderService = SpringContext.getBean(PurchaseOrderService.class);
        super.setUp();
    }

    @Override
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
        AccountsPayableDocumentTestUtils.testAddItem(DocumentTestUtils.createDocument(documentService, DOCUMENT_CLASS), items, expectedItemTotal);
    }

    public final void testNothing() throws Exception {
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, documentService);
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testConvertIntoErrorCorrection() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, false);
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, purchaseOrderDocument, true, new KualiDecimal[] { new KualiDecimal(100) });
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(paymentRequestDocument, getExpectedPrePeCount(), documentService, SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testSaveDocument() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, false);
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, purchaseOrderDocument, true, new KualiDecimal[] { new KualiDecimal(100) });
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testRouteDocument() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        purchaseOrderDocument.setAccountDistributionMethod("S");
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, purchaseOrderDocument, true, new KualiDecimal[] { new KualiDecimal(100) });
        paymentRequestDocument.setAccountDistributionMethod("S");
        AccountingDocumentTestUtils.testRouteDocument(paymentRequestDocument, documentService);
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testRouteDocumentToFinal() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, purchaseOrderDocument, true, new KualiDecimal[] { new KualiDecimal(100) });

        final String docId = paymentRequestDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to vputman as Fiscal Officer
        changeCurrentUser(rorenfro);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("rorenfro should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as rorenfro", null);

        WorkflowTestUtils.waitForDocumentApproval(paymentRequestDocument.getDocumentNumber());

        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isFinal());
    }

    // Commented due to Jira issue preventing documents created by PREQ from going to final
    // the underlying issue with this test is that the way it is written causes an exception we are unable to solve. The result is:
    //
    // Caused by: org.apache.ojb.broker.OptimisticLockException: Object has been modified by someone else
    //
    // This test needs to be re-written to alleviate the OptimisticLockException.
    //
    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testClosePo() throws Exception {
        // route a preq and mark the close po indicator
        /*
                purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.CLOSE_PO_WITH_PREQ, true);

                paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.CLOSE_PO_WITH_PREQ,
                        purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});

                final String docId = paymentRequestDocument.getDocumentNumber();
                final String poDocId = paymentRequestDocument.getPurchaseOrderDocument().getDocumentNumber();
                Thread.sleep(5000);  // attempt to make sure any other async processing on this document is done

                // another attempt to solve the problem of OptimisticLockException in the call to routeDocument... still no good.
        //        for (int i = 0; i < 10; i++) {
        //            try {
        //                AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        //                break;
        //            } catch (Exception e) {
        //                LOG.warn("attempt #" + (i + 1) + " exception trying to routDocument: [" + e + "]");
        //                Thread.sleep(6000);
        //            }
        //        }
                WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

                // now route
                changeCurrentUser(rorenfro);
                paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
                assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, ACCOUNT_REVIEW));
                assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
                assertTrue("rorenfro should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
                documentService.approveDocument(paymentRequestDocument, "Test approving as rorenfro", null);

                WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), KewApiConstants.ROUTE_HEADER_FINAL_CD);

                // check if the purchase order document is closed
                PurchaseOrderDocument purchaseOrderDocument =(PurchaseOrderDocument) purchaseOrderService.getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderDocument().getPurapDocumentIdentifier()); //documentService.getByDocumentHeaderId(poDocId);
                assertTrue( "Purchase order should be closed.", PurchaseOrderStatuses.CLOSED.equals( purchaseOrderDocument.getStatusCode() ) );
                */
    }

    // Commented due to Jira issue preventing documents created by PREQ from going to final
    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testReopenPo() throws Exception {
        // route a preq and mark the close po indicator
        /*
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.REOPEN_PO_WITH_PREQ, true);
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
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("rorenfro should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(paymentRequestDocument, "Test approving as rorenfro", null);

        WorkflowTestUtils.waitForStatusChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), KewApiConstants.ROUTE_HEADER_FINAL_CD);

        // check if the purchase order document is open
        PurchaseOrderDocument purchaseOrderDocument =(PurchaseOrderDocument)purchaseOrderService.getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderDocument().getPurapDocumentIdentifier()); //documentService.getByDocumentHeaderId(poDocId);
        assertTrue( "Purchase order should be opened.", PurchaseOrderStatuses.OPEN.equals( purchaseOrderDocument.getStatusCode() ) );
        */
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testRequestCancel() throws Exception {
        // route a preq and mark the preq as requested cancel
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

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testRequestHold() throws Exception {
        // route a preq and mark the preq as requested hold
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

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testCalculate() throws Exception {
    }

    /**
     *
     * Test Usetax and split accounting
     * @throws Exception
     */
    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testUseTax() throws Exception {
        purchaseOrderDocument = createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        purchaseOrderDocument.setDeliveryBuildingCode("BL");
        purchaseOrderDocument.setDeliveryBuildingLine1Address("2332 Correa Rd");
        purchaseOrderDocument.setDeliveryBuildingRoomNumber("124");
        purchaseOrderDocument.setDeliveryCityName("Tuscon");
        purchaseOrderDocument.setDeliveryStateCode("AZ");
        purchaseOrderDocument.setDeliveryPostalCode("85034");
        purchaseOrderDocument.setDeliveryCountryCode("US");

        purchaseOrderDocument.setBillingLine1Address("2332 Correa Rd");
        purchaseOrderDocument.setBillingCityName("Tuscon");
        purchaseOrderDocument.setBillingStateCode("AZ");
        purchaseOrderDocument.setBillingPostalCode("85034");
        purchaseOrderDocument.setBillingCountryCode("US");

        purchaseOrderDocument.setVendorName("Prepotech");
        purchaseOrderDocument.setVendorCityName("Rocky Hill");
        purchaseOrderDocument.setVendorNumber("4105-0");
        purchaseOrderDocument.setVendorStateCode("NJ");
        purchaseOrderDocument.setVendorPostalCode("08553");
        purchaseOrderDocument.setVendorCountryCode("US");
        purchaseOrderDocument.setUseTaxIndicator(true);
        List<PurchaseOrderItem> poiList = generateItems();
        purchaseOrderDocument.setItems(poiList); // add items
        purchaseOrderDocument.fixItemReferences();
        purchaseOrderDocument.setTotalDollarAmount(new KualiDecimal(1000));
        paymentRequestDocument = createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, purchaseOrderDocument, true, new KualiDecimal[] { new KualiDecimal(1) });
        // setup payment request items

        for (PurchaseOrderItem poi : poiList) {
            if (poi.isItemActiveIndicator() && (poi.getItemQuantity().isPositive()) && (poi.getItemUnitPrice().intValue() > 0)) {
                PaymentRequestItem pri = new PaymentRequestItem(poi, paymentRequestDocument);
                // pri.setUseTaxItems(poi.getUseTaxItems());
                paymentRequestDocument.addItem(pri);
            }
        }
        updateQuantityAndPrice(paymentRequestDocument, new KualiDecimal[] { new KualiDecimal(1) });
        paymentRequestDocument.setUseTaxIndicator(true);
        AccountingDocumentTestUtils.testSaveDocument(paymentRequestDocument, documentService);

        // retrieve saved payment request
        final String preqDocId = paymentRequestDocument.getDocumentNumber();
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(preqDocId);

        final String docId = paymentRequestDocument.getDocumentNumber();

        AccountingDocumentTestUtils.routeDocument(paymentRequestDocument, documentService);
        WorkflowTestUtils.waitForNodeChange(paymentRequestDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to vputman as Fiscal Officer
        changeCurrentUser(rorenfro);
        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(paymentRequestDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("rorenfro should have an approve request.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());

        // TODO: This assert needs to be based on non-DB data. Until this is re-written, it will have to be manually changed every time the DB changes.
        // assertTrue("total values should be 183", paymentRequestDocument.getTotalDollarAmount().equals(new KualiDecimal(183)));
        documentService.approveDocument(paymentRequestDocument, "Test approving as rorenfro", null);

        WorkflowTestUtils.waitForDocumentApproval(paymentRequestDocument.getDocumentNumber());

        paymentRequestDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", paymentRequestDocument.getDocumentHeader().getWorkflowDocument().isFinal());

        // paymentRequestDocument.getItems();

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
    public final PurchaseOrderDocument createPurchaseOrderDocument(PurchaseOrderDocumentFixture poFixture, boolean routePO) throws Exception {

        // check if test has been setup
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }

        // setup purchase order and save
        changeCurrentUser(parke);
        PurchaseOrderDocument po = poFixture.createPurchaseOrderDocument();
        po.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_OPEN);
        po.refreshNonUpdateableReferences();
        AccountingDocumentTestUtils.testSaveDocument(po, documentService);

        // retrieve saved purchase order
        final String poDocId = po.getDocumentNumber();
        po = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocId);

        // route if requested
        if (routePO) {
            AccountingDocumentTestUtils.testRouteDocument(po, documentService);
            WorkflowTestUtils.waitForDocumentApproval(po.getDocumentNumber());
            po = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocId);
        }
        po.setAccountDistributionMethod("S");
        return po;
    }

    /* Populates the Purchase Order Item using the info contained in this fixture with split accounting 30/70
     *
     * @return the populated Purchase Order Item.
     */
    private PurchaseOrderItem createPoItem() {
        String chart_code = "BL";
        BigDecimal percentage = BigDecimal.valueOf(100);
        String item_desc = "Iphones";
        String item_um = "CT";
        String item_catalog_number = "1F742";

        PurchaseOrderItem poi = new PurchaseOrderItem();
        SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
        Integer itemIdentifier = sas.getNextAvailableSequenceNumber("PO_ITM_ID", PurchaseOrderDocument.class).intValue();
        poi.setItemIdentifier(itemIdentifier);
        poi.setItemDescription(item_desc);
        poi.setItemUnitOfMeasureCode(item_um);
        poi.setItemUnitPrice(BigDecimal.valueOf(100));
        poi.setItemTypeCode("ITEM");
        poi.setItemQuantity(new KualiDecimal(2));
        poi.setItemLineNumber(new Integer(1));
        poi.setItemUnitPrice(BigDecimal.valueOf(100));

        poi.setTotalAmount(new KualiDecimal(5000));
        poi.setItemCatalogNumber(item_catalog_number);

        itemIdentifier = sas.getNextAvailableSequenceNumber("PO_ITM_ID", PurchaseOrderDocument.class).intValue();
        PurchaseOrderItemUseTax newItemUseTax = new PurchaseOrderItemUseTax();
//        newItemUseTax.setAutoIncrementSet(true);
        newItemUseTax.setChartOfAccountsCode(chart_code);
        newItemUseTax.setAccountNumber("0102395");
        newItemUseTax.setFinancialObjectCode("9016");
        newItemUseTax.setTaxAmount(new KualiDecimal(56));
        newItemUseTax.setRateCode("5");
        newItemUseTax.setItemIdentifier(itemIdentifier);
        poi.getUseTaxItems().add(newItemUseTax);

        itemIdentifier = sas.getNextAvailableSequenceNumber("PO_ITM_ID", PurchaseOrderDocument.class).intValue();
        newItemUseTax = new PurchaseOrderItemUseTax();
//        newItemUseTax.setAutoIncrementSet(true);
        newItemUseTax.setChartOfAccountsCode(chart_code);
        newItemUseTax.setAccountNumber("0102395");
        newItemUseTax.setFinancialObjectCode("9015");
        newItemUseTax.setTaxAmount(new KualiDecimal(20));
        newItemUseTax.setRateCode("7");
        newItemUseTax.setItemIdentifier(itemIdentifier);
        poi.getUseTaxItems().add(newItemUseTax);

        itemIdentifier = sas.getNextAvailableSequenceNumber("PO_ITM_ID", PurchaseOrderDocument.class).intValue();
        newItemUseTax = new PurchaseOrderItemUseTax();
//        newItemUseTax.setAutoIncrementSet(true);
        newItemUseTax.setChartOfAccountsCode(chart_code);
        newItemUseTax.setAccountNumber("0102395");
        newItemUseTax.setFinancialObjectCode("9016");
        newItemUseTax.setTaxAmount(new KualiDecimal(7));
        newItemUseTax.setRateCode("12");
        newItemUseTax.setItemIdentifier(itemIdentifier);
        poi.getUseTaxItems().add(newItemUseTax);

        // poi.refreshNonUpdateableReferences();
        List<PurApAccountingLine> lines = new ArrayList<PurApAccountingLine>();
        PurchaseOrderAccount poAccount = new PurchaseOrderAccount();
        poAccount.setAccountNumber("1031400");
        poAccount.setAccountLinePercent(BigDecimal.valueOf(70));
        poAccount.setAmount(new KualiDecimal(70));
        poAccount.setChartOfAccountsCode(chart_code);
        poAccount.setFinancialObjectCode("5000");
        lines.add(poAccount);

        PurchaseOrderAccount poAccount1 = new PurchaseOrderAccount();
        poAccount1.setAccountNumber("1031420");
        poAccount1.setAccountLinePercent(BigDecimal.valueOf(30));
        poAccount1.setAmount(new KualiDecimal(30));
        poAccount1.setChartOfAccountsCode(chart_code);
        poAccount1.setFinancialObjectCode("5000");
        lines.add(poAccount1);

        poi.setSourceAccountingLines(lines);
        poi.setItemActiveIndicator(true);
        poi.refreshNonUpdateableReferences();
        return poi;

    }

    /**
     *
     * generate list of usetax items to test with
     * @return
     * @throws Exception
     */
    private List<PurchaseOrderItem> generateItems() throws Exception {
        List<PurchaseOrderItem> items = new ArrayList<PurchaseOrderItem>();
        // set items to document
        items.add(createPoItem());
        return items;
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
    public final PaymentRequestDocument createPaymentRequestDocument(PaymentRequestDocumentFixture preqFixture, PurchaseOrderDocument po, boolean copyPoItems, KualiDecimal[] itemQuantityList) throws Exception {

        // check if test has been setup
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }

        // setup purchase order and save
        changeCurrentUser(appleton);
        PaymentRequestDocument preq = preqFixture.createPaymentRequestDocument();
        preq.initiateDocument();

        // set payment request from po, and optionally copy the items
        if (copyPoItems) {
            preq.populatePaymentRequestFromPurchaseOrder(po, new HashMap());
        } else {
            populatePaymentRequestFromPurchaseOrderWithoutItems(preq, po);
        }

        // set quantity and calculate price
        updateQuantityAndPrice(preq, itemQuantityList);

        AccountingDocumentTestUtils.testSaveDocument(preq, documentService);

        // retrieve saved payment request
        final String preqDocId = preq.getDocumentNumber();
        preq = (PaymentRequestDocument) documentService.getByDocumentHeaderId(preqDocId);
        preq.setAccountDistributionMethod("S");
        return preq;
    }

    /**
     * Populates fields on a payment request with data from a purchase order, but
     * does not copy the purchase order items.
     *
     * @param preq
     * @param po
     */
    private void populatePaymentRequestFromPurchaseOrderWithoutItems(PaymentRequestDocument preq, PurchaseOrderDocument po) {

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
        } else {
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
        } else {
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
    public void updateQuantityAndPrice(PaymentRequestDocument preq, KualiDecimal[] quantityList) {

        int quantityIndex = 0;
        KualiDecimal quantity = null;

        for (PaymentRequestItem pri : (List<PaymentRequestItem>) preq.getItems()) {
            if (pri.getItemType().isLineItemIndicator()) {
                if (pri.getItemType().isQuantityBasedGeneralLedgerIndicator()) {

                    // if within range of passed in quantities
                    if (quantityIndex <= (quantityList.length - 1)) {
                        quantity = quantityList[quantityIndex];
                    } else {
                        quantity = new KualiDecimal(0);

                    }
                    pri.setItemQuantity(quantity);
                    pri.setExtendedPrice(pri.calculateExtendedPrice());
                    BigDecimal calcExtendedPrice = pri.getItemUnitPrice().multiply(pri.getItemQuantity().bigDecimalValue());
                } else {
                    pri.setExtendedPrice(pri.calculateExtendedPrice());
                }
            } else {
                pri.setExtendedPrice(pri.calculateExtendedPrice());
                // LOG.info("updateQuantityandPrice() no gl"+ pri.getItemQuantity() + " : "+ pri.getItemDescription() +
                // " : up "+ pri.getItemUnitPrice() + " calc " + pri.calculateExtendedPrice());
            }

            for (PurApAccountingLine accountingLine : pri.getSourceAccountingLines()) {
                accountingLine.setAmount(pri.getExtendedPrice().multiply(new KualiDecimal(accountingLine.getAccountLinePercent())).divide(new KualiDecimal(100)));
            }
        }

    }

}
