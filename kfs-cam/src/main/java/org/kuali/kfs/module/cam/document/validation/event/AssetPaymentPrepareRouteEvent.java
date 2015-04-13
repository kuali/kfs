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
package org.kuali.kfs.module.cam.document.validation.event;

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

public class AssetPaymentPrepareRouteEvent extends AttributedDocumentEventBase {

    /**
     * Constructs an AssetPaymentPrepareRouteEvent with the given errorPathPrefix, document. This even will be triggered when
     * document starts routing. We run all route validations by this event. Then we could show the warning msg which will be
     * triggered by pre-rule at a later time.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AssetPaymentPrepareRouteEvent(String errorPathPrefix, Document document) {
        super("Prepare for routing Asset Payment document " + getDocumentId(document), errorPathPrefix, document);
    }
}
