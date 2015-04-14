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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ContractsGrantsLOCReport extends TransientBusinessObjectBase {

    private String documentNumber;
    private String letterOfCreditFundCode;
    private String letterOfCreditFundGroupCode;
    private Date letterOfCreditReviewCreateDate;
    private KualiDecimal amountAvailableToDraw;
    private KualiDecimal claimOnCashBalance;
    private KualiDecimal amountToDraw;
    private KualiDecimal fundsNotDrawn;
    private String reportType;

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
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
    public KualiDecimal getAmountAvailableToDraw() {
        return amountAvailableToDraw;
    }

    /**
     * Sets the amountAvailableToDraw attribute value.
     *
     * @param amountAvailableToDraw The amountAvailableToDraw to set.
     */
    public void setAmountAvailableToDraw(KualiDecimal amountAvailableToDraw) {
        this.amountAvailableToDraw = amountAvailableToDraw;
    }

    /**
     * Gets the claimOnCashBalance attribute.
     *
     * @return Returns the claimOnCashBalance.
     */
    public KualiDecimal getClaimOnCashBalance() {
        return claimOnCashBalance;
    }

    /**
     * Sets the claimOnCashBalance attribute value.
     *
     * @param claimOnCashBalance The claimOnCashBalance to set.
     */
    public void setClaimOnCashBalance(KualiDecimal claimOnCashBalance) {
        this.claimOnCashBalance = claimOnCashBalance;
    }

    /**
     * Gets the amountToDraw attribute.
     *
     * @return Returns the amountToDraw.
     */
    public KualiDecimal getAmountToDraw() {
        return amountToDraw;
    }

    /**
     * Sets the amountToDraw attribute value.
     *
     * @param amountToDraw The amountToDraw to set.
     */
    public void setAmountToDraw(KualiDecimal amountToDraw) {
        this.amountToDraw = amountToDraw;
    }

    /**
     * Gets the fundsNotDrawn attribute.
     *
     * @return Returns the fundsNotDrawn.
     */
    public KualiDecimal getFundsNotDrawn() {
        return fundsNotDrawn;
    }

    /**
     * Sets the fundsNotDrawn attribute value.
     *
     * @param fundsNotDrawn The fundsNotDrawn to set.
     */
    public void setFundsNotDrawn(KualiDecimal fundsNotDrawn) {
        this.fundsNotDrawn = fundsNotDrawn;
    }

    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("letterOfCreditFundCode", this.letterOfCreditFundCode);
        m.put("letterOfCreditFundGroupCode", this.letterOfCreditFundGroupCode);

        if (this.letterOfCreditReviewCreateDate != null) {
            m.put("letterOfCreditReviewCreateDate", this.letterOfCreditReviewCreateDate.toString());
        }

        if (this.amountAvailableToDraw != null) {
            m.put("amountAvailableToDraw", this.amountAvailableToDraw.toString());
        }

        if (this.claimOnCashBalance != null) {
            m.put("claimOnCashBalance", this.claimOnCashBalance.toString());
        }

        if (this.amountToDraw != null) {
            m.put("amountToDraw", this.amountToDraw.toString());
        }

        if (this.fundsNotDrawn != null) {
            m.put("fundsNotDrawn", this.fundsNotDrawn.toString());
        }

        return m;
    }
}
