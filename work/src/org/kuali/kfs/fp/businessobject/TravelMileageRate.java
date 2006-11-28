/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/TravelMileageRate.java,v $
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

package org.kuali.module.financial.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class TravelMileageRate extends BusinessObjectBase {
    private Date disbursementVoucherMileageEffectiveDate;
    private Integer mileageLimitAmount;
    private BigDecimal mileageRate;

    /**
     * Default no-arg constructor.
     */
    public TravelMileageRate() {

    }

    /**
     * Gets the mileageLimitAmount attribute.
     * 
     * @return Returns the mileageLimitAmount
     * 
     */
    public Integer getMileageLimitAmount() {
        return mileageLimitAmount;
    }


    /**
     * Sets the mileageLimitAmount attribute.
     * 
     * @param mileageLimitAmount The disbVchrMileageLimitAmount to set.
     * 
     */
    public void setMileageLimitAmount(Integer mileageLimitAmount) {
        this.mileageLimitAmount = mileageLimitAmount;
    }

    /**
     * Gets the mileageRate attribute.
     * 
     * @return Returns the mileageRate
     * 
     */
    public BigDecimal getMileageRate() {
        return mileageRate;
    }


    /**
     * Sets the mileageRate attribute.
     * 
     * @param mileageRate The mileageRate to set.
     * 
     */
    public void setMileageRate(BigDecimal mileageRate) {
        this.mileageRate = mileageRate;
    }

    /**
     * @return Returns the disbursementVoucherMileageEffectiveDate.
     */
    public Date getDisbursementVoucherMileageEffectiveDate() {
        return disbursementVoucherMileageEffectiveDate;
    }

    /**
     * @param disbursementVoucherMileageEffectiveDate The disbursementVoucherMileageEffectiveDate to set.
     */
    public void setDisbursementVoucherMileageEffectiveDate(Date disbursementVoucherMileageEffectiveDate) {
        this.disbursementVoucherMileageEffectiveDate = disbursementVoucherMileageEffectiveDate;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.disbursementVoucherMileageEffectiveDate != null) {
            m.put("disbursementVoucherMileageEffectiveDate", this.disbursementVoucherMileageEffectiveDate.toString());
        }
        if (this.mileageLimitAmount != null) {
            m.put("disbVchrMileageLimitAmount", this.mileageLimitAmount.toString());
        }
        return m;
    }


}