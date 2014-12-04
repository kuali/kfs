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
 * Import Purchasing Accounts Payble Item Event. 
 * This is triggered when a user presses the import button on a purchasing document, 
 * and then clicks the add button after selecting a file to import items from.
 */
public class AttributedImportPurchasingAccountsPayableItemEvent extends AttributedPurchasingAccountsPayableItemEventBase {
    /**
     * Constructs an ImportItemEvent with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param item the item being added 
     */
    public AttributedImportPurchasingAccountsPayableItemEvent(String errorPathPrefix, Document document, PurApItem item) {
        super("importing item to document " + getDocumentId(document), errorPathPrefix, document, item);
    }

}
