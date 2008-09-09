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
package org.kuali.kfs.vnd.document.validation.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.Threshold;
import org.kuali.kfs.module.purap.util.ThresholdField;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

public class PurchaseOrderCostSourceRule extends MaintenanceDocumentRuleBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderCostSourceRule.class);
    private PurchaseOrderCostSource newPurchaseOrderCostSource;
    
    public PurchaseOrderCostSourceRule(){
    }
    
    @Override
    protected boolean isDocumentValidForSave(MaintenanceDocument document) {
        if (document.isNew() || document.isEdit() || document.isNewWithExisting()) {
            newPurchaseOrderCostSource = (PurchaseOrderCostSource) document.getNewMaintainableObject().getBusinessObject();
            return isValidDocument(newPurchaseOrderCostSource);
        }
        return  true;
    }
    
    private boolean isValidDocument(PurchaseOrderCostSource purchaseOrderCostSource){
        
        if (purchaseOrderCostSource.getItemUnitPriceLowerVariancePercent() == null &&
            purchaseOrderCostSource.getItemUnitPriceUpperVariancePercent() == null){
            putFieldError(VendorPropertyConstants.ITEM_UNIT_PRICE_UPPER_VARIANCE_PERCENT, VendorKeyConstants.ERROR_ITEM_UNIT_PRICE_VARIANCE_PERCENT_EMPTY);
            return false;
        }else if (purchaseOrderCostSource.getItemUnitPriceLowerVariancePercent() != null &&
                  purchaseOrderCostSource.getItemUnitPriceUpperVariancePercent() != null){
            putFieldError(VendorPropertyConstants.ITEM_UNIT_PRICE_UPPER_VARIANCE_PERCENT, VendorKeyConstants.ERROR_ITEM_UNIT_PRICE_VARIANCE_PERCENT_REQUIRED);
            return false;
        }
        
        if (!isValidPercent(purchaseOrderCostSource.getItemUnitPriceLowerVariancePercent())){
            putFieldError(VendorPropertyConstants.ITEM_UNIT_PRICE_LOWER_VARIANCE_PERCENT, VendorKeyConstants.ERROR_ITEM_UNIT_PRICE_VARIANCE_PERCENT_INVALID);
            return false;
        }else if (!isValidPercent(purchaseOrderCostSource.getItemUnitPriceUpperVariancePercent())){
            putFieldError(VendorPropertyConstants.ITEM_UNIT_PRICE_UPPER_VARIANCE_PERCENT, VendorKeyConstants.ERROR_ITEM_UNIT_PRICE_VARIANCE_PERCENT_INVALID);
            return false;
        }
        
        return true;
    }
    
    private boolean isValidPercent(BigDecimal percent){
        if (percent != null){
            if (percent.intValue() < 0){
                return false;
            }else if (percent.intValue() > 100){
                return false;
            }
        }
        return true;
    }
    
}
