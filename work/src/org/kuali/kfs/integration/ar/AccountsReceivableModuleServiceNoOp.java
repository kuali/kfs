/*
 * Copyright 2009 The Kuali Foundation
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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * No-Op class for AccountsReceivableModuleService
 */
public class AccountsReceivableModuleServiceNoOp implements AccountsReceivableModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAccountsReceivablePaymentClaimingStrategy()
     */
    public ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ElectronicPaymentClaimingDocumentGenerationStrategy() {
            public boolean userMayUseToClaim(Person claimingUser) {
                return false;
            }

            public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
                return null;
            }

            public String getClaimingDocumentWorkflowDocumentType() {
                return null;
            }

            public String getDocumentLabel() {
                return "AR NoOp Module Service";
            }

            public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
                return false;
            }

        };
    }

    /* Start TEM REFUND Merge */
    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#addNoteToRelatedPaymentRequestDocument(java.lang.String,
     *      java.lang.String)
     */
    public void addNoteToRelatedPaymentRequestDocument(String paymentRequestDocumentNumber, String noteText) {
        // do nothing
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getProcessingOrganizationForRelatedPaymentRequestDocument(java.lang.String)
     */
    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomers(java.util.Map)
     */
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues) {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomer(java.lang.String)
     */
    public AccountsReceivableCustomer findCustomer(String customerNumber) {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomerAddresses(java.util.Map)
     */
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues) {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomerAddress(java.lang.String, java.lang.String)
     */
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer) {
        return null;
    }

    /* End TEM REFUND Merge */


    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createAndSaveCustomer(java.lang.String,
     *      org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency)
     */
    public String createAndSaveCustomer(String description, ContractsAndGrantsCGBAgency agency) throws WorkflowException {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }


    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#retrieveGLPEReceivableParameterValue()
     */
    public String retrieveGLPEReceivableParameterValue() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }


    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAwardBilledToDateByProposalNumber(java.lang.Long)
     */
    public KualiDecimal getAwardBilledToDateByProposalNumber(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#calculateTotalPaymentsToDateByAward(java.lang.Long)
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;

    }
}
