/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;


/**
 * DAO interface for the balancing process of ledger entries
 */
public interface LedgerEntryBalancingDao {
    /**
     * Does a group by query on the parameters passed in and returns an object array with the count as first parameter and sum of TRANSACTION_LEDGER_ENTRY_AMOUNT of the second.
     * @param universityFiscalYear the given university fiscal year
     * @param chartOfAccountsCode the given chart of account code
     * @param financialObjectCode the given object code
     * @param financialBalanceTypeCode the given balance type code
     * @param universityFiscalPeriodCode the given university fiscal period code
     * @param transactionDebitCreditCode the given transaction debit or credit code
     * @return object array with [0] being count(*) and [1] sum(TRANSACTION_LEDGER_ENTRY_AMOUNT). Returns null if data was not found
     */
    public Object[] findEntryByGroup(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode, String financialBalanceTypeCode, String universityFiscalPeriodCode, String transactionDebitCreditCode);

    /**
     * Finds the count of rows for >= fiscal year passed in.
     * @param the given university fiscal year
     * @return count
     */
    public Integer findCountGreaterOrEqualThan(Integer year);
}
