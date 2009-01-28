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
package org.kuali.kfs.module.cam.businessobject.inquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

public class AssetInquirableImpl extends KfsInquirableImpl {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetInquirableImpl.class);
    
    /**
     * Executes service methods to populate appropriate data in the Asset BO.
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
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
            AssetService assetService = SpringContext.getBean(AssetService.class);
            assetService.setSeparateHistory(asset);

            // Finds out the latest retirement info, is asset is currently retired.
            RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
            retirementInfoService.setRetirementInfo(asset);
            retirementInfoService.setMergeHistory(asset);
    
            // Finds out the latest equipment loan or return information if available
            EquipmentLoanOrReturnService equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
            equipmentLoanOrReturnService.setEquipmentLoanInfo(asset);
        }
        
        return asset;
    }

    /**
     * Hide payments if there are more then the allowable number.
     * 
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public List<Section> getSections(BusinessObject businessObject) {
        List<Section> sections = super.getSections(businessObject);

        // sectionToRemove is hoky but it looks like that section.setHidden doesn't work on inquirable. And to avoid
        // ConcurrentModificationException we do this
        Section sectionToRemove = null;
        
        Asset asset = (Asset) businessObject;
        for (Section section : sections) {
            if (CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION.equals(section.getSectionId()) && asset.getAssetPayments().size() > CamsConstants.Asset.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY) {
                // Hide the payment section if there are more then CamsConstants.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY
                //section.setHidden(true);
                sectionToRemove = section;
            }
        }

        if (sectionToRemove != null) {
            sections.remove(sectionToRemove);
        }        
        return sections;
    }        
}
