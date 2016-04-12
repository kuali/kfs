package edu.arizona.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

public class PaymentMethodChart extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String paymentMethodCode;
    protected String chartOfAccountsCode;
    protected java.sql.Date effectiveDate;

    protected String feeIncomeChartOfAccountsCode;
    protected String feeIncomeAccountNumber;
    protected String feeIncomeFinancialObjectCode;
    protected String feeExpenseFinancialObjectCode;
    protected KualiDecimal feeAmount;

    protected String clearingChartOfAccountsCode;
    protected String clearingAccountNumber;
    protected String clearingFinancialObjectCode;

    protected boolean active = true;    

    protected Chart chartOfAccounts;
    
    protected Chart feeIncomeChartOfAccounts;
    protected Account feeIncomeAccount;
    protected ObjectCodeCurrent feeIncomeFinancialObject;
    protected ObjectCodeCurrent feeExpenseFinancialObject;
    
    protected Chart clearingChartOfAccounts;
    protected Account clearingAccount;
    protected ObjectCodeCurrent clearingFinancialObject;
    
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> lhm = new LinkedHashMap<String, Object>();
        lhm.put("paymentMethodCode", paymentMethodCode);
        lhm.put("chartOfAccountsCode", chartOfAccountsCode);
        lhm.put("effectiveDate", effectiveDate);
        return lhm;
    }


    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }


    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }


    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public java.sql.Date getEffectiveDate() {
        return effectiveDate;
    }


    public void setEffectiveDate(java.sql.Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }


    public String getFeeIncomeChartOfAccountsCode() {
        return feeIncomeChartOfAccountsCode;
    }


    public void setFeeIncomeChartOfAccountsCode(String feeIncomeChartOfAccountsCode) {
        this.feeIncomeChartOfAccountsCode = feeIncomeChartOfAccountsCode;
    }


    public String getFeeIncomeAccountNumber() {
        return feeIncomeAccountNumber;
    }


    public void setFeeIncomeAccountNumber(String feeIncomeAccountNumber) {
        this.feeIncomeAccountNumber = feeIncomeAccountNumber;
    }


    public String getFeeIncomeFinancialObjectCode() {
        return feeIncomeFinancialObjectCode;
    }


    public void setFeeIncomeFinancialObjectCode(String feeIncomeFinancialObjectCode) {
        this.feeIncomeFinancialObjectCode = feeIncomeFinancialObjectCode;
    }


    public String getFeeExpenseFinancialObjectCode() {
        return feeExpenseFinancialObjectCode;
    }


    public void setFeeExpenseFinancialObjectCode(String feeExpenseFinancialObjectCode) {
        this.feeExpenseFinancialObjectCode = feeExpenseFinancialObjectCode;
    }


    public KualiDecimal getFeeAmount() {
        return feeAmount;
    }


    public void setFeeAmount(KualiDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }


    public String getClearingChartOfAccountsCode() {
        return clearingChartOfAccountsCode;
    }


    public void setClearingChartOfAccountsCode(String clearingChartOfAccountsCode) {
        this.clearingChartOfAccountsCode = clearingChartOfAccountsCode;
    }


    public String getClearingAccountNumber() {
        return clearingAccountNumber;
    }


    public void setClearingAccountNumber(String clearingAccountNumber) {
        this.clearingAccountNumber = clearingAccountNumber;
    }


    public String getClearingFinancialObjectCode() {
        return clearingFinancialObjectCode;
    }


    public void setClearingFinancialObjectCode(String clearingFinancialObjectCode) {
        this.clearingFinancialObjectCode = clearingFinancialObjectCode;
    }


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }


    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }


    public Chart getFeeIncomeChartOfAccounts() {
        return feeIncomeChartOfAccounts;
    }


    public void setFeeIncomeChartOfAccounts(Chart feeIncomeChartOfAccounts) {
        this.feeIncomeChartOfAccounts = feeIncomeChartOfAccounts;
    }


    public Account getFeeIncomeAccount() {
        return feeIncomeAccount;
    }


    public void setFeeIncomeAccount(Account feeIncomeAccount) {
        this.feeIncomeAccount = feeIncomeAccount;
    }


    public ObjectCodeCurrent getFeeIncomeFinancialObject() {
        return feeIncomeFinancialObject;
    }


    public void setFeeIncomeFinancialObject(ObjectCodeCurrent feeIncomeFinancialObject) {
        this.feeIncomeFinancialObject = feeIncomeFinancialObject;
    }


    public ObjectCodeCurrent getFeeExpenseFinancialObject() {
        return feeExpenseFinancialObject;
    }


    public void setFeeExpenseFinancialObject(ObjectCodeCurrent feeExpenseFinancialObject) {
        this.feeExpenseFinancialObject = feeExpenseFinancialObject;
    }


    public Chart getClearingChartOfAccounts() {
        return clearingChartOfAccounts;
    }


    public void setClearingChartOfAccounts(Chart clearingChartOfAccounts) {
        this.clearingChartOfAccounts = clearingChartOfAccounts;
    }


    public Account getClearingAccount() {
        return clearingAccount;
    }


    public void setClearingAccount(Account clearingAccount) {
        this.clearingAccount = clearingAccount;
    }


    public ObjectCodeCurrent getClearingFinancialObject() {
        return clearingFinancialObject;
    }


    public void setClearingFinancialObject(ObjectCodeCurrent clearingFinancialObject) {
        this.clearingFinancialObject = clearingFinancialObject;
    }
}

