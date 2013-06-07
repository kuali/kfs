/*
 * Copyright 2007-2008 The Kuali Foundation
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

import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import junit.framework.Assert;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceLoadSummaryFixture;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceRejectDocumentFixture;
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
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Used to create and test populated Requisition Documents of various kinds.
 */
@ConfigureContext(session = appleton)
public class ElectronicInvoiceRejectDocumentTest extends KualiTestBase {
    public static final Class<ElectronicInvoiceRejectDocument> DOCUMENT_CLASS = ElectronicInvoiceRejectDocument.class;

    private ElectronicInvoiceRejectDocument eirDoc = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        eirDoc = null;
        super.tearDown();
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        ElectronicInvoiceLoadSummary eils = ElectronicInvoiceLoadSummaryFixture.EILS_BASIC.createElectronicInvoiceLoadSummary();
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        boService.save(eils);

        GlobalVariables.getUserSession().setBackdoorUser( "kfs" );
        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument(eils);
        eirDoc.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        assertFalse(DocumentStatus.ENROUTE.equals(eirDoc.getDocumentHeader().getWorkflowDocument().getStatus()));
        saveDocument(eirDoc, "saving copy source document", documentService);
        GlobalVariables.getUserSession().clearBackdoorUser();

        Document document = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
        assertTrue("Document should  be saved.", document.getDocumentHeader().getWorkflowDocument().isSaved());
        Document result = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
        assertMatch(eirDoc, result);
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = true)
    public final void testSaveDocumentWithNonMatchingPO() throws Exception {
        ElectronicInvoiceLoadSummary eils = ElectronicInvoiceLoadSummaryFixture.EILS_BASIC.createElectronicInvoiceLoadSummary();
        BusinessObjectService boService =  SpringContext.getBean(BusinessObjectService.class);
        boService.save(eils);

        GlobalVariables.getUserSession().setBackdoorUser( "parke" );
        Integer poId = routePO();

        GlobalVariables.getUserSession().setBackdoorUser( "kfs" );
        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument(eils);
        eirDoc.setPurchaseOrderIdentifier(poId);
        eirDoc.setInvoicePurchaseOrderNumber(poId.toString());
        eirDoc.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        assertFalse(DocumentStatus.ENROUTE.equals(eirDoc.getDocumentHeader().getWorkflowDocument().getStatus()));
        saveDocument(eirDoc, "saving copy source document", documentService);
        GlobalVariables.getUserSession().clearBackdoorUser();

        Document document = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
        assertTrue("Document should  be saved.", document.getDocumentHeader().getWorkflowDocument().isSaved());
        Document result = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
        assertMatch(eirDoc, result);
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = true)
    public final void testSaveDocumentWithMatchingPO() throws Exception {
        ElectronicInvoiceLoadSummary eils = ElectronicInvoiceLoadSummaryFixture.EILS_MATCHING.createElectronicInvoiceLoadSummary();
        BusinessObjectService boService =  SpringContext.getBean(BusinessObjectService.class);
        boService.save(eils);

        GlobalVariables.getUserSession().setBackdoorUser( "parke" );
        Integer poId = routeMatchingPO();

        GlobalVariables.getUserSession().setBackdoorUser( "kfs" );
        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_MATCHING.createElectronicInvoiceRejectDocument(eils);
        eirDoc.setPurchaseOrderIdentifier(poId);
        eirDoc.setInvoicePurchaseOrderNumber(poId.toString());
        eirDoc.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        assertFalse(DocumentStatus.ENROUTE.equals(eirDoc.getDocumentHeader().getWorkflowDocument().getStatus()));
        saveDocument(eirDoc, "saving copy source document", documentService);
        GlobalVariables.getUserSession().clearBackdoorUser();

        Document document = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
        assertTrue("Document should  be saved.", document.getDocumentHeader().getWorkflowDocument().isSaved());
        Document result = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
        assertMatch(eirDoc, result);

    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testRouteDocument() throws Exception {
//        ElectronicInvoiceLoadSummary eils = ElectronicInvoiceLoadSummaryFixture.EILS_BASIC.createElectronicInvoiceLoadSummary();
//        BusinessObjectService boService =  SpringContext.getBean(BusinessObjectService.class);
//        boService.save(eils);
//
//        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument(eils);
//        eirDoc.prepareForSave();
//
//        DocumentService documentService = SpringContext.getBean(DocumentService.class);
//        assertFalse(DocumentStatus.ENROUTE.equals(eirDoc.getDocumentHeader().getWorkflowDocument().getStatus()));
//        routeDocument(eirDoc, "saving copy source document", documentService);
//        WorkflowTestUtils.waitForStatusChange(eirDoc.getDocumentHeader().getWorkflowDocument(), KewApiConstants.ROUTE_HEADER_FINAL_CD);
//        Document result = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
//        assertTrue("Document should  be final.", result.getDocumentHeader().getWorkflowDocument().isFinal());
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions = false)
    public final void testRouteDocumentToFinal() throws Exception {
//        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument();

//        final String docId = requisitionDocument.getDocumentNumber();
//        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
//
//        // the document should now be routed to vputman as Fiscal Officer
//        changeCurrentUser(rorenfro);
//        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
//        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(requisitionDocument, ACCOUNT_REVIEW));
//        assertTrue("Document should be enroute.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
//        assertTrue("rorenfro should have an approve request.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
//        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as rorenfro", null);
//
//        WorkflowTestUtils.waitForStatusChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);
//
//        changeCurrentUser(khuntley);
//        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
//        assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isFinal());
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

    /**
     * Helper method to route the document.
     *
     * @param document                 The assign contract manager document to be routed.
     * @param annotation               The annotation String.
     * @param documentService          The service to use to route the document.
     * @throws WorkflowException
     */
    private void saveDocument(Document document, String annotation, DocumentService documentService) throws WorkflowException {
        try {
            documentService.saveDocument(document);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }
    }

    public static <T extends Document> void assertMatch(T document1, T document2) {
        Assert.assertEquals(document1.getDocumentNumber(), document2.getDocumentNumber());
        Assert.assertEquals(document1.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), document2.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());


        ElectronicInvoiceRejectDocument d1 = (ElectronicInvoiceRejectDocument) document1;
        ElectronicInvoiceRejectDocument d2 = (ElectronicInvoiceRejectDocument) document2;
        Assert.assertEquals(d1.getInvoiceFileName(), d2.getInvoiceFileName());
        Assert.assertEquals(d1.getVendorDunsNumber(), d2.getVendorDunsNumber());
        Assert.assertEquals(d1.getInvoiceRejectItems().size(), d2.getInvoiceRejectItems().size());
        Assert.assertEquals(d1.getInvoiceRejectReasons().size(), d2.getInvoiceRejectReasons().size());

//        for (int i = 0; i < d1.getInvoiceRejectItems().size(); i++) {
//            d1.getInvoiceRejectItem(i).isLike(d2.getInvoiceRejectItem(i));
//        }
//        for (int i = 0; i < d1.getInvoiceRejectReasons().size(); i++) {
//            d1.getInvoiceRejectReason(i).isLike(d2.getInvoiceRejectReason(i));
//        }
    }

    private Integer routePO() {
        PurchaseOrderDocumentTest purchaseOrderDocumentTest = new PurchaseOrderDocumentTest();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PurchaseOrderDocument poDocument;
        try {
            RequisitionDocument requisition =RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
            requisition.prepareForSave();
            AccountingDocumentTestUtils.routeDocument(requisition, "saving copy source document", null, documentService);
            poDocument = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
            poDocument.setRequisitionIdentifier(requisition.getPurapDocumentIdentifier());
            poDocument.prepareForSave();
            AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
            WorkflowTestUtils.waitForDocumentApproval(poDocument.getDocumentNumber());
            return poDocument.getPurapDocumentIdentifier();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private Integer routeMatchingPO() {
        PurchaseOrderDocumentTest purchaseOrderDocumentTest = new PurchaseOrderDocumentTest();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PurchaseOrderDocument poDocument;
        try {
            RequisitionDocument requisition =RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
            requisition.prepareForSave();
            AccountingDocumentTestUtils.routeDocument(requisition, "saving copy source document", null, documentService);
            poDocument = PurchaseOrderDocumentFixture.EINVOICE_PO.createPurchaseOrderDocument();
            poDocument.setRequisitionIdentifier(requisition.getPurapDocumentIdentifier());
            poDocument.prepareForSave();
            AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
            WorkflowTestUtils.waitForDocumentApproval(poDocument.getDocumentNumber());
            return poDocument.getPurapDocumentIdentifier();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}

