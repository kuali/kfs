/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PooledFundControlTransactionFixture {

    ASSET_INCOME_DATA("TSTSECID1","Pooled Short Term Fund Income Test",new Integer(0),"RC1","TSTKEMID1","01205","01205","78100","42010",false,false,true,
            new KualiDecimal(1110.00),"PURCHASE_DESCRIPTION","T", "PURCHASE_NO_ROUTE_IND", "P"),
    ASSET_PURCHASE_DATA("TSTSECID2","Pooled Short Term Fund Income Test",new Integer(0),"RC2","TSTKEMID2","01205","01205","78100","42010",false,false,true,
        new KualiDecimal(1110.00),"PURCHASE_DESCRIPTION","F", "PURCHASE_NO_ROUTE_IND", "I"),             
    CAPITAL_GAIN_LOSS_DISTRIBUTION_TRANSACTION_COMMITTED("DUMMYID", //pooledSecurityID
            "Pooled Short Term Fund Income Test", //pooledFundDescription
            new Integer(0), //incrementValuationDays
            "2TST", //fundRegistrationCode
            "TEST_KEMID", //fundKEMID
            "01205", //fundAssetPurchaseOffsetTranCode
            "01205", //fundAssetSaleOffsetTranCode
            "78100", //fundSaleGainLossOffsetTranCode
            "42010", //fundCashDepositOffsetTranCode
            false, //distributeGainsAndLossesIndicator
            false, //allowFractionalShares
            true, //active
            new KualiDecimal(1110.00), //totalAmount
            "PURCHASE_DESCRIPTION", //paramDescriptionName
            "F", //securityLineType
            "PURCHASE_NO_ROUTE_IND", //paramNoRouteInd
            "I" //incomePrincipalIndicator
      );
    
    // Pooled Fund Control 
    private String pooledSecurityID;
    private String pooledFundDescription;
    private Integer incrementValuationDays;
    private String fundRegistrationCode;
    private String fundKEMID;
    private String fundAssetPurchaseOffsetTranCode;
    private String fundAssetSaleOffsetTranCode;
    private String fundSaleGainLossOffsetTranCode;
    private String fundCashDepositOffsetTranCode;
    private boolean distributeGainsAndLossesIndicator;
    private boolean allowFractionalShares;
    private boolean active;
    
    private KualiDecimal totalAmount;
    private String paramDescriptionName;
    private String securityLineType;
    private String paramNoRouteInd;
    private String incomePrincipalIndicator;
    
    private PooledFundControlTransactionFixture() {};
    
    private PooledFundControlTransactionFixture(    
        String pooledSecurityID, 
        String pooledFundDescription, 
        Integer incrementValuationDays, 
        String fundRegistrationCode, 
        String fundKEMID, 
        String fundAssetPurchaseOffsetTranCode, 
        String fundAssetSaleOffsetTranCode, 
        String fundSaleGainLossOffsetTranCode, 
        String fundCashDepositOffsetTranCode, 
        boolean distributeGainsAndLossesIndicator, 
        boolean allowFractionalShares, 
        boolean active,     
        KualiDecimal totalAmount,
        String paramDescriptionName,
        String securityLineType,
        String paramNoRouteInd,
        String incomePrincipalIndicator) {
        
        this.pooledSecurityID = pooledSecurityID;
        this.pooledFundDescription = pooledFundDescription;
        this.incrementValuationDays = incrementValuationDays;
        this.fundRegistrationCode = fundRegistrationCode;
        this.fundKEMID = fundKEMID;
        this.fundAssetPurchaseOffsetTranCode = fundAssetPurchaseOffsetTranCode;
        this.fundAssetSaleOffsetTranCode = fundAssetSaleOffsetTranCode;
        this.fundSaleGainLossOffsetTranCode = fundSaleGainLossOffsetTranCode;
        this.fundCashDepositOffsetTranCode = fundCashDepositOffsetTranCode;
        this.distributeGainsAndLossesIndicator = distributeGainsAndLossesIndicator;
        this.allowFractionalShares = allowFractionalShares;
        this.active = active;
        
        this.totalAmount = totalAmount;
        this.paramDescriptionName = paramDescriptionName;
        this.securityLineType = securityLineType;
        this.paramNoRouteInd = paramNoRouteInd;
        this.incomePrincipalIndicator = incomePrincipalIndicator;        
    }

    public PooledFundControl createPooledFundControl() {
        
        PooledFundControl pooledFundControl = new PooledFundControl();
        pooledFundControl.setPooledSecurityID(pooledSecurityID);
        pooledFundControl.setPooledFundDescription(pooledFundDescription);
        pooledFundControl.setIncrementValuationDays(incrementValuationDays);
        pooledFundControl.setFundRegistrationCode(fundRegistrationCode);
        pooledFundControl.setFundKEMID(fundKEMID);
        pooledFundControl.setFundAssetPurchaseOffsetTranCode(fundAssetPurchaseOffsetTranCode);
        pooledFundControl.setFundAssetSaleOffsetTranCode(fundAssetSaleOffsetTranCode);
        pooledFundControl.setFundSaleGainLossOffsetTranCode(fundSaleGainLossOffsetTranCode);
        pooledFundControl.setFundCashDepositOffsetTranCode(fundCashDepositOffsetTranCode);
        pooledFundControl.setDistributeGainsAndLossesIndicator(distributeGainsAndLossesIndicator);
        pooledFundControl.setAllowFractionalShares(allowFractionalShares);
        pooledFundControl.setActive(active);
                
        return pooledFundControl;
    }
    
    public PooledFundControl createSavePooledFundControl() {
    
        PooledFundControl pooledFundControl = new PooledFundControl();
        pooledFundControl.setPooledSecurityID(pooledSecurityID);
        pooledFundControl.setPooledFundDescription(pooledFundDescription);
        pooledFundControl.setIncrementValuationDays(incrementValuationDays);
        pooledFundControl.setFundRegistrationCode(fundRegistrationCode);
        pooledFundControl.setFundKEMID(fundKEMID);
        pooledFundControl.setFundAssetPurchaseOffsetTranCode(fundAssetPurchaseOffsetTranCode);
        pooledFundControl.setFundAssetSaleOffsetTranCode(fundAssetSaleOffsetTranCode);
        pooledFundControl.setFundSaleGainLossOffsetTranCode(fundSaleGainLossOffsetTranCode);
        pooledFundControl.setFundCashDepositOffsetTranCode(fundCashDepositOffsetTranCode);
        pooledFundControl.setDistributeGainsAndLossesIndicator(distributeGainsAndLossesIndicator);
        pooledFundControl.setAllowFractionalShares(allowFractionalShares);
        pooledFundControl.setActive(active);
                
        savePooledFundControl(pooledFundControl);
        
        return pooledFundControl;
    }
    
    public void savePooledFundControl(PooledFundControl pooledFundControl) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(pooledFundControl);
    }
    
    /**
     * gets totalAmount
     */
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * gets parameterDescriptionName
     */
    public String getParamDescriptionName() {
        return paramDescriptionName;
    }

    /**
     * gets securityLineType
     */
    public String getSecurityLineType() {
        return securityLineType;
    }

    /**
     * gets paramNoRouteInd
     */
    public String getParamNoRouteInd() {
        return paramNoRouteInd;
    }

    /**
     * gets incomePrincipalIndicator
     */
    public String getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }
    
    
}
