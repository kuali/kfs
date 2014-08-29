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
    * Check if Preferred Billing Frequency is set correctly.
    *
    * @param award
    * @return False if preferred billing schedule is set as perdetermined billing schedule or milestone billing schedule, and award
    *         has no award account or more than 1 award accounts assigned.
    */
   public boolean isPreferredBillingFrequencySetCorrectly(ContractsAndGrantsBillingAward award);

   /**
    * Check if the value of PreferredBillingFrequency is in the value set.
    *
    * @param award
    * @return
    */
   public boolean isValueOfPreferredBillingFrequencyValid(ContractsAndGrantsBillingAward award);

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
   public boolean owningAgencyHasNoCustomerRecord(ContractsAndGrantsBillingAward award);

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

   /**
    * This method checks if there is atleast one AR Invoice Account present when the GLPE is 3.
    *
    * @param award
    * @return
    */
   public boolean hasARInvoiceAccountAssigned(ContractsAndGrantsBillingAward award);

   /**
    * This method checks if the Offset Definition is setup for the Chart Code from the award accounts.
    *
    * @param award
    * @return
    */
   public boolean isOffsetDefinitionSetupForInvoicing(ContractsAndGrantsBillingAward award);
}