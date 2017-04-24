package edu.arizona.kfs.rq.batch.service;

import edu.arizona.kfs.rq.batch.helper.DocumentRequeueReportWriter;

/**
 * UAF-475: MOD-WKFLW-03 Requeue Stuck Documents Job
 *
 * @author Josh Shaloo <shaloo@email.arizona.edu>
 */
public interface RequeueStuckDocumentsService {
    boolean runDocumentRequeueJob(DocumentRequeueReportWriter reportWriter);
}
