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
package org.kuali.kfs.pdp.businessobject.lookup;

import java.util.ArrayList;
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
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.lookup.AnchorHtmlBase;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

public class PaymentDetailLookupableHelperService extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        List paymentDetailsFromPaymentGroupHistoryList = new ArrayList();
        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_NUMBER)) {
            String disbursementNumberValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_NUMBER);
            if (!StringUtils.isEmpty(disbursementNumberValue)) {
                List resultsForPaymentGroupHistory = searchForPaymentGroupHistory(fieldValues);
                paymentDetailsFromPaymentGroupHistoryList = getPaymentDetailsFromPaymentGroupHistoryList(resultsForPaymentGroupHistory);
            }
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_STATUS_CODE)) {
            String paymentStatusCodeValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_STATUS_CODE);
            if (!StringUtils.isEmpty(paymentStatusCodeValue) && paymentStatusCodeValue.equalsIgnoreCase(PdpConstants.PaymentStatusCodes.HELD_TAX_ALL)) {
                paymentStatusCodeValue = PdpConstants.PaymentStatusCodes.HELD_TAX_ALL_FOR_SEARCH;
                fieldValues.put(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_STATUS_CODE, paymentStatusCodeValue);
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
        String custPaymentDocNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CUSTOMER_DOC_NUMBER);
        String invoiceNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_INVOICE_NUMBER);
        String purchaseOrderNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PURCHASE_ORDER_NUMBER);
        String processIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PROCESS_ID);
        String paymentIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ID);
        String payeeNameValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_NAME);
        String payeeIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_ID);
        String payeeIdTypeCdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_ID_TYPE_CODE);
        String disbursementTypeCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_TYPE_CODE);
        String paymentStatusCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_STATUS_CODE);
        String netPaymentAmountValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_NET_AMOUNT);
        String disbursementDateValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_DATE);
        String paymentDateValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DATE);
        String disbursementNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_NUMBER);
        String chartCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CHART_CODE);
        String orgCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ORG_CODE);
        String subUnitCodeValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_SUBUNIT_CODE);
        String requisitionNbrValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_REQUISITION_NUMBER);
        String customerInstitutionNumberValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CUSTOMER_INSTITUTION_NUMBER);
        String pymtAttachmentValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ATTACHMENT);
        String processImmediateValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CUSTOMER_INSTITUTION_NUMBER);
        String pymtSpecialHandlingValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_SPECIAL_HANDLING);
        String batchIdValue = (String) fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_GROUP_BATCH_ID);

        if ((StringUtils.isEmpty(custPaymentDocNbrValue)) && (StringUtils.isEmpty(invoiceNbrValue)) && (StringUtils.isEmpty(purchaseOrderNbrValue)) && (StringUtils.isEmpty(processIdValue)) && (StringUtils.isEmpty(paymentIdValue)) && (StringUtils.isEmpty(payeeNameValue)) && (StringUtils.isEmpty(payeeIdValue)) && (StringUtils.isEmpty(payeeIdTypeCdValue)) && (StringUtils.isEmpty(disbursementTypeCodeValue)) && (StringUtils.isEmpty(paymentStatusCodeValue)) && (StringUtils.isEmpty(netPaymentAmountValue)) && (StringUtils.isEmpty(disbursementDateValue)) && (StringUtils.isEmpty(paymentDateValue)) && (StringUtils.isEmpty(disbursementNbrValue)) && (StringUtils.isEmpty(chartCodeValue)) && (StringUtils.isEmpty(orgCodeValue)) && (StringUtils.isEmpty(subUnitCodeValue)) && (StringUtils.isEmpty(requisitionNbrValue)) && (StringUtils.isEmpty(customerInstitutionNumberValue)) && (StringUtils.isEmpty(pymtAttachmentValue)) && (StringUtils.isEmpty(processImmediateValue))
                && (StringUtils.isEmpty(pymtSpecialHandlingValue)) && (StringUtils.isEmpty(batchIdValue)) ) {

            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_HEADER_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_CRITERIA_NOT_ENTERED);
        }
        else {

            if ((StringUtils.isNotEmpty(payeeIdValue)) && (StringUtils.isEmpty(payeeIdTypeCdValue))) {
                GlobalVariables.getErrorMap().putError(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_ID_TYPE_CODE, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_PAYEE_ID_TYPE_CODE_NULL_WITH_PAYEE_ID);
            }
            if ((StringUtils.isEmpty(payeeIdValue)) && (StringUtils.isNotEmpty(payeeIdTypeCdValue))) {
                GlobalVariables.getErrorMap().putError(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_ID, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_PAYEE_ID_NULL_WITH_PAYEE_ID_TYPE_CODE);
            }

            if (StringUtils.isNotEmpty(custPaymentDocNbrValue)) {
                if (custPaymentDocNbrValue.length() < 2) {
                    GlobalVariables.getErrorMap().putError(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CUSTOMER_DOC_NUMBER, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_CUST_DOC_NBR_LESS_THAN_2_CHARS);
                }
            }
            if (StringUtils.isNotEmpty(invoiceNbrValue)) {
                if (invoiceNbrValue.length() < 2) {

                    GlobalVariables.getErrorMap().putError(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_INVOICE_NUMBER, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_INVOICE_NBR_LESS_THAN_2_CHARS);
                }
            }
            if (StringUtils.isNotEmpty(requisitionNbrValue)) {
                if (requisitionNbrValue.length() < 2) {
                    GlobalVariables.getErrorMap().putError(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_REQUISITION_NUMBER, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_REQUISITION_NBR_LESS_THAN_2_CHARS);
                }
            }
            if (StringUtils.isNotEmpty(purchaseOrderNbrValue)) {
                if (purchaseOrderNbrValue.length() < 2) {
                    GlobalVariables.getErrorMap().putError(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PURCHASE_ORDER_NUMBER, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_PURCHASE_ORDER_NBR_LESS_THAN_2_CHARS);
                }
            }
            if (StringUtils.isNotEmpty(payeeNameValue)) {
                if (payeeNameValue.length() < 2) {
                    GlobalVariables.getErrorMap().putError(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_NAME, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_DETAIL_PAYEE_NAME_LESS_THAN_2_CHARS);

                }
            }
        }
        
        if (!GlobalVariables.getErrorMap().isEmpty()) {
            throw new ValidationException("errors in search criteria");
        }

    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        return super.getEmptyActionUrls();
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
    @Override
    public AnchorHtmlBase getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlBase inquiryUrl = super.getInquiryUrl(bo, propertyName);

