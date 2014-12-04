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
package org.kuali.kfs.module.endow.report.util;

import java.math.BigDecimal;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * The data holder for Trial Balance report
 */
public class TrialBalanceReportDataHolder {

    private String kemid;
    private String kemidName;
    private KualiDecimal InocmeCashBalance;
    private KualiDecimal PrincipalcashBalance;
    private BigDecimal KemidTotalMarketValue;
    private BigDecimal AvailableExpendableFunds;
    private BigDecimal FyRemainderEstimatedIncome;

    public TrialBalanceReportDataHolder() {}

    /**
     * Constructor
     * @param kemid
     * @param kemidName
     * @param InocmeCashBalance
     * @param PrincipalcashBalance
     * @param KemidTotalMarketValue
     * @param AvailableExpendableFunds
     * @param FyRemainderEstimatedIncome
     */
    public TrialBalanceReportDataHolder(
            String kemid,
            String kemidName,
            KualiDecimal InocmeCashBalance,
            KualiDecimal PrincipalcashBalance,
            BigDecimal KemidTotalMarketValue,
            BigDecimal AvailableExpendableFunds,
            BigDecimal FyRemainderEstimatedIncome) {
        
        this.kemid = kemid;
        this.kemidName = kemidName;
        this.InocmeCashBalance = InocmeCashBalance;
        this.PrincipalcashBalance = PrincipalcashBalance;
        this.KemidTotalMarketValue = KemidTotalMarketValue;
        this.AvailableExpendableFunds = AvailableExpendableFunds;
        this.FyRemainderEstimatedIncome = FyRemainderEstimatedIncome;
    }

    public String getKemid() {
        return kemid;
    }

    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    public String getKemidName() {
        return kemidName;
    }

    public void setKemidName(String kemidName) {
        this.kemidName = kemidName;
    }

    public KualiDecimal getInocmeCashBalance() {
        return InocmeCashBalance;
    }

    public void setInocmeCashBalance(KualiDecimal inocmeCashBalance) {
        InocmeCashBalance = inocmeCashBalance;
    }

    public KualiDecimal getPrincipalcashBalance() {
        return PrincipalcashBalance;
    }

    public void setPrincipalcashBalance(KualiDecimal principalcashBalance) {
        PrincipalcashBalance = principalcashBalance;
    }

    public BigDecimal getKemidTotalMarketValue() {
        return KemidTotalMarketValue;
    }

    public void setKemidTotalMarketValue(BigDecimal kemidTotalMarketValue) {
        KemidTotalMarketValue = kemidTotalMarketValue;
    }

    public BigDecimal getAvailableExpendableFunds() {
        return AvailableExpendableFunds;
    }

    public void setAvailableExpendableFunds(BigDecimal availableExpendableFunds) {
        AvailableExpendableFunds = availableExpendableFunds;
    }

    public BigDecimal getFyRemainderEstimatedIncome() {
        return FyRemainderEstimatedIncome;
    }

    public void setFyRemainderEstimatedIncome(BigDecimal fyRemainderEstimatedIncome) {
        FyRemainderEstimatedIncome = fyRemainderEstimatedIncome;
    }
            
}
