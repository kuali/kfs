/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.document.validation.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.MassImportDocument;

/**
 * Event Base class for Purchasing Accounts Payable Item contains the base methods for item events
 */
public class AttributedMassImportLineEventBase extends AttributedDocumentEventBase {
    private static final Logger LOG = Logger.getLogger(AttributedMassImportLineEventBase.class);

    private final List<MassImportLineBase> importedLines;

    /**
     * Copies the line and calls the super constructor
     *
     * @param description the description of the event
     * @param errorPathPrefix the error path
     * @param document the document the event is being called on
     * @param item the item that is having the event called on
     */
    public AttributedMassImportLineEventBase(String description, String errorPathPrefix, MassImportDocument document, List<MassImportLineBase> importedLines) {
        super(description, errorPathPrefix, document);

        this.importedLines = importedLines;
        logEvent();
    }


    /**
     * Gets the importedLines attribute.
     *
     * @return Returns the importedLines.
     */
    public List<MassImportLineBase> getImportedLines() {
        return importedLines;
    }


    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#validate()
     */
    @Override
    public void validate() {
        super.validate();
        if (getImportedLines() == null || getImportedLines().isEmpty()) {
            throw new IllegalArgumentException("invalid (null) item");
        }
    }

    /**
     * Logs the event type and some information about the associated item
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (importedLines == null || importedLines.isEmpty()) {
            logMessage.append("null mass imported line");
        }

        LOG.debug(logMessage);
    }
}
