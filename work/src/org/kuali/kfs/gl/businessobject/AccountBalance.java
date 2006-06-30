/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.Options;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.gl.web.Constant;

/**
 * @author jsissom
 * 
 */
public class AccountBalance extends BusinessObjectBase {
    static final long serialVersionUID = 6873573726961704771L;

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String objectCode;
    private String subObjectCode;
    private KualiDecimal currentBudgetLineBalanceAmount;
    private KualiDecimal accountLineActualsBalanceAmount;
    private KualiDecimal accountLineEncumbranceBalanceAmount;
    private Date timestamp;

    private Chart chart;
    private Account account;
    private SubAccount subAccount;
    private ObjectCode financialObject;
    private A21SubAccount a21SubAccount;
    private DummyBusinessObject dummyBusinessObject;
    private Options option;

    public AccountBalance() {
        super();
        this.dummyBusinessObject = new DummyBusinessObject();
        this.financialObject = new ObjectCode();
    }

    public AccountBalance(Transaction t) {
        super();
        universityFiscalYear = t.getUniversityFiscalYear();
        chartOfAccountsCode = t.getChartOfAccountsCode();
        accountNumber = t.getAccountNumber();
        subAccountNumber = t.getSubAccountNumber();
        objectCode = t.getFinancialObjectCode();
        subObjectCode = t.getFinancialSubObjectCode();
        currentBudgetLineBalanceAmount = KualiDecimal.ZERO;
        accountLineActualsBalanceAmount = KualiDecimal.ZERO;
        accountLineEncumbranceBalanceAmount = KualiDecimal.ZERO;

        this.dummyBusinessObject = new DummyBusinessObject();
        this.financialObject = new ObjectCode();
    }

    public AccountBalance(String type, Map data, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        this();

        this.universityFiscalYear = universityFiscalYear;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        subAccountNumber = (String) data.get("SUB_ACCT_NBR");

        currentBudgetLineBalanceAmount = new KualiDecimal((BigDecimal) data.get("CURR_BDLN_BAL_AMT"));
        accountLineActualsBalanceAmount = new KualiDecimal((BigDecimal) data.get("ACLN_ACTLS_BAL_AMT"));
        accountLineEncumbranceBalanceAmount = new KualiDecimal((BigDecimal) data.get("ACLN_ENCUM_BAL_AMT"));

        if ("Consolidation".equals(type)) {
            financialObject.getFinancialObjectType().setFinancialReportingSortCode((String) data.get("FIN_REPORT_SORT_CD"));
            financialObject.getFinancialObjectLevel().getFinancialConsolidationObject().setFinancialReportingSortCode((String) data.get("CONS_FIN_REPORT_SORT_CD"));
            financialObject.getFinancialObjectLevel().setFinancialConsolidationObjectCode((String) data.get("FIN_CONS_OBJ_CD"));
            fixVariance();
        }
        else if ("Level".equals(type)) {
            financialObject.getFinancialObjectLevel().setFinancialConsolidationObjectCode((String) data.get("FIN_CONS_OBJ_CD"));
            financialObject.getFinancialObjectLevel().setFinancialReportingSortCode((String) data.get("FIN_REPORT_SORT_CD"));
            financialObject.getFinancialObjectLevel().setFinancialObjectLevelCode((String) data.get("FIN_OBJ_LEVEL_CD"));

            // tricking it so getVariance() works
            financialObject.getFinancialObjectType().setFinancialReportingSortCode("B");
            fixVariance();
        }
        else if ("Object".equals(type)) {
            objectCode = (String) data.get("FIN_OBJECT_CD");
            financialObject.setFinancialObjectLevelCode((String) data.get("FIN_OBJ_LVL_CD"));

            // tricking it so getVariance() works
            financialObject.getFinancialObjectType().setFinancialReportingSortCode("B");
            fixVariance();
        }
        else {
            throw new RuntimeException("Unknown type: " + type);
        }
    }

    public AccountBalance(String title) {
        this();
        financialObject.getFinancialObjectLevel().setFinancialConsolidationObjectCode(title);
        currentBudgetLineBalanceAmount = KualiDecimal.ZERO;
        accountLineActualsBalanceAmount = KualiDecimal.ZERO;
        accountLineEncumbranceBalanceAmount = KualiDecimal.ZERO;
    }

    public void fixVariance() {
        dummyBusinessObject.setGenericAmount(getVariance());
    }

    public KualiDecimal getVariance() {

        KualiDecimal variance = KualiDecimal.ZERO;

        // get the reporting sort code
        String reportingSortCode = financialObject.getFinancialObjectType().getFinancialReportingSortCode();

        // calculate the variance based on the starting character of reporting sort code
        if (reportingSortCode.startsWith(Constant.START_CHAR_OF_REPORTING_SORT_CODE_B)) {
            variance = currentBudgetLineBalanceAmount.subtract(accountLineActualsBalanceAmount);
            variance = variance.subtract(accountLineEncumbranceBalanceAmount);
        }
        else {
            variance = accountLineActualsBalanceAmount.subtract(currentBudgetLineBalanceAmount);
        }
        return variance;
    }

    public void add(AccountBalance ab) {
        currentBudgetLineBalanceAmount = currentBudgetLineBalanceAmount.add(ab.currentBudgetLineBalanceAmount);
        accountLineActualsBalanceAmount = accountLineActualsBalanceAmount.add(ab.accountLineActualsBalanceAmount);
        accountLineEncumbranceBalanceAmount = accountLineEncumbranceBalanceAmount.add(ab.accountLineEncumbranceBalanceAmount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("universityFiscalYear", getUniversityFiscalYear());
        map.put("chartOfAccountsCode", getChartOfAccountsCode());
        map.put("accountNumber", getAccountNumber());
        map.put("subAccountNumber", getSubAccountNumber());
        map.put("objectCode", getObjectCode());
        map.put("subObjectCode", getSubObjectCode());
        return map;
    }

    public A21SubAccount getA21SubAccount() {
        return a21SubAccount;
    }

    public void setA21SubAccount(A21SubAccount subAccount) {
        a21SubAccount = subAccount;
    }

    public Options getOption() {
        return option;
    }

    public void setOption(Options option) {
        this.option = option;
    }

    public KualiDecimal getAccountLineActualsBalanceAmount() {
        return accountLineActualsBalanceAmount;
    }

    public void setAccountLineActualsBalanceAmount(KualiDecimal accountLineActualsBalanceAmount) {
        this.accountLineActualsBalanceAmount = accountLineActualsBalanceAmount;
    }

    public KualiDecimal getAccountLineEncumbranceBalanceAmount() {
        return accountLineEncumbranceBalanceAmount;
    }

    public void setAccountLineEncumbranceBalanceAmount(KualiDecimal accountLineEncumbranceBalanceAmount) {
        this.accountLineEncumbranceBalanceAmount = accountLineEncumbranceBalanceAmount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public KualiDecimal getCurrentBudgetLineBalanceAmount() {
        return currentBudgetLineBalanceAmount;
    }

    public void setCurrentBudgetLineBalanceAmount(KualiDecimal currentBudgetLineBalanceAmount) {
        this.currentBudgetLineBalanceAmount = currentBudgetLineBalanceAmount;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getSubObjectCode() {
        return subObjectCode;
    }

    public void setSubObjectCode(String subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

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
     * Gets the dummyBusinessObject attribute.
     * 
     * @return Returns the dummyBusinessObject.
     */
    public DummyBusinessObject getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     * 
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    public void setDummyBusinessObject(DummyBusinessObject dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }
}
