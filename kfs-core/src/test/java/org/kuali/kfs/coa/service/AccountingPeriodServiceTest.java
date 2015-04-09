/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.service;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the AccountingPeriod business object from a persistence standpoint using the BusinessObjectService.
 */
@ConfigureContext
public class AccountingPeriodServiceTest extends KualiTestBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingPeriodServiceTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.getLogger("p6spy").setLevel(Level.INFO);
    }

    public void testPersistenceAndCaching() {
        Date date = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        AccountingPeriod period = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);
        assertNotNull(period);

        Integer year = period.getUniversityFiscalYear();
        String universityFiscalPeriodCode = "UT";
        String periodName = "unitTest";

        AccountingPeriod nullPeriod = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(universityFiscalPeriodCode, year );
        assertNull( "UT accounting period should not already exist", nullPeriod );

        period.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
        period.setUniversityFiscalPeriodName(periodName);

        SpringContext.getBean(BusinessObjectService.class).save(period);

        AccountingPeriod result = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(universityFiscalPeriodCode, year );
        assertNull( "Previous null result should have been retrieved from cache", result );
        SpringContext.getBean(AccountingPeriodService.class).clearCache();

        result = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(universityFiscalPeriodCode, year );
        assertNotNull( "After clearing cache, we should have retrieved a value", result);
        assertEquals(periodName, result.getUniversityFiscalPeriodName());

        SpringContext.getBean(BusinessObjectService.class).delete(result);

        result = getAccountingPeriodByPrimaryKeys(year, universityFiscalPeriodCode);
        assertNull(result);
    }

    protected AccountingPeriod getAccountingPeriodByPrimaryKeys(Integer fiscalYear, String fiscalPeriodcode) {
        Map<String, Object> h = new HashMap<String, Object>();
        h.put("universityFiscalYear", fiscalYear);
        h.put("universityFiscalPeriodCode", fiscalPeriodcode);
        AccountingPeriod ap2 = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AccountingPeriod.class, h);
        return ap2;
    }

    public void testGetAllAccountingPeriods() {
        Collection<AccountingPeriod> accountingPeriods = SpringContext.getBean(AccountingPeriodService.class).getAllAccountingPeriods();
        assertNotNull(accountingPeriods);
        assertFalse(accountingPeriods.isEmpty());
    }

    public void testGetOpenAccountingPeriods() {
        Collection<AccountingPeriod> accountingPeriods = SpringContext.getBean(AccountingPeriodService.class).getOpenAccountingPeriods();
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Number of OpenAccountingPeriods found: " + accountingPeriods.size());
        }

        assertNotNull(accountingPeriods);
        assertFalse(accountingPeriods.isEmpty());
        // all returned AccountingPeriod instances should be marked as OPEN
        for (AccountingPeriod accountingPeriod : accountingPeriods) {
            boolean activeCode = accountingPeriod.isActive();
            assertTrue(activeCode);
        }
    }

    public void testGetByPeriod() {
        Collection<AccountingPeriod> accountingPeriods = SpringContext.getBean(AccountingPeriodService.class).getAllAccountingPeriods();
        AccountingPeriod periodToTest = accountingPeriods.iterator().next();
        AccountingPeriod period = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(periodToTest.getUniversityFiscalPeriodCode(),periodToTest.getUniversityFiscalYear());

        AccountingPeriod periodFromBoService = getAccountingPeriodByPrimaryKeys(periodToTest.getUniversityFiscalYear(), periodToTest.getUniversityFiscalPeriodCode());

        assertEquals( "service not retrieving expected object", periodFromBoService, period );
    }
}
