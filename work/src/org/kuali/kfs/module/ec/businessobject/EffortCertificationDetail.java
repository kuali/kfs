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
    private String sourceFinancialChartOfAccountsCode;
    private String sourceAccountNumber;
    private KualiDecimal a21LaborPayrollAmount;
    private String a21LaborDerivedPayrollCode;
    private Integer a21LaborDerivedPayrollPercent;
    private String a21LaborCostSharingCode;
    private Integer a21LaborCostSharingPercent;
    private String a21LaborCalculatedOverallCode;
    private Integer a21LaborCalculatedOverallPercent;
    private String a21LaborUpdatedOverallCode;
    private Integer a21LaborUpdatedOverallPercent;
    private String a21LaborProratedCode;
    private Integer a21LaborProratedPercent;
    private Integer financialDocumentPostingYear;
    private String costShareSourceSubAccountNumber;
    private KualiDecimal a21LaborOriginalPayrollAmount;

    private DocumentHeader financialDocument;
    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private Chart sourceFinancialChartOfAccounts;
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
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
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
     * Gets the sourceFinancialChartOfAccountsCode attribute.
     * 
     * @return Returns the sourceFinancialChartOfAccountsCode
     */
    public String getSourceFinancialChartOfAccountsCode() {
        return sourceFinancialChartOfAccountsCode;
    }

    /**
     * Sets the sourceFinancialChartOfAccountsCode attribute.
     * 
     * @param sourceFinancialChartOfAccountsCode The sourceFinancialChartOfAccountsCode to set.
     */
    public void setSourceFinancialChartOfAccountsCode(String sourceFinancialChartOfAccountsCode) {
        this.sourceFinancialChartOfAccountsCode = sourceFinancialChartOfAccountsCode;
    }


    /**
     * Gets the sourceAccountNumber attribute.
     * 
     * @return Returns the sourceAccountNumber
     */
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    /**
     * Sets the sourceAccountNumber attribute.
     * 
     * @param sourceAccountNumber The sourceAccountNumber to set.
     */
    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }


    /**
     * Gets the a21LaborPayrollAmount attribute.
     * 
     * @return Returns the a21LaborPayrollAmount
     */
    public KualiDecimal getA21LaborPayrollAmount() {
        return a21LaborPayrollAmount;
    }

    /**
     * Sets the a21LaborPayrollAmount attribute.
     * 
     * @param a21LaborPayrollAmount The a21LaborPayrollAmount to set.
     */
    public void setA21LaborPayrollAmount(KualiDecimal a21LaborPayrollAmount) {
        this.a21LaborPayrollAmount = a21LaborPayrollAmount;
    }


    /**
     * Gets the a21LaborDerivedPayrollCode attribute.
     * 
     * @return Returns the a21LaborDerivedPayrollCode
     */
    public String getA21LaborDerivedPayrollCode() {
        return a21LaborDerivedPayrollCode;
    }

    /**
     * Sets the a21LaborDerivedPayrollCode attribute.
     * 
     * @param a21LaborDerivedPayrollCode The a21LaborDerivedPayrollCode to set.
     */
    public void setA21LaborDerivedPayrollCode(String a21LaborDerivedPayrollCode) {
        this.a21LaborDerivedPayrollCode = a21LaborDerivedPayrollCode;
    }


    /**
     * Gets the a21LaborDerivedPayrollPercent attribute.
     * 
     * @return Returns the a21LaborDerivedPayrollPercent
     */
    public Integer getA21LaborDerivedPayrollPercent() {
        return a21LaborDerivedPayrollPercent;
    }

    /**
     * Sets the a21LaborDerivedPayrollPercent attribute.
     * 
     * @param a21LaborDerivedPayrollPercent The a21LaborDerivedPayrollPercent to set.
     */
    public void setA21LaborDerivedPayrollPercent(Integer a21LaborDerivedPayrollPercent) {
        this.a21LaborDerivedPayrollPercent = a21LaborDerivedPayrollPercent;
    }


    /**
     * Gets the a21LaborCostSharingCode attribute.
     * 
     * @return Returns the a21LaborCostSharingCode
     */
    public String getA21LaborCostSharingCode() {
        return a21LaborCostSharingCode;
    }

    /**
     * Sets the a21LaborCostSharingCode attribute.
     * 
     * @param a21LaborCostSharingCode The a21LaborCostSharingCode to set.
     */
    public void setA21LaborCostSharingCode(String a21LaborCostSharingCode) {
        this.a21LaborCostSharingCode = a21LaborCostSharingCode;
    }


    /**
     * Gets the a21LaborCostSharingPercent attribute.
     * 
     * @return Returns the a21LaborCostSharingPercent
     */
    public Integer getA21LaborCostSharingPercent() {
        return a21LaborCostSharingPercent;
    }

    /**
     * Sets the a21LaborCostSharingPercent attribute.
     * 
     * @param a21LaborCostSharingPercent The a21LaborCostSharingPercent to set.
     */
    public void setA21LaborCostSharingPercent(Integer a21LaborCostSharingPercent) {
        this.a21LaborCostSharingPercent = a21LaborCostSharingPercent;
    }


    /**
     * Gets the a21LaborCalculatedOverallCode attribute.
     * 
     * @return Returns the a21LaborCalculatedOverallCode
     */
    public String getA21LaborCalculatedOverallCode() {
        return a21LaborCalculatedOverallCode;
    }

    /**
     * Sets the a21LaborCalculatedOverallCode attribute.
     * 
     * @param a21LaborCalculatedOverallCode The a21LaborCalculatedOverallCode to set.
     */
    public void setA21LaborCalculatedOverallCode(String a21LaborCalculatedOverallCode) {
        this.a21LaborCalculatedOverallCode = a21LaborCalculatedOverallCode;
    }


    /**
     * Gets the a21LaborCalculatedOverallPercent attribute.
     * 
     * @return Returns the a21LaborCalculatedOverallPercent
     */
    public Integer getA21LaborCalculatedOverallPercent() {
        return a21LaborCalculatedOverallPercent;
    }

    /**
     * Sets the a21LaborCalculatedOverallPercent attribute.
     * 
     * @param a21LaborCalculatedOverallPercent The a21LaborCalculatedOverallPercent to set.
     */
    public void setA21LaborCalculatedOverallPercent(Integer a21LaborCalculatedOverallPercent) {
        this.a21LaborCalculatedOverallPercent = a21LaborCalculatedOverallPercent;
    }


    /**
     * Gets the a21LaborUpdatedOverallCode attribute.
     * 
     * @return Returns the a21LaborUpdatedOverallCode
     */
    public String getA21LaborUpdatedOverallCode() {
        return a21LaborUpdatedOverallCode;
    }

    /**
     * Sets the a21LaborUpdatedOverallCode attribute.
     * 
     * @param a21LaborUpdatedOverallCode The a21LaborUpdatedOverallCode to set.
     */
    public void setA21LaborUpdatedOverallCode(String a21LaborUpdatedOverallCode) {
        this.a21LaborUpdatedOverallCode = a21LaborUpdatedOverallCode;
    }


    /**
     * Gets the a21LaborUpdatedOverallPercent attribute.
     * 
     * @return Returns the a21LaborUpdatedOverallPercent
     */
    public Integer getA21LaborUpdatedOverallPercent() {
        return a21LaborUpdatedOverallPercent;
    }

    /**
     * Sets the a21LaborUpdatedOverallPercent attribute.
     * 
     * @param a21LaborUpdatedOverallPercent The a21LaborUpdatedOverallPercent to set.
     */
    public void setA21LaborUpdatedOverallPercent(Integer a21LaborUpdatedOverallPercent) {
        this.a21LaborUpdatedOverallPercent = a21LaborUpdatedOverallPercent;
    }


    /**
     * Gets the a21LaborProratedCode attribute.
     * 
     * @return Returns the a21LaborProratedCode
     */
    public String getA21LaborProratedCode() {
        return a21LaborProratedCode;
    }

    /**
     * Sets the a21LaborProratedCode attribute.
     * 
     * @param a21LaborProratedCode The a21LaborProratedCode to set.
     */
    public void setA21LaborProratedCode(String a21LaborProratedCode) {
        this.a21LaborProratedCode = a21LaborProratedCode;
    }


    /**
     * Gets the a21LaborProratedPercent attribute.
     * 
     * @return Returns the a21LaborProratedPercent
     */
    public Integer getA21LaborProratedPercent() {
        return a21LaborProratedPercent;
    }

    /**
     * Sets the a21LaborProratedPercent attribute.
     * 
     * @param a21LaborProratedPercent The a21LaborProratedPercent to set.
     */
    public void setA21LaborProratedPercent(Integer a21LaborProratedPercent) {
        this.a21LaborProratedPercent = a21LaborProratedPercent;
    }


    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return Returns the financialDocumentPostingYear
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }

    /**
     * Sets the financialDocumentPostingYear attribute.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }


    /**
     * Gets the costShareSourceSubAccountNumber attribute.
     * 
     * @return Returns the costShareSourceSubAccountNumber
     */
    public String getCostShareSourceSubAccountNumber() {
        return costShareSourceSubAccountNumber;
    }

    /**
     * Sets the costShareSourceSubAccountNumber attribute.
     * 
     * @param costShareSourceSubAccountNumber The costShareSourceSubAccountNumber to set.
     */
    public void setCostShareSourceSubAccountNumber(String costShareSourceSubAccountNumber) {
        this.costShareSourceSubAccountNumber = costShareSourceSubAccountNumber;
    }


    /**
     * Gets the a21LaborOriginalPayrollAmount attribute.
     * 
     * @return Returns the a21LaborOriginalPayrollAmount
     */
    public KualiDecimal getA21LaborOriginalPayrollAmount() {
        return a21LaborOriginalPayrollAmount;
    }

    /**
     * Sets the a21LaborOriginalPayrollAmount attribute.
     * 
     * @param a21LaborOriginalPayrollAmount The a21LaborOriginalPayrollAmount to set.
     */
    public void setA21LaborOriginalPayrollAmount(KualiDecimal a21LaborOriginalPayrollAmount) {
        this.a21LaborOriginalPayrollAmount = a21LaborOriginalPayrollAmount;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     * 
     * @param financialObject The financialObject to set.
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     */
    @Deprecated
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the sourceFinancialChartOfAccounts attribute.
     * 
     * @return Returns the sourceFinancialChartOfAccounts
     */
    public Chart getSourceFinancialChartOfAccounts() {
        return sourceFinancialChartOfAccounts;
    }

    /**
     * Sets the sourceFinancialChartOfAccounts attribute.
     * 
     * @param sourceFinancialChartOfAccounts The sourceFinancialChartOfAccounts to set.
     */
    @Deprecated
    public void setSourceFinancialChartOfAccounts(Chart sourceFinancialChartOfAccounts) {
        this.sourceFinancialChartOfAccounts = sourceFinancialChartOfAccounts;
    }

    /**
     * Gets the sourceAccount attribute.
     * 
     * @return Returns the sourceAccount
     */
    public Account getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Sets the sourceAccount attribute.
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
     * Gets the financialDocument attribute.
     * 
     * @return Returns the financialDocument
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute.
     * 
     * @param financialDocument The financialDocument to set.
     */
    @Deprecated
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
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
        m.put("sourceFinancialChartOfAccountsCode", this.sourceFinancialChartOfAccountsCode);
        m.put("sourceAccountNumber", this.sourceAccountNumber);
        return m;
    }

}
