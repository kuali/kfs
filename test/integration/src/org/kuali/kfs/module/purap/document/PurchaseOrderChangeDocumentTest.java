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
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.fixtures.PaymentRequestDocumentFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.RelatesTo;
import org.kuali.test.suite.RelatesTo.JiraIssue;

public class PurchaseOrderChangeDocumentTest extends KualiTestBase {
    
    protected static DocumentService docService = null;
    protected static PurchaseOrderService poService = null;
    protected static PurchaseOrderDocument poTest = null;
    protected static PurchaseOrderDocument poChange = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        docService = SpringContext.getBean(DocumentService.class);
        poService = SpringContext.getBean(PurchaseOrderService.class);
        // Create and save a minimally-populated basic PO document for each test.
        poTest = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        poTest.setStatusCode(PurchaseOrderStatuses.OPEN);
        poTest.refreshNonUpdateableReferences();
        poTest.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(poTest, docService);
    }

    protected void tearDown() throws Exception {
        docService = null;
        poService = null;
        poTest = null;
        poChange = null;
        super.tearDown();
    }
    
    /**
     * Refreshes poTest and poChange by fetching them from DB, 
     * so that changes made to them during routing will be reflected.
     */
    private void refreshPO() {
        poTest = poService.getPurchaseOrderByDocumentNumber(poTest.getDocumentNumber());
        //poChange = poService.getPurchaseOrderByDocumentNumber(poChange.getDocumentNumber());        
    }
    
    /**
     * Calls PO service to create and save a PO change document and assigns the new document to poChange;
     * refreshes poTest by fetching it from DB so that changes made to the existing PO will be reflected;
     * also, makes reasons for test exceptions more obvious by inserting into the exception's 
     * message the error maps which contain the keys to human-readable reasons for rule failure.
     * 
     * @param documentType      A String, one of the PurchaseOrderDocumentTypes.
     * @param documentStatus    A String, one of the PurchaseOrderDocumentStatuses.
     * 
     * @throws Exception
     */
    private void createAndSavePOChangeDocument(String documentType, String documentStatus) throws Exception {
        try {
            poChange = poService.createAndSavePotentialChangeDocument(
                    poTest.getPurchaseOrderRestrictedMaterials(), poTest.getPurchaseOrderRestrictionStatusHistories(), poTest.getDocumentNumber(), documentType, documentStatus);
            poTest = poService.getPurchaseOrderByDocumentNumber(poTest.getDocumentNumber());
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getErrorMap().toString() + ve);
        }
    }
    
    /**
     * Calls PO service to create and route a PO change document and assigns the new document to poChange;
     * refreshes poTest by fetching it from DB so that changes made to the existing PO will be reflected;
     * also, makes reasons for test exceptions more obvious by inserting into the exception's 
     * message the error maps which contain the keys to human-readable reasons for rule failure.
     * 
     * @param documentType      A String, one of the PurchaseOrderDocumentTypes.
     * @param documentStatus    A String, one of the PurchaseOrderDocumentStatuses.
     * 
     * @throws Exception
     */
    private void createAndRoutePOChangeDocument(String documentType, String documentStatus) throws Exception {
        try {
            poChange = poService.createAndRoutePotentialChangeDocument(
                    poTest.getPurchaseOrderRestrictedMaterials(), poTest.getPurchaseOrderRestrictionStatusHistories(), poTest.getDocumentNumber(), documentType, "unit test", new ArrayList(), documentStatus);
            poTest = poService.getPurchaseOrderByDocumentNumber(poTest.getDocumentNumber());
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getErrorMap().toString() + ve);
        }
    }
    
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testAmendPurchaseOrder() throws Exception {        
        createAndSavePOChangeDocument(
                    PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, 
                    PurchaseOrderStatuses.AMENDMENT);
        assertMatchChangePO(poTest, poChange);
        if (!poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals("F")) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.AMENDMENT));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CHANGE_IN_PROCESS));
        } /*
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), "F");     
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.OPEN));
        } */
    }
    
    /**
     * Creates and saves a PurchaseOrderAmendmentDocument, then calls
     * the documentService to cancel the PurchaseOrderAmendmentDocument.
     * This is to test the case for canceling a PurchaseOrderAmendmentDocument.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testCancelAmendPurchaseOrder() throws Exception {        
        createAndSavePOChangeDocument(
                    PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, 
                    PurchaseOrderStatuses.AMENDMENT);
        assertMatchChangePO(poTest, poChange);
        if (!poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals("F")) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.AMENDMENT));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CHANGE_IN_PROCESS));
        } 
        SpringContext.getBean(DocumentService.class).cancelDocument(poChange, "");
        assertTrue(poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals(KFSConstants.DocumentStatusCodes.CANCELLED));
    }
    
    @RelatesTo(JiraIssue.KULPURAP2226)
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testPurchaseOrderPaymentHold() throws Exception {
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT, 
                PurchaseOrderStatuses.PENDING_PAYMENT_HOLD);       
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals("F")) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.PENDING_PAYMENT_HOLD));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), "F");           
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().stateIsFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.PAYMENT_HOLD));   
        //}
    }
    
    @RelatesTo(JiraIssue.KULPURAP2226)
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testPurchaseOrderRemoveHold() throws Exception {
        poTest.setStatusCode(PurchaseOrderStatuses.PAYMENT_HOLD);
        poTest.refreshNonUpdateableReferences();
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT, 
                PurchaseOrderStatuses.PENDING_REMOVE_HOLD);       
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals("F")) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.PENDING_REMOVE_HOLD));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), "F");           
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().stateIsFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.OPEN));   
        //}
    }
    
    @RelatesTo(JiraIssue.KULPURAP2226)
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testPurchaseOrderClose() throws Exception {
        // There must be a PREQ against this PO in order to close this PO.
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_FOR_PO_CLOSE_DOC.createPaymentRequestDocument();
        preq.setPurchaseOrderIdentifier(poTest.getPurapDocumentIdentifier());
        AccountingDocumentTestUtils.saveDocument(preq, docService);
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT,
                PurchaseOrderStatuses.PENDING_CLOSE);
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals("F")) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.PENDING_CLOSE));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), "F");           
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().stateIsFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CLOSED));   
        //}
    }   
    
    @RelatesTo(JiraIssue.KULPURAP2226)
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testPurchaseOrderReopen() throws Exception {     
        poTest.setStatusCode(PurchaseOrderStatuses.CLOSED);
        poTest.refreshNonUpdateableReferences();
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, 
                PurchaseOrderStatuses.PENDING_REOPEN);       
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals("F")) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.PENDING_REOPEN));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), "F");           
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().stateIsFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.OPEN));   
        //}
    }
    
    @RelatesTo(JiraIssue.KULPURAP2226)
    @ConfigureContext(session = KULUSER, shouldCommitTransactions=true)
    public final void testPurchaseOrderVoid() throws Exception {
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT,
                PurchaseOrderStatuses.PENDING_VOID);      
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus().equals("F")) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.PENDING_VOID));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), "F");           
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().stateIsFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getStatusCode().equals(PurchaseOrderStatuses.RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getStatusCode().equals(PurchaseOrderStatuses.VOID));   
        //}
    }
    
    /**
     * Matches an existing Purchase Order Document with a change PO Document newly generated from it;
     * Fails the assertion if any of the copied persistent fields don't match.
     */
    public static void assertMatchChangePO(PurchaseOrderDocument doc1, PurchaseOrderDocument doc2) {
        // match posting year
        if (StringUtils.isNotBlank(doc1.getPostingPeriodCode()) && StringUtils.isNotBlank(doc2.getPostingPeriodCode())) {
            Assert.assertEquals(doc1.getPostingPeriodCode(), doc2.getPostingPeriodCode());
        }
        Assert.assertEquals(doc1.getPostingYear(), doc2.getPostingYear());

        // match important fields in PO        
        Assert.assertEquals(doc1.getVendorHeaderGeneratedIdentifier(), doc2.getVendorHeaderGeneratedIdentifier());
        Assert.assertEquals(doc1.getVendorDetailAssignedIdentifier(), doc2.getVendorDetailAssignedIdentifier());
        Assert.assertEquals(doc1.getVendorName(), doc2.getVendorName());
        Assert.assertEquals(doc1.getVendorNumber(), doc2.getVendorNumber());

        Assert.assertEquals(doc1.getChartOfAccountsCode(), doc2.getChartOfAccountsCode());
        Assert.assertEquals(doc1.getOrganizationCode(), doc2.getOrganizationCode());
        Assert.assertEquals(doc1.getDeliveryCampusCode(), doc2.getDeliveryCampusCode());
        Assert.assertEquals(doc1.getDeliveryRequiredDate(), doc2.getDeliveryRequiredDate());
        Assert.assertEquals(doc1.getRequestorPersonName(), doc2.getRequestorPersonName());
        Assert.assertEquals(doc1.getContractManagerCode(), doc2.getContractManagerCode());
        Assert.assertEquals(doc1.getVendorContractName(), doc2.getVendorContractName());
        Assert.assertEquals(doc1.getPurchaseOrderAutomaticIndicator(), doc2.getPurchaseOrderAutomaticIndicator());
        Assert.assertEquals(doc1.getPurchaseOrderTransmissionMethodCode(), doc2.getPurchaseOrderTransmissionMethodCode());

        Assert.assertEquals(doc1.getRequisitionIdentifier(), doc2.getRequisitionIdentifier());
        Assert.assertEquals(doc1.getPurchaseOrderPreviousIdentifier(), doc2.getPurchaseOrderPreviousIdentifier());
        Assert.assertEquals(doc1.getPurchaseOrderCreateDate(), doc2.getPurchaseOrderCreateDate());
        Assert.assertEquals(doc1.getPurchaseOrderLastTransmitDate(), doc2.getPurchaseOrderLastTransmitDate());        
    }

}
