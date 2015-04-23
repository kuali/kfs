package org.kuali.kfs.module.ar.businessobject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.module.ar.ArConstants;

import java.sql.Date;

public class BillingPeriodTest {

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
    public void testDetermineBillingPeriodPriorTo_Monthly_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_nullLastBilled_2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-03-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-02-28";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    protected void verifyBillingPeriodPriorTo(String awardStartDate, String currentDate, String lastBilledDate, String expectedBillingPeriodStart, String expectedBillingPeriodEnd, boolean expectedBillable, String billingFrequencyCode) {
        Date lastBilledDateAsDate = nullSafeDateFromString(lastBilledDate);
        BillingPeriod priorBillingPeriod = BillingPeriod.determineBillingPeriodPriorTo(Date.valueOf(awardStartDate), Date.valueOf(currentDate), lastBilledDateAsDate, billingFrequencyCode);

        Date expectedStartDate = nullSafeDateFromString(expectedBillingPeriodStart);
        Assert.assertEquals("Billing period start wasn't what we thought it was going to be", expectedStartDate, priorBillingPeriod.getStartDate());
        Date expectedEndDate = nullSafeDateFromString(expectedBillingPeriodEnd);
        Assert.assertEquals("Billing period end wasn't what we thought it was going to be", expectedEndDate, priorBillingPeriod.getEndDate());
        Assert.assertEquals("Billing period billable wasn't what we thought it was going to be", expectedBillable, priorBillingPeriod.isBillable());
    }

    private Date nullSafeDateFromString(String date) {
        return (StringUtils.isBlank(date)) ? null : Date.valueOf(date);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsNull() {
        boolean expectedCanThisBeBilled = true;

        String lastBilledDate = null;
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsToday() {
        boolean expectedCanThisBeBilled = false;

        String lastBilledDate = "2015-04-21";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsYesterday() {
        boolean expectedCanThisBeBilled = false;

        String lastBilledDate = "2015-04-20";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsBeforeYesterday() {
        boolean expectedCanThisBeBilled = true;

        String lastBilledDate = "2015-04-19";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    protected void validateCanThisBeBilled(boolean expectedCanThisBeBilled, String lastBilledDate, String currentDate) {
        BillingPeriod billingPeriod = new BillingPeriod();
        Assert.assertEquals(expectedCanThisBeBilled, billingPeriod.canThisBeBilled(nullSafeDateFromString(lastBilledDate), Date.valueOf(currentDate)));
    }


}
