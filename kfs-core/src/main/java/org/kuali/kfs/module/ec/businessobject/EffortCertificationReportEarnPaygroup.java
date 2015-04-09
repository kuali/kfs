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
