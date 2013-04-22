/*
 * Copyright 2007 The Kuali Foundation
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

import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.UserNameFixture.jgerhart;
import static org.kuali.kfs.sys.fixture.UserNameFixture.jkitchen;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rjweiss;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;
import static org.kuali.kfs.sys.fixture.UserNameFixture.sterner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCommodityCodeFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Used to create and test populated Requisition Documents of various kinds.
 */
@ConfigureContext(session = khuntley)
public class RequisitionDocumentTest extends KualiTestBase {
    public static final Class<RequisitionDocument> DOCUMENT_CLASS = RequisitionDocument.class;
    private static final String ACCOUNT_REVIEW = "Account";
    private static final String ORGANIZATION = "Organization";
    private static final String COMMODITY_CODE_REVIEW = "Commodity";

    private RequisitionDocument requisitionDocument = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
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
        PurchasingDocumentTestUtils.testAddItem(DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
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

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(requisitionDocument, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testRouteDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testSaveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testSaveDocumentWithItemDeletion() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testSaveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        List<RequisitionItem> items = requisitionDocument.getItems();
        RequisitionItem item = items.get(0);
        List<PurApAccountingLine> accounts = item.getSourceAccountingLines();
        RequisitionAccount account = (RequisitionAccount)item.getSourceAccountingLine(0);

        requisitionDocument.deleteItem(0);
        AccountingDocumentTestUtils.testSaveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteSavedDocumentWithAccountDeletion() throws Exception {
        requisitionDocument = buildComplexDocument();
        List<RequisitionItem> items = requisitionDocument.getItems();
        RequisitionItem item = items.get(0);
        List<PurApAccountingLine> accounts = item.getSourceAccountingLines();
        AccountingDocumentTestUtils.testSaveDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(requisitionDocument.getDocumentNumber());
        RequisitionAccount account = (RequisitionAccount)item.getSourceAccountingLine(0);
        account.setAccountLinePercent(new BigDecimal(100.00));
        accounts.remove(0);
        account = (RequisitionAccount)item.getSourceAccountingLine(0);
        account.setAccountLinePercent(new BigDecimal(100.00));
        // for the sequential method, we need to set the amount in addition to the percent
        account.setAmount(item.calculateExtendedPrice().multiply(new KualiDecimal(account.getAccountLinePercent())).divide(new KualiDecimal(100)));

        AccountingDocumentTestUtils.testRouteDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testConvertIntoCopy() throws Exception {
        requisitionDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoCopy(requisitionDocument, SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteDocumentToFinal() throws Exception {
        requisitionDocument = RequisitionDocumentFixture.REQ_NO_APO_VALID.createRequisitionDocument();
        final String docId = requisitionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to the Fiscal Officer
        changeCurrentUser(sterner);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(requisitionDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("sterner should have an approve request.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as sterner", null);

        changeCurrentUser(jgerhart);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        SpringContext.getBean(DocumentService.class).acknowledgeDocument(requisitionDocument, "Acknowledging as jgerhart", null);

        WorkflowTestUtils.waitForDocumentApproval(requisitionDocument.getDocumentNumber());


        changeCurrentUser(khuntley);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isFinal());
    }

    @ConfigureContext(session = rorenfro, shouldCommitTransactions = true)
    public final void testCreateAPOAlternateRequisition() throws Exception {
        RequisitionDocument altAPORequisition = RequisitionDocumentFixture.REQ_ALTERNATE_APO.createRequisitionDocument();
        AccountingDocumentTestUtils.testSaveDocument(altAPORequisition, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteDocumentToFinalWithBasicActiveCommodityCode() throws Exception {
        requisitionDocument = RequisitionDocumentWithCommodityCodeFixture.REQ_VALID_ACTIVE_COMMODITY_CODE.createRequisitionDocument();
        final String docId = requisitionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to rorenfro as Fiscal Officer
        changeCurrentUser(sterner);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(requisitionDocument, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("sterner should have an approve request.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as sterner", null);

        // the document should now be routed to Awaiting Commodity Code approval which is to the PA_PUR_COMM_CODE workgroup,
        // we'll use jkitchen as the user.
        changeCurrentUser(jkitchen);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        WorkflowDocument workflowDocument = requisitionDocument.getDocumentHeader().getWorkflowDocument();
        //assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(requisitionDocument, COMMODITY_CODE_REVIEW));
        //assertTrue("Document should be enroute.", workflowDocument.isEnroute());
        //assertTrue("jkitchen should have an approve request.", workflowDocument.isApprovalRequested());
        //SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as jkitchen", null);

        changeCurrentUser(jgerhart);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        SpringContext.getBean(DocumentService.class).acknowledgeDocument(requisitionDocument, "Acknowledging as jgerhart", null);

        WorkflowTestUtils.waitForDocumentApproval(requisitionDocument.getDocumentNumber());

        changeCurrentUser(khuntley);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isFinal());
    }

    private RequisitionDocument buildSimpleDocument() throws Exception {
        return RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
    }

    private RequisitionDocument buildComplexDocument() throws Exception {
        RequisitionDocument complexRequisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS_MULTIPLE_ACCOUNTS.createRequisitionDocument();

        return complexRequisitionDocument;
    }

    public void testRouteBrokenDocument_ItemQuantityBased_NoQuantity() {
        requisitionDocument = RequisitionDocumentFixture.REQ_INVALID_ITEM_QUANTITY_BASED_NO_QUANTITY.createRequisitionDocument();
        SpringContext.getBean(KualiRuleService.class).applyRules(new RouteDocumentEvent(requisitionDocument));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
    }

    private UserNameFixture getInitialUserName() {
        return rjweiss;
    }

    protected UserNameFixture getTestUserName() {
        return rorenfro;
    }

    // create document fixture
}

