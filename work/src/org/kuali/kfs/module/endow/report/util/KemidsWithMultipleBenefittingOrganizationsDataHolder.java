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
