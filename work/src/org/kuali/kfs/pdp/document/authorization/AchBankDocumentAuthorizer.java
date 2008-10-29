/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.document.authorization;

import java.util.Map;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;

public class AchBankDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    
    /**
     * If the current user is a member of PD_VIEW_ACH then add entry to the editMode Map and set it true.
     * 
     * @see org.kuali.rice.kns.document.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        Map editMode = super.getEditMode(document, user);
        String viewAllWorkgroup = "PD_VIEW_ACH";

        if (user.isMember(viewAllWorkgroup)) {
            editMode.put(PdpConstants.PDPEditMode.ENTRY, "TRUE");
        }

        return editMode;
    }
}

