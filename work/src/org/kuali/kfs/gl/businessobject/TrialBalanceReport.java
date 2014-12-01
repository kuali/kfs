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

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class TrialBalanceReport extends Balance {
    private Integer index;
    private Integer beginningFiscalYear;
    private KualiDecimal debitAmount;
    private KualiDecimal creditAmount;
    private String finChartOfAccountDescription;
    private String financialObjectCodeName;
    private String universityFiscalPeriodCode;


    /**
     * Gets the beginningFiscalYear attribute.
     *
     * @return Returns the beginningFiscalYear.
     */
    public Integer getBeginningFiscalYear() {
        return beginningFiscalYear;
    }


    /**
     * Sets the beginningFiscalYear attribute value.
     *
     * @param beginningFiscalYear The beginningFiscalYear to set.
     */
    public void setBeginningFiscalYear(Integer beginningFiscalYear) {
        this.beginningFiscalYear = beginningFiscalYear;
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
     * Gets the finChartOfAccountDescription attribute.
     *
     * @return Returns the finChartOfAccountDescription.
     */
    public String getFinChartOfAccountDescription() {
        return finChartOfAccountDescription;
    }


    /**
     * Sets the finChartOfAccountDescription attribute value.
     *
     * @param finChartOfAccountDescription The finChartOfAccountDescription to set.
     */
    public void setFinChartOfAccountDescription(String finChartOfAccountDescription) {
        this.finChartOfAccountDescription = finChartOfAccountDescription;
    }


    /**
     * Gets the financialObjectCodeName attribute.
     *
     * @return Returns the financialObjectCodeName.
     */
    public String getFinancialObjectCodeName() {
        return financialObjectCodeName;
    }


    /**
     * Sets the financialObjectCodeName attribute value.
     *
     * @param financialObjectCodeName The financialObjectCodeName to set.
     */
    public void setFinancialObjectCodeName(String financialObjectCodeName) {
        this.financialObjectCodeName = financialObjectCodeName;
    }
    
   
    /**
     * Gets the universityFiscalPeriodCode attribute.
     * 
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    
    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }


    public Integer getIndex() {
        return index;
    }


    public void setIndex(Integer index) {
        this.index = index;
    }


}
