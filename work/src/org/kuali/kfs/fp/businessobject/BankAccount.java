package org.kuali.module.financial.bo;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */

public class BankAccount extends BusinessObjectBase {

    private static final long serialVersionUID = -1056266362255635896L;
    private String finDocumentBankAccountDesc;
    private String finDocumentBankAccountNumber;
    private String financialDocumentBankCode;
    private String chartOfAccountsCode;
    private String universityAcctChartOfAcctCd;
    private String universityAccountNumber;
    private String organizationCode;
    private String cashOffsetFinancialChartOfAccountCode;
    private String cashOffsetAccountNumber;
    private String cashOffsetObjectCode;
    private String cashOffsetSubObjectCode;

    private Chart chart;
    private Chart universityAcctChartOfAcct;
    private Bank bank;
    private Org organization;
    private Account universityAccount;
    private Chart cashOffsetFinancialChartOfAccount;
    private Account cashOffsetAccount;
    private ObjectCode cashOffsetObject;

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
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
     * Gets the universityAccountNumber attribute.
     * 
     * @return Returns the universityAccountNumber.
     */
    public String getUniversityAccountNumber() {
        return universityAccountNumber;
    }

    /**
     * Sets the universityAccountNumber attribute value.
     * 
     * @param universityAccountNumber The universityAccountNumber to set.
     */
    public void setUniversityAccountNumber(String universityAccountNumber) {
        this.universityAccountNumber = universityAccountNumber;
    }

    /**
     * Gets the universityAcctChartOfAcctCd attribute.
     * 
     * @return Returns the universityAcctChartOfAcctCd.
     */
    public String getUniversityAcctChartOfAcctCd() {
        return universityAcctChartOfAcctCd;
    }

    /**
     * Sets the universityAcctChartOfAcctCd attribute value.
     * 
     * @param universityAcctChartOfAcctCd The universityAcctChartOfAcctCd to set.
     */
    public void setUniversityAcctChartOfAcctCd(String universityAcctChartOfAcctCd) {
        this.universityAcctChartOfAcctCd = universityAcctChartOfAcctCd;
    }

    /**
     * Default no-arg constructor.
     */
    public BankAccount() {

    }

    /**
     * Gets the bank attribute.
     * 
     * @return - Returns the financialDocumentBankCode
     * 
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank attribute.
     * 
     * @param bank The bank to set.
     * @deprecated
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Gets the financialDocumentBankCode attribute.
     * 
     * @return - Returns the financialDocumentBankCode
     * 
     */
    public String getFinancialDocumentBankCode() {
        return financialDocumentBankCode;
    }

    /**
     * Sets the financialDocumentBankCode attribute.
     * 
     * @param financialDocumentBankCode The financialDocumentBankCode to set.
     * 
     */
    public void setFinancialDocumentBankCode(String financialDocumentBankCode) {
        this.financialDocumentBankCode = financialDocumentBankCode;
    }

    /**
     * Gets the finDocumentBankAccountDesc attribute.
     * 
     * @return - Returns the finDocumentBankAccountDesc
     * 
     */
    public String getFinDocumentBankAccountDesc() {
        return finDocumentBankAccountDesc;
    }

