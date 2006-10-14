/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.kra.budget.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.authorization.DocumentActionFlags;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentAuthorizerBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.service.BudgetPermissionsService;

/**
 * DocumentAuthorizer class for KRA Budget Documents.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetDocumentAuthorizer extends DocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(BudgetDocumentAuthorizer.class);
    
    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document d, KualiUser u) {
        Map editModeMap = new HashMap();
        editModeMap.put(SpringServiceLocator.getBudgetPermissionsService().getUserPermissionCode((BudgetDocument) d, u), "TRUE");
        return editModeMap;
    }
    
    /**
     * Overrides most of the inherited flags so that the buttons behave exactly like they used to in the obsoleted
     * budgetDocumentControls.tag
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, KualiUser user) {
        LOG.debug("calling BudgetDocumentAuthorizer.getDocumentActionFlags");

        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        flags.setCanAcknowledge(false);
        flags.setCanApprove(false);
        flags.setCanBlanketApprove(false);
        flags.setCanCancel(false);
        flags.setCanDisapprove(false);
        flags.setCanFYI(false);
        flags.setCanClose(false);

        BudgetDocument budgetDocument = (BudgetDocument) document;
        
        
        if (user.getPersonUniversalIdentifier().equals(budgetDocument.getBudget().getBudgetProjectDirectorSystemId())) {
            flags.setCanSave(true);
        }

        // else use inherited canSave, canRoute, canAnnotate, and canReload values

        return flags;
    }
}
