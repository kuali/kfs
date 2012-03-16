/*
 * Copyright 2006 The Kuali Foundation
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
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.integration.ld.LaborLedgerEntry;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

/**
 * Labor business object for LedgerEntry
 */
public class LedgerEntry extends Entry implements LaborLedgerEntry {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String universityFiscalPeriodCode;
    private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private String positionNumber;
    private String projectCode;
    private String transactionLedgerEntryDescription;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionDebitCreditCode;
    private Date transactionDate;
    private String organizationDocumentNumber;
    private String organizationReferenceId;
    private String referenceFinancialDocumentTypeCode;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private Date financialDocumentReversalDate;
    private String transactionEncumbranceUpdateCode;
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

    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private SubObjectCode financialSubObject;
    private ObjectType financialObjectType;
    private BalanceType balanceType;
    private AccountingPeriod universityFiscalPeriod;
    private AccountingPeriod payrollEndDateFiscalPeriod;
    private DocumentTypeEBO financialSystemDocumentTypeCode;
    private DocumentTypeEBO referenceFinancialSystemDocumentTypeCode;
    private SystemOptions option;
    private OriginationCode referenceOriginationCode;
    private ProjectCode project;
    private OriginationCode financialSystemOrigination;
    private LaborObject laborObject;
    private Person employee;

    /**
     * Default constructor.
     */
    public LedgerEntry() {
        super();
        this.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
    }

    /**
     * Constructs a LedgerEntry.java.
     *
     * @param transaction the given transaction
     */
    public LedgerEntry(LaborTransaction transaction) {
        super(transaction);
        this.setEarnCode(transaction.getEarnCode());
        this.setEmplid(transaction.getEmplid());
        this.setEmployeeRecord(transaction.getEmployeeRecord());
        this.setGrade(transaction.getGrade());
        this.setHrmsCompany(transaction.getHrmsCompany());
        this.setLaborLedgerOriginalAccountNumber(transaction.getLaborLedgerOriginalAccountNumber());
        this.setLaborLedgerOriginalChartOfAccountsCode(transaction.getLaborLedgerOriginalChartOfAccountsCode());
        this.setLaborLedgerOriginalFinancialObjectCode(transaction.getLaborLedgerOriginalFinancialObjectCode());
        this.setLaborLedgerOriginalFinancialSubObjectCode(transaction.getLaborLedgerOriginalFinancialSubObjectCode());
        this.setLaborLedgerOriginalSubAccountNumber(transaction.getLaborLedgerOriginalSubAccountNumber());
        this.setPayGroup(transaction.getPayGroup());
        this.setPayPeriodEndDate(transaction.getPayPeriodEndDate());
        this.setPayrollEndDateFiscalPeriodCode(transaction.getPayrollEndDateFiscalPeriodCode());
        this.setPayrollEndDateFiscalYear(transaction.getPayrollEndDateFiscalYear());
        this.setPositionNumber(transaction.getPositionNumber());
        this.setRunIdentifier(transaction.getRunIdentifier());
        this.setProjectCode(transaction.getProjectCode());
        this.setSetid(transaction.getSetid());
        this.setSalaryAdministrationPlan(transaction.getSalaryAdministrationPlan());
        this.setTransactionTotalHours(transaction.getTransactionTotalHours());
    }

