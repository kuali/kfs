/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import java.io.Serializable;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.Transaction;

public class SufficientFundsItem implements Serializable, Comparable {
    private Options year;
    private Account account;
    private ObjectCode financialObject;
    private ObjectType financialObjectType;
    private String sufficientFundsObjectCode;
    private KualiDecimal amount;
    private String documentTypeCode;
    private BalanceTyp balanceTyp;

    public BalanceTyp getBalanceTyp() {
        return balanceTyp;
    }

    public void setBalanceTyp(BalanceTyp balanceTyp) {
        this.balanceTyp = balanceTyp;
    }

    public SufficientFundsItem() {
        amount = KualiDecimal.ZERO;
    }

    public SufficientFundsItem(Options universityFiscalYear, Transaction tran, String sufficientFundsObjectCode) {

        amount = KualiDecimal.ZERO;
        year = universityFiscalYear;
        account = tran.getAccount();
        financialObject = tran.getFinancialObject();
        financialObjectType = tran.getObjectType();
        this.sufficientFundsObjectCode = sufficientFundsObjectCode;
        this.balanceTyp = tran.getBalanceType();

        add(tran);
    }

    public void add(Transaction t) {
        if (t.getObjectType().getFinObjectTypeDebitcreditCd().equals(t.getTransactionDebitCreditCode()) || Constants.EMPTY_STRING.equals(t.getTransactionDebitCreditCode())) {
            amount = amount.add(t.getTransactionLedgerEntryAmount());
        }
        else {
            amount = amount.subtract(t.getTransactionLedgerEntryAmount());
        }
    }

    public int compareTo(Object arg0) {
        SufficientFundsItem item = (SufficientFundsItem) arg0;
        return getKey().compareTo(item.getKey());
    }

    public String getKey() {
        return year.getUniversityFiscalYear() + account.getChartOfAccountsCode() + account.getAccountNumber() + financialObjectType.getCode() + sufficientFundsObjectCode + balanceTyp.getCode();
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public String getAccountSufficientFundsCode() {
        return account.getAccountSufficientFundsCode();
    }

    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    @Override
    public String toString() {
        return year.getUniversityFiscalYear() + "-" + account.getChartOfAccountsCode() + "-" + account.getAccountNumber() + "-" + financialObject.getFinancialObjectCode() + "-" + account.getAccountSufficientFundsCode() + "-" + sufficientFundsObjectCode + "-" + amount.toString();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public String getSufficientFundsObjectCode() {
        return sufficientFundsObjectCode;
    }

    public void setSufficientFundsObjectCode(String sufficientFundsObjectCode) {
        this.sufficientFundsObjectCode = sufficientFundsObjectCode;
    }

    public Options getYear() {
        return year;
    }

    public void setYear(Options year) {
        this.year = year;
    }


}
