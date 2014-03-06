/*
 * Copyright 2006-2008 The Kuali Foundation
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

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class is an empty derived class
 */
public class ContractsAndGrantsAgingReport extends TransientBusinessObjectBase {
    private String reportOption = ArConstants.CustomerAgingReportFields.PROCESSING_ORG;
    private String organizationCode;
    private String processingOrBillingChartCode;

    private String customerName;
    private String customerNumber;
    private String accountChartOfAccountsCode;
    private String accountNumber;
    private String fundManager;
    private Long proposalNumber;
    private String awardDocumentNumber;
    private boolean markedAsFinalInd;
    private Date awardEndDate;
    private KualiDecimal invoiceAmountTo;
    private KualiDecimal invoiceAmountFrom;
    private String invoiceNumber;
    private Date reportRunDate;
    private String reportingName;
    private String agencyNumber;
    private Date invoiceDate;
    private String contractsAndGrantsAccountResponsibilityId;



    private KualiDecimal unpaidBalance0to30 = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalance31to60 = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalance61to90 = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalance91toSYSPR = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalanceSYSPRplus1orMore = KualiDecimal.ZERO;


    private KualiDecimal totalOpenInvoices = KualiDecimal.ZERO;
    private KualiDecimal totalCredits = KualiDecimal.ZERO;
    private KualiDecimal totalWriteOff = KualiDecimal.ZERO;

    private String principalId;
    private Person collector;

    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;
    private Person lookupFundMgrPerson;
    private Customer customer;
    private Account account;
    private Chart chart;
    private ContractsAndGrantsBillingAward award;
    private Organization organization;
    private boolean finalInvoiceInd;

    /**
     * Constructs a ContractsAndGrantsAgingReportDetail.java.
     */
    public ContractsAndGrantsAgingReport() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("customerNumber", this.customerNumber);
        m.put("customerName", this.customerName);
        m.put("accountNumber", this.accountNumber);
        return m;
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
     * Sets the customerName attribute value.
     *
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
     * Gets the accountChartOfAccountsCode attribute.
     *
     * @return Returns the accountChartOfAccountsCode.
     */
    public String getAccountChartOfAccountsCode() {
        return accountChartOfAccountsCode;
    }

    /**
     * Sets the accountChartOfAccountsCode attribute value.
     *
     * @param accountChartOfAccountsCode The accountChartOfAccountsCode to set.
     */
    public void setAccountChartOfAccountsCode(String accountChartOfAccountsCode) {
        this.accountChartOfAccountsCode = accountChartOfAccountsCode;
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
     * Gets the fundManager attribute.
     *
     * @return Returns the fundManager.
     */
    public String getFundManager() {
        return fundManager;
    }

    /**
     * Sets the fundManager attribute value.
     *
     * @param fundManager The fundManager to set.
     */
    public void setFundManager(String fundManager) {
        this.fundManager = fundManager;
    }


    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
     */

    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
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
     * Gets the awardDocumentNumber attribute.
     *
     * @return Returns the awardDocumentNumber.
     */
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    /**
     * Sets the awardDocumentNumber attribute value.
     *
     * @param awardDocumentNumber The awardDocumentNumber to set.
     */
    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }

    /**
     * Gets the markedAsFinalInd attribute.
     *
     * @return Returns the markedAsFinalInd.
     */
    public boolean isMarkedAsFinalInd() {
        return markedAsFinalInd;
    }

    /**
     * Sets the markedAsFinalInd attribute value.
     *
     * @param markedAsFinalInd The markedAsFinalInd to set.
     */
    public void setMarkedAsFinalInd(boolean markedAsFinalInd) {
        this.markedAsFinalInd = markedAsFinalInd;
    }

    /**
     * Gets the awardEndDate attribute.
     *
     * @return Returns the awardEndDate.
     */
    public Date getAwardEndDate() {
        return awardEndDate;
    }

    /**
     * Sets the awardEndDate attribute value.
     *
     * @param awardEndDate The awardEndDate to set.
     */
    public void setAwardEndDate(Date awardEndDate) {
        this.awardEndDate = awardEndDate;
    }

    /**
     * Gets the invoiceAmountTo attribute.
     *
     * @return Returns the invoiceAmountTo.
     */
    public KualiDecimal getInvoiceAmountTo() {
        return invoiceAmountTo;
    }

    /**
     * Sets the invoiceAmountTo attribute value.
     *
     * @param invoiceAmountTo The invoiceAmountTo to set.
     */
    public void setInvoiceAmountTo(KualiDecimal invoiceAmountTo) {
        this.invoiceAmountTo = invoiceAmountTo;
    }

