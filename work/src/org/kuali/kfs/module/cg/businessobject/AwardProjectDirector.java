/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class AwardProjectDirector extends PersistableBusinessObjectBase implements Primaryable, CGProjectDirector {

    private String personUniversalIdentifier;
    private Long proposalNumber;
    private boolean awardPrimaryProjectDirectorIndicator;
    private String awardProjectDirectorProjectTitle;

    private ProjectDirector projectDirector;

    /**
     * Default constructor.
     */
    public AwardProjectDirector() {
        // Struts needs this instance to populate the secondary key, personUserIdentifier.
        projectDirector = new ProjectDirector();
    }

    /**
     * @see org.kuali.module.cg.bo.CGProjectDirector#getPersonUniversalIdentifier()
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * @see org.kuali.module.cg.bo.CGProjectDirector#setPersonUniversalIdentifier(java.lang.String)
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * @see org.kuali.module.cg.bo.CGProjectDirector#getProposalNumber()
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * @see org.kuali.module.cg.bo.CGProjectDirector#setProposalNumber(java.lang.Long)
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the awardPrimaryProjectDirectorIndicator attribute.
     * 
     * @return Returns the awardPrimaryProjectDirectorIndicator
     */
    public boolean isAwardPrimaryProjectDirectorIndicator() {
        return awardPrimaryProjectDirectorIndicator;
    }


    /**
     * Sets the awardPrimaryProjectDirectorIndicator attribute.
     * 
     * @param awardPrimaryProjectDirectorIndicator The awardPrimaryProjectDirectorIndicator to set.
     */
    public void setAwardPrimaryProjectDirectorIndicator(boolean awardPrimaryProjectDirectorIndicator) {
        this.awardPrimaryProjectDirectorIndicator = awardPrimaryProjectDirectorIndicator;
    }


    /**
     * Gets the awardProjectDirectorProjectTitle attribute.
     * 
     * @return Returns the awardProjectDirectorProjectTitle
     */
    public String getAwardProjectDirectorProjectTitle() {
        return awardProjectDirectorProjectTitle;
    }

    /**
     * Sets the awardProjectDirectorProjectTitle attribute.
     * 
     * @param awardProjectDirectorProjectTitle The awardProjectDirectorProjectTitle to set.
     */
    public void setAwardProjectDirectorProjectTitle(String awardProjectDirectorProjectTitle) {
        this.awardProjectDirectorProjectTitle = awardProjectDirectorProjectTitle;
    }

    /**
     * @see org.kuali.module.cg.bo.CGProjectDirector#getProjectDirector()
     */
    public ProjectDirector getProjectDirector() {
        return projectDirector;
    }

    /**
     * @see org.kuali.module.cg.bo.CGProjectDirector#setProjectDirector(org.kuali.module.cg.bo.ProjectDirector)
     */
    public void setProjectDirector(ProjectDirector projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see Primaryable#isPrimary()
     */
    public boolean isPrimary() {
        return isAwardPrimaryProjectDirectorIndicator();
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }

}
