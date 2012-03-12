/*
 * Copyright 2009 The Kuali Foundation
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
