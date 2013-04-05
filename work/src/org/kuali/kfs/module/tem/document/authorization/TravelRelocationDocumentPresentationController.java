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

import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants.TEMRoleNames;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;


/**
 * Travel Reimbursement Document Presentation Controller
 *
 */
public class TravelRelocationDocumentPresentationController extends TravelDocumentPresentationController {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        return editModes;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController#getDocumentManagerApprovalNode()
     */
    @Override
    public String getDocumentManagerApprovalNode(){
        return TemWorkflowConstants.RouteNodeNames.MOVING_AND_RELOCATION_MANAGER;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController#enableForDocumentManager(org.kuali.rice.kim.bo.Person, boolean)
     */
    @Override
    public boolean enableForDocumentManager(Person currentUser, boolean checkParameters){
        boolean isRelocationManager = getTemRoleService().checkUserTEMRole(currentUser, TEMRoleNames.MOVING_AND_RELOCATION_MANAGER);
        boolean allowUpdate = checkParameters? getParamService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.TRAVEL_OFFICE_MODIFY_ALL_FIELDS_IND) : true;

        boolean isEnabled = isRelocationManager && allowUpdate;
        return isEnabled;
    }

}
