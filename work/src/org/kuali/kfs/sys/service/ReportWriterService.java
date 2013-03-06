/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import java.util.List;

import org.kuali.kfs.sys.Message;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Service interface defines methods that are relevant to writing a report
 */
public interface ReportWriterService {
    /**
     * Writes a centered message. The purpose of this is primarily to write custom subtitles necessary in some reports
     * @param message to be written centered
     */
    public void writeSubTitle(String message);

    /**
     * Writes an error message for the passed in business object. If the business object is the first or different then the
     * previous one a table header is printed per definition in the implementation of this service
     * @param businessObject controlling the table header and values to be printed
     * @param message associated with the businessObject
     */
    public void writeError(BusinessObject businessObject, Message message);

    /**
     * Same as writeError except that it provides for multiple messages for the BO. BO values are only printed once and then messages each
     * row below that.
     * @param businessObject controlling the table header and values to be printed
     * @param messages associated with the businessObject
     */
    public void writeError(BusinessObject businessObject, List<Message> messages);

    /**
     * Writes statistics usually placed at the end of the report. If this is the first time this method is called then a statistics header
     * is written. All messages are indented per STATISTICS_LEFT_PADDING. If multiple lines are needed, call this method multiple times to
     * assure pagination works properly
     * @param message to write
     * @param args for the message per standard String.format
     */
    public void writeStatisticLine(String message, Object ... args);

    /**
     * Writes parameter usually placed at the end of the report. If this is the first time this method is called then a parameter header
     * is written. All messages are indented per PARAMETERS_LEFT_PADDING. If multiple lines are needed, call this method multiple times to
     * assure pagination works properly
     * @param message to write
     * @param args for the message per standard String.format
     */
    public abstract void writeParameterLine(String message, Object ... args);

    /**
     * Writes "lines" number of newlines to the report
     * @param lines number of newlines to write to the report
     */
    public void writeNewLines(int lines);

    /**
     * Pass through to PrintStream.printf except that it also handles pagination. If multiple lines are needed, call this method multiple
     * times to assure pagination works properly
     * @param format
     * @param args
     */
    public void writeFormattedMessageLine(String format, Object ... args);

    public void writeMultipleFormattedMessageLines(String format, Object... args);

    /**
     * Write table header into a report for the given business object
     * @param businessObject the given business object
     */
    public void writeTableHeader(BusinessObject businessObject);

    /**
     * Write table header into a report for business objects of the given class
     * @param businessObjectClass the given class of a business object
     */
    public abstract void writeTableHeader(Class<? extends BusinessObject> businessObjectClass);

    /**
     * Write table row into a report for the given business object
     * @param businessObject the given business object
     */
    public void writeTableRow(BusinessObject businessObject);

    /**
     * Write table into a report for the given list of business objects
     * @param businessObjects the given business objects
     * @param isHeaderRepeatedInNewPage instruct if the header row needs to be repeated in a new page
     * @param isRowBreakAcrossPageAllowed determine whether a row can be broken across pages
     */
    public void writeTable(List<? extends BusinessObject> businessObjects, boolean isHeaderRepeatedInNewPage, boolean isRowBreakAcrossPageAllowed);

    /**
     * Breaking the page and write a new header
     */
    public void pageBreak();

    /**
     * Write table row into a report for the given business object and also take the colspan in account
     * @param businessObject the given business object
     */
    public void writeTableRowWithColspan(BusinessObject businessObject);

    /**
     * write a separation line in a table
     * @param businessObject the given business object
     */
    public void writeTableRowSeparationLine(BusinessObject businessObject);
}
