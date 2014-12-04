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

import org.kuali.kfs.integration.cg.ContractsAndGrantsProjectDirector;
import org.kuali.rice.kim.api.identity.Person;

public class AwardProjectDirector implements ContractsAndGrantsProjectDirector {

    private String principalId;
    private Long proposalNumber;
    private Person projectDirector;

    @Override
    public void refresh() { }

    @Override
    public String getPrincipalId() {
        return principalId;
    }

    @Override
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    @Override
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    @Override
    public Person getProjectDirector() {
        return projectDirector;
    }

    @Override
    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }
}
