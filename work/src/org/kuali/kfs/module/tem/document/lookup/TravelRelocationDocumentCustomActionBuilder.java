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
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelCustomSearchLinks;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;

public class TravelRelocationDocumentCustomActionBuilder extends DocumentActionBuilderBase implements TravelDocumentCustomActionBuilder{

    protected static Logger LOG = Logger.getLogger(TravelRelocationDocumentCustomActionBuilder.class);


    /**
   * @param travelDocumentIdentifier
   * @return
   */
  public String createRelocationLink(String travelDocumentIdentifier) {
          final DocumentType docType = getDocumentTypeService().getDocumentTypeByName(TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);
          if (docType == null) {
              throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT));
          }
          String linkPopup = "target=\"_blank\"";

          String link = String.format("<a href=\"%s&travelDocumentIdentifier=%s&command=initiate&docTypeName=%s\" %s>%s</a>",
                  docType.getResolvedDocumentHandlerUrl(),
                  travelDocumentIdentifier,
                  TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT,
                  linkPopup,
                  TravelCustomSearchLinks.NEW_RELOCATION);
          return link;
      }

  /**
   * @see org.kuali.kfs.module.tem.document.lookup.TravelDocumentCustomActionBuilder#buildCustomActionHTML(org.kuali.rice.kew.api.document.search.DocumentSearchResult, org.kuali.kfs.module.tem.document.TravelDocument)
   */
  @Override
  public String buildCustomActionHTML(DocumentSearchResult documentSearchResult, TravelDocument document) {
      String tripId = document.getTravelDocumentIdentifier();
      StrBuilder actionsHTML = new StrBuilder();
      actionsHTML.setNewLineText("<br/>");
      actionsHTML.appendln(createRelocationLink(tripId));
      actionsHTML.append(createPaymentsURL(documentSearchResult, tripId));

      return actionsHTML.toString();
  }
}
