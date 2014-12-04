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
