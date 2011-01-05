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
import org.kuali.rice.kns.util.KualiDecimal;

public enum PooledFundControlTransactionFixture {

    ECDD_DATA("99PSTF018","Pooled Short Term Fund Income Test",new Integer(0),"0AI","099PSTF018","01205","01205","78100","42010",false,false,true,
                new KualiDecimal(1110.00),"PURCHASE_DESCRIPTION","T", "PURCHASE_NO_ROUTE_IND", "P"),
    ECI_DATA("99PSTF018","Pooled Short Term Fund Income Test",new Integer(0),"0AI","099PSTF018","01205","01205","78100","42010",false,false,true,
            new KualiDecimal(1110.00),"PURCHASE_DESCRIPTION","F", "PURCHASE_NO_ROUTE_IND", "I");
    
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
