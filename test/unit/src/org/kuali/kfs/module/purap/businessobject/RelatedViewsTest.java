/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocumentTest;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestServiceTest;
import org.kuali.kfs.module.purap.fixture.BulkReceivingDocumentFixture;
import org.kuali.kfs.module.purap.fixture.CreditMemoDocumentFixture;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceLoadSummaryFixture;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceRejectDocumentFixture;
import org.kuali.kfs.module.purap.fixture.LineItemReceivingDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = UserNameFixture.appleton)
public class RelatedViewsTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceTest.class);

    private DocumentService documentService;
    private PaymentRequestDocumentTest preqDocTest;
    private BusinessObjectService businessObjectService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        if (null == documentService) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        if (null == businessObjectService) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        if (null == preqDocTest) {
            preqDocTest = new PaymentRequestDocumentTest();
        }
    }

    private PurchaseOrderDocument createBasicPurchaseOrderDocument() throws Exception {
        return preqDocTest.createPurchaseOrderDocument(PurchaseOrderDocumentFixture.PO_APPROVAL_REQUIRED,false);
    }

    private PaymentRequestDocument createBasicPaymentRequestDocument() throws Exception {
        PaymentRequestDocument preq = preqDocTest.createPaymentRequestDocument(
                PaymentRequestDocumentFixture.PREQ_APPROVAL_REQUIRED,
                createBasicPurchaseOrderDocument(),
                true, new KualiDecimal[] {new KualiDecimal(100)});

        return preq;
    }

    private RequisitionDocument createBasicRequisitionDocument() throws Exception {
        return RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
    }

    public VendorCreditMemoDocument createBasicVendorCreditMemoDocument() throws Exception {
        return CreditMemoDocumentFixture.CM_ONLY_REQUIRED_FIELDS.createCreditMemoDocument();
    }

    private LineItemReceivingDocument createBasicLineItemReceivingDocument() throws Exception {
        LineItemReceivingDocument lird = LineItemReceivingDocumentFixture.EMPTY_LINE_ITEM_RECEIVING.createLineItemReceivingDocument();
        lird.populateReceivingLineFromPurchaseOrder(createBasicPurchaseOrderDocument());
        return lird;
    }

    private BulkReceivingDocument createBasicBulkReceivingDocument() throws Exception {
        return BulkReceivingDocumentFixture.SIMPLE_DOCUMENT.createBulkReceivingDocument();
    }

    private ElectronicInvoiceRejectDocument createBasicElectronicInvoiceRejectDocument() throws Exception {
        ElectronicInvoiceLoadSummary eils = ElectronicInvoiceLoadSummaryFixture.EILS_BASIC.createElectronicInvoiceLoadSummary();
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        boService.save(eils);

        changeCurrentUser(UserNameFixture.kfs);

        return ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument(eils);
    }
    public void testPaymentRequestView() throws Exception {
        PaymentRequestDocument preq = createBasicPaymentRequestDocument();
        preq.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_AWAITING_FISCAL_REVIEW);
        documentService.saveDocument(preq);

        Map <String, String> keys = new HashMap <String, String>();
        keys.put("documentNumber", preq.getDocumentNumber());
        keys.put("purapDocumentIdentifier", preq.getPurapDocumentIdentifier().toString());

        PaymentRequestView prv = businessObjectService.findByPrimaryKey(PaymentRequestView.class, keys);

        assertEquals(preq.getApplicationDocumentStatus(), prv.getApplicationDocumentStatus());
    }

    public void testPurchaseOrderView() throws Exception {
        PurchaseOrderDocument po = createBasicPurchaseOrderDocument();
        po.setApplicationDocumentStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_AMENDMENT);
        documentService.saveDocument(po);

        Map <String, String> keys = new HashMap <String, String>();
        keys.put("documentNumber", po.getDocumentNumber());
        keys.put("purapDocumentIdentifier", po.getPurapDocumentIdentifier().toString());

        PurchaseOrderView pov = businessObjectService.findByPrimaryKey(PurchaseOrderView.class, keys);

        assertEquals(po.getApplicationDocumentStatus(), pov.getApplicationDocumentStatus());
    }

    public void testVendorCreditMemoView() throws Exception {
        VendorCreditMemoDocument vcm = createBasicVendorCreditMemoDocument();
        vcm.setApplicationDocumentStatus(PurapConstants.CreditMemoStatuses.APPDOC_AWAITING_ACCOUNTS_PAYABLE_REVIEW);
        documentService.saveDocument(vcm);

        Map <String, String> keys = new HashMap <String, String>();
        keys.put("documentNumber", vcm.getDocumentNumber());
        keys.put("purapDocumentIdentifier", vcm.getPurapDocumentIdentifier().toString());

        CreditMemoView cmv = businessObjectService.findByPrimaryKey(CreditMemoView.class, keys);

        assertEquals(vcm.getApplicationDocumentStatus(), cmv.getApplicationDocumentStatus());
    }

    public void testBulkReceivingView() throws Exception {
        BulkReceivingDocument br = createBasicBulkReceivingDocument();
        br.setApplicationDocumentStatus(PurapConstants.CreditMemoStatuses.APPDOC_AWAITING_ACCOUNTS_PAYABLE_REVIEW);
        documentService.saveDocument(br);

        Map <String, String> keys = new HashMap <String, String>();
        keys.put("documentNumber", br.getDocumentNumber());

        BulkReceivingView brv = businessObjectService.findByPrimaryKey(BulkReceivingView.class, keys);

        assertEquals(br.getApplicationDocumentStatus(), brv.getApplicationDocumentStatus());
    }

    public void testElectronicInvoiceRejectView() throws Exception {
        ElectronicInvoiceRejectDocument eirt = createBasicElectronicInvoiceRejectDocument();
        eirt.setApplicationDocumentStatus(PurapConstants.CreditMemoStatuses.APPDOC_AWAITING_ACCOUNTS_PAYABLE_REVIEW);
        documentService.saveDocument(eirt);

        Map <String, String> keys = new HashMap <String, String>();
        keys.put("documentNumber", eirt.getDocumentNumber());

        ElectronicInvoiceRejectView erv = businessObjectService.findByPrimaryKey(ElectronicInvoiceRejectView.class, keys);

        assertEquals(eirt.getApplicationDocumentStatus(), erv.getApplicationDocumentStatus());
    }

    public void testLineItemReceivingView() throws Exception {
        LineItemReceivingDocument lird = createBasicLineItemReceivingDocument();
        lird.setApplicationDocumentStatus(PurapConstants.LineItemReceivingStatuses.APPDOC_IN_PROCESS);
        documentService.saveDocument(lird);

        Map <String, String> keys = new HashMap <String, String>();
        keys.put("documentNumber", lird.getDocumentNumber());

        LineItemReceivingView lirv = businessObjectService.findByPrimaryKey(LineItemReceivingView.class, keys);

        assertEquals(lird.getApplicationDocumentStatus(), lirv.getApplicationDocumentStatus());
    }

    public void testRequisitionView() throws Exception {
        RequisitionDocument reqs = createBasicRequisitionDocument();
        reqs.setApplicationDocumentStatus(PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CHART_REVIEW);
        documentService.saveDocument(reqs);

        Map <String, String> keys = new HashMap <String, String>();
        keys.put("documentNumber", reqs.getDocumentNumber());

        RequisitionView reqsv = businessObjectService.findByPrimaryKey(RequisitionView.class, keys);

        assertEquals(reqs.getApplicationDocumentStatus(), reqsv.getApplicationDocumentStatus());
    }

}
