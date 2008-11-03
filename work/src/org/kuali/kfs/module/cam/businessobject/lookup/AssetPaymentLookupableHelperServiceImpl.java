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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class overrides the base getActionUrls method for Asset Payment. Even though it's a payment lookup screen we are maintaining
 * assets.
 */
public class AssetPaymentLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private LookupableHelperService assetLookupableHelperService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
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


        assetLookupableHelperService.setBusinessObjectClass(Asset.class);

        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();

        anchorHtmlDataList = assetLookupableHelperService.getCustomActionUrls(asset, assetPrimaryKey);
        anchorHtmlDataList.add(getPaymentUrl(asset));

        return anchorHtmlDataList;

        // return assetLookupableHelperService.getCustomActionUrls(asset, assetPrimaryKey);
    }

    /**
     * Returns the url for any drill down links within the lookup. Override so that documentNumber is not an inquiry if it doesn't exist.
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
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
    
    private HtmlData getPaymentUrl(Asset asset) {
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData("", "", CamsConstants.AssetActions.PAYMENT);

        // Only active capital assets will have the payment link.
        if (SpringContext.getBean(AssetService.class).isCapitalAsset(asset) && !SpringContext.getBean(AssetService.class).isAssetRetired(asset)) {
            String href = "../camsAssetPayment.do?methodToCall=docHandler&command=initiate&docTypeName=AssetPaymentDocument&capitalAssetNumber=" + asset.getCapitalAssetNumber();
            anchorHtmlData = new AnchorHtmlData(href, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.PAYMENT);
        }

        return anchorHtmlData;
    }


    /**
     * Gets the assetLookupableHelperService attribute.
     * 
     * @return Returns the assetLookupableHelperService.
     */
    public LookupableHelperService getAssetLookupableHelperService() {
        return assetLookupableHelperService;
    }

    /**
     * Sets the assetLookupableHelperService attribute value.
     * 
     * @param assetLookupableHelperService The assetLookupableHelperService to set.
     */
    public void setAssetLookupableHelperService(LookupableHelperService assetLookupableHelperService) {
        this.assetLookupableHelperService = assetLookupableHelperService;
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
