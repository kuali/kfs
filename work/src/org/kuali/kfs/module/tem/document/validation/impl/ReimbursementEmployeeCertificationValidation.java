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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class ReimbursementEmployeeCertificationValidation extends GenericValidation {

    private TravelerService travelerService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        final TravelReimbursementDocument reimbursement = (TravelReimbursementDocument)event.getDocument();
        if (requiresCertification(reimbursement.getTraveler())) {
            if (!reimbursement.getEmployeeCertification()) {
                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EMPLOYEE_CERTIFICATION, TemKeyConstants.ERROR_TR_EMPLOYEE_INITIATOR_MUST_CERTIFY);
                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
                valid = false;
            }
        }

        return valid;
    }  

    protected boolean requiresCertification(final TravelerDetail traveler) {
        final Person user = GlobalVariables.getUserSession().getPerson();
        if (user.getPrincipalId().equals(traveler.getPrincipalId())
            && isEmployee(traveler)) {
            return true;
        }        
        return false;
    }

    protected boolean isEmployee(final TravelerDetail traveler) {
        if (traveler == null) {
            return false;
        }
        return getTravelerService().isEmployee(traveler);
    }



    /**
     * Gets the value of travelerService
     *
     * @return the value of travelerService
     */
    public final TravelerService getTravelerService() {
        return this.travelerService;
    }

    /**
     * Sets the value of travelerService
     *
     * @param argTravelerService Value to assign to this.travelerService
     */
    public final void setTravelerService(final TravelerService argTravelerService) {
        this.travelerService = argTravelerService;
    }
}
