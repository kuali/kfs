/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.module.purap.bo;

import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;

/**
 * Purap General Ledger Pending Entry Business Object. The general ledger pending entry structure holds financial transaction info
 * that will post to the general ledger as an entry.
 */
public class PurapGeneralLedgerPendingEntry extends PersistableBusinessObjectBase implements Transaction, Serializable, Cloneable {

    private static final long serialVersionUID = 4041748389323105932L;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private Integer universityFiscalYear;
    private String universityFiscalPeriodCode;
    private String transactionLedgerEntryDescription;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionDebitCreditCode;
    private Date transactionDate;
    private String financialDocumentTypeCode;
    private String organizationDocumentNumber;
    private String projectCode;
    private String organizationReferenceId;
    private String referenceFinancialDocumentTypeCode;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private Date financialDocumentReversalDate;
    private String transactionEncumbranceUpdateCode;
    private String financialDocumentApprovedCode;
    private String acctSufficientFundsFinObjCd;
    private boolean transactionEntryOffsetIndicator;
    private Date transactionEntryProcessedTs;

    private DocumentType documentType;
    private DocumentHeader documentHeader;

    private Options option;
    private Chart chart;
    private Account account;
    private SubAccount subAccount;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
    private BalanceTyp balanceType;
    private ObjectType objectType;
    private A21SubAccount a21SubAccount;
    private TransientBalanceInquiryAttributes dummyBusinessObject;
    private OriginationCode originationCode;
    private ProjectCode project;
    private OriginationCode referenceOriginationCode;
    private DocumentType referenceDocumentType;
    private AccountingPeriod accountingPeriod;

    /**
     * Default no-arg constructor.
     */
    public PurapGeneralLedgerPendingEntry() {
        this.objectType = new ObjectType();
        this.balanceType = new BalanceTyp();
        this.dummyBusinessObject = new TransientBalanceInquiryAttributes();
        this.financialObject = new ObjectCode();
    }

    /**
     * Copy constructor Constructs a GeneralLedgerPendingEntry.java.
     * 
     * @param original entry to copy
     */
    public PurapGeneralLedgerPendingEntry(PurapGeneralLedgerPendingEntry original) {
        financialSystemOriginationCode = original.financialSystemOriginationCode;
        documentNumber = original.documentNumber;
        transactionLedgerEntrySequenceNumber = original.transactionLedgerEntrySequenceNumber;
        chartOfAccountsCode = original.chartOfAccountsCode;
        accountNumber = original.accountNumber;
        subAccountNumber = original.subAccountNumber;
        financialObjectCode = original.financialObjectCode;
        financialSubObjectCode = original.financialSubObjectCode;
        financialBalanceTypeCode = original.financialBalanceTypeCode;
        financialObjectTypeCode = original.financialObjectTypeCode;
        universityFiscalYear = original.universityFiscalYear;
        universityFiscalPeriodCode = original.universityFiscalPeriodCode;
        transactionLedgerEntryDescription = original.transactionLedgerEntryDescription;
        transactionLedgerEntryAmount = original.transactionLedgerEntryAmount;
        transactionDebitCreditCode = original.transactionDebitCreditCode;
        transactionDate = original.transactionDate;
        financialDocumentTypeCode = original.financialDocumentTypeCode;
        organizationDocumentNumber = original.organizationDocumentNumber;
        projectCode = original.projectCode;
        organizationReferenceId = original.organizationReferenceId;
        referenceFinancialDocumentTypeCode = original.referenceFinancialDocumentTypeCode;
        referenceFinancialSystemOriginationCode = original.referenceFinancialSystemOriginationCode;
        referenceFinancialDocumentNumber = original.referenceFinancialDocumentNumber;
        financialDocumentReversalDate = original.financialDocumentReversalDate;
        transactionEncumbranceUpdateCode = original.transactionEncumbranceUpdateCode;
        financialDocumentApprovedCode = original.financialDocumentApprovedCode;
        acctSufficientFundsFinObjCd = original.acctSufficientFundsFinObjCd;
        transactionEntryOffsetIndicator = original.transactionEntryOffsetIndicator;
        transactionEntryProcessedTs = original.transactionEntryProcessedTs;
    }

