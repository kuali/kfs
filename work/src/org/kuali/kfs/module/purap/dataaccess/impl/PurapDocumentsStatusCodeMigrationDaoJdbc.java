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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

/**
 * A class to do the database queries needed to prepare documents status codes and descriptions.
 */
@Transactional
public class PurapDocumentsStatusCodeMigrationDaoJdbc extends PlatformAwareDaoBaseJdbc implements PurapDocumentsStatusCodeMigrationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapDocumentsStatusCodeMigrationDaoJdbc.class);
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getRequisitionDocumentDetails()
     */
    public Map<String, String> getRequisitionDocumentDetails() {
        LOG.debug("getRequisitionDocumentDetails() started");
        
        Map<String, String> requistionDetails = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM PUR_REQS_T WHERE REQS_STAT_CD IS NOT NULL"); 

            while (statusesRowSet.next()) {
                requistionDetails.put(statusesRowSet.getString("FDOC_NBR"), statusesRowSet.getString("REQS_STAT_CD"));
            }
            
            LOG.debug("getRequisitionDocumentDetails() exited");
            return requistionDetails;
            
        } catch (DataAccessException dae) {
            return requistionDetails;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getPurchaseOrderDocumentDetails()
     */
    public Map<String, String> getPurchaseOrderDocumentDetails() {
        LOG.debug("getPurchaseOrderDocumentDetails() started");
        
        Map<String, String> purchaseOrderDetails = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM PUR_PO_T WHERE PO_STAT_CD IS NOT NULL"); 

            while (statusesRowSet.next()) {
                purchaseOrderDetails.put(statusesRowSet.getString("FDOC_NBR"), statusesRowSet.getString("PO_STAT_CD"));
            }
            
            LOG.debug("getPurchaseOrderDocumentDetails() exited");
            
            return purchaseOrderDetails;
        } catch (DataAccessException dae) {
            return purchaseOrderDetails;
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getPaymentRequestDocumentDetails()
     */
    public Map<String, String> getPaymentRequestDocumentDetails() {
        LOG.debug("getPaymentRequestDocumentDetails() started");
        
        Map<String, String> paymentRequestDetails = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM AP_PMT_RQST_T WHERE PMT_RQST_STAT_CD IS NOT NULL"); 

            while (statusesRowSet.next()) {
                paymentRequestDetails.put(statusesRowSet.getString("FDOC_NBR"), statusesRowSet.getString("PMT_RQST_STAT_CD"));
            }
            
            LOG.debug("getPaymentRequestDocumentDetails() exited");
            
            return paymentRequestDetails;
        } catch (DataAccessException dae) {
            return paymentRequestDetails;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getVendorCreditMemoDocumentDetails()
     */
    public Map<String, String> getVendorCreditMemoDocumentDetails() {
        LOG.debug("getVendorCreditMemoDocumentDetails() started");
        
        Map<String, String> vendorCreditMemoDetails = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM AP_CRDT_MEMO_T WHERE CRDT_MEMO_STAT_CD IS NOT NULL"); 

            while (statusesRowSet.next()) {
                vendorCreditMemoDetails.put(statusesRowSet.getString("FDOC_NBR"), statusesRowSet.getString("CRDT_MEMO_STAT_CD"));
            }
            
            LOG.debug("getVendorCreditMemoDocumentDetails() exited");
            
            return vendorCreditMemoDetails;
        } catch (DataAccessException dae) {
            return vendorCreditMemoDetails;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#getLineItemReceivingDocumentDetails()
     */
    public Map<String, String> getLineItemReceivingDocumentDetails() {
        LOG.debug("getLineItemReceivingDocumentDetails() started");
        
        Map<String, String> lineItemRecvDetails = new HashMap<String, String>();
        
        try {
            SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM PUR_RCVNG_LN_T WHERE RCVNG_LN_STAT_CD IS NOT NULL"); 

            while (statusesRowSet.next()) {
                lineItemRecvDetails.put(statusesRowSet.getString("FDOC_NBR"), statusesRowSet.getString("RCVNG_LN_STAT_CD"));
            }
            
            LOG.debug("getLineItemReceivingDocumentDetails() exited");
            
            return lineItemRecvDetails;
        } catch (DataAccessException dae) {
            return lineItemRecvDetails;
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao#updateAndSaveMigratedApplicationDocumentStatuses(java.lang.String, java.lang.String, java.util.Date)
     */
    public boolean updateAndSaveMigratedApplicationDocumentStatuses(String documentNumber, String applicationDocumentStatus, Date applicationDocumentStatusModifiedDate) {
        boolean success = true;
        
        String sql = "UPDATE KREW_DOC_HDR_T SET APP_DOC_STAT = '" + applicationDocumentStatus + "', APP_DOC_STAT_MDFN_DT = '" + applicationDocumentStatusModifiedDate + "' WHERE DOC_HDR_ID = '" + documentNumber + "'";
        
        getJdbcTemplate().execute(sql);
        
        return success;
    }
    
}   
