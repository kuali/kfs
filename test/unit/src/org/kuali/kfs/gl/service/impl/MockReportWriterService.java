/*
 * Copyright 2009 The Kuali Foundation
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
