/*
 * Copyright 2013 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.validation.impl;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelReimbursementDocumentPreRules extends PromptBeforeValidationBase {

    @SuppressWarnings("rawtypes")
    @Override
    public boolean doPrompts(Document document) {
        boolean foundMatchingTrips = false;
        TravelReimbursementDocument reimbursementDocument = (TravelReimbursementDocument) document;

        //traveler, dates and destination
        String documentNumber = reimbursementDocument.getDocumentNumber();
        Integer temProfileId = reimbursementDocument.getTemProfileId();
        Timestamp tripBegin = reimbursementDocument.getTripBegin() ;
        Timestamp tripEnd = reimbursementDocument.getTripEnd();
        Integer primaryDestinationId = reimbursementDocument.getPrimaryDestinationId();

        List<String> documentIds = SpringContext.getBean(TravelReimbursementService.class).findMatchingTrips(temProfileId, tripBegin, tripEnd, primaryDestinationId);

        if(ObjectUtils.isNotNull(documentIds)&& !documentIds.isEmpty()) {
            foundMatchingTrips = true;
        }

       boolean shouldAskQuestion = false;
        String proceed =  SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TRVL_REMIB_PROCEED_QUESTION);;
        String question = "";
        if(foundMatchingTrips){
            question = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TRVL_REMIB_DUPLICATE_TRIP_QUESTION);
            shouldAskQuestion = true;
        }


        if (shouldAskQuestion) {
            boolean userClickedYes = super.askOrAnalyzeYesNoQuestion("REMIB_WARNING", question + " " + documentIds.toString() + " " + proceed  );
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
