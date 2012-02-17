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
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class CustomerAgingReportDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerAgingReportDao {
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findInvoiceAmountByProcessingChartAndOrg(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findInvoiceAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria criteria = new Criteria();
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            criteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        criteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo("customerInvoiceDocument.accountsReceivableDocumentHeader.processingChartOfAccountCode", chart);
        criteria.addEqualTo("customerInvoiceDocument.accountsReceivableDocumentHeader.processingOrganizationCode", org);
        criteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(amount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findAppliedAmountByProcessingChartAndOrg(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findAppliedAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        
        // get approved application payments only
        Criteria subCriteria = new Criteria();
        subCriteria.addEqualTo("financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(FinancialSystemDocumentHeader.class, subCriteria);
        subQuery.setAttributes(new String[] {"documentNumber"});
        
        Criteria criteria = new Criteria();
        criteria.addIn("documentNumber", subQuery);        
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            criteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        criteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo("customerInvoiceDocument.accountsReceivableDocumentHeader.processingChartOfAccountCode", chart);
        criteria.addEqualTo("customerInvoiceDocument.accountsReceivableDocumentHeader.processingOrganizationCode", org);
        criteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);        
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(invoiceItemAppliedAmount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findDiscountAmountByProcessingChartAndOrg(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findDiscountAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {

        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria subCriteria = new Criteria();
        if (begin != null) {
            subCriteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            subCriteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        subCriteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        subCriteria.addEqualTo("customerInvoiceDocument.accountsReceivableDocumentHeader.processingChartOfAccountCode", chart);
        subCriteria.addEqualTo("customerInvoiceDocument.accountsReceivableDocumentHeader.processingOrganizationCode", org);
        subCriteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        subCriteria.addEqualToField("documentNumber", Criteria.PARENT_QUERY_PREFIX + "documentNumber");

        ReportQueryByCriteria subReportQuery = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "invoiceItemDiscountLineNumber" }, subCriteria);

        Criteria criteria = new Criteria();
        criteria.addIn("sequenceNumber", subReportQuery);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(amount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;

    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findInvoiceAmountByBillingChartAndOrg(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findInvoiceAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria criteria = new Criteria();
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            criteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        criteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo("customerInvoiceDocument.billByChartOfAccountCode", chart);
        criteria.addEqualTo("customerInvoiceDocument.billedByOrganizationCode", org);
        criteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(amount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findAppliedAmountByBillingChartAndOrg(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findAppliedAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        
        // get approved application payments only
        Criteria subCriteria = new Criteria();
        subCriteria.addEqualTo("financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(FinancialSystemDocumentHeader.class, subCriteria);
        subQuery.setAttributes(new String[] {"documentNumber"});
        
        Criteria criteria = new Criteria();
        criteria.addIn("documentNumber", subQuery);
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            criteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        criteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo("customerInvoiceDocument.billByChartOfAccountCode", chart);
        criteria.addEqualTo("customerInvoiceDocument.billedByOrganizationCode", org);
        criteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(invoiceItemAppliedAmount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findDiscountAmountByBillingChartAndOrg(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findDiscountAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end) {

        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria subCriteria = new Criteria();
        if (begin != null) {
            subCriteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            subCriteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        subCriteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        subCriteria.addEqualTo("customerInvoiceDocument.billByChartOfAccountCode", chart);
        subCriteria.addEqualTo("customerInvoiceDocument.billedByOrganizationCode", org);
        subCriteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        subCriteria.addEqualToField("documentNumber", Criteria.PARENT_QUERY_PREFIX + "documentNumber");

        ReportQueryByCriteria subReportQuery = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "invoiceItemDiscountLineNumber" }, subCriteria);

        Criteria criteria = new Criteria();
        criteria.addIn("sequenceNumber", subReportQuery);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(amount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;

    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findInvoiceAmountByAccount(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findInvoiceAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria criteria = new Criteria();
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            criteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        criteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        criteria.addEqualTo("chartOfAccountsCode", chart);
        criteria.addEqualTo("accountNumber", account);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(amount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findAppliedAmountByAccount(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findAppliedAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end) {
        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        
        // get approved application payments only
        Criteria subCriteria = new Criteria();
        subCriteria.addEqualTo("financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(FinancialSystemDocumentHeader.class, subCriteria);
        subQuery.setAttributes(new String[] {"documentNumber"});
        
        Criteria criteria = new Criteria();
        criteria.addIn("documentNumber", subQuery);
        if (begin != null) {
            criteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            criteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        criteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        criteria.addEqualTo("invoiceDetail.chartOfAccountsCode", chart);
        criteria.addEqualTo("invoiceDetail.accountNumber", account);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(invoiceItemAppliedAmount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerAgingReportDao#findDiscountAmountByAccount(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date)
     */
    public HashMap<String, KualiDecimal> findDiscountAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end) {

        HashMap<String, KualiDecimal> map = new HashMap<String, KualiDecimal>();
        Criteria subCriteria = new Criteria();
        if (begin != null) {
            subCriteria.addGreaterOrEqualThan("customerInvoiceDocument.billingDate", begin);
        }
        if (end != null) {
            subCriteria.addLessOrEqualThan("customerInvoiceDocument.billingDate", end);
        }
        subCriteria.addEqualTo("customerInvoiceDocument.documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        subCriteria.addEqualTo("customerInvoiceDocument.openInvoiceIndicator", true);
        subCriteria.addEqualTo("chartOfAccountsCode", chart);
        subCriteria.addEqualTo("accountNumber", account);
        subCriteria.addEqualToField("documentNumber", Criteria.PARENT_QUERY_PREFIX + "documentNumber");

        ReportQueryByCriteria subReportQuery = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "invoiceItemDiscountLineNumber" }, subCriteria);

        Criteria criteria = new Criteria();
        criteria.addIn("sequenceNumber", subReportQuery);
        ReportQueryByCriteria reportByCriteria = new ReportQueryByCriteria(CustomerInvoiceDetail.class, new String[] { "customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber", "customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName", "sum(amount)" }, criteria);
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber");
        reportByCriteria.addGroupBy("customerInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName");
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportByCriteria);

        while (iterator != null && iterator.hasNext()) {
            Object[] data = (Object[]) iterator.next();
            map.put((String) data[0] + "-" + data[1], (KualiDecimal) data[2]);
        }
        return map;

    }
}
