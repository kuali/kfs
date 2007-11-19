/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.ObjectTypeService;

/**
 * This class represents a poster output summary entry
 */
public class PosterOutputSummaryEntry implements Comparable {
    private Integer universityFiscalYear;
    private String fiscalPeriodCode;
    private String balanceTypeCode;
    private String fundGroup;
    private String objectTypeCode;
    private KualiDecimal creditAmount;
    private KualiDecimal debitAmount;
    private KualiDecimal budgetAmount;
    private KualiDecimal netAmount;

    private final String[] assetExpenseObjectTypeCodes;

    public PosterOutputSummaryEntry() {
        creditAmount = KualiDecimal.ZERO;
        debitAmount = KualiDecimal.ZERO;
        budgetAmount = KualiDecimal.ZERO;
        netAmount = KualiDecimal.ZERO;

        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getCurrentYearExpenseObjectTypes();
        objectTypes.add(objectTypeService.getCurrentYearAssetObjectType());

        assetExpenseObjectTypeCodes = objectTypes.toArray(new String[0]);
    }

    /**
     * This method returns the key for the poster output summary entry
     * 
     * @return String returns a string with format "universityFiscalYear-balanceTypeCode-fiscalPeriodCode-fundGroup"
     */
    public String getKey() {
        return universityFiscalYear + "-" + balanceTypeCode + "-" + fiscalPeriodCode + "-" + fundGroup;
    }

    /**
     * Add the amounts of two summary entries
     * 
     * @param posterInputSummaryEntry a poster input summary entry which contains the amounts to add to this poster output summary entry 
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
     * This method sets the amounts for this poster output summary entry.
     * 
     * @param debitCreditCode credit code used to determine whether amounts is debit or credit
     * @param objectTypeCode object type code associated with amount
     * @param amount amount to add
     */
    public void setAmount(String debitCreditCode, String objectTypeCode, KualiDecimal amount) {

        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            setCreditAmount(creditAmount.add(amount));
            if (ArrayUtils.contains(assetExpenseObjectTypeCodes, objectTypeCode)) {
                setNetAmount(netAmount.subtract(amount));
            }
            else {
                setNetAmount(netAmount.add(amount));
            }
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode)) {
            setDebitAmount(debitAmount.add(amount));
            if (ArrayUtils.contains(assetExpenseObjectTypeCodes, objectTypeCode)) {
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

    public String toString() {
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

    /**
     * Compares this poster output summary entry with another poster output summary entry based on key value
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object anotherPosterOutputSummaryEntry) {
        PosterOutputSummaryEntry tempPosterOutputSummaryEntry = (PosterOutputSummaryEntry) anotherPosterOutputSummaryEntry;

        return getKey().compareTo(tempPosterOutputSummaryEntry.getKey());
    }

    /**
     * This method returns an empty PosterOutputSummaryEntry 
     * 
     * @param entrySummary
     * @return
     */
    public static PosterOutputSummaryEntry buildPosterOutputSummaryEntry(Object[] entrySummary) {
        PosterOutputSummaryEntry posterOutputSummaryEntry = new PosterOutputSummaryEntry();
        int indexOfField = 0;

        Object tempEntry = entrySummary[indexOfField++];
        String entry = (tempEntry == null) ? "" : tempEntry.toString();
        posterOutputSummaryEntry.setBalanceTypeCode(entry);

        tempEntry = entrySummary[indexOfField++];
        entry = (tempEntry == null) ? null : tempEntry.toString();
        posterOutputSummaryEntry.setUniversityFiscalYear(new Integer(entry));

        tempEntry = entrySummary[indexOfField++];
        entry = (tempEntry == null) ? "" : tempEntry.toString();
        posterOutputSummaryEntry.setFiscalPeriodCode(entry);

        tempEntry = entrySummary[indexOfField++];
        entry = (tempEntry == null) ? "" : tempEntry.toString();
        posterOutputSummaryEntry.setFundGroup(entry);

        tempEntry = entrySummary[indexOfField++];
        String objectTypeCode = (tempEntry == null) ? "" : tempEntry.toString();
        posterOutputSummaryEntry.setObjectTypeCode(objectTypeCode);

        tempEntry = entrySummary[indexOfField++];
        String debitCreditCode = (tempEntry == null) ? KFSConstants.GL_BUDGET_CODE : tempEntry.toString();

        tempEntry = entrySummary[indexOfField];
        entry = (tempEntry == null) ? "0" : tempEntry.toString();
        KualiDecimal amount = new KualiDecimal(entry);

        posterOutputSummaryEntry.setAmount(debitCreditCode, objectTypeCode, amount);

        return posterOutputSummaryEntry;
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
