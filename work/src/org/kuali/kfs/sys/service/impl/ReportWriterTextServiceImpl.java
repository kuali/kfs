/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Text output implementation of <code>ReportWriterService</code> interface. If you are a developer attempting to add a new business
 * object for error report writing, take a look at the Spring definitions for BusinessObjectReportHelper.<br>
 * This class CANNOT be used by 2 processes simultaneously. It is for very specific batch processes that should not run at the same
 * time, and initialize and destroy must be called and the beginning and end of each process that uses it.
 * 
 * @see org.kuali.kfs.sys.report.BusinessObjectReportHelper
 */
public class ReportWriterTextServiceImpl implements ReportWriterService, WrappingBatchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportWriterTextServiceImpl.class);

    // Changing the initial line number would only affect that a page break occurs early. It does not actually print in the
    // middle of the page. Hence changing this has little use.
    protected static final int INITIAL_LINE_NUMBER = 0;

    protected String filePath;
    protected String fileNamePrefix;
    protected String fileNameSuffix;
    protected String title;
    protected int pageWidth;
    protected int pageLength;
    protected int initialPageNumber;
    protected String errorSubTitle;
    protected String statisticsLabel;
    protected String statisticsLeftPadding;
    protected String pageLabel;
    protected DateTimeService dateTimeService;

    /**
     * A map of BO classes to {@link BusinessObjectReportHelper} bean names, to configure which BO's will be rendered by which
     * BusinessObjectReportHelper. This property should be configured via the spring bean definition
     */
    protected Map<Class<? extends BusinessObject>, String> classToBusinessObjectReportHelperBeanNames;

    // Local caching field to speed up the selection of formatting BusinessObjectReportHelper to use per configuration in Spring
    protected Map<Class<? extends BusinessObject>, BusinessObjectReportHelper> businessObjectReportHelpers;

    protected PrintStream printStream;
    protected int page;
    protected int line = INITIAL_LINE_NUMBER;
    protected String errorFormat;

    // Ensures that the statistics header isn't written multiple times. Does not check that a user doesn't write other stuff into
    // the statistics
    // section. A developer is responsible for ensuring that themselves
    protected boolean modeStatistics = false;

    // So that writeError knows when to writeErrorHeader
    protected boolean newPage = true;

    // For printing new headers when the BO is changed
    protected Class<? extends BusinessObject> businessObjectClass;

    /**
     * @see org.kuali.kfs.sys.batch.service.WrappingBatchService#initialize()
     */
    public void initialize() {
        try {
            printStream = new PrintStream(generateFullFilePath());
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        page = initialPageNumber;

        initializeBusinessObjectReportHelpers();
        // Initial header
        this.writeHeader(title);
    }

    protected void initializeBusinessObjectReportHelpers() {
        businessObjectReportHelpers = new HashMap<Class<? extends BusinessObject>, BusinessObjectReportHelper>();
        if (classToBusinessObjectReportHelperBeanNames != null) {
            for (Class<? extends BusinessObject> clazz : classToBusinessObjectReportHelperBeanNames.keySet()) {
                String businessObjectReportHelperBeanName = classToBusinessObjectReportHelperBeanNames.get(clazz);
                BusinessObjectReportHelper reportHelper = (BusinessObjectReportHelper) SpringContext.getService(businessObjectReportHelperBeanName);
                if (ObjectUtils.isNull(reportHelper)) {
                    LOG.error("Cannot find BusinessObjectReportHelper implementation for class: " + clazz.getName() + " bean name: " + businessObjectReportHelperBeanName);
                    throw new RuntimeException("Cannot find BusinessObjectReportHelper implementation for class: " + clazz.getName() + " bean name: " + businessObjectReportHelperBeanName);
                }
                businessObjectReportHelpers.put(clazz, reportHelper);
            }
        }
    }

    protected String generateFullFilePath() {
        return filePath + File.separator + this.fileNamePrefix + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + fileNameSuffix;
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.WrappingBatchService#destroy()
     */
    public void destroy() {
        printStream.close();

        // reset variables that track state
        page = initialPageNumber;
        line = INITIAL_LINE_NUMBER;
        modeStatistics = false;
        newPage = true;
        businessObjectClass = null;
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeSubTitle(java.lang.String)
     */
    public void writeSubTitle(String message) {
        if (message.length() > pageWidth) {
            LOG.warn("sub title to be written exceeds pageWidth. Printing anyway.");
            this.writeFormattedMessageLine(message);
        }
        else {
            int padding = (pageWidth - message.length()) / 2;
            this.writeFormattedMessageLine("%" + (padding + message.length()) + "s", message);
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeError(java.lang.Class, org.kuali.kfs.sys.Message)
     */
    public void writeError(BusinessObject businessObject, Message message) {
        // Check if we need to write a new table header. We do this if it hasn't been written before or if the businessObject
        // changed
        if (newPage || businessObjectClass == null || !businessObjectClass.getName().equals(businessObject.getClass().getName())) {
            if (businessObjectClass == null) {
                // If we didn't write the header before, write it with a subTitle
                this.writeSubTitle(errorSubTitle);
            }
            else if (!businessObjectClass.getName().equals(businessObject.getClass().getName())) {
                // If it changed push a newline in for neater formatting
                this.writeNewLines(1);
            }

            this.writeErrorHeader(businessObject);
            newPage = false;
            businessObjectClass = businessObject.getClass();
        }

        // Get business object formatter that will be used
        BusinessObjectReportHelper businessObjectReportHelper = getBusinessObjectReportHelper(businessObject);

        // Print the values of the businessObject per formatting determined by writeErrorHeader
        List<Object> formatterArgs = new ArrayList<Object>();
        formatterArgs.addAll(businessObjectReportHelper.getValues(businessObject));
        formatterArgs.add(message.getMessage());
        this.writeFormattedMessageLine(errorFormat, formatterArgs.toArray());
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeError(java.lang.Class, java.util.List)
     */
    public void writeError(BusinessObject businessObject, List<Message> messages) {
        int i = 0;
        for (Iterator<Message> messagesIter = messages.iterator(); messagesIter.hasNext();) {
            Message message = messagesIter.next();

            if (i == 0) {
                // First one has its values written
                this.writeError(businessObject, message);
            }
            else {
                // Any consecutive one only has message written, hence use padding

                // Get business object formatter that will be used
                BusinessObjectReportHelper businessObjectReportHelper = getBusinessObjectReportHelper(businessObject);

                // Print the values of the businessObject per formatting determined by writeErrorHeader
                List<Object> formatterArgs = new ArrayList<Object>();
                formatterArgs.addAll(businessObjectReportHelper.getBlankValues(businessObject));
                formatterArgs.add(message.getMessage());
                this.writeFormattedMessageLine(errorFormat, formatterArgs.toArray());
            }

            i++;
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeNewLines(int)
     */
    public void writeNewLines(int lines) {
        for (int i = 0; i < lines; i++) {
            this.writeFormattedMessageLine("");
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeStatisticLine(java.lang.String, java.lang.Object[])
     */
    public void writeStatisticLine(String message, Object... args) {
        // Statistics header is only written if it hasn't been written before
        if (!modeStatistics) {
            this.modeStatistics = true;

            // If nothing has been written to the report we don't want to page break
            if (!(page == initialPageNumber && line == INITIAL_LINE_NUMBER)) {
                this.pageBreak();
            }

            this.writeFormattedMessageLine("*********************************************************************************************************************************");
            this.writeFormattedMessageLine("*********************************************************************************************************************************");
            this.writeFormattedMessageLine("*******************" + statisticsLabel + "*******************");
            this.writeFormattedMessageLine("*********************************************************************************************************************************");
            this.writeFormattedMessageLine("*********************************************************************************************************************************");
        }

        this.writeFormattedMessageLine(statisticsLeftPadding + message, args);
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeFormattedMessageLine(java.lang.String)
     */
    public void writeFormattedMessageLine(String format) {
        this.writeFormattedMessageLine(format, new Object());
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeFormattedMessageLine(java.lang.String, java.lang.Object[])
     */
    public void writeFormattedMessageLine(String format, Object... args) {
        String message = String.format(format + "\n", args);

        // Log we are writing out of bounds. Would be nice to show message here but not so sure if it's wise to dump that data into
        // logs
        if (message.length() > pageWidth) {
            LOG.warn("message is out of bounds writing anyway");
        }

        printStream.printf(message);

        line++;
        if (line >= pageLength) {
            this.pageBreak();
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#pageBreak()
     */
    public void pageBreak() {
        // Intentionally not using writeFormattedMessageLine here since it would loop trying to page break ;)
        printStream.printf("%c\n", 12);
        page++;
        line = INITIAL_LINE_NUMBER;
        newPage = true;

        this.writeHeader(title);
    }

    /**
     * Helper method to write a header for placement at top of new page
     * 
     * @param title that should be printed on this header
     */
    protected void writeHeader(String title) {
        String headerText = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", dateTimeService.getCurrentDate());
        int reportTitlePadding = pageWidth / 2 - headerText.length() - title.length() / 2;
        headerText = String.format("%s%" + (reportTitlePadding + title.length()) + "s%" + reportTitlePadding + "s", headerText, title, "");

        this.writeFormattedMessageLine("%s%s%,9d", headerText, pageLabel, page);
        this.writeNewLines(1);
    }

    /**
     * Helper method to write the error header
     * 
     * @param businessObject to print header for
     */
    protected void writeErrorHeader(BusinessObject businessObject) {
        BusinessObjectReportHelper businessObjectReportHelper = getBusinessObjectReportHelper(businessObject);
        List<String> errorHeader = businessObjectReportHelper.getTableHeader(pageWidth);

        // If we are at end of page and don't have space for table header, go ahead and page break
        if (errorHeader.size() + line >= pageLength) {
            this.pageBreak();
        }

        // Print the header one by one. Note the last element is the formatter. So capture that seperately
        for (Iterator<String> headers = errorHeader.iterator(); headers.hasNext();) {
            String header = headers.next();

            if (headers.hasNext()) {
                this.writeFormattedMessageLine("%s", header);
            }
            else {
                errorFormat = header;
            }
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeTableHeader(org.kuali.rice.kns.bo.BusinessObject)
     */
    public void writeTableHeader(BusinessObject businessObject) {
        BusinessObjectReportHelper businessObjectReportHelper = getBusinessObjectReportHelper(businessObject);

        Map<String, String> tableDefinition = businessObjectReportHelper.getTableDefintion();
        String tableHeaderFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY);

        String[] headerLines = this.getMultipleFormattedMessageLines(tableHeaderFormat, new Object());
        this.writeMultipleFormattedMessageLines(headerLines);
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeTableRow(org.kuali.rice.kns.bo.BusinessObject)
     */
    public void writeTableRow(BusinessObject businessObject) {
        if (newPage) {
            writeTableHeader(businessObject);
            newPage = false;
        }
        BusinessObjectReportHelper businessObjectReportHelper = getBusinessObjectReportHelper(businessObject);
        Map<String, String> tableDefinition = businessObjectReportHelper.getTableDefintion();

        String tableCellFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY);
        List<String> tableCellValues = businessObjectReportHelper.getTableCellValuesPaddingWithEmptyCell(businessObject, false);

        String[] rowMessageLines = this.getMultipleFormattedMessageLines(tableCellFormat, tableCellValues.toArray());
        this.writeMultipleFormattedMessageLines(rowMessageLines);
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeTableRowWithColspan(org.kuali.rice.kns.bo.BusinessObject)
     */
    public void writeTableRowWithColspan(BusinessObject businessObject) {
        BusinessObjectReportHelper businessObjectReportHelper = getBusinessObjectReportHelper(businessObject);
        Map<String, String> tableDefinition = businessObjectReportHelper.getTableDefintion();

        String tableCellFormat = businessObjectReportHelper.getTableCellFormat(true);
        List<String> tableCellValues = businessObjectReportHelper.getTableCellValuesPaddingWithEmptyCell(businessObject, true);

        String[] rowMessageLines = this.getMultipleFormattedMessageLines(tableCellFormat, tableCellValues.toArray());
        this.writeMultipleFormattedMessageLines(rowMessageLines);
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeTable(java.util.List, boolean, boolean)
     */
    public void writeTable(List<? extends BusinessObject> businessObjects, boolean isHeaderRepeatedInNewPage, boolean isRowBreakAcrossPageAllowed) {
        if (ObjectUtils.isNull(businessObjects) || businessObjects.isEmpty()) {
            return;
        }

        BusinessObject firstBusinessObject = businessObjects.get(0);
        this.writeTableHeader(firstBusinessObject);

        BusinessObjectReportHelper businessObjectReportHelper = getBusinessObjectReportHelper(businessObjects.get(0));
        Map<String, String> tableDefinition = businessObjectReportHelper.getTableDefintion();
        String tableHeaderFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY);
        String[] headerLines = this.getMultipleFormattedMessageLines(tableHeaderFormat, new Object());

        String tableCellFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY);

        for (BusinessObject businessObject : businessObjects) {

            List<String> tableCellValues = businessObjectReportHelper.getTableCellValuesPaddingWithEmptyCell(businessObject, false);
            String[] messageLines = this.getMultipleFormattedMessageLines(tableCellFormat, tableCellValues.toArray());

            boolean hasEnoughLinesInPage = messageLines.length <= (this.pageLength - this.line);
            if (!hasEnoughLinesInPage && !isRowBreakAcrossPageAllowed) {
                this.pageBreak();

                if (isHeaderRepeatedInNewPage) {
                    this.writeTableHeader(firstBusinessObject);
                }
            }

            this.writeMultipleFormattedMessageLines(messageLines, headerLines, isRowBreakAcrossPageAllowed);
        }

    }

    /**
     * get the business report helper for the given business object
     * 
     * @param businessObject the given business object
     * @return the business report helper for the given business object
     */
    public BusinessObjectReportHelper getBusinessObjectReportHelper(BusinessObject businessObject) {
        BusinessObjectReportHelper businessObjectReportHelper = this.businessObjectReportHelpers.get(businessObject.getClass());
        if (ObjectUtils.isNull(businessObjectReportHelper)) {
            throw new RuntimeException(businessObject.getClass().toString() + " is not handled");
        }

        return businessObjectReportHelper;
    }

    /**
     * write the given information as multiple lines if it contains more than one line breaks
     * 
     * @param format the given text format definition
     * @param messageLines the given information being written out
     * @param headerLinesInNewPage the given header lines being written in the begin of a new page
     */
    private void writeMultipleFormattedMessageLines(String[] messageLines, String[] headerLinesInNewPage, boolean isRowBreakAcrossPageAllowed) {
        int currentPageNumber = this.page;

        for (String line : messageLines) {
            boolean hasEnoughLinesInPage = messageLines.length <= (this.pageLength - this.line);
            if (!hasEnoughLinesInPage && !isRowBreakAcrossPageAllowed) {
                this.pageBreak();
            }

            if (currentPageNumber < this.page && ObjectUtils.isNotNull(headerLinesInNewPage)) {
                currentPageNumber = this.page;

                for (String headerLine : headerLinesInNewPage) {
                    this.writeFormattedMessageLine(headerLine);
                }
            }

            this.writeFormattedMessageLine(line);
        }
    }

    /**
     * write the given information as multiple lines if it contains more than one line breaks
     * 
     * @param format the given text format definition
     * @param args the given information being written out
     */
    public void writeMultipleFormattedMessageLines(String[] messageLines) {
        this.writeMultipleFormattedMessageLines(messageLines, null, false);
    }

    /**
     * This method...
     * 
     * @param format
     * @param args
     * @return
     */
    public String[] getMultipleFormattedMessageLines(String format, Object... args) {
        String message = String.format(format, args);
        return StringUtils.split(message, "\n");
    }

    /**
     * Sets the filePath
     * 
     * @param filePath The filePath to set.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Sets the fileNamePrefix
     * 
     * @param fileNamePrefix The fileNamePrefix to set.
     */
    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    /**
     * Sets the fileNameSuffix
     * 
     * @param fileNameSuffix The fileNameSuffix to set.
     */
    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
    }

    /**
     * Sets the title
     * 
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the pageWidth
     * 
     * @param pageWidth The pageWidth to set.
     */
    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    /**
     * Sets the pageLength
     * 
     * @param pageLength The pageLength to set.
     */
    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
    }

    /**
     * Sets the initialPageNumber
     * 
     * @param initialPageNumber The initialPageNumber to set.
     */
    public void setInitialPageNumber(int initialPageNumber) {
        this.initialPageNumber = initialPageNumber;
    }

    /**
     * Sets the errorSubTitle
     * 
     * @param errorSubTitle The errorSubTitle to set.
     */
    public void setErrorSubTitle(String errorSubTitle) {
        this.errorSubTitle = errorSubTitle;
    }

    /**
     * Sets the statisticsLabel
     * 
     * @param statisticsLabel The statisticsLabel to set.
     */
    public void setStatisticsLabel(String statisticsLabel) {
        this.statisticsLabel = statisticsLabel;
    }

    /**
     * Sets the statisticsLeftPadding
     * 
     * @param statisticsLeftPadding The statisticsLeftPadding to set.
     */
    public void setStatisticsLeftPadding(String statisticsLeftPadding) {
        this.statisticsLeftPadding = statisticsLeftPadding;
    }

    /**
     * Sets the pageLabel
     * 
     * @param pageLabel The pageLabel to set.
     */
    public void setPageLabel(String pageLabel) {
        this.pageLabel = pageLabel;
    }

    /**
     * Sets the DateTimeService
     * 
     * @param dateTimeService The DateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets a map of BO classes to {@link BusinessObjectReportHelper} bean names, to configure which BO's will be rendered by which
     * BusinessObjectReportHelper. This property should be configured via the spring bean definition
     * 
     * @param classToBusinessObjectReportHelperBeanNames The classToBusinessObjectReportHelperBeanNames to set.
     */
    public void setClassToBusinessObjectReportHelperBeanNames(Map<Class<? extends BusinessObject>, String> classToBusinessObjectReportHelperBeanNames) {
        this.classToBusinessObjectReportHelperBeanNames = classToBusinessObjectReportHelperBeanNames;
    }
}
