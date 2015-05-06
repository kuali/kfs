package org.kuali.kfs.module.ar.businessobject;

import org.junit.Assert;import org.junit.Test;
import org.kuali.kfs.coa.service.AccountingPeriodService;import org.kuali.kfs.module.ar.ArConstants;

public class LetterOfCreditBillingPeriodTest extends BillingPeriodTest {

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-20";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-04-19";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_nullLastBilled_2() {
        String awardStartDate = "2014-08-01";
        String currentDate = "2015-04-20";
        String expectedBillingPeriodStart = "2014-08-01";
        String expectedBillingPeriodEnd = "2015-04-19";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_BillSingleDay() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-19";
        String expectedBillingPeriodStart = "2015-04-19";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_BillSeveralMonths() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-11-15";
        String expectedBillingPeriodStart = "2014-11-15";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_BillAcrossFiscalYears() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-06-15";
        String expectedBillingPeriodStart = "2014-06-15";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_MayNotBillNow() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-20";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsNull() {
        boolean expectedCanThisBeBilled = true;

        String awardStartDate = "2015-01-01";
        String lastBilledDate = null;
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, awardStartDate, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsToday() {
        boolean expectedCanThisBeBilled = false;

        String awardStartDate = "2015-01-01";
        String lastBilledDate = "2015-04-21";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, awardStartDate, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsYesterday() {
        boolean expectedCanThisBeBilled = false;

        String awardStartDate = "2015-01-01";
        String lastBilledDate = "2015-04-20";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, awardStartDate, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsBeforeYesterday() {
        boolean expectedCanThisBeBilled = true;

        String awardStartDate = "2015-01-01";
        String lastBilledDate = "2015-04-19";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, awardStartDate, lastBilledDate, currentDate);
    }

    protected void validateCanThisBeBilled(boolean expectedCanThisBeBilled, String awardStartDate, String lastBilledDate, String currentDate) {
        AccountingPeriodService accountingPeriodService = getMockAccountingPeriodService();
        BillingPeriod billingPeriod = new LetterOfCreditBillingPeriod(ArConstants.LOC_BILLING_SCHEDULE_CODE, nullSafeDateFromString(awardStartDate), nullSafeDateFromString(currentDate), nullSafeDateFromString(lastBilledDate), accountingPeriodService);
        Assert.assertEquals(expectedCanThisBeBilled, billingPeriod.canThisBeBilled());
    }
}
