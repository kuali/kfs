/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.APPDOC_OPENsource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurApItemFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchaseOrderChangeDocumentTest extends KualiTestBase {

    protected static DocumentService docService = null;
    protected static PurchaseOrderService poService = null;
    protected static PurchaseOrderDocument poTest = null;
    protected static PurchaseOrderDocument poChange = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        docService = SpringContext.getBean(DocumentService.class);
        poService = SpringContext.getBean(PurchaseOrderService.class);
        // Create and save a minimally-populated basic PO document for each test.
        poTest = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        poTest.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_OPEN);
        poTest.refreshNonUpdateableReferences();
        poTest.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(poTest, docService);
    }

    @Override
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
                    poTest.getDocumentNumber(), documentType, documentStatus);
            poChange = (PurchaseOrderDocument) docService.getByDocumentHeaderId(poChange.getDocumentNumber());
            poTest = poService.getPurchaseOrderByDocumentNumber(poTest.getDocumentNumber());
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getMessageMap().toString() + ve);
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
                    poTest.getDocumentNumber(), documentType, "unit test", new ArrayList(), documentStatus);
            poTest = poService.getPurchaseOrderByDocumentNumber(poTest.getDocumentNumber());
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getMessageMap().toString() + ve);
        }
    }

    private void createAndSavePOSplitDocument(List<PurchaseOrderItem> newPOItems, boolean copyNotes, String splitNoteText) throws Exception {
        try {
            poTest.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_IN_PROCESS);
            poChange = poService.createAndSavePurchaseOrderSplitDocument(
                    newPOItems, poTest, copyNotes, splitNoteText);
            poTest = poService.getPurchaseOrderByDocumentNumber(poTest.getDocumentNumber());
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getMessageMap().toString() + ve);
        }
    }

    @ConfigureContext(session = kfs, shouldCommitTransactions=true)
    public final void testPurchaseOrderClose() throws Exception {
        // There must be a PREQ against this PO in order to close this PO.

        changeCurrentUser(appleton);
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_FOR_PO_CLOSE_DOC.createPaymentRequestDocument();
        preq.setPurchaseOrderIdentifier(poTest.getPurapDocumentIdentifier());
        AccountingDocumentTestUtils.saveDocument(preq, docService);
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT,
                PurchaseOrderStatuses.APPDOC_PENDING_CLOSE);
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.PENDING_CLOSE));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().isFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CLOSED));
        //}
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testSplitPurchaseOrder() throws Exception {
        List<PurchaseOrderItem> items = new ArrayList<PurchaseOrderItem>();
        items.add((PurchaseOrderItem)PurchaseOrderItemFixture.PO_QTY_UNRESTRICTED_ITEM_2.createPurchaseOrderItem(PurApItemFixture.BASIC_QTY_ITEM_2));
        createAndSavePOSplitDocument(items, true, "Reason for splitting.");
        // Proving that most things other than items are the same.
        assertMatchChangePO(poTest, poChange);
        assertTrue(poTest.getPurapDocumentIdentifier().compareTo(poChange.getPurapDocumentIdentifier()) < 0);
        // Neither the original nor the resulting PO may have no items.
        assertFalse(poChange.getItems().size() == 1);
        assertFalse(poTest.getItems().size() == 1);
        List<PurchaseOrderItem> splitPOItems = poChange.getItems();
        // Check renumbering.
        int i = 0;
        for (PurchaseOrderItem splitPOItem : splitPOItems ) {
            if (splitPOItem.getItemType().isLineItemIndicator()) {
                    assertTrue(splitPOItem.getItemLineNumber().intValue() == ++i);
            }
        }
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public final void testAmendPurchaseOrder() throws Exception {
        createAndSavePOChangeDocument(
                    PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT,
                    PurchaseOrderStatuses.APPDOC_AMENDMENT);
        assertMatchChangePO(poTest, poChange);
        if (!poChange.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_AMENDMENT));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS));
        } /*
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().isFinal()) {
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_OPEN));
        } */
    }

    /**
     * Creates and saves a PurchaseOrderAmendmentDocument, then calls
     * the documentService to cancel the PurchaseOrderAmendmentDocument.
     * This is to test the case for canceling a PurchaseOrderAmendmentDocument.
     *
     * @throws Exception
     */
    @ConfigureContext(session = kfs, shouldCommitTransactions=true)
    public final void testCancelAmendPurchaseOrder() throws Exception {
        createAndSavePOChangeDocument(
                    PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT,
                    PurchaseOrderStatuses.APPDOC_AMENDMENT);
        assertMatchChangePO(poTest, poChange);
        if (!poChange.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_AMENDMENT));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS));
        }
        SpringContext.getBean(DocumentService.class).cancelDocument(poChange, "");
        assertTrue(poChange.getDocumentHeader().getWorkflowDocument().getStatus().getCode().equals(KFSConstants.DocumentStatusCodes.CANCELLED));
    }

    @ConfigureContext(session = kfs, shouldCommitTransactions=true)
    public final void testPurchaseOrderPaymentHold() throws Exception {
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT,
                PurchaseOrderStatuses.APPDOC_PENDING_PAYMENT_HOLD);
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_PENDING_APPDOC_PAYMENT_HOLD));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().isFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_PAYMENT_HOLD));
        //}
    }

    @ConfigureContext(session = kfs, shouldCommitTransactions=true)
    public final void testPurchaseOrderRemoveHold() throws Exception {
        poTest.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_PAYMENT_HOLD);
        poTest.refreshNonUpdateableReferences();
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT,
                PurchaseOrderStatuses.APPDOC_PENDING_REMOVE_HOLD);
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_PENDING_REMOVE_HOLD));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().isFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_OPEN));
        //}
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public final void testPurchaseOrderReopen() throws Exception {
        poTest.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_CLOSED);
        poTest.refreshNonUpdateableReferences();
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT,
                PurchaseOrderStatuses.APPDOC_PENDING_REOPEN);
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_PENDING_REOPEN));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().isFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_OPEN));
        //}
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public final void testPurchaseOrderVoid() throws Exception {
        createAndRoutePOChangeDocument(
                PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT,
                PurchaseOrderStatuses.APPDOC_PENDING_VOID);
        assertMatchChangePO(poTest, poChange); /*
        if (!poChange.getDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.FINAL)) {
            assertTrue(poTest.isPurchaseOrderCurrentIndicator());
            assertTrue(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_PENDING_VOID));
            assertFalse(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS));
        }
        WorkflowTestUtils.waitForStatusChange(poChange.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
        refreshPO();
        if (poChange.getDocumentHeader().getWorkflowDocument().isFinal()) { */
            assertFalse(poTest.isPurchaseOrderCurrentIndicator());
            assertFalse(poTest.isPendingActionIndicator());
            assertTrue(poTest.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_RETIRED_VERSION));
            assertTrue(poChange.isPurchaseOrderCurrentIndicator());
            assertFalse(poChange.isPendingActionIndicator());
            assertTrue(poChange.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_VOID));
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
        Assert.assertEquals(doc1.getPurchaseOrderCreateTimestamp(), doc2.getPurchaseOrderCreateTimestamp());
        Assert.assertEquals(doc1.getPurchaseOrderLastTransmitTimestamp(), doc2.getPurchaseOrderLastTransmitTimestamp());
    }



}

