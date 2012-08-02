/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.authorization;

import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE5;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE7;
import static org.kuali.kfs.sys.fixture.UserNameFixture.vputman;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KualiTestConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext
public class FinancialSystemTransactionalDocumentAuthorizerBaseTest extends KualiTestBase {

    private static final String ACCOUNT_REVIEW = "Account";
    
    @ConfigureContext(session=UserNameFixture.khuntley)
    public void testCannotRecallRequisition() throws Exception {
        DocumentAuthorizer auth = new FinancialSystemTransactionalDocumentAuthorizerBase();
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        AccountingDocumentTestUtils.testRouteDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));

        Person user = GlobalVariables.getUserSession().getPerson();
        assertFalse("Recall should not be applicable for Requisition documents. (Only FP docs)", auth.canRecall(requisitionDocument, user));
    }

    @ConfigureContext(session=UserNameFixture.khuntley)
    public void testCannotRecallJournalVoucher() throws Exception {
        DocumentAuthorizer auth = new FinancialSystemTransactionalDocumentAuthorizerBase();

        AccountingDocument document = buildJournalVoucherDocument();
//        AccountingDocumentTestUtils.testRouteDocument(document, SpringContext.getBean(DocumentService.class));

        SpringContext.getBean(DocumentService.class).routeDocument(document, "routing JV doc for testCannotRecallJournalVoucher()", null);

        Person user = GlobalVariables.getUserSession().getPerson();
        assertFalse("Recall should not be applicable for Journal Voucher documents since they route straight to FINAL.", auth.canRecall(document, user));

     // TODO: Put back when WorkflowTestUtils.waitForDocumentApproval is working again
//        // jv docs go sttraight to final
//        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
//        assertFalse("Recall should not be applicable for Journal Voucher documents since they route straight to FINAL.", auth.canRecall(document, user));
    }

    @ConfigureContext(session=UserNameFixture.khuntley)
    public void testCanRecallDisbursementVoucher() throws Exception {
        DocumentAuthorizer auth = new FinancialSystemTransactionalDocumentAuthorizerBase();
        Document disbursementVoucherDocument = buildDisbursementVoucherDocument();
        SpringContext.getBean(DocumentService.class).routeDocument(disbursementVoucherDocument, "routing test doc", null);

        Person user = GlobalVariables.getUserSession().getPerson();
        assertTrue("Recall should be applicable for DisbursementVoucher documents prior to approval.", auth.canRecall(disbursementVoucherDocument, user));
        
        // recall and test for no FYI?
    }


// TODO: Put back when WorkflowTestUtils.waitForNodeChange is working again
//    @ConfigureContext(session=UserNameFixture.khuntley)
//    public void testCannotRecallDisbursementVoucherAfterApproval() throws Exception {
//        DocumentAuthorizer auth = new FinancialSystemTransactionalDocumentAuthorizerBase();
//        Document disbursementVoucherDocument = buildDisbursementVoucherDocument();
//        final String docId = disbursementVoucherDocument.getDocumentNumber();
//        SpringContext.getBean(DocumentService.class).routeDocument(disbursementVoucherDocument, "routing test doc", null);
//
//        WorkflowTestUtils.waitForNodeChange(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
//
//        Person user = GlobalVariables.getUserSession().getPerson();
//
////        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
//        disbursementVoucherDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
//
//        // the document should now be routed to vputman as Fiscal Officer
//        changeCurrentUser(vputman);
//        disbursementVoucherDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
//        SpringContext.getBean(DocumentService.class).approveDocument(disbursementVoucherDocument, "Test approving as vputman", null);
//
//    
//    }

    private JournalVoucherDocument buildJournalVoucherDocument() throws Exception {
        // put accounting lines into document parameter for later
        JournalVoucherDocument document = (JournalVoucherDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        document.getDocumentHeader().setDocumentDescription(StringUtils.abbreviate("Unit Test doc for "+trace[3].getMethodName(), SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(document.getDocumentHeader().getClass(), "documentDescription")));
        document.getDocumentHeader().setExplanation(StringUtils.abbreviate("Unit test created document for "+trace[3].getClassName()+"."+trace[3].getMethodName(), SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(document.getDocumentHeader().getClass(), "explanation")));

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getJVSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsVoucherSourceTo(document);
        }

        document.setBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        return document;
    }

    private List<AccountingLineFixture> getJVSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE5);
        return list;
    }

    private Document buildDisbursementVoucherDocument() throws Exception {
        // put accounting lines into document parameter for later
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) getDVDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getDVSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsSourceTo(document);
        }

        for (AccountingLineFixture targetFixture : getDVTargetAccountingLineParametersFromFixtures()) {
            targetFixture.addAsTargetTo(document);
        }

        return document;
    }

    private List<AccountingLineFixture> getDVTargetAccountingLineParametersFromFixtures() {
        return new ArrayList<AccountingLineFixture>();
    }

    private List<AccountingLineFixture> getDVSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE7);
        return list;
    }

    private Document getDVDocumentParameterFixture() throws Exception {
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
        for (AccountingLineFixture fixture : getDVSourceAccountingLineParametersFromFixtures()) {
            amount = amount.add(fixture.amount);
        }
        for (AccountingLineFixture fixture : getDVTargetAccountingLineParametersFromFixtures()) {
            amount = amount.add(fixture.amount);
        }
        document.setDisbVchrCheckTotalAmount(amount);
        return document;
    }

}
