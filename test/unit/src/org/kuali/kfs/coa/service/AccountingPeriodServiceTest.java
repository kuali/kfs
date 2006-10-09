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
package org.kuali.module.chart.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the AccountingPeriod business object from a persistence standpoint using the BusinessObjectService.
 * 
 * 
 */
@WithTestSpringContext
public class AccountingPeriodServiceTest extends KualiTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingPeriodServiceTest.class);

    private BusinessObjectService bos = null;
    private AccountingPeriodService aps = null;
    public static final boolean BUDGET_ROLLOVER_IND = true;
    public static final String GUID = "123456789012345678901234567890123456";
    public static final String UNIV_FISC_PERD_CODE = "01";
    public static final Date UNIV_FISC_PERD_END_DATE = new java.sql.Date(System.currentTimeMillis());
    public static final Integer UNIV_FISC_YEAR = new Integer(1776);
    public static final String UNIV_FISC_PRD_NAME = "JUL. 1776";
    public static final String UNIV_FISC_PRD_STATUS_CODE = "C";
    public static final Long VER_NBR = new Long(1);

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bos = SpringServiceLocator.getBusinessObjectService();
        aps = SpringServiceLocator.getAccountingPeriodService();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPersistence() {
        AccountingPeriod ap = new AccountingPeriod();
        ap.setBudgetRolloverIndicator(BUDGET_ROLLOVER_IND);
        ap.setExtendedAttributeValues(new ArrayList());
        ap.setObjectId(GUID);
        ap.setUniversityFiscalPeriodCode(UNIV_FISC_PERD_CODE);
        ap.setUniversityFiscalPeriodEndDate(UNIV_FISC_PERD_END_DATE);
        ap.setUniversityFiscalPeriodName(UNIV_FISC_PRD_NAME);
        ap.setUniversityFiscalPeriodStatusCode(UNIV_FISC_PRD_STATUS_CODE);
        ap.setUniversityFiscalYear(UNIV_FISC_YEAR);
        ap.setVersionNumber(VER_NBR);

        bos.save(ap);
        assertNotNull(getAccountingPeriodByPrimaryKeys());

        AccountingPeriod ap2 = getAccountingPeriodByPrimaryKeys();
        assertEquals(ap2.getUniversityFiscalPeriodName(), UNIV_FISC_PRD_NAME);

        AccountingPeriod ap3 = getAccountingPeriodByPrimaryKeys();
        bos.delete(ap3);
        assertNull(getAccountingPeriodByPrimaryKeys());
    }

    private AccountingPeriod getAccountingPeriodByPrimaryKeys() {
        HashMap h = new HashMap();
        h.put("universityFiscalYear", UNIV_FISC_YEAR);
        h.put("universityFiscalPeriodCode", UNIV_FISC_PERD_CODE);
        AccountingPeriod ap2 = (AccountingPeriod) bos.findByPrimaryKey(AccountingPeriod.class, h);
        return ap2;
    }

    public void testGetAllAccountingPeriods() {
        ArrayList acctPers = new ArrayList(aps.getAllAccountingPeriods());
        assertNotNull(acctPers);
        assertTrue(acctPers.size() > 0);
    }

    public void testGetOpenAccountingPeriods() {
        ArrayList acctPers = new ArrayList(aps.getOpenAccountingPeriods());
        LOG.info("Number of OpenAccountingPeriods found: " + acctPers.size());

        // all returned AccountingPeriod instances should be marked as OPEN
        for (Iterator iter = acctPers.iterator(); iter.hasNext();) {
            AccountingPeriod ap = (AccountingPeriod) iter.next();
            String statusCode = ap.getUniversityFiscalPeriodStatusCode();
            assertTrue(statusCode.equals(Constants.ACCOUNTING_PERIOD_STATUS_OPEN));
        }
    }
}
