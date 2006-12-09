/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import org.kuali.Constants;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.IndirectCostRecoveryExclusionAccount;
import org.kuali.module.chart.bo.IndirectCostRecoveryExclusionType;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.dao.A21SubAccountDao;
import org.kuali.module.chart.dao.IndirectCostRecoveryExclusionAccountDao;
import org.kuali.module.chart.dao.IndirectCostRecoveryExclusionTypeDao;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.ExpenditureTransaction;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ExpenditureTransactionDao;
import org.kuali.module.gl.service.IcrTransaction;
import org.springframework.util.StringUtils;

/**
 * 
 * 
 */
public class PostExpenditureTransaction implements IcrTransaction, PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostExpenditureTransaction.class);

    private A21SubAccountDao a21SubAccountDao;
    private IndirectCostRecoveryExclusionAccountDao indirectCostRecoveryExclusionAccountDao;
    private IndirectCostRecoveryExclusionTypeDao indirectCostRecoveryExclusionTypeDao;
    private ExpenditureTransactionDao expenditureTransactionDao;

    public void setA21SubAccountDao(A21SubAccountDao asad) {
        a21SubAccountDao = asad;
    }

    public void setIndirectCostRecoveryExclusionAccountDao(IndirectCostRecoveryExclusionAccountDao icrea) {
        indirectCostRecoveryExclusionAccountDao = icrea;
    }

    public void setIndirectCostRecoveryExclusionTypeDao(IndirectCostRecoveryExclusionTypeDao icrea) {
        indirectCostRecoveryExclusionTypeDao = icrea;
    }

    public void setExpenditureTransactionDao(ExpenditureTransactionDao etd) {
        expenditureTransactionDao = etd;
    }

    /**
     * 
     */
    public PostExpenditureTransaction() {
        super();
    }

    /**
     * This will determine if this transaction is an ICR eligible transaction
     * 
     * @return
     */
    public boolean isIcrTransaction(ObjectType objectType, Account account, String subAccountNumber, ObjectCode objectCode, String universityFiscalPeriodCode) {
        LOG.debug("isIcrTransaction() started");

        // Is the ICR indicator set and the ICR Series identifier set?
        // Is the period code a non-balance period? If so, continue, if not, we aren't posting this transaction
        if (objectType.isFinObjectTypeIcrSelectionIndicator() && StringUtils.hasText(account.getFinancialIcrSeriesIdentifier()) && (!Constants.PERIOD_CODE_ANNUAL_BALNCE.equals(universityFiscalPeriodCode)) && (!Constants.PERIOD_CODE_BEGINNING_BALNCE.equals(universityFiscalPeriodCode)) && (!Constants.PERIOD_CODE_CG_BEGINNING_BALNCE.equals(universityFiscalPeriodCode))) {
            // Continue on the posting process

            // Check the sub account type code. A21 subaccounts with the type of CS don't get posted
            A21SubAccount a21SubAccount = a21SubAccountDao.getByPrimaryKey(account.getChartOfAccountsCode(), account.getAccountNumber(), subAccountNumber);
            if ((a21SubAccount != null) && Constants.COST_SHARE.equals(a21SubAccount.getSubAccountTypeCode())) {
                // No need to post this
                LOG.debug("isIcrTransaction() A21 subaccounts with type of CS - not posted");
                return false;
            }

            // Do we exclude this account from ICR because account/object is in the table?
            IndirectCostRecoveryExclusionAccount excAccount = indirectCostRecoveryExclusionAccountDao.getByPrimaryKey(account.getChartOfAccountsCode(), account.getAccountNumber(), objectCode.getReportsToChartOfAccountsCode(), objectCode.getReportsToFinancialObjectCode());
            if (excAccount != null) {
                // No need to post this
                LOG.debug("isIcrTransaction() ICR Excluded account - not posted");
                return false;
            }

            // How about if we just look based on account?
            if (indirectCostRecoveryExclusionAccountDao.existByAccount(account.getChartOfAccountsCode(), account.getAccountNumber())) {
                return true;
            }
            else {
                // If the ICR type code is empty or 10, don't post
                if ((!StringUtils.hasText(account.getAcctIndirectCostRcvyTypeCd())) || Constants.MONTH10.equals(account.getAcctIndirectCostRcvyTypeCd())) {
                    // No need to post this
                    LOG.debug("isIcrTransaction() ICR type is null or 10 - not posted");
                    return false;
                }

                // If the type is excluded, don't post
                IndirectCostRecoveryExclusionType excType = indirectCostRecoveryExclusionTypeDao.getByPrimaryKey(account.getAcctIndirectCostRcvyTypeCd(), objectCode.getReportsToChartOfAccountsCode(), objectCode.getReportsToFinancialObjectCode());
                if (excType != null) {
                    // No need to post this
                    LOG.debug("isIcrTransaction() ICR Excluded type - not posted");
                    return false;
                }
                return true;
            }
        }
        else {
            // Don't need to post anything
            LOG.debug("isIcrTransaction() Not ICR account or invalid period code - not posted");
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        if (isIcrTransaction(t.getObjectType(), t.getAccount(), t.getSubAccountNumber(), t.getFinancialObject(), t.getUniversityFiscalPeriodCode())) {
            return postTransaction(t, mode, postDate);
        }
        return "";
    }

    private String postTransaction(Transaction t, int mode, Date postDate) {
        LOG.debug("postTransaction() started");

        String returnCode = "U";

        ExpenditureTransaction et = expenditureTransactionDao.getByTransaction(t);
        if (et == null) {
            et = new ExpenditureTransaction(t);
            returnCode = "I";
        }

        if (t.getOrganizationReferenceId() == null) {
            et.setOrganizationReferenceId(GLConstants.DASH_ORGANIZATION_REFERENCE_ID);
        }

        if (Constants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode()) || Constants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
            et.setAccountObjectDirectCostAmount(et.getAccountObjectDirectCostAmount().add(t.getTransactionLedgerEntryAmount()));
        }
        else {
            et.setAccountObjectDirectCostAmount(et.getAccountObjectDirectCostAmount().subtract(t.getTransactionLedgerEntryAmount()));
        }

        expenditureTransactionDao.save(et);

        return returnCode;
    }

    public String getDestinationName() {
        return "GL_EXPEND_TRN_T";
    }
}
