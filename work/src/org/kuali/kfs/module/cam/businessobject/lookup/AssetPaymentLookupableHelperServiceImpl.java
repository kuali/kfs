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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.authorization.AssetAuthorizer;
import org.kuali.kfs.module.cam.document.authorization.AssetPaymentDocumentAuthorizer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class overrides the base getActionUrls method for Asset Payment. Even though it's a payment lookup screen we are maintaining
 * assets.
 */
public class AssetPaymentLookupableHelperServiceImpl extends AssetLookupableHelperServiceImpl {
    
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        AssetPayment assetPayment = (AssetPayment) businessObject;

        // Same thing but we're maintaining assets, not asset payments.
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, assetPayment.getCapitalAssetNumber());
        Asset asset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, primaryKeys);

        List assetPrimaryKey = new ArrayList();
        assetPrimaryKey.add(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER);

        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        
        // For retired asset, all action link will be hidden.
        if (assetService.isAssetRetired(asset)) {
            anchorHtmlDataList.add(super.getViewAssetUrl(asset));
        }
        else {
            anchorHtmlDataList.add(this.getAssetUrl(asset));
            anchorHtmlDataList.add(super.getLoanUrl(asset));
            anchorHtmlDataList.add(super.getMergeUrl(asset));
            anchorHtmlDataList.add(this.getSeparateUrl(assetPayment));
            anchorHtmlDataList.add(super.getTransferUrl(asset));
            anchorHtmlDataList.add(this.getPaymentUrl(asset));
        }
        return anchorHtmlDataList;
    }

    /**
     * Returns the url for any drill down links within the lookup. Override so that documentNumber is not an inquiry if it doesn't exist.
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName)) {
            AssetPayment assetPayment = (AssetPayment) businessObject;
            
            if (ObjectUtils.isNull(assetPayment.getDocumentHeader())) {
                // If the document isn't found, don't show the inquirable
                return new AnchorHtmlData(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING);
            }
        }
        return super.getInquiryUrl(businessObject, propertyName);
    }

    protected HtmlData getPaymentUrl(Asset asset) {
        AssetPaymentDocumentAuthorizer assetPaymentAuhorizer =new AssetPaymentDocumentAuthorizer();
        boolean isAuhtorize = assetPaymentAuhorizer.canInitiate(CamsConstants.DocumentTypeName.ASSET_PAYMENT, GlobalVariables.getUserSession().getPerson());
        
        if (assetService.isCapitalAsset(asset) && isAuhtorize) {
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
            parameters.put(CamsPropertyConstants.AssetPaymentDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
            parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
            parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, CamsConstants.DocumentTypeName.ASSET_PAYMENT);

            String href = UrlFactory.parameterizeUrl(CamsConstants.StrutsActions.ONE_UP + CamsConstants.StrutsActions.PAYMENT, parameters);

            return new AnchorHtmlData(href, KRADConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.PAYMENT);
        } else {
            return new AnchorHtmlData("", "", "");
        }
    }

    
    protected HtmlData getSeparateUrl(AssetPayment assetPayment) {
        Asset asset = assetPayment.getAsset();
        
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL);
        boolean isAuthorized = documentAuthorizer.isAuthorized(asset, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.SEPARATE, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (isAuthorized) {
            Properties parameters = getSeparateParameters(assetPayment.getAsset());
            parameters.put(CamsPropertyConstants.AssetGlobal.SEPERATE_SOURCE_PAYMENT_SEQUENCE_NUMBER, assetPayment.getPaymentSequenceNumber().toString());
            String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
            return new AnchorHtmlData(href, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL, CamsConstants.AssetActions.SEPARATE);
        } else {
                return new AnchorHtmlData("", "", "");
            }
        }
        
    protected HtmlData getAssetUrl(Asset asset) {
        AssetAuthorizer assetAuthorizer = new AssetAuthorizer();
        boolean isAuthorized = assetAuthorizer.canMaintain(asset, GlobalVariables.getUserSession().getPerson());
        
        if (isAuthorized) {
            Properties parameters = new Properties();            
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
            parameters.put(CamsPropertyConstants.AssetPaymentDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Asset.class.getName());
    
            String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
            return new AnchorHtmlData(href, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        } else {
            return new AnchorHtmlData("", "", "");
        }
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
