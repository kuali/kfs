/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.util.Date;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;

/**
 * @author jsissom
 *
 */
public class PostSufficientFundBalances implements PostTransaction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostSufficientFundBalances.class);

  private SufficientFundBalancesDao sufficientFundBalancesDao;

  public PostSufficientFundBalances() {
    super();
  }

  public void setSufficientFundBalancesDao(SufficientFundBalancesDao sfbd) {
    sufficientFundBalancesDao = sfbd;
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
   */
  public String post(Transaction t,int mode,Date postDate) {
    LOG.debug("post() started");

    String returnCode = "U";

    if ( "N".equals(t.getAccount().getAccountSufficientFundsCode()) ) {
      // Don't need to post
      return "";
    }

    // Get the Sufficient funds code
    // Sufficient Funds Code
    String sufficientFundsObjectCode = null;
    if ( Constants.SF_TYPE_OBJECT.equals(t.getAccount().getAccountSufficientFundsCode()) ) {
      sufficientFundsObjectCode = t.getFinancialObjectCode();
    } else if ( Constants.SF_TYPE_LEVEL.equals(t.getAccount().getAccountSufficientFundsCode()) ) {
      sufficientFundsObjectCode = t.getFinancialObject().getFinancialObjectLevelCode();
    } else if ( Constants.SF_TYPE_CONSOLIDATION.equals(t.getAccount().getAccountSufficientFundsCode()) ) {
      sufficientFundsObjectCode = t.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObjectCode();
    } else if ( Constants.SF_TYPE_CASH_AT_ACCOUNT.equals(t.getAccount().getAccountSufficientFundsCode()) || Constants.SF_TYPE_ACCOUNT.equals(t.getAccount().getAccountSufficientFundsCode()) ) {
      sufficientFundsObjectCode = "    ";
    } else {
      return "E:Invalid sufficient funds code (" + t.getAccount().getAccountSufficientFundsCode() + ")";
    }

    // Look to see if there is a sufficient funds record for this
    SufficientFundBalances sfBalance = sufficientFundBalancesDao.getByPrimaryId(t.getUniversityFiscalYear(),t.getChartOfAccountsCode(),
        t.getAccountNumber(),sufficientFundsObjectCode);
    if ( sfBalance == null ) {
      returnCode = "I";
      sfBalance = new SufficientFundBalances();
      sfBalance.setUniversityFiscalYear(t.getUniversityFiscalYear());
      sfBalance.setChartOfAccountsCode(t.getChartOfAccountsCode());
      sfBalance.setAccountNumber(t.getAccountNumber());
      sfBalance.setFinancialObjectCode(sufficientFundsObjectCode);
      sfBalance.setAccountActualExpenditureAmt(KualiDecimal.ZERO);
      sfBalance.setAccountEncumbranceAmount(KualiDecimal.ZERO);
      sfBalance.setCurrentBudgetBalanceAmount(KualiDecimal.ZERO);
    }

    if ( "H".equals(t.getAccount().getAccountSufficientFundsCode()) ) {
      // 2640-PROCESS-CASH
      if ( t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd()) ) {
        if ( t.getFinancialObjectCode().equals(t.getChart().getFinancialCashObjectCode()) ||
            t.getFinancialObjectCode().equals(t.getChart().getFinAccountsPayableObjectCode()) ) {
          // 2641-PROCESS-CASH-ACTUAL
          updateBudgetAmount(t.getTransactionDebitCreditCode(),sfBalance,t.getTransactionLedgerEntryAmount());
        } else {
          // No need to post this
          return "";
        }
      } else if ( t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) ||
          t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) ||
          t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) ||
          "CE".equals(t.getFinancialBalanceTypeCode()) ) {
        if ( t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpenditureexpCd()) ||
            t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpendNotExpCode()) ||
            "TE".equals(t.getFinancialObjectTypeCode()) || "ES".equals(t.getFinancialObjectTypeCode()) ) {
          // 2462-PROCESS-CASH-ENCUMBRANCE
          updateEncumbranceAmount(t.getTransactionDebitCreditCode(),sfBalance,t.getTransactionLedgerEntryAmount());
        } else {
          // No need to post this
          return "";
        }
      } else {
        // No need to post this
        return "";
      }      
    } else {
      // 2630-PROCESS-OBJECT-OR-ACCOUNT
      if ( t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpenditureexpCd()) ||
          t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjTypeExpendNotExpCode()) ) {
        if ( t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd()) ) {
          // 2631-PROCESS-OBJTACCT-ACTUAL
          updateExpendedAmount(t.getTransactionDebitCreditCode(),sfBalance,t.getTransactionLedgerEntryAmount());
        } else if ( t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) ||
            t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) ||
            t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) ||
            "CE".equals(t.getFinancialBalanceTypeCode()) ) {
          // 2632-PROCESS-OBJTACCT-ENCMBRNC
          updateEncumbranceAmount(t.getTransactionDebitCreditCode(),sfBalance,t.getTransactionLedgerEntryAmount());
        } else {
          // No need to post this
          return "";
        }
      } else {
        // No need to post this
        return "";
      }
    }

    // If we get here, we need to save the balance entry
    sufficientFundBalancesDao.save(sfBalance);

    return returnCode;
  }

  // 2631-PROCESS-OBJTACCT-ACTUAL
  private void updateExpendedAmount(String debitCreditCode,SufficientFundBalances bal,KualiDecimal amount) {
    if ( Constants.GL_CREDIT_CODE.equals(debitCreditCode) ) {
      bal.setAccountActualExpenditureAmt(bal.getAccountActualExpenditureAmt().subtract(amount));
    } else if ( Constants.GL_DEBIT_CODE.equals(debitCreditCode) || Constants.GL_BUDGET_CODE.equals(debitCreditCode) ) {
      bal.setAccountActualExpenditureAmt(bal.getAccountActualExpenditureAmt().add(amount));
    }
  }

  // 2642-PROCESS-CASH-ENCUMBRANCE
  // 2632-PROCESS-OBJTACCT-ENCMBRNC
  private void updateEncumbranceAmount(String debitCreditCode,SufficientFundBalances bal,KualiDecimal amount) {
    if ( Constants.GL_CREDIT_CODE.equals(debitCreditCode) ) {
      bal.setAccountEncumbranceAmount(bal.getAccountEncumbranceAmount().subtract(amount));
    } else if ( Constants.GL_DEBIT_CODE.equals(debitCreditCode) || Constants.GL_BUDGET_CODE.equals(debitCreditCode) ) {
      bal.setAccountEncumbranceAmount(bal.getAccountEncumbranceAmount().add(amount));
    }
  }

  // 2641-PROCESS-CASH-ACTUAL
  private void updateBudgetAmount(String debitCreditCode,SufficientFundBalances bal,KualiDecimal amount) {
    if ( Constants.GL_CREDIT_CODE.equals(debitCreditCode) ) {
      bal.setCurrentBudgetBalanceAmount(bal.getCurrentBudgetBalanceAmount().subtract(amount));
    } else if ( Constants.GL_DEBIT_CODE.equals(debitCreditCode) || Constants.GL_BUDGET_CODE.equals(debitCreditCode) ) {
      bal.setCurrentBudgetBalanceAmount(bal.getCurrentBudgetBalanceAmount().add(amount));
    }
  }

  public String getDestinationName() {
    return "GL_SF_BALANCES_T";
  }
}
