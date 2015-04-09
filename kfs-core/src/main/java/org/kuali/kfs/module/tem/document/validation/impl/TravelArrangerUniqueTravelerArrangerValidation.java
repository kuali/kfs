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
import org.kuali.kfs.module.tem.TemPropertyConstants.ArrangerFields;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelArrangerUniqueTravelerArrangerValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        TravelArrangerDocument document = (TravelArrangerDocument)event.getDocument();
        Integer profileId = document.getProfileId();
        String arrangerId = document.getArrangerId();

        if(ObjectUtils.isNotNull(profileId)) {
            document.refreshReferenceObject("profile");
        }

        if (ObjectUtils.isNull(document.getProfile()) ) {
            // Route document does a validation of the DD and already put the error message on in the message map so we do not need to put another on the stack.
            // If we do not return here, we'll get a NPE down below.
            return false;
        }

        if (ObjectUtils.isNull(document.getProfile().getPrincipalId())) {
            //Non KIM TEM profile
            return true;
        }

        if(document.getProfile().getPrincipalId().equals(arrangerId)) {
            GlobalVariables.getMessageMap().putError(ArrangerFields.TRAVELER_NAME, TemKeyConstants.ERROR_TTA_ARRGR_TRVLR_SAME);
            return false;
        }

        return success;
    }

}
