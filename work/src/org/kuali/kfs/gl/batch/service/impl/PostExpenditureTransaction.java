/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Date;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionAccount;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionAccountDao;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionTypeDao;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.IndirectCostRecoveryService;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.CachingDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * This implementation of PostTransaction creates ExpenditureTransactions, temporary records used
 * for ICR generation
 */
@Transactional
public class PostExpenditureTransaction implements IndirectCostRecoveryService, PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostExpenditureTransaction.class);

    private IndirectCostRecoveryExclusionAccountDao indirectCostRecoveryExclusionAccountDao;
    private IndirectCostRecoveryExclusionTypeDao indirectCostRecoveryExclusionTypeDao;
    private CachingDao cachingDao;
    
    public void setIndirectCostRecoveryExclusionAccountDao(IndirectCostRecoveryExclusionAccountDao icrea) {
        indirectCostRecoveryExclusionAccountDao = icrea;
    }

    public void setIndirectCostRecoveryExclusionTypeDao(IndirectCostRecoveryExclusionTypeDao icrea) {
        indirectCostRecoveryExclusionTypeDao = icrea;
    }

    /**
     * Creates a PostExpenditureTransaction instance
     */
    public PostExpenditureTransaction() {
        super();
    }

    /**
     * This will determine if this transaction is an ICR eligible transaction
     * 
     * @param objectType the object type of the transaction
     * @param account the account of the transaction
     * @param subAccountNumber the subAccountNumber of the transaction
     * @param objectCode the object code of the transaction
     * @param universityFiscalPeriodCode the accounting period code of the transaction
     * @return true if the transaction is an ICR transaction and therefore should have an expenditure transaction created for it; false if otherwise
     */
    public boolean isIcrTransaction(ObjectType objectType, Account account, String subAccountNumber, ObjectCode objectCode, String universityFiscalPeriodCode) {
        LOG.debug("isIcrTransaction() started");
        
        // Is the ICR indicator set?
        // Is the period code a non-balance period? If so, continue, if not, we aren't posting this transaction
        if (objectType.isFinObjectTypeIcrSelectionIndicator() && (!KFSConstants.PERIOD_CODE_ANNUAL_BALANCE.equals(universityFiscalPeriodCode)) && (!KFSConstants.PERIOD_CODE_BEGINNING_BALANCE.equals(universityFiscalPeriodCode)) && (!KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE.equals(universityFiscalPeriodCode))) {
            // Continue on the posting process

            // Check the sub account type code. A21 subaccounts with the type of CS don't get posted
            A21SubAccount a21SubAccount = cachingDao.getA21SubAccount(account.getChartOfAccountsCode(), account.getAccountNumber(), subAccountNumber);
            String financialIcrSeriesIdentifier;
            String indirectCostRecoveryTypeCode;
            
            // first, do a check to ensure that if the sub-account is set up for ICR, that the account is also set up for ICR
            if (a21SubAccount != null) {
                if (StringUtils.hasText(a21SubAccount.getFinancialIcrSeriesIdentifier()) && StringUtils.hasText(a21SubAccount.getIndirectCostRecoveryTypeCode())) {
                    // the sub account is set up for ICR, make sure that the corresponding account is as well, just for validation purposes
                    if (!StringUtils.hasText(account.getFinancialIcrSeriesIdentifier()) || !StringUtils.hasText(account.getAcctIndirectCostRcvyTypeCd())) {
                        throw new IncorrectIndirectCostRecoveryMetadataException();
                    }
                    // A21SubAccount info set up correctly
                    financialIcrSeriesIdentifier = a21SubAccount.getFinancialIcrSeriesIdentifier();
                    indirectCostRecoveryTypeCode = a21SubAccount.getIndirectCostRecoveryTypeCode();
                }
                else {
                    // we had an A21SubAccount, but it was not set up for ICR, use account values instead
                    financialIcrSeriesIdentifier = account.getFinancialIcrSeriesIdentifier();
                    indirectCostRecoveryTypeCode = account.getAcctIndirectCostRcvyTypeCd();
                }
            }
            else {
                // no A21SubAccount found, default to using Account
                financialIcrSeriesIdentifier = account.getFinancialIcrSeriesIdentifier();
                indirectCostRecoveryTypeCode = account.getAcctIndirectCostRcvyTypeCd();
            }
            
            // the ICR Series identifier set?
            if (!StringUtils.hasText(financialIcrSeriesIdentifier)) {
                LOG.debug("isIcrTransaction() Not ICR Account");
                return false;
            }
            
            if ((a21SubAccount != null) && KFSConstants.SubAccountType.COST_SHARE.equals(a21SubAccount.getSubAccountTypeCode())) {
                // No need to post this
                LOG.debug("isIcrTransaction() A21 subaccounts with type of CS - not posted");
                return false;
            }

            // Do we exclude this account from ICR because account/object is in the table?
            IndirectCostRecoveryExclusionAccount excAccount = indirectCostRecoveryExclusionAccountDao.getByPrimaryKey(account.getChartOfAccountsCode(), account.getAccountNumber(), objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode());
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

                // TODO: use type 10 constant
                if ((!StringUtils.hasText(indirectCostRecoveryTypeCode)) || KFSConstants.MONTH10.equals(indirectCostRecoveryTypeCode)) {
                    // No need to post this
                    LOG.debug("isIcrTransaction() ICR type is null or 10 - not posted");
                    return false;
                }

                // If the type is excluded, don't post. First step finds the top level object code...
                ObjectCode currentObjectCode = objectCode;
                boolean foundIt = false;
                while (!foundIt) {
                    if (currentObjectCode.getChartOfAccountsCode().equals(currentObjectCode.getReportsToChartOfAccountsCode()) && currentObjectCode.getFinancialObjectCode().equals(currentObjectCode.getReportsToFinancialObjectCode())) {
                        foundIt = true;
                    }
                    else {
                        currentObjectCode.setReportsToFinancialObject(cachingDao.getObjectCode(currentObjectCode.getUniversityFiscalYear(), currentObjectCode.getReportsToChartOfAccountsCode(), currentObjectCode.getReportsToFinancialObjectCode()));
                        if (ObjectUtils.isNull(currentObjectCode.getReportsToFinancialObject())) {
                            foundIt = true;
                        }
                        else {
                            currentObjectCode = currentObjectCode.getReportsToFinancialObject();
                        }
                    }
                }
                // second step checks if the top level object code is to be excluded...
                IndirectCostRecoveryExclusionType excType = indirectCostRecoveryExclusionTypeDao.getByPrimaryKey(indirectCostRecoveryTypeCode, currentObjectCode.getChartOfAccountsCode(), currentObjectCode.getFinancialObjectCode());
                
                if(excType != null && excType.isActive()) {
                	if(ObjectUtils.isNotNull(excType.getIndirectCostRecoveryType())) {
                		if(excType.getIndirectCostRecoveryType().isActive()) {
                			// No need to post this
                            LOG.debug("isIcrTransaction() ICR Excluded type - not posted");
                            return false;
                		}
                	} else {
            			// No need to post this
                        LOG.debug("isIcrTransaction() ICR Excluded type - not posted");
                        return false;
                	}
                }
                return true;
            }
        }
        else {
            // Don't need to post anything
            LOG.debug("isIcrTransaction() invalid period code - not posted");
            return false;
        }
    }

    /**
     * If the transaction is a valid ICR transaction, posts an expenditure transaction record for the transaction
     * 
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @return the accomplished post type
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        try {
            if (isIcrTransaction(t.getObjectType(), t.getAccount(), t.getSubAccountNumber(), t.getFinancialObject(), t.getUniversityFiscalPeriodCode())) {
                return postTransaction(t, mode);
            }
        }
        catch (IncorrectIndirectCostRecoveryMetadataException e) {
            // HACK ALERT: the code that calls this method requires that an error message begin with GeneralLedgerConstants.ERROR_CODE, which is "E"
            return GeneralLedgerConstants.ERROR_CODE + "rror - excluding transaction from Indirect Cost Recovery because Sub-Account is set up for ICR, but Account is not.";
        }
        return GeneralLedgerConstants.EMPTY_CODE;
    }

    /**
     * Actually posts the transaction to the appropriate expenditure transaction record
     * 
     * @param t the transaction to post
     * @param mode the mode of the poster as it is currently running
     * @return the accomplished post type
     */
    private String postTransaction(Transaction t, int mode) {
        LOG.debug("postTransaction() started");

        String returnCode = GeneralLedgerConstants.UPDATE_CODE;
        ExpenditureTransaction et = cachingDao.getExpenditureTransaction(t);
        if (et == null) {
            LOG.warn("Posting expenditure transation");
            et = new ExpenditureTransaction(t);
            returnCode = GeneralLedgerConstants.INSERT_CODE;
        }

        if (org.apache.commons.lang.StringUtils.isBlank(t.getOrganizationReferenceId())) {
            et.setOrganizationReferenceId(GeneralLedgerConstants.getDashOrganizationReferenceId());
        }

        if (KFSConstants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode()) || KFSConstants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
            et.setAccountObjectDirectCostAmount(et.getAccountObjectDirectCostAmount().add(t.getTransactionLedgerEntryAmount()));
        }
        else {
            et.setAccountObjectDirectCostAmount(et.getAccountObjectDirectCostAmount().subtract(t.getTransactionLedgerEntryAmount()));
        }

        if (returnCode.equals(GeneralLedgerConstants.INSERT_CODE)) {
            cachingDao.insertExpenditureTransaction(et);
        } else {
            cachingDao.updateExpenditureTransaction(et);
        }

        return returnCode;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(ExpenditureTransaction.class).getFullTableName();
    }
    
    protected class IncorrectIndirectCostRecoveryMetadataException extends RuntimeException {
    }

    public void setCachingDao(CachingDao cachingDao) {
        this.cachingDao = cachingDao;
    }
}
