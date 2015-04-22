package org.kuali.kfs.module.ar.businessobject;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.module.ar.ArConstants;

import java.sql.Date;

public abstract class BillingPeriod {

    protected Date startDate;
    protected Date endDate;
    protected boolean billable;
    protected final AccountingPeriodService accountingPeriodService;
    protected final String billingFrequency;
    protected final Date awardStartDate;
    protected final Date currentDate;
    protected final Date lastBilledDate;

    protected BillingPeriod(String billingFrequency, Date awardStartDate, Date currentDate, Date lastBilledDate, AccountingPeriodService accountingPeriodService) {
        this.awardStartDate = awardStartDate;
        this.lastBilledDate = lastBilledDate;
        this.accountingPeriodService = accountingPeriodService;
        this.billingFrequency = billingFrequency;
        this.currentDate = currentDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public static BillingPeriod determineBillingPeriodPriorTo(Date awardStartDate, Date currentDate, Date lastBilledDate, String billingFrequency, AccountingPeriodService accountingPeriodService) {
        BillingPeriod billingPeriod;
        if (StringUtils.equals(billingFrequency, ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
            billingPeriod = new LetterOfCreditBillingPeriod(billingFrequency, awardStartDate, currentDate, lastBilledDate, accountingPeriodService);
        } else {
            billingPeriod = new TimeBasedBillingPeriod(billingFrequency, awardStartDate, currentDate, lastBilledDate, accountingPeriodService);
        }
        billingPeriod.billable = billingPeriod.canThisBeBilled();
        if (billingPeriod.billable) {
            billingPeriod.startDate = billingPeriod.determineStartDate();
            billingPeriod.endDate = billingPeriod.determineEndDateByFrequency();
        }

        return billingPeriod;
    }

    protected abstract Date determineEndDateByFrequency();

    protected AccountingPeriod findAccountingPeriodBy(Date date) {
        return accountingPeriodService.getByDate(date);
    }


    protected boolean canThisBeBilled() {
        if (awardStartDate.after(currentDate)) {
            return false; // do not bill future awards!
        }

        if (lastBilledDate == null) {
            return true;
        }

        return canThisBeBilledByBillingFrequency();
    }

    protected abstract boolean canThisBeBilledByBillingFrequency();

    protected Date determineStartDate() {
        if (lastBilledDate == null) {
            return awardStartDate;
        }
        return determineStartDateByFrequency();
    }

    protected abstract Date determineStartDateByFrequency();

    protected Date calculatePreviousDate(Date date) {
        return new Date(DateUtils.addDays(date, -1).getTime());
    }

    protected Date calculateNextDay(Date date) {
        return new Date(DateUtils.addDays(date, 1).getTime());
    }

    public boolean isBillable() {
        return billable;
    }

}
