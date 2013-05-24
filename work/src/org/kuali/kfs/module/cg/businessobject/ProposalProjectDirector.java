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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Represents a relationship between a {@link Proposal} and a {@link ProjectDirector}.
 */
public class ProposalProjectDirector extends PersistableBusinessObjectBase implements Primaryable, CGProjectDirector, MutableInactivatable {

    private String principalId;
    private Long proposalNumber;
    private boolean proposalPrimaryProjectDirectorIndicator;
    private String proposalProjectDirectorProjectTitle;
    private boolean active = true;

    private Person projectDirector;


    private final String userLookupRoleNamespaceCode = KFSConstants.ParameterNamespaces.KFS;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR;

    /**
     * Default constructor.
     */
    public ProposalProjectDirector() {
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getPrincipalId()
     */
    @Override
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setPrincipalId(java.lang.String)
     */
    @Override
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProposalNumber()
     */
    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProposalNumber(java.lang.Long)
     */
    @Override
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
    @Override
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
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProjectDirector()
     */
    @Override
    public Person getProjectDirector() {
        if (principalId != null) {
            projectDirector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
        }
        return projectDirector;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProjectDirector(org.kuali.kfs.module.cg.businessobject.ProjectDirector)
     */
    @Override
    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, this.principalId);
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
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
        String name = ObjectUtils.isNull(getProjectDirector()) ? "nonexistent" : getProjectDirector().getName();
        String title = getProposalProjectDirectorProjectTitle() == null ? "" : " " + getProposalProjectDirectorProjectTitle();
        return name + " " + (isProposalPrimaryProjectDirectorIndicator() ? "primary" : "secondary") + title;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     * @return Returns the userLookupRoleNamespaceCode.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     * @return Returns the userLookupRoleName.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }
}

