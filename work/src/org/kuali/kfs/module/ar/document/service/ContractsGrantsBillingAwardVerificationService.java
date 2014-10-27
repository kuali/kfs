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