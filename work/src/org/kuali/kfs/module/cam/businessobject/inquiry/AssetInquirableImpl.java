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
package org.kuali.module.cams.web.inquirable;

import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.inquiry.KfsInquirableImpl;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.service.AssetDispositionService;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.EquipmentLoanInfoService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;

public class AssetInquirableImpl extends KfsInquirableImpl {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetInquirableImpl.class);
    
    /**
     * Executes service methods to populate appropriate data in the Asset BO.
     * @see org.kuali.core.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
     */
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        Asset asset = (Asset) super.getBusinessObject(fieldValues);
        
        if (ObjectUtils.isNotNull(asset)) {
            // Identifies the latest location information
            AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
            assetlocationService.setOffCampusLocation(asset);
    
            // Calculates payment summary and depreciation summary based on available payment records
            PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
            paymentSummaryService.calculateAndSetPaymentSummary(asset);
    
            // Identifies the merge history and separation history based on asset disposition records
            AssetDispositionService assetDispService = SpringContext.getBean(AssetDispositionService.class);
            assetDispService.setAssetDispositionHistory(asset);
    
            // Finds out the latest retirement info, is asset is currently retired.
            RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
            retirementInfoService.setRetirementInfo(asset);
            retirementInfoService.setMergeHistory(asset);
    
            // Finds out the latest equipment loan or return information if available
            EquipmentLoanInfoService equipmentLoanInfoService = SpringContext.getBean(EquipmentLoanInfoService.class);
            equipmentLoanInfoService.setEquipmentLoanInfo(asset);
        }
        
        return asset;
    }

}
