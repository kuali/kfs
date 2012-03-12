/*
 * Copyright 2008 The Kuali Foundation
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

