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
