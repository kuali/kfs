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
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.Options;
import org.kuali.core.bo.OriginationCode;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.document.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class LaborOriginEntry extends OriginEntry {

    private String chartOfAccountsCode;
    private String positionNumber;
    private Date transactionPostingDate;
    private Date payPeriodEndDate;
    private BigDecimal transactionTotalHours;
    private Integer payrollEndDateFiscalYear;
    private String payrollEndDateFiscalPeriodCode;
    private String financialDocumentApprovedCode;
    private String transactionEntryOffsetCode;
    private Date transactionEntryProcessedTimestamp;
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
    private Date transactionDateTimeStamp;

    private ObjectCode laborLedgerOriginalFinancialObject;
    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private Chart laborLedgerOriginalChartOfAccounts;
    private Account laborLedgerOriginalAccount;
    private ObjectType financialObjectType;
    private BalanceTyp balanceType;
    private AccountingPeriod universityFiscalPeriod;
    private DocumentType documentType;
    private SubAccount subAccount;
    private SubObjCd financialSubObject;
    private ProjectCode project;
    private DocumentHeader financialDocument;
    private DocumentType referenceFinancialDocumentType;
    private OriginationCode referenceFinancialSystemOrigination;
    private AccountingPeriod payrollEndDateFiscalPeriod;
    private SubAccount laborLedgerOriginalSubAccount;
    private SubObjCd laborLedgerOriginalFinancialSubObject;
    private Options option;
    private UniversityDate reversalDate;
    private OriginEntryGroup originEntryGroup;
    private OriginationCode financialSystemOrigination;
    
    /**
     * Default constructor.
     */
    public LaborOriginEntry() {

    }

    

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() { 
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     * 
     */
    public String getPositionNumber() { 
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     * 
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }


    /**
     * Gets the projectCode attribute.
     * 
     * @return Returns the projectCode
     * 
     */

    /**
     * Gets the transactionPostingDate attribute.
     * 
     * @return Returns the transactionPostingDate
     * 
     */
    public Date getTransactionPostingDate() { 
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate attribute.
     * 
     * @param transactionPostingDate The transactionPostingDate to set.
     * 
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }


    /**
     * Gets the payPeriodEndDate attribute.
     * 
     * @return Returns the payPeriodEndDate
     * 
     */
    public Date getPayPeriodEndDate() { 
        return payPeriodEndDate;
    }

    /**
     * Sets the payPeriodEndDate attribute.
     * 
     * @param payPeriodEndDate The payPeriodEndDate to set.
     * 
     */
    public void setPayPeriodEndDate(Date payPeriodEndDate) {
        this.payPeriodEndDate = payPeriodEndDate;
    }


    /**
     * Gets the transactionTotalHours attribute.
     * 
     * @return Returns the transactionTotalHours
     * 
     */
    public BigDecimal getTransactionTotalHours() { 
        return transactionTotalHours;
    }

    /**
     * Sets the transactionTotalHours attribute.
     * 
     * @param transactionTotalHours The transactionTotalHours to set.
     * 
     */
    public void setTransactionTotalHours(BigDecimal transactionTotalHours) {
        this.transactionTotalHours = transactionTotalHours;
    }


    /**
     * Gets the payrollEndDateFiscalYear attribute.
     * 
     * @return Returns the payrollEndDateFiscalYear
     * 
     */
    public Integer getPayrollEndDateFiscalYear() { 
        return payrollEndDateFiscalYear;
    }

    /**
     * Sets the payrollEndDateFiscalYear attribute.
     * 
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     * 
     */
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }


    /**
     * Gets the payrollEndDateFiscalPeriodCode attribute.
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode
     * 
     */
    public String getPayrollEndDateFiscalPeriodCode() { 
        return payrollEndDateFiscalPeriodCode;
    }

    /**
     * Sets the payrollEndDateFiscalPeriodCode attribute.
     * 
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     * 
     */
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }


    /**
     * Gets the financialDocumentApprovedCode attribute.
     * 
     * @return Returns the financialDocumentApprovedCode
     * 
     */
    public String getFinancialDocumentApprovedCode() { 
        return financialDocumentApprovedCode;
    }

    /**
     * Sets the financialDocumentApprovedCode attribute.
     * 
     * @param financialDocumentApprovedCode The financialDocumentApprovedCode to set.
     * 
     */
    public void setFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        this.financialDocumentApprovedCode = financialDocumentApprovedCode;
    }


    /**
     * Gets the transactionEntryOffsetCode attribute.
     * 
     * @return Returns the transactionEntryOffsetCode
     * 
     */
    public String getTransactionEntryOffsetCode() { 
        return transactionEntryOffsetCode;
    }

    /**
     * Sets the transactionEntryOffsetCode attribute.
     * 
     * @param transactionEntryOffsetCode The transactionEntryOffsetCode to set.
     * 
     */
    public void setTransactionEntryOffsetCode(String transactionEntryOffsetCode) {
        this.transactionEntryOffsetCode = transactionEntryOffsetCode;
    }


    /**
     * Gets the transactionEntryProcessedTimestamp attribute.
     * 
     * @return Returns the transactionEntryProcessedTimestamp
     * 
     */
    public Date getTransactionEntryProcessedTimestamp() { 
        return transactionEntryProcessedTimestamp;
    }

    /**
     * Sets the transactionEntryProcessedTimestamp attribute.
     * 
     * @param transactionEntryProcessedTimestamp The transactionEntryProcessedTimestamp to set.
     * 
     */
    public void setTransactionEntryProcessedTimestamp(Date transactionEntryProcessedTimestamp) {
        this.transactionEntryProcessedTimestamp = transactionEntryProcessedTimestamp;
    }


    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     * 
     */
    public String getEmplid() { 
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     * 
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }


    /**
     * Gets the employeeRecord attribute.
     * 
     * @return Returns the employeeRecord
     * 
     */
    public Integer getEmployeeRecord() { 
        return employeeRecord;
    }

    /**
     * Sets the employeeRecord attribute.
     * 
     * @param employeeRecord The employeeRecord to set.
     * 
     */
    public void setEmployeeRecord(Integer employeeRecord) {
        this.employeeRecord = employeeRecord;
    }


    /**
     * Gets the earnCode attribute.
     * 
     * @return Returns the earnCode
     * 
     */
    public String getEarnCode() { 
        return earnCode;
    }

    /**
     * Sets the earnCode attribute.
     * 
     * @param earnCode The earnCode to set.
     * 
     */
    public void setEarnCode(String earnCode) {
        this.earnCode = earnCode;
    }


    /**
     * Gets the payGroup attribute.
     * 
     * @return Returns the payGroup
     * 
     */
    public String getPayGroup() { 
        return payGroup;
    }

    /**
     * Sets the payGroup attribute.
     * 
     * @param payGroup The payGroup to set.
     * 
     */
    public void setPayGroup(String payGroup) {
        this.payGroup = payGroup;
    }


    /**
     * Gets the salaryAdministrationPlan attribute.
     * 
     * @return Returns the salaryAdministrationPlan
     * 
     */
    public String getSalaryAdministrationPlan() { 
        return salaryAdministrationPlan;
    }

    /**
     * Sets the salaryAdministrationPlan attribute.
     * 
     * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
     * 
     */
    public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
        this.salaryAdministrationPlan = salaryAdministrationPlan;
    }


    /**
     * Gets the grade attribute.
     * 
     * @return Returns the grade
     * 
     */
    public String getGrade() { 
        return grade;
    }

    /**
     * Sets the grade attribute.
     * 
     * @param grade The grade to set.
     * 
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }


    /**
     * Gets the runIdentifier attribute.
     * 
     * @return Returns the runIdentifier
     * 
     */
    public String getRunIdentifier() { 
        return runIdentifier;
    }

    /**
     * Sets the runIdentifier attribute.
     * 
     * @param runIdentifier The runIdentifier to set.
     * 
     */
    public void setRunIdentifier(String runIdentifier) {
        this.runIdentifier = runIdentifier;
    }
    
    /**
     * Gets the laborLedgerOriginalChartOfAccountsCode attribute.
     * 
     * @return Returns the laborLedgerOriginalChartOfAccountsCode
     * 
     */
    public String getLaborLedgerOriginalChartOfAccountsCode() { 
        return laborLedgerOriginalChartOfAccountsCode;
    }

    /**
     * Sets the laborLedgerOriginalChartOfAccountsCode attribute.
     * 
     * @param laborLedgerOriginalChartOfAccountsCode The laborLedgerOriginalChartOfAccountsCode to set.
     * 
     */
    public void setLaborLedgerOriginalChartOfAccountsCode(String laborLedgerOriginalChartOfAccountsCode) {
        this.laborLedgerOriginalChartOfAccountsCode = laborLedgerOriginalChartOfAccountsCode;
    }

    

    /**
     * Gets the laborLedgerOriginalAccountNumber attribute.
     * 
     * @return Returns the laborLedgerOriginalAccountNumber
     * 
     */
    public String getLaborLedgerOriginalAccountNumber() { 
        return laborLedgerOriginalAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalAccountNumber attribute.
     * 
     * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
     * 
     */
    public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber) {
        this.laborLedgerOriginalAccountNumber = laborLedgerOriginalAccountNumber;
    }


    /**
     * Gets the laborLedgerOriginalSubAccountNumber attribute.
     * 
     * @return Returns the laborLedgerOriginalSubAccountNumber
     * 
     */
    public String getLaborLedgerOriginalSubAccountNumber() { 
        return laborLedgerOriginalSubAccountNumber;
    }

    /**
     * Sets the laborLedgerOriginalSubAccountNumber attribute.
     * 
     * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
     * 
     */
    public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber) {
        this.laborLedgerOriginalSubAccountNumber = laborLedgerOriginalSubAccountNumber;
    }


    /**
     * Gets the laborLedgerOriginalFinancialObjectCode attribute.
     * 
     * @return Returns the laborLedgerOriginalFinancialObjectCode
     * 
     */
    public String getLaborLedgerOriginalFinancialObjectCode() { 
        return laborLedgerOriginalFinancialObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialObjectCode attribute.
     * 
     * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
     * 
     */
    public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode) {
        this.laborLedgerOriginalFinancialObjectCode = laborLedgerOriginalFinancialObjectCode;
    }


    /**
     * Gets the laborLedgerOriginalFinancialSubObjectCode attribute.
     * 
     * @return Returns the laborLedgerOriginalFinancialSubObjectCode
     * 
     */
    public String getLaborLedgerOriginalFinancialSubObjectCode() { 
        return laborLedgerOriginalFinancialSubObjectCode;
    }

    /**
     * Sets the laborLedgerOriginalFinancialSubObjectCode attribute.
     * 
     * @param laborLedgerOriginalFinancialSubObjectCode The laborLedgerOriginalFinancialSubObjectCode to set.
     * 
     */
    public void setLaborLedgerOriginalFinancialSubObjectCode(String laborLedgerOriginalFinancialSubObjectCode) {
        this.laborLedgerOriginalFinancialSubObjectCode = laborLedgerOriginalFinancialSubObjectCode;
    }


    /**
     * Gets the hrmsCompany attribute.
     * 
     * @return Returns the hrmsCompany
     * 
     */
    public String getHrmsCompany() { 
        return hrmsCompany;
    }

    /**
     * Sets the hrmsCompany attribute.
     * 
     * @param hrmsCompany The hrmsCompany to set.
     * 
     */
    public void setHrmsCompany(String hrmsCompany) {
        this.hrmsCompany = hrmsCompany;
    }


    /**
     * Gets the setid attribute.
     * 
     * @return Returns the setid
     * 
     */
    public String getSetid() { 
        return setid;
    }

    /**
     * Sets the setid attribute.
     * 
     * @param setid The setid to set.
     * 
     */
    public void setSetid(String setid) {
        this.setid = setid;
    }


    /**
     * Gets the transactionDateTimeStamp attribute.
     * 
     * @return Returns the transactionDateTimeStamp
     * 
     */
    public Date getTransactionDateTimeStamp() { 
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp attribute.
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     * 
     */
    public void setTransactionDateTimeStamp(Date transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }


    /**
     * Gets the laborLedgerOriginalFinancialObject attribute.
     * 
     * @return Returns the laborLedgerOriginalFinancialObject
     * 
     */
    public ObjectCode getLaborLedgerOriginalFinancialObject() { 
        return laborLedgerOriginalFinancialObject;
    }

    /**
     * Sets the laborLedgerOriginalFinancialObject attribute.
     * 
     * @param laborLedgerOriginalFinancialObject The laborLedgerOriginalFinancialObject to set.
     * @deprecated
     */
    public void setLaborLedgerOriginalFinancialObject(ObjectCode laborLedgerOriginalFinancialObject) {
        this.laborLedgerOriginalFinancialObject = laborLedgerOriginalFinancialObject;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
     * 
     */
    public ObjectCode getFinancialObject() { 
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     * 
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() { 
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     * 
     */
    public Account getAccount() { 
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the laborLedgerOriginalChartOfAccounts attribute.
     * 
     * @return Returns the laborLedgerOriginalChartOfAccounts
     * 
     */
    public Chart getLaborLedgerOriginalChartOfAccounts() { 
        return laborLedgerOriginalChartOfAccounts;
    }

    /**
     * Sets the laborLedgerOriginalChartOfAccounts attribute.
     * 
     * @param laborLedgerOriginalChartOfAccounts The laborLedgerOriginalChartOfAccounts to set.
     * @deprecated
     */
    public void setLaborLedgerOriginalChartOfAccounts(Chart laborLedgerOriginalChartOfAccounts) {
        this.laborLedgerOriginalChartOfAccounts = laborLedgerOriginalChartOfAccounts;
    }

    /**
     * Gets the laborLedgerOriginalAccount attribute.
     * 
     * @return Returns the laborLedgerOriginalAccount
     * 
     */
    public Account getLaborLedgerOriginalAccount() { 
        return laborLedgerOriginalAccount;
    }

    /**
     * Sets the laborLedgerOriginalAccount attribute.
     * 
     * @param laborLedgerOriginalAccount The laborLedgerOriginalAccount to set.
     * @deprecated
     */
    public void setLaborLedgerOriginalAccount(Account laborLedgerOriginalAccount) {
        this.laborLedgerOriginalAccount = laborLedgerOriginalAccount;
    }

    /**
     * Gets the balanceType attribute. 
     * @return Returns the balanceType.
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * @param balanceType The balanceType to set.
     * @deprecated
     */
    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the documentType attribute. 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * @param documentType The documentType to set.
     * @deprecated
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the financialDocument attribute. 
     * @return Returns the financialDocument.
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute value.
     * @param financialDocument The financialDocument to set.
     * @deprecated
     */
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the financialObjectType attribute. 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * @param financialObjectType The financialObjectType to set.
     * @deprecated
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Gets the financialSubObject attribute. 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the laborLedgerOriginalFinancialSubObject attribute. 
     * @return Returns the laborLedgerOriginalFinancialSubObject.
     */
    public SubObjCd getLaborLedgerOriginalFinancialSubObject() {
        return laborLedgerOriginalFinancialSubObject;
    }

    /**
     * Sets the laborLedgerOriginalFinancialSubObject attribute value.
     * @param laborLedgerOriginalFinancialSubObject The laborLedgerOriginalFinancialSubObject to set.
     * @deprecated
     */
    public void setLaborLedgerOriginalFinancialSubObject(SubObjCd laborLedgerOriginalFinancialSubObject) {
        this.laborLedgerOriginalFinancialSubObject = laborLedgerOriginalFinancialSubObject;
    }

    /**
     * Gets the laborLedgerOriginalSubAccount attribute. 
     * @return Returns the laborLedgerOriginalSubAccount.
     */
    public SubAccount getLaborLedgerOriginalSubAccount() {
        return laborLedgerOriginalSubAccount;
    }

    /**
     * Sets the laborLedgerOriginalSubAccount attribute value.
     * @param laborLedgerOriginalSubAccount The laborLedgerOriginalSubAccount to set.
     * @deprecated
     */
    public void setLaborLedgerOriginalSubAccount(SubAccount laborLedgerOriginalSubAccount) {
        this.laborLedgerOriginalSubAccount = laborLedgerOriginalSubAccount;
    }

    /**
     * Gets the option attribute. 
     * @return Returns the option.
     */
    public Options getOption() {
        return option;
    }

    /**
     * Sets the option attribute value.
     * @param option The option to set.
     * @deprecated
     */
    public void setOption(Options option) {
        this.option = option;
    }

    /**
     * Gets the payrollEndDateFiscalPeriod attribute. 
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    public AccountingPeriod getPayrollEndDateFiscalPeriod() {
        return payrollEndDateFiscalPeriod;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod attribute value.
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     * @deprecated
     */
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
        this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
    }

    /**
     * Gets the project attribute. 
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * Sets the project attribute value.
     * @param project The project to set.
     * @deprecated
     */
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * Gets the referenceFinancialDocumentType attribute. 
     * @return Returns the referenceFinancialDocumentType.
     */
    public DocumentType getReferenceFinancialDocumentType() {
        return referenceFinancialDocumentType;
    }

    /**
     * Sets the referenceFinancialDocumentType attribute value.
     * @param referenceFinancialDocumentType The referenceFinancialDocumentType to set.
     * @deprecated
     */
    public void setReferenceFinancialDocumentType(DocumentType referenceFinancialDocumentType) {
        this.referenceFinancialDocumentType = referenceFinancialDocumentType;
    }

    /**
     * Gets the referenceFinancialSystemOrigination attribute. 
     * @return Returns the referenceFinancialSystemOrigination.
     */
    public OriginationCode getReferenceFinancialSystemOrigination() {
        return referenceFinancialSystemOrigination;
    }

    /**
     * Sets the referenceFinancialSystemOrigination attribute value.
     * @param referenceFinancialSystemOrigination The referenceFinancialSystemOrigination to set.
     * @deprecated
     */
    public void setReferenceFinancialSystemOrigination(OriginationCode referenceFinancialSystemOrigination) {
        this.referenceFinancialSystemOrigination = referenceFinancialSystemOrigination;
    }

    /**
     * Gets the reversalDate attribute. 
     * @return Returns the reversalDate.
     */
    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    /**
     * Sets the reversalDate attribute value.
     * @param reversalDate The reversalDate to set.
     * @deprecated
     */
    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the universityFiscalPeriod attribute. 
     * @return Returns the universityFiscalPeriod.
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod attribute value.
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     * @deprecated
     */
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }    

    /**
     * Gets the originEntryGroup. 
     * @return Returns the originEntryGroup.
     */    
    public OriginEntryGroup getOriginEntryGroup() {
        return originEntryGroup;
    }

    /**
     * Sets the originEntryGroup attribute value.
     * @param originEntryGroup The originEntryGroup to set.
     * @deprecated
     */    
    public void setOriginEntryGroup(OriginEntryGroup originEntryGroup) {
        this.originEntryGroup = originEntryGroup;
    }    
    
    /**
     * Gets the financialSystemOrigination attribute. 
     * @return Returns the financialSystemOrigination.
     */
    public OriginationCode getFinancialSystemOrigination() {
        return financialSystemOrigination;
    }

    /**
     * Sets the financialSystemOrigination attribute value.
     * @param financialSystemOrigination The financialSystemOrigination to set.
     * @deprecated
     */
    public void setFinancialSystemOrigination(OriginationCode financialSystemOrigination) {
        this.financialSystemOrigination = financialSystemOrigination;
    }

  /*  *//**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     *//*
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (super.getEntryId() != null) {
            m.put("entryId", super.getEntryId().toString());
        }
        return m;
    }
    */
    
}

