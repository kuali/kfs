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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.fixture.LineItemReceivingDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Used to create and test populated Receiving Line Documents of various kinds.
 */
@ConfigureContext(session = khuntley)
public class LineItemReceivingDocumentTest extends KualiTestBase {
    public static final Class<LineItemReceivingDocument> DOCUMENT_CLASS = LineItemReceivingDocument.class;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private int getExpectedPrePeCount() {
        return 0;
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public final void testRouteDocument() throws Exception {
        //create PO
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        RequisitionDocument requisition =RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        requisition.prepareForSave();
        AccountingDocumentTestUtils.routeDocument(requisition, "saving copy source document", null, documentService);
        PurchaseOrderDocument po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        po.setRequisitionIdentifier(requisition.getPurapDocumentIdentifier());
        po.prepareForSave();
        AccountingDocumentTestUtils.routeDocument(po, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForDocumentApproval(po.getDocumentNumber());
        PurchaseOrderDocument poResult = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(po.getDocumentNumber());

        //create Receiving
        LineItemReceivingDocument receivingLineDocument = LineItemReceivingDocumentFixture.EMPTY_LINE_ITEM_RECEIVING.createLineItemReceivingDocument();
        receivingLineDocument.populateReceivingLineFromPurchaseOrder(poResult);
        for(LineItemReceivingItem rli : (List<LineItemReceivingItem>)receivingLineDocument.getItems()){
            rli.setItemReceivedTotalQuantity( rli.getItemOrderedQuantity());
        }
        receivingLineDocument.prepareForSave();
        assertFalse(DocumentStatus.ENROUTE.equals(receivingLineDocument.getDocumentHeader().getWorkflowDocument().getStatus()));
        routeDocument(receivingLineDocument, "routing line item receiving document", documentService);
        WorkflowTestUtils.waitForDocumentApproval(receivingLineDocument.getDocumentNumber());
        Document document = documentService.getByDocumentHeaderId(receivingLineDocument.getDocumentNumber());
        assertTrue("Document should now be final.", receivingLineDocument.getDocumentHeader().getWorkflowDocument().isFinal());
    }

    /**
     * Helper method to route the document.
     *
     * @param document                 The assign contract manager document to be routed.
     * @param annotation               The annotation String.
     * @param documentService          The service to use to route the document.
     * @throws WorkflowException
     */
    private void routeDocument(Document document, String annotation, DocumentService documentService) throws WorkflowException {
        try {
            documentService.routeDocument(document, annotation, null);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }
    }

}

