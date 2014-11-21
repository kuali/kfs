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

import org.kuali.kfs.sys.KFSConstants;
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
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setPrincipalId(java.lang.String)
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProposalNumber()
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProposalNumber(java.lang.Long)
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
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProjectDirector()
     */
    public Person getProjectDirector() {
        if (principalId != null) {
            projectDirector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
        }
        return projectDirector;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProjectDirector(org.kuali.kfs.module.cg.businessobject.ProjectDirector)
     */
    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
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

