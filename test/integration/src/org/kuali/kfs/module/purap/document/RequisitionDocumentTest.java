/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.module.purap.fixtures.RequisitionItemAccountsFixture.WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RJWEISS;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;
import static org.kuali.test.fixtures.UserNameFixture.VPUTMAN;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingItem;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.fixtures.RequisitionItemAccountsFixture;
import org.kuali.module.purap.rules.RequisitionDocumentRule;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * This class is used to create and test populated Requisition Documents of various kinds.
 */
@ConfigureContext(session = KHUNTLEY)
public class RequisitionDocumentTest extends KualiTestBase {
    public static final Class<RequisitionDocument> DOCUMENT_CLASS = RequisitionDocument.class;
    private static final String ACCOUNT_REVIEW = "Account Review";
    
    private RequisitionDocument requisitionDocument = null;

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        requisitionDocument = null;
        super.tearDown();      
    }
    
    
    private List<RequisitionItemAccountsFixture> getItemParametersFromFixtures() {
        List<RequisitionItemAccountsFixture> list = new ArrayList<RequisitionItemAccountsFixture>();
        list.add(WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT);
        return list;
    }

    private int getExpectedPrePeCount() {
        return 0;
    }
    
    public final void testAddItem() throws Exception {
        List<PurchasingItem> items = new ArrayList<PurchasingItem>();
        for (RequisitionItem item : generateItems()) {
            items.add(item);
        }
        int expectedItemTotal = items.size();
        PurchasingDocumentTestUtils.testAddItem((PurchasingDocument)DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildSimpleDocument(), SpringContext.getBean(DataDictionaryService.class));

    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildSimpleDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(requisitionDocument, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testRouteDocument() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testRouteDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testSaveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoCopy() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoCopy(requisitionDocument, SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
    }
    
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testRouteDocumentToFinal() throws Exception {
        requisitionDocument = RequisitionDocumentFixture.REQ_NO_APO_VALID.createRequisitionDocument();
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
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testAPOValid() throws Exception {
        requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
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

        WorkflowTestUtils.waitForStatusChange(600,requisitionDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(KHUNTLEY);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());

        // get related POs, if count = 1 then all is well
        Integer linkIdentifier = requisitionDocument.getAccountsPayablePurchasingDocumentLinkIdentifier();
        List<PurchaseOrderView> relatedPOs = SpringContext.getBean(PurapService.class).getRelatedViews(PurchaseOrderView.class, linkIdentifier);
        assertNotNull(relatedPOs);
        assertTrue(relatedPOs.size() > 0);
    }
    
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidLimit() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_ALTERNATE_VENDOR_NAMES.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidZeroTotal() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidRestrictedItem() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidNoVendor() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidRestrictedVendor() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidRecurringPaymentType() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidNotToExceedEntered() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
//
//    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
//    public final void testAPOInvalidAltVendorName() throws Exception {
//        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        AccountingDocumentTestUtils.saveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//    }
    
    @ConfigureContext(session = RORENFRO, shouldCommitTransactions=true)
    public final void testCreateAPOAlternateRequisition() throws Exception {
        RequisitionDocument altAPORequisition = RequisitionDocumentFixture.REQ_ALTERNATE_APO.createRequisitionDocument();
        AccountingDocumentTestUtils.testSaveDocument(altAPORequisition, SpringContext.getBean(DocumentService.class));
    }

    // test util methods
    private List<RequisitionItem> generateItems() throws Exception {
        List<RequisitionItem> items = new ArrayList<RequisitionItem>();
        // set items to document
        for (RequisitionItemAccountsFixture itemFixture : getItemParametersFromFixtures()) {
            items.add(itemFixture.populateItem());
        }

        return items;
    }

    private RequisitionDocument buildSimpleDocument() throws Exception {
        return RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
    }

    private UserNameFixture getInitialUserName() {
        return RJWEISS;
    }

    protected UserNameFixture getTestUserName() {
        return RORENFRO;
    }

    // create document fixture
}
