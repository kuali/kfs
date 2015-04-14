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

package org.kuali.kfs.module.cg.report;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Defines a financial award object.
 */
public class ContractsGrantsAwardBalancesReportDetailDataHolder {

    private Long proposalNumber;
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

    public boolean displaySubtotal;

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
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


    public boolean isDisplaySubtotal() {
        return displaySubtotal;
    }

    public void setDisplaySubtotal(boolean displaySubtotal) {
        this.displaySubtotal = displaySubtotal;
    }

}
