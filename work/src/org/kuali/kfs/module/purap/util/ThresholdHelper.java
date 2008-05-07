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
package org.kuali.module.purap.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.enums.Enum;
import org.apache.log4j.Logger;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.Threshold;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.ThresholdService;

/**
 * A helper class to decide whether to set the receiving document required flag for a purchasing document or not.
 */
public class ThresholdHelper {

    ////////////////////////////////////////////////////////////////////////
    //CLASS VARIABLES
    ////////////////////////////////////////////////////////////////////////
    private static Logger LOG = Logger.getLogger(ThresholdHelper.class);
    
    public static final ThresholdCriteria CHART = new ThresholdCriteria("CHART");
    public static final ThresholdCriteria CHART_AND_FUND = new ThresholdCriteria("CHART_AND_FUND");
    public static final ThresholdCriteria CHART_AND_SUBFUND = new ThresholdCriteria("CHART_AND_SUBFUND");
    public static final ThresholdCriteria CHART_AND_COMMODITYCODE = new ThresholdCriteria("CHART_AND_COMMODITYCODE");
    public static final ThresholdCriteria CHART_AND_OBJECTCODE = new ThresholdCriteria("CHART_AND_OBJECTCODE");
    public static final ThresholdCriteria CHART_AND_ORGCODE = new ThresholdCriteria("CHART_AND_ORGCODE");
    public static final ThresholdCriteria CHART_AND_VENDOR = new ThresholdCriteria("CHART_AND_VENDOR");
    
    ////////////////////////////////////////////////////////////////////////
    //INSTANCE VARIABLES
    ////////////////////////////////////////////////////////////////////////
    private PurapAccountingService purapAccountingService;
    private ThresholdService thresholdService;
    
    private List<ThresholdSummary> chartCodeSummary = new ArrayList();
    private List<ThresholdSummary> chartCodeAndFundSummary = new ArrayList();
    private List<ThresholdSummary> chartCodeAndSubFundSummary = new ArrayList();
    private List<ThresholdSummary> chartCodeAndCommodityCodeSummary = new ArrayList();
    private List<ThresholdSummary> chartCodeAndObjectCodeSummary = new ArrayList();
    private List<ThresholdSummary> chartCodeAndOrgCodeSummary = new ArrayList();
    private List<ThresholdSummary> chartCodeAndVendorSummary = new ArrayList();
    
    public ThresholdHelper(PurchaseOrderDocument document){
        purapAccountingService = SpringContext.getBean(PurapAccountingService.class);
        thresholdService = SpringContext.getBean(ThresholdService.class);
        setupForThresholdCheck(document);
    }
    
    private void setupForThresholdCheck(PurchaseOrderDocument document){
        
        List<SummaryAccount> accounts = purapAccountingService.generateSummaryAccounts(document);
        
        if (accounts != null){

            for (SummaryAccount account : accounts) {
                
                updateThresholdSummary(CHART,account);
                updateThresholdSummary(CHART_AND_FUND,account);
                updateThresholdSummary(CHART_AND_SUBFUND,account);
                updateThresholdSummary(CHART_AND_OBJECTCODE,account);
                updateThresholdSummary(CHART_AND_ORGCODE,account);
                
                processVendorForThresholdSummary(account,
                                                 document.getVendorHeaderGeneratedIdentifier().toString(),
                                                 document.getVendorDetailAssignedIdentifier().toString());
                
            }
        }
        
        processCommodityCodeForThreshold(document.getItems());
        
    }
    
