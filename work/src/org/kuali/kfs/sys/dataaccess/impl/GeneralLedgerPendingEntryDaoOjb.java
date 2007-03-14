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
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.dao.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.util.OJBUtility;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class GeneralLedgerPendingEntryDaoOjb extends PersistenceBrokerDaoSupport implements GeneralLedgerPendingEntryDao {
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
        criteria.addEqualTo(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo("acctSufficientFundsFinObjCd", acctSufficientFundsFinObjCd);
        criteria.addIn(Constants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, balanceTypeCodes);
        criteria.addIn("financialObjectTypeCode", objectTypeCodes);

        if (isYearEnd) {
            criteria.addLike("financialDocumentTypeCode", "YE%");
        }
        else {
            criteria.addNotLike("financialDocumentTypeCode", "YE%");
        }

        Collection status = new ArrayList();
        status.add(Constants.DocumentStatusCodes.CANCELLED);
        status.add(Constants.DocumentStatusCodes.DISAPPROVED);

        criteria.addNotIn(Constants.DOCUMENT_HEADER_PROPERTY_NAME + "." + Constants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, status);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + Constants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        KualiDecimal rv = null;
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iterator.hasNext()) {
            rv = (KualiDecimal) ((Object[]) iterator.next())[0];
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
        criteria.addEqualTo(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addEqualTo("acctSufficientFundsFinObjCd", acctSufficientFundsFinObjCd);
        criteria.addIn(Constants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, balanceTypeCodes);
        criteria.addIn("financialObjectTypeCode", objectTypeCodes);

        if (isYearEnd) {
            criteria.addLike("financialDocumentTypeCode", "YE%");
        }
        else {
            criteria.addNotLike("financialDocumentTypeCode", "YE%");
        }

        if (isDebit) {
            criteria.addEqualTo(Constants.TRANSACTION_DEBIT_CREDIT_CODE, Constants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(Constants.TRANSACTION_DEBIT_CREDIT_CODE, Constants.GL_DEBIT_CODE);
        }

        Collection status = new ArrayList();
        status.add(Constants.DocumentStatusCodes.CANCELLED);
        status.add(Constants.DocumentStatusCodes.DISAPPROVED);

        criteria.addNotIn(Constants.DOCUMENT_HEADER_PROPERTY_NAME + "." + Constants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, status);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + Constants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

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
        criteria.addIn(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYears);
        criteria.addEqualTo(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
        criteria.addIn(Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, objectCodes);
        criteria.addIn(Constants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, balanceTypeCodes);

        if (isDebit) {
            criteria.addEqualTo(Constants.TRANSACTION_DEBIT_CREDIT_CODE, Constants.GL_DEBIT_CODE);
        }
        else {
            criteria.addNotEqualTo(Constants.TRANSACTION_DEBIT_CREDIT_CODE, Constants.GL_DEBIT_CODE);
        }

        Collection status = new ArrayList();
        status.add(Constants.DocumentStatusCodes.CANCELLED);
        status.add(Constants.DocumentStatusCodes.DISAPPROVED);

        criteria.addNotIn(Constants.DOCUMENT_HEADER_PROPERTY_NAME + "." + Constants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME, status);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + Constants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        KualiDecimal rv = null;
        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iterator.hasNext()) {
            rv = (KualiDecimal) ((Object[]) iterator.next())[0];
        }
        return (rv == null) ? KualiDecimal.ZERO : rv;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.GeneralLedgerPendingEntryDao#deleteEntriesForCancelledOrDisapprovedDocuments()
     */
    public void deleteEntriesForCancelledOrDisapprovedDocuments() {
        LOG.debug("deleteEntriesForCancelledOrDisapprovedDocuments() started");

        Criteria subCriteria = new Criteria();
        Criteria criteria = new Criteria();

        List codes = new ArrayList();
        codes.add(Constants.DocumentStatusCodes.DISAPPROVED);
        codes.add(Constants.DocumentStatusCodes.CANCELLED);

        subCriteria.addIn(PropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, codes);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(DocumentHeader.class, subCriteria);

        subQuery.setAttributes(new String[] { PropertyConstants.DOCUMENT_NUMBER });

        criteria.addIn(PropertyConstants.DOCUMENT_NUMBER, subQuery);

        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.GeneralLedgerPendingEntryDao#getByPrimaryId(java.lang.Long, java.lang.Long)
     */
    public GeneralLedgerPendingEntry getByPrimaryId(String documentHeaderId, Integer transactionLedgerEntrySequenceNumber) {
        LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.DOCUMENT_NUMBER, documentHeaderId);
        criteria.addEqualTo(TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER, transactionLedgerEntrySequenceNumber);

        return (GeneralLedgerPendingEntry) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria));
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
            criteria.addEqualTo(PropertyConstants.DOCUMENT_NUMBER, documentHeaderId);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }

    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        LOG.debug("deleteByFinancialDocumentApprovedCode() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(FINANCIAL_DOCUMENT_APPROVED_CODE, financialDocumentApprovedCode);

        QueryByCriteria qbc = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
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
        this.addStatusCode(criteria, true);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
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

        ReportQueryByCriteria query = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
        query.setAttributes(new String[] { "count(*)" });
        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        if (i.hasNext()) {
            Object[] values = (Object[]) i.next();
            BigDecimal count = (BigDecimal) values[0];
            return count.intValue();
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
        criteria.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, encumbrance.getUniversityFiscalYear());
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, encumbrance.getChartOfAccountsCode());
        criteria.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, encumbrance.getAccountNumber());
        criteria.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, encumbrance.getSubAccountNumber());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_CODE, encumbrance.getObjectCode());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE, encumbrance.getSubObjectCode());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, encumbrance.getBalanceTypeCode());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, encumbrance.getDocumentTypeCode());
        criteria.addEqualTo(PropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, encumbrance.getDocumentNumber());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        // Criteria: (originCode=originationCode OR originCode=originationReferenceCode)
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, encumbrance.getOriginCode());
        criteria2.addEqualTo(PropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE, encumbrance.getOriginCode());
        criteria1.addOrCriteria(criteria2);

        // combine all criteria together
        criteria.addAndCriteria(criteria1);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
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
        criteria.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, balance.getUniversityFiscalYear());
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, balance.getChartOfAccountsCode());
        criteria.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, balance.getAccountNumber());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_CODE, balance.getObjectCode());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balance.getBalanceTypeCode());

        if (!isConsolidated) {
            criteria.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, balance.getSubAccountNumber());
        }

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
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

        LookupUtils.applySearchResultsLimit(criteria);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForBalance(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForCashBalance(java.util.Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForCashBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForCashBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());
        criteria.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");
        criteria.addEqualToField(PropertyConstants.FINANCIAL_OBJECT_CODE, CHART_FINANCIAL_CASH_OBJECT_CODE);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForEncumbrance(Map, boolean)
     */
    public Iterator findPendingLedgerEntriesForEncumbrance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForEncumbrance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());
        criteria.addIn(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, Arrays.asList(Constants.ENCUMBRANCE_BALANCE_TYPE));

        List encumbranceUpdateCodeList = new ArrayList();
        encumbranceUpdateCodeList.add(Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        encumbranceUpdateCodeList.add(Constants.ENCUMB_UPDT_DOCUMENT_CD);
        criteria.addIn(PropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, encumbranceUpdateCodeList);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForAccountBalance(java.util.Map, boolean,
     *      boolean)
     */
    public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntriesForAccountBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntrySummaryForAccountBalance(java.util.Map,
     *      boolean, boolean)
     */
    public Iterator findPendingLedgerEntrySummaryForAccountBalance(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingLedgerEntrySummaryForAccountBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);

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
        attributeList.add("sum(" + PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")");
        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     * 
     * @return List an group by attribute list
     */
    private List buildGroupList() {
        List groupList = new ArrayList();

        groupList.add(PropertyConstants.FINANCIAL_OBJECT_CODE);
        groupList.add(PropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE);
        groupList.add(PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        groupList.add(OBJECT_TYPE_FIN_OBJECT_TYPE_DEBITCREDIT_CD);
        return groupList;
    }

    /**
     * add the status code into the given criteria. The status code can be categorized into approved and all.
     * 
     * @param criteria the given criteria
     * @param isApproved the indicator of approval status
     */
    private void addStatusCode(Criteria criteria, boolean isApproved) {
        // add criteria for the approved pending entries
        if (isApproved) {
            criteria.addIn("documentHeader.financialDocumentStatusCode", this.buildApprovalCodeList());
        }
        criteria.addNotIn("documentHeader.financialDocumentStatusCode", this.buildExcludedStatusCodeList());
        criteria.addNotEqualTo("financialDocumentApprovedCode", "X");
    }

    /**
     * build a status code list including the legal approval codes
     * 
     * @return an approval code list
     */
    private List buildApprovalCodeList() {
        List approvalCodeList = new ArrayList();

        approvalCodeList.add(Constants.DocumentStatusCodes.APPROVED);
        return approvalCodeList;
    }

    /**
     * build a status code list including the codes that will not be processed
     * 
     * @return a status code list including the codes that will not be processed
     */
    private List buildExcludedStatusCodeList() {
        List exclusiveCodeList = new ArrayList();

        exclusiveCodeList.add(Constants.DocumentStatusCodes.CANCELLED);
        exclusiveCodeList.add(Constants.DocumentStatusCodes.DISAPPROVED);
        return exclusiveCodeList;
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

        UniversityDate currentUniversityDate = SpringServiceLocator.getUniversityDateService().getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();

        // deal with null fiscal year and fiscal period code as current fiscal year and period code respectively
        String fiscalPeriodFromForm = null;
        if (fieldValues.containsKey(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE)) {
            fiscalPeriodFromForm = (String) fieldValues.get(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        }

        String fiscalYearFromForm = null;
        if (fieldValues.containsKey(PropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            fiscalYearFromForm = (String) fieldValues.get(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        boolean includeNullFiscalPeriodCodeInLookup = null != currentFiscalPeriodCode && currentFiscalPeriodCode.equals(fiscalPeriodFromForm);
        boolean includeNullFiscalYearInLookup = null != currentFiscalYear && currentFiscalYear.toString().equals(fiscalYearFromForm);

        if (includeNullFiscalPeriodCodeInLookup) {
            Criteria apValueCriteria = new Criteria();
            apValueCriteria.addLike(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, fiscalPeriodFromForm);

            Criteria apNullCriteria = new Criteria();
            apNullCriteria.addIsNull(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

            apValueCriteria.addOrCriteria(apNullCriteria);
            criteria.addAndCriteria(apValueCriteria);

            fieldValues.remove(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        }

        if (includeNullFiscalYearInLookup) {
            Criteria fyValueCriteria = new Criteria();
            fyValueCriteria.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYearFromForm);

            Criteria fyNullCriteria = new Criteria();
            fyNullCriteria.addIsNull(PropertyConstants.UNIVERSITY_FISCAL_YEAR);

            fyValueCriteria.addOrCriteria(fyNullCriteria);
            criteria.addAndCriteria(fyValueCriteria);

            fieldValues.remove(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        // handle encumbrance balance type
        Map localFieldValues = new HashMap();        
        localFieldValues.putAll(fieldValues);
        
        String propertyName = PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) fieldValues.get(propertyName);
            if (Constants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                criteria.addIn(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.getEncumbranceBalanceTypeCodeList());
            }
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, businessObject));
        return criteria;
    }

    private List<String> getEncumbranceBalanceTypeCodeList() {
        String[] balanceTypesAsArray = kualiConfigurationService.getApplicationParameterValues("Kuali.GeneralLedger.AvailableBalanceInquiry", "GeneralLedger.BalanceInquiry.AvailableBalances.EncumbranceDrillDownBalanceTypes");
        return Arrays.asList(balanceTypesAsArray);
    }

    public Collection findPendingEntries(Map fieldValues, boolean isApproved) {
        LOG.debug("findPendingEntries(Map, boolean) started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry());

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        LookupUtils.applySearchResultsLimit(criteria);

        QueryByCriteria query = QueryFactory.newQuery(GeneralLedgerPendingEntry.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}