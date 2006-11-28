/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/ProposalAwardType.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class ProposalAwardType extends BusinessObjectBase {

    private String proposalAwardTypeCode;
    private String proposalAwardTypeDescription;
    private boolean rowActiveIndicator;
    
    /**
     * Default constructor.
     */
    public ProposalAwardType() {

    }

    /**
     * Gets the proposalAwardTypeCode attribute.
     * 
     * @return Returns the proposalAwardTypeCode
     * 
     */
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    /**
     * Sets the proposalAwardTypeCode attribute.
     * 
     * @param proposalAwardTypeCode The proposalAwardTypeCode to set.
     * 
     */
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }


    /**
     * Gets the proposalAwardTypeDescription attribute.
     * 
     * @return Returns the proposalAwardTypeDescription
     * 
     */
    public String getProposalAwardTypeDescription() {
        return proposalAwardTypeDescription;
    }

    /**
     * Sets the proposalAwardTypeDescription attribute.
     * 
     * @param proposalAwardTypeDescription The proposalAwardTypeDescription to set.
     * 
     */
    public void setProposalAwardTypeDescription(String proposalAwardTypeDescription) {
        this.proposalAwardTypeDescription = proposalAwardTypeDescription;
    }

    /**
     * Gets the rowActiveIndicator attribute. 
     * @return Returns the rowActiveIndicator.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * Sets the rowActiveIndicator attribute value.
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }    

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalAwardTypeCode", this.proposalAwardTypeCode);
        return m;
    }
}
