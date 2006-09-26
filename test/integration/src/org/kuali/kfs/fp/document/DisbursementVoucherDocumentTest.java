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

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.test.parameters.DisbursementVoucherDocumentParameter;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;

/**
 * This class is used to test DisbursementVoucherDocument.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class DisbursementVoucherDocumentTest extends TransactionalDocumentTestBase {
    public static final String COLLECTION_NAME = "DisbursementVoucherDocumentTest.collection1";
    public static final String USER_NAME = "user1";
    public static final String DV_USER_NAME = "dvUser1";
    public static final String DOCUMENT_PARAMETER = "disbursementVoucherDocumentParameter1";
    public static final String SOURCE_LINE7 = "sourceLine7";

    // The set of Route Nodes that the test document will progress through

    private static final String ADHOC = "Adhoc Routing";
    private static final String ACCOUNT_REVIEW = "Account Review";
    private static final String ORG_REVIEW = "Org Review";
    private static final String EMPLOYEE_INDICATOR = "Employee Indicator";
    private static final String TAX_CONTROL_CODE = "Tax Control Code";
    private static final String ALIEN_INDICATOR = "Alien Indicator";
    private static final String PAYMENT_REASON = "Payment Reason";
    private static final String PAYMENT_REASON_CAMPUS_CODE = "Payment Reason+Campus Code";
    private static final String CAMPUS_CODE = "Campus Code";
    private static final String ALIEN_INDICATOR_PAYMENT_REASON = "Alien Indicator+Payment Reason";
    private static final String PAYMENT_METHOD = "Payment Method";

    /*
     * @see org.kuali.core.document.TransactionalDocumentTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        changeCurrentUser((String) getFixtureEntryFromCollection(COLLECTION_NAME, DV_USER_NAME).createObject());
    }


    public void testConvertIntoCopy_clear_additionalCodeInvalidPayee() throws Exception {
        GlobalVariables.setMessageList(new ArrayList());
        DisbursementVoucherDocumentParameter dvParameter = (DisbursementVoucherDocumentParameter) getDocumentParameterFixture();
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvParameter.createDocument(getDocumentService());
        document.getDvPayeeDetail().setDisbVchrPayeeIdNumber("1234");
        document.convertIntoCopy();

        // the dvParameter doc number needs to be resynced
        dvParameter.setDocumentNumber(document.getFinancialDocumentNumber());
        dvParameter.setDisbVchrContactPhoneNumber("");
        dvParameter.setDisbVchrContactEmailId("");
        dvParameter.getPayeeDetail().setDisbVchrPayeePersonName("");
        dvParameter.getPayeeDetail().setDisbVchrPayeeLine1Addr("");
        dvParameter.getPayeeDetail().setDisbVchrPayeeLine2Addr("");
        dvParameter.getPayeeDetail().setDisbVchrPayeeCityName("");
        dvParameter.getPayeeDetail().setDisbVchrPayeeStateCode("");
        dvParameter.getPayeeDetail().setDisbVchrPayeeZipCode("");
        dvParameter.getPayeeDetail().setDisbVchrPayeeCountryCode("");
        dvParameter.getPayeeDetail().setDisbVchrAlienPaymentCode(false);
        dvParameter.setDvNonResidentAlienTax(new DisbursementVoucherNonResidentAlienTax());
        dvParameter.setDisbVchrPayeeTaxControlCode("");

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

        dvParameter.assertMatch(document);

    }

    @TestsWorkflowViaDatabase
    public void testWorkflowRouting() throws Exception {
        NetworkIdVO VPUTMAN = new NetworkIdVO("VPUTMAN");
        NetworkIdVO CSWINSON = new NetworkIdVO("CSWINSON");
        NetworkIdVO MYLARGE = new NetworkIdVO("MYLARGE");

        // save and route the document
        Document document = buildDocument();
        getDocumentService().routeDocument(document, "routing test doc", null);

        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        // the document should now be routed to VPUTMAN as Fiscal Officer
        KualiWorkflowDocument wfDoc = WorkflowTestUtils.refreshDocument(document, VPUTMAN);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, ACCOUNT_REVIEW));
        assertTrue("Document should be enroute.", wfDoc.stateIsEnroute());
        assertTrue("VPUTMAN should have an approve request.", wfDoc.isApprovalRequested());
        getDocumentService().approveDocument(document, "Test approving as VPUTMAN", null);

        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ORG_REVIEW);

        // now doc should be in Org Review routing to CSWINSON
        wfDoc = WorkflowTestUtils.refreshDocument(document, CSWINSON);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, ORG_REVIEW));
        assertTrue("CSWINSON should have an approve request.", wfDoc.isApprovalRequested());
        getDocumentService().approveDocument(document, "Test approving as CSWINSON", null);

        // this is going to skip a bunch of other routing and end up at campus code
        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), CAMPUS_CODE);

        // doc should be in "Campus Code" routing to MYLARGE
        wfDoc = WorkflowTestUtils.refreshDocument(document, MYLARGE);
        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(document, CAMPUS_CODE));
        assertTrue("Should have an approve request.", wfDoc.isApprovalRequested());
        getDocumentService().approveDocument(document, "Approve", null);

        WorkflowTestUtils.waitForStatusChange(wfDoc, EdenConstants.ROUTE_HEADER_FINAL_CD);

        wfDoc = WorkflowTestUtils.refreshDocument(document, VPUTMAN);
        assertTrue("Document should now be final.", wfDoc.stateIsFinal());
    }

    protected int getExpectedPrePeCount() {
        return 2;
    }

    /**
     * Get names of fixture collections test class is using.
     * 
     * @return String[]
     */
    public String[] getFixtureCollectionNames() {
        return new String[] { COLLECTION_NAME };
    }

    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public DocumentParameter getDocumentParameterFixture() {
        return (TransactionalDocumentParameter) getFixtureEntryFromCollection(COLLECTION_NAME, DOCUMENT_PARAMETER).createObject();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    public List getTargetAccountingLineParametersFromFixtures() {
        return new ArrayList();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    public List getSourceAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, SOURCE_LINE7).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getUserName()
     */
    public String getUserName() {
        return (String) getFixtureEntryFromCollection(COLLECTION_NAME, USER_NAME).createObject();
    }


}
