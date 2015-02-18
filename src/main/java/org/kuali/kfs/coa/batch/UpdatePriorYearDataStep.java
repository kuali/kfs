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
package org.kuali.kfs.coa.batch;

import java.util.Date;

import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.coa.service.PriorYearOrganizationService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This class updates the prior year data in the prior year account and prior year org tables to set it to the new year This is
 * typically run at year end
 */
public class UpdatePriorYearDataStep extends AbstractStep {

    private PriorYearAccountService priorYearAccountService;
    private PriorYearOrganizationService priorYearOrganizationService;

    /**
     * Executes the table updates when
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        priorYearAccountService.populatePriorYearAccountsFromCurrent();
        priorYearOrganizationService.populatePriorYearOrganizationsFromCurrent();
        return true;
    }

    /**
     * Service setter for Spring injection
     * 
     * @param priorYearAccountService
     */
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    /**
     * Service setter for Spring injection
     * 
     * @param priorYearOrganizationService
     */
    public void setPriorYearOrganizationService(PriorYearOrganizationService priorYearOrganizationService) {
        this.priorYearOrganizationService = priorYearOrganizationService;
    }

}
