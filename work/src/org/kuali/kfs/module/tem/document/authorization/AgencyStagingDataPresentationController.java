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
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;

public class AgencyStagingDataPresentationController extends MaintenanceDocumentPresentationControllerBase {

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

    public DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            documentHelperService = KNSServiceLocator.getDocumentHelperService();
        }
        return documentHelperService;
    }
}
