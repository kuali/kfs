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

import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.AccountBalanceDao;

/**
 * @author jsissom
 *
 */
public class PostGlAccountBalance implements PostTransaction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostGlAccountBalance.class);

  private AccountBalanceDao accountBalanceDao;

  public void setAccountBalanceDao(AccountBalanceDao abd) {
    accountBalanceDao = abd;
  }

  public PostGlAccountBalance() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
   */
  public String post(Transaction t,int mode,Date postDate) {
    LOG.debug("post() started");

    if ( (t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd()) ||
        t.getFinancialBalanceTypeCode().equals(t.getOption().getBudgetCheckingBalanceTypeCd()) || 
        t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) ||
        t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) ||
        t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) ||
        t.getFinancialBalanceTypeCode().equals("CE")) && (! t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjectTypeFundBalanceCd())) ){
      // We are posting this transaction
      String returnCode = "U";

      // Load it
      AccountBalance ab = accountBalanceDao.getByTransaction(t);

      if ( ab == null ) {
        returnCode = "I";
        ab = new AccountBalance(t);
      }

      ab.setTimestamp(new java.sql.Date(postDate.getTime()));

      if ( t.getFinancialBalanceTypeCode().equals(t.getOption().getBudgetCheckingBalanceTypeCd()) ) {
        ab.setCurrentBudgetLineBalanceAmount(ab.getCurrentBudgetLineBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
      } else if ( t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd()) ) {
        if ( t.getObjectType().getFinObjectTypeDebitcreditCd().equals(t.getTransactionDebitCreditCode()) ||
            ( ( !t.getBalanceType().isFinancialOffsetGenerationIndicator()) && " ".equals(t.getTransactionDebitCreditCode()) ) ) {
          ab.setAccountLineActualsBalanceAmount(ab.getAccountLineActualsBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
        } else {
          ab.setAccountLineActualsBalanceAmount(ab.getAccountLineActualsBalanceAmount().subtract(t.getTransactionLedgerEntryAmount()));          
        }
      } else if ( t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) ||
        t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) ||
        t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) ||
        t.getFinancialBalanceTypeCode().equals("CE") ) {
        if ( t.getObjectType().getFinObjectTypeDebitcreditCd().equals(t.getTransactionDebitCreditCode()) ||
            ( (! t.getBalanceType().isFinancialOffsetGenerationIndicator()) &&
                " ".equals(t.getTransactionDebitCreditCode()) ) ) {
          ab.setAccountLineEncumbranceBalanceAmount(ab.getAccountLineEncumbranceBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
        } else {
          ab.setAccountLineEncumbranceBalanceAmount(ab.getAccountLineEncumbranceBalanceAmount().subtract(t.getTransactionLedgerEntryAmount()));
        }
      } else {
        // Don't need to post
        return "";
      }

      accountBalanceDao.save(ab);

      return returnCode;      
    } else {
      // Don't need to post
      return "";
    }
  }

  public String getDestinationName() {
    return "GL_ACCT_BALANCES_T";
  }
}
