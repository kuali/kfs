/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import java.io.ByteArrayOutputStream;
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

import com.lowagie.text.DocumentException;


/**
 * defines methods that help build report data in Budget Construction
 */
public interface BudgetConstructionReportsServiceHelper {

    /**
     * generates the data for an object representing the report data from temporary storage for a specific user
     * 
     * @param clazz
     * @param principalName
     * @param orderList
     * @return
     */
    public Collection getDataForBuildingReports(Class clazz, String principalName, List<String> orderList);

    /**
     * generates the data for an object representing the report data
     * 
     * @param clazz
     * @param searchCriteria
     * @param orderList
     * @return
     */
    public Collection getDataForBuildingReports(Class clazz, Map searchCriteria, List<String> orderList);

    /**
     * generates PFD file containing the errorMessages passed in
     * 
     * @param errorMessages
     * @param baos
     * @throws DocumentException
     */
    public void generatePdf(List<String> errorMessages, ByteArrayOutputStream baos) throws DocumentException;

    /**
     * get an object code
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param financialObjectCode
     * @return
     */
    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    /**
     * get the selected object codes from the list displayed to the user
     * 
     * @param principalName
     * @return
     */
    public String getSelectedObjectCodes(String principalName);

    /**
     * get the selected reason codes from the list displayed to the user
     * 
     * @param principalName
     * @return
     */
    public String getSelectedReasonCodes(String principalName);

    /**
     * get a budget construction administrative post
     * 
     * @param appointmentFundingEntry
     * @return
     */
    public BudgetConstructionAdministrativePost getBudgetConstructionAdministrativePost(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry);

    /**
     * get a budget construction position for an appointment funding
     * 
     * @param universityFiscalYear
     * @param appointmentFundingEntry
     * @return
     */
    public BudgetConstructionPosition getBudgetConstructionPosition(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry);

    /**
     * get a budget construction intended incumbent for an appointment funding
     * 
     * @param appointmentFundingEntry
     * @return
     */
    public BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry);

    /**
     * gets the budget construction appointment fundings for an object dump
     * 
     * @param universityFiscalYear
     * @param budgetConstructionObjectDump
     * @return
     */
    public Collection<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFundingList(Integer universityFiscalYear, BudgetConstructionObjectDump budgetConstructionObjectDump);

    /**
     * gets a budget construction salary ssn for salary funding
     * 
     * @param principalName
     * @param salaryFunding
     * @return
     */
    public BudgetConstructionSalarySocialSecurityNumber getBudgetConstructionSalarySocialSecurityNumber(String principalName, BudgetConstructionSalaryFunding salaryFunding);

    /**
     * gets salary funding from temporary storage for a specific user
     * 
     * @param principalName
     * @param emplid
     * @return
     */
    public Collection<BudgetConstructionSalaryFunding> getSalaryFunding(String principalName, String emplid);
}
