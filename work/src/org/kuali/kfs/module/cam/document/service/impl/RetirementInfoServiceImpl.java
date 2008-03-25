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

import static org.kuali.module.cams.CamsConstants.DOC_APPROVED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.RetirementInfoService;

/**
 * Implements RetirementInfoService, assists in identifying the latest retirement record
 */
public class RetirementInfoServiceImpl implements RetirementInfoService {

    // TODO Change to use system parameters
    private static final String[] RETIRED_INV_CODES = new String[] { "O", "R", "E" };


    /**
     * Identifies the latest retirement record for the asset if current status is retired
     * 
     * <li>Sorts all approved retirement documents by retirement date</li>
     * <li>Latest retirement document is identified and assigns to asset</li>
     * 
     * @param asset Asset
     * @see org.kuali.module.cams.service.RetirementInfoService#setRetirementInfo(org.kuali.module.cams.bo.Asset)
     */
    public void setRetirementInfo(Asset asset) {
        // If current status is not retired, return empty
        if (!ArrayUtils.contains(RETIRED_INV_CODES, asset.getInventoryStatusCode())) {
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
    public boolean isDocumentApproved(AssetRetirementGlobal assetRetirementDoc) {
        assetRetirementDoc.refreshReferenceObject(CamsConstants.AssetRetirementGlobal.DOCUMENT_HEADER);
        DocumentHeader documentHeader = assetRetirementDoc.getDocumentHeader();
        if (documentHeader != null && DOC_APPROVED.equals(documentHeader.getFinancialDocumentStatusCode())) {
            return true;
        }
        return false;
    }

}
