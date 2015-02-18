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
package org.kuali.kfs.sys.document.validation.event;

import org.kuali.rice.krad.document.Document;

/**
 * An attributed version of the Approve Document event.
 */
public class AttributedApproveDocumentEvent extends AttributedDocumentEventBase {
    /**
     * Constructs an ApproveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param errorPathPrefix
     * @param document
     */
    public AttributedApproveDocumentEvent(String errorPathPrefix, Document document) {
        this("approve", errorPathPrefix, document);
    }

    /**
     * Constructs an ApproveDocumentEvent with the given document
     * 
     * @param document
     */
    public AttributedApproveDocumentEvent(Document document) {
        this("approve", "", document);
    }

    /**
     * Constructs a ApproveDocumentEvent, allowing the eventType to be passed in so that subclasses can specify a more accurate
     * message.
     * 
     * @param eventType
     * @param errorPathPrefix
     * @param document
     */
    protected AttributedApproveDocumentEvent(String eventType, String errorPathPrefix, Document document) {
        super("creating " + eventType + " event for document " + getDocumentId(document), errorPathPrefix, document);
    }
}
