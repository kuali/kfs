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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ReportBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Just as Balance is a summarization of Entry, so AccountBalance is a summarization of Balance.
 * Specifically, it stores the current budget, actual, and encumbrance totals in one record.
 */
public class AccountBalance extends PersistableBusinessObjectBase implements ReportBusinessObject{
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
    private SubObjectCode financialSubObject;
    private A21SubAccount a21SubAccount;
    private TransientBalanceInquiryAttributes dummyBusinessObject;
    private SystemOptions option;
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
        subAccountNumber = (String) data.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER);

        currentBudgetLineBalanceAmount = new KualiDecimal((BigDecimal) data.get(GeneralLedgerConstants.ColumnNames.CURRENT_BDLN_BALANCE_AMOUNT));
        accountLineActualsBalanceAmount = new KualiDecimal((BigDecimal) data.get(GeneralLedgerConstants.ColumnNames.ACCOUNTING_LINE_ACTUALS_BALANCE_AMOUNT));
        accountLineEncumbranceBalanceAmount = new KualiDecimal((BigDecimal) data.get(GeneralLedgerConstants.ColumnNames.ACCOUNTING_LINE_ENCUMBRANCE_BALANCE_AMOUNT));

        financialObject.getFinancialObjectLevel().setFinancialConsolidationObjectCode((String) data.get(GeneralLedgerConstants.ColumnNames.CONSOLIDATION_OBJECT_CODE));
        financialObject.getFinancialObjectLevel().getFinancialConsolidationObject().setFinConsolidationObjectCode((String) data.get(GeneralLedgerConstants.ColumnNames.CONSOLIDATION_OBJECT_CODE));

        if (TYPE_CONSOLIDATION.equals(type)) {
            financialObject.getFinancialObjectType().setFinancialReportingSortCode((String) data.get(GeneralLedgerConstants.ColumnNames.REPORT_SORT_CODE));
            financialObject.getFinancialObjectLevel().getFinancialConsolidationObject().setFinancialReportingSortCode((String) data.get(GeneralLedgerConstants.ColumnNames.CONSOLIDATION_REPORT_SORT_CODE));
            
            financialObject.getFinancialObjectType().setBasicAccountingCategoryCode((String) data.get(GeneralLedgerConstants.ColumnNames.ACCTG_CTGRY_CD));
            fixVariance();
        }
        else if (TYPE_LEVEL.equals(type)) {
            financialObject.getFinancialObjectLevel().setFinancialReportingSortCode((String) data.get(GeneralLedgerConstants.ColumnNames.REPORT_SORT_CODE));
            financialObject.setFinancialObjectLevelCode((String) data.get(GeneralLedgerConstants.ColumnNames.OBJECT_LEVEL_CODE2));
            financialObject.getFinancialObjectLevel().setFinancialObjectLevelCode((String) data.get(GeneralLedgerConstants.ColumnNames.OBJECT_LEVEL_CODE2));

            // tricking it so getVariance() works
            financialObject.getFinancialObjectType().setBasicAccountingCategoryCode((String) data.get(GeneralLedgerConstants.ColumnNames.ACCTG_CTGRY_CD));
            fixVariance();
        }
        else if (TYPE_OBJECT.equals(type)) {
            objectCode = (String) data.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE);
            financialObject.setFinancialObjectLevelCode((String) data.get(GeneralLedgerConstants.ColumnNames.OBJECT_LEVEL_CODE));
            financialObject.getFinancialObjectLevel().setFinancialObjectLevelCode((String) data.get(GeneralLedgerConstants.ColumnNames.OBJECT_LEVEL_CODE));

            // tricking it so getVariance() works
            financialObject.getFinancialObjectType().setBasicAccountingCategoryCode((String) data.get(GeneralLedgerConstants.ColumnNames.ACCTG_CTGRY_CD));
            fixVariance();
        }
        else {
            throw new RuntimeException("Unknown type: " + type);
        }
    }
              
    /**
     * Perform the refresh non-updateable method but do an additional check on the following  objects
     * within financialObject if either the object is null or the primary key returned null.  If that is true,
     * re-use the original object/values.
     * 
     * 1. FinancialObjectLevel
     * 2. FinancialObjectType
     *
     * @see org.kuali.kfs.gl.businessobject.ReportBusinessObject#refreshNonUpdateableForReport()
     */
    @Override
    public void refreshNonUpdateableForReport() {
        //store the orignal financial object
        ObjectCode origfinancialObject = getFinancialObject();
        super.refreshNonUpdateableReferences();
        
        if (ObjectUtils.isNull(financialObject)){
            //entire financial object is  null, simply replace with the original
            setFinancialObject(origfinancialObject);
        }else{
            //check individual subobjects
            
            //check financial object level - if the object is null or primary key value is null, this object needs to be updated
            if (ObjectUtils.isNull(financialObject.getFinancialObjectLevel()) || ObjectUtils.isNull(financialObject.getFinancialObjectLevel().getFinancialObjectLevelCode())){
                financialObject.setFinancialObjectLevel(origfinancialObject.getFinancialObjectLevel());
                financialObject.setFinancialObjectLevelCode(origfinancialObject.getFinancialObjectCode());
            }
            //check financial object type - if the object is null or primary key value is null, this object needs to be updated
            if (ObjectUtils.isNull(financialObject.getFinancialObjectType().getCode()) || ObjectUtils.isNull(financialObject.getFinancialObjectType())){
                financialObject.setFinancialObjectType(origfinancialObject.getFinancialObjectType());
            }
        }
    }

    /**
     * Retrieve from parameter the Accounting Category Expense Code 
     * 
     * @return
     */
    public String getAccountingCategoryExpenseCode(){
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String accountingCategoryExpenseCode = parameterService.getParameterValueAsString(AccountBalanceByConsolidation.class, GeneralLedgerConstants.BASIC_ACCOUNTING_CATEGORY_REPRESENTING_EXPENSES);
        return accountingCategoryExpenseCode;
    }

    public AccountBalance(String title) {
        this();
        this.title = title;
        // financialObject.getFinancialObjectLevel().setFinancialConsolidationObjectCode(title);
        currentBudgetLineBalanceAmount = KualiDecimal.ZERO;
        accountLineActualsBalanceAmount = KualiDecimal.ZERO;
        accountLineEncumbranceBalanceAmount = KualiDecimal.ZERO;
    }
    
    /**
     * Constructs a AccountBalance.java per the primary keys only of the passed in accountBalanceHistory
     * @param accountBalanceHistory
     */
    public AccountBalance(AccountBalanceHistory accountBalanceHistory) {
        universityFiscalYear = accountBalanceHistory.getUniversityFiscalYear();
        chartOfAccountsCode = accountBalanceHistory.getChartOfAccountsCode();
        accountNumber = accountBalanceHistory.getAccountNumber();
        subAccountNumber = accountBalanceHistory.getSubAccountNumber();
        objectCode = accountBalanceHistory.getObjectCode();
        subObjectCode = accountBalanceHistory.getSubObjectCode();
    }

    public void fixVariance() {
        dummyBusinessObject.setGenericAmount(getVariance());
    }

    public KualiDecimal getVariance() {

        KualiDecimal variance = KualiDecimal.ZERO;

        // calculate the variance based on the basic accounting category code
        if (getAccountingCategoryExpenseCode().equals(financialObject.getFinancialObjectType().getBasicAccountingCategoryCode())) {
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {

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

    public SystemOptions getOption() {
        return option;
    }

    public void setOption(SystemOptions option) {
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
    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the subObject.
     * 
     * @param financialSubObject
     */
    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

}
