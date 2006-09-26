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
package org.kuali.bo;

import java.sql.Date;
import java.util.ArrayList;

import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.test.KualiTestBase;

/**
 * This class tests the AccountingPeriod business object.
 * 
 * @author Kuali Nervous System Team ()
 */
public class AccountingPeriodTest extends KualiTestBase {
    AccountingPeriod ap = null;
    public static final boolean BUDGET_ROLLOVER_IND = true;
    public static final String GUID = "123456789012345678901234567890123456";
    public static final String UNIV_FISC_PERD_CODE = "BB";
    public static final Date UNIV_FISC_PERD_END_DATE = new java.sql.Date(System.currentTimeMillis());
    public static final Integer UNIV_FISC_YEAR = new Integer(2005);
    public static final String UNIV_FISC_PRD_NAME = "JAN. 1776";
    public static final String UNIV_FISC_PRD_STATUS_CODE = "C";
    public static final Long VER_NBR = new Long(1);

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        ap = new AccountingPeriod();
        ap.setBudgetRolloverIndicator(BUDGET_ROLLOVER_IND);
        ap.setExtendedAttributeValues(new ArrayList());
        ap.setObjectId(GUID);
        ap.setUniversityFiscalPeriodCode(UNIV_FISC_PERD_CODE);
        ap.setUniversityFiscalPeriodEndDate(UNIV_FISC_PERD_END_DATE);
        ap.setUniversityFiscalPeriodName(UNIV_FISC_PRD_NAME);
        ap.setUniversityFiscalPeriodStatusCode(UNIV_FISC_PRD_STATUS_CODE);
        ap.setUniversityFiscalYear(UNIV_FISC_YEAR);
        ap.setVersionNumber(VER_NBR);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        ap = null;
    }

    /**
     * Constructor for AccountingPeriodTest.
     */
    public AccountingPeriodTest() {
    }

    public void testAccountingPeriodPojo() {
        assertEquals(BUDGET_ROLLOVER_IND, ap.isBudgetRolloverIndicator());
        assertEquals(0, ap.getExtendedAttributeValues().size());
        assertEquals(GUID, ap.getObjectId());
        assertEquals(UNIV_FISC_PERD_CODE, ap.getUniversityFiscalPeriodCode());
        assertEquals(UNIV_FISC_PERD_END_DATE, ap.getUniversityFiscalPeriodEndDate());
        assertEquals(UNIV_FISC_PRD_NAME, ap.getUniversityFiscalPeriodName());
        assertEquals(UNIV_FISC_PRD_STATUS_CODE, ap.getUniversityFiscalPeriodStatusCode());
        assertEquals(UNIV_FISC_YEAR, ap.getUniversityFiscalYear());
        assertEquals(VER_NBR, ap.getVersionNumber());
    }

}
