/*
 * Copyright 2010 The Kuali Foundation.
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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_TRVL_REQ_GRTR_THAN_ZERO;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddTravelAdvanceLineEvent;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedApproveDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedBlanketApproveDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class TravelAuthTravelAdvanceValidation extends GenericValidation {
    private TemProfileService temProfileService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        TravelAuthorizationDocument document = (TravelAuthorizationDocument)event.getDocument();

        if (document.getTraveler() == null){
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRAVELER_TYPE, TemKeyConstants.ERROR_TA_TRVL_ADV_MISSING_PROFILE);
            return false;
        }

        if (event instanceof AttributedRouteDocumentEvent
                || event instanceof AttributedApproveDocumentEvent
                || event instanceof AttributedBlanketApproveDocumentEvent){
            for (int i=0;i<document.getTravelAdvances().size();i++){
                TravelAdvance advance = document.getTravelAdvances().get(i);
                success = isTravelAdvanceValid(document, advance,i);
            }

        }
        else{
            AddTravelAdvanceLineEvent travelEvent = (AddTravelAdvanceLineEvent)event;
            TravelAdvance advance = travelEvent.getTravelAdvance();

            success = isTravelAdvanceValid(document, advance, -1);
        }

        return success;
    }

    private boolean isTravelAdvanceValid(TravelAuthorizationDocument document, TravelAdvance advance, int index){
        boolean success = true;
        String temp = "";
        if (index > -1){
            GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);
            temp = TravelAuthorizationFields.TRVL_ADV + "[" + index + "].";
        }

        if(advance.getTravelAdvanceRequested() != null) {
            KualiDecimal advReq = advance.getTravelAdvanceRequested();
            if(advReq.isLessEqual(KualiDecimal.ZERO)) {
                success = false;
            }
        }
        else{
            success = false;
        }

        if(!success) {
            GlobalVariables.getMessageMap().putError(temp+TravelAuthorizationFields.TRVL_ADV_REQUESTED, ERROR_TA_TRVL_REQ_GRTR_THAN_ZERO);
        }

        if (advance.getDueDate() == null) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRVL_ADV_DUE_DATE, TemKeyConstants.ERROR_TA_TRVL_ADV_DUE_DATE_MISSING);
            success = false;
        }
        else {
            Date dueDate = KfsDateUtils.clearTimeFields(advance.getDueDate());
            Date today = KfsDateUtils.clearTimeFields(new Date());

            if (dueDate != null && dueDate.before(today)) {
                GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_CUSTOM, "The Payment Due Date cannot be in the past.");
                success = false;
            }
        }

        if (advance.getDueDate() != null && document.getTripEnd() == null){
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRIP_END_DT, TemKeyConstants.ERROR_TA_TRVL_TRIP_END_MISSING);
            success = false;
        }
        else if (advance.getDueDate() != null && advance.getDueDate().after(document.getTripEnd())) {
            GlobalVariables.getMessageMap().putError(temp + TravelAuthorizationFields.TRVL_ADV_DUE_DATE, TemKeyConstants.ERROR_TA_TRVL_ADV_DUE_DATE_INVALID);
            success = false;
        }

        String initiator = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        String travelerID = document.getTraveler().getPrincipalId();
        Boolean checkPolicy = Boolean.FALSE;
        if (travelerID != null){
            //traveler must accept policy, if initiator is arranger, the traveler will have to accept later.
            checkPolicy = initiator.equals(travelerID) || GlobalVariables.getUserSession().getPrincipalId().equals(travelerID);
        }
        else{ //Non-kim traveler, arranger accepts policy
            checkPolicy = Boolean.TRUE;
        }

        if (checkPolicy){
            if (!advance.getTravelAdvancePolicy()){
                success = false;
                GlobalVariables.getMessageMap().putError(temp+TravelAuthorizationFields.TRVL_ADV_POLICY, TemKeyConstants.ERROR_TA_TRVL_ADV_POLICY);
            }
        }

        boolean testCards = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.ENABLE_CC_CASH_ADVANCE_WARNING_IND);
        if (testCards){
            Collection<String> cardTypes = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.CASH_ADVANCE_CREDIT_CARD_TYPES);
            Map<String,String> cardTypeMap = new HashMap<String, String>();
            for (String cardType : cardTypes){
                cardTypeMap.put(cardType.toUpperCase(), cardType.toUpperCase());
            }

            TEMProfile temProfile = document.getTemProfile();
            if (temProfile == null && travelerID != null){
                temProfile = temProfileService.findTemProfileByPrincipalId(travelerID);
            }

            if (temProfile != null && temProfile.getAccounts() != null && temProfile.getAccounts().size() > 0){
                for (TEMProfileAccount account  : temProfile.getAccounts()){
                    if (cardTypeMap.containsKey(account.getName().toUpperCase())){
                        if (StringUtils.isBlank(advance.getAdditionalJustification())){
                            success = false;

                            GlobalVariables.getMessageMap().putError(temp+TravelAuthorizationFields.TRVL_ADV_ADD_JUST, TemKeyConstants.ERROR_TA_TRVL_ADV_ADD_JUST);
                        }
                    }
                }
            }
        }
        if (index > -1){
            GlobalVariables.getMessageMap().removeFromErrorPath("document");
        }

        return success;
    }

    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }
}
