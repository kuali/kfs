/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/AwardProjectDirector.java,v $
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
public class AwardProjectDirector extends BusinessObjectBase {

    private String personUniversalIdentifier;
    private Long proposalNumber;
    private boolean awardPrimaryProjectDirectorIndicator;
    private String awardProjectDirectorNote1Text;
    private String awardProjectDirectorNote2Text;
    private String awardProjectDirectorNote3Text;
    private String awardProjectDirectorProjectTitle;

    private ProjectDirector projectDirector;

    /**
     * Default constructor.
     */
    public AwardProjectDirector() {

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
     * Gets the awardPrimaryProjectDirectorIndicator attribute.
     * 
     * @return Returns the awardPrimaryProjectDirectorIndicator
     * 
     */
    public boolean isAwardPrimaryProjectDirectorIndicator() {
        return awardPrimaryProjectDirectorIndicator;
    }


    /**
     * Sets the awardPrimaryProjectDirectorIndicator attribute.
     * 
     * @param awardPrimaryProjectDirectorIndicator The awardPrimaryProjectDirectorIndicator to set.
     * 
     */
    public void setAwardPrimaryProjectDirectorIndicator(boolean awardPrimaryProjectDirectorIndicator) {
        this.awardPrimaryProjectDirectorIndicator = awardPrimaryProjectDirectorIndicator;
    }


    /**
     * Gets the awardProjectDirectorNote1Text attribute.
     * 
     * @return Returns the awardProjectDirectorNote1Text
     * 
     */
    public String getAwardProjectDirectorNote1Text() {
        return awardProjectDirectorNote1Text;
    }

    /**
     * Sets the awardProjectDirectorNote1Text attribute.
     * 
     * @param awardProjectDirectorNote1Text The awardProjectDirectorNote1Text to set.
     * 
     */
    public void setAwardProjectDirectorNote1Text(String awardProjectDirectorNote1Text) {
        this.awardProjectDirectorNote1Text = awardProjectDirectorNote1Text;
    }


    /**
     * Gets the awardProjectDirectorNote2Text attribute.
     * 
     * @return Returns the awardProjectDirectorNote2Text
     * 
     */
    public String getAwardProjectDirectorNote2Text() {
        return awardProjectDirectorNote2Text;
    }

    /**
     * Sets the awardProjectDirectorNote2Text attribute.
     * 
     * @param awardProjectDirectorNote2Text The awardProjectDirectorNote2Text to set.
     * 
     */
    public void setAwardProjectDirectorNote2Text(String awardProjectDirectorNote2Text) {
        this.awardProjectDirectorNote2Text = awardProjectDirectorNote2Text;
    }


    /**
     * Gets the awardProjectDirectorNote3Text attribute.
     * 
     * @return Returns the awardProjectDirectorNote3Text
     * 
     */
    public String getAwardProjectDirectorNote3Text() {
        return awardProjectDirectorNote3Text;
    }

    /**
     * Sets the awardProjectDirectorNote3Text attribute.
     * 
     * @param awardProjectDirectorNote3Text The awardProjectDirectorNote3Text to set.
     * 
     */
    public void setAwardProjectDirectorNote3Text(String awardProjectDirectorNote3Text) {
        this.awardProjectDirectorNote3Text = awardProjectDirectorNote3Text;
    }


    /**
     * Gets the awardProjectDirectorProjectTitle attribute.
     * 
     * @return Returns the awardProjectDirectorProjectTitle
     * 
     */
    public String getAwardProjectDirectorProjectTitle() {
        return awardProjectDirectorProjectTitle;
    }

    /**
     * Sets the awardProjectDirectorProjectTitle attribute.
     * 
     * @param awardProjectDirectorProjectTitle The awardProjectDirectorProjectTitle to set.
     * 
     */
    public void setAwardProjectDirectorProjectTitle(String awardProjectDirectorProjectTitle) {
        this.awardProjectDirectorProjectTitle = awardProjectDirectorProjectTitle;
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
