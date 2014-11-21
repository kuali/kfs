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

package org.kuali.kfs.fp.businessobject;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a travel mileage rate business object.
 */
public class TravelMileageRate extends PersistableBusinessObjectBase {
    protected Date disbursementVoucherMileageEffectiveDate;
    protected Integer mileageLimitAmount;
    protected BigDecimal mileageRate;

    /**
     * Default no-arg constructor.
     */
    public TravelMileageRate() {

    }

    /**
     * Gets the mileageLimitAmount attribute.
     *
     * @return Returns the mileageLimitAmount
     */
    public Integer getMileageLimitAmount() {
        return mileageLimitAmount;
    }


    /**
     * Sets the mileageLimitAmount attribute.
     *
     * @param mileageLimitAmount The disbVchrMileageLimitAmount to set.
     */
    public void setMileageLimitAmount(Integer mileageLimitAmount) {
        this.mileageLimitAmount = mileageLimitAmount;
    }

    /**
     * Gets the mileageRate attribute.
     *
     * @return Returns the mileageRate
     */
    public BigDecimal getMileageRate() {
        return mileageRate;
    }


    /**
     * Sets the mileageRate attribute.
     *
     * @param mileageRate The mileageRate to set.
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

}
