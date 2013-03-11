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
package org.kuali.kfs.module.cam.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
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
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class overrids the base getActionUrls method
 */
public class AssetLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLookupableHelperServiceImpl.class);

    protected AssetService assetService;

    /**
     * Custom action urls for Asset.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
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
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.ASSET_RETIREMENT_GLOBAL);
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

    @Override
    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
        String href = super.getReturnHref(parameters, lookupForm, returnKeys);
        href += "&referencesToRefresh=mergedTargetCapitalAsset";
        return href;

    }


    protected HtmlData getLoanUrl(Asset asset) {
        AnchorHtmlData anchorHtmlData = null;
        List<HtmlData> childURLDataList = new ArrayList<HtmlData>();

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
        parameters.put(CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, CamsConstants.DocumentTypeName.ASSET_EQUIPMENT_LOAN_OR_RETURN);

        if (getAssetService().isAssetLoaned(asset)) {
            anchorHtmlData = new AnchorHtmlData("", "", "");

            AnchorHtmlData childURLData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN);
            childURLDataList.add(childURLData);

            parameters.put(CamsConstants.AssetActions.LOAN_TYPE, CamsConstants.AssetActions.LOAN_RENEW);
            String childHref = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.EQUIPMENT_LOAN_OR_RETURN, parameters);
            childURLData = new AnchorHtmlData(childHref, KRADConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN_RENEW);
            childURLDataList.add(childURLData);

            parameters.remove(CamsConstants.AssetActions.LOAN_TYPE);
            parameters.put(CamsConstants.AssetActions.LOAN_TYPE, CamsConstants.AssetActions.LOAN_RETURN);
            childHref = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.EQUIPMENT_LOAN_OR_RETURN, parameters);
            childURLData = new AnchorHtmlData(childHref, KRADConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN_RETURN);
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
                childURLData = new AnchorHtmlData(childHref, KRADConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN);
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
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL);
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
        boolean isAuthorized = true;
        boolean assetMovable = false;
        try {
           assetMovable = getAssetService().isAssetMovableCheckByPayment(asset);
        } catch (ValidationException ve) {
            isAuthorized = false;
        }
        if (!assetMovable) {
            FinancialSystemTransactionalDocumentAuthorizerBase documentAuthorizer = (FinancialSystemTransactionalDocumentAuthorizerBase)SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.ASSET_TRANSFER);
            isAuthorized = documentAuthorizer.isAuthorized(asset, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.SEPARATE, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }

        if (isAuthorized) {
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
            parameters.put(CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
            parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
            parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, CamsConstants.DocumentTypeName.ASSET_TRANSFER);

            String href = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.TRANSFER, parameters);

            return new AnchorHtmlData(href, KRADConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.TRANSFER);
        }
        else {
            return new AnchorHtmlData("", "", "");
        }
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }


    /**
     * Overridden to fix a field conversion
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
     *
     * KRAD Conversion: Performs customization of the  Row objects to be used to generate the search query screen.
     */
    @Override
    public List<Row> getRows() {
        convertOrganizationOwnerAccountField();
        return super.getRows();
    }

    /**
     * Overridden to fix a field conversion
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
     */
    @Override
    protected void setRows() {
        super.setRows();
        convertOrganizationOwnerAccountField();
    }

    /**
     * Goes through all the rows, making sure that problematic field conversions are fixed
     *
     * KRAD Conversion: Performs customization of the fields.
     */
    protected void convertOrganizationOwnerAccountField() {
        boolean foundField = false;
        int i = 0;
        while (!foundField && i < super.getRows().size()) {
            final Row r = super.getRows().get(i);
            int j = 0;
            while (!foundField && j < r.getFields().size()) {
                Field f = r.getField(j);
                if (f.getPropertyName().equals(CamsPropertyConstants.Asset.ORGANIZATION_CODE)) {
                    f.setFieldConversions(fixProblematicField(f.getFieldConversions()));
                    f.setLookupParameters(fixProblematicField(f.getLookupParameters()));
                    foundField = true;
                }
                j += 1;
            }
            i += 1;
        }
    }

    /**
     * Fixes the field conversions - replaces "organizationOwnerAccount.chartOfAccountsCode" with
     * "organizationOwnerChartOfAccountsCode"
     *
     * @param fieldConversions the original field conversions
     * @return the fixed field conversions
     */
    protected String fixProblematicField(String problemChildField) {
        if (StringUtils.isBlank(problemChildField)) {
            return problemChildField;
        }
        return problemChildField.replace(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNTS_COA_CODE, CamsPropertyConstants.Asset.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE);
    }

    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // perform the lookup on the asset representative first
        String principalName = fieldValues.get(CamsPropertyConstants.Asset.REP_USER_AUTH_ID);
        if (StringUtils.isNotBlank(principalName)) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(principalName);

            if (principal == null) {
                return Collections.EMPTY_LIST;
            }
            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(CamsPropertyConstants.Asset.REPRESENTATIVE_UNIVERSAL_IDENTIFIER, principal.getPrincipalId());
            fieldValues.remove(CamsPropertyConstants.Asset.REP_USER_AUTH_ID);
        }

        return super.getSearchResultsHelper(fieldValues, unbounded);
    }
}
