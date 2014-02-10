/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Business object for Dunning Letter Distribution Lookup Result.
 */
public class DunningLetterDistributionLookupResult extends TransientBusinessObjectBase {

    private String principalId;
    private Long proposalNumber;
    private String accountNumber;
    private String agencyNumber;
    private String customerNumber;
    private String invoiceDocumentNumber;
    private String campaignID;
    private String agingBucket;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;
    private Collection<ContractsGrantsInvoiceDocument> invoices;
    private ContractsAndGrantsAward award;
    private ContractsAndGrantsAgency agency;
    private Customer customer;
    private Chart billingChart;
    private Organization billingOrganization;
    private String billingChartCode;
    private String billingOrganizationCode;
    private Chart processingChart;
    private Organization processingOrganization;
    private String processingChartCode;
    private String processingOrganizationCode;

    private Person collector;
    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;

    /**
     * Default constructor.
     */
    public DunningLetterDistributionLookupResult() {

    }


    public Chart getBillingChart() {
        return billingChart;
    }


    public void setBillingChart(Chart chart) {
        this.billingChart = chart;
    }


    public Organization getBillingOrganization() {
        return billingOrganization;
    }


    public void setBillingOrganization(Organization organization) {
        this.billingOrganization = organization;
    }


    public String getBillingChartCode() {
        return billingChartCode;
    }


    public void setBillingChartCode(String billingChartCode) {
        this.billingChartCode = billingChartCode;
    }


    public String getBillingOrganizationCode() {
        return billingOrganizationCode;
    }


    public void setBillingOrganizationCode(String billingOrganizationCode) {
        this.billingOrganizationCode = billingOrganizationCode;
    }

    public Chart getProcessingChart() {
        return processingChart;
    }


    public void setProcessingChart(Chart chart) {
        this.processingChart = chart;
    }


    public Organization getProcessingOrganization() {
        return processingOrganization;
    }


    public void setProcessingOrganization(Organization organization) {
        this.processingOrganization = organization;
    }


    public String getProcessingChartCode() {
        return processingChartCode;
    }


    public void setProcessingChartCode(String processingChartCode) {
        this.processingChartCode = processingChartCode;
    }


    public String getProcessingOrganizationCode() {
        return processingOrganizationCode;
    }


    public void setProcessingOrganizationCode(String processingOrganizationCode) {
        this.processingOrganizationCode = processingOrganizationCode;
    }


    /**
     * Gets the principalId attribute.
     *
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the collector attribute.
     *
     * @return Returns the collector.
     */
    public Person getCollector() {
        collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, collector);
        return collector;
    }


    /**
     * Sets the collector attribute value.
     *
     * @param collector The collector to set.
     */
    public void setCollector(Person collector) {
        this.collector = collector;
    }


    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     *
     * @return Returns the userLookupRoleNamespaceCode.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }


    /**
     * Gets the userLookupRoleName attribute.
     *
     * @return Returns the userLookupRoleName.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }


    /**
     * Gets the agingBucket attribute.
     *
     * @return Returns the agingBucket.
     */
    public String getAgingBucket() {
        return agingBucket;
    }


    /**
     * Sets the agingBucket attribute value.
     *
     * @param agingBucket The agingBucket to set.
     */
    public void setAgingBucket(String agingBucket) {
        this.agingBucket = agingBucket;
    }


    /**
     * Gets the campaignID attribute.
     *
     * @return Returns the campaignID.
     */
    public String getCampaignID() {
        return campaignID;
    }


    /**
     * Sets the campaignID attribute value.
     *
     * @param campaignID The campaignID to set.
     */
    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
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
     * /** Gets the awardTotal attribute.
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
     * Gets the invoices attribute.
     *
     * @return Returns the invoices.
     */
    public Collection<ContractsGrantsInvoiceDocument> getInvoices() {
        return invoices;
    }


    /**
     * Sets the invoices attribute value.
     *
     * @param invoices The invoices to set.
     */
    public void setInvoices(Collection<ContractsGrantsInvoiceDocument> invoices) {
        this.invoices = invoices;
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
     * Gets the invoiceDocumentNumber attribute.
     *
     * @return Returns the invoiceDocumentNumber.
     */
    public String getInvoiceDocumentNumber() {
        return invoiceDocumentNumber;
    }


    /**
     * Sets the invoiceDocumentNumber attribute value.
     *
     * @param invoiceDocumentNumber The invoiceDocumentNumber to set.
     */
    public void setInvoiceDocumentNumber(String invoiceDocumentNumber) {
        this.invoiceDocumentNumber = invoiceDocumentNumber;
    }


    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsAward getAward() {
        return award;
    }


    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsAward award) {
        this.award = award;
    }


    /**
     * Gets the agency attribute.
     *
     * @return Returns the agency.
     */
    public ContractsAndGrantsAgency getAgency() {
        return agency;
    }


    /**
     * Sets the agency attribute value.
     *
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsAgency agency) {
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
     * Sets the customer attribute value.
     *
     * @param customer The customer to set.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
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



    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber);
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.invoiceDocumentNumber);


        return m;
    }

    public List<String> getInvoiceAttributesForDisplay() {
        List<String> invoiceAttributesForDisplay = new ArrayList<String>();
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER);
        invoiceAttributesForDisplay.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.AGE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.DunningLetterDistributionFields.DUNNING_LETTER_TAMPLATE_SENT_DATE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.SOURCE_TOTAL);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.OPEN_AMOUNT);
        return invoiceAttributesForDisplay;
    }


}
