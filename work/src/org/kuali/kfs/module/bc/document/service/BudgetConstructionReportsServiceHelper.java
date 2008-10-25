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
package org.kuali.kfs.module.bc.document.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectDump;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryFunding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;


public interface BudgetConstructionReportsServiceHelper {

    public Collection getDataForBuildingReports(Class clazz, String principalName, List<String> orderList);
    
    public Collection getDataForBuildingReports(Class clazz, Map searchCriteria, List<String> orderList);
    
    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);
        
    public String getSelectedObjectCodes (String principalName);
        
    public BudgetConstructionAdministrativePost getBudgetConstructionAdministrativePost(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry); 

    public BudgetConstructionPosition getBudgetConstructionPosition(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry);
    
    public BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry);
        
    public Collection<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFundingList(Integer universityFiscalYear, BudgetConstructionObjectDump budgetConstructionObjectDump);
    
    public BudgetConstructionSalarySocialSecurityNumber getBudgetConstructionSalarySocialSecurityNumber(String principalName, BudgetConstructionSalaryFunding salaryFunding);
    
    public Collection<BudgetConstructionSalaryFunding> getSalaryFunding(String principalName, String emplid);
}

