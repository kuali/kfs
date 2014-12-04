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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the VerifyBillingFrequencyService
 */
@ConfigureContext(session = khuntley)
public class VerifyBillingFrequencyServiceTest extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    /**
     * This method test the validateBillingFrequency functionality. To make it flexible to test all the billing frequencies with
     * various billing periods, this test method call the internal methods and tests it rather testing it as a whole.
     */
    public void testValidateBillingFrequency() {
        ContractsAndGrantsModuleBillingService ModuleBillingService = SpringContext.getBean(ContractsAndGrantsModuleBillingService.class);


        VerifyBillingFrequencyService verifyBillingFrequencyService = SpringContext.getBean(VerifyBillingFrequencyService.class);
        boolean valid = false;
        Date testEndDay = null;
        Date testStartDay = null;
        // To test all billing frequencies with award last billed date null

        // CASE 1 - award last billed Date is null.


        Date date = Date.valueOf("2011-10-31");
        AccountingPeriod currPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);
        // Now to set currentPeriod based on the billing frequency we would want to test.
        // 1. MONTHLY / Milestone/Predetermined.
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();


        testEndDay = Date.valueOf("2011-09-30");
        testStartDay = Date.valueOf("2011-01-01");

        Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        award = ARAwardFixture.CG_AWARD_MILESTONE_BILLED_DATE_NULL.createAward();
        testEndDay = Date.valueOf("2011-09-30");
        testStartDay = Date.valueOf("2011-01-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);


        award = ARAwardFixture.CG_AWARD_PREDETERMINED_BILLED_DATE_NULL.createAward();

        testEndDay = Date.valueOf("2011-09-30");
        testStartDay = Date.valueOf("2011-01-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 2. QUARTERLY


        award = ARAwardFixture.CG_AWARD_QUAR_BILLED_DATE_NULL.createAward();

        testEndDay = Date.valueOf("2011-09-30");
        testStartDay = Date.valueOf("2011-01-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 3. SEMI-ANNUAL

        award = ARAwardFixture.CG_AWARD_SEMI_ANN_BILLED_DATE_NULL.createAward();
        testEndDay = Date.valueOf("2011-06-30");
        testStartDay = Date.valueOf("2011-01-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 4. ANNUALLY


        award = ARAwardFixture.CG_AWARD_ANNUAL_BILLED_DATE_NULL.createAward();
        testEndDay = Date.valueOf("2011-06-30");
        testStartDay = Date.valueOf("2011-01-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 5. LOC Billing

        award = ARAwardFixture.CG_AWARD_LOCB_BILLED_DATE_NULL.createAward();

        testStartDay = Date.valueOf("2011-01-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        assertEquals(pair[0], testStartDay);


        // CASE 2 - award last billed Date is set to appropriate period, current period will also be set to appropriate values.

        // 1. MONTHLY / Milestone/Predetermined.
        date = Date.valueOf("2011-11-01");
        currPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);


        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();

        testEndDay = Date.valueOf("2011-10-31");
        testStartDay = Date.valueOf("2011-10-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);


        award = ARAwardFixture.CG_AWARD_MILESTONE_BILLED_DATE_VALID.createAward();
        testEndDay = Date.valueOf("2011-10-31");
        testStartDay = Date.valueOf("2011-10-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);


        award = ARAwardFixture.CG_AWARD_PREDETERMINED_BILLED_DATE_VALID.createAward();

        testEndDay = Date.valueOf("2011-10-31");
        testStartDay = Date.valueOf("2011-10-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 2. QUARTERLY

        award = ARAwardFixture.CG_AWARD_QUAR_BILLED_DATE_VALID.createAward();

        testEndDay = Date.valueOf("2011-09-30");
        testStartDay = Date.valueOf("2011-07-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 3. SEMI-ANNUAL
        date = Date.valueOf("2012-01-01");
        currPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);

        award = ARAwardFixture.CG_AWARD_SEMI_ANN_BILLED_DATE_VALID.createAward();
        testEndDay = Date.valueOf("2011-12-31");
        testStartDay = Date.valueOf("2011-07-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 4. ANNUALLY


        date = Date.valueOf("2011-07-01");
        currPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);

        award = ARAwardFixture.CG_AWARD_ANNUAL_BILLED_DATE_VALID.createAward();
        testEndDay = Date.valueOf("2011-06-30");
        testStartDay = Date.valueOf("2010-07-01");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = false;
        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));


        assertEquals(pair[0], testStartDay);
        assertEquals(pair[1], testEndDay);
        assertTrue(valid);

        // 5. LOC Billing - No grace period validation involved here.


        date = Date.valueOf("2011-12-15");
        currPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(date);
        award = ARAwardFixture.CG_AWARD_LOCB_BILLED_DATE_VALID.createAward();

        testStartDay = Date.valueOf("2010-12-14");

        pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);

        valid = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, pair[1], pair[0], award.getLastBilledDate(), new Integer(0));
        assertEquals(pair[0], testStartDay);


    }

}
