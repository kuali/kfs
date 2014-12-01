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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 */
public class ProposalSubcontractor extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String proposalSubcontractorNumber;
    private Long proposalNumber;
    private String subcontractorNumber;
    private KualiDecimal proposalSubcontractorAmount;
    private String proposalSubcontractorDescription;
    private boolean active = true;

    private SubContractor subcontractor;

    /**
     * Default constructor.
     */
    public ProposalSubcontractor() {

    }

    /**
     * Gets the proposalSubcontractorNumber attribute.
     * 
     * @return Returns the proposalSubcontractorNumber
     */
    public String getProposalSubcontractorNumber() {
        return proposalSubcontractorNumber;
    }

    /**
     * Sets the proposalSubcontractorNumber attribute.
     * 
     * @param proposalSubcontractorNumber The proposalSubcontractorNumber to set.
     */
    public void setProposalSubcontractorNumber(String proposalSubcontractorNumber) {
        this.proposalSubcontractorNumber = proposalSubcontractorNumber;
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
     * Gets the subcontractorNumber attribute.
     * 
     * @return Returns the subcontractorNumber
     */
    public String getSubcontractorNumber() {
        return subcontractorNumber;
    }

    /**
     * Sets the subcontractorNumber attribute.
     * 
     * @param subcontractorNumber The subcontractorNumber to set.
     */
    public void setSubcontractorNumber(String subcontractorNumber) {
        this.subcontractorNumber = subcontractorNumber;
    }


    /**
     * Gets the proposalSubcontractorAmount attribute.
     * 
     * @return Returns the proposalSubcontractorAmount
     */
    public KualiDecimal getProposalSubcontractorAmount() {
        return proposalSubcontractorAmount;
    }

    /**
     * Sets the proposalSubcontractorAmount attribute.
     * 
     * @param proposalSubcontractorAmount The proposalSubcontractorAmount to set.
     */
    public void setProposalSubcontractorAmount(KualiDecimal proposalSubcontractorAmount) {
        this.proposalSubcontractorAmount = proposalSubcontractorAmount;
    }

    /**
     * Gets the proposalSubcontractorDescription attribute.
     * 
     * @return Returns the proposalSubcontractorDescription.
     */
    public String getProposalSubcontractorDescription() {
        return proposalSubcontractorDescription;
    }

    /**
     * Sets the proposalSubcontractorDescription attribute value.
     * 
     * @param proposalSubcontractorDescription The proposalSubcontractorDescription to set.
     */
    public void setProposalSubcontractorDescription(String proposalSubcontractorDescription) {
        this.proposalSubcontractorDescription = proposalSubcontractorDescription;
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

    /**
     * @return Returns the subcontractor.
     */
    public SubContractor getSubcontractor() {
        return subcontractor;
    }

    /**
     * @param subcontractor The subcontractor to set.
     * @deprecated
     */
    public void setSubcontractor(SubContractor subcontractor) {
        this.subcontractor = subcontractor;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalSubcontractorNumber", this.proposalSubcontractorNumber);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        m.put("subcontractorNumber", this.subcontractorNumber);
        return m;
    }

    /**
     * This can be displayed by Proposal.xml lookup results.
     * 
     * @see Object#toString()
     */
    @Override
    public String toString() {
        // todo: get "nonexistent" from ApplicationResources.properties via KFSKeyConstants?
        String name = ObjectUtils.isNull(getSubcontractor()) ? "nonexistent" : getSubcontractor().getSubcontractorName();
        String description = getProposalSubcontractorDescription() == null ? "" : " " + getProposalSubcontractorDescription();
        return name + " " + getProposalSubcontractorAmount() + description;
    }
}