//        if (propertyName.equalsIgnoreCase(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CUSTOMER_DOC_NUMBER)) {
//            PaymentDetail paymentDetail = (PaymentDetail) bo;
//            Properties params = new Properties();
//            params.put(PdpParameterConstants.PaymentDetail.DETAIL_ID_PARAM, UrlFactory.encode(String.valueOf(paymentDetail.getId())));
//            String url = UrlFactory.parameterizeUrl(PdpConstants.Actions.PAYMENT_DETAIL_ACTION, params);
//            inquiryUrl.setHref(url);
//        }

        return inquiryUrl;
    }

    /**
     * This method builds the search fields for PaymentGroupHistory.
     * @param fieldValues entry fields from PaymentDetail
     * @return the fields map
     */
    private Map<String, String> buildSearchFieldsMapForPaymentGroupHistory(Map<String, String> fieldValues) {
        Map resultMap = new Properties();
        String fieldValue = KFSConstants.EMPTY_STRING;

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CUSTOMER_INSTITUTION_NUMBER)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CUSTOMER_INSTITUTION_NUMBER);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_CUSTOMER_INSTITUTION_NUMBER, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_NAME)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_NAME);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_PAYEE_NAME, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_ID)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PAYEE_ID);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_PAYEE_ID, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ATTACHMENT)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ATTACHMENT);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_PAYMENT_ATTACHMENT, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_SPECIAL_HANDLING)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_SPECIAL_HANDLING);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_ORIGIN_PAYMENT_SPECIAL_HANDLING, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PROCESS_IMEDIATE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PROCESS_IMEDIATE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_ORIGIN_PROCESS_IMMEDIATE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_NUMBER)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_NUMBER);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_ORIGIN_DISBURSEMENT_NUMBER, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PROCESS_ID)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_PROCESS_ID);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_PAYMENT_PROCESS_ID, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_NET_AMOUNT)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_NET_AMOUNT);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_PAYMENT_DETAILS_NET_AMOUNT, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_DATE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_DATE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_ORIGIN_DISBURSE_DATE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DATE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DATE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_ORIGIN_PAYMENT_DATE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_STATUS_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_STATUS_CODE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_ORIGIN_PAYMENT_STATUS_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_TYPE_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_DISBURSEMENT_TYPE_CODE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CHART_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_CHART_CODE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_CHART_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ORG_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ORG_CODE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_ORG_CODE, fieldValue);
        }

        if (fieldValues.containsKey(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_SUBUNIT_CODE)) {
            fieldValue = fieldValues.get(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_SUBUNIT_CODE);
            resultMap.put(PdpPropertyConstants.PaymenGroupHistory.Fields.PAYMENT_GROUP_SUB_UNIT_CODE, fieldValue);
        }

        return resultMap;

    }

    /**
     * This method searches for PaymentGroupHistory
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
     * @param searchResults the list to be sorted
     */
    private void sortResultListByPayeeName(List searchResults) {
        Collections.sort(searchResults, new Comparator() {
            public int compare(Object o1, Object o2) {
                return (((org.kuali.kfs.pdp.businessobject.PaymentDetail) o1).getPaymentGroup().getPayeeName()).compareTo(((org.kuali.kfs.pdp.businessobject.PaymentDetail) o2).getPaymentGroup().getPayeeName());
            }
        });
    }

}
