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
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * A generalized purpose for a proposal.
 */
public class ProposalPurpose extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String proposalPurposeCode;
    private String proposalPurposeDescription;
    private boolean active;

    /**
     * Gets the proposalPurposeCode attribute.
     *
     * @return Returns the proposalPurposeCode
     */
    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }

    /**
     * Sets the proposalPurposeCode attribute.
     *
     * @param proposalPurposeCode The proposalPurposeCode to set.
     */
    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }


    /**
     * Gets the proposalPurposeDescription attribute.
     *
     * @return Returns the proposalPurposeDescription
     */
    public String getProposalPurposeDescription() {
        return proposalPurposeDescription;
    }

    /**
     * Sets the proposalPurposeDescription attribute.
     *
     * @param proposalPurposeDescription The proposalPurposeDescription to set.
     */
    public void setProposalPurposeDescription(String proposalPurposeDescription) {
        this.proposalPurposeDescription = proposalPurposeDescription;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalPurposeCode", this.proposalPurposeCode);
        return m;
    }

}
