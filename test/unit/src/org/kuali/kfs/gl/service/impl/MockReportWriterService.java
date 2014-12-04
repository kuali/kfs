/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.service.impl;

import java.util.List;

import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * A class which pretends to be a good report writer service, but really does nothing
 */
public class MockReportWriterService implements ReportWriterService, WrappingBatchService, DocumentNumberAwareReportWriterService {

    public void pageBreak() {
        // nothing to do
    }

    public void writeError(BusinessObject businessObject, Message message) {
        // nothing
    }

    public void writeError(BusinessObject businessObject, List<Message> messages) {
        // nothing
    }

    public void writeFormattedMessageLine(String format, Object... args) {
        // nothing to do
    }

    public void writeMultipleFormattedMessageLines(String format, Object... args) {
        // nothing
    }

    public void writeNewLines(int lines) {
        // nothing to do
    }

    public void writeStatisticLine(String message, Object... args) {
        // nothing to do
    }

    public void writeSubTitle(String message) {
        // nothing to do
    }

    public void writeTable(List<? extends BusinessObject> businessObjects, boolean isHeaderRepeatedInNewPage, boolean isRowBreakAcrossPageAllowed) {
        // nothing to do
    }

    public void writeTableHeader(BusinessObject businessObject) {
        // nothing to do
    }

    public void writeTableRow(BusinessObject businessObject) {
        // nothing to do
    }

    public void writeTableRowSeparationLine(BusinessObject businessObject) {
        // nothing to do
    }

    public void writeTableRowWithColspan(BusinessObject businessObject) {
        // nothing to do
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.WrappingBatchService#destroy()
     */
    public void destroy() {
        // don't do nothing
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.WrappingBatchService#initialize()
     */
    public void initialize() {
        // nothing to do
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeTableHeader(java.lang.Class)
     */
    public void writeTableHeader(Class<? extends BusinessObject> businessObjectClass) {
        // ain't nothing to do, so we ain't doing no thing
    }

    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeParameterLine(java.lang.String, java.lang.Object[])
     */
    public void writeParameterLine(String message, Object... args) {
        // not doing anything
    }

    /**
     * @see org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService#setDocumentNumber(java.lang.String)
     */
    public void setDocumentNumber(String documentNumber) {
        // that's a nice document number
    }
}
