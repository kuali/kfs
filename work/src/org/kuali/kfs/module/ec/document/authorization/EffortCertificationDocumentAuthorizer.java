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
package org.kuali.module.effort.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.effort.EffortConstants.EffortCertificationEditMode;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;
import org.kuali.workflow.KualiWorkflowUtils;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

/**
 * Document Authorizer for the Effort Certification document.
 */
public class EffortCertificationDocumentAuthorizer extends TransactionalDocumentAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentAuthorizer.class);

    /**
     * @see org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        TransactionalDocumentActionFlags documentActionFlags = (TransactionalDocumentActionFlags) flags;

        boolean initiated = document.getDocumentHeader().getWorkflowDocument().stateIsInitiated();
        if (initiated) {
            // if the status code is intitiated, then the document should be a recreate document that has not been submitted
            documentActionFlags.setCanBlanketApprove(false);
        }
        else {
            // diallowed actions for enroute
            documentActionFlags.setCanDisapprove(false);
        }

        documentActionFlags.setHasAmountTotal(true);
        
        // disallowed actions for all status(s)
        documentActionFlags.setCanCancel(false);
        documentActionFlags.setCanSave(false);
        documentActionFlags.setCanCopy(false);
        documentActionFlags.setCanErrorCorrect(false);

        return flags;
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public Map<String, String> getEditMode(Document document, UniversalUser universalUser) {
        Map<String, String> editModeMap = new HashMap<String, String>();
        String editMode = EffortCertificationEditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();  
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, universalUser)) {
                editModeMap.put(EffortCertificationEditMode.FULL_ENTRY, Boolean.TRUE.toString());
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            String routeLevel = workflowDocument.getCurrentRouteNodeNames();

            Map<String, String> editModes = this.getEditModeSetup();
            Map<String,Boolean> editableIndicators = this.getEditableIndicator();
            if (editModes.containsKey(routeLevel) && editableIndicators.get(routeLevel)) {
                editMode = editModes.get(routeLevel);
                editModeMap.put(EffortCertificationEditMode.FULL_ENTRY, Boolean.FALSE.toString());
            }
        }

        editModeMap.put(editMode, Boolean.TRUE.toString());
        return editModeMap;
    }

    // setup the edit map where its key is route level name and its value the edit mode.
    private Map<String, String> getEditModeSetup() {
        Map<String, String> editModes = new HashMap<String, String>();

        editModes.put(RouteLevelNames.PROJECT_DIRECTOR, EffortCertificationEditMode.PROJECT_ENTRY);
        editModes.put(RouteLevelNames.ACCOUNT_REVIEW, EffortCertificationEditMode.EXPENSE_ENTRY);
        editModes.put(RouteLevelNames.ORG_REVIEW, EffortCertificationEditMode.EXPENSE_ENTRY);
        editModes.put(RouteLevelNames.CG_WORKGROUP, EffortCertificationEditMode.EXPENSE_ENTRY);
        editModes.put(RouteLevelNames.RECREATE_WORKGROUP, EffortCertificationEditMode.EXPENSE_ENTRY);

        return editModes;
    }
    
    // setup the editable indicator map where its key is route level name and its value the editable indicator.
    private Map<String, Boolean> getEditableIndicator() {
        Map<String, Boolean> editableIndicators = new HashMap<String, Boolean>();

        editableIndicators.put(RouteLevelNames.PROJECT_DIRECTOR, true);
        editableIndicators.put(RouteLevelNames.ACCOUNT_REVIEW, true);
        editableIndicators.put(RouteLevelNames.ORG_REVIEW, EffortCertificationParameterFinder.getOrganizationRoutingEditableIndicator());
        editableIndicators.put(RouteLevelNames.CG_WORKGROUP, EffortCertificationParameterFinder.getAwardRoutingEditableIndicator());
        editableIndicators.put(RouteLevelNames.RECREATE_WORKGROUP, EffortCertificationParameterFinder.getAdministrationRoutingEditableIndicator());

        return editableIndicators;
    }
}