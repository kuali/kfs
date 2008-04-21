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
package org.kuali.module.purap.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.integration.bo.PurchasingAccountsPayableItemBuyerAndSellerSummary;
import org.kuali.module.integration.bo.PurchasingAccountsPayableItemCostSummary;
import org.kuali.module.integration.bo.PurchasingAccountsPayableRestrictedMaterial;
import org.kuali.module.integration.service.PurchasingAccountsPayableModuleService;
import org.kuali.module.purap.bo.RestrictedMaterial;

/**
 * 
 */
public class PurchasingAccountsPayableModuleServiceImpl implements PurchasingAccountsPayableModuleService {

    /**
     * @see org.kuali.module.integration.service.PurchasingAccountsPayableModuleService#addAssignedAssetNumbers(java.lang.Integer, java.util.List)
     */
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, List<Integer> assetNumbers) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.module.integration.service.PurchasingAccountsPayableModuleService#getItemBuyerAndSellerSummarys(java.util.List, java.util.List, java.util.List, java.util.Date, org.kuali.core.util.KualiDecimal)
     */
    public List<PurchasingAccountsPayableItemBuyerAndSellerSummary> getItemBuyerAndSellerSummarys(List<String> chartCodes, List<String> objectSubTypeCodes, List<String> subFundGroupCodes, Date purchaseOrderOpenAsOfDate, KualiDecimal capitalizationLimit) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.module.integration.service.PurchasingAccountsPayableModuleService#getItemCostSummarys(java.util.Date)
     */
    public List<PurchasingAccountsPayableItemCostSummary> getItemCostSummarys(Date purchaseOrderOpenAsOfDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.module.integration.service.PurchasingAccountsPayableModuleService#getPurchaseOrderInquiryUrl(java.lang.Integer)
     */
    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.module.integration.service.PurchasingAccountsPayableModuleService#populateAssetBuilderInformation()
     */
    public void populateAssetBuilderInformation() {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.module.integration.service.PurchasingAccountsPayableModuleService#getAllRestrictedMaterials()
     */
    public List<PurchasingAccountsPayableRestrictedMaterial> getAllRestrictedMaterials() {
        List<PurchasingAccountsPayableRestrictedMaterial> restrictedMaterials = new ArrayList<PurchasingAccountsPayableRestrictedMaterial>();
        Collection restrictedMaterialsAsObjects = SpringContext.getBean(BusinessObjectService.class).findAll(RestrictedMaterial.class);
        for (Object rm: restrictedMaterialsAsObjects) {
            restrictedMaterials.add((PurchasingAccountsPayableRestrictedMaterial)rm);
        }
        return restrictedMaterials;
    }

    /**
     * @see org.kuali.module.integration.service.PurchasingAccountsPayableModuleService#getRestrictedMaterialByCode(java.lang.String)
     */
    public PurchasingAccountsPayableRestrictedMaterial getRestrictedMaterialByCode(String restrictedMaterialCode) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("restrictedMaterialCode", restrictedMaterialCode);
        return (PurchasingAccountsPayableRestrictedMaterial)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(RestrictedMaterial.class, primaryKeys);
    }

}
