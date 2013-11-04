/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class CustomerInvoiceDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerInvoiceDocumentDao {

    private static org.apache.log4j.Logger LOG = 
        org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentDaoOjb.class);
    
    public List<String> getPrintableCustomerInvoiceDocumentNumbersFromUserQueue() {
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("printInvoiceIndicator", ArConstants.PrintInvoiceOptions.PRINT_BY_USER);
        criteria.addIsNull("printDate");
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        
        //  Why use the OJB reports approach here, rather than a list of CustomerInvoiceDocuments?  
        //
        //  This was done because even if we had the invoice documents, we then need to do a proper document load 
        // via the documentService, which loads up the workflow information as well and properly prepares the document.
        //
        //  Therefore, at this stage, there's no reason to load entire documents, all we need are document numbers.  And with 
        // OJB, this is how you get just a collection of a single column's value out.  Given the performance issues associated 
        // with live reporting like this, the attempt was made to minimize the resource usage. 
        
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(CustomerInvoiceDocument.class, new String[] { "documentNumber" }, criteria, false);
        
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        List<String> invoiceNumbers = new ArrayList<String>(); 
        while (iter.hasNext()) {
            invoiceNumbers.add((String)iter.next()[0]);
        }
        return invoiceNumbers;
    }
    
    public List<String> getPrintableCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank chartOfAccountsCode parameter.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank organizationCode parameter.");
        }

        //  Why use the OJB reports approach here, rather than a list of CustomerInvoiceDocuments?  
        //
        //  This was done because even if we had the invoice documents, we then need to do a proper document load 
        // via the documentService, which loads up the workflow information as well and properly prepares the document.
        //
        //  Therefore, at this stage, there's no reason to load entire documents, all we need are document numbers.  And with 
        // OJB, this is how you get just a collection of a single column's value out.  Given the performance issues associated 
        // with live reporting like this, the attempt was made to minimize the resource usage. 
        
        // select i.fdoc_nbr
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        // where h.prcs_fin_coa_cd = ? and h.prcs_org_cd = ? 
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsReceivableDocumentHeader.processingChartOfAccountCode", chartOfAccountsCode);
        criteria.addEqualTo("accountsReceivableDocumentHeader.processingOrganizationCode", organizationCode);
        criteria.addEqualTo("printInvoiceIndicator", ArConstants.PrintInvoiceOptions.PRINT_BY_PROCESSING_ORG);
        criteria.addIsNull("printDate");
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(CustomerInvoiceDocument.class, new String[] { "documentNumber" }, criteria, false);
        
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        List<String> invoiceNumbers = new ArrayList<String>(); 
        while (iter.hasNext()) {
            invoiceNumbers.add((String)iter.next()[0]);
        }
        return new ArrayList<String>(invoiceNumbers);
    }
    
    public List<String> getPrintableCustomerInvoiceDocumentNumbersByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank chartOfAccountsCode parameter.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank organizationCode parameter.");
        }

        //  Why use the OJB reports approach here, rather than a list of CustomerInvoiceDocuments?  
        //
        //  This was done because even if we had the invoice documents, we then need to do a proper document load 
        // via the documentService, which loads up the workflow information as well and properly prepares the document.
        //
        //  Therefore, at this stage, there's no reason to load entire documents, all we need are document numbers.  And with 
        // OJB, this is how you get just a collection of a single column's value out.  Given the performance issues associated 
        // with live reporting like this, the attempt was made to minimize the resource usage. 
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("billByChartOfAccountCode", chartOfAccountsCode);
        criteria.addEqualTo("billedByOrganizationCode", organizationCode);
        criteria.addEqualTo("printInvoiceIndicator", ArConstants.PrintInvoiceOptions.PRINT_BY_BILLING_ORG);
        criteria.addIsNull("printDate");
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(CustomerInvoiceDocument.class, new String[] { "documentNumber" }, criteria, false);
        
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        List<String> invoiceNumbers = new ArrayList<String>(); 
        while (iter.hasNext()) {
            invoiceNumbers.add((String)iter.next()[0]);
        }
        return new ArrayList<String>(invoiceNumbers);
    }

    /**
     * Very similar to above except lacks check for print invoice indicator and print date.
     * 
     * @see org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao#getPrintableCustomerInvoiceDocumentNumbersForBillingStatementByBillingChartAndOrg(java.lang.String, java.lang.String)
     */
    public List<String> getPrintableCustomerInvoiceDocumentNumbersForBillingStatementByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank chartOfAccountsCode parameter.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank organizationCode parameter.");
        }

        //  Why use the OJB reports approach here, rather than a list of CustomerInvoiceDocuments?  
        //
        //  This was done because even if we had the invoice documents, we then need to do a proper document load 
        // via the documentService, which loads up the workflow information as well and properly prepares the document.
        //
        //  Therefore, at this stage, there's no reason to load entire documents, all we need are document numbers.  And with 
        // OJB, this is how you get just a collection of a single column's value out.  Given the performance issues associated 
        // with live reporting like this, the attempt was made to minimize the resource usage. 
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("billByChartOfAccountCode", chartOfAccountsCode);
        criteria.addEqualTo("billedByOrganizationCode", organizationCode);
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(CustomerInvoiceDocument.class, new String[] { "documentNumber" }, criteria, false);
        
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        List<String> invoiceNumbers = new ArrayList<String>(); 
        while (iter.hasNext()) {
            invoiceNumbers.add((String)iter.next()[0]);
        }
        return new ArrayList<String>(invoiceNumbers);
    }

    public List<String> getCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank chartOfAccountsCode parameter.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank organizationCode parameter.");
        }

        //  Why use the OJB reports approach here, rather than a list of CustomerInvoiceDocuments?  
        //
        //  This was done because even if we had the invoice documents, we then need to do a proper document load 
        // via the documentService, which loads up the workflow information as well and properly prepares the document.
        //
        //  Therefore, at this stage, there's no reason to load entire documents, all we need are document numbers.  And with 
        // OJB, this is how you get just a collection of a single column's value out.  Given the performance issues associated 
        // with live reporting like this, the attempt was made to minimize the resource usage. 
        
        // select i.fdoc_nbr
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        // where h.prcs_fin_coa_cd = ? and h.prcs_org_cd = ? 
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsReceivableDocumentHeader.processingChartOfAccountCode", chartOfAccountsCode);
        criteria.addEqualTo("accountsReceivableDocumentHeader.processingOrganizationCode", organizationCode);
        
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(CustomerInvoiceDocument.class, new String[] { "documentNumber" }, criteria, false);
        
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        List<String> invoiceNumbers = new ArrayList<String>(); 
        while (iter.hasNext()) {
            invoiceNumbers.add((String)iter.next()[0]);
        }
        return new ArrayList<String>(invoiceNumbers);
    }
    
    public List<String> getCustomerInvoiceDocumentNumbersByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank chartOfAccountsCode parameter.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("The method was called with a Null or Blank organizationCode parameter.");
        }

        //  Why use the OJB reports approach here, rather than a list of CustomerInvoiceDocuments?  
        //
        //  This was done because even if we had the invoice documents, we then need to do a proper document load 
        // via the documentService, which loads up the workflow information as well and properly prepares the document.
        //
        //  Therefore, at this stage, there's no reason to load entire documents, all we need are document numbers.  And with 
        // OJB, this is how you get just a collection of a single column's value out.  Given the performance issues associated 
        // with live reporting like this, the attempt was made to minimize the resource usage. 
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("billByChartOfAccountCode", chartOfAccountsCode);
        criteria.addEqualTo("billedByOrganizationCode", organizationCode);
        
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(CustomerInvoiceDocument.class, new String[] { "documentNumber" }, criteria, false);
        
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        List<String> invoiceNumbers = new ArrayList<String>(); 
        while (iter.hasNext()) {
            invoiceNumbers.add((String)iter.next()[0]);
        }
        return new ArrayList<String>(invoiceNumbers);
    }
    
    public Collection getAllOpen() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("openInvoiceIndicator", true);
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);

        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }

    public Collection getOpenByCustomerNumber(String customerNumber) {
        // select i.* 
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        // where h.cust_nbr = ?
        
        //  OJB deals with the inner join automatically, because we have it setup with 
        // accountsReceivableDocumentHeader as a ReferenceDescriptor to Invoice.
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsReceivableDocumentHeader.customerNumber", customerNumber==null?customerNumber:customerNumber.toUpperCase());
        criteria.addEqualTo("openInvoiceIndicator", "true");
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);
        
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }
    
    public Collection getOpenByCustomerNameByCustomerType(String customerName, String customerTypeCode) {
        // select i.* 
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        //   inner join ar_cust_t c 
        //   on h.cust_nbr = c.cust_nbr 
        // where c.cust_nm like ? and c.cust_typ_cd = ?
        
        Criteria criteria = new Criteria();
        criteria.addLike("accountsReceivableDocumentHeader.customer.customerName", customerName);
        criteria.addEqualTo("accountsReceivableDocumentHeader.customer.customerTypeCode", customerTypeCode);
        criteria.addEqualTo("openInvoiceIndicator", "true");
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);
        
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }
    
    public Collection getOpenByCustomerName(String customerName) {
        // select i.* 
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        //   inner join ar_cust_t c 
        //   on h.cust_nbr = c.cust_nbr 
        // where c.cust_nm like ? 
        
        Criteria criteria = new Criteria();
        criteria.addLike("accountsReceivableDocumentHeader.customer.customerName", customerName);
        criteria.addEqualTo("openInvoiceIndicator", "true");
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);
        
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }
    
    public Collection getOpenByCustomerType(String customerTypeCode) {
        // select i.* 
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        //   inner join ar_cust_t c 
        //   on h.cust_nbr = c.cust_nbr 
        // where c.cust_typ_cd = ?  
        
        //  OJB deals with the inner join automatically, because we have it setup with 
        // accountsReceivableDocumentHeader as a ReferenceDescriptor to Invoice, and Customer 
        // as a referencedescriptor to accountsReceivableDocumentHeader.
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsReceivableDocumentHeader.customer.customerTypeCode", customerTypeCode);
        criteria.addEqualTo("openInvoiceIndicator", "true");
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);

        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);
        
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao#getInvoiceByOrganizationInvoiceNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("organizationInvoiceNumber", organizationInvoiceNumber);
        
        return (CustomerInvoiceDocument) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria));
        
    }

    /**
     * @see org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao#getInvoiceByInvoiceDocumentNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        return (CustomerInvoiceDocument) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria));
    }
    
}
