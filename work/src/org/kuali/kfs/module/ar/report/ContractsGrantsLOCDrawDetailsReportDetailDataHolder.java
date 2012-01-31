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
import java.sql.Date;

/**
 * This class defines a data holder object for report generation services for Contracts Grants LOC Draw Details Report Detail.
 */
public class ContractsGrantsLOCDrawDetailsReportDetailDataHolder {
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

    public boolean displaySubtotalInd;


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
     * @return displaySubtotalInd
     */
    public boolean isDisplaySubtotalInd() {
        return displaySubtotalInd;
    }

    /**
     * @param displaySubtotalInd
     */
    public void setDisplaySubtotalInd(boolean displaySubtotalInd) {
        this.displaySubtotalInd = displaySubtotalInd;
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
