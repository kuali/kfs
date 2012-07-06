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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * Travel Reimbursement Document Presentation Controller
 * 
 */
public class TravelAuthorizationDocumentPresentationController extends TravelDocumentPresentationController{

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        addFullEntryEntryMode(document, editModes);

        return editModes;
    }

    /**
     * Override this method to add extra validation, so that when the user is not a travel
     * arranger and does not have a TEM Profile, an error message will be displayed, instead 
     * of creating an TravelAuthorizationDocument.
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canInitiate(java.lang.String)
     */
    @Override
    public boolean canInitiate(String documentTypeName) {
    	if(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT.equalsIgnoreCase(documentTypeName)) {
	        Person currentUser = GlobalVariables.getUserSession().getPerson();
	        if(!getTravelDocumentService().isTravelArranger(currentUser)) {
	            TEMProfile temProfile = getTravelService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
	            if(temProfile == null) {
	                throw new DocumentInitiationException(TemKeyConstants.ERROR_AUTHORIZATION_TA_INITIATION, new String[] { documentTypeName }, true);
	            }
	        }
    	}

        return super.canInitiate(documentTypeName);
    }
    
	/**
	 * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
	 */
	@Override
	protected boolean canEdit(Document document) {
		Person currentUser = GlobalVariables.getUserSession().getPerson();
		TravelDocument travelDocument = (TravelDocument) document;
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        
        if (TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT.equalsIgnoreCase(workflowDocument.getDocumentType()) && (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved())) {
			Map<String, String> primaryKeys = new HashMap<String, String>();
	    	
			if(ObjectUtils.isNotNull(travelDocument.getTemProfileId())) {
	            TEMProfile temProfile = getTravelService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
		    	if(ObjectUtils.isNull(temProfile) || !travelDocument.getTemProfileId().equals(temProfile.getProfileId())) {
	    	    	primaryKeys.put(TEMProfileProperties.PROFILE_ID, travelDocument.getTemProfileId().toString());
	    	    	TEMProfile profile = (TEMProfile) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TEMProfile.class, primaryKeys);
	    	    	
	    	    	if(ObjectUtils.isNotNull(profile) && !getTravelDocumentService().isTravelArranger(currentUser, profile.getHomeDepartment())) {
		                throw new DocumentInitiationException(TemKeyConstants.ERROR_AUTHORIZATION_TA_EDIT, new String[] { }, true);
			        }
				}
			}
        }

        return super.canEdit(document);
	}
    
    @Override
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }
    
    @Override
    protected TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }
    
    
}
