/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cg.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.service.AgencyService;
import org.kuali.kfs.coa.service.CfdaService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

@NonTransactional
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleServiceImpl.class);

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getProjectDirectorForAccount(java.lang.String,
     *      java.lang.String)
     */
    public Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {
        Map<String, Object> awardAccountMap = new HashMap<String, Object>();
        awardAccountMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        awardAccountMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        Collection<AwardAccount> proposals = getBusinessObjectService().findMatchingOrderBy(AwardAccount.class, awardAccountMap, KFSPropertyConstants.PROPOSAL_NUMBER, false);
        if (proposals != null && !proposals.isEmpty()) {
            AwardAccount proposalWithMaxProposalNumber = proposals.iterator().next();

            return proposalWithMaxProposalNumber.getProjectDirector();
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.integration.service.ContractsAndGrantsModuleService#getProjectDirectorForAccount(org.kuali.kfs.coa.businessobject.Account)
     */
    public Person getProjectDirectorForAccount(Account account) {
        
        if (ObjectUtils.isNotNull(account)) {
            account.refreshNonUpdateableReferences();
            String chartOfAccountsCode = account.getChartOfAccountsCode();
            String accountNumber = account.getAccountNumber();
            return this.getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.service.ContractsAndGrantsModuleService#isAwardedByFederalAgency(java.lang.String,
     *      java.lang.String, java.util.List)
     */
    public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, Collection<String> federalAgencyTypeCodes) {
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
    protected AwardAccount getPrimaryAwardAccount(String chartOfAccountsCode, String accountNumber) {
        AwardAccount primaryAwardAccount = null;
        long highestProposalNumber = 0;

        Map<String, Object> accountKeyValues = new HashMap<String, Object>();
        accountKeyValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        accountKeyValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        for (Object awardAccountAsObject : getBusinessObjectService().findMatching(AwardAccount.class, accountKeyValues)) {
            AwardAccount awardAccount = (AwardAccount) awardAccountAsObject;
            Long proposalNumber = awardAccount.getProposalNumber();

            if (proposalNumber >= highestProposalNumber) {
                highestProposalNumber = proposalNumber;
                primaryAwardAccount = awardAccount;
            }
        }

        return primaryAwardAccount;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getAllAccountReponsiblityIds()
     */
    public List<Integer> getAllAccountReponsiblityIds() {
        int maxResponsibilityId = this.getMaxiumAccountResponsibilityId();

        List<Integer> contractsAndGrantsReponsiblityIds = new ArrayList<Integer>();
        for (int id = 1; id <= maxResponsibilityId; id++) {
            contractsAndGrantsReponsiblityIds.add(id);
        }

        return contractsAndGrantsReponsiblityIds;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#hasValidAccountReponsiblityIdIfExists(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean hasValidAccountReponsiblityIdIfNotNull(Account account) {
        Integer accountResponsiblityId = account.getContractsAndGrantsAccountResponsibilityId();

        if (accountResponsiblityId == null) {
            return true;
        }

        return accountResponsiblityId >= 1 && accountResponsiblityId <= this.getMaxiumAccountResponsibilityId();
    }

    /**
     * retieve the maxium account responsiblity id from system parameter
     * 
     * @return the maxium account responsiblity id from system parameter
     */
    protected int getMaxiumAccountResponsibilityId() {
        String maxResponsibilityId = getParameterService().getParameterValueAsString(KfsParameterConstants.CHART_ALL.class, CGConstants.MAXIMUM_ACCOUNT_RESPONSIBILITY_ID);
        return Integer.valueOf(maxResponsibilityId);
    }

    /**
     * Returns an implementation of the parameterService
     * 
     * @return an implementation of the parameterService
     */
    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    /**
     * Returns the default implementation of the C&G AgencyService
     * 
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
    
    public List<String> getParentUnits(String unitNumber) {
        return null;
    }

    public String getProposalNumberForAccountAndProjectDirector(String chartOfAccountsCode, String accountNumber, String projectDirectorId) {
        String proposalNumber = null;
        
        Map<String, Object> awardAccountMap = new HashMap<String, Object>();
        awardAccountMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        awardAccountMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        Collection<AwardAccount> proposals = getBusinessObjectService().findMatchingOrderBy(AwardAccount.class, awardAccountMap, KFSPropertyConstants.PROPOSAL_NUMBER, false);
        if (proposals != null && !proposals.isEmpty()) {
            AwardAccount proposalWithMaxProposalNumber = proposals.iterator().next();

            if( StringUtils.equalsIgnoreCase(proposalWithMaxProposalNumber.getProjectDirector().getPrincipalId(), projectDirectorId) ){
                proposalNumber = proposalWithMaxProposalNumber.getProposalNumber().toString();
            }
        }
        
        return proposalNumber;
    }
}

