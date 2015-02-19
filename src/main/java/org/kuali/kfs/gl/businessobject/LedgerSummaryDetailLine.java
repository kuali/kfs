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
