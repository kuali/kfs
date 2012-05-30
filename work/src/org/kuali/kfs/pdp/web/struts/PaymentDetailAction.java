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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.lookup.PaymentDetailLookupableHelperService;
import org.kuali.kfs.pdp.service.PaymentMaintenanceService;
import org.kuali.kfs.pdp.util.PdpPaymentDetailQuestionCallback;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class defines actions for Payment
 */
public class PaymentDetailAction extends KualiAction {

    private PaymentMaintenanceService paymentMaintenanceService;
    private BusinessObjectService businessObjectService;

    /**
     * Constructs a PaymentDetailAction.java.
     */
    public PaymentDetailAction() {
        setPaymentMaintenanceService(SpringContext.getBean(PaymentMaintenanceService.class));
        setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));

    }

    /**
     * This method confirms and cancels a payment.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performCancel(paymentDetailId, changeText, user);
            }
        };

        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_PAYMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_PAYMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.PAYMENT_SUCCESSFULLY_CANCELED, "confirmAndCancel", callback);
    }

    /**
     * This method cancels a payment.
     * @param paymentDetailId the payment detail id.
     * @param changeText the text of the change
     * @param user the user that perfomed the change
     * @return true if payment successfully canceled, false otherwise
     */
    private boolean performCancel(int paymentDetailId, String changeText, Person user) {

        Map keyMap = new HashMap();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findByPrimaryKey(PaymentDetail.class, keyMap);
        if (ObjectUtils.isNotNull(paymentDetail)) {
            int paymentGroupId = paymentDetail.getPaymentGroupId().intValue();
            return paymentMaintenanceService.cancelPendingPayment(paymentGroupId, paymentDetailId, changeText, user);
        }
        else {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }
    }

    /**
     * This method confirms and holds a payment
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndHold(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performHold(paymentDetailId, changeText, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.HOLD_PAYMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.HOLD_PAYMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.PAYMENT_SUCCESSFULLY_HOLD, "confirmAndHold", callback);
    }

    /**

     */
    /**
     * This method performs a hold on a payment.
     * @param paymentDetailId the payment detail id
     * @param changeText the text of the user change
     * @param user the user that performed the change
     * @return true if payment successfully held, false otherwise
     */
    private boolean performHold(int paymentDetailId, String changeText, Person user) {

        Map keyMap = new HashMap();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findByPrimaryKey(PaymentDetail.class, keyMap);
        if (ObjectUtils.isNotNull(paymentDetail)) {
            int paymentGroupId = paymentDetail.getPaymentGroupId().intValue();
            return paymentMaintenanceService.holdPendingPayment(paymentGroupId, changeText, user);
        }
        else {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }
    }

    /**
     * This method confirms and removes a hold on a payment.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return an ActionForward
     * @throws Exception
     */
    public ActionForward confirmAndRemoveHold(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performRemoveHold(paymentDetailId, changeText, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.REMOVE_HOLD_PAYMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.REMOVE_HOLD_PAYMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.HOLD_SUCCESSFULLY_REMOVED_ON_PAYMENT, "confirmAndRemoveHold", callback);
    }

    /**
     * This method removes a hold on payment.
     * @param paymentDetailId the payment detail id
     * @param changeText the text of the user change
     * @param user the user that performs the change
     * @return true if hold successfully removed from payment, false otherwise
     */
    private boolean performRemoveHold(int paymentDetailId, String changeText, Person user) {

        Map keyMap = new HashMap();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findByPrimaryKey(PaymentDetail.class, keyMap);
        if (ObjectUtils.isNotNull(paymentDetail)) {
            int paymentGroupId = paymentDetail.getPaymentGroupId().intValue();
            return paymentMaintenanceService.removeHoldPendingPayment(paymentGroupId, changeText, user);
        }
        else {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }
    }

    /**
     * This method confirms and sets the immediate flag.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return an ActionForward
     * @throws Exception
     */
    public ActionForward confirmAndSetImmediate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performSetImmediate(paymentDetailId, changeText, user);

            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.CHANGE_IMMEDIATE_PAYMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.CHANGE_IMMEDIATE_PAYMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.PAYMENT_SUCCESSFULLY_SET_AS_IMMEDIATE, "confirmAndSetImmediate", callback);
    }

    /**
     * This method sets the immediate flag
     * @param paymentDetailId the payment detail id
     * @param changeText the text of the change
     * @param user the user that performed the change
     * @return true if flag successfully set on payment, false otherwise
     */
    private boolean performSetImmediate(int paymentDetailId, String changeText, Person user) {
        Map keyMap = new HashMap();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findByPrimaryKey(PaymentDetail.class, keyMap);
        if (ObjectUtils.isNotNull(paymentDetail)) {
            int paymentGroupId = paymentDetail.getPaymentGroupId().intValue();
            paymentMaintenanceService.changeImmediateFlag(paymentGroupId, changeText, user);
            return true;
        }
        else {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

    }

    /**
     * This method confirms and removes the immediate flag.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndRemoveImmediate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performSetImmediate(paymentDetailId, changeText, user);

            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.CHANGE_IMMEDIATE_PAYMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.CHANGE_IMMEDIATE_PAYMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.IMMEDIATE_SUCCESSFULLY_REMOVED_ON_PAYMENT, "confirmAndRemoveImmediate", callback);
    }

    /**
     * This method confirms and cancels a disbursement.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return an ActionForward
     * @throws Exception
     */
    public ActionForward confirmAndCancelDisbursement(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performCancelDisbursement(paymentDetailId, changeText, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_DISBURSEMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_DISBURSEMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.DISBURSEMENT_SUCCESSFULLY_CANCELED, "confirmAndRemoveHold", callback);
    }

    /**
     * This method cancels a disbursement
     * @param paymentDetailId the payment detail id
     * @param changeText the text entered by user
     * @param user the user that canceled the disbursement
     * @return true if disbursement successfully canceled, false otherwise
     */
    private boolean performCancelDisbursement(int paymentDetailId, String changeText, Person user) {
        Map keyMap = new HashMap();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findByPrimaryKey(PaymentDetail.class, keyMap);
        if (ObjectUtils.isNotNull(paymentDetail)) {
            int paymentGroupId = paymentDetail.getPaymentGroupId().intValue();
            return paymentMaintenanceService.cancelDisbursement(paymentGroupId, paymentDetailId, changeText, user);
        }
        else {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }
    }

    /**
     * This method confirms an reissues/cancels a disbursement.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndReIssue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performReIssueWithoutCancelDisbursement(paymentDetailId, changeText, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_REISSUE_DISBURSEMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.REISSUE_DISBURSEMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.DISBURSEMENT_SUCCESSFULLY_REISSUED, "confirmAndReissue", callback);
    }

    /**
     * This method reissue a disbursement
     * @param paymentDetailId the payment detail id
     * @param changeText the text entered by the user
     * @param user the user that canceled the disbursement
     * @return true if disbursement successfully reissued/canceled, false otherwise
     */
    private boolean performReIssueWithoutCancelDisbursement(int paymentDetailId, String changeText, Person user) {
        Map keyMap = new HashMap();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findByPrimaryKey(PaymentDetail.class, keyMap);
        if (ObjectUtils.isNotNull(paymentDetail)) {
            int paymentGroupId = paymentDetail.getPaymentGroupId().intValue();
            return paymentMaintenanceService.reissueDisbursement(paymentGroupId, changeText, user);
        }
        else {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }
    }

    /**
     * This method confirms an reissues/cancels a disbursement.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmAndReIssueCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PdpPaymentDetailQuestionCallback callback = new PdpPaymentDetailQuestionCallback() {
            public boolean doPostQuestion(int paymentDetailId, String changeText, Person user) {
                return performReIssueDisbursement(paymentDetailId, changeText, user);
            }
        };
        return askQuestionWithInput(mapping, form, request, response, PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_REISSUE_DISBURSEMENT_QUESTION, PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_REISSUE_DISBURSEMENT_MESSAGE, PdpKeyConstants.PaymentDetail.Messages.DISBURSEMENT_SUCCESSFULLY_CANCELED, "confirmAndReissueCancel", callback);
    }

    /**
     * This method reissue/cancels a disbursement
     * @param paymentDetailId the payment detail id
     * @param changeText the text entered by the user
     * @param user the user that canceled the disbursement
     * @return true if disbursement successfully reissued/canceled, false otherwise
     */
    private boolean performReIssueDisbursement(int paymentDetailId, String changeText, Person user) {
        Map keyMap = new HashMap();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findByPrimaryKey(PaymentDetail.class, keyMap);
        if (ObjectUtils.isNotNull(paymentDetail)) {
            int paymentGroupId = paymentDetail.getPaymentGroupId().intValue();
            return paymentMaintenanceService.cancelReissueDisbursement(paymentGroupId, changeText, user);
        }
        else {
            GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }
    }

    /**
     * This method prompts for a reason to perform an action on a payment detail.
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
    private ActionForward askQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String confirmationQuestion, String confirmationText, String successMessage, String caller, PdpPaymentDetailQuestionCallback callback) throws Exception {
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = KFSConstants.EMPTY_STRING;
        Person person = GlobalVariables.getUserSession().getPerson();
        boolean actionStatus;
        String message = KFSConstants.EMPTY_STRING;

        String paymentDetailId = request.getParameter(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM);
        if (paymentDetailId == null) {
            paymentDetailId = request.getParameter(KRADConstants.QUESTION_CONTEXT);
        }

        PaymentDetail paymentDetail = (PaymentDetail) businessObjectService.findBySinglePrimaryKey(PaymentDetail.class, paymentDetailId);
        PaymentGroup paymentGroup = paymentDetail.getPaymentGroup();
        int paymentsInGroup = paymentGroup.getPaymentDetails().size() - 1;
        int paymentsInDisbursement = paymentDetail.getNbrOfPaymentsInDisbursement() - 1;

        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
        String confirmationTextValue = kualiConfiguration.getPropertyValueAsString(confirmationText);

        if (confirmationText.equals(PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_PAYMENT_MESSAGE)) {
            confirmationText = MessageFormat.format(confirmationTextValue, paymentsInGroup, paymentGroup.getId().toString());
            if (paymentsInGroup == 0) {
                int start = confirmationText.indexOf(".") + 2;
                confirmationText = confirmationText.substring(start);
            }
        }
        else if (confirmationText.equals(PdpKeyConstants.PaymentDetail.Confirmation.CANCEL_DISBURSEMENT_MESSAGE)) {
            confirmationText = MessageFormat.format(confirmationTextValue, paymentsInDisbursement, paymentGroup.getDisbursementNbr().toString());
            if (paymentsInDisbursement == 0) {
                int start = confirmationText.indexOf(".") + 2;
                confirmationText = confirmationText.substring(start);
            }

        }
        else {
            confirmationText = MessageFormat.format(confirmationTextValue, paymentDetailId);
        }

        if (question == null) {

            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, confirmationQuestion, confirmationText, KRADConstants.CONFIRMATION_QUESTION, caller, paymentDetailId);
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
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, confirmationQuestion, confirmationText, KRADConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, paymentDetailId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_EMPTY, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }
                else if (noteTextLength > noteTextMaxLength) {
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, confirmationQuestion, confirmationText, KRADConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_BASIC, paymentDetailId, reason, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOTE_TOO_LONG, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, "");
                }

                actionStatus = callback.doPostQuestion(Integer.parseInt(paymentDetailId), noteText, person);
                if (actionStatus) {
                    message = successMessage;
                }

            }
        }

        String returnUrl = buildUrl(paymentDetailId, actionStatus, message, buildErrorMesageKeyList());
        return new ActionForward(returnUrl, true);
    }

    /**
     * This method builds the forward url.
     * 
     * @param paymentDetailId the payment detail id
     * @param success action status: true if success, false otherwise
     * @param message the message for the user
     * @return the build url
     */
    private String buildUrl(String paymentDetailId, boolean success, String message, String errorList) {
        // build url to return user back to PaymentDetail Search with fields filled in as user inputted
        String lookupUrl = (String) GlobalVariables.getUserSession().retrieveObject(PaymentDetailLookupableHelperService.PDP_PAYMENTDETAIL_KEY);
        if (lookupUrl != null) {
            GlobalVariables.getUserSession().removeObject(PaymentDetailLookupableHelperService.PDP_PAYMENTDETAIL_KEY);

            Properties parameters = new Properties();
            parameters.put(PdpParameterConstants.ACTION_SUCCESSFUL_PARAM, String.valueOf(success));
            if (message != null && !message.equalsIgnoreCase(KFSConstants.EMPTY_STRING)) {
                parameters.put(PdpParameterConstants.MESSAGE_PARAM, message);
            }

            if (StringUtils.isNotEmpty(errorList)) {
                parameters.put(PdpParameterConstants.ERROR_KEY_LIST_PARAM, errorList);
            }

            lookupUrl = lookupUrl + UrlFactory.parameterizeUrl("&", parameters);
        }
        else {
            // session expired -  resort to alternative plan
            String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            parameters.put(KRADConstants.DOC_FORM_KEY, "88888888");
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
            parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);
            parameters.put(PdpParameterConstants.ACTION_SUCCESSFUL_PARAM, String.valueOf(success));
            if (message != null && !message.equalsIgnoreCase(KFSConstants.EMPTY_STRING)) {
                parameters.put(PdpParameterConstants.MESSAGE_PARAM, message);
            }

            if (StringUtils.isNotEmpty(errorList)) {
                parameters.put(PdpParameterConstants.ERROR_KEY_LIST_PARAM, errorList);
            }

            lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);
        }
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
     * This method gets the payment maintenance service
     * @return the paymentMaintenanceService
     */
    public PaymentMaintenanceService getPaymentMaintenanceService() {
        return paymentMaintenanceService;
    }

    /**
     * This method sets the payment maintenance service
     * @param paymentMaintenanceService
     */
    public void setPaymentMaintenanceService(PaymentMaintenanceService paymentMaintenanceService) {
        this.paymentMaintenanceService = paymentMaintenanceService;
    }

    /**
     * This method gets the business object service
     * @return the businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the business object service.
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
