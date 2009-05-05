/*
 * Copyright 2009 The Kuali Foundation.
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

import static org.kuali.kfs.sys.fixture.UserNameFixture.butt;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.fixture.ContractManagerAssignmentDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Sets up documents optionally to be used by the following PurAp JMeter performance tests:
 * 
 * purapComplexSearch.jmx (needs Closed RequisitionDocument)
 * purapPaymentRequestDocument.jmx (needs Open PurchaseOrderDocument)
 * 
 * To run, change the value of SETUP_PERFORMANCE_TESTS to true.
 */
public class PurapPerformanceDocumentTest extends KualiTestBase {
    
    private static final String ACCOUNT_REVIEW = "Account";
    private RequisitionDocument requisitionDocument = null;
    private boolean SETUP_PERFORMANCE_TESTS = false; // Set to true locally to setup performance tests.
    
    private DocumentService documentService = null;

    protected void setUp() throws Exception {
        super.setUp();
        if( documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
    }

    protected void tearDown() throws Exception {
        requisitionDocument = null;
        super.tearDown();
    }

    @ConfigureContext(session = parke, shouldCommitTransactions = true)
    public final void testFullRoutePerformanceDocuments() throws Exception {
        if( !SETUP_PERFORMANCE_TESTS ) {
            assertTrue(true);
        }
        else {
            requisitionDocument = RequisitionDocumentFixture.REQ_PERFORMANCE.createRequisitionDocument();
            requisitionDocument.getDocumentHeader().setDocumentDescription("Load Testing");
            final String docId = requisitionDocument.getDocumentNumber();
            AccountingDocumentTestUtils.routeDocument(requisitionDocument, documentService);
            WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
    
            changeCurrentUser(rorenfro);  //Approving as Fiscal Officer.
            requisitionDocument = (RequisitionDocument)documentService.getByDocumentHeaderId(docId);
            assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(requisitionDocument, ACCOUNT_REVIEW));
            assertTrue("Document should be enroute.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
            assertTrue("Fiscal Officer should have an approve request.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
            SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as Fiscal Officer.", null);
            
            WorkflowTestUtils.waitForStatusChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
    
            changeCurrentUser(parke);  //Switch back to original User.
            requisitionDocument = (RequisitionDocument)documentService.getByDocumentHeaderId(docId);
            assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
            
            ContractManagerAssignmentDocument cmaDocument = buildContractManagerAssignmentDocument();       
            cmaDocument.prepareForSave();
            
            assertFalse("R".equals(cmaDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
            routeDocument(cmaDocument, "saving copy source document", documentService);
            
            WorkflowTestUtils.waitForStatusChange(cmaDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);      
            cmaDocument = (ContractManagerAssignmentDocument)documentService.getByDocumentHeaderId(cmaDocument.getDocumentNumber());
            assertTrue("Document should now be final.", cmaDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
            
            String poNumber = requisitionDocument.getRelatedViews().getRelatedPurchaseOrderViews().get(0).getDocumentNumber();
            PurchaseOrderDocument poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poNumber);
            
            poDoc.setVendorName("KUALI UNIVERSITY");
            poDoc.setVendorHeaderGeneratedIdentifier(1001);
            poDoc.setVendorDetailAssignedIdentifier(0);
            poDoc.setVendorLine1Address("341 PINE TREE RD");
            poDoc.setVendorCityName("ITHACA");
            poDoc.setVendorStateCode("NY");
            poDoc.setVendorPostalCode("14850");
            poDoc.setVendorCountryCode("US");
            poDoc.setVendorPaymentTermsCode("00N30");       
            poDoc.setPurchaseOrderVendorChoiceCode("ONLY");
            
            routeDocument(poDoc, "Test routing as parke", documentService);
            
            changeCurrentUser(butt);  //Approving at the Budget Level
            poDoc = (PurchaseOrderDocument)documentService.getByDocumentHeaderId(poNumber);
            assertTrue("Document should be enroute.", poDoc.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
            assertTrue("Budget Approver should have an approve request.", poDoc.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
            SpringContext.getBean(DocumentService.class).approveDocument(poDoc, "Test approving as Budget Approver.", null);
            
            WorkflowTestUtils.waitForStatusChange(poDoc.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
    
            changeCurrentUser(parke);  //Switch back to original User.
            poDoc = (PurchaseOrderDocument)documentService.getByDocumentHeaderId(poNumber);
            assertTrue("Document should now be final.", poDoc.getDocumentHeader().getWorkflowDocument().stateIsFinal());
        }
    }
    
    private ContractManagerAssignmentDocument buildContractManagerAssignmentDocument() throws Exception {
        ContractManagerAssignmentDocument cmaDocument = null;
        List<ContractManagerAssignmentDetail> details = ContractManagerAssignmentDocumentFixture.ACM_DOCUMENT_PERFORMANCE.getContractManagerAssignmentDetails();
        for (ContractManagerAssignmentDetail detail : details) {
            detail.setRequisitionIdentifier(requisitionDocument.getPurapDocumentIdentifier());
            detail.refreshNonUpdateableReferences();
        }
        cmaDocument = ContractManagerAssignmentDocumentFixture.ACM_DOCUMENT_PERFORMANCE.createContractManagerAssignmentDocument();
        for (ContractManagerAssignmentDetail detail : details) {
            detail.setContractManagerAssignmentDocument(cmaDocument);
        }
        cmaDocument.setContractManagerAssignmentDetailss(details);
        return cmaDocument;
    }
    
    private void routeDocument(Document document, String annotation, DocumentService documentService) throws WorkflowException {
        try {
            documentService.routeDocument(document, annotation, null);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }
}
