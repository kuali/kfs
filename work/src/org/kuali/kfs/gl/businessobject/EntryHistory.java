/*
 * Copyright 2006-2009 The Kuali Foundation
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

package org.kuali.kfs.gl.businessobject;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * General Ledger business object for LedgerEntryHistory
 */
public class EntryHistory extends Entry implements LedgerEntryHistory {

    private Integer rowCount;

    /**
     * Default constructor.
     */
    public EntryHistory() {
        super();
        this.setTransactionLedgerEntryAmount(KualiDecimal.ZERO);
        this.setRowCount(0);
    }
    
    /**
     * Constructs a EntryHistory.java.
     * 
     * @param transaction
     */
    public EntryHistory(OriginEntryInformation originEntry) {
        this();
        this.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
        this.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        this.setFinancialObjectCode(originEntry.getFinancialObjectCode());
        this.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
        this.setUniversityFiscalPeriodCode(originEntry.getUniversityFiscalPeriodCode());
        this.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
    }
    
    /**
     * Adds a transactionLedgerEntryAmount and increments the rowCount.
     * 
     * @param transactionLedgerEntryAmount
     */
    public void addAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.setTransactionLedgerEntryAmount(this.getTransactionLedgerEntryAmount().add(transactionLedgerEntryAmount));
        rowCount++;
    }
    
    /**
     * Gets the rowCount
     * 
     * @return Returns the rowCount
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * Sets the rowCount
     * 
     * @param rowCount The rowCount to set.
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }
    
    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getAccountNumber()
     */
    @Override
    public String getAccountNumber() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setAccountNumber(java.lang.String)
     */
    @Override
    public void setAccountNumber(String accountNumber) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getSubAccountNumber()
     */
    @Override
    public String getSubAccountNumber() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setSubAccountNumber(java.lang.String)
     */
    @Override
    public void setSubAccountNumber(String subAccountNumber) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getFinancialSubObjectCode()
     */
    @Override
    public String getFinancialSubObjectCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setFinancialSubObjectCode(java.lang.String)
     */
    @Override
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getFinancialObjectTypeCode()
     */
    @Override
    public String getFinancialObjectTypeCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setFinancialObjectTypeCode(java.lang.String)
     */
    @Override
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getFinancialDocumentTypeCode()
     */
    @Override
    public String getFinancialDocumentTypeCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getFinancialSystemOriginationCode()
     */
    @Override
    public String getFinancialSystemOriginationCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setFinancialSystemOriginationCode(java.lang.String)
     */
    @Override
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setFinancialDocumentTypeCode(java.lang.String)
     */
    @Override
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getDocumentNumber()
     */
    @Override
    public String getDocumentNumber() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setDocumentNumber(java.lang.String)
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getTransactionLedgerEntrySequenceNumber()
     */
    @Override
    public Integer getTransactionLedgerEntrySequenceNumber() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setTransactionLedgerEntrySequenceNumber(java.lang.Integer)
     */
    @Override
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getProjectCode()
     */
    @Override
    public String getProjectCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setProjectCode(java.lang.String)
     */
    @Override
    public void setProjectCode(String projectCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getTransactionLedgerEntryDescription()
     */
    @Override
    public String getTransactionLedgerEntryDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setTransactionLedgerEntryDescription(java.lang.String)
     */
    @Override
    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getTransactionDate()
     */
    @Override
    public Date getTransactionDate() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setTransactionDate(java.sql.Date)
     */
    @Override
    public void setTransactionDate(Date transactionDate) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getOrganizationDocumentNumber()
     */
    @Override
    public String getOrganizationDocumentNumber() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setOrganizationDocumentNumber(java.lang.String)
     */
    @Override
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getOrganizationReferenceId()
     */
    @Override
    public String getOrganizationReferenceId() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setOrganizationReferenceId(java.lang.String)
     */
    @Override
    public void setOrganizationReferenceId(String organizationReferenceId) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getReferenceFinancialDocumentTypeCode()
     */
    @Override
    public String getReferenceFinancialDocumentTypeCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setReferenceFinancialDocumentTypeCode(java.lang.String)
     */
    @Override
    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getReferenceFinancialSystemOriginationCode()
     */
    @Override
    public String getReferenceFinancialSystemOriginationCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setReferenceFinancialSystemOriginationCode(java.lang.String)
     */
    @Override
    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getReferenceFinancialDocumentNumber()
     */
    @Override
    public String getReferenceFinancialDocumentNumber() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setReferenceFinancialDocumentNumber(java.lang.String)
     */
    @Override
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getFinancialDocumentReversalDate()
     */
    @Override
    public Date getFinancialDocumentReversalDate() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setFinancialDocumentReversalDate(java.sql.Date)
     */
    @Override
    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getTransactionEncumbranceUpdateCode()
     */
    @Override
    public String getTransactionEncumbranceUpdateCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setTransactionEncumbranceUpdateCode(java.lang.String)
     */
    @Override
    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getTransactionPostingDate()
     */
    @Override
    public Date getTransactionPostingDate() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setTransactionPostingDate(java.sql.Date)
     */
    @Override
    public void setTransactionPostingDate(Date transactionPostingDate) {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#getTransactionDateTimeStamp()
     */
    @Override
    public Timestamp getTransactionDateTimeStamp() {
        throw new UnsupportedOperationException();
    }

    /**
     * History does not track this field.
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry#setTransactionDateTimeStamp(java.sql.Timestamp)
     */
    @Override
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        throw new UnsupportedOperationException();
    }
}
