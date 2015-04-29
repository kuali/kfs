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
    private AccountingPeriodService accountingPeriodService;

    public BillingPeriod(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public static BillingPeriod determineBillingPeriodPriorTo(Date awardStartDate, Date currentDate, Date lastBilledDate, String billingFrequency, AccountingPeriodService accountingPeriodService) {
        BillingPeriod billingPeriod = new BillingPeriod(accountingPeriodService);
        billingPeriod.billable = billingPeriod.canThisBeBilled(lastBilledDate, currentDate, billingFrequency);
        if (billingPeriod.billable) {
            billingPeriod.startDate = billingPeriod.determineStartDate(awardStartDate, lastBilledDate, billingFrequency);
            if (billingFrequency.equals(ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
                billingPeriod.endDate = billingPeriod.calculatePreviousDate(currentDate);
            } else {
                billingPeriod.endDate = billingPeriod.findEndDate(currentDate, billingFrequency);
            }
        }

        return billingPeriod;
    }

    protected Date findEndDate(Date currentDate, String billingFrequency) {
        final AccountingPeriod accountingPeriod = findPreviousAccountingPeriod(currentDate, billingFrequency);
        return accountingPeriod.getUniversityFiscalPeriodEndDate();
    }

    // TODO: fix currentDate to be more generic, it is called with lastBilledDate and currentDate
    protected AccountingPeriod findPreviousAccountingPeriod(final Date currentDate, String billingFrequency) {
        final AccountingPeriod currentAccountingPeriod = findAccountingPeriodBy(currentDate);
        final Integer currentAccountingPeriodCode = Integer.parseInt(currentAccountingPeriod.getUniversityFiscalPeriodCode());
        Integer previousAccountingPeriodCode;
        previousAccountingPeriodCode = findPreviousAccountingPeriodCode(billingFrequency, currentAccountingPeriodCode);

        Integer currentFiscalYear = currentAccountingPeriod.getUniversityFiscalYear();
        if (previousAccountingPeriodCode == 0) {
            previousAccountingPeriodCode = 12;
            currentFiscalYear -=1;
        }

        String periodCode;
        if (previousAccountingPeriodCode < 10) {
            periodCode = "0" + previousAccountingPeriodCode;
        } else {
            periodCode = "" + previousAccountingPeriodCode;
        }

        final AccountingPeriod previousAccountingPeriod = accountingPeriodService.getByPeriod(periodCode, currentFiscalYear);

        return previousAccountingPeriod;
    }

    protected Integer findPreviousAccountingPeriodCode(String billingFrequency, Integer currentAccountingPeriodCode) {
        Integer previousAccountingPeriodCode;
        if (StringUtils.equals(billingFrequency, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE)) {
            previousAccountingPeriodCode = calculatePreviousPeriodByFrequency(currentAccountingPeriodCode, 1);
        } else { // if (StringUtils.equals(billingFrequency, ArConstants.QUATERLY_BILLING_SCHEDULE_CODE)) {
            previousAccountingPeriodCode = calculatePreviousPeriodByFrequency(currentAccountingPeriodCode, 3);
        }
        return previousAccountingPeriodCode;
    }

    protected Integer calculatePreviousPeriodByFrequency(Integer currentAccountingPeriodCode, int periodsInBillingFrequency) {
        Integer previousAccountingPeriodCode;
        final int subAmt = (currentAccountingPeriodCode % periodsInBillingFrequency) == 0 ? periodsInBillingFrequency : currentAccountingPeriodCode % periodsInBillingFrequency;

        previousAccountingPeriodCode = currentAccountingPeriodCode - subAmt;
        return previousAccountingPeriodCode;
    }

    protected AccountingPeriod findAccountingPeriodBy(final Date currentDate) {
        return accountingPeriodService.getByDate(currentDate);
    }


    protected boolean canThisBeBilled(Date lastBilledDate, Date currentDate, String billingFrequency) {
        if (lastBilledDate == null) {
            return true;
        }

        if (StringUtils.equals(billingFrequency, ArConstants.LOC_BILLING_SCHEDULE_CODE) && (KfsDateUtils.isSameDay(currentDate, lastBilledDate) ||
                KfsDateUtils.isSameDay(lastBilledDate, calculatePreviousDate(currentDate)))) {
            return false;
        } else if (StringUtils.equals(billingFrequency, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE) &&
                StringUtils.equals(accountingPeriodService.getByDate(lastBilledDate).getUniversityFiscalPeriodCode(), accountingPeriodService.getByDate(currentDate).getUniversityFiscalPeriodCode()) &&
                accountingPeriodService.getByDate(lastBilledDate).getUniversityFiscalYear().equals(accountingPeriodService.getByDate(currentDate).getUniversityFiscalYear())) {
            return false;
        } else if (StringUtils.equals(billingFrequency, ArConstants.QUATERLY_BILLING_SCHEDULE_CODE) &&
                StringUtils.equals(findPreviousAccountingPeriod(currentDate, billingFrequency).getUniversityFiscalPeriodCode(), findPreviousAccountingPeriod(lastBilledDate, billingFrequency).getUniversityFiscalPeriodCode()) &&
                accountingPeriodService.getByDate(lastBilledDate).getUniversityFiscalYear().equals(accountingPeriodService.getByDate(currentDate).getUniversityFiscalYear())) {
            return false;
        }

        return true;
    }

    protected Date determineStartDate(Date awardStartDate, Date lastBilledDate, String billingFrequency) {
        if (lastBilledDate == null) {
            return awardStartDate;
        }
        if (StringUtils.equals(billingFrequency, ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
            return lastBilledDate;
        }
        AccountingPeriod lastBilledDateAccountingPeriod = findPreviousAccountingPeriod(lastBilledDate, billingFrequency);
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
