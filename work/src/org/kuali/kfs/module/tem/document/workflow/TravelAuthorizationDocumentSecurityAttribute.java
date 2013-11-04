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
package org.kuali.kfs.module.tem.document.workflow;

import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * TEM Security Attribute restrict doc search results and view route log
 */
public class TravelAuthorizationDocumentSecurityAttribute extends TEMSecurityAttribute {

    private DocumentHelperService documentHelperService;
    private DocumentService documentService;

    /**
     * @see org.kuali.kfs.module.tem.document.workflow.TEMSecurityAttribute#isAuthorizedForDocument(java.lang.String,org.kuali.rice.kew.api.document.Document)
     */
    @Override
    public boolean isAuthorizedForDocument(String principalId, Document document) {
        boolean authorized = super.isAuthorizedForDocument(principalId, document) && canOpen(GlobalVariables.getUserSession().getPerson(), document.getDocumentTypeName(), document.getDocumentId());

        // /TA doc allows search result IF user has TR arranger access
        TravelDocument travelDocument = getDocument(document.getDocumentId());
        Person user = GlobalVariables.getUserSession().getPerson();
        // check if user is an TR arranger to the document
        boolean arrangerAccess = true;

        if (!user.getPrincipalId().equals(travelDocument.getTraveler().getPrincipalId())) {
            arrangerAccess = getTemRoleService().isTravelArranger(user, "", travelDocument.getTemProfileId().toString(), TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        }
        return authorized && arrangerAccess;

    }

    protected TemRoleService getTemRoleService() {
        return SpringContext.getBean(TemRoleService.class);
    }

}
