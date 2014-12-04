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

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * Defines an entry in the Contracts and Grants Invoice Lookup Result.
 */
public class ContractsGrantsInvoiceLookupResult extends TransientBusinessObjectBase {

    private Long proposalNumber;
    private String agencyNumber;
    private String customerNumber;
    private String accountNumber;
    private String agencyReportingName;
    private String agencyFullName;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private String awardBillingFrequency;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;
    private Collection<ContractsAndGrantsBillingAward> awards;
    private ContractsAndGrantsBillingAgency agency;
    private ContractsAndGrantsBillingFrequency billingFrequency;
    private Customer customer;

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
     * Gets the awardBillingFrequency attribute.
     *
     * @return Returns the awardBillingFrequency.
     */
    public String getAwardBillingFrequency() {
        return awardBillingFrequency;
    }


    /**
     * Sets the awardBillingFrequency attribute value.
     *
     * @param awardBillingFrequency The awardBillingFrequency to set.
     */
    public void setAwardBillingFrequency(String awardBillingFrequency) {
        this.awardBillingFrequency = awardBillingFrequency;
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
     * Gets the awards attribute.
     *
     * @return Returns the awards.
     */
    public Collection<ContractsAndGrantsBillingAward> getAwards() {

        return awards;
    }

    /**
     * Sets the awards attribute value.
     *
     * @param awards The awards to set.
     */
    public void setAwards(Collection awards) {
        this.awards = awards;
    }


    /**
     * Gets the billingFrequency attribute.
     *
     * @return Returns the billingFrequency.
     */
    public ContractsAndGrantsBillingFrequency getBillingFrequency() {
        return billingFrequency = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingFrequency.class).retrieveExternalizableBusinessObjectIfNecessary(this, billingFrequency, ArPropertyConstants.BILLING_FREQUENCY);
    }


    /**
     * Sets the billingFrequency attribute value.
     *
     * @param billingFrequency The billingFrequency to set.
     */
    public void setBillingFrequency(ContractsAndGrantsBillingFrequency billingFrequency) {
        this.billingFrequency = billingFrequency;
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
     * Gets the agencyReportingName attribute.
     *
     * @return Returns the agencyReportingName.
     */
    public String getAgencyReportingName() {
        return agencyReportingName;
    }


    /**
     * Sets the agencyReportingName attribute value.
     *
     * @param agencyReportingName The agencyReportingName to set.
     */
    public void setAgencyReportingName(String agencyReportingName) {
        this.agencyReportingName = agencyReportingName;
    }


    /**
     * Gets the agencyFullName attribute.
     *
     * @return Returns the agencyFullName.
     */
    public String getAgencyFullName() {
        return agencyFullName;
    }


    /**
     * Sets the agencyFullName attribute value.
     *
     * @param agencyFullName The agencyFullName to set.
     */
    public void setAgencyFullName(String agencyFullName) {
        this.agencyFullName = agencyFullName;
    }



    public ContractsAndGrantsBillingAgency getAgency() {
        return agency;
    }


    public void setAgency(ContractsAndGrantsBillingAgency agency) {
        this.agency = agency;
    }


    public Customer getCustomer() {
        return customer;
    }


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        if (this.awardBeginningDate != null) {
            m.put("awardBeginningDate", this.awardBeginningDate.toString());
        }
        if (this.awardEndingDate != null) {
            m.put("awardEndingDate", this.awardEndingDate.toString());
        }
        if (this.awardTotal != null) {
            m.put("awardTotal", this.awardTotal.toString());
        }
        m.put(KFSPropertyConstants.AGENCY_NUMBER, this.agencyNumber);
        m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber);
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.accountNumber);
        m.put("agencyReportingName", this.agencyReportingName);
        m.put("agencyFullName", this.agencyFullName);
        m.put("awardBillingFrequency", this.awardBillingFrequency);

        return m;
    }

    public List<String> getAwardAttributesForDisplay() {
        List<String> awardAttributesForDisplay = new ArrayList<String>();
        awardAttributesForDisplay.add(KFSPropertyConstants.PROPOSAL_NUMBER);
        awardAttributesForDisplay.add(KFSPropertyConstants.AWARD_BEGINNING_DATE);
        awardAttributesForDisplay.add(KFSPropertyConstants.AWARD_ENDING_DATE);
        awardAttributesForDisplay.add(ArPropertyConstants.BILLING_FREQUENCY_CODE);
        awardAttributesForDisplay.add(ArPropertyConstants.INSTRUMENT_TYPE_CODE);
        awardAttributesForDisplay.add(ArPropertyConstants.INVOICING_OPTION_DESCRIPTION);
        awardAttributesForDisplay.add(KFSPropertyConstants.AWARD_TOTAL_AMOUNT);

        return awardAttributesForDisplay;
    }


}
