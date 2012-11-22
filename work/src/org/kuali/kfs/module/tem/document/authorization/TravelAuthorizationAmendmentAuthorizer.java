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
package org.kuali.kfs.module.tem.document.authorization;

import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

public class TravelAuthorizationAmendmentAuthorizer extends TravelAuthorizationAuthorizer {

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.TravelAuthorizationAuthorizer#canCopy(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument, org.kuali.rice.kim.bo.Person)
     */
    public boolean canCopy(TravelAuthorizationDocument travelDocument, Person user) {
        //do not allow copy in Travel Authorization Amendment document
        return false;
    }
    
//    /**
//     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#getDocumentActions(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person, java.util.Set)
//     */
//    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
//        
//        //do not allow document to be route when there is an enroute TR document
//        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);
//        if (getTravelAuthorizationService().findEnrouteOrProcessedTravelReimbursement((TravelAuthorizationDocument)document) != null){
//            documentActionsToReturn.remove(KRADConstants.KUALI_ACTION_CAN_ROUTE);
//            documentActionsToReturn.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
//        }
//        return documentActionsToReturn;
//    }

    public TravelAuthorizationService getTravelAuthorizationService(){
        return SpringContext.getBean(TravelAuthorizationService.class);
    }
}
