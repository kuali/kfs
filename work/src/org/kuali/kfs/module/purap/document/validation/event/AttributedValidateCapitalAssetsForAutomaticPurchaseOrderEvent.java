/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;

public final class AttributedValidateCapitalAssetsForAutomaticPurchaseOrderEvent extends AttributedDocumentEventBase {

    /**
     * Overridden constructor.
     * 
     * @param document the document for this event
     */
    public AttributedValidateCapitalAssetsForAutomaticPurchaseOrderEvent(Document document) {
        this(KFSConstants.EMPTY_STRING, document);
    }

    /**
     * Constructs a ValidateCapitalAssetsForAutomaticPurchaseOrder with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked upon
     */
    public AttributedValidateCapitalAssetsForAutomaticPurchaseOrderEvent(String errorPathPrefix, Document document) {
        super("calculating on document " + getDocumentId(document), errorPathPrefix, document);
    }
}
