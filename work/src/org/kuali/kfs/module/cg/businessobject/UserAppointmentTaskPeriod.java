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

import java.util.LinkedHashMap;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.budget.service.impl.BudgetPersonnelServiceImpl.PeriodSalary;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class UserAppointmentTaskPeriod extends BusinessObjectBase implements Comparable {

    private String documentHeaderId;
    private Integer budgetTaskSequenceNumber;
    private Integer budgetPeriodSequenceNumber;
    private Integer budgetUserSequenceNumber;
    private String universityAppointmentTypeCode;
    private KualiInteger agencyFringeBenefitTotalAmount;
    private KualiInteger agencyPercentEffortAmount;
    private KualiInteger agencyRequestTotalAmount;
    private Integer personWeeksAmount;
    private String personWeeksJustificationText;
    private KualiInteger universityCostShareFringeBenefitTotalAmount;
    private KualiInteger universityCostSharePercentEffortAmount;
    private KualiInteger universityCostShareRequestTotalAmount;
    private KualiInteger userBudgetPeriodSalaryAmount;
    private KualiInteger userAgencyHours;
    private KualiDecimal userHourlyRate;
    private KualiInteger userUniversityHours;
    private KualiInteger agencyFullTimeEquivalentPercent;
    private KualiInteger agencyHealthInsuranceAmount;
    private KualiInteger agencyRequestedFeesAmount;
    private KualiInteger agencySalaryAmount;
    private KualiInteger universityFullTimeEquivalentPercent;
    private KualiInteger universityHealthInsuranceAmount;
    private KualiInteger universityRequestedFeesAmount;
    private KualiInteger universitySalaryAmount;
    private KualiInteger userCreditHoursNumber;
    private KualiDecimal userCreditHourAmount;
    private KualiInteger userMiscellaneousFeeAmount;
    private KualiInteger totalPercentEffort;
    private KualiInteger totalSalaryAmount;
    private KualiInteger totalFringeAmount;
    private KualiInteger totalFeeRemissionsAmount;
    private KualiInteger totalFteAmount;
    private KualiInteger totalHealthInsuranceAmount;
    private KualiInteger totalGradAsstSalaryAmount;


    private BudgetFringeRate budgetFringeRate;
    private transient PeriodSalary periodSalary;


    private BudgetTask task;
    private BudgetPeriod period;

    /**
     * Default no-arg constructor.
     */
    public UserAppointmentTaskPeriod() {
        super();
        agencyFringeBenefitTotalAmount = new KualiInteger(0);
        agencyPercentEffortAmount = new KualiInteger(0);
        agencyRequestTotalAmount = new KualiInteger(0);
        personWeeksAmount = new Integer(0);
        universityCostShareFringeBenefitTotalAmount = new KualiInteger(0);
        universityCostSharePercentEffortAmount = new KualiInteger(0);
        universityCostShareRequestTotalAmount = new KualiInteger(0);
        userBudgetPeriodSalaryAmount = new KualiInteger(0);
        userAgencyHours = new KualiInteger(0);
        userHourlyRate = new KualiDecimal(0);
        userUniversityHours = new KualiInteger(0);

        agencyFullTimeEquivalentPercent = new KualiInteger(0);
        agencyHealthInsuranceAmount = new KualiInteger(0);
        agencyRequestedFeesAmount = new KualiInteger(0);
        agencySalaryAmount = new KualiInteger(0);
        universityFullTimeEquivalentPercent = new KualiInteger(0);
        universityHealthInsuranceAmount = new KualiInteger(0);
        universityRequestedFeesAmount = new KualiInteger(0);
        universitySalaryAmount = new KualiInteger(0);
        userCreditHoursNumber = new KualiInteger(0);
        userCreditHourAmount = new KualiDecimal(0);
        userMiscellaneousFeeAmount = new KualiInteger(0);
        totalFeeRemissionsAmount = new KualiInteger(0);
        totalFteAmount = new KualiInteger(0);
        totalHealthInsuranceAmount = new KualiInteger(0);
        totalGradAsstSalaryAmount = new KualiInteger(0);
        totalPercentEffort = new KualiInteger(0);
        totalSalaryAmount = new KualiInteger(0);
        totalFringeAmount = new KualiInteger(0);

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
     * Gets the budgetPeriodSequenceNumber attribute.
     * 
     * @return - Returns the budgetPeriodSequenceNumber
     * 
     */
    public Integer getBudgetPeriodSequenceNumber() {
        return budgetPeriodSequenceNumber;
    }

    /**
     * Sets the budgetPeriodSequenceNumber attribute.
     * 
     * @param budgetPeriodSequenceNumber The budgetPeriodSequenceNumber to set.
     * 
     */
    public void setBudgetPeriodSequenceNumber(Integer budgetPeriodSequenceNumber) {
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
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
     * Gets the agencyFringeBenefitTotalAmount attribute.
     * 
     * @return - Returns the agencyFringeBenefitTotalAmount
     * 
     */
    public KualiInteger getAgencyFringeBenefitTotalAmount() {
        return agencyFringeBenefitTotalAmount;
    }

    /**
     * Sets the agencyFringeBenefitTotalAmount attribute.
     * 
     * @param agencyFringeBenefitTotalAmount The agencyFringeBenefitTotalAmount to set.
     * 
     */
    public void setAgencyFringeBenefitTotalAmount(KualiInteger agencyFringeBenefitTotalAmount) {
        this.agencyFringeBenefitTotalAmount = agencyFringeBenefitTotalAmount;
    }

    /**
     * Gets the agencyPercentEffortAmount attribute.
     * 
     * @return - Returns the agencyPercentEffortAmount
     * 
     */
    public KualiInteger getAgencyPercentEffortAmount() {
        return agencyPercentEffortAmount;
    }

    /**
     * Sets the agencyPercentEffortAmount attribute.
     * 
     * @param agencyPercentEffortAmount The agencyPercentEffortAmount to set.
     * 
     */
    public void setAgencyPercentEffortAmount(KualiInteger agencyPercentEffortAmount) {
        this.agencyPercentEffortAmount = agencyPercentEffortAmount;
    }

    /**
     * Gets the agencyRequestTotalAmount attribute.
     * 
     * @return - Returns the agencyRequestTotalAmount
     * 
     */
    public KualiInteger getAgencyRequestTotalAmount() {
        return agencyRequestTotalAmount;
    }

    /**
     * Sets the agencyRequestTotalAmount attribute.
     * 
     * @param agencyRequestTotalAmount The agencyRequestTotalAmount to set.
     * 
     */
    public void setAgencyRequestTotalAmount(KualiInteger agencyRequestTotalAmount) {
        this.agencyRequestTotalAmount = agencyRequestTotalAmount;
    }

    /**
     * Gets the personWeeksAmount attribute.
     * 
     * @return - Returns the personWeeksAmount
     * 
     */
    public Integer getPersonWeeksAmount() {
        return personWeeksAmount;
    }

    /**
     * Sets the personWeeksAmount attribute.
     * 
     * @param personWeeksAmount The personWeeksAmount to set.
     * 
     */
    public void setPersonWeeksAmount(Integer personWeeksAmount) {
        this.personWeeksAmount = personWeeksAmount;
    }

    /**
     * Gets the personWeeksJustificationText attribute.
     * 
     * @return - Returns the personWeeksJustificationText
     * 
     */
    public String getPersonWeeksJustificationText() {
        return personWeeksJustificationText;
    }

    /**
     * Sets the personWeeksJustificationText attribute.
     * 
     * @param personWeeksJustificationText The personWeeksJustificationText to set.
     * 
     */
    public void setPersonWeeksJustificationText(String personWeeksJustificationText) {
        this.personWeeksJustificationText = personWeeksJustificationText;
    }

    /**
     * Gets the universityCostShareFringeBenefitTotalAmount attribute.
     * 
     * @return - Returns the universityCostShareFringeBenefitTotalAmount
     * 
     */
    public KualiInteger getUniversityCostShareFringeBenefitTotalAmount() {
        return universityCostShareFringeBenefitTotalAmount;
    }

    /**
     * Sets the universityCostShareFringeBenefitTotalAmount attribute.
     * 
     * @param universityCostShareFringeBenefitTotalAmount The universityCostShareFringeBenefitTotalAmount to set.
     * 
     */
    public void setUniversityCostShareFringeBenefitTotalAmount(KualiInteger universityCostShareFringeBenefitTotalAmount) {
        this.universityCostShareFringeBenefitTotalAmount = universityCostShareFringeBenefitTotalAmount;
    }

    /**
     * Gets the universityCostSharePercentEffortAmount attribute.
     * 
     * @return - Returns the universityCostSharePercentEffortAmount
     * 
     */
    public KualiInteger getUniversityCostSharePercentEffortAmount() {
        return universityCostSharePercentEffortAmount;
    }

    /**
     * Sets the universityCostSharePercentEffortAmount attribute.
     * 
     * @param universityCostSharePercentEffortAmount The universityCostSharePercentEffortAmount to set.
     * 
     */
    public void setUniversityCostSharePercentEffortAmount(KualiInteger universityCostSharePercentEffortAmount) {
        this.universityCostSharePercentEffortAmount = universityCostSharePercentEffortAmount;
    }

    /**
     * Gets the universityCostShareRequestTotalAmount attribute.
     * 
     * @return - Returns the universityCostShareRequestTotalAmount
     * 
     */
    public KualiInteger getUniversityCostShareRequestTotalAmount() {
        return universityCostShareRequestTotalAmount;
    }

    /**
     * Sets the universityCostShareRequestTotalAmount attribute.
     * 
     * @param universityCostShareRequestTotalAmount The universityCostShareRequestTotalAmount to set.
     * 
     */
    public void setUniversityCostShareRequestTotalAmount(KualiInteger universityCostShareRequestTotalAmount) {
        this.universityCostShareRequestTotalAmount = universityCostShareRequestTotalAmount;
    }

    /**
     * Gets the userBudgetPeriodSalaryAmount attribute.
     * 
     * @return - Returns the userBudgetPeriodSalaryAmount
     * 
     */
    public KualiInteger getUserBudgetPeriodSalaryAmount() {
        return userBudgetPeriodSalaryAmount;
    }

    /**
     * Sets the userBudgetPeriodSalaryAmount attribute.
     * 
     * @param userBudgetPeriodSalaryAmount The userBudgetPeriodSalaryAmount to set.
     * 
     */
    public void setUserBudgetPeriodSalaryAmount(KualiInteger userBudgetPeriodSalaryAmount) {
        this.userBudgetPeriodSalaryAmount = userBudgetPeriodSalaryAmount;
    }

    /**
     * Gets the userAgencyHours attribute.
     * 
     * @return - Returns the userAgencyHours
     * 
     */
    public KualiInteger getUserAgencyHours() {
        return userAgencyHours;
    }

    /**
     * Sets the userAgencyHours attribute.
     * 
     * @param userAgencyHours The userAgencyHours to set.
     * 
     */
    public void setUserAgencyHours(KualiInteger userAgencyHours) {
        this.userAgencyHours = userAgencyHours;
    }

    /**
     * Gets the userHourlyRate attribute.
     * 
     * @return - Returns the userHourlyRate
     * 
     */
    public KualiDecimal getUserHourlyRate() {
        return userHourlyRate;
    }

    /**
     * Sets the userHourlyRate attribute.
     * 
     * @param userHourlyRate The userHourlyRate to set.
     * 
     */
    public void setUserHourlyRate(KualiDecimal userHourlyRate) {
        this.userHourlyRate = userHourlyRate;
    }

    /**
     * Gets the userUniversityHours attribute.
     * 
     * @return - Returns the userUniversityHours
     * 
     */
    public KualiInteger getUserUniversityHours() {
        return userUniversityHours;
    }

    /**
     * Sets the userUniversityHours attribute.
     * 
     * @param userUniversityHours The userUniversityHours to set.
     * 
     */
    public void setUserUniversityHours(KualiInteger userUniversityHours) {
        this.userUniversityHours = userUniversityHours;
    }

    /**
     * Gets the agencyFullTimeEquivalentPercent attribute.
     * 
     * @return - Returns the agencyFullTimeEquivalentPercent
     * 
     */
    public KualiInteger getAgencyFullTimeEquivalentPercent() {
        return agencyFullTimeEquivalentPercent;
    }

    public void setTotalFringeAmount(KualiInteger totalFringeAmount) {
        this.totalFringeAmount = totalFringeAmount;
    }

    public void setTotalPercentEffort(KualiInteger totalPercentEffort) {
        this.totalPercentEffort = totalPercentEffort;
    }

    public void setTotalSalaryAmount(KualiInteger totalSalaryAmount) {
        this.totalSalaryAmount = totalSalaryAmount;
    }

    /**
     * Sets the agencyFullTimeEquivalentPercent attribute.
     * 
     * @param agencyFullTimeEquivalentPercent The agencyFullTimeEquivalentPercent to set.
     * 
     */
    public void setAgencyFullTimeEquivalentPercent(KualiInteger agencyFullTimeEquivalentPercent) {
        this.agencyFullTimeEquivalentPercent = agencyFullTimeEquivalentPercent;
    }

    /**
     * Gets the agencyHealthInsuranceAmount attribute.
     * 
     * @return - Returns the agencyHealthInsuranceAmount
     * 
     */
    public KualiInteger getAgencyHealthInsuranceAmount() {
        return agencyHealthInsuranceAmount;
    }

    /**
     * Sets the agencyHealthInsuranceAmount attribute.
     * 
     * @param agencyHealthInsuranceAmount The agencyHealthInsuranceAmount to set.
     * 
     */
    public void setAgencyHealthInsuranceAmount(KualiInteger agencyHealthInsuranceAmount) {
        this.agencyHealthInsuranceAmount = agencyHealthInsuranceAmount;
    }

    /**
     * Gets the agencyRequestedFeesAmount attribute.
     * 
     * @return - Returns the agencyRequestedFeesAmount
     * 
     */
    public KualiInteger getAgencyRequestedFeesAmount() {
        return agencyRequestedFeesAmount;
    }

    /**
     * Sets the agencyRequestedFeesAmount attribute.
     * 
     * @param agencyRequestedFeesAmount The agencyRequestedFeesAmount to set.
     * 
     */
    public void setAgencyRequestedFeesAmount(KualiInteger agencyRequestedFeesAmount) {
        this.agencyRequestedFeesAmount = agencyRequestedFeesAmount;
    }

    /**
     * Gets the agencySalaryAmount attribute.
     * 
     * @return - Returns the agencySalaryAmount
     * 
     */
    public KualiInteger getAgencySalaryAmount() {
        return agencySalaryAmount;
    }

    /**
     * Sets the agencySalaryAmount attribute.
     * 
     * @param agencySalaryAmount The agencySalaryAmount to set.
     * 
     */
    public void setAgencySalaryAmount(KualiInteger agencySalaryAmount) {
        this.agencySalaryAmount = agencySalaryAmount;
    }

    /**
     * Gets the universityFullTimeEquivalentPercent attribute.
     * 
     * @return - Returns the universityFullTimeEquivalentPercent
     * 
     */
    public KualiInteger getUniversityFullTimeEquivalentPercent() {
        return universityFullTimeEquivalentPercent;
    }

    /**
     * Sets the universityFullTimeEquivalentPercent attribute.
     * 
     * @param universityFullTimeEquivalentPercent The universityFullTimeEquivalentPercent to set.
     * 
     */
    public void setUniversityFullTimeEquivalentPercent(KualiInteger universityFullTimeEquivalentPercent) {
        this.universityFullTimeEquivalentPercent = universityFullTimeEquivalentPercent;
    }

    /**
     * Gets the universityHealthInsuranceAmount attribute.
     * 
     * @return - Returns the universityHealthInsuranceAmount
     * 
     */
    public KualiInteger getUniversityHealthInsuranceAmount() {
        return universityHealthInsuranceAmount;
    }

    /**
     * Sets the universityHealthInsuranceAmount attribute.
     * 
     * @param universityHealthInsuranceAmount The universityHealthInsuranceAmount to set.
     * 
     */
    public void setUniversityHealthInsuranceAmount(KualiInteger universityHealthInsuranceAmount) {
        this.universityHealthInsuranceAmount = universityHealthInsuranceAmount;
    }

    /**
     * Gets the universityRequestedFeesAmount attribute.
     * 
     * @return - Returns the universityRequestedFeesAmount
     * 
     */
    public KualiInteger getUniversityRequestedFeesAmount() {
        return universityRequestedFeesAmount;
    }

    /**
     * Sets the universityRequestedFeesAmount attribute.
     * 
     * @param universityRequestedFeesAmount The universityRequestedFeesAmount to set.
     * 
     */
    public void setUniversityRequestedFeesAmount(KualiInteger universityRequestedFeesAmount) {
        this.universityRequestedFeesAmount = universityRequestedFeesAmount;
    }

    /**
     * Gets the universitySalaryAmount attribute.
     * 
     * @return - Returns the universitySalaryAmount
     * 
     */
    public KualiInteger getUniversitySalaryAmount() {
        return universitySalaryAmount;
    }

    /**
     * Sets the universitySalaryAmount attribute.
     * 
     * @param universitySalaryAmount The universitySalaryAmount to set.
     * 
     */
    public void setUniversitySalaryAmount(KualiInteger universitySalaryAmount) {
        this.universitySalaryAmount = universitySalaryAmount;
    }

    /**
     * Gets the userCreditHoursNumber attribute.
     * 
     * @return - Returns the userCreditHoursNumber
     * 
     */
    public KualiInteger getUserCreditHoursNumber() {
        return userCreditHoursNumber;
    }

    /**
     * Sets the userCreditHoursNumber attribute.
     * 
     * @param userCreditHoursNumber The userCreditHoursNumber to set.
     * 
     */
    public void setUserCreditHoursNumber(KualiInteger userCreditHoursNumber) {
        this.userCreditHoursNumber = userCreditHoursNumber;
    }

    /**
     * Gets the userCreditHourAmount attribute.
     * 
     * @return - Returns the userCreditHourAmount
     * 
     */
    public KualiDecimal getUserCreditHourAmount() {
        return userCreditHourAmount;
    }

    /**
     * Sets the userCreditHourAmount attribute.
     * 
     * @param userCreditHourAmount The userCreditHourAmount to set.
     * 
     */
    public void setUserCreditHourAmount(KualiDecimal userCreditHourAmount) {
        this.userCreditHourAmount = userCreditHourAmount;
    }

    /**
     * Gets the userMiscellaneousFeeAmount attribute.
     * 
     * @return - Returns the userMiscellaneousFeeAmount
     * 
     */
    public KualiInteger getUserMiscellaneousFeeAmount() {
        return userMiscellaneousFeeAmount;
    }

    /**
     * Sets the userMiscellaneousFeeAmount attribute.
     * 
     * @param userMiscellaneousFeeAmount The userMiscellaneousFeeAmount to set.
     * 
     */
    public void setUserMiscellaneousFeeAmount(KualiInteger userMiscellaneousFeeAmount) {
        this.userMiscellaneousFeeAmount = userMiscellaneousFeeAmount;
    }

    public KualiInteger getTotalPercentEffort() {
        return this.totalPercentEffort;
    }

    public KualiInteger getTotalSalaryAmount() {
        return this.totalSalaryAmount;
    }

    public KualiInteger getTotalFringeAmount() {
        return this.totalFringeAmount;
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
     * Gets the period attribute.
     * 
     * @return Returns the period.
     */
    public BudgetPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the period attribute value.
     * 
     * @param period The period to set.
     */
    public void setPeriod(BudgetPeriod period) {
        this.period = period;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentHeaderId", this.getDocumentHeaderId());
        m.put("budgetUserSequenceNumber", this.getBudgetUserSequenceNumber());
        m.put("universityAppointmentTypeCode", this.getUniversityAppointmentTypeCode());
        m.put("budgetTaskSequenceNumber", this.getBudgetTaskSequenceNumber());
        m.put("budgetPeriodSequenceNumber", this.getBudgetPeriodSequenceNumber());

        return m;
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
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return this.getPeriod().compareTo(((UserAppointmentTaskPeriod) o).getPeriod());
    }

    /**
     * Gets the totalFeeRemissionsAmount attribute.
     * 
     * @return Returns the totalFeeRemissionsAmount.
     */
    public KualiInteger getTotalFeeRemissionsAmount() {
        return this.totalFeeRemissionsAmount;
    }

    /**
     * Sets the totalFeeRemissionsAmount attribute value.
     * 
     * @param totalFeeRemissionsAmount The totalFeeRemissionsAmount to set.
     */
    public void setTotalFeeRemissionsAmount(KualiInteger totalFeeRemissionsAmount) {
        this.totalFeeRemissionsAmount = totalFeeRemissionsAmount;
    }

    /**
     * Gets the totalFteAmount attribute.
     * 
     * @return Returns the totalFteAmount.
     */
    public KualiInteger getTotalFteAmount() {
        return this.totalFteAmount;
    }

    /**
     * Sets the totalFteAmount attribute value.
     * 
     * @param totalFteAmount The totalFteAmount to set.
     */
    public void setTotalFteAmount(KualiInteger totalFteAmount) {
        this.totalFteAmount = totalFteAmount;
    }

    /**
     * Gets the totalHealthInsuranceAmount attribute.
     * 
     * @return Returns the totalHealthInsuranceAmount.
     */
    public KualiInteger getTotalHealthInsuranceAmount() {
        return this.totalHealthInsuranceAmount;
    }

    /**
     * Sets the totalHealthInsuranceAmount attribute value.
     * 
     * @param totalHealthInsuranceAmount The totalHealthInsuranceAmount to set.
     */
    public void setTotalHealthInsuranceAmount(KualiInteger totalHealthInsuranceAmount) {
        this.totalHealthInsuranceAmount = totalHealthInsuranceAmount;
    }

    /**
     * Gets the totalGradAsstSalaryAmount attribute.
     * 
     * @return Returns the totalGradAsstSalaryAmount.
     */
    public KualiInteger getTotalGradAsstSalaryAmount() {
        return this.totalGradAsstSalaryAmount;
    }

    /**
     * Sets the totalGradAsstSalaryAmount attribute value.
     * 
     * @param totalGradAsstSalaryAmount The totalGradAsstSalaryAmount to set.
     */
    public void setTotalGradAsstSalaryAmount(KualiInteger totalGradAsstSalaryAmount) {
        this.totalGradAsstSalaryAmount = totalGradAsstSalaryAmount;
    }

    public PeriodSalary getPeriodSalary() {
        return periodSalary;
    }

    public void setPeriodSalary(PeriodSalary periodSalary) {
        this.periodSalary = periodSalary;
    }

    // Object methods
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = true;

        if (ObjectUtils.isNotNull(obj) && obj instanceof UserAppointmentTaskPeriod) {
            UserAppointmentTaskPeriod objCompare = (UserAppointmentTaskPeriod) obj;
            equals &= this.documentHeaderId.equals(objCompare.getDocumentHeaderId());
            if (this.getAgencyFringeBenefitTotalAmount() == null && objCompare.getAgencyFringeBenefitTotalAmount() == null) {
            }
            else {
                equals &= this.getAgencyFringeBenefitTotalAmount() != null && objCompare.getAgencyFringeBenefitTotalAmount() != null && this.getAgencyFringeBenefitTotalAmount().equals(objCompare.getAgencyFringeBenefitTotalAmount());
            }
            if (this.agencyFullTimeEquivalentPercent == null && objCompare.getAgencyFullTimeEquivalentPercent() == null) {
            }
            else {
                equals &= this.getAgencyFullTimeEquivalentPercent() != null && objCompare.getAgencyFullTimeEquivalentPercent() != null && this.getAgencyFullTimeEquivalentPercent().equals(objCompare.getAgencyFullTimeEquivalentPercent());
            }
            if (this.agencyHealthInsuranceAmount == null && objCompare.getAgencyHealthInsuranceAmount() == null) {
            }
            else {
                equals &= this.agencyHealthInsuranceAmount != null && objCompare.getAgencyHealthInsuranceAmount() != null && this.agencyHealthInsuranceAmount.equals(objCompare.getAgencyHealthInsuranceAmount());
            }
            if (this.agencyPercentEffortAmount == null && objCompare.getAgencyPercentEffortAmount() == null) {
            }
            else {
                equals &= this.agencyPercentEffortAmount != null && objCompare.getAgencyPercentEffortAmount() != null && this.agencyPercentEffortAmount.equals(objCompare.getAgencyPercentEffortAmount());
            }
            if (this.agencyRequestedFeesAmount == null && objCompare.getAgencyRequestedFeesAmount() == null) {
            }
            else {
                equals &= this.agencyRequestedFeesAmount != null && objCompare.getAgencyRequestedFeesAmount() != null && this.agencyRequestedFeesAmount.equals(objCompare.getAgencyRequestedFeesAmount());
            }
            if (this.agencyRequestTotalAmount == null && objCompare.getAgencyRequestTotalAmount() == null) {
            }
            else {
                equals &= this.agencyRequestTotalAmount != null && objCompare.getAgencyRequestTotalAmount() != null && this.agencyRequestTotalAmount.equals(objCompare.getAgencyRequestTotalAmount());
            }
            if (this.agencySalaryAmount == null && objCompare.getAgencySalaryAmount() == null) {
            }
            else {
                equals &= this.agencySalaryAmount != null && objCompare.getAgencySalaryAmount() != null && this.agencySalaryAmount.equals(objCompare.getAgencySalaryAmount());
            }
            if (this.budgetFringeRate == null && objCompare.getBudgetFringeRate() == null) {
            }
            else {
                equals &= this.budgetFringeRate != null && objCompare.getBudgetFringeRate() != null && ObjectUtils.equalByKeys(this.budgetFringeRate, objCompare.getBudgetFringeRate());
            }
            if (this.budgetPeriodSequenceNumber == null && objCompare.getBudgetPeriodSequenceNumber() == null) {
            }
            else {
                equals &= this.budgetPeriodSequenceNumber != null && objCompare.getBudgetPeriodSequenceNumber() != null && this.budgetPeriodSequenceNumber.equals(objCompare.getBudgetPeriodSequenceNumber());
            }
            if (this.budgetTaskSequenceNumber == null && objCompare.getBudgetTaskSequenceNumber() == null) {
            }
            else {
                equals &= this.budgetTaskSequenceNumber != null && objCompare.getBudgetTaskSequenceNumber() != null && this.budgetTaskSequenceNumber.equals(objCompare.getBudgetTaskSequenceNumber());
            }
            if (this.budgetTaskSequenceNumber == null && objCompare.getBudgetTaskSequenceNumber() == null) {
            }
            else {
                equals &= this.budgetTaskSequenceNumber != null && objCompare.getBudgetTaskSequenceNumber() != null && this.budgetTaskSequenceNumber.equals(objCompare.getBudgetTaskSequenceNumber());
            }
            if (this.budgetUserSequenceNumber == null && objCompare.getBudgetUserSequenceNumber() == null) {
            }
            else {
                equals &= this.budgetUserSequenceNumber != null && objCompare.getBudgetUserSequenceNumber() != null && this.budgetUserSequenceNumber.equals(objCompare.getBudgetUserSequenceNumber());
            }
            if (ObjectUtils.isNull(this.period) && ObjectUtils.isNull(objCompare.getPeriod())) {
            }
            else {
                equals &= ObjectUtils.isNotNull(this.period) && ObjectUtils.isNotNull(objCompare.getPeriod()) && ObjectUtils.equalByKeys(this.period, objCompare.getPeriod());
            }
            if (this.personWeeksAmount == null && objCompare.getPersonWeeksAmount() == null) {
            }
            else {
                equals &= this.personWeeksAmount != null && objCompare.getPersonWeeksAmount() != null && this.personWeeksAmount.equals(objCompare.getPersonWeeksAmount());
            }
            if (ObjectUtils.isNull(this.task) && ObjectUtils.isNull(objCompare.getTask())) {
            }
            else {
                equals &= ObjectUtils.isNotNull(this.task) && ObjectUtils.isNotNull(objCompare.getTask()) && ObjectUtils.equalByKeys(this.getTask(), objCompare.getTask());
            }
            if (this.totalFeeRemissionsAmount == null && objCompare.getTotalFeeRemissionsAmount() == null) {
            }
            else {
                equals &= this.totalFeeRemissionsAmount != null && objCompare.getTotalFeeRemissionsAmount() != null && this.totalFeeRemissionsAmount.equals(objCompare.getTotalFeeRemissionsAmount());
            }
            if (this.totalFteAmount == null && objCompare.getTotalFteAmount() == null) {
            }
            else {
                equals &= this.getTotalFteAmount() != null && objCompare.getTotalFteAmount() != null && this.getTotalFteAmount().equals(objCompare.getTotalFteAmount());
            }
            if (this.totalGradAsstSalaryAmount == null && objCompare.getTotalGradAsstSalaryAmount() == null) {
            }
            else {
                equals &= this.totalGradAsstSalaryAmount != null && objCompare.getTotalGradAsstSalaryAmount() != null && this.totalGradAsstSalaryAmount.equals(objCompare.getTotalGradAsstSalaryAmount());
            }
            if (this.totalHealthInsuranceAmount == null && objCompare.getTotalHealthInsuranceAmount() == null) {
            }
            else {
                equals &= this.totalHealthInsuranceAmount != null && objCompare.getTotalHealthInsuranceAmount() != null && this.totalHealthInsuranceAmount.equals(objCompare.getTotalHealthInsuranceAmount());
            }
            if (this.universityAppointmentTypeCode == null && objCompare.getUniversityAppointmentTypeCode() == null) {
            }
            else {
                equals &= this.universityAppointmentTypeCode != null && objCompare.getUniversityAppointmentTypeCode() != null && this.universityAppointmentTypeCode.equals(objCompare.getUniversityAppointmentTypeCode());
            }
            if (this.universityCostShareFringeBenefitTotalAmount == null && objCompare.getUniversityCostShareFringeBenefitTotalAmount() == null) {
            }
            else {
                equals &= this.universityCostShareFringeBenefitTotalAmount != null && objCompare.getUniversityCostShareFringeBenefitTotalAmount() != null && this.universityCostShareFringeBenefitTotalAmount.equals(objCompare.getUniversityCostShareFringeBenefitTotalAmount());
            }
            if (this.universityCostSharePercentEffortAmount == null && objCompare.getUniversityCostSharePercentEffortAmount() == null) {
            }
            else {
                equals &= this.universityCostSharePercentEffortAmount != null && objCompare.getUniversityCostSharePercentEffortAmount() != null && this.universityCostSharePercentEffortAmount.equals(objCompare.getUniversityCostSharePercentEffortAmount());
            }
            if (this.universityCostShareRequestTotalAmount == null && objCompare.getUniversityCostShareRequestTotalAmount() == null) {
            }
            else {
                equals &= this.universityCostShareRequestTotalAmount != null && objCompare.getUniversityCostShareRequestTotalAmount() != null && this.universityCostShareRequestTotalAmount.equals(objCompare.getUniversityCostShareRequestTotalAmount());
            }
            if (this.universityFullTimeEquivalentPercent == null && objCompare.getUniversityFullTimeEquivalentPercent() == null) {
            }
            else {
                equals &= this.universityFullTimeEquivalentPercent != null && objCompare.getUniversityFullTimeEquivalentPercent() != null && this.universityFullTimeEquivalentPercent.equals(objCompare.getUniversityFullTimeEquivalentPercent());
            }
            if (this.universityHealthInsuranceAmount == null && objCompare.getUniversityHealthInsuranceAmount() == null) {
            }
            else {
                equals &= this.universityHealthInsuranceAmount != null && objCompare.getUniversityHealthInsuranceAmount() != null && this.universityHealthInsuranceAmount.equals(objCompare.getUniversityHealthInsuranceAmount());
            }
            if (this.universityRequestedFeesAmount == null && objCompare.getUniversityRequestedFeesAmount() == null) {
            }
            else {
                equals &= this.universityRequestedFeesAmount != null && objCompare.getUniversityRequestedFeesAmount() != null && this.universityRequestedFeesAmount.equals(objCompare.getUniversityRequestedFeesAmount());
            }
            if (this.universitySalaryAmount == null && objCompare.getUniversitySalaryAmount() == null) {
            }
            else {
                equals &= this.universitySalaryAmount != null && objCompare.getUniversitySalaryAmount() != null && this.universitySalaryAmount.equals(objCompare.getUniversitySalaryAmount());
            }
            if (this.userAgencyHours == null && objCompare.getUserAgencyHours() == null) {
            }
            else {
                equals &= this.userAgencyHours != null && objCompare.getUserAgencyHours() != null && this.userAgencyHours.equals(objCompare.getUserAgencyHours());
            }
            if (this.userBudgetPeriodSalaryAmount == null && objCompare.getUserBudgetPeriodSalaryAmount() == null) {
            }
            else {
                equals &= this.userBudgetPeriodSalaryAmount != null && objCompare.getUserBudgetPeriodSalaryAmount() != null && this.userBudgetPeriodSalaryAmount.equals(objCompare.getUserBudgetPeriodSalaryAmount());
            }
            if (this.userCreditHourAmount == null && objCompare.getUserCreditHourAmount() == null) {
            }
            else {
                equals &= this.userCreditHourAmount != null && objCompare.getUserCreditHourAmount() != null && this.userCreditHourAmount.equals(objCompare.getUserCreditHourAmount());
            }
            if (this.userCreditHoursNumber == null && objCompare.getUserCreditHoursNumber() == null) {
            }
            else {
                equals &= this.userCreditHoursNumber != null && objCompare.getUserCreditHoursNumber() != null && this.userCreditHoursNumber.equals(objCompare.getUserCreditHoursNumber());
            }
            if (this.userHourlyRate == null && objCompare.getUserHourlyRate() == null) {
            }
            else {
                equals &= this.userHourlyRate != null && objCompare.getUserHourlyRate() != null && this.userHourlyRate.equals(objCompare.getUserHourlyRate());
            }
            if (this.userMiscellaneousFeeAmount == null && objCompare.getUserMiscellaneousFeeAmount() == null) {
            }
            else {
                equals &= this.userMiscellaneousFeeAmount != null && objCompare.getUserMiscellaneousFeeAmount() != null && this.userMiscellaneousFeeAmount.equals(objCompare.getUserMiscellaneousFeeAmount());
            }
            if (this.userUniversityHours == null && objCompare.getUserUniversityHours() == null) {
            }
            else {
                equals &= this.userUniversityHours != null && objCompare.getUserUniversityHours() != null && this.userUniversityHours.equals(objCompare.getUserUniversityHours());
            }
        }

        return equals;
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = this.getDocumentHeaderId() + "|" + this.getPersonWeeksJustificationText() + "|" + this.getUniversityAppointmentTypeCode() + "|" + this.getAgencyFringeBenefitTotalAmount().toString() + "|" + this.getAgencyFullTimeEquivalentPercent().toString() + "|" + this.getAgencyHealthInsuranceAmount().toString() + "|" + this.getAgencyPercentEffortAmount().toString() + "|" + this.getAgencyRequestedFeesAmount().toString() + "|" + this.getAgencySalaryAmount().toString() + "|" + this.getBudgetFringeRate().toString() + "|" + this.getBudgetPeriodSequenceNumber().toString() + "|" + this.getBudgetTaskSequenceNumber().toString() + "|" + this.getBudgetUserSequenceNumber().toString() + "|" + this.getPeriod().toString() + "|" + this.getPeriodSalary().toString() + "|" + this.getPersonWeeksAmount().toString() + "|" + this.getTask().toString() + "|" + this.getTotalFeeRemissionsAmount().toString() + "|" + this.getTotalFringeAmount().toString() + "|" + this.getTotalFteAmount().toString()
                + "|" + this.getTotalGradAsstSalaryAmount().toString() + "|" + this.getTotalHealthInsuranceAmount().toString() + "|" + this.getTotalPercentEffort().toString() + "|" + this.getTotalSalaryAmount().toString() + "|" + this.getUniversityCostShareFringeBenefitTotalAmount().toString() + "|" + this.getUniversityCostSharePercentEffortAmount().toString() + "|" + this.getUniversityCostShareRequestTotalAmount().toString() + "|" + this.getUniversityFullTimeEquivalentPercent().toString() + "|" + this.getUniversityHealthInsuranceAmount().toString() + "|" + this.getUniversityRequestedFeesAmount().toString() + "|" + this.getUniversitySalaryAmount().toString() + "|" + this.getUserAgencyHours().toString() + "|" + this.getUserBudgetPeriodSalaryAmount().toString() + "|" + this.getUserCreditHourAmount().toString() + "|" + this.getUserCreditHoursNumber().toString() + "|" + this.getUserHourlyRate().toString() + "|" + this.getUserMiscellaneousFeeAmount().toString() + "|"
                + this.getUserUniversityHours().toString();
        return hashString.hashCode();
    }
}
