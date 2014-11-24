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
package org.kuali.kfs.integration.cg;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.rice.kim.api.identity.Person;

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

