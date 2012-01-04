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

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.sys.businessobject.SystemOptions;

/**
 * Labor Journal Voucher Detail Business Object.
 */
public class LaborJournalVoucherDetail extends VoucherSourceAccountingLine {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborJournalVoucherDetail.class);
    private String positionNumber;
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
    private SystemOptions payrollEndDateOptions;
    private AccountingPeriod payrollEndDateFiscalPeriod;
    private SystemOptions options;

    /**
     * Default constructor.
     */
    public LaborJournalVoucherDetail() {
        super();
    }

    /**
     * Gets the earnCode.
     * 
     * @return Returns the earnCode.
     */
    public String getEarnCode() {
        return earnCode;
    }

    /**
     * Gets the emplid.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Gets the employeeRecord.
     * 
     * @return Returns the employeeRecord.
     */
    public Integer getEmployeeRecord() {
        return employeeRecord;
    }

    /**
     * Gets the grade.
     * 
     * @return Returns the grade.
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Gets the hrmsCompany.
     * 
     * @return Returns the hrmsCompany.
     */
    public String getHrmsCompany() {
        return hrmsCompany;
    }

    /**
     * Gets the laborLedgerOriginalAccountNumber.
     * 
     * @return Returns the laborLedgerOriginalAccountNumber.
     */
    public String getLaborLedgerOriginalAccountNumber() {
        return laborLedgerOriginalAccountNumber;
    }

    /**
     * Gets the laborLedgerOriginalChartOfAccountsCode.
     * 
     * @return Returns the laborLedgerOriginalChartOfAccountsCode.
     */
    public String getLaborLedgerOriginalChartOfAccountsCode() {
        return laborLedgerOriginalChartOfAccountsCode;
    }

    /**
     * Gets the laborLedgerOriginalFinancialObjectCode.
     * 
     * @return Returns the laborLedgerOriginalFinancialObjectCode.
     */
    public String getLaborLedgerOriginalFinancialObjectCode() {
        return laborLedgerOriginalFinancialObjectCode;
    }

    /**
     * Gets the laborLedgerOriginalFinancialSubObjectCode.
     * 
     * @return Returns the laborLedgerOriginalFinancialSubObjectCode.
     */
    public String getLaborLedgerOriginalFinancialSubObjectCode() {
        return laborLedgerOriginalFinancialSubObjectCode;
    }

    /**
     * Gets the laborLedgerOriginalSubAccountNumber.
     * 
     * @return Returns the laborLedgerOriginalSubAccountNumber.
     */
    public String getLaborLedgerOriginalSubAccountNumber() {
        return laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Gets the options.
     * 
     * @return Returns the options.
     */
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Gets the payGroup.
     * 
     * @return Returns the payGroup.
     */
    public String getPayGroup() {
        return payGroup;
    }

    /**
     * Gets the payPeriodEndDate.
     * 
     * @return Returns the payPeriodEndDate.
     */
    public Date getPayPeriodEndDate() {
        return payPeriodEndDate;
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
     * Gets the payrollEndDateOptions.
     * 
     * @return Returns the payrollEndDateOptions.
     */
    public SystemOptions getPayrollEndDateOptions() {
        return payrollEndDateOptions;
    }

    /**
     * Gets the positionNumber.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Gets the runIdentifier.
     * 
     * @return Returns the runIdentifier.
     */
    public String getRunIdentifier() {
        return runIdentifier;
    }

    /**
     * Gets the salaryAdministrationPlan.
     * 
     * @return Returns the salaryAdministrationPlan.
     */
    public String getSalaryAdministrationPlan() {
        return salaryAdministrationPlan;
    }

    /**
     * Gets the setid.
     * 
     * @return Returns the setid.
     */
    public String getSetid() {
        return setid;
    }

    /**
     * Gets the transactionTotalHours.
     * 
     * @return Returns the transactionTotalHours.
     */
    public BigDecimal getTransactionTotalHours() {
        return transactionTotalHours;
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
     * Sets the emplid.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
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
     * Sets the grade.
     * 
     * @param grade The grade to set.
     */
    public void setGrade(String grade) {
        this.grade = grade;
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
     * Sets the laborLedgerOriginalAccountNumber.
     * 
     * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
     */
    public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber) {
        this.laborLedgerOriginalAccountNumber = laborLedgerOriginalAccountNumber;
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
     * Sets the laborLedgerOriginalFinancialObjectCode.
     * 
     * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
     */
    public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode) {
        this.laborLedgerOriginalFinancialObjectCode = laborLedgerOriginalFinancialObjectCode;
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
     * Sets the laborLedgerOriginalSubAccountNumber.
     * 
     * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
     */
    public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber) {
        this.laborLedgerOriginalSubAccountNumber = laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Sets the options.
     * 
     * @param options The options to set.
     */
    public void setOptions(SystemOptions options) {
        this.options = options;
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
     * Sets the payPeriodEndDate.
     * 
     * @param payPeriodEndDate The payPeriodEndDate to set.
     */
    public void setPayPeriodEndDate(Date payPeriodEndDate) {
        this.payPeriodEndDate = payPeriodEndDate;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod.
     * 
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
        this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
    }

    /**
     * Sets the payrollEndDateOptions.
     * 
     * @param payrollEndDateOptions The payrollEndDateOptions to set.
     */
    public void setPayrollEndDateOptions(SystemOptions payrollEndDateOptions) {
        this.payrollEndDateOptions = payrollEndDateOptions;
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
     * Sets the runIdentifier.
     * 
     * @param runIdentifier The runIdentifier to set.
     */
    public void setRunIdentifier(String runIdentifier) {
        this.runIdentifier = runIdentifier;
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
     * Sets the setid.
     * 
     * @param setid The setid to set.
     */
    public void setSetid(String setid) {
        this.setid = setid;
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
     * Gets the payrollEndDateFiscalPeriodCode.
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode.
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
        try {
            Integer i = new Integer(payrollEndDateFiscalPeriodCode);
            if (i < 10 && payrollEndDateFiscalPeriodCode.length() == 1) {
                payrollEndDateFiscalPeriodCode = "0" + payrollEndDateFiscalPeriodCode;
            }
        }
        catch (NumberFormatException e) {
        }
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the payrollEndDateFiscalYear.
     * 
     * @return Returns the payrollEndDateFiscalYear.
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

}
