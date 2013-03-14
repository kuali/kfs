/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document;

import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.saveDocument;
import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE7;
import static org.kuali.kfs.sys.fixture.UserNameFixture.hschrein;
import static org.kuali.kfs.sys.fixture.UserNameFixture.mylarge;
import static org.kuali.kfs.sys.fixture.UserNameFixture.vputman;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KualiTestConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class is used to test DisbursementVoucherDocument.
 */
@ConfigureContext(session = hschrein)
public class DisbursementVoucherDocumentTest extends KualiTestBase {
    private static Logger LOG = Logger.getLogger(DisbursementVoucherDocumentTest.class);

    public static final Class<DisbursementVoucherDocument> DOCUMENT_CLASS = DisbursementVoucherDocument.class;
    // The set of Route Nodes that the test document will progress through

    private static final String ACCOUNT_REVIEW = "Account";
    private static final String ORG_REVIEW = "AccountingOrganizationHierarchy";
    private static final String TAX_REVIEW = "Tax";
    private static final String CAMPUS_CODE = "Campus";
    private static final String PAYMENT_METHOD = "PaymentMethod";


    public final void testConvertIntoCopy_clear_additionalCodeInvalidVendor() throws Exception {
        // Clear both Message lists until rice drops the latter
        GlobalVariables.getMessageMap().clearErrorMessages();
        KNSGlobalVariables.getMessageList().clear();

    	DisbursementVoucherDocument dvParameter = (DisbursementVoucherDocument) getDocumentParameterFixture();
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) getDocumentParameterFixture();
        document.getDvPayeeDetail().setDisbVchrPayeeIdNumber("1234-0");
        document.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode("V");
        document.toCopy();

        // the dvParameter doc number needs to be resynced
        dvParameter.setDocumentNumber(document.getDocumentNumber());
        dvParameter.setDisbVchrContactPhoneNumber("");
        dvParameter.setDisbVchrContactEmailId("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeePersonName(null);
        dvParameter.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);
        dvParameter.getDvPayeeDetail().setDisbVchrPaymentReasonCode(null);

        dvParameter.setDvNonResidentAlienTax(new DisbursementVoucherNonResidentAlienTax());
        dvParameter.setDisbVchrPayeeTaxControlCode("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeIdNumber("");

        dvParameter.setDisbVchrContactPersonName(GlobalVariables.getUserSession().getPerson().getName());
        // set to tomorrow
        Calendar calendar = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.clear(Calendar.MILLISECOND);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.HOUR);
        dvParameter.setDisbursementVoucherDueDate(new Date(calendar.getTimeInMillis()));

        // clear document time since just want to compare dates
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(document.getDisbursementVoucherDueDate().getTime());
        calendar2.clear(Calendar.MILLISECOND);
        calendar2.clear(Calendar.SECOND);
        calendar2.clear(Calendar.MINUTE);
        calendar2.clear(Calendar.HOUR);
        document.setDisbursementVoucherDueDate(new Date(calendar2.getTimeInMillis()));

