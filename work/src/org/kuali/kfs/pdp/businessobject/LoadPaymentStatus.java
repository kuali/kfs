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
 * Created on Oct 4, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.service.impl.paymentparser.XmlHeader;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.ErrorMap;


/**
 * Holds status information for a payment load.
 */
public class LoadPaymentStatus extends TransientBusinessObjectBase {
    public static enum LoadStatus {
        SUCCESS, FAILURE
    }

    private int detailCount;
    private BigDecimal detailTotal;
    private String chart;
    private String org;
    private String subUnit;
    private Date creationDate;
    private Integer batchId;

    private LoadStatus loadStatus;
    private List<String> warnings;
    private ErrorMap errorMap;

    public LoadPaymentStatus() {
        super();
    }

    public LoadPaymentStatus(List<String> w, int d, BigDecimal dt) {
        warnings = w;
        detailCount = d;
        detailTotal = dt;
    }

    /**
     * Gets the warnings attribute.
     * 
     * @return Returns the warnings.
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Sets the warnings attribute value.
     * 
     * @param warnings The warnings to set.
     */
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    /**
     * Gets the detailCount attribute.
     * 
     * @return Returns the detailCount.
     */
    public int getDetailCount() {
        return detailCount;
    }

    /**
     * Sets the detailCount attribute value.
     * 
     * @param detailCount The detailCount to set.
     */
    public void setDetailCount(int detailCount) {
        this.detailCount = detailCount;
    }

    /**
     * Gets the detailTotal attribute.
     * 
     * @return Returns the detailTotal.
     */
    public BigDecimal getDetailTotal() {
        return detailTotal;
    }

    /**
     * Sets the detailTotal attribute value.
     * 
     * @param detailTotal The detailTotal to set.
     */
    public void setDetailTotal(BigDecimal detailTotal) {
        this.detailTotal = detailTotal;
    }

    /**
     * Gets the batchId attribute.
     * 
     * @return Returns the batchId.
     */
    public Integer getBatchId() {
        return batchId;
    }

    /**
     * Sets the batchId attribute value.
     * 
     * @param batchId The batchId to set.
     */
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    /**
     * Gets the loadStatus attribute.
     * 
     * @return Returns the loadStatus.
     */
    public LoadStatus getLoadStatus() {
        return loadStatus;
    }

    /**
     * Sets the loadStatus attribute value.
     * 
     * @param loadStatus The loadStatus to set.
     */
    public void setLoadStatus(LoadStatus loadStatus) {
        this.loadStatus = loadStatus;
    }

    /**
     * Gets the errorMap attribute.
     * 
     * @return Returns the errorMap.
     */
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    /**
     * Sets the errorMap attribute value.
     * 
     * @param errorMap The errorMap to set.
     */
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart.
     */
    public String getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     * 
     * @param chart The chart to set.
     */
    public void setChart(String chart) {
        this.chart = chart;
    }

    /**
     * Gets the org attribute.
     * 
     * @return Returns the org.
     */
    public String getOrg() {
        return org;
    }

    /**
     * Sets the org attribute value.
     * 
     * @param org The org to set.
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * Gets the subUnit attribute.
     * 
     * @return Returns the subUnit.
     */
    public String getSubUnit() {
        return subUnit;
    }

    /**
     * Sets the subUnit attribute value.
     * 
     * @param subUnit The subUnit to set.
     */
    public void setSubUnit(String subUnit) {
        this.subUnit = subUnit;
    }

    /**
     * Gets the creationDate attribute.
     * 
     * @return Returns the creationDate.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creationDate attribute value.
     * 
     * @param creationDate The creationDate to set.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PdpPropertyConstants.DETAIL_COUNT, this.detailCount);
        m.put(PdpPropertyConstants.BATCH_ID, this.batchId);

        return m;
    }
}
