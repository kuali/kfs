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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccount;
import org.kuali.kfs.module.external.kc.webService.InstitutionalUnitSoapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kra.external.unit.service.InstitutionalUnitService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

@NonTransactional
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleServiceImpl.class);
    private BusinessObjectService businessObjectService;
    
    protected List <AwardAccount> getAwardAccounts(String chartCode, String accountNumber) {
        Map objectKeys = new HashMap();
        if ((chartCode != null) && (!chartCode.isEmpty())) objectKeys.put(KcConstants.AccountCreationDefaults.CHART_OF_ACCOUNT_CODE, chartCode);
        objectKeys.put(KcConstants.AccountCreationDefaults.ACCOUNT_NUMBER, accountNumber);
                
        List <AwardAccount> awardAccountDTOs = (List<AwardAccount>)SpringContext.getBean(AwardAccountServiceImpl.class).findMatching(objectKeys);
        
        return awardAccountDTOs;
    }

    protected InstitutionalUnitService getInstitutionalUnitWebService() {
        InstitutionalUnitSoapService institutionalUnitSoapService = null;
        try {
            institutionalUnitSoapService = new InstitutionalUnitSoapService();
        }
        catch (MalformedURLException ex) {
            LOG.error("Could not intialize InstitutionalUnitSoapService: " + ex.getMessage());
            throw new RuntimeException("Could not intialize InstitutionalUnitSoapService: " + ex.getMessage());
        }
        InstitutionalUnitService port = institutionalUnitSoapService.getInstitutionalUnitServicePort();
        return port;
    }

    public List<String> getParentUnits(String unitNumber) {
        List<String> parentUnits = this.getInstitutionalUnitWebService().getParentUnits(unitNumber);
        return parentUnits;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getProjectDirectorForAccount(java.lang.String,
     *      java.lang.String)
     */
    public Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {
        
        List<AwardAccount> awardAccountDTOs = this.getAwardAccounts(chartOfAccountsCode, accountNumber);     
        AwardAccount awardAccount = determineAwardAccountForProjectDirector(awardAccountDTOs);   
        if (awardAccount == null)
            return null;
        
        String projectDirectorId = awardAccount.getPrincipalId();
        LOG.debug("getProjectDirectorForAccount Web Service sent " + chartOfAccountsCode + "/" + accountNumber + " got " + projectDirectorId);
        if (projectDirectorId != null) {
            Person projectDirector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPerson(projectDirectorId);
            return projectDirector;
        }
        return null;
    }
    
    /**
     * Looks for first non-blank project director id within a list of award accounts sorted newest first
     * and returns the award account object, otherwise returns null.
     * 
     * @param awardAccountDTOs
     * @return
     */
    protected AwardAccount determineAwardAccountForProjectDirector(List<AwardAccount> awardAccounts){
        AwardAccount awardAccountReturn = null;
        
        //sorts awards in reverse order, newest first
        if(ObjectUtils.isNotNull(awardAccounts)){
            Collections.sort(awardAccounts, new Comparator<AwardAccount>() {
                public int compare(AwardAccount o1, AwardAccount o2) {
                    String awardId1 = String.valueOf(o1.getAward().getProposalNumber());
                    String awardId2 = String.valueOf(o2.getAward().getProposalNumber());
                    
                    return awardId2.compareTo(awardId1);                                
                }
            });
        }
        
        if(ObjectUtils.isNotNull(awardAccounts) && !awardAccounts.isEmpty()){
            
            for(AwardAccount awardAccount : (List<AwardAccount>)awardAccounts){
                //break on first award account with a non-blank project director
                if(StringUtils.isNotBlank(awardAccount.getPrincipalId())){
                    awardAccountReturn = awardAccount;
                    break;
                }
            }
        }
        
        return awardAccountReturn;
    }

    /**
     * @see org.kuali.kfs.integration.service.ContractsAndGrantsModuleService#getProjectDirectorForAccount(org.kuali.kfs.coa.businessobject.Account)
     */
    public Person getProjectDirectorForAccount(Account account) {
        if (account != null) {
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
        boolean _isFederalSponsor_return = false;
        List<String> federalSponsorTypeCodes = null; 
        List<AwardAccount> awardAccounts = this.getAwardAccounts(chartOfAccountsCode, accountNumber);           
        AwardAccount awardAccount = determineAwardAccountForFederalAgency(awardAccounts);

        if(ObjectUtils.isNotNull(awardAccount)){
            _isFederalSponsor_return = awardAccount.isFederalSponsor();
        }
        
        LOG.debug("isAwardedByFederalAgency" + accountNumber + " got " + _isFederalSponsor_return);

        return _isFederalSponsor_return;
    }

    /**
     * Looks for the newest award account object, otherwise returns null.
     * 
     * @param awardAccounts
     * @return
     */
    protected AwardAccount determineAwardAccountForFederalAgency(List<AwardAccount> awardAccounts){
        AwardAccount awardAccountReturn = null;

        //sorts awards in reverse order, newest first
        if(ObjectUtils.isNotNull(awardAccounts)){
            Collections.sort(awardAccounts, new Comparator<AwardAccount>() {
                public int compare(AwardAccount o1, AwardAccount o2) {
                    String awardId1 = String.valueOf(o1.getProposalNumber());
                    String awardId2 = String.valueOf(o2.getProposalNumber());
                    
                    return awardId2.compareTo(awardId1);                                
                }
            });
        }
    
        if(ObjectUtils.isNotNull(awardAccounts) && !awardAccounts.isEmpty()){
            
            for(AwardAccount awardAccount : awardAccounts){
                    awardAccountReturn = awardAccount;
                    break;
            }
        }
        
        return awardAccountReturn;
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

    public String getProposalNumberForAccountAndProjectDirector(String chartOfAccountsCode, String accountNumber, String projectDirectorId) {
        String proposalNumber = null;
        String awardProjectDirectorId = null;
        
        List<AwardAccount> awardAccountDTOs = this.getAwardAccounts(chartOfAccountsCode, accountNumber);     
        AwardAccount awardAccount = determineAwardAccountForProjectDirector(awardAccountDTOs);   

        //if we have an award, then proceed
        if (ObjectUtils.isNotNull(awardAccount)){
            
            awardProjectDirectorId = awardAccount.getPrincipalId();
            
            LOG.debug("getProjectDirectorForAccount Web Service sent " + chartOfAccountsCode + "/" + accountNumber + " got " + StringUtils.trimToEmpty(awardProjectDirectorId));
            
            //if what we passed in and what we found match, return Proposal Number (in kc this is award number)
            if (StringUtils.equalsIgnoreCase(
                    StringUtils.trimToEmpty(awardProjectDirectorId), 
                    StringUtils.trimToEmpty(projectDirectorId))) {
                
                if(ObjectUtils.isNotNull(awardAccount.getAward())){
                    proposalNumber = awardAccount.getAward().getAwardNumber();
                }
            }
        }
        
        return proposalNumber;
    }

    /**
     * retieve the maxium account responsiblity id from system parameter
     * 
     * @return the maxium account responsiblity id from system parameter
     */
    protected int getMaxiumAccountResponsibilityId() {
        String maxResponsibilityId = getParameterService().getParameterValueAsString(KfsParameterConstants.CHART_ALL.class, KcConstants.MAXIMUM_ACCOUNT_RESPONSIBILITY_ID);
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

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
   
}
