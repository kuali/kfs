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
package org.kuali.module.purap.dao.ojb;


import java.sql.Date;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.util.VendorGroupingHelper;

/**
 * OJB Implementation of CreditMemoDao. Provides persistence layer methods for the credit memo document.
 */
public class CreditMemoDaoOjb extends PlatformAwareDaoBaseOjb implements CreditMemoDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoDaoOjb.class);

    /**
     * @see org.kuali.module.purap.dao.CreditMemoDao#getCreditMemosToExtract(java.lang.String)
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtract(String chartCode) {
        LOG.debug("getCreditMemosToExtract() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processingCampusCode", chartCode);
        criteria.addIn("statusCode", Arrays.asList(CreditMemoStatuses.STATUSES_ALLOWED_FOR_EXTRACTION));
        criteria.addIsNull("extractedDate");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(CreditMemoDocument.class, criteria));
    }

    
    /**
     * @see org.kuali.module.purap.dao.CreditMemoDao#getCreditMemosToExtractByVendor(java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    public Iterator<CreditMemoDocument> getCreditMemosToExtractByVendor(String chartCode, VendorGroupingHelper vendor ) {
        LOG.debug("getCreditMemosToExtractByVendor() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo( "processingCampusCode", chartCode );
        criteria.addIn( "statusCode", Arrays.asList(CreditMemoStatuses.STATUSES_ALLOWED_FOR_EXTRACTION) );
        criteria.addIsNull( "extractedDate" );
        criteria.addEqualTo( "holdIndicator", Boolean.FALSE );
        criteria.addEqualTo( "vendorHeaderGeneratedIdentifier", vendor.getVendorHeaderGeneratedIdentifier() );
        criteria.addEqualTo( "vendorDetailAssignedIdentifier", vendor.getVendorDetailAssignedIdentifier() );
        criteria.addEqualTo( "vendorCountryCode", vendor.getVendorCountry() );
        criteria.addEqualTo( "vendorPostalCode", vendor.getVendorPostalCode() );

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(CreditMemoDocument.class, criteria));
    }



    /**
     * @see edu.iu.uis.pur.cm.dao.CreditMemoDao#duplicateExists(java.lang.String, java.lang.String)
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, String creditMemoNumber) {
        LOG.debug("duplicateExists() started");

        // criteria: vendorNumberHeader AND vendorNumberDetail AND creditMemoNumber
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorNumberHeaderId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorNumberDetailId);
        criteria.addEqualTo("creditMemoNumber", creditMemoNumber);

        criteria.addNotIn(PurapPropertyConstants.STATUS_CODE, PurapConstants.CreditMemoStatuses.CANCELLED_STATUSES);

        // use the criteria to do a Count against the DB, and return the resulting
        // number. Any positive non-zero result means that a potential duplicate
        // exists and we return true, otherwise, return false.
        int cmCount = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(CreditMemoDocument.class, criteria));
        if (cmCount > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see edu.iu.uis.pur.cm.dao.CreditMemoDao#duplicateExists(java.lang.String, java.lang.String)
     */
    public boolean duplicateExists(Integer vendorNumberHeaderId, Integer vendorNumberDetailId, Date date, KualiDecimal amount) {
        LOG.debug("duplicateExists() started");

        // criteria: vendorNumberHeader AND vendorNumberDetail AND date AND amount
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorNumberHeaderId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorNumberDetailId);
        criteria.addEqualTo("creditMemoDate", date);
        criteria.addEqualTo("creditMemoAmount", amount);

        criteria.addNotIn(PurapPropertyConstants.STATUS_CODE, PurapConstants.CreditMemoStatuses.CANCELLED_STATUSES);

        // use the criteria to do a Count against the DB, and return the resulting
        // number. Any positive non-zero result means that a potential duplicate
        // exists and we return true, otherwise, return false.
        int cmCount = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(CreditMemoDocument.class, criteria));
        if (cmCount > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.module.purap.dao.CreditMemoDao#getDocumentNumberByCreditMemoId(java.lang.Integer)
     */
    public String getDocumentNumberByCreditMemoId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        return getDocumentNumberOfCreditMemoByCriteria(criteria);
    }

    /**
     * Retrieves a document number for a credit memo by user defined criteria.
     * 
     * @param criteria - holds field and value pairs defined by the calling method
     * @return - document number
     */
    private String getDocumentNumberOfCreditMemoByCriteria(Criteria criteria) {
        LOG.debug("getDocumentNumberOfCreditMemoByCriteria() started");
        Iterator<Object[]> iter = getDocumentNumbersOfCreditMemoByCriteria(criteria, false);
        if (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            if (iter.hasNext()) {
                // the iterator should have held only a single doc id of data but it holds 2 or more
                String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
                LOG.error(errorMsg);
                TransactionalServiceUtils.exhaustIterator(iter);
                throw new RuntimeException();
            }
            // at this part of the code, we know there's no more elements in iterator
            return (String) cols[0];
        }
        return null;
    }

    /**
     * Retrieves a document number for a credit memo by user defined criteria and sorts the values ascending if orderByAscending
     * parameter is true, descending otherwise.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @param orderByAscending - boolean indicating results should be sorted ascending, descending otherwise
     * @return - Iterator of document numbers
     */
    private Iterator<Object[]> getDocumentNumbersOfCreditMemoByCriteria(Criteria criteria, boolean orderByAscending) {
        LOG.debug("getDocumentNumberOfCreditMemoByCriteria() started");
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(CreditMemoDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }

}
