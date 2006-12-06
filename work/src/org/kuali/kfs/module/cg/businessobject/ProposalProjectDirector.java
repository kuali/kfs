/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/ProposalProjectDirector.java,v $
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
import org.kuali.core.bo.user.UniversalUser;

/**
 * 
 */
public class ProposalProjectDirector extends BusinessObjectBase {

    private String personUniversalIdentifier;
    private Long proposalNumber;
    private String proposalProjectDirectorNote1Text;
    private String proposalProjectDirectorNote2Text;
    private String proposalProjectDirectorNote3Text;
    private boolean proposalPrimaryProjectDirectorIndicator;
    private String proposalProjectDirectorProjectTitle;

    private ProjectDirector projectDirector;

    /**
     * Default constructor.
     */
    public ProposalProjectDirector() {

    }

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier
     * 
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     * 
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     * 
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     * 
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the proposalProjectDirectorNote1Text attribute.
     * 
     * @return Returns the proposalProjectDirectorNote1Text
     * 
     */
    public String getProposalProjectDirectorNote1Text() {
        return proposalProjectDirectorNote1Text;
    }

    /**
     * Sets the proposalProjectDirectorNote1Text attribute.
     * 
     * @param proposalProjectDirectorNote1Text The proposalProjectDirectorNote1Text to set.
     * 
     */
    public void setProposalProjectDirectorNote1Text(String proposalProjectDirectorNote1Text) {
        this.proposalProjectDirectorNote1Text = proposalProjectDirectorNote1Text;
    }


    /**
     * Gets the proposalProjectDirectorNote2Text attribute.
     * 
     * @return Returns the proposalProjectDirectorNote2Text
     * 
     */
    public String getProposalProjectDirectorNote2Text() {
        return proposalProjectDirectorNote2Text;
    }

    /**
     * Sets the proposalProjectDirectorNote2Text attribute.
     * 
     * @param proposalProjectDirectorNote2Text The proposalProjectDirectorNote2Text to set.
     * 
     */
    public void setProposalProjectDirectorNote2Text(String proposalProjectDirectorNote2Text) {
        this.proposalProjectDirectorNote2Text = proposalProjectDirectorNote2Text;
    }


    /**
     * Gets the proposalProjectDirectorNote3Text attribute.
     * 
     * @return Returns the proposalProjectDirectorNote3Text
     * 
     */
    public String getProposalProjectDirectorNote3Text() {
        return proposalProjectDirectorNote3Text;
    }

    /**
     * Sets the proposalProjectDirectorNote3Text attribute.
     * 
     * @param proposalProjectDirectorNote3Text The proposalProjectDirectorNote3Text to set.
     * 
     */
    public void setProposalProjectDirectorNote3Text(String proposalProjectDirectorNote3Text) {
        this.proposalProjectDirectorNote3Text = proposalProjectDirectorNote3Text;
    }


    /**
     * Gets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @return Returns the proposalPrimaryProjectDirectorIndicator
     * 
     */
    public boolean isProposalPrimaryProjectDirectorIndicator() {
        return proposalPrimaryProjectDirectorIndicator;
    }


    /**
     * Sets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @param proposalPrimaryProjectDirectorIndicator The proposalPrimaryProjectDirectorIndicator to set.
     * 
     */
    public void setProposalPrimaryProjectDirectorIndicator(boolean proposalPrimaryProjectDirectorIndicator) {
        this.proposalPrimaryProjectDirectorIndicator = proposalPrimaryProjectDirectorIndicator;
    }


    /**
     * Gets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @return Returns the proposalProjectDirectorProjectTitle
     * 
     */
    public String getProposalProjectDirectorProjectTitle() {
        return proposalProjectDirectorProjectTitle;
    }

    /**
     * Sets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @param proposalProjectDirectorProjectTitle The proposalProjectDirectorProjectTitle to set.
     * 
     */
    public void setProposalProjectDirectorProjectTitle(String proposalProjectDirectorProjectTitle) {
        this.proposalProjectDirectorProjectTitle = proposalProjectDirectorProjectTitle;
    }

    /**
     * @return Returns the personUniversal.
     */
    public ProjectDirector getProjectDirector() {
        return projectDirector;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }
}
