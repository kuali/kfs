/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.labor.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.UniversityDate;

/**
 * Business object representing a pending entry to the labor ledger.
 */
public class LaborLedgerPendingEntry extends GeneralLedgerPendingEntry implements LaborTransaction {

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
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the transactionPostingDate attribute.
     * 
     * @return Returns the transactionPostingDate
     */
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate attribute.
     * 
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }


    /**
     * Gets the payPeriodEndDate attribute.
     * 
     * @return Returns the payPeriodEndDate
     */
    public Date getPayPeriodEndDate() {
        return payPeriodEndDate;
    }

    /**
     * Sets the payPeriodEndDate attribute.
     * 
     * @param payPeriodEndDate The payPeriodEndDate to set.
     */
    public void setPayPeriodEndDate(Date payPeriodEndDate) {
        this.payPeriodEndDate = payPeriodEndDate;
    }


    /**
     * Gets the transactionTotalHours attribute.
     * 
     * @return Returns the transactionTotalHours
     */
    public BigDecimal getTransactionTotalHours() {
        return transactionTotalHours;
    }

    /**
     * Sets the transactionTotalHours attribute.
     * 
     * @param transactionTotalHours The transactionTotalHours to set.
     */
    public void setTransactionTotalHours(BigDecimal transactionTotalHours) {
        this.transactionTotalHours = transactionTotalHours;
    }


    /**
     * Gets the payrollEndDateFiscalYear attribute.
     * 
     * @return Returns the payrollEndDateFiscalYear
     */
    public Integer getPayrollEndDateFiscalYear() {
        return payrollEndDateFiscalYear;
    }

    /**
     * Sets the payrollEndDateFiscalYear attribute.
     * 
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     */
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }


    /**
     * Gets the payrollEndDateFiscalPeriodCode attribute.
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode
     */
    public String getPayrollEndDateFiscalPeriodCode() {
        return payrollEndDateFiscalPeriodCode;
    }

    /**
     * Sets the payrollEndDateFiscalPeriodCode attribute.
     * 
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     */
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }


    /**
     * Gets the employeeRecord attribute.
     * 
     * @return Returns the employeeRecord
     */
    public Integer getEmployeeRecord() {
        return employeeRecord;
    }

    /**
     * Sets the employeeRecord attribute.
     * 
     * @param employeeRecord The employeeRecord to set.
     */
    public void setEmployeeRecord(Integer employeeRecord) {
        this.employeeRecord = employeeRecord;
    }


    /**
     * Gets the earnCode attribute.
     * 
     * @return Returns the earnCode
     */
    public String getEarnCode() {
        return earnCode;
    }

    /**
     * Sets the earnCode attribute.
     * 
     * @param earnCode The earnCode to set.
     */
    public void setEarnCode(String earnCode) {
        this.earnCode = earnCode;
    }


    /**
     * Gets the payGroup attribute.
     * 
     * @return Returns the payGroup
     */
    public String getPayGroup() {
        return payGroup;
    }

    /**
     * Sets the payGroup attribute.
     * 
     * @param payGroup The payGroup to set.
     */
    public void setPayGroup(String payGroup) {
        this.payGroup = payGroup;
    }


    /**
     * Gets the salaryAdministrationPlan attribute.
     * 
     * @return Returns the salaryAdministrationPlan
     */
    public String getSalaryAdministrationPlan() {
        return salaryAdministrationPlan;
    }

    /**
     * Sets the salaryAdministrationPlan attribute.
     * 
     * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
     */
    public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
        this.salaryAdministrationPlan = salaryAdministrationPlan;
    }


    /**
     * Gets the grade attribute.
     * 
     * @return Returns the grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the grade attribute.
     * 
     * @param grade The grade to set.
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }


    /**
     * Gets the runIdentifier attribute.
     * 
     * @return Returns the runIdentifier
     */
    public String getRunIdentifier() {
        return runIdentifier;
    }

    /**
     * Sets the runIdentifier attribute.
     * 
     * @param runIdentifier The runIdentifier to set.
     */
    public void setRunIdentifier(String runIdentifier) {
        this.runIdentifier = runIdentifier;
    }


    /**
     * Gets the laborLedgerOriginalChartOfAccountsCode attribute.
     * 
     * @return Returns the laborLedgerOriginalChartOfAccountsCode
     */
    public String getLaborLedgerOriginalChartOfAccountsCode() {
        return laborLedgerOriginalChartOfAccountsCode;
    }

    /**
     * Sets the laborLedgerOriginalChartOfAccountsCode attribute.
     * 
     * @param laborLedgerOriginalChartOfAccountsCode The laborLedgerOriginalChartOfAccountsCode to set.
     */
    public void setLaborLedgerOriginalChartOfAccountsCode(String laborLedgerOriginalChartOfAccountsCode) {
        this.laborLedgerOriginalChartOfAccountsCode = laborLedgerOriginalChartOfAccountsCode;
    }


    /**
     * Gets the laborLedgerOriginalAccountNumber attribute.
     * 
     * @return Returns the laborLedgerOriginalAccountNumber
     */
    public String getLaborLedgerOriginalAccountNumber() {
        return laborLedgerOriginalAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalAccountNumber attribute.
     * 
     * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
     */
    public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber) {
        this.laborLedgerOriginalAccountNumber = laborLedgerOriginalAccountNumber;
    }


    /**
     * Gets the laborLedgerOriginalSubAccountNumber attribute.
     * 
     * @return Returns the laborLedgerOriginalSubAccountNumber
     */
    public String getLaborLedgerOriginalSubAccountNumber() {
        return laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalSubAccountNumber attribute.
     * 
     * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
     */
    public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber) {
        this.laborLedgerOriginalSubAccountNumber = laborLedgerOriginalSubAccountNumber;
    }


    /**
     * Gets the laborLedgerOriginalFinancialObjectCode attribute.
     * 
     * @return Returns the laborLedgerOriginalFinancialObjectCode
     */
    public String getLaborLedgerOriginalFinancialObjectCode() {
        return laborLedgerOriginalFinancialObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialObjectCode attribute.
     * 
     * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
     */
    public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode) {
        this.laborLedgerOriginalFinancialObjectCode = laborLedgerOriginalFinancialObjectCode;
    }


    /**
     * Gets the laborLedgerOriginalFinancialSubObjectCode attribute.
     * 
     * @return Returns the laborLedgerOriginalFinancialSubObjectCode
     */
    public String getLaborLedgerOriginalFinancialSubObjectCode() {
        return laborLedgerOriginalFinancialSubObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialSubObjectCode attribute.
     * 
     * @param laborLedgerOriginalFinancialSubObjectCode The laborLedgerOriginalFinancialSubObjectCode to set.
     */
    public void setLaborLedgerOriginalFinancialSubObjectCode(String laborLedgerOriginalFinancialSubObjectCode) {
        this.laborLedgerOriginalFinancialSubObjectCode = laborLedgerOriginalFinancialSubObjectCode;
    }


    /**
     * Gets the hrmsCompany attribute.
     * 
     * @return Returns the hrmsCompany
     */
    public String getHrmsCompany() {
        return hrmsCompany;
    }

    /**
     * Sets the hrmsCompany attribute.
     * 
     * @param hrmsCompany The hrmsCompany to set.
     */
    public void setHrmsCompany(String hrmsCompany) {
        this.hrmsCompany = hrmsCompany;
    }


    /**
     * Gets the setid attribute.
     * 
     * @return Returns the setid
     */
    public String getSetid() {
        return setid;
    }

    /**
     * Sets the setid attribute.
     * 
     * @param setid The setid to set.
     */
    public void setSetid(String setid) {
        this.setid = setid;
    }


    /**
     * Gets the transactionDateTimeStamp attribute.
     * 
     * @return Returns the transactionDateTimeStamp
     */
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp attribute.
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    /**
     * Gets the payrollEndDateFiscalPeriod attribute.
     * 
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    public AccountingPeriod getPayrollEndDateFiscalPeriod() {
        return payrollEndDateFiscalPeriod;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod attribute value.
     * 
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    @Deprecated
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
        this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
    }

    /**
     * Gets the reversalDate attribute.
     * 
     * @return Returns the reversalDate.
     */
    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    /**
     * Sets the reversalDate attribute value.
     * 
     * @param reversalDate The reversalDate to set.
     */
    @Deprecated
    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Gets the universityFiscalPeriod attribute.
     * 
     * @return Returns the universityFiscalPeriod.
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod attribute value.
     * 
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     */
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * Gets the positionData attribute.
     * 
     * @return Returns the positionData.
     */
    public PositionData getPositionData() {
        return positionData;
    }

    /**
     * Sets the positionData attribute value.
     * 
     * @param positionData The positionData to set.
     */
    public void setPositionData(PositionData positionData) {
        this.positionData = positionData;
    }

    /**
     * @see org.kuali.module.labor.bo.LaborTransaction#getFinancialDocument()
     */
    public DocumentHeader getFinancialDocument() {
        return super.getDocumentHeader();
    }

    /**
     * @see org.kuali.module.labor.bo.LaborTransaction#getReferenceFinancialDocumentType()
     */
    public DocumentType getReferenceFinancialDocumentType() {
        return super.getReferenceDocumentType();
    }

    /**
     * @see org.kuali.module.labor.bo.LaborTransaction#getReferenceFinancialSystemOrigination()
     */
    public OriginationCode getReferenceFinancialSystemOrigination() {
        return super.getReferenceOriginationCode();
    }

    /**
     * Sets the transactionEntryOffsetCode attribute value.
     * 
     * @param transactionEntryOffsetCode The transactionEntryOffsetCode to set.
     */
    public void setTransactionEntryOffsetCode(String transactionEntryOffsetCode) {
        this.transactionEntryOffsetCode = transactionEntryOffsetCode;
    }

    /**
     * @see org.kuali.module.labor.bo.LaborTransaction#getTransactionEntryOffsetCode()
     */
    public String getTransactionEntryOffsetCode() {
        return transactionEntryOffsetCode;
    }

    /**
     * @see org.kuali.module.labor.bo.LaborTransaction#getTransactionEntryProcessedTimestamp()
     */
    public Date getTransactionEntryProcessedTimestamp() {
        return super.getTransactionEntryProcessedTs();
    }
}
