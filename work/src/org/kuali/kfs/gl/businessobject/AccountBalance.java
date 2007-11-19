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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.web.Constant;

/**
 * Just as Balance is a summarization of Entry, so AccountBalance is a summarization of Balance.
 * Specifically, it stores the current budget, actual, and encumbrance totals in one record.
 */
public class AccountBalance extends PersistableBusinessObjectBase {
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
    private SubObjCd financialSubObject;
    private A21SubAccount a21SubAccount;
    private TransientBalanceInquiryAttributes dummyBusinessObject;
    private Options option;
    private String title;

    public static final String TYPE_CONSOLIDATION = "Consolidation";
    public static final String TYPE_LEVEL = "Level";
    public static final String TYPE_OBJECT = "Object";

    public AccountBalance() {
        super();
        this.dummyBusinessObject = new TransientBalanceInquiryAttributes();
        this.financialObject = new ObjectCode();
    }

    public AccountBalance(Transaction t) {
        this();
        universityFiscalYear = t.getUniversityFiscalYear();
        chartOfAccountsCode = t.getChartOfAccountsCode();
        accountNumber = t.getAccountNumber();
        subAccountNumber = t.getSubAccountNumber();
        objectCode = t.getFinancialObjectCode();
        subObjectCode = t.getFinancialSubObjectCode();
        currentBudgetLineBalanceAmount = KualiDecimal.ZERO;
        accountLineActualsBalanceAmount = KualiDecimal.ZERO;
        accountLineEncumbranceBalanceAmount = KualiDecimal.ZERO;
    }

    public AccountBalance(String type, Map data, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        this();

        this.universityFiscalYear = universityFiscalYear;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        subAccountNumber = (String) data.get(GLConstants.ColumnNames.SUB_ACCOUNT_NUMBER);

        currentBudgetLineBalanceAmount = new KualiDecimal((BigDecimal) data.get(GLConstants.ColumnNames.CURRENT_BDLN_BALANCE_AMOUNT));
        accountLineActualsBalanceAmount = new KualiDecimal((BigDecimal) data.get(GLConstants.ColumnNames.ACCOUNTING_LINE_ACTUALS_BALANCE_AMOUNT));
        accountLineEncumbranceBalanceAmount = new KualiDecimal((BigDecimal) data.get(GLConstants.ColumnNames.ACCOUNTING_LINE_ENCUMBRANCE_BALANCE_AMOUNT));

        financialObject.getFinancialObjectLevel().setFinancialConsolidationObjectCode((String) data.get(GLConstants.ColumnNames.CONSOLIDATION_OBJECT_CODE));
        financialObject.getFinancialObjectLevel().getFinancialConsolidationObject().setFinConsolidationObjectCode((String) data.get(GLConstants.ColumnNames.CONSOLIDATION_OBJECT_CODE));

        if (TYPE_CONSOLIDATION.equals(type)) {
            financialObject.getFinancialObjectType().setFinancialReportingSortCode((String) data.get(GLConstants.ColumnNames.REPORT_SORT_CODE));
            financialObject.getFinancialObjectLevel().getFinancialConsolidationObject().setFinancialReportingSortCode((String) data.get(GLConstants.ColumnNames.CONSOLIDATION_REPORT_SORT_CODE));
            fixVariance();
        }
        else if (TYPE_LEVEL.equals(type)) {
            financialObject.getFinancialObjectLevel().setFinancialReportingSortCode((String) data.get(GLConstants.ColumnNames.REPORT_SORT_CODE));
            financialObject.setFinancialObjectLevelCode((String) data.get(GLConstants.ColumnNames.OBJECT_LEVEL_CODE2));
            financialObject.getFinancialObjectLevel().setFinancialObjectLevelCode((String) data.get(GLConstants.ColumnNames.OBJECT_LEVEL_CODE2));

            // tricking it so getVariance() works
            financialObject.getFinancialObjectType().setFinancialReportingSortCode(Constant.START_CHAR_OF_REPORTING_SORT_CODE_B);
            fixVariance();
        }
        else if (TYPE_OBJECT.equals(type)) {
            objectCode = (String) data.get(GLConstants.ColumnNames.OBJECT_CODE);
            financialObject.setFinancialObjectLevelCode((String) data.get(GLConstants.ColumnNames.OBJECT_LEVEL_CODE));
            financialObject.getFinancialObjectLevel().setFinancialObjectLevelCode((String) data.get(GLConstants.ColumnNames.OBJECT_LEVEL_CODE));

            // tricking it so getVariance() works
            financialObject.getFinancialObjectType().setFinancialReportingSortCode(Constant.START_CHAR_OF_REPORTING_SORT_CODE_B);
            fixVariance();
        }
        else {
            throw new RuntimeException("Unknown type: " + type);
        }
    }

    public AccountBalance(String title) {
        this();
        this.title = title;
        // financialObject.getFinancialObjectLevel().setFinancialConsolidationObjectCode(title);
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
        map.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getUniversityFiscalYear());
        map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        map.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
        map.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, getSubAccountNumber());
        map.put(KFSPropertyConstants.OBJECT_CODE, getObjectCode());
        map.put(KFSPropertyConstants.SUB_OBJECT_CODE, getSubObjectCode());
        return map;
    }

    public String getTitle() {
        return title;
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
     * Gets the subObject
     * 
     * @return
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the subObject.
     * 
     * @param financialSubObject
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

}
