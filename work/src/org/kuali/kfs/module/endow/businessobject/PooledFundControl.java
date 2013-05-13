/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for Pooled Fund Control table.
 */
public class PooledFundControl extends PersistableBusinessObjectBase implements MutableInactivatable {
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

    private Security security;
    private RegistrationCode  registrationCodeObj;
    private KEMID kemid;
    private EndowmentTransactionCode assetPurchaseOffsetTranCode;
    private EndowmentTransactionCode assetSaleOffsetTranCode;
    private EndowmentTransactionCode saleGainLossOffsetTranCode;
    private EndowmentTransactionCode cashDepositOffsetTranCode;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.POOL_SECURITY_ID, this.pooledSecurityID);
        return m;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the pooledSecurityID.
     * 
     * @return pooledSecurityID
     */
    public String getPooledSecurityID() {
        return pooledSecurityID;
    }

    /**
     * Sets the pooledSecurityID.
     * 
     * @param pooledSecurityID
     */
    public void setPooledSecurityID(String pooledSecurityID) {
        this.pooledSecurityID = pooledSecurityID;
    }

    /**
     * Gets the pooledFundDescription.
     * 
     * @return pooledFundDescription
     */
    public String getPooledFundDescription() {
        return pooledFundDescription;
    }

    /**
     * Sets the pooledFundDescription.
     * 
     * @param pooledFundDescription
     */
    public void setPooledFundDescription(String pooledFundDescription) {
        this.pooledFundDescription = pooledFundDescription;
    }

    /**
     * Gets the incrementValuationDays
     * 
     * @return incrementValuationDays
     */
    public Integer getIncrementValuationDays() {
        return incrementValuationDays;
    }

    /**
     * Sets the incrementValuationDays
     * 
     * @param incrementValuationDays
     */
    public void setIncrementValuationDays(Integer incrementValuationDays) {
        this.incrementValuationDays = incrementValuationDays;
    }
    
    /**
     * Gets the fundRegistrationCode
     * 
     * @return fundRegistrationCode
     */
    public String getFundRegistrationCode() {
        return fundRegistrationCode;
    }

    /**
     * Sets the fundRegistrationCode
     * 
     * @param fundRegistrationCode
     */
    public void setFundRegistrationCode(String fundRegistrationCode) {
        this.fundRegistrationCode = fundRegistrationCode;
    }
    

    /**
     * Gets the fundKEMID.
     * 
     * @return fundKEMID
     */
    public String getFundKEMID() {
        return fundKEMID;
    }

    /**
     * Sets the fundKEMID.
     * 
     * @param fundKEMID
     */
    public void setFundKEMID(String fundKEMID) {
        this.fundKEMID = fundKEMID;
    }

    /**
     * Gets the fundAssetPurchaseOffsetTranCode
     * 
     * @return fundAssetPurchaseOffsetTranCode
     */
    public String getFundAssetPurchaseOffsetTranCode() {
        return fundAssetPurchaseOffsetTranCode;
    }

    /**
     * Set the fundAssetPurchaseOffsetTranCode
     * 
     * @param fundAssetPurchaseOffsetTranCode
     */
    public void setFundAssetPurchaseOffsetTranCode(String fundAssetPurchaseOffsetTranCode) {
        this.fundAssetPurchaseOffsetTranCode = fundAssetPurchaseOffsetTranCode;
    }

    /**
     * Gets the fundAssetSaleOffsetTranCode
     * 
     * @return fundAssetSaleOffsetTranCode
     */
    public String getFundAssetSaleOffsetTranCode() {
        return fundAssetSaleOffsetTranCode;
    }

    /**
     * Set the fundAssetSaleOffsetTranCode
     * 
     * @param fundAssetSaleOffsetTranCode
     */
    public void setFundAssetSaleOffsetTranCode(String fundAssetSaleOffsetTranCode) {
        this.fundAssetSaleOffsetTranCode = fundAssetSaleOffsetTranCode;
    }

    /**
     * Gets the fundSaleGainLossOffsetTranCode
     * 
     * @return fundSaleGainLossOffsetTranCode
     */
    public String getFundSaleGainLossOffsetTranCode() {
        return fundSaleGainLossOffsetTranCode;
    }

    /**
     * Set the fundSaleGainLossOffsetTranCode
     * 
     * @param fundSaleGainLossOffsetTranCode
     */
    public void setFundSaleGainLossOffsetTranCode(String fundSaleGainLossOffsetTranCode) {
        this.fundSaleGainLossOffsetTranCode = fundSaleGainLossOffsetTranCode;
    }

    /**
     * Gets the fundCashDepositOffsetTranCode
     * 
     * @return fundCashDepositOffsetTranCode
     */
    public String getFundCashDepositOffsetTranCode() {
        return fundCashDepositOffsetTranCode;
    }

    /**
     * Set the fundCashDepositOffsetTranCode
     * 
     * @param fundCashDepositOffsetTranCode
     */
    public void setFundCashDepositOffsetTranCode(String fundCashDepositOffsetTranCode) {
        this.fundCashDepositOffsetTranCode = fundCashDepositOffsetTranCode;
    }

    /**
     * Gets the distributeGainsAndLossesIndicator.
     * 
     * @return distributeGainsAndLossesIndicator
     */
    public boolean isDistributeGainsAndLossesIndicator() {
        return distributeGainsAndLossesIndicator;
    }

    /**
     * Sets the distributeGainsAndLossesIndicator.
     * 
     * @param distributeGainsAndLossesIndicator
     */
    public void setDistributeGainsAndLossesIndicator(boolean distributeGainsAndLossesIndicator) {
        this.distributeGainsAndLossesIndicator = distributeGainsAndLossesIndicator;
    }

    /**
     * Gets the security object
     * 
     * @return security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets the security object
     * 
     * @param security
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * Gets the registrationCodeObj
     * 
     * @return registrationCodeObj
     */
    public RegistrationCode getRegistrationCodeObj() {
        return registrationCodeObj;
    }

    /**
     * Sets the registrationCodeObj
     * 
     * @param registrationCodeObj
     */
    public void setRegistrationCodeObj(RegistrationCode registrationCodeObj) {
        this.registrationCodeObj = registrationCodeObj;
    }
    /**
     * Gets the kemid object
     * 
     * @return kemid
     */
    public KEMID getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid object
     * 
     * @param kemid
     */
    public void setKemid(KEMID kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the assetPurchaseOffsetTranCode
     * 
     * @return assetPurchaseOffsetTranCode
     */
    public EndowmentTransactionCode getAssetPurchaseOffsetTranCode() {
        return assetPurchaseOffsetTranCode;
    }

    /**
     * Sets the assetPurchaseOffsetTranCode.
     * 
     * @param assetPurchaseOffsetTranCode
     */
    public void setAssetPurchaseOffsetTranCode(EndowmentTransactionCode assetPurchaseOffsetTranCode) {
        this.assetPurchaseOffsetTranCode = assetPurchaseOffsetTranCode;
    }

    /**
     * Gets the assetSaleOffsetTranCode
     * 
     * @return assetSaleOffsetTranCode
     */
    public EndowmentTransactionCode getAssetSaleOffsetTranCode() {
        return assetSaleOffsetTranCode;
    }

    /**
     * Sets the assetSaleOffsetTranCode.
     * 
     * @param assetSaleOffsetTranCode
     */
    public void setAssetSaleOffsetTranCode(EndowmentTransactionCode assetSaleOffsetTranCode) {
        this.assetSaleOffsetTranCode = assetSaleOffsetTranCode;
    }

    /**
     * Gets the saleGainLossOffsetTranCode
     * 
     * @return saleGainLossOffsetTranCode
     */
    public EndowmentTransactionCode getSaleGainLossOffsetTranCode() {
        return saleGainLossOffsetTranCode;
    }

    /**
     * Sets the saleGainLossOffsetTranCode.
     * 
     * @param saleGainLossOffsetTranCode
     */
    public void setSaleGainLossOffsetTranCode(EndowmentTransactionCode saleGainLossOffsetTranCode) {
        this.saleGainLossOffsetTranCode = saleGainLossOffsetTranCode;
    }

    /**
     * Gets the cashDepositOffsetTranCode
     * 
     * @return cashDepositOffsetTranCode
     */
    public EndowmentTransactionCode getCashDepositOffsetTranCode() {
        return cashDepositOffsetTranCode;
    }

    /**
     * Sets the cashDepositOffsetTranCode.
     * 
     * @param cashDepositOffsetTranCode
     */
    public void setCashDepositOffsetTranCode(EndowmentTransactionCode cashDepositOffsetTranCode) {
        this.cashDepositOffsetTranCode = cashDepositOffsetTranCode;
    }

    /**
     * Gets pooledSecurityID and pooledFundDescription.
     * 
     * @return
     */
    public String getPooledSecurityIDAndDescription() {
        if (StringUtils.isEmpty(pooledSecurityID)) {
            return KFSConstants.EMPTY_STRING;
        }
        else
            return getPooledSecurityID() + " - " + getPooledFundDescription();
    }

    /**
     * Sets pooledSecurityID and pooledFundDescription.
     * 
     * @param pooledSecurityIDAndDescription
     */
    public void setPooledSecurityIDAndDescription(String pooledSecurityIDAndDescription) {

    }

    /**
     * Gets the allowFractionalShares.
     * 
     * @return allowFractionalShares
     */
    public boolean isAllowFractionalShares() {
        return allowFractionalShares;
    }

    /**
     * Sets the allowFractionalShares.
     * 
     * @param allowFractionalShares
     */
    public void setAllowFractionalShares(boolean allowFractionalShares) {
        this.allowFractionalShares = allowFractionalShares;
    }

}
