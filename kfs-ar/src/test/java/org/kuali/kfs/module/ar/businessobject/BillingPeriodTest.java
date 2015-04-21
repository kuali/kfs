package org.kuali.kfs.module.ar.businessobject;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;

public class BillingPeriodTest {

    @Test
    public void isValid() {
        Date startDate = Date.valueOf("2015-04-21");
        Date endDate = Date.valueOf("2015-04-22");
        BillingPeriod billingPeriod = new BillingPeriod(startDate, endDate);
        Assert.assertTrue(billingPeriod.isValid());
    }

    @Test
    public void isInvalid() {
        Date startDate = Date.valueOf("2015-04-21");
        Date endDate = Date.valueOf("2015-04-20");
        BillingPeriod billingPeriod = new BillingPeriod(startDate, endDate);
        Assert.assertFalse(billingPeriod.isValid());
    }

}
