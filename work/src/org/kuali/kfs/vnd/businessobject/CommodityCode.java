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

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.purap.bo.RestrictedMaterial;

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
    
    private RestrictedMaterial restrictedMaterial;
        
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

    public RestrictedMaterial getRestrictedMaterial() {
        return restrictedMaterial;
    }

    public void setRestrictedMaterial(RestrictedMaterial restrictedMaterial) {
        this.restrictedMaterial = restrictedMaterial;
    }

    public boolean isSalesTaxIndicator() {
        return salesTaxIndicator;
    }

    public void setSalesTaxIndicator(boolean salesTaxIndicator) {
        this.salesTaxIndicator = salesTaxIndicator;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purchasingCommodityCode",this.purchasingCommodityCode);
        m.put("commodityDescription",this.commodityDescription);
        return null;
    }

}
