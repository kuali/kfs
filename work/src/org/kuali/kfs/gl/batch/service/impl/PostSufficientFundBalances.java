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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of PostTransaction which posts a transaction to the appropriate sufficient funds record
 */
@Transactional
public class PostSufficientFundBalances implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostSufficientFundBalances.class);
    
    private AccountingCycleCachingService accountingCycleCachingService;
    private PersistenceStructureService persistenceStructureService;
    
    /**
     * Constructs a PostSufficientFundBalances instance
     */
    public PostSufficientFundBalances() {
        super();
    }

    /**
     * Posts the transaction to the appropriate sufficient funds records
     * 
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @param posterReportWriterService the writer service where the poster is writing its report
     * @return the accomplished post type
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction t, int mode, Date postDate, ReportWriterService posterReportWriterService) {
        LOG.debug("post() started");

        String returnCode = GeneralLedgerConstants.UPDATE_CODE;

        if (KFSConstants.SF_TYPE_NO_CHECKING.equals(t.getAccount().getAccountSufficientFundsCode())) {
            // Don't need to post
            return GeneralLedgerConstants.EMPTY_CODE;
        }

        // Get the Sufficient funds code
        // Sufficient Funds Code
        String sufficientFundsObjectCode = null;
        if (KFSConstants.SF_TYPE_OBJECT.equals(t.getAccount().getAccountSufficientFundsCode())) {
            sufficientFundsObjectCode = t.getFinancialObjectCode();
        }
        else if (KFSConstants.SF_TYPE_LEVEL.equals(t.getAccount().getAccountSufficientFundsCode())) {
            if (ObjectUtils.isNull(t.getFinancialObject())) {
                return "E:Could not find sufficient funds object code for " + t.toString();
            }
            sufficientFundsObjectCode = t.getFinancialObject().getFinancialObjectLevelCode();
        }
        else if (KFSConstants.SF_TYPE_CONSOLIDATION.equals(t.getAccount().getAccountSufficientFundsCode())) {
            //sufficientFundsObjectCode = t.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObjectCode();
            if (ObjectUtils.isNull(t.getFinancialObject())) {
                return "E:Could not find sufficient funds object code for " + t.toString();
            }
            sufficientFundsObjectCode = accountingCycleCachingService.getObjectLevel(t.getFinancialObject().getChartOfAccountsCode(), t.getFinancialObject().getFinancialObjectLevelCode()).getFinancialConsolidationObjectCode();
        }
        else if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(t.getAccount().getAccountSufficientFundsCode()) || KFSConstants.SF_TYPE_ACCOUNT.equals(t.getAccount().getAccountSufficientFundsCode())) {
            sufficientFundsObjectCode = GeneralLedgerConstants.getSpaceFinancialObjectCode();
        }
        else {
            return "E:Invalid sufficient funds code (" + t.getAccount().getAccountSufficientFundsCode() + ")";
        }

        // Look to see if there is a sufficient funds record for this
        SufficientFundBalances sfBalance = accountingCycleCachingService.getSufficientFundBalances(t.getUniversityFiscalYear(), t.getChartOfAccountsCode(), t.getAccountNumber(), sufficientFundsObjectCode);
        if (sfBalance == null) {
            returnCode = GeneralLedgerConstants.INSERT_CODE;
            sfBalance = new SufficientFundBalances();
            sfBalance.setUniversityFiscalYear(t.getUniversityFiscalYear());
            sfBalance.setChartOfAccountsCode(t.getChartOfAccountsCode());
            sfBalance.setAccountNumber(t.getAccountNumber());
            sfBalance.setFinancialObjectCode(sufficientFundsObjectCode);
            sfBalance.setAccountActualExpenditureAmt(KualiDecimal.ZERO);
            sfBalance.setAccountEncumbranceAmount(KualiDecimal.ZERO);
            sfBalance.setCurrentBudgetBalanceAmount(KualiDecimal.ZERO);
            sfBalance.setAccountSufficientFundsCode(t.getAccount().getAccountSufficientFundsCode());
        }

        if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(t.getAccount().getAccountSufficientFundsCode())) {
            // 2640-PROCESS-CASH
            if (t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd())) {
                if (t.getFinancialObjectCode().equals(t.getChart().getFinancialCashObjectCode()) || t.getFinancialObjectCode().equals(t.getChart().getFinAccountsPayableObjectCode())) {
                    // 2641-PROCESS-CASH-ACTUAL
                    updateBudgetAmount(t.getTransactionDebitCreditCode(), sfBalance, t.getTransactionLedgerEntryAmount());
                }
                else {
                    // No need to post this
                    return GeneralLedgerConstants.EMPTY_CODE;
                }
            }
            else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) || t.getOption().getCostShareEncumbranceBalanceTypeCd().equals(t.getFinancialBalanceTypeCode())) {
                if (t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpenditureexpCd()) || t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpendNotExpCode()) || t.getOption().getFinancialObjectTypeTransferExpenseCd().equals(t.getFinancialObjectTypeCode()) || t.getOption().getFinObjTypeExpNotExpendCode().equals(t.getFinancialObjectTypeCode())) {
                    // 2462-PROCESS-CASH-ENCUMBRANCE
                    updateEncumbranceAmount(t.getTransactionDebitCreditCode(), sfBalance, t.getTransactionLedgerEntryAmount());
                }
                else {
                    // No need to post this
                    return GeneralLedgerConstants.EMPTY_CODE;
                }
            }
            else {
                // No need to post this
                return GeneralLedgerConstants.EMPTY_CODE;
            }
        }
        else {
            // 2630-PROCESS-OBJECT-OR-ACCOUNT
            if (t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpenditureexpCd()) || t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpendNotExpCode()) || t.getOption().getFinancialObjectTypeTransferExpenseCd().equals(t.getFinancialObjectTypeCode()) || t.getOption().getFinObjTypeExpNotExpendCode().equals(t.getFinancialObjectTypeCode())) {
                if (t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd())) {
                    // 2631-PROCESS-OBJTACCT-ACTUAL
                    updateExpendedAmount(t.getTransactionDebitCreditCode(), sfBalance, t.getTransactionLedgerEntryAmount());
                }
                else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getCostShareEncumbranceBalanceTypeCd())) {
                    // 2632-PROCESS-OBJTACCT-ENCMBRNC
                    updateEncumbranceAmount(t.getTransactionDebitCreditCode(), sfBalance, t.getTransactionLedgerEntryAmount());
                }
                else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getBudgetCheckingBalanceTypeCd())) {
                    sfBalance.setCurrentBudgetBalanceAmount(sfBalance.getCurrentBudgetBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
                }
                else {
                    // No need to post this
                    return GeneralLedgerConstants.EMPTY_CODE;
                }
            }
            else {
                // No need to post this
                return GeneralLedgerConstants.EMPTY_CODE;
            }
        }

        // If we get here, we need to save the balance entry
        if (returnCode.equals(GeneralLedgerConstants.INSERT_CODE)) {
            accountingCycleCachingService.insertSufficientFundBalances(sfBalance);
        } else {
            accountingCycleCachingService.updateSufficientFundBalances(sfBalance);
        }


        return returnCode;
    }

    /**
     * Updates the expenditure amount of a given sufficient funds balance record
     * 
     * @param debitCreditCode whether the the amount should be debited or credited to the SF balance
     * @param bal a sufficient funds balance to update
     * @param amount the amount to debit or credit
     */
    protected void updateExpendedAmount(String debitCreditCode, SufficientFundBalances bal, KualiDecimal amount) {
        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            bal.setAccountActualExpenditureAmt(bal.getAccountActualExpenditureAmt().subtract(amount));
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode) || KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)) {
            bal.setAccountActualExpenditureAmt(bal.getAccountActualExpenditureAmt().add(amount));
        }
    }

    /**
     * Updates the encumbrance amount of a given sufficient funds balance record
     * 
     * @param debitCreditCode whether the the amount should be debited or credited to the SF balance
     * @param bal a sufficient funds balance to update
     * @param amount the amount to debit or credit
     */
    protected void updateEncumbranceAmount(String debitCreditCode, SufficientFundBalances bal, KualiDecimal amount) {
        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            bal.setAccountEncumbranceAmount(bal.getAccountEncumbranceAmount().subtract(amount));
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode) || KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)) {
            bal.setAccountEncumbranceAmount(bal.getAccountEncumbranceAmount().add(amount));
        }
    }

    /**
     * Updates the budget amount of a given sufficient funds balance record
     * 
     * @param debitCreditCode whether the the amount should be debited or credited to the SF balance
     * @param bal a sufficient funds balance to update
     * @param amount the amount to debit or credit
     */
    protected void updateBudgetAmount(String debitCreditCode, SufficientFundBalances bal, KualiDecimal amount) {
        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            bal.setCurrentBudgetBalanceAmount(bal.getCurrentBudgetBalanceAmount().subtract(amount));
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode) || KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)) {
            bal.setCurrentBudgetBalanceAmount(bal.getCurrentBudgetBalanceAmount().add(amount));
        }
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return persistenceStructureService.getTableName(SufficientFundBalances.class);
    }
    
    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
}
