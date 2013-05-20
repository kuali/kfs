/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.cg.report;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Defines a financial award object.
 */
public class ContractsGrantsAwardBalancesReportDetailDataHolder {

    private Long proposalNumber;
    private String awardId;
    private String agencyName;
    private String awardProjectTitle;
    private String awardStatusCode;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private String primaryProjectDirector;
    private String primaryFundManager;
    private BigDecimal awardTotalAmount;
    private BigDecimal totalBilledToDate;
    private BigDecimal totalPaymentsToDate;
    private BigDecimal amountCurrentlyDue;

    private String sortedFieldValue;

    private BigDecimal subTotal;

    public boolean displaySubtotalInd;

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }

    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }

    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }

    public String getPrimaryProjectDirector() {
        return primaryProjectDirector;
    }

    public void setPrimaryProjectDirector(String primaryProjectDirector) {
        this.primaryProjectDirector = primaryProjectDirector;
    }

    public String getPrimaryFundManager() {
        return primaryFundManager;
    }

    public void setPrimaryFundManager(String primaryFundManager) {
        this.primaryFundManager = primaryFundManager;
    }

    public BigDecimal getAwardTotalAmount() {
        return awardTotalAmount;
    }

    public void setAwardTotalAmount(BigDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }

    public BigDecimal getTotalBilledToDate() {
        return totalBilledToDate;
    }

    public void setTotalBilledToDate(BigDecimal totalBilledToDate) {
        this.totalBilledToDate = totalBilledToDate;
    }

    public BigDecimal getTotalPaymentsToDate() {
        return totalPaymentsToDate;
    }

    public void setTotalPaymentsToDate(BigDecimal totalPaymentsToDate) {
        this.totalPaymentsToDate = totalPaymentsToDate;
    }

    public BigDecimal getAmountCurrentlyDue() {
        return amountCurrentlyDue;
    }

    public void setAmountCurrentlyDue(BigDecimal amountCurrentlyDue) {
        this.amountCurrentlyDue = amountCurrentlyDue;
    }

    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }


    public boolean isDisplaySubtotalInd() {
        return displaySubtotalInd;
    }

    public void setDisplaySubtotalInd(boolean displaySubtotalInd) {
        this.displaySubtotalInd = displaySubtotalInd;
    }

}
