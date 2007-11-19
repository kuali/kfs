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

public class DailyReport implements Comparable {
    private boolean pymtAttachment;
    private boolean pymtSpecialHandling;
    private boolean processImmediate;
    private String customer;
    private BigDecimal amount;
    private Integer payments;
    private Integer payees;

    public DailyReport() {
        payments = 0;
        payees = 0;
        amount = new BigDecimal("0");
    }

    public DailyReport(DailyReport dr) {
        this();
        pymtAttachment = dr.pymtAttachment;
        pymtSpecialHandling = dr.pymtSpecialHandling;
        processImmediate = dr.processImmediate;
        customer = dr.customer;
    }

    public DailyReport(boolean att,boolean spec,boolean immed, String c, BigDecimal a, Integer pm, Integer py) {
        this();
        pymtAttachment = att;
        pymtSpecialHandling = spec;
        processImmediate = immed;
        customer = c;
        amount = a;
        payments = pm;
        payees = py;
    }

    public String getSortGroupId() {
        if (isProcessImmediate()) {
            return "B";
        } else if (isPymtSpecialHandling()) {
            return "C";
        } else if (isPymtAttachment()) {
            return "D";
        } else {
            return "E";
        }
    }

    public String getSortGroupName() {
        String sortGroup = getSortGroupId();
        if ("B".equals(sortGroup)) {
            return "Immediate       ";
        }
        else if ("C".equals(sortGroup)) {
            return "Special Handling";
        }
        else if ("D".equals(sortGroup)) {
            return "Attachment      ";
        }
        else {
            return "Other           ";
        }
    }

    @Override
    public String toString() {
        return pymtAttachment + " " + pymtSpecialHandling + " " + processImmediate + " " + customer + " " + amount + " " + payments + " " + payees;
    }

    public int compareTo(Object o) {
        DailyReport dro = (DailyReport) o;
        return getKey().compareTo(dro.getKey());
    }

    public String getKey() {
        return getSortGroupId() + customer;
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

    public boolean isProcessImmediate() {
        return processImmediate;
    }

    public void setProcessImmediate(boolean processImmediate) {
        this.processImmediate = processImmediate;
    }

    public boolean isPymtAttachment() {
        return pymtAttachment;
    }

    public void setPymtAttachment(boolean pymtAttachment) {
        this.pymtAttachment = pymtAttachment;
    }

    public boolean isPymtSpecialHandling() {
        return pymtSpecialHandling;
    }

    public void setPymtSpecialHandling(boolean pymtSpecialHandling) {
        this.pymtSpecialHandling = pymtSpecialHandling;
    }

    
}
