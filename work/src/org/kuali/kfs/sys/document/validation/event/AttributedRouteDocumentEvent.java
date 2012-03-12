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
 * An attributed version of the route document event.
 */
public class AttributedRouteDocumentEvent extends AttributedDocumentEventBase {
    /**
     * Constructs a RouteDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param errorPathPrefix
     * @param document
     */
    public AttributedRouteDocumentEvent(String errorPathPrefix, Document document) {
        super("creating route event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a RouteDocumentEvent with the given document
     * 
     * @param document
     */
    public AttributedRouteDocumentEvent(Document document) {
        this("", document);
    }
}
