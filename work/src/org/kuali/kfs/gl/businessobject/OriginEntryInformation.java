/*
 * Copyright 2005 The Kuali Foundation
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

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Represents an origin entry
 */
public interface OriginEntryInformation extends BusinessObject {

    /**
     * gets the transactionLedgerEntryDescription attribute value
     * 
     * @return
     */
    String getTransactionLedgerEntryDescription();

    /**
     * sets the transactionLedgerEntryDescription attribute value
     * 
     * @param transactionLedgerEntryDescription
     */
    void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription);

    /**
     * gets the documentNumber attribute value
     * 
     * @return
     */
    String getDocumentNumber();

    /**
     * sets the documentNumber attribute value
     * 
     * @param documentNumber
     */
    void setDocumentNumber(String documentNumber);

    /**
     * This gets the origin entry in its standard string output form
     * 
     * @return
     */
    String getLine();

    /**
     * gets the referenceFinancialDocumentNumber attribute value
     * 
     * @return
     */
    String getReferenceFinancialDocumentNumber();

    /**
     * sets the referenceFinancialDocumentNumber attribute value
     * 
     * @param referenceFinancialDocumentNumber
     */
    void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber);

    /**
     * gets the organizationReferenceId attribute value
     * 
     * @return
     */
    String getOrganizationReferenceId();

    /**
     * sets the organizationReferenceId attribute value
     * 
     * @param organizationReferenceId
     */
    void setOrganizationReferenceId(String organizationReferenceId);

    /**
     * gets the accountNumber attribute value
     * 
     * @return
     */
    String getAccountNumber();

    /**
     * sets the accountNumber attribute value
     * 
     * @param accountNumber
     */
    void setAccountNumber(String accountNumber);

    /**
     * gets the subAccountNumber attribute value
     * 
     * @return
     */
    String getSubAccountNumber();

    /**
     * sets the subAccountNumber attribute value
     * 
     * @param subAccountNumber
     */
    void setSubAccountNumber(String subAccountNumber);

    /**
     * gets the chartOfAccountsCode attribute value
     * 
     * @return
     */
    String getChartOfAccountsCode();

    /**
     * sets the chartOfAccountsCode attribute value
     * 
     * @param chartOfAccountsCode
     */
    void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * gets the projectCode attribute value
     * 
     * @return
     */
    String getProjectCode();

    /**
     * sets the projectCode attribute value
     * 
     * @param projectCode
     */
    void setProjectCode(String projectCode);

    /**
     * gets the universityFiscalYear attribute value
     * 
     * @return
     */
    Integer getUniversityFiscalYear();

    /**
     * sets the universityFiscalYear attribute value
     * 
     * @param fiscalYear
     */
    void setUniversityFiscalYear(Integer fiscalYear);

    /**
     * gets the transactionDate attribute value
     * 
     * @return
     */
    Date getTransactionDate();

    /**
     * sets the transactionDate attribute value
     * 
     * @param transactionDate
     */
    void setTransactionDate(Date transactionDate);

    /**
     * gets the financialDocumentTypeCode attribute value
     * 
     * @return
     */
    String getFinancialDocumentTypeCode();

    /**
     * sets the financialDocumentTypeCode attribute value
     * 
     * @param financialDocumentTypeCode
     */
    void setFinancialDocumentTypeCode(String financialDocumentTypeCode);

    /**
     * gets the financialSystemOriginationCode attribute value
     * 
     * @return
     */
    String getFinancialSystemOriginationCode();

    /**
     * sets the financialSystemOriginationCode attribute value
     * 
     * @param origCode
     */
    void setFinancialSystemOriginationCode(String origCode);

    /**
     * gets the financialObjectCode attribute value
     * 
     * @return
     */
    String getFinancialObjectCode();

    /**
     * sets the financialObjectCode attribute value
     * 
     * @param financialObjectCode
     */
    void setFinancialObjectCode(String financialObjectCode);

    /**
     * gets the financialObjectTypeCode attribute value
     * 
     * @return
     */
    String getFinancialObjectTypeCode();

    /**
     * sets the financialObjectTypeCode attribute value
     * 
     * @param financialObjectTypeCode
     */
    void setFinancialObjectTypeCode(String financialObjectTypeCode);

    /**
     * gets the financialSubObjectCode attribute value
     * 
     * @return
     */
    String getFinancialSubObjectCode();

    /**
     * sets the financialSubObjectCode attribute value
     * 
     * @param financialSubObjectCode
     */
    void setFinancialSubObjectCode(String financialSubObjectCode);

    /**
     * gets the transactionLedgerEntryAmount attribute value
     * 
     * @return
     */
    KualiDecimal getTransactionLedgerEntryAmount();

    /**
     * sets the transactionLedgerEntryAmount attribute value
     * 
     * @param amount
     */
    void setTransactionLedgerEntryAmount(KualiDecimal amount);

    /**
     * gets the transactionDebitCreditCode attribute value
     * 
     * @return
     */
    String getTransactionDebitCreditCode();

    /**
     * sets the transactionDebitCreditCode attribute value
     * 
     * @param debitCreditCode
     */
    void setTransactionDebitCreditCode(String debitCreditCode);

    /**
     * gets the financialBalanceTypeCode attribute value
     * 
     * @return
     */
    String getFinancialBalanceTypeCode();

    /**
     * sets the financialBalanceTypeCode attribute value
     * 
     * @param balanceTypeCode
     */
    void setFinancialBalanceTypeCode(String balanceTypeCode);

    /**
     * gets the financialDocumentReversalDate attribute value
     * 
     * @return
     */
    Date getFinancialDocumentReversalDate();

    /**
     * sets the financialDocumentReversalDate attribute value
     * 
     * @param reversalDate
     */
    void setFinancialDocumentReversalDate(Date reversalDate);

    /**
     * true if this origin entry is a credit, false otherwise
     * 
     * @return
     */
    boolean isCredit();

    /**
     * true if this origin entry is a debit, false otherwise
     * 
     * @return
     */
    boolean isDebit();

    /**
     * gets the universityFiscalPeriodCode attribute value
     * 
     * @return
     */
    String getUniversityFiscalPeriodCode();

    /**
     * sets the universityFiscalPeriodCode attribute value
     * 
     * @param fiscalPeriodCode
     */
    void setUniversityFiscalPeriodCode(String fiscalPeriodCode);

    /**
     * gets the referenceFinancialDocumentTypeCode attribute value
     * 
     * @return
     */
    String getReferenceFinancialDocumentTypeCode();

    /**
     * sets the referenceFinancialDocumentTypeCode attribute value
     * 
     * @param refernenceFinancialDocumentTypeCode
     */
    void setReferenceFinancialDocumentTypeCode(String refernenceFinancialDocumentTypeCode);

    /**
     * gets the referenceFinancialSystemOriginationCode attribute value
     * 
     * @return
     */
    String getReferenceFinancialSystemOriginationCode();

    /**
     * sets referenceFinancialSystemOriginationCode attribute value
     * 
     * @param referenceFinancialSystemOriginationCode
     */
    void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode);

    /**
     * gets transactionEncumbranceUpdateCode attribute value
     * 
     * @return
     */
    String getTransactionEncumbranceUpdateCode();

    /**
     * sets transactionEncumbranceUpdateCode attribute value
     * 
     * @param code
     */
    void setTransactionEncumbranceUpdateCode(String code);

    /**
     * gets the organizationDocumentNumber attribute value
     * 
     * @return
     */
    String getOrganizationDocumentNumber();

    /**
     * sets the organizationDocumentNumber attribute value
     * 
     * @param organizationDocumentNumber
     */
    void setOrganizationDocumentNumber(String organizationDocumentNumber);

    /**
     * gets the transactionLedgerEntrySequenceNumber attribute value
     * 
     * @return
     */
    Integer getTransactionLedgerEntrySequenceNumber();

    /**
     * sets the transactionLedgerEntrySequenceNumber attribute value
     * 
     * @param transactionLedgerEntrySequenceNumber
     */
    void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber);

    /**
     * gets the transactionScrubberOffsetGenerationIndicator attribute value
     * 
     * @param b
     */
    void setTransactionScrubberOffsetGenerationIndicator(boolean b);

    /**
     * sets the entryGroupId attribute value
     * 
     * @param groupId
     */
    void setEntryGroupId(Integer groupId);

    /**
     * This method forces the origin entryable to reset its id to null
     */
    void resetEntryId();
}
