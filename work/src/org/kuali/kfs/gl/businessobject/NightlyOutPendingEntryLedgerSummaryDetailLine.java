/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

/**
 * Holds summary data for the Nightly Out job's pending entry ledger summary report
 */
public class NightlyOutPendingEntryLedgerSummaryDetailLine extends NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine {
    private String financialSystemOriginationCode;
    private Integer universityFiscalYear;
    private String universityAccountPeriodCode; 
    
    /**
     * Constructs a NightlyOutPendingEntryLedgerSummaryDetailLine
     * @param balanceTypeCode
     * @param financialSystemOriginationCode
     * @param universityFiscalYear
     * @param universityAccountPeriodCode
     */
    public NightlyOutPendingEntryLedgerSummaryDetailLine(String balanceTypeCode, String financialSystemOriginationCode, Integer universityFiscalYear, String universityAccountPeriodCode) {
        super(balanceTypeCode);
        this.financialSystemOriginationCode = financialSystemOriginationCode;
        this.universityFiscalYear = universityFiscalYear;
        this.universityAccountPeriodCode = universityAccountPeriodCode;
    }

    /**
     * Gets the financialSystemOriginationCode attribute. 
     * @return Returns the financialSystemOriginationCode.
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Gets the universityAccountPeriodCode attribute. 
     * @return Returns the universityAccountPeriodCode.
     */
    public String getUniversityAccountPeriodCode() {
        return universityAccountPeriodCode;
    }

    /**
     * @return gets a "key" for this summary line - just a convenient key for Maps which might hold these
     */
    public String getKey() {
        return NightlyOutPendingEntryLedgerSummaryDetailLine.makeKey(this.getFinancialBalanceTypeCode(),this.getFinancialSystemOriginationCode(),this.getUniversityFiscalYear(),this.getUniversityAccountPeriodCode());
    }
    
    /**
     * Generates a Map key in a consistent format with the rest of the uses of this class for a given OriginEntry 
     * @param entry the entry to build a key for
     * @return the "key" for a summary line which would include totals from entries like the given origin entry
     */
    public static String getKeyString(OriginEntry entry) {
        return NightlyOutPendingEntryLedgerSummaryDetailLine.makeKey(entry.getFinancialBalanceTypeCode(), entry.getFinancialSystemOriginationCode(), entry.getUniversityFiscalYear(), entry.getUniversityFiscalPeriodCode());
    }
    
    /**
     * Given the various values, puts together a convenient Map key
     * @param balanceTypeCode a balance type code
     * @param financialSystemOriginationCode an origination code
     * @param universityFiscalYear a fiscal year, smothered in mustard
     * @param universityAccountingPeriodCode an accounting period code
     * @return all of them magically put together, to form a Map key.  Like Voltron, but more financially oriented
     */
    private static String makeKey(String balanceTypeCode, String financialSystemOriginationCode, Integer universityFiscalYear, String universityAccountingPeriodCode) {
        return StringUtils.join(new String[] {balanceTypeCode, financialSystemOriginationCode,universityFiscalYear.toString(),universityAccountingPeriodCode}, ':');
    }
    
    /**
     * @return a standard comparator for comparing NightlyOutPendingEntryLedgerSummaryDetailLine objects
     */
    public static Comparator<NightlyOutPendingEntryLedgerSummaryDetailLine> getStandardComparator() {
        return new Comparator<NightlyOutPendingEntryLedgerSummaryDetailLine>() {
            /**
             * Compares two NightlyOutPendingEntryLedgerSummaryDetailLine objects
             * @param tweedleDee the first NightlyOutPendingEntryLedgerSummaryDetailLine object
             * @param tweedleDum the second NightlyOutPendingEntryLedgerSummaryDetailLine other
             * @return the standard 0 for equals, greater than 0 for greater than, less than 0 for less than
             */
            public int compare(NightlyOutPendingEntryLedgerSummaryDetailLine tweedleDee, NightlyOutPendingEntryLedgerSummaryDetailLine tweedleDum) {
                if (shouldCompare(tweedleDee.getFinancialBalanceTypeCode(), tweedleDum.getFinancialBalanceTypeCode())) {
                    return tweedleDee.getFinancialBalanceTypeCode().compareTo(tweedleDum.getFinancialBalanceTypeCode());
                } else if (shouldCompare(tweedleDee.getFinancialSystemOriginationCode(), tweedleDum.getFinancialSystemOriginationCode())) {
                    return tweedleDee.getFinancialSystemOriginationCode().compareTo(tweedleDum.getFinancialSystemOriginationCode());
                } else if (shouldCompare(tweedleDee.getUniversityFiscalYear(), tweedleDum.getUniversityFiscalYear())) {
                    return tweedleDee.getUniversityFiscalYear().compareTo(tweedleDum.getUniversityFiscalYear());
                } else if (shouldCompare(tweedleDee.getUniversityAccountPeriodCode(), tweedleDum.getUniversityAccountPeriodCode())) {
                    return tweedleDee.getUniversityAccountPeriodCode().compareTo(tweedleDum.getUniversityAccountPeriodCode());
                } else {
                    return 0;
                }
            }
            
            /**
             * Determines if it's safe to compare two Strings
             * @param s1 the first String we may compare
             * @param s2 the second String we may compare
             * @return true if comparison of these two Strings would be meaningful
             */
            protected boolean shouldCompare(String s1, String s2) {
                return !StringUtils.isBlank(s1) && !StringUtils.isBlank(s2) && !s1.equals(s2);
            }
            
            /**
             * Determine if it's safe to compare two Integers
             * @param i1 the first Integer we may compare
             * @param i2 the second Integer we may compare
             * @return true if comparison of the two Integers would be meaningful
             */
            protected boolean shouldCompare(Integer i1, Integer i2) {
                return i1 != null && i2 != null && !i1.equals(i2);
            }
        };
    }
}