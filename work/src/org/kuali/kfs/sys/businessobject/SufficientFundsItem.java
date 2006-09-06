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
package org.kuali.module.gl.util;

import java.io.Serializable;

import org.kuali.core.bo.user.Options;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.gl.bo.Transaction;

public class SufficientFundsItem implements Serializable, Comparable {
    private Options year;
    private Account account;
    private ObjectCode financialObject;
    private ObjectType financialObjectType;
    private String sufficientFundsObjectCode;
    private KualiDecimal amount;
    private String documentTypeCode;

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

        add(tran);
    }

    public void add(Transaction t) {
        if (t.getObjectType().getFinObjectTypeDebitcreditCd().equals(t.getTransactionDebitCreditCode())) {
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
        return year.getUniversityFiscalYear() + account.getChartOfAccountsCode() + account.getAccountNumber() + financialObjectType.getCode() + sufficientFundsObjectCode;
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