    /**
     * Gets the universityFiscalYear
     *
     * @return Returns the universityFiscalYear
     */
    @Override
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    @Override
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode
     *
     * @return Returns the chartOfAccountsCode
     */
    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    @Override
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber
     *
     * @return Returns the accountNumber
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber
     *
     * @param accountNumber The accountNumber to set.
     */
    @Override
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber
     *
     * @return Returns the subAccountNumber
     */
    @Override
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber
     *
     * @param subAccountNumber The subAccountNumber to set.
     */
    @Override
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode
     *
     * @return Returns the financialObjectCode
     */
    @Override
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    @Override
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode
     *
     * @return Returns the financialSubObjectCode
     */
    @Override
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode
     *
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    @Override
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the financialBalanceTypeCode
     *
     * @return Returns the financialBalanceTypeCode
     */
    @Override
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode
     *
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    @Override
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode
     *
     * @return Returns the financialObjectTypeCode
     */
    @Override
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode
     *
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    @Override
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the universityFiscalPeriodCode
     *
     * @return Returns the universityFiscalPeriodCode
     */
    @Override
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode
     *
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    @Override
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    /**
     * Gets the financialDocumentTypeCode
     *
     * @return Returns the financialDocumentTypeCode
     */
    @Override
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Gets the financialSystemOriginationCode
     *
     * @return Returns the financialSystemOriginationCode.
     */
    @Override
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode
     *
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    @Override
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    /**
     * Sets the financialDocumentTypeCode
     *
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    @Override
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the documentNumber
     *
     * @return Returns the documentNumber
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber
     *
     * @param documentNumber The documentNumber to set.
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the transactionLedgerEntrySequenceNumber
     *
     * @return Returns the transactionLedgerEntrySequenceNumber
     */
    @Override
    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the transactionLedgerEntrySequenceNumber
     *
     * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
     */
    @Override
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    /**
     * Gets the positionNumber
     *
     * @return Returns the positionNumber
     */
    @Override
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber
     *
     * @param positionNumber The positionNumber to set.
     */
    @Override
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the projectCode
     *
     * @return Returns the projectCode
     */
    @Override
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode
     *
     * @param projectCode The projectCode to set.
     */
    @Override
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the transactionLedgerEntryDescription
     *
     * @return Returns the transactionLedgerEntryDescription
     */
    @Override
    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    /**
     * Sets the transactionLedgerEntryDescription
     *
     * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
     */
    @Override
    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    /**
     * Gets the transactionLedgerEntryAmount
     *
     * @return Returns the transactionLedgerEntryAmount
     */
    @Override
    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    /**
     * Sets the transactionLedgerEntryAmount
     *
     * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
     */
    @Override
    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    /**
     * Gets the transactionDebitCreditCode
     *
     * @return Returns the transactionDebitCreditCode
     */
    @Override
    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    /**
     * Sets the transactionDebitCreditCode
     *
     * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
     */
    @Override
    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    /**
     * Gets the transactionDate
     *
     * @return Returns the transactionDate
     */
    @Override
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transactionDate
     *
     * @param transactionDate The transactionDate to set.
     */
    @Override
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the organizationDocumentNumber
     *
     * @return Returns the organizationDocumentNumber
     */
    @Override
    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    /**
     * Sets the organizationDocumentNumber
     *
     * @param organizationDocumentNumber The organizationDocumentNumber to set.
     */
    @Override
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    /**
     * Gets the organizationReferenceId
     *
     * @return Returns the organizationReferenceId
     */
    @Override
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * Sets the organizationReferenceId
     *
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    @Override
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * Gets the referenceFinancialDocumentTypeCode
     *
     * @return Returns the referenceFinancialDocumentTypeCode
     */
    @Override
    public String getReferenceFinancialDocumentTypeCode() {
        return referenceFinancialDocumentTypeCode;
    }

