package org.kuali.kfs.module.ar.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.module.ar.ArConstants;

import java.sql.Date;

public class TimeBasedBillingPeriod extends BillingPeriod {
    public TimeBasedBillingPeriod(String billingFrequency, Date awardStartDate, Date currentDate, Date lastBilledDate, AccountingPeriodService accountingPeriodService) {
        super(billingFrequency, awardStartDate, currentDate, lastBilledDate, accountingPeriodService);
    }

    @Override
    protected Date determineEndDateByFrequency() {
        final AccountingPeriod accountingPeriod = findPreviousAccountingPeriod(currentDate);
        return accountingPeriod.getUniversityFiscalPeriodEndDate();
    }

    protected Integer calculatePreviousPeriodByFrequency(Integer currentAccountingPeriodCode, int periodsInBillingFrequency) {
        Integer previousAccountingPeriodCode;
        final int subAmt = (currentAccountingPeriodCode % periodsInBillingFrequency) == 0 ? periodsInBillingFrequency : currentAccountingPeriodCode % periodsInBillingFrequency;

        previousAccountingPeriodCode = currentAccountingPeriodCode - subAmt;
        return previousAccountingPeriodCode;
    }

    @Override
    protected boolean canThisBeBilledByBillingFrequency() {
        if (StringUtils.equals(billingFrequency, ArConstants.ANNUALLY_BILLING_SCHEDULE_CODE) && accountingPeriodService.getByDate(lastBilledDate).getUniversityFiscalYear() >= accountingPeriodService.getByDate(currentDate).getUniversityFiscalYear()) {
            return false;
        } else if (StringUtils.equals(findPreviousAccountingPeriod(currentDate).getUniversityFiscalPeriodCode(), findPreviousAccountingPeriod(lastBilledDate).getUniversityFiscalPeriodCode()) &&
                accountingPeriodService.getByDate(lastBilledDate).getUniversityFiscalYear().equals(accountingPeriodService.getByDate(currentDate).getUniversityFiscalYear())) {
                return false;
        }

        return true;
    }

    @Override
    protected Date determineStartDateByFrequency() {
        AccountingPeriod lastBilledDateAccountingPeriod = findPreviousAccountingPeriod(lastBilledDate);
        return calculateNextDay(lastBilledDateAccountingPeriod.getUniversityFiscalPeriodEndDate());
    }

    protected AccountingPeriod findPreviousAccountingPeriod(final Date date) {
        final AccountingPeriod currentAccountingPeriod = findAccountingPeriodBy(date);
        final Integer currentAccountingPeriodCode = Integer.parseInt(currentAccountingPeriod.getUniversityFiscalPeriodCode());
        Integer previousAccountingPeriodCode;
        previousAccountingPeriodCode = findPreviousAccountingPeriodCode(currentAccountingPeriodCode);

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

    protected Integer findPreviousAccountingPeriodCode(Integer currentAccountingPeriodCode) {
        Integer previousAccountingPeriodCode;
        if (StringUtils.equals(billingFrequency, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE) ||
                StringUtils.equals(billingFrequency, ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) ||
                StringUtils.equals(billingFrequency, ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            previousAccountingPeriodCode = calculatePreviousPeriodByFrequency(currentAccountingPeriodCode, 1);
        } else if (StringUtils.equals(billingFrequency, ArConstants.QUATERLY_BILLING_SCHEDULE_CODE)) {
            previousAccountingPeriodCode = calculatePreviousPeriodByFrequency(currentAccountingPeriodCode, 3);
        } else if (StringUtils.equals(billingFrequency, ArConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE)){
            previousAccountingPeriodCode = calculatePreviousPeriodByFrequency(currentAccountingPeriodCode, 6);
        } else {
            previousAccountingPeriodCode = calculatePreviousPeriodByFrequency(currentAccountingPeriodCode, 12);
        }
        return previousAccountingPeriodCode;
    }
}
