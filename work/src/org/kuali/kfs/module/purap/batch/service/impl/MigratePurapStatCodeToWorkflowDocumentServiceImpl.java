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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.batch.service.MigratePurapStatCodeToWorkflowDocumentService;
import org.kuali.kfs.module.purap.dataaccess.PurapDocumentsStatusCodeMigrationDao;
import org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
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
        
        LOG.debug("migratePurapStatCodeToWorkflowDocuments() completed");
        
        return success;
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
        
        //get the status code/descriptions from the table pur_reqs_stat_t
        Map<String, String> requisitionStatusMap = getStatusCodeAndDescriptionForPurapDocumentsDao().getRequisitionDocumentStatuses();
        
        //get the requisitions where REQS_STAT_CD is not null....
        List<RequisitionDocument> reqDocs = (List<RequisitionDocument>) SpringContext.getBean(PurapDocumentsStatusCodeMigrationDao.class).getRequisitionDocumentsForStatusCodeMigration();
        
        for (RequisitionDocument reqDoc : reqDocs) {
            String newApplicationDocumentStatus = requisitionStatusMap.get(reqDoc.getStatusCode());
            migratePurapStatCodeReportService.writeFormattedMessageLine("\t\tREQ Doc: " + reqDoc.getDocumentNumber() + " Status Code: " + reqDoc.getStatusCode() + " Status Description: " + newApplicationDocumentStatus);
            
            //find the workflow document and update the app_doc_stat and app_doc_stat_mdfn_dt columns.
            getStatusCodeAndDescriptionForPurapDocumentsDao().updateAndSaveMigratedApplicationDocumentStatuses(reqDoc.getDocumentNumber(), newApplicationDocumentStatus, getDateTimeService().getCurrentTimestamp());
        }
        
        migratePurapStatCodeReportService.writeFormattedMessageLine("\n********** Migration of Requistions StatusCode to Workflow document completed **********\n");
        
        //now reindex the requistions for search...
        for (RequisitionDocument reqDoc : reqDocs) {
            final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
            documentAttributeIndexingQueue.indexDocument(reqDoc.getDocumentNumber());
        }
        
        LOG.debug("processRequisitionDocumentsForStatusCodeMigration() completed");
        
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
}
