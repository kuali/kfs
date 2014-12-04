/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.util.GlobalVariables;

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
            DocumentStatus status = reimbursement.getDocumentHeader().getWorkflowDocument().getStatus();
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, TemKeyConstants.ERROR_TAA_WITH_TR_ENROUTE, reimbursement.getDocumentNumber(), KewApiConstants.DOCUMENT_STATUSES.get(status));
        }
        return validated;
    }

    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }


}
