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
package org.kuali.module.purap.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AcctType;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.Threshold;
import org.kuali.module.purap.util.ThresholdField;
import org.kuali.module.vendor.bo.CommodityCode;
import org.kuali.module.vendor.bo.VendorDetail;

public class ThresholdRule extends MaintenanceDocumentRuleBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ThresholdRule.class);
    private ChartService chartService;
    private AccountService accountService;
    private Threshold newThreshold;
    private ThresholdField fieldToValidate;
    
    public ThresholdRule(){
        chartService = SpringContext.getBean(ChartService.class);
        accountService = SpringContext.getBean(AccountService.class);
    }
    
    @Override
    protected boolean isDocumentValidForSave(MaintenanceDocument document) {
        if (document.isNew() || document.isEdit() || document.isNewWithExisting()) {
            newThreshold = (Threshold) document.getNewMaintainableObject().getBusinessObject();
            return isValidDocument(newThreshold);
        }
        return  true;
    }
    
    private boolean isValidDocument(Threshold threshold){
        
        boolean valid = isValidThresholdCriteria(threshold);
        
        if (!valid){
            constructFieldError(threshold);
            return false;
        }
        
        valid = isValidChartCode(threshold);
        
        if (fieldToValidate == ThresholdField.CHART_OF_ACCOUNTS_CODE || !valid){
            return valid;
        }
        
        valid = isValidSubFund(threshold) &&
                isValidCommodityCode(threshold) &&
                isValidObjectCode(threshold) &&
                isValidOrgCode(threshold) &&
                isValidVendorNumber(threshold);
        
        return valid;
    }
    
    private void constructFieldError(Threshold threshold){
        
        if (StringUtils.isNotBlank(threshold.getAccountTypeCode())){
            putFieldError(ThresholdField.ACCOUNT_TYPE_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getSubFundGroupCode())){
            putFieldError(ThresholdField.SUBFUND_GROUP_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getPurchasingCommodityCode())){
            putFieldError(ThresholdField.COMMODITY_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getFinancialObjectCode())){
            putFieldError(ThresholdField.FINANCIAL_OBJECT_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getOrganizationCode())){
            putFieldError(ThresholdField.ORGANIZATION_CODE.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        if (StringUtils.isNotBlank(threshold.getVendorNumber())){
            putFieldError(ThresholdField.VENDOR_NUMBER.getName(), PurapKeyConstants.INVALID_THRESHOLD_CRITERIA);
        }
        
    }
    
    private boolean isValidChartCode(Threshold threshold){
        if (StringUtils.isNotBlank(threshold.getChartOfAccountsCode())){
            Map pkMap = new HashMap();
            pkMap.put(ThresholdField.CHART_OF_ACCOUNTS_CODE.getName(), newThreshold.getChartOfAccountsCode());
    
            Chart chart = (Chart) getBoService().findByPrimaryKey(Chart.class, pkMap);
            if (chart == null) {
               putFieldError(ThresholdField.CHART_OF_ACCOUNTS_CODE.getName(), PurapKeyConstants.THRESHOLD_FIELD_INVALID, newThreshold.getChartOfAccountsCode());
               return false;
            }else{
               return true;
            }
        }
        return false;
    }
    
    private boolean isValidSubFund(Threshold threshold){
    
        if (StringUtils.isNotBlank(threshold.getSubFundGroupCode())){
            Map pkMap = new HashMap();
            pkMap.put(ThresholdField.SUBFUND_GROUP_CODE.getName(), newThreshold.getSubFundGroupCode());
            SubFundGroup subFundGroup = (SubFundGroup) getBoService().findByPrimaryKey(SubFundGroup.class, pkMap);
            if (subFundGroup == null) {
               putFieldError(ThresholdField.SUBFUND_GROUP_CODE.getName(), PurapKeyConstants.THRESHOLD_FIELD_INVALID, newThreshold.getSubFundGroupCode());
               return false;
            }
        }
        return true;
    }
    
    private boolean isValidCommodityCode(Threshold threshold){
        
        if (StringUtils.isNotBlank(threshold.getPurchasingCommodityCode())){
            Map pkMap = new HashMap();
            pkMap.put(ThresholdField.COMMODITY_CODE.getName(), newThreshold.getPurchasingCommodityCode());
        
            CommodityCode commodityCode = (CommodityCode) getBoService().findByPrimaryKey(CommodityCode.class, pkMap);
            if (commodityCode == null) {
               putFieldError(ThresholdField.COMMODITY_CODE.getName(), PurapKeyConstants.THRESHOLD_FIELD_INVALID, newThreshold.getPurchasingCommodityCode());
               return false;
            }
        }
        return true;
    }

    private boolean isValidObjectCode(Threshold threshold){
        
        if (StringUtils.isNotBlank(threshold.getFinancialObjectCode())){
            Map pkMap = new HashMap();
            pkMap.put(ThresholdField.FINANCIAL_OBJECT_CODE.getName(), newThreshold.getFinancialObjectCode());
        
            ObjectCode objectCode = (ObjectCode) getBoService().findByPrimaryKey(ObjectCode.class, pkMap);
            if (objectCode == null) {
               putFieldError(ThresholdField.FINANCIAL_OBJECT_CODE.getName(), PurapKeyConstants.THRESHOLD_FIELD_INVALID, newThreshold.getFinancialObjectCode());
               return false;
            }
        }
        return true;
    }
    
    private boolean isValidOrgCode(Threshold threshold){
        
        if (StringUtils.isNotBlank(threshold.getOrganizationCode())){
            Map pkMap = new HashMap();
            pkMap.put(ThresholdField.ORGANIZATION_CODE.getName(), newThreshold.getOrganizationCode());
        
            Org org = (Org) getBoService().findByPrimaryKey(Org.class, pkMap);
            if (org == null) {
               putFieldError(ThresholdField.ORGANIZATION_CODE.getName(), PurapKeyConstants.THRESHOLD_FIELD_INVALID, newThreshold.getOrganizationCode());
               return false;
            }
        }
        return true;
    }
    
    private boolean isValidVendorNumber(Threshold threshold){
        
        if (StringUtils.isNotBlank(threshold.getVendorNumber())){
            Map keys = new HashMap();
            keys.put(ThresholdField.VENDOR_HEADER_GENERATED_ID.getName(), threshold.getVendorHeaderGeneratedIdentifier());
            keys.put(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID.getName(), threshold.getVendorDetailAssignedIdentifier());
            
            VendorDetail vendorDetail = (VendorDetail) getBoService().findByPrimaryKey(VendorDetail.class, keys);
            if (vendorDetail == null) {
                putFieldError(ThresholdField.VENDOR_NUMBER.getName(), PurapKeyConstants.THRESHOLD_FIELD_INVALID, newThreshold.getVendorNumber());
                return false;
            }
        }
        return true;
    }
    
    private boolean isValidThresholdCriteria(Threshold threshold){
        
        if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
            StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
            StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
            StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
            StringUtils.isBlank(threshold.getOrganizationCode()) && 
            StringUtils.isBlank(threshold.getVendorNumber())){
                
            fieldToValidate = ThresholdField.CHART_OF_ACCOUNTS_CODE;
            return true;
            
        }else if (StringUtils.isNotBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) && 
                  StringUtils.isBlank(threshold.getVendorNumber())){
            
                fieldToValidate = ThresholdField.ACCOUNT_TYPE_CODE;
                return true;
            
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isNotBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) && 
                  StringUtils.isBlank(threshold.getVendorNumber())){
            
                  fieldToValidate = ThresholdField.SUBFUND_GROUP_CODE;
                  return true;
                  
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isNotBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) && 
                  StringUtils.isBlank(threshold.getVendorNumber())){
            
                  fieldToValidate = ThresholdField.COMMODITY_CODE;
                  return true;
                  
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isNotBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) && 
                  StringUtils.isBlank(threshold.getVendorNumber())){
          
                  fieldToValidate = ThresholdField.FINANCIAL_OBJECT_CODE;
                  return true;
                
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isNotBlank(threshold.getOrganizationCode()) && 
                  StringUtils.isBlank(threshold.getVendorNumber())){
        
                  fieldToValidate = ThresholdField.ORGANIZATION_CODE;
                  return true;
              
        }else if (StringUtils.isBlank(threshold.getAccountTypeCode()) &&
                  StringUtils.isBlank(threshold.getSubFundGroupCode()) &&
                  StringUtils.isBlank(threshold.getPurchasingCommodityCode()) &&
                  StringUtils.isBlank(threshold.getFinancialObjectCode()) &&
                  StringUtils.isBlank(threshold.getOrganizationCode()) && 
                  StringUtils.isNotBlank(threshold.getVendorNumber())){
      
                  fieldToValidate = ThresholdField.VENDOR_HEADER_GENERATED_ID;
                  return true;
        }
        
        return false;
        
    }
    
}
