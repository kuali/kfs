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
package org.kuali.kfs.module.cam.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * This class overrids the base getActionUrls method
 */
public class AssetLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLookupableHelperServiceImpl.class);

    AssetService assetService;

    /**
     * Custom action urls for Asset.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        List<HtmlData> anchorHtmlDataList = super.getCustomActionUrls(bo, pkNames);
        Asset asset = (Asset) bo;

        // For retired asset, all action link will be hidden.
        if (assetService.isAssetRetired(asset)) {
            // clear 'edit' link
            anchorHtmlDataList.clear();
            // add 'view' link instead
            anchorHtmlDataList.add(getViewAssetUrl(asset));
        }
        else {
            anchorHtmlDataList.add(getLoanUrl(asset));
            anchorHtmlDataList.add(getMergeUrl(asset));
            anchorHtmlDataList.add(getSeparateUrl(asset));
            anchorHtmlDataList.add(getTransferUrl(asset));
        }

        return anchorHtmlDataList;
    }

    protected HtmlData getViewAssetUrl(Asset asset) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CapitalAssetManagementAsset.class.getName());

        String href = UrlFactory.parameterizeUrl(CamsConstants.INQUIRY_URL, parameters);

        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KFSConstants.START_METHOD, CamsConstants.AssetActions.VIEW);
        anchorHtmlData.setTarget("blank");
        return anchorHtmlData;
    }

    protected HtmlData getMergeUrl(Asset asset) {
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.RETIREMENT);
        boolean isAuthorized = documentAuthorizer.isAuthorized(asset, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.MERGE, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (isAuthorized) {
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetRetirementGlobal.class.getName());
            parameters.put(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
            parameters.put(KFSConstants.OVERRIDE_KEYS, CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE + KFSConstants.FIELD_CONVERSIONS_SEPERATOR + CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER);
            parameters.put(CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE, CamsConstants.AssetRetirementReasonCode.MERGED);
            parameters.put(KFSConstants.REFRESH_CALLER, CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE + "::" + CamsConstants.AssetRetirementReasonCode.MERGED);

            String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);

            return new AnchorHtmlData(href, CamsConstants.AssetActions.MERGE, CamsConstants.AssetActions.MERGE);
        }
        else {
            return new AnchorHtmlData("", "", "");
        }
    }

    protected HtmlData getLoanUrl(Asset asset) {
        AnchorHtmlData anchorHtmlData = null;
        List<HtmlData> childURLDataList = new ArrayList<HtmlData>();

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
        parameters.put(CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, CamsConstants.DocumentTypeName.EQUIPMENT_LOAN_OR_RETURN);

        if (getAssetService().isAssetLoaned(asset)) {
            anchorHtmlData = new AnchorHtmlData("", "", "");

            AnchorHtmlData childURLData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN);
            childURLDataList.add(childURLData);

            parameters.put(CamsConstants.AssetActions.LOAN_TYPE, CamsConstants.AssetActions.LOAN_RENEW);
            String childHref = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.EQUIPMENT_LOAN_OR_RETURN, parameters);
            childURLData = new AnchorHtmlData(childHref, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN_RENEW);
            childURLDataList.add(childURLData);

            parameters.remove(CamsConstants.AssetActions.LOAN_TYPE);
            parameters.put(CamsConstants.AssetActions.LOAN_TYPE, CamsConstants.AssetActions.LOAN_RETURN);
            childHref = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.EQUIPMENT_LOAN_OR_RETURN, parameters);
            childURLData = new AnchorHtmlData(childHref, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN_RETURN);
            childURLDataList.add(childURLData);

            anchorHtmlData.setChildUrlDataList(childURLDataList);
        }
        else {
            anchorHtmlData = new AnchorHtmlData("", "", "");
            // 
            AnchorHtmlData childURLData = new AnchorHtmlData("", "", "");
            if (asset.getCampusTagNumber() == null) {
                childURLData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN);
                childURLDataList.add(childURLData);
            }
            else {
                parameters.put(CamsConstants.AssetActions.LOAN_TYPE, CamsConstants.AssetActions.LOAN);
                String childHref = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.EQUIPMENT_LOAN_OR_RETURN, parameters);
                childURLData = new AnchorHtmlData(childHref, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN);
                childURLDataList.add(childURLData);
            }

            childURLData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN_RENEW);
            childURLDataList.add(childURLData);

            childURLData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN_RETURN);
            childURLDataList.add(childURLData);

            anchorHtmlData.setChildUrlDataList(childURLDataList);
        }

        return anchorHtmlData;
    }

    protected HtmlData getSeparateUrl(Asset asset) {
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.RETIREMENT);
        boolean isAuthorized = documentAuthorizer.isAuthorized(asset, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.SEPARATE, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (isAuthorized) {
            String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, getSeparateParameters(asset));

            return new AnchorHtmlData(href, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL, CamsConstants.AssetActions.SEPARATE);
        }
        else {
            return new AnchorHtmlData("", "", "");
        }
    }

    protected Properties getSeparateParameters(Asset asset) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetGlobal.class.getName());
        parameters.put(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        // parameter that tells us this is a separate action. We read this in AssetMaintenanbleImpl.processAfterNew
        parameters.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);

        return parameters;
    }

    protected HtmlData getTransferUrl(Asset asset) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
        parameters.put(CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, CamsConstants.DocumentTypeName.TRANSFER);

        String href = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.TRANSFER, parameters);

        return new AnchorHtmlData(href, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.TRANSFER);
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * Generates the list of search results on campusTagNumber that meet the string range search criteria.
     * 
     * @param fieldValues the field values of the query to carry out
     * @return List the search results returned by the lookup
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean stringRangeCriteria = false;

        String searchTagNumber = fieldValues.get(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER);

        // the following "if" statement is modified from rice/kns/src/main/java/org/kuali/core/dao/ojb/LookupDaoOjb.java
        // addCriteria() addStringRangeCriteria()
        if (StringUtils.isNotBlank(searchTagNumber) && (StringUtils.contains(searchTagNumber, "..") || StringUtils.contains(searchTagNumber, ">") || StringUtils.contains(searchTagNumber, "<") || StringUtils.contains(searchTagNumber, ">=") || StringUtils.contains(searchTagNumber, "<="))) {
            stringRangeCriteria = true;
            fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, null);
        }

        List<? extends BusinessObject> searchResults = super.getSearchResults(fieldValues);
        if (searchResults == null || searchResults.isEmpty() || StringUtils.isBlank(searchTagNumber) || !stringRangeCriteria) {
            return searchResults;
        }

        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(Asset.class);
        Long matchingResultsCount = null;
        List<Asset> newList = new ArrayList<Asset>();
        for (BusinessObject businessObject : searchResults) {
            Asset asset = (Asset) businessObject;
            if (ObjectUtils.isNotNull(asset) && isInStringRangeCriteria(searchTagNumber, asset.getCampusTagNumber())) {
                newList.add(asset);
            }
        }

        matchingResultsCount = Long.valueOf(newList.size());

        return new CollectionIncomplete(newList, matchingResultsCount);
    }

    /**
     * This method flags if the checked value is within the search criteria
     * 
     * @param String searchTagNumber
     * @param String checkValue
     * @return boolean isInStringRangeCriteria
     */
    private boolean isInStringRangeCriteria(String searchTagNumber, String checkValue) {
        boolean isInStringRangeCriteria = false;

        if (StringUtils.contains(searchTagNumber, "..")) {
            String[] rangeValues = StringUtils.split(searchTagNumber, "..");
            if (rangeValues[0].compareTo(checkValue) <= 0 && rangeValues[1].compareTo(checkValue) >= 0)
                isInStringRangeCriteria = true;
        }
        else if (searchTagNumber.startsWith(">=")) {
            if (searchTagNumber.substring(2).compareTo(checkValue) <= 0) {
                isInStringRangeCriteria = true;
            }
        }
        else if (searchTagNumber.startsWith("<=")) {
            if (searchTagNumber.substring(2).compareTo(checkValue) >= 0)
                isInStringRangeCriteria = true;
        }
        else if (searchTagNumber.startsWith(">")) {
            if (searchTagNumber.substring(1).compareTo(checkValue) < 0)
                isInStringRangeCriteria = true;
        }
        else if (searchTagNumber.startsWith("<")) {
            if (searchTagNumber.substring(1).compareTo(checkValue) > 0)
                isInStringRangeCriteria = true;
        }

        return isInStringRangeCriteria;
    }
}