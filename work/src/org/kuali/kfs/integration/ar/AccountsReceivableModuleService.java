/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Methods which allow core KFS modules to interact with the Accounts Receivable module.
 */
public interface AccountsReceivableModuleService {
    /**
     * A method that returns an implementation of the ElectronicPaymentClaimingDocumentGenerationStrategy interface which will claim
     * electronic payments for the Accounts Receivable module.
     * 
     * @return an appropriate implementation of ElectronicPaymentClaimingDocumentGenerationStrategy
     */
    public abstract ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy();

    /* Start TEM REFUND Merge */
    /**
     * When refund DV is disapproved, a note needs to be added to the related payment request document
     * 
     * @param relatedDocumentNumber - document number for the related document (dv)
     * @param noteText - text for the new note
     */
    public void addNoteToRelatedPaymentRequestDocument(String relatedDocumentNumber, String noteText);

    /**
     * Returns the processing organization associated with the payment request given by the related document number
     * 
     * @param relatedDocumentNumber - document number for the related document (dv)
     * @return Organization instance for processing org
     */
    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber);

    /**
     * Performs a search against AR customers with the given criteria
     * 
     * @param fieldValues - Map of criteria to use (field name as map key, value as map value), supports standard lookup wildcards
     * @return Collection of matching Customers
     */
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues);

    /**
     * Returns the AccountsReceivableCustomer for the given customer number
     * 
     * @param customerNumber - number of customer to find
     * @return AccountsReceivableCustomer instance with the customer information
     */
    public AccountsReceivableCustomer findCustomer(String customerNumber);

    /**
     * Performs a search against AR customer addresses with the given criteria
     * 
     * @param fieldValues - Map of criteria to use (field name as map key, value as map value), supports standard lookup wildcards
     * @return Collection of matching Customer Addresses
     */
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues);

    /**
     * Returns the AccountsReceivableCustomerAddress for the given customer address identifier
     * 
     * @param customerNumber - number of customer for address
     * @param customerAddressIdentifer - id for the customer address to find
     * @return AccountsReceivableCustomerAddress instance with the customer address information
     */
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer);

    /* End TEM REFUND Merge */

    /**
     * This method Creates and Saves a customer when CG Agency document does to final.
     * 
     * @param description
     * @param agency
     * @return customerNumber
     * @throws WorkflowException
     */
    public String createAndSaveCustomer(String description, ContractsAndGrantsCGBAgency agency) throws WorkflowException;

    /**
     * This method retrieves the value of the Parameter GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD
     * 
     * @param parameterName
     * @return
     */
    public String retrieveGLPEReceivableParameterValue();


    /**
     * This method gets the award billed to date using ContractsGrantsInvoiceDocumentService
     * 
     * @param roposalNumber
     * @return
     */
    public KualiDecimal getAwardBilledToDateByProposalNumber(Long proposalNumber);

    /**
     * This method calculates total payments to date by Award using ContractsGrantsInvoiceDocumentService
     * 
     * @param proposalNumber
     * @return
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber);
}
