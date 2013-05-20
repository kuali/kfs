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
package org.kuali.kfs.integration.cg;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Methods which allow core KFS modules to interact with the ContractsAndGrants module.
 */
public interface ContractsAndGrantsModuleService {

    public Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber);

    public Person getProjectDirectorForAccount(Account account);

    /**
     * determine if the given account is awarded by a federal agency
     * 
     * @param chartOfAccountsCode the given account's chart of accounts code
     * @param accountNumber the given account's account number
     * @param federalAgencyTypeCodes the given federal agency type code
     * @return true if the given account is funded by a federal agency or associated with federal pass through indicator; otherwise,
     *         false
     */
    public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, Collection<String> federalAgencyTypeCodes);

    /**
     * get all possible account responsibility ids on contracts & grants Accounts
     * 
     * @return all possible account responsibility ids on contracts & grants Accounts
     */
    public List<Integer> getAllAccountReponsiblityIds();

    /**
     * determine whether the given account has a valid responsibility id if its responsibility id is not null
     * 
     * @param account the given account
     * @return true if the given account is a contracts & grants account with a valid responsibility id; otherwise, return false
     */
    public boolean hasValidAccountReponsiblityIdIfNotNull(Account account);
    
    public List<String> getParentUnits(String unitNumber);

    /**
     * Returns the proposal number for an award associated with an account and project director
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return
     */
    public String getProposalNumberForAccountAndProjectDirector(String chartOfAccountsCode, String accountNumber, String projectDirectorId);


}
