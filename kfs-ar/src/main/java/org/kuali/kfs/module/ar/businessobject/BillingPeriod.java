package org.kuali.kfs.module.ar.businessobject;


import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.sys.util.KfsDateUtils;

import java.sql.Date;

public class BillingPeriod {

    private Date startDate;
    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public BillingPeriod determineBillingPeriodPriorTo(Date awardStartDate, Date currentDate, Date lastBilledDate) {
        BillingPeriod billingPeriod = new BillingPeriod();
        if (canThisBeBilled(lastBilledDate, currentDate)) {
            billingPeriod.startDate = determineStartDate(awardStartDate, lastBilledDate);
            billingPeriod.endDate = calculatePreviousDate(currentDate);
        }

        return billingPeriod;
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
}
