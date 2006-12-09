/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;

public class PosterOutputSummaryEntry implements Comparable{
    private static String[] assetExpenseObjectTypeCodeList = new String[] { "AS", "EE", "ES", "EX", "TE" };

    private Integer universityFiscalYear;
    private String fiscalPeriodCode;
    private String balanceTypeCode;
    private String fundGroup;
    private String objectTypeCode;
    private KualiDecimal creditAmount;
    private KualiDecimal debitAmount;
    private KualiDecimal budgetAmount;
    private KualiDecimal netAmount;

    public PosterOutputSummaryEntry() {
        creditAmount = KualiDecimal.ZERO;
        debitAmount = KualiDecimal.ZERO;
        budgetAmount = KualiDecimal.ZERO;
        netAmount = KualiDecimal.ZERO;
    }

    public String getKey() {
        return universityFiscalYear + "-" + balanceTypeCode + "-" + fiscalPeriodCode + "-" + fundGroup; 
    }

    /**
     * add the amounts of two summary entries
     */
    public void add(PosterOutputSummaryEntry posterInputSummaryEntry) {
        // calculate the credit amount
        setCreditAmount(creditAmount.add(posterInputSummaryEntry.getCreditAmount()));

        // calculate the debit amount
        setDebitAmount(debitAmount.add(posterInputSummaryEntry.getDebitAmount()));

        // calculate the budget amount
        setBudgetAmount(budgetAmount.add(posterInputSummaryEntry.getBudgetAmount()));

        // calculate the net amount
        setNetAmount(netAmount.add(posterInputSummaryEntry.getNetAmount()));
    }

    /**
     * add the amounts of two summary entries
     */
    public void setAmount(String debitCreditCode, String objectTypeCode, KualiDecimal amount) {

        if (Constants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            setCreditAmount(creditAmount.add(amount));
            if ( ArrayUtils.contains(assetExpenseObjectTypeCodeList, objectTypeCode) ) {
                setNetAmount(netAmount.subtract(amount));
            }
            else {
                setNetAmount(netAmount.add(amount));
            }
        }
        else if (Constants.GL_DEBIT_CODE.equals(debitCreditCode)) {
            setDebitAmount(debitAmount.add(amount));
            if ( ArrayUtils.contains(assetExpenseObjectTypeCodeList, objectTypeCode) ) {
                setNetAmount(netAmount.add(amount));
            }
            else {
                setNetAmount(netAmount.subtract(amount));
            }
        }
        else {
            setNetAmount(netAmount.add(amount));
            setBudgetAmount(amount);
        }
    }

    public String toString(){
       String posterOutputSummaryEntry = "";
       posterOutputSummaryEntry += "[UniversityFiscalYear: " + this.getUniversityFiscalYear();
       posterOutputSummaryEntry += ", FiscalPeriodCode: " + this.getFiscalPeriodCode();
       posterOutputSummaryEntry += ", BalanceTypeCode:" + this.getBalanceTypeCode();
       posterOutputSummaryEntry += ", FundGroup: " + this.getFundGroup();
       posterOutputSummaryEntry += ", ObjectTypeCode: " + this.getObjectTypeCode();
       posterOutputSummaryEntry += ", CreditAmount: " + this.getCreditAmount();
       posterOutputSummaryEntry += ", DebitAmount: " + this.getDebitAmount();
       posterOutputSummaryEntry += ", BudgetAmount: " + this.getBudgetAmount();
       posterOutputSummaryEntry += ", NetAmount: " + this.getNetAmount();
       posterOutputSummaryEntry += "]";
        
        return posterOutputSummaryEntry;
    }

    public int compareTo(Object anotherPosterOutputSummaryEntry) {
        PosterOutputSummaryEntry tempPosterOutputSummaryEntry = (PosterOutputSummaryEntry)anotherPosterOutputSummaryEntry;

        return getKey().compareTo(tempPosterOutputSummaryEntry.getKey());
    }

    public String getFiscalPeriodCode() {
        return fiscalPeriodCode;
    }

    public void setFiscalPeriodCode(String fiscalPeriodCode) {
        this.fiscalPeriodCode = fiscalPeriodCode;
    }

    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    public KualiDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(KualiDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public KualiDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(KualiDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public KualiDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(KualiDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getFundGroup() {
        return fundGroup;
    }

    public void setFundGroup(String fundGroup) {
        this.fundGroup = fundGroup;
    }

    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    public void setObjectTypeCode(String objectTypeCode) {
        this.objectTypeCode = objectTypeCode;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public KualiDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(KualiDecimal netAmount) {
        this.netAmount = netAmount;
    }
}
