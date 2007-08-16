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
package org.kuali.module.chart.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the AccountingPeriod business object from a persistence standpoint using the BusinessObjectService.
 * 
 * 
 */
@ConfigureContext
public class AccountingPeriodServiceTest extends KualiTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingPeriodServiceTest.class);

    public void testPersistence() {
        Date date = SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentSqlDate();
        AccountingPeriod period = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);
        assertNotNull(period);

        Integer year = period.getUniversityFiscalYear();
        String universityFiscalPeriodCode = "UT";
        String periodName = "unitTest";

        period.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
        period.setUniversityFiscalPeriodName(periodName);

        SpringContext.getBean(BusinessObjectService.class).save(period);

        AccountingPeriod result = getAccountingPeriodByPrimaryKeys(year, universityFiscalPeriodCode);
        assertNotNull(result);
        assertEquals(periodName, result.getUniversityFiscalPeriodName());

        SpringContext.getBean(BusinessObjectService.class).delete(result);

        result = getAccountingPeriodByPrimaryKeys(year, universityFiscalPeriodCode);
        assertNull(result);
    }

    private AccountingPeriod getAccountingPeriodByPrimaryKeys(Integer fiscalYear, String fiscalPeriodcode) {
        Map<String, Object> h = new HashMap<String, Object>();
        h.put("universityFiscalYear", fiscalYear);
        h.put("universityFiscalPeriodCode", fiscalPeriodcode);
        AccountingPeriod ap2 = (AccountingPeriod) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AccountingPeriod.class, h);
        return ap2;
    }

    public void testGetAllAccountingPeriods() {
        List<AccountingPeriod> accountingPeriods = (List<AccountingPeriod>) SpringContext.getBean(AccountingPeriodService.class).getAllAccountingPeriods();
        assertNotNull(accountingPeriods);
        assertFalse(accountingPeriods.isEmpty());
    }

    public void testGetOpenAccountingPeriods() {
        List<AccountingPeriod> accountingPeriods = (List<AccountingPeriod>) SpringContext.getBean(AccountingPeriodService.class).getOpenAccountingPeriods();
        LOG.info("Number of OpenAccountingPeriods found: " + accountingPeriods.size());

        assertNotNull(accountingPeriods);
        assertFalse(accountingPeriods.isEmpty());
        // all returned AccountingPeriod instances should be marked as OPEN
        for (AccountingPeriod accountingPeriod : accountingPeriods) {
            String statusCode = accountingPeriod.getUniversityFiscalPeriodStatusCode();
            assertTrue(statusCode.equals(KFSConstants.ACCOUNTING_PERIOD_STATUS_OPEN));
        }
    }
}
