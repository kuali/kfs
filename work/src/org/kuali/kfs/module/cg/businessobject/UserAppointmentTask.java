/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

package org.kuali.module.kra.budget.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiInteger;

/**
 * 
 */
public class UserAppointmentTask extends BusinessObjectBase implements Comparable {

    private String documentNumber;
    private Integer budgetTaskSequenceNumber;
    private Integer budgetUserSequenceNumber;
    private String institutionAppointmentTypeCode;

    private KualiInteger agencyFringeBenefitTotalAmountTask = new KualiInteger(0); 
    private KualiInteger agencyRequestTotalAmountTask = new KualiInteger(0); 
    private KualiInteger institutionCostShareFringeBenefitTotalAmountTask = new KualiInteger(0);
    private KualiInteger institutionCostShareRequestTotalAmountTask = new KualiInteger(0);
    
    private KualiInteger gradAsstAgencySalaryTotal = new KualiInteger(0);
    private KualiInteger gradAsstAgencyHealthInsuranceTotal = new KualiInteger(0);
    private KualiInteger gradAsstInstSalaryTotal = new KualiInteger(0);
    private KualiInteger gradAsstInstHealthInsuranceTotal = new KualiInteger(0);

    private List userAppointmentTaskPeriods = new ArrayList();

    private BudgetTask task;
    private BudgetFringeRate budgetFringeRate;

    private boolean secondaryAppointment;

    /**
     * Default no-arg constructor.
     */
    public UserAppointmentTask() {
        super();
    }

