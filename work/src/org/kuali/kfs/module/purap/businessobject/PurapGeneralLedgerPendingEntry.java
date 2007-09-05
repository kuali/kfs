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
 * The general ledger pending entry structure holds financial transaction info that will post to the general ledger as an entry.
 * 
 * 
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
     * Copy constructor
     * 
     * Constructs a GeneralLedgerPendingEntry.java.
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
     * Copy from GeneralLedgerPendingEntry constructor
     * 
     * Constructs a PurapGeneralLedgerPendingEntry.java from GeneralLedgerPendingEntry.
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

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the transactionLedgerEntrySequenceNumber attribute.
     * 
     * @return Returns the transactionLedgerEntrySequenceNumber
     */
    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the transactionLedgerEntrySequenceNumber attribute.
     * 
     * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
     */
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }


    /**
     * Sets the financialObjectTypeCode attribute.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }


    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the universityFiscalPeriodCode attribute.
     * 
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    /**
     * Gets the transactionLedgerEntryDescription attribute.
     * 
     * @return Returns the transactionLedgerEntryDescription
     */
    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    /**
     * Sets the transactionLedgerEntryDescription attribute.
     * 
     * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
     */
    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    /**
     * Gets the transactionLedgerEntryAmount attribute.
     * 
     * @return Returns the transactionLedgerEntryAmount
     */
    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    /**
     * Sets the transactionLedgerEntryAmount attribute.
     * 
     * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
     */
    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    /**
     * Gets the transactionDebitCreditCode attribute.
     * 
     * @return Returns the transactionDebitCreditCode
     */
    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    /**
     * Sets the transactionDebitCreditCode attribute.
     * 
     * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
     */
    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    /**
     * Gets the transactionDate attribute.
     * 
     * @return Returns the transactionDate
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transactionDate attribute.
     * 
     * @param transactionDate The transactionDate to set.
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the organizationDocumentNumber attribute.
     * 
     * @return Returns the organizationDocumentNumber
     */
    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    /**
     * Sets the organizationDocumentNumber attribute.
     * 
     * @param organizationDocumentNumber The organizationDocumentNumber to set.
     */
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    /**
     * Gets the projectCode attribute.
     * 
     * @return Returns the projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute.
     * 
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the organizationReferenceId attribute.
     * 
     * @return Returns the organizationReferenceId
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * Sets the organizationReferenceId attribute.
     * 
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * Gets the referenceFinancialDocumentTypeCode attribute.
     * 
     * @return Returns the referenceFinancialDocumentTypeCode
     */
    public String getReferenceFinancialDocumentTypeCode() {
        return referenceFinancialDocumentTypeCode;
    }

    /**
     * Sets the referenceFinancialDocumentTypeCode attribute.
     * 
     * @param referenceFinancialDocumentTypeCode The referenceFinancialDocumentTypeCode to set.
     */
    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
    }

    /**
     * Gets the referenceFinancialSystemOriginationCode attribute.
     * 
     * @return Returns the referenceFinancialSystemOriginationCode
     */
    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    /**
     * Sets the referenceFinancialSystemOriginationCode attribute.
     * 
     * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
     */
    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    /**
     * Gets the financialDocumentReversalDate attribute.
     * 
     * @return Returns the financialDocumentReversalDate
     */
    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    /**
     * Sets the financialDocumentReversalDate attribute.
     * 
     * @param financialDocumentReversalDate The financialDocumentReversalDate to set.
     */
    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    /**
     * Gets the transactionEncumbranceUpdateCode attribute.
     * 
     * @return Returns the transactionEncumbranceUpdateCode
     * 
     */
    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    /**
     * Sets the transactionEncumbranceUpdateCode attribute.
     * 
     * @param transactionEncumbranceUpdateCode The transactionEncumbranceUpdateCode to set.
     */
    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    /**
     * Gets the financialDocumentApprovedCode attribute.
     * 
     * @return Returns the financialDocumentApprovedCode
     */
    public String getFinancialDocumentApprovedCode() {
        return financialDocumentApprovedCode;
    }


    /**
     * Sets the financialDocumentApprovedCode attribute.
     * 
     * @param financialDocumentApprovedCode The financialDocumentApprovedCode to set.
     */
    public void setFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
        this.financialDocumentApprovedCode = financialDocumentApprovedCode;
    }

    /**
     * Gets the acctSufficientFundsFinObjCd attribute.
     * 
     * @return Returns the acctSufficientFundsFinObjCd
     */
    public String getAcctSufficientFundsFinObjCd() {
        return acctSufficientFundsFinObjCd;
    }

    /**
     * Sets the acctSufficientFundsFinObjCd attribute.
     * 
     * @param acctSufficientFundsFinObjCd The acctSufficientFundsFinObjCd to set.
     */
    public void setAcctSufficientFundsFinObjCd(String acctSufficientFundsFinObjCd) {
        this.acctSufficientFundsFinObjCd = acctSufficientFundsFinObjCd;
    }

    /**
     * Gets the transactionEntryOffsetIndicator attribute.
     * 
     * @return Returns the transactionEntryOffsetIndicator
     */
    public boolean isTransactionEntryOffsetIndicator() {
        return transactionEntryOffsetIndicator;
    }

    /**
     * Sets the transactionEntryOffsetIndicator attribute.
     * 
     * @param transactionEntryOffsetIndicator The transactionEntryOffsetIndicator to set.
     */
    public void setTransactionEntryOffsetIndicator(boolean transactionEntryOffsetIndicator) {
        this.transactionEntryOffsetIndicator = transactionEntryOffsetIndicator;
    }

    /**
     * Gets the transactionEntryProcessedTs attribute.
     * 
     * @return Returns the transactionEntryProcessedTs
     */
    public Date getTransactionEntryProcessedTs() {
        return transactionEntryProcessedTs;
    }

    /**
     * Sets the transactionEntryProcessedTs attribute.
     * 
     * @param transactionEntryProcessedTs The transactionEntryProcessedTs to set.
     */
    public void setTransactionEntryProcessedTs(Date transactionEntryProcessedTs) {
        this.transactionEntryProcessedTs = transactionEntryProcessedTs;
    }

    /**
     * @return Returns the financialSystemOriginationCode.
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
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

    /**
     * Gets the documentType attribute.
     * 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * 
     * @param documentType The documentType to set.
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the documentHeader attribute.
     * 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * 
     * @param documentHeader The documentHeader to set.
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the balanceType attribute.
     * 
     * @return Returns the balanceType.
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * 
     * @param balanceType The balanceType to set.
     */
    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     * 
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject.
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute value.
     * 
     * @param financialObject The financialObject to set.
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the objectType attribute.
     * 
     * @return Returns the objectType.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Sets the objectType attribute value.
     * 
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * Gets the a21SubAccount attribute.
     * 
     * @return Returns the a21SubAccount.
     */
    public A21SubAccount getA21SubAccount() {
        return this.a21SubAccount;
    }

    /**
     * Sets the a21SubAccount attribute value.
     * 
     * @param a21SubAccount The a21SubAccount to set.
     */
    public void setA21SubAccount(A21SubAccount a21SubAccount) {
        this.a21SubAccount = a21SubAccount;
    }

    /**
     * Gets the dummyBusinessObject attribute.
     * 
     * @return Returns the dummyBusinessObject.
     */
    public TransientBalanceInquiryAttributes getDummyBusinessObject() {
        return this.dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     * 
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
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

    /**
     * Gets the accountingPeriod attribute. 
     * @return Returns the accountingPeriod.
     */
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