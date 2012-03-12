/*
 * Copyright 2006 The Kuali Foundation
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
     * Default constructor.
     */
    public ProposalPurpose() {
    }

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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalPurposeCode", this.proposalPurposeCode);
        return m;
    }

}