    /**
     * Gets the invoiceAmountFrom attribute.
     *
     * @return Returns the invoiceAmountFrom.
     */
    public KualiDecimal getInvoiceAmountFrom() {
        return invoiceAmountFrom;
    }

    /**
     * Sets the invoiceAmountFrom attribute value.
     *
     * @param invoiceAmountFrom The invoiceAmountFrom to set.
     */
    public void setInvoiceAmountFrom(KualiDecimal invoiceAmountFrom) {
        this.invoiceAmountFrom = invoiceAmountFrom;
    }

    /**
     * Gets the invoiceNumber attribute.
     *
     * @return Returns the invoiceNumber.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute value.
     *
     * @param invoiceNumber The invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the reportRunDate attribute.
     *
     * @return Returns the reportRunDate.
     */
    public Date getReportRunDate() {
        return reportRunDate;
    }

    /**
     * Sets the reportRunDate attribute value.
     *
     * @param reportRunDate The reportRunDate to set.
     */
    public void setReportRunDate(Date reportRunDate) {
        this.reportRunDate = reportRunDate;
    }

    /**
     * Gets the unpaidBalance0to30 attribute.
     *
     * @return Returns the unpaidBalance0to30.
     */
    public KualiDecimal getUnpaidBalance0to30() {
        return unpaidBalance0to30;
    }

    /**
     * Sets the unpaidBalance0to30 attribute value.
     *
     * @param unpaidBalance0to30 The unpaidBalance0to30 to set.
     */
    public void setUnpaidBalance0to30(KualiDecimal unpaidBalance0to30) {
        this.unpaidBalance0to30 = unpaidBalance0to30;
    }

    /**
     * Gets the unpaidBalance31to60 attribute.
     *
     * @return Returns the unpaidBalance31to60.
     */
    public KualiDecimal getUnpaidBalance31to60() {
        return unpaidBalance31to60;
    }

    /**
     * Sets the unpaidBalance31to60 attribute value.
     *
     * @param reportRunDate The unpaidBalance31to60 to set.
     */
    public void setUnpaidBalance31to60(KualiDecimal unpaidBalance31to60) {
        this.unpaidBalance31to60 = unpaidBalance31to60;
    }

    /**
     * Gets the unpaidBalance61to90 attribute.
     *
     * @return Returns the unpaidBalance61to90.
     */
    public KualiDecimal getUnpaidBalance61to90() {
        return unpaidBalance61to90;
    }

    /**
     * Sets the unpaidBalance61to90 attribute value.
     *
     * @param unpaidBalance61to90 The unpaidBalance61to90 to set.
     */
    public void setUnpaidBalance61to90(KualiDecimal unpaidBalance61to90) {
        this.unpaidBalance61to90 = unpaidBalance61to90;
    }

    /**
     * Gets the unpaidBalance91toSYSPR attribute.
     *
     * @return Returns the unpaidBalance91toSYSPR.
     */
    public KualiDecimal getUnpaidBalance91toSYSPR() {
        return unpaidBalance91toSYSPR;
    }

    /**
     * Sets the unpaidBalance91toSYSPR attribute value.
     *
     * @param unpaidBalance91toSYSPR The unpaidBalance91toSYSPR to set.
     */
    public void setUnpaidBalance91toSYSPR(KualiDecimal unpaidBalance91toSYSPR) {
        this.unpaidBalance91toSYSPR = unpaidBalance91toSYSPR;
    }

    /**
     * Gets the unpaidBalanceSYSPRplus1orMore attribute.
     *
     * @return Returns the unpaidBalanceSYSPRplus1orMore.
     */
    public KualiDecimal getUnpaidBalanceSYSPRplus1orMore() {
        return unpaidBalanceSYSPRplus1orMore;
    }

    /**
     * Sets the unpaidBalanceSYSPRplus1orMore attribute value.
     *
     * @param unpaidBalanceSYSPRplus1orMore The unpaidBalanceSYSPRplus1orMore to set.
     */
    public void setUnpaidBalanceSYSPRplus1orMore(KualiDecimal unpaidBalanceSYSPRplus1orMore) {
        this.unpaidBalanceSYSPRplus1orMore = unpaidBalanceSYSPRplus1orMore;
    }

    /**
     * Gets the reportingName attribute.
     *
     * @return Returns the reportingName.
     */
    public String getReportingName() {
        return reportingName;
    }

    /**
     * Sets the reportingName attribute value.
     *
     * @param reportingName The reportingName to set.
     */
    public void setReportingName(String reportingName) {
        this.reportingName = reportingName;
    }

