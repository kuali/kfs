/*
 * Copyright 2009 The Kuali Foundation
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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GlInterfaceBatchProcessKemLine extends TransientBusinessObjectBase {
    
    private String documentNumber;
    private int lineNumber;
    private String lineTypeCode;
    private String typeCode;
    private String subTypeCode;
    private String kemid;
    private String incomePrincipalIndicatorCode;
    private BigDecimal transactionArchiveIncomeAmount;
    private BigDecimal transactionArchivePrincipalAmount;
    
    //security fields...
    private String securityId;
    private BigDecimal holdingCost;
    private BigDecimal longTermGainLoss;
    private BigDecimal shortTermGainLoss;
 
    //KEMIDGeneralLinkTable...
    private String chartCode;
    private String accountNumber;
    private String objectCode;
    private String nonCashOffsetObjectCode;

    public GlInterfaceBatchProcessKemLine() {
    }
    
    /**
     * gets documentNumber
     * @return documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * sets documentNumber attribute
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * gets lineNumber
     * @return lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * sets lineNumber attribute
     * @param lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * gets lineTypeCode
     * @return lineTypeCode
     */
    public String getLineTypeCode() {
        return lineTypeCode;
    }

    /**
     * sets lineTypeCode attribute
     * @param lineTypeCode
     */
    public void setLineTypeCode(String lineTypeCode) {
        this.lineTypeCode = lineTypeCode;
    }

    /**
     * gets typeCode
     * @return typeCode
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * sets typeCode attribute
     * @param typeCode
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * gets subTypeCode
     * @return subTypeCode
     */
    public String getSubTypeCode() {
        return subTypeCode;
    }

    /**
     * sets subTypeCode attribute
     * @param subTypeCode
     */
    public void setSubTypeCode(String subTypeCode) {
        this.subTypeCode = subTypeCode;
    }

    /**
     * gets incomePrincipalIndicatorCode
     * @return incomePrincipalIndicatorCode
     */
    public String getIncomePrincipalIndicatorCode() {
        return incomePrincipalIndicatorCode;
    }

    /**
     * sets incomePrincipalIndicatorCode attribute
     * @param incomePrincipalIndicatorCode
     */
    public void setIncomePrincipalIndicatorCode(String incomePrincipalIndicatorCode) {
        this.incomePrincipalIndicatorCode = incomePrincipalIndicatorCode;
    }

    /**
     * gets transactionArchiveIncomeAmount
     * @return transactionArchiveIncomeAmount
     */
    public BigDecimal getTransactionArchiveIncomeAmount() {
        return transactionArchiveIncomeAmount;
    }

    /**
     * sets transactionArchiveIncomeAmount attribute
     * @param transactionArchiveIncomeAmount
     */
    public void setTransactionArchiveIncomeAmount(BigDecimal transactionArchiveIncomeAmount) {
        this.transactionArchiveIncomeAmount = transactionArchiveIncomeAmount;
    }

    /**
     * gets transactionArchivePrincipalAmount
     * @return transactionArchivePrincipalAmount
     */
    public BigDecimal getTransactionArchivePrincipalAmount() {
        return transactionArchivePrincipalAmount;
    }

    /**
     * sets transactionArchiveIncomeAmount attribute
     * @param transactionArchiveIncomeAmount
     */
    public void setTransactionArchivePrincipalAmount(BigDecimal transactionArchivePrincipalAmount) {
        this.transactionArchivePrincipalAmount = transactionArchivePrincipalAmount;
    }
    
    /**
     * gets securityId
     * @return securityId
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * sets securityId
     * @param securityId
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    /**
     * gets holdingCost
     * @return holdingCost
     */
    public BigDecimal getHoldingCost() {
        return holdingCost;
    }

    /**
     * sets holdingCost attribute
     * @param holdingCost
     */
    public void setHoldingCost(BigDecimal holdingCost) {
        this.holdingCost = holdingCost;
    }

    /**
     * Gets the longTermGainLoss attribute. 
     * @return Returns the longTermGainLoss.
     */
    public BigDecimal getLongTermGainLoss() {
        return longTermGainLoss;
    }

    /**
     * Sets the longTermGainLoss attribute value.
     * @param longTermGainLoss The longTermGainLoss to set.
     */
    public void setLongTermGainLoss(BigDecimal longTermGainLoss) {
        this.longTermGainLoss = longTermGainLoss;
    }

    /**
     * Gets the shortTermGainLoss attribute. 
     * @return Returns the shortTermGainLoss.
     */
    public BigDecimal getShortTermGainLoss() {
        return shortTermGainLoss;
    }

    /**
     * Sets the shortTermGainLoss attribute value.
     * @param shortTermGainLoss The shortTermGainLoss to set.
     */
    public void setShortTermGainLoss(BigDecimal shortTermGainLoss) {
        this.shortTermGainLoss = shortTermGainLoss;
    }
    
    /**
     * gets chartCode
     * @return chartCode
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * sets chartCode attribute
     * @param chartCode
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * gets accountNumber
     * @return accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * sets documentNumber attribute
     * @param documentNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * gets objectCode
     * @return objectCode
     */
    public String getObjectCode() {
        return objectCode;
    }

    /**
     * sets objectCode attribute
     * @param objectCode
     */
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * gets kemid attribute
     * @return kemid;
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * set kemid attribute
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }
    
    /**
     * gets attribute nonCashOffsetObjectCode
     * @return nonCashOffsetObjectCode
     */
    public String getNonCashOffsetObjectCode() {
        return nonCashOffsetObjectCode;
    }

    /**
     * sets attribute nonCashOffsetObjectCode
     */
    public void setNonCashOffsetObjectCode(String nonCashOffsetObjectCode) {
        this.nonCashOffsetObjectCode = nonCashOffsetObjectCode;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        
        pks.put("documentNumber", this.getDocumentNumber());
        pks.put("lineNumber", this.getLineNumber());
        pks.put("lineTypeCode", this.getLineTypeCode());
        pks.put("typeCode", this.getTypeCode());
        pks.put("kemid", this.getKemid());
        pks.put("subTypeCode", this.getSubTypeCode());
        pks.put("incomePrincipalIndicatorCode", this.getIncomePrincipalIndicatorCode());
        pks.put("transactionArchiveIncomeAmount", this.getTransactionArchiveIncomeAmount());
        pks.put("transactionArchivePrincipalAmount", this.getTransactionArchivePrincipalAmount());
        pks.put("securityId", this.getSecurityId());
        pks.put("holdingCost", this.getHoldingCost());
        pks.put(longTermGainLoss, this.getLongTermGainLoss());
        pks.put("shortTermGainLoss", this.getShortTermGainLoss());
        pks.put("chartCode", this.getChartCode());
        pks.put("objectCode", this.getObjectCode());
        pks.put("nonCashOffsetObjectCode", this.getNonCashOffsetObjectCode());
        
        return pks;
    }
}
