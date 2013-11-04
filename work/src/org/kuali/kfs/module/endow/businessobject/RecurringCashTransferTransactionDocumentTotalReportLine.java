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
