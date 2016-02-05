package edu.arizona.kfs.rq.batch.helper.impl;

import edu.arizona.kfs.rq.batch.helper.DocumentRequeueReportWriter;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

/**
 * UAF-475: MOD-WKFLW-03 Requeue Stuck Documents Job
 *
 * @author Josh Shaloo <shaloo@email.arizona.edu>
 */
public class DocumentRequeueReportWriterTextImpl implements DocumentRequeueReportWriter {

    protected String reportDirectoryName;
    protected String reportDate;
    protected String outputFileName;

    protected final String REPORT_FILENAME = "RequeuedDocuments";
    protected final String REPORT_FILE_SUFFIX = ".txt";

    protected final String REPORT_STATUS_HEADER_TEXT = "Status";
    protected final String REPORT_DOCNUM_HEADER_TEXT = "Doc Num";
    protected final String REPORT_DOCTYPE_HEADER_TEXT = "Doc Type";
    protected final String REPORT_MODIFIED_HEADER_TEXT = "Last Modified";
    protected final String REPORT_DOCUMENT_TITTLE_HEADER_TEXT = "Document Title";
    protected final String REPORT_BLANK_SPACE_TEXT = " ";

    protected final Integer REPORT_STATUS_COLUMN_LENGTH = 9;
    protected final Integer REPORT_DOCNUM_COLUMN_LENGTH = 9;
    protected final Integer REPORT_DOCTYPE_COLUMN_LENGTH = 40;
    protected final Integer REPORT_LAST_MODIFIED_COLUMN_LENGTH = 30;
    protected final Integer REPORT_DOCUMENT_TITLE_COLUMN_LENGTH = 60;
    protected final Integer REPORT_BUFFER_LENGTH = 200;

    private StringBuffer reportLineBuffer;
    private File reportFile;
    private PrintStream outputStream;
    private DateTimeService dateTimeService;

    public DocumentRequeueReportWriterTextImpl ( String reportDirectoryName, DateTimeService dateTimeService ) {
        setReportDirectoryName(reportDirectoryName);
        setDateTimeService(dateTimeService);
    }

    public void initializeReport() {

        File reportFile = getReportFile();

        try {
            reportFile.createNewFile();
            this.outputStream = new PrintStream(reportFile);
        } catch (IOException ex) {
            finalizeReport();
            throw new RuntimeException("Failed to open report for writing.", ex);
        }

        printReportHeader();
    }



    public void reportRequeueStatus( String requeueStatus, String documentNumber, String documentType, String lastModified, String lastAction ) {
        printRow(
                requeueStatus,
                documentNumber,
                documentType,
                lastModified,
                lastAction
        );
    }

    public void finalizeReport() {
        PrintStream outputStream = getOutputStream();

        if (ObjectUtils.isNotNull(outputStream)) {
            outputStream.close();
        }
    }



    protected void printReportHeader() {
        printRow(
                REPORT_STATUS_HEADER_TEXT,
                REPORT_DOCNUM_HEADER_TEXT,
                REPORT_DOCTYPE_HEADER_TEXT,
                REPORT_MODIFIED_HEADER_TEXT,
                REPORT_DOCUMENT_TITTLE_HEADER_TEXT
        );
    }

    protected void printRow( String column1, String column2, String column3,  String column4, String column5 ) {

        PrintStream outputStream = getOutputStream();
        StringBuffer reportLineBuffer = getReportLineBuffer();

        reportLineBuffer.setLength(0);

        reportLineBuffer.append(StringUtils.rightPad(
                column1,
                REPORT_STATUS_COLUMN_LENGTH,
                REPORT_BLANK_SPACE_TEXT));

        reportLineBuffer.append(StringUtils.rightPad(
                column2,
                REPORT_DOCNUM_COLUMN_LENGTH,
                REPORT_BLANK_SPACE_TEXT));

        reportLineBuffer.append(StringUtils.rightPad(
                column3,
                REPORT_DOCTYPE_COLUMN_LENGTH,
                REPORT_BLANK_SPACE_TEXT));

        reportLineBuffer.append(StringUtils.rightPad(
                column4,
                REPORT_LAST_MODIFIED_COLUMN_LENGTH,
                REPORT_BLANK_SPACE_TEXT));

        reportLineBuffer.append(StringUtils.rightPad(
                column5,
                REPORT_DOCUMENT_TITLE_COLUMN_LENGTH,
                REPORT_BLANK_SPACE_TEXT));

        outputStream.println(reportLineBuffer.toString());
    }

    protected String getOutputFileName() {

        if ( ObjectUtils.isNull(this.outputFileName) ) {
            StringBuilder fileNameBuilder = new StringBuilder();

            fileNameBuilder.append(getReportDirectoryName());
            fileNameBuilder.append(File.separator);
            fileNameBuilder.append(REPORT_FILENAME);
            fileNameBuilder.append(getReportDate());
            fileNameBuilder.append(REPORT_FILE_SUFFIX);
            this.outputFileName = fileNameBuilder.toString();
        }
        return this.outputFileName;
    }

    protected String getReportDate() {
        if (ObjectUtils.isNull(this.reportDate)) {
            this.reportDate = getDateTimeService().toDateStringForFilename(new Date());
        }
        return this.reportDate;
    }

    protected String getReportDirectoryName() {
        return this.reportDirectoryName;
    }

    protected StringBuffer getReportLineBuffer() {
        if ( ObjectUtils.isNull( this.reportLineBuffer ) ) {
            this.reportLineBuffer = new StringBuffer(REPORT_BUFFER_LENGTH);
        }
        return this.reportLineBuffer;
    }

    protected File getReportFile() {
        if ( ObjectUtils.isNull(this.reportFile) ) {
            this.reportFile = new File(getOutputFileName());
        }
        return this.reportFile;
    }

    protected PrintStream getOutputStream() {
        return this.outputStream;
    }

    protected DateTimeService getDateTimeService() {
        return this.dateTimeService;
    }

    protected void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected void setReportDirectoryName( String reportDirectoryName ) {
        this.reportDirectoryName = reportDirectoryName;
    }
}
