/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.document;

import java.util.Map;

import org.kuali.KeyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.KraKeyConstants;

public class RoutingFormDocumentAuthorizer extends DocumentAuthorizerBase {

    @Override
    public Map getEditMode(Document d, UniversalUser u) {
        // TODO Auto-generated method stub
        
        Map editModes = super.getEditMode(d, u);
        
        RoutingFormDocument rfd = (RoutingFormDocument)d;
        if (rfd.getRoutingFormBudgetNumber() != null) {
            editModes.put(KraConstants.AuthorizationConstants.BUDGET_LINKED, "TRUE");
            if (!GlobalVariables.getMessageList().contains(KraKeyConstants.BUDGET_OVERRIDE))
                GlobalVariables.getMessageList().add(0, KraKeyConstants.BUDGET_OVERRIDE);
        }
        
        return editModes;
    }

    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {

        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        flags.setCanAcknowledge(false);
//        flags.setCanApprove(false);
        flags.setCanBlanketApprove(false);
        flags.setCanCancel(false);
        flags.setCanDisapprove(false);
        flags.setCanFYI(false);
        flags.setCanClose(false);
        flags.setCanSave(true);

        return flags;
    }
    
}