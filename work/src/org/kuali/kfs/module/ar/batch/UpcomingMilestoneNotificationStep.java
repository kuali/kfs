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
package org.kuali.kfs.module.ar.batch;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.ar.batch.service.UpcomingMilestoneNotificationService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Batch step for sending ACH Advice notifications to payees receiving an ACH payment
 */
public class UpcomingMilestoneNotificationStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpcomingMilestoneNotificationStep.class);
    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected UpcomingMilestoneNotificationService upcomingMilestoneNotificationService;

    /**
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            LOG.debug("UpcomingMilestoneNotificationStep: execute() started");
            getUpcomingMilestoneNotificationService().sendNotificationsForMilestones();
        } else {
            LOG.info("Contracts & Grants Billing enhancement not turned on; therefore, not running upcomingMilestoneNotificationStep");
        }

        return true;
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    /**
     * Gets the upcomingMilestoneNotificationService attribute.
     *
     * @return Returns the upcomingMilestoneNotificationService.
     */
    public UpcomingMilestoneNotificationService getUpcomingMilestoneNotificationService() {
        return upcomingMilestoneNotificationService;
    }

    /**
     * Sets the upcomingMilestoneNotificationService attribute value.
     *
     * @param upcomingMilestoneNotificationService The upcomingMilestoneNotificationService to set.
     */
    public void setUpcomingMilestoneNotificationService(UpcomingMilestoneNotificationService upcomingMilestoneNotificationService) {
        this.upcomingMilestoneNotificationService = upcomingMilestoneNotificationService;
    }
}
