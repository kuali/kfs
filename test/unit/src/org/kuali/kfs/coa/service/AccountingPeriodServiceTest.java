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
