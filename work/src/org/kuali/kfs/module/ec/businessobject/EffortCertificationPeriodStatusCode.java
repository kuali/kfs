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
package org.kuali.module.effort.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

public class EffortCertificationPeriodStatusCode extends PersistableBusinessObjectBase {
    
    private String laborReportPeriodStatusCode;
    private String laborPeriodStatusDescription;    
    private boolean rowActiveIndicator;
    
    /**
     * Constructs a EffortCertificationPeriodStatusCode.java.
     */
    EffortCertificationPeriodStatusCode() {
        
    }
    /**
     * Gets Labor period status description
     * @return laborPeriodStausDescription
     */
    public String getLaborPeriodStatusDescription() {
        return laborPeriodStatusDescription;
    }
    
    /**
     * 
     * Sets labor period status description
     * @param laborPeriodStatusDescription
     */
    public void setLaborPeriodStatusDescription(String laborPeriodStatusDescription) {
        this.laborPeriodStatusDescription = laborPeriodStatusDescription;
    }
    
    /**
     * 
     * Gets the report period status code
     * @return reportPeriodStatusCode
     */
    public String getReportPeriodStatusCode() {
        return laborReportPeriodStatusCode;
    }
    
    /**
     * 
     * Sets the report period status code
     * @param reportPeriodStatusCode
     */
    public void setReportPeriodStatusCode(String reportPeriodStatusCode) {
        this.laborReportPeriodStatusCode = reportPeriodStatusCode;
    }
    
    /**
     * Gets the rowActiveIndicator attribute.
     * 
     * @return Returns the rowActiveIndicator
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }
    
    /**
     * Sets the rowActiveIndicator attribute.
     * 
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("reportPeriodStatusCode", this.laborReportPeriodStatusCode);
        return m;
    }
    
    

}
