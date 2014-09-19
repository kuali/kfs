/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;

/**
 * Suspension Category that checks to see if the invoice's award has a closed account which still has a current expenditure.
 */
public class ClosedAccountWithCurrentExpendituresSuspensionCategory extends SuspensionCategoryBase {

    protected AccountService accountService;

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        // for each InvoiceDetailAccountObjectCode, extract the chart code and account number, and store it in a map
        // where the key is chart code and value is a set of account numbers (no duplicates).
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes()) {
            String chartOfAccountsCode = invoiceDetailAccountObjectCode.getChartOfAccountsCode();
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            if (map.containsKey(chartOfAccountsCode)) {
                Set<String> set = map.get(chartOfAccountsCode);
                set.add(accountNumber);
            }
            else {
                Set<String> set = new HashSet<String>();
                set.add(accountNumber);
                map.put(chartOfAccountsCode, set);
            }
        }

        // Then go through the map and check to see if any of them have closed accounts
        Set<String> keys = map.keySet();
        for (String chartOfAccountsCode : keys) {
            Set<String> values = map.get(chartOfAccountsCode);
            for (String accountNumber : values) {
                if (accountService.getByPrimaryId(chartOfAccountsCode, accountNumber).isClosed()) {
                    return true;
                }
            }
        }
        return false;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
