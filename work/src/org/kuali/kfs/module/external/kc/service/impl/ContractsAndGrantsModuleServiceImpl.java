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
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccountDTO;
import org.kuali.kfs.module.external.kc.webService.AwardAccountService;
import org.kuali.kfs.module.external.kc.webService.AwardAccountSoapService;
import org.kuali.kfs.module.external.kc.webService.InstitutionalUnitService;
import org.kuali.kfs.module.external.kc.webService.InstitutionalUnitSoapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.ParameterService;

@NonTransactional
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleServiceImpl.class);

    protected AwardAccountService getAwardAccountingWebService() {
        AwardAccountSoapService awardAccountSoapService = null;
        try {
            awardAccountSoapService = new AwardAccountSoapService();
        }
        catch (MalformedURLException ex) {
            LOG.error("Could not intialize AwardAccountSoapService: " + ex.getMessage());
            throw new RuntimeException("Could not intialize AwardAccountSoapService: " + ex.getMessage());
        }
        AwardAccountService port = awardAccountSoapService.getAwardAccountServicePort();
        return port;
        
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
        List<String> parentUnits  = this.getInstitutionalUnitWebService().getParentUnits(unitNumber);
        return parentUnits;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getProjectDirectorForAccount(java.lang.String,
     *      java.lang.String)
     */
    public Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {  
        AwardAccountDTO awardAccountDTO = this.getAwardAccountingWebService().getAwardAccount(accountNumber);
        if (awardAccountDTO == null) return null;
        String projectDirectorId = awardAccountDTO.getProjectDirector();
        LOG.info("Web Service sent " + accountNumber + " got " + projectDirectorId);
        if (projectDirectorId != null) {
            Person projectDirector = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByEmployeeId(projectDirectorId);
              return projectDirector;
        }
        return null;
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
    public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, List<String> federalAgencyTypeCodes) {   
        AwardAccountDTO awardAccountDTO = this.getAwardAccountingWebService().getAwardAccount(accountNumber);
        if (awardAccountDTO == null) return false;
        
        boolean _isFederalSponsor_return = awardAccountDTO.getFederalSponsor();
        return _isFederalSponsor_return;
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
        String maxResponsibilityId = getParameterService().getParameterValue(KfsParameterConstants.CONTRACTS_AND_GRANTS_ALL.class, KcConstants.MAXIMUM_ACCOUNT_RESPONSIBILITY_ID);
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

}

