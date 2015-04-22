package org.kuali.kfs.module.ar.businessobject;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;

import java.sql.Date;

public class BillingPeriodTest {

    Date currentDate = Date.valueOf("2015-04-21");
    @Test
    public void testDetermineBillingPeriodPriorTo() {
        Date awardStartDate = Date.valueOf("2014-07-01");
        Date expectedBillingPeriodBegin = Date.valueOf("2014-07-01");
        Date expectedBillingPeriodEnd = Date.valueOf("2015-04-20");

        BillingPeriod billingPeriod = new BillingPeriod();
        AccountingPeriod currentPeriod = null;
        ContractsAndGrantsBillingAward award = null;
        BillingPeriod priorBillingPeriod = billingPeriod.determineBillingPeriodPriorTo(currentPeriod, award);

        Assert.assertEquals(expectedBillingPeriodBegin, priorBillingPeriod.getStartDate());
        Assert.assertEquals(expectedBillingPeriodEnd, priorBillingPeriod.getEndDate());
    }
}
