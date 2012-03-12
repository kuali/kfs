/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedSaveDocumentEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.SaveEvent;

/**
 * SelectSystem Event for Accounts Payable Document.
 * This could be triggered when a user presses the SelectSystem button to go to the next page.
 */
public final class AttributedSelectSystemPurapEvent extends AttributedSaveDocumentEvent implements SaveEvent {

    /**
     * Overridden constructor.
     * 
     * @param document the document for this event
     */
    public AttributedSelectSystemPurapEvent(Document document) {
        this(KFSConstants.EMPTY_STRING, document);
    }

    /**
     * Constructs a SelectSystemPurapEvent with the given errorPathPrefix and document.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked upon
     */
    public AttributedSelectSystemPurapEvent(String errorPathPrefix, Document document) {
        super("selecting system for document " + getDocumentId(document), errorPathPrefix, document);
    }
}
