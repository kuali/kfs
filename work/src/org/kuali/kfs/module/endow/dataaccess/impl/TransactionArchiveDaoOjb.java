/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeEndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeTransaction;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class TransactionArchiveDaoOjb extends PlatformAwareDaoBaseOjb implements TransactionArchiveDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionArchiveDaoOjb.class);

    /**
     * @@see {@link org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getAllTransactionArchives(Date)
     */
    public Collection<TransactionArchive> getAllTransactionArchives(java.util.Date postedDate) {
        Collection<TransactionArchive> transactionArchives = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, postedDate);

        // sort the data on these columns....
        QueryByCriteria qbc = QueryFactory.newQuery(TransactionArchive.class, criteria);

        qbc.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE);
        qbc.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_SUB_TYPE_CODE);
        qbc.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR);
        qbc.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_KEM_ID);
        qbc.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE);

        transactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

        return transactionArchives;
    }

    /**
     * @@see {@link org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getAllTransactionArchives()
     */
    public Collection<TransactionArchive> getAllTransactionArchives() {
        Collection<TransactionArchive> transactionArchives = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addNotNull(EndowPropertyConstants.DOCUMENT_NUMBER);

        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);
        transactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return transactionArchives;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getByPrimaryKey(String, int, String)
     */
    public TransactionArchive getByPrimaryKey(String documentNumber, int lineNumber, String lineTypeCode) {
        TransactionArchive transactionArchive = null;

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, lineNumber);
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, lineTypeCode);

        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);
        transactionArchive = (TransactionArchive) getPersistenceBrokerTemplate().getObjectByQuery(query);

        return transactionArchive;
    }

    /**
     * Prepares the criteria to select the records from END_TRAN_ARCHV_T table
     * 
     * @param feeMethod
     * @param feeMethodCodeForTypeCodes the feeMethod code that should be passed in as uppercase or not depending on FeeTransaction
     *        DD definition for fee method code
     * @param feeMethodCodeForEtranCodes the feeMethod code that should be passed in as uppercase or not depending on
     *        FeeEndowmentTransactionCode DD definition for fee method code
     * @return
     */
    protected Collection<TransactionArchive> getTransactionArchivesForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes) {
        Collection<TransactionArchive> transactionArchives = new ArrayList();

        Collection incomePrincipalValues = new ArrayList();
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);

        Criteria criteria = new Criteria();

        if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME_AND_PRINCIPAL)) {
            criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR, incomePrincipalValues);
        }
        else {
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME)) {
                criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);

            }
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL)) {
                criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);
            }
        }

        if (feeMethod.getFeeByTransactionType() && feeMethod.getFeeByETranCode()) {
            criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, getTypeCodes(feeMethodCodeForTypeCodes));
            criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, getETranCodes(feeMethodCodeForEtranCodes));
        }
        else {
            if (feeMethod.getFeeByTransactionType()) {
                criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, getTypeCodes(feeMethodCodeForTypeCodes));
            }

            if (feeMethod.getFeeByETranCode()) {
                criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, getETranCodes(feeMethodCodeForEtranCodes));
            }
        }

        if (feeMethod.getFeeByTransactionType() || feeMethod.getFeeByETranCode()) {
            criteria.addGreaterThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, feeMethod.getFeeLastProcessDate());
        }

        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);

        transactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return transactionArchives;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesCountForTransactions(org.kuali.kfs.module.endow.businessobject.FeeMethod,
     *      java.lang.String, java.lang.String)
     */
    public long getTransactionArchivesCountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes) {
        long totalTransactionArchives = 0;
        totalTransactionArchives = getTransactionArchivesForTransactions(feeMethod, feeMethodCodeForTypeCodes, feeMethodCodeForEtranCodes).size();

        return totalTransactionArchives;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesIncomeCashAmountForTransactions(org.kuali.kfs.module.endow.businessobject.FeeMethod,
     *      java.lang.String, java.lang.String)
     */
    public BigDecimal getTransactionArchivesIncomeCashAmountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes) {
        BigDecimal incomeCashAmount = BigDecimal.ZERO;

        Collection<TransactionArchive> transactionArchives = new ArrayList();
        transactionArchives = getTransactionArchivesForTransactions(feeMethod, feeMethodCodeForTypeCodes, feeMethodCodeForEtranCodes);
        for (TransactionArchive transactionArchive : transactionArchives) {
            incomeCashAmount = incomeCashAmount.add(transactionArchive.getIncomeCashAmount());
        }

        return incomeCashAmount;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesPrincipalCashAmountForTransactions(org.kuali.kfs.module.endow.businessobject.FeeMethod,
     *      java.lang.String, java.lang.String)
     */
    public BigDecimal getTransactionArchivesPrincipalCashAmountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes) {
        BigDecimal principalCashAmount = BigDecimal.ZERO;

        Collection<TransactionArchive> transactionArchives = new ArrayList();
        transactionArchives = getTransactionArchivesForTransactions(feeMethod, feeMethodCodeForTypeCodes, feeMethodCodeForEtranCodes);
        for (TransactionArchive transactionArchive : transactionArchives) {
            principalCashAmount = principalCashAmount.add(transactionArchive.getPrincipalCashAmount());
        }

        return principalCashAmount;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesIncomeAndPrincipalCashAmountForTransactions(org.kuali.kfs.module.endow.businessobject.FeeMethod,
     *      java.lang.String, java.lang.String)
     */
    public HashMap<String, BigDecimal> getTransactionArchivesIncomeAndPrincipalCashAmountForTransactions(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes) {
        BigDecimal incomeCashAmount = BigDecimal.ZERO;
        BigDecimal principalCashAmount = BigDecimal.ZERO;

        HashMap<String, BigDecimal> incomeAndPrincipalCashAmounts = new HashMap();

        incomeAndPrincipalCashAmounts.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_CASH_AMOUNT, getTransactionArchivesIncomeCashAmountForTransactions(feeMethod, feeMethodCodeForTypeCodes, feeMethodCodeForEtranCodes));
        incomeAndPrincipalCashAmounts.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_PRINCIPAL_CASH_AMOUNT, getTransactionArchivesPrincipalCashAmountForTransactions(feeMethod, feeMethodCodeForTypeCodes, feeMethodCodeForEtranCodes));

        return incomeAndPrincipalCashAmounts;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesCountByDocumentTypeName(String, long)
     */
    public long getTransactionArchivesCountByDocumentTypeName(String feeMethodCode, Date transactionPostedDate) {
        long totalTransactionArchives = 0;

        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, getTypeCodes(feeMethodCode));
        criteria.addGreaterThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, transactionPostedDate);
        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);

        totalTransactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query).size();

        return totalTransactionArchives;
    }

    /**
     * Gets the document Type codes for a given feeMethodCode in END_FEE_TRAN_DOC_TYP_T table
     * 
     * @feeMethodCode FEE_MTH
     * @return typeCodes
     */
    protected Collection getTypeCodes(String feeMethodCode) {
        Collection typeCodes = new ArrayList();
        Collection<FeeTransaction> feeTransactions = new ArrayList();

        if (StringUtils.isNotBlank(feeMethodCode)) {
            Map<String, String> crit = new HashMap<String, String>();

            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_TRANSACTION_INCLUDE, EndowConstants.YES);
            QueryByCriteria query = QueryFactory.newQuery(FeeTransaction.class, criteria);

            feeTransactions = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeTransaction feeTransaction : feeTransactions) {
                typeCodes.add(feeTransaction.getDocumentTypeName());
            }
        }

        return typeCodes;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesCountByETranCode(java.lang.String,
     *      java.sql.Date, java.lang.String)
     */
    public long getTransactionArchivesCountByETranCode(String feeMethodCode, Date transactionPostedDate, String feeMethodCodeForEtranCodes) {
        long totalTransactionArchives = 0;

        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, getETranCodes(feeMethodCodeForEtranCodes));
        criteria.addGreaterThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, transactionPostedDate);
        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);

        totalTransactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query).size();

        return totalTransactionArchives;
    }

    /**
     * Gets the document Type codes for a given feeMethodCode in END_FEE_TRAN_DOC_TYP_T table
     * 
     * @feeMethodCode FEE_MTH
     * @return typeCodes
     */
    protected Collection getETranCodes(String feeMethodCode) {
        Collection<FeeEndowmentTransactionCode> feeEndowmentTransactions = new ArrayList();
        Collection etranCodes = new ArrayList();

        if (StringUtils.isNotBlank(feeMethodCode)) {
            Map<String, String> crit = new HashMap<String, String>();

            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_INCLUDE, EndowConstants.YES);
            QueryByCriteria query = QueryFactory.newQuery(FeeEndowmentTransactionCode.class, criteria);

            feeEndowmentTransactions = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeEndowmentTransactionCode feeEndowmentTransaction : feeEndowmentTransactions) {
                etranCodes.add(feeEndowmentTransaction.getEndowmentTransactionCode());
            }
        }

        return etranCodes;
    }


    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesCountByDocumentTypeNameAndETranCode(java.lang.String,
     *      java.sql.Date, java.lang.String, java.lang.String)
     */
    public long getTransactionArchivesCountByDocumentTypeNameAndETranCode(String feeMethodCode, Date transactionPostedDate, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes) {
        long totalTransactionArchives = 0;

        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, getTypeCodes(feeMethodCodeForTypeCodes));
        criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, getETranCodes(feeMethodCodeForEtranCodes));
        criteria.addGreaterThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, transactionPostedDate);
        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);

        totalTransactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query).size();

        return totalTransactionArchives;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesCountByIncomeOrPrincipal(String)
     */
    public long getTransactionArchivesCountByIncomeOrPrincipal(String incomeOrPrincipalIndicator) {
        long totalTransactionArchives = 0;

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR, incomeOrPrincipalIndicator);
        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);

        totalTransactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query).size();

        return totalTransactionArchives;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesCountByBothIncomeAndPrincipal(org.kuali.kfs.module.endow.businessobject.FeeMethod,
     *      java.lang.String, java.lang.String)
     */
    public long getTransactionArchivesCountByBothIncomeAndPrincipal(FeeMethod feeMethod, String feeMethodCodeForTypeCodes, String feeMethodCodeForEtranCodes) {
        long totalTransactionArchives = 0;

        Collection incomePrincipalValues = new ArrayList();
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);

        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR, incomePrincipalValues);

        if (feeMethod.getFeeByTransactionType() && feeMethod.getFeeByETranCode()) {
            criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, getTypeCodes(feeMethodCodeForTypeCodes));
            criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, getETranCodes(feeMethodCodeForEtranCodes));
        }
        else {
            if (feeMethod.getFeeByTransactionType()) {
                criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, getTypeCodes(feeMethodCodeForTypeCodes));
            }

            if (feeMethod.getFeeByETranCode()) {
                criteria.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, getETranCodes(feeMethodCodeForEtranCodes));
            }
        }

        if (feeMethod.getFeeByTransactionType() || feeMethod.getFeeByETranCode()) {
            criteria.addGreaterThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, feeMethod.getFeeLastProcessDate());
        }

        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);

        totalTransactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query).size();

        return totalTransactionArchives;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesTotalCashActivity(String)
     */
    public BigDecimal getTransactionArchivesTotalCashActivity(String kemid, String securityId) {
        BigDecimal totalCashActivity = BigDecimal.ZERO;

        Collection<TransactionArchive> transactionArchives = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_KEM_ID, kemid);

        QueryByCriteria query = QueryFactory.newQuery(TransactionArchive.class, criteria);
        transactionArchives = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        for (TransactionArchive transactionArchive : transactionArchives) {
            if (transactionArchive.getArchiveSecurity().getSecurityId().equals(securityId)) {
                totalCashActivity = totalCashActivity.add(transactionArchive.getIncomeCashAmount());
                totalCashActivity = totalCashActivity.add(transactionArchive.getPrincipalCashAmount());
            }
        }

        return totalCashActivity;
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchiveByKemidsAndPostedDate(java.lang.String,
     *      java.lang.String, java.util.Date, java.util.Date, java.lang.String, java.lang.String)
     */
    public List<TransactionArchive> getTransactionArchiveByKemidsAndPostedDate(String kemid, String endowmentOption, java.util.Date beginningDate, java.util.Date endingDate, String closedIndicator, String transactionSubType) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_KEM_ID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_SUB_TYPE_CODE, transactionSubType);
        criteria.addGreaterOrEqualThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, beginningDate);
        criteria.addLessOrEqualThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, endingDate);

        QueryByCriteria qbc = QueryFactory.newQuery(TransactionArchive.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE);

        return (List<TransactionArchive>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao#getTransactionArchivesByKemid(java.lang.String,
     *      java.util.Date, java.util.Date)
     */
    public List<TransactionArchive> getTransactionArchivesByKemid(String kemid, java.util.Date beginningDate, java.util.Date endingDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_KEM_ID, kemid);
        criteria.addGreaterOrEqualThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, beginningDate);
        criteria.addLessOrEqualThan(EndowPropertyConstants.TRANSACTION_ARCHIVE_POSTED_DATE, endingDate);

        List<String> documentTypesAllowed = new ArrayList();
        documentTypesAllowed.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
        documentTypesAllowed.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
        documentTypesAllowed.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER);
        documentTypesAllowed.add(EndowConstants.DocumentTypeNames.ENDOWMENT_SECURITY_TRANSFER);

        Criteria c = new Criteria();
        c.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, documentTypesAllowed);

        Criteria c1 = new Criteria();
        documentTypesAllowed.clear();
        documentTypesAllowed.add(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE);
        documentTypesAllowed.add(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        documentTypesAllowed.add(EndowConstants.DocumentTypeNames.ENDOWMENT_LIABILITY_DECREASE);
        c1.addIn(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, documentTypesAllowed);
        c1.addEqualTo(EndowPropertyConstants.TRANSACTION_ARCHIVE_SUB_TYPE_CODE, EndowConstants.TransactionSubTypeCode.NON_CASH);

        c.addOrCriteria(c1);

        criteria.addAndCriteria(c);

        QueryByCriteria qbc2 = QueryFactory.newQuery(TransactionArchive.class, criteria);
        qbc2.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_KEM_ID);
        qbc2.addOrderByAscending(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE);

        return (List<TransactionArchive>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc2);
    }
}
