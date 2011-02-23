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

import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;

public class AssetStatementReportDataHolder {

    // header
    private String institution;
    private String monthEndDate;
    private String endingDate;
    private String kemid;
    private String kemidLongTitle;
    
    // body
    private SecurityReportingGroup securityReportingGroup;
    private Security security;
    private HoldingHistory holdingHistory;
    
    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    public String getMonthEndDate() {
        return monthEndDate;
    }
    public void setMonthEndDate(String monthEndDate) {
        this.monthEndDate = monthEndDate;
    }
    public String getEndingDate() {
        return endingDate;
    }
    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }
    public String getKemid() {
        return kemid;
    }
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }
    public String getKemidLongTitle() {
        return kemidLongTitle;
    }
    public void setKemidLongTitle(String kemidLongTitle) {
        this.kemidLongTitle = kemidLongTitle;
    }
    public SecurityReportingGroup getSecurityReportingGroup() {
        return securityReportingGroup;
    }
    public void setSecurityReportingGroup(SecurityReportingGroup securityReportingGroup) {
        this.securityReportingGroup = securityReportingGroup;
    }
    public Security getSecurity() {
        return security;
    }
    public void setSecurity(Security security) {
        this.security = security;
    }
    public HoldingHistory getHoldingHistory() {
        return holdingHistory;
    }
    public void setHoldingHistory(HoldingHistory holdingHistory) {
        this.holdingHistory = holdingHistory;
    }
    
}
