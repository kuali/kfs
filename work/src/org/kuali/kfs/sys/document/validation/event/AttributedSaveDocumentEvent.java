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

public class AttributedSaveDocumentEvent extends AttributedDocumentEventBase {
    /**
     * Constructs a SaveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param document
     * @param errorPathPrefix
     */
    public AttributedSaveDocumentEvent(String errorPathPrefix, Document document) {
        this("creating save event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a SaveDocumentEvent with the given document
     * 
     * @param document
     */
    public AttributedSaveDocumentEvent(Document document) {
        this("", document);
    }
    
    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEventBase#KualiDocumentEventBase(java.lang.String, java.lang.String, org.kuali.rice.krad.document.Document)
     */
    public AttributedSaveDocumentEvent(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
    }
}
