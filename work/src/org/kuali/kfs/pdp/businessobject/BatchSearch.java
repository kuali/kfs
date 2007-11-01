/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author delyea
 */
public class BatchSearch implements Serializable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSearch.class);

    private Integer batchId;
    private Integer paymentCount;
    private BigDecimal paymentTotalAmount;
    private Date beginDate;
    private Date endDate;
    private String chartCode;
    private String orgCode;
    private String subUnitCode;

    /**
     * @return Returns the batchId.
     */
    public Integer getBatchId() {
        return batchId;
    }

    /**
     * @return Returns the beginDate.
     */
    public Date getBeginDate() {
        return beginDate;
    }

    /**
     * @return Returns the chartCode.
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * @return Returns the endDate.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @return Returns the orgCode.
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * @return Returns the paymentCount.
     */
    public Integer getPaymentCount() {
        return paymentCount;
    }

    /**
     * @return Returns the paymentTotalAmount.
     */
    public BigDecimal getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    /**
     * @return Returns the subUnitCode.
     */
    public String getSubUnitCode() {
        return subUnitCode;
    }

    /**
     * @param batchId The batchId to set.
     */
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    /**
     * @param beginDate The beginDate to set.
     */
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * @param chartCode The chartCode to set.
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * @param endDate The endDate to set.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @param orgCode The orgCode to set.
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * @param paymentCount The paymentCount to set.
     */
    public void setPaymentCount(Integer paymentCount) {
        this.paymentCount = paymentCount;
    }

    /**
     * @param paymentTotalAmount The paymentTotalAmount to set.
     */
    public void setPaymentTotalAmount(BigDecimal paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }

    /**
     * @param subUnitCode The subUnitCode to set.
     */
    public void setSubUnitCode(String subUnitCode) {
        this.subUnitCode = subUnitCode;
    }
}
