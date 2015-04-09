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
