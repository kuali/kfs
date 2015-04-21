package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;

public class BillingPeriod {

    protected Date startDate;

    protected Date endDate;

    public BillingPeriod(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isValid() {
        return getStartDate().before(getEndDate());
    }

}
