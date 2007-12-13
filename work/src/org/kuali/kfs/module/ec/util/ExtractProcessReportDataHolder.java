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
package org.kuali.module.effort.util;

import java.util.HashMap;
import java.util.Map;

import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.labor.bo.LedgerBalance;

/**
 * This class groups and holds the data presented to working reports of extract process 
 */
public class ExtractProcessReportDataHolder {
    private EffortCertificationReportDefinition reportDefinition;    
    private Map<String, Integer> basicStatistics;
    private Map<LedgerBalance, String> errorMap;
    
    /**
     * Constructs a ExtractProcessReportDataHolder.java.
     */
    public ExtractProcessReportDataHolder() {
        super();
        basicStatistics = new HashMap<String, Integer>();
        errorMap = new HashMap<LedgerBalance, String>();
    }
    
    public void updateBasicStatistics(String key, Integer count) {
        if(basicStatistics.containsKey(key)) {
            Integer currentCount = basicStatistics.get(key);
            count = currentCount + count;
        }
        basicStatistics.put(key, count);
    }

    /**
     * Gets the reportDefinition attribute. 
     * @return Returns the reportDefinition.
     */
    public EffortCertificationReportDefinition getReportDefinition() {
        return reportDefinition;
    }

    /**
     * Sets the reportDefinition attribute value.
     * @param reportDefinition The reportDefinition to set.
     */
    public void setReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        this.reportDefinition = reportDefinition;
    }

    /**
     * Gets the basicStatistics attribute. 
     * @return Returns the basicStatistics.
     */
    public Map<String, Integer> getBasicStatistics() {
        return basicStatistics;
    }

    /**
     * Sets the basicStatistics attribute value.
     * @param basicStatistics The basicStatistics to set.
     */
    public void setBasicStatistics(Map<String, Integer> basicStatistics) {
        this.basicStatistics = basicStatistics;
    }

    /**
     * Gets the errorMap attribute. 
     * @return Returns the errorMap.
     */
    public Map<LedgerBalance, String> getErrorMap() {
        return errorMap;
    }

    /**
     * Sets the errorMap attribute value.
     * @param errorMap The errorMap to set.
     */
    public void setErrorMap(Map<LedgerBalance, String> errorMap) {
        this.errorMap = errorMap;
    }  
}
