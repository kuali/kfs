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

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCodeCurrent;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OffsetAccount extends BusinessObjectBase {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialOffsetObjectCode;
    private String financialOffsetChartOfAccountCode;
    private String financialOffsetAccountNumber;

    private Chart chart;
    private Account account;
    private Chart financialOffsetChartOfAccount;
    private Account financialOffsetAccount;
    private ObjectCodeCurrent objectCodeCurrent;

    /**
     * Default constructor.
     */
    public OffsetAccount() {

    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return - Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return - Returns the accountNumber
     * 
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     * 
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the financialOffsetObjectCode attribute.
     * 
     * @return - Returns the financialOffsetObjectCode
     * 
     */
    public String getFinancialOffsetObjectCode() {
        return financialOffsetObjectCode;
    }

    /**
     * Sets the financialOffsetObjectCode attribute.
     * 
     * @param financialOffsetObjectCode The financialOffsetObjectCode to set.
     * 
     */
    public void setFinancialOffsetObjectCode(String financialOffsetObjectCode) {
        this.financialOffsetObjectCode = financialOffsetObjectCode;
    }


    /**
     * Gets the financialOffsetChartOfAccountCode attribute.
     * 
     * @return - Returns the financialOffsetChartOfAccountCode
     * 
     */
    public String getFinancialOffsetChartOfAccountCode() {
        return financialOffsetChartOfAccountCode;
    }

    /**
     * Sets the financialOffsetChartOfAccountCode attribute.
     * 
     * @param financialOffsetChartOfAccountCode The financialOffsetChartOfAccountCode to set.
     * 
     */
    public void setFinancialOffsetChartOfAccountCode(String financialOffsetChartOfAccountCode) {
        this.financialOffsetChartOfAccountCode = financialOffsetChartOfAccountCode;
    }


    /**
     * Gets the financialOffsetAccountNumber attribute.
     * 
     * @return - Returns the financialOffsetAccountNumber
     * 
     */
    public String getFinancialOffsetAccountNumber() {
        return financialOffsetAccountNumber;
    }

    /**
     * Sets the financialOffsetAccountNumber attribute.
     * 
     * @param financialOffsetAccountNumber The financialOffsetAccountNumber to set.
     * 
     */
    public void setFinancialOffsetAccountNumber(String financialOffsetAccountNumber) {
        this.financialOffsetAccountNumber = financialOffsetAccountNumber;
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
     * Gets the financialOffsetChartOfAccount attribute.
     * 
     * @return - Returns the financialOffsetChartOfAccount
     * 
     */
    public Chart getFinancialOffsetChartOfAccount() {
        return financialOffsetChartOfAccount;
    }

    /**
     * Sets the financialOffsetChartOfAccount attribute.
     * 
     * @param financialOffsetChartOfAccount The financialOffsetChartOfAccount to set.
     * @deprecated
     */
    public void setFinancialOffsetChartOfAccount(Chart financialOffsetChartOfAccount) {
        this.financialOffsetChartOfAccount = financialOffsetChartOfAccount;
    }

    /**
     * @return Returns the financialOffsetAccount.
     */
    public Account getFinancialOffsetAccount() {
        return financialOffsetAccount;
    }

    /**
     * @param financialOffsetAccount The financialOffsetAccount to set.
     * @deprecated
     */
    public void setFinancialOffsetAccount(Account financialOffsetAccount) {
        this.financialOffsetAccount = financialOffsetAccount;
    }

    /**
     * @return Returns the objectCodeCurrent.
     */
    public ObjectCodeCurrent getObjectCodeCurrent() {
        return objectCodeCurrent;
    }

    /**
     * @param objectCodeCurrent The objectCodeCurrent to set.
     * @deprecated
     */
    public void setObjectCodeCurrent(ObjectCodeCurrent objectCodeCurrent) {
        this.objectCodeCurrent = objectCodeCurrent;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("financialOffsetObjectCode", this.financialOffsetObjectCode);
        return m;
    }

}
