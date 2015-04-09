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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

public class CreditCardImportedExpenseClearingDetail extends GlobalBusinessObjectDetailBase {
    private Integer creditCardStagingDataId;

    private String merchantName;
    private String travelerName;
    private Date bankPostDate;

    private transient CreditCardStagingData creditCardStagingData;

    /**
     * Gets the creditCardStagingDataId attribute.
     *
     * @return Returns the creditCardStagingDataId
     */

    public Integer getCreditCardStagingDataId() {
        return creditCardStagingDataId;
    }


    /**
     * Sets the creditCardStagingDataId attribute.
     *
     * @param historicalTravelExpenseId The creditCardStagingDataId to set.
     */
    public void setCreditCardStagingDataId(Integer creditCardStagingDataId) {
        this.creditCardStagingDataId = creditCardStagingDataId;
    }


    /**
     * Gets the merchantName attribute.
     *
     * @return Returns the merchantName
     */

    public String getMerchantName() {
        return getCreditCardStagingData().getMerchantName();
    }

    /**
     * Gets the travelerName attribute.
     *
     * @return Returns the travelerName
     */

    public String getTravelerName() {
        return getCreditCardStagingData().getTravelerName();
    }

    /**
     * Gets the bankPostDate attribute.
     *
     * @return Returns the bankPostDate
     */

    public Date getBankPostDate() {
        return getCreditCardStagingData().getBankPostDate();
    }

    /**
     * Sets the merchantName attribute.
     *
     * @param merchantName The merchantName to set.
     */
    public void setMerchantName(String merchantName) {
    }


    /**
     * Sets the travelerName attribute.
     *
     * @param travelerName The travelerName to set.
     */
    public void setTravelerName(String travelerName) {
    }


    /**
     * Sets the bankPostDate attribute.
     *
     * @param bankPostDate The bankPostDate to set.
     */
    public void setBankPostDate(Date bankPostDate) {
    }


    /**
     * Gets the historicalTravelExpense attribute.
     *
     * @return Returns the historicalTravelExpense
     */

    public CreditCardStagingData getCreditCardStagingData() {
        this.refreshReferenceObject("creditCardStagingData");
        return creditCardStagingData;
    }

    /**
     * Sets the historicalTravelExpense attribute.
     *
     * @param historicalTravelExpense The historicalTravelExpense to set.
     */
    public void setCreditCardStagingData(CreditCardStagingData creditCardStagingData) {
        this.creditCardStagingData = creditCardStagingData;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

}
