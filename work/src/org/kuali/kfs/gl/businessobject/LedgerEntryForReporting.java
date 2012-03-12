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
package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * A representation of LedgerEntries, which are summaries that show up on Ledger Reports created by the scrubber and poster.
 */
public class LedgerEntryForReporting implements BusinessObject{

    private String balanceType;
    private String originCode;
    private Integer fiscalYear;
    private String period;
    private int recordCount;
    private KualiDecimal debitAmount;
    private int debitCount;
    private KualiDecimal creditAmount;
    private int creditCount;
    private KualiDecimal noDCAmount;
    private int noDCCount;

    /**
     * Constructs a LedgerEntry.java.
     */
    public LedgerEntryForReporting() {
        this(null, null, null, null);
    }

    /**
     * Constructs a LedgerEntry.java.
     * 
     * @param fiscalYear
     * @param period
     * @param balanceType
     * @param originCode
     */
    public LedgerEntryForReporting(Integer fiscalYear, String period, String balanceType, String originCode) {
        this.fiscalYear = fiscalYear;
        this.period = period;
        this.balanceType = balanceType;
        this.originCode = originCode;

        this.creditAmount = KualiDecimal.ZERO;
        this.debitAmount = KualiDecimal.ZERO;
        this.noDCAmount = KualiDecimal.ZERO;
    }

    /**
     * Add the amounts of the given ledger entry into those of current ledger entry, and update the counts of corresponding fields
     * 
     * @param addend the given ledger entry to be added into current one
     */
    public void add(LedgerEntryForReporting addend) {
        this.creditAmount = this.creditAmount.add(addend.getCreditAmount());
        this.creditCount += addend.getCreditCount();

        this.debitAmount = this.debitAmount.add(addend.getDebitAmount());
        this.debitCount += addend.getDebitCount();

        this.noDCAmount = this.noDCAmount.add(addend.getNoDCAmount());
        this.noDCCount += addend.getNoDCCount();

        this.recordCount = this.creditCount + this.debitCount + this.noDCCount;
    }

    /**
     * create or update a ledger entry with the array of information from the given entry summary object
     * 
     * @param entrySummary an entry summary to turn into a ledger entry
     * @return a LedgerEntry created from the entrySummary array
     */
    public static LedgerEntryForReporting buildLedgerEntry(Object[] entrySummary) {
        // extract the data from an array and use them to populate a ledger entry
        Object oFiscalYear = entrySummary[0];
        Object oPeriodCode = entrySummary[1];
        Object oBalanceType = entrySummary[2];
        Object oOriginCode = entrySummary[3];
        Object oDebitCreditCode = entrySummary[4];
        Object oAmount = entrySummary[5];
        Object oCount = entrySummary[6];

        Integer fiscalYear = oFiscalYear != null ? new Integer(oFiscalYear.toString()) : null;
        String periodCode = oPeriodCode != null ? oPeriodCode.toString() : "  ";
        String balanceType = oBalanceType != null ? oBalanceType.toString() : "  ";
        String originCode = oOriginCode != null ? oOriginCode.toString() : "  ";
        String debitCreditCode = oDebitCreditCode != null ? oDebitCreditCode.toString() : " ";
        KualiDecimal amount = oAmount != null ? new KualiDecimal(oAmount.toString()) : KualiDecimal.ZERO;
        int count = oCount != null ? Integer.parseInt(oCount.toString()) : 0;

        // construct a ledger entry with the information fetched from the given array
        LedgerEntryForReporting ledgerEntry = new LedgerEntryForReporting(fiscalYear, periodCode, balanceType, originCode);
        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setCreditAmount(amount);
            ledgerEntry.setCreditCount(count);
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setDebitAmount(amount);
            ledgerEntry.setDebitCount(count);
        }
        else {
            ledgerEntry.setNoDCAmount(amount);
            ledgerEntry.setNoDCCount(count);
        }
        ledgerEntry.setRecordCount(count);

