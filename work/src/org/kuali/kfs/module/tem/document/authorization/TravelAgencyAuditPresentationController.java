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

import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.PermissionTemplate;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAgencyAuditPresentationController extends MaintenanceDocumentPresentationControllerBase {

    DocumentHelperService documentHelperService;
   
    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canInitiate(java.lang.String)
     */
    @Override
    public boolean canInitiate(String documentTypeName) {
        Person user = GlobalVariables.getUserSession().getPerson();
        if (!SpringContext.getBean(TravelDocumentService.class).isTravelManager(user)) {
            throw new DocumentInitiationException(TemKeyConstants.ERROR_TRAVEL_AGENCY_AUDIT_INITIATION, new String[] {}, true);
        }
                
        return true;
    }
    
    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        
        Person user = GlobalVariables.getUserSession().getPerson();
        DocumentAuthorizer authorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        //if not authorized for full edit permission, set the following fields as read only
        if (!authorizer.isAuthorizedByTemplate(document, TemConstants.NAMESPACE, PermissionTemplate.FULL_EDIT_AGENCY_DATA, user.getPrincipalId())){
            readOnlyPropertyNames.add(TemPropertyConstants.IMPORT_BY);
            readOnlyPropertyNames.add(TemPropertyConstants.AGENCY_DATA_ID);
            readOnlyPropertyNames.add(TemPropertyConstants.OTHER_COMPANY_NAME);
            readOnlyPropertyNames.add(TemPropertyConstants.TRIP_TRAVELER_TYPE_ID);
            readOnlyPropertyNames.add(TemPropertyConstants.OTHER_AMOUNT);
            readOnlyPropertyNames.add(TemPropertyConstants.TRAVELER_NAME);
            readOnlyPropertyNames.add(TemPropertyConstants.TRAVELER_NETWORK_ID);
            readOnlyPropertyNames.add(TemPropertyConstants.TRIP_EXPENSE_AMOUNT);
            readOnlyPropertyNames.add(TemPropertyConstants.TRIP_ARRANGER_NAME);
        }
        return readOnlyPropertyNames;
    }

    public DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            documentHelperService = KNSServiceLocator.getDocumentHelperService();
        }
        return documentHelperService;
    }
}
