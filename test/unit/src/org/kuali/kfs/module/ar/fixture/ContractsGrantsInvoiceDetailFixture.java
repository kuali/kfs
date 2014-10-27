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

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Fixture class for ContractsGrantsInvoiceDetail
 */
public enum ContractsGrantsInvoiceDetailFixture {
    INV_DTL1(new Long(2341), "6320", "SAL", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),
    INV_DTL2(new Long(2350), "6321", "SAL", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),
    INV_DTL3(new Long(2350), "6322", "EMPB", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO, false),
    INV_DTL4(new Long(2355), "6324", "SAL", new KualiDecimal(320.00), KualiDecimal.ZERO, new KualiDecimal(340.00), new KualiDecimal(-20.00), KualiDecimal.ZERO, false),
    INV_DTL5(new Long(2356), "6325", "EMPB", new KualiDecimal(320.00), KualiDecimal.ZERO, new KualiDecimal(340.00), new KualiDecimal(-20.00), KualiDecimal.ZERO, false),
    INV_DTL6(new Long(2357), "6326", "OIC", new KualiDecimal(320.00), KualiDecimal.ZERO, new KualiDecimal(340.00), new KualiDecimal(-20.00), KualiDecimal.ZERO, true),
    INV_DTL7(new Long(2357), "6326", null, new KualiDecimal(300.00), KualiDecimal.ZERO, new KualiDecimal(300.00), new KualiDecimal(0.00), KualiDecimal.ZERO, true);

    private Long invoiceDetailIdentifier;
    private String documentNumber;
    private String categoryCode;
    private KualiDecimal budget = KualiDecimal.ZERO;
    private KualiDecimal expenditures = KualiDecimal.ZERO;
    private KualiDecimal cumulative = KualiDecimal.ZERO;
    private KualiDecimal balance = KualiDecimal.ZERO;
    private KualiDecimal billed = KualiDecimal.ZERO;
    private boolean indirectCostIndicator;

    private ContractsGrantsInvoiceDetailFixture(Long invoiceDetailIdentifier, String documentNumber, String categoryCode, KualiDecimal budget, KualiDecimal expenditures, KualiDecimal cumulative, KualiDecimal balance, KualiDecimal billed, boolean indirectCostIndicator) {

        this.invoiceDetailIdentifier = invoiceDetailIdentifier;
        this.documentNumber = documentNumber;
        this.categoryCode = categoryCode;
        this.budget = budget;
        this.expenditures = expenditures;
        this.cumulative = cumulative;
        this.balance = balance;
        this.billed = billed;
        this.indirectCostIndicator = indirectCostIndicator;

    }

    public ContractsGrantsInvoiceDetail createInvoiceDetail() {
        ContractsGrantsInvoiceDetail invoiceDetail = new ContractsGrantsInvoiceDetail();
        invoiceDetail.setInvoiceDetailIdentifier(this.invoiceDetailIdentifier);

        invoiceDetail.setDocumentNumber(this.documentNumber);
        invoiceDetail.setCategoryCode(this.categoryCode);
        invoiceDetail.refreshReferenceObject(ArPropertyConstants.COST_CATEGORY);
        if (!ObjectUtils.isNull(invoiceDetail.getCostCategory())) {
            invoiceDetail.setCategoryName(invoiceDetail.getCostCategory().getCategoryName());
        }
        invoiceDetail.setBudget(this.budget);
        invoiceDetail.setExpenditures(this.expenditures);
        invoiceDetail.setCumulative(this.cumulative);
        invoiceDetail.setBilled(this.billed);
        invoiceDetail.setIndirectCostIndicator(this.indirectCostIndicator);

        return invoiceDetail;

    }


}
