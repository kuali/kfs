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
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;

/**
 * 
 */
public class ProposalProjectDirector extends PersistableBusinessObjectBase implements Primaryable, CGProjectDirector {

    private String personUniversalIdentifier;
    private Long proposalNumber;
    private boolean proposalPrimaryProjectDirectorIndicator;
    private String proposalProjectDirectorProjectTitle;

    private ProjectDirector projectDirector;

    /**
     * Default constructor.
     */
    public ProposalProjectDirector() {
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
        if (projectDirector != null) {
            projectDirector.setPersonUniversalIdentifier(personUniversalIdentifier);
        }
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
     * Gets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @return Returns the proposalPrimaryProjectDirectorIndicator
     */
    public boolean isProposalPrimaryProjectDirectorIndicator() {
        return proposalPrimaryProjectDirectorIndicator;
    }

    /**
     * @see Primaryable#isPrimary()
     */
    public boolean isPrimary() {
        return isProposalPrimaryProjectDirectorIndicator();
    }

    /**
     * Sets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @param proposalPrimaryProjectDirectorIndicator The proposalPrimaryProjectDirectorIndicator to set.
     */
    public void setProposalPrimaryProjectDirectorIndicator(boolean proposalPrimaryProjectDirectorIndicator) {
        this.proposalPrimaryProjectDirectorIndicator = proposalPrimaryProjectDirectorIndicator;
    }


    /**
     * Gets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @return Returns the proposalProjectDirectorProjectTitle
     */
    public String getProposalProjectDirectorProjectTitle() {
        return proposalProjectDirectorProjectTitle;
    }

    /**
     * Sets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @param proposalProjectDirectorProjectTitle The proposalProjectDirectorProjectTitle to set.
     */
    public void setProposalProjectDirectorProjectTitle(String proposalProjectDirectorProjectTitle) {
        this.proposalProjectDirectorProjectTitle = proposalProjectDirectorProjectTitle;
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

    /**
     * This can be displayed by Proposal.xml lookup results.
     * 
     * @see Object#toString()
     */
    @Override
    public String toString() {
        // todo: get "nonexistent", "primary", and "secondary" from ApplicationResources.properties via KFSKeyConstants?
        String name = ObjectUtils.isNull(getProjectDirector()) ? "nonexistent" : getProjectDirector().getPersonName();
        String title = getProposalProjectDirectorProjectTitle() == null ? "" : " " + getProposalProjectDirectorProjectTitle();
        return name + " " + (isProposalPrimaryProjectDirectorIndicator() ? "primary" : "secondary") + title;
    }
}
