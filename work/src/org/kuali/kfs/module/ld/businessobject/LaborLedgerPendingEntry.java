/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.module.ld.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.integration.ld.LaborLedgerPendingEntryForSearching;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.UniversityDate;

/**
 * Labor business object for LaborLedgerPendingEntry.
 */
public class LaborLedgerPendingEntry extends GeneralLedgerPendingEntry implements LaborTransaction, LaborLedgerPendingEntryForSearching {
    private String positionNumber;
    private Date transactionPostingDate;
    private Date payPeriodEndDate;
    private BigDecimal transactionTotalHours;
    private Integer payrollEndDateFiscalYear;
    private String payrollEndDateFiscalPeriodCode;
    private String emplid;
    private Integer employeeRecord;
    private String earnCode;
    private String payGroup;
    private String salaryAdministrationPlan;
    private String grade;
    private String runIdentifier;
    private String laborLedgerOriginalChartOfAccountsCode;
    private String laborLedgerOriginalAccountNumber;
    private String laborLedgerOriginalSubAccountNumber;
    private String laborLedgerOriginalFinancialObjectCode;
    private String laborLedgerOriginalFinancialSubObjectCode;
    private String hrmsCompany;
    private String setid;
    private Timestamp transactionDateTimeStamp;
    private String transactionEntryOffsetCode;
    private AccountingPeriod universityFiscalPeriod;
    private AccountingPeriod payrollEndDateFiscalPeriod;
    private UniversityDate reversalDate;
    private PositionData positionData;

    /**
     * Default constructor.
     */
    public LaborLedgerPendingEntry() {
        super();
    }

    /**
     * Gets the positionNumber.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the transactionPostingDate.
     * 
     * @return Returns the transactionPostingDate
     */
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate.
     * 
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    /**
     * Gets the payPeriodEndDate.
     * 
     * @return Returns the payPeriodEndDate
     */
    public Date getPayPeriodEndDate() {
        return payPeriodEndDate;
    }

    /**
     * Sets the payPeriodEndDate.
     * 
     * @param payPeriodEndDate The payPeriodEndDate to set.
     */
    public void setPayPeriodEndDate(Date payPeriodEndDate) {
        this.payPeriodEndDate = payPeriodEndDate;
    }

    /**
     * Gets the transactionTotalHours.
     * 
     * @return Returns the transactionTotalHours
     */
    public BigDecimal getTransactionTotalHours() {
        return transactionTotalHours;
    }

    /**
     * Sets the transactionTotalHours.
     * 
     * @param transactionTotalHours The transactionTotalHours to set.
     */
    public void setTransactionTotalHours(BigDecimal transactionTotalHours) {
        this.transactionTotalHours = transactionTotalHours;
    }

    /**
     * Gets the payrollEndDateFiscalYear.
     * 
     * @return Returns the payrollEndDateFiscalYear
     */
    public Integer getPayrollEndDateFiscalYear() {
        return payrollEndDateFiscalYear;
    }