    private void updateThresholdSummary(ThresholdCriteria thresholdCriteria,
                                        SummaryAccount account){
        
        if (thresholdCriteria != CHART_AND_COMMODITYCODE && 
            thresholdCriteria != CHART_AND_VENDOR){
            
            ThresholdSummary thresholdSummary = new ThresholdSummary(thresholdCriteria);
            thresholdSummary.setProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE,
                                         account.getAccount().getChartOfAccountsCode());
            
            if (thresholdCriteria == CHART_AND_FUND){
                account.getAccount().refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                if (StringUtils.isEmpty(account.getAccount().getAccount().getAccountTypeCode())){
                    return;
                }
                thresholdSummary.setProperty(ThresholdField.ACCOUNT_TYPE_CODE,
                                             account.getAccount().getAccount().getAccountTypeCode());
                
            }else if (thresholdCriteria == CHART_AND_SUBFUND){
                account.getAccount().refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                if (StringUtils.isEmpty(account.getAccount().getAccount().getSubFundGroupCode())){
                    return;
                }
                thresholdSummary.setProperty(ThresholdField.SUBFUND_GROUP_CODE,
                                             account.getAccount().getAccount().getSubFundGroupCode());
            }else if (thresholdCriteria == CHART_AND_OBJECTCODE){
                if (StringUtils.isEmpty(account.getAccount().getFinancialObjectCode())){
                    return;
                }
                thresholdSummary.setProperty(ThresholdField.FINANCIAL_OBJECT_CODE,
                                             account.getAccount().getFinancialObjectCode());
            }else if (thresholdCriteria == CHART_AND_ORGCODE){
                account.getAccount().refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                if (StringUtils.isEmpty(account.getAccount().getAccount().getOrganizationCode())){
                    return;
                }
                thresholdSummary.setProperty(ThresholdField.ORGANIZATION_CODE,
                                             account.getAccount().getAccount().getOrganizationCode());
            }
            
            thresholdSummary.addTotalAmount(account.getAccount().getAmount());
            addToSummaryList(thresholdSummary);
        }
    }
    
    private void addToSummaryList(ThresholdSummary thresholdSummary){
        
        List<ThresholdSummary> summaryList = getThresholdSummaryCollection(thresholdSummary.getThresholdCriteria());
        
        boolean matchFound = false;
        for (int i = 0; i < summaryList.size(); i++) {
            if (thresholdSummary.equals(summaryList.get(i))){
                summaryList.get(i).addTotalAmount(thresholdSummary.getTotalAmount());
                matchFound = true;
                break;
            }
        }
        
        if (!matchFound){
            summaryList.add(thresholdSummary);
        }
    }
    
    private List<ThresholdSummary> getThresholdSummaryCollection(ThresholdCriteria thresholdCriteria){
        
        if (thresholdCriteria == CHART){
            return chartCodeSummary;
        }else if (thresholdCriteria == CHART_AND_FUND){
            return chartCodeAndFundSummary;
        }else if (thresholdCriteria == CHART_AND_SUBFUND){
            return chartCodeAndSubFundSummary;
        }else if (thresholdCriteria == CHART_AND_COMMODITYCODE){
            return chartCodeAndCommodityCodeSummary;
        }else if (thresholdCriteria == CHART_AND_OBJECTCODE){
            return chartCodeAndObjectCodeSummary;
        }else if (thresholdCriteria == CHART_AND_ORGCODE){
            return chartCodeAndOrgCodeSummary;
        }else if (thresholdCriteria == CHART_AND_VENDOR){
            return chartCodeAndVendorSummary;
        }
        
        throw new RuntimeException("Invalid ThresholdCriteria Enum - " + thresholdCriteria);
    }
    
    private void processVendorForThresholdSummary(SummaryAccount account,
                                                  String vendorHeaderGeneratedIdentifier,
                                                  String vendorDetailAssignedIdentifier){
        
        ThresholdSummary thresholdSummary = new ThresholdSummary(CHART_AND_VENDOR);
        thresholdSummary.setProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE,account.getAccount().getChartOfAccountsCode());
        thresholdSummary.setProperty(ThresholdField.VENDOR_HEADER_GENERATED_ID,vendorHeaderGeneratedIdentifier);
        thresholdSummary.setProperty(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID,vendorDetailAssignedIdentifier);
        thresholdSummary.addTotalAmount(account.getAccount().getAmount());
        
        addToSummaryList(thresholdSummary);
        
    }
    
    private void processCommodityCodeForThreshold(List<PurchaseOrderItem> items){
        if (items != null){
            for (PurchaseOrderItem item : items) {
                if (item.isItemActiveIndicator()){
                    List<PurApAccountingLine> accountingLines = item.getSourceAccountingLines();
                    for (int i = 0; i < accountingLines.size(); i++) {
                        if (StringUtils.isNotBlank(item.getPurchasingCommodityCode())){
                            ThresholdSummary thresholdSummary = new ThresholdSummary(CHART_AND_COMMODITYCODE);
                            thresholdSummary.setProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE,accountingLines.get(i).getChartOfAccountsCode());
                            thresholdSummary.setProperty(ThresholdField.COMMODITY_CODE,item.getPurchasingCommodityCode());
                            thresholdSummary.addTotalAmount(item.getExtendedPrice());
                            addToSummaryList(thresholdSummary);
                        }
                    }
                }
            }
        }
    }
 
    public boolean isReceivingDocumentRequired(){
        for (ThresholdCriteria thresholdEnum : ThresholdCriteria.getEnumList()) {
            boolean result = isReceivingDocumentRequired(thresholdEnum);
            if (result){
                return true;
            }
        }
        return false;
    }
    
    private boolean isReceivingDocumentRequired(ThresholdCriteria thresholdEnum){
        
        List<ThresholdSummary> summaryList = getThresholdSummaryCollection(thresholdEnum);
        
        if (summaryList != null){
            for (ThresholdSummary thresholdSummary : summaryList) {
                Collection collection = null;
                
                if (thresholdEnum == CHART){
                    collection = thresholdService.findByChart(thresholdSummary.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE));
                }else if (thresholdEnum == CHART_AND_FUND){
                    collection = thresholdService.findByChartAndFund(thresholdSummary.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                                                     thresholdSummary.getProperty(ThresholdField.ACCOUNT_TYPE_CODE));
                }else if (thresholdEnum == CHART_AND_SUBFUND){
                    collection = thresholdService.findByChartAndSubFund(thresholdSummary.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                                                        thresholdSummary.getProperty(ThresholdField.SUBFUND_GROUP_CODE));
                }else if (thresholdEnum == CHART_AND_COMMODITYCODE){
                    collection = thresholdService.findByChartAndCommodity(thresholdSummary.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                                                        thresholdSummary.getProperty(ThresholdField.COMMODITY_CODE));
                }else if (thresholdEnum == CHART_AND_OBJECTCODE){
                    collection = thresholdService.findByChartAndObjectCode(thresholdSummary.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                                                           thresholdSummary.getProperty(ThresholdField.FINANCIAL_OBJECT_CODE));
                }else if (thresholdEnum == CHART_AND_ORGCODE){
                    collection = thresholdService.findByChartAndOrg(thresholdSummary.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                                                    thresholdSummary.getProperty(ThresholdField.ORGANIZATION_CODE));
                }else if (thresholdEnum == CHART_AND_VENDOR){
                    collection = thresholdService.findByChartAndVendor(thresholdSummary.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                                                       thresholdSummary.getProperty(ThresholdField.VENDOR_HEADER_GENERATED_ID),
                                                                       thresholdSummary.getProperty(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID));
                }
                
                if (collection != null){
                    for (Threshold threshold :(List<Threshold>) collection){
                        if (threshold.getThresholdAmount() == null ||
                            threshold.getThresholdAmount().isLessThan(thresholdSummary.getTotalAmount())){
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
        
    }

    private class ThresholdSummary {
        
        private ThresholdCriteria thresholdCriteria;
        private Map<ThresholdField,String> property2Value = new HashMap<ThresholdField,String>(); 
        private KualiDecimal totalAmount = KualiDecimal.ZERO;
        
        ThresholdSummary(ThresholdCriteria thresholdCriteria){
            this.thresholdCriteria = thresholdCriteria;
        }
        
        void setProperty(ThresholdField thresholdField,
                         String fieldValue){
            if (!isValidProperty(thresholdField)){
                throw new RuntimeException("Property[" + thresholdField + "] not allowed for the threshold criteria[" + thresholdCriteria + "]");
            }
            
            property2Value.put(thresholdField,fieldValue);
        }
        
        String getProperty(ThresholdField thresholdEnum){
            return property2Value.get(thresholdEnum);
        }

        ThresholdCriteria getThresholdCriteria() {
            return thresholdCriteria;
        }

        KualiDecimal getTotalAmount() {
            return totalAmount;
        }

        void addTotalAmount(KualiDecimal totalAmount) {
            if (totalAmount != null){
                this.totalAmount = this.totalAmount.add(totalAmount);
            }
        }
        
        boolean isValidProperty(ThresholdField thresholdField){
            
            if (getThresholdCriteria() == CHART && 
                ThresholdField.CHART_OF_ACCOUNTS_CODE == thresholdField){
                return true;
            }else if ((getThresholdCriteria() == CHART_AND_FUND) && 
                      (ThresholdField.CHART_OF_ACCOUNTS_CODE == thresholdField ||
                       ThresholdField.ACCOUNT_TYPE_CODE == thresholdField)){
                return true;
            }else if ((getThresholdCriteria() == CHART_AND_SUBFUND) && 
                      (ThresholdField.CHART_OF_ACCOUNTS_CODE == thresholdField ||
                       ThresholdField.SUBFUND_GROUP_CODE == thresholdField)){
                return true;
            }else if ((getThresholdCriteria() == CHART_AND_COMMODITYCODE) && 
                      (ThresholdField.CHART_OF_ACCOUNTS_CODE == thresholdField ||
                       ThresholdField.COMMODITY_CODE == thresholdField)){
                return true;
            }else if ((getThresholdCriteria() == CHART_AND_OBJECTCODE) && 
                      (ThresholdField.CHART_OF_ACCOUNTS_CODE == thresholdField ||
                       ThresholdField.FINANCIAL_OBJECT_CODE == thresholdField)){
                return true;
            }else if ((getThresholdCriteria() == CHART_AND_ORGCODE) && 
                      (ThresholdField.CHART_OF_ACCOUNTS_CODE == thresholdField ||
                       ThresholdField.ORGANIZATION_CODE == thresholdField)){
               return true;
            }else if ((getThresholdCriteria() == CHART_AND_VENDOR) && 
                      (ThresholdField.CHART_OF_ACCOUNTS_CODE == thresholdField ||
                       ThresholdField.VENDOR_HEADER_GENERATED_ID == thresholdField || 
                       ThresholdField.VENDOR_DETAIL_ASSIGNED_ID == thresholdField)){
               return true;
            }
            
            return false;
        }
        
        @Override
        public boolean equals(Object obj){
            
            if (obj != null){
                
                if (!(obj instanceof ThresholdSummary)){
                  return false;
                }   
                
                ThresholdSummary thresholdItem = (ThresholdSummary)obj;
                
                if (getThresholdCriteria() == thresholdItem.getThresholdCriteria()){
                    
                    if (getThresholdCriteria() == CHART){
                        
                        if (StringUtils.equals(property2Value.get(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                               thresholdItem.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE))){
                            return true;
                        }
                        
                    }else if (getThresholdCriteria() == CHART_AND_FUND){
                        
                        if (StringUtils.equals(property2Value.get(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                               thresholdItem.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE)) &&
                            StringUtils.equals(property2Value.get(ThresholdField.ACCOUNT_TYPE_CODE),
                                               thresholdItem.getProperty(ThresholdField.ACCOUNT_TYPE_CODE))){
                            return true;
                        }
                        
                    }else if (getThresholdCriteria() == CHART_AND_SUBFUND){
                        
                        if (StringUtils.equals(property2Value.get(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                               thresholdItem.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE)) &&
                            StringUtils.equals(property2Value.get(ThresholdField.SUBFUND_GROUP_CODE),
                                               thresholdItem.getProperty(ThresholdField.SUBFUND_GROUP_CODE))){
                            return true;
                        }
                        
                    }else if (getThresholdCriteria() == CHART_AND_COMMODITYCODE){
                        
                        if (StringUtils.equals(property2Value.get(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                               thresholdItem.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE)) &&
                            StringUtils.equals(property2Value.get(ThresholdField.COMMODITY_CODE),
                                               thresholdItem.getProperty(ThresholdField.COMMODITY_CODE))){
                            return true;
                        }
                        
                    }else if (getThresholdCriteria() == CHART_AND_OBJECTCODE){
                        
                        if (StringUtils.equals(property2Value.get(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                               thresholdItem.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE)) &&
                            StringUtils.equals(property2Value.get(ThresholdField.FINANCIAL_OBJECT_CODE),
                                               thresholdItem.getProperty(ThresholdField.FINANCIAL_OBJECT_CODE))){
                            return true;
                        }
                        
                    }else if (getThresholdCriteria() == CHART_AND_ORGCODE){
                        
                        if (StringUtils.equals(property2Value.get(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                               thresholdItem.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE)) &&
                            StringUtils.equals(property2Value.get(ThresholdField.ORGANIZATION_CODE),
                                               thresholdItem.getProperty(ThresholdField.ORGANIZATION_CODE))){
                            return true;
                        }
                        
                    }else if (getThresholdCriteria() == CHART_AND_VENDOR){
                        
                        if (StringUtils.equals(property2Value.get(ThresholdField.CHART_OF_ACCOUNTS_CODE),
                                               thresholdItem.getProperty(ThresholdField.CHART_OF_ACCOUNTS_CODE)) &&
                            StringUtils.equals(property2Value.get(ThresholdField.VENDOR_HEADER_GENERATED_ID),
                                               thresholdItem.getProperty(ThresholdField.VENDOR_HEADER_GENERATED_ID)) &&
                            StringUtils.equals(property2Value.get(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID),
                                               thresholdItem.getProperty(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID))){
                            return true;
                        }
                        
                    }
                }
            }
            return false;
        }
        
        @Override
        public String toString(){
            ToStringBuilder stringBuilder = new ToStringBuilder(this);
            stringBuilder.append("ThresholdCriteria",getThresholdCriteria().getName());
            stringBuilder.append("Amount",getTotalAmount());
            stringBuilder.append("Field2Values",property2Value);
            
            return stringBuilder.toString();
        }
    }
}

final class ThresholdCriteria extends Enum {

    ThresholdCriteria(String name) {
        super(name);
    }
  
    public static List<ThresholdCriteria> getEnumList() {
        return getEnumList(ThresholdCriteria.class);
    }
}
