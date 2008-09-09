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
import java.util.Properties;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * This class overrids the base getActionUrls method
 */
public class AssetLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLookupableHelperServiceImpl.class);
    AssetService assetService;

    /**
     * Custom action urls for Asset.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();

        /** TODO per authorization don't show some links * */
        /** TODO per Asset status don't show some links * */

        anchorHtmlDataList.add(getURLData(bo, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        anchorHtmlDataList.add(getLoanUrl(bo));
        anchorHtmlDataList.add(getMergeUrl(bo));
        anchorHtmlDataList.add(getSeparateUrl(bo));        anchorHtmlDataList.add(getTransferUrl(bo));

        return anchorHtmlDataList;
    }

    private HtmlData getMergeUrl(BusinessObject bo) {
        // TODO use system parameter
        Asset asset = (Asset) bo;
        String href = "maintenance.do?methodToCall=newWithExisting&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal&" + KFSConstants.OVERRIDE_KEYS + "=retirementReasonCode" + KFSConstants.FIELD_CONVERSIONS_SEPERATOR + "mergedTargetCapitalAssetNumber&docFormKey=88888888&retirementReasonCode=M&mergedTargetCapitalAssetNumber=" + asset.getCapitalAssetNumber();
        return new AnchorHtmlData(href, CamsConstants.AssetActions.MERGE, CamsConstants.AssetActions.MERGE);
    }

    private HtmlData getLoanUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        AnchorHtmlData anchorHtmlData = null;
        List<HtmlData> childURLDataList = new ArrayList<HtmlData>();
        if (getAssetService().isAssetLoaned(asset)){
            anchorHtmlData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN);
            String childHref = "../camsEquipmentLoanOrReturn.do?methodToCall=docHandler&command=initiate&docTypeName=EquipmentLoanOrReturnDocument&loanType=renew&capitalAssetNumber=" + asset.getCapitalAssetNumber();

            AnchorHtmlData childURLData = new AnchorHtmlData(childHref, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN_RENEW);
            childURLDataList.add(childURLData);
            childHref = "../camsEquipmentLoanOrReturn.do?methodToCall=docHandler&command=initiate&docTypeName=EquipmentLoanOrReturnDocument&loanType=return&capitalAssetNumber=" + asset.getCapitalAssetNumber();
            childURLData = new AnchorHtmlData(childHref, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN_RETURN);
            childURLDataList.add(childURLData);
            anchorHtmlData.setChildUrlDataList(childURLDataList);
        } else {
            String href = "../camsEquipmentLoanOrReturn.do?methodToCall=docHandler&command=initiate&docTypeName=EquipmentLoanOrReturnDocument&loanType=loan&capitalAssetNumber=" + asset.getCapitalAssetNumber();
            anchorHtmlData = new AnchorHtmlData(href, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.LOAN);

            AnchorHtmlData childURLData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN_RENEW);
            childURLDataList.add(childURLData);
            childURLData = new AnchorHtmlData("", "", CamsConstants.AssetActions.LOAN_RETURN);
            childURLDataList.add(childURLData);
            anchorHtmlData.setChildUrlDataList(childURLDataList);
        }

        return anchorHtmlData;
    }

    private HtmlData getSeparateUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetGlobal.class.getName());

        // Asset PK - constant will later be in OJB
        parameters.put(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        //parameters.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());

        // parameter that tells us this is a separate action. We read this in AssetMaintenanbleImpl.processAfterNew
        parameters.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);

        String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
        return new AnchorHtmlData(href, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL, CamsConstants.AssetActions.SEPARATE);
    }

    private HtmlData getTransferUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        String href = "../camsAssetTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=AssetTransferDocument&capitalAssetNumber=" + asset.getCapitalAssetNumber();
        return new AnchorHtmlData(href, KNSConstants.DOC_HANDLER_METHOD, CamsConstants.AssetActions.TRANSFER);
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

}