        return ledgerEntry;
    }

    /**
     * Gets the balanceType attribute.
     * 
     * @return Returns the balanceType.
     */
    public String getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * 
     * @param balanceType The balanceType to set.
     */
    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the creditAmount attribute.
     * 
     * @return Returns the creditAmount.
     */
    public KualiDecimal getCreditAmount() {
        return creditAmount;
    }

    /**
     * Sets the creditAmount attribute value.
     * 
     * @param creditAmount The creditAmount to set.
     */
    public void setCreditAmount(KualiDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    /**
     * Gets the creditCount attribute.
     * 
     * @return Returns the creditCount.
     */
    public int getCreditCount() {
        return creditCount;
    }

    /**
     * Sets the creditCount attribute value.
     * 
     * @param creditCount The creditCount to set.
     */
    public void setCreditCount(int creditCount) {
        this.creditCount = creditCount;
    }

    /**
     * Gets the debitAmount attribute.
     * 
     * @return Returns the debitAmount.
     */
    public KualiDecimal getDebitAmount() {
        return debitAmount;
    }

    /**
     * Sets the debitAmount attribute value.
     * 
     * @param debitAmount The debitAmount to set.
     */
    public void setDebitAmount(KualiDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    /**
     * Gets the debitCount attribute.
     * 
     * @return Returns the debitCount.
     */
    public int getDebitCount() {
        return debitCount;
    }

    /**
     * Sets the debitCount attribute value.
     * 
     * @param debitCount The debitCount to set.
     */
    public void setDebitCount(int debitCount) {
        this.debitCount = debitCount;
    }

    /**
     * Gets the fiscalYear attribute.
     * 
     * @return Returns the fiscalYear.
     */
    public Integer getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear attribute value.
     * 
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * Gets the noDCAmount attribute.
     * 
     * @return Returns the noDCAmount.
     */
    public KualiDecimal getNoDCAmount() {
        return noDCAmount;
    }

    /**
     * Sets the noDCAmount attribute value.
     * 
     * @param noDCAmount The noDCAmount to set.
     */
    public void setNoDCAmount(KualiDecimal noDCAmount) {
        this.noDCAmount = noDCAmount;
    }

    /**
     * Gets the noDCCount attribute.
     * 
     * @return Returns the noDCCount.
     */
    public int getNoDCCount() {
        return noDCCount;
    }

    /**
     * Sets the noDCCount attribute value.
     * 
     * @param noDCCount The noDCCount to set.
     */
    public void setNoDCCount(int noDCCount) {
        this.noDCCount = noDCCount;
    }

    /**
     * Gets the originCode attribute.
     * 
     * @return Returns the originCode.
     */
    public String getOriginCode() {
        return originCode;
    }

    /**
     * Sets the originCode attribute value.
     * 
     * @param originCode The originCode to set.
     */
    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    /**
     * Gets the period attribute.
     * 
     * @return Returns the period.
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets the period attribute value.
     * 
     * @param period The period to set.
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * Gets the recordCount attribute.
     * 
     * @return Returns the recordCount.
     */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * Sets the recordCount attribute value.
     * 
     * @param recordCount The recordCount to set.
     */
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer ledgerEntryDescription = new StringBuffer();

        ledgerEntryDescription.append(fiscalYear + "\t");
        ledgerEntryDescription.append(period + "\t");
        ledgerEntryDescription.append(balanceType + "\t");
        ledgerEntryDescription.append(originCode + "\t");
        ledgerEntryDescription.append(recordCount + "\t");

        ledgerEntryDescription.append(debitAmount + "\t\t");
        ledgerEntryDescription.append(debitCount + "\t");

        ledgerEntryDescription.append(creditAmount + "\t\t");
        ledgerEntryDescription.append(creditCount + "\t");

        ledgerEntryDescription.append(noDCAmount + "\t\t");
        ledgerEntryDescription.append(noDCCount + "\t");

        return ledgerEntryDescription.toString();
    }

    public void prepareForWorkflow() {}
    public void refresh() { }
}
