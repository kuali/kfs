package org.kuali.module.gl.dao;

import java.util.List;

import org.kuali.core.util.KualiDecimal;

public interface SufficientFundsDao {
    /**
     * fp_sasfc:49-2...54-3 m113 checking:calculates pfyr_budget
     * 
     * @param universityFiscalYear
     * @param chartOfAccountCode
     * @param accountNumber
     * @return
     */
    public KualiDecimal calculateM113PfyrBudget(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * fp_sasfc:55-2...60-3 m113 checking: calculates pfyr_encum
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return
     */
    public KualiDecimal calculateM113PfyrEncum(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);

    /**
     * fp_sasfc:61-2...78-3 m113 checking: calculate pend_actual
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param specialFinancialObjectCodes
     * @param financialObjectCodeForCashInBank TODO
     * @return
     */
    public KualiDecimal calculateM113PendActual(boolean financialBeginBalanceLoadInd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, List specialFinancialObjectCodes, String financialObjectCodeForCashInBank);

    /**
     * fp_sasfc:99-1...125-2
     * 
     * @param isYearEndDocument
     * @param actualFinancialBalanceTypeCd
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param acctSufficientFundsFinObjCd
     * @param expenditureCodes
     * @return
     */
    public KualiDecimal calculatePendActual(boolean isYearEndDocument, String actualFinancialBalanceTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes);

    /**
     * fp_sasfc: 126-2...140-2
     * 
     * @param isYearEndDocument
     * @param budgetCheckingBalanceTypeCd
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param acctSufficientFundsFinObjCd
     * @param expenditureCodes
     * @return
     */
    public KualiDecimal calculatePendBudget(boolean isYearEndDocument, String budgetCheckingBalanceTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes);

    /**
     * fp_sasfc: 141-2-166-2
     * 
     * @param isYearEndDocument
     * @param extrnlEncumFinBalanceTypCd
     * @param intrnlEncumFinBalanceTypCd
     * @param preencumbranceFinBalTypeCd
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param acctSufficientFundsFinObjCd
     * @param expenditureCodes
     * @return
     */
    public KualiDecimal calculatePendEncum(boolean isYearEndDocument, String extrnlEncumFinBalanceTypCd, String intrnlEncumFinBalanceTypCd, String preencumbranceFinBalTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes);

    /**
     * Purge table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year);
}