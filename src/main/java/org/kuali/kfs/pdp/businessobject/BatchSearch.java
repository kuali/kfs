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
/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * 
 */
public class BatchSearch extends TransientBusinessObjectBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSearch.class);

    private KualiInteger batchId;
    private KualiInteger paymentCount;
    private BigDecimal paymentTotalAmount;
    private Date beginDate;
    private Date endDate;
    private String chartCode;
    private String orgCode;
    private String subUnitCode;

    /**
     * @return Returns the batchId.
     */
    public KualiInteger getBatchId() {
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
    public KualiInteger getPaymentCount() {
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
    public void setBatchId(KualiInteger batchId) {
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
    public void setPaymentCount(KualiInteger paymentCount) {
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

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put(PdpPropertyConstants.BATCH_ID, this.batchId);                  
        m.put(PdpPropertyConstants.PAYMENT_COUNT, this.paymentCount);         
        m.put(PdpPropertyConstants.PAYMENT_TOTAL_AMOUNT, this.paymentTotalAmount);
        m.put(PdpPropertyConstants.BEGIN_DATE, this.beginDate);               
        m.put(PdpPropertyConstants.END_DATE, this.endDate);                 
        m.put(PdpPropertyConstants.CHART_CODE, this.chartCode);             
        m.put(PdpPropertyConstants.ORG_CODE, this.orgCode);               
        m.put(PdpPropertyConstants.SUB_UNIT_CODE, this.subUnitCode);    
        
        return m;
    }
}
