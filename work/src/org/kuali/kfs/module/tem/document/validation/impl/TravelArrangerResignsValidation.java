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
import org.kuali.kfs.module.tem.TemPropertyConstants.ArrangerFields;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
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
    protected boolean isAnyActiveArrangersBesidesResigner(TemProfile profile, String arrangerId) {
        for (TemProfileArranger arranger : profile.getArrangers()) {
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
