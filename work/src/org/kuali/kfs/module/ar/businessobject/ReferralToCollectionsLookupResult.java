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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
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
    private String chartOfAccountsCode;

    private Collection<ContractsGrantsInvoiceDocument> invoices;
    private ContractsAndGrantsBillingAward award;
    private ContractsAndGrantsBillingAgency agency;
    private Customer customer;
    private Account account;
    private Chart chart;


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

    /**
     * Gets the agency attribute.
     *
     * @return Returns the agency.
     */
    public ContractsAndGrantsBillingAgency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     *
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsBillingAgency agency) {
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
     * gets chartOfAccountsCode
     *
     * @return
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * sets chartOfAccountsCode
     *
     * @param chartOfAccountsCode
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * gets the chart
     *
     * @return
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * sets the chart
     *
     * @param chart
     */
    public void setChart(Chart chart) {
        this.chart = chart;
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
        invoiceAttributesForDisplay.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        invoiceAttributesForDisplay.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.AGE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.SOURCE_TOTAL);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.OPEN_AMOUNT);
        return invoiceAttributesForDisplay;
    }
}
