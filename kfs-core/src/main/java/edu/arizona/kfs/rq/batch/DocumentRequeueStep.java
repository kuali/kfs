package edu.arizona.kfs.rq.batch;

import edu.arizona.kfs.rq.batch.helper.DocumentRequeueReportWriter;
import edu.arizona.kfs.rq.batch.helper.impl.DocumentRequeueReportWriterTextImpl;
import edu.arizona.kfs.rq.batch.service.RequeueStuckDocumentsService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.util.Date;

/**
 * UAF-475: MOD-WKFLW-03 Requeue Stuck Documents Job
 *
 * @author Josh Shaloo <shaloo@email.arizona.edu>
 */
public class DocumentRequeueStep extends AbstractStep {

    private RequeueStuckDocumentsService requeueStuckDocumentsService;
    private DateTimeService dateTimeService;
    private String reportDirectoryName;

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        DocumentRequeueReportWriter reportWriter = getNewReportWriter();
        return getRequeueStuckDocumentsService().runDocumentRequeueJob(reportWriter);
    }

    protected DocumentRequeueReportWriter getNewReportWriter() {
        return new DocumentRequeueReportWriterTextImpl( getReportDirectoryName(), getDateTimeService() );
    }

    protected RequeueStuckDocumentsService getRequeueStuckDocumentsService() {
        return this.requeueStuckDocumentsService;
    }

    public void setRequeueStuckDocumentsService(RequeueStuckDocumentsService requeueStuckDocumentsService) {
        this.requeueStuckDocumentsService = requeueStuckDocumentsService;
    }

    protected DateTimeService getDateTimeService() {
        return this.dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setReportDirectoryName( String reportDirectoryName ) {
        this.reportDirectoryName = reportDirectoryName;
    }

    protected String getReportDirectoryName() {
        return this.reportDirectoryName;
    }


}