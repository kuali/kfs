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

package org.kuali.module.effort.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * Business Object for the Effort Certification Detail Table.
 */
public class EffortCertificationDetail extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String positionNumber;
    private String financialObjectCode;
    private String sourceChartOfAccountsCode;
    private String sourceAccountNumber;
    private KualiDecimal effortCertificationPayrollAmount;
    private String effortCertificationDerivedPayrollCode;
    private Integer effortCertificationDerivedPayrollPercent;
    private String effortCertificationCostSharingCode;
    private Integer effortCertificationCostSharingPercent;
    private String effortCertificationCalculatedOverallCode;
    private Integer effortCertificationCalculatedOverallPercent;
    private String effortCertificationUpdatedOverallCode;
    private Integer effortCertificationUpdatedOverallPercent;
    private String effortCertificationProratedCode;
    private Integer effortCertificationProratedPercent;
    private Integer financialDocumentPostingYear;
    private String costShareSourceSubAccountNumber;
    private KualiDecimal effortCertificationOriginalPayrollAmount;

    private EffortCertificationDocument financialDocument;
    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private Chart sourceChartOfAccounts;
    private Account sourceAccount;
    private SubAccount subAccount;

    /**
     * Default constructor.
     */
    public EffortCertificationDetail() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the sourceChartOfAccountsCode attribute.
     * 
     * @return Returns the sourceChartOfAccountsCode.
     */
    public String getSourceChartOfAccountsCode() {
        return sourceChartOfAccountsCode;
    }

    /**
     * Sets the sourceChartOfAccountsCode attribute value.
     * 
     * @param sourceChartOfAccountsCode The sourceChartOfAccountsCode to set.
     */
    public void setSourceChartOfAccountsCode(String sourceChartOfAccountsCode) {
        this.sourceChartOfAccountsCode = sourceChartOfAccountsCode;
    }

    /**
     * Gets the sourceAccountNumber attribute.
     * 
     * @return Returns the sourceAccountNumber.
     */
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    /**
     * Sets the sourceAccountNumber attribute value.
     * 
     * @param sourceAccountNumber The sourceAccountNumber to set.
     */
    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    /**
     * Gets the effortCertificationPayrollAmount attribute.
     * 
     * @return Returns the effortCertificationPayrollAmount.
     */
    public KualiDecimal getEffortCertificationPayrollAmount() {
        return effortCertificationPayrollAmount;
    }

    /**
     * Sets the effortCertificationPayrollAmount attribute value.
     * 
     * @param effortCertificationPayrollAmount The effortCertificationPayrollAmount to set.
     */
    public void setEffortCertificationPayrollAmount(KualiDecimal effortCertificationPayrollAmount) {
        this.effortCertificationPayrollAmount = effortCertificationPayrollAmount;
    }

    /**
     * Gets the effortCertificationDerivedPayrollCode attribute.
     * 
     * @return Returns the effortCertificationDerivedPayrollCode.
     */
    public String getEffortCertificationDerivedPayrollCode() {
        return effortCertificationDerivedPayrollCode;
    }

    /**
     * Sets the effortCertificationDerivedPayrollCode attribute value.
     * 
     * @param effortCertificationDerivedPayrollCode The effortCertificationDerivedPayrollCode to set.
     */
    public void setEffortCertificationDerivedPayrollCode(String effortCertificationDerivedPayrollCode) {
        this.effortCertificationDerivedPayrollCode = effortCertificationDerivedPayrollCode;
    }

    /**
     * Gets the effortCertificationDerivedPayrollPercent attribute.
     * 
     * @return Returns the effortCertificationDerivedPayrollPercent.
     */
    public Integer getEffortCertificationDerivedPayrollPercent() {
        return effortCertificationDerivedPayrollPercent;
    }

    /**
     * Sets the effortCertificationDerivedPayrollPercent attribute value.
     * 
     * @param effortCertificationDerivedPayrollPercent The effortCertificationDerivedPayrollPercent to set.
     */
    public void setEffortCertificationDerivedPayrollPercent(Integer effortCertificationDerivedPayrollPercent) {
        this.effortCertificationDerivedPayrollPercent = effortCertificationDerivedPayrollPercent;
    }

    /**
     * Gets the effortCertificationCostSharingCode attribute.
     * 
     * @return Returns the effortCertificationCostSharingCode.
     */
    public String getEffortCertificationCostSharingCode() {
        return effortCertificationCostSharingCode;
    }

    /**
     * Sets the effortCertificationCostSharingCode attribute value.
     * 
     * @param effortCertificationCostSharingCode The effortCertificationCostSharingCode to set.
     */
    public void setEffortCertificationCostSharingCode(String effortCertificationCostSharingCode) {
        this.effortCertificationCostSharingCode = effortCertificationCostSharingCode;
    }

    /**
     * Gets the effortCertificationCostSharingPercent attribute.
     * 
     * @return Returns the effortCertificationCostSharingPercent.
     */
    public Integer getEffortCertificationCostSharingPercent() {
        return effortCertificationCostSharingPercent;
    }

    /**
     * Sets the effortCertificationCostSharingPercent attribute value.
     * 
     * @param effortCertificationCostSharingPercent The effortCertificationCostSharingPercent to set.
     */
    public void setEffortCertificationCostSharingPercent(Integer effortCertificationCostSharingPercent) {
        this.effortCertificationCostSharingPercent = effortCertificationCostSharingPercent;
    }

    /**
     * Gets the effortCertificationCalculatedOverallCode attribute.
     * 
     * @return Returns the effortCertificationCalculatedOverallCode.
     */
    public String getEffortCertificationCalculatedOverallCode() {
        return effortCertificationCalculatedOverallCode;
    }

    /**
     * Sets the effortCertificationCalculatedOverallCode attribute value.
     * 
     * @param effortCertificationCalculatedOverallCode The effortCertificationCalculatedOverallCode to set.
     */
    public void setEffortCertificationCalculatedOverallCode(String effortCertificationCalculatedOverallCode) {
        this.effortCertificationCalculatedOverallCode = effortCertificationCalculatedOverallCode;
    }

    /**
     * Gets the effortCertificationCalculatedOverallPercent attribute.
     * 
     * @return Returns the effortCertificationCalculatedOverallPercent.
     */
    public Integer getEffortCertificationCalculatedOverallPercent() {
        return effortCertificationCalculatedOverallPercent;
    }

    /**
     * Sets the effortCertificationCalculatedOverallPercent attribute value.
     * 
     * @param effortCertificationCalculatedOverallPercent The effortCertificationCalculatedOverallPercent to set.
     */
    public void setEffortCertificationCalculatedOverallPercent(Integer effortCertificationCalculatedOverallPercent) {
        this.effortCertificationCalculatedOverallPercent = effortCertificationCalculatedOverallPercent;
    }

    /**
     * Gets the effortCertificationUpdatedOverallCode attribute.
     * 
     * @return Returns the effortCertificationUpdatedOverallCode.
     */
    public String getEffortCertificationUpdatedOverallCode() {
        return effortCertificationUpdatedOverallCode;
    }

    /**
     * Sets the effortCertificationUpdatedOverallCode attribute value.
     * 
     * @param effortCertificationUpdatedOverallCode The effortCertificationUpdatedOverallCode to set.
     */
    public void setEffortCertificationUpdatedOverallCode(String effortCertificationUpdatedOverallCode) {
        this.effortCertificationUpdatedOverallCode = effortCertificationUpdatedOverallCode;
    }

    /**
     * Gets the effortCertificationUpdatedOverallPercent attribute.
     * 
     * @return Returns the effortCertificationUpdatedOverallPercent.
     */
    public Integer getEffortCertificationUpdatedOverallPercent() {
        return effortCertificationUpdatedOverallPercent;
    }

    /**
     * Sets the effortCertificationUpdatedOverallPercent attribute value.
     * 
     * @param effortCertificationUpdatedOverallPercent The effortCertificationUpdatedOverallPercent to set.
     */
    public void setEffortCertificationUpdatedOverallPercent(Integer effortCertificationUpdatedOverallPercent) {
        this.effortCertificationUpdatedOverallPercent = effortCertificationUpdatedOverallPercent;
    }

    /**
     * Gets the effortCertificationProratedCode attribute.
     * 
     * @return Returns the effortCertificationProratedCode.
     */
    public String getEffortCertificationProratedCode() {
        return effortCertificationProratedCode;
    }

    /**
     * Sets the effortCertificationProratedCode attribute value.
     * 
     * @param effortCertificationProratedCode The effortCertificationProratedCode to set.
     */
    public void setEffortCertificationProratedCode(String effortCertificationProratedCode) {
        this.effortCertificationProratedCode = effortCertificationProratedCode;
    }

    /**
     * Gets the effortCertificationProratedPercent attribute.
     * 
     * @return Returns the effortCertificationProratedPercent.
     */
    public Integer getEffortCertificationProratedPercent() {
        return effortCertificationProratedPercent;
    }

    /**
     * Sets the effortCertificationProratedPercent attribute value.
     * 
     * @param effortCertificationProratedPercent The effortCertificationProratedPercent to set.
     */
    public void setEffortCertificationProratedPercent(Integer effortCertificationProratedPercent) {
        this.effortCertificationProratedPercent = effortCertificationProratedPercent;
    }

    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return Returns the financialDocumentPostingYear.
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }

    /**
     * Sets the financialDocumentPostingYear attribute value.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }

    /**
     * Gets the costShareSourceSubAccountNumber attribute.
     * 
     * @return Returns the costShareSourceSubAccountNumber.
     */
    public String getCostShareSourceSubAccountNumber() {
        return costShareSourceSubAccountNumber;
    }

    /**
     * Sets the costShareSourceSubAccountNumber attribute value.
     * 
     * @param costShareSourceSubAccountNumber The costShareSourceSubAccountNumber to set.
     */
    public void setCostShareSourceSubAccountNumber(String costShareSourceSubAccountNumber) {
        this.costShareSourceSubAccountNumber = costShareSourceSubAccountNumber;
    }

    /**
     * Gets the effortCertificationOriginalPayrollAmount attribute.
     * 
     * @return Returns the effortCertificationOriginalPayrollAmount.
     */
    public KualiDecimal getEffortCertificationOriginalPayrollAmount() {
        return effortCertificationOriginalPayrollAmount;
    }

    /**
     * Sets the effortCertificationOriginalPayrollAmount attribute value.
     * 
     * @param effortCertificationOriginalPayrollAmount The effortCertificationOriginalPayrollAmount to set.
     */
    public void setEffortCertificationOriginalPayrollAmount(KualiDecimal effortCertificationOriginalPayrollAmount) {
        this.effortCertificationOriginalPayrollAmount = effortCertificationOriginalPayrollAmount;
    }

    /**
     * Gets the financialDocument attribute.
     * 
     * @return Returns the financialDocument.
     */
    public EffortCertificationDocument getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute value.
     * 
     * @param financialDocument The financialDocument to set.
     */
    public void setFinancialDocument(EffortCertificationDocument financialDocument) {
        this.financialDocument = financialDocument;
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
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
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
    @Deprecated
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the sourceChartOfAccounts attribute.
     * 
     * @return Returns the sourceChartOfAccounts.
     */
    public Chart getSourceChartOfAccounts() {
        return sourceChartOfAccounts;
    }

    /**
     * Sets the sourceChartOfAccounts attribute value.
     * 
     * @param sourceChartOfAccounts The sourceChartOfAccounts to set.
     */
    @Deprecated
    public void setSourceChartOfAccounts(Chart sourceChartOfAccounts) {
        this.sourceChartOfAccounts = sourceChartOfAccounts;
    }

    /**
     * Gets the sourceAccount attribute.
     * 
     * @return Returns the sourceAccount.
     */
    public Account getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Sets the sourceAccount attribute value.
     * 
     * @param sourceAccount The sourceAccount to set.
     */
    @Deprecated
    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
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
    @Deprecated
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("positionNumber", this.positionNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("sourceChartOfAccountsCode", this.sourceChartOfAccountsCode);
        m.put("sourceAccountNumber", this.sourceAccountNumber);
        return m;
    }
}
