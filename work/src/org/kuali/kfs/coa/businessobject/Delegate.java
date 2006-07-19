package org.kuali.module.chart.bo;

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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.DocumentType;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Delegate extends BusinessObjectBase {

    private static final long serialVersionUID = 6883162275377881235L;

    /**
     * Default no-arg constructor.
     */
    public Delegate() {
        this.finDocApprovalFromThisAmt = new KualiDecimal(0);
        this.finDocApprovalToThisAmount = new KualiDecimal(0);
    }

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialDocumentTypeCode;
    private String accountDelegateSystemId;
    private KualiDecimal finDocApprovalFromThisAmt;
    private boolean accountsDelegatePrmrtIndicator;
    private boolean accountDelegateActiveIndicator;
    private Timestamp accountDelegateStartDate;
    private KualiDecimal finDocApprovalToThisAmount;

    private Chart chart;
    private Account account;
    private DocumentType documentType;
    private KualiUser accountDelegate;

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
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return - Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the accountDelegateSystemId attribute.
     * 
     * @return Returns the accountDelegateSystemId.
     */
    public String getAccountDelegateSystemId() {
        return accountDelegateSystemId;
    }

    /**
     * Sets the accountDelegateSystemId attribute value.
     * 
     * @param accountDelegateSystemId The accountDelegateSystemId to set.
     */
    public void setAccountDelegateSystemId(String accountDelegateSystemId) {
        this.accountDelegateSystemId = accountDelegateSystemId;
    }

    /**
     * Gets the finDocApprovalFromThisAmt attribute.
     * 
     * @return - Returns the finDocApprovalFromThisAmt
     * 
     */
    public KualiDecimal getFinDocApprovalFromThisAmt() {
        return finDocApprovalFromThisAmt;
    }

    /**
     * Sets the finDocApprovalFromThisAmt attribute.
     * 
     * @param finDocApprovalFromThisAmt The finDocApprovalFromThisAmt to set.
     * 
     */
    public void setFinDocApprovalFromThisAmt(KualiDecimal finDocApprovalFromThisAmt) {
        this.finDocApprovalFromThisAmt = finDocApprovalFromThisAmt;
    }

    /**
     * Gets the accountsDelegatePrmrtIndicator attribute.
     * 
     * @return - Returns the accountsDelegatePrmrtIndicator
     * 
     */
    public boolean isAccountsDelegatePrmrtIndicator() {
        return accountsDelegatePrmrtIndicator;
    }

    /**
     * Sets the accountsDelegatePrmrtIndicator attribute.
     * 
     * @param accountsDelegatePrmrtIndicator The accountsDelegatePrmrtIndicator to set.
     * @deprecated
     */
    public void setAccountsDelegatePrmrtIndicator(boolean accountsDelegatePrmrtIndicator) {
        this.accountsDelegatePrmrtIndicator = accountsDelegatePrmrtIndicator;
    }

    /**
     * Gets the accountDelegateActiveIndicator attribute.
     * 
     * @return - Returns the accountDelegateActiveIndicator
     * 
     */
    public boolean isAccountDelegateActiveIndicator() {
        return accountDelegateActiveIndicator;
    }

    /**
     * Sets the accountDelegateActiveIndicator attribute.
     * 
     * @param accountDelegateActiveIndicator The accountDelegateActiveIndicator to set.
     * @deprecated
     */
    public void setAccountDelegateActiveIndicator(boolean accountDelegateActiveIndicator) {
        this.accountDelegateActiveIndicator = accountDelegateActiveIndicator;
    }

    /**
     * Gets the accountDelegateStartDate attribute.
     * 
     * @return - Returns the accountDelegateStartDate
     * 
     */
    public Timestamp getAccountDelegateStartDate() {
        return accountDelegateStartDate;
    }

    /**
     * Sets the accountDelegateStartDate attribute.
     * 
     * @param accountDelegateStartDate The accountDelegateStartDate to set.
     * 
     */
    public void setAccountDelegateStartDate(Timestamp accountDelegateStartDate) {
        this.accountDelegateStartDate = accountDelegateStartDate;
    }

    /**
     * Gets the finDocApprovalToThisAmount attribute.
     * 
     * @return - Returns the finDocApprovalToThisAmount
     * 
     */
    public KualiDecimal getFinDocApprovalToThisAmount() {
        return finDocApprovalToThisAmount;
    }

    /**
     * Sets the finDocApprovalToThisAmount attribute.
     * 
     * @param finDocApprovalToThisAmount The finDocApprovalToThisAmount to set.
     * 
     */
    public void setFinDocApprovalToThisAmount(KualiDecimal finDocApprovalToThisAmount) {
        this.finDocApprovalToThisAmount = finDocApprovalToThisAmount;
    }

    /**
     * Gets the account attribute.
     * 
     * @return - Returns the account
     * 
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
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
     * Gets the accountDelegate attribute.
     * 
     * @return Returns the accountDelegate.
     */
    public KualiUser getAccountDelegate() {
        return accountDelegate;
    }

    /**
     * Sets the accountDelegate attribute value.
     * 
     * @param accountDelegate The accountDelegate to set.
     */
    public void setAccountDelegate(KualiUser accountDelegate) {
        this.accountDelegate = accountDelegate;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("documentTypeCode", this.financialDocumentTypeCode);
        m.put("accountDelegateSystemId", this.accountDelegateSystemId);

        return m;
    }
}