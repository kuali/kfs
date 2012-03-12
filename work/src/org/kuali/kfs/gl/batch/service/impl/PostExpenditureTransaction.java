/*
 * Copyright 2005-2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionAccount;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionAccountDao;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryExclusionTypeDao;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterIndirectCostRecoveryEntriesStep;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.IndirectCostRecoveryService;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * This implementation of PostTransaction creates ExpenditureTransactions, temporary records used
 * for ICR generation
 */
@Transactional
public class PostExpenditureTransaction implements IndirectCostRecoveryService, PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostExpenditureTransaction.class);
    
    private static final String INDIRECT_COST_TYPES_PARAMETER = "INDIRECT_COST_TYPES";
    private static final String INDIRECT_COST_FISCAL_PERIODS_PARAMETER = "INDIRECT_COST_FISCAL_PERIODS";
    private static final String ICR_EXCLUSIONS_AT_TRANSACTION_AND_TOP_LEVEL_ONLY_PARAMETER_NAME = "ICR_EXCLUSIONS_AT_TRANSACTION_AND_TOP_LEVEL_ONLY_IND";

    private IndirectCostRecoveryExclusionAccountDao indirectCostRecoveryExclusionAccountDao;
    private IndirectCostRecoveryExclusionTypeDao indirectCostRecoveryExclusionTypeDao;
    private AccountingCycleCachingService accountingCycleCachingService;
    private PersistenceStructureService persistenceStructureService;
    private ParameterService parameterService;
    
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
     * @param transaction the transaction which is being determined to be ICR or not
     * @param objectType the object type of the transaction
     * @param account the account of the transaction
     * @param objectCode the object code of the transaction
     * @return true if the transaction is an ICR transaction and therefore should have an expenditure transaction created for it; false if otherwise
     */
    public boolean isIcrTransaction(Transaction transaction, ReportWriterService reportWriterService) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isIcrTransaction() started");
        }
        
        // Is the ICR indicator set?
        // Is the period code a non-balance period, as specified by KFS-GL / Poster Indirect Cost Recoveries Step / INDIRECT_COST_FISCAL_PERIODS? If so, continue, if not, we aren't posting this transaction
        if (transaction.getObjectType().isFinObjectTypeIcrSelectionIndicator() && /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(PosterIndirectCostRecoveryEntriesStep.class, PostExpenditureTransaction.INDIRECT_COST_FISCAL_PERIODS_PARAMETER, transaction.getUniversityFiscalPeriodCode()).evaluationSucceeds()) {
            // Continue on the posting process

            // Check the sub account type code. A21 sub-accounts with the type of CS don't get posted
            A21SubAccount a21SubAccount = accountingCycleCachingService.getA21SubAccount(transaction.getAccount().getChartOfAccountsCode(), transaction.getAccount().getAccountNumber(), transaction.getSubAccountNumber());
            String financialIcrSeriesIdentifier;
            String indirectCostRecoveryTypeCode;
            
            // first, do a check to ensure that if the sub-account is set up for ICR, that the account is also set up for ICR
            if (a21SubAccount != null) {
                if (StringUtils.hasText(a21SubAccount.getFinancialIcrSeriesIdentifier()) && StringUtils.hasText(a21SubAccount.getIndirectCostRecoveryTypeCode())) {
                    // the sub account is set up for ICR, make sure that the corresponding account is as well, just for validation purposes
                    if (!StringUtils.hasText(transaction.getAccount().getFinancialIcrSeriesIdentifier()) || !StringUtils.hasText(transaction.getAccount().getAcctIndirectCostRcvyTypeCd())) {
                        List<Message> warnings = new ArrayList<Message>();
                        warnings.add(new Message("Warning - excluding transaction from Indirect Cost Recovery because Sub-Account is set up for ICR, but Account is not.", Message.TYPE_WARNING));
                        reportWriterService.writeError(transaction, warnings);
                    }
                }
                
                if (StringUtils.hasText(a21SubAccount.getFinancialIcrSeriesIdentifier()) && StringUtils.hasText(a21SubAccount.getIndirectCostRecoveryTypeCode())) {
                    // A21SubAccount info set up correctly
                    financialIcrSeriesIdentifier = a21SubAccount.getFinancialIcrSeriesIdentifier();
                    indirectCostRecoveryTypeCode = a21SubAccount.getIndirectCostRecoveryTypeCode();
                }
                else {
                    // we had an A21SubAccount, but it was not set up for ICR, use account values instead
                    financialIcrSeriesIdentifier = transaction.getAccount().getFinancialIcrSeriesIdentifier();
                    indirectCostRecoveryTypeCode = transaction.getAccount().getAcctIndirectCostRcvyTypeCd();
                }
            }
            else {
                // no A21SubAccount found, default to using Account
                financialIcrSeriesIdentifier = transaction.getAccount().getFinancialIcrSeriesIdentifier();
                indirectCostRecoveryTypeCode = transaction.getAccount().getAcctIndirectCostRcvyTypeCd();
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

            // do we have an exclusion by type or by account?  then we don't have to post no expenditure transaction
            final boolean selfAndTopLevelOnly = getParameterService().getParameterValueAsBoolean(PosterIndirectCostRecoveryEntriesStep.class, PostExpenditureTransaction.ICR_EXCLUSIONS_AT_TRANSACTION_AND_TOP_LEVEL_ONLY_PARAMETER_NAME);
            if (excludedByType(indirectCostRecoveryTypeCode, transaction.getFinancialObject(), selfAndTopLevelOnly)) return false;
            if (excludedByAccount(transaction.getAccount(), transaction.getFinancialObject(), selfAndTopLevelOnly)) return false;

            return true;  // still here?  then I guess we don't have an exclusion
        }
        else {
            // Don't need to post anything
            LOG.debug("isIcrTransaction() invalid period code - not posted");
            return false;
        }
    }
    
    /**
     * Determines if there's an exclusion by type record existing for the given ICR type code and object code or object codes within the object code's reportsTo hierarchy
     * @param indirectCostRecoveryTypeCode the ICR type code to check
     * @param objectCode the object code to check for, as well as check the reports-to hierarchy
     * @param selfAndTopLevelOnly whether only the given object code and the top level object code should be checked
     * @return true if the transaction with the given ICR type code and object code have an exclusion by type record, false otherwise
     */
    protected boolean excludedByType(String indirectCostRecoveryTypeCode, ObjectCode objectCode, boolean selfAndTopLevelOnly) {
        // If the ICR type code is empty or excluded by the KFS-GL / Poster Indirect Cost Recoveries Step / INDIRECT_COST_TYPES parameter, don't post
        if ((!StringUtils.hasText(indirectCostRecoveryTypeCode)) || !/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(PosterIndirectCostRecoveryEntriesStep.class, PostExpenditureTransaction.INDIRECT_COST_TYPES_PARAMETER, indirectCostRecoveryTypeCode).evaluationSucceeds()) {
            // No need to post this
            if (LOG.isDebugEnabled()) {
                LOG.debug("isIcrTransaction() ICR type is null or excluded by the KFS-GL / Poster Indirect Cost Recoveries Step / INDIRECT_COST_TYPES parameter - not posted");
            }
            return true;
        }
        
        if (hasExclusionByType(indirectCostRecoveryTypeCode, objectCode)) return true;
        
        ObjectCode currentObjectCode = getReportsToObjectCode(objectCode);
        while (currentObjectCode != null && !currentObjectCode.isReportingToSelf()) {
            if (!selfAndTopLevelOnly && hasExclusionByType(indirectCostRecoveryTypeCode, currentObjectCode)) return true;
            
            currentObjectCode = getReportsToObjectCode(currentObjectCode);
        }
        if (currentObjectCode != null && hasExclusionByType(indirectCostRecoveryTypeCode, currentObjectCode)) return true; // we must be top level if the object code isn't null
        
        return false;
    }
    
    /**
     * Determines if the given object code and indirect cost recovery type code have an exclusion by type record associated with them
     * @param indirectCostRecoveryTypeCode the indirect cost recovery type code to check
     * @param objectCode the object code to check
     * @return true if there's an exclusion by type record for this type code and object code
     */
    protected boolean hasExclusionByType(String indirectCostRecoveryTypeCode, ObjectCode objectCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.ACCOUNT_INDIRECT_COST_RECOVERY_TYPE_CODE, indirectCostRecoveryTypeCode);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        final IndirectCostRecoveryExclusionType excType = (IndirectCostRecoveryExclusionType)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(IndirectCostRecoveryExclusionType.class, keys);
        return !ObjectUtils.isNull(excType) && excType.isActive();
    }
    
    /**
     * Determine if the given account and object code have an exclusion by account associated which should prevent this transaction from posting an ExpenditureTransaction
     * @param account account to check
     * @param objectCode object code to check
     * @param selfAndTopLevelOnly if only the given object code and the top level object code should seek exclusion by account records or not
     * @return true if the given account and object code have an associated exclusion by account, false otherwise
     */
    protected boolean excludedByAccount(Account account, ObjectCode objectCode, boolean selfAndTopLevelOnly) {
        if (hasExclusionByAccount(account, objectCode)) return true;
        
        ObjectCode currentObjectCode = getReportsToObjectCode(objectCode);
        while (currentObjectCode != null && !currentObjectCode.isReportingToSelf()) {
            if (!selfAndTopLevelOnly && hasExclusionByAccount(account, currentObjectCode)) return true;
            
            currentObjectCode = getReportsToObjectCode(currentObjectCode);
        }
        if (currentObjectCode != null && hasExclusionByAccount(account, currentObjectCode)) return true; // we must be top level if we got this far
        
        return false;
    }
    
    /**
     * Determines if there's an exclusion by account record for the given account and object code
     * @param account the account to check
     * @param objectCode the object code to check
     * @return true if the given account and object code have an exclusion by account record, false otherwise
     */
    protected boolean hasExclusionByAccount(Account account, ObjectCode objectCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CHART_OF_ACCOUNT_CODE, objectCode.getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        final IndirectCostRecoveryExclusionAccount excAccount = (IndirectCostRecoveryExclusionAccount)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(IndirectCostRecoveryExclusionAccount.class, keys);
        
        return !ObjectUtils.isNull(excAccount);
    }
    
    /**
     * Determines if the given object code has a valid reports-to hierarchy
     * @param objectCode the object code to check
     * @return true if the object code has a valid reports-to hierarchy with no nulls; false otherwise
     */
    protected boolean hasValidObjectCodeReportingHierarchy(ObjectCode objectCode) {
        ObjectCode currentObjectCode = objectCode;
        while (hasValidReportsToFields(currentObjectCode) && !currentObjectCode.isReportingToSelf()) {
            currentObjectCode = getReportsToObjectCode(currentObjectCode);
            if (ObjectUtils.isNull(currentObjectCode) || !currentObjectCode.isActive()) {
                return false;
            }
        }
        if (!hasValidReportsToFields(currentObjectCode)) return false;
        return true;
    }
    
    /**
     * Determines if the given object code has all the fields it would need for a strong and healthy reports to hierarchy
     * @param objectCode the object code to give a little check
     * @return true if everything is good, false if the object code has a bad, rotted reports to hierarchy
     */
    protected boolean hasValidReportsToFields(ObjectCode objectCode) {
        return !org.apache.commons.lang.StringUtils.isBlank(objectCode.getReportsToChartOfAccountsCode()) && !org.apache.commons.lang.StringUtils.isBlank(objectCode.getReportsToFinancialObjectCode());
    }
    
    /**
     * Uses the caching DAO instead of regular OJB to find the reports-to object code
     * @param objectCode the object code to get the reporter of
     * @return the reports to object code, or, if that is impossible, null
     */
    protected ObjectCode getReportsToObjectCode(ObjectCode objectCode) {
       return accountingCycleCachingService.getObjectCode(objectCode.getUniversityFiscalYear(), objectCode.getReportsToChartOfAccountsCode(), objectCode.getReportsToFinancialObjectCode()); 
    }

    /**
     * If the transaction is a valid ICR transaction, posts an expenditure transaction record for the transaction
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

        if (ObjectUtils.isNull(t.getFinancialObject()) || !hasValidObjectCodeReportingHierarchy(t.getFinancialObject())) {
            // I agree with the commenter below...this seems totally lame
            return GeneralLedgerConstants.ERROR_CODE + ": Warning - excluding transaction from Indirect Cost Recovery because "+t.getUniversityFiscalYear().toString()+"-"+t.getChartOfAccountsCode()+"-"+t.getFinancialObjectCode()+" has an invalid reports to hierarchy (either has an non-existent object or an inactive object)";
        }
        else if (isIcrTransaction(t, posterReportWriterService)) {
            return postTransaction(t, mode);
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
    protected String postTransaction(Transaction t, int mode) {
        LOG.debug("postTransaction() started");

        String returnCode = GeneralLedgerConstants.UPDATE_CODE;
        ExpenditureTransaction et = accountingCycleCachingService.getExpenditureTransaction(t);
        if (et == null) {
            LOG.debug("Posting expenditure transation");
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
            //TODO: remove this log statement. Added to troubleshoot FSKD-194.
            LOG.info("Inserting a GLEX record. Transaction:"+t);
            accountingCycleCachingService.insertExpenditureTransaction(et);
        } else {
            //TODO: remove this log statement. Added to troubleshoot FSKD-194.
            LOG.info("Updating a GLEX record. Transaction:"+t);
            accountingCycleCachingService.updateExpenditureTransaction(et);
        }

        return returnCode;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return persistenceStructureService.getTableName(ExpenditureTransaction.class);
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
}
