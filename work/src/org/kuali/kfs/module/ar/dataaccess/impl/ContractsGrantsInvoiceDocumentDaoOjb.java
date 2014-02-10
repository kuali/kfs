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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation class for ContractsGrantsInvoiceDocumentDao DAO.
 */
public class ContractsGrantsInvoiceDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements ContractsGrantsInvoiceDocumentDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getAllOpen()
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getAllOpen() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, true);
        criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        QueryByCriteria qbc = QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria);

        Collection<ContractsGrantsInvoiceDocument> customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return customerinvoicedocuments;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getAllCGInvoiceDocuments()
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getAllCGInvoiceDocuments() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        QueryByCriteria qbc = QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria);

        Collection<ContractsGrantsInvoiceDocument> customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return customerinvoicedocuments;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesByCollection(java.util.Map)
     *      Retrieve CG Invoices that are in final, with some additional field values passed.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByCollection(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new ContractsGrantsInvoiceDocument());

        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesForReferallExcludingOutsideCollectionAgency(java.util.Map,
     *      String) Retrieve CG Invoices that are in final, with some additional field values passed.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesForReferallExcludingOutsideCollectionAgency(Map fieldValues, String outsideColAgencyCodeToExclude) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new ContractsGrantsInvoiceDocument());
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        Criteria referralNull = new Criteria();
        Criteria referralOutside = new Criteria();
        Criteria subCriteria = new Criteria();


        referralNull.addIsNull(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TYPE);
        referralOutside.addNotEqualTo(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TYPE, outsideColAgencyCodeToExclude);

        subCriteria.addOrCriteria(referralNull);
        subCriteria.addOrCriteria(referralOutside);

        criteria.addAndCriteria(subCriteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesByCollection(java.util.Map)
     *      Retrieve CG Invoices that are in final, with some additional field values passed.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByCollectionAndDateRange(Map fieldValues, Date begin, Date end) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new ContractsGrantsInvoiceDocument());
        criteria.addNotNull(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE);

        if (ObjectUtils.isNotNull(begin)) {
            criteria.addGreaterOrEqualThan(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE, begin);
        }
        if (ObjectUtils.isNotNull(end)) {
            criteria.addLessOrEqualThan(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE, end);
        }

        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getMatchingInvoicesByCollection(java.util.Map)
     *      Retrieve CG Invoices that are in final, with some additional field values passed.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByCollection(Map fieldValues, Collection<String> excludedInvoiceNumbers) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new ContractsGrantsInvoiceDocument());

        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        if (CollectionUtils.isNotEmpty(excludedInvoiceNumbers)) {
            criteria.addNotIn(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, excludedInvoiceNumbers);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getOpenAndFinalInvoicesByCustomerNumber(java.lang.String)
     *      Retrieve CG Invoices that are open and final, with param customer number.
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getOpenInvoicesByCustomerNumber(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, "true");
        criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(ContractsGrantsInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao#getFinancialDocumentInErrorNumbers()
     */
    @Override
    public Collection<String> getFinancialDocumentInErrorNumbers() {
        Criteria subCri = new Criteria();
        subCri.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        subCri.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);
        subCri.addNotNull(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_IN_ERROR_NUMBER);
        ReportQueryByCriteria reportQuery = new ReportQueryByCriteria(ContractsGrantsInvoiceDocument.class, new String[] { KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_IN_ERROR_NUMBER }, subCri);

        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        List<String> invoiceNumbers = new ArrayList<String>();
        while (iter.hasNext()) {
            invoiceNumbers.add((String) iter.next()[0]);
        }
        return invoiceNumbers;
    }

}