        assertMatch(dvParameter, document);

    }

    @ConfigureContext(session = hschrein, shouldCommitTransactions = true)
    // @RelatesTo(RelatesTo.JiraIssue.KULRNE4834)
    public final void testWorkflowRouting() throws Exception {
        // save and route the document
        Document document = buildDocument();
        final String docId = document.getDocumentNumber();
        SpringContext.getBean(DocumentService.class).routeDocument(document, "routing test doc", null);

        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to vputman as Fiscal Officer
        changeCurrentUser(vputman);
        document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", document.getDocumentHeader().getWorkflowDocument().isEnroute());
        assertTrue("vputman should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(document, "Test approving as vputman", null);

        /*WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ORG_REVIEW);
        // now doc should be in Org Review routing to cswinson
        changeCurrentUser(UserNameFixture.cswinson);
        document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, ORG_REVIEW));
        assertTrue("cswinson should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(document, "Test approving as cswinson", null);*/

        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), TAX_REVIEW);
        // now doc should be in Org Review routing to cswinson
        changeCurrentUser(UserNameFixture.dfogle);
        document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, TAX_REVIEW));
        assertTrue("dfogle should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(document, "Test approving as dfogle", null);

        // this is going to skip a bunch of other routing and end up at campus code
        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), CAMPUS_CODE);
        // doc should be in "Campus Code" routing to mylarge
        changeCurrentUser(mylarge);
        document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, CAMPUS_CODE));
        assertTrue("Should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(document, "Approve", null);

        /*WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), PAYMENT_METHOD);
        // doc should be in "Campus Code" routing to mylarge
        changeCurrentUser(UserNameFixture.appleton);
        document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, PAYMENT_METHOD));
        assertTrue("Should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        SpringContext.getBean(DocumentService.class).approveDocument(document, "Approve", null);*/

        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());

        changeCurrentUser(vputman);
        document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", document.getDocumentHeader().getWorkflowDocument().isFinal());
    }

    private int getExpectedPrePeCount() {
        return 2;
    }

    private Document getDocumentParameterFixture() throws Exception {
        DisbursementVoucherDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DisbursementVoucherDocument.class);
        DisbursementVoucherPayeeDetail payeeDetail = new DisbursementVoucherPayeeDetail();
        payeeDetail.setDisbVchrPayeeIdNumber("1013-0");
       // payeeDetail.setDisbVchrAlienPaymentCode(true);
        payeeDetail.setDisbursementVoucherPayeeTypeCode("V");
        payeeDetail.setDisbVchrPayeeLine1Addr("100 Main St");
        payeeDetail.setDisbVchrPayeeCityName("Bloomington");
        payeeDetail.setDisbVchrPayeeStateCode("IN");
        payeeDetail.setDisbVchrPayeeZipCode("47405");
        payeeDetail.setDisbVchrPayeeCountryCode("US");

        payeeDetail.setDisbVchrVendorDetailAssignedIdNumber("0");
        payeeDetail.setDisbVchrPayeePersonName("Jerry Neal");
        payeeDetail.setDisbVchrPaymentReasonCode("B");
        payeeDetail.setDocumentNumber(document.getDocumentNumber());
        // payee detail
        document.setDvPayeeDetail(payeeDetail);
        // payment info
        document.setDisbVchrPaymentMethodCode("P");
        document.setDisbursementVoucherDueDate(Date.valueOf("2010-01-24"));
        document.setDisbursementVoucherDocumentationLocationCode("F");
        // contact information
        document.setCampusCode("BL");
        document.setDisbVchrContactPhoneNumber("808-123-4567");
        document.setDisbVchrContactPersonName("aynalem");
        document.setDisbVchrCheckStubText("Test DV Check");
        document.setDisbVchrBankCode(KualiTestConstants.TestConstants.BankCodeTestData.BANK_CODE);

        KualiDecimal amount = KualiDecimal.ZERO;
        for (AccountingLineFixture fixture : getSourceAccountingLineParametersFromFixtures()) {
            amount = amount.add(fixture.amount);
        }
        for (AccountingLineFixture fixture : getTargetAccountingLineParametersFromFixtures()) {
            amount = amount.add(fixture.amount);
        }
        document.setDisbVchrCheckTotalAmount(amount);
        return document;
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        return new ArrayList<AccountingLineFixture>();
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE7);
        return list;
    }

    private <T extends Document> void assertMatch(T document1, T document2) {
        AccountingDocumentTestUtils.assertMatch(document1, document2);
        DisbursementVoucherDocument d1 = (DisbursementVoucherDocument) document1;
        DisbursementVoucherDocument d2 = (DisbursementVoucherDocument) document2;

        assertPayeeDetail(d1.getDvPayeeDetail(), d2.getDvPayeeDetail());

        Assert.assertEquals(d2.getDisbVchrCheckTotalAmount(), d2.getDisbVchrCheckTotalAmount());
        Assert.assertEquals(d1.getDisbVchrPaymentMethodCode(), d2.getDisbVchrPaymentMethodCode());
        Assert.assertEquals(d1.getDisbursementVoucherDueDate(), d2.getDisbursementVoucherDueDate());
        Assert.assertEquals(d1.getDisbursementVoucherDocumentationLocationCode(), d2.getDisbursementVoucherDocumentationLocationCode());
        Assert.assertEquals(d1.getDisbVchrContactEmailId(), d2.getDisbVchrContactEmailId());
        Assert.assertEquals(d1.getDisbVchrContactPhoneNumber(), d2.getDisbVchrContactPhoneNumber());
        Assert.assertEquals(d1.getDisbVchrPayeeTaxControlCode(), d2.getDisbVchrPayeeTaxControlCode());
        Assert.assertEquals(d1.getDisbVchrContactPersonName(), d2.getDisbVchrContactPersonName());
    }

    private void assertPayeeDetail(DisbursementVoucherPayeeDetail d1, DisbursementVoucherPayeeDetail d2) {
        Assert.assertEquals(d1.getDisbVchrPayeeIdNumber(), d2.getDisbVchrPayeeIdNumber());
        Assert.assertEquals(d1.getDisbVchrPayeePersonName(), d2.getDisbVchrPayeePersonName());
        Assert.assertEquals(d1.getDisbVchrPaymentReasonCode(), d2.getDisbVchrPaymentReasonCode());
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        AccountingDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), SpringContext.getBean(DataDictionaryService.class));

    }

    @ConfigureContext(session = hschrein, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        DisbursementVoucherDocument document = buildDocument();
        AccountingDocumentTestUtils.testRouteDocument(document, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = hschrein, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        // get document parameter
        AccountingDocument document = buildDocument();
        document.prepareForSave();

        // save
        saveDocument(document, SpringContext.getBean(DocumentService.class));

        // retrieve
        AccountingDocument result = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(document.getDocumentNumber());
        // verify
        assertMatch(document, result);
    }

    @ConfigureContext(session = hschrein, shouldCommitTransactions = true)
    public final void testConvertIntoCopy() throws Exception {
        DisbursementVoucherDocument document = buildDocument();
        String originalDocumentNumber = document.getDocumentNumber();

        AccountingDocumentTestUtils.testConvertIntoCopy(document, SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
        String copiedDocumentNumber = document.getDocumentNumber();

        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        // original document is dirty and can't be deleted until copied document is saved
        document.prepareForSave();
        saveDocument(document, documentService);

        SpringContext.getBean(BusinessObjectService.class).delete(documentService.getByDocumentHeaderId(originalDocumentNumber));
        SpringContext.getBean(BusinessObjectService.class).delete(documentService.getByDocumentHeaderId(copiedDocumentNumber));
    }

    // test util methods
    private List<SourceAccountingLine> generateSouceAccountingLines() throws Exception {
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceLines.add(sourceFixture.createSourceAccountingLine());
        }

        return sourceLines;
    }

    private List<TargetAccountingLine> generateTargetAccountingLines() throws Exception {
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetLines.add(targetFixture.createTargetAccountingLine());
        }

        return targetLines;
    }

    private DisbursementVoucherDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsSourceTo(document);
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetFixture.addAsTargetTo(document);
        }

        return document;
    }

}

