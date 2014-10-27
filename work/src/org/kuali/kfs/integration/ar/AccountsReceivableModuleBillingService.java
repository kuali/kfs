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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Methods needed for Contracts and Grants modules to interact with an Accounts Receivable module to perform Contracts & Grants Billing
 * operations.
 * NOTE: If you are not using Contracts and Grants Billing functionality, then there is no reason for your institution to implement this service;
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
     * @return true if Contracts and Grants Billing enhancement is enabled
     */
    public boolean isContractsGrantsBillingEnhancementActive();

    /**
     * Obtain list Kuali Coeus Award Status Codes that indicate the award should not be invoiced
     * from parameter. Used by Kuali Coeus module when Contracts and Grants Billing is enabled.
     *
     * @return list of award status codes
     */
    public Collection<String> getDoNotInvoiceStatuses();
}