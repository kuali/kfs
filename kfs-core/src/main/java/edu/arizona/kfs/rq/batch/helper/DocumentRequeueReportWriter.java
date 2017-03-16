package edu.arizona.kfs.rq.batch.helper;

/**
 * UAF-475: MOD-WKFLW-03 Requeue Stuck Documents Job
 *
 * @author Josh Shaloo <shaloo@email.arizona.edu>
 */
public interface DocumentRequeueReportWriter {
    void initializeReport();
    void reportRequeueStatus( String requeueStatus, String documentNumber, String documentType, String lastModified, String lastAction );
    void finalizeReport();
}
