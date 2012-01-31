/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.integration.cg.dto;

import java.io.Serializable;

/**
 * Integration class for AgencyParametersDTO
 */
public class AgencyParametersDTO implements Serializable {

    private static final long serialVersionUID = 8417796622708399543L;

    private String principalId;
    private String agencyNumber;
    private String reportingName;
    private String fullName;
    private String agencyTypeCode;
    private boolean active;
    private boolean inState;

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getReportingName() {
        return reportingName;
    }

    public void setReportingName(String reportingName) {
        this.reportingName = reportingName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAgencyTypeCode() {
        return agencyTypeCode;
    }

    public void setAgencyTypeCode(String agencyTypeCode) {
        this.agencyTypeCode = agencyTypeCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isInState() {
        return inState;
    }

    public void setInState(boolean inState) {
        this.inState = inState;
    }


}
