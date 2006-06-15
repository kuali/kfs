/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
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
     * @return - Returns the mileageLimitAmount
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
     * @return - Returns the mileageRate
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