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
