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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.rice.krad.document.Document;

/**
 * Add Purchasing Capital Asset Location Event. 
 * This is triggered when a user presses the add button for a given document's location line.
 */
public final class AttributedAddPurchasingCapitalAssetLocationEvent extends AttributedPurchasingCapitalAssetLocationEventBase {
    /**
     * Constructs an AddLocationEvent with the given errorPathPrefix, document, and location.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param item the item being added 
     */
    public AttributedAddPurchasingCapitalAssetLocationEvent(String errorPathPrefix, Document document, CapitalAssetLocation location) {
        super("adding location to document " + getDocumentId(document), errorPathPrefix, document, location);
    }
}
