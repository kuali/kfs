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
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.module.cg.service.CfdaService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This Class provides implementation to the services required for inter module communication.
 */
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {
    protected org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleServiceImpl.class);

    protected AwardService awardService;
    protected ParameterService parameterService;
    protected AgencyService agencyService;
    protected CfdaService cfdaService;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getProjectDirectorForAccount(java.lang.String,
     *      java.lang.String)
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
        return parameterService;
    }

    /**
     * Returns the default implementation of the C&G AgencyService
     *
     * @return an implementation of AgencyService
     */
    public AgencyService getAgencyService() {
        return agencyService;
    }

    /**
     * Returns an implementation of the CfdaService
     *
     * @return an implementation of the CfdaService
     */
    public CfdaService getCfdaService() {
        return cfdaService;
    }

    /**
     * Returns an implementation of the BusinessObjectService
     *
     * @return an implementation of the BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getParentUnits(java.lang.String)
     */
    @Override
    public List<String> getParentUnits(String unitNumber) {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getProposalNumberForAccountAndProjectDirector(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public String getProposalNumberForAccountAndProjectDirector(String chartOfAccountsCode, String accountNumber, String projectDirectorId) {
        String proposalNumber = null;

        Map<String, Object> awardAccountMap = new HashMap<String, Object>();
        awardAccountMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        awardAccountMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        Collection<AwardAccount> proposals = getBusinessObjectService().findMatchingOrderBy(AwardAccount.class, awardAccountMap, KFSPropertyConstants.PROPOSAL_NUMBER, false);
        if (proposals != null && !proposals.isEmpty()) {
            AwardAccount proposalWithMaxProposalNumber = proposals.iterator().next();

            if (StringUtils.equalsIgnoreCase(proposalWithMaxProposalNumber.getProjectDirector().getPrincipalId(), projectDirectorId)) {
                proposalNumber = proposalWithMaxProposalNumber.getProposalNumber().toString();
            }
        }

        return proposalNumber;
    }

    /**
     * Gets the awardService attribute.
     *
     * @return Returns the awardService.
     */
    public AwardService getAwardService() {
        return awardService;
    }

    public void setAwardService(AwardService awardService) {
        this.awardService = awardService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setAgencyService(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    public void setCfdaService(CfdaService cfdaService) {
        this.cfdaService = cfdaService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
