/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

public class TravelAuthAmendmentValidation extends GenericValidation {

    TravelAuthorizationService travelAuthorizationService;
    
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean validated = true;
        TravelDocument document = (TravelDocument)event.getDocument();

        //special rule for Travel Authorization Amendment documents
        if (document instanceof TravelAuthorizationAmendmentDocument){
            List<String> errorPath = GlobalVariables.getMessageMap().getErrorPath(); 
            GlobalVariables.getMessageMap().clearErrorPath();
            
            //validate if there is no enroute TR 
            validated &= validateSubmissionWithoutEnrouteReimbursement((TravelAuthorizationDocument)document);
    
            //reset the error path
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().getErrorPath().addAll(errorPath);
        }
        
        return validated;
    }

    /**
     * Invalidate the document if there is an TR document enroute (no more amendment is allowed once TR is enroute) 
     * 
     * @param document
     * @return
     */
    public boolean validateSubmissionWithoutEnrouteReimbursement(TravelAuthorizationDocument authorization){
        boolean validated = true;
        
        TravelReimbursementDocument reimbursement = travelAuthorizationService.findEnrouteOrProcessedTravelReimbursement(authorization);
        if (reimbursement != null){
            validated = false;
            String status = reimbursement.getDocumentHeader().getWorkflowDocument().getStatusDisplayValue();
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, TemKeyConstants.ERROR_TAA_WITH_TR_ENROUTE, reimbursement.getDocumentNumber(), status);
        }
        return validated;
    }

    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }
    
    
}
