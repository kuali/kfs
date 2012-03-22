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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.service.BatchMaintenanceService;
import org.kuali.kfs.pdp.service.PdpAuthorizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class allows custom handling of Batches within the lookup framework.
 */
public class BatchLookupableHelperService extends KualiLookupableHelperServiceImpl {
    private BatchMaintenanceService batchMaintenanceService;
    private ConfigurationService configurationService;
    private LookupDao lookupDao;
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

                    // if the action performed on batch was not successful we get the error message list and add them to
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

        List results = super.getSearchResults(fieldValues);
        return results;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlData inquiryUrl = (AnchorHtmlData) super.getInquiryUrl(bo, propertyName);
        Batch batch = (Batch) bo;
        if (propertyName.equalsIgnoreCase(PdpPropertyConstants.BatchConstants.BATCH_ID)) {
            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP_BATCH_ID, UrlFactory.encode(String.valueOf(batch.getId())));
            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);
            inquiryUrl.setHref(url);
        }
        return inquiryUrl;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {

        if (businessObject instanceof Batch) {
            Person person = GlobalVariables.getUserSession().getPerson();
            Batch batch = (Batch) businessObject;
            Integer batchId = batch.getId().intValue();
            List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
            String linkText = KFSConstants.EMPTY_STRING;
            String url = KFSConstants.EMPTY_STRING;
            String basePath = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/" + PdpConstants.Actions.BATCH_SEARCH_DETAIL_ACTION;

            if ( pdpAuthorizationService.hasCancelPaymentPermission(person.getPrincipalId()) && batchMaintenanceService.doBatchPaymentsHaveOpenOrHeldStatus(batchId)) {

                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_CANCEL_ACTION);
                params.put(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM, UrlFactory.encode(String.valueOf(batchId)));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = configurationService.getPropertyValueAsString(PdpKeyConstants.BatchConstants.LinkText.CANCEL_BATCH);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_CANCEL_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);
            }
            
            if ( pdpAuthorizationService.hasHoldPaymentPermission(person.getPrincipalId())) {

                if (batchMaintenanceService.doBatchPaymentsHaveHeldStatus(batchId)) {

                    Properties params = new Properties();
                    params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION);
                    params.put(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM, UrlFactory.encode(String.valueOf(batchId)));
                    url = UrlFactory.parameterizeUrl(basePath, params);

                    linkText = configurationService.getPropertyValueAsString(PdpKeyConstants.BatchConstants.LinkText.REMOVE_BATCH_HOLD);

                    AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION, linkText);
                    anchorHtmlDataList.add(anchorHtmlData);
                }
                else if (batchMaintenanceService.doBatchPaymentsHaveOpenStatus(batchId)) {

                    Properties params = new Properties();
                    params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_HOLD_ACTION);
                    params.put(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM, UrlFactory.encode(String.valueOf(batchId)));
                    url = UrlFactory.parameterizeUrl(basePath, params);

                    linkText = configurationService.getPropertyValueAsString(PdpKeyConstants.BatchConstants.LinkText.HOLD_BATCH);
                    AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_HOLD_ACTION, linkText);
                    anchorHtmlDataList.add(anchorHtmlData);
                }
            }

            if (anchorHtmlDataList.isEmpty()) {
                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, "&nbsp;", "&nbsp;");
                anchorHtmlDataList.add(anchorHtmlData);
            }

            return anchorHtmlDataList;
        }
        return super.getEmptyActionUrls();
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        // call super method to check validation against DD
        super.validateSearchParameters(fieldValues);

        // get field values
        String batchIdValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.BATCH_ID);
        String paymentCountValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.PAYMENT_COUNT);
        String paymentTotalAmountValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.PAYMENT_TOTAL_AMOUNT);
        String fileCreationTimeValueLower = (String) fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME);
        String fileCreationTimeValueUpper = (String) fieldValues.get(KRADConstants.LOOKUP_DEFAULT_RANGE_SEARCH_UPPER_BOUND_LABEL + PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME);
        String chartCodeValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.CHART_CODE);
        String orgCodeValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.ORG_CODE);
        String subUnitCodeValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.SUB_UNIT_CODE);

        // check if there is any search criteria entered
        if (StringUtils.isBlank(batchIdValue) && StringUtils.isBlank(chartCodeValue) && StringUtils.isBlank(orgCodeValue) && StringUtils.isBlank(subUnitCodeValue) && StringUtils.isBlank(paymentCountValue) && StringUtils.isBlank(paymentTotalAmountValue) && StringUtils.isBlank(fileCreationTimeValueLower) && StringUtils.isBlank(fileCreationTimeValueUpper)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_HEADER_ERRORS, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_CRITERIA_NONE_ENTERED);
        }
        else if (StringUtils.isBlank(batchIdValue) && StringUtils.isBlank(paymentCountValue) && StringUtils.isBlank(paymentTotalAmountValue)) {
            // If batchId, paymentCount, and paymentTotalAmount are empty then at least creation date is required
            if (StringUtils.isBlank(fileCreationTimeValueLower) && StringUtils.isBlank(fileCreationTimeValueUpper) ) {
                GlobalVariables.getMessageMap().putError(PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_CRITERIA_NO_DATE);
            }
            else if (StringUtils.isBlank(fileCreationTimeValueLower) || StringUtils.isBlank(fileCreationTimeValueUpper)) {
                // If we have one (but not both) dates the user must enter either the chartCode, orgCode, or subUnitCode
                if (StringUtils.isBlank(chartCodeValue) && StringUtils.isBlank(orgCodeValue) && StringUtils.isBlank(subUnitCodeValue)) {
                    GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_CRITERIA_SOURCE_MISSING);
                }
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    /**
     * This method gets the kualiConfigurationService.
     * 
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * This method sets the configurationService.
     * 
     * @param configurationService ConfigurationService
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * This method gets the batchMaintenanceService.
     * 
     * @return the batchMaintenanceService
     */
    public BatchMaintenanceService getBatchMaintenanceService() {
        return batchMaintenanceService;
    }

    /**
     * This method sets the batchMaintenanceService.
     * 
     * @param batchMaintenanceService BatchMaintenanceService
     */
    public void setBatchMaintenanceService(BatchMaintenanceService batchMaintenanceService) {
        this.batchMaintenanceService = batchMaintenanceService;
    }

    /**
     * This method gets the lookupDao.
     * 
     * @return the lookupDao
     */
    public LookupDao getLookupDao() {
        return lookupDao;
    }

    /**
     * This method sets lookupDao.
     * 
     * @param lookupDao LookupDao
     */
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    /**
     * This method sets the pdpAuthorizationService.
     * @param pdpAuthorizationService The pdpAuthorizationService to be set.
     */
    public void setPdpAuthorizationService(PdpAuthorizationService pdpAuthorizationService) {
        this.pdpAuthorizationService = pdpAuthorizationService;
    }

}
