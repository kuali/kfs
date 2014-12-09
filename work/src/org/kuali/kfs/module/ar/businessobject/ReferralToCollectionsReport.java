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
package org.kuali.kfs.module.ar.businessobject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class is used for generation of PDf and html report for ReferralToCollectionsReport.
 */
public class ReferralToCollectionsReport extends TransientBusinessObjectBase {

    private String principalId;
    private Long proposalNumber;
    private String agencyNumber;

    private String invoiceNumber;
    private String agencyName;
    private String chartOfAccountsCode;
    private String accountNumber;

    private String documentNumber;
    private Date documentDate;
    private String customerNumber;
    private String customerType;
    private String referralType;
    private String collectionStatus;

    private Date billingDate;
    private BigDecimal invoiceAmount;
    private BigDecimal openAmount;
    private String referredTo;
    private String finalDisposition;

    private Person collector;
    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;
    private static final String COLLECTION_ACTIVITY_TITLE_PROPERTY = ArKeyConstants.ContractsGrantsCollectionActivityDocumentConstants.TITLE_PROPERTY;
    private ContractsAndGrantsAward award;
    private ContractsAndGrantsAgency agency;
    private Account account;
    private Chart chart;
    private Customer customer;

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("proposalNumber", this.proposalNumber);
        m.put("agencyNumber", this.agencyNumber);
        m.put("invoiceNumber", this.invoiceNumber);
        m.put("documentNumber", this.documentNumber);
        m.put("customerNumber", this.customerNumber);
        return m;
    }

    /**
     * Gets the principalId attribute.
     *
     * @return Return the principal id of collector.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     *
     * @param principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Return the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Return the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     *
     * @param agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the invoiceNumber attribute.
     *
     * @return Return the invoiceNumber.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute.
     *
     * @param invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the agencyName attribute.
     *
     * @return Return the agencyName of agency.
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Sets the agencyName attribute.
     *
     * @param agencyName to set.
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    /**
     * Gets the collector attribute.
     *
     * @return Return the collector.
     */
    public Person getCollector() {
        return collector;
    }

    /**
     * Sets the collector attribute.
     *
     * @param collector The collector to set.
     */
    public void setCollector(Person collector) {
        this.collector = collector;
    }

    /**
     * Gets the award attribute.
     *
     * @return Return the award.
     */
    public ContractsAndGrantsAward getAward() {
        return award;
    }

    /**
     * Sets the award attribute.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsAward award) {
        this.award = award;
    }

    /**
     * Gets the agency attribute.
     *
     * @return Return the agency.
     */
    public ContractsAndGrantsAgency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     *
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsAgency agency) {
        this.agency = agency;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     *
     * @return Return the userLookupRoleNamespaceCode
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     *
     * @return Return the userLookupRoleName
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the account attribute.
     *
     * @return Return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     *
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Return the accountNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the documentDate attribute.
     *
     * @return Return the documentDate
     */
    public Date getDocumentDate() {
        return documentDate;
    }

    /**
     * Sets the documentDate attribute.
     *
     * @param documentDate The documentDate to set.
     */
    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Return the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the customerType attribute.
     *
     * @return Return the customerType
     */
    public String getCustomerType() {
        return customerType;
    }

    /**
     * Sets the customerType attribute.
     *
     * @param customerType The customerType to set.
     */
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    /**
     * Gets the referralType attribute.
     *
     * @return Return the referralType
     */
    public String getReferralType() {
        return referralType;
    }

    /**
     * Sets the referralType attribute.
     *
     * @param referralType The referralType to set.
     */
    public void setReferralType(String referralType) {
        this.referralType = referralType;
    }

    /**
     * Gets the collectionStatus attribute.
     *
     * @return Return the collectionStatus
     */
    public String getCollectionStatus() {
        return collectionStatus;
    }

    /**
     * Sets the collectionStatus attribute.
     *
     * @param collectionStatus The collectionStatus to set.
     */
    public void setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    /**
     * Gets the billingDate attribute.
     *
     * @return Return the billingDate
     */
    public Date getBillingDate() {
        return billingDate;
    }

    /**
     * Sets the billingDate attribute.
     *
     * @param billingDate The billingDate to set.
     */
    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    /**
     * Gets the invoiceAmount attribute.
     *
     * @return Return the invoiceAmount
     */
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute.
     *
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the openAmount attribute.
     *
     * @return Return the openAmount
     */
    public BigDecimal getOpenAmount() {
        return openAmount;
    }

    /**
     * Sets the openAmount attribute.
     *
     * @param openAmount The openAmount to set.
     */
    public void setOpenAmount(BigDecimal openAmount) {
        this.openAmount = openAmount;
    }

    /**
     * Gets the referredTo attribute.
     *
     * @return Return the referredTo
     */
    public String getReferredTo() {
        return referredTo;
    }

    /**
     * Sets the referredTo attribute.
     *
     * @param referredTo The referredTo to set.
     */
    public void setReferredTo(String referredTo) {
        this.referredTo = referredTo;
    }

    /**
     * Gets the finalDisposition attribute.
     *
     * @return Return the finalDisposition
     */
    public String getFinalDisposition() {
        return finalDisposition;
    }

    /**
     * Sets the finalDisposition attribute.
     *
     * @param finalDisposition The finalDisposition to set.
     */
    public void setFinalDisposition(String finalDisposition) {
        this.finalDisposition = finalDisposition;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the chart attribute.
     *
     * @return Returns the chart
     */

    public Chart getChart() {
        return chart;
    }


    /**
     * Sets the chart attribute.
     *
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


}
