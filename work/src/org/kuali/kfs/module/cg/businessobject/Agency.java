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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableInvoiceTemplate;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.module.cg.service.AgencyService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class defines an agency as it is used and referenced within the Contracts and Grants portion of a college or university
 * financial system.
 */
public class Agency extends PersistableBusinessObjectBase implements ContractsAndGrantsBillingAgency, MutableInactivatable {

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
    private AccountsReceivableInvoiceTemplate agencyInvoiceTemplate;


    private Date agencyExpirationDate;

    private List<AgencyAddress> agencyAddresses;

    private String notes;
    private boolean stateAgencyIndicator;

    // Creating Customer from Agency
    private AccountsReceivableCustomer customer;
    private String customerCreated;
    private String customerNumber;
    private String customerTypeCode;
    private String dunningCampaign;

    //To add boNotes
    private List boNotes;
    /**
     * Default no-arg constructor.
     */
    public Agency() {
        agencyAddresses = new ArrayList<AgencyAddress>();
    }

    // Getters and setters for Billing frequency
    /**
     * Gets the agencyFrequencyCode attribute.
     *
     * @return Returns the agencyFrequencyCode.
     */
    public String getAgencyFrequencyCode() {
        return agencyFrequencyCode;
    }

    /**
     * Sets the agencyFrequencyCode attribute value.
     *
     * @param agencyFrequencyCode The agencyFrequencyCode to set.
     */
    public void setAgencyFrequencyCode(String agencyFrequencyCode) {
        this.agencyFrequencyCode = agencyFrequencyCode;
    }

    /**
     * Gets the agencybillingFrequency attribute.
     *
     * @return Returns the agencybillingFrequency.
     */
    public BillingFrequency getAgencybillingFrequency() {
        return agencybillingFrequency;
    }

    /**
     * Sets the agencybillingFrequency attribute value.
     *
     * @param agencybillingFrequency The agencybillingFrequency to set.
     */
    public void setAgencybillingFrequency(BillingFrequency agencybillingFrequency) {
        this.agencybillingFrequency = agencybillingFrequency;
    }


    // Getters and setters for Invoice Types
    /**
     * Gets the agencyInvoiceTemplateCode attribute.
     *
     * @return Returns the agencyInvoiceTemplateCode.
     */
    public String getAgencyInvoiceTemplateCode() {
        return agencyInvoiceTemplateCode;
    }

    /**
     * Sets the agencyInvoiceTemplateCode attribute value.
     *
     * @param agencyInvoiceTemplateCode The agencyInvoiceTemplateCode to set.
     */
    public void setAgencyInvoiceTemplateCode(String agencyInvoiceTemplateCode) {

        this.agencyInvoiceTemplateCode = agencyInvoiceTemplateCode;
    }

    /**
     * Gets the agencyInvoiceTemplate attribute.
     *
     * @return Returns the agencyInvoiceTemplate.
     */
    public AccountsReceivableInvoiceTemplate getAgencyInvoiceTemplate() {
        return agencyInvoiceTemplate;
    }

    /**
     * Sets the agencyInvoiceTemplate attribute value.
     *
     * @param agencyInvoiceTemplate The agencyInvoiceTemplate to set.
     */
    public void setAgencyInvoiceTemplate(AccountsReceivableInvoiceTemplate agencyInvoiceTemplate) {
        this.agencyInvoiceTemplate = agencyInvoiceTemplate;
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
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber
     */
    @Override
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
    @Override
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
    @Override
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
    @Deprecated
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
    @Deprecated
    public void setAgencyType(AgencyType agencyType) {
        this.agencyType = agencyType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.AGENCY_NUMBER, getAgencyNumber());
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
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the cageNumber attribute.
     *
     * @return Returns the cageNumber.
     */
    public String getCageNumber() {
        return cageNumber;
    }

    /**
     * Sets the cageNumber attribute value.
     *
     * @param cageNumber The cageNumber to set.
     */
    public void setCageNumber(String cageNumber) {
        this.cageNumber = cageNumber;
    }

    /**
     * Gets the dodacNumber attribute.
     *
     * @return Returns the dodacNumber.
     */
    public String getDodacNumber() {
        return dodacNumber;
    }

    /**
     * Sets the dodacNumber attribute value.
     *
     * @param dodacNumber The dodacNumber to set.
     */
    public void setDodacNumber(String dodacNumber) {
        this.dodacNumber = dodacNumber;
    }

    /**
     * Gets the dunAndBradstreetNumber attribute.
     *
     * @return Returns the dunAndBradstreetNumber.
     */
    public String getDunAndBradstreetNumber() {
        return dunAndBradstreetNumber;
    }

    /**
     * Sets the dunAndBradstreetNumber attribute value.
     *
     * @param dunAndBradstreetNumber The dunAndBradstreetNumber to set.
     */
    public void setDunAndBradstreetNumber(String dunAndBradstreetNumber) {
        this.dunAndBradstreetNumber = dunAndBradstreetNumber;
    }

    /**
     * Gets the dunsPlusFourNumber attribute.
     *
     * @return Returns the dunsPlusFourNumber.
     */
    @Override
    public String getDunsPlusFourNumber() {
        return dunsPlusFourNumber;
    }

    /**
     * Sets the dunsPlusFourNumber attribute value.
     *
     * @param dunsPlusFourNumber The dunsPlusFourNumber to set.
     */
    public void setDunsPlusFourNumber(String dunsPlusFourNumber) {
        this.dunsPlusFourNumber = dunsPlusFourNumber;
    }

    /**
     * This method gets agencyAddresses
     *
     * @return agencyAddresses
     */
    @Override
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
    @Override
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
    @Override
    public AccountsReceivableCustomer getCustomer() {
        return customer = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(AccountsReceivableCustomer.class).retrieveExternalizableBusinessObjectIfNecessary(this, customer, "customer");
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
    @Override
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

    /**
     * Gets the dunningCampaign attribute.
     *
     * @return Returns the dunningCampaign.
     */
    public String getDunningCampaign() {
        return dunningCampaign;
    }

    /**
     * Sets the dunningCampaign attribute value.
     *
     * @param dunningCampaign The dunningCampaign to set.
     */
    public void setDunningCampaign(String dunningCampaign) {
        this.dunningCampaign = dunningCampaign;
    }

    /**
     * Gets the notes attribute.
     *
     * @return Returns the notes.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes attribute value.
     *
     * @param notes The notes to set.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets the stateAgencyIndicator attribute.
     *
     * @return Returns the stateAgencyIndicator.
     */
    @Override
    public boolean isStateAgencyIndicator() {
        return stateAgencyIndicator;
    }

    /**
     * Sets the stateAgencyIndicator attribute value.
     *
     * @param stateAgencyIndicator The stateAgencyIndicator to set.
     */
    public void setStateAgencyIndicator(boolean stateAgencyIndicator) {
        this.stateAgencyIndicator = stateAgencyIndicator;
    }

    /**
     * Gets the boNotes attribute.
     *
     * @return Returns the boNotes
     */

    public List<Note> getBoNotes() {
        if (StringUtils.isEmpty(agencyNumber)) {
            return new ArrayList<Note>();
        }
        AgencyService agencyService = SpringContext.getBean(AgencyService.class);
        return agencyService.getAgencyNotes(agencyNumber);
    }

    /**
     * Sets the boNotes attribute.
     *
     * @param boNotes The boNotes to set.
     */
    public void setBoNotes(List boNotes) {
        this.boNotes = boNotes;
    }
}
