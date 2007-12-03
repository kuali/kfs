package org.kuali.module.effort.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Business Object for the Effort Certification Report Earn Paygroup Table.
 */
public class EffortCertificationReportEarnPaygroup extends PersistableBusinessObjectBase {
	private Integer universityFiscalYear;
	private String a21LaborReportTypeCode;
	private String earnCode;
	private String payGroup;
	private boolean active;

    private EffortCertificationReportType a21LaborReportType;
    
	/**
	 * Default constructor.
	 */
	public EffortCertificationReportEarnPaygroup() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the a21LaborReportTypeCode attribute.
	 * 
	 * @return Returns the a21LaborReportTypeCode
	 * 
	 */
	public String getA21LaborReportTypeCode() { 
		return a21LaborReportTypeCode;
	}

	/**
	 * Sets the a21LaborReportTypeCode attribute.
	 * 
	 * @param a21LaborReportTypeCode The a21LaborReportTypeCode to set.
	 * 
	 */
	public void setA21LaborReportTypeCode(String a21LaborReportTypeCode) {
		this.a21LaborReportTypeCode = a21LaborReportTypeCode;
	}


	/**
	 * Gets the earnCode attribute.
	 * 
	 * @return Returns the earnCode
	 * 
	 */
	public String getEarnCode() { 
		return earnCode;
	}

	/**
	 * Sets the earnCode attribute.
	 * 
	 * @param earnCode The earnCode to set.
	 * 
	 */
	public void setEarnCode(String earnCode) {
		this.earnCode = earnCode;
	}


	/**
	 * Gets the payGroup attribute.
	 * 
	 * @return Returns the payGroup
	 * 
	 */
	public String getPayGroup() { 
		return payGroup;
	}

	/**
	 * Sets the payGroup attribute.
	 * 
	 * @param payGroup The payGroup to set.
	 * 
	 */
	public void setPayGroup(String payGroup) {
		this.payGroup = payGroup;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
     * Gets the a21LaborReportType attribute. 
     * @return Returns the a21LaborReportType.
     */
    public EffortCertificationReportType getA21LaborReportType() {
        return a21LaborReportType;
    }

    /**
     * Sets the a21LaborReportType attribute value.
     * @param a21LaborReportType The a21LaborReportType to set.
     * @deprecated
     */
    public void setA21LaborReportType(EffortCertificationReportType a21LaborReportType) {
        this.a21LaborReportType = a21LaborReportType;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("a21LaborReportTypeCode", this.a21LaborReportTypeCode);
        m.put("earnCode", this.earnCode);
        m.put("payGroup", this.payGroup);
	    return m;
    }
}
