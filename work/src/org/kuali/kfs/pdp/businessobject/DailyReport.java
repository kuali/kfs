/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.bo;

import java.math.BigDecimal;

public class DailyReport {
    private String bankName;
    private String sortOrder;
    private String customer;
    private BigDecimal amount;
    private Integer payments;
    private Integer payees;

    public DailyReport() {
        payments = 0;
        payees = 0;
        amount = new BigDecimal("0");
    }

    public DailyReport(String n,String s,String c,BigDecimal a,Integer pm,Integer py) {
        this();
        bankName = n;
        sortOrder = s;
        customer = c;
        amount = a;
        payments = pm;
        payees = py;
    }

    public void addRow(DailyReport r) {
        payments = payments + r.payments;
        payees = payees + r.payees;
        amount = amount.add(r.amount);
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public Integer getPayees() {
        return payees;
    }
    public void setPayees(Integer payees) {
        this.payees = payees;
    }
    public Integer getPayments() {
        return payments;
    }
    public void setPayments(Integer payments) {
        this.payments = payments;
    }
    public String getSortOrder() {
        return sortOrder;
    }
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
