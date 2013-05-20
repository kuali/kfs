/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * @author yozkan
 *
 */
public class ContractsGrantsInvoiceReport extends TransientBusinessObjectBase {

    private String documentNumber;
    private Long proposalNumber;
    private String invoiceType;
    private Date invoiceDate;
    private Date invoiceDueDate;
    private String openInvoiceIndicator;
    private String customerNumber;
    private String customerName;
    private KualiDecimal invoiceAmount;
    private KualiDecimal paymentAmount;
    private KualiDecimal remainingAmount;
    private Long ageInDays;
    private TransientContractsGrantsAttributes dummyBusinessObject;

    private ContractAndGrantsProposal proposal;
    private ContractsGrantsInvoiceDocument invoiceDocument;
    private Customer customer;



    /**
     * Gets the invoiceDocument attribute.
     *
     * @return Returns the invoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        return invoiceDocument;
    }


    /**
     * Sets the invoiceDocument attribute value.
     *
     * @param invoiceDocument The invoiceDocument to set.
     */
    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }


    /**
     * Gets the customer attribute.
     *
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }


    /**
     * Sets the customer attribute value.
     *
     * @param customer The customer to set.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    
    /**
     * Gets the proposal attribute.
     *
     * @return Returns the proposal. 
     */
    public ContractAndGrantsProposal getProposal() {
        return proposal = (ContractAndGrantsProposal) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractAndGrantsProposal.class).retrieveExternalizableBusinessObjectIfNecessary(this, proposal, "proposal");
    }

    
    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber. 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber. 
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the invoiceType attribute.
     *
     * @return Returns the invoiceType.
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * Sets the invoiceType attribute value.
     *
     * @param invoiceType The invoiceType to set.
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * Gets the invoiceDate attribute.
     *
     * @return Returns the invoiceDate. 
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoiceDate attribute value.
     *
     * @param invoiceDate The invoiceDate to set.
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the invoiceDueDate attribute.
     *
     * @return Returns the invoiceDueDate. 
     */
    public Date getInvoiceDueDate() {
        return invoiceDueDate;
    }

    /**
     * Sets the invoiceDueDate attribute value.
     *
     * @param invoiceDueDate The invoiceDueDate to set.
     */
    public void setInvoiceDueDate(Date invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    /**
     * Gets the openInvoiceIndicator attribute.
     *
     * @return Returns the openInvoiceIndicator. 
     */
    public String getOpenInvoiceIndicator() {
        return openInvoiceIndicator;
    }

    /**
     * Sets the openInvoiceIndicator attribute value.
     *
     * @param openInvoiceIndicator The openInvoiceIndicator to set.
     */
    public void setOpenInvoiceIndicator(String openInvoiceIndicator) {
        this.openInvoiceIndicator = openInvoiceIndicator;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber. 
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the customerName attribute.
     *
     * @return Returns the customerName 
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     *
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the invoiceAmount attribute.
     *
     * @return Returns the invoiceAmount. 
     */
    public KualiDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute value.
     *
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(KualiDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the paymentAmount attribute.
     *
     * @return Returns the paymentAmount. 
     */
    public KualiDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the paymentAmount attribute value.
     *
     * @param paymentAmount The paymentAmount to set.
     */
    public void setPaymentAmount(KualiDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Gets the remainingAmount attribute.
     *
     * @return Returns the remainingAmount. 
     */
    public KualiDecimal getRemainingAmount() {
        return remainingAmount;
    }

    /**
     * Sets the remainingAmount attribute value.
     *
     * @param remainingAmount The remainingAmount to set.
     */
    public void setRemainingAmount(KualiDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    /**
     * Gets the ageInDays attribute.
     *
     * @return Returns the ageInDays. 
     */
    public Long getAgeInDays() {
        return ageInDays;
    }

    /**
     * Sets the ageInDays attribute value.
     *
     * @param ageInDays The ageInDays to set.
     */
    public void setAgeInDays(Long ageInDays) {
        this.ageInDays = ageInDays;
    }

    /**
     * Gets the dummyBusinessObject attribute.
     *
     * @return Returns the dummyBusinessObject. 
     */
    public TransientContractsGrantsAttributes getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     *
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    public void setDummyBusinessObject(TransientContractsGrantsAttributes dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    /**
     * Sets the proposal attribute value.
     *
     * @param proposal The proposal to set.
     */
    public void setProposal(ContractAndGrantsProposal proposal) {
        this.proposal = proposal;
    }

    
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
        m.put("invoiceType", this.invoiceType);
        m.put("openInvoiceIndicator", this.openInvoiceIndicator);
        m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber);
        m.put("customerName", this.customerName);
        if (this.invoiceAmount != null) {
            m.put("invoiceAmount", this.invoiceAmount.toString());
        }
        if (this.paymentAmount != null) {
            m.put("paymentAmount", this.paymentAmount.toString());
        }
        if (this.remainingAmount != null) {
            m.put("remainingAmount", this.remainingAmount.toString());
        }
        if (this.ageInDays != null) {
            m.put("ageInDays", this.ageInDays.toString());
        }
        return m;
    }

}
