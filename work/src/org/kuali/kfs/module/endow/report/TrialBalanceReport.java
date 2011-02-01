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
package org.kuali.kfs.module.endow.report;

import org.kuali.rice.kns.util.KualiDecimal;

public class TrialBalanceReport {

    private String kemid;
    private String kemidName;
    private KualiDecimal InocmeCashBalance;
    private KualiDecimal PrincipalcashBalance;
    private KualiDecimal KemidTotalMarketValue;
    private KualiDecimal AvailableExpendableFunds;
    private KualiDecimal FyRemainderEstimatedIncome;

    /**
     * 
     */
    public TrialBalanceReport() {}

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
    public TrialBalanceReport(
            String kemid,
            String kemidName,
            KualiDecimal InocmeCashBalance,
            KualiDecimal PrincipalcashBalance,
            KualiDecimal KemidTotalMarketValue,
            KualiDecimal AvailableExpendableFunds,
            KualiDecimal FyRemainderEstimatedIncome) {
        
        this.kemid = kemid;
        this.kemidName = kemidName;
        this.InocmeCashBalance = InocmeCashBalance;
        this.PrincipalcashBalance = PrincipalcashBalance;
        this.KemidTotalMarketValue = KemidTotalMarketValue;
        this.AvailableExpendableFunds = AvailableExpendableFunds;
        this.FyRemainderEstimatedIncome = FyRemainderEstimatedIncome;
    }
        
    /**
     * 
     */
    public String getKemid() {
        return kemid;
    }
    /**
     * 
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }
    /**
     * 
     */
    public String getKemidName() {
        return kemidName;
    }
    /**
     * 
     */
    public void setKemidName(String kemidName) {
        this.kemidName = kemidName;
    }
    /**
     * 
     */
    public KualiDecimal getInocmeCashBalance() {
        return InocmeCashBalance;
    }
    /**
     * 
     */
    public void setInocmeCashBalance(KualiDecimal inocmeCashBalance) {
        InocmeCashBalance = inocmeCashBalance;
    }
    /**
     * 
     */
    public KualiDecimal getPrincipalcashBalance() {
        return PrincipalcashBalance;
    }
    /**
     * 
     */
    public void setPrincipalcashBalance(KualiDecimal principalcashBalance) {
        PrincipalcashBalance = principalcashBalance;
    }
    /**
     * 
     */
    public KualiDecimal getKemidTotalMarketValue() {
        return KemidTotalMarketValue;
    }
    /**
     * 
     */
    public void setKemidTotalMarketValue(KualiDecimal kemidTotalMarketValue) {
        KemidTotalMarketValue = kemidTotalMarketValue;
    }
    /**
     * 
     */
    public KualiDecimal getAvailableExpendableFunds() {
        return AvailableExpendableFunds;
    }
    /**
     * 
     */
    public void setAvailableExpendableFunds(KualiDecimal availableExpendableFunds) {
        AvailableExpendableFunds = availableExpendableFunds;
    }
    /**
     * 
     */
    public KualiDecimal getFyRemainderEstimatedIncome() {
        return FyRemainderEstimatedIncome;
    }
    /**
     * 
     */
    public void setFyRemainderEstimatedIncome(KualiDecimal fyRemainderEstimatedIncome) {
        FyRemainderEstimatedIncome = fyRemainderEstimatedIncome;
    }
    
    
}
