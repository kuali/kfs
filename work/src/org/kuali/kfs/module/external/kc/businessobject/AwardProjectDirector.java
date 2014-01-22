/*
 * Copyright 2014 The Kuali Foundation.
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
