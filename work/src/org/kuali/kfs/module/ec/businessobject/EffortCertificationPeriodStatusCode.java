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

/**
 * Business Object for the Effort Certification Report Status Code Table.
 */
public class EffortCertificationPeriodStatusCode extends PersistableBusinessObjectBase {
    private String effortCertificationReportPeriodStatusCode;
    private String effortCertificationReportPeriodStatusDescription;

    /**
     * Constructs a EffortCertificationPeriodStatusCode.java.
     */
    public EffortCertificationPeriodStatusCode() {

    }

    /**
     * Gets the effortCertificationReportPeriodStatusCode attribute.
     * 
     * @return Returns the effortCertificationReportPeriodStatusCode.
     */
    public String getEffortCertificationReportPeriodStatusCode() {
        return effortCertificationReportPeriodStatusCode;
    }

    /**
     * Sets the effortCertificationReportPeriodStatusCode attribute value.
     * 
     * @param effortCertificationReportPeriodStatusCode The effortCertificationReportPeriodStatusCode to set.
     */
    public void setEffortCertificationReportPeriodStatusCode(String effortCertificationReportPeriodStatusCode) {
        this.effortCertificationReportPeriodStatusCode = effortCertificationReportPeriodStatusCode;
    }

    /**
     * Gets the effortCertificationReportPeriodStatusDescription attribute.
     * 
     * @return Returns the effortCertificationReportPeriodStatusDescription.
     */
    public String getEffortCertificationReportPeriodStatusDescription() {
        return effortCertificationReportPeriodStatusDescription;
    }

    /**
     * Sets the effortCertificationReportPeriodStatusDescription attribute value.
     * 
     * @param effortCertificationReportPeriodStatusDescription The effortCertificationReportPeriodStatusDescription to set.
     */
    public void setEffortCertificationReportPeriodStatusDescription(String effortCertificationReportPeriodStatusDescription) {
        this.effortCertificationReportPeriodStatusDescription = effortCertificationReportPeriodStatusDescription;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("effortCertificationReportPeriodStatusCode", this.effortCertificationReportPeriodStatusCode);
        return m;
    }


}
