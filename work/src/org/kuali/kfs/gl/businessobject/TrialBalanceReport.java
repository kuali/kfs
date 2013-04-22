/*
 * Copyright 2010 The Kuali Foundation.
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

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class TrialBalanceReport extends Balance {
    private Integer index;
    private Integer beginningFiscalYear;
    private KualiDecimal debitAmount;
    private KualiDecimal creditAmount;
    private String finChartOfAccountDescription;
    private String financialObjectCodeName;


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


    public Integer getIndex() {
        return index;
    }


    public void setIndex(Integer index) {
        this.index = index;
    }


}
