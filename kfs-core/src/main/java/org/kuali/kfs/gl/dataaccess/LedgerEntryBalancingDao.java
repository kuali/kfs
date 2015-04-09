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
