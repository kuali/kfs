/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
