/*
 * Copyright 2010 The Kuali Foundation.
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
import java.util.List;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GainLossDistributionTotalReportLine extends TransientBusinessObjectBase {

    protected String documentType;
    protected String documentId;
    protected String securityId;
    protected int totalNumberOfTransactionLines = 0;
    protected BigDecimal unitAdjustmentAmount = BigDecimal.ZERO;
    protected BigDecimal totalHoldingAdjustmentAmount = BigDecimal.ZERO;

    /**
     * Constructs a GainLossDistributionTotalReportLine.java.
     * 
     * @param documentType
     * @param documentId
     * @param securityId
     */
    public GainLossDistributionTotalReportLine(String documentType, String documentId, String securityId) {
        this.documentType = documentType;
        this.documentId = documentId;
        this.securityId = securityId;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Gets the documentType.
     * 
     * @return documentType
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType.
     * 
     * @param documentType
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the documentId.
     * 
     * @return documentId
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the documentId.
     * 
     * @param documentId
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Gets the securityId.
     * 
     * @return securityId
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * Sets the securityId.
     * 
     * @param securityId
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    /**
     * Gets the totalNumberOfTransactionLines.
     * 
     * @return totalNumberOfTransactionLines
     */
    public int getTotalNumberOfTransactionLines() {
        return totalNumberOfTransactionLines;
    }

    /**
     * Sets the totalNumberOfTransactionLines.
     * 
     * @param totalNumberOfTransactionLines
     */
    public void setTotalNumberOfTransactionLines(int totalNumberOfTransactionLines) {
        this.totalNumberOfTransactionLines = totalNumberOfTransactionLines;
    }

    /**
     * Gets the unitAdjustmentAmount.
     * 
     * @return unitAdjustmentAmount
     */
    public BigDecimal getUnitAdjustmentAmount() {
        return unitAdjustmentAmount;
    }

    /**
     * Sets the unitAdjustmentAmount.
     * 
     * @param unitAdjustmentAmount
     */
    public void setUnitAdjustmentAmount(BigDecimal unitAdjustmentAmount) {
        this.unitAdjustmentAmount = unitAdjustmentAmount;
    }

    /**
     * Gets the totalHoldingAdjustmentAmount.
     * 
     * @return totalHoldingAdjustmentAmount
     */
    public BigDecimal getTotalHoldingAdjustmentAmount() {
        return totalHoldingAdjustmentAmount;
    }

    /**
     * Sets the totalHoldingAdjustmentAmount.
     * 
     * @param totalHoldingAdjustmentAmount
     */
    public void setTotalHoldingAdjustmentAmount(BigDecimal totalHoldingAdjustmentAmount) {
        this.totalHoldingAdjustmentAmount = totalHoldingAdjustmentAmount;
    }

    /**
     * Adds the unitAdjustmentAmount.
     * 
     * @param unitAdjustmentAmount
     */
    public void addUnitAdjustmentAmount(BigDecimal unitAdjustmentAmount) {
        this.unitAdjustmentAmount = this.unitAdjustmentAmount.add(unitAdjustmentAmount);
        totalNumberOfTransactionLines++;
    }

    /**
     * Computes the total holding adjustment based on the tax lots holding cost and adds it to totalHoldingAdjustmentAmount.
     * 
     * @param endowmentTransactionLine
     */
    public void addTotalHoldingAdjustmentAmount(EndowmentTransactionLine endowmentTransactionLine) {

        List<EndowmentTransactionTaxLotLine> taxLotLines = endowmentTransactionLine.getTaxLotLines();
        BigDecimal totalHoldingAmount = BigDecimal.ZERO;

        for (EndowmentTransactionTaxLotLine taxLotLine : taxLotLines) {
            totalHoldingAmount = totalHoldingAmount.add(taxLotLine.getLotHoldingCost());
        }

        this.totalHoldingAdjustmentAmount = this.totalHoldingAdjustmentAmount.add(totalHoldingAmount);
    }

}
