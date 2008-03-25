/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.AwardAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.integration.service.ContractsAndGrantsModuleService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {

    public String getAwardWorkgroupForAccount(String chartOfAccountsCode, String accountNumber) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Long maxProposalNumber;
        Map awardAccountMap = new HashMap();
        Map awardMap = new HashMap();
        awardAccountMap.put("chartOfAccountsCode", chartOfAccountsCode);
        awardAccountMap.put("accountNumber", accountNumber);
        Collection proposals = boService.findMatchingOrderBy(AwardAccount.class, awardAccountMap, "proposalNumber", false);
        if (proposals != null && !proposals.isEmpty()) {
            maxProposalNumber = ((AwardAccount) proposals.iterator().next()).getProposalNumber();
            awardMap.put("proposalNumber", maxProposalNumber);
            return ((Award) boService.findByPrimaryKey(Award.class, awardMap)).getWorkgroupName();
        }
        else {
            return null;
        }

    }

    public UniversalUser getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Long maxProposalNumber;
        Map awardAccountMap = new HashMap();
        Map awardMap = new HashMap();
        awardAccountMap.put("chartOfAccountsCode", chartOfAccountsCode);
        awardAccountMap.put("accountNumber", accountNumber);
        Collection proposals = boService.findMatchingOrderBy(AwardAccount.class, awardAccountMap, "proposalNumber", false);
        if (proposals != null && !proposals.isEmpty()) {
            maxProposalNumber = ((AwardAccount) proposals.iterator().next()).getProposalNumber();
            awardMap.put("proposalNumber", maxProposalNumber);
            return ((AwardAccount) boService.findByPrimaryKey(AwardAccount.class, awardMap)).getProjectDirector().getUniversalUser();
        }
        else {
            return null;
        }

    }
    
    /**
     * @see org.kuali.module.integration.service.ContractsAndGrantsModuleService#getProjectDirectorForAccount(org.kuali.module.chart.bo.Account)
     */
    public UniversalUser getProjectDirectorForAccount(Account account) {
        if(account != null) {
            String chartOfAccountsCode = account.getChartOfAccountsCode(); 
            String accountNumber = account.getAccountNumber();
            return this.getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
        }
        return null;
    }
}