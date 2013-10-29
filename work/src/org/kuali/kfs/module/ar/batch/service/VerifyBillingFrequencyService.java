/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.batch.service;

import java.sql.Date;
import java.util.ArrayList;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;

/**
 * Interface class for Billing Frequency validation.
 */
public interface VerifyBillingFrequencyService {
    
    /**
     * This method checks if the award is within the grace period.
     * @param award
     * @return true if valid else false.
     */
    public boolean validatBillingFrequency(ContractsAndGrantsBillingAward award);

    /**
     * This method returns the start and end date of previous billing period.
     * @param award
     * @param currPeriod
     * @return
     */
    public Date[] getStartDateAndEndDateOfPreviousBillingPeriod(ContractsAndGrantsBillingAward award, AccountingPeriod currPeriod);
    
    /**
     * This method gets a sorted list of end dates of each period of current fiscal year.
     * @param currPeriod
     * @return
     */
    public ArrayList<Date> getSortedListOfPeriodEndDatesOfCurrentFiscalYear(AccountingPeriod currPeriod);
    /**
     * This method checks if a given moment of time is within an accounting period, or its billing frequency grace period.
     * 
     * @param today a date to check if it is within the period
     * @param periodToCheck the account period to check against
     * @return true if a given moment in time is within an accounting period or an billing frequency grace period
     */
    public boolean calculateIfWithinGracePeriod(Date today, Date previousAccountingPeriodEndDate, Date previousAccountingPeriodStartDate, Date lastBilledDate, int gracePeriodDays);

}
