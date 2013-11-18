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

import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_TRVL_REQ_GRTR_THAN_ZERO;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.service.TemProfileService;
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
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.document.DocumentPresentationController;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class TravelAuthTravelAdvanceValidation extends GenericValidation {
    protected TemProfileService temProfileService;
    protected DocumentDictionaryService documentDictionaryService;

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
            if (document.shouldProcessAdvanceForDocument()) {
                success = isTravelAdvanceValid(document, document.getTravelAdvance());
            }
        }

        return success;
    }

    private boolean isTravelAdvanceValid(TravelAuthorizationDocument document, TravelAdvance advance) {
        boolean success = true;

        GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT+"."+TravelAuthorizationFields.TRVL_ADV + ".");

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
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRVL_ADV_REQUESTED, ERROR_TA_TRVL_REQ_GRTR_THAN_ZERO);
        }

        if (canCurrentUserEditDocument(document)) {
            success = success && validateDueDate(advance, document.getTripEnd());
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
                GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRVL_ADV_POLICY, TemKeyConstants.ERROR_TA_TRVL_ADV_POLICY);
            }
        }

        boolean testCards = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.CASH_ADVANCE_WARNING_IND);
        if (testCards){
            Collection<String> cardTypes = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.CASH_ADVANCE_CREDIT_CARD_TYPES);
            Map<String,String> cardTypeMap = new HashMap<String, String>();
            for (String cardType : cardTypes){
                cardTypeMap.put(cardType.toUpperCase(), cardType.toUpperCase());
            }

            TemProfile temProfile = document.getTemProfile();
            if (temProfile == null && travelerID != null){
                temProfile = temProfileService.findTemProfileByPrincipalId(travelerID);
            }

            if (temProfile != null && temProfile.getAccounts() != null && temProfile.getAccounts().size() > 0){
                for (TemProfileAccount account  : temProfile.getAccounts()){
                    if (cardTypeMap.containsKey(account.getName().toUpperCase())){
                        if (StringUtils.isBlank(advance.getAdditionalJustification())){
                            success = false;

                            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRVL_ADV_ADD_JUST, TemKeyConstants.ERROR_TA_TRVL_ADV_ADD_JUST);
                        }
                    }
                }
            }
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KRADPropertyConstants.DOCUMENT+"."+TravelAuthorizationFields.TRVL_ADV + ".");

        return success;
    }

    /**
     * Runs a number of rules against the due date of the advance: whether it has been filled it, that it's not in the past, that both it and the trip end must be filled in, and that the advance's due date is before the end of hte trip
     * @param advance the travel advance to validate
     * @param tripEnd the specified end of the trip
     * @return true if the advance passed this gauntlet of validations, false otherwise
     */
    protected boolean validateDueDate(TravelAdvance advance, Timestamp tripEnd) {
        boolean success = true;

        if (advance.getDueDate() != null) {
            Date dueDate = KfsDateUtils.clearTimeFields(advance.getDueDate());
            Date today = KfsDateUtils.clearTimeFields(new Date());

            if (dueDate != null && dueDate.before(today)) {
                GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRVL_ADV_DUE_DATE, KFSKeyConstants.ERROR_CUSTOM, "The Payment Due Date cannot be in the past.");
                success = false;
            }
        }

        if (advance.getDueDate() != null && tripEnd == null){
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRIP_END_DT, TemKeyConstants.ERROR_TA_TRVL_TRIP_END_MISSING);
            success = false;
        }
        else if (advance.getDueDate() != null && advance.getDueDate().after(tripEnd)) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRVL_ADV_DUE_DATE, TemKeyConstants.ERROR_TA_TRVL_ADV_DUE_DATE_INVALID);
            success = false;
        }
        return success;
    }

    /**
     * Uses the presentation controller and the authorizer for the travel auth doc to determine if the current user can edit the doc and if they have full edit edit mode
     * @return true if the doc is editable for the current user, false otherwise
     */
    protected boolean canCurrentUserEditDocument(TravelAuthorizationDocument doc) {
        // i hope no one tries to run this validation against something which isn't a TravelAuthDoc.  I mean...even if they do, they won't for very long
        final String documentTypeName = getDocumentDictionaryService().getDocumentTypeByClass(doc.getClass());
        final DocumentPresentationController presController = getDocumentDictionaryService().getDocumentPresentationController(documentTypeName);
        final DocumentAuthorizer authorizer = getDocumentDictionaryService().getDocumentAuthorizer(documentTypeName);
        final Set<String> presControllerEditModes = ((TransactionalDocumentPresentationController)presController).getEditModes(doc);
        final Set<String> editModes = ((TransactionalDocumentAuthorizer)authorizer).getEditModes(doc, GlobalVariables.getUserSession().getPerson(), presControllerEditModes);
        return editModes.contains(TemConstants.TravelEditMode.FULL_ENTRY) && presController.canEdit(doc) && authorizer.canEdit(doc, GlobalVariables.getUserSession().getPerson());
    }

    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

    /**
     * @return the injected implementation of the DocumentDictionaryService
     */
    public DocumentDictionaryService getDocumentDictionaryService() {
        return this.documentDictionaryService;
    }

    /**
     * Injects an implementation of the document dictionary service
     * @param documentDictionaryService an implementation of the document dictionary service to inject
     */
    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
