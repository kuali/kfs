/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.pdp.web.struts;

import java.text.MessageFormat;
import java.util.List;
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
import org.kuali.kfs.pdp.service.BatchMaintenanceService;
import org.kuali.kfs.pdp.util.PdpBatchQuestionCallback;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class defines actions for Batch (cancel, hold, remove hold).
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
     * This method confirms and performs batch cancel.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PdpBatchQuestionCallback callback = new PdpBatchQuestionCallback() {
            public boolean doPostQuestion(String batchIdString, String cancelNote, Person user) {
                return performCancel(batchIdString, cancelNote, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.CANCEL_BATCH_QUESTION, PdpKeyConstants.BatchConstants.Confirmation.CANCEL_BATCH_MESSAGE, PdpKeyConstants.BatchConstants.Messages.BATCH_SUCCESSFULLY_CANCELED, "confirmAndCancel", callback);

    }

    /**
     * This method cancels a batch.
     * 
     * @param batchIdString a string representing the batch id
     * @param cancelNote the cancelation note entered by the user
     * @param user the current user
     */
    private boolean performCancel(String batchIdString, String cancelNote, Person user) {
        try {
            Integer batchId = Integer.parseInt(batchIdString);
            return batchMaintenanceService.cancelPendingBatch(batchId, cancelNote, user);
        }
        catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.BatchConstants.BATCH_ID, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_ID_IS_NOT_NUMERIC);
            return false;
        }

    }

    /**
     * This method confirms and performs batch hold.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndHold(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PdpBatchQuestionCallback callback = new PdpBatchQuestionCallback() {
            public boolean doPostQuestion(String batchIdString, String note, Person user) {
                return performHold(batchIdString, note, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.HOLD_BATCH_QUESTION, PdpKeyConstants.BatchConstants.Confirmation.HOLD_BATCH_MESSAGE, PdpKeyConstants.BatchConstants.Messages.BATCH_SUCCESSFULLY_HOLD, "confirmAndHold", callback);
    }

    /**
     * This method holds a batch
     * 
     * @param batchIdString
     * @param holdNote
     * @param user
     * @throws PdpException
     */
    private boolean performHold(String batchIdString, String holdNote, Person user) {
        try {
            Integer batchId = Integer.parseInt(batchIdString);
            return batchMaintenanceService.holdPendingBatch(batchId, holdNote, user);
        }
        catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.BatchConstants.BATCH_ID, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_ID_IS_NOT_NUMERIC);
            return false;
        }

    }

    /**
     * This method confirms and peforms remove hold on batch action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndRemoveHold(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PdpBatchQuestionCallback callback = new PdpBatchQuestionCallback() {
            public boolean doPostQuestion(String batchIdString, String note, Person user) {
                return performRemoveHold(batchIdString, note, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.BatchConstants.Confirmation.REMOVE_HOLD_BATCH_QUESTION, PdpKeyConstants.BatchConstants.Confirmation.REMOVE_HOLD_BATCH_MESSAGE, PdpKeyConstants.BatchConstants.Messages.HOLD_SUCCESSFULLY_REMOVED_ON_BATCH, "confirmAndRemoveHold", callback);
    }

    /**
     * This method removes a batch hold.
     * 
     * @param batchIdString
     * @param changeText
     * @param user
     * @throws PdpException
     */
    private boolean performRemoveHold(String batchIdString, String changeText, Person user) {
        try {
            Integer batchId = Integer.parseInt(batchIdString);
            return batchMaintenanceService.removeBatchHold(batchId, changeText, user);
        }
        catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.BatchConstants.BATCH_ID, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_ID_IS_NOT_NUMERIC);
            return false;
        }

    }

    /**
     * This method prompts for a reason to perfomr an action on a batch (cancel, hold, remove hold).
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param confirmationQuestion
     * @param confirmationText
     * @param caller
     * @param callback
     * @return
     * @throws Exception
     */
    private ActionForward askQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String confirmationQuestion, String confirmationText, String successMessage, String caller, PdpBatchQuestionCallback callback) throws Exception {
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = KFSConstants.EMPTY_STRING;
        Person person = GlobalVariables.getUserSession().getPerson();
        boolean actionStatus;
        String message = KFSConstants.EMPTY_STRING;

        String batchId = request.getParameter(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM);
        if (batchId == null) {
            batchId = request.getParameter(KRADConstants.QUESTION_CONTEXT);
        }

        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
        confirmationText = kualiConfiguration.getPropertyValueAsString(confirmationText);
        confirmationText = MessageFormat.format(confirmationText, batchId);

        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, confirmationQuestion, confirmationText, KRADConstants.CONFIRMATION_QUESTION, caller, batchId);
        }
        else {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if ((confirmationQuestion.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                actionStatus = false;
            }
            else {
                noteText = reason;
                int noteTextLength = (reason == null) ? 0 : noteText.length();
                int noteTextMaxLength = PdpKeyConstants.BatchConstants.Confirmation.NOTE_TEXT_MAX_LENGTH;

                if (StringUtils.isBlank(reason)) {

                    if (reason == null) {
                        // prevent a NPE by setting the reason to a blank string
                        reason = KFSConstants.EMPTY_STRING;
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, confirmationQuestion, confirmationText, KRADConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_EMPTY, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }
                else if (noteTextLength > noteTextMaxLength) {
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, confirmationQuestion, confirmationText, KRADConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, batchId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_TOO_LONG, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }

                actionStatus = callback.doPostQuestion(batchId, noteText, person);
                if (actionStatus) {
                    message = successMessage;
                }

            }
        }

        String returnUrl = buildUrl(batchId, actionStatus, message, buildErrorMesageKeyList());
        return new ActionForward(returnUrl, true);
    }

    /**
     * This method builds the forward url.
     * 
     * @param batchId the batch id
     * @param success action status: true if success, false otherwise
     * @param message the message for the user
     * @return the build url
     */
    private String buildUrl(String batchId, boolean success, String message, String errorList) {
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KRADConstants.DOC_FORM_KEY, "88888888");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Batch.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(PdpPropertyConstants.BatchConstants.BATCH_ID, batchId);
        parameters.put(PdpParameterConstants.ACTION_SUCCESSFUL_PARAM, String.valueOf(success));
        if (message != null && !message.equalsIgnoreCase(KFSConstants.EMPTY_STRING)) {
            parameters.put(PdpParameterConstants.MESSAGE_PARAM, message);
        }

        if (StringUtils.isNotEmpty(errorList)) {
            parameters.put(PdpParameterConstants.ERROR_KEY_LIST_PARAM, errorList);
        }

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);

        return lookupUrl;
    }

    /**
     * This method build a string list of error message keys out of the error map in GlobalVariables
     * 
     * @return a String representing the list of error message keys
     */
    private String buildErrorMesageKeyList() {
        MessageMap errorMap = GlobalVariables.getMessageMap();
        StringBuffer errorList = new StringBuffer();

        for (String errorKey : (List<String>) errorMap.getPropertiesWithErrors()) {
            for (ErrorMessage errorMessage : (List<ErrorMessage>) errorMap.getMessages(errorKey)) {

                errorList.append(errorMessage.getErrorKey());
                errorList.append(PdpParameterConstants.ERROR_KEY_LIST_SEPARATOR);
            }
        }
        if (errorList.length() > 0) {
            errorList.replace(errorList.lastIndexOf(PdpParameterConstants.ERROR_KEY_LIST_SEPARATOR), errorList.lastIndexOf(PdpParameterConstants.ERROR_KEY_LIST_SEPARATOR) + PdpParameterConstants.ERROR_KEY_LIST_SEPARATOR.length(), "");
        }

        return errorList.toString();
    }

    /**
     * This method gets the batch maintenance service.
     * 
     * @return the BatchMaintenanceService
     */
    public BatchMaintenanceService getBatchMaintenanceService() {
        return batchMaintenanceService;
    }

    /**
     * This method sets the batch maintenance service.
     * 
     * @param batchMaintenanceService
     */
    public void setBatchMaintenanceService(BatchMaintenanceService batchMaintenanceService) {
        this.batchMaintenanceService = batchMaintenanceService;
    }

}

