/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.dao.ojb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.dao.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.util.KFSUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.util.OJBUtility;

/**
 * 
 * 
 */
public class GeneralLedgerPendingEntryDaoOjb extends PlatformAwareDaoBaseOjb implements GeneralLedgerPendingEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPendingEntryDaoOjb.class);

    private final static String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";
    private final static String FINANCIAL_DOCUMENT_APPROVED_CODE = "financialDocumentApprovedCode";
    private final static String ACCOUNT_NUMBER = "accountNumber";
    private final static String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private final static String CHART_FINANCIAL_CASH_OBJECT_CODE = "chart.financialCashObjectCode";
    private final static String OBJECT_TYPE_FIN_OBJECT_TYPE_DEBITCREDIT_CD = "objectType.finObjectTypeDebitcreditCd";

    private KualiConfigurationService kualiConfigurationService;

    /**
     * 
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#getTransactionSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.util.Collection, java.util.Collection, java.lang.String, boolean)
     */
    public KualiDecimal getTransactionSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, Collection objectTypeCodes, Collection balanceTypeCodes, String acctSufficientFundsFinObjCd, boolean isYearEnd) {
        LOG.debug("getTransactionSummary() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo("acctSufficientFundsFinObjCd", acctSufficientFundsFinObjCd);
        criteria.addIn(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, balanceTypeCodes);
        criteria.addIn("financialObjectTypeCode", objectTypeCodes);

        if (isYearEnd) {
            criteria.addLike("financialDocumentTypeCode", "YE%");
        }
        else {
            criteria.addNotLike("financialDocumentTypeCode", "YE%");
        }

        Collection status = new ArrayList();
        status.add(KFSConstants.DocumentStatusCodes.CANCELLED);
        status.add(KFSConstants.DocumentStatusCodes.DISAPPROVED);

        criteria.addNotIn(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, status);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        KualiDecimal rv = null;
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iterator.hasNext()) {
            rv = (KualiDecimal) ((Object[]) KFSUtils.retrieveFirstAndExhaustIterator(iterator))[0];
        }
        return (rv == null) ? KualiDecimal.ZERO : rv;
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#getTransactionSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.util.Collection, java.util.Collection, boolean, boolean)
     */
    public KualiDecimal getTransactionSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, Collection objectTypeCodes, Collection balanceTypeCodes, String acctSufficientFundsFinObjCd, boolean isDebit, boolean isYearEnd) {
        LOG.debug("getTransactionSummary() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo("acctSufficientFundsFinObjCd", acctSufficientFundsFinObjCd);
        criteria.addIn(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, balanceTypeCodes);
        criteria.addIn("financialObjectTypeCode", objectTypeCodes);

        if (isYearEnd) {
            criteria.addLike("financialDocumentTypeCode", "YE%");
        }
        else {
            criteria.addNotLike("financialDocumentTypeCode", "YE%");
        }

        if (isDebit) {
            criteria.addEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }

        Collection status = new ArrayList();
        status.add(KFSConstants.DocumentStatusCodes.CANCELLED);
        status.add(KFSConstants.DocumentStatusCodes.DISAPPROVED);

        criteria.addNotIn(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, status);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        KualiDecimal rv = null;
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iterator.hasNext()) {
            rv = (KualiDecimal) ((Object[]) iterator.next())[0];
        }
        return (rv == null) ? KualiDecimal.ZERO : rv;
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#getTransactionSummary(java.util.Collection, java.lang.String,
     *      java.lang.String, java.util.Collection, java.util.Collection, boolean)
     */
    public KualiDecimal getTransactionSummary(Collection universityFiscalYears, String chartOfAccountsCode, String accountNumber, Collection objectCodes, Collection balanceTypeCodes, boolean isDebit) {
        LOG.debug("getTransactionSummary() started");

        Criteria criteria = new Criteria();
        criteria.addIn(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYears);
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addIn(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, objectCodes);
        criteria.addIn(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, balanceTypeCodes);

        if (isDebit) {
            criteria.addEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
        }

        Collection status = new ArrayList();
        status.add(KFSConstants.DocumentStatusCodes.CANCELLED);
        status.add(KFSConstants.DocumentStatusCodes.DISAPPROVED);

        criteria.addNotIn(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, status);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        KualiDecimal rv = null;
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iterator.hasNext()) {
            rv = (KualiDecimal) ((Object[]) KFSUtils.retrieveFirstAndExhaustIterator(iterator))[0];
        }
        return (rv == null) ? KualiDecimal.ZERO : rv;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.GeneralLedgerPendingEntryDao#getByPrimaryId(java.lang.Long, java.lang.Long)
     */
    public GeneralLedgerPendingEntry getByPrimaryId(String documentHeaderId, Integer transactionLedgerEntrySequenceNumber) {
        LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentHeaderId);
        criteria.addEqualTo(TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER, transactionLedgerEntrySequenceNumber);

        return (GeneralLedgerPendingEntry) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(this.getEntryClass(), criteria));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.GeneralLedgerPendingEntryDao#save(org.kuali.bo.GeneralLedgerPendingEntry)
     */
    public void save(GeneralLedgerPendingEntry generalLedgerPendingEntry) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(generalLedgerPendingEntry);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.GeneralLedgerPendingEntryDao#delete(Long)
     */
    public void delete(String documentHeaderId) {
        LOG.debug("delete() started");

        if (documentHeaderId != null) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentHeaderId);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(this.getEntryClass(), criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }

    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        LOG.debug("deleteByFinancialDocumentApprovedCode() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(FINANCIAL_DOCUMENT_APPROVED_CODE, financialDocumentApprovedCode);

        QueryByCriteria qbc = QueryFactory.newQuery(this.getEntryClass(), criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findApprovedPendingLedgerEntries()
     */
    public Iterator findApprovedPendingLedgerEntries() {
        LOG.debug("findApprovedPendingLedgerEntries() started");

        // only process the document for which document status code is A (approved)
        Criteria criteria = new Criteria();
        criteria.addEqualTo("financialDocumentApprovedCode", KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#countPendingLedgerEntries(org.kuali.module.chart.bo.Account)
     */
    public int countPendingLedgerEntries(Account account) {
        LOG.debug("findPendingLedgerEntries(Account) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(ACCOUNT_NUMBER, account.getAccountNumber());
        criteria.addEqualTo(CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());

        ReportQueryByCriteria query = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        query.setAttributes(new String[] { "count(*)" });
        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        if (i.hasNext()) {
            Object[] values = (Object[]) KFSUtils.retrieveFirstAndExhaustIterator(i);
            if (values[0] instanceof BigDecimal) {
                return ((BigDecimal)values[0]).intValue();
            } else {
                return ((Long)values[0]).intValue();
            }
        }
        else {
            return 0;
        }
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntries(org.kuali.module.gl.bo.Encumbrance,
     *      boolean)
     */
    public Iterator findPendingLedgerEntries(Encumbrance encumbrance, boolean isApproved) {
        LOG.debug("findPendingLedgerEntries(Encumbrance, boolean) started");

        // find pending ledger entry by the primary key fields of encumbrance
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, encumbrance.getUniversityFiscalYear());
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, encumbrance.getChartOfAccountsCode());
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, encumbrance.getAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, encumbrance.getSubAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, encumbrance.getObjectCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, encumbrance.getSubObjectCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, encumbrance.getBalanceTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, encumbrance.getDocumentTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, encumbrance.getDocumentNumber());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        // Criteria: (originCode=originationCode OR originCode=originationReferenceCode)
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, encumbrance.getOriginCode());
        criteria2.addEqualTo(KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE, encumbrance.getOriginCode());
        criteria1.addOrCriteria(criteria2);

        // combine all criteria together
        criteria.addAndCriteria(criteria1);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntries(org.kuali.module.gl.bo.Balance, boolean,
     *      boolean)
     */
    public Iterator findPendingLedgerEntries(Balance balance, boolean isApproved, boolean isConsolidated) {
        LOG.debug("findPendingLedgerEntries(Balance, boolean, boolean) started");

        // find pending ledger entry by the primary key fields of balance
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, balance.getUniversityFiscalYear());
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, balance.getChartOfAccountsCode());
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, balance.getAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, balance.getObjectCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balance.getBalanceTypeCode());

        if (!isConsolidated) {
            criteria.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, balance.getSubAccountNumber());
        }

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForEntry(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEntry started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        LookupUtils.applySearchResultsLimit(criteria, getDbPlatform());

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForBalance(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForCashBalance(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForCashBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForCashBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");
        criteria.addEqualToField(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, CHART_FINANCIAL_CASH_OBJECT_CODE);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForEncumbrance(Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEncumbrance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEncumbrance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance());
        criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, Arrays.asList(KFSConstants.ENCUMBRANCE_BALANCE_TYPE));

        List encumbranceUpdateCodeList = new ArrayList();
        encumbranceUpdateCodeList.add(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        encumbranceUpdateCodeList.add(KFSConstants.ENCUMB_UPDT_DOCUMENT_CD);
        criteria.addIn(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, encumbranceUpdateCodeList);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForAccountBalance(java.util.Map, boolean,
     *      boolean)
     */
    public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForAccountBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntrySummaryForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    public Iterator findPendingLedgerEntrySummaryForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntrySummaryForAccountBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(this.getEntryClass(), criteria);

        List attributeList = buildAttributeList();
        List groupByList = buildGroupList();

        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @return List an attribute list
     */
    private List buildAttributeList() {
        List attributeList = buildGroupList();
        attributeList.add("sum(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")");
        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     * 
     * @return List an group by attribute list
     */
    private List buildGroupList() {
        List groupList = new ArrayList();

        groupList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupList.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE);
        groupList.add(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        groupList.add(OBJECT_TYPE_FIN_OBJECT_TYPE_DEBITCREDIT_CD);
        return groupList;
    }

    /**
     * add the status code into the given criteria. The status code can be categorized into approved and all.
     * 
     * @param criteria the given criteria
     * @param isApproved the flag that indictates if only approved status code can be added into the given searach criteria 
     */
    private void addStatusCode(Criteria criteria, boolean isOnlyApproved) {
        // add criteria for the approved pending entries
        if (isOnlyApproved) {
            criteria.addIn("documentHeader.financialDocumentStatusCode", this.buildApprovalCodeList());
            criteria.addNotEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
        }
        else{
            Criteria subCriteria1 = new Criteria();
            subCriteria1.addNotEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
            
            Criteria subCriteria2 = new Criteria();
            subCriteria2.addIsNull(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE);
            
            subCriteria1.addOrCriteria(subCriteria2);
            criteria.addAndCriteria(subCriteria1);
        }
    }

    /**
     * build a status code list including the legal approval codes
     * 
     * @return an approval code list
     */
    private List buildApprovalCodeList() {
        List approvalCodeList = new ArrayList();

        approvalCodeList.add(KFSConstants.DocumentStatusCodes.APPROVED);
        return approvalCodeList;
    }

    /**
     * This method builds an OJB query criteria based on the input field map
     * 
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return an OJB query criteria
     */
    public Criteria buildCriteriaFromMap(Map fieldValues, Object businessObject) {
        Criteria criteria = new Criteria();

        UniversityDate currentUniversityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();

        // deal with null fiscal year and fiscal period code as current fiscal year and period code respectively
        String fiscalPeriodFromForm = null;
        if (fieldValues.containsKey(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE)) {
            fiscalPeriodFromForm = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        }

        String fiscalYearFromForm = null;
        if (fieldValues.containsKey(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            fiscalYearFromForm = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        boolean includeNullFiscalPeriodCodeInLookup = null != currentFiscalPeriodCode && currentFiscalPeriodCode.equals(fiscalPeriodFromForm);
        boolean includeNullFiscalYearInLookup = null != currentFiscalYear && currentFiscalYear.toString().equals(fiscalYearFromForm);

        if (includeNullFiscalPeriodCodeInLookup) {
            Criteria apValueCriteria = new Criteria();
            apValueCriteria.addLike(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, fiscalPeriodFromForm);

            Criteria apNullCriteria = new Criteria();
            apNullCriteria.addIsNull(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

            apValueCriteria.addOrCriteria(apNullCriteria);
            criteria.addAndCriteria(apValueCriteria);

            fieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        }

        if (includeNullFiscalYearInLookup) {
            Criteria fyValueCriteria = new Criteria();
            fyValueCriteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYearFromForm);

            Criteria fyNullCriteria = new Criteria();
            fyNullCriteria.addIsNull(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);

            fyValueCriteria.addOrCriteria(fyNullCriteria);
            criteria.addAndCriteria(fyValueCriteria);

            fieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        // handle encumbrance balance type
        Map<String,Object> localFieldValues = new HashMap();        
        localFieldValues.putAll(fieldValues);
        
        String propertyName = KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) fieldValues.get(propertyName);
            if (KFSConstants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.getEncumbranceBalanceTypeCodeList());
            }
        }

        // remove dummyBusinessObject references - no longer needed
        List<String> keysToRemove = new ArrayList<String>();
        for ( String key : localFieldValues.keySet() ) {
        	if ( key.startsWith( "dummyBusinessObject." ) ) {
        		keysToRemove.add( key );
        	}
        }
        for ( String key : keysToRemove ) {
        	localFieldValues.remove( key );
        }
        
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, businessObject));
        return criteria;
    }

    private List<String> getEncumbranceBalanceTypeCodeList() {
        String[] balanceTypesAsArray = kualiConfigurationService.getParameterValues(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BALANCE_INQUIRY_AVAILABLE_BALANCES, "EncumbranceDrillDownBalanceTypes");
        return Arrays.asList(balanceTypesAsArray);
    }

    public Collection findPendingEntries(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingEntries(Map, boolean) started");
        
        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        LookupUtils.applySearchResultsLimit(criteria, getDbPlatform());

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the entryClass attribute. 
     * @return Returns the entryClass.
     */
    public Class getEntryClass() {
        return GeneralLedgerPendingEntry.class;
    }
    
    private Object getEntryClassInstance(){
        Object entryObject = null;
        try{
            entryObject = getEntryClass().newInstance();
        }
        catch(Exception e){
            LOG.debug("Wrong object type" + e);
        }
        return entryObject;
    }
}