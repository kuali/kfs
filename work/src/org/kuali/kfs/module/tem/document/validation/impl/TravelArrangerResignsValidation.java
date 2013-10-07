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
import org.kuali.kfs.module.tem.TemPropertyConstants.ArrangerFields;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileArranger;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelArrangerResignsValidation extends GenericValidation {
    protected TemProfileService temProfileService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        TravelArrangerDocument document = (TravelArrangerDocument)event.getDocument();

        if(document.getResign()) {
            if(document.getTaInd() || document.getTrInd() || document.getPrimaryInd()) {
                GlobalVariables.getMessageMap().putError(ArrangerFields.RESIGN, TemKeyConstants.ERROR_TTA_ARRGR_RESIGN);
                success = false;
            }

            if (getTemProfileService().isProfileNonEmploye(document.getProfile())) {
                if (!isAnyActiveArrangersBesidesResigner(document.getProfile(), document.getArrangerId())) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.RESIGN, TemKeyConstants.ERROR_TEM_PROFILE_NONEMPLOYEE_MUST_HAVE_ACTIVE_ARRANGER);
                    success = false;
                }
            }
        }

        return success;
    }

    /**
     * Determines if there are any active arrangers on the given profile besides the current arranger
     * @param profile the profile to check
     * @param arrangerId the arranger to ignore
     * @return true if there are active arrangers besides the resigner; false otherwise
     */
    protected boolean isAnyActiveArrangersBesidesResigner(TEMProfile profile, String arrangerId) {
        for (TEMProfileArranger arranger : profile.getArrangers()) {
            if (arranger.isActive() && !arranger.getPrincipalId().equals(arrangerId)) {
                return true;
            }
        }
        return false;
    }

    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }
}
