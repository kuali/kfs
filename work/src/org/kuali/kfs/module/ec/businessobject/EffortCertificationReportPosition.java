package org.kuali.module.effort.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Business Object for the Effort Certification Report Position Table.
 */
public class EffortCertificationReportPosition extends PersistableBusinessObjectBase {
    private Integer universityFiscalYear;
    private String a21LaborReportNumber;
    private String a21LaborReportPositionObjectGroupCode;
    private boolean active;

    /**
     * Default constructor.
     */
    public EffortCertificationReportPosition() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the a21LaborReportNumber attribute.
     * 
     * @return Returns the a21LaborReportNumber
     */
    public String getA21LaborReportNumber() {
        return a21LaborReportNumber;
    }

    /**
     * Sets the a21LaborReportNumber attribute.
     * 
     * @param a21LaborReportNumber The a21LaborReportNumber to set.
     */
    public void setA21LaborReportNumber(String a21LaborReportNumber) {
        this.a21LaborReportNumber = a21LaborReportNumber;
    }


    /**
     * Gets the a21LaborReportPositionObjectGroupCode attribute.
     * 
     * @return Returns the a21LaborReportPositionObjectGroupCode
     */
    public String getA21LaborReportPositionObjectGroupCode() {
        return a21LaborReportPositionObjectGroupCode;
    }

    /**
     * Sets the a21LaborReportPositionObjectGroupCode attribute.
     * 
     * @param a21LaborReportPositionObjectGroupCode The a21LaborReportPositionObjectGroupCode to set.
     */
    public void setA21LaborReportPositionObjectGroupCode(String a21LaborReportPositionObjectGroupCode) {
        this.a21LaborReportPositionObjectGroupCode = a21LaborReportPositionObjectGroupCode;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("a21LaborReportNumber", this.a21LaborReportNumber);
        m.put("a21LaborReportPositionObjectGroupCode", this.a21LaborReportPositionObjectGroupCode);
        return m;
    }
}
