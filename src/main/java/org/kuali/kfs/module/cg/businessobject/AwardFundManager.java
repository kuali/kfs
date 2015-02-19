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

import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents an association between an award and a fund manager. It's like a reference to the fund manager from the
 * award. This way an award can maintain a collection of these references instead of owning fund managers directly.
 */
public class AwardFundManager extends PersistableBusinessObjectBase implements Primaryable, MutableInactivatable, ContractsAndGrantsFundManager {

    private String principalId;
    private Long proposalNumber;
    private boolean primaryFundManagerIndicator;
    private String projectTitle;
    private boolean active = true;

    private Person fundManager;

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager#getPrincipalId()
     */
    @Override
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager#getProposalNumber()
     */
    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the primaryFundManagerIndicator attribute.
     *
     * @return Returns the primaryFundManagerIndicator
     */
    public boolean isPrimaryFundManagerIndicator() {
        return primaryFundManagerIndicator;
    }

    /**
     * Sets the primaryFundManagerIndicator attribute.
     *
     * @param primaryFundManagerIndicator The primaryFundManagerIndicator to set.
     */
    public void setPrimaryFundManagerIndicator(boolean primaryFundManagerIndicator) {
        this.primaryFundManagerIndicator = primaryFundManagerIndicator;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager#getProjectTitle()
     */
    @Override
    public String getProjectTitle() {
        return projectTitle;
    }

    /**
     * Sets the projectTitle attribute.
     *
     * @param projectTitle The projectTitle to set.
     */
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager#getFundManager()
     */
    @Override
    public Person getFundManager() {
        fundManager = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, fundManager);
        return fundManager;
    }

    public void setFundManager(Person fundManager) {
        this.fundManager = fundManager;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.Primaryable#isPrimary()
     */
    @Override
    public boolean isPrimary() {
        return isPrimaryFundManagerIndicator();
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.Inactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, this.principalId);
        m.put(CGPropertyConstants.PROJECT_TITLE, this.projectTitle);
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        return m;
    }

}
