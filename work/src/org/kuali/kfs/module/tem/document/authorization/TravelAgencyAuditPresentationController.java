/*
 * Copyright 2011 The Kuali Foundation.
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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.util.GlobalVariables;

public class TravelAgencyAuditPresentationController extends MaintenanceDocumentPresentationControllerBase {

   
    @Override
    public boolean canInitiate(String documentTypeName) {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (!SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser)) {
            throw new DocumentInitiationException(TemKeyConstants.ERROR_TRAVEL_AGENCY_AUDIT_INITIATION, new String[] {}, true);
        }
                
        return true;
    }
    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canCopy(Document document) {
        // TODO Auto-generated method stub
        return false;
    }

    
}
