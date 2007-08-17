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
package org.kuali.bo;

import java.sql.Date;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the AccountingPeriod business object.
 */
@ConfigureContext
public class AccountingPeriodTest extends KualiTestBase {
    AccountingPeriod ap;
    public static final boolean BUDGET_ROLLOVER_IND = true;
    public static final String GUID = "123456789012345678901234567890123456";
    public static final String UNIV_FISC_PERD_CODE = "BB";
    public static Date univFiscPerdEndDate;
    public static final Integer UNIV_FISC_YEAR = new Integer(2005);
    public static final String UNIV_FISC_PRD_NAME = "JAN. 1776";
    public static final String UNIV_FISC_PRD_STATUS_CODE = "C";
    public static final Long VER_NBR = new Long(1);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        univFiscPerdEndDate = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        ap = new AccountingPeriod();
        ap.setBudgetRolloverIndicator(BUDGET_ROLLOVER_IND);
        ap.setObjectId(GUID);
        ap.setUniversityFiscalPeriodCode(UNIV_FISC_PERD_CODE);
        ap.setUniversityFiscalPeriodEndDate(univFiscPerdEndDate);
        ap.setUniversityFiscalPeriodName(UNIV_FISC_PRD_NAME);
        ap.setUniversityFiscalPeriodStatusCode(UNIV_FISC_PRD_STATUS_CODE);
        ap.setUniversityFiscalYear(UNIV_FISC_YEAR);
        ap.setVersionNumber(VER_NBR);
    }

    public void testAccountingPeriodPojo() {
        assertEquals(BUDGET_ROLLOVER_IND, ap.isBudgetRolloverIndicator());
        assertEquals(GUID, ap.getObjectId());
        assertEquals(UNIV_FISC_PERD_CODE, ap.getUniversityFiscalPeriodCode());
        assertEquals(univFiscPerdEndDate, ap.getUniversityFiscalPeriodEndDate());
        assertEquals(UNIV_FISC_PRD_NAME, ap.getUniversityFiscalPeriodName());
        assertEquals(UNIV_FISC_PRD_STATUS_CODE, ap.getUniversityFiscalPeriodStatusCode());
        assertEquals(UNIV_FISC_YEAR, ap.getUniversityFiscalYear());
        assertEquals(VER_NBR, ap.getVersionNumber());
    }

}
