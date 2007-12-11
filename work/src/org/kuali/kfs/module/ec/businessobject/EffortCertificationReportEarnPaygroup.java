package org.kuali.module.effort.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Options;

/**
 * Business Object for the Effort Certification Report Earn Paygroup Table.
 */
public class EffortCertificationReportEarnPaygroup extends PersistableBusinessObjectBase {
    private Integer universityFiscalYear;
    private String effortCertificationReportTypeCode;
    private String earnCode;
    private String payGroup;
    private boolean active;

    private EffortCertificationReportType effortCertificationReportType;
    private Options options;
    
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
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    @Deprecated
    public void setOptions(Options options) {
        this.options = options;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
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
