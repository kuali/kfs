/*
 * Copyright 2008 The Kuali Foundation
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
