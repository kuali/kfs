/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.document.web.struts;

import java.text.MessageFormat;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.document.service.BatchMaintenanceService;
import org.kuali.kfs.pdp.exception.PdpException;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.action.KualiAction;

/**
 * This class defines action for Batch (cancel, hold, remove hold).
 */
public class BatchAction extends KualiAction {

    private BatchMaintenanceService batchMaintenanceService;

    /**
     * Constructs a BatchAction.java.
     */
    public BatchAction() {
        setBatchMaintenanceService(SpringContext.getBean(BatchMaintenanceService.class));
    }


    /**
     * This method confirms batch cancel action.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String cancelNoteText = KFSConstants.EMPTY_STRING;
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        boolean actionStatus;
        String message = KFSConstants.EMPTY_STRING;

        String batchId = request.getParameter(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM);
        if (batchId == null) {
            batchId = request.getParameter(KNSConstants.QUESTION_CONTEXT);
        }

        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();
        String confirmationText = kualiConfiguration.getPropertyString(PdpKeyConstants.BatchConstants.Confirmation.CANCEL_BATCH_MESSAGE);
        confirmationText = MessageFormat.format(confirmationText, batchId);

        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.CANCEL_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, "confirmCancel", batchId);
        }
        else {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((PdpKeyConstants.BatchConstants.Confirmation.CANCEL_BATCH_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                actionStatus = false;
            }
            else {
                cancelNoteText = reason;
                int noteTextLength = (reason == null) ? 0 : cancelNoteText.length();
                int noteTextMaxLength = PdpKeyConstants.BatchConstants.Confirmation.NOTE_TEXT_MAX_LENGTH;

                if (StringUtils.isBlank(reason)) {

                    if (reason == null) {
                        // prevent a NPE by setting the reason to a blank string
                        reason = KFSConstants.EMPTY_STRING;
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.CANCEL_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_EMPTY, KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }
                else if (noteTextLength > noteTextMaxLength) {
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.CANCEL_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_TOO_LONG, KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }

                try {
                    performCancel(batchId, cancelNoteText, universalUser);
                    actionStatus = true;
                    message = PdpKeyConstants.BatchConstants.Messages.BATCH_SUCCESSFULLY_CANCELED;
                }
                catch (PdpException pdpe) {
                    actionStatus = false;
                    message = pdpe.getMessage();
                }
            }
        }
        String returnUrl = buildUrl(batchId, actionStatus, message);
        return new ActionForward(returnUrl, true);

    }

    /**
     * This method cancels a batch.
     * 
     * @param batchIdString a string representing the batch id
     * @param cancelNote the cancelation note entered by the user
     * @param user the current user
     */
    private void performCancel(String batchIdString, String cancelNote, UniversalUser user) throws PdpException {
        try {
            Integer batchId = Integer.parseInt(batchIdString);
            batchMaintenanceService.cancelPendingBatch(batchId, cancelNote, user);
        }
        catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError(PdpPropertyConstants.BatchConstants.Fields.BATCH_ID, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_ID_IS_NOT_NUMERIC);
        }

    }

    /**
     * This method confirms batch hold action
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndHold(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String holdNoteText = KFSConstants.EMPTY_STRING;
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        boolean actionStatus;
        String message = KFSConstants.EMPTY_STRING;

        String batchId = request.getParameter(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM);
        if (batchId == null) {
            batchId = request.getParameter(KNSConstants.QUESTION_CONTEXT);
        }

        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();
        String confirmationText = kualiConfiguration.getPropertyString(PdpKeyConstants.BatchConstants.Confirmation.HOLD_BATCH_MESSAGE);
        confirmationText = MessageFormat.format(confirmationText, batchId);

        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.HOLD_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, "confirmHold", batchId);
        }
        else {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((PdpKeyConstants.BatchConstants.Confirmation.HOLD_BATCH_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                actionStatus = false;
            }
            else {
                holdNoteText = reason;
                int noteTextLength = (reason == null) ? 0 : holdNoteText.length();
                int noteTextMaxLength = PdpKeyConstants.BatchConstants.Confirmation.NOTE_TEXT_MAX_LENGTH;

                if (StringUtils.isBlank(reason)) {

                    if (reason == null) {
                        // prevent a NPE by setting the reason to a blank string
                        reason = KFSConstants.EMPTY_STRING;
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.HOLD_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_EMPTY, KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }
                else if (noteTextLength > noteTextMaxLength) {
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.HOLD_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_TOO_LONG, KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }

                try {
                    performHold(batchId, holdNoteText, universalUser);
                    actionStatus = true;
                    message = PdpKeyConstants.BatchConstants.Messages.BATCH_SUCCESSFULLY_HOLD;
                }
                catch (PdpException pdpe) {
                    actionStatus = false;
                    message = pdpe.getMessage();
                }
            }
        }

        String returnUrl = buildUrl(batchId, actionStatus, message);
        return new ActionForward(returnUrl, true);
    }

    /**
     * This method holds a batch
     * @param batchIdString
     * @param holdNote
     * @param user
     * @throws PdpException
     */
    private void performHold(String batchIdString, String holdNote, UniversalUser user) throws PdpException {
        try {
            Integer batchId = Integer.parseInt(batchIdString);
            batchMaintenanceService.holdPendingBatch(batchId, holdNote, user);
        }
        catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError(PdpPropertyConstants.BatchConstants.Fields.BATCH_ID, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_ID_IS_NOT_NUMERIC);
        }

    }

    /**
     * This method confirms remove hold action
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndRemoveHold(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String removeHoldNoteText = KFSConstants.EMPTY_STRING;
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        boolean actionStatus;
        String message = KFSConstants.EMPTY_STRING;

        String batchId = request.getParameter(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM);
        if (batchId == null) {
            batchId = request.getParameter(KNSConstants.QUESTION_CONTEXT);
        }

        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();
        String confirmationText = kualiConfiguration.getPropertyString(PdpKeyConstants.BatchConstants.Confirmation.REMOVE_HOLD_BATCH_MESSAGE);
        confirmationText = MessageFormat.format(confirmationText, batchId);

        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.REMOVE_HOLD_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, "confirmHold", batchId);
        }
        else {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((PdpKeyConstants.BatchConstants.Confirmation.REMOVE_HOLD_BATCH_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                actionStatus = false;
            }
            else {
                removeHoldNoteText = reason;
                int noteTextLength = (reason == null) ? 0 : removeHoldNoteText.length();
                int noteTextMaxLength = PdpKeyConstants.BatchConstants.Confirmation.NOTE_TEXT_MAX_LENGTH;

                if (StringUtils.isBlank(reason)) {

                    if (reason == null) {
                        // prevent a NPE by setting the reason to a blank string
                        reason = KFSConstants.EMPTY_STRING;
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.REMOVE_HOLD_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_EMPTY, KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }
                else if (noteTextLength > noteTextMaxLength) {
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.REMOVE_HOLD_BATCH_QUESTION, confirmationText, KNSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_TOO_LONG, KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }

                try {
                    performRemoveHold(batchId, removeHoldNoteText, universalUser);
                    actionStatus = true;
                    message = PdpKeyConstants.BatchConstants.Messages.HOLD_SUCCESSFULLY_REMOVED_ON_BATCH;

                }
                catch (PdpException pdpe) {

                    actionStatus = false;
                    message = pdpe.getMessage();
                }
            }
        }

        String returnUrl = buildUrl(batchId, actionStatus, message);
        return new ActionForward(returnUrl, true);
    }

    /**
     * This method removes a batch hold.
     * @param batchIdString
     * @param changeText
     * @param user
     * @throws PdpException
     */
    private void performRemoveHold(String batchIdString, String changeText, UniversalUser user) throws PdpException {
        try {
            Integer batchId = Integer.parseInt(batchIdString);
            batchMaintenanceService.removeBatchHold(batchId, changeText, user);
        }
        catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError(PdpPropertyConstants.BatchConstants.Fields.BATCH_ID, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_ID_IS_NOT_NUMERIC);
        }

    }

    /**
     * This method builds the forward url.
     * @param batchId the batch id
     * @param success action status: true if success, false otherwise
     * @param message the message for the user
     * @return the build url
     */
    private String buildUrl(String batchId, boolean success, String message) {
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KNSConstants.DOC_FORM_KEY, "88888888");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Batch.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(PdpPropertyConstants.BatchConstants.Fields.BATCH_ID, batchId);
        parameters.put(PdpParameterConstants.BatchConstants.ACTION_SUCCESSFUL_PARAM, String.valueOf(success));
        if (message != null && !message.equalsIgnoreCase(KFSConstants.EMPTY_STRING)) {
            parameters.put(PdpParameterConstants.BatchConstants.MESSAGE_PARAM, message);
        }

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);

        return lookupUrl;
    }

    /**
     * This method gets the batch maintenance service.
     * @return the BatchMaintenanceService
     */
    public BatchMaintenanceService getBatchMaintenanceService() {
        return batchMaintenanceService;
    }

    /**
     * This method sets the batch maintenance service.
     * @param batchMaintenanceService
     */
    public void setBatchMaintenanceService(BatchMaintenanceService batchMaintenanceService) {
        this.batchMaintenanceService = batchMaintenanceService;
    }

}
