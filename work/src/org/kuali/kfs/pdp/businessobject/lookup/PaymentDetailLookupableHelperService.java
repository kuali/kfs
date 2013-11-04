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
package org.kuali.kfs.pdp.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.service.PdpAuthorizationService;
import org.kuali.kfs.pdp.service.ResearchParticipantPaymentValidationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class PaymentDetailLookupableHelperService extends KualiLookupableHelperServiceImpl {
    public static final String PDP_PAYMENTDETAIL_KEY = "PDPHOLDKEY";
    private ConfigurationService kualiConfigurationService;
    private PdpAuthorizationService pdpAuthorizationService;

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        Map parameters = super.getParameters();
        String errorList;

        if (parameters.containsKey(PdpParameterConstants.ACTION_SUCCESSFUL_PARAM)) {
            String[] actionSuccessRequestParm = (String[]) parameters.get(PdpParameterConstants.ACTION_SUCCESSFUL_PARAM);
            Boolean actionSuccess = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(actionSuccessRequestParm[0]);

            if (actionSuccess != null) {

                if (!actionSuccess) {

                    // if the action performed on payment was not successful we get the error message list and add them to
                    // GlobalVariables errorMap
                    if (parameters.containsKey(PdpParameterConstants.ERROR_KEY_LIST_PARAM)) {
                        String[] errorListParam = (String[]) parameters.get(PdpParameterConstants.ERROR_KEY_LIST_PARAM);
                        errorList = errorListParam[0];
                        if (StringUtils.isNotEmpty(errorList)) {
                            String[] errorMsgs = StringUtils.split(errorList, PdpParameterConstants.ERROR_KEY_LIST_SEPARATOR);
                            for (String error : errorMsgs) {
                                if (StringUtils.isNotEmpty(error)) {
                                    GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, error);
                                }
                            }
                        }
                    }
                }
                else {
                    if (parameters.containsKey(PdpParameterConstants.MESSAGE_PARAM)) {
                        String[] messageRequestParm = (String[]) parameters.get(PdpParameterConstants.MESSAGE_PARAM);
                        String message = messageRequestParm[0];
                        KNSGlobalVariables.getMessageList().add(message);
                    }
                }
            }
        }

        List paymentDetailsFromPaymentGroupHistoryList = new ArrayList();
        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER)) {
            String disbursementNumberValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER);
            if (!StringUtils.isEmpty(disbursementNumberValue)) {
                List resultsForPaymentGroupHistory = searchForPaymentGroupHistory(fieldValues);
                paymentDetailsFromPaymentGroupHistoryList = getPaymentDetailsFromPaymentGroupHistoryList(resultsForPaymentGroupHistory);
            }
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE)) {
            String paymentStatusCodeValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE);
            if (!StringUtils.isEmpty(paymentStatusCodeValue) && paymentStatusCodeValue.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_ALL)) {
                paymentStatusCodeValue = PdpConstants.PaymentStatusCodes.HELD_TAX_ALL_FOR_SEARCH;
                fieldValues.put(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE, paymentStatusCodeValue);
            }
        }

        List searchResults = super.getSearchResults(fieldValues);

        searchResults.addAll(paymentDetailsFromPaymentGroupHistoryList);

        sortResultListByPayeeName(searchResults);

        return searchResults;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        // get field values
        String custPaymentDocNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER);
        String invoiceNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER);
        String purchaseOrderNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER);
        String processIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PROCESS_ID);
        String paymentIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_ID);
        String payeeNameValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_NAME);
        String payeeIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID);
        String payeeIdTypeCdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID_TYPE_CODE);
        String disbursementTypeCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_TYPE_CODE);
        String paymentStatusCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE);
        String netPaymentAmountValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT);
        String disbursementDateValueLower = (String) fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_DATE);
        String disbursementDateValueUpper = (String) fieldValues.get(KRADConstants.LOOKUP_DEFAULT_RANGE_SEARCH_UPPER_BOUND_LABEL + PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_DATE);
        String paymentDateValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DATE);
        String disbursementNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER);
        String chartCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_CHART_CODE);
        String orgCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_UNIT_CODE);
        String subUnitCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE);
        String requisitionNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_REQUISITION_NUMBER);
        String customerInstitutionNumberValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_INSTITUTION_NUMBER);
        String pymtAttachmentValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_ATTACHMENT);
        String processImmediateValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_INSTITUTION_NUMBER);
        String pymtSpecialHandlingValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_SPECIAL_HANDLING);
        String batchIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP_BATCH_ID);
        String paymentGroupIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DETAIL_PAYMENT_GROUP_ID);

        if ((StringUtils.isEmpty(custPaymentDocNbrValue)) && (StringUtils.isEmpty(invoiceNbrValue)) && (StringUtils.isEmpty(purchaseOrderNbrValue)) && (StringUtils.isEmpty(processIdValue)) && (StringUtils.isEmpty(paymentIdValue)) && (StringUtils.isEmpty(payeeNameValue)) && (StringUtils.isEmpty(payeeIdValue)) && (StringUtils.isEmpty(payeeIdTypeCdValue)) && (StringUtils.isEmpty(disbursementTypeCodeValue)) && (StringUtils.isEmpty(paymentStatusCodeValue)) && (StringUtils.isEmpty(netPaymentAmountValue)) &&
                (StringUtils.isEmpty(disbursementDateValueLower)) && (StringUtils.isEmpty(disbursementDateValueUpper)) && (StringUtils.isEmpty(paymentDateValue)) && (StringUtils.isEmpty(disbursementNbrValue)) && (StringUtils.isEmpty(chartCodeValue)) && (StringUtils.isEmpty(orgCodeValue)) && (StringUtils.isEmpty(subUnitCodeValue)) && (StringUtils.isEmpty(requisitionNbrValue)) && (StringUtils.isEmpty(customerInstitutionNumberValue)) && (StringUtils.isEmpty(pymtAttachmentValue)) && (StringUtils.isEmpty(processImmediateValue))
                && (StringUtils.isEmpty(pymtSpecialHandlingValue)) && (StringUtils.isEmpty(batchIdValue)) && (StringUtils.isEmpty(paymentGroupIdValue))) {

            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_HEADER_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_CRITERIA_NOT_ENTERED);
        }
        else {
            if ((StringUtils.isNotEmpty(payeeIdValue)) && (StringUtils.isEmpty(payeeIdTypeCdValue))) {
                GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID_TYPE_CODE, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_PAYEE_ID_TYPE_CODE_NULL_WITH_PAYEE_ID);
            }
            if ((StringUtils.isEmpty(payeeIdValue)) && (StringUtils.isNotEmpty(payeeIdTypeCdValue))) {
                GlobalVariables.getMessageMap().putError(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_PAYEE_ID_NULL_WITH_PAYEE_ID_TYPE_CODE);
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }

        buildAndStoreReturnUrl(fieldValues);
     }

    /**
     * Create a return URL for hold, cancel,set as and immediate keys to return back.  - the return url is stored and then retreived in PaymentDetailAction buildURL
     *
     * @param fieldValues
     */
    protected void buildAndStoreReturnUrl(Map<String, String> fieldValues) {
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KRADConstants.DOC_FORM_KEY, "88888888");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.putAll(fieldValues);
        Object lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);
        GlobalVariables.getUserSession().addObject(PDP_PAYMENTDETAIL_KEY,  lookupUrl);
    }


    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if (businessObject instanceof PaymentDetail) {
            Person person = GlobalVariables.getUserSession().getPerson();
            PaymentDetail paymentDetail = (PaymentDetail) businessObject;
            Integer paymentDetailId = paymentDetail.getId().intValue();
            String paymentDetailStatus = paymentDetail.getPaymentGroup().getPaymentStatusCode();
            List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
            String linkText = KFSConstants.EMPTY_STRING;
            String url = KFSConstants.EMPTY_STRING;
            String basePath = kualiConfigurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/" + PdpConstants.Actions.PAYMENT_DETAIL_ACTION;

            boolean showCancel = paymentDetailStatus != null && ((paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.OPEN) && pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId())) || (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_CD) && pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId())) || ((paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD) || paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD) || paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD)) && pdpAuthorizationService.hasRemovePaymentTaxHoldPermission(person.getPrincipalId())));

            if (showCancel) {
                Properties params = new Properties();

                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_CANCEL_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));

                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.CANCEL_PAYMENT);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_CANCEL_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);
            }

            boolean showHold = paymentDetailStatus != null && (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.OPEN) && pdpAuthorizationService.hasHoldPaymentPermission(person.getPrincipalId()));
            if (showHold) {
                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_HOLD_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.HOLD_PAYMENT);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_HOLD_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);

            }
            boolean showRemoveTaxHold = paymentDetailStatus != null && (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD) && pdpAuthorizationService.hasRemovePaymentTaxHoldPermission(person.getPrincipalId()));
            if (showRemoveTaxHold) {
                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.REMOVE_HTXN_HOLD);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);
            }

            boolean showRemoveImmediatePrint = paymentDetailStatus != null && (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.OPEN) && pdpAuthorizationService.hasSetAsImmediatePayPermission(person.getPrincipalId()) && paymentDetail.getPaymentGroup().getProcessImmediate());

            if (showRemoveImmediatePrint) {

                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_REMOVE_IMMEDIATE_PRINT_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.REMOVE_IMMEDIATE_PRINT);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_REMOVE_IMMEDIATE_PRINT_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);

            }

            boolean showSetImmediatePrint = paymentDetailStatus != null && (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.OPEN) && pdpAuthorizationService.hasSetAsImmediatePayPermission(person.getPrincipalId()) && !paymentDetail.getPaymentGroup().getProcessImmediate());

            if (showSetImmediatePrint) {

                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_SET_IMMEDIATE_PRINT_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.SET_IMMEDIATE_PRINT);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_SET_IMMEDIATE_PRINT_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);

            }

            boolean showRemoveHold = paymentDetailStatus != null && ((paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_CD) && pdpAuthorizationService.hasHoldPaymentPermission(person.getPrincipalId())) || ((paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD) || paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD)) && pdpAuthorizationService.hasRemovePaymentTaxHoldPermission(person.getPrincipalId())));

            if (showRemoveHold) {

                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.REMOVE_PAYMENT_HOLD);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);

            }

            boolean showDisbursementCancel = paymentDetailStatus != null && ((paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.PENDING_ACH) && (pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId()))) || (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.EXTRACTED) && pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId()) && paymentDetail.getPaymentGroup().getDisbursementDate() != null && paymentDetail.isDisbursementActionAllowed()));

            if (showDisbursementCancel) {

                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_DISBURSEMENT_CANCEL_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.CANCEL_DISBURSEMENT);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_DISBURSEMENT_CANCEL_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);

            }

            boolean showReissue = paymentDetailStatus != null && (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT) && (pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId()) && paymentDetail.isDisbursementActionAllowed()));
            if (showReissue) {
                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_REISSUE_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.REISSUE);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_REISSUE_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);
            }

            boolean showReissueCancel = paymentDetailStatus != null && ((paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.PENDING_ACH) && (pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId()))) || (paymentDetailStatus.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.EXTRACTED) && pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId()) && paymentDetail.getPaymentGroup().getDisbursementDate() != null && paymentDetail.isDisbursementActionAllowed()));

            if (showReissueCancel) {

                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_REISSUE_CANCEL_ACTION);
                params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetailId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.PaymentDetail.LinkText.REISSUE_CANCEL);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_REISSUE_CANCEL_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);

            }

            if (anchorHtmlDataList.isEmpty()) {
                AnchorHtmlData anchorHtmlData = new AnchorHtmlData("&nbsp;", "", "");
                anchorHtmlDataList.add(anchorHtmlData);
            }
            return anchorHtmlDataList;
        }
        return super.getEmptyActionUrls();
    }

    /**
     * This method builds the search fields for PaymentGroupHistory.
     *
     * @param fieldValues entry fields from PaymentDetail
     * @return the fields map
     */
    private Map<String, String> buildSearchFieldsMapForPaymentGroupHistory(Map<String, String> fieldValues) {
        Map resultMap = new Properties();
        String fieldValue = KFSConstants.EMPTY_STRING;

        //TODO removed
//        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_INSTITUTION_NUMBER)) {
//            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_INSTITUTION_NUMBER);
//            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_CUSTOMER_INSTITUTION_NUMBER, fieldValue);
//        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_NAME)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_NAME);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYEE_NAME, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYEE_ID, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_ATTACHMENT)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_ATTACHMENT);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYMENT_ATTACHMENT, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_SPECIAL_HANDLING)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_SPECIAL_HANDLING);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_ORIGIN_PAYMENT_SPECIAL_HANDLING, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_PROCESS_IMEDIATE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PROCESS_IMEDIATE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_ORIGIN_PROCESS_IMMEDIATE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_ORIGIN_DISBURSEMENT_NUMBER, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_PROCESS_ID)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_PROCESS_ID);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYMENT_PROCESS_ID, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYMENT_DETAILS_NET_AMOUNT, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_DATE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_DATE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_ORIGIN_DISBURSE_DATE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_DATE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DATE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_ORIGIN_PAYMENT_DATE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_ORIGIN_PAYMENT_STATUS_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_TYPE_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_TYPE_CODE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_CHART_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_CHART_CODE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_CHART_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_UNIT_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_UNIT_CODE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_ORG_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE);
            resultMap.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_SUB_UNIT_CODE, fieldValue);
        }

        return resultMap;

    }

    /**
     * This method searches for PaymentGroupHistory
     *
     * @param fieldValues search field values
     * @return the list of PaymentGroupHistory
     */
    private List searchForPaymentGroupHistory(Map<String, String> fieldValues) {
        List resultsForPaymentGroupHistory = new ArrayList();
        Map fieldsForPaymentGroupHistory = buildSearchFieldsMapForPaymentGroupHistory(fieldValues);
        resultsForPaymentGroupHistory = (List) getLookupService().findCollectionBySearchHelper(PaymentGroupHistory.class, fieldsForPaymentGroupHistory, false);
        return resultsForPaymentGroupHistory;
    }

    /**
     * This method gets the PaymentDetails for the given list og PaymentGroupHistory
     *
     * @param resultsForPaymentGroupHistory the list of PaymentGoupHistory objects
     * @return the list of PaymentDetails
     */
    private List getPaymentDetailsFromPaymentGroupHistoryList(List resultsForPaymentGroupHistory) {
        List finalResults = new ArrayList();
        for (Iterator iter = resultsForPaymentGroupHistory.iterator(); iter.hasNext();) {
            PaymentGroupHistory pgh = (PaymentGroupHistory) iter.next();
            finalResults.addAll(pgh.getPaymentGroup().getPaymentDetails());
        }
        return finalResults;
    }

    /**
     * This method sorts the given list by payee name
     *
     * @param searchResults the list to be sorted
     */
    protected void sortResultListByPayeeName(List searchResults) {
        Collections.sort(searchResults, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                PaymentDetail detail1 = (org.kuali.kfs.pdp.businessobject.PaymentDetail) o1;
                PaymentDetail detail2 = (org.kuali.kfs.pdp.businessobject.PaymentDetail) o2;

                if (detail1 == null || detail1.getPaymentGroup() == null || detail1.getPaymentGroup().getPayeeName() == null) {
                    return -1;
                }

                if (detail2 == null || detail2.getPaymentGroup() == null || detail2.getPaymentGroup().getPayeeName() == null) {
                    return 1;
                }

                return detail1.getPaymentGroup().getPayeeName().compareTo(detail2.getPaymentGroup().getPayeeName());
            }
        });
    }

    /**
     * Overrides superclass method so that we could mask the payee name field whenever the customer profile is
     * the one for Research Participant Upload and don't need to mask the payee name field for any other cases.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
     */
    @Override
    public Collection<? extends BusinessObject> performLookup(LookupForm lookupForm, Collection<ResultRow> resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupFormFields.get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupFormFields.get(KRADConstants.DOC_FORM_KEY));
        Collection<? extends BusinessObject> displayList;

        LookupUtils.preProcessRangeFields(lookupFormFields);

        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupFormFields);
        } else {
            displayList = getSearchResultsUnbounded(lookupFormFields);
        }

        boolean hasReturnableRow = false;

        List<String> returnKeys = getReturnKeys();
        List<String> pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        //Get and keep a copy of the original attributeSecurity of payeeName from the DD
        BusinessObjectEntry businessObjectEntry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(PaymentDetail.class.getName());
        AttributeDefinition attributeDefinition = businessObjectEntry.getAttributeDefinition("paymentGroup.payeeName");
        AttributeSecurity originalPayeeNameAttributeSecurity = attributeDefinition.getAttributeSecurity();

        // iterate through result list and wrap rows with return url and action
        // urls
        for (BusinessObject element : displayList) {
            BusinessObject baseElement = element;

            if(element instanceof PersistableBusinessObject){
                lookupForm.setLookupObjectId(((PersistableBusinessObject)element).getObjectId());
            }

            CustomerProfile customerProfile = ((PaymentDetail)element).getPaymentGroup().getBatch().getCustomerProfile();

            if (!(SpringContext.getBean(ResearchParticipantPaymentValidationService.class)).isResearchParticipantPayment(customerProfile)) {
                // This is not the case for the Research Upload, so we don't need to apply mask on the payee name.
                attributeDefinition.setAttributeSecurity(null);
            }
            else {
                // setting back the attribute security just as in DD so that we don't override it with null.
                attributeDefinition.setAttributeSecurity(originalPayeeNameAttributeSecurity);
            }

            final String lookupId = getLookupResultsService().getLookupId(baseElement);
            if (lookupId != null) {
                lookupForm.setLookupObjectId(lookupId);
            }

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService()
                    .getLookupResultRestrictions(element, user);

            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
            String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
            // Fix for JIRA - KFSMI-2417
            if ("".equals(actionUrls)) {
                actionUrls = ACTION_URLS_EMPTY;
            }

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue,
                        businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // if property value is masked, don't display additional or alternate properties, or allow totals
                if (StringUtils.equals(propValueBeforePotientalMasking, propValue)) {
                    if (StringUtils.isNotBlank(col.getAlternateDisplayPropertyName())) {
                        String alternatePropertyValue = ObjectUtils.getFormattedPropertyValue(element, col
                                .getAlternateDisplayPropertyName(), null);
                        col.setPropertyValue(alternatePropertyValue);
                    }

                    if (StringUtils.isNotBlank(col.getAdditionalDisplayPropertyName())) {
                        String additionalPropertyValue = ObjectUtils.getFormattedPropertyValue(element, col
                                .getAdditionalDisplayPropertyName(), null);
                        col.setPropertyValue(col.getPropertyValue() + " *-* " + additionalPropertyValue);
                    }
                } else {
                    col.setTotal(false);
                }

                if (col.isTotal()) {
                    Object unformattedPropValue = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                    col.setUnformattedPropertyValue(unformattedPropValue);
                }

                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
                }
            }

            ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            row.setReturnUrlHtmlData(returnUrl);

            // because of concerns of the BO being cached in session on the
            // ResultRow,
            // let's only attach it when needed (currently in the case of
            // export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }

            if (lookupId != null) {
                row.setObjectId(lookupId);
            }

            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * This method sets the pdpAuthorizationService attribute valuse
     *
     * @param pdpAuthorizationService The pdpAuthorizationService to set.
     */
    public void setPdpAuthorizationService(PdpAuthorizationService pdpAuthorizationService) {
        this.pdpAuthorizationService = pdpAuthorizationService;
    }

}
