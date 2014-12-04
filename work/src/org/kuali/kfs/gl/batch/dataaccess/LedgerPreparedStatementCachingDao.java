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
package org.kuali.kfs.gl.batch.dataaccess;

import java.sql.Timestamp;

import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.batch.dataaccess.PreparedStatementCachingDao;

public interface LedgerPreparedStatementCachingDao extends PreparedStatementCachingDao {
    public int getMaxSequenceNumber(Transaction t);


    public Balance getBalance(Transaction t);

    public void insertBalance(Balance balance, Timestamp currentTimestamp);

    public void updateBalance(Balance balance, Timestamp currentTimestamp);

    public Encumbrance getEncumbrance(Entry entry);

    public void insertEncumbrance(Encumbrance encumbrance, Timestamp currentTimestamp);

    public void updateEncumbrance(Encumbrance encumbrance, Timestamp currentTimestamp);

    public ExpenditureTransaction getExpenditureTransaction(Transaction t);

    public void insertExpenditureTransaction(ExpenditureTransaction expenditureTransaction);

    public void updateExpenditureTransaction(ExpenditureTransaction expenditureTransaction);

    public SufficientFundBalances getSufficientFundBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode);

    public void insertSufficientFundBalances(SufficientFundBalances sufficientFundBalances, Timestamp currentTimestamp);

    public void updateSufficientFundBalances(SufficientFundBalances sufficientFundBalances, Timestamp currentTimestamp);

    public AccountBalance getAccountBalance(Transaction t);

    public void insertAccountBalance(AccountBalance accountBalance, Timestamp currentTimestamp);

    public void updateAccountBalance(AccountBalance accountBalance, Timestamp currentTimestamp);


    public void insertReversal(Reversal reversal);

    public void insertEntry(Entry entry, Timestamp currentTimestamp);
}
