/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.pdp.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class DailyReport extends TransientBusinessObjectBase {
    private String sortOrder;
    private String customer;
    private KualiDecimal amount;
    private Integer payments;
    private Integer payees;

    private PaymentGroup paymentGroup;

    public DailyReport() {
        payments = 0;
        payees = 0;
        amount = KualiDecimal.ZERO;
    }

    public DailyReport(DailyReport dr) {
        this();
        customer = dr.customer;
        paymentGroup = dr.paymentGroup;
    }

    public DailyReport(String c, KualiDecimal a, Integer pm, Integer py, PaymentGroup paymentGroup) {
        this();
        customer = c;
        amount = a;
        payments = pm;
        payees = py;
        this.paymentGroup = paymentGroup;
    }


    @Override
    public String toString() {
        return customer + " " + amount + " " + payments + " " + payees;
    }

    public void addRow(DailyReport r) {
        payments = payments + r.payments;
        payees = payees + r.payees;
        amount = amount.add(r.amount);

        if (ObjectUtils.isNull(this.paymentGroup)) {
            this.paymentGroup = r.paymentGroup;
        }
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
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

    public PaymentGroup getPaymentGroup() {
        return paymentGroup;
    }

    public void setPaymentGroup(PaymentGroup paymentGroup) {
        this.paymentGroup = paymentGroup;
    }
}
