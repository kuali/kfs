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

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Transaction;

/**
 * @author jsissom
 *
 */
public class VerifyTransactionImpl implements VerifyTransaction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VerifyTransactionImpl.class);

  public VerifyTransactionImpl() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.batch.poster.VerifyTransaction#verifyTransaction(org.kuali.module.gl.bo.Transaction)
   */
  public List verifyTransaction(Transaction t) {
    LOG.debug("verifyTransaction() started");

    // List of error messages for the current transaction
    List errors = new ArrayList();

    // Check the chart of accounts code
    if ( t.getChart() == null ) {
      errors.add("fin_coa_cd not found in ca_chart_t");
    }

    // Check the account
    if ( t.getAccount() == null ) {
      errors.add("account_nbr not found in ca_account_t");
    }

    // Check the object type
    if ( t.getObjectType() == null ) {
      errors.add("fin_obj_typ_cd not found in ca_obj_type_t");
    }

    // Check the balance type
    if ( t.getBalanceType() == null ) {
      errors.add("fin_balance_typ_cd not found in ca_balance_type_t");
    }

    // Check the fiscal year
    if ( t.getOption() == null ) {
      errors.add("univ_fiscal_yr not found in fs_option_t");
    }

    // Check the debit/credit code (only if we have a valid balance type code)
    if ( t.getTransactionDebitCreditCode() == null ) {
      errors.add("trn_debit_crdt_cd cannot be null");
    } else {
      if ( t.getBalanceType() != null ) {
        if ( t.getBalanceType().isFinancialOffsetGenerationIndicator() ) {
          if ( (! Constants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode())) && (! Constants.GL_CREDIT_CODE.equals(t.getTransactionDebitCreditCode())) ) {
            errors.add("trn_debit_crdt_cd must be " + Constants.GL_DEBIT_CODE + " or " + Constants.GL_CREDIT_CODE + " for this fin_balance_typ_cd");
          }
        } else {
          if ( ! Constants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode()) ) {
            errors.add("trn_debit_crdt_cd must be '" + Constants.GL_BUDGET_CODE + "' for this fin_balance_typ_cd");
          }
        }
      }
    }

    return errors;
  }
}
