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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;

/**
 * Labor business object for LaborOriginEntry.
 */
public class LaborOriginEntry extends OriginEntryFull implements OriginEntryInformation, LaborTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborOriginEntry.class);
    private static LaborOriginEntryFieldUtil laborOriginEntryFieldUtil;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String positionNumber;
    private Date transactionPostingDate;
    private Date payPeriodEndDate;
    private BigDecimal transactionTotalHours;
    private Integer payrollEndDateFiscalYear;
    private String payrollEndDateFiscalPeriodCode;
    private String financialDocumentApprovedCode;
    private String transactionEntryOffsetCode;
    private Timestamp transactionEntryProcessedTimestamp;
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
    private DocumentTypeEBO  referenceFinancialSystemDocumentTypeCode;
    private OriginationCode referenceFinancialSystemOrigination;
    private AccountingPeriod payrollEndDateFiscalPeriod;

    public LaborOriginEntry(LaborLedgerPendingEntry pendingEntry){

        accountNumber = pendingEntry.getAccountNumber();
        documentNumber = pendingEntry.getDocumentNumber();
        referenceFinancialDocumentNumber = pendingEntry.getReferenceFinancialDocumentNumber();
        referenceFinancialDocumentTypeCode = pendingEntry.getReferenceFinancialDocumentTypeCode();
        financialDocumentReversalDate = pendingEntry.getFinancialDocumentReversalDate();
        financialDocumentTypeCode = pendingEntry.getFinancialDocumentTypeCode();
        financialBalanceTypeCode = pendingEntry.getFinancialBalanceTypeCode();
        chartOfAccountsCode = pendingEntry.getChartOfAccountsCode();
        financialObjectTypeCode = pendingEntry.getFinancialObjectTypeCode();
        financialObjectCode = pendingEntry.getFinancialObjectCode();
        financialSubObjectCode = pendingEntry.getFinancialSubObjectCode();
        financialSystemOriginationCode = pendingEntry.getFinancialSystemOriginationCode();
        referenceFinancialSystemOriginationCode = pendingEntry.getReferenceFinancialSystemOriginationCode();
        organizationDocumentNumber = pendingEntry.getOrganizationDocumentNumber();
        organizationReferenceId = pendingEntry.getOrganizationReferenceId();
        projectCode = pendingEntry.getProjectCode();
        subAccountNumber = pendingEntry.getSubAccountNumber();
        transactionDate = pendingEntry.getTransactionDate();
        transactionDebitCreditCode = pendingEntry.getTransactionDebitCreditCode();
        transactionEncumbranceUpdateCode = pendingEntry.getTransactionEncumbranceUpdateCode();
        transactionLedgerEntrySequenceNumber = pendingEntry.getTransactionLedgerEntrySequenceNumber();
        transactionLedgerEntryAmount = pendingEntry.getTransactionLedgerEntryAmount();
        transactionLedgerEntryDescription = pendingEntry.getTransactionLedgerEntryDescription();
        universityFiscalPeriodCode = pendingEntry.getUniversityFiscalPeriodCode();
        universityFiscalYear = pendingEntry.getUniversityFiscalYear();

        //TODO:- need to check

        positionNumber = pendingEntry.getPositionNumber();
        transactionPostingDate = pendingEntry.getTransactionPostingDate();
        payPeriodEndDate = pendingEntry.getPayPeriodEndDate();
        transactionTotalHours = pendingEntry.getTransactionTotalHours();
        payrollEndDateFiscalYear = pendingEntry.getPayrollEndDateFiscalYear();
        payrollEndDateFiscalPeriodCode = pendingEntry.getPayrollEndDateFiscalPeriodCode();
        emplid = pendingEntry.getEmplid();
        employeeRecord = pendingEntry.getEmployeeRecord();
        earnCode = pendingEntry.getEarnCode();
        payGroup = pendingEntry.getPayGroup();
        salaryAdministrationPlan = pendingEntry.getSalaryAdministrationPlan();
        grade = pendingEntry.getGrade();
        runIdentifier = pendingEntry.getRunIdentifier();
        laborLedgerOriginalChartOfAccountsCode = pendingEntry.getLaborLedgerOriginalChartOfAccountsCode();
        laborLedgerOriginalAccountNumber = pendingEntry.getLaborLedgerOriginalAccountNumber();
        laborLedgerOriginalSubAccountNumber = pendingEntry.getLaborLedgerOriginalSubAccountNumber();
        laborLedgerOriginalFinancialObjectCode = pendingEntry.getLaborLedgerOriginalFinancialObjectCode();
        laborLedgerOriginalFinancialSubObjectCode = pendingEntry.getLaborLedgerOriginalFinancialSubObjectCode();
        hrmsCompany = pendingEntry.getHrmsCompany();
        setid = pendingEntry.getSetid();
        //TODO:- timestamp????
        //transactionDateTimeStamp = pendingEntry.getTransactionDateTimeStamp();
        transactionEntryOffsetCode = pendingEntry.getTransactionEntryOffsetCode();
        payrollEndDateFiscalPeriod = pendingEntry.getPayrollEndDateFiscalPeriod();

        //TODO:- don't need it?
        //reversalDate = pendingEntry.getReversalDate();

        //TODO:- check: no positionData in laborOriginEntry
        //positionData = pendingEntry.getPositionData();

    }

    /**
     * Constructor with financialDocumentTypeCode and financialSystemOriginationCode.
     *
     * @param financialDocumentTypeCode
     * @param financialSystemOriginationCode
     */
    public LaborOriginEntry(String financialDocumentTypeCode, String financialSystemOriginationCode) {
        super(financialDocumentTypeCode, financialSystemOriginationCode);
    }

    /**
     * Default constructor.
     */
    public LaborOriginEntry() {
        this(null, null);
    }

    /**
     * Constructor with laborTransaction
     *
     * @param t
     */
    public LaborOriginEntry(LaborTransaction t) {
        this();
        copyFieldsFromTransaction(t);
        setPositionNumber(t.getPositionNumber());
        setTransactionPostingDate(t.getTransactionPostingDate());
        setPayPeriodEndDate(t.getPayPeriodEndDate());
        setTransactionTotalHours(t.getTransactionTotalHours());
        setPayrollEndDateFiscalYear(t.getPayrollEndDateFiscalYear());
        setPayrollEndDateFiscalPeriodCode(t.getPayrollEndDateFiscalPeriodCode());
        setFinancialDocumentApprovedCode(t.getFinancialDocumentApprovedCode());
        setTransactionEntryOffsetCode(t.getTransactionEntryOffsetCode());
        setTransactionEntryProcessedTimestamp(t.getTransactionEntryProcessedTimestamp());
        setEmplid(t.getEmplid());
        setEmployeeRecord(t.getEmployeeRecord());
        setEarnCode(t.getEarnCode());
        setPayGroup(t.getPayGroup());
        setSalaryAdministrationPlan(t.getSalaryAdministrationPlan());
        setGrade(t.getGrade());
        setRunIdentifier(t.getRunIdentifier());
        setLaborLedgerOriginalChartOfAccountsCode(t.getLaborLedgerOriginalChartOfAccountsCode());
        setLaborLedgerOriginalAccountNumber(t.getLaborLedgerOriginalAccountNumber());
        setLaborLedgerOriginalSubAccountNumber(t.getLaborLedgerOriginalSubAccountNumber());
        setLaborLedgerOriginalFinancialObjectCode(t.getLaborLedgerOriginalFinancialObjectCode());
        setLaborLedgerOriginalFinancialSubObjectCode(t.getLaborLedgerOriginalFinancialSubObjectCode());
        setHrmsCompany(t.getHrmsCompany());
        setSetid(t.getSetid());
        this.referenceFinancialSystemDocumentTypeCode = t.getReferenceFinancialSystemDocumentTypeCode();
        setReferenceFinancialSystemOrigination(t.getReferenceFinancialSystemOrigination());
        setPayrollEndDateFiscalPeriod(t.getPayrollEndDateFiscalPeriod());
    }

    /**
     * Constructor with string line
     *
     * @param line
     */
    public LaborOriginEntry(String line) {
        setFromTextFileForBatch(line, 0);
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
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
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
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the financialDocumentApprovedCode
     *
     * @return Returns the financialDocumentApprovedCode
     */
    @Override
    public String getFinancialDocumentApprovedCode() {
        return financialDocumentApprovedCode;
    }

    /**
     * Sets the financialDocumentApprovedCode
     *
     * @param financialDocumentApprovedCode The financialDocumentApprovedCode to set.
     */
    public void setFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        this.financialDocumentApprovedCode = financialDocumentApprovedCode;
    }

    /**
     * Gets the transactionEntryOffsetCode
     *
     * @return Returns the transactionEntryOffsetCode
     */
    @Override
    public String getTransactionEntryOffsetCode() {
        return transactionEntryOffsetCode;
    }

    /**
     * Sets the transactionEntryOffsetCode
     *
     * @param transactionEntryOffsetCode The transactionEntryOffsetCode to set.
     */
    public void setTransactionEntryOffsetCode(String transactionEntryOffsetCode) {
        this.transactionEntryOffsetCode = transactionEntryOffsetCode;
    }

    /**
     * Gets the transactionEntryProcessedTimestamp
     *
     * @return Returns the transactionEntryProcessedTimestamp
     */
    @Override
    public Timestamp getTransactionEntryProcessedTimestamp() {
        return transactionEntryProcessedTimestamp;
    }

    /**
     * Sets the transactionEntryProcessedTimestamp
     *
     * @param transactionEntryProcessedTimestamp The transactionEntryProcessedTimestamp to set.
     */
    public void setTransactionEntryProcessedTimestamp(Timestamp transactionEntryProcessedTimestamp) {
        this.transactionEntryProcessedTimestamp = transactionEntryProcessedTimestamp;
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
    public void setSetid(String setid) {
        this.setid = setid;
    }

    /**
     * Gets the transactionDateTimeStamp
     *
     * @return Returns the transactionDateTimeStamp
     */
    public Date getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp
     *
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Date transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
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
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
        this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
    }

    /**
     * Gets the referenceFinancialDocumentType
     *
     * @return Returns the referenceFinancialDocumentType.
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
     * Gets the referenceFinancialSystemOrigination
     *
     * @return Returns the referenceFinancialSystemOrigination.
     */
    @Override
    public OriginationCode getReferenceFinancialSystemOrigination() {
        return referenceFinancialSystemOrigination;
    }

    /**
     * Sets the referenceFinancialSystemOrigination
     *
     * @param referenceFinancialSystemOrigination The referenceFinancialSystemOrigination to set.
     */
    public void setReferenceFinancialSystemOrigination(OriginationCode referenceFinancialSystemOrigination) {
        this.referenceFinancialSystemOrigination = referenceFinancialSystemOrigination;
    }

    /**
     * Sets transactionTotalHours to null.
     */
    public void clearTransactionTotalHours() {
        this.transactionTotalHours = null;
    }

    /**
     * Get lines from string
     */
    @Override
    public String getLine() {
        StringBuilder sb = new StringBuilder();
        Map<String, Integer> lMap = getLaborOriginEntryFieldUtil().getFieldLengthMap();
        Map<String, Integer> pMap = getLaborOriginEntryFieldUtil().getFieldBeginningPositionMap();
        int entryLength = pMap.get(LaborPropertyConstants.SET_ID) +  lMap.get(LaborPropertyConstants.SET_ID);

        if (universityFiscalYear == null) {
            sb.append(GeneralLedgerConstants.getSpaceUniversityFiscalYear());
        }
        else {
            sb.append(universityFiscalYear);
        }

        sb.append(getField(lMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), chartOfAccountsCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.ACCOUNT_NUMBER), accountNumber));
        sb.append(getField(lMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER), subAccountNumber));
        sb.append(getField(lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE), financialObjectCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE), financialSubObjectCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), financialBalanceTypeCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE), financialObjectTypeCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE), universityFiscalPeriodCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), financialDocumentTypeCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE), financialSystemOriginationCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.DOCUMENT_NUMBER), documentNumber));

        String seqNum ="";
        if (transactionLedgerEntrySequenceNumber != null) {
            seqNum = transactionLedgerEntrySequenceNumber.toString();
        }
        // Format to a length of 5
        sb.append(StringUtils.leftPad(seqNum.trim(), lMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), "0"));

        // Labor Specified fields
        sb.append(getField(lMap.get(KFSPropertyConstants.POSITION_NUMBER), positionNumber));
        sb.append(getField(lMap.get(KFSPropertyConstants.PROJECT_CODE), projectCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC), transactionLedgerEntryDescription));

        if (transactionLedgerEntryAmount == null) {
            sb.append(GeneralLedgerConstants.getZeroTransactionLedgerEntryAmount());
        }
        else {
            String a = transactionLedgerEntryAmount.abs().toString();
            if (transactionLedgerEntryAmount.isNegative()) {
                sb.append("-");
            } else {
                sb.append("+");
            }
            sb.append(GeneralLedgerConstants.getZeroTransactionLedgerEntryAmount().substring(1, lMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT) - a.length()));
            sb.append(a);
        }

        sb.append(getField(lMap.get(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE), transactionDebitCreditCode));
        sb.append(formatDate(transactionDate));
        sb.append(getField(lMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER), organizationDocumentNumber));
        sb.append(getField(lMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID), organizationReferenceId));
        sb.append(getField(lMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE), referenceFinancialDocumentTypeCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE), referenceFinancialSystemOriginationCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR), referenceFinancialDocumentNumber));
        sb.append(formatDate(financialDocumentReversalDate));
        sb.append(getField(lMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD), transactionEncumbranceUpdateCode));
        sb.append(formatDate(transactionPostingDate));
        sb.append(formatDate(payPeriodEndDate));

        if (transactionTotalHours == null) {
            sb.append(StringUtils.rightPad("", lMap.get(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS), " "));
        }
        else {
            String totalhour = getField(lMap.get(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS), transactionTotalHours.toString());
            sb.append(StringUtils.leftPad(totalhour.trim(), lMap.get(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS), " "));
        }

        if (payrollEndDateFiscalYear == null) {
            sb.append(StringUtils.rightPad("", lMap.get(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR), " "));
        }
        else {
            sb.append(getField(lMap.get(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR), payrollEndDateFiscalYear.toString()));
        }

        sb.append(getField(lMap.get(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE), payrollEndDateFiscalPeriodCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.EMPLID), emplid));

        if (employeeRecord == null) {
            sb.append(StringUtils.rightPad("", lMap.get(KFSPropertyConstants.EMPLOYEE_RECORD), " "));
        }
        else {
            String empRecord = getField(lMap.get(KFSPropertyConstants.EMPLOYEE_RECORD), employeeRecord.toString());
            sb.append(StringUtils.leftPad(empRecord.trim(), lMap.get(KFSPropertyConstants.EMPLOYEE_RECORD), " "));
        }

        sb.append(getField(lMap.get(KFSPropertyConstants.EARN_CODE), earnCode));
        sb.append(getField(lMap.get(KFSPropertyConstants.PAY_GROUP), payGroup));
        sb.append(getField(lMap.get(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN), salaryAdministrationPlan));
        sb.append(getField(lMap.get(LaborPropertyConstants.GRADE), grade));
        sb.append(getField(lMap.get(LaborPropertyConstants.RUN_IDENTIFIER), runIdentifier));
        sb.append(getField(lMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE), laborLedgerOriginalChartOfAccountsCode));
        sb.append(getField(lMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER), laborLedgerOriginalAccountNumber));
        sb.append(getField(lMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER), laborLedgerOriginalSubAccountNumber));
        sb.append(getField(lMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE), laborLedgerOriginalFinancialObjectCode));
        sb.append(getField(lMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE), laborLedgerOriginalFinancialSubObjectCode));
        sb.append(getField(lMap.get(LaborPropertyConstants.HRMS_COMPANY), hrmsCompany));
        sb.append(getField(lMap.get(LaborPropertyConstants.SET_ID), setid));

        // pad to full length of 295 chars.
        while (entryLength > sb.toString().length()) {
            sb.append(' ');
        }

        // KFSMI-5958: Don't want any control characters in output files. They potentially disrupt further processing
        Matcher controlCharacterMatcher = MATCH_CONTROL_CHARACTERS.matcher(sb);
        String returnString = controlCharacterMatcher.replaceAll(REPLACE_MATCHED_CONTROL_CHARACTERS);

        return returnString;
    }

    /**
     * Sets the entries from text file.
     */

    @Override
    public List<Message> setFromTextFileForBatch(String line, int lineNumber)  {
        List<Message> returnList = new ArrayList();
        Map<String, Integer> pMap = getLaborOriginEntryFieldUtil().getFieldBeginningPositionMap();
        Map<String, Integer> lMap = getLaborOriginEntryFieldUtil().getFieldLengthMap();
        int entryLength = pMap.get(LaborPropertyConstants.SET_ID) +  lMap.get(LaborPropertyConstants.SET_ID);

        // KFSMI-5958: Don't want any control characters in output files. They potentially disrupt further processing
        Matcher controlCharacterMatcher = MATCH_CONTROL_CHARACTERS.matcher(line);
        line = controlCharacterMatcher.replaceAll(REPLACE_MATCHED_CONTROL_CHARACTERS);

        // Just in case
        line = org.apache.commons.lang.StringUtils.rightPad(line, entryLength, ' ');
        String fiscalYearString = line.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        if (!GeneralLedgerConstants.getSpaceUniversityFiscalYear().equals(fiscalYearString)) {
            try {
                setUniversityFiscalYear(new Integer(fiscalYearString));
            }
            catch (NumberFormatException e) {
                returnList.add(new Message("Fiscal year '" + fiscalYearString + "' contains an invalid value." , Message.TYPE_FATAL));
                setUniversityFiscalYear(null);
            }

        }
        else {
            setUniversityFiscalYear(null);
        }

        setChartOfAccountsCode(getValue(line, pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), pMap.get(KFSPropertyConstants.ACCOUNT_NUMBER)));
        setAccountNumber(getValue(line, pMap.get(KFSPropertyConstants.ACCOUNT_NUMBER), pMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER)));

        // if chart code is empty while accounts cannot cross charts, then derive chart code from account number
        AccountService acctserv = SpringContext.getBean(AccountService.class);
        if (StringUtils.isEmpty(getChartOfAccountsCode()) && StringUtils.isNotEmpty(getAccountNumber()) && !acctserv.accountsCanCrossCharts()) {
            Account account = acctserv.getUniqueAccountForAccountNumber(getAccountNumber());
            if (account != null) {
                setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }

        setSubAccountNumber(getValue(line, pMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE)));
        setFinancialObjectCode(getValue(line, pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)));
        setFinancialSubObjectCode(getValue(line, pMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE)));
        setFinancialBalanceTypeCode(getValue(line, pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE)));
        setFinancialObjectTypeCode(getValue(line, pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE), pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE)));
        setUniversityFiscalPeriodCode(getValue(line, pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE)));
        setFinancialDocumentTypeCode(getValue(line, pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE)));
        setFinancialSystemOriginationCode(getValue(line, pMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE), pMap.get(KFSPropertyConstants.DOCUMENT_NUMBER)));
        setDocumentNumber(getValue(line, pMap.get(KFSPropertyConstants.DOCUMENT_NUMBER), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)));

        String sequenceNumberString = line.substring(pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.POSITION_NUMBER));
        if (!GeneralLedgerConstants.getSpaceTransactionEntrySequenceNumber().equals(sequenceNumberString) && !GeneralLedgerConstants.getZeroTransactionEntrySequenceNumber().equals(sequenceNumberString)) {
            try {
                setTransactionLedgerEntrySequenceNumber(new Integer(sequenceNumberString.trim()));
            }
            catch (NumberFormatException e) {
                returnList.add(new Message("Transaction Sequence Number '" + sequenceNumberString + "' contains an invalid value." , Message.TYPE_FATAL));
                setTransactionLedgerEntrySequenceNumber(null);
            }
        }
        else {
            setTransactionLedgerEntrySequenceNumber(null);
        }

        setPositionNumber(getValue(line, pMap.get(KFSPropertyConstants.POSITION_NUMBER), pMap.get(KFSPropertyConstants.PROJECT_CODE)));
        setProjectCode(getValue(line, pMap.get(KFSPropertyConstants.PROJECT_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC)));
        setTransactionLedgerEntryDescription(getValue(line, pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT)));

        String amountString = line.substring(pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT), pMap.get(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE));
        if (!amountString.trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
            try {
                setTransactionLedgerEntryAmount(new KualiDecimal(amountString.trim()));
            }
            catch (NumberFormatException e) {
                returnList.add(new Message("Transaction Amount '" + amountString + "' contains an invalid value." , Message.TYPE_FATAL));
                setTransactionLedgerEntryAmount(KualiDecimal.ZERO);
            }
        } else {
            returnList.add(new Message("Transaction Amount cannot be blank." , Message.TYPE_FATAL));
            setTransactionLedgerEntryAmount(KualiDecimal.ZERO);
        }

        setTransactionDebitCreditCode(line.substring(pMap.get(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_DATE)));

        String transactionDateString = line.substring(pMap.get(KFSPropertyConstants.TRANSACTION_DATE), pMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER));
        if (!transactionDateString.trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
            try {
                setTransactionDate(parseDate(org.springframework.util.StringUtils.trimTrailingWhitespace(transactionDateString), false));
            }
            catch (ParseException e) {
                setTransactionDate(null);
                returnList.add(new Message("Transaction Date '" + transactionDateString + "' contains an invalid value." , Message.TYPE_FATAL));
            }
        } else {
            setTransactionDate(null);
        }

        setOrganizationDocumentNumber(getValue(line, pMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER), pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID)));
        setOrganizationReferenceId(getValue(line, pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID), pMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE)));
        setReferenceFinancialDocumentTypeCode(getValue(line, pMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE)));
        setReferenceFinancialSystemOriginationCode(getValue(line, pMap.get(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR)));
        setReferenceFinancialDocumentNumber(getValue(line, pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR), pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE)));

        String revDateStr = line.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE), pMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD));
        if (!revDateStr.trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
            try {
                setFinancialDocumentReversalDate(parseDate(org.springframework.util.StringUtils.trimTrailingWhitespace(revDateStr), false));
            }
            catch (ParseException e) {
                setFinancialDocumentReversalDate(null);
                returnList.add(new Message("Reversal Date '" + revDateStr + "' contains an invalid value." , Message.TYPE_FATAL));
            }
        } else {
            setFinancialDocumentReversalDate(null);
        }

        setTransactionEncumbranceUpdateCode(line.substring(pMap.get(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD), pMap.get(KFSPropertyConstants.TRANSACTION_POSTING_DATE)));

        String postDateStr = line.substring(pMap.get(KFSPropertyConstants.TRANSACTION_POSTING_DATE), pMap.get(KFSPropertyConstants.PAY_PERIOD_END_DATE));
        if (!postDateStr.trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
            try {
                setTransactionPostingDate(parseDate(org.springframework.util.StringUtils.trimTrailingWhitespace(postDateStr), false));
            }
            catch (ParseException e) {
                setTransactionPostingDate(null);
                returnList.add(new Message("Transaction Posting Date '" + postDateStr + "' contains an invalid value." , Message.TYPE_FATAL));
            }
        } else {
            setTransactionPostingDate(null);
        }

        String payPeriodDateStr = line.substring(pMap.get(KFSPropertyConstants.PAY_PERIOD_END_DATE), pMap.get(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS));
        if (!payPeriodDateStr.trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
            try {
                setPayPeriodEndDate(parseDate(org.springframework.util.StringUtils.trimTrailingWhitespace(payPeriodDateStr), false));
            }
            catch (ParseException e) {
                setPayPeriodEndDate(null);
                returnList.add(new Message("Pay Period End Date '" + payPeriodDateStr + "' contains an invalid value." , Message.TYPE_FATAL));
            }
        } else {
            setPayPeriodEndDate(null);
        }

        String transTotHrsStr = line.substring(pMap.get(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS), pMap.get(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR));
        if (!transTotHrsStr.trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
            try {
                setTransactionTotalHours(new BigDecimal(transTotHrsStr.trim()));
            }
            catch (NumberFormatException e) {
                setTransactionTotalHours(null);
                returnList.add(new Message("Transaction Total Hours '" + transTotHrsStr.trim() + "' contains an invalid value." , Message.TYPE_FATAL));
            }
        } else {
            setTransactionTotalHours(null);
        }
        String payEndFisYrStr = line.substring(pMap.get(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR), pMap.get(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE));
        if (!GeneralLedgerConstants.getSpaceUniversityFiscalYear().equals(payEndFisYrStr)) {
            try {
                setPayrollEndDateFiscalYear(new Integer(org.springframework.util.StringUtils.trimTrailingWhitespace(payEndFisYrStr)));
            }
            catch (NumberFormatException e) {
                returnList.add(new Message("Payroll End Date Fiscal Year '" + payEndFisYrStr + "' contains an invalid value." , Message.TYPE_FATAL));
                setPayrollEndDateFiscalYear(null);
            }
        }
        else {
            setPayrollEndDateFiscalYear(null);
        }

        setPayrollEndDateFiscalPeriodCode(getValue(line, pMap.get(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE), pMap.get(KFSPropertyConstants.EMPLID)));
        setEmplid(getValue(line, pMap.get(KFSPropertyConstants.EMPLID), pMap.get(KFSPropertyConstants.EMPLOYEE_RECORD)));

        String empRecordStr = line.substring(pMap.get(KFSPropertyConstants.EMPLOYEE_RECORD), pMap.get(KFSPropertyConstants.EARN_CODE));
        if (!empRecordStr.trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
            try {
                setEmployeeRecord(new Integer(empRecordStr.trim()));
            }
            catch (NumberFormatException e) {
                returnList.add(new Message("Employee Record '" + empRecordStr.trim() + "' contains an invalid value." , Message.TYPE_FATAL));
                setEmployeeRecord(null);
            }
        } else {
            setEmployeeRecord(null);
        }

        setEarnCode(getValue(line, pMap.get(KFSPropertyConstants.EARN_CODE), pMap.get(KFSPropertyConstants.PAY_GROUP)));
        setPayGroup(getValue(line, pMap.get(KFSPropertyConstants.PAY_GROUP), pMap.get(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN)));
        setSalaryAdministrationPlan(getValue(line, pMap.get(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN), pMap.get(LaborPropertyConstants.GRADE)));
        setGrade(getValue(line, pMap.get(LaborPropertyConstants.GRADE), pMap.get(LaborPropertyConstants.RUN_IDENTIFIER)));
        setRunIdentifier(getValue(line, pMap.get(LaborPropertyConstants.RUN_IDENTIFIER), pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE)));
        setLaborLedgerOriginalChartOfAccountsCode(getValue(line, pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE), pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER)));
        setLaborLedgerOriginalAccountNumber(getValue(line, pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER), pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER)));
        setLaborLedgerOriginalSubAccountNumber(getValue(line, pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER), pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE)));
        setLaborLedgerOriginalFinancialObjectCode(getValue(line, pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE), pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE)));
        setLaborLedgerOriginalFinancialSubObjectCode(getValue(line, pMap.get(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE), pMap.get(LaborPropertyConstants.HRMS_COMPANY)));
        setHrmsCompany(getValue(line, pMap.get(LaborPropertyConstants.HRMS_COMPANY), pMap.get(LaborPropertyConstants.SET_ID)));
        setSetid(getValue(line, pMap.get(LaborPropertyConstants.SET_ID), entryLength));

        return returnList;
    }


    /**
     * Get fieldValue from fieldName.
     *
     * @param fieldName
     */

    @Override
    public Object getFieldValue(String fieldName) {
        if ("universityFiscalYear".equals(fieldName)) {
            return getUniversityFiscalYear();
        }
        else if ("chartOfAccountsCode".equals(fieldName)) {
            return getChartOfAccountsCode();
        }
        else if ("accountNumber".equals(fieldName)) {
            return getAccountNumber();
        }
        else if ("subAccountNumber".equals(fieldName)) {
            return getSubAccountNumber();
        }
        else if ("financialObjectCode".equals(fieldName)) {
            return getFinancialObjectCode();
        }
        else if ("financialSubObjectCode".equals(fieldName)) {
            return getFinancialSubObjectCode();
        }
        else if ("financialBalanceTypeCode".equals(fieldName)) {
            return getFinancialBalanceTypeCode();
        }
        else if ("financialObjectTypeCode".equals(fieldName)) {
            return getFinancialObjectTypeCode();
        }
        else if ("universityFiscalPeriodCode".equals(fieldName)) {
            return getUniversityFiscalPeriodCode();
        }
        else if ("financialDocumentTypeCode".equals(fieldName)) {
            return getFinancialDocumentTypeCode();
        }
        else if ("financialSystemOriginationCode".equals(fieldName)) {
            return getFinancialSystemOriginationCode();
        }
        else if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(fieldName)) {
            return getDocumentNumber();
        }
        else if ("transactionLedgerEntrySequenceNumber".equals(fieldName)) {
            return getTransactionLedgerEntrySequenceNumber();
        }
        else if ("positionNumber".equals(fieldName)) {
            return getPositionNumber();
        }
        else if ("transactionLedgerEntryDescription".equals(fieldName)) {
            return getTransactionLedgerEntryDescription();
        }
        else if ("transactionLedgerEntryAmount".equals(fieldName)) {
            return getTransactionLedgerEntryAmount();
        }
        else if ("transactionDebitCreditCode".equals(fieldName)) {
            return getTransactionDebitCreditCode();
        }
        else if ("transactionDate".equals(fieldName)) {
            return getTransactionDate();
        }
        else if ("organizationDocumentNumber".equals(fieldName)) {
            return getOrganizationDocumentNumber();
        }
        else if ("projectCode".equals(fieldName)) {
            return getProjectCode();
        }
        else if ("organizationReferenceId".equals(fieldName)) {
            return getOrganizationReferenceId();
        }
        else if ("referenceFinancialDocumentTypeCode".equals(fieldName)) {
            return getReferenceFinancialDocumentTypeCode();
        }
        else if ("referenceFinancialSystemOriginationCode".equals(fieldName)) {
            return getReferenceFinancialSystemOriginationCode();
        }
        else if ("referenceFinancialDocumentNumber".equals(fieldName)) {
            return getReferenceFinancialDocumentNumber();
        }
        else if ("financialDocumentReversalDate".equals(fieldName)) {
            return getFinancialDocumentReversalDate();
        }
        else if ("transactionEncumbranceUpdateCode".equals(fieldName)) {
            return getTransactionEncumbranceUpdateCode();
        }
        else if ("transactionPostingDate".equals(fieldName)) {
            return getTransactionPostingDate();
        }
        else if ("payPeriodEndDate".equals(fieldName)) {
            return getPayPeriodEndDate();
        }
        else if ("transactionTotalHours".equals(fieldName)) {
            return getTransactionTotalHours();
        }
        else if ("payrollEndDateFiscalYear".equals(fieldName)) {
            return getPayrollEndDateFiscalYear();
        }
        else if ("payrollEndDateFiscalPeriodCode".equals(fieldName)) {
            return getPayrollEndDateFiscalPeriodCode();
        }
        else if ("financialDocumentApprovedCode".equals(fieldName)) {
            return getFinancialDocumentApprovedCode();
        }
        else if ("transactionEntryOffsetCode".equals(fieldName)) {
            return getTransactionEntryOffsetCode();
        }
        else if ("financialDocumentApprovedCode".equals(fieldName)) {
            return getFinancialDocumentApprovedCode();
        }
        else if ("transactionEntryProcessedTimestamp".equals(fieldName)) {
            return getTransactionEntryProcessedTimestamp();
        }
        else if ("emplid".equals(fieldName)) {
            return getEmplid();
        }
        else if ("employeeRecord".equals(fieldName)) {
            return getEmployeeRecord();
        }
        else if ("earnCode".equals(fieldName)) {
            return getEarnCode();
        }
        else if ("payGroup".equals(fieldName)) {
            return getPayGroup();
        }
        else if ("salaryAdministrationPlan".equals(fieldName)) {
            return getSalaryAdministrationPlan();
        }
        else if ("grade".equals(fieldName)) {
            return getGrade();
        }
        else if ("runIdentifier".equals(fieldName)) {
            return getRunIdentifier();
        }
        else if ("laborLedgerOriginalChartOfAccountsCode".equals(fieldName)) {
            return getLaborLedgerOriginalChartOfAccountsCode();
        }
        else if ("laborLedgerOriginalAccountNumber".equals(fieldName)) {
            return getLaborLedgerOriginalAccountNumber();
        }
        else if ("laborLedgerOriginalSubAccountNumber".equals(fieldName)) {
            return getLaborLedgerOriginalSubAccountNumber();
        }
        else if ("laborLedgerOriginalFinancialObjectCode".equals(fieldName)) {
            return getLaborLedgerOriginalFinancialObjectCode();
        }
        else if ("laborLedgerOriginalFinancialSubObjectCode".equals(fieldName)) {
            return getLaborLedgerOriginalFinancialSubObjectCode();
        }
        else if ("hrmsCompany".equals(fieldName)) {
            return getHrmsCompany();
        }
        else if ("setid".equals(fieldName)) {
            return getSetid();
        }
        else {
            throw new IllegalArgumentException("Invalid Field Name " + fieldName);
        }
    }

    /**
     * Sets the fieldValue
     *
     * @param fieldName
     * @param fieldValue
     */
    @Override
    public void setFieldValue(String fieldName, String fieldValue) {
        if ("universityFiscalYear".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                setUniversityFiscalYear(Integer.parseInt(fieldValue));
            }
            else {
                setUniversityFiscalYear(null);
            }
        }
        else if ("chartOfAccountsCode".equals(fieldName)) {
            setChartOfAccountsCode(fieldValue);
        }
        else if ("accountNumber".equals(fieldName)) {
            setAccountNumber(fieldValue);
        }
        else if ("subAccountNumber".equals(fieldName)) {
            setSubAccountNumber(fieldValue);
        }
        else if ("financialObjectCode".equals(fieldName)) {
            setFinancialObjectCode(fieldValue);
        }
        else if ("financialSubObjectCode".equals(fieldName)) {
            setFinancialSubObjectCode(fieldValue);
        }
        else if ("financialBalanceTypeCode".equals(fieldName)) {
            setFinancialBalanceTypeCode(fieldValue);
        }
        else if ("financialObjectTypeCode".equals(fieldName)) {
            setFinancialObjectTypeCode(fieldValue);
        }
        else if ("universityFiscalPeriodCode".equals(fieldName)) {
            setUniversityFiscalPeriodCode(fieldValue);
        }
        else if ("financialDocumentTypeCode".equals(fieldName)) {
            setFinancialDocumentTypeCode(fieldValue);
        }
        else if ("financialSystemOriginationCode".equals(fieldName)) {
            setFinancialSystemOriginationCode(fieldValue);
        }
        else if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(fieldName)) {
            setDocumentNumber(fieldValue);
        }
        else if ("transactionLedgerEntrySequenceNumber".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                setTransactionLedgerEntrySequenceNumber(Integer.parseInt(fieldValue));
            }
            else {
                setTransactionLedgerEntrySequenceNumber(null);
            }
        }
        else if ("positionNumber".equals(fieldName)) {
            setPositionNumber(fieldValue);
        }
        else if ("projectCode".equals(fieldName)) {
            setProjectCode(fieldValue);
        }
        else if ("transactionLedgerEntryDescription".equals(fieldName)) {
            setTransactionLedgerEntryDescription(fieldValue);
        }
        else if ("transactionLedgerEntryAmount".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                setTransactionLedgerEntryAmount(new KualiDecimal(fieldValue));
            }
            else {
                clearTransactionLedgerEntryAmount();
            }
        }
        else if ("transactionDebitCreditCode".equals(fieldName)) {
            setTransactionDebitCreditCode(fieldValue);
        }
        else if ("transactionDate".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    setTransactionDate(new java.sql.Date((df.parse(fieldValue)).getTime()));
                }
                catch (ParseException e) {
                    setTransactionDate(null);
                }
            }
            else {
                setTransactionDate(null);
            }
        }
        else if ("organizationDocumentNumber".equals(fieldName)) {
            setOrganizationDocumentNumber(fieldValue);
        }
        else if ("organizationReferenceId".equals(fieldName)) {
            setOrganizationReferenceId(fieldValue);
        }
        else if ("referenceFinancialDocumentTypeCode".equals(fieldName)) {
            setReferenceFinancialDocumentTypeCode(fieldValue);
        }
        else if ("referenceFinancialSystemOriginationCode".equals(fieldName)) {
            setReferenceFinancialSystemOriginationCode(fieldValue);
        }
        else if ("referenceFinancialDocumentNumber".equals(fieldName)) {
            setReferenceFinancialDocumentNumber(fieldValue);
        }
        else if ("financialDocumentReversalDate".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    setFinancialDocumentReversalDate(new java.sql.Date((df.parse(fieldValue)).getTime()));
                }
                catch (ParseException e) {
                    setFinancialDocumentReversalDate(null);
                }
            }
            else {
                setFinancialDocumentReversalDate(null);
            }
        }
        else if ("transactionEncumbranceUpdateCode".equals(fieldName)) {
            setTransactionEncumbranceUpdateCode(fieldValue);
        }
        else if ("transactionPostingDate".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    setTransactionPostingDate(new java.sql.Date((df.parse(fieldValue)).getTime()));
                }
                catch (ParseException e) {
                    setTransactionPostingDate(null);
                }
            }
            else {
                setTransactionPostingDate(null);
            }
        }
        else if ("payPeriodEndDate".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    setPayPeriodEndDate(new java.sql.Date((df.parse(fieldValue)).getTime()));
                }
                catch (ParseException e) {
                    setPayPeriodEndDate(null);
                }
            }
            else {
                setPayPeriodEndDate(null);
            }
        }
        else if ("transactionTotalHours".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                setTransactionTotalHours(new BigDecimal(fieldValue));
            }
            else {
                clearTransactionTotalHours();
            }
        }
        else if ("payrollEndDateFiscalYear".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                setPayrollEndDateFiscalYear(Integer.parseInt(fieldValue));
            }
            else {
                setPayrollEndDateFiscalYear(null);
            }
        }
        else if ("payrollEndDateFiscalPeriodCode".equals(fieldName)) {
            setPayrollEndDateFiscalPeriodCode(fieldValue);
        }
        else if ("emplid".equals(fieldName)) {
            setEmplid(fieldValue);
        }
        else if ("employeeRecord".equals(fieldName)) {
            if (StringUtils.isNotBlank(fieldValue)) {
                setEmployeeRecord(Integer.parseInt(fieldValue));
            }
            else {
                setEmployeeRecord(null);
            }
        }
        else if ("earnCode".equals(fieldName)) {
            setEarnCode(fieldValue);
        }
        else if ("payGroup".equals(fieldName)) {
            setPayGroup(fieldValue);
        }
        else if ("salaryAdministrationPlan".equals(fieldName)) {
            setSalaryAdministrationPlan(fieldValue);
        }
        else if ("grade".equals(fieldName)) {
            setGrade(fieldValue);
        }
        else if ("runIdentifier".equals(fieldName)) {
            setRunIdentifier(fieldValue);
        }
        else if ("laborLedgerOriginalChartOfAccountsCode".equals(fieldName)) {
            setLaborLedgerOriginalChartOfAccountsCode(fieldValue);
        }
        else if ("laborLedgerOriginalAccountNumber".equals(fieldName)) {
            setLaborLedgerOriginalAccountNumber(fieldValue);
        }
        else if ("laborLedgerOriginalSubAccountNumber".equals(fieldName)) {
            setLaborLedgerOriginalSubAccountNumber(fieldValue);
        }
        else if ("laborLedgerOriginalFinancialObjectCode".equals(fieldName)) {
            setLaborLedgerOriginalFinancialObjectCode(fieldValue);
        }
        else if ("laborLedgerOriginalFinancialSubObjectCode".equals(fieldName)) {
            setLaborLedgerOriginalFinancialSubObjectCode(fieldValue);
        }
        else if ("hrmsCompany".equals(fieldName)) {
            setHrmsCompany(fieldValue);
        }
        else if ("setid".equals(fieldName)) {
            setSetid(fieldValue);
        }
        else {
            throw new IllegalArgumentException("Invalid Field Name " + fieldName);
        }
    }

    /**
     * Formats date and returns date
     *
     * @param date
     * @see org.kuali.kfs.gl.businessobject.OriginEntryLite#formatDate(java.sql.Date)
     */
    @Override
    protected String formatDate(Date date) {
        if (date == null) {
            return LaborConstants.getSpaceTransactionDate();
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.format(date);
        }
    }

    /**
     * @return an initialized version of the LaborOriginEntryFieldUtil
     */
    protected static LaborOriginEntryFieldUtil getLaborOriginEntryFieldUtil() {
        if (laborOriginEntryFieldUtil == null) {
            laborOriginEntryFieldUtil = new LaborOriginEntryFieldUtil();
        }
        return laborOriginEntryFieldUtil;
    }
}
