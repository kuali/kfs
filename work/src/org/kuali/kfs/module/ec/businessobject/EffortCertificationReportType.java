/*
 * Copyright 2006-2007 The Kuali Foundation.
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
 * Business Object for the Effort Certification Report Type
 */
public class EffortCertificationReportType extends PersistableBusinessObjectBase {

    private String effortCertificationLaborReportTypeCode;
    private String effortCertificationLaborReportDescription;
    private boolean rowActiveIndicator;

    /**
     * Default constructor.
     */
    public EffortCertificationReportType() {

    }

    /**
     * Gets the a21LaborReportTypeCode attribute.
     * 
     * @return Returns the a21LaborReportTypeCode
     */
    public String getEffortCertificationLaborReportTypeCode() {
        return effortCertificationLaborReportTypeCode;
    }

    /**
     * Sets the a21LaborReportTypeCode attribute.
     * 
     * @param a21LaborReportTypeCode The a21LaborReportTypeCode to set.
     */
    public void setEffortCertificationLaborReportTypeCode(String a21LaborReportTypeCode) {
        this.effortCertificationLaborReportTypeCode = a21LaborReportTypeCode;
    }


    /**
     * Gets the a21LaborReportDescription attribute.
     * 
     * @return Returns the a21LaborReportDescription
     */
    public String getEffortCertificationLaborReportDescription() {
        return effortCertificationLaborReportDescription;
    }

    /**
     * Sets the a21LaborReportDescription attribute.
     * 
     * @param a21LaborReportDescription The a21LaborReportDescription to set.
     */
    public void setEffortCertificationLaborReportDescription(String a21LaborReportDescription) {
        this.effortCertificationLaborReportDescription = a21LaborReportDescription;
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
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("effortCertificationLaborReportTypeCode", this.effortCertificationLaborReportTypeCode);
        return m;
    }
}
