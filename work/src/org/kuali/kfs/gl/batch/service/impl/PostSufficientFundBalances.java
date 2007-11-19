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
package org.kuali.module.gl.batch.poster.impl;

import java.util.Date;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of PostTransaction which posts a transaction to the appropriate sufficient funds record
 */
@Transactional
public class PostSufficientFundBalances implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostSufficientFundBalances.class);

    private SufficientFundBalancesDao sufficientFundBalancesDao;

    /**
     * Constructs a PostSufficientFundBalances instance
     */
    public PostSufficientFundBalances() {
        super();
    }

    public void setSufficientFundBalancesDao(SufficientFundBalancesDao sfbd) {
        sufficientFundBalancesDao = sfbd;
    }

    /**
     * Posts the transaction to the appropriate sufficient funds records
     * 
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @return the accomplished post type
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction, int, java.util.Date)
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        String returnCode = GLConstants.UPDATE_CODE;

        if (KFSConstants.SF_TYPE_NO_CHECKING.equals(t.getAccount().getAccountSufficientFundsCode())) {
            // Don't need to post
            return GLConstants.EMPTY_CODE;
        }

        // Get the Sufficient funds code
        // Sufficient Funds Code
        String sufficientFundsObjectCode = null;
        if (KFSConstants.SF_TYPE_OBJECT.equals(t.getAccount().getAccountSufficientFundsCode())) {
            sufficientFundsObjectCode = t.getFinancialObjectCode();
        }
        else if (KFSConstants.SF_TYPE_LEVEL.equals(t.getAccount().getAccountSufficientFundsCode())) {
            sufficientFundsObjectCode = t.getFinancialObject().getFinancialObjectLevelCode();
        }
        else if (KFSConstants.SF_TYPE_CONSOLIDATION.equals(t.getAccount().getAccountSufficientFundsCode())) {
            sufficientFundsObjectCode = t.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        }
        else if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(t.getAccount().getAccountSufficientFundsCode()) || KFSConstants.SF_TYPE_ACCOUNT.equals(t.getAccount().getAccountSufficientFundsCode())) {
            sufficientFundsObjectCode = GLConstants.getSpaceFinancialObjectCode();
        }
        else {
            return "E:Invalid sufficient funds code (" + t.getAccount().getAccountSufficientFundsCode() + ")";
        }

        // Look to see if there is a sufficient funds record for this
        SufficientFundBalances sfBalance = sufficientFundBalancesDao.getByPrimaryId(t.getUniversityFiscalYear(), t.getChartOfAccountsCode(), t.getAccountNumber(), sufficientFundsObjectCode);
        if (sfBalance == null) {
            returnCode = GLConstants.INSERT_CODE;
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
                    return GLConstants.EMPTY_CODE;
                }
            }
            else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) || t.getOption().getCostShareEncumbranceBalanceTypeCd().equals(t.getFinancialBalanceTypeCode())) {
                if (t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpenditureexpCd()) || t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpendNotExpCode()) || t.getOption().getFinancialObjectTypeTransferExpenseCd().equals(t.getFinancialObjectTypeCode()) || t.getOption().getFinObjTypeExpNotExpendCode().equals(t.getFinancialObjectTypeCode())) {
                    // 2462-PROCESS-CASH-ENCUMBRANCE
                    updateEncumbranceAmount(t.getTransactionDebitCreditCode(), sfBalance, t.getTransactionLedgerEntryAmount());
                }
                else {
                    // No need to post this
                    return GLConstants.EMPTY_CODE;
                }
            }
            else {
                // No need to post this
                return GLConstants.EMPTY_CODE;
            }
        }
        else {
            // 2630-PROCESS-OBJECT-OR-ACCOUNT
            if (t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpenditureexpCd()) || t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpendNotExpCode()) || t.getOption().getFinancialObjectTypeTransferExpenseCd().equals(t.getFinancialObjectTypeCode()) || t.getOption().getFinObjTypeExpNotExpendCode().equals(t.getFinancialObjectTypeCode())) {
                if (t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd())) {
                    // 2631-PROCESS-OBJTACCT-ACTUAL
                    updateExpendedAmount(t.getTransactionDebitCreditCode(), sfBalance, t.getTransactionLedgerEntryAmount());
                }
                else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) || "CE".equals(t.getFinancialBalanceTypeCode())) {
                    // 2632-PROCESS-OBJTACCT-ENCMBRNC
                    updateEncumbranceAmount(t.getTransactionDebitCreditCode(), sfBalance, t.getTransactionLedgerEntryAmount());
                }
                else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getBudgetCheckingBalanceTypeCd())) {
                    sfBalance.setCurrentBudgetBalanceAmount(sfBalance.getCurrentBudgetBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
                }
                else {
                    // No need to post this
                    return GLConstants.EMPTY_CODE;
                }
            }
            else {
                // No need to post this
                return GLConstants.EMPTY_CODE;
            }
        }

        // If we get here, we need to save the balance entry
        sufficientFundBalancesDao.save(sfBalance);

        return returnCode;
    }

    /**
     * Updates the expenditure amount of a given sufficient funds balance record
     * 
     * @param debitCreditCode whether the the amount should be debited or credited to the SF balance
     * @param bal a sufficient funds balance to update
     * @param amount the amount to debit or credit
     */
    private void updateExpendedAmount(String debitCreditCode, SufficientFundBalances bal, KualiDecimal amount) {
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
    private void updateEncumbranceAmount(String debitCreditCode, SufficientFundBalances bal, KualiDecimal amount) {
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
    private void updateBudgetAmount(String debitCreditCode, SufficientFundBalances bal, KualiDecimal amount) {
        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            bal.setCurrentBudgetBalanceAmount(bal.getCurrentBudgetBalanceAmount().subtract(amount));
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode) || KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)) {
            bal.setCurrentBudgetBalanceAmount(bal.getCurrentBudgetBalanceAmount().add(amount));
        }
    }

    /**
     * @see org.kuali.module.gl.batch.poster.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(SufficientFundBalances.class).getFullTableName();
    }
}
