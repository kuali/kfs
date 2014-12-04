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
package org.kuali.kfs.coa.businessobject;

import java.sql.Date;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

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
    public static final boolean UNIV_FISC_PRD_ACTIVE_INDICATOR = false;
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
        ap.setActive(UNIV_FISC_PRD_ACTIVE_INDICATOR);
        ap.setUniversityFiscalYear(UNIV_FISC_YEAR);
        ap.setVersionNumber(VER_NBR);
    }

    public void testAccountingPeriodPojo() {
        assertEquals(BUDGET_ROLLOVER_IND, ap.isBudgetRolloverIndicator());
        assertEquals(GUID, ap.getObjectId());
        assertEquals(UNIV_FISC_PERD_CODE, ap.getUniversityFiscalPeriodCode());
        assertEquals(univFiscPerdEndDate, ap.getUniversityFiscalPeriodEndDate());
        assertEquals(UNIV_FISC_PRD_NAME, ap.getUniversityFiscalPeriodName());
        assertEquals(UNIV_FISC_PRD_ACTIVE_INDICATOR, ap.isActive());
        assertEquals(UNIV_FISC_YEAR, ap.getUniversityFiscalYear());
        assertEquals(VER_NBR, ap.getVersionNumber());
    }

}
