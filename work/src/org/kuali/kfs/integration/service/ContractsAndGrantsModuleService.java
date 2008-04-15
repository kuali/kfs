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
package org.kuali.module.integration.service;

import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.integration.bo.ContractsAndGrantsAccountAwardInformation;
import org.kuali.module.integration.bo.ContractsAndGrantsCfda;

public interface ContractsAndGrantsModuleService {
    public String getAwardWorkgroupForAccount(String chartOfAccountsCode, String accountNumber);

    public UniversalUser getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber);
    
    public UniversalUser getProjectDirectorForAccount(Account account);
    
    /**
     * Returns CFDA information for the given CFDA number
     * @param cfdaNumber a CFDA number
     * @return information about that CFDA
     */
    public ContractsAndGrantsCfda getCfda(String cfdaNumber);
    
    /**
     * determine if the given account is awarded by a federal agency
     * 
     * @param chartOfAccountsCode the given account's chart of accounts code
     * @param accountNumber the given account's account number
     * @param federalAgencyTypeCodes the given federal agency type code
     * @return true if the given account is funded by a federal agency or associated with federal pass through indicator; otherwise,
     *         false
     */
    public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, List<String> federalAgencyTypeCodes);
    
    /**
     * Retrieves a list of information about awards associated with the specified account
     * @param chartOfAccountsCode the chart of accounts code of the specified account
     * @param accountNumber the account number of the specified account
     * @return a List of ContractsAndGrantsAccountAwardInformation records with the award information
     */
    public List<ContractsAndGrantsAccountAwardInformation> getAwardInformationForAccount(String chartOfAccountsCode, String accountNumber);
}
