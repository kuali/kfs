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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

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
