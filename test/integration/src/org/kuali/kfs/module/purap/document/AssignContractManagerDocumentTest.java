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

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.PARKE;
import static org.kuali.test.fixtures.UserNameFixture.RJWEISS;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;
import static org.kuali.test.fixtures.UserNameFixture.KULUSER;

import java.util.List;

import org.kuali.core.bo.AdHocRouteRecipient;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.fixtures.AssignContractManagerDocumentFixture;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is used to test create and route populated Assign Contract Manager Documents.
 */
@ConfigureContext(session = PARKE)
public class AssignContractManagerDocumentTest extends KualiTestBase {
    public static final Class<AssignContractManagerDocument> DOCUMENT_CLASS = AssignContractManagerDocument.class;
    private static final String ACCOUNT_REVIEW = "Account Review";

    private AssignContractManagerDocument acmDocument = null;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        acmDocument = null;
        super.tearDown();
    }

    private int getExpectedPrePeCount() {
        return 0;
    }

    /**
     * Tests the routing of AssignContractManagerDocument to final.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = PARKE, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        acmDocument = buildSimpleDocument();
        
        acmDocument.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        assertFalse("R".equals(acmDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        routeDocument(acmDocument, "saving copy source document", documentService);
        WorkflowTestUtils.waitForStatusChange(acmDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);
        Document document = documentService.getByDocumentHeaderId(acmDocument.getDocumentNumber());
        assertTrue("Document should now be final.", document.getDocumentHeader().getWorkflowDocument().stateIsFinal());
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
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }

    /**
     * Helper method to create a new valid AssignContractManagerDocument.
     * 
     * @return            The AssignContractManagerDocument created by this method.
     * @throws Exception
     */
    private AssignContractManagerDocument buildSimpleDocument() throws Exception {
        List<AssignContractManagerDetail> details = AssignContractManagerDocumentFixture.ACM_DOCUMENT_VALID.getAssignContractManagerDetails();
        for (AssignContractManagerDetail detail : details) {
            RequisitionDocument routedReq = routeRequisitionUntilAwaitingContractManager(detail.getRequisition());
            detail.setRequisitionIdentifier(routedReq.getPurapDocumentIdentifier());
            detail.refreshNonUpdateableReferences();
        }
        acmDocument = AssignContractManagerDocumentFixture.ACM_DOCUMENT_VALID.createAssignContractManagerDocument();
        for (AssignContractManagerDetail detail : details) {
            detail.setAssignContractManagerDocument(acmDocument);
        }
        acmDocument.setAssignContractManagerDetails(details);
        return acmDocument;
    }

    /**
     * Helper method to route a requisition document until AwaitingContractManager status.
     * The requisition document will be used to create the AssignContractManagerDocument.
     * 
     * @param requisitionDocument The RequisitionDocument to be routed until AwaitingContractManager status.
     * @return                    The RequisitionDocument that was routed until AwaitingContractManager status.
     * @throws Exception
     */
    private RequisitionDocument routeRequisitionUntilAwaitingContractManager(RequisitionDocument requisitionDocument) throws Exception {
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

        WorkflowTestUtils.waitForStatusChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(KHUNTLEY);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());   
        changeCurrentUser(PARKE);
        return requisitionDocument;
    }

}
