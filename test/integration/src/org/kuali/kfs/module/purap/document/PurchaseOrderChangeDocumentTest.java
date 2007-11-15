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

import static org.kuali.test.fixtures.UserNameFixture.KULUSER;

import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.test.ConfigureContext;

public class PurchaseOrderChangeDocumentTest extends KualiTestBase {
    
    protected static DocumentService documentService = null;
    protected static PurchaseOrderDocument purchaseOrderDocument = null;
    protected static PurchaseOrderDocument testPO = null;
    
    protected void setUp() throws Exception {
        documentService = SpringContext.getBean(DocumentService.class);
        // Save a minimally-populated basic PO document, once.
        if(ObjectUtils.isNull(purchaseOrderDocument)){
            purchaseOrderDocument = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
            purchaseOrderDocument.setStatusCode(PurchaseOrderStatuses.OPEN);
            purchaseOrderDocument.refreshNonUpdateableReferences();
            purchaseOrderDocument.prepareForSave();       
            AccountingDocumentTestUtils.saveDocument(purchaseOrderDocument, documentService);
        }
        // Make a copy of the basic PO for the use of the current test.
        testPO = purchaseOrderDocument;
        super.setUp();
    }

    protected void tearDown() throws Exception {
        documentService = null;
        purchaseOrderDocument = null;
        testPO = null;
        super.tearDown();
    }
    
    /**
     * Used to make the reasons for test exceptions more obvious by inserting into the exception's 
     * message the error maps which contain the keys to human-readable reasons for rule failure.
     * 
     * @param documentType      A String, one of the PurchaseOrderDocumentTypes.
     * @param documentStatus    A String, one of the PurchaseOrderDocumentStatuses.
     * @throws Exception
     */
    private void createAndRoutePOChangeDocument(String documentType, String documentStatus) throws Exception {
        try {
            testPO = SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(
                    testPO.getDocumentNumber(), 
                    documentType,
                    "unit test", new ArrayList(),
                    documentStatus);
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getErrorMap().toString() + ve);
        }
    }
    
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCreateAmendPurchaseOrder() throws Exception {
        try {
            testPO = SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(
                    testPO.getDocumentNumber(), 
                    PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, 
                    PurchaseOrderStatuses.AMENDMENT);
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getErrorMap().toString() + ve);
        }
        PurchaseOrderAmendmentDocument retrievedPO = (PurchaseOrderAmendmentDocument)documentService.getByDocumentHeaderId(
                testPO.getDocumentNumber()); 
        assertMatch(testPO, retrievedPO);
    }
    
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCreatePurchaseOrderPaymentHold() throws Exception {
        createAndRoutePOChangeDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT,
                PurchaseOrderStatuses.PENDING_PAYMENT_HOLD);       
        PurchaseOrderPaymentHoldDocument retrievedPO = (PurchaseOrderPaymentHoldDocument)documentService.getByDocumentHeaderId(
                testPO.getDocumentNumber()); 
        assertMatch(testPO, retrievedPO);
    }
    
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCreatePurchaseOrderRemoveHold() throws Exception {
        testPO.setStatusCode(PurchaseOrderStatuses.PAYMENT_HOLD);
        createAndRoutePOChangeDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT,
                PurchaseOrderStatuses.PENDING_REMOVE_HOLD);       
        PurchaseOrderRemoveHoldDocument retrievedPO = (PurchaseOrderRemoveHoldDocument)documentService.getByDocumentHeaderId(
                testPO.getDocumentNumber()); 
        assertMatch(testPO, retrievedPO);
    }
    
    /*
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCreatePurchaseOrderClose() throws Exception {
        // There must be a PREQ against this PO in order to close this PO.
        // TODO: Add a PREQ, once there is a PaymentRequestDocumentFixture.
        createAndRoutePOChangeDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT,
                PurchaseOrderStatuses.PENDING_CLOSE);
        PurchaseOrderCloseDocument retrievedPO = (PurchaseOrderCloseDocument)documentService.getByDocumentHeaderId(
                testPO.getDocumentNumber()); 
        assertMatch(testPO, retrievedPO);
    }   
    
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCreatePurchaseOrderReopen() throws Exception {
        testPO.setStatusCode(PurchaseOrderStatuses.CLOSED);
        createAndRoutePOChangeDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT,
                PurchaseOrderStatuses.PENDING_REOPEN);       
        PurchaseOrderReopenDocument retrievedPO = (PurchaseOrderReopenDocument)documentService.getByDocumentHeaderId(
                testPO.getDocumentNumber()); 
        assertMatch(testPO, retrievedPO);
    }
    
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCreatePurchaseOrderVoid() throws Exception {
        createAndRoutePOChangeDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT,
                PurchaseOrderStatuses.PENDING_VOID);      
        PurchaseOrderVoidDocument retrievedPO = (PurchaseOrderVoidDocument)documentService.getByDocumentHeaderId(
                testPO.getDocumentNumber()); 
        assertMatch(testPO, retrievedPO);
    }
    */
    
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCreatePurchaseOrderRetransmit() throws Exception {
        testPO.setStatusCode(PurchaseOrderStatuses.CLOSED);
        createAndRoutePOChangeDocument(PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT,
                PurchaseOrderStatuses.PENDING_RETRANSMIT);       
        PurchaseOrderRetransmitDocument retrievedPO = (PurchaseOrderRetransmitDocument)documentService.getByDocumentHeaderId(
                testPO.getDocumentNumber()); 
        assertMatchExceptTransmitDate(testPO, retrievedPO);        
    }
    
    private static void assertMatch(PurchaseOrderDocument doc1, PurchaseOrderDocument doc2) {
        PurchaseOrderDocumentTest.assertMatch(doc1, doc2);
    }
    
    public static void assertMatchExceptTransmitDate(PurchaseOrderDocument doc1, PurchaseOrderDocument doc2) {
        // match header
        assertEquals(doc1.getDocumentNumber(), doc2.getDocumentNumber());
        assertEquals(doc1.getDocumentHeader().getWorkflowDocument().getDocumentType(), doc2.getDocumentHeader().getWorkflowDocument().getDocumentType());
    
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
        assertEquals(doc1.getStatusCode(), doc2.getStatusCode());
    
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
        assertEquals(doc1.isPurchaseOrderCurrentIndicator(), doc2.isPurchaseOrderCurrentIndicator());
        assertEquals(doc1.getPurchaseOrderCreateDate(), doc2.getPurchaseOrderCreateDate());
    }
}
