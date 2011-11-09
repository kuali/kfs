/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Oct 4, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.util.MessageMap;


/**
 * Holds status information for a payment load.
 */
public class LoadPaymentStatus extends TransientBusinessObjectBase {
    public static enum LoadStatus {
        SUCCESS, FAILURE
    }

    private int detailCount;
    private KualiDecimal detailTotal;
    private String chart;
    private String unit;
    private String subUnit;
    private Date creationDate;
    private KualiInteger batchId;

    private LoadStatus loadStatus;
    private List<String> warnings;
    private MessageMap errorMap;

    public LoadPaymentStatus() {
        super();
    }

    public LoadPaymentStatus(List<String> w, int d, KualiDecimal dt) {
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
    public KualiDecimal getDetailTotal() {
        return detailTotal;
    }

    /**
     * Sets the detailTotal attribute value.
     * 
     * @param detailTotal The detailTotal to set.
     */
    public void setDetailTotal(KualiDecimal detailTotal) {
        this.detailTotal = detailTotal;
    }

    /**
     * Gets the batchId attribute.
     * 
     * @return Returns the batchId.
     */
    public KualiInteger getBatchId() {
        return batchId;
    }

    /**
     * Sets the batchId attribute value.
     * 
     * @param batchId The batchId to set.
     */
    public void setBatchId(KualiInteger batchId) {
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
    public MessageMap getMessageMap() {
        return errorMap;
    }

    /**
     * Sets the errorMap attribute value.
     * 
     * @param errorMap The errorMap to set.
     */
    public void setMessageMap(MessageMap errorMap) {
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
     * Gets the unit attribute.
     * 
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit attribute value.
     * 
     * @param unit The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
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

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PdpPropertyConstants.DETAIL_COUNT, this.detailCount);
        m.put(PdpPropertyConstants.BATCH_ID, this.batchId);

        return m;
    }
}
