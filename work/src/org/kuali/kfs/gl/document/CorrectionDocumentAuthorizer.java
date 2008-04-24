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

package org.kuali.module.gl.document;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;

public class CorrectionDocumentAuthorizer extends DocumentAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionDocumentAuthorizer.class);

    public CorrectionDocumentAuthorizer() {
        super();
    }

    /**
     * Adds hasAmountTotal flag.
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        LOG.debug("calling DocumentActionFlags.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");
        DocumentActionFlags flags = new DocumentActionFlags(super.getDocumentActionFlags(document, user));

        // if document implements AmountTotaling interface, then we should display the total
        if (document instanceof AmountTotaling) {
            flags.setHasAmountTotal(true);
        }
        else {
            flags.setHasAmountTotal(false);
        }

        return flags;
    }

    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        LOG.debug("getEditMode() started");

        String editMode = KfsAuthorizationConstants.TransactionalEditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsCanceled() || (document.getDocumentHeader().getFinancialDocumentInErrorNumber() != null)) {
            editMode = KfsAuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
        }
        else if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (workflowDocument.userIsInitiator(user)) {
                editMode = KfsAuthorizationConstants.TransactionalEditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute()) {
            editMode = KfsAuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
        }

        Map editModeMap = new HashMap();
        editModeMap.put(editMode, "TRUE");

        LOG.debug("getEditMode() editMode = " + editMode);
        return editModeMap;
    }
}
