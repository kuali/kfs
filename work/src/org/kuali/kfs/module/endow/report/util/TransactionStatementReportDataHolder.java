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
import java.sql.Date;

public class TransactionStatementReportDataHolder {

    // header
    private String institution;
    private String beginningDate;
    private String endingDate;
    private String kemid;
    private String kemidLongTitle;
    
    // body
    private String postedDate;    
    private BigDecimal historyIncomeCash1;
    private BigDecimal historyPrincipalCash1;
    private BigDecimal incomeAmount;
    private BigDecimal principalAmount;
    private BigDecimal historyIncomeCash2;
    private BigDecimal historyPrincipalCash2;
    
    // description
    private String documentName;
    private String etranCode;
    private String etranCodeDesc;
    private String transactionDesc;
    private String transactionSecurity;
    private BigDecimal transactionSecurityUnits;
    private BigDecimal transactionSecurityUnitValue;
    //private Date monthEndDate;

    /**
     * Gets the first row description
     * 
     * @return
     */
    public String getBeginningDescription() {
        return "Beginning Cash Balance";
    }
    
    /**
     * Gets the send row description 
     * 
     * @return
     */
    public String getDescription2() {
        StringBuffer description = new StringBuffer();
        description.append(documentName).append("\n")
                   .append(getEtranCodeAndDescription()).append("\n")
                   .append(transactionDesc).append("\n")
                   .append(transactionSecurity).append("\n")
                   .append(transactionSecurityUnits).append(" at ").append(transactionSecurityUnitValue);
                   
        return description.toString();
    }
    
    /**
     * Gets the third row description
     * 
     * @return
     */
    public String getDescription3() {
        StringBuffer description = new StringBuffer();
        description.append(documentName).append("\n")
                   .append(getEtranCodeAndDescription()).append("\n")
                   .append(transactionDesc);
                   
        return description.toString();
    }
    
    /**
     * Gets the fourth row description
     * 
     * @return
     */
    public String getDescription4() {
        StringBuffer description = new StringBuffer();
        description.append(documentName).append("\n")
                   .append(getEtranCodeAndDescription()).append("\n")
                   .append(transactionDesc).append("\n")
                   .append(transactionSecurity);
                   
        return description.toString();
    }

    /**
     * Gets the last row description
     * 
     * @return
     */
    public String getEndingDescription() {
        return "Ending Cash Balance";
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

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public BigDecimal getHistoryIncomeCash1() {
        return historyIncomeCash1;
    }

    public void setHistoryIncomeCash1(BigDecimal historyIncomeCash1) {
        this.historyIncomeCash1 = historyIncomeCash1;
    }

    public BigDecimal getHistoryPrincipalCash1() {
        return historyPrincipalCash1;
    }

    public void setHistoryPrincipalCash1(BigDecimal historyPrincipalCash1) {
        this.historyPrincipalCash1 = historyPrincipalCash1;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getHistoryIncomeCash2() {
        return historyIncomeCash2;
    }

    public void setHistoryIncomeCash2(BigDecimal historyIncomeCash2) {
        this.historyIncomeCash2 = historyIncomeCash2;
    }

    public BigDecimal getHistoryPrincipalCash2() {
        return historyPrincipalCash2;
    }

    public void setHistoryPrincipalCash2(BigDecimal historyPrincipalCash2) {
        this.historyPrincipalCash2 = historyPrincipalCash2;
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
        if (transactionDesc == null || transactionDesc.isEmpty()) {
            return "No Transaction Description";
        } else {
            return transactionDesc;
        }
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

    public String getEtranCodeAndDescription() {
        if (etranCode != null && etranCode.equalsIgnoreCase("null") && !etranCode.isEmpty()) {
            return etranCode + " - " + etranCodeDesc;
        } else {
            return "No Etran Code";
        }
    }
}
