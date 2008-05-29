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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cg.bo.AccountAwardInformation;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.AwardAccount;
import org.kuali.module.cg.service.AgencyService;
import org.kuali.module.cg.service.CfdaService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation;
import org.kuali.module.integration.bo.ContractsAndGrantsAgency;
import org.kuali.module.integration.bo.ContractsAndGrantsCfda;
import org.kuali.module.integration.service.ContractsAndGrantsModuleService;
import org.springframework.transaction.annotation.Transactional;

@NonTransactional
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleServiceImpl.class);

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

    /**
     * @see org.kuali.module.integration.service.ContractsAndGrantsModuleService#getCfda(java.lang.String)
     */
    public ContractsAndGrantsCfda getCfda(String cfdaNumber) {
        return getCfdaService().getByPrimaryId(cfdaNumber);
    }

    /**
     * @see org.kuali.module.integration.service.ContractsAndGrantsModuleService#isAwardedByFederalAgency(java.lang.String, java.lang.String, java.util.List)
     */
    public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, List<String> federalAgencyTypeCodes) {
        AwardAccount primaryAward = getPrimaryAwardAccount(chartOfAccountsCode, accountNumber);
        if (primaryAward == null) {
            return false;
        }

        String agencyTypeCode = primaryAward.getAward().getAgency().getAgencyTypeCode();
        if (federalAgencyTypeCodes.contains(agencyTypeCode) || primaryAward.getAward().getFederalPassThroughIndicator()) {
            return true;
        }

        return false;
    }
    
    /**
     * get the primary award account for the given account
     * 
     * @param account the given account
     * @return the primary award account for the given account
     */
    private AwardAccount getPrimaryAwardAccount(String chartOfAccountsCode, String accountNumber) {
        AwardAccount primaryAwardAccount = null;
        long highestProposalNumber = 0;
        
        Map accountKeyValues = new HashMap();
        accountKeyValues.put("chartOfAccountsCode", chartOfAccountsCode);
        accountKeyValues.put("accountNumber", accountNumber);

        for (Object awardAccountAsObject : getBusinessObjectService().findMatching(AwardAccount.class, accountKeyValues)) {
            AwardAccount awardAccount = (AwardAccount)awardAccountAsObject;
            Long proposalNumber = awardAccount.getProposalNumber();

            if (proposalNumber >= highestProposalNumber) {
                highestProposalNumber = proposalNumber;
                primaryAwardAccount = awardAccount;
            }
        }

        return primaryAwardAccount;
    }
    
    /**
     * @see org.kuali.module.integration.service.ContractsAndGrantsModuleService#getAwardInformationForAccount(java.lang.String, java.lang.String)
     */
    public List<ContractsAndGrantsAccountAwardInformation> getAwardInformationForAccount(String chartOfAccountsCode, String accountNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getting award information for account "+chartOfAccountsCode+"-"+accountNumber);
        }
        List<ContractsAndGrantsAccountAwardInformation> awardAccounts = new ArrayList<ContractsAndGrantsAccountAwardInformation>();
        
        Map accountKeyValues = new HashMap();
        accountKeyValues.put("chartOfAccountsCode", chartOfAccountsCode);
        accountKeyValues.put("accountNumber", accountNumber);

        for (Object awardAccountAsObject : getBusinessObjectService().findMatching(AwardAccount.class, accountKeyValues)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding award: "+awardAccountAsObject.toString());
            }
            awardAccounts.add(new AccountAwardInformation((AwardAccount)awardAccountAsObject));
        }
        return awardAccounts;
    }

    /**
     * @see org.kuali.module.integration.service.ContractsAndGrantsModuleService#getAgencyByAgencyNumber(java.lang.String)
     */
    public ContractsAndGrantsAgency getAgencyByAgencyNumber(String agencyNumber) {
        return (ContractsAndGrantsAgency)getAgencyService().getByPrimaryId(agencyNumber);
    }
    
    /**
     * Returns the default implementation of the C&G AgencyService
     * @return an implementation of AgencyService
     */
    public AgencyService getAgencyService() {
        return SpringContext.getBean(AgencyService.class);
    }

    /**
     * Returns an implementation of the CfdaService
     * 
     * @return an implementation of the CfdaService
     */
    public CfdaService getCfdaService() {
        return SpringContext.getBean(CfdaService.class);
    }
    
    /**
     * Returns an implementation of the BusinessObjectService
     * 
     * @return an implementation of the BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
}