/*
 * Copyright 2008-2009 The Kuali Foundation
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
