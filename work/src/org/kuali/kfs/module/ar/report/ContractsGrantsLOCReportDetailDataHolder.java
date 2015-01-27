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
import java.sql.Date;

/**
 * This class defines a data holder object for report generation services for Contracts & Grants LOC Draw Details Report Detail.
 */
public class ContractsGrantsLOCReportDetailDataHolder {
    private String documentNumber;
    private String letterOfCreditFundCode;
    private String letterOfCreditFundGroupCode;
    private Date letterOfCreditReviewCreateDate;
    private BigDecimal amountAvailableToDraw;
    private BigDecimal claimOnCashBalance;
    private BigDecimal amountToDraw;
    private BigDecimal fundsNotDrawn;


    private String sortedFieldValue;

    private BigDecimal subTotal;

    public boolean displaySubtotal;


    /**
     * @return sortedFieldValue
     */
    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    /**
     * @param sortedFieldValue
     */
    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    /**
     * @return subTotal
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * @param subTotal
     */
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * @return displaySubtotal
     */
    public boolean isDisplaySubtotal() {
        return displaySubtotal;
    }

    /**
     * @param displaySubtotal
     */
    public void setDisplaySubtotal(boolean displaySubtotal) {
        this.displaySubtotal = displaySubtotal;
    }

    /**
     * @return the letterOfCreditFundCode
     */
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    /**
     * @param letterOfCreditFundCode the letterOfCreditFundCode to set
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    /**
     * @return the letterOfCreditFundGroupCode
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * @param letterOfCreditFundGroupCode the letterOfCreditFundGroupCode to set
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    /**
     * @return the letterOfCreditReviewCreateDate
     */
    public Date getLetterOfCreditReviewCreateDate() {
        return letterOfCreditReviewCreateDate;
    }

    /**
     * @param letterOfCreditReviewCreateDate the letterOfCreditReviewCreateDate to set
     */
    public void setLetterOfCreditReviewCreateDate(Date letterOfCreditReviewCreateDate) {
        this.letterOfCreditReviewCreateDate = letterOfCreditReviewCreateDate;
    }

    /**
     * Gets the amountAvailableToDraw attribute.
     *
     * @return Returns the amountAvailableToDraw.
     */
    public BigDecimal getAmountAvailableToDraw() {
        return amountAvailableToDraw;
    }

    /**
     * Sets the amountAvailableToDraw attribute value.
     *
     * @param amountAvailableToDraw The amountAvailableToDraw to set.
     */
    public void setAmountAvailableToDraw(BigDecimal amountAvailableToDraw) {
        this.amountAvailableToDraw = amountAvailableToDraw;
    }

    /**
     * Gets the claimOnCashBalance attribute.
     *
     * @return Returns the claimOnCashBalance.
     */
    public BigDecimal getClaimOnCashBalance() {
        return claimOnCashBalance;
    }

    /**
     * Sets the claimOnCashBalance attribute value.
     *
     * @param claimOnCashBalance The claimOnCashBalance to set.
     */
    public void setClaimOnCashBalance(BigDecimal claimOnCashBalance) {
        this.claimOnCashBalance = claimOnCashBalance;
    }

    /**
     * Gets the amountToDraw attribute.
     *
     * @return Returns the amountToDraw.
     */
    public BigDecimal getAmountToDraw() {
        return amountToDraw;
    }

    /**
     * Sets the amountToDraw attribute value.
     *
     * @param amountToDraw The amountToDraw to set.
     */
    public void setAmountToDraw(BigDecimal amountToDraw) {
        this.amountToDraw = amountToDraw;
    }

    /**
     * Gets the fundsNotDrawn attribute.
     *
     * @return Returns the fundsNotDrawn.
     */
    public BigDecimal getFundsNotDrawn() {
        return fundsNotDrawn;
    }

    /**
     * Sets the fundsNotDrawn attribute value.
     *
     * @param fundsNotDrawn The fundsNotDrawn to set.
     */
    public void setFundsNotDrawn(BigDecimal fundsNotDrawn) {
        this.fundsNotDrawn = fundsNotDrawn;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
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

}
