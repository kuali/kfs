/*
 * Copyright 2006-2009 The Kuali Foundation
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

