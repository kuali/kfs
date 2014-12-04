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

import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * A DAO interface.  This one seems to refer to an old FIS issue, M113, which was: "On account sufficient funds checking, what needs to be done between beginning of fiscal year and loading of beginning balances?"
 * Therefore, this DAO is to find year end balance totals, as far as I can tell
 */
public interface SufficientFundsDao {
    /**
     * Calculate the Prior Fiscal Year Budget total
     * 
     * @param universityFiscalYear the university fiscal year of sufficient funds balances that will be summarized
     * @param chartOfAccountCode the chart of accounts code of sufficient fund balance records that will be summarized
     * @param accountNumber the account number of sufficient fund balances that will be summarized
     * @return the sum of the prior fiscal year budget
     */
    public KualiDecimal calculateM113PfyrBudget(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * Calculate the prior fiscal year encumbrnace total
     * 
     * 
     * @param universityFiscalYear the university fiscal year of sufficient funds balances to summarize
     * @param chartOfAccountsCode the chart of accounts code of sufficient funds balances to summarize
     * @param accountNumber the account number of sufficient fund balances to summarize
     * @return the prior fiscal year encumbrance total
     */
    public KualiDecimal calculateM113PfyrEncum(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * Calculate the prior fiscal year pending actual amount
     * 
     * @param universityFiscalYear the university fiscal year of sufficient funds balances to summarize
     * @param chartOfAccountsCode the chart of accounts code of sufficient funds balances to summarize
     * @param accountNumber the account number of sufficient fund balances to summarize
     * @param specialFinancialObjectCodes include only these financial object codes
     * @param financialObjectCodeForCashInBank the object code for cash in the bank
     * @return the prior fiscal year pending actual amount
     */
    public KualiDecimal calculateM113PendActual(boolean financialBeginBalanceLoadInd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, List specialFinancialObjectCodes, String financialObjectCodeForCashInBank);

    /**
     * Calculates the current pending actual
     * 
     * @param isYearEndDocument should year end documents be included?
     * @param actualFinancialBalanceTypeCd the actual balance type code
     * @param universityFiscalYear the university fiscal year of sufficient funds balances to summarize
     * @param chartOfAccountsCode the chart of accounts code of sufficient funds balances to summarize
     * @param accountNumber the account number of sufficient fund balances to summarize
     * @param acctSufficientFundsFinObjCd the object code for sufficient funds
     * @param expenditureCodes object codes that represent expenditures
     * @return the current pending actual total
     */
    public KualiDecimal calculatePendActual(boolean isYearEndDocument, String actualFinancialBalanceTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes);

    /**
     * calculates the current year pending budget total
     * 
     * @param isYearEndDocument should year end documents be included?
     * @param budgetCheckingBalanceTypeCd the budget balance type code
     * @param universityFiscalYear the university fiscal year of sufficient funds balances to summarize
     * @param chartOfAccountsCode the chart of accounts code of sufficient funds balances to summarize
     * @param accountNumber the account number of sufficient fund balances to summarize
     * @param acctSufficientFundsFinObjCd the object code for sufficient funds
     * @param expenditureCodes object codes that represent expenditures
     * @return calculates the current year pending budget total
     */
    public KualiDecimal calculatePendBudget(boolean isYearEndDocument, String budgetCheckingBalanceTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes);

    /**
     * Calculates the current year pending encumbrance total
     * 
     * @param isYearEndDocument should year end documents be included?
     * @param extrnlEncumFinBalanceTypCd the external encumbrance balance type
     * @param intrnlEncumFinBalanceTypCd the internal encumbrance balance type
     * @param preencumbranceFinBalTypeCd the pre-encumbrance balance type
     * @param universityFiscalYear the university fiscal year of sufficient funds balances to summarize
     * @param chartOfAccountsCode the chart of accounts code of sufficient funds balances to summarize
     * @param accountNumber the account number of sufficient fund balances to summarize
     * @param acctSufficientFundsFinObjCd the object code for sufficient funds
     * @param expenditureCodes object codes that represent expenditures
     * @return the current year pending encumbrance total
     */
    public KualiDecimal calculatePendEncum(boolean isYearEndDocument, String extrnlEncumFinBalanceTypCd, String intrnlEncumFinBalanceTypCd, String preencumbranceFinBalTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes);

    /**
     * Purge table by year/chart
     * 
     * @param chart the chart of sufficient fund records to purge
     * @param year the year of sufficient fund records to purge
     */
    public void purgeYearByChart(String chart, int year);
}
