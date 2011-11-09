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

import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.document.Document;

/**
 * An attributed version (AccountingRulesEngine version) of the AddAdHocRouteWorkgroupEvent.
 */
public class AttributedAddAdHocRouteWorkgroupEvent extends AttributedDocumentEventBase {
    private AdHocRouteWorkgroup adHocRouteWorkgroup;

    /**
     * Constructs an AddAdHocRouteWorkgroupEvent with the specified errorPathPrefix, document, and adHocRouteWorkgroup
     * 
     * @param document
     * @param adHocRouteWorkgroup
     * @param errorPathPrefix
     */
    public AttributedAddAdHocRouteWorkgroupEvent(String errorPathPrefix, Document document, AdHocRouteWorkgroup adHocRouteWorkgroup) {
        super("creating add ad hoc route workgroup event for document " + getDocumentId(document), errorPathPrefix, document);
        this.adHocRouteWorkgroup = adHocRouteWorkgroup;
    }

    /**
     * Constructs an AddAdHocRouteWorkgroupEvent with the given document
     * 
     * @param document
     * @param adHocRouteWorkgroup
     */
    public AttributedAddAdHocRouteWorkgroupEvent(Document document, AdHocRouteWorkgroup adHocRouteWorkgroup) {
        this("", document, adHocRouteWorkgroup);
    }

    /**
     * This method retrieves the document adHocRouteWorkgroup associated with this event.
     * 
     * @return AdHocRouteWorkgroup
     */
    public AdHocRouteWorkgroup getAdHocRouteWorkgroup() {
        return adHocRouteWorkgroup;
    }
}
