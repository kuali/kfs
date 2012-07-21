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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE;

import java.util.Set;

import org.kuali.kfs.module.tem.TemAuthorizationConstants;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Travel Reimbursement Document Presentation Controller
 * 
 */
public class TravelDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase{

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        ParameterService paramService = SpringContext.getBean(ParameterService.class);

        if (paramService.getIndicatorParameter(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.ENABLE_CONTACT_INFORMATION_IND)) {
            editModes.add(TemConstants.DISPLAY_EMERGENCY_CONTACT_TAB);
        }

        return editModes;
    }
    
    protected void addFullEntryEntryMode(Document document, Set<String> editModes) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        
        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved())) {
            editModes.add(TemAuthorizationConstants.TravelEditMode.FULL_ENTRY);
        }
        
        if(enableForTravelManager(workflowDocument)){
            editModes.add(TemAuthorizationConstants.TravelEditMode.FULL_ENTRY);
        }        
    }

    public boolean enableForTravelManager(KualiWorkflowDocument workflowDocument){
        boolean isTravelManager = getTravelDocumentService().isTravelManager(GlobalVariables.getUserSession().getPerson()); 
        boolean allowUpdate = getParamService().getIndicatorParameter(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TravelParameters.ALLOW_TRAVEL_OFFICE_TO_MODIFY_ALL_IND);
        
        if (isTravelManager && allowUpdate && workflowDocument.getCurrentRouteNodeNames().equals(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL)){
            return true;
        }
        return false;
    }
    
    public ParameterService getParamService() {
        return SpringContext.getBean(ParameterService.class);
    }
    
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }
    
    protected TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }
}