    /**
     * Copy from GeneralLedgerPendingEntry constructor Constructs a PurapGeneralLedgerPendingEntry.java from
     * GeneralLedgerPendingEntry.
     * 
     * @param original entry to copy
     */
    public PurapGeneralLedgerPendingEntry(GeneralLedgerPendingEntry original) {
        financialSystemOriginationCode = original.getFinancialSystemOriginationCode();
        documentNumber = original.getDocumentNumber();
        transactionLedgerEntrySequenceNumber = original.getTransactionLedgerEntrySequenceNumber();
        chartOfAccountsCode = original.getChartOfAccountsCode();
        accountNumber = original.getAccountNumber();
        subAccountNumber = original.getSubAccountNumber();
        financialObjectCode = original.getFinancialObjectCode();
        financialSubObjectCode = original.getFinancialSubObjectCode();
        financialBalanceTypeCode = original.getFinancialBalanceTypeCode();
        financialObjectTypeCode = original.getFinancialObjectTypeCode();
        universityFiscalYear = original.getUniversityFiscalYear();
        universityFiscalPeriodCode = original.getUniversityFiscalPeriodCode();
        transactionLedgerEntryDescription = original.getTransactionLedgerEntryDescription();
        transactionLedgerEntryAmount = original.getTransactionLedgerEntryAmount();
        transactionDebitCreditCode = original.getTransactionDebitCreditCode();
        transactionDate = original.getTransactionDate();
        financialDocumentTypeCode = original.getFinancialDocumentTypeCode();
        organizationDocumentNumber = original.getOrganizationDocumentNumber();
        projectCode = original.getProjectCode();
        organizationReferenceId = original.getOrganizationReferenceId();
        referenceFinancialDocumentTypeCode = original.getReferenceFinancialDocumentTypeCode();
        referenceFinancialSystemOriginationCode = original.getReferenceFinancialSystemOriginationCode();
        referenceFinancialDocumentNumber = original.getReferenceFinancialDocumentNumber();
        financialDocumentReversalDate = original.getFinancialDocumentReversalDate();
        transactionEncumbranceUpdateCode = original.getTransactionEncumbranceUpdateCode();
        financialDocumentApprovedCode = original.getFinancialDocumentApprovedCode();
        acctSufficientFundsFinObjCd = original.getAcctSufficientFundsFinObjCd();
        transactionEntryOffsetIndicator = original.isTransactionEntryOffsetIndicator();
        transactionEntryProcessedTs = original.getTransactionEntryProcessedTs();
    }

    public DocumentType getReferenceDocumentType() {
        return referenceDocumentType;
    }

    public void setReferenceDocumentType(DocumentType referenceDocumentType) {
        this.referenceDocumentType = referenceDocumentType;
    }

    public OriginationCode getReferenceOriginationCode() {
        return referenceOriginationCode;
    }

    public void setReferenceOriginationCode(OriginationCode referenceOriginationCode) {
        this.referenceOriginationCode = referenceOriginationCode;
    }

    public ProjectCode getProject() {
        return project;
    }

    public void setProject(ProjectCode project) {
        this.project = project;
    }

    public OriginationCode getOriginationCode() {
        return originationCode;
    }

    public void setOriginationCode(OriginationCode originationCode) {
        this.originationCode = originationCode;
    }

    public void setOption(Options option) {
        this.option = option;
    }

    public Options getOption() {
        return option;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    public String getReferenceFinancialDocumentTypeCode() {
        return referenceFinancialDocumentTypeCode;
    }

    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
    }

    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    public String getFinancialDocumentApprovedCode() {
        return financialDocumentApprovedCode;
    }

    public void setFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        this.financialDocumentApprovedCode = financialDocumentApprovedCode;
    }

    public String getAcctSufficientFundsFinObjCd() {
        return acctSufficientFundsFinObjCd;
    }

    public void setAcctSufficientFundsFinObjCd(String acctSufficientFundsFinObjCd) {
        this.acctSufficientFundsFinObjCd = acctSufficientFundsFinObjCd;
    }

    public boolean isTransactionEntryOffsetIndicator() {
        return transactionEntryOffsetIndicator;
    }

    public void setTransactionEntryOffsetIndicator(boolean transactionEntryOffsetIndicator) {
        this.transactionEntryOffsetIndicator = transactionEntryOffsetIndicator;
    }

    public Date getTransactionEntryProcessedTs() {
        return transactionEntryProcessedTs;
    }

    public void setTransactionEntryProcessedTs(Date transactionEntryProcessedTs) {
        this.transactionEntryProcessedTs = transactionEntryProcessedTs;
    }

    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialSystemOriginationCode", this.financialSystemOriginationCode);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (transactionLedgerEntrySequenceNumber == null) {
            m.put("transactionLedgerEntrySequenceNumber", null);
        }
        else {
            m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        }
        return m;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public A21SubAccount getA21SubAccount() {
        return this.a21SubAccount;
    }

    public void setA21SubAccount(A21SubAccount a21SubAccount) {
        this.a21SubAccount = a21SubAccount;
    }

    public TransientBalanceInquiryAttributes getDummyBusinessObject() {
        return this.dummyBusinessObject;
    }

    public void setDummyBusinessObject(TransientBalanceInquiryAttributes dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    public boolean isSubAccountNumberBlank() {
        return subAccountNumber == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber().equals(subAccountNumber);
    }

    public boolean isFinancialObjectCodeBlank() {
        return financialObjectCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode().equals(financialObjectCode);
    }

    public boolean isFinancialSubObjectCodeBlank() {
        return financialSubObjectCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode().equals(financialSubObjectCode);
    }

    public boolean isProjectCodeBlank() {
        return projectCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode().equals(projectCode);
    }

    public boolean isFinancialObjectTypeCodeBlank() {
        return financialObjectTypeCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType().equals(financialObjectTypeCode);
    }

}