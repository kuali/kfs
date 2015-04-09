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


import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rjweiss;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.fixture.CreditMemoDocumentFixture;
import org.kuali.kfs.module.purap.fixture.CreditMemoItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class is used to create and test populated CreditMemo Documents of various kinds.
 */
@ConfigureContext(session = appleton)
public class CreditMemoDocumentTest extends KualiTestBase {
    public static final Class<VendorCreditMemoDocument> DOCUMENT_CLASS = VendorCreditMemoDocument.class;
    private static final String ACCOUNT_REVIEW = "Account Review";

    private RequisitionDocument requisitionDocument = null;
    private VendorCreditMemoDocument creditMemoDocument = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        creditMemoDocument = null;
        super.tearDown();
    }

    private List<CreditMemoItemFixture> getItemParametersFromFixtures() {
        List<CreditMemoItemFixture> list = new ArrayList<CreditMemoItemFixture>();
        list.add(CreditMemoItemFixture.CM_ITEM_NO_APO);
        return list;
    }

    private int getExpectedPrePeCount() {
        return 0;
    }

    public final void testAddItem() throws Exception {
        List<AccountsPayableItem> items = new ArrayList<AccountsPayableItem>();
        items.add(CreditMemoItemFixture.CM_ITEM_NO_APO.createCreditMemoItem());

        int expectedItemTotal = items.size();
        AccountsPayableDocumentTestUtils.testAddItem(DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        creditMemoDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(creditMemoDocument, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {
        creditMemoDocument = buildSimpleDocument();
        creditMemoDocument.setAccountsPayableProcessorIdentifier("khuntley");
        //creditMemoDocument.setPurchaseOrderIdentifier(createAPORequisition());
        AccountingDocumentTestUtils.testSaveDocument(creditMemoDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public final VendorCreditMemoDocument routeDocument(PaymentRequestDocument preqDocument) throws Exception {
        creditMemoDocument = buildSimpleDocument();
        creditMemoDocument.setPaymentRequestDocument(preqDocument);
        creditMemoDocument.setPurchaseOrderIdentifier(preqDocument.getPurchaseOrderIdentifier());
        CreditMemoItem cmItem = (CreditMemoItem) creditMemoDocument.getItem(0);
        cmItem.setPreqInvoicedTotalQuantity(new KualiDecimal(1));
        cmItem.setItemQuantity(new KualiDecimal(1));
        cmItem.setPreqTotalAmount(new KualiDecimal(1));
        SpringContext.getBean(CreditMemoService.class).calculateCreditMemo(creditMemoDocument);
        creditMemoDocument.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        assertFalse(DocumentStatus.ENROUTE.equals(creditMemoDocument.getDocumentHeader().getWorkflowDocument().getStatus()));
        AccountingDocumentTestUtils.routeDocument(creditMemoDocument, "saving copy source docu ament", null, documentService);
        WorkflowTestUtils.waitForDocumentApproval(creditMemoDocument.getDocumentNumber());
        return creditMemoDocument;
    }

    /*
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public final void testRouteDocumentToFinal() throws Exception {
        // To pass validation, the Credit Memo must be associated with another PO or PREQ.
        PurchaseOrderDocumentTest poDocTest = new PurchaseOrderDocumentTest();
        PurchaseOrderDocument po = poDocTest.buildSimpleDocument();
        po.refreshNonUpdateableReferences();
        AccountingDocumentTestUtils.saveDocument(po,SpringContext.getBean(DocumentService.class));
        String poId = po.getDocumentNumber();
        po = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poId);
        Integer purchaseOrderIdentifier = po.getPurapDocumentIdentifier();

        // Create and save the Credit Memo.
        creditMemoDocument = buildSimpleDocument();
        creditMemoDocument.setPurchaseOrderIdentifier(purchaseOrderIdentifier);
        CreditMemoItem creditMemoItem = (CreditMemoItem)creditMemoDocument.getItemByLineNumber(1);
        creditMemoItem.setPoExtendedPrice(new KualiDecimal(1.00));
        creditMemoItem.setPoInvoicedTotalQuantity(creditMemoItem.getItemQuantity());
        creditMemoDocument.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(creditMemoDocument, SpringContext.getBean(DocumentService.class));

        // Process and Calculate.
        //SpringContext.getBean(AccountsPayableService.class).updateItemList(creditMemoDocument);
        SpringContext.getBean(CreditMemoCreateService.class).populateDocumentAfterInit(creditMemoDocument);
        SpringContext.getBean(CreditMemoService.class).calculateCreditMemo(creditMemoDocument);
        assertFalse(DocumentStatus.ENROUTE.equals(creditMemoDocument.getDocumentHeader().getWorkflowDocument().getStatus()));

        // Route and test.
        AccountingDocumentTestUtils.routeDocument(creditMemoDocument, "routing document", null, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForStatusChange(creditMemoDocument.getDocumentHeader().getWorkflowDocument(), KewApiConstants.ROUTE_HEADER_FINAL_CD);
        final String docId = creditMemoDocument.getDocumentNumber();
        creditMemoDocument = (CreditMemoDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", creditMemoDocument.getDocumentHeader().getWorkflowDocument().isFinal());
    }
    */

    public VendorCreditMemoDocument buildSimpleDocument() throws Exception {
        return CreditMemoDocumentFixture.CM_ONLY_REQUIRED_FIELDS.createCreditMemoDocument();
    }

    private UserNameFixture getInitialUserName() {
        return rjweiss;
    }

    protected UserNameFixture getTestUserName() {
        return rorenfro;
    }

    /*
    private Integer createAPORequisition() throws Exception {
        requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        final String docId = requisitionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        changeCurrentUser(rorenfro);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as rorenfro", null);

        WorkflowTestUtils.waitForStatusChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), KewApiConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(khuntley);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        //assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isFinal());
        // get related POs, if count = 1 then all is well
        Integer linkIdentifier = requisitionDocument.getAccountsPayablePurchasingDocumentLinkIdentifier();
        List<PurchaseOrderView> relatedPOs = SpringContext.getBean(PurapService.class).getRelatedViews(PurchaseOrderView.class, linkIdentifier);
        Integer POId = relatedPOs.get(0).getPurapDocumentIdentifier();

        assertNotNull(relatedPOs);
        assertTrue(relatedPOs.size() > 0);
        return POId;
    }
    */
}

