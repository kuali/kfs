package org.kuali.kfs.module.ar.businessobject;


import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
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
        if (lastBilledDate == null) {
            billingPeriod.startDate = awardStartDate;
        } else {
            billingPeriod.startDate = lastBilledDate;
        }

        billingPeriod.endDate = calculatePreviousDate(currentDate);
        return billingPeriod;
    }

    protected Date calculatePreviousDate(Date currentDate) {
        return new Date(DateUtils.addDays(currentDate, -1).getTime());
    }
}
