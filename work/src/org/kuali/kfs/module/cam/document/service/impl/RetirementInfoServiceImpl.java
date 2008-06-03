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
package org.kuali.module.cams.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.RetirementInfoService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Implements RetirementInfoService, assists in identifying the latest retirement record
 */
public class RetirementInfoServiceImpl implements RetirementInfoService {

    private ParameterService parameterService;
    private AssetService assetService;

    /**
     * @see org.kuali.module.cams.service.RetirementInfoService#setRetirementInfo(org.kuali.module.cams.bo.Asset)
     */
    public void setRetirementInfo(Asset asset) {
        // If current status is not retired, return empty
        if (!assetService.isAssetRetired(asset)) {
            return;
        }
        List<AssetRetirementGlobalDetail> retirementHistory = asset.getAssetRetirementHistory();

        List<AssetRetirementGlobalDetail> sortableList = new ArrayList<AssetRetirementGlobalDetail>();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : retirementHistory) {
            AssetRetirementGlobal assetRetirementGlobal = assetRetirementGlobalDetail.getAssetRetirementGlobal();
            // check if document is approved
            if (assetRetirementGlobal != null && isDocumentApproved(assetRetirementGlobal)) {
                sortableList.add(assetRetirementGlobalDetail);
            }

        }


        Comparator<AssetRetirementGlobalDetail> comparator = new Comparator<AssetRetirementGlobalDetail>() {
            public int compare(AssetRetirementGlobalDetail o1, AssetRetirementGlobalDetail o2) {
                // sort descending based on retirement date
                return o2.getAssetRetirementGlobal().getRetirementDate().compareTo(o1.getAssetRetirementGlobal().getRetirementDate());
            }
        };
        Collections.sort(sortableList, comparator);

        if (!sortableList.isEmpty()) {
            asset.setRetirementInfo(sortableList.get(0));
        }
    }


    /**
     * Checks asset retirement document status, if approved returns true
     * 
     * @param assetRetirementDoc Asset Retirement Document
     * @return "true" if approved
     */
    private boolean isDocumentApproved(AssetRetirementGlobal assetRetirementDoc) {
        assetRetirementDoc.refreshReferenceObject(CamsConstants.AssetRetirementGlobal.DOCUMENT_HEADER);
        DocumentHeader documentHeader = assetRetirementDoc.getDocumentHeader();
        if (documentHeader != null && KNSConstants.DocumentStatusCodes.APPROVED.equals(documentHeader.getFinancialDocumentStatusCode())) {
            return true;
        }
        return false;
    }


    public ParameterService getParameterService() {
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    public AssetService getAssetService() {
        return assetService;
    }


    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }


    public void setMergeHistory(Asset asset) {
        List<AssetRetirementGlobal> retirementGlobals = asset.getRetirementGlobals();
        List<AssetRetirementGlobalDetail> mergeHistory = new ArrayList<AssetRetirementGlobalDetail>();

        // TODO Auto-generated method stub
        for (AssetRetirementGlobal retirementGlobal : retirementGlobals) {
            if (CamsConstants.RETIREMENT_REASON_CODE_M.equalsIgnoreCase(retirementGlobal.getRetirementReasonCode())) {
                List<AssetRetirementGlobalDetail> retirementDetails = retirementGlobal.getAssetRetirementGlobalDetails();

                for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : retirementDetails) {
                    mergeHistory.add(assetRetirementGlobalDetail);
                }
            }
        }

        if (!mergeHistory.isEmpty()) {
            asset.setMergeHistory(mergeHistory);
        }
    }


}
