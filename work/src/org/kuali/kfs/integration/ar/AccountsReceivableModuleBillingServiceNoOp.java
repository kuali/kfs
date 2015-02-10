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
package org.kuali.kfs.integration.ar;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class AccountsReceivableModuleBillingServiceNoOp implements AccountsReceivableModuleBillingService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableModuleBillingServiceNoOp.class);

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService#getAwardBilledToDateAmountForAward(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public KualiDecimal getAwardBilledToDateAmount(ContractsAndGrantsBillingAward award) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#calculateTotalPaymentsToDateByAward(java.lang.Long)
     */
    @Override
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivableMilestoneSchedule getMilestoneSchedule() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public AccountsReceivablePredeterminedBillingSchedule getPredeterminedBillingSchedule() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public Date getLastBilledDate(ContractsAndGrantsBillingAward award) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public boolean hasPredeterminedBillingSchedule(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public boolean hasMilestoneSchedule(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public String getContractsGrantsInvoiceDocumentType() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * Note that since AR is required for CGB, returning false here results in the correct, desired behavior even
     * without attempting to obtain and return the value from the CGB configuration property. Specifying the CGB
     * enhancement config enabled property to true but disabling AR is an invalid configuration.
     *
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#isContractsGrantsBillingEnhancementActive()
     */
    @Override
    public boolean isContractsGrantsBillingEnhancementActive() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public String getDefaultDunningCampaignCode() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public String getDefaultBillingFrequency() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public String getDefaultInvoicingOption() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public boolean hasActiveMilestones(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public boolean hasActiveBills(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }


}
