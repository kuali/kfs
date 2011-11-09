/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.util;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Used to hold the KEMIDs with multiple BenefittingOrganizations data for report
 */
public class KemidsWithMultipleBenefittingOrganizationsDataHolder {

    private String kemid;
    private String campus;
    private String chart;
    private String organization;
    private KualiDecimal percent;
    
    public String getKemid() {
        return kemid;
    }
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }
    public String getCampus() {
        return campus;
    }
    public void setCampus(String campus) {
        this.campus = campus;
    }
    public String getChart() {
        return chart;
    }
    public void setChart(String chart) {
        this.chart = chart;
    }
    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public KualiDecimal getPercent() {
        return percent;
    }
    public void setPercent(KualiDecimal percent) {
        this.percent = percent;
    }
    
    
}
