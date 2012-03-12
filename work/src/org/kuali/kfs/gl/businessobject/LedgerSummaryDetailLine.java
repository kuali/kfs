/*
 * Copyright 2009 The Kuali Foundation
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

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Holds summary data for the ledger summary report
 */
public class LedgerSummaryDetailLine extends LedgerBalanceTypeSummaryTotalLine {
    private String financialSystemOriginationCode;
    private Integer universityFiscalYear;
    private String universityAccountPeriodCode;

    /**
     * Constructs a LedgerSummaryDetailLine
     * 
     * @param balanceTypeCode
     * @param financialSystemOriginationCode
     * @param universityFiscalYear
     * @param universityAccountPeriodCode
     */
    public LedgerSummaryDetailLine(String balanceTypeCode, String financialSystemOriginationCode, Integer universityFiscalYear, String universityAccountPeriodCode) {
        super(balanceTypeCode);
        this.financialSystemOriginationCode = financialSystemOriginationCode;
        this.universityFiscalYear = universityFiscalYear;
        this.universityAccountPeriodCode = universityAccountPeriodCode;
    }

    /**
     * Gets the financialSystemOriginationCode attribute.
     * 
     * @return Returns the financialSystemOriginationCode.
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Gets the universityAccountPeriodCode attribute.
     * 
     * @return Returns the universityAccountPeriodCode.
     */
    public String getUniversityAccountPeriodCode() {
        return universityAccountPeriodCode;
    }

    /**
     * @return gets a "key" for this summary line - just a convenient key for Maps which might hold these
     */
    public String getKey() {
        return LedgerSummaryDetailLine.makeKey(this.getFinancialBalanceTypeCode(), this.getFinancialSystemOriginationCode(), this.getUniversityFiscalYear(), this.getUniversityAccountPeriodCode());
    }

    /**
     * Generates a Map key in a consistent format with the rest of the uses of this class for a given OriginEntryInformation
     * 
     * @param entry the entry to build a key for
     * @return the "key" for a summary line which would include totals from entries like the given origin entry
     */
    public static String getKeyString(OriginEntryInformation entry) {
        return LedgerSummaryDetailLine.makeKey(entry.getFinancialBalanceTypeCode(), entry.getFinancialSystemOriginationCode(), entry.getUniversityFiscalYear(), entry.getUniversityFiscalPeriodCode());
    }

    /**
     * Given the various values, puts together a convenient Map key
     * 
     * @param balanceTypeCode a balance type code
     * @param financialSystemOriginationCode an origination code
     * @param universityFiscalYear a fiscal year, smothered in mustard
     * @param universityAccountingPeriodCode an accounting period code
     * @return all of them magically put together, to form a Map key. Like Voltron, but more financially oriented
     */
    private static String makeKey(String balanceTypeCode, String financialSystemOriginationCode, Integer universityFiscalYear, String universityAccountingPeriodCode) {
        return StringUtils.join(new String[] { balanceTypeCode, financialSystemOriginationCode, universityFiscalYear == null ? "" : universityFiscalYear.toString(), universityAccountingPeriodCode }, ':');
    }

    /**
     * @return a standard comparator for comparing NightlyOutPendingEntryLedgerSummaryDetailLine objects
     */
    public static Comparator<LedgerSummaryDetailLine> getStandardComparator() {
        return new Comparator<LedgerSummaryDetailLine>() {

            /**
             * Compares two NightlyOutPendingEntryLedgerSummaryDetailLine objects
             * 
             * @param detail1 the first NightlyOutPendingEntryLedgerSummaryDetailLine object
             * @param detail2 the second NightlyOutPendingEntryLedgerSummaryDetailLine other
             * @return the standard 0 for equals, greater than 0 for greater than, less than 0 for less than
             */
            public int compare(LedgerSummaryDetailLine detail1, LedgerSummaryDetailLine detail2) {
                int comp = 0;
                comp = nullSafeCompare(detail1.getFinancialBalanceTypeCode(), detail2.getFinancialBalanceTypeCode());

                if (comp == 0) {
                    comp = nullSafeCompare(detail1.getFinancialSystemOriginationCode(), detail2.getFinancialSystemOriginationCode());
                }

                if (comp == 0) {
                    comp = nullSafeCompare(detail1.getUniversityFiscalYear(), detail2.getUniversityFiscalYear());
                }

                if (comp == 0) {
                    comp = nullSafeCompare(detail1.getUniversityAccountPeriodCode(), detail2.getUniversityAccountPeriodCode());
                }

                return comp;
            }

            /**
             * Checks for nulls in the two comparables before calling the compare. If one is null and not the other, the null is
             * considered less. If both are null they are considered equal.
             * 
             * @param o1 object to compare
             * @param o2 object to compare o1 to
             * @return -1 for less, 0 for equal, 1 for greater
             */
            protected int nullSafeCompare(Comparable o1, Comparable o2) {
                if (o1 == null && o2 != null) {
                    return -1;
                }

                if (o1 != null && o2 == null) {
                    return 1;
                }

                if (o1 == null && o2 == null) {
                    return 0;
                }

                return o1.compareTo(o2);
            }
        };
    }

    public static String[] keyFields = new String[] { KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "universityAccountPeriodCode" };

}
