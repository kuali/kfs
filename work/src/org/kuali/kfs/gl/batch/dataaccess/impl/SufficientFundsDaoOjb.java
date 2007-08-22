/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.ojb;

import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.util.KFSUtils;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.dao.SufficientFundsDao;

public class SufficientFundsDaoOjb extends PlatformAwareDaoBaseOjb implements SufficientFundsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsDaoOjb.class);

    private static final String YEAR_END_DOC_PREFIX = "YE%";

    // these
    public SufficientFundsDaoOjb() {
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.SufficientFundsDao#calculateM113PfyrBudget(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public KualiDecimal calculateM113PfyrBudget(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, new Integer(universityFiscalYear.intValue() - 1));
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo(KFSConstants.ACCOUNT_SUFFICIENT_FUNDS_CODE_PROPERTY_NAME, KFSConstants.SF_TYPE_CASH_AT_ACCOUNT);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(SufficientFundBalances.class, criteria);
        reportQuery.setAttributes(new String[] { KFSConstants.CURRENT_BUDGET_BALANCE_AMOUNT_PROPERTY_NAME });


        return executeReportQuery(reportQuery);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.SufficientFundsDao#calculateM113PfyrEncum(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public KualiDecimal calculateM113PfyrEncum(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, new Integer(universityFiscalYear.intValue() - 1));
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo(KFSConstants.ACCOUNT_SUFFICIENT_FUNDS_CODE_PROPERTY_NAME, KFSConstants.SF_TYPE_CASH_AT_ACCOUNT);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(SufficientFundBalances.class, criteria);
        reportQuery.setAttributes(new String[] { KFSConstants.ACCOUNT_ENCUMBRANCE_AMOUNT_PROPERTY_NAME });

        return executeReportQuery(reportQuery);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.SufficientFundsDao#calculateM113PendActual(boolean, java.lang.Integer, java.lang.String,
     *      java.lang.String, List, String)
     */
    public KualiDecimal calculateM113PendActual(boolean financialBeginBalanceLoadInd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, List specialFinancialObjectCodes, String financialObjectCodeForCashInBank) {
        // fp_sasfc:61-2...78-3 m113 calculate pendActual
        KualiDecimal pendActual = calculateM113PendActual1(financialBeginBalanceLoadInd, universityFiscalYear, chartOfAccountsCode, accountNumber, true, financialObjectCodeForCashInBank);
        pendActual = pendActual.subtract(calculateM113PendActual1(financialBeginBalanceLoadInd, universityFiscalYear, chartOfAccountsCode, accountNumber, false, financialObjectCodeForCashInBank));
        pendActual = pendActual.add(calculateM113PendActual2(financialBeginBalanceLoadInd, universityFiscalYear, chartOfAccountsCode, accountNumber, false, specialFinancialObjectCodes));
        pendActual = pendActual.subtract(calculateM113PendActual2(financialBeginBalanceLoadInd, universityFiscalYear, chartOfAccountsCode, accountNumber, true, specialFinancialObjectCodes));

        return pendActual;

    }

    /**
     * 
     * @see org.kuali.module.gl.dao.SufficientFundsDao#calculatePendActual(boolean, java.lang.String, java.lang.Integer,
     *      java.lang.String, java.lang.String, java.lang.String, List)
     */
    public KualiDecimal calculatePendActual(boolean isYearEndDocument, String actualFinancialBalanceTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes) {
        KualiDecimal pendActual = calculatePendActual1(isYearEndDocument, actualFinancialBalanceTypeCd, universityFiscalYear, chartOfAccountsCode, accountNumber, acctSufficientFundsFinObjCd, true, expenditureCodes);
        pendActual = pendActual.subtract(calculatePendActual1(isYearEndDocument, actualFinancialBalanceTypeCd, universityFiscalYear, chartOfAccountsCode, accountNumber, acctSufficientFundsFinObjCd, false, expenditureCodes));
        return pendActual;
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.SufficientFundsDao#calculatePendBudget(boolean, java.lang.String, java.lang.Integer,
     *      java.lang.String, java.lang.String, java.lang.String, List)
     */
    public KualiDecimal calculatePendBudget(boolean isYearEndDocument, String budgetCheckingBalanceTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, budgetCheckingBalanceTypeCd);
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addIn(KFSConstants.FINANCIAL_OBJECT_TYPE_CODE, expenditureCodes);
        criteria.addEqualTo(KFSConstants.ACCOUNT_SUFFICIENT_FUNDS_FINANCIAL_OBJECT_CODE_PROPERTY_NAME, acctSufficientFundsFinObjCd);
        criteria.addNotEqualTo(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, KFSConstants.DocumentStatusCodes.CANCELLED);

        if (isYearEndDocument) {
            criteria.addLike(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, YEAR_END_DOC_PREFIX);
        }
        else {
            criteria.addNotLike(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, YEAR_END_DOC_PREFIX);
        }

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        return executeReportQuery(reportQuery);

    }

    /**
     * 
     * @see org.kuali.module.gl.dao.SufficientFundsDao#calculatePendEncum(boolean, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, List)
     */
    public KualiDecimal calculatePendEncum(boolean isYearEndDocument, String extrnlEncumFinBalanceTypCd, String intrnlEncumFinBalanceTypCd, String preencumbranceFinBalTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, List expenditureCodes) {
        KualiDecimal pendEncum = calculatePendEncum1(isYearEndDocument, extrnlEncumFinBalanceTypCd, intrnlEncumFinBalanceTypCd, preencumbranceFinBalTypeCd, universityFiscalYear, chartOfAccountsCode, accountNumber, acctSufficientFundsFinObjCd, true, expenditureCodes);
        pendEncum = pendEncum.subtract(calculatePendEncum1(isYearEndDocument, extrnlEncumFinBalanceTypCd, intrnlEncumFinBalanceTypCd, preencumbranceFinBalTypeCd, universityFiscalYear, chartOfAccountsCode, accountNumber, acctSufficientFundsFinObjCd, false, expenditureCodes));
        return pendEncum;
    }

    /**
     * fp_sasfc:141-2...1622-2
     * 
     * @param isYearEndDocument
     * @param extrnlEncumFinBalanceTypCd
     * @param intrnlEncumFinBalanceTypCd
     * @param preencumbranceFinBalTypeCd
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param acctSufficientFundsFinObjCd
     * @param isEqualDebitCode
     * @return
     */
    KualiDecimal calculatePendEncum1(boolean isYearEndDocument, String extrnlEncumFinBalanceTypCd, String intrnlEncumFinBalanceTypCd, String preencumbranceFinBalTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, boolean isEqualDebitCode, List expenditureCodes) {
        Criteria criteria = new Criteria();

        Criteria sub1 = new Criteria();
        sub1.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, extrnlEncumFinBalanceTypCd);
        Criteria sub1_1 = new Criteria();
        sub1_1.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, intrnlEncumFinBalanceTypCd);
        Criteria sub1_2 = new Criteria();
        sub1_2.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, preencumbranceFinBalTypeCd);
        sub1_1.addOrCriteria(sub1_2);
        sub1.addOrCriteria(sub1_1);
        criteria.addOrCriteria(sub1);


        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo(KFSConstants.ACCOUNT_SUFFICIENT_FUNDS_FINANCIAL_OBJECT_CODE_PROPERTY_NAME, acctSufficientFundsFinObjCd);
        criteria.addIn(KFSConstants.FINANCIAL_OBJECT_TYPE_CODE, expenditureCodes);

        if (isEqualDebitCode) {
            criteria.addEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }

        criteria.addNotEqualTo(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, KFSConstants.DocumentStatusCodes.CANCELLED);

        if (isYearEndDocument) {
            criteria.addLike(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, YEAR_END_DOC_PREFIX);
        }
        else {
            criteria.addNotLike(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, YEAR_END_DOC_PREFIX);
        }

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        return executeReportQuery(reportQuery);


    }

    /**
     * fp_sasf: 101-2/108-2/115-2/121-2
     * 
     * @param isYearEndDocument
     * @param actualFinancialBalanceTypeCd
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param acctSufficientFundsFinObjCd
     * @return
     */
    KualiDecimal calculatePendActual1(boolean isYearEndDocument, String actualFinancialBalanceTypeCd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String acctSufficientFundsFinObjCd, boolean isEqualDebitCode, List expenditureCodes) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, actualFinancialBalanceTypeCd);
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo(KFSConstants.ACCOUNT_SUFFICIENT_FUNDS_FINANCIAL_OBJECT_CODE_PROPERTY_NAME, acctSufficientFundsFinObjCd);
        criteria.addIn(KFSConstants.FINANCIAL_OBJECT_TYPE_CODE, expenditureCodes);

        if (isEqualDebitCode) {
            criteria.addEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }

        criteria.addNotEqualTo(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, KFSConstants.DocumentStatusCodes.CANCELLED);

        if (isYearEndDocument) {
            criteria.addLike(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, YEAR_END_DOC_PREFIX);
        }
        else {
            criteria.addNotLike(KFSConstants.FINANCIAL_DOCUMENT_TYPE_CODE, YEAR_END_DOC_PREFIX);
        }

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });
        return executeReportQuery(reportQuery);
    }

    /**
     * fp_sasfc:61-2/67-2/80-2/86-2
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return
     */
    KualiDecimal calculateM113PendActual1(boolean financialBeginBalanceLoadInd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isEqualDebitCode, String financialObjectCodeForCashInBank) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, KFSConstants.BALANCE_TYPE_ACTUAL);

        if (financialBeginBalanceLoadInd) {
            criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        }
        else {
            Criteria sub1 = new Criteria();
            sub1.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
            Criteria sub1_1 = new Criteria();
            sub1_1.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, new Integer(universityFiscalYear.intValue() - 1));
            sub1.addOrCriteria(sub1_1);
            criteria.addAndCriteria(sub1);
        }

        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCodeForCashInBank);

        if (isEqualDebitCode) {
            criteria.addEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }

        criteria.addNotEqualTo(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, KFSConstants.DocumentStatusCodes.CANCELLED);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        return executeReportQuery(reportQuery);
    }

    /**
     * fp_sasfc:71-2/75-2/90-2/94-2
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return
     */
    KualiDecimal calculateM113PendActual2(boolean financialBeginBalanceLoadInd, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isEqualDebitCode, List specialFinancialObjectCodes) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, KFSConstants.BALANCE_TYPE_ACTUAL);

        if (financialBeginBalanceLoadInd) {
            criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        }
        else {
            Criteria sub1 = new Criteria();
            sub1.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
            Criteria sub1_1 = new Criteria();
            sub1_1.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, new Integer(universityFiscalYear.intValue() - 1));
            sub1.addOrCriteria(sub1_1);
            criteria.addAndCriteria(sub1);
        }

        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addIn(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, specialFinancialObjectCodes);

        if (isEqualDebitCode) {
            criteria.addEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }

        criteria.addNotEqualTo(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, KFSConstants.DocumentStatusCodes.CANCELLED);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        return executeReportQuery(reportQuery);
    }

    /**
     * Purge table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addLessThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(SufficientFundBalances.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * class util methods
     */

    private KualiDecimal executeReportQuery(ReportQueryByCriteria reportQuery) {
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iterator.hasNext()) {
            KualiDecimal returnResult = (KualiDecimal) ((Object[]) KFSUtils.retrieveFirstAndExhaustIterator(iterator))[0];
            return returnResult;
        }
        else {
            return KualiDecimal.ZERO;
        }
    }
}