    /**
     * Sets the referenceFinancialDocumentTypeCode
     *
     * @param referenceFinancialDocumentTypeCode The referenceFinancialDocumentTypeCode to set.
     */
    @Override
    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
    }

    /**
     * Gets the referenceFinancialSystemOriginationCode
     *
     * @return Returns the referenceFinancialSystemOriginationCode
     */
    @Override
    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    /**
     * Sets the referenceFinancialSystemOriginationCode
     *
     * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
     */
    @Override
    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    /**
     * Gets the referenceFinancialDocumentNumber
     *
     * @return Returns the referenceFinancialDocumentNumber
     */
    @Override
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber
     *
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    @Override
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    /**
     * Gets the financialDocumentReversalDate
     *
     * @return Returns the financialDocumentReversalDate
     */
    @Override
    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    /**
     * Sets the financialDocumentReversalDate
     *
     * @param financialDocumentReversalDate The financialDocumentReversalDate to set.
     */
    @Override
    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    /**
     * Gets the transactionEncumbranceUpdateCode
     *
     * @return Returns the transactionEncumbranceUpdateCode
     */
    @Override
    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    /**
     * Sets the transactionEncumbranceUpdateCode
     *
     * @param transactionEncumbranceUpdateCode The transactionEncumbranceUpdateCode to set.
     */
    @Override
    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    /**
     * Gets the transactionPostingDate
     *
     * @return Returns the transactionPostingDate
     */
    @Override
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate
     *
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    @Override
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    /**
     * Gets the payPeriodEndDate
     *
     * @return Returns the payPeriodEndDate
     */
    @Override
    public Date getPayPeriodEndDate() {
        return payPeriodEndDate;
    }

    /**
     * Sets the payPeriodEndDate
     *
     * @param payPeriodEndDate The payPeriodEndDate to set.
     */
    @Override
    public void setPayPeriodEndDate(Date payPeriodEndDate) {
        this.payPeriodEndDate = payPeriodEndDate;
    }

    /**
     * Gets the transactionTotalHours
     *
     * @return Returns the transactionTotalHours
     */
    @Override
    public BigDecimal getTransactionTotalHours() {
        return transactionTotalHours;
    }

    /**
     * Sets the transactionTotalHours
     *
     * @param transactionTotalHours The transactionTotalHours to set.
     */
    @Override
    public void setTransactionTotalHours(BigDecimal transactionTotalHours) {
        this.transactionTotalHours = transactionTotalHours;
    }

    /**
     * Gets the payrollEndDateFiscalYear
     *
     * @return Returns the payrollEndDateFiscalYear
     */
    @Override
    public Integer getPayrollEndDateFiscalYear() {
        return payrollEndDateFiscalYear;
    }

    /**
     * Sets the payrollEndDateFiscalYear
     *
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     */
    @Override
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }

    /**
     * Gets the payrollEndDateFiscalPeriodCode
     *
     * @return Returns the payrollEndDateFiscalPeriodCode
     */
    @Override
    public String getPayrollEndDateFiscalPeriodCode() {
        return payrollEndDateFiscalPeriodCode;
    }

    /**
     * Sets the payrollEndDateFiscalPeriodCode
     *
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     */
    @Override
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the emplid
     *
     * @return Returns the emplid
     */
    @Override
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid
     *
     * @param emplid The emplid to set.
     */
    @Override
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the employeeRecord
     *
     * @return Returns the employeeRecord
     */
    @Override
    public Integer getEmployeeRecord() {
        return employeeRecord;
    }

    /**
     * Sets the employeeRecord
     *
     * @param employeeRecord The employeeRecord to set.
     */
    @Override
    public void setEmployeeRecord(Integer employeeRecord) {
        this.employeeRecord = employeeRecord;
    }

    /**
     * Gets the earnCode
     *
     * @return Returns the earnCode
     */
    @Override
    public String getEarnCode() {
        return earnCode;
    }

    /**
     * Sets the earnCode
     *
     * @param earnCode The earnCode to set.
     */
    @Override
    public void setEarnCode(String earnCode) {
        this.earnCode = earnCode;
    }

    /**
     * Gets the payGroup
     *
     * @return Returns the payGroup
     */
    @Override
    public String getPayGroup() {
        return payGroup;
    }

    /**
     * Sets the payGroup
     *
     * @param payGroup The payGroup to set.
     */
    @Override
    public void setPayGroup(String payGroup) {
        this.payGroup = payGroup;
    }

    /**
     * Gets the salaryAdministrationPlan
     *
     * @return Returns the salaryAdministrationPlan
     */
    @Override
    public String getSalaryAdministrationPlan() {
        return salaryAdministrationPlan;
    }

    /**
     * Sets the salaryAdministrationPlan
     *
     * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
     */
    @Override
    public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
        this.salaryAdministrationPlan = salaryAdministrationPlan;
    }

    /**
     * Gets the grade
     *
     * @return Returns the grade
     */
    @Override
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the grade
     *
     * @param grade The grade to set.
     */
    @Override
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Gets the runIdentifier
     *
     * @return Returns the runIdentifier
     */
    @Override
    public String getRunIdentifier() {
        return runIdentifier;
    }

    /**
     * Sets the runIdentifier
     *
     * @param runIdentifier The runIdentifier to set.
     */
    @Override
    public void setRunIdentifier(String runIdentifier) {
        this.runIdentifier = runIdentifier;
    }

    /**
     * Gets the laborLedgerOriginalChartOfAccountsCode
     *
     * @return Returns the laborLedgerOriginalChartOfAccountsCode
     */
    @Override
    public String getLaborLedgerOriginalChartOfAccountsCode() {
        return laborLedgerOriginalChartOfAccountsCode;
    }

    /**
     * Sets the laborLedgerOriginalChartOfAccountsCode
     *
     * @param laborLedgerOriginalChartOfAccountsCode The laborLedgerOriginalChartOfAccountsCode to set.
     */
    @Override
    public void setLaborLedgerOriginalChartOfAccountsCode(String laborLedgerOriginalChartOfAccountsCode) {
        this.laborLedgerOriginalChartOfAccountsCode = laborLedgerOriginalChartOfAccountsCode;
    }

    /**
     * Gets the laborLedgerOriginalAccountNumber
     *
     * @return Returns the laborLedgerOriginalAccountNumber
     */
    @Override
    public String getLaborLedgerOriginalAccountNumber() {
        return laborLedgerOriginalAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalAccountNumber
     *
     * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
     */
    @Override
    public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber) {
        this.laborLedgerOriginalAccountNumber = laborLedgerOriginalAccountNumber;
    }

    /**
     * Gets the laborLedgerOriginalSubAccountNumber
     *
     * @return Returns the laborLedgerOriginalSubAccountNumber
     */
    @Override
    public String getLaborLedgerOriginalSubAccountNumber() {
        return laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalSubAccountNumber
     *
     * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
     */
    @Override
    public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber) {
        this.laborLedgerOriginalSubAccountNumber = laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Gets the laborLedgerOriginalFinancialObjectCode
     *
     * @return Returns the laborLedgerOriginalFinancialObjectCode
     */
    @Override
    public String getLaborLedgerOriginalFinancialObjectCode() {
        return laborLedgerOriginalFinancialObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialObjectCode
     *
     * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
     */
    @Override
    public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode) {
        this.laborLedgerOriginalFinancialObjectCode = laborLedgerOriginalFinancialObjectCode;
    }

    /**
     * Gets the laborLedgerOriginalFinancialSubObjectCode
     *
     * @return Returns the laborLedgerOriginalFinancialSubObjectCode
     */
    @Override
    public String getLaborLedgerOriginalFinancialSubObjectCode() {
        return laborLedgerOriginalFinancialSubObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialSubObjectCode
     *
     * @param laborLedgerOriginalFinancialSubObjectCode The laborLedgerOriginalFinancialSubObjectCode to set.
     */
    @Override
    public void setLaborLedgerOriginalFinancialSubObjectCode(String laborLedgerOriginalFinancialSubObjectCode) {
        this.laborLedgerOriginalFinancialSubObjectCode = laborLedgerOriginalFinancialSubObjectCode;
    }

    /**
     * Gets the hrmsCompany
     *
     * @return Returns the hrmsCompany
     */
    @Override
    public String getHrmsCompany() {
        return hrmsCompany;
    }

    /**
     * Sets the hrmsCompany
     *
     * @param hrmsCompany The hrmsCompany to set.
     */
    @Override
    public void setHrmsCompany(String hrmsCompany) {
        this.hrmsCompany = hrmsCompany;
    }

    /**
     * Gets the setid
     *
     * @return Returns the setid
     */
    @Override
    public String getSetid() {
        return setid;
    }

    /**
     * Sets the setid
     *
     * @param setid The setid to set.
     */
    @Override
    public void setSetid(String setid) {
        this.setid = setid;
    }

    /**
     * Gets the transactionDateTimeStamp
     *
     * @return Returns the transactionDateTimeStamp
     */
    @Override
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp
     *
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    @Override
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    /**
     * Gets the financialObject
     *
     * @return Returns the financialObject
     */
    @Override
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject
     *
     * @param financialObject The financialObject to set.
     */
    @Override
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts
     *
     * @return Returns the chartOfAccounts
     */
    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Override
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account
     *
     * @return Returns the account
     */
    @Override
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account
     *
     * @param account The account to set.
     */
    @Override
    @Deprecated
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the universityFiscalPeriod
     *
     * @return Returns the universityFiscalPeriod.
     */
    @Override
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod
     *
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     */
    @Override
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * Gets the balanceType
     *
     * @return Returns the balanceType.
     */
    @Override
    public BalanceType getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType
     *
     * @param balanceType The balanceType to set.
     */
    @Override
    @Deprecated
    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     * @return Returns the financialSystemDocumentTypeCode.
     */
    @Override
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        if ( financialSystemDocumentTypeCode == null || !StringUtils.equals(financialSystemDocumentTypeCode.getName(), financialDocumentTypeCode) ) {
            financialSystemDocumentTypeCode = null;
            if ( StringUtils.isNotBlank(financialDocumentTypeCode) ) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(financialDocumentTypeCode);
                if ( docType != null ) {
                    financialSystemDocumentTypeCode = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return financialSystemDocumentTypeCode;
    }

    /**
     * Gets the referenceFinancialSystemDocumentTypeCode attribute.
     * @return Returns the referenceFinancialSystemDocumentTypeCode.
     */
    @Override
    public DocumentTypeEBO getReferenceFinancialSystemDocumentTypeCode() {
        if ( referenceFinancialSystemDocumentTypeCode == null || !StringUtils.equals(referenceFinancialSystemDocumentTypeCode.getName(), referenceFinancialDocumentTypeCode) ) {
            referenceFinancialSystemDocumentTypeCode = null;
            if ( StringUtils.isNotBlank(referenceFinancialDocumentTypeCode) ) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(referenceFinancialDocumentTypeCode);
                if ( docType != null ) {
                    referenceFinancialSystemDocumentTypeCode = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return referenceFinancialSystemDocumentTypeCode;
    }

    /**
     * Gets the financialObjectType
     *
     * @return Returns the financialObjectType.
     */
    @Override
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType
     *
     * @param financialObjectType The financialObjectType to set.
     */
    @Override
    @Deprecated
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Gets the financialSubObject
     *
     * @return Returns the financialSubObject.
     */
    @Override
    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject
     *
     * @param financialSubObject The financialSubObject to set.
     */
    @Override
    @Deprecated
    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the option
     *
     * @return Returns the option.
     */
    @Override
    public SystemOptions getOption() {
        return option;
    }

    /**
     * Sets the option
     *
     * @param option The option to set.
     */
    @Override
    @Deprecated
    public void setOption(SystemOptions option) {
        this.option = option;
    }

    /**
     * Gets the payrollEndDateFiscalPeriod
     *
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    @Override
    public AccountingPeriod getPayrollEndDateFiscalPeriod() {
        return payrollEndDateFiscalPeriod;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod
     *
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    @Override
    @Deprecated
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
        this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
    }

    /**
     * Gets the project
     *
     * @return Returns the project.
     */
    @Override
    public ProjectCode getProject() {
        return project;
    }

    /**
     * Sets the project
     *
     * @param project The project to set.
     */
    @Override
    @Deprecated
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * Gets the referenceOriginationCode
     *
     * @return Returns the referenceOriginationCode.
     */
    @Override
    public OriginationCode getReferenceOriginationCode() {
        return referenceOriginationCode;
    }

    /**
     * Sets the referenceOriginationCode
     *
     * @param referenceOriginationCode The referenceOriginationCode to set.
     */
    @Override
    @Deprecated
    public void setReferenceOriginationCode(OriginationCode referenceOriginationCode) {
        this.referenceOriginationCode = referenceOriginationCode;
    }

    /**
     * Gets the subAccount
     *
     * @return Returns the subAccount.
     */
    @Override
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount
     *
     * @param subAccount The subAccount to set.
     */
    @Override
    @Deprecated
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the financialSystemOrigination
     *
     * @return Returns the financialSystemOrigination.
     */
    @Override
    public OriginationCode getFinancialSystemOrigination() {
        return financialSystemOrigination;
    }

    /**
     * Sets the financialSystemOrigination
     *
     * @param financialSystemOrigination The financialSystemOrigination to set.
     */
    @Override
    @Deprecated
    public void setFinancialSystemOrigination(OriginationCode financialSystemOrigination) {
        this.financialSystemOrigination = financialSystemOrigination;
    }

    /**
     * @see org.kuali.kfs.bo.LaborLedgerEntry#getLaborLedgerObject()
     */
    @Override
    public LaborLedgerObject getLaborLedgerObject() {
        return this.laborObject;
    }

    /**
     * @see org.kuali.kfs.bo.LaborLedgerEntry#setLaborLedgerObject(org.kuali.kfs.bo.LaborLedgerObject)
     */
    @Override
    @Deprecated
    public void setLaborLedgerObject(LaborLedgerObject laborLedgerObject) {
        this.laborObject = (LaborObject) laborLedgerObject;
    }

    /**
     * Gets the laborObject attribute.
     *
     * @return Returns the laborObject.
     */
    public LaborObject getLaborObject() {
        return laborObject;
    }

    /**
     * Sets the laborObject attribute value.
     *
     * @param laborObject The laborObject to set.
     */
    @Deprecated
    public void setLaborObject(LaborObject laborObject) {
        this.laborObject = laborObject;
    }

    /**
     * @return the employee associated with this record
     */
    public Person getEmployee() {
        if (employee == null || !StringUtils.equals(employee.getEmployeeId(), getEmplid())) {
            employee = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(getEmplid());
            if (employee == null) {
                final Class<? extends Person> employeeClass = SpringContext.getBean(PersonService.class).getPersonImplementationClass();
                try {
                    employee = employeeClass.newInstance();
                }
                catch (InstantiationException ie) {
                    throw new RuntimeException("Could not instantiate empty Person object", ie);
                }
                catch (IllegalAccessException iae) {
                    throw new RuntimeException("Could not instantiate empty Person object", iae);
                }
            }
        }
        return employee;
    }

    /**
     * Sets the employee.
     * @param employee the employee to set
     */
    @Deprecated
    public void setEmployee(Person employee) {
        this.employee = employee;
    }

    /**
     * construct the key list of the business object.
     *
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("financialBalanceTypeCode", this.financialBalanceTypeCode);
        m.put("financialObjectTypeCode", this.financialObjectTypeCode);
        m.put("universityFiscalPeriodCode", this.universityFiscalPeriodCode);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.transactionLedgerEntrySequenceNumber != null) {
            m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        }

        return m;
    }
}
