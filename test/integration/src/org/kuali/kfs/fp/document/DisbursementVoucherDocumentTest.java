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

import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.test.parameters.DisbursementVoucherDocumentParameter;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class is used to test DisbursementVoucherDocument.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentTest extends TransactionalDocumentTestBase {
    public static final String COLLECTION_NAME = "DisbursementVoucherDocumentTest.collection1";
    public static final String USER_NAME = "user1";
    public static final String DV_USER_NAME = "dvUser1";
    public static final String DOCUMENT_PARAMETER = "disbursementVoucherDocumentParameter1";
    public static final String SOURCE_LINE7 = "sourceLine7";

    /**
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

        dvParameter.setDisbVchrContactPersonName(GlobalVariables.getUserSession().getKualiUser().getPersonName());
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
