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

package org.kuali.kfs.module.ar.report;

import java.math.BigDecimal;

/**
 * Defines a data holder class for Contracts and Grants Suspended Invoice Detail Reports.
 */
public class ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder {
    private String suspenseCategory;
    private String documentNumber;
    private String letterOfCreditFundGroupCode;
    private String fundManager;
    private String projectDirector;
    private BigDecimal awardTotal;
    private String sortedFieldValue;
    private BigDecimal subTotal;
    public boolean displaySubtotalInd;

    /**
     * Gets the suspenseCategory attribute.
     *
     * @return Returns the suspenseCategory
     */
    public String getSuspenseCategory() {
        return suspenseCategory;
    }

    /**
     * Sets the suspenseCategory attribute value.
     *
     * @param suspenseCategory The suspenseCategory to set.
     */
    public void setSuspenseCategory(String suspenseCategory) {
        this.suspenseCategory = suspenseCategory;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute value.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    /**
     * Gets the fundManager attribute.
     *
     * @return Returns the fundManager
     */
    public String getFundManager() {
        return fundManager;
    }

    /**
     * Sets the fundManager attribute value.
     *
     * @param fundManager The fundManager to set.
     */
    public void setFundManager(String fundManager) {
        this.fundManager = fundManager;
    }

    /**
     * Gets the projectDirector attribute.
     *
     * @return Returns the projectDirector
     */
    public String getProjectDirector() {
        return projectDirector;
    }

    /**
     * Sets the projectDirector attribute value.
     *
     * @param projectDirector The projectDirector to set.
     */
    public void setProjectDirector(String projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * Gets the awardTotal attribute.
     *
     * @return Returns the awardTotal
     */
    public BigDecimal getAwardTotal() {
        return awardTotal;
    }

    /**
     * Sets the awardTotal attribute value.
     *
     * @param awardTotal The awardTotal to set.
     */
    public void setAwardTotal(BigDecimal awardTotal) {
        this.awardTotal = awardTotal;
    }

    /**
     * Gets the sortedFieldValue attribute.
     *
     * @return Returns the sortedFieldValue
     */
    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    /**
     * Sets the sortedFieldValue attribute value.
     *
     * @param sortedFieldValue The sortedFieldValue to set.
     */
    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    /**
     * Gets the subTotal attribute.
     *
     * @return Returns the subTotal
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * Sets the subTotal attribute value.
     *
     * @param subTotal The subTotal to set.
     */
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * Gets the displaySubtotalInd attribute.
     *
     * @return Returns the displaySubtotalInd
     */
    public boolean isDisplaySubtotalInd() {
        return displaySubtotalInd;
    }

    /**
     * Sets the displaySubtotalInd attribute value.
     *
     * @param displaySubtotalInd The displaySubtotalInd to set.
     */
    public void setDisplaySubtotalInd(boolean displaySubtotalInd) {
        this.displaySubtotalInd = displaySubtotalInd;
    }

}
