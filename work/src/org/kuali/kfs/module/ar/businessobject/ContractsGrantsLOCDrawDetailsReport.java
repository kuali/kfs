/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ContractsGrantsLOCDrawDetailsReport extends TransientBusinessObjectBase {

    private String documentNumber;
    private String letterOfCreditFundCode;
    private String letterOfCreditFundGroupCode;
    private Date letterOfCreditReviewCreateDate;
    private KualiDecimal amountAvailableToDraw;
    private KualiDecimal claimOnCashBalance;
    private KualiDecimal amountToDraw;
    private KualiDecimal fundsNotDrawn;

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

        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
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
