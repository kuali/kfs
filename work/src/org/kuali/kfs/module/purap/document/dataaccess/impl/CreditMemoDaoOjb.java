/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.dataaccess.CreditMemoDao;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

/**
 * OJB Implementation of CreditMemoDao. Provides persistence layer methods for the credit memo document.
 */
@Transactional
public class CreditMemoDaoOjb extends PlatformAwareDaoBaseOjb implements CreditMemoDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.CreditMemoDao#getCreditMemosToExtract(java.lang.String)
     */
    public List<VendorCreditMemoDocument> getCreditMemosToExtract(String chartCode) {
        LOG.debug("getCreditMemosToExtract() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processingCampusCode", chartCode);
        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        return (List<VendorCreditMemoDocument>)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(VendorCreditMemoDocument.class, criteria));
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.CreditMemoDao#getCreditMemosToExtractByVendor(java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    public Collection<VendorCreditMemoDocument> getCreditMemosToExtractByVendor(String chartCode, VendorGroupingHelper vendor ) {
        LOG.debug("getCreditMemosToExtractByVendor() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo( "processingCampusCode", chartCode );
        criteria.addIsNull( "extractedTimestamp" );
        criteria.addEqualTo( "holdIndicator", Boolean.FALSE );
        criteria.addEqualTo( "vendorHeaderGeneratedIdentifier", vendor.getVendorHeaderGeneratedIdentifier() );
        criteria.addEqualTo( "vendorDetailAssignedIdentifier", vendor.getVendorDetailAssignedIdentifier() );
        criteria.addEqualTo( "vendorCountryCode", vendor.getVendorCountry() );
        criteria.addLike( "vendorPostalCode", vendor.getVendorPostalCode() + "%" );

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(VendorCreditMemoDocument.class, criteria));
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

        // use the criteria to do a Count against the DB, and return the resulting
        // number. Any positive non-zero result means that a potential duplicate
        // exists and we return true, otherwise, return false.
        int cmCount = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(VendorCreditMemoDocument.class, criteria));
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

        // use the criteria to do a Count against the DB, and return the resulting
        // number. Any positive non-zero result means that a potential duplicate
        // exists and we return true, otherwise, return false.
        int cmCount = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(VendorCreditMemoDocument.class, criteria));
        if (cmCount > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.CreditMemoDao#getDocumentNumberByCreditMemoId(java.lang.Integer)
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
    protected String getDocumentNumberOfCreditMemoByCriteria(Criteria criteria) {
        LOG.debug("getDocumentNumberOfCreditMemoByCriteria() started");
     //   Iterator<Object[]> iter = getDocumentNumbersOfCreditMemoByCriteria(criteria, false);
        List<String> returnList = getDocumentNumbersOfCreditMemoByCriteria(criteria, false);
        if (returnList.isEmpty()) {
            return null;
        }
        
        if (returnList.size() > 1) {
            String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
            LOG.error(errorMsg);
            throw new RuntimeException();
        } else {
            return returnList.get(0);
        }
    }

    /**
     * Retrieves a document number for a credit memo by user defined criteria and sorts the values ascending if orderByAscending
     * parameter is true, descending otherwise.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @param orderByAscending - boolean indicating results should be sorted ascending, descending otherwise
     * @return - Iterator of document numbers
     */
    protected List<String> getDocumentNumbersOfCreditMemoByCriteria(Criteria criteria, boolean orderByAscending) {
        LOG.debug("getDocumentNumberOfCreditMemoByCriteria() started");
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(VendorCreditMemoDocument.class, criteria);
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        
        List<VendorCreditMemoDocument> vcmds = (List<VendorCreditMemoDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(rqbc);
        
        List<String> returnList = new ArrayList<String>();
        
        for (VendorCreditMemoDocument vcmd : vcmds) {
            returnList.add(vcmd.getDocumentNumber());
        }
        return returnList;
    }

    public List<String> getActiveCreditMemoDocumentNumbersForPurchaseOrder(Integer purchaseOrderId){
        LOG.debug("getActiveCreditmemoDocumentNumbersForPurchaseOrder() started");
                
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, purchaseOrderId);
        //criteria.addIn(PurapPropertyConstants.STATUS_CODE, Arrays.asList(CreditMemoStatuses.STATUSES_POTENTIALLY_ACTIVE));
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        
        returnList = getDocumentNumbersOfCreditMemoByCriteria(criteria, false);
        return returnList;
    }
}
