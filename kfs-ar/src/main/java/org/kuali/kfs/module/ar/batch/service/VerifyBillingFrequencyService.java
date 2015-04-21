/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.batch.service;

import java.sql.Date;
import java.util.ArrayList;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.BillingPeriod;

/**
 * Interface class for Billing Frequency validation.
 */
public interface VerifyBillingFrequencyService {

    /**
     * This method checks if the award is within the grace period.
     *
     * @param award ContractsAndGrantsBillingAward to validate billing frequency for
     * @return true if valid else false.
     */
    public boolean validateBillingFrequency(ContractsAndGrantsBillingAward award);

    /**
     * This method checks if the award account is within the grace period.
     *
     * @param award ContractsAndGrantsBillingAward to validate billing frequency for
     * @param award ContractsAndGrantsBillingAwardAccount to validate billing frequency for
     * @return true if valid else false.
     */
    public boolean validateBillingFrequency(ContractsAndGrantsBillingAward award, ContractsAndGrantsBillingAwardAccount awardAccount);

    /**
     * This method returns the start and end date of previous billing period.
     *
     * @param currPeriod accounting period used for calculations (typically the current period)
     * @param award ContractsAndGrantsBillingAward used to get dates and billing frequency for calculations
     * @return Date array containing start date and end date of previous billing period
     */
    public BillingPeriod determineBillingPeriodPriorTo(AccountingPeriod currPeriod, ContractsAndGrantsBillingAward award);

    /**
     * This method gets a sorted list of end dates of each period of current fiscal year.
     *
     * @param currPeriod accounting period used to obtain period end dates for the current fiscal year (typically the current period)
     * @return ArrayList of period end dates
     */
    public ArrayList<Date> getSortedListOfPeriodEndDatesOfCurrentFiscalYear(AccountingPeriod currPeriod);

}
