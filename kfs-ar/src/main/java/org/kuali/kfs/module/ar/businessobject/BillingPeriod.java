package org.kuali.kfs.module.ar.businessobject;


import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;

import java.sql.Date;

public class BillingPeriod {


    public Date getStartDate() {
        return Date.valueOf("2014-07-01");
    }

    public Date getEndDate() {
        return Date.valueOf("2015-04-20");
    }

    public BillingPeriod determineBillingPeriodPriorTo(AccountingPeriod currentPeriod, ContractsAndGrantsBillingAward award) {
        return new BillingPeriod();
    }
}
