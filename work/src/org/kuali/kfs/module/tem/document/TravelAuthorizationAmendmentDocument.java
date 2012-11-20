/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.document.Document;


@Entity
@Table(name = "TEM_TRVL_AUTH_AMEND_DOC_T")
public class TravelAuthorizationAmendmentDocument extends TravelAuthorizationDocument {

    /**
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        
        super.doRouteStatusChange(statusChangeEvent);
      
        //doc is processed
        if (KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            
            List<Document> relatedDocs = getTravelDocumentService().getDocumentsRelatedTo(this, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT,
                    TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
            
            //updating the related's document appDocStatus to be retired
            for (Document document : relatedDocs){
                if (!document.getDocumentNumber().equals(this.getDocumentNumber())) {
                    ((TravelAuthorizationDocument) document).updateAppDocStatus(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION);
                }
            }
            getTravelEncumbranceService().adjustEncumbranceForAmendment((TravelAuthorizationAmendmentDocument)this);
        }
    }
}
