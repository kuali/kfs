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
package org.kuali.kfs.module.ar.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.service.CustomerDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * The KFS AR module implementation of the AccountsReceivableModuleService
 */
public class AccountsReceivableModuleServiceImpl implements AccountsReceivableModuleService {
    protected static final String CASH_CONTROL_ELECTRONIC_PAYMENT_CLAIMING_DOCUMENT_GENERATION_STRATEGY_BEAN_NAME = "cashControlElectronicPaymentClaimingDocumentHelper";

    protected Lookupable customerLookupable;
    private CustomerDocumentService customerDocumentService;
    private ParameterService parameterService;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    /**
     * @see org.kuali.kfs.integration.service.AccountsReceivableModuleService#getAccountsReceivablePaymentClaimingStrategy()
     */
    public ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy() {
        return SpringContext.getBean(ElectronicPaymentClaimingDocumentGenerationStrategy.class, AccountsReceivableModuleServiceImpl.CASH_CONTROL_ELECTRONIC_PAYMENT_CLAIMING_DOCUMENT_GENERATION_STRATEGY_BEAN_NAME);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#addNoteToPaymentRequestDocument(java.lang.String,
     *      java.lang.String)
     */
    public void addNoteToRelatedPaymentRequestDocument(String relatedDocumentNumber, String noteText) {
        SpringContext.getBean(PaymentApplicationDocumentService.class).addNoteToRelatedPaymentRequestDocument(relatedDocumentNumber, noteText);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getProcessingOrganizationForRelatedPaymentRequestDocument(java.lang.String)
     */
    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        return SpringContext.getBean(PaymentApplicationDocumentService.class).getProcessingOrganizationForRelatedPaymentRequestDocument(relatedDocumentNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomers(java.util.Map)
     */
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues) {
        customerLookupable.setBusinessObjectClass(Customer.class);
        List<BusinessObject> results = customerLookupable.getSearchResults(fieldValues);

        Collection<AccountsReceivableCustomer> customers = new ArrayList<AccountsReceivableCustomer>();
        for (BusinessObject result : results) {
            customers.add((AccountsReceivableCustomer) result);
        }

        return customers;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomer(java.lang.String)
     */
    public AccountsReceivableCustomer findCustomer(String customerNumber) {
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(Customer.class, customerNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomerAddresses(java.util.Map)
     */
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues) {
        return SpringContext.getBean(BusinessObjectService.class).findMatching(CustomerAddress.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomerAddress(java.lang.String)
     */
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer) {
        Map<String, String> addressKey = new HashMap<String, String>();
        addressKey.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        addressKey.put(KFSPropertyConstants.CUSTOMER_ADDRESS_IDENTIFIER, customerAddressIdentifer);

        return (AccountsReceivableCustomerAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CustomerAddress.class, addressKey);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#createAndSaveCustomer(java.lang.String,
     *      org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency) This method Creates and Saves a customer when CG Agency
     *      document does to final.
     * @param description
     * @param agency
     * @return
     * @throws WorkflowException
     */

    public String createAndSaveCustomer(String description, ContractsAndGrantsCGBAgency agency) throws WorkflowException {
        return customerDocumentService.createAndSaveCustomer(description, agency);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#retrieveGLPEReceivableParameterValue() This method
     *      retrieves the value of the Parameter GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD
     * @param parameterName
     * @return
     */

    public String retrieveGLPEReceivableParameterValue() {

        String parameterValue = parameterService.getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        return parameterValue;

    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAwardBilledToDateByProposalNumber(java.lang.Long) This
     *      method gets the award billed to date using ContractsGrantsInvoiceDocumentService
     * @param roposalNumber
     * @return
     */
    public KualiDecimal getAwardBilledToDateByProposalNumber(Long proposalNumber) {
        return contractsGrantsInvoiceDocumentService.getAwardBilledToDateByProposalNumber(proposalNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#calculateTotalPaymentsToDateByAward(java.lang.Long) This
     *      method calculates total payments to date by Award using ContractsGrantsInvoiceDocumentService
     * @param proposalNumber
     * @return
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("proposalNumber", proposalNumber);

        ContractsAndGrantsCGBAward award = (ContractsAndGrantsCGBAward) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAward.class, map);

        return contractsGrantsInvoiceDocumentService.calculateTotalPaymentsToDateByAward(award);

    }

    public void setCustomerLookupable(Lookupable customerLookupable) {
        this.customerLookupable = customerLookupable;
    }

    /**
     * Sets the customerDocumentService attribute value.
     * 
     * @param customerDocumentService The customerDocumentService to set.
     */
    public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
        this.customerDocumentService = customerDocumentService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentService attribute value.
     * 
     * @param contractsGrantsInvoiceDocumentService
     */
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }


}
