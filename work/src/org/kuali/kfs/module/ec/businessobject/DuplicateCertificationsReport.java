/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.core.bo.TransientBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortPropertyConstants;

public class DuplicateCertificationsReport extends TransientBusinessObjectBase {

    private String universityFiscalYear;
    private String effortCertificationReportNumber;
    private String emplid;
    
    private UniversalUser employee;
    
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
    public String getUniversityFiscalYear() {
        return universityFiscalYear;
    }
    
    /**
     * Sets universityFiscalYear
     * 
     * @param universityFiscalYear
     */
    public void setUniversityFiscalYear(String universityFiscalYear) {
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

    @Override
    protected LinkedHashMap toStringMapper() {
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
    public UniversalUser getEmployee() {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER, getEmplid());

        return new ArrayList<UniversalUser>(SpringContext.getBean(UniversalUserService.class).findUniversalUsers(searchCriteria)).get(0);
    }

    /**
     * Sets the employee attribute value.
     * 
     * @param employee The employee to set.
     */
    public void setEmployee(UniversalUser employee) {
        this.employee = employee;
    }
}
