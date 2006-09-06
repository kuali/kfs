/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class UserAppointmentTask extends BusinessObjectBase implements Comparable {

    private String documentHeaderId;
    private Integer budgetTaskSequenceNumber;
    private Integer budgetUserSequenceNumber;
    private String universityAppointmentTypeCode;

    private KualiInteger agencyFringeBenefitTotalAmountTask = new KualiInteger(0); 
    private KualiInteger agencyRequestTotalAmountTask = new KualiInteger(0); 
    private KualiInteger universityCostShareFringeBenefitTotalAmountTask = new KualiInteger(0);
    private KualiInteger universityCostShareRequestTotalAmountTask = new KualiInteger(0);
    
    private KualiInteger gradAsstAgencySalaryTotal = new KualiInteger(0);
    private KualiInteger gradAsstAgencyHealthInsuranceTotal = new KualiInteger(0);
    private KualiInteger gradAsstUnivSalaryTotal = new KualiInteger(0);
    private KualiInteger gradAsstUnivHealthInsuranceTotal = new KualiInteger(0);

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
        this.documentHeaderId = userAppointmentTask.getDocumentHeaderId();
        this.budgetTaskSequenceNumber = userAppointmentTask.getBudgetTaskSequenceNumber();
        this.budgetUserSequenceNumber = userAppointmentTask.getBudgetUserSequenceNumber();
        this.universityAppointmentTypeCode = userAppointmentTask.getUniversityAppointmentTypeCode();

        this.userAppointmentTaskPeriods = new ArrayList(userAppointmentTask.getUserAppointmentTaskPeriods() != null ? userAppointmentTask.getUserAppointmentTaskPeriods() : null);
    }


    /**
     * Gets the documentHeaderId attribute.
     * 
     * @return - Returns the documentHeaderId
     * 
     */
    public String getDocumentHeaderId() {
        return documentHeaderId;
    }

    /**
     * Sets the documentHeaderId attribute.
     * 
     * @param documentHeaderId The documentHeaderId to set.
     * 
     */
    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }

    /**
     * Gets the budgetTaskSequenceNumber attribute.
     * 
     * @return - Returns the budgetTaskSequenceNumber
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
     * @return - Returns the budgetUserSequenceNumber
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
     * Sets the universityAppointmentTypeCode attribute.
     * 
     * @param universityAppointmentTypeCode The universityAppointmentTypeCode to set.
     * 
     */
    public String getUniversityAppointmentTypeCode() {
        return universityAppointmentTypeCode;
    }

    /**
     * Gets the universityAppointmentTypeCode attribute.
     * 
     * @return - Returns the universityAppointmentTypeCode
     * 
     */
    public void setUniversityAppointmentTypeCode(String universityAppointmentTypeCode) {
        this.universityAppointmentTypeCode = universityAppointmentTypeCode;
    }

    /**
     * Gets the universityAppointmentType attribute.
     * 
     * @return - Returns the universityAppointmentType
     * 
     */
    public BudgetFringeRate getBudgetFringeRate() {
        return budgetFringeRate;
    }

    /**
     * Sets the universityAppointmentType attribute.
     * 
     * @param universityAppointmentType The universityAppointmentType to set.
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
     * Gets the universityCostShareFringeBenefitTotalAmountTask attribute.
     * 
     * @return Returns the universityCostShareFringeBenefitTotalAmountTask.
     */
    public KualiInteger getUniversityCostShareFringeBenefitTotalAmountTask() {
        return universityCostShareFringeBenefitTotalAmountTask;
    }

    /**
     * Sets the universityCostShareFringeBenefitTotalAmountTask attribute value.
     * 
     * @param universityCostShareFringeBenefitTotalAmountTask The universityCostShareFringeBenefitTotalAmountTask to set.
     */
    public void setUniversityCostShareFringeBenefitTotalAmountTask(KualiInteger universityCostShareFringeBenefitTotalAmountTask) {
        this.universityCostShareFringeBenefitTotalAmountTask = universityCostShareFringeBenefitTotalAmountTask;
    }

    /**
     * Gets the universityCostShareRequestTotalAmountTask attribute.
     * 
     * @return Returns the universityCostShareRequestTotalAmountTask.
     */
    public KualiInteger getUniversityCostShareRequestTotalAmountTask() {
        return universityCostShareRequestTotalAmountTask;
    }

    /**
     * Sets the universityCostShareRequestTotalAmountTask attribute value.
     * 
     * @param universityCostShareRequestTotalAmountTask The universityCostShareRequestTotalAmountTask to set.
     */
    public void setUniversityCostShareRequestTotalAmountTask(KualiInteger universityCostShareRequestTotalAmountTask) {
        this.universityCostShareRequestTotalAmountTask = universityCostShareRequestTotalAmountTask;
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
    public KualiInteger getGradAsstUnivHealthInsuranceTotal() {
        return gradAsstUnivHealthInsuranceTotal;
    }

    /**
     * Sets the gradAsstUnivHealthInsurance attribute value.
     * 
     * @param gradAsstUnivHealthInsurance The gradAsstUnivHealthInsurance to set.
     */
    public void setGradAsstUnivHealthInsuranceTotal(KualiInteger gradAsstUnivHealthInsuranceTotal) {
        this.gradAsstUnivHealthInsuranceTotal = gradAsstUnivHealthInsuranceTotal;
    }

    /**
     * Gets the gradAsstUnivSalary attribute.
     * 
     * @return Returns the gradAsstUnivSalary.
     */
    public KualiInteger getGradAsstUnivSalaryTotal() {
        return gradAsstUnivSalaryTotal;
    }

    /**
     * Sets the gradAsstUnivSalary attribute value.
     * 
     * @param gradAsstUnivSalary The gradAsstUnivSalary to set.
     */
    public void setGradAsstUnivSalaryTotal(KualiInteger gradAsstUnivSalaryTotal) {
        this.gradAsstUnivSalaryTotal = gradAsstUnivSalaryTotal;
    }

    public boolean isSecondaryAppointment() {
        return secondaryAppointment;
    }

    public void setSecondaryAppointment(boolean isSecondaryAppointment) {
        this.secondaryAppointment = isSecondaryAppointment;
    }
}
