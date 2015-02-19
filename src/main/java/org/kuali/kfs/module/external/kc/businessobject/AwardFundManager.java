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
package org.kuali.kfs.module.external.kc.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

public class AwardFundManager implements ContractsAndGrantsFundManager {

    private String principalId;
    private Long proposalNumber;
    private String projectTitle;
    private Person fundManager;

    public AwardFundManager() { }
    public AwardFundManager(Long proposalNumber, String principalId) {
        this.proposalNumber = proposalNumber;
        this.principalId = principalId;
        this.projectTitle = KcConstants.DEFAULT_AWARD_FUND_MANAGER_TITLE;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#getFundManager()
     */
    @Override
    public Person getFundManager() {
        fundManager = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, fundManager);
        return fundManager;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#setFundManager(org.kuali.kfs.module.cg.businessobject.FundManager)
     */
    public void setFundManager(Person fundManager) {
        this.fundManager = fundManager;
    }

    @Override
    public void refresh() { }

    @Override
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    @Override
    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

}
