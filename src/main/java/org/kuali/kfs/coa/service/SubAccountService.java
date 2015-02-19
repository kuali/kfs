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
package org.kuali.kfs.coa.service;

import org.kuali.kfs.coa.businessobject.SubAccount;

/**
 * This interface defines methods that a SubAccount Service must provide.
 */
public interface SubAccountService {
    /**
     * Retrieves a SubAccount object based on primary key.
     *
     * @param chartOfAccountsCode - Chart of Accounts Code
     * @param accountNumber - Account Number
     * @param subAccountNumber - Sub Account Number
     * @return SubAccount
     * @see SubAccountService
     */
    public SubAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

    /**
     * Method is used by KualiSubAccountAttribute to enable caching for routing.
     *
     * @see SubAccountService#getByPrimaryId(String, String, String)
     */
    public SubAccount getByPrimaryIdWithCaching(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

}
