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
import static org.kuali.module.purap.fixtures.PurchaseOrderItemAccountsFixture.WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RJWEISS;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;
import static org.kuali.test.fixtures.UserNameFixture.PARKE;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.bo.PurchasingItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.WorkflowXmlPurchaseOrderDocument.DummyContractManager;
import org.kuali.module.purap.document.WorkflowXmlPurchaseOrderDocument.DummyStatus;
import org.kuali.module.purap.document.WorkflowXmlPurchaseOrderDocument.DummyVendorContract;
import org.kuali.module.purap.fixtures.PurchaseOrderDocumentFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderItemAccountsFixture;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * Used to create and test populated Purchase Order Documents of various kinds. 
 */
@ConfigureContext(session = KHUNTLEY)
public class PurchaseOrderDocumentTest extends KualiTestBase {
    public static final Class<PurchaseOrderDocument> DOCUMENT_CLASS = PurchaseOrderDocument.class;

    private List<PurchaseOrderItemAccountsFixture> getItemParametersFromFixtures() {
        List<PurchaseOrderItemAccountsFixture> list = new ArrayList<PurchaseOrderItemAccountsFixture>();
        list.add(WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT);
        return list;
    }

    private int getExpectedPrePeCount() {
        return 0;
    }

    public final void testAddItem() throws Exception {
        List<PurchasingItem> items = new ArrayList<PurchasingItem>();
        for (PurchaseOrderItem item : generateItems()) {
            items.add(item);
        }
        int expectedItemTotal = items.size();
        PurchasingDocumentTestUtils.testAddItem((PurchasingDocument)DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildSimpleDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(buildSimpleDocument(), getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public final void testRouteDocument() throws Exception {
        PurchaseOrderDocument poDocument = buildSimpleDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        poDocument.prepareForSave();       
        assertFalse("R".equals(poDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), "F");        
        //WorkflowTestUtils.waitForNodeChange(poDocument.getDocumentHeader().getWorkflowDocument(), "Open");
        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {    
        PurchaseOrderDocument poDocument = buildSimpleDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        poDocument.prepareForSave();       
        AccountingDocumentTestUtils.saveDocument(poDocument, documentService);
        PurchaseOrderDocument result = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocument.getDocumentNumber());
        assertMatch(poDocument, result);
    }

    // test util methods

    public static void assertMatch(PurchaseOrderDocument doc1, PurchaseOrderDocument doc2) {
        // match header
        Assert.assertEquals(doc1.getDocumentNumber(), doc2.getDocumentNumber());
        Assert.assertEquals(doc1.getDocumentHeader().getWorkflowDocument().getDocumentType(), doc2.getDocumentHeader().getWorkflowDocument().getDocumentType());

        // match posting year
        if (StringUtils.isNotBlank(doc1.getPostingPeriodCode()) && StringUtils.isNotBlank(doc2.getPostingPeriodCode())) {
            Assert.assertEquals(doc1.getPostingPeriodCode(), doc2.getPostingPeriodCode());
        }
        Assert.assertEquals(doc1.getPostingYear(), doc2.getPostingYear());

        // match important fields in PO
        
        Assert.assertEquals(doc1.getVendorHeaderGeneratedIdentifier(), doc2.getVendorHeaderGeneratedIdentifier());
        Assert.assertEquals(doc1.getVendorDetailAssignedIdentifier(), doc2.getVendorDetailAssignedIdentifier());
        Assert.assertEquals(doc1.getVendorName(), doc2.getVendorName());
        Assert.assertEquals(doc1.getVendorNumber(), doc2.getVendorNumber());
        Assert.assertEquals(doc1.getStatusCode(), doc2.getStatusCode());

        Assert.assertEquals(doc1.getChartOfAccountsCode(), doc2.getChartOfAccountsCode());
        Assert.assertEquals(doc1.getOrganizationCode(), doc2.getOrganizationCode());
        Assert.assertEquals(doc1.getDeliveryCampusCode(), doc2.getDeliveryCampusCode());
        Assert.assertEquals(doc1.getDeliveryRequiredDate(), doc2.getDeliveryRequiredDate());
        Assert.assertEquals(doc1.getRequestorPersonName(), doc2.getRequestorPersonName());
        Assert.assertEquals(doc1.getContractManagerCode(), doc2.getContractManagerCode());
        Assert.assertEquals(doc1.getVendorContractName(), doc2.getVendorContractName());
        Assert.assertEquals(doc1.getPurchaseOrderAutomaticIndicator(), doc2.getPurchaseOrderAutomaticIndicator());
        Assert.assertEquals(doc1.getPurchaseOrderTransmissionMethodCode(), doc2.getPurchaseOrderTransmissionMethodCode());

        Assert.assertEquals(doc1.getRequisitionIdentifier(), doc2.getRequisitionIdentifier());
        Assert.assertEquals(doc1.getPurchaseOrderPreviousIdentifier(), doc2.getPurchaseOrderPreviousIdentifier());
        Assert.assertEquals(doc1.isPurchaseOrderCurrentIndicator(), doc2.isPurchaseOrderCurrentIndicator());
        Assert.assertEquals(doc1.getPurchaseOrderCreateDate(), doc2.getPurchaseOrderCreateDate());
        Assert.assertEquals(doc1.getPurchaseOrderLastTransmitDate(), doc2.getPurchaseOrderLastTransmitDate());        
    }
    
    private List<PurchaseOrderItem> generateItems() throws Exception {
        List<PurchaseOrderItem> items = new ArrayList<PurchaseOrderItem>();
        // set items to document
        for (PurchaseOrderItemAccountsFixture itemFixture : getItemParametersFromFixtures()) {
            items.add(itemFixture.populateItem());
        }

        return items;
    }

    protected PurchaseOrderDocument buildSimpleDocument() throws Exception {
        return PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
    }

    private UserNameFixture getInitialUserName() {
        return RJWEISS;
    }

    protected UserNameFixture getTestUserName() {
        return RORENFRO;
    }
}
