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

/**
 * Methods needed for Contracts & Grants modules to interact with an Accounts Receivable module to perform Contracts & Grants Billing
 * operations.
 * NOTE: If you are not using Contracts & Grants Billing functionality, then there is no reason for your institution to implement this service;
 * just use the provided no op implementation
 */
public interface AccountsReceivableModuleBillingService {
    /**
     * This method gets the award billed to date amount using ContractsGrantsInvoiceDocumentService
     *
     * @param roposalNumber
     * @return
     */
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber);

    /**
     * This method calculates total payments to date by Award using ContractsGrantsInvoiceDocumentService
     *
     * @param proposalNumber
     * @return
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber);

    /**
     * This method returns a new instance of the MilestoneSchedule class.
     *
     * @return new MilestoneSchedule instance
     */
    public AccountsReceivableMilestoneSchedule getMilestoneSchedule();

    /**
     * This method returns a new instance of the PredeterminedBillingSchedule class.
     *
     * @return new PredeterminedBillingSchedule instance
     */
    public AccountsReceivablePredeterminedBillingSchedule getPredeterminedBillingSchedule();

    /**
     * Checks to see if the award corresponding to the passed in proposalNumber has a
     * MilestoneSchedule associated with it.
     *
     * @param proposalNumber proposalNumber for the Award use as key to look for MilestoneSchedule
     * @return true if there is an active MilestoneSchedule for this proposalNumber, false otherwise
     */
    public boolean hasMilestoneSchedule(Long proposalNumber);

    /**
     * Checks to see if the award corresponding to the passed in proposalNumber has a
     * PredeterminedBillingSchedule associated with it.
     *
     * @param proposalNumber proposalNumber for the Award use as key to look for PredeterminedBillingSchedule
     * @return true if there is an active PredeterminedBillingSchedule for this proposalNumber, false otherwise
     */
    public boolean hasPredeterminedBillingSchedule(Long proposalNumber);

    /**
     * Checks to see if the award corresponding to the passed in proposalNumber has active
     * Milestones associated with it.
     *
     * @param proposalNumber proposalNumber for the Award use as key to look for MilestoneSchedule
     * @return true if there is at least one active Milestone for this proposalNumber, false otherwise
     */
    public boolean hasActiveMilestones(Long proposalNumber);

    /**
     * Checks to see if the award corresponding to the passed in proposalNumber has active
     * Bills associated with it.
     *
     * @param proposalNumber proposalNumber for the Award use as key to look for PredeterminedBillingSchedule
     * @return true if there is at least one active Bill for this proposalNumber, false otherwise
     */
    public boolean hasActiveBills(Long proposalNumber);

    /**
     * Calculate the lastBilledDate for the Award based on it's AwardAccounts
     *
     * @param award the Award used to calculate lastBilledDate
     * @return the lastBilledDate
     */
    public Date getLastBilledDate(ContractsAndGrantsBillingAward award);

    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     *
     * @return errorString
     */
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award);

    /**
     * Gets the Contracts & Grants Invoice Document Type
     *
     * @return Contracts & Grants Invoice Document Type
     */
    public String getContractsGrantsInvoiceDocumentType();

    /**
     * Determines whether the CG and Billing Enhancements are on from the system parameters
     *
     * @return true if Contracts & Grants Billing enhancement is enabled
     */
    public boolean isContractsGrantsBillingEnhancementActive();
}
