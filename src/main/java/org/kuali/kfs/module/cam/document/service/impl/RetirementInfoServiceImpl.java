/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * Implements RetirementInfoService, assists in identifying the latest retirement record
 */
public class RetirementInfoServiceImpl implements RetirementInfoService {

    private ParameterService parameterService;
    private AssetService assetService;

    /**
     * @see org.kuali.kfs.module.cam.document.service.RetirementInfoService#setRetirementInfo(org.kuali.kfs.module.cam.businessobject.Asset)
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
    protected boolean isDocumentApproved(AssetRetirementGlobal assetRetirementDoc) {
        assetRetirementDoc.refreshReferenceObject(CamsConstants.AssetRetirementGlobal.DOCUMENT_HEADER);
        if (assetRetirementDoc.getDocumentHeader() != null && KFSConstants.DocumentStatusCodes.APPROVED.equals(assetRetirementDoc.getDocumentHeader().getFinancialDocumentStatusCode())) {
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

        for (AssetRetirementGlobal retirementGlobal : retirementGlobals) {
            if (CamsConstants.AssetRetirementReasonCode.MERGED.equalsIgnoreCase(retirementGlobal.getRetirementReasonCode())) {
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
