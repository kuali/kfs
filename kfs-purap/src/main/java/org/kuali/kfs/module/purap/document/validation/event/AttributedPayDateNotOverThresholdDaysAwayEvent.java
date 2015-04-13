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

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

/**
 * Calculate event for Commodity Codes for distribution
 * 
 */
public final class AttributedPayDateNotOverThresholdDaysAwayEvent extends AttributedDocumentEventBase {

    /**
     * Constructs a CommodityCodesForDistributionEvent with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked upon
     */
    public AttributedPayDateNotOverThresholdDaysAwayEvent(String errorPathPrefix, Document document) {
        super("Determing commodity codes for distribution on document " + getDocumentId(document), errorPathPrefix, document);
    }
}
