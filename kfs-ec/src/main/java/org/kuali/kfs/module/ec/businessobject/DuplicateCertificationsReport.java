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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class DuplicateCertificationsReport extends TransientBusinessObjectBase {

    private Integer universityFiscalYear;
    private String effortCertificationReportNumber;
    private String emplid;
    
    private Person employee;
    private SystemOptions options;
    private EffortCertificationReportDefinition effortCertificationReportDefinition;
    
    /**
     * Gets effortCertificationReportNumber
     * 
     * @return
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }
    
    /**
     * Sets effortCertificationReportNumber
     * 
     * @param effortCertificationReportNumber
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }
    
    /**
     * Gets universityFiscalYear
     * 
     * @return
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }
    
    /**
     * Sets universityFiscalYear
     * 
     * @param universityFiscalYear
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
    
    /**
     * Returns the employee's id
     * 
     * @return
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * sets emplid
     * 
     * @param emplid
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.universityFiscalYear);
        m.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, this.effortCertificationReportNumber);
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.emplid);
        
        return m;
    }
    
    /**
     * Gets the employee attribute.
     * 
     * @return Returns the employee.
     */
    public Person getEmployee() {
        Map<String, String> searchCriteria = new HashMap<String, String>();
        searchCriteria.put(KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER, getEmplid());

        return new ArrayList<Person>(SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).findPeople(searchCriteria)).get(0);
    }

    /**
     * Sets the employee attribute value.
     * 
     * @param employee The employee to set.
     */
    public void setEmployee(Person employee) {
        this.employee = employee;
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
    public void setOptions(SystemOptions options) {
        this.options = options;
    }
    
    /**
     * Gets the effort certification report definition
     * 
     * @return
     */
    public EffortCertificationReportDefinition getEffortCertificationReportDefinition() {
        return effortCertificationReportDefinition;
    }
    
    /**
     * Sets effort certification report definition
     * 
     * @param effortCertificationReportDefinition
     */
    public void setEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        this.effortCertificationReportDefinition = effortCertificationReportDefinition;
    }
}

