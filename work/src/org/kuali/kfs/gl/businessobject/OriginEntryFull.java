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
package org.kuali.module.gl.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
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
import org.kuali.module.gl.exception.LoadException;

/**
 * This class represents a full origin entry
 */
public class OriginEntryFull extends OriginEntryLite implements Transaction, OriginEntry {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryFull.class);

    // bo references
    private OriginEntryGroup group;
    private Account account;
    private SubAccount subAccount;
    private A21SubAccount a21SubAccount;
    private BalanceTyp balanceType;
    private Chart chart;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
    private ObjectType objectType;
    private ProjectCode project;
    private DocumentType documentType;
    private UniversityDate universityDate;
    private Options option;
    private AccountingPeriod accountingPeriod;
    private UniversityDate reversalDate;
    private OriginationCode origination;
    private DocumentType referenceDocumentType;

    public OriginEntryFull(GeneralLedgerPendingEntry glpe) {
        accountNumber = glpe.getAccountNumber();
        documentNumber = glpe.getDocumentNumber();
        referenceFinancialDocumentNumber = glpe.getReferenceFinancialDocumentNumber();
        referenceFinancialDocumentTypeCode = glpe.getReferenceFinancialDocumentTypeCode();
        financialDocumentReversalDate = glpe.getFinancialDocumentReversalDate();
        financialDocumentTypeCode = glpe.getFinancialDocumentTypeCode();
        financialBalanceTypeCode = glpe.getFinancialBalanceTypeCode();
        chartOfAccountsCode = glpe.getChartOfAccountsCode();
        financialObjectTypeCode = glpe.getFinancialObjectTypeCode();
        financialObjectCode = glpe.getFinancialObjectCode();
        financialSubObjectCode = glpe.getFinancialSubObjectCode();
        financialSystemOriginationCode = glpe.getFinancialSystemOriginationCode();
        referenceFinancialSystemOriginationCode = glpe.getReferenceFinancialSystemOriginationCode();
        organizationDocumentNumber = glpe.getOrganizationDocumentNumber();
        organizationReferenceId = glpe.getOrganizationReferenceId();
        projectCode = glpe.getProjectCode();
        subAccountNumber = glpe.getSubAccountNumber();
        transactionDate = glpe.getTransactionDate();
        transactionDebitCreditCode = glpe.getTransactionDebitCreditCode();
        transactionEncumbranceUpdateCode = glpe.getTransactionEncumbranceUpdateCode();
        transactionLedgerEntrySequenceNumber = glpe.getTransactionLedgerEntrySequenceNumber();
        transactionLedgerEntryAmount = glpe.getTransactionLedgerEntryAmount();
        transactionLedgerEntryDescription = glpe.getTransactionLedgerEntryDescription();
        universityFiscalPeriodCode = glpe.getUniversityFiscalPeriodCode();
        universityFiscalYear = glpe.getUniversityFiscalYear();
    }

    /**
     * 
     */
    public OriginEntryFull(String financialDocumentTypeCode, String financialSystemOriginationCode) {
        super();

        setChartOfAccountsCode(KFSConstants.EMPTY_STRING);
        setAccountNumber(KFSConstants.EMPTY_STRING);
        setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        setProjectCode(KFSConstants.getDashProjectCode());

        setFinancialDocumentTypeCode(financialDocumentTypeCode);
        setFinancialSystemOriginationCode(financialSystemOriginationCode);

        setFinancialObjectCode(KFSConstants.EMPTY_STRING);
        setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        setFinancialBalanceTypeCode(KFSConstants.EMPTY_STRING);
        setFinancialObjectTypeCode(KFSConstants.EMPTY_STRING);
        setDocumentNumber(KFSConstants.EMPTY_STRING);
        setFinancialDocumentReversalDate(null);

        setUniversityFiscalYear(new Integer(0));
        setUniversityFiscalPeriodCode(KFSConstants.EMPTY_STRING);

        setTransactionLedgerEntrySequenceNumber(new Integer(1));
        setTransactionLedgerEntryAmount(new KualiDecimal(0));
        setTransactionLedgerEntryDescription(KFSConstants.EMPTY_STRING);
        setTransactionDate(null);
        setTransactionDebitCreditCode(KFSConstants.EMPTY_STRING);
        setTransactionEncumbranceUpdateCode(KFSConstants.EMPTY_STRING);

        setOrganizationDocumentNumber(KFSConstants.EMPTY_STRING);
        setOrganizationReferenceId(KFSConstants.EMPTY_STRING);

        setReferenceFinancialDocumentTypeCode(KFSConstants.EMPTY_STRING);
        setReferenceFinancialSystemOriginationCode(KFSConstants.EMPTY_STRING);
        setReferenceFinancialDocumentNumber(KFSConstants.EMPTY_STRING);
    }

    /**
     * 
     */
    public OriginEntryFull() {
        this(null, null);
    }

    public OriginEntryFull(Transaction t) {
        this();
        copyFieldsFromTransaction(t);
    }

    public OriginEntryFull(String line) {
        try {
            setFromTextFile(line, 0);
        }
        catch (LoadException e) {
            LOG.error("OriginEntryFull() Error loading line", e);
        }
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("entryId", getEntryId());
        map.put("entryGroupId", getEntryGroupId());
        map.put("universityFiscalYear", getUniversityFiscalYear());
        map.put("universityFiscalPeriodCode", getUniversityFiscalPeriodCode());
        map.put("chartOfAccountsCode", getChartOfAccountsCode());
        map.put("accountNumber", getAccountNumber());
        map.put("subAccountNumber", getSubAccountNumber());
        map.put("financialObjectCode", getFinancialObjectCode());
        map.put("financialObjectTypeCode", getFinancialObjectTypeCode());
        map.put("financialSubObjectCode", getFinancialSubObjectCode());
        map.put("financialBalanceTypeCode", getFinancialBalanceTypeCode());
        map.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        map.put("financialDocumentTypeCode", getFinancialDocumentTypeCode());
        map.put("financialSystemOriginationCode", getFinancialSystemOriginationCode());
        map.put("transactionLedgerEntrySequenceNumber", getTransactionLedgerEntrySequenceNumber());
        map.put("transactionLedgerEntryDescription", getTransactionLedgerEntryDescription());
        return map;
    }

    public OriginEntryGroup getGroup() {
        return group;
    }

    public void setGroup(OriginEntryGroup oeg) {
        if (oeg != null) {
            setEntryGroupId(oeg.getId());
            group = oeg;
        }
        else {
            setEntryGroupId(null);
            group = null;
        }
    }

    public A21SubAccount getA21SubAccount() {
        return a21SubAccount;
    }

    public void setA21SubAccount(A21SubAccount subAccount) {
        a21SubAccount = subAccount;
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

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Options getOption() {
        return option;
    }

    public void setOption(Options option) {
        this.option = option;
    }

    public ProjectCode getProject() {
        return project;
    }

    public void setProject(ProjectCode project) {
        this.project = project;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public UniversityDate getUniversityDate() {
        return universityDate;
    }

    public void setUniversityDate(UniversityDate universityDate) {
        this.universityDate = universityDate;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    public OriginationCode getOrigination() {
        return origination;
    }

    public void setOrigination(OriginationCode origination) {
        this.origination = origination;
    }


    public DocumentType getReferenceDocumentType() {
        return referenceDocumentType;
    }

    public void setReferenceDocumentType(DocumentType referenceDocumentType) {
        this.referenceDocumentType = referenceDocumentType;
    }

    public static OriginEntryFull copyFromOriginEntryable(OriginEntry oe) {
        OriginEntryFull newOriginEntry = new OriginEntryFull();
        newOriginEntry.setAccountNumber(oe.getAccountNumber());
        newOriginEntry.setDocumentNumber(oe.getDocumentNumber());
        newOriginEntry.setReferenceFinancialDocumentNumber(oe.getReferenceFinancialDocumentNumber());
        newOriginEntry.setReferenceFinancialDocumentTypeCode(oe.getReferenceFinancialDocumentTypeCode());
        newOriginEntry.setFinancialDocumentReversalDate(oe.getFinancialDocumentReversalDate());
        newOriginEntry.setFinancialDocumentTypeCode(oe.getFinancialDocumentTypeCode());
        newOriginEntry.setFinancialBalanceTypeCode(oe.getFinancialBalanceTypeCode());
        newOriginEntry.setChartOfAccountsCode(oe.getChartOfAccountsCode());
        newOriginEntry.setFinancialObjectTypeCode(oe.getFinancialObjectTypeCode());
        newOriginEntry.setFinancialObjectCode(oe.getFinancialObjectCode());
        newOriginEntry.setFinancialSubObjectCode(oe.getFinancialSubObjectCode());
        newOriginEntry.setFinancialSystemOriginationCode(oe.getFinancialSystemOriginationCode());
        newOriginEntry.setReferenceFinancialSystemOriginationCode(oe.getReferenceFinancialSystemOriginationCode());
        newOriginEntry.setOrganizationDocumentNumber(oe.getOrganizationDocumentNumber());
        newOriginEntry.setOrganizationReferenceId(oe.getOrganizationReferenceId());
        newOriginEntry.setProjectCode(oe.getProjectCode());
        newOriginEntry.setSubAccountNumber(oe.getSubAccountNumber());
        newOriginEntry.setTransactionDate(oe.getTransactionDate());
        newOriginEntry.setTransactionDebitCreditCode(oe.getTransactionDebitCreditCode());
        newOriginEntry.setTransactionEncumbranceUpdateCode(oe.getTransactionEncumbranceUpdateCode());
        newOriginEntry.setTransactionLedgerEntrySequenceNumber(oe.getTransactionLedgerEntrySequenceNumber());
        newOriginEntry.setTransactionLedgerEntryAmount(oe.getTransactionLedgerEntryAmount());
        newOriginEntry.setTransactionLedgerEntryDescription(oe.getTransactionLedgerEntryDescription());
        newOriginEntry.setUniversityFiscalPeriodCode(oe.getUniversityFiscalPeriodCode());
        newOriginEntry.setUniversityFiscalYear(oe.getUniversityFiscalYear());
        return newOriginEntry;
    }
}