    /**
     * Sets the payrollEndDateFiscalYear.
     * 
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     */
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }

    /**
     * Gets the payrollEndDateFiscalPeriodCode.
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode
     */
    public String getPayrollEndDateFiscalPeriodCode() {
        return payrollEndDateFiscalPeriodCode;
    }

    /**
     * Sets the payrollEndDateFiscalPeriodCode.
     * 
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     */
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the emplid.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the employeeRecord.
     * 
     * @return Returns the employeeRecord
     */
    public Integer getEmployeeRecord() {
        return employeeRecord;
    }

    /**
     * Sets the employeeRecord.
     * 
     * @param employeeRecord The employeeRecord to set.
     */
    public void setEmployeeRecord(Integer employeeRecord) {
        this.employeeRecord = employeeRecord;
    }

    /**
     * Gets the earnCode.
     * 
     * @return Returns the earnCode
     */
    public String getEarnCode() {
        return earnCode;
    }

    /**
     * Sets the earnCode.
     * 
     * @param earnCode The earnCode to set.
     */
    public void setEarnCode(String earnCode) {
        this.earnCode = earnCode;
    }

    /**
     * Gets the payGroup.
     * 
     * @return Returns the payGroup
     */
    public String getPayGroup() {
        return payGroup;
    }

    /**
     * Sets the payGroup.
     * 
     * @param payGroup The payGroup to set.
     */
    public void setPayGroup(String payGroup) {
        this.payGroup = payGroup;
    }

    /**
     * Gets the salaryAdministrationPlan.
     * 
     * @return Returns the salaryAdministrationPlan
     */
    public String getSalaryAdministrationPlan() {
        return salaryAdministrationPlan;
    }

    /**
     * Sets the salaryAdministrationPlan.
     * 
     * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
     */
    public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
        this.salaryAdministrationPlan = salaryAdministrationPlan;
    }

    /**
     * Gets the grade.
     * 
     * @return Returns the grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the grade.
     * 
     * @param grade The grade to set.
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Gets the runIdentifier.
     * 
     * @return Returns the runIdentifier
     */
    public String getRunIdentifier() {
        return runIdentifier;
    }

    /**
     * Sets the runIdentifier.
     * 
     * @param runIdentifier The runIdentifier to set.
     */
    public void setRunIdentifier(String runIdentifier) {
        this.runIdentifier = runIdentifier;
    }

    /**
     * Gets the laborLedgerOriginalChartOfAccountsCode.
     * 
     * @return Returns the laborLedgerOriginalChartOfAccountsCode
     */
    public String getLaborLedgerOriginalChartOfAccountsCode() {
        return laborLedgerOriginalChartOfAccountsCode;
    }

    /**
     * Sets the laborLedgerOriginalChartOfAccountsCode.
     * 
     * @param laborLedgerOriginalChartOfAccountsCode The laborLedgerOriginalChartOfAccountsCode to set.
     */
    public void setLaborLedgerOriginalChartOfAccountsCode(String laborLedgerOriginalChartOfAccountsCode) {
        this.laborLedgerOriginalChartOfAccountsCode = laborLedgerOriginalChartOfAccountsCode;
    }

    /**
     * Gets the laborLedgerOriginalAccountNumber.
     * 
     * @return Returns the laborLedgerOriginalAccountNumber
     */
    public String getLaborLedgerOriginalAccountNumber() {
        return laborLedgerOriginalAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalAccountNumber.
     * 
     * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
     */
    public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber) {
        this.laborLedgerOriginalAccountNumber = laborLedgerOriginalAccountNumber;
    }

    /**
     * Gets the laborLedgerOriginalSubAccountNumber.
     * 
     * @return Returns the laborLedgerOriginalSubAccountNumber
     */
    public String getLaborLedgerOriginalSubAccountNumber() {
        return laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalSubAccountNumber.
     * 
     * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
     */
    public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber) {
        this.laborLedgerOriginalSubAccountNumber = laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Gets the laborLedgerOriginalFinancialObjectCode.
     * 
     * @return Returns the laborLedgerOriginalFinancialObjectCode
     */
    public String getLaborLedgerOriginalFinancialObjectCode() {
        return laborLedgerOriginalFinancialObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialObjectCode.
     * 
     * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
     */
    public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode) {
        this.laborLedgerOriginalFinancialObjectCode = laborLedgerOriginalFinancialObjectCode;
    }

    /**
     * Gets the laborLedgerOriginalFinancialSubObjectCode.
     * 
     * @return Returns the laborLedgerOriginalFinancialSubObjectCode
     */
    public String getLaborLedgerOriginalFinancialSubObjectCode() {
        return laborLedgerOriginalFinancialSubObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialSubObjectCode.
     * 
     * @param laborLedgerOriginalFinancialSubObjectCode The laborLedgerOriginalFinancialSubObjectCode to set.
     */
    public void setLaborLedgerOriginalFinancialSubObjectCode(String laborLedgerOriginalFinancialSubObjectCode) {
        this.laborLedgerOriginalFinancialSubObjectCode = laborLedgerOriginalFinancialSubObjectCode;
    }

    /**
     * Gets the hrmsCompany.
     * 
     * @return Returns the hrmsCompany
     */
    public String getHrmsCompany() {
        return hrmsCompany;
    }

    /**
     * Sets the hrmsCompany.
     * 
     * @param hrmsCompany The hrmsCompany to set.
     */
    public void setHrmsCompany(String hrmsCompany) {
        this.hrmsCompany = hrmsCompany;
    }

    /**
     * Gets the setid.
     * 
     * @return Returns the setid
     */
    public String getSetid() {
        return setid;
    }

    /**
     * Sets the setid.
     * 
     * @param setid The setid to set.
     */
    public void setSetid(String setid) {
        this.setid = setid;
    }

    /**
     * Gets the transactionDateTimeStamp.
     * 
     * @return Returns the transactionDateTimeStamp
     */
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp.
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    /**
     * Gets the payrollEndDateFiscalPeriod.
     * 
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    public AccountingPeriod getPayrollEndDateFiscalPeriod() {
        return payrollEndDateFiscalPeriod;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod.
     * 
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    @Deprecated
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
        this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
    }

    /**
     * Gets the reversalDate.
     * 
     * @return Returns the reversalDate.
     */
    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    /**
     * Sets the reversalDate.
     * 
     * @param reversalDate The reversalDate to set.
     */
    @Deprecated
    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Gets the universityFiscalPeriod.
     * 
     * @return Returns the universityFiscalPeriod.
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod.
     * 
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     */
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * Gets the positionData.
     * 
     * @return Returns the positionData.
     */
    public PositionData getPositionData() {
        return positionData;
    }

    /**
     * Sets the positionData.
     * 
     * @param positionData The positionData to set.
     */
    public void setPositionData(PositionData positionData) {
        this.positionData = positionData;
    }

    /**
     * Gets the ReferenceOriginationCode.
     * 
     * @see org.kuali.kfs.module.ld.businessobject.LaborTransaction#getReferenceFinancialSystemOrigination()
     */
    public OriginationCode getReferenceFinancialSystemOrigination() {
        return super.getReferenceOriginationCode();
    }

    /**
     * Sets the transactionEntryOffsetCode.
     * 
     * @param transactionEntryOffsetCode The transactionEntryOffsetCode to set.
     */
    public void setTransactionEntryOffsetCode(String transactionEntryOffsetCode) {
        this.transactionEntryOffsetCode = transactionEntryOffsetCode;
    }

    /**
     * Gets the transactionEntryOffsetCode.
     * 
     * @see org.kuali.kfs.module.ld.businessobject.LaborTransaction#getTransactionEntryOffsetCode()
     */
    public String getTransactionEntryOffsetCode() {
        return transactionEntryOffsetCode;
    }

    /**
     * Gets the TransactionEntryProcessedTs.
     * 
     * @see org.kuali.kfs.module.ld.businessobject.LaborTransaction#getTransactionEntryProcessedTimestamp()
     */
    public Timestamp getTransactionEntryProcessedTimestamp() {
        return super.getTransactionEntryProcessedTs();
    }
}
