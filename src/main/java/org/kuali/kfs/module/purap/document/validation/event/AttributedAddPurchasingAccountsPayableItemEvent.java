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

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.rice.krad.document.Document;

/**
 * Add Purchasing Accounts Payble Item Event. 
 * This is triggered when a user presses the add button for a given document's item line.
 */
public final class AttributedAddPurchasingAccountsPayableItemEvent extends AttributedPurchasingAccountsPayableItemEventBase {
    /**
     * Constructs an AddItemEvent with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param item the item being added 
     */
    public AttributedAddPurchasingAccountsPayableItemEvent(String errorPathPrefix, Document document, PurApItem item) {
        super("adding item to document " + getDocumentId(document), errorPathPrefix, document, item);
    }
}