    /**
     * Sets the finDocumentBankAccountDesc attribute.
     * 
     * @param finDocumentBankAccountDesc The finDocumentBankAccountDesc to set.
     * 
     */
    public void setFinDocumentBankAccountDesc(String finDocumentBankAccountDesc) {
        this.finDocumentBankAccountDesc = finDocumentBankAccountDesc;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return - Returns the organization
     * 
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * Gets the finDocumentBankAccount attribute.
     * 
     * @return - Returns the finDocumentBankAccountNumber
     * 
     */
    public String getFinDocumentBankAccountNumber() {
        return finDocumentBankAccountNumber;
    }

    /**
     * Sets the finDocumentBankAccountNumber attribute.
     * 
     * @param finDocumentBankAccountNumber The finDocumentBankAccountNumber to set.
     * 
     */
    public void setFinDocumentBankAccountNumber(String finDocumentBankAccountNumber) {
        this.finDocumentBankAccountNumber = finDocumentBankAccountNumber;
    }

    /**
     * @return Returns the cashOffsetAccountNumber.
     */
    public String getCashOffsetAccountNumber() {
        return cashOffsetAccountNumber;
    }

    /**
     * @param cashOffsetAccountNumber The cashOffsetAccountNumber to set.
     */
    public void setCashOffsetAccountNumber(String cashOffsetAccountNumber) {
        this.cashOffsetAccountNumber = cashOffsetAccountNumber;
    }

    /**
     * @return Returns the cashOffsetFinancialChartOfAccountCode.
     */
    public String getCashOffsetFinancialChartOfAccountCode() {
        return cashOffsetFinancialChartOfAccountCode;
    }

    /**
     * @param cashOffsetFinancialChartOfAccountCode The cashOffsetFinancialChartOfAccountCode to set.
     */
    public void setCashOffsetFinancialChartOfAccountCode(String cashOffsetFinancialChartOfAccountCode) {
        this.cashOffsetFinancialChartOfAccountCode = cashOffsetFinancialChartOfAccountCode;
    }

    /**
     * @return Returns the cashOffsetObjectCode.
     */
    public String getCashOffsetObjectCode() {
        return cashOffsetObjectCode;
    }

    /**
     * @param cashOffsetObjectCode The cashOffsetObjectCode to set.
     */
    public void setCashOffsetObjectCode(String cashOffsetObjectCode) {
        this.cashOffsetObjectCode = cashOffsetObjectCode;
    }

    /**
     * @return Returns the cashOffsetSubObjectCode.
     */
    public String getCashOffsetSubObjectCode() {
        return cashOffsetSubObjectCode;
    }

    /**
     * @param cashOffsetSubObjectCode The cashOffsetSubObjectCode to set.
     */
    public void setCashOffsetSubObjectCode(String cashOffsetSubObjectCode) {
        this.cashOffsetSubObjectCode = cashOffsetSubObjectCode;
    }

    /**
     * Gets the universityAccount attribute.
     * 
     * @return - Returns the universityAccount
     * 
     */
    public Account getUniversityAccount() {
        return universityAccount;
    }

    /**
     * Sets the universityAccount attribute.
     * 
     * @param universityAccount The universityAccount to set.
     * @deprecated
     */
    public void setUniversityAccount(Account universityAccount) {
        this.universityAccount = universityAccount;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return - Returns the chart
     * 
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     * 
     * @param chart The chart to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the universityAcctChartOfAcct attribute.
     * 
     * @return - Returns the universityAcctChartOfAcct
     * 
     */
    public Chart getUniversityAcctChartOfAcct() {
        return universityAcctChartOfAcct;
    }

    /**
     * Sets the universityAcctChartOfAcct attribute.
     * 
     * @param universityAcctChartOfAcct The universityAcctChartOfAcct to set.
     * @deprecated
     */
    public void setUniversityAcctChartOfAcct(Chart universityAcctChartOfAcct) {
        this.universityAcctChartOfAcct = universityAcctChartOfAcct;
    }

    /**
     * Gets the cashOffsetAccount attribute.
     * 
     * @return - Returns the cashOffsetAccount
     * 
     */
    public Account getCashOffsetAccount() {
        return cashOffsetAccount;
    }

    /**
     * Sets the cashOffsetAccount attribute.
     * 
     * @param cashOffsetAccount The cashOffsetAccount to set.
     * @deprecated
     */
    public void setCashOffsetAccount(Account cashOffsetAccount) {
        this.cashOffsetAccount = cashOffsetAccount;
    }

    /**
     * Gets the cashOffsetFinancialChartOfAccount attribute.
     * 
     * @return - Returns the cashOffsetFinancialChartOfAccount
     * 
     */
    public Chart getCashOffsetFinancialChartOfAccount() {
        return cashOffsetFinancialChartOfAccount;
    }

    /**
     * Sets the cashOffsetFinancialChartOfAccount attribute.
     * 
     * @param cashOffsetFinancialChartOfAccount The cashOffsetFinancialChartOfAccount to set.
     * @deprecated
     */
    public void setCashOffsetFinancialChartOfAccount(Chart cashOffsetFinancialChartOfAccount) {
        this.cashOffsetFinancialChartOfAccount = cashOffsetFinancialChartOfAccount;
    }

    /**
     * Gets the cashOffsetObject attribute.
     * 
     * @return Returns the cashOffsetObject.
     */
    public ObjectCode getCashOffsetObject() {
        return cashOffsetObject;
    }

    /**
     * Sets the cashOffsetObject attribute value.
     * 
     * @param cashOffsetObject The cashOffsetObject to set.
     * @deprecated
     */
    public void setCashOffsetObject(ObjectCode cashOffsetObject) {
        this.cashOffsetObject = cashOffsetObject;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("bankCode", getFinancialDocumentBankCode());
        m.put("bankAccountNumber", getFinDocumentBankAccountNumber());
        return m;
    }


}