    /**
     * Gets the totalOpenInvoices attribute.
     *
     * @return Returns the totalOpenInvoices.
     */
    public KualiDecimal getTotalOpenInvoices() {
        return totalOpenInvoices;
    }

    /**
     * Sets the totalOpenInvoices attribute value.
     *
     * @param totalOpenInvoices The totalOpenInvoices to set.
     */
    public void setTotalOpenInvoices(KualiDecimal totalOpenInvoices) {
        this.totalOpenInvoices = totalOpenInvoices;
    }

    /**
     * Gets the totalCredits attribute.
     *
     * @return Returns the totalCredits.
     */
    public KualiDecimal getTotalCredits() {
        return totalCredits;
    }

    /**
     * Sets the totalCredits attribute value.
     *
     * @param totalCredits The totalCredits to set.
     */
    public void setTotalCredits(KualiDecimal totalCredits) {
        this.totalCredits = totalCredits;
    }

    /**
     * Gets the totalWriteOff attribute.
     *
     * @return Returns the totalWriteOff.
     */
    public KualiDecimal getTotalWriteOff() {
        return totalWriteOff;
    }

    /**
     * Sets the totalWriteOff attribute value.
     *
     * @param totalWriteOff The totalWriteOff to set.
     */
    public void setTotalWriteOff(KualiDecimal totalWriteOff) {
        this.totalWriteOff = totalWriteOff;
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
     * Gets the organizationCode attribute.
     *
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     *
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the lookupFundMgrPerson attribute.
     *
     * @return Returns the lookupFundMgrPerson.
     */
    public Person getLookupFundMgrPerson() {
        lookupFundMgrPerson = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(fundManager, lookupFundMgrPerson);
        return lookupFundMgrPerson;
    }

    /**
     * Sets the lookupFundMgrPerson attribute value.
     *
     * @param lookupFundMgrPerson The lookupFundMgrPerson to set.
     */
    public void setLookupFundMgrPerson(Person lookupFundMgrPerson) {
        this.lookupFundMgrPerson = lookupFundMgrPerson;
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
     * Gets the account attribute.
     *
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     *
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the contractsAndGrantsAccountResponsibilityId attribute.
     *
     * @return Returns the contractsAndGrantsAccountResponsibilityId.
     */
    public String getContractsAndGrantsAccountResponsibilityId() {
        return contractsAndGrantsAccountResponsibilityId;
    }

    /**
     * Sets the contractsAndGrantsAccountResponsibilityId attribute value.
     *
     * @param contractsAndGrantsAccountResponsibilityId The contractsAndGrantsAccountResponsibilityId to set.
     */
    public void setContractsAndGrantsAccountResponsibilityId(String contractsAndGrantsAccountResponsibilityId) {
        this.contractsAndGrantsAccountResponsibilityId = contractsAndGrantsAccountResponsibilityId;
    }

    /**
     * Gets the reportOption attribute.
     *
     * @return Returns the reportOption
     */

    public String getReportOption() {
        return reportOption;
    }

    /**
     * Sets the reportOption attribute.
     *
     * @param reportOption The reportOption to set.
     */
    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    /**
     * Gets the processingOrBillingChartCode attribute.
     *
     * @return Returns the processingOrBillingChartCode
     */

    public String getProcessingOrBillingChartCode() {
        return processingOrBillingChartCode;
    }

    /**
     * Sets the processingOrBillingChartCode attribute.
     *
     * @param processingOrBillingChartCode The processingOrBillingChartCode to set.
     */
    public void setProcessingOrBillingChartCode(String processingOrBillingChartCode) {
        this.processingOrBillingChartCode = processingOrBillingChartCode;
    }

    /**
     * Gets the finalInvoiceInd attribute.
     *
     * @return Returns the finalInvoiceInd
     */

    public boolean isFinalInvoiceInd() {
        return finalInvoiceInd;
    }

    /**
     * Sets the finalInvoiceInd attribute.
     *
     * @param finalInvoiceInd The finalInvoiceInd to set.
     */
    public void setFinalInvoiceInd(boolean finalInvoiceInd) {
        this.finalInvoiceInd = finalInvoiceInd;
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

    /**
     * Gets the organization attribute.
     *
     * @return Returns the organization
     */

    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     *
     * @param organization The organization to set.
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the award attribute.
     *
     * @return Returns the award
     */

    public ContractsAndGrantsBillingAward getAward() {
        return award;
    }

    /**
     * Sets the award attribute.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsBillingAward award) {
        this.award = award;
    }


}
