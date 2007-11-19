/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * Represents the encumbrance amount for a specific university fiscal year, 
 * chart of accounts code, account number, sub account number, object code,
 * sub object code, balance type code, document type code, origin code, and document number.
 * This encumbrance object contains amounts for actual enumbrance amount, closed amount,
 * outstanding amount 
 * 
*/
public class Encumbrance extends PersistableBusinessObjectBase {
    static final long serialVersionUID = -7494473472438516396L;

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String objectCode;
    private String subObjectCode;
    private String balanceTypeCode;
    private String documentTypeCode;
    private String originCode;
    private String documentNumber;
    private String transactionEncumbranceDescription;
    private Date transactionEncumbranceDate;
    private KualiDecimal accountLineEncumbranceAmount;
    private KualiDecimal accountLineEncumbranceClosedAmount;
    private KualiDecimal accountLineEncumbranceOutstandingAmount;
    private String accountLineEncumbrancePurgeCode;
    private Date timestamp;

    private SubAccount subAccount;
    private Chart chart;
    private Account account;
    private SubObjCd financialSubObject;
    private DocumentType documentType;

    private ObjectCode financialObject;
    private BalanceTyp balanceType;
    private OriginationCode originationCode;
    private Options option;

    private TransientBalanceInquiryAttributes dummyBusinessObject;

    public Encumbrance() {
    }

    public Encumbrance(Transaction t) {
        universityFiscalYear = t.getUniversityFiscalYear();
        chartOfAccountsCode = t.getChartOfAccountsCode();
        accountNumber = t.getAccountNumber();
        subAccountNumber = t.getSubAccountNumber();
        objectCode = t.getFinancialObjectCode();
        subObjectCode = t.getFinancialSubObjectCode();
        balanceTypeCode = t.getFinancialBalanceTypeCode();
        documentTypeCode = t.getFinancialDocumentTypeCode();
        originCode = t.getFinancialSystemOriginationCode();
        documentNumber = t.getDocumentNumber();
        transactionEncumbranceDescription = t.getTransactionLedgerEntryDescription();
        transactionEncumbranceDate = t.getTransactionDate();
        accountLineEncumbranceAmount = new KualiDecimal("0");
        accountLineEncumbranceClosedAmount = new KualiDecimal("0");
        accountLineEncumbrancePurgeCode = " ";
        this.dummyBusinessObject = new TransientBalanceInquiryAttributes();
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getUniversityFiscalYear());
        map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        map.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
        map.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, getSubAccountNumber());
        map.put(KFSPropertyConstants.OBJECT_CODE, getObjectCode());
        map.put(KFSPropertyConstants.SUB_OBJECT_CODE, getSubObjectCode());
        map.put(KFSPropertyConstants.BALANCE_TYPE_CODE, getBalanceTypeCode());
        map.put(KFSPropertyConstants.DOCUMENT_TYPE_CODE, getDocumentTypeCode());
        map.put(KFSPropertyConstants.ORIGIN_CODE, getOriginCode());
        map.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        map.put(KFSPropertyConstants.ACCOUNT_LINE_ENCUMBRANCE_AMOUNT, getAccountLineEncumbranceAmount());
        map.put(KFSPropertyConstants.ACCOUNT_LINE_ENCUMBRANCE_CLOSED_AMOUNT, getAccountLineEncumbranceClosedAmount());
        return map;
    }

    public OriginationCode getOriginationCode() {
        return originationCode;
    }

    public void setOriginationCode(OriginationCode originationCode) {
        this.originationCode = originationCode;
    }

    /**
     * @return Returns the accountLineEncumbranceAmount.
     */
    public KualiDecimal getAccountLineEncumbranceAmount() {
        return accountLineEncumbranceAmount;
    }

    /**
     * @param accountLineEncumbranceAmount The accountLineEncumbranceAmount to set.
     */
    public void setAccountLineEncumbranceAmount(KualiDecimal accountLineEncumbranceAmount) {
        this.accountLineEncumbranceAmount = accountLineEncumbranceAmount;
    }

    /**
     * @return Returns the accountLineEncumbranceClearedAmount.
     */
    public KualiDecimal getAccountLineEncumbranceClosedAmount() {
        return accountLineEncumbranceClosedAmount;
    }

    public void setAccountLineEncumbranceOutstandingAmount() {
    }

    public KualiDecimal getAccountLineEncumbranceOutstandingAmount() {
        return accountLineEncumbranceAmount.subtract(accountLineEncumbranceClosedAmount);
    }

    /**
     * @param accountLineEncumbranceClearedAmount The accountLineEncumbranceClearedAmount to set.
     */
    public void setAccountLineEncumbranceClosedAmount(KualiDecimal accountLineEncumbranceClosedAmount) {
        this.accountLineEncumbranceClosedAmount = accountLineEncumbranceClosedAmount;
    }

    /**
     * @return Returns the accountLineEncumbrancePrg.
     */
    public String getAccountLineEncumbrancePurgeCode() {
        return accountLineEncumbrancePurgeCode;
    }

    /**
     * @param accountLineEncumbrancePrg The accountLineEncumbrancePrg to set.
     */
    public void setAccountLineEncumbrancePurgeCode(String accountLineEncumbrancePurgeCode) {
        this.accountLineEncumbrancePurgeCode = accountLineEncumbrancePurgeCode;
    }

    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return Returns the balanceTypeCode.
     */
    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return Returns the documentTypeCode.
     */
    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    /**
     * @param documentTypeCode The documentTypeCode to set.
     */
    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    /**
     * @return Returns the objectCode.
     */
    public String getObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     */
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * @return Returns the originCode.
     */
    public String getOriginCode() {
        return originCode;
    }

    /**
     * @param originCode The originCode to set.
     */
    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    /**
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @return Returns the subObjectCode.
     */
    public String getSubObjectCode() {
        return subObjectCode;
    }

    /**
     * @param subObjectCode The subObjectCode to set.
     */
    public void setSubObjectCode(String subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    /**
     * @return Returns the timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Returns the transactionEncumbranceDate.
     */
    public Date getTransactionEncumbranceDate() {
        return transactionEncumbranceDate;
    }

    /**
     * @param transactionEncumbranceDate The transactionEncumbranceDate to set.
     */
    public void setTransactionEncumbranceDate(Date transactionEncumbranceDate) {
        this.transactionEncumbranceDate = transactionEncumbranceDate;
    }

    /**
     * @return Returns the transactionEncumbranceDescription.
     */
    public String getTransactionEncumbranceDescription() {
        return transactionEncumbranceDescription;
    }

    /**
     * @param transactionEncumbranceDescription The transactionEncumbranceDescription to set.
     */
    public void setTransactionEncumbranceDescription(String transactionEncumbranceDescription) {
        this.transactionEncumbranceDescription = transactionEncumbranceDescription;
    }

    /**
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
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
     * Gets the dummyBusinessObject attribute.
     * 
     * @return Returns the dummyBusinessObject.
     */
    public TransientBalanceInquiryAttributes getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     * 
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    public void setDummyBusinessObject(TransientBalanceInquiryAttributes dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    /**
     * Gets the option attribute.
     * 
     * @return Returns the option.
     */
    public Options getOption() {
        return option;
    }

    /**
     * Sets the option attribute value.
     * 
     * @param option The option to set.
     */
    public void setOption(Options option) {
        this.option = option;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
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
     * Gets the financialSubObject attribute.
     * 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * 
     * @param financialSubObject The financialSubObject to set.
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }
}
