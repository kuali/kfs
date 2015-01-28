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

package org.kuali.kfs.module.cg.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.cg.service.AgencyService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class defines an agency as it is used and referenced within the Contracts & Grants portion of a college or university
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

    // Contracts & Grants fields
    private String cageNumber;
    private String dodacNumber;
    private String dunAndBradstreetNumber;
    private String dunsPlusFourNumber;

    private List<AgencyAddress> agencyAddresses;

    private boolean stateAgencyIndicator;

    // Creating Customer from Agency
    private AccountsReceivableCustomer customer;
    private String customerCreationOptionCode;
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
        return customer = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(AccountsReceivableCustomer.class).retrieveExternalizableBusinessObjectIfNecessary(this, customer, ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER);
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
     * Gets the customerCreationOptionCode attribute.
     *
     * @return Returns the customerCreationOptionCode.
     */
    public String getCustomerCreationOptionCode() {
        return customerCreationOptionCode;
    }

    /**
     * Sets the customerCreationOptionCode attribute value.
     *
     * @param customerCreationOptionCode The customerCreationOptionCode to set.
     */
    public void setCustomerCreationOptionCode(String customerCreationOptionCode) {
        this.customerCreationOptionCode = customerCreationOptionCode;
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
