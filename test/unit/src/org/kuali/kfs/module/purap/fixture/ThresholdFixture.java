/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.businessobject.ReceivingThreshold;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum ThresholdFixture {

    CHARTCODE(PurapTestConstants.Threshold.CHART_CODE, 
              null,
              null,
              null,
              null,
              null,
              null,
              PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
    
    CHARTCODE_INVALID(PurapTestConstants.Threshold.CHART_CODE_INVALID, 
                      null,
                      null,
                      null,
                      null,
                      null,
                      null,
                      PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
    
    CHARTCODE_AND_ACCOUNTTYPE(PurapTestConstants.Threshold.CHART_CODE, 
                              PurapTestConstants.Threshold.ACCOUNT_TYPE,
                              null,
                              null,
                              null,
                              null,
                              null,
                              PurapTestConstants.Threshold.THRESHOLD_AMOUNT), 
                          
    CHARTCODE_AND_SUBACCOUNTTYPE(PurapTestConstants.Threshold.CHART_CODE, 
                                 null,
                                 PurapTestConstants.Threshold.SUBACCOUNT_TYPE,
                                 null,
                                 null,
                                 null,
                                 null,
                                 PurapTestConstants.Threshold.THRESHOLD_AMOUNT),

     CHARTCODE_AND_SUBACCOUNTTYPE_INVALID(PurapTestConstants.Threshold.CHART_CODE, 
                                          null,
                                          PurapTestConstants.Threshold.SUBACCOUNT_TYPE_INVALID,
                                          null,
                                          null,
                                          null,
                                          null,
                                          PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
                                         
    CHARTCODE_AND_ACCOUNTTYPE_AND_SUBACCOUNTTYPE(PurapTestConstants.Threshold.CHART_CODE, 
                                                 PurapTestConstants.Threshold.ACCOUNT_TYPE,
                                                 PurapTestConstants.Threshold.SUBACCOUNT_TYPE,
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
                                    
    CHARTCODE_AND_COMMODITYCODE(PurapTestConstants.Threshold.CHART_CODE, 
                                null,
                                null,
                                PurapTestConstants.Threshold.COMMODITY_CODE,
                                null,
                                null,
                                null,
                                PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
                                
    CHARTCODE_AND_COMMODITYCODE_INVALID(PurapTestConstants.Threshold.CHART_CODE, 
                                        null,
                                        null,
                                        PurapTestConstants.Threshold.COMMODITY_CODE_INVALID,
                                        null,
                                        null,
                                        null,
                                        PurapTestConstants.Threshold.THRESHOLD_AMOUNT),   
                                
    CHARTCODE_AND_OBJECTCODE(PurapTestConstants.Threshold.CHART_CODE, 
                             null,
                             null,
                             null,
                             PurapTestConstants.Threshold.OBJECT_CODE,
                             null,
                             null,
                             PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
                             
     CHARTCODE_AND_OBJECTCODE_INVALID(PurapTestConstants.Threshold.CHART_CODE, 
                                      null,
                                      null,
                                      null,
                                      PurapTestConstants.Threshold.OBJECT_CODE_INVALID,
                                      null,
                                      null,
                                      PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
                                     
    CHARTCODE_AND_ORGCODE(PurapTestConstants.Threshold.CHART_CODE, 
                          null,
                          null,
                          null,
                          null,
                          PurapTestConstants.Threshold.ORG_CODE,
                          null,
                          PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
                          
    CHARTCODE_AND_ORGCODE_INVALID(PurapTestConstants.Threshold.CHART_CODE, 
                                  null,
                                  null,
                                  null,
                                  null,
                                  PurapTestConstants.Threshold.ORG_CODE_INVALID,
                                  null,
                                  PurapTestConstants.Threshold.THRESHOLD_AMOUNT),                          
                            
     CHARTCODE_AND_VENDORNUMBER(PurapTestConstants.Threshold.CHART_CODE, 
                                null,
                                null,
                                null,
                                null,
                                null,
                                PurapTestConstants.Threshold.VENDOR_NUMBER,
                                PurapTestConstants.Threshold.THRESHOLD_AMOUNT),
                                
    CHARTCODE_AND_VENDORNUMBER_INVALID(PurapTestConstants.Threshold.CHART_CODE, 
                                       null,
                                       null,
                                       null,
                                       null,
                                       null,
                                       PurapTestConstants.Threshold.VENDOR_NUMBER_INVALID,
                                       PurapTestConstants.Threshold.THRESHOLD_AMOUNT);
                                
    public final String chartOfAccount;
    public final String accountType;
    public final String subAccountType;
    public final String commodityCode;
    public final String financialObjectCode;
    public final String orgCode;
    public final String vendorNumber;
    public final KualiDecimal thresholdAmount;

    private ThresholdFixture(String chartOfAccount, 
                             String accountType, 
                             String subAccountType, 
                             String commodityCode, 
                             String financialObjectCode, 
                             String orgCode, 
                             String vendorNumber, 
                             KualiDecimal thresholdAmount) {
        this.chartOfAccount = chartOfAccount;
        this.accountType = accountType;
        this.subAccountType = subAccountType;
        this.commodityCode = commodityCode;
        this.financialObjectCode = financialObjectCode;
        this.orgCode = orgCode;
        this.vendorNumber = vendorNumber;
        this.thresholdAmount = thresholdAmount;
    }

    public ReceivingThreshold getThresholdBO() {
        ReceivingThreshold threshold = new ReceivingThreshold();
        threshold.setChartOfAccountsCode(chartOfAccount);
        threshold.setAccountTypeCode(accountType);
        threshold.setSubFundGroupCode(subAccountType);
        threshold.setPurchasingCommodityCode(commodityCode);
        threshold.setFinancialObjectCode(financialObjectCode);
        threshold.setOrganizationCode(orgCode);
        threshold.setVendorNumber(vendorNumber);
        threshold.setThresholdAmount(thresholdAmount);
        return threshold;
    }

}
