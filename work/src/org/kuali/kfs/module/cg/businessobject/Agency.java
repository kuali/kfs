/*
 * Copyright 2005 The Kuali Foundation
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

package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;


/**
 * This class defines an agency as it is used and referenced within the Contracts and Grants portion of a college or university
 * financial system.
 */
public class Agency extends PersistableBusinessObjectBase implements ContractsAndGrantsCGBAgency, Inactivateable, ContractsAndGrantsAgency {

    private String agencyNumber;
    private String reportingName;
    private String fullName;
    private String agencyTypeCode;
    private String reportsToAgencyNumber;
    private KualiDecimal indirectAmount;
    private boolean inStateIndicator;
    private Agency reportsToAgency;
    private AgencyType agencyType;
    private boolean active;


    // Contracts and Grants fields
    private String cageNumber;
    private String dodacNumber;
    private String dunAndBradstreetNumber;
    private String dunsPlusFourNumber;


    // Billing Frequency link

    private String agencyFrequencyCode;
    private BillingFrequency agencybillingFrequency;

    // Invoice Types link

    private String agencyInvoiceTemplateCode;
    private InvoiceTemplate agencyInvoiceTemplate;

    // Financial Report Type link

    private String agencyFinancialReportCode;
    private FinancialFormTemplate agencyFinancialReportType;

    // Financial Report Frequency

    private String agencyFinancialReportFreqCode;
    private FinancialReportFrequencies agencyFinancialReportFreq;

    private Date agencyExpirationDate;

    private List<AgencyAddress> agencyAddresses;

    // Creating Customer from Agency
    private AccountsReceivableCustomer customer;
    private String customerCreated;
    private String customerNumber;
    private String customerTypeCode;


    // Getters and setters for Billing frequency

    public String getAgencyFrequencyCode() {
        return agencyFrequencyCode;
    }

    public void setAgencyFrequencyCode(String agencyFrequencyCode) {
        this.agencyFrequencyCode = agencyFrequencyCode;
    }

    public BillingFrequency getAgencybillingFrequency() {
        return agencybillingFrequency;
    }

    public void setAgencybillingFrequency(BillingFrequency agencybillingFrequency) {
        this.agencybillingFrequency = agencybillingFrequency;
    }


    // Getters and setters for Invoice Types

    public String getAgencyInvoiceTemplateCode() {
        return agencyInvoiceTemplateCode;
    }

    public void setAgencyInvoiceTemplateCode(String agencyInvoiceTemplateCode) {

        this.agencyInvoiceTemplateCode = agencyInvoiceTemplateCode;
    }

    public InvoiceTemplate getAgencyInvoiceTemplate() {
        return agencyInvoiceTemplate;
    }

    public void setAgencyInvoiceTemplate(InvoiceTemplate agencyInvoiceTemplate) {
        this.agencyInvoiceTemplate = agencyInvoiceTemplate;
    }


    // Getters and setters for Financial Report Type

    public String getAgencyFinancialReportCode() {
        return agencyFinancialReportCode;
    }

    public void setAgencyFinancialReportCode(String agencyFinancialReportCode) {
        this.agencyFinancialReportCode = agencyFinancialReportCode;
    }

    public FinancialFormTemplate getAgencyFinancialReportType() {
        return agencyFinancialReportType;
    }

    public void setAgencyFinancialReportType(FinancialFormTemplate agencyFinancialReportType) {
        this.agencyFinancialReportType = agencyFinancialReportType;
    }


    /**
     * Gets the agencyFinancialReportFreqCode attribute.
     * 
     * @return Returns the agencyFinancialReportFreqCode.
     */
    public String getAgencyFinancialReportFreqCode() {
        return agencyFinancialReportFreqCode;
    }

    /**
     * Sets the agencyFinancialReportFreqCode attribute value.
     * 
     * @param agencyFinancialReportFreqCode The agencyFinancialReportFreqCode to set.
     */
    public void setAgencyFinancialReportFreqCode(String agencyFinancialReportFreqCode) {
        this.agencyFinancialReportFreqCode = agencyFinancialReportFreqCode;
    }

    /**
     * Gets the agencyFinancialReportFreq attribute.
     * 
     * @return Returns the agencyFinancialReportFreq.
     */
    public FinancialReportFrequencies getAgencyFinancialReportFreq() {
        return agencyFinancialReportFreq;
    }

    /**
     * Sets the agencyFinancialReportFreq attribute value.
     * 
     * @param agencyFinancialReportFreq The agencyFinancialReportFreq to set.
     */
    public void setAgencyFinancialReportFreq(FinancialReportFrequencies agencyFinancialReportFreq) {
        this.agencyFinancialReportFreq = agencyFinancialReportFreq;
    }

    /**
     * Gets the agencyExpirationDate attribute.
     * 
     * @return Returns the agencyExpirationDate.
     */
    public Date getAgencyExpirationDate() {
        return agencyExpirationDate;
    }

    /**
     * Sets the agencyExpirationDate attribute value.
     * 
     * @param agencyExpirationDate The agencyExpirationDate to set.
     */
    public void setAgencyExpirationDate(Date agencyExpirationDate) {
        this.agencyExpirationDate = agencyExpirationDate;
    }

    /**
     * Gets the agencyTypeCode attribute.
     * 
     * @return Returns the agencyTypeCode.
     */
    public String getAgencyTypeCode() {
        return agencyTypeCode;
    }

    /**
     * Sets the agencyTypeCode attribute value.
     * 
     * @param agencyTypeCode The agencyTypeCode to set.
     */
    public void setAgencyTypeCode(String agencyTypeCode) {
        this.agencyTypeCode = agencyTypeCode;
    }

