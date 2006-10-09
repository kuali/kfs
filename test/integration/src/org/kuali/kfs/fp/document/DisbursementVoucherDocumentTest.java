/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE7;
import static org.kuali.test.fixtures.UserNameFixture.CSWINSON;
import static org.kuali.test.fixtures.UserNameFixture.HSCHREIN;
import static org.kuali.test.fixtures.UserNameFixture.MYLARGE;
import static org.kuali.test.fixtures.UserNameFixture.VPUTMAN;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * This class is used to test DisbursementVoucherDocument.
 * 
 * 
 */
@WithTestSpringContext(session = HSCHREIN)
public class DisbursementVoucherDocumentTest extends TransactionalDocumentTestBase {

    // The set of Route Nodes that the test document will progress through

    private static final String ACCOUNT_REVIEW = "Account Review";
    private static final String ORG_REVIEW = "Org Review";
    private static final String CAMPUS_CODE = "Campus Code";

    public void testConvertIntoCopy_clear_additionalCodeInvalidPayee() throws Exception {
        GlobalVariables.setMessageList(new ArrayList());
        DisbursementVoucherDocument dvParameter = (DisbursementVoucherDocument ) getDocumentParameterFixture();
        DisbursementVoucherDocument document = (DisbursementVoucherDocument ) getDocumentParameterFixture();
        document.getDvPayeeDetail().setDisbVchrPayeeIdNumber("1234");
        document.convertIntoCopy();

        // the dvParameter doc number needs to be resynced
        dvParameter.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
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

        dvParameter.setDisbVchrContactPersonName(GlobalVariables.getUserSession().getKualiUser().getUniversalUser().getPersonName());
        // set to tomorrow
        Calendar calendar = Calendar.getInstance();
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

        assertMatch(dvParameter,document);

    }

    @TestsWorkflowViaDatabase
    public void testWorkflowRouting() throws Exception {
        // save and route the document
        Document document = buildDocument();
        final String docId = document.getFinancialDocumentNumber();
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

    protected int getExpectedPrePeCount() {
        return 2;
    }


    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public Document getDocumentParameterFixture() throws Exception {
        DisbursementVoucherDocument document=DocumentTestUtils.createTransactionalDocument(getDocumentService(), DisbursementVoucherDocument.class, 2007, "06");
        DisbursementVoucherPayeeDetail payeeDetail = new DisbursementVoucherPayeeDetail();
        payeeDetail.setDisbVchrPayeeIdNumber("P000178071");
        payeeDetail.setDisbVchrPayeePersonName("Jerry Neal");
payeeDetail.setDisbVchrPayeeLine1Addr("Poplars 423");
payeeDetail.setDisbVchrPayeeCountryCode("UK");
payeeDetail.setDisbVchrPaymentReasonCode("B");
payeeDetail.setDisbursementVoucherPayeeTypeCode("P");
        payeeDetail.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
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
        for(AccountingLineFixture fixture: getSourceAccountingLineParametersFromFixtures()){
            amount=amount.add(fixture.amount);
        }
        for(AccountingLineFixture fixture:getTargetAccountingLineParametersFromFixtures()){
            amount=amount.add(fixture.amount);
        }
        document.setDisbVchrCheckTotalAmount(amount);
        return document;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    @Override
    public List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        return new ArrayList<AccountingLineFixture>();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    @Override
    public List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE7);
        return list;
    }

    public<T extends Document> void assertMatch(T document1,T document2) {
        super.assertMatch(document1,document2);
        DisbursementVoucherDocument d1 = (DisbursementVoucherDocument)document1;
        DisbursementVoucherDocument d2 = (DisbursementVoucherDocument)document2;

        assertPayeeDetail(d1.getDvPayeeDetail(),d2.getDvPayeeDetail());

        Assert.assertEquals(d2.getDisbVchrCheckTotalAmount(), d2.getDisbVchrCheckTotalAmount());
        Assert.assertEquals(d1.getDisbVchrPaymentMethodCode(), d2.getDisbVchrPaymentMethodCode());
        Assert.assertEquals(d1.getDisbursementVoucherDueDate(), d2.getDisbursementVoucherDueDate());
        Assert.assertEquals(d1.getDisbursementVoucherDocumentationLocationCode(), d2.getDisbursementVoucherDocumentationLocationCode());
        Assert.assertEquals(d1.getDisbVchrContactEmailId(), d2.getDisbVchrContactEmailId());
        Assert.assertEquals(d1.getDisbVchrContactPhoneNumber(), d2.getDisbVchrContactPhoneNumber());
        Assert.assertEquals(d1.getDisbVchrPayeeTaxControlCode(), d2.getDisbVchrPayeeTaxControlCode());
        Assert.assertEquals(d1.getDisbVchrContactPersonName(), d2.getDisbVchrContactPersonName());
    }

    protected void assertPayeeDetail(DisbursementVoucherPayeeDetail d1, DisbursementVoucherPayeeDetail d2) {
        Assert.assertEquals(d1.getDisbVchrPayeeIdNumber(), d2.getDisbVchrPayeeIdNumber());
        Assert.assertEquals(d1.getDisbVchrPayeePersonName(), d2.getDisbVchrPayeePersonName());
        Assert.assertEquals(d1.getDisbVchrPayeeLine1Addr(), d2.getDisbVchrPayeeLine1Addr());
        Assert.assertEquals(d1.getDisbVchrPayeeCountryCode(), d2.getDisbVchrPayeeCountryCode());
        Assert.assertEquals(d1.getDisbVchrPaymentReasonCode(), d2.getDisbVchrPaymentReasonCode());
    }


}
