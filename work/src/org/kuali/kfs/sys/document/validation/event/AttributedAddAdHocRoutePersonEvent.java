/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.rule.event;

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;


public class AttributedAddAdHocRoutePersonEvent extends AttributedDocumentEventBase implements AttributedDocumentEvent {
    Map<String, Object> attributes;
    private AdHocRoutePerson adHocRoutePerson;
    
    /**
     * Constructs an AddAdHocRoutePersonEvent with the specified errorPathPrefix, document, and adHocRoutePerson
     * 
     * @param document
     * @param adHocRoutePerson
     * @param errorPathPrefix
     */
    public AttributedAddAdHocRoutePersonEvent(String errorPathPrefix, Document document, AdHocRoutePerson adHocRoutePerson) {
        super("creating add ad hoc route person event for document " + getDocumentId(document), errorPathPrefix, document);
        this.adHocRoutePerson = adHocRoutePerson;
    }

    /**
     * Constructs an AddAdHocRoutePersonEvent with the given document
     * 
     * @param document
     * @param adHocRoutePerson
     */
    public AttributedAddAdHocRoutePersonEvent(Document document, AdHocRoutePerson adHocRoutePerson) {
        this("", document, adHocRoutePerson);
    }

    /**
     * This method retrieves the document adHocRoutePerson associated with this event.
     * 
     * @return AdHocRoutePerson
     */
    public AdHocRoutePerson getAdHocRoutePerson() {
        return adHocRoutePerson;
    }
}
