/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class RecurringCashTransferTransactionDocumentTotalReportLine extends TransactionDocumentForReportLineBase {

    private String transferNumber;
    private String sourceKemid;
    private Integer targetLinesGenerated;
    private KualiDecimal totalTransferAmount;
    
    public RecurringCashTransferTransactionDocumentTotalReportLine() {
        this("", "", "", "", 0, KualiDecimal.ZERO);
    }
    
    public RecurringCashTransferTransactionDocumentTotalReportLine(String documentType, String documentId, String transferNumber, String sourcekemid, Integer targetLinesGenerated, KualiDecimal totalTransferAmount) {
        this.documentType = documentType;
        this.documentId = documentId;
        this.transferNumber = transferNumber;
        this.sourceKemid = sourcekemid;
        this.targetLinesGenerated = targetLinesGenerated;
        this.totalTransferAmount = totalTransferAmount;
    }
    
    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public String getSourceKemid() {
        return sourceKemid;
    }

    public void setSourceKemid(String sourceKemid) {
        this.sourceKemid = sourceKemid;
    }

    public Integer getTargetLinesGenerated() {
        return targetLinesGenerated;
    }

    public void setTargetLinesGenerated(Integer targetLinesGenerated) {
        this.targetLinesGenerated = targetLinesGenerated;
    }

    public KualiDecimal getTotalTransferAmount() {
        return totalTransferAmount;
    }

    public void setTotalTransferAmount(KualiDecimal totalTransferAmount) {
        this.totalTransferAmount = totalTransferAmount;
    }
    
    public void incrementTargetLinesGenerated(Integer targetLines){
        this.targetLinesGenerated += targetLines;  
    }
    
    public void incrementTotalTransferAmount(KualiDecimal transferAmount){
        this.totalTransferAmount = this.totalTransferAmount.add(transferAmount);  
    }
}
