/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation;

/**
 * A wrapper for AccountAward, for the purposes of integrating with Account.
 */
public class AccountAwardInformation implements ContractsAndGrantsAccountAwardInformation {
    private AwardAccount awardAccount;
    
    public AccountAwardInformation(AwardAccount awardAccount) {
        this.awardAccount = awardAccount;
    }

    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getAwardWorkgroupName()
     */
    public String getAwardWorkgroupName() {
        return awardAccount.getAward().getWorkgroupName();
    }

    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getProjectDirectorPersonName()
     */
    public String getProjectDirectorPersonName() {
        return awardAccount.getProjectDirector().getUniversalUser().getPersonName();
    }

    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getProposalNumber()
     */
    public Long getProposalNumber() {
        return awardAccount.getProposalNumber();
    }

    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getAwardKualiGroupNames()
     */
    public String getAwardKualiGroupNames() {
        return awardAccount.getAward().getKualiGroupNames();
    }

    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getAwardProposalFederalPassThroughAgencyNumber()
     */
    public String getAwardProposalFederalPassThroughAgencyNumber() {
        return awardAccount.getAward().getProposal().getFederalPassThroughAgencyNumber();
    }

    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getAwardProposalFederalPassThroughIndicator()
     */
    public boolean getAwardProposalFederalPassThroughIndicator() {
        return awardAccount.getAward().getProposal().getProposalFederalPassThroughIndicator();
    }
    
    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getAccountNumber()
     */
    public String getAccountNumber() {
        return awardAccount.getAccountNumber();
    }

    /**
     * @see org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation#getChartOfAccountsCode()
     */
    public String getChartOfAccountsCode() {
        return awardAccount.getChartOfAccountsCode();
    }
}
