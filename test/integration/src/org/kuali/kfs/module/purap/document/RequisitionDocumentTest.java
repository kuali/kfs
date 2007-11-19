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

import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RJWEISS;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingItem;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.fixtures.RequisitionItemFixture;
import org.kuali.module.purap.service.PurapService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * Used to create and test populated Requisition Documents of various kinds.
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


    private List<RequisitionItemFixture> getItemParametersFromFixtures() {
        List<RequisitionItemFixture> list = new ArrayList<RequisitionItemFixture>();
        list.add(RequisitionItemFixture.REQ_ITEM_NO_APO);
        return list;
    }

    private int getExpectedPrePeCount() {
        return 0;
    }

    public final void testAddItem() throws Exception {
        List<PurchasingItem> items = new ArrayList<PurchasingItem>();
        items.add(RequisitionItemFixture.REQ_ITEM_NO_APO.createRequisitionItem());

        int expectedItemTotal = items.size();
        PurchasingDocumentTestUtils.testAddItem((PurchasingDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
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

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(requisitionDocument, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testRouteDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testSaveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public final void testConvertIntoCopy() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoCopy(requisitionDocument, SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
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

    @ConfigureContext(session = RORENFRO, shouldCommitTransactions = true)
    public final void testCreateAPOAlternateRequisition() throws Exception {
        RequisitionDocument altAPORequisition = RequisitionDocumentFixture.REQ_ALTERNATE_APO.createRequisitionDocument();
        AccountingDocumentTestUtils.testSaveDocument(altAPORequisition, SpringContext.getBean(DocumentService.class));
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
