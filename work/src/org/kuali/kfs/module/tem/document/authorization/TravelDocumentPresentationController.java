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

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelEditMode;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Travel Document Presentation Controller
 *
 */
public class TravelDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase{

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        ParameterService paramService = SpringContext.getBean(ParameterService.class);

        if (paramService.getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.DISPLAY_EMERGENCY_CONTACT_IND)) {
            editModes.add(TemConstants.DISPLAY_EMERGENCY_CONTACT_TAB);
        }

        editModes.add(KfsAuthorizationConstants.TransactionalEditMode.IMMEDIATE_DISBURSEMENT_ENTRY);
        editModes.add(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);
        editModes.add(TemConstants.EditModes.EXPENSE_LIMIT_ENTRY);
        return editModes;
    }

    /**
     * Check user's edit permission in order to grant full entry edit
     *
     * When the document is routed to document manager's approval node, check for permission on document
     * manager to get full edit
     *
     * @param document
     * @param editModes
     */
    protected void addFullEntryEditMode(Document document, Set<String> editModes) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        Person currentUser = GlobalVariables.getUserSession().getPerson();

        DocumentAuthorizer authorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        TravelArrangeableAuthorizer travelAuthorizer = (TravelArrangeableAuthorizer)authorizer;

        //check edit permission when document is in init or saved
        if ((workflowDocument.isInitiated() || workflowDocument.isSaved())) {
            //check for edit permission on the document
            if (travelAuthorizer.canEditDocument(document, currentUser)){
                editModes.add(TravelEditMode.FULL_ENTRY);
            }
        } else if(isAtNode(workflowDocument, getDocumentManagerApprovalNode())){ //Document manager will also get full entry edit mode on the approval node
            if (travelAuthorizer.canEditDocument(document, currentUser)){
                editModes.add(TravelEditMode.FULL_ENTRY);
            }
        }
    }

    /**
     * Check if workflow is at the specific node
     *
     * @param workflowDocument
     * @param nodeName
     * @return
     */
    public boolean isAtNode(WorkflowDocument workflowDocument, String nodeName) {
        Set<String> nodeNames = workflowDocument.getNodeNames();
        for (String nodeNamesNode : nodeNames) {
            if (nodeName.equals(nodeNamesNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the Document Manager approval node
     *
     * Default to Travel Manager's - AP_TRAVEL
     *
     * @return
     */
    public String getDocumentManagerApprovalNode(){
        return TemWorkflowConstants.RouteNodeNames.AP_TRAVEL;
    }

    /**
     * Check current user is the initiator
     *
     * @param workflowDocument
     * @return
     */
    public boolean isInitiator(WorkflowDocument workflowDocument){
        String docInitiator = workflowDocument.getInitiatorPrincipalId();
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        return docInitiator.equals(currentUser.getPrincipalId());
    }

    public ParameterService getParamService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected TemRoleService getTemRoleService() {
        return SpringContext.getBean(TemRoleService.class);
    }

    protected TemProfileService getTemProfileService() {
        return SpringContext.getBean(TemProfileService.class);
    }

    protected DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }

    protected ConfigurationService getConfigurationService() {
        return SpringContext.getBean(ConfigurationService.class);
    }

}
