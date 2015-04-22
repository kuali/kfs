package org.kuali.kfs.module.ar.businessobject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;

public class BillingPeriodTest {

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-20";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-04-19";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_nullLastBilled_2() {
        String awardStartDate = "2014-08-01";
        String currentDate = "2015-04-20";
        String expectedBillingPeriodStart = "2014-08-01";
        String expectedBillingPeriodEnd = "2015-04-19";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_nullLastBilled_3() {
        String awardStartDate = "2014-08-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-08-01";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_BillSingleDay() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-19";
        String expectedBillingPeriodStart = "2015-04-19";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd);
    }

    protected void verifyBillingPeriodPriorTo(String awardStartDate, String currentDate, String lastBilledDate, String expectedBillingPeriodStart, String expectedBillingPeriodEnd) {
        BillingPeriod billingPeriod = new BillingPeriod();
        Date lastBilledDateAsDate = (StringUtils.isBlank(lastBilledDate)) ? null : Date.valueOf(lastBilledDate);
        BillingPeriod priorBillingPeriod = billingPeriod.determineBillingPeriodPriorTo(Date.valueOf(awardStartDate), Date.valueOf(currentDate), lastBilledDateAsDate);

        Assert.assertEquals("Billing period start wasn't what we thought it was going to be", Date.valueOf(expectedBillingPeriodStart), priorBillingPeriod.getStartDate());
        Assert.assertEquals("Billing period end wasn't what we thought it was going to be", Date.valueOf(expectedBillingPeriodEnd), priorBillingPeriod.getEndDate());
    }


}
