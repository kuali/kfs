package org.kuali.kfs.module.ar.businessobject;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
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

    public static BillingPeriod determineBillingPeriodPriorTo(Date awardStartDate, Date currentDate, Date lastBilledDate, String billingFrequency, AccountingPeriodService accountingPeriodService) {
        BillingPeriod billingPeriod = new BillingPeriod();
        billingPeriod.billable = billingPeriod.canThisBeBilled(lastBilledDate, currentDate);
        if (billingPeriod.billable) {
            billingPeriod.startDate = billingPeriod.determineStartDate(awardStartDate, lastBilledDate, billingFrequency, accountingPeriodService);
            if (billingFrequency.equals(ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
                billingPeriod.endDate = billingPeriod.calculatePreviousDate(currentDate);
            } else {
                billingPeriod.endDate = billingPeriod.findEndDate(currentDate, accountingPeriodService);
            }
        }

        return billingPeriod;
    }

    protected Date findEndDate(Date currentDate, AccountingPeriodService accountingPeriodService) {
        final AccountingPeriod accountingPeriod = findPreviousAccountingPeriod(currentDate, accountingPeriodService);
        return accountingPeriod.getUniversityFiscalPeriodEndDate();
    }

    protected AccountingPeriod findPreviousAccountingPeriod(final Date currentDate, AccountingPeriodService accountingPeriodService) {
        final AccountingPeriod currentAccountingPeriod = findAccountingPeriodBy(currentDate, accountingPeriodService);
        final Integer currentFiscalYear = currentAccountingPeriod.getUniversityFiscalYear();
        final Integer currentAccountingPeriodCode = Integer.parseInt(currentAccountingPeriod.getUniversityFiscalPeriodCode());
        final Integer previousAccountingPeriodCode = (currentAccountingPeriodCode.intValue() - 1);
        //TODO: fix this suspect hack.
        final AccountingPeriod previousAccountingPeriod = accountingPeriodService.getByPeriod("0"+previousAccountingPeriodCode, currentFiscalYear);

        return previousAccountingPeriod;
    }

    protected AccountingPeriod findAccountingPeriodBy(final Date currentDate, AccountingPeriodService accountingPeriodService) {
        return accountingPeriodService.getByDate(currentDate);
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

    protected Date determineStartDate(Date awardStartDate, Date lastBilledDate, String billingFrequency, AccountingPeriodService accountingPeriodService) {
        if (lastBilledDate == null) {
            return awardStartDate;
        }
        if (StringUtils.equals(billingFrequency, ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
            return lastBilledDate;
        }
        AccountingPeriod lastBilledDateAccountingPeriod = findPreviousAccountingPeriod(lastBilledDate, accountingPeriodService);
        return calculateNextDay(lastBilledDateAccountingPeriod.getUniversityFiscalPeriodEndDate());
    }

    protected Date calculatePreviousDate(Date currentDate) {
        return new Date(DateUtils.addDays(currentDate, -1).getTime());
    }

    protected Date calculateNextDay(Date currentDate) {
        return new Date(DateUtils.addDays(currentDate, 1).getTime());
    }

    public boolean isBillable() {
        return billable;
    }

}