    public UserAppointmentTask(UserAppointmentTask userAppointmentTask) {
        super();
        this.documentNumber = userAppointmentTask.getDocumentNumber();
        this.budgetTaskSequenceNumber = userAppointmentTask.getBudgetTaskSequenceNumber();
        this.budgetUserSequenceNumber = userAppointmentTask.getBudgetUserSequenceNumber();
        this.institutionAppointmentTypeCode = userAppointmentTask.getInstitutionAppointmentTypeCode();

        this.userAppointmentTaskPeriods = new ArrayList(userAppointmentTask.getUserAppointmentTaskPeriods() != null ? userAppointmentTask.getUserAppointmentTaskPeriods() : null);
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the budgetTaskSequenceNumber attribute.
     * 
     * @return Returns the budgetTaskSequenceNumber
     * 
     */
    public Integer getBudgetTaskSequenceNumber() {
        return budgetTaskSequenceNumber;
    }

    /**
     * Sets the budgetTaskSequenceNumber attribute.
     * 
     * @param budgetTaskSequenceNumber The budgetTaskSequenceNumber to set.
     * 
     */
    public void setBudgetTaskSequenceNumber(Integer budgetTaskSequenceNumber) {
        this.budgetTaskSequenceNumber = budgetTaskSequenceNumber;
    }

    /**
     * Gets the budgetUserSequenceNumber attribute.
     * 
     * @return Returns the budgetUserSequenceNumber
     * 
     */
    public Integer getBudgetUserSequenceNumber() {
        return budgetUserSequenceNumber;
    }

    /**
     * Sets the budgetUserSequenceNumber attribute.
     * 
     * @param budgetUserSequenceNumber The budgetUserSequenceNumber to set.
     * 
     */
    public void setBudgetUserSequenceNumber(Integer budgetUserSequenceNumber) {
        this.budgetUserSequenceNumber = budgetUserSequenceNumber;
    }

    /**
     * Sets the institutionAppointmentTypeCode attribute.
     * 
     * @param institutionAppointmentTypeCode The institutionAppointmentTypeCode to set.
     * 
     */
    public String getInstitutionAppointmentTypeCode() {
        return institutionAppointmentTypeCode;
    }

    /**
     * Gets the institutionAppointmentTypeCode attribute.
     * 
     * @return Returns the institutionAppointmentTypeCode
     * 
     */
    public void setInstitutionAppointmentTypeCode(String institutionAppointmentTypeCode) {
        this.institutionAppointmentTypeCode = institutionAppointmentTypeCode;
    }

    /**
     * Gets the institutionAppointmentType attribute.
     * 
     * @return Returns the institutionAppointmentType
     * 
     */
    public BudgetFringeRate getBudgetFringeRate() {
        return budgetFringeRate;
    }

    /**
     * Sets the institutionAppointmentType attribute.
     * 
     * @param institutionAppointmentType The institutionAppointmentType to set.
     * 
     */
    public void setBudgetFringeRate(BudgetFringeRate budgetFringeRate) {
        this.budgetFringeRate = budgetFringeRate;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        // m.put("<unique identifier 1>", this.<UniqueIdentifier1>());
        // m.put("<unique identifier 2>", this.<UniqueIdentifier2>());

        return m;
    }

    /**
     * Gets the userAppointmentTaskPeriods attribute.
     * 
     * @return Returns the userAppointmentTaskPeriods.
     */
    public List<UserAppointmentTaskPeriod> getUserAppointmentTaskPeriods() {
        return userAppointmentTaskPeriods;
    }


    public UserAppointmentTaskPeriod getUserAppointmentTaskPeriod(int index) {
        while (getUserAppointmentTaskPeriods().size() <= index) {
            getUserAppointmentTaskPeriods().add(new UserAppointmentTaskPeriod());
        }
        return (UserAppointmentTaskPeriod) (getUserAppointmentTaskPeriods().get(index));
    }

    /**
     * Sets the userAppointmentTaskPeriods attribute value.
     * 
     * @param userAppointmentTaskPeriods The userAppointmentTaskPeriods to set.
     */
    public void setUserAppointmentTaskPeriods(List userAppointmentTaskPeriods) {
        this.userAppointmentTaskPeriods = userAppointmentTaskPeriods;
    }

    /**
     * Gets the task attribute.
     * 
     * @return Returns the task.
     */
    public BudgetTask getTask() {
        return task;
    }

    /**
     * Sets the task attribute value.
     * 
     * @param task The task to set.
     */
    public void setTask(BudgetTask task) {
        this.task = task;
    }

    /**
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeInsert(persistenceBroker);
        this.refreshReferenceObject("budgetFringeRate");
    }

    /**
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeUpdate(persistenceBroker);
        this.refreshReferenceObject("budgetFringeRate");
    }

    /**
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeDelete(persistenceBroker);
        this.refreshReferenceObject("userAppointmentTaskPeriods");
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return this.getTask().compareTo(((UserAppointmentTask) o).getTask());
    }

    /**
     * Gets the agencyFringeBenefitTotalAmountTask attribute.
     * 
     * @return Returns the agencyFringeBenefitTotalAmountTask.
     */
    public KualiInteger getAgencyFringeBenefitTotalAmountTask() {
        return agencyFringeBenefitTotalAmountTask;
    }

    /**
     * Sets the agencyFringeBenefitTotalAmountTask attribute value.
     * 
     * @param agencyFringeBenefitTotalAmountTask The agencyFringeBenefitTotalAmountTask to set.
     */
    public void setAgencyFringeBenefitTotalAmountTask(KualiInteger agencyFringeBenefitTotalAmountTask) {
        this.agencyFringeBenefitTotalAmountTask = agencyFringeBenefitTotalAmountTask;
    }

    /**
     * Gets the agencyRequestTotalAmountTask attribute.
     * 
     * @return Returns the agencyRequestTotalAmountTask.
     */
    public KualiInteger getAgencyRequestTotalAmountTask() {
        return agencyRequestTotalAmountTask;
    }

    /**
     * Sets the agencyRequestTotalAmountTask attribute value.
     * 
     * @param agencyRequestTotalAmountTask The agencyRequestTotalAmountTask to set.
     */
    public void setAgencyRequestTotalAmountTask(KualiInteger agencyRequestTotalAmountTask) {
        this.agencyRequestTotalAmountTask = agencyRequestTotalAmountTask;
    }

    /**
     * Gets the institutionCostShareFringeBenefitTotalAmountTask attribute.
     * 
     * @return Returns the institutionCostShareFringeBenefitTotalAmountTask.
     */
    public KualiInteger getInstitutionCostShareFringeBenefitTotalAmountTask() {
        return institutionCostShareFringeBenefitTotalAmountTask;
    }

    /**
     * Sets the institutionCostShareFringeBenefitTotalAmountTask attribute value.
     * 
     * @param institutionCostShareFringeBenefitTotalAmountTask The institutionCostShareFringeBenefitTotalAmountTask to set.
     */
    public void setInstitutionCostShareFringeBenefitTotalAmountTask(KualiInteger institutionCostShareFringeBenefitTotalAmountTask) {
        this.institutionCostShareFringeBenefitTotalAmountTask = institutionCostShareFringeBenefitTotalAmountTask;
    }

    /**
     * Gets the institutionCostShareRequestTotalAmountTask attribute.
     * 
     * @return Returns the institutionCostShareRequestTotalAmountTask.
     */
    public KualiInteger getInstitutionCostShareRequestTotalAmountTask() {
        return institutionCostShareRequestTotalAmountTask;
    }

    /**
     * Sets the institutionCostShareRequestTotalAmountTask attribute value.
     * 
     * @param institutionCostShareRequestTotalAmountTask The institutionCostShareRequestTotalAmountTask to set.
     */
    public void setInstitutionCostShareRequestTotalAmountTask(KualiInteger institutionCostShareRequestTotalAmountTask) {
        this.institutionCostShareRequestTotalAmountTask = institutionCostShareRequestTotalAmountTask;
    }

    /**
     * Gets the gradAsstAgencyHealthInsurance attribute.
     * 
     * @return Returns the gradAsstAgencyHealthInsurance.
     */
    public KualiInteger getGradAsstAgencyHealthInsuranceTotal() {
        return gradAsstAgencyHealthInsuranceTotal;
    }

    /**
     * Sets the gradAsstAgencyHealthInsurance attribute value.
     * 
     * @param gradAsstAgencyHealthInsurance The gradAsstAgencyHealthInsurance to set.
     */
    public void setGradAsstAgencyHealthInsuranceTotal(KualiInteger gradAsstAgencyHealthInsuranceTotal) {
        this.gradAsstAgencyHealthInsuranceTotal = gradAsstAgencyHealthInsuranceTotal;
    }

    /**
     * Gets the gradAsstAgencySalary attribute.
     * 
     * @return Returns the gradAsstAgencySalary.
     */
    public KualiInteger getGradAsstAgencySalaryTotal() {
        return gradAsstAgencySalaryTotal;
    }

    /**
     * Sets the gradAsstAgencySalary attribute value.
     * 
     * @param gradAsstAgencySalary The gradAsstAgencySalary to set.
     */
    public void setGradAsstAgencySalaryTotal(KualiInteger gradAsstAgencySalaryTotal) {
        this.gradAsstAgencySalaryTotal = gradAsstAgencySalaryTotal;
    }

    /**
     * Gets the gradAsstUnivHealthInsurance attribute.
     * 
     * @return Returns the gradAsstUnivHealthInsurance.
     */
    public KualiInteger getGradAsstInstHealthInsuranceTotal() {
        return gradAsstInstHealthInsuranceTotal;
    }

    /**
     * Sets the gradAsstUnivHealthInsurance attribute value.
     * 
     * @param gradAsstUnivHealthInsurance The gradAsstUnivHealthInsurance to set.
     */
    public void setGradAsstInstHealthInsuranceTotal(KualiInteger gradAsstInstHealthInsuranceTotal) {
        this.gradAsstInstHealthInsuranceTotal = gradAsstInstHealthInsuranceTotal;
    }

    /**
     * Gets the gradAsstUnivSalary attribute.
     * 
     * @return Returns the gradAsstUnivSalary.
     */
    public KualiInteger getGradAsstInstSalaryTotal() {
        return gradAsstInstSalaryTotal;
    }

    /**
     * Sets the gradAsstUnivSalary attribute value.
     * 
     * @param gradAsstUnivSalary The gradAsstUnivSalary to set.
     */
    public void setGradAsstInstSalaryTotal(KualiInteger gradAsstInstSalaryTotal) {
        this.gradAsstInstSalaryTotal = gradAsstInstSalaryTotal;
    }

    public boolean isSecondaryAppointment() {
        return secondaryAppointment;
    }

    public void setSecondaryAppointment(boolean isSecondaryAppointment) {
        this.secondaryAppointment = isSecondaryAppointment;
    }
}
