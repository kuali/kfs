/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class GainLossDistributionTotalReportLine extends TransientBusinessObjectBase {

    protected String documentType;
    protected String documentId;
    protected String securityId;
    protected int totalNumberOfTransactionLines = 0;
    protected KualiDecimal unitAdjustmentAmount = KualiDecimal.ZERO;
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

    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public int getTotalNumberOfTransactionLines() {
        return totalNumberOfTransactionLines;
    }

    public void setTotalNumberOfTransactionLines(int totalNumberOfTransactionLines) {
        this.totalNumberOfTransactionLines = totalNumberOfTransactionLines;
    }

    public KualiDecimal getUnitAdjustmentAmount() {
        return unitAdjustmentAmount;
    }

    public void setUnitAdjustmentAmount(KualiDecimal unitAdjustmentAmount) {
        this.unitAdjustmentAmount = unitAdjustmentAmount;
    }

    public BigDecimal getTotalHoldingAdjustmentAmount() {
        return totalHoldingAdjustmentAmount;
    }

    public void setTotalHoldingAdjustmentAmount(BigDecimal totalHoldingAdjustmentAmount) {
        this.totalHoldingAdjustmentAmount = totalHoldingAdjustmentAmount;
    }

    public void addUnitAdjustmentAmount(KualiDecimal unitAdjustmentAmount) {
        this.unitAdjustmentAmount = this.unitAdjustmentAmount.add(unitAdjustmentAmount);
        totalNumberOfTransactionLines++;
    }

    /**
     * This method...
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
