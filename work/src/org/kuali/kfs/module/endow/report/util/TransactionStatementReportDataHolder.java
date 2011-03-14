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
package org.kuali.kfs.module.endow.report.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class TransactionStatementReportDataHolder {

    // header
    private String institution;
    private String beginningDate;
    private String endingDate;
    private String kemid;
    private String kemidLongTitle;
    
    // body
    private BigDecimal beginningIncomeCash = BigDecimal.ZERO;;     // li
    private BigDecimal beginningPrincipalCash = BigDecimal.ZERO;;  // 1p
    private BigDecimal endingIncomeCash = BigDecimal.ZERO;;        // 3i
    private BigDecimal endingPrincipalCash = BigDecimal.ZERO;;     // 3p
    
    private List<TransactionArchiveInfo> transactionArchiveInfoList = new ArrayList<TransactionArchiveInfo>(); 
    
    // description
    public class TransactionArchiveInfo {
        private String postedDate;   
        private String documentName;
        private String etranCode;
        private String etranCodeDesc;
        private String transactionDesc;
        private String transactionSecurity;
        private BigDecimal transactionSecurityUnits = BigDecimal.ZERO;
        private BigDecimal transactionSecurityUnitValue = BigDecimal.ZERO;
        private BigDecimal transactionIncomeCash = BigDecimal.ZERO;        
        private BigDecimal transactionPrincipalCash = BigDecimal.ZERO;
        public String getPostedDate() {
            return postedDate;
        }
        public void setPostedDate(String postedDate) {
            this.postedDate = postedDate;
        }
        public String getDocumentName() {
            return documentName;
        }
        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }
        public String getEtranCode() {
            return etranCode;
        }
        public void setEtranCode(String etranCode) {
            this.etranCode = etranCode;
        }
        public String getEtranCodeDesc() {
            return etranCodeDesc;
        }
        public void setEtranCodeDesc(String etranCodeDesc) {
            this.etranCodeDesc = etranCodeDesc;
        }
        public String getTransactionDesc() {
            return transactionDesc;
        }
        public void setTransactionDesc(String transactionDesc) {
            this.transactionDesc = transactionDesc;
        }
        public String getTransactionSecurity() {
            return transactionSecurity;
        }
        public void setTransactionSecurity(String transactionSecurity) {
            this.transactionSecurity = transactionSecurity;
        }
        public BigDecimal getTransactionSecurityUnits() {
            return transactionSecurityUnits;
        }
        public void setTransactionSecurityUnits(BigDecimal transactionSecurityUnits) {
            this.transactionSecurityUnits = transactionSecurityUnits;
        }
        public BigDecimal getTransactionSecurityUnitValue() {
            return transactionSecurityUnitValue;
        }
        public void setTransactionSecurityUnitValue(BigDecimal transactionSecurityUnitValue) {
            this.transactionSecurityUnitValue = transactionSecurityUnitValue;
        }
        public BigDecimal getTransactionIncomeCash() {
            return transactionIncomeCash;
        }
        public void setTransactionIncomeCash(BigDecimal transactionIncomeCash) {
            this.transactionIncomeCash = transactionIncomeCash;
        }
        public BigDecimal getTransactionPrincipalCash() {
            return transactionPrincipalCash;
        }
        public void setTransactionPrincipalCash(BigDecimal transactionPrincipalCash) {
            this.transactionPrincipalCash = transactionPrincipalCash;
        }        
    }

    public TransactionArchiveInfo createTransactionArchiveInfo() {
        TransactionArchiveInfo transactionArchiveInfo = new TransactionArchiveInfo();        
        transactionArchiveInfoList.add(transactionArchiveInfo);
        
        return transactionArchiveInfo;
    }
    
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(String beginningDate) {
        this.beginningDate = beginningDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getKemid() {
        return kemid;
    }

    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    public String getKemidLongTitle() {
        return kemidLongTitle;
    }

    public void setKemidLongTitle(String kemidLongTitle) {
        this.kemidLongTitle = kemidLongTitle;
    }

    public BigDecimal getBeginningIncomeCash() {
        return beginningIncomeCash;
    }

    public void setBeginningIncomeCash(BigDecimal beginningIncomeCash) {
        this.beginningIncomeCash = beginningIncomeCash;
    }

    public BigDecimal getBeginningPrincipalCash() {
        return beginningPrincipalCash;
    }

    public void setBeginningPrincipalCash(BigDecimal beginningPrincipalCash) {
        this.beginningPrincipalCash = beginningPrincipalCash;
    }

    public BigDecimal getEndingIncomeCash() {
        return endingIncomeCash;
    }

    public void setEndingIncomeCash(BigDecimal endingIncomeCash) {
        this.endingIncomeCash = endingIncomeCash;
    }

    public BigDecimal getEndingPrincipalCash() {
        return endingPrincipalCash;
    }

    public void setEndingPrincipalCash(BigDecimal endingPrincipalCash) {
        this.endingPrincipalCash = endingPrincipalCash;
    }

    public List<TransactionArchiveInfo> getTransactionArchiveInfoList() {
        return transactionArchiveInfoList;
    }

    public void setTransactionArchiveInfoList(List<TransactionArchiveInfo> transactionArchiveInfoList) {
        this.transactionArchiveInfoList = transactionArchiveInfoList;
    }


    
//    /**
//     * Gets the send row description 
//     * 
//     * @return
//     */
//    public String getDescription2() {
//        StringBuffer description = new StringBuffer();
//        description.append(documentName).append("\n")
//                   .append(getEtranCodeAndDescription()).append("\n")
//                   .append(transactionDesc).append("\n")
//                   .append(transactionSecurity).append("\n")
//                   .append(transactionSecurityUnits).append(" at ").append(transactionSecurityUnitValue);
//                   
//        return description.toString();
//    }
//
//    /**
//     * Gets the last row description
//     * 
//     * @return
//     */
//
//
//    public String getTransactionDesc() {
//        if (transactionDesc == null || transactionDesc.isEmpty()) {
//            return "No Transaction Description";
//        } else {
//            return transactionDesc;
//        }
//    }
//
//
//    public String getEtranCodeAndDescription() {
//        if (etranCode != null && etranCode.equalsIgnoreCase("null") && !etranCode.isEmpty()) {
//            return etranCode + " - " + etranCodeDesc;
//        } else {
//            return "No Etran Code";
//        }
//    }
}
