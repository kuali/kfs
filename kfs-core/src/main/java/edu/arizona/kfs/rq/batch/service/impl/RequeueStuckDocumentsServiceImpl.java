package edu.arizona.kfs.rq.batch.service.impl;

import edu.arizona.kfs.rq.batch.dataaccess.StuckDocumentsDAO;
import edu.arizona.kfs.rq.batch.helper.DocumentRequeueReportWriter;
import edu.arizona.kfs.rq.batch.service.RequeueStuckDocumentsService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentRefreshQueue;

import java.util.List;

/**
 * UAF-475: MOD-WKFLW-03 Requeue Stuck Documents Job
 *
 * @author Josh Shaloo <shaloo@email.arizona.edu>
 */
public class RequeueStuckDocumentsServiceImpl implements RequeueStuckDocumentsService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequeueStuckDocumentsServiceImpl.class);

    private StuckDocumentsDAO stuckDocumentsDAO;
    private DocumentTypeService documentTypeService;

    protected enum RequeueStatus {
        SUCCESS, FAIL
    }

    @Override
    public boolean runDocumentRequeueJob(DocumentRequeueReportWriter requeueReport) {

        List<Document> stuckDocuments = getSuckdDocumentsDAO().getStuckDocuments();

        requeueReport.initializeReport();

        for ( Document stuckDocument : stuckDocuments ) {

            RequeueStatus requeueStatus = requeueStuckDocument(stuckDocument);
            requeueReport.reportRequeueStatus(
                    requeueStatus.toString(),
                    stuckDocument.getDocumentId(),
                    stuckDocument.getDocumentTypeName(),
                    stuckDocument.getDateLastModified().toString(),
                    stuckDocument.getTitle()
            );

        }

        requeueReport.finalizeReport();

        return false;
    }

    protected DocumentTypeService getDocumentTypeService() {
        return this.documentTypeService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    protected StuckDocumentsDAO getSuckdDocumentsDAO() {
        return this.stuckDocumentsDAO;
    }

    public void setStuckDocumentsDAO(StuckDocumentsDAO suckdDocumentsDAO) {
        this.stuckDocumentsDAO = suckdDocumentsDAO;
    }

    protected RequeueStatus requeueStuckDocument(Document stuckDocument) {
        RequeueStatus requeueStatus = RequeueStatus.SUCCESS;

        try {

            DocumentType documentType = getDocumentTypeService().getDocumentTypeByName(stuckDocument.getDocumentTypeName());
            DocumentRefreshQueue documentRequeueService = KewApiServiceLocator.getDocumentRequeuerService(
                    documentType.getApplicationId(),
                    stuckDocument.getDocumentId(),
                    0
            );
            LOG.info("Requeueing stuck document: " + stuckDocument.getDocumentId());
            documentRequeueService.refreshDocument(stuckDocument.getDocumentId());
        }
        catch (Exception ex ) {
            requeueStatus = requeueStatus.FAIL;
            LOG.error("Fialed to requeue stuck document.");
            LOG.error(ex.getMessage(),ex );
        }

        return requeueStatus;

    }
}

