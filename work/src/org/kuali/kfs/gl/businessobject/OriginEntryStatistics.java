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
package org.kuali.kfs.gl.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class represents origin entry statistics for debit, credit, and budget total amounts 
 */
public class OriginEntryStatistics {
    private KualiDecimal debitTotalAmount = KualiDecimal.ZERO;
    private KualiDecimal creditTotalAmount = KualiDecimal.ZERO;
    private KualiDecimal budgetTotalAmount = KualiDecimal.ZERO;
    private Integer rowCount = 0;

    public void addDebit(KualiDecimal d) {
        if (d != null) {
            debitTotalAmount = debitTotalAmount.add(d);
        }
    }

    public void addCredit(KualiDecimal c) {
        if (c != null) {
            creditTotalAmount = creditTotalAmount.add(c);
        }
    }

    public void addBudget(KualiDecimal b) {
        if (b != null) {
            budgetTotalAmount = budgetTotalAmount.add(b);
        }
    }

    public void incrementCount() {
        rowCount++;
    }

    public KualiDecimal getCreditTotalAmount() {
        return creditTotalAmount;
    }

    public void setCreditTotalAmount(KualiDecimal creditTotalAmount) {
        this.creditTotalAmount = creditTotalAmount;
    }

    public KualiDecimal getDebitTotalAmount() {
        return debitTotalAmount;
    }

    public void setDebitTotalAmount(KualiDecimal debitTotalAmount) {
        this.debitTotalAmount = debitTotalAmount;
    }

    public KualiDecimal getBudgetTotalAmount() {
        return budgetTotalAmount;
    }

    public void setBudgetTotalAmount(KualiDecimal budgetTotalAmount) {
        this.budgetTotalAmount = budgetTotalAmount;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }
}
