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
package org.kuali.module.vendor.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.integration.bo.PurchasingAccountsPayableRestrictedMaterial;
import org.kuali.module.integration.service.PurchasingAccountsPayableModuleService;

/**
 * CommodityCode Code Business Object
 */
public class CommodityCode extends PersistableBusinessObjectBase {
    
    private String purchasingCommodityCode;
    private String commodityDescription;
    private boolean salesTaxIndicator;
    private boolean restrictedItemsIndicator;
    private String restrictedMaterialCode;
    private boolean active;
    
    private PurchasingAccountsPayableRestrictedMaterial restrictedMaterial;
    
    private List<CommodityContractManager> commodityContractManagers;
    
    public CommodityCode() {
        commodityContractManagers = new TypedArrayList(CommodityContractManager.class);
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPurchasingCommodityCode() {
        return purchasingCommodityCode;
    }

    public void setPurchasingCommodityCode(String purchasingCommodityCode) {
        this.purchasingCommodityCode = purchasingCommodityCode;
    }

    public String getCommodityDescription() {
        return commodityDescription;
    }

    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    public boolean isRestrictedItemsIndicator() {
        return restrictedItemsIndicator;
    }

    public void setRestrictedItemsIndicator(boolean restrictedItemsIndicator) {
        this.restrictedItemsIndicator = restrictedItemsIndicator;
    }
    
    public String getRestrictedMaterialCode() {
        return restrictedMaterialCode;
    }

    public void setRestrictedMaterialCode(String restrictedMaterialCode) {
        this.restrictedMaterialCode = restrictedMaterialCode;
    }

    public PurchasingAccountsPayableRestrictedMaterial getRestrictedMaterial() {
        if (StringUtils.isBlank(restrictedMaterialCode)) {
            if (restrictedMaterial != null) {
                restrictedMaterial = null;
            }
        } else {
            if (restrictedMaterial == null || !restrictedMaterial.getRestrictedMaterialCode().equals(this.restrictedMaterialCode)) {
                restrictedMaterial = SpringContext.getBean(PurchasingAccountsPayableModuleService.class).getRestrictedMaterialByCode(this.restrictedMaterialCode);
            }
        }
        return restrictedMaterial;
    }

    public boolean isSalesTaxIndicator() {
        return salesTaxIndicator;
    }

    public void setSalesTaxIndicator(boolean salesTaxIndicator) {
        this.salesTaxIndicator = salesTaxIndicator;
    }
    
    public List<CommodityContractManager> getCommodityContractManagers() {
        return commodityContractManagers;
    }

    public void setCommodityContractManagers(List<CommodityContractManager> commodityContractManagers) {
        this.commodityContractManagers = commodityContractManagers;
    }    

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purchasingCommodityCode",this.purchasingCommodityCode);
        m.put("commodityDescription",this.commodityDescription);
        return null;
    }

}
