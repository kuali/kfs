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

package org.kuali.kfs.integration.cg.businessobject;

import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Defines a financial award object.
 */
public class Award implements ContractsAndGrantsAward {
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";

    private Long proposalNumber;

    /**
     * Default no-args constructor.
     */
    public Award() {
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


    public ContractAndGrantsProposal getProposal() {
        return null;
    }


    public void prepareForWorkflow() {}


    public void refresh() {}
}

