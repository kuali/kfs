/*
 * Copyright 2014 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.integration.ar;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class AccountsReceivableModuleBillingServiceNoOp implements AccountsReceivableModuleBillingService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableModuleBillingServiceNoOp.class);

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAwardBilledToDateByProposalNumber(java.lang.Long)
     */
    @Override
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber) {
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
