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
package org.kuali.module.financial.document;

import static org.kuali.kfs.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.kfs.util.SpringServiceLocator.getDocumentService;
import static org.kuali.kfs.util.SpringServiceLocator.getTransactionalDocumentDictionaryService;
import static org.kuali.kfs.util.SpringServiceLocator.getAccountingPeriodService;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.saveDocument;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE7;
import static org.kuali.test.fixtures.UserNameFixture.CSWINSON;
import static org.kuali.test.fixtures.UserNameFixture.HSCHREIN;
import static org.kuali.test.fixtures.UserNameFixture.MYLARGE;
import static org.kuali.test.fixtures.UserNameFixture.VPUTMAN;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import org.kuali.test.suite.RelatesTo;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * This class is used to test DisbursementVoucherDocument.
 * 
 * 
 */
@WithTestSpringContext(session = HSCHREIN)
public class DisbursementVoucherDocumentTest extends KualiTestBase {

    public static final Class<DisbursementVoucherDocument> DOCUMENT_CLASS = DisbursementVoucherDocument.class;
    // The set of Route Nodes that the test document will progress through

    private static final String ACCOUNT_REVIEW = "Account Review";
    private static final String ORG_REVIEW = "Org Review";
    private static final String CAMPUS_CODE = "Campus Code";

    public final void testConvertIntoCopy_clear_additionalCodeInvalidPayee() throws Exception {
        GlobalVariables.setMessageList(new ArrayList());
        DisbursementVoucherDocument dvParameter = (DisbursementVoucherDocument) getDocumentParameterFixture();
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) getDocumentParameterFixture();
        document.getDvPayeeDetail().setDisbVchrPayeeIdNumber("1234");
        document.toCopy();

        // the dvParameter doc number needs to be resynced
        dvParameter.setDocumentNumber(document.getDocumentNumber());
        dvParameter.setDisbVchrContactPhoneNumber("");
        dvParameter.setDisbVchrContactEmailId("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeePersonName("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeLine1Addr("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeLine2Addr("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeCityName("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeStateCode("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeZipCode("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeCountryCode("");
        dvParameter.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);
        dvParameter.setDvNonResidentAlienTax(new DisbursementVoucherNonResidentAlienTax());
        dvParameter.setDisbVchrPayeeTaxControlCode("");
        dvParameter.getDvPayeeDetail().setDisbVchrPayeeIdNumber("");

        dvParameter.setDisbVchrContactPersonName(GlobalVariables.getUserSession().getUniversalUser().getPersonName());
        // set to tomorrow
        Calendar calendar = SpringServiceLocator.getDateTimeService().getCurrentCalendar();
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

    @TestsWorkflowViaDatabase
    @RelatesTo(RelatesTo.JiraIssue.KULUT10)
    public final void testWorkflowRouting() throws Exception {
        // save and route the document
        Document document = buildDocument();
        final String docId = document.getDocumentNumber();
        getDocumentService().routeDocument(document, "routing test doc", null);

        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to VPUTMAN as Fiscal Officer
        changeCurrentUser(VPUTMAN);
        document = getDocumentService().getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", document.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue("VPUTMAN should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        getDocumentService().approveDocument(document, "Test approving as VPUTMAN", null); 

        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ORG_REVIEW);
        // now doc should be in Org Review routing to CSWINSON
        changeCurrentUser(CSWINSON);
        document = getDocumentService().getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, ORG_REVIEW));
        assertTrue("CSWINSON should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        getDocumentService().approveDocument(document, "Test approving as CSWINSON", null);

        // this is going to skip a bunch of other routing and end up at campus code
        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), CAMPUS_CODE);

        // doc should be in "Campus Code" routing to MYLARGE
        changeCurrentUser(MYLARGE);
        document = getDocumentService().getByDocumentHeaderId(docId);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, CAMPUS_CODE));
        assertTrue("Should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        getDocumentService().approveDocument(document, "Approve", null);

        WorkflowTestUtils.waitForStatusChange(document.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(VPUTMAN);
        document = getDocumentService().getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", document.getDocumentHeader().getWorkflowDocument().stateIsFinal());
    }

    private int getExpectedPrePeCount() {
        return 2;
    }

    private Document getDocumentParameterFixture() throws Exception {
        DisbursementVoucherDocument document = DocumentTestUtils.createDocument(getDocumentService(), DisbursementVoucherDocument.class);
        DisbursementVoucherPayeeDetail payeeDetail = new DisbursementVoucherPayeeDetail();
        payeeDetail.setDisbVchrPayeeIdNumber("P000178071");
        payeeDetail.setDisbVchrPayeePersonName("Jerry Neal");
        payeeDetail.setDisbVchrPayeeLine1Addr("Poplars 423");
        payeeDetail.setDisbVchrPayeeCountryCode("UK");
        payeeDetail.setDisbVchrPaymentReasonCode("B");
        payeeDetail.setDisbursementVoucherPayeeTypeCode("P");
        payeeDetail.setDocumentNumber(document.getDocumentNumber());
        // payee detail
        document.setDvPayeeDetail(payeeDetail);
        // payment info
        document.setDisbVchrPaymentMethodCode("P");
        document.setDisbursementVoucherDueDate(Date.valueOf("2010-01-24"));
        document.setDisbursementVoucherDocumentationLocationCode("F");
        // contact information
        document.setCampusCode("BL");
        document.setDisbVchrContactPhoneNumber("8081234567");
        document.setDisbVchrContactPersonName("aynalem");
        document.setDisbVchrCheckStubText("Test DV Check");

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
        Assert.assertEquals(d1.getDisbVchrPayeeLine1Addr(), d2.getDisbVchrPayeeLine1Addr());
        Assert.assertEquals(d1.getDisbVchrPayeeCountryCode(), d2.getDisbVchrPayeeCountryCode());
        Assert.assertEquals(d1.getDisbVchrPaymentReasonCode(), d2.getDisbVchrPaymentReasonCode());
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        AccountingDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(getDocumentService(), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, getDocumentService());
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), getDataDictionaryService());

    }

    @TestsWorkflowViaDatabase
    public final void testRouteDocument() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), getDocumentService());
    }

    @TestsWorkflowViaDatabase
    public final void testSaveDocument() throws Exception {
        // get document parameter
        AccountingDocument document = buildDocument();
        document.prepareForSave();

        // save
        saveDocument(document, getDocumentService());

        // retrieve
        AccountingDocument result = (AccountingDocument) getDocumentService().getByDocumentHeaderId(document.getDocumentNumber());
        // verify
        assertMatch(document, result);

    }

    @TestsWorkflowViaDatabase
    public final void testConvertIntoCopy() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy(buildDocument(), getDocumentService(), getExpectedPrePeCount());
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
