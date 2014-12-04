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
    public boolean displaySubtotal;

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
     * Gets the displaySubtotal attribute.
     *
     * @return Returns the displaySubtotal
     */
    public boolean isDisplaySubtotal() {
        return displaySubtotal;
    }

    /**
     * Sets the displaySubtotal attribute value.
     *
     * @param displaySubtotal The displaySubtotal to set.
     */
    public void setDisplaySubtotal(boolean displaySubtotal) {
        this.displaySubtotal = displaySubtotal;
    }

}
