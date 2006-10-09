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

import static org.kuali.core.util.SpringServiceLocator.getAccountingPeriodService;
import static org.kuali.core.util.SpringServiceLocator.getBusinessObjectService;
import static org.kuali.core.util.SpringServiceLocator.getDateTimeService;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
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

    public void testPersistence() {
        Date date = getDateTimeService().getCurrentSqlDate();
        AccountingPeriod period = getAccountingPeriodService().getByDate(date);
        assertNotNull(period);

        Integer year = period.getUniversityFiscalYear();
        String universityFiscalPeriodCode = "UT";
        String periodName = "unitTest";

        period.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
        period.setUniversityFiscalPeriodName(periodName);

        getBusinessObjectService().save(period);

        AccountingPeriod result = getAccountingPeriodByPrimaryKeys(year, universityFiscalPeriodCode);
        assertNotNull(result);
        assertEquals(periodName, result.getUniversityFiscalPeriodName());

        getBusinessObjectService().delete(result);

        result = getAccountingPeriodByPrimaryKeys(year, universityFiscalPeriodCode);
        assertNull(result);
    }

    private AccountingPeriod getAccountingPeriodByPrimaryKeys(Integer fiscalYear, String fiscalPeriodcode) {
        Map<String, Object> h = new HashMap<String, Object>();
        h.put("universityFiscalYear", fiscalYear);
        h.put("universityFiscalPeriodCode", fiscalPeriodcode);
        AccountingPeriod ap2 = (AccountingPeriod) getBusinessObjectService().findByPrimaryKey(AccountingPeriod.class, h);
        return ap2;
    }

    public void testGetAllAccountingPeriods() {
        List<AccountingPeriod> accountingPeriods = (List<AccountingPeriod>) getAccountingPeriodService().getAllAccountingPeriods();
        assertNotNull(accountingPeriods);
        assertFalse(accountingPeriods.isEmpty());
    }

    public void testGetOpenAccountingPeriods() {
        List<AccountingPeriod> accountingPeriods = (List<AccountingPeriod>) getAccountingPeriodService().getOpenAccountingPeriods();
        LOG.info("Number of OpenAccountingPeriods found: " + accountingPeriods.size());

        assertNotNull(accountingPeriods);
        assertFalse(accountingPeriods.isEmpty());
        // all returned AccountingPeriod instances should be marked as OPEN
        for (AccountingPeriod accountingPeriod : accountingPeriods) {
            String statusCode = accountingPeriod.getUniversityFiscalPeriodStatusCode();
            assertTrue(statusCode.equals(Constants.ACCOUNTING_PERIOD_STATUS_OPEN));
        }
    }
}
