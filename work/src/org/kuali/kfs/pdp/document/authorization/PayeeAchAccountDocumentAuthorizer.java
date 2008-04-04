/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.document.authorization;

import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.module.pdp.PdpAuthorizationConstants;


public class PayeeAchAccountDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    /**
     * If the current user is a member of KUALI_PDP_VIEWALL then add entry to the editMode Map and set it true.
     * 
     * @see org.kuali.core.document.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        Map editMode = super.getEditMode(document, user);
        // String viewAllWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(PayeeAchAccount.class,
        // PdpConstants.Groups.VIEWALL_GROUP);
        String viewAllWorkgroup = "PD_VIEW_ACH";

        if (user.isMember(viewAllWorkgroup)) {
            editMode.put(PdpAuthorizationConstants.PDPEditMode.ENTRY, "TRUE");
        }

        return editMode;
    }
}
