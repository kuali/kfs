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

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.SaveOnlyDocumentEvent;

/**
 * This class...
 */
public class DocumentSystemSaveEvent extends SaveOnlyDocumentEvent {

    /**
     * Constructs a DocumentSystemSaveEvent with the specified errorPathPrefix and document. Event will
     * 
     * @param document
     * @param errorPathPrefix
     */
    public DocumentSystemSaveEvent(String errorPathPrefix, Document document) {
        super("creating unvalidated save event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * METHOD ALWAYS RETURNS TRUE
     * 
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    @Override
    public boolean invokeRuleMethod(BusinessRule arg0) {
        return true;
    }

}
