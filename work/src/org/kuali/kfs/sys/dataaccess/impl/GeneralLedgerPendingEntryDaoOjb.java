/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.dataaccess.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.dataaccess.BalanceTypeDao;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.lookup.LookupUtils;

/**
 *
 *
 */
public class GeneralLedgerPendingEntryDaoOjb extends PlatformAwareDaoBaseOjb implements GeneralLedgerPendingEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPendingEntryDaoOjb.class);

    protected final static String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";
    protected final static String FINANCIAL_DOCUMENT_APPROVED_CODE = "financialDocumentApprovedCode";
    protected final static String ACCOUNT_NUMBER = "accountNumber";
    protected final static String DOC_NUMBER = "documentNumber";
    protected final static String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    protected final static String CHART_FINANCIAL_CASH_OBJECT_CODE = "chart.financialCashObjectCode";
    protected final static String OBJECT_TYPE_FIN_OBJECT_TYPE_DEBITCREDIT_CD = "objectType.finObjectTypeDebitcreditCd";

    protected BalanceTypeDao balanceTypeDao;

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#getTransactionSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.util.Collection, java.util.Collection, java.lang.String, boolean)
     */
    @Override
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
            rv = (KualiDecimal) ((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator))[0];
        }
        return (rv == null) ? KualiDecimal.ZERO : rv;
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#getTransactionSummary(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.util.Collection, java.util.Collection, boolean, boolean)
     */
    @Override
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
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#getTransactionSummary(java.util.Collection, java.lang.String,
     *      java.lang.String, java.util.Collection, java.util.Collection, boolean)
     */
    @Override
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
            rv = (KualiDecimal) ((Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator))[0];
        }
        return (rv == null) ? KualiDecimal.ZERO : rv;
    }

    /*
     * (non-Javadoc)
     * @see org.kuali.dao.GeneralLedgerPendingEntryDao#delete(Long)
     */
    @Override
    public void delete(String documentHeaderId) {
        LOG.debug("delete() started");

        if (documentHeaderId != null) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentHeaderId);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(this.getEntryClass(), criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }

    @Override
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
    @Override
    public Iterator findApprovedPendingLedgerEntries() {
        LOG.debug("findApprovedPendingLedgerEntries() started");

        // only process the document for which document status code is A (approved)
        Criteria criteria = new Criteria();
        criteria.addEqualTo("financialDocumentApprovedCode", KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#countPendingLedgerEntries(org.kuali.kfs.coa.businessobject.Account)
     */
    @Override
    public int countPendingLedgerEntries(Account account) {
        LOG.debug("findPendingLedgerEntries(Account) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(ACCOUNT_NUMBER, account.getAccountNumber());
        criteria.addEqualTo(CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());

        ReportQueryByCriteria query = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        query.setAttributes(new String[] { "count(*)" });
        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        if (i.hasNext()) {
            Object[] values = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(i);
            if (values[0] instanceof BigDecimal) {
                return ((BigDecimal) values[0]).intValue();
            }
            else {
                return ((Long) values[0]).intValue();
            }
        }
        else {
            return 0;
        }
    }

    /**
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntries(org.kuali.kfs.gl.businessobject.Encumbrance,
     *      boolean)
     */
    @Override
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
     * @see org.kuali.module.gl.dao.GeneralLedgerPendingEntryDao#findPendingLedgerEntries(org.kuali.kfs.gl.businessobject.Balance,
     *      boolean, boolean)
     */
    @Override
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
     * @see org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForEntry(java.util.Map, boolean,
     *      java.lang.String, int)
     */
    @Override
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFY, List<String> encumbranceBalanceTypes) {
        LOG.debug("findPendingLedgerEntriesForEntry started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new GeneralLedgerPendingEntry(), currentFiscalPeriodCode, currentFY, encumbranceBalanceTypes);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        LookupUtils.applySearchResultsLimit(this.getEntryClass(), criteria, getDbPlatform());

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForBalance(java.util.Map, boolean,
     *      java.lang.String, int)
     */
    @Override
    public Iterator findPendingLedgerEntriesForBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFY, List<String> encumbranceBalanceTypes) {
        LOG.debug("findPendingLedgerEntriesForBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance(), currentFiscalPeriodCode, currentFY, encumbranceBalanceTypes);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForCashBalance(java.util.Map, boolean,
     *      java.lang.String, int)
     */
    @Override
    public Iterator findPendingLedgerEntriesForCashBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceType) {
        LOG.debug("findPendingLedgerEntriesForCashBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance(), currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceType);
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");
        criteria.addEqualToField(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, CHART_FINANCIAL_CASH_OBJECT_CODE);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }


    /**
     * @see org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForEncumbrance(java.util.Map, boolean,
     *      java.lang.String, int, org.kuali.kfs.sys.businessobject.SystemOptions, java.util.List)
     */
    @Override
    public Iterator findPendingLedgerEntriesForEncumbrance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, SystemOptions currentYearOptions, List<String> encumbranceBalanceTypes) {
        LOG.debug("findPendingLedgerEntriesForEncumbrance started");

        String docNumber = (String)fieldValues.get(DOC_NUMBER);
        Criteria subCriteria1 = new Criteria();
        boolean docNbrCriteriaNeeded = false;
        if (StringUtils.isNotBlank(docNumber)) {
            fieldValues.remove(DOC_NUMBER);
            subCriteria1 = new Criteria();
            subCriteria1.addEqualTo(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
            Criteria subCriteria1b = new Criteria();
            subCriteria1b.addEqualTo(KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_NUMBER, docNumber);
            subCriteria1.addAndCriteria(subCriteria1b);
            Criteria subCriteria2 = new Criteria();
            subCriteria2.addEqualTo(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, KFSConstants.ENCUMB_UPDT_DOCUMENT_CD);
            Criteria subCriteria2b = new Criteria();
            subCriteria2b.addEqualTo(DOC_NUMBER, docNumber);
            subCriteria2.addAndCriteria(subCriteria2b);
            subCriteria1.addOrCriteria(subCriteria2);
            docNbrCriteriaNeeded = true;
        }

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance(), currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);
        criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, harvestCodesFromEncumbranceBalanceTypes());

        List encumbranceUpdateCodeList = new ArrayList();
        encumbranceUpdateCodeList.add(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        encumbranceUpdateCodeList.add(KFSConstants.ENCUMB_UPDT_DOCUMENT_CD);
        criteria.addIn(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, encumbranceUpdateCodeList);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        // add criteria to exclude fund balance object type code
        criteria.addAndCriteria(buildCriteriaToExcludeFundBalance(currentYearOptions));

        if (docNbrCriteriaNeeded) {
            criteria.addAndCriteria(subCriteria1);
        }

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @return returns only the codes from encumbrance balance types
     */
    protected List<String> harvestCodesFromEncumbranceBalanceTypes() {
        List<String> balanceTypeCodes = new ArrayList<String>();
        Collection<BalanceType> encumbranceBalanceTypes = getBalanceTypeDao().getEncumbranceBalanceTypes();
        for (BalanceType encumbranceBalanceType : encumbranceBalanceTypes) {
            balanceTypeCodes.add(encumbranceBalanceType.getCode());
        }
        return balanceTypeCodes;
    }

    /**
     * This method creates Criteria that exclude the fund balance object type from the result.
     *
     * @return Criteria
     */
    protected Criteria buildCriteriaToExcludeFundBalance(SystemOptions currentYearOptions) {

        String fundBalanceObjectTypeCode = currentYearOptions.getFinObjectTypeFundBalanceCd();

        Criteria criteria = new Criteria();
        criteria.addNotEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, fundBalanceObjectTypeCode);
        return criteria;
    }


    /**
     * @see org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao#findPendingLedgerEntriesForAccountBalance(Map, boolean,
     *      String, int, List)
     */
    @Override
    public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceTypes) {
        LOG.debug("findPendingLedgerEntriesForAccountBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance(), currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }


    /**
     * @see org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao#findPendingLedgerEntrySummaryForAccountBalance(java.util.Map,
     *      boolean, java.lang.String, int, java.util.List)
     */
    @Override
    public Iterator findPendingLedgerEntrySummaryForAccountBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceTypes) {
        LOG.debug("findPendingLedgerEntrySummaryForAccountBalance started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance(), currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);

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
    protected List buildAttributeList() {
        List attributeList = buildGroupList();
        attributeList.add("sum(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")");
        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     *
     * @return List an group by attribute list
     */
    protected List buildGroupList() {
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
    protected void addStatusCode(Criteria criteria, boolean isOnlyApproved) {
        // add criteria for the approved pending entries
        if (isOnlyApproved) {
            criteria.addIn("documentHeader.financialDocumentStatusCode", this.buildApprovalCodeList());
            criteria.addNotEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
        }
        else {
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
    protected List buildApprovalCodeList() {
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
    public Criteria buildCriteriaFromMap(Map fieldValues, Object businessObject, String currentFiscalPeriodCode, Integer currentFiscalYear, List<String> encumbranceBalanceTypes) {
        Criteria criteria = new Criteria();

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

        }

        if (includeNullFiscalYearInLookup) {
            Criteria fyValueCriteria = new Criteria();
            fyValueCriteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYearFromForm);

            Criteria fyNullCriteria = new Criteria();
            fyNullCriteria.addIsNull(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);

            fyValueCriteria.addOrCriteria(fyNullCriteria);
            criteria.addAndCriteria(fyValueCriteria);
        }

        // handle encumbrance balance type
        Map<String, Object> localFieldValues = new HashMap();
        localFieldValues.putAll(fieldValues);

        // we've already taken care of these fields...
        if (includeNullFiscalPeriodCodeInLookup) {
            localFieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        }
        if (includeNullFiscalYearInLookup) {
            localFieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        }

        String propertyName = KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) fieldValues.get(propertyName);
            if (KFSConstants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, encumbranceBalanceTypes);
            }
        }

        // remove dummyBusinessObject references - no longer needed
        List<String> keysToRemove = new ArrayList<String>();
        for (String key : localFieldValues.keySet()) {
            if (key.startsWith("dummyBusinessObject.")) {
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            localFieldValues.remove(key);
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, businessObject));
        return criteria;
    }

    /**
     * @see org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao#findPendingEntries(java.util.Map, boolean, java.lang.String,
     *      int, java.util.List)
     */
    @Override
    public Collection findPendingEntries(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceTypes) {
        LOG.debug("findPendingEntries(Map, boolean) started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, this.getEntryClassInstance(), currentFiscalPeriodCode, currentFiscalYear, encumbranceBalanceTypes);

        // add the status codes into the criteria
        this.addStatusCode(criteria, isApproved);

        LookupUtils.applySearchResultsLimit(this.getEntryClass(), criteria, getDbPlatform());

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * Gets the entryClass attribute.
     *
     * @return Returns the entryClass.
     */
    public Class getEntryClass() {
        return GeneralLedgerPendingEntry.class;
    }

    protected Object getEntryClassInstance() {
        Object entryObject = null;
        try {
            entryObject = getEntryClass().newInstance();
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Wrong object type" + e);
            }
        }
        return entryObject;
    }

    /**
     * @return the injected implementation of balance type dao
     */
    public BalanceTypeDao getBalanceTypeDao() {
        return balanceTypeDao;
    }

    /**
     * Injects an implementation of BalanceTypeDao
     * @param balanceTypeDao the implementation of BalanceTypeDao to inject
     */
    public void setBalanceTypeDao(BalanceTypeDao balanceTypeDao) {
        this.balanceTypeDao = balanceTypeDao;
    }
}
