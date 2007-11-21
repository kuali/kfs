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
import static org.kuali.test.fixtures.UserNameFixture.APPLETON;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RJWEISS;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.bo.AccountsPayableItem;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.fixtures.CreditMemoDocumentFixture;
import org.kuali.module.purap.fixtures.CreditMemoItemFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.CreditMemoCreateService;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * This class is used to create and test populated CreditMemo Documents of various kinds.
 */
@ConfigureContext(session = APPLETON)
public class CreditMemoDocumentTest extends KualiTestBase {
    public static final Class<CreditMemoDocument> DOCUMENT_CLASS = CreditMemoDocument.class;
    private static final String ACCOUNT_REVIEW = "Account Review";
    
    private RequisitionDocument requisitionDocument = null;
    private CreditMemoDocument CreditMemoDocument = null;
   
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        CreditMemoDocument = null;
        super.tearDown();      
    }
    
 /*   
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
        AccountsPayableDocumentTestUtils.testAddItem((AccountsPayableDocument)DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        CreditMemoDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(CreditMemoDocument, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {
        CreditMemoDocument = buildSimpleDocument();
        CreditMemoDocument.setAccountsPayableProcessorIdentifier("khuntley");
        //CreditMemoDocument.setPurchaseOrderIdentifier(CreateAPORequisition());
        AccountingDocumentTestUtils.testSaveDocument(CreditMemoDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testRouteDocumentToFinal() throws Exception {
        CreditMemoDocument = buildSimpleDocument();
        final String docId = CreditMemoDocument.getDocumentNumber();
        CreditMemoDocument.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(CreditMemoDocument, SpringContext.getBean(DocumentService.class));
        SpringContext.getBean(AccountsPayableService.class).updateItemList(CreditMemoDocument);
        SpringContext.getBean(CreditMemoCreateService.class).populateDocumentAfterInit(CreditMemoDocument);
        SpringContext.getBean(CreditMemoService.class).calculateCreditMemo(CreditMemoDocument);
        assertFalse("R".equals(CreditMemoDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        AccountingDocumentTestUtils.routeDocument(CreditMemoDocument, "routing document", null, SpringContext.getBean(DocumentService.class));
        
        WorkflowTestUtils.waitForStatusChange(CreditMemoDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        CreditMemoDocument = (CreditMemoDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", CreditMemoDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
    }
    


    private CreditMemoDocument buildSimpleDocument() throws Exception {
        return CreditMemoDocumentFixture.CM_ONLY_REQUIRED_FIELDS.createCreditMemoDocument();
    }

    private UserNameFixture getInitialUserName() {
        return RJWEISS;
    }

    protected UserNameFixture getTestUserName() {
        return RORENFRO;
    }
    
    
    private Integer CreateAPORequisition() throws Exception {
        requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        final String docId = requisitionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
       
        changeCurrentUser(RORENFRO);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as RORENFRO", null);

        WorkflowTestUtils.waitForStatusChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(KHUNTLEY);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        //assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
        // get related POs, if count = 1 then all is well
        Integer linkIdentifier = requisitionDocument.getAccountsPayablePurchasingDocumentLinkIdentifier();
        List<PurchaseOrderView> relatedPOs = SpringContext.getBean(PurapService.class).getRelatedViews(PurchaseOrderView.class, linkIdentifier);
        Integer POId = relatedPOs.get(0).getPurapDocumentIdentifier();
        return POId;
        //assertNotNull(relatedPOs);
        //assertTrue(relatedPOs.size() > 0);
        
    }
    // create document fixture
 */   
}
