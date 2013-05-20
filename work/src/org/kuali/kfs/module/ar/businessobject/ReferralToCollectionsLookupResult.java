/*
 * Copyright 2012 The Kuali Foundation.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines an entry in the Referral To Collections Lookup Result.
 */
public class ReferralToCollectionsLookupResult extends TransientBusinessObjectBase {

    private Long proposalNumber;
    private String agencyNumber;
    private String customerNumber;
    private String customerName;
    private String accountNumber;
    private String awardDocumentNumber;
    private String invoiceDocumentNumber;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;

    private Collection<ContractsGrantsInvoiceDocument> invoices;
    private ContractsAndGrantsCGBAward award;
    private ContractsAndGrantsCGBAgency agency;
    private Customer customer;
    private Account account;

    /**
     * Default Constructor.
     */
    public ReferralToCollectionsLookupResult() {
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
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the awardTotal attribute.
     * 
     * @return Returns the awardTotal.
     */
    public KualiDecimal getAwardTotal() {
        return awardTotal;
    }


    /**
     * Sets the awardTotal attribute value.
     * 
     * @param awardTotal The awardTotal to set.
     */
    public void setAwardTotal(KualiDecimal awardTotal) {
        this.awardTotal = awardTotal;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }


    /**
     * Sets the agencyNumber attribute value.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
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
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute.
     * 
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the awardDocumentNumber attribute.
     * 
     * @return Returns the awardDocumentNumber.
     */
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    /**
     * Sets the awardDocumentNumber attribute.
     * 
     * @param awardDocumentNumber The awardDocumentNumber to set.
     */
    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }

    /**
     * Gets the invoiceDocumentNumber attribute.
     * 
     * @return Returns the invoiceDocumentNumber.
     */
    public String getInvoiceDocumentNumber() {
        return invoiceDocumentNumber;
    }

    /**
     * Sets the invoiceDocumentNumber attribute.
     * 
     * @param invoiceDocumentNumber The invoiceDocumentNumber to set.
     */
    public void setInvoiceDocumentNumber(String invoiceDocumentNumber) {
        this.invoiceDocumentNumber = invoiceDocumentNumber;
    }

    /**
     * Gets the awardBeginningDate attribute.
     * 
     * @return Returns the awardBeginningDate.
     */
    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    /**
     * Sets the awardBeginningDate attribute value.
     * 
     * @param awardBeginningDate The awardBeginningDate to set.
     */
    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    /**
     * Gets the awardEndingDate attribute.
     * 
     * @return Returns the awardEndingDate.
     */
    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    /**
     * Sets the awardEndingDate attribute value.
     * 
     * @param awardEndingDate The awardEndingDate to set.
     */
    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }

    /**
     * Gets the invoices attribute.
     * 
     * @return Returns the invoices.
     */
    public Collection<ContractsGrantsInvoiceDocument> getInvoices() {
        return invoices;
    }

    /**
     * Sets the invoices attribute.
     * 
     * @param invoices The invoices to set.
     */
    public void setInvoices(Collection<ContractsGrantsInvoiceDocument> invoices) {
        this.invoices = invoices;
    }

    /**
     * Gets the award attribute.
     * 
     * @return Returns the award.
     */
    public ContractsAndGrantsCGBAward getAward() {
        return award;
    }

    /**
     * Sets the award attribute.
     * 
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsCGBAward award) {
        this.award = award;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency.
     */
    public ContractsAndGrantsCGBAgency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     * 
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsCGBAgency agency) {
        this.agency = agency;
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
     * Sets the customer attribute.
     * 
     * @param customer The customer to set.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account object.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account attribute to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (ObjectUtils.isNotNull(this.proposalNumber)) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        if (ObjectUtils.isNotNull(this.awardBeginningDate)) {
            m.put("awardBeginningDate", this.awardBeginningDate.toString());
        }
        if (ObjectUtils.isNotNull(this.awardEndingDate)) {
            m.put("awardEndingDate", this.awardEndingDate.toString());
        }
        if (ObjectUtils.isNotNull(this.awardTotal)) {
            m.put("awardTotal", this.awardTotal.toString());
        }
        m.put(KFSPropertyConstants.AGENCY_NUMBER, this.agencyNumber);
        m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber);
        m.put(KFSPropertyConstants.CUSTOMER_NAME, this.customerName);
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.accountNumber);

        return m;
    }

    /**
     * Gets the invoice attributes to display.
     * 
     * @return The list of attributes.
     */
    public List<String> getInvoiceAttributesForDisplay() {
        List<String> invoiceAttributesForDisplay = new ArrayList<String>();
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER);
        invoiceAttributesForDisplay.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.AGE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.SOURCE_TOTAL);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.OPEN_AMOUNT);
        return invoiceAttributesForDisplay;
    }
}
