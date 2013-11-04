/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCodeCurrent;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Provides basic implementation for the accounting lines used in the Transfer of Funds documents in the Endowment module.
 */
public class EndowmentAccountingLineBase extends PersistableBusinessObjectBase implements EndowmentAccountingLine {
    private static Logger LOG = Logger.getLogger(AccountingLineBase.class);

    private String documentNumber;
    private Integer accountingLineNumber; // relative to the grouping of acctng lines
    private KualiDecimal amount;
    private String organizationReferenceId;
    protected String financialDocumentLineTypeCode;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String subAccountNumber;
    private String financialSubObjectCode;
    private String projectCode;

    // bo references
    private Chart chart;
    private Account account;
    private ObjectCodeCurrent objectCode;
    private SubAccount subAccount;
    private SubObjectCodeCurrent subObjectCode;
    private ProjectCode project;

    /**
     * This constructor sets up empty instances for the dependent objects.
     */
    public EndowmentAccountingLineBase() {
        setAmount(KualiDecimal.ZERO);
        chart = new Chart();
        account = new Account();
        objectCode = new ObjectCodeCurrent();
        subAccount = new SubAccount();
        subObjectCode = new SubObjectCodeCurrent();
        project = new ProjectCode();
    }


    /**
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * @param chart The chartOfAccountsCode to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @return Returns the amount.
     */
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return Returns the objectCode.
     */
    public ObjectCodeCurrent getObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCode(ObjectCodeCurrent objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * @return Returns the organizationReferenceId.
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * Gets the accountingLineNumber.
     * 
     * @return accountingLineNumber
     */
    public Integer getAccountingLineNumber() {
        return accountingLineNumber;
    }


    /**
     * Sets the accountingLineNumber.
     * 
     * @param accountingLineNumber
     */
    public void setAccountingLineNumber(Integer accountingLineNumber) {
        this.accountingLineNumber = accountingLineNumber;
    }

    /**
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @return Returns the subObjectCode.
     */
    public SubObjectCodeCurrent getSubObjectCode() {
        return subObjectCode;
    }

    /**
     * @param subObjectCode The subObjectCode to set.
     * @deprecated
     */
    public void setSubObjectCode(SubObjectCodeCurrent subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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
        // if accounts can't cross charts, set chart code whenever account number is set
        // SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(this);
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
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * @return Returns the financialDocumentLineTypeCode.
     */
    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    /**
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }

    /**
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * @param project The project to set.
     * @deprecated
     */
    public void setProject(ProjectCode project) {
        this.project = project;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        m.put("accountingLineNumber", accountingLineNumber);
        m.put("amount", amount);
        m.put("chartOfAccountsCode", getChartOfAccountsCode());
        m.put("accountNumber", getAccountNumber());
        m.put("objectCode", getFinancialObjectCode());
        m.put("subAccountNumber", getSubAccountNumber());
        m.put("financialSubObjectCode", getFinancialSubObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("organizationReferenceId", getOrganizationReferenceId());

        return m;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine#isSourceAccountingLine()
     */
    public boolean isSourceAccountingLine() {
        return (this instanceof SourceEndowmentAccountingLine);
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine#isTargetAccountingLine()
     */
    public boolean isTargetAccountingLine() {
        return (this instanceof TargetEndowmentAccountingLine);
    }

}
