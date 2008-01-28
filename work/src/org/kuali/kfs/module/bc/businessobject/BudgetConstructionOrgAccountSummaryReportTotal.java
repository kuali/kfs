/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.bo;

import java.math.BigDecimal;

public class BudgetConstructionOrgAccountSummaryReportTotal {
    
    BudgetConstructionAccountSummary bcas;
    
    private BigDecimal totalRevenueBaseAmount;
    private BigDecimal totalGrossBaseAmount;
    private BigDecimal totalTransferInBaseAmount;
    private BigDecimal totalNetTransferBaseAmount;

    private BigDecimal totalRevenueReqAmount;
    private BigDecimal totalGrossReqAmount;
    private BigDecimal totalTransferInReqAmount;
    private BigDecimal totalNetTransferReqAmount;
    
    public BudgetConstructionOrgAccountSummaryReportTotal(){
        
    }
    
    public BigDecimal getTotalGrossBaseAmount() {
        return totalGrossBaseAmount;
    }
    public void setTotalGrossBaseAmount(BigDecimal totalGrossBaseAmount) {
        this.totalGrossBaseAmount = totalGrossBaseAmount;
    }
    public BigDecimal getTotalGrossReqAmount() {
        return totalGrossReqAmount;
    }
    public void setTotalGrossReqAmount(BigDecimal totalGrossReqAmount) {
        this.totalGrossReqAmount = totalGrossReqAmount;
    }
    public BigDecimal getTotalNetTransferBaseAmount() {
        return totalNetTransferBaseAmount;
    }
    public void setTotalNetTransferBaseAmount(BigDecimal totalNetTransferBaseAmount) {
        this.totalNetTransferBaseAmount = totalNetTransferBaseAmount;
    }
    public BigDecimal getTotalNetTransferReqAmount() {
        return totalNetTransferReqAmount;
    }
    public void setTotalNetTransferReqAmount(BigDecimal totalNetTransferReqAmount) {
        this.totalNetTransferReqAmount = totalNetTransferReqAmount;
    }
    public BigDecimal getTotalRevenueBaseAmount() {
        return totalRevenueBaseAmount;
    }
    public void setTotalRevenueBaseAmount(BigDecimal totalRevenueBaseAmount) {
        this.totalRevenueBaseAmount = totalRevenueBaseAmount;
    }
    public BigDecimal getTotalRevenueReqAmount() {
        return totalRevenueReqAmount;
    }
    public void setTotalRevenueReqAmount(BigDecimal totalRevenueReqAmount) {
        this.totalRevenueReqAmount = totalRevenueReqAmount;
    }
    public BigDecimal getTotalTransferInBaseAmount() {
        return totalTransferInBaseAmount;
    }
    public void setTotalTransferInBaseAmount(BigDecimal totalTransferInBaseAmount) {
        this.totalTransferInBaseAmount = totalTransferInBaseAmount;
    }
    public BigDecimal getTotalTransferInReqAmount() {
        return totalTransferInReqAmount;
    }
    public void setTotalTransferInReqAmount(BigDecimal totalTransferInReqAmount) {
        this.totalTransferInReqAmount = totalTransferInReqAmount;
    }



    public BudgetConstructionAccountSummary getBcas() {
        return bcas;
    }



    public void setBcas(BudgetConstructionAccountSummary bcas) {
        this.bcas = bcas;
    }
}
