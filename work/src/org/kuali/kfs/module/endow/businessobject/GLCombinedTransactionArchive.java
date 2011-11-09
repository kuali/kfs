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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GLCombinedTransactionArchive extends TransientBusinessObjectBase {
    
    private long combinedEntryCount = 0;
    
    private BigDecimal incomeAmount;
    private BigDecimal principalAmount;
    private BigDecimal holdingAmount;
    private BigDecimal longTermAmount;
    private BigDecimal shortTermAmount;
    private String documentNumber;
    private int lineNumber;
    private String lineTypeCode;
    private String typeCode;
    private String subTypeCode;
    private String kemid;
    private String incomePrincipalIndicatorCode;
    
    //KEMIDGeneralLinkTable...
    private String chartCode;
    private String accountNumber;
    private String objectCode;
    private String nonCashOffsetObjectCode;

    public GLCombinedTransactionArchive() {
        chartCode = null;
        accountNumber = null;
        objectCode = null;
        nonCashOffsetObjectCode = null;
        
        lineTypeCode = null;
        subTypeCode = null;
        typeCode = null;
        incomePrincipalIndicatorCode = null;
        kemid = null;
        documentNumber = null;
        
        this.initializeAmounts();
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
     * gets holdingAmount
     * @return holdingAmount
     */
    public BigDecimal getHoldingAmount() {
        return holdingAmount;
    }

    /**
     * sets holdingAmount attribute
     * @param holdingAmount
     */
    public void setHoldingAmount(BigDecimal holdingAmount) {
        this.holdingAmount = holdingAmount;
    }

    /**
     * Gets the longTermAmount attribute. 
     * @return Returns the longTermAmount.
     */
    public BigDecimal getLongTermAmount() {
        return longTermAmount;
    }

    /**
     * Sets the longTermAmount attribute value.
     * @param longTermAmount The longTermAmount to set.
     */
    public void setLongTermAmount(BigDecimal longTermAmount) {
        this.longTermAmount = longTermAmount;
    }

    /**
     * Gets the shortTermAmount attribute. 
     * @return Returns the shortTermAmount.
     */
    public BigDecimal getShortTermAmount() {
        return shortTermAmount;
    }

    /**
     * Sets the shortTermAmount attribute value.
     * @param shortTermAmount The shortTermGainLoss to set.
     */
    public void setShortTermAmount(BigDecimal shortTermAmount) {
        this.shortTermAmount = shortTermAmount;
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
     * method to initialize the amount fields...
     */
    public void initializeAmounts() {
        combinedEntryCount = 0;
        
        incomeAmount = BigDecimal.ZERO;
        principalAmount = BigDecimal.ZERO;
        holdingAmount = BigDecimal.ZERO;
        longTermAmount = BigDecimal.ZERO;
        shortTermAmount = BigDecimal.ZERO;
    }
    
    /**
     * gets attribute combinedEntryCount
     * @return combinedEntryCount
     */
    public long getCombinedEntryCount() {
        return combinedEntryCount;
    }

    /**
     * sets attribute combinedEntryCount
     */
    public void setCombinedEntryCount(long combinedEntryCount) {
        this.combinedEntryCount = combinedEntryCount;
    }

    /**
     * gets attribute incomeAmount
     * @return incomeAmount
     */
    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    /**
     * sets attribute incomeAmount
     */
    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    /**
     * gets attribute principalAmount
     * @return principalAmount
     */
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * sets attribute principalAmount
     */
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * method to increment combinedEntryCount 
     */
    public void incrementCombinedEntryCount() {
        this.combinedEntryCount++;
    }
    
    /**
     * method to copy values from archive transaction to properties in the transient 
     * business object so they can be copied to the combined transaction archive line later
     * The amounts from single transaction archive are added to the property variables and
     * will be copied to the combined archive record. 
     * 
     * @param kemArchiveTransaction
     * @param cashType
     */
    public void copyKemArchiveTransactionValues(GlInterfaceBatchProcessKemLine kemArchiveTransaction, boolean cashType) {

        lineNumber = kemArchiveTransaction.getLineNumber();
        lineTypeCode = kemArchiveTransaction.getLineTypeCode();
        subTypeCode = kemArchiveTransaction.getSubTypeCode();
        typeCode = kemArchiveTransaction.getTypeCode();
        incomePrincipalIndicatorCode = kemArchiveTransaction.getIncomePrincipalIndicatorCode();
        kemid = kemArchiveTransaction.getKemid();
        documentNumber = kemArchiveTransaction.getDocumentNumber();
        nonCashOffsetObjectCode = kemArchiveTransaction.getNonCashOffsetObjectCode();
        
        if (cashType) {
            incomeAmount = incomeAmount.add(kemArchiveTransaction.getTransactionArchiveIncomeAmount());
            principalAmount = principalAmount.add(kemArchiveTransaction.getTransactionArchivePrincipalAmount());
            if (EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE.equalsIgnoreCase(kemArchiveTransaction.getTypeCode())) {
                holdingAmount = holdingAmount.add(kemArchiveTransaction.getHoldingCost());
                shortTermAmount = shortTermAmount.add(kemArchiveTransaction.getShortTermGainLoss());
                longTermAmount = longTermAmount.add(kemArchiveTransaction.getLongTermGainLoss());
            }
        }
        else {
            holdingAmount = holdingAmount.add(kemArchiveTransaction.getHoldingCost());
            shortTermAmount = shortTermAmount.add(kemArchiveTransaction.getShortTermGainLoss());
            longTermAmount = longTermAmount.add(kemArchiveTransaction.getLongTermGainLoss());
        }
    }
    
    /**
     * method to copy values from transaction archive into chart, account number, and object code
     * @param kemArchiveTransaction
     */
    public void copyChartAndAccountNumberAndObjectCodeValues(GlInterfaceBatchProcessKemLine kemArchiveTransaction) {
        this.setChartCode(kemArchiveTransaction.getChartCode());
        this.setAccountNumber(kemArchiveTransaction.getAccountNumber());
        this.setObjectCode(kemArchiveTransaction.getObjectCode());
    }
    
    /**
     * method to copy the values stored in the bo to the combined transaction archive record
     * @param cashType
     */
    public GlInterfaceBatchProcessKemLine copyValuesToCombinedTransactionArchive(boolean cashType) {
        GlInterfaceBatchProcessKemLine glKemLine = new GlInterfaceBatchProcessKemLine();
        
        if (combinedEntryCount > 1) {
            glKemLine.setDocumentNumber("Summary");
            glKemLine.setKemid("Summary");
        }
        else {
            glKemLine.setDocumentNumber(documentNumber);
            glKemLine.setKemid(kemid);
        }
        glKemLine.setLineNumber(lineNumber);
        glKemLine.setLineTypeCode(lineTypeCode);
        glKemLine.setSubTypeCode(subTypeCode);
        glKemLine.setTypeCode(typeCode);
        glKemLine.setIncomePrincipalIndicatorCode(incomePrincipalIndicatorCode);
        glKemLine.setObjectCode(objectCode);
        glKemLine.setNonCashOffsetObjectCode(nonCashOffsetObjectCode);
        
        //get transaction amount....
        if (cashType) {
            glKemLine.setTransactionArchiveIncomeAmount(incomeAmount);
            glKemLine.setTransactionArchivePrincipalAmount(principalAmount);
            glKemLine.setHoldingCost(BigDecimal.ZERO);
            glKemLine.setLongTermGainLoss(BigDecimal.ZERO);
            glKemLine.setShortTermGainLoss(BigDecimal.ZERO);
            if (glKemLine.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE)) {
                glKemLine.setHoldingCost(holdingAmount);
                glKemLine.setShortTermGainLoss(shortTermAmount);
                glKemLine.setLongTermGainLoss(longTermAmount);
            }                
        }
        else {
            glKemLine.setTransactionArchiveIncomeAmount(BigDecimal.ZERO);
            glKemLine.setTransactionArchivePrincipalAmount(BigDecimal.ZERO);
            glKemLine.setHoldingCost(holdingAmount);
            glKemLine.setShortTermGainLoss(shortTermAmount);
            glKemLine.setLongTermGainLoss(longTermAmount);
        }
        
        glKemLine.setChartCode(chartCode);
        glKemLine.setAccountNumber(accountNumber);
        
        return glKemLine;
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
        pks.put("holdingAmount", this.getHoldingAmount());
        pks.put(longTermAmount, this.getLongTermAmount());
        pks.put("shortTermAmount", this.getShortTermAmount());
        pks.put("chartCode", this.getChartCode());
        pks.put("objectCode", this.getObjectCode());
        pks.put("accountNumber", this.getAccountNumber());
        pks.put("nonCashOffsetObjectCode", this.getNonCashOffsetObjectCode());
        
        return pks;
    }
}
