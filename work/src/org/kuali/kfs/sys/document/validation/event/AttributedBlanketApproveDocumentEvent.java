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

public class AttributedBlanketApproveDocumentEvent extends AttributedDocumentEventBase {
    /**
     * Constructs an BlanketApproveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param errorPathPrefix
     * @param document
     */
    public AttributedBlanketApproveDocumentEvent(String errorPathPrefix, Document document) {
        super("blanketApprove", errorPathPrefix, document);
    }

    /**
     * Constructs a BlanketApproveDocumentEvent with the given document
     * 
     * @param document
     */
    public AttributedBlanketApproveDocumentEvent(Document document) {
        super("blanketApprove", "", document);
    }
}
