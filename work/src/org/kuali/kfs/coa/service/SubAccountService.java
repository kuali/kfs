/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.service;

import java.util.List;

import org.kuali.module.chart.bo.SubAccount;

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

    /**
     * Retrieves SubAccount objects associated with the given chart-org-subAccount code combination
     * 
     * @param chartOfAccountsCode - 'Reports To' Chart of Accounts Code
     * @param organizationCode - 'Reports To' Organization Code
     * @param subAccountNumber - Sub Account Number
     * @return a list of SubAccount objects
     */
    public List getSubAccountsByReportsToOrganization(String chartOfAccountsCode, String organizationCode, String subAccountNumber);
}
