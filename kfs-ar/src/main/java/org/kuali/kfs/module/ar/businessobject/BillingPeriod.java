package org.kuali.kfs.module.ar.businessobject;


import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;

import java.sql.Date;

public class BillingPeriod {

    private Date startDate;
    private Date endDate;
    private boolean billable;

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public static BillingPeriod determineBillingPeriodPriorTo(Date awardStartDate, Date currentDate, Date lastBilledDate, String billingFrequency) {
        BillingPeriod billingPeriod = new BillingPeriod();
        billingPeriod.billable = billingPeriod.canThisBeBilled(lastBilledDate, currentDate);
        if (billingPeriod.billable) {
            billingPeriod.startDate = billingPeriod.determineStartDate(awardStartDate, lastBilledDate);
            if (billingFrequency.equals(ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
                billingPeriod.endDate = billingPeriod.calculatePreviousDate(currentDate);
            } else {
                billingPeriod.endDate = billingPeriod.findEndDate(currentDate);
            }
        }

        return billingPeriod;
    }

    protected Date findEndDate(Date currentDate) {

        return Date.valueOf("2015-03-31");
    }

    protected boolean canThisBeBilled(Date lastBilledDate, Date currentDate) {
        if (lastBilledDate == null) {
            return true;
        }

        if (KfsDateUtils.isSameDay(currentDate, lastBilledDate) ||
                KfsDateUtils.isSameDay(lastBilledDate, calculatePreviousDate(currentDate))) {
            return false;
        }

        return true;
    }

    protected Date determineStartDate(Date awardStartDate, Date lastBilledDate) {
        if (lastBilledDate == null) {
            return awardStartDate;
        }
        return lastBilledDate;
    }

    protected Date calculatePreviousDate(Date currentDate) {
        return new Date(DateUtils.addDays(currentDate, -1).getTime());
    }

    public boolean isBillable() {
        return billable;
    }
}