    /**
     * Gets the reportsToAgencyNumber attribute.
     * 
     * @return Returns the reportsToAgencyNumber.
     */
    public String getReportsToAgencyNumber() {
        return reportsToAgencyNumber;
    }

    /**
     * Sets the reportsToAgencyNumber attribute value.
     * 
     * @param reportsToAgencyNumber The reportsToAgencyNumber to set.
     */
    public void setReportsToAgencyNumber(String reportsToAgencyNumber) {
        this.reportsToAgencyNumber = reportsToAgencyNumber;
    }

    /**
     * Default no-arg constructor.
     */
    public Agency() {
        agencyAddresses = new TypedArrayList(AgencyAddress.class);
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the reportingName attribute.
     * 
     * @return Returns the reportingName
     */
    public String getReportingName() {
        return reportingName;
    }

    /**
     * Sets the reportingName attribute.
     * 
     * @param reportingName The reportingName to set.
     */
    public void setReportingName(String reportingName) {
        this.reportingName = reportingName;
    }

    /**
     * Gets the fullName attribute.
     * 
     * @return Returns the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the fullName attribute.
     * 
     * @param fullName The fullName to set.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the indirectAmount attribute.
     * 
     * @return Returns the indirectAmount
     */
    public KualiDecimal getIndirectAmount() {
        return indirectAmount;
    }

    /**
     * Sets the indirectAmount attribute.
     * 
     * @param indirectAmount The indirectAmount to set.
     */
    public void setIndirectAmount(KualiDecimal indirectAmount) {
        this.indirectAmount = indirectAmount;
    }

    /**
     * Gets the inStateIndicator attribute.
     * 
     * @return Returns the inStateIndicator
     */
    public boolean isInStateIndicator() {
        return inStateIndicator;
    }

    /**
     * Sets the inStateIndicator attribute.
     * 
     * @param inStateIndicator The inStateIndicator to set.
     */
    public void setInStateIndicator(boolean inStateIndicator) {
        this.inStateIndicator = inStateIndicator;
    }

    /**
     * Gets the reportsToAgency attribute.
     * 
     * @return Returns the reportsToAgency
     */
    public Agency getReportsToAgency() {
        return reportsToAgency;
    }

    /**
     * Sets the reportsToAgency attribute.
     * 
     * @param reportsToAgencyNumber The reportsToAgency to set.
     * @deprecated
     * @todo Why is this deprecated?
     */
    public void setReportsToAgency(Agency reportsToAgencyNumber) {
        this.reportsToAgency = reportsToAgencyNumber;
    }

    /**
     * Gets the agencyType attribute.
     * 
     * @return Returns the agencyType
     */
    public AgencyType getAgencyType() {
        return agencyType;
    }

    /**
     * Sets the agencyType attribute.
     * 
     * @param agencyType The agencyType to set.
     * @deprecated
     * @todo Why is this deprecated?
     */
    public void setAgencyType(AgencyType agencyType) {
        this.agencyType = agencyType;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("agencyNumber", getAgencyNumber());
        return m;
    }

    /**
     * This method compares the passed in agency object against this agency object to check for equality. Equality is defined by if
     * the agency passed in has the same agency number as the agency being compared to.
     * 
     * @param agency The agency object to be compared.
     * @return True if the agency passed in is determined to be equal, false otherwise.
     */
    public boolean equals(Agency agency) {
        return this.agencyNumber.equals(agency.getAgencyNumber());
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCageNumber() {
        return cageNumber;
    }

    public void setCageNumber(String cageNumber) {
        this.cageNumber = cageNumber;
    }

    public String getDodacNumber() {
        return dodacNumber;
    }

    public void setDodacNumber(String dodacNumber) {
        this.dodacNumber = dodacNumber;
    }

    public String getDunAndBradstreetNumber() {
        return dunAndBradstreetNumber;
    }

    public void setDunAndBradstreetNumber(String dunAndBradstreetNumber) {
        this.dunAndBradstreetNumber = dunAndBradstreetNumber;
    }

    public String getDunsPlusFourNumber() {
        return dunsPlusFourNumber;
    }

    public void setDunsPlusFourNumber(String dunsPlusFourNumber) {
        this.dunsPlusFourNumber = dunsPlusFourNumber;
    }

    /**
     * This method gets agencyAddresses
     * 
     * @return agencyAddresses
     */
    public List<AgencyAddress> getAgencyAddresses() {
        return agencyAddresses;
    }

    /**
     * This method sets agencyAddresses
     * 
     * @param agencyAddresses
     */
    public void setAgencyAddresses(List<AgencyAddress> agencyAddresses) {
        this.agencyAddresses = agencyAddresses;
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
     * Gets the customer attribute.
     * 
     * @return Returns the customer.
     */
    public AccountsReceivableCustomer getCustomer() {
        return customer = (AccountsReceivableCustomer) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(AccountsReceivableCustomer.class).retrieveExternalizableBusinessObjectIfNecessary(this, customer, "customer");
    }

    /**
     * Sets the customer attribute value.
     * 
     * @param customer The customer to set.
     */
    public void setCustomer(AccountsReceivableCustomer customer) {
        this.customer = customer;
    }

    /**
     * Gets the customerCreated attribute.
     * 
     * @return Returns the customerCreated.
     */
    public String getCustomerCreated() {
        return customerCreated;
    }

    /**
     * Sets the customerCreated attribute value.
     * 
     * @param customerCreated The customerCreated to set.
     */
    public void setCustomerCreated(String customerCreated) {
        this.customerCreated = customerCreated;
    }

    /**
     * Gets the customerTypeCode attribute.
     * 
     * @return Returns the customerTypeCode.
     */
    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    /**
     * Sets the customerTypeCode attribute value.
     * 
     * @param customerTypeCode The customerTypeCode to set.
     */
    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

}
