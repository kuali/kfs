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
package org.kuali.kfs.module.ar.core;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class InvoiceDetailExpecteds {

    private KualiDecimal amount;
    private KualiDecimal amountDiscounted;
    private KualiDecimal amountOpen;
    private boolean discounted;
    private int paidAppliedsCount;
    private List<KualiDecimal> paidAppliedAmounts;
    
    public InvoiceDetailExpecteds() {
        paidAppliedAmounts = new ArrayList<KualiDecimal>();
    }
    
    public InvoiceDetailExpecteds(KualiDecimal amount, KualiDecimal amountDiscounted, KualiDecimal amountOpen, boolean discounted, int paidAppliedsCount) {
        this();
        this.amount = amount;
        this.amountDiscounted = amountDiscounted;
        this.amountOpen = amountOpen;
        this.discounted = discounted;
        this.paidAppliedsCount = paidAppliedsCount;
    }
    
    public InvoiceDetailExpecteds(KualiDecimal amount, KualiDecimal amountDiscounted, KualiDecimal amountOpen, boolean discounted, int paidAppliedsCount, List<KualiDecimal> paidAppliedAmounts) {
        this(amount, amountDiscounted, amountOpen, discounted, paidAppliedsCount);
        this.paidAppliedAmounts.addAll(paidAppliedAmounts);
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public KualiDecimal getAmountDiscounted() {
        return amountDiscounted;
    }

    public void setAmountDiscounted(KualiDecimal amountDiscounted) {
        this.amountDiscounted = amountDiscounted;
    }

    public KualiDecimal getAmountOpen() {
        return amountOpen;
    }

    public void setAmountOpen(KualiDecimal amountOpen) {
        this.amountOpen = amountOpen;
    }

    public boolean isDiscounted() {
        return discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    public int getPaidAppliedsCount() {
        return paidAppliedsCount;
    }

    public void setPaidAppliedsCount(int paidAppliedsCount) {
        this.paidAppliedsCount = paidAppliedsCount;
    }

    public List<KualiDecimal> getPaidAppliedAmounts() {
        return paidAppliedAmounts;
    }

    public void setPaidAppliedAmounts(List<KualiDecimal> paidAppliedAmounts) {
        this.paidAppliedAmounts = paidAppliedAmounts;
    }
    
}
