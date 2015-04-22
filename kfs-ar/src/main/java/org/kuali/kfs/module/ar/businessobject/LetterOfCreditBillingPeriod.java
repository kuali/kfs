package org.kuali.kfs.module.ar.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;

import java.sql.Date;

public class LetterOfCreditBillingPeriod extends BillingPeriod {
    public LetterOfCreditBillingPeriod(String billingFrequency, Date awardStartDate, Date currentDate, Date lastBilledDate, AccountingPeriodService accountingPeriodService) {
        super(billingFrequency, awardStartDate, currentDate, lastBilledDate, accountingPeriodService);
    }

    @Override
    protected Date determineEndDateByFrequency() {
        return calculatePreviousDate(currentDate);
    }

    @Override
    protected boolean canThisBeBilledByBillingFrequency() {
        return (!KfsDateUtils.isSameDay(currentDate, lastBilledDate) && !KfsDateUtils.isSameDay(lastBilledDate, calculatePreviousDate(currentDate)));
    }

    @Override
    protected Date determineStartDateByFrequency() {
        return lastBilledDate;
    }

}
