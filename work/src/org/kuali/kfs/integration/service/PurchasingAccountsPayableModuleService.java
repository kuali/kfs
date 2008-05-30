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
package org.kuali.module.integration.service;

import java.util.Date;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.integration.bo.PurchasingAccountsPayableItemCostSummary;
import org.kuali.module.integration.bo.PurchasingAccountsPayableItemBuyerAndSellerSummary;
import org.kuali.module.integration.bo.PurchasingAccountsPayableRestrictedMaterial;

/**
 * Methods needed to interface with a Purchasing/Accounts Payable module
 */
public interface PurchasingAccountsPayableModuleService {
    /**
     * Provides the inquiry Url for a purchase order. Used by the PurAp / CAMs document to show user further information about the PO.
     * @param purchaseOrderNumber
     * @return
     */
    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber);
    
    /**
     * Adds asset numbers that were created to a Purchase Order that caused the creation.
     * @param purchaseOrderNumber
     * @param assetNumbers
     */
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, List<Integer> assetNumbers);
    
    /**
     * Returns a restricted material record associated with the given code
     * @param restrictedMaterialCode the code of the restricted material
     * @return a record of restricted material information
     */
    public PurchasingAccountsPayableRestrictedMaterial getRestrictedMaterialByCode(String restrictedMaterialCode);
    
    /**
     * Returns all restricted materials records known to the module
     * @return a List of all restricted materials known to the module
     */
    public List<PurchasingAccountsPayableRestrictedMaterial> getAllRestrictedMaterials();
}
