/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is Implementation class for CustomerAgingReportDao DAO Interface.
 */
public class CustomerAgingReportDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerAgingReportDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findInvoiceAmountByProcessingChartAndOrg(java.lang.String,
     *      java.lang.String, java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findInvoiceAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria criteria = new Criteria();
        if (ObjectUtils.isNotNull(begin)) {
            criteria.addGreaterOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            criteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CHART_CODE, chart);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_ORG_CODE, org);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findAppliedAmountByProcessingChartAndOrg(java.lang.String,
     *      java.lang.String, java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findAppliedAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();

        // get approved application payments only
        Criteria subCriteria = new Criteria();
        subCriteria.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(FinancialSystemDocumentHeader.class, subCriteria);
        subQuery.setAttributes(new String[] {KFSPropertyConstants.DOCUMENT_NUMBER});

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, subQuery);
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            criteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CHART_CODE, chart);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_ORG_CODE, org);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.INVOICE_ITEM_APPLIED_AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findDiscountAmountByProcessingChartAndOrg(java.lang.String,
     *      java.lang.String, java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findDiscountAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {

        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria subCriteria = new Criteria();
        if (ObjectUtils.isNotNull(begin)) {
            subCriteria.addGreaterOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            subCriteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CHART_CODE, chart);
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_ORG_CODE, org);
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        subCriteria.addEqualToField(KFSPropertyConstants.DOCUMENT_NUMBER, Criteria.PARENT_QUERY_PREFIX + KFSPropertyConstants.DOCUMENT_NUMBER);

        ReportQueryByCriteria subReportQuery = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.INVOICE_ITEM_DISCOUNT_LINE_NUMBER }, subCriteria);

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.SEQUENCE_NUMBER, subReportQuery);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;

    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findInvoiceAmountByBillingChartAndOrg(java.lang.String,
     *      java.lang.String, java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findInvoiceAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria criteria = new Criteria();
        if (ObjectUtils.isNotNull(begin)) {
            criteria.addGreaterOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            criteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chart);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.BILLED_BY_ORGANIZATION_CODE, org);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findAppliedAmountByBillingChartAndOrg(java.lang.String,
     *      java.lang.String, java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findAppliedAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();

        // get approved application payments only
        Criteria subCriteria = new Criteria();
        subCriteria.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(FinancialSystemDocumentHeader.class, subCriteria);
        subQuery.setAttributes(new String[] {KFSPropertyConstants.DOCUMENT_NUMBER});

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, subQuery);
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            criteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chart);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.BILLED_BY_ORGANIZATION_CODE, org);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.INVOICE_ITEM_APPLIED_AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findDiscountAmountByBillingChartAndOrg(java.lang.String,
     *      java.lang.String, java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findDiscountAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {

        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria subCriteria = new Criteria();
        if (ObjectUtils.isNotNull(begin)) {
            subCriteria.addGreaterOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            subCriteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chart);
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.BILLED_BY_ORGANIZATION_CODE, org);
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        subCriteria.addEqualToField(KFSPropertyConstants.DOCUMENT_NUMBER, Criteria.PARENT_QUERY_PREFIX + KFSPropertyConstants.DOCUMENT_NUMBER);

        ReportQueryByCriteria subReportQuery = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.INVOICE_ITEM_DISCOUNT_LINE_NUMBER }, subCriteria);

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.SEQUENCE_NUMBER, subReportQuery);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;

    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findInvoiceAmountByAccount(java.lang.String, java.lang.String,
     *      java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findInvoiceAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria criteria = new Criteria();
        if (ObjectUtils.isNotNull(begin)) {
            criteria.addGreaterOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            criteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, account);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findAppliedAmountByAccount(java.lang.String, java.lang.String,
     *      java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findAppliedAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();

        // get approved application payments only
        Criteria subCriteria = new Criteria();
        subCriteria.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(FinancialSystemDocumentHeader.class, subCriteria);
        subQuery.setAttributes(new String[] {KFSPropertyConstants.DOCUMENT_NUMBER});

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, subQuery);
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            criteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.CHART_OF_ACCOUNTS_CODE, chart);
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNT_NUMBER, account);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.INVOICE_ITEM_APPLIED_AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findDiscountAmountByAccount(java.lang.String,
     *      java.lang.String, java.sql.Date, java.sql.Date)
     */
    @Override
    public HashMap<String, KualiDecimal> findDiscountAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end) {

        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria subCriteria = new Criteria();
        if (ObjectUtils.isNotNull(begin)) {
            subCriteria.addGreaterOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            subCriteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDetailFields.BILLING_DATE, end);
        }
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.OPEN_INVOICE_IND, true);
        subCriteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);
        subCriteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, account);
        subCriteria.addEqualToField(KFSPropertyConstants.DOCUMENT_NUMBER, Criteria.PARENT_QUERY_PREFIX + KFSPropertyConstants.DOCUMENT_NUMBER);

        ReportQueryByCriteria subReportQuery = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.INVOICE_ITEM_DISCOUNT_LINE_NUMBER }, subCriteria);

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.SEQUENCE_NUMBER, subReportQuery);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER, ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, "sum(" + ArPropertyConstants.CustomerInvoiceDetailFields.AMOUNT + ")" }, criteria);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NUMBER);
        reportByCriteria.addGroupBy(ArPropertyConstants.CustomerInvoiceDetailFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;

    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findWriteOffAmountByCustomerNumber(java.lang.String)
     */
    @Override
    public KualiDecimal findWriteOffAmountByCustomerNumber(String customerNumber) {
        KualiDecimal writeOffAmt = KualiDecimal.ZERO;
        Criteria subCriteria = new Criteria();
        subCriteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDetailFields.WRITEOFF_CUSTOMER_NUMBER, customerNumber);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceWriteoffDocument.class, new String[] { "sum(" + ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.INVOICE_WRITEOFF_AMOUNT + ")" }, subCriteria);
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);
        while (ObjectUtils.isNotNull(iterator) && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            writeOffAmt = (KualiDecimal) data[0];
        }
        return writeOffAmt;
    }

}
