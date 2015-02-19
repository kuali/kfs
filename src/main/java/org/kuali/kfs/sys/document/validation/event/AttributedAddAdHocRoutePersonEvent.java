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

import java.util.Map;

import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.document.Document;


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
