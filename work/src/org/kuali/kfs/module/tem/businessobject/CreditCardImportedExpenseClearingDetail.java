/*
 * Copyright 2012 The Kuali Foundation.
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
        // TODO Auto-generated method stub
        return null;
    }

}
