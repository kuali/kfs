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

package org.kuali.kfs.module.external.kc.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Defines a financial award object.
 */
public class Award implements ContractsAndGrantsAward {
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";

    private Long proposalNumber;
    private String awardNumber;
    private String agencyNumber;
    private String primeAgencyNumber;
    private String awardTitle;
    private String grantNumber;
    private String cfdaNumber;
    
    private Proposal proposal;
    private Agency agency;
    private Agency primeAgency;
    private List<AwardAccount> awardAccounts;
    
    /**
     * Default no-args constructor.
     */
    public Award() {
        awardAccounts = new ArrayList<AwardAccount>();
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
     * @return a String to represent this field on the inquiry
     */
    public String getAwardInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(AWARD_INQUIRY_TITLE_PROPERTY);
    }


    public Proposal getProposal() {
        return proposal;
    }


    public void prepareForWorkflow() {}


    public void refresh() {}


    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }


    public Agency getAgency() {
        return agency;
    }


    public void setAgency(Agency agency) {
        this.agency = agency;
    }


    /**
     * Gets the awardAccounts list.
     * 
     * @return Returns the awardAccounts.
     */
    public List<AwardAccount> getAwardAccounts() {
        return awardAccounts;
    }

    /**
     * Sets the awardAccounts list.
     * 
     * @param awardAccounts The awardAccounts to set.
     */
    public void setAwardAccounts(List<AwardAccount> awardAccounts) {
        this.awardAccounts = awardAccounts;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getAwardTitle() {
        return awardTitle;
    }

    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    public String getPrimeAgencyNumber() {
        return primeAgencyNumber;
    }

    public void setPrimeAgencyNumber(String primeAgencyNumber) {
        this.primeAgencyNumber = primeAgencyNumber;
    }

    public Agency getPrimeAgency() {
        return primeAgency;
    }

    public void setPrimeAgency(Agency primeAgency) {
        this.primeAgency = primeAgency;
    }

    public String getAwardNumber() {
        return awardNumber;
    }

    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }


    /**
     * Gets the cfdaNumber attribute.
     * 
     * @return Returns the cfdaNumber
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     * 
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

}

