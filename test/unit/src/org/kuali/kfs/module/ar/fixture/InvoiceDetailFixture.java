/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.InvoiceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceDetail
 */
public enum InvoiceDetailFixture {
    INV_DTL1(new Long(2341), "6320", "SAL", "Salaries and Wages", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false), INV_DTL2(new Long(2350), "6321", "SAL", "Salaries and Wages", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false), INV_DTL3(new Long(2350), "6322", "EMPB", "Employee Benefits", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false);


    private Long invoiceDetailIdentifier;
    private String documentNumber;
    private String categoryCode;
    private String category;
    private KualiDecimal budget = KualiDecimal.ZERO;
    private KualiDecimal expenditures = KualiDecimal.ZERO;
    private KualiDecimal cumulative = KualiDecimal.ZERO;
    private KualiDecimal balance = KualiDecimal.ZERO;
    private KualiDecimal billed = KualiDecimal.ZERO;
    private boolean indirectCostIndicator;

    private InvoiceDetailFixture(Long invoiceDetailIdentifier, String documentNumber, String categoryCode, String category, KualiDecimal budget, KualiDecimal expenditures, KualiDecimal cumulative, KualiDecimal balance, KualiDecimal billed, boolean indirectCostIndicator) {

        this.invoiceDetailIdentifier = invoiceDetailIdentifier;
        this.documentNumber = documentNumber;
        this.categoryCode = categoryCode;
        this.category = category;
        this.budget = budget;
        this.expenditures = expenditures;
        this.cumulative = cumulative;
        this.balance = balance;
        this.billed = billed;
        this.indirectCostIndicator = indirectCostIndicator;

    }

    public InvoiceDetail createInvoiceDetail() {
        InvoiceDetail invoiceDetail = new InvoiceDetail();
        invoiceDetail.setInvoiceDetailIdentifier(this.invoiceDetailIdentifier);

        invoiceDetail.setDocumentNumber(this.documentNumber);
        invoiceDetail.setCategoryCode(this.categoryCode);
        invoiceDetail.setCategory(this.category);
        invoiceDetail.setBudget(this.budget);
        invoiceDetail.setExpenditures(this.expenditures);
        invoiceDetail.setCumulative(this.cumulative);
        invoiceDetail.setBalance(this.balance);
        invoiceDetail.setBilled(this.billed);
        invoiceDetail.setIndirectCostIndicator(this.indirectCostIndicator);

        return invoiceDetail;

    }


}
