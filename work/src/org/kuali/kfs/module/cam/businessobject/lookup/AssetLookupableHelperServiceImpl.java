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

import java.util.Properties;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
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
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject bo) {
        StringBuffer actions = new StringBuffer();

        /** TODO per authorization don't show some links * */
        /** TODO per Asset status don't show some links * */

        actions.append(getMaintenanceUrl(bo, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL));
        actions.append("&nbsp;&nbsp;");
        actions.append(getLoanUrl(bo));
        actions.append("&nbsp;&nbsp;");
        actions.append(getMergeUrl(bo));
        actions.append("&nbsp;&nbsp;");

        actions.append(getPaymentUrl(bo));
        actions.append("&nbsp;&nbsp;");
        
        actions.append(getSeparateUrl(bo));
        actions.append("&nbsp;&nbsp;");
        actions.append(getTransferUrl(bo));

        return actions.toString();
    }

    private Object getMergeUrl(BusinessObject bo) {
        // TODO use system parameter
        Asset asset = (Asset) bo;
        return "<a href=\"maintenance.do?methodToCall=newWithExisting&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal&" + KFSConstants.OVERRIDE_KEYS + "=retirementReasonCode" + KFSConstants.FIELD_CONVERSIONS_SEPERATOR + "mergedTargetCapitalAssetNumber&docFormKey=88888888&retirementReasonCode=M&mergedTargetCapitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.MERGE + "</a>";
    }

    private String getLoanUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        String url;

        if (getAssetService().isAssetLoaned(asset)) {
            url = CamsConstants.AssetActions.LOAN + "&nbsp;[" 
            + "<a href=\"../camsEquipmentLoanOrReturn.do?methodToCall=docHandler&command=initiate&docTypeName=EquipmentLoanOrReturnDocument&loanType=renew&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.LOAN_RENEW + "</a>" 
            + "&nbsp;|&nbsp;" 
            + "<a href=\"../camsEquipmentLoanOrReturn.do?methodToCall=docHandler&command=initiate&docTypeName=EquipmentLoanOrReturnDocument&loanType=return&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.LOAN_RETURN + "</a>" 
            + "]";
        } else {
            url = "<a href=\"../camsEquipmentLoanOrReturn.do?methodToCall=docHandler&command=initiate&docTypeName=EquipmentLoanOrReturnDocument&loanType=loan&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.LOAN + "</a>" 
            + "&nbsp;[" + CamsConstants.AssetActions.LOAN_RENEW + "&nbsp;|&nbsp;" + CamsConstants.AssetActions.LOAN_RETURN + "]";
        }

        return url;
    }

    private String getSeparateUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetGlobal.class.getName());
        
        // Asset PK - constant will later be in OJB
        parameters.put(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        //parameters.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        
        // parameter that tells us this is a separate action. We read this in AssetMaintenanbleImpl.processAfterNew
        parameters.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, CamsConstants.DocumentTypeCodes.ASSET_SEPARATE);

        String url = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
        url = "<a href=\"" + url + "\">" + CamsConstants.AssetActions.SEPARATE + "</a>";
        return url;
    }
    
    private String getTransferUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        return "<a href=\"../camsAssetTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=AssetTransferDocument&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.TRANSFER + "</a>";
    }

    private String getPaymentUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        String anchor = CamsConstants.AssetActions.PAYMENT;

        // Only active capital assets will have the payment link.
        if (getAssetService().isCapitalAsset(asset) && !getAssetService().isAssetRetired(asset))
            anchor = "<a href=\"../camsAssetPayment.do?methodToCall=docHandler&command=initiate&docTypeName=AssetPaymentDocument&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.PAYMENT + "</a>";

        return anchor;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

}
