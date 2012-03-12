/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for the Effort Certification Report Earn Paygroup Table.
 */
public class EffortCertificationReportEarnPaygroup extends PersistableBusinessObjectBase implements MutableInactivatable, FiscalYearBasedBusinessObject {
    private Integer universityFiscalYear;
    private String effortCertificationReportTypeCode;
    private String earnCode;
    private String payGroup;
    private boolean active;

    private EffortCertificationReportType effortCertificationReportType;
    private SystemOptions options;
    
    /**
     * Constructs a EffortCertificationReportEarnPaygroup.java.
     */
    public EffortCertificationReportEarnPaygroup() {
        super();
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the effortCertificationReportTypeCode attribute.
     * 
     * @return Returns the effortCertificationReportTypeCode.
     */
    public String getEffortCertificationReportTypeCode() {
        return effortCertificationReportTypeCode;
    }

    /**
     * Sets the effortCertificationReportTypeCode attribute value.
     * 
     * @param effortCertificationReportTypeCode The effortCertificationReportTypeCode to set.
     */
    public void setEffortCertificationReportTypeCode(String effortCertificationReportTypeCode) {
        this.effortCertificationReportTypeCode = effortCertificationReportTypeCode;
    }

    /**
     * Gets the earnCode attribute.
     * 
     * @return Returns the earnCode.
     */
    public String getEarnCode() {
        return earnCode;
    }

    /**
     * Sets the earnCode attribute value.
     * 
     * @param earnCode The earnCode to set.
     */
    public void setEarnCode(String earnCode) {
        this.earnCode = earnCode;
    }

    /**
     * Gets the payGroup attribute.
     * 
     * @return Returns the payGroup.
     */
    public String getPayGroup() {
        return payGroup;
    }

    /**
     * Sets the payGroup attribute value.
     * 
     * @param payGroup The payGroup to set.
     */
    public void setPayGroup(String payGroup) {
        this.payGroup = payGroup;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the effortCertificationReportType attribute.
     * 
     * @return Returns the effortCertificationReportType.
     */
    public EffortCertificationReportType getEffortCertificationReportType() {
        return effortCertificationReportType;
    }

    /**
     * Sets the effortCertificationReportType attribute value.
     * 
     * @param effortCertificationReportType The effortCertificationReportType to set.
     */
    @Deprecated
    public void setEffortCertificationReportType(EffortCertificationReportType effortCertificationReportType) {
        this.effortCertificationReportType = effortCertificationReportType;
    }

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    @Deprecated
    public void setOptions(SystemOptions options) {
        this.options = options;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("effortCertificationReportTypeCode", this.effortCertificationReportTypeCode);
        m.put("earnCode", this.earnCode);
        m.put("payGroup", this.payGroup);
        return m;
    }
}
