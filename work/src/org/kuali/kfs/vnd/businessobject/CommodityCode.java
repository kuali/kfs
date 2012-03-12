/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.vnd.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.purap.PurchasingAccountsPayableSensitiveData;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * CommodityCode Business Object
 */
public class CommodityCode extends PersistableBusinessObjectBase implements MutableInactivatable  {
    
    private String purchasingCommodityCode;
    private String commodityDescription;
    private boolean salesTaxIndicator;
    private boolean restrictedItemsIndicator;
    private String sensitiveDataCode;
    private boolean active;
    
    private PurchasingAccountsPayableSensitiveData sensitiveData;
    
    private List<CommodityContractManager> commodityContractManagers;
    
    public CommodityCode() {
        commodityContractManagers = new ArrayList<CommodityContractManager>();
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
    
    public String getSensitiveDataCode() {
        return sensitiveDataCode;
    }

    public void setSensitiveDataCode(String sensitiveDataCode) {
        this.sensitiveDataCode = sensitiveDataCode;
    }

    public PurchasingAccountsPayableSensitiveData getSensitiveData() {
        /*if (StringUtils.isBlank(sensitiveDataCode)) {
            if (sensitiveData != null) {
                sensitiveData = null;
            }
        } else {
            if (sensitiveData == null || !sensitiveData.getSensitiveDataCode().equals(this.sensitiveDataCode)) {
                sensitiveData = SpringContext.getBean(PurchasingAccountsPayableModuleService.class).getSensitiveDataByCode(this.sensitiveDataCode);
            }
        }
        return sensitiveData;*/
        return sensitiveData = (PurchasingAccountsPayableSensitiveData)SpringContext.getBean(KualiModuleService.class)
                                .getResponsibleModuleService(PurchasingAccountsPayableSensitiveData.class)
                                .retrieveExternalizableBusinessObjectIfNecessary(this, sensitiveData, "sensitiveData");
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(VendorPropertyConstants.PURCHASING_COMMODITY_CODE, this.purchasingCommodityCode);
        m.put(VendorPropertyConstants.COMMODITY_DESCRIPTION, this.commodityDescription);
        return null;
    }

}
