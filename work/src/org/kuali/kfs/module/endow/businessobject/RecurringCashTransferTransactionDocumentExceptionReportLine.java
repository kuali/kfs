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


public class RecurringCashTransferTransactionDocumentExceptionReportLine extends TransactionDocumentForReportLineBase {

    private String sourceKemid;
    private String transferNumber;
    private String targetSeqNumber;
    
    public RecurringCashTransferTransactionDocumentExceptionReportLine() {
        this("", "", "", "");
    }
    
    
    public RecurringCashTransferTransactionDocumentExceptionReportLine(String documentType, String sourceKemid, String transferNumber, String targetSeqNumber) {
        this.documentType = documentType;
        this.sourceKemid = sourceKemid;
        this.transferNumber = transferNumber;
        this.targetSeqNumber = targetSeqNumber;
        
    }


    public String getSourceKemid() {
        return sourceKemid;
    }


    public void setSourceKemid(String sourceKemid) {
        this.sourceKemid = sourceKemid;
    }


    public String getTransferNumber() {
        return transferNumber;
    }


    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }


    public String getTargetSeqNumber() {
        return targetSeqNumber;
    }


    public void setTargetSeqNumber(String targetSeqNumber) {
        this.targetSeqNumber = targetSeqNumber;
    }
}
