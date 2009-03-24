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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.CreditMemoDocumentTest;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocumentTest;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentWithCommodityCodeFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderVendorQuoteFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.suite.RelatesTo;
import org.kuali.kfs.sys.suite.RelatesTo.JiraIssue;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = parke, shouldCommitTransactions=true)
public class PurchaseOrderServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderServiceTest.class);

    private DocumentService docService;
    private PurchaseOrderService poService;
    private static final String ACCOUNT_REVIEW = "Account Review";
    
    @Override
    protected void setUp() throws Exception {      
        super.setUp();
        if (null == docService) {
            docService = SpringContext.getBean(DocumentService.class);
        }
        if (null == poService) {
            poService = SpringContext.getBean(PurchaseOrderService.class);
        }
    }

    public final void testPurchaseOrderRetransmit() throws Exception {   
        // Create and save a minimally-populated basic PO document for each test.
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        po.refreshNonUpdateableReferences();
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);

        PurchaseOrderDocument poRetrans = null;
        try {
            poRetrans = poService.createAndSavePotentialChangeDocument(
                    po.getDocumentNumber(), 
                    PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, 
                    PurchaseOrderStatuses.PENDING_RETRANSMIT);  
            po = poService.getPurchaseOrderByDocumentNumber(po.getDocumentNumber());
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getErrorMap().toString() + ve);
        }      
        assertMatchRetransmit(po, poRetrans); 
        ((PurchaseOrderItem)poRetrans.getItem(0)).setItemSelectedForRetransmitIndicator(true);
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            poService.retransmitPurchaseOrderPDF(poRetrans, baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }
    
    public final void testPurchaseOrderPrint() throws Exception {        
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        po.refreshNonUpdateableReferences();
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            poService.performPrintPurchaseOrderPDFOnly(po.getDocumentNumber(), baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }

    
    public final void testGetInternalPurchasingDollarLimit() {
        PurchaseOrderDocument po = PurchaseOrderDocumentFixture.PO_WITH_VENDOR_CONTRACT.createPurchaseOrderDocument();

        VendorContract contract = new VendorContract();
        Integer contrID = po.getVendorContractGeneratedIdentifier();
        KualiDecimal contrAPOLimit = new KualiDecimal(100000.00);
        contract.setVendorContractGeneratedIdentifier(contrID);
        contract.setOrganizationAutomaticPurchaseOrderLimit(contrAPOLimit);
        
        ContractManager manager = new ContractManager();
        Integer mngrCode = po.getContractManagerCode();
        manager.setContractManagerCode(mngrCode);
        KualiDecimal mngrDDLimit = new KualiDecimal(100000.00);  
        manager.setContractManagerDelegationDollarLimit(mngrDDLimit);

        VendorService vdService = SpringContext.getBean(VendorService.class);
        String chartCode = po.getChartOfAccountsCode();
        String orgCode = po.getOrganizationCode();
        KualiDecimal contrDLimit = vdService.getApoLimitFromContract(contrID, chartCode, orgCode);
        KualiDecimal result;

        // contract == null & manager != null
        po.setContractManager(manager);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, mngrDDLimit);
        
        // contract != null & manager == null
        po.setVendorContract(contract);
        po.setContractManager(null);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, contrDLimit);
        
        // contract != null & manager != null, and contract limit > manager limit
        po.setVendorContract(contract);
        po.setContractManager(manager);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, contrDLimit);
        
        // contract != null & manager != null, and contract limit < manager limit
        po.setVendorContract(contract);
        po.setContractManager(manager);
        KualiDecimal mngrDDLimit1 = new KualiDecimal(150000.00);  
        manager.setContractManagerDelegationDollarLimit(mngrDDLimit1);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, mngrDDLimit1);
    }
        
    /**
     * Matches an existing Purchase Order Document with a retransmited PO Document newly generated from it;
     * Fails the assertion if any of the copied persistent fields don't match.
     */
    public static void assertMatchRetransmit(PurchaseOrderDocument doc1, PurchaseOrderDocument doc2) {
        // match posting year
        if (StringUtils.isNotBlank(doc1.getPostingPeriodCode()) && StringUtils.isNotBlank(doc2.getPostingPeriodCode())) {
            assertEquals(doc1.getPostingPeriodCode(), doc2.getPostingPeriodCode());
        }
        assertEquals(doc1.getPostingYear(), doc2.getPostingYear());
    
        // match important fields in PO       
        assertEquals(doc1.getVendorHeaderGeneratedIdentifier(), doc2.getVendorHeaderGeneratedIdentifier());
        assertEquals(doc1.getVendorDetailAssignedIdentifier(), doc2.getVendorDetailAssignedIdentifier());
        assertEquals(doc1.getVendorName(), doc2.getVendorName());
        assertEquals(doc1.getVendorNumber(), doc2.getVendorNumber());
    
        assertEquals(doc1.getChartOfAccountsCode(), doc2.getChartOfAccountsCode());
        assertEquals(doc1.getOrganizationCode(), doc2.getOrganizationCode());
        assertEquals(doc1.getDeliveryCampusCode(), doc2.getDeliveryCampusCode());
        assertEquals(doc1.getDeliveryRequiredDate(), doc2.getDeliveryRequiredDate());
        assertEquals(doc1.getRequestorPersonName(), doc2.getRequestorPersonName());
        assertEquals(doc1.getContractManagerCode(), doc2.getContractManagerCode());
        assertEquals(doc1.getVendorContractName(), doc2.getVendorContractName());
        assertEquals(doc1.getPurchaseOrderAutomaticIndicator(), doc2.getPurchaseOrderAutomaticIndicator());
        assertEquals(doc1.getPurchaseOrderTransmissionMethodCode(), doc2.getPurchaseOrderTransmissionMethodCode());
    
        assertEquals(doc1.getRequisitionIdentifier(), doc2.getRequisitionIdentifier());
        assertEquals(doc1.getPurchaseOrderPreviousIdentifier(), doc2.getPurchaseOrderPreviousIdentifier());
        assertEquals(doc1.getPurchaseOrderCreateTimestamp(), doc2.getPurchaseOrderCreateTimestamp());
    }    
    
    /**
     * Tests that the PurchaseOrderService would attempt to update vendor with missing commodity codes.
     * 
     */
    public void testUpdateVendorCommodityCode() {
        PurchaseOrderDocument po = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_ACTIVE_COMMODITY_CODE_WITH_VENDOR_COMMODITY_CODE.createPurchaseOrderDocument();
        
        VendorDetail updatedVendor = poService.updateVendorWithMissingCommodityCodesIfNecessary(po);
        
        assertFalse(updatedVendor == null);

        if (updatedVendor != null) {
            boolean foundMatching = false;
            for (VendorCommodityCode vcc : updatedVendor.getVendorCommodities()) {
                if (vcc.getPurchasingCommodityCode().equals(((PurchaseOrderItem) po.getItem(0)).getPurchasingCommodityCode())) {
                    foundMatching = true;
                    break;
                }
            }

            assertTrue(foundMatching);
        }
    }
    
    /**
     * Tests that the PurchaseOrderService would create an Automatic Purchase Order document.
     * 
     * @throws Exception
     */
    public void testCreateAutomaticPurchaseOrderDocument() throws Exception {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        AccountingDocumentTestUtils.routeDocument(req, SpringContext.getBean(DocumentService.class));
        String docId = req.getDocumentNumber();
        poService.createAutomaticPurchaseOrderDocument(req);
        RequisitionDocument requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        String poDocId = requisitionDocument.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        assertTrue(purchaseOrderDocument.getPurchaseOrderAutomaticIndicator());
        assertEquals(purchaseOrderDocument.getContractManagerCode(), new Integer(99));
    }
    
    /**
     * Tests that the PurchaseOrderService would create a Purchase Order document with 
     * status In Process.
     * 
     * @throws Exception
     */
    public void testCreatePurchaseOrderDocument() throws Exception {
        RequisitionDocument req = RequisitionDocumentFixture.REQ_NO_APO_VALID.createRequisitionDocument();
        AccountingDocumentTestUtils.routeDocument(req, SpringContext.getBean(DocumentService.class));
        String docId = req.getDocumentNumber();
        Integer contractManagerCode = new Integer(12);
        poService.createPurchaseOrderDocument(req, "parke", contractManagerCode);
        RequisitionDocument requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        String poDocId = requisitionDocument.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        assertEquals(purchaseOrderDocument.getStatusCode(),PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
        assertEquals(purchaseOrderDocument.getContractManagerCode(), contractManagerCode);
    }
    
    /**
     * Tests that the PurchaseOrderService would create and save potential change document.
     * 
     * @throws Exception
     */
    public void testCreateAndSavePotentialChangeDocument() throws Exception {
        //Need to create a requisition first to be used to create an APO
        RequisitionDocument req = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        AccountingDocumentTestUtils.routeDocument(req, SpringContext.getBean(DocumentService.class));
        String docId = req.getDocumentNumber();
        
        //Create an APO using the requisition we created earlier in this method
        poService.createAutomaticPurchaseOrderDocument(req);
        RequisitionDocument requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        String poDocId = requisitionDocument.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        
        //Test the pending action indicator and status codes after invoking 
        //the createAndSavePotentialChangeDocument method.
        PurchaseOrderDocument newDocument = poService.createAndSavePotentialChangeDocument(poDocId, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PurchaseOrderStatuses.AMENDMENT);
        assertEquals (newDocument.getStatusCode(), PurchaseOrderStatuses.CHANGE_IN_PROCESS);
        PurchaseOrderDocument oldDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        assertTrue(oldDocument.isPendingActionIndicator());
        assertEquals(oldDocument.getStatusCode(), PurchaseOrderStatuses.AMENDMENT);
    }
    
    /**
     * Tests that the PurchaseOrderService would create and route potential change document.
     * 
     * @throws Exception
     */
    @RelatesTo(JiraIssue.KULPURAP3167)
    public void testCreateAndRoutePotentialChangeDocument() throws Exception {
        //Need to create a requisition first to be used to create an APO
        RequisitionDocument req = RequisitionDocumentFixture.REQ_ALTERNATE_APO.createRequisitionDocument();
        AccountingDocumentTestUtils.routeDocument(req, SpringContext.getBean(DocumentService.class));
        String docId = req.getDocumentNumber();
        
        //Create an APO using the requisition we created earlier in this method
        poService.createAutomaticPurchaseOrderDocument(req);
        RequisitionDocument requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        String poDocId = requisitionDocument.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        WorkflowTestUtils.waitForStatusChange(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
        //Test the status codes after invoking 
        //the createAndRoutePotentialChangeDocument method.
        PurchaseOrderDocument newDocument = poService.createAndRoutePotentialChangeDocument(poDocId, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, "", null, PurchaseOrderStatuses.PENDING_VOID);
        PurchaseOrderDocument oldDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        assertEquals(oldDocument.getStatusCode(), PurchaseOrderStatuses.RETIRED_VERSION);
    }

    /**
     * Tests that the PurchaseOrderService would print purchase order quote requests list pdf.
     * 
     * @throws Exception
     */
    @RelatesTo(JiraIssue.KULPURAP3140)
    public void testPrintPurchaseOrderQuoteRequestsListPDF() throws Exception {
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        
        po.refreshNonUpdateableReferences();
        
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            DateTimeService dtService = SpringContext.getBean(DateTimeService.class);

            poService.printPurchaseOrderQuoteRequestsListPDF(po, baosPDF);
            
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }
    
    /**
     * Tests that the PurchaseOrderService would print purchase order quote pdf.
     * 
     * @throws Exception
     */
    @RelatesTo(JiraIssue.KULPURAP3140)
    public void testPrintPurchaseOrderQuotePDF() throws Exception {
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        
        po.refreshNonUpdateableReferences();
        
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        
        PurchaseOrderVendorQuote vendorQuote = PurchaseOrderVendorQuoteFixture.BASIC_VENDOR_QUOTE_1.createPurchaseOrderVendorQuote();
        vendorQuote.setDocumentNumber(po.getDocumentNumber());

        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            poService.printPurchaseOrderQuotePDF(po, vendorQuote, baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }

    /**
     * Tests that the PurchaseOrderService would performPurchaseOrderFirstTransmitViaPrinting
     * 
     * 
     * @throws Exception
     */
    public void testPerformPurchaseOrderFirstTransmitViaPrinting() throws Exception {
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        
        po.refreshNonUpdateableReferences();
        
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_REQUEST_LIST");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(dtService.getCurrentDate().getTime());
            sbFilename.append(".pdf");
            poService.performPurchaseOrderFirstTransmitViaPrinting(po.getDocumentNumber(), baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }
    
    /**
     * Tests that the PurchaseOrderService would performPurchaseOrderPreviewPrinting
     * 
     * @throws Exception
     */
    public void testPerformPurchaseOrderPreviewPrinting() throws Exception {
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        
        po.refreshNonUpdateableReferences();
        
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_REQUEST_LIST");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(dtService.getCurrentDate().getTime());
            sbFilename.append(".pdf");
            poService.performPurchaseOrderPreviewPrinting(po.getDocumentNumber(), baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }
    
    /**
     * Tests that the PurchaseOrderService would performPrintPurchaseOrderPDFOnly
     * 
     * @throws Exception
     */
    public void testPerformPrintPurchaseOrderPDFOnly() throws Exception {
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        
        po.refreshNonUpdateableReferences();
        
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_REQUEST_LIST");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(dtService.getCurrentDate().getTime());
            sbFilename.append(".pdf");
            poService.performPrintPurchaseOrderPDFOnly(po.getDocumentNumber(), baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }
    
    /**
     * Tests that the PurchaseOrderService would do the completePurchaseOrder
     * for non B2B purchase orders.
     * 
     * @throws Exception
     */
    public void testCompletePurchaseOrder_NonB2B() throws Exception {
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.IN_PROCESS);
        po.refreshNonUpdateableReferences();
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        poService.completePurchaseOrder(po);
        assertEquals(po.getStatusCode(), PurchaseOrderStatuses.OPEN);
        assertTrue(po.isPurchaseOrderCurrentIndicator());
        assertNotNull(po.getPurchaseOrderInitialOpenTimestamp());
    }

    /**
     * Tests that the PurchaseOrderService would do the completePurchaseOrder
     * for B2B purchase orders.
     * 
     * @throws Exception
     */
    public void testCompletePurchaseOrder_B2B() throws Exception {
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_B2B_WITH_PO_VENDOR.createRequisitionDocument();
        final String docId = requisitionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        poService.createAutomaticPurchaseOrderDocument(requisitionDocument);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        String poDocId = requisitionDocument.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        WorkflowTestUtils.waitForStatusChange(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
        poService.completePurchaseOrder(purchaseOrderDocument);
        assertEquals(purchaseOrderDocument.getStatusCode(), PurchaseOrderStatuses.OPEN);
        assertTrue(purchaseOrderDocument.isPurchaseOrderCurrentIndicator());
        assertNotNull(purchaseOrderDocument.getPurchaseOrderInitialOpenTimestamp());
    }
    

    /**
     * This is not quite ready yet. Got some XML parsing error.
     */
//    @ConfigureContext(session = parke, shouldCommitTransactions = true)
//    public void testRetransmitB2BPurchaseOrder() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_B2B_WITH_PO_VENDOR.createRequisitionDocument();
//        final String docId = requisitionDocument.getDocumentNumber();
//        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//        poService.createAutomaticPurchaseOrderDocument(requisitionDocument);
//        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
//        String poDocId = requisitionDocument.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
//        PurchaseOrderDocument purchaseOrderDocument = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
//        WorkflowTestUtils.waitForStatusChange(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
//        poService.completePurchaseOrder(purchaseOrderDocument);
//
//        poService.retransmitB2BPurchaseOrder(purchaseOrderDocument);
//        assertTrue(GlobalVariables.getMessageList().contains(PurapKeyConstants.B2B_PO_RETRANSMIT_SUCCESS));
//    }
    
    public void testIsPurchaseOrderOpenForProcessing_HappyPath() throws Exception {
        //Create and route a basic PO to Open status.
        PurchaseOrderDocument poDocument = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        poDocument.prepareForSave();       
        assertFalse("R".equals(poDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        AccountingDocumentTestUtils.routeDocument(poDocument, "test annotation", null, docService);
        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), "F");    
    
        assertTrue(poService.isPurchaseOrderOpenForProcessing(poDocument.getPurapDocumentIdentifier()));
        
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=false)
    public void testIsPurchaseOrderOpenForProcessing_With_PREQ() throws Exception {
        PaymentRequestDocumentTest preqDocTest = new PaymentRequestDocumentTest();
        PurchaseOrderDocument purchaseOrderDocument = preqDocTest.createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED, true);
        purchaseOrderDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(new Integer(SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber("AP_PUR_DOC_LNK_ID").toString()));
        PaymentRequestDocument paymentRequestDocument = preqDocTest.createPaymentRequestDocument(PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED, 
                purchaseOrderDocument, true, new KualiDecimal[] {new KualiDecimal(100)});
        paymentRequestDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(purchaseOrderDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());
        paymentRequestDocument.setStatusCode(PaymentRequestStatuses.IN_PROCESS);        
        AccountingDocumentTestUtils.testSaveDocument(paymentRequestDocument, docService);
        assertFalse(poService.isPurchaseOrderOpenForProcessing(purchaseOrderDocument));
    }

    //testCreateAndSavePurchaseOrderSplitDocument() - this method is being tested in PurchaseOrderChangeDocumentTest.java 
    
    //testRetransmitPurchaseOrderPDF() - this method is being tested in the testPurchaseOrderRetransmit.
 
    //testCompletePurchaseOrder() - this method is being tested in the testCompletePurchaseOrder_B2B and testCompletePurchaseOrder_NonB2B.
    
}

