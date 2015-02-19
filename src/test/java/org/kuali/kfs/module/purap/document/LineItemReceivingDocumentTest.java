/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.fixture.LineItemReceivingDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
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
        PurchaseOrderDocument po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
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

