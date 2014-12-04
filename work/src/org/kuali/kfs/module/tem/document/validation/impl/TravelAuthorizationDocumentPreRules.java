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
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthorizationDocumentPreRules extends PromptBeforeValidationBase {

    @SuppressWarnings("rawtypes")
    @Override
    public boolean doPrompts(Document document) {
        boolean foundMatchingTrips = false;
        TravelAuthorizationDocument authorizationDocument = (TravelAuthorizationDocument) document;


        List<String> documentIds = SpringContext.getBean(TravelAuthorizationService.class).findMatchingTrips(authorizationDocument);

        if(ObjectUtils.isNotNull(documentIds)&& !documentIds.isEmpty()) {
            foundMatchingTrips = true;
        }

       boolean shouldAskQuestion = false;
        String proceed =  SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TRVL_DOCUMENT_PROCEED_QUESTION);
        String question = "";
        if(foundMatchingTrips){
            question = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TRVL_DUPLICATE_TRIP_QUESTION);
            shouldAskQuestion = true;
        }


        if (shouldAskQuestion) {
            boolean userClickedYes = super.askOrAnalyzeYesNoQuestion("AUTHORIZATION_WARNING", question + " " + documentIds.toString() + " " + proceed  );
            if (!userClickedYes) {
                this.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
            }
            return userClickedYes;
        }
        else {
            //no question necessary- continue as normal
            return true;
        }
    }




}
