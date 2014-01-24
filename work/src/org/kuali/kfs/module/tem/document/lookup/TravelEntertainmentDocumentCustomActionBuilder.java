/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.lookup;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;

public class TravelEntertainmentDocumentCustomActionBuilder extends DocumentActionBuilderBase implements TravelDocumentCustomActionBuilder{

    protected static Logger LOG = Logger.getLogger(TravelEntertainmentDocumentCustomActionBuilder.class);

    /**
     * @see org.kuali.kfs.module.tem.document.lookup.TravelDocumentCustomActionBuilder#buildCustomActionHTML(org.kuali.rice.kew.api.document.search.DocumentSearchResult, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public String buildCustomActionHTML(DocumentSearchResult documentSearchResult, TravelDocument document) {
        String tripId = document.getTravelDocumentIdentifier();
        StrBuilder actionsHTML = new StrBuilder();
        actionsHTML.setNewLineText("<br/>");
        if (!getAccountingDocumentRelationshipService().isDocumentSomebodysChild(document.getDocumentNumber())) {
            actionsHTML.appendln(createEntertainmentLink(tripId, documentSearchResult));
        }
        actionsHTML.append(createPaymentsURL(documentSearchResult, tripId));

        return actionsHTML.toString();
    }

}
