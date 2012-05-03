/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.purap.batch.service.impl;

import java.util.Map;

import org.kuali.kfs.module.purap.batch.service.MigratePurapStatCodeToWorkflowDocumentService;
import org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao;
import org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the MigratePurapStatCodeToWorkflowDocumentService batch job.
 */
@Transactional
public class MigratePurapStatCodeToWorkflowDocumentServiceImpl implements MigratePurapStatCodeToWorkflowDocumentService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MigratePurapStatCodeToWorkflowDocumentServiceImpl.class);
    public static final String WORKFLOW_DOCUMENT_HEADER_ID_SEARCH_RESULT_KEY = "routeHeaderId";
    
    protected WorkflowDocumentService workflowDocumentService;
    protected StatusCodeAndDescriptionForPurapDocumentsDao statusCodeAndDescriptionForPurapDocumentsDao;
    protected ReportWriterService migratePurapStatCodeReportService;
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected PurapDocumentsStatusCodeMigrationDao purapDocumentsStatusCodeMigrationDao;
    
    /**
     * Constructs a AutoDisapproveDocumentsServiceImpl instance
     */
    public MigratePurapStatCodeToWorkflowDocumentServiceImpl() {
        
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.batch.service.MigratePurapStatCodeToWorkflowDocumentService#migratePurapStatCodeToWorkflowDocuments()
     */
    public boolean migratePurapStatCodeToWorkflowDocuments() {
        LOG.debug("migratePurapStatCodeToWorkflowDocuments() started");
        
        boolean success = true ;
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("******************** Migration of StatusCode to Workflow document started ********************\n");

        //Step 1: Go through REQ documents and move the status code to workflow documents
        success &= processRequisitionDocumentsForStatusCodeMigration();
        
      //Step 2: Go through PO documents and move the status code to workflow documents
        success &= processPurchaseOrderDocumentsForStatusCodeMigration();

        //Step 3: Go through PREQ documents and move the status code to workflow documents
        success &= processPaymentRequestDocumentsForStatusCodeMigration();
        
        //Step 4: Go through Vendor Credit Memo documents and move the status code to workflow documents
        success &= processVendorCreditMemoDocumentsForStatusCodeMigration();
        
        //Step 5: Go through Line Item Receiving documents and move the status code to workflow documents
        success &= processLineItemReceivingDocumentsForStatusCodeMigration();
        
        LOG.debug("migratePurapStatCodeToWorkflowDocuments() completed");
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("\n******************** Migration of StatusCode to Workflow document completed ********************\n");
        
        return success;
    }
    
    /**
     * Helper method to write a no records to process message
     * 
     * @param details Name of the processing details, like Requisition Documents etc,.
     * @param processType
     */
    protected void writeNoRecordsMessage(Map<String, String> details, String detailName) {
        if (details == null || details.size() == 0) {
            migratePurapStatCodeReportService.writeFormattedMessageLine("\t\tNo Data exists for processing in  " + detailName);
        }
    }
    
    /**
     * Processes the requisitions for status code migration.  Creates a list of documents numbers
     * and current status code where status code needs migration and using that list the workflow documents
     * will be retrieved.  Each workflow document then will be updated with the corresponding
     * application document status that is new in KFS 5.0
     * 
     * @return true if the requisitions status code successfully migrates else return false.
     */
    protected boolean processRequisitionDocumentsForStatusCodeMigration() {
        LOG.debug("processRequisitionDocumentsForStatusCodeMigration() started");
        
        boolean success = true;

        migratePurapStatCodeReportService.writeFormattedMessageLine("********** Migration of Requistions StatusCode to Workflow document started **********\n");
        
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        
        //get the status code/descriptions from the table pur_reqs_stat_t
        Map<String, String> requisitionStatusMap = getStatusCodeAndDescriptionForPurapDocumentsDao().getRequisitionDocumentStatuses();
        
        //get the requisitions details where REQS_STAT_CD is not null....
        Map<String, String> requisitionDetails = getPurapDocumentsStatusCodeMigrationDao().getRequisitionDocumentDetails();
        
        writeNoRecordsMessage(requisitionDetails, "Requisition Documents.");
        
        for (String reqDocNumber : requisitionDetails.keySet()) {
            String statusCode = requisitionDetails.get(reqDocNumber);
            
            String newApplicationDocumentStatus = requisitionStatusMap.get(statusCode);
            
            migratePurapStatCodeReportService.writeFormattedMessageLine("\t\tREQ Doc: " + reqDocNumber + " Status Code: " + statusCode + " Status Description: " + newApplicationDocumentStatus);
            
            //find the workflow document and update the app_doc_stat and app_doc_stat_mdfn_dt columns.
            getPurapDocumentsStatusCodeMigrationDao().updateAndSaveMigratedApplicationDocumentStatuses(reqDocNumber, newApplicationDocumentStatus, getDateTimeService().getCurrentTimestamp());

            //now reindex the requistion for document search...
            documentAttributeIndexingQueue.indexDocument(reqDocNumber);
        }
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("\n********** Migration of Requistions StatusCode to Workflow document completed **********\n\n");
        
        LOG.debug("processRequisitionDocumentsForStatusCodeMigration() completed");
        
        return success;
    }
    
    /**
     * Processes the payment request documents for status code migration.  Creates a list of documents numbers
     * and current status code where status code needs migration and using that list the workflow documents
     * will be retrieved.  Each workflow document then will be updated with the corresponding
     * application document status that is new in KFS 5.0
     * 
     * @return true if the payment request documents status code successfully migrates else return false.
     */
    protected boolean processPaymentRequestDocumentsForStatusCodeMigration() {
        LOG.debug("processPaymentRequestDocumentsForStatusCodeMigration() started");
        
        boolean success = true;

        migratePurapStatCodeReportService.writeFormattedMessageLine("********** Migration of Payment Requests StatusCode to Workflow document started **********\n");
        
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
 
        //get the status code/descriptions from the table ap_pmt_rqst_stat_t
        Map<String, String> paymentRequestStatusMap = getStatusCodeAndDescriptionForPurapDocumentsDao().getPaymentRequestDocumentStatuses();
        
        //get the payment request details where PMT_RQST_STAT_CD is not null....
        Map<String, String> paymentRequestDetails = getPurapDocumentsStatusCodeMigrationDao().getPaymentRequestDocumentDetails();

        writeNoRecordsMessage(paymentRequestDetails, "Payment Request Documents.");

        for (String preqDocNumber : paymentRequestDetails.keySet()) {
            String statusCode = paymentRequestDetails.get(preqDocNumber);
            
            String newApplicationDocumentStatus = paymentRequestStatusMap.get(statusCode);
            
            migratePurapStatCodeReportService.writeFormattedMessageLine("\t\tPREQ Doc: " + preqDocNumber + " Status Code: " + statusCode + " Status Description: " + newApplicationDocumentStatus);
            
            //find the workflow document and update the app_doc_stat and app_doc_stat_mdfn_dt columns.
            getPurapDocumentsStatusCodeMigrationDao().updateAndSaveMigratedApplicationDocumentStatuses(preqDocNumber, newApplicationDocumentStatus, getDateTimeService().getCurrentTimestamp());

            //now reindex the requistion for document search...
            documentAttributeIndexingQueue.indexDocument(preqDocNumber);
        }
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("\n********** Migration of Payment Requests StatusCode to Workflow document completed **********\n\n");
        
        LOG.debug("processPaymentRequestDocumentsForStatusCodeMigration() completed");
        
        return success;
    }
    
    /**
     * Processes the purchase order documents for status code migration.  Creates a list of documents numbers
     * and current status code where status code needs migration and using that list the workflow documents
     * will be retrieved.  Each workflow document then will be updated with the corresponding
     * application document status that is new in KFS 5.0
     * 
     * @return true if the payment request status code successfully migrates else return false.
     */
    protected boolean processPurchaseOrderDocumentsForStatusCodeMigration() {
        LOG.debug("processPurchaseOrderDocumentsForStatusCodeMigration() started");
        
        boolean success = true;

        migratePurapStatCodeReportService.writeFormattedMessageLine("********** Migration of Purchase Order StatusCode to Workflow document started **********\n");
        
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
 
        //get the status code/descriptions from the table PUR_PO_STAT_T
        Map<String, String> purchaseOrderStatusMap = getStatusCodeAndDescriptionForPurapDocumentsDao().getPurchaseOrderDocumentStatuses();
        
        //get the purchase order details where PO_STAT_CD is not null....
        Map<String, String> poDetails = getPurapDocumentsStatusCodeMigrationDao().getPurchaseOrderDocumentDetails();

        writeNoRecordsMessage(poDetails, "Purchase Order Documents.");
        
        for (String poDocNumber : poDetails.keySet()) {
            String statusCode = poDetails.get(poDocNumber);
            
            String newApplicationDocumentStatus = purchaseOrderStatusMap.get(statusCode);
            
            migratePurapStatCodeReportService.writeFormattedMessageLine("\t\tPO Doc: " + poDocNumber + " Status Code: " + statusCode + " Status Description: " + newApplicationDocumentStatus);
            
            //find the workflow document and update the app_doc_stat and app_doc_stat_mdfn_dt columns.
            getPurapDocumentsStatusCodeMigrationDao().updateAndSaveMigratedApplicationDocumentStatuses(poDocNumber, newApplicationDocumentStatus, getDateTimeService().getCurrentTimestamp());

            //now reindex the requistion for document search...
            documentAttributeIndexingQueue.indexDocument(poDocNumber);
        }
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("\n********** Migration of Purchase Order StatusCode to Workflow document completed **********\n\n");
        
        LOG.debug("processPurchaseOrderDocumentsForStatusCodeMigration() completed");
        
        return success;
    }

    
    /**
     * Processes the vendor credit memos for status code migration.  Creates a list of documents numbers
     * and current status code where status code needs migration and using that list the workflow documents
     * will be retrieved.  Each workflow document then will be updated with the corresponding
     * application document status that is new in KFS 5.0
     * 
     * @return true if the vendor credit memo status code successfully migrates else return false.
     */
    protected boolean processVendorCreditMemoDocumentsForStatusCodeMigration() {
        LOG.debug("processVendorCreditMemoDocumentsForStatusCodeMigration() started");
        
        boolean success = true;

        migratePurapStatCodeReportService.writeFormattedMessageLine("********** Migration of Vendor Credit Memo StatusCode to Workflow document started **********\n");
        
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
 
        //get the status code/descriptions from the table ap_crdt_memo_stat_t
        Map<String, String> vendorCreditMemoStatusMap = getStatusCodeAndDescriptionForPurapDocumentsDao().getVendorCreditMemoDocumentStatuses();
        
      //get the vendor credit memo details where CRDT_MEMO_STAT_CD is not null....        
        Map<String, String> vcmDetails = getPurapDocumentsStatusCodeMigrationDao().getVendorCreditMemoDocumentDetails();

        writeNoRecordsMessage(vcmDetails, "Vendor Credit Memo Documents.");
        
        for (String vcmDocNumber : vcmDetails.keySet()) {
            String statusCode = vcmDetails.get(vcmDocNumber);
            
            String newApplicationDocumentStatus = vendorCreditMemoStatusMap.get(statusCode);
            
            migratePurapStatCodeReportService.writeFormattedMessageLine("\t\tVendor Credit Memo Doc: " + vcmDocNumber + " Status Code: " + statusCode + " Status Description: " + newApplicationDocumentStatus);
            
            //find the workflow document and update the app_doc_stat and app_doc_stat_mdfn_dt columns.
            getPurapDocumentsStatusCodeMigrationDao().updateAndSaveMigratedApplicationDocumentStatuses(vcmDocNumber, newApplicationDocumentStatus, getDateTimeService().getCurrentTimestamp());

            //now reindex the requistion for document search...
            documentAttributeIndexingQueue.indexDocument(vcmDocNumber);
        }
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("\n********** Migration of Vendor Credit Memo StatusCode to Workflow document completed **********\n\n");
        
        LOG.debug("processVendorCreditMemoDocumentsForStatusCodeMigration() completed");
        
        return success;
    }
    
    /**
     * Processes the Line Item Receiving for status code migration.  Creates a list of documents numbers
     * and current status code where status code needs migration and using that list the workflow documents
     * will be retrieved.  Each workflow document then will be updated with the corresponding
     * application document status that is new in KFS 5.0
     * 
     * @return true if the Line Item Receiving status code successfully migrates else return false.
     */
    protected boolean processLineItemReceivingDocumentsForStatusCodeMigration() {
        LOG.debug("processLineItemReceivingDocumentsForStatusCodeMigration() started");
        
        boolean success = true;

        migratePurapStatCodeReportService.writeFormattedMessageLine("********** Migration of Line Item Receiving StatusCode to Workflow document started **********\n");
        
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
 
        //get the status cde/descriptions from the table PUR_RCVNG_LN_STAT_T
        Map<String, String> lineItemReceivingStatusMap = getStatusCodeAndDescriptionForPurapDocumentsDao().getLineItemReceivingDocumentStatuses();
        
        //get the line item receiving documents details where RCVNG_LN_STAT_CD is not null....        
        Map<String, String> lineItemRecvDetails = getPurapDocumentsStatusCodeMigrationDao().getLineItemReceivingDocumentDetails();

        writeNoRecordsMessage(lineItemRecvDetails, "Line Item Receiving Documents.");
        
        for (String lineItemRecvDocNumber : lineItemRecvDetails.keySet()) {
            String statusCode = lineItemRecvDetails.get(lineItemRecvDocNumber);
            
            String newApplicationDocumentStatus = lineItemReceivingStatusMap.get(statusCode);
            
            migratePurapStatCodeReportService.writeFormattedMessageLine("\t\tLine Item Receiving Doc: " + lineItemRecvDocNumber + " Status Code: " + statusCode + " Status Description: " + newApplicationDocumentStatus);
            
            //find the workflow document and update the app_doc_stat and app_doc_stat_mdfn_dt columns.
            getPurapDocumentsStatusCodeMigrationDao().updateAndSaveMigratedApplicationDocumentStatuses(lineItemRecvDocNumber, newApplicationDocumentStatus, getDateTimeService().getCurrentTimestamp());

            //now reindex the requistion for document search...
            documentAttributeIndexingQueue.indexDocument(lineItemRecvDocNumber);
        }
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("\n********** Migration of Line Item Receiving StatusCode to Workflow document completed **********\n\n");
        
        LOG.debug("processVendorCreditMemoDocumentsForStatusCodeMigration() completed");
        
        return success;
    }
    
    
    /**
     * Gets the migratePurapStatCodeReportService attribute. 
     * @return Returns the migratePurapStatCodeReportService.
     */
    protected ReportWriterService getMigratePurapStatCodeReportService() {
        return migratePurapStatCodeReportService;
    }
    
    /**
     * Sets the migratePurapStatCodeReportService attribute value.
     * @param migratePurapStatCodeReportService The autoDisapproveErrorReportWriterService to set.
     */
    public void setMigratePurapStatCodeReportService(ReportWriterService migratePurapStatCodeReportService) {
        this.migratePurapStatCodeReportService = migratePurapStatCodeReportService;
    }
    
    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService
     */
    
    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService =  SpringContext.getBean(BusinessObjectService.class);
        }
        
        return businessObjectService;
    }

    /** 
     * Sets the businessObjectService attribute.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * Gets the statusCodeAndDescriptionForPurapDocumentsDao attribute.
     * 
     * @return Returns the statusCodeAndDescriptionForPurapDocumentsDao
     */
    
    public StatusCodeAndDescriptionForPurapDocumentsDao getStatusCodeAndDescriptionForPurapDocumentsDao() {
        if (statusCodeAndDescriptionForPurapDocumentsDao == null) {
            statusCodeAndDescriptionForPurapDocumentsDao = SpringContext.getBean(StatusCodeAndDescriptionForPurapDocumentsDao.class);
        }
        
        return statusCodeAndDescriptionForPurapDocumentsDao;
    }

    /** 
     * Sets the statusCodeAndDescriptionForPurapDocumentsDao attribute.
     * 
     * @param statusCodeAndDescriptionForPurapDocumentsDao The statusCodeAndDescriptionForPurapDocumentsDao to set.
     */
    public void setStatusCodeAndDescriptionForPurapDocumentsDao(StatusCodeAndDescriptionForPurapDocumentsDao statusCodeAndDescriptionForPurapDocumentsDao) {
        this.statusCodeAndDescriptionForPurapDocumentsDao = statusCodeAndDescriptionForPurapDocumentsDao;
    }
    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService
     */
    
    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        
        return dateTimeService;
    }

    /** 
     * Sets the dateTimeService attribute.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    /**
     * Gets the purapDocumentsStatusCodeMigrationDao attribute.
     * 
     * @return Returns the purapDocumentsStatusCodeMigrationDao
     */
    
    public PurapDocumentsStatusCodeMigrationDao getPurapDocumentsStatusCodeMigrationDao() {
        if (purapDocumentsStatusCodeMigrationDao == null) {
            purapDocumentsStatusCodeMigrationDao = SpringContext.getBean(PurapDocumentsStatusCodeMigrationDao.class);
        }
        
        return purapDocumentsStatusCodeMigrationDao;
    }

    /** 
     * Sets the purapDocumentsStatusCodeMigrationDao attribute.
     * 
     * @param purapDocumentsStatusCodeMigrationDao The purapDocumentsStatusCodeMigrationDao to set.
     */
    public void setPurapDocumentsStatusCodeMigrationDao(PurapDocumentsStatusCodeMigrationDao purapDocumentsStatusCodeMigrationDao) {
        this.purapDocumentsStatusCodeMigrationDao = purapDocumentsStatusCodeMigrationDao;
    }

    
}
