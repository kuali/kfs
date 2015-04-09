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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * A class to do the database queries needed to prepare documents status codes and descriptions.
 */
public class StatusCodeAndDescriptionForPurapDocumentsDaoJdbc extends PlatformAwareDaoBaseJdbc implements StatusCodeAndDescriptionForPurapDocumentsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StatusCodeAndDescriptionForPurapDocumentsDaoJdbc.class);
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao#getRequisitionDocumentStatuses()
     */
    public Map<String, String> getRequisitionDocumentStatuses() {
        LOG.debug("getRequisitionDocumentStatuses() started");
        
        Map<String, String> requistionStatuses = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM PUR_REQS_STAT_T ORDER BY REQS_STAT_CD"); 

            while (statusesRowSet.next()) {
                requistionStatuses.put(statusesRowSet.getString("REQS_STAT_CD"), statusesRowSet.getString("REQS_STAT_DESC"));
            }
            
            LOG.debug("getRequisitionDocumentStatuses() exited");
            
            return requistionStatuses;
        } catch (DataAccessException dae) {
            return requistionStatuses;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao#getPurchaseOrderDocumentStatuses()
     */
    public Map<String, String> getPurchaseOrderDocumentStatuses() {
        LOG.debug("getPurchaseOrderDocumentStatuses() started");
        
        Map<String, String> purchaseOrderStatuses = new HashMap<String, String>();
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM PUR_PO_STAT_T ORDER BY PO_STAT_CD"); 
    
            while (statusesRowSet.next()) {
                purchaseOrderStatuses.put(statusesRowSet.getString("PO_STAT_CD"), statusesRowSet.getString("PO_STAT_DESC"));
            }
            
            LOG.debug("getPurchaseOrderDocumentStatuses() exited");
            
            return purchaseOrderStatuses;
        } catch (DataAccessException dae) {
            return purchaseOrderStatuses;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao#getPaymentRequestDocumentStatuses()
     */
    public Map<String, String> getPaymentRequestDocumentStatuses() {
        LOG.debug("getPaymentRequestDocumentStatuses() started");
        
        Map<String, String> paymentRequestStatuses = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM AP_PMT_RQST_STAT_T ORDER BY PMT_RQST_STAT_CD"); 
    
            while (statusesRowSet.next()) {
                paymentRequestStatuses.put(statusesRowSet.getString("PMT_RQST_STAT_CD"), statusesRowSet.getString("PMT_RQST_STAT_DESC"));
            }
            
            LOG.debug("getPaymentRequestDocumentStatuses() exited");
            
            return paymentRequestStatuses;
        } catch (DataAccessException dae) {
            return paymentRequestStatuses;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao#getVendorCreditMemoDocumentStatuses()
     */
    public Map<String, String> getVendorCreditMemoDocumentStatuses() {
        LOG.debug("getVendorCreditMemoDocumentStatuses() started");
        
        Map<String, String> vendorCreditMemoStatuses = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM AP_CRDT_MEMO_STAT_T ORDER BY CRDT_MEMO_STAT_CD"); 
    
            while (statusesRowSet.next()) {
                vendorCreditMemoStatuses.put(statusesRowSet.getString("CRDT_MEMO_STAT_CD"), statusesRowSet.getString("CRDT_MEMO_STAT_DESC"));
            }
            
            LOG.debug("getVendorCreditMemoDocumentStatuses() exited");
            
            return vendorCreditMemoStatuses;
        } catch (DataAccessException dae) {
            return vendorCreditMemoStatuses;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao#getLineItemReceivingDocumentStatuses()
     */
    public Map<String, String> getLineItemReceivingDocumentStatuses() {
        LOG.debug("getLineItemReceivingDocumentStatuses() started");
        
        Map<String, String> lineItemReceivingStatuses = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM PUR_RCVNG_LN_STAT_T ORDER BY RCVNG_LN_STAT_CD"); 
    
            while (statusesRowSet.next()) {
                lineItemReceivingStatuses.put(statusesRowSet.getString("RCVNG_LN_STAT_CD"), statusesRowSet.getString("RCVNG_LN_STAT_DESC"));
            }
            
            LOG.debug("getLineItemReceivingDocumentStatuses() exited");
            
            return lineItemReceivingStatuses;
        } catch (DataAccessException dae) {
            return lineItemReceivingStatuses;
        }
    }
}   
