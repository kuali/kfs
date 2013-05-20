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
package org.kuali.kfs.module.cgb.service.impl;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.cg.KcModuleService;
import org.kuali.kfs.integration.cg.KfsCgModuleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kim.bo.Person;

@NonTransactional
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleServiceImpl.class);

    public List<String> getParentUnits(String unitNumber) {        
        return SpringContext.getBean(KcModuleService.class, "kcModuleService").getParentUnits(unitNumber);
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getProjectDirectorForAccount(java.lang.String,
     *      java.lang.String)
     */
    public Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {
        return SpringContext.getBean(KfsCgModuleService.class, "kfsCgModuleService").getProjectDirectorForAccount(chartOfAccountsCode, accountNumber);
    }

    /**
     * @see org.kuali.kfs.integration.service.ContractsAndGrantsModuleService#getProjectDirectorForAccount(org.kuali.kfs.coa.businessobject.Account)
     */
    public Person getProjectDirectorForAccount(Account account) {
        return SpringContext.getBean(KfsCgModuleService.class, "kfsCgModuleService").getProjectDirectorForAccount(account);
    }

    /**
     * @see org.kuali.kfs.integration.service.ContractsAndGrantsModuleService#isAwardedByFederalAgency(java.lang.String,
     *      java.lang.String, java.util.List)
     */
   public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, List<String> federalAgencyTypeCodes) {
        return SpringContext.getBean(KfsCgModuleService.class, "kfsCgModuleService").isAwardedByFederalAgency(chartOfAccountsCode, accountNumber, federalAgencyTypeCodes);
    }

   
    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#getAllAccountReponsiblityIds()
     */
    public List<Integer> getAllAccountReponsiblityIds() {
        return SpringContext.getBean(KfsCgModuleService.class, "kfsCgModuleService").getAllAccountReponsiblityIds();
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService#hasValidAccountReponsiblityIdIfExists(org.kuali.kfs.coa.businessobject.Account)
     */
    public boolean hasValidAccountReponsiblityIdIfNotNull(Account account) {
        return SpringContext.getBean(KfsCgModuleService.class, "kfsCgModuleService").hasValidAccountReponsiblityIdIfNotNull(account);
    }

    public String getProposalNumberForAccountAndProjectDirector(String chartOfAccountsCode, String accountNumber, String projectDirectorId) {
        return SpringContext.getBean(KfsCgModuleService.class, "kfsCgModuleService").getProposalNumberForAccountAndProjectDirector(chartOfAccountsCode, accountNumber, projectDirectorId);
    }

}
