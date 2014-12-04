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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;

/**
 * Methods which test various aspects of awards, verifying that the award can be used to create a contracts & grants invoice
 */
public interface ContractsGrantsBillingAwardVerificationService {

   /**
    * Check if Billing Frequency Code is set correctly.
    *
    * @param award
    * @return False if billing frequency is set as predetermined billing schedule or milestone billing schedule, and award
    *         has no award account or more than 1 award accounts assigned.
    */
   public boolean isBillingFrequencySetCorrectly(ContractsAndGrantsBillingAward award);

   /**
    * Check if the value of Billing Frequency Code is in the value set.
    *
    * @param award
    * @return
    */
   public boolean isValueOfBillingFrequencyValid(ContractsAndGrantsBillingAward award);

   /**
    * Check if the final Invoice for all accounts in the invoice have already been built.
    *
    * @param award
    * @return
    */
   public boolean isAwardFinalInvoiceAlreadyBuilt(ContractsAndGrantsBillingAward award);

   /**
    * Checks if the award has valid milestones to invoice.
    *
    * @param award
    * @return true if has valid milestones to invoice. false if not.
    */
   public boolean hasMilestonesToInvoice(ContractsAndGrantsBillingAward award);

   /**
    * Checks if the award has valid milestones to invoice.
    *
    * @param award
    * @return true if has valid milestones to invoice. false if not.
    */
   public boolean hasBillsToInvoice(ContractsAndGrantsBillingAward award);

   /**
    * Check if agency owning award has no customer record
    *
    * @param award
    * @return
    */
   public boolean owningAgencyHasCustomerRecord(ContractsAndGrantsBillingAward award);

   /**
    * This method checks if the System Information and ORganization Accounting Default are setup for the Chart Code and Org Code
    * from the award accounts.
    *
    * @param award
    * @return
    */
   public boolean isChartAndOrgSetupForInvoicing(ContractsAndGrantsBillingAward award);

   /**
    * this method checks If all accounts of award has invoices in progress.
    *
    * @param award
    * @return
    */
   public boolean isInvoiceInProgress(ContractsAndGrantsBillingAward award);
}
