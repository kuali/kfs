/*
 * Copyright 2013 The Kuali Foundation.
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

import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

public class AwardFundManager implements ContractsAndGrantsFundManager {

    private static final String DEFAULT_AWARD_FUND_MANAGER_TITLE = "Fund Manager";

    private String principalId;
    private Long proposalNumber;
    private String awardFundManagerProjectTitle;
    private Person fundManager;

    public AwardFundManager() { }
    public AwardFundManager(Long proposalNumber, String principalId) {
        this.proposalNumber = proposalNumber;
        this.principalId = principalId;
        this.awardFundManagerProjectTitle = DEFAULT_AWARD_FUND_MANAGER_TITLE;
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
    public String getAwardFundManagerProjectTitle() {
        return awardFundManagerProjectTitle;
    }

    public void setAwardFundManagerProjectTitle(String awardFundManagerProjectTitle) {
        this.awardFundManagerProjectTitle = awardFundManagerProjectTitle;
    }

}
